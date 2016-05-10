package io.jvoid.instrumentation.provider.api;

import javassist.CannotCompileException;
import javassist.CtClass;

/**
 * Class able to handle a class and perform any modification/tracking on it.
 *
 */
public interface ClassHandler {

    void handleClass(CtClass ctClass) throws CannotCompileException;

}
