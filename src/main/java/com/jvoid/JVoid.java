package com.jvoid;

import java.lang.instrument.Instrumentation;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.jvoid.configuration.JVoidConfigurationService;
import com.jvoid.exceptions.JVoidConfigurationException;
import com.jvoid.execution.JVoidExecutionContext;
import com.jvoid.guice.JVoidModule;
import com.jvoid.instrumentation.JVoidClassFileTransformer;
import com.jvoid.instrumentation.JVoidInstrumentationHelper;
import com.jvoid.instrumentation.JVoidInstrumentationHelperHolder;

import lombok.extern.slf4j.Slf4j;

/**
 * JVoid main class containing the premain for the Java
 * agent that is responsible for the runtime classes instrumentation.
 */
@Slf4j
public class JVoid {

    /**
     * The JVoid agent tracks the classes and methods that are involved in unit-testing
     * per execution. That allows to skip the tests that have not been affected by source
     * code local modifications. 
     *
     */
    public static void premain(String agentArguments, Instrumentation instrumentation) {
        log.info("JVoid getting ready to jvoiding your code!");

        Injector injector = Guice.createInjector(new JVoidModule());

        JVoidConfigurationService jvoidConfigurationService = injector.getInstance(JVoidConfigurationService.class);

        try {
            jvoidConfigurationService.loadConfiguration(agentArguments);
        } catch (JVoidConfigurationException e) {
            System.exit(-1);
        }

        JVoidExecutionContext jvoidExecutionContext = injector.getInstance(JVoidExecutionContext.class);
        jvoidExecutionContext.startNewExecution();

        JVoidInstrumentationHelper helper = injector.getInstance(JVoidInstrumentationHelper.class);
        JVoidInstrumentationHelperHolder.getInstance().set(helper);

        JVoidClassFileTransformer transformer = injector.getInstance(JVoidClassFileTransformer.class);
        instrumentation.addTransformer(transformer);
    }

}
