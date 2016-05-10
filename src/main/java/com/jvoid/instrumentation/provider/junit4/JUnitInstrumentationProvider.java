package com.jvoid.instrumentation.provider.junit4;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.inject.Inject;
import com.jvoid.instrumentation.provider.api.ClassHandler;
import com.jvoid.instrumentation.provider.api.InstrumentationProvider;
import com.jvoid.instrumentation.provider.api.MethodInstrumenter;

import javassist.CtClass;
import javassist.CtMethod;

/**
 * JVoid instrumentation provider for JUnit 4. It will integrate with JUnit 4 test
 * lifecycle, being able to track the status of the tests and skipping them in case
 * their execution is not necessary because no changes have been made to the classes
 * of the application.
 *
 */
public class JUnitInstrumentationProvider implements InstrumentationProvider {

    private Map<String, MethodInstrumenter> instrumenterMap;

    @Inject
    public JUnitInstrumentationProvider(JUnitRunNotifierMethodInstrumenter junitRunNotifierMethodInstrumenter,
                                        RunChildMethodInstrumenter runChildMethodInstrumenter) {
        this.instrumenterMap = new HashMap<>();
        this.instrumenterMap.put("org.junit.runner.notification.RunNotifier", junitRunNotifierMethodInstrumenter);
        this.instrumenterMap.put("org.junit.runners.BlockJUnit4ClassRunner", runChildMethodInstrumenter);
        // Let's be friends with Spring ;)
        this.instrumenterMap.put("org.springframework.test.context.junit4.SpringJUnit4ClassRunner", runChildMethodInstrumenter);
    }

    @Override
    public boolean matches(CtClass ctClass) {
        return instrumenterMap.containsKey(ctClass.getName());
    }

    @Override
    public List<ClassHandler> getClassHandlers(CtClass ctClass) {
        return Collections.emptyList();
    }

    @Override
    public List<MethodInstrumenter> getMethodInstrumenters(CtMethod ctMethod) {
        return Collections.singletonList(instrumenterMap.get(ctMethod.getDeclaringClass().getName()));
    }

}
