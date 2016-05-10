package io.jvoid.instrumentation.provider.api;

import java.util.List;

import javassist.CtClass;
import javassist.CtMethod;

/**
 * Describes a JVoid instrumentation provider. A provider is able to tell whether it
 * is able to instrument a certain class (i.e., {@code matches} method) and returns
 * all the {@code ClassHandler}s and {@code MethodInstrumenter}s needed to perform
 * the instrumentation.
 *
 */
public interface InstrumentationProvider {

    boolean matches(CtClass ctClass);

    List<ClassHandler> getClassHandlers(CtClass ctClass);

    List<MethodInstrumenter> getMethodInstrumenters(CtMethod ctMethod);

}
