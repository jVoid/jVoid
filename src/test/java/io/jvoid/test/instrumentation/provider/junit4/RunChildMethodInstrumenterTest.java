package io.jvoid.test.instrumentation.provider.junit4;

import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.lang.reflect.Method;

import org.junit.Test;
import org.junit.runner.Description;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.model.FrameworkMethod;

import io.jvoid.instrumentation.JVoidInstrumentationHelper;
import io.jvoid.instrumentation.JVoidInstrumentationHelperHolder;
import io.jvoid.instrumentation.provider.junit4.RunChildMethodInstrumenter;
import io.jvoid.test.AbstractJVoidTest;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.CtNewMethod;

public class RunChildMethodInstrumenterTest extends AbstractJVoidTest {

    @Test
    public void testInstrument() throws Exception {
        JVoidInstrumentationHelper mockHelper = mock(JVoidInstrumentationHelper.class);
        JVoidInstrumentationHelperHolder.getInstance().set(mockHelper);

        // We need to forge a class at runtime, because reloading the same class with changes in the bytecode is really tricky :)
        CtClass ctPrototypeClass = classPool.get(MockRunnerPrototype.class.getName());
        CtClass ctForgedClass = classPool.makeClass("MockRunner", ctPrototypeClass);
        ctForgedClass.addMethod(CtNewMethod.make(
                "public void runChild(org.junit.runners.model.FrameworkMethod method, org.junit.runner.notification.RunNotifier notifier) {  }",
                ctForgedClass));

        // Instrumentation
        CtMethod runChildMethod = ctForgedClass.getDeclaredMethod("runChild");
        RunChildMethodInstrumenter instrumenter = new RunChildMethodInstrumenter();
        instrumenter.instrument(runChildMethod);

        // Verify instrumentation
        Method mockMethod = MockTestClass.class.getMethod("mockTestMethod");
        final String FEATURE_IDENTIFIER = (mockMethod.getDeclaringClass().getName() + "#" + mockMethod.getName());
        FrameworkMethod mockFrameworkMethod = mock(FrameworkMethod.class);
        RunNotifier mockNotifier = mock(RunNotifier.class);
        when(mockFrameworkMethod.getMethod()).thenReturn(mockMethod);

        MockRunnerPrototype mockRunNotifier = (MockRunnerPrototype) classPool.toClass(ctForgedClass).newInstance();
        // First check non modified test
        when(mockHelper.wasTestRelatedCodeModified(FEATURE_IDENTIFIER)).thenReturn(true);
        mockRunNotifier.runChild(mockFrameworkMethod, mockNotifier);
        verify(mockHelper, times(1)).beginTest(FEATURE_IDENTIFIER);
        verify(mockNotifier, times(0)).fireTestIgnored(any(Description.class));

        when(mockHelper.wasTestRelatedCodeModified(FEATURE_IDENTIFIER)).thenReturn(false);
        mockRunNotifier.runChild(mockFrameworkMethod, mockNotifier);
        verify(mockHelper, times(2)).beginTest(FEATURE_IDENTIFIER);
        verify(mockNotifier, times(1)).fireTestIgnored(any(Description.class));

        ctForgedClass.prune();
        ctPrototypeClass.prune();
    }

    @Test
    public void testShouldInstrument() {
        assertTrue(new RunChildMethodInstrumenter().shouldInstrument(null));
    }

    public static class MockRunnerPrototype {
        public void runChild(FrameworkMethod method, RunNotifier notifier) {  }
        // This is necessary to check the successful instrumentation
        protected Description describeChild(Object obj) { return mock(Description.class); }
    }

    // to obtain a java.lang.Method
    private static class MockTestClass {
        @SuppressWarnings("unused")
        public void mockTestMethod() { /* NOP */ }
    }
}
