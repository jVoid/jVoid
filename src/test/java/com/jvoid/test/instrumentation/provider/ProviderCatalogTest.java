package com.jvoid.test.instrumentation.provider;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.jvoid.instrumentation.provider.ProviderCatalog;
import com.jvoid.instrumentation.provider.api.ClassHandler;
import com.jvoid.instrumentation.provider.api.InstrumentationProvider;
import com.jvoid.instrumentation.provider.api.MethodInstrumenter;
import com.jvoid.test.AbstractJVoidTest;

import javassist.CtClass;
import javassist.CtMethod;
import javassist.NotFoundException;

public class ProviderCatalogTest extends AbstractJVoidTest {

    private CtClass ctTestClass1, ctTestClass2;

    private ProviderCatalog providerCatalog;

    @Override
    @Before
    public void setUp() throws NotFoundException {
        ctTestClass1 = classPool.get(TestClass1.class.getName());
        ctTestClass2 = classPool.get(TestClass2.class.getName());
        providerCatalog = new ProviderCatalog();
        providerCatalog.addProvider(new Provider1());
        providerCatalog.addProvider(new Provider2());
        providerCatalog.addProvider(new Provider3());
    }

    @Override
    @After
    public void tearDown() {
        ctTestClass1.prune();
        ctTestClass2.prune();
    }

    @Test
    public void testGetProviderForClass() {
        List<InstrumentationProvider> providers = new ArrayList<>(providerCatalog.getProvidersForClass(ctTestClass1));
        assertEquals(2, providers.size());
        assertTrue(providers.get(0) instanceof Provider1);
        assertTrue(providers.get(1) instanceof Provider2);
        providers = new ArrayList<>(providerCatalog.getProvidersForClass(ctTestClass2));
        assertEquals(1, providers.size());
        assertTrue(providers.get(0) instanceof Provider3);
    }

    /*
     * ====================================== Helper mock classes
     * ======================================
     */

    // Mocked classes for getting ctClass
    private static class TestClass1 {
    }

    private static class TestClass2 {
    }

    private static abstract class AbstractMockProvider implements InstrumentationProvider {
        @Override
        public List<ClassHandler> getClassHandlers(CtClass ctClass) {
            return Collections.emptyList();
        }

        @Override
        public List<MethodInstrumenter> getMethodInstrumenters(CtMethod ctMethod) {
            return Collections.emptyList();
        }
    }

    private static class Provider1 extends AbstractMockProvider {
        @Override
        public boolean matches(CtClass ctClass) {
            return ctClass.getName().contains("TestClass1");
        }
    }

    private static class Provider2 extends AbstractMockProvider {
        @Override
        public boolean matches(CtClass ctClass) {
            return ctClass.getName().contains("TestClass1");
        }
    }

    private static class Provider3 extends AbstractMockProvider {
        @Override
        public boolean matches(CtClass ctClass) {
            return ctClass.getName().contains("TestClass2");
        }
    }

}
