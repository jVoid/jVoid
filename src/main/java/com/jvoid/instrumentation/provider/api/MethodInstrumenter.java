package com.jvoid.instrumentation.provider.api;

import javassist.CannotCompileException;
import javassist.CtMethod;

/**
 * Class able to perform method instrumentation.
 *
 */
public interface MethodInstrumenter {

    void instrument(CtMethod ctMethod) throws CannotCompileException;

    boolean shouldInstrument(CtMethod ctMethod);

}
