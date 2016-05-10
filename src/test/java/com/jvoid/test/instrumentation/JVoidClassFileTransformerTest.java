package com.jvoid.test.instrumentation;

import static org.junit.Assert.assertNull;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

import java.util.Arrays;

import org.junit.Assert;
import org.junit.Test;

import com.jvoid.instrumentation.JVoidClassFileTransformer;
import com.jvoid.instrumentation.provider.ProviderCatalog;
import com.jvoid.instrumentation.provider.api.ClassHandler;
import com.jvoid.instrumentation.provider.api.InstrumentationProvider;
import com.jvoid.instrumentation.provider.api.MethodInstrumenter;
import com.jvoid.test.AbstractJVoidTest;

import javassist.CtClass;
import javassist.CtMethod;

public class JVoidClassFileTransformerTest extends AbstractJVoidTest {

    @Test
    public void testTransformMethod() throws Exception {
        // Get the bytecode of the mock class under test
        CtClass ctTestClass = classPool.get(TestClass.class.getName());
        byte[] bytecode = ctTestClass.toBytecode();
        ctTestClass.defrost();
        String className = TestClass.class.getName().replace(".", "/");

        // Prepare the mock to check against after the transformation
        InstrumentationProvider matchedProvider_1 = mock(InstrumentationProvider.class);
        ClassHandler classHandler_1_1 = mock(ClassHandler.class);
        ClassHandler classHandler_1_2 = mock(ClassHandler.class);
        MethodInstrumenter methodInstrumenter_1_1 = mock(MethodInstrumenter.class);
        when(matchedProvider_1.matches(any(CtClass.class))).thenReturn(true); // match
        when(matchedProvider_1.getClassHandlers(any(CtClass.class))).thenReturn(Arrays.asList(classHandler_1_1, classHandler_1_2));
        when(matchedProvider_1.getMethodInstrumenters(any(CtMethod.class))).thenReturn(Arrays.asList(methodInstrumenter_1_1));
        when(methodInstrumenter_1_1.shouldInstrument(any(CtMethod.class))).thenReturn(true);

        InstrumentationProvider matchedProvider_2 = mock(InstrumentationProvider.class);
        ClassHandler classHandler_2_1 = mock(ClassHandler.class);
        MethodInstrumenter methodInstrumenter_2_1 = mock(MethodInstrumenter.class);
        when(methodInstrumenter_2_1.shouldInstrument(any(CtMethod.class))).thenReturn(true);
        MethodInstrumenter methodInstrumenter_2_2 = mock(MethodInstrumenter.class);
        when(methodInstrumenter_2_2.shouldInstrument(any(CtMethod.class))).thenReturn(true);
        when(matchedProvider_2.matches(any(CtClass.class))).thenReturn(true); // match
        when(matchedProvider_2.getClassHandlers(any(CtClass.class))).thenReturn(Arrays.asList(classHandler_2_1));
        when(matchedProvider_2.getMethodInstrumenters(any(CtMethod.class)))
                .thenReturn(Arrays.asList(methodInstrumenter_2_1, methodInstrumenter_2_2));

        InstrumentationProvider notMatchedProvider_3 = mock(InstrumentationProvider.class);
        ClassHandler classHandler_3_1 = mock(ClassHandler.class);
        MethodInstrumenter methodInstrumenter_3_1 = mock(MethodInstrumenter.class);
        when(methodInstrumenter_3_1.shouldInstrument(any(CtMethod.class))).thenReturn(false);
        when(notMatchedProvider_3.matches(any(CtClass.class))).thenReturn(false); // non-match
        when(notMatchedProvider_3.getClassHandlers(any(CtClass.class))).thenReturn(Arrays.asList(classHandler_3_1));
        when(notMatchedProvider_3.getMethodInstrumenters(any(CtMethod.class))).thenReturn(Arrays.asList(methodInstrumenter_3_1));

        ProviderCatalog mockCatalog = new ProviderCatalog();
        mockCatalog.addProvider(matchedProvider_1);
        mockCatalog.addProvider(matchedProvider_2);
        mockCatalog.addProvider(notMatchedProvider_3);
        new JVoidClassFileTransformer(mockCatalog).transform(null, className, TestClass.class, null, bytecode);

        verify(matchedProvider_1, times(1)).matches(any(CtClass.class));
        verify(matchedProvider_2, times(1)).matches(any(CtClass.class));
        verify(notMatchedProvider_3, times(1)).matches(any(CtClass.class));

        verify(classHandler_1_1, times(1)).handleClass(any(CtClass.class));
        verify(classHandler_1_2, times(1)).handleClass(any(CtClass.class));
        verify(classHandler_2_1, times(1)).handleClass(any(CtClass.class));
        verifyZeroInteractions(classHandler_3_1);

        verify(methodInstrumenter_1_1, times(1)).shouldInstrument(any(CtMethod.class));
        verify(methodInstrumenter_1_1, times(1)).instrument(any(CtMethod.class));
        verify(methodInstrumenter_2_1, times(1)).shouldInstrument(any(CtMethod.class));
        verify(methodInstrumenter_2_1, times(1)).instrument(any(CtMethod.class));
        verify(methodInstrumenter_2_2, times(1)).shouldInstrument(any(CtMethod.class));
        verify(methodInstrumenter_2_2, times(1)).instrument(any(CtMethod.class));
        verifyZeroInteractions(methodInstrumenter_3_1);

        ctTestClass.prune();
    }

    @Test
    public void testClassNotFound() throws Exception {
        ProviderCatalog mockCatalog = new ProviderCatalog();
        byte[] result = new JVoidClassFileTransformer(mockCatalog).transform(null, "prova/Clazz", TestClass.class, null, new byte[0]);
        Assert.assertNull(result);
    }

    @Test
    public void testReturnBufferAsItIsInCaseOfNullClassName() throws Exception {
        ProviderCatalog mockCatalog = new ProviderCatalog();
        byte[] buffer = new byte[3];
        buffer[0] = 1;
        buffer[1] = 2;
        buffer[2] = 3;
        byte[] result = new JVoidClassFileTransformer(mockCatalog).transform(null, null, TestClass.class, null, buffer);
        assertNull(result); // No modifications happened
    }

    private static class TestClass {
        @SuppressWarnings("unused")
        public void testMethod3() {
            // NOP
        }
    }
}
