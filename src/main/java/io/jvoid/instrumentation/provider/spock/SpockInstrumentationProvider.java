package io.jvoid.instrumentation.provider.spock;

import java.util.Collections;
import java.util.List;

import com.google.inject.Inject;

import io.jvoid.instrumentation.provider.api.ClassHandler;
import io.jvoid.instrumentation.provider.api.InstrumentationProvider;
import io.jvoid.instrumentation.provider.api.MethodInstrumenter;
import javassist.CtClass;
import javassist.CtMethod;

/**
 * JVoid instrumentation provider for Spock Framework. It indirectly uses also
 * the JUnit instrumentation to realize the full JVoid functionalities.
 *
 */
public class SpockInstrumentationProvider implements InstrumentationProvider {

    private MethodInstrumenter runFeatureMethodInstrumenter;

    @Inject
    public SpockInstrumentationProvider(RunFeatureMethodInstrumenter runFeatureMethodInstrumenter) {
        this.runFeatureMethodInstrumenter = runFeatureMethodInstrumenter;
    }

    @Override
    public boolean matches(CtClass ctClass) {
        return ctClass.getName().contains("org.spockframework.runtime.BaseSpecRunner");
    }

    @Override
    public List<ClassHandler> getClassHandlers(CtClass ctClass) {
        return Collections.emptyList();
    }

    @Override
    public List<MethodInstrumenter> getMethodInstrumenters(CtMethod ctMethod) {
        if (ctMethod.getName().equals("runFeature")) {
            return Collections.singletonList(runFeatureMethodInstrumenter);
        }
        return Collections.emptyList();
    }

}
