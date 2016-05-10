package com.jvoid.test.instrumentation.provider.spock;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.Arrays;
import java.util.Collection;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;

import com.google.inject.Inject;
import com.jvoid.execution.JVoidExecutionContext;
import com.jvoid.instrumentation.JVoidInstrumentationHelper;
import com.jvoid.instrumentation.JVoidInstrumentationHelperHolder;
import com.jvoid.instrumentation.provider.spock.RunFeatureMethodInstrumenter;
import com.jvoid.metadata.model.JTest;
import com.jvoid.metadata.repositories.TestsRepository;
import com.jvoid.test.AbstractJVoidTest;

import javassist.CtClass;
import javassist.CtMethod;
import javassist.CtNewMethod;

public class RunFeatureMethodInstrumenterTest extends AbstractJVoidTest {

    public static final String MOCKED_FEATURE_ID = "YES_FEATURE";

    private CtClass ctBaseSpecRunnerMock;

    @Inject
    private TestsRepository testsRepository;

    @Override
    @Before
    public void setUp() throws Exception {
        super.setUp();
        ctBaseSpecRunnerMock = classPool.get(BaseSpecRunnerMock.class.getName());
    }

    @Override
    @After
    public void tearDown() {
        ctBaseSpecRunnerMock.prune();
    }

    @Parameters
    public static Collection<Boolean> data() {
        return Arrays.asList(new Boolean[] { true, false });
    }

    @Parameter
    public /* NOT private */ boolean isFeatureSkipped;

    @Test
    public void testInstrument() throws Exception {
        // We need to forge a class at runtime, because reloading the same class with changes in the bytecode is really tricky :)
        CtClass ctForgedClass = classPool.makeClass("ForgedBaseSpecRunnerMock", ctBaseSpecRunnerMock);
        ctForgedClass.addMethod(CtNewMethod.make("public void runFeature() {  }", ctForgedClass));

        CtMethod ctRunFeature = ctForgedClass.getDeclaredMethod("runFeature");
        RunFeatureMethodInstrumenter instrumenter = new RunFeatureMethodInstrumenter();
        instrumenter.instrument(ctRunFeature);

        @SuppressWarnings("unchecked")
        Class<? extends BaseSpecRunnerMock> instrumentedClass = classPool.toClass(ctForgedClass);
        BaseSpecRunnerMock baseSpecRunnerMock = instrumentedClass.newInstance();
        JVoidInstrumentationHelper mockInjectedCodeHelper = new MockInjectedCodeHelper(!isFeatureSkipped, testsRepository,
                jvoidExecutionContext);
        JVoidInstrumentationHelperHolder.getInstance().set(mockInjectedCodeHelper);

        // Make sure we have a current execution before calling runFeature
        setupCurrentExecution(333L);

        baseSpecRunnerMock.runFeature();

        // We must have this feature in the context
        assertEquals(MOCKED_FEATURE_ID, jvoidExecutionContext.getRunningTest().getIdentifier());
        // The feature must have been saved with the current execution
        JTest test = testsRepository.findByIdenfifierAndExecutionId(MOCKED_FEATURE_ID, getCurrentExecutionId());
        assertNotNull(test);
        // The feature is skipped depending on the InjectedCodeHelper (that is mocked)
        verify(baseSpecRunnerMock.supervisor, times(isFeatureSkipped ? 1 : 0)).featureSkipped(null);

        ctForgedClass.prune(); // cleanup that forged class
    }

    @Test
    public void testShouldInstrumentMethod() {
        CtMethod randomMethod = ctBaseSpecRunnerMock.getMethods()[0];
        assertThat(new RunFeatureMethodInstrumenter().shouldInstrument(randomMethod), is(true));
    }

    private static class MockInjectedCodeHelper extends JVoidInstrumentationHelper {
        private boolean isTestRelatedCodeModified;

        public MockInjectedCodeHelper(boolean isTestRelatedCodeModified, TestsRepository testsRepository,
                JVoidExecutionContext jvoidExecutionContext) {
            super(null,  testsRepository, jvoidExecutionContext);
            this.isTestRelatedCodeModified = isTestRelatedCodeModified;
        }

        @Override
        public boolean wasTestRelatedCodeModified(String featureId) {
            return isTestRelatedCodeModified;
        }
    }
}
