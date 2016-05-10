package com.jvoid.test.instrumentation.provider.app;

import static org.junit.Assert.assertEquals;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.google.inject.Inject;
import com.jvoid.configuration.JVoidConfiguration;
import com.jvoid.instrumentation.JVoidInstrumentationHelper;
import com.jvoid.instrumentation.JVoidInstrumentationHelperHolder;
import com.jvoid.instrumentation.provider.ProviderUtil;
import com.jvoid.instrumentation.provider.app.AppInstrumentationJClassHolder;
import com.jvoid.instrumentation.provider.app.TrackerMethodInstrumenter;
import com.jvoid.metadata.checksum.CodeChecksummer;
import com.jvoid.metadata.model.JClass;
import com.jvoid.metadata.model.JMethod;
import com.jvoid.metadata.repositories.ClassesRepository;
import com.jvoid.metadata.repositories.MethodsRepository;
import com.jvoid.test.AbstractJVoidTest;
import com.jvoid.test.instrumentation.provider.app.fake.FakeAppClass;

import javassist.CtClass;
import javassist.CtMethod;
import javassist.CtNewMethod;

public class TrackerMethodInstrumenterTest extends AbstractJVoidTest {

    @Inject
    private CodeChecksummer codeChecksummer;

    @Inject
    private ClassesRepository classesRepository;

    @Inject
    private MethodsRepository methodsRepository;

    @Inject
    private AppInstrumentationJClassHolder jClassHolder;

    @Inject
    private ProviderUtil providerUtils;

    @Inject
    private JVoidConfiguration jvoidConfiguration;

    private CtClass ctFakeAppClass;

    @Override
    @Before
    public void setUp() throws Exception {
        super.setUp();
        ctFakeAppClass = classPool.get(FakeAppClass.class.getName());
        // We need to forge a class at runtime, because reloading the same class
        // with changes in the bytecode is really tricky :)
    }

    @Override
    @After
    public void tearDown() {
        ctFakeAppClass.prune();
    }

    @Test
    public void testInstrument() throws Exception {
        CtClass ctForgedClass = classPool.makeClass("ForgedFakeAppClass", ctFakeAppClass);
        ctForgedClass.addMethod(CtNewMethod.make("public void hello() { int a = 42;  }", ctForgedClass));
        
        setupCurrentExecution(333L);
        Long currentExecutionId = jvoidExecutionContext.getCurrentExecutionId();

        String classIdentifier = providerUtils.getClassIdentifier(ctForgedClass);

        JClass jClass = new JClass();
        jClass.setExecutionId(currentExecutionId);
        jClass.setChecksum("aaaa");
        jClass.setIdentifier(classIdentifier);
        jClass = classesRepository.add(jClass);

        // This is performed by the class handler
        jClassHolder.setJClassUnderInstrumentation(jClass);

        CtMethod ctHello = ctForgedClass.getDeclaredMethod("hello");
        TrackerMethodInstrumenter instrumenter = new TrackerMethodInstrumenter(codeChecksummer,
                providerUtils, methodsRepository, jvoidExecutionContext,
                jvoidConfiguration, jClassHolder);
        instrumenter.instrument(ctHello);

        @SuppressWarnings("unchecked")
        Class<? extends FakeAppClass> instrumentedClass = classPool.toClass(ctForgedClass);
        FakeAppClass fakeApp = instrumentedClass.newInstance();

        MockInjectedCodeHelper mockInjectedCodeHelper = new MockInjectedCodeHelper();
        JVoidInstrumentationHelperHolder.getInstance().set(mockInjectedCodeHelper);

        fakeApp.hello();

        String methodIdentifier = providerUtils.getMethodIdentifier(ctHello);
        JMethod jMethod = methodsRepository.findByIdenfifierAndExecutionId(methodIdentifier,
                currentExecutionId);

        mockInjectedCodeHelper.bindInvokedWith(jMethod.getId(), jClass.getId());
        ctForgedClass.prune();
    }
    
    @Test
    public void testInstrumentWithEmtpyMethodBody() throws Exception {
        CtClass ctForgedClass = classPool.makeClass("ForgedFakeAppClass2", ctFakeAppClass);
        ctForgedClass.addMethod(CtNewMethod.make("public void helloWithoutContent() {  }", ctForgedClass));
        setupCurrentExecution(333L);
        Long currentExecutionId = jvoidExecutionContext.getCurrentExecutionId();

        String classIdentifier = providerUtils.getClassIdentifier(ctForgedClass);

        JClass jClass = new JClass();
        jClass.setExecutionId(currentExecutionId);
        jClass.setChecksum("aaaa");
        jClass.setIdentifier(classIdentifier);
        jClass = classesRepository.add(jClass);

        CtMethod ctHello = ctForgedClass.getDeclaredMethod("helloWithoutContent");
        TrackerMethodInstrumenter instrumenter = new TrackerMethodInstrumenter(codeChecksummer,
                providerUtils, methodsRepository, jvoidExecutionContext,
                jvoidConfiguration, jClassHolder);
        instrumenter.instrument(ctHello);

        @SuppressWarnings("unchecked")
        Class<? extends FakeAppClass> instrumentedClass = classPool.toClass(ctForgedClass);
        FakeAppClass fakeApp = instrumentedClass.newInstance();

        MockInjectedCodeHelper mockInjectedCodeHelper = new MockInjectedCodeHelper();
        JVoidInstrumentationHelperHolder.getInstance().set(mockInjectedCodeHelper);

        fakeApp.hello();

        String methodIdentifier = providerUtils.getMethodIdentifier(ctHello);
        JMethod jMethod = methodsRepository.findByIdenfifierAndExecutionId(methodIdentifier,
                currentExecutionId);
        Assert.assertNull(jMethod);
        ctForgedClass.prune();
    }

    // Mock of the InjectedCodeHelper just for testing purposes
    private static class MockInjectedCodeHelper extends JVoidInstrumentationHelper {

        public MockInjectedCodeHelper() {
            super(null, null, null);
        }

        private Long bindMethodId;
        private Long bindClassId;
        private boolean bindInvoked = false;

        @Override
        public void bindMethodToCurrentRunningTest(long methodId, long classId) {
            if (bindInvoked) {
                throw new RuntimeException("bind already invoked");
            }
            bindMethodId = methodId;
            bindClassId = classId;
            bindInvoked = true;
        }

        public void bindInvokedWith(Long methodId, Long classId) {
            assertEquals(bindMethodId,methodId);
            assertEquals(bindClassId, classId);
        }
    }
}
