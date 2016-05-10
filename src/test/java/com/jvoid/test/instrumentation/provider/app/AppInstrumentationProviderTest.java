package com.jvoid.test.instrumentation.provider.app;

import static org.hamcrest.Matchers.instanceOf;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.google.inject.Inject;
import com.jvoid.instrumentation.provider.app.AppClassHandler;
import com.jvoid.instrumentation.provider.app.AppInstrumentationProvider;
import com.jvoid.instrumentation.provider.app.TrackerMethodInstrumenter;
import com.jvoid.test.AbstractJVoidTest;

import javassist.CtClass;
import javassist.CtMethod;
import javassist.NotFoundException;

public class AppInstrumentationProviderTest extends AbstractJVoidTest {

    @Inject
    private AppInstrumentationProvider appProvider;

    private CtClass ctBaseSpecRunner;
    private CtClass ctFakeAppClass;

    @Override
    @Before
    public void setUp() throws Exception {
        super.setUp();
        ctBaseSpecRunner = classPool.get("org.spockframework.runtime.BaseSpecRunner");
        ctFakeAppClass = classPool.get("com.jvoid.test.instrumentation.provider.app.fake.FakeAppClass");
    }

    @Override
    @After
    public void tearDown() {
        ctBaseSpecRunner.prune();
        ctFakeAppClass.prune();
    }

    @Test
    public void testMatches() {
        assertTrue(appProvider.matches(ctFakeAppClass));
        assertFalse(appProvider.matches(ctBaseSpecRunner));
    }

    @Test
    public void testGetClassHandlers() {
        assertThat(appProvider.getClassHandlers(ctFakeAppClass).get(0), instanceOf(AppClassHandler.class));
        // No distinctions here per class, because we rely on `matches`
        assertThat(appProvider.getClassHandlers(ctBaseSpecRunner).get(0), instanceOf(AppClassHandler.class));
    }

    @Test
    public void testGetMethodInstrumenters() throws NotFoundException {
        CtMethod randomMethod = ctFakeAppClass.getMethods()[0];
        assertThat(appProvider.getMethodInstrumenters(randomMethod).get(0), instanceOf(TrackerMethodInstrumenter.class));
        // No distinction here as well, since we rely on `matches`
        CtMethod methodOfOtherClass = ctBaseSpecRunner.getMethods()[0];
        assertThat(appProvider.getMethodInstrumenters(methodOfOtherClass).get(0), instanceOf(TrackerMethodInstrumenter.class));
    }

}
