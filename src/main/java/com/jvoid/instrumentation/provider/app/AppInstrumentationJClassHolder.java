package com.jvoid.instrumentation.provider.app;

import com.google.inject.Singleton;
import com.jvoid.metadata.model.JClass;

/**
 * This class is used to keep track of the current JClass object
 * created by the {@code AppClassHandler} and useful in the
 * {@code TrackerMethodInstrumenter} in order to avoid multiple
 * queries to the DB.
 *
 */
@Singleton
public class AppInstrumentationJClassHolder {

    private ThreadLocal<JClass> jClassUnderInstrumentation = new ThreadLocal<>();

    public JClass getJClassUnderInstrumentation() {
        return jClassUnderInstrumentation.get();
    }

    public void setJClassUnderInstrumentation(JClass jClass) {
        jClassUnderInstrumentation.set(jClass);
    }
}
