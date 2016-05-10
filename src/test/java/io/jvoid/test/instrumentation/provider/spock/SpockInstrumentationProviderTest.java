package io.jvoid.test.instrumentation.provider.spock;

import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.google.inject.Inject;

import io.jvoid.instrumentation.provider.spock.RunFeatureMethodInstrumenter;
import io.jvoid.instrumentation.provider.spock.SpockInstrumentationProvider;
import io.jvoid.test.AbstractJVoidTest;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.NotFoundException;

public class SpockInstrumentationProviderTest extends AbstractJVoidTest {

    @Inject
    private RunFeatureMethodInstrumenter runFeatureMethodInstrumenter;

    private SpockInstrumentationProvider spockProvider;

    private CtClass ctBaseSpecRunner;
    private CtClass ctParameterizedSpecRunner;

    @Override
    @Before
    public void setUp() throws Exception {
        super.setUp();
        ctBaseSpecRunner = classPool.get("org.spockframework.runtime.BaseSpecRunner");
        ctParameterizedSpecRunner = classPool
                .get("org.spockframework.runtime.ParameterizedSpecRunner");

        spockProvider = new SpockInstrumentationProvider(runFeatureMethodInstrumenter);
    }

    @Override
    @After
    public void tearDown() {
        ctBaseSpecRunner.prune();
        ctParameterizedSpecRunner.prune();
    }

    @Test
    public void testMatches() {
        assertTrue(spockProvider.matches(ctBaseSpecRunner));
        assertFalse(spockProvider.matches(ctParameterizedSpecRunner));
    }

    @Test
    public void testGetClassHandlers() {
        assertThat(spockProvider.getClassHandlers(ctBaseSpecRunner), is(empty()));
    }

    @Test
    public void testGetMethodInstrumenters() throws NotFoundException {
        CtMethod randomMethod = ctBaseSpecRunner.getMethods()[0];
        assertThat(spockProvider.getMethodInstrumenters(randomMethod), is(empty()));
        CtMethod runFeatureMethod = ctBaseSpecRunner.getDeclaredMethod("runFeature");
        assertThat(spockProvider.getMethodInstrumenters(runFeatureMethod).size(), is(1));
        assertThat(spockProvider.getMethodInstrumenters(runFeatureMethod).get(0),
                instanceOf(RunFeatureMethodInstrumenter.class));
    }
}
