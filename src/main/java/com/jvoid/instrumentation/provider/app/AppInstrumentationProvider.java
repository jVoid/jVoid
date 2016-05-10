package com.jvoid.instrumentation.provider.app;

import java.util.Collections;
import java.util.List;

import com.google.inject.Inject;
import com.jvoid.configuration.JVoidConfiguration;
import com.jvoid.instrumentation.provider.api.ClassHandler;
import com.jvoid.instrumentation.provider.api.InstrumentationProvider;
import com.jvoid.instrumentation.provider.api.MethodInstrumenter;

import javassist.CtClass;
import javassist.CtMethod;

/**
 * JVoid instrumentation provider for the application classes.
 *
 */
public class AppInstrumentationProvider implements InstrumentationProvider {

    private AppClassHandler appClassHandler;
    private MethodInstrumenter trackerMethodInstrumenter;
    private JVoidConfiguration jvoidConfiguration;

    @Inject
    public AppInstrumentationProvider(AppClassHandler appClassHandler,
            TrackerMethodInstrumenter trackerMethodInstrumenter, JVoidConfiguration jvoidConfiguration) {
        super();
        this.appClassHandler = appClassHandler;
        this.trackerMethodInstrumenter = trackerMethodInstrumenter;
        this.jvoidConfiguration = jvoidConfiguration;
    }

    @Override
    public boolean matches(CtClass ctClass) {
        JVoidConfiguration config = jvoidConfiguration;
        boolean matches = ctClass.getName().startsWith(config.basePackage());
        if (jvoidConfiguration.heuristicExcludeGroovyCallSite()) {
            matches = matches && !AppHeuristicHelper.isGroovyCallSite(ctClass);
        }
        return matches;
    }

    @Override
    public List<ClassHandler> getClassHandlers(CtClass ctClass) {
        return Collections.<ClassHandler> singletonList(appClassHandler);
    }

    @Override
    public List<MethodInstrumenter> getMethodInstrumenters(CtMethod ctMethod) {
        return Collections.singletonList(trackerMethodInstrumenter);
    }
}
