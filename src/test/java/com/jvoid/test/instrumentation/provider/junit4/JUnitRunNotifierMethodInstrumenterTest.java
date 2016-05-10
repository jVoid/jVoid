package com.jvoid.test.instrumentation.provider.junit4;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.junit.Test;

import com.jvoid.instrumentation.JVoidInstrumentationHelper;
import com.jvoid.instrumentation.JVoidInstrumentationHelperHolder;
import com.jvoid.instrumentation.provider.junit4.JUnitRunNotifierMethodInstrumenter;
import com.jvoid.test.AbstractJVoidTest;

import javassist.CtClass;
import javassist.CtMethod;
import javassist.CtNewMethod;

public class JUnitRunNotifierMethodInstrumenterTest extends AbstractJVoidTest {

    @Test
    public void testInstrument() throws Exception {
        JVoidInstrumentationHelper mockHelper = mock(JVoidInstrumentationHelper.class);
        JVoidInstrumentationHelperHolder.getInstance().set(mockHelper);

        // We need to forge a class at runtime, because reloading the same class with changes in the bytecode is really tricky :)
        CtClass ctPrototypeClass = classPool.get(MockRunNotifierPrototype.class.getName());
        CtClass ctForgedClass = classPool.makeClass("MockRunNotifier", ctPrototypeClass);
        ctForgedClass.addMethod(CtNewMethod.make("public void fireTestFailure() {  }", ctForgedClass));
        ctForgedClass.addMethod(CtNewMethod.make("public void fireTestAssumptionFailed() {  }", ctForgedClass));
        ctForgedClass.addMethod(CtNewMethod.make("public void fireTestIgnored() {  }", ctForgedClass));
        ctForgedClass.addMethod(CtNewMethod.make("public void fireTestFinished() {  }", ctForgedClass));

        // Instrumentation
        CtMethod fireTestFailureMethod = ctForgedClass.getDeclaredMethod("fireTestFailure");
        CtMethod fireTestAssumptionFailedMethod = ctForgedClass.getDeclaredMethod("fireTestAssumptionFailed");
        CtMethod fireTestIgnoredMethod = ctForgedClass.getDeclaredMethod("fireTestIgnored");
        CtMethod fireTestFinishedMethod = ctForgedClass.getDeclaredMethod("fireTestFinished");

        JUnitRunNotifierMethodInstrumenter instrumenter = new JUnitRunNotifierMethodInstrumenter();
        instrumenter.instrument(fireTestFailureMethod);
        instrumenter.instrument(fireTestAssumptionFailedMethod);
        instrumenter.instrument(fireTestIgnoredMethod);
        instrumenter.instrument(fireTestFinishedMethod);
        
        // Verify instrumentation
        MockRunNotifierPrototype mockRunNotifier = (MockRunNotifierPrototype) classPool.toClass(ctForgedClass).newInstance();
        mockRunNotifier.fireTestFailure();
        mockRunNotifier.fireTestAssumptionFailed();
        verify(mockHelper, times(2)).detectTestStatusFailure();
        mockRunNotifier.fireTestIgnored();
        verify(mockHelper, times(1)).detectTestStatusSkip();
        mockRunNotifier.fireTestFinished();
        verify(mockHelper, times(1)).detectTestStatusComplete();

        ctForgedClass.prune();
        ctPrototypeClass.prune();
    }

    @Test
    public void testShouldInstrument() {
        assertTrue(new JUnitRunNotifierMethodInstrumenter().shouldInstrument(null));
    }

    public static class MockRunNotifierPrototype {
        public void fireTestFailure() {  }
        public void fireTestAssumptionFailed() {  }
        public void fireTestIgnored() {  }
        public void fireTestFinished() {  }
    }
}
