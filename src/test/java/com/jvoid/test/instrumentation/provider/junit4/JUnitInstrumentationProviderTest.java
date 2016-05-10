package com.jvoid.test.instrumentation.provider.junit4;

import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.google.inject.Inject;
import com.jvoid.instrumentation.provider.junit4.JUnitInstrumentationProvider;
import com.jvoid.instrumentation.provider.junit4.JUnitRunNotifierMethodInstrumenter;
import com.jvoid.instrumentation.provider.junit4.RunChildMethodInstrumenter;
import com.jvoid.test.AbstractJVoidTest;

import javassist.CtClass;
import javassist.CtMethod;
import javassist.NotFoundException;

public class JUnitInstrumentationProviderTest extends AbstractJVoidTest {

    @Inject
    private JUnitRunNotifierMethodInstrumenter runNotifierMethodInstrumenter;

    @Inject
    private RunChildMethodInstrumenter runChildMethodInstrumenter;

    private JUnitInstrumentationProvider junitProvider;

    private CtClass ctRunNotifier;
    private CtClass ctParentRunner;
    private CtClass ctParameterizedSpecRunner;

    @Override
    @Before
    public void setUp() throws Exception {
        super.setUp();
        ctRunNotifier = classPool.get("org.junit.runner.notification.RunNotifier");
        ctParentRunner = classPool.get("org.junit.runners.BlockJUnit4ClassRunner");
        ctParameterizedSpecRunner = classPool
                .get("org.spockframework.runtime.ParameterizedSpecRunner");

        junitProvider = new JUnitInstrumentationProvider(runNotifierMethodInstrumenter,
                runChildMethodInstrumenter);
    }

    @Override
    @After
    public void tearDown() {
        ctRunNotifier.prune();
        ctParentRunner.prune();
        ctParameterizedSpecRunner.prune();
    }

    @Test
    public void testMatches() {
        assertTrue(junitProvider.matches(ctRunNotifier));
        assertFalse(junitProvider.matches(ctParameterizedSpecRunner));
    }

    @Test
    public void testGetClassHandlers() {
        assertThat(junitProvider.getClassHandlers(ctRunNotifier), is(empty()));
    }

    @Test
    public void testGetMethodInstrumenters() throws NotFoundException {
        CtMethod randomMethod = ctRunNotifier.getMethods()[0];
        assertNull(junitProvider.getMethodInstrumenters(randomMethod).get(0));

        CtMethod runNotifierMethod = ctRunNotifier.getDeclaredMethod("fireTestFailure");
        assertThat(junitProvider.getMethodInstrumenters(runNotifierMethod).size(), is(1));
        assertThat(junitProvider.getMethodInstrumenters(runNotifierMethod).get(0),
                instanceOf(JUnitRunNotifierMethodInstrumenter.class));

        CtMethod parentRunnerMethod = ctParentRunner.getDeclaredMethod("runChild");
        assertThat(junitProvider.getMethodInstrumenters(parentRunnerMethod).size(), is(1));
        assertThat(junitProvider.getMethodInstrumenters(parentRunnerMethod).get(0),
                instanceOf(RunChildMethodInstrumenter.class));
    }
}
