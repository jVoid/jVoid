package com.jvoid.instrumentation.provider;

import com.google.inject.Singleton;

import javassist.CtClass;
import javassist.CtConstructor;
import javassist.CtMethod;

/**
 * Utility class used by various providers to determine the class/method/constructor
 * identifiers used for semantically identify features and methods.
 *
 */
@Singleton
public class ProviderUtil {

    public String getClassIdentifier(CtClass clazz) {
        return clazz.getName();
    }

    public String getConstructorIdentifier(CtConstructor constructor) {
        return constructor.getLongName();
    }

    public String getMethodIdentifier(CtMethod method) {
        return method.getLongName();
    }

}
