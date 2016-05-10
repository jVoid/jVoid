package com.jvoid.test.instrumentation.provider.app;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.codehaus.groovy.runtime.callsite.CallSiteArray;
import org.junit.Test;

import com.jvoid.instrumentation.provider.app.AppHeuristicHelper;
import com.jvoid.test.AbstractJVoidTest;

import javassist.CtClass;

public class AppHeuristicHelperTest extends AbstractJVoidTest {

    @Test
    public void testHeuristicCglib() throws Exception {
        CtClass cglibMock = classPool.get(ClassWithCglibMethods.class.getName());
        assertTrue(AppHeuristicHelper.isCGLIBProxy(cglibMock.getDeclaredMethod("this$$ShouldBeCaught")));
        assertTrue(AppHeuristicHelper.isCGLIBProxy(cglibMock.getDeclaredMethod("thatOneWithCGLIBShouldBeCaught")));
        assertFalse(AppHeuristicHelper.isCGLIBProxy(cglibMock.getDeclaredMethod("this$Should$Be$Fine")));
        cglibMock.prune();
    }

    @Test
    public void testHeuristicGroovyCallsite() throws Exception {
        CtClass groovyCallsiteMock = classPool.get(MockGroovyCallsite.class.getName());
        CtClass cglibMock = classPool.get(ClassWithCglibMethods.class.getName());
        assertTrue(AppHeuristicHelper.isGroovyCallSite(groovyCallsiteMock));
        assertFalse(AppHeuristicHelper.isGroovyCallSite(cglibMock));
        groovyCallsiteMock.prune();
        cglibMock.prune();
    }

    @Test
    public void testHeuristicJacoco() throws Exception {
        CtClass cglibMock = classPool.get(ClassWithCglibMethods.class.getName());
        assertTrue(AppHeuristicHelper.isJacocoMethod(cglibMock.getDeclaredMethod("hey$jacocoInit")));
        assertFalse(AppHeuristicHelper.isJacocoMethod(cglibMock.getDeclaredMethod("this$Should$Be$Fine")));
        cglibMock.prune();
    }

    // NOTE That if you put "CGLIB" capitalized, all methods match!
    protected static class ClassWithCglibMethods {
        public void this$$ShouldBeCaught() { }
        public void thatOneWithCGLIBShouldBeCaught() { }
        public void this$Should$Be$Fine() { }
        public void hey$jacocoInit() { }
    }

    protected static class MockGroovyCallsite extends org.codehaus.groovy.runtime.callsite.DummyCallSite {
        public MockGroovyCallsite(CallSiteArray array, int index, String name) {
            super(array, index, name);
        }
    }
}
