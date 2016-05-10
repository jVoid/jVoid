package io.jvoid.instrumentation.provider.app;

import javassist.CtClass;
import javassist.CtField;
import javassist.CtMethod;

/**
 * Helper class for the implementation of the heuristics. It might grow and
 * hopefully be smarter and smarter.
 *
 */
public class AppHeuristicHelper {

    public static boolean isCGLIBProxy(CtClass ctClass) {
        String name = ctClass.getName();
        return name.contains("$$") || name.contains("CGLIB");
        
    }
    public static boolean isCGLIBProxy(CtMethod ctMethod) {
        String methodName = ctMethod.getLongName();
        return methodName.contains("$$") || methodName.contains("CGLIB");
    }

    public static boolean isGroovyCallSite(CtClass ctClass) {
        try {
            CtClass superclass = ctClass.getSuperclass();
            return superclass != null && superclass.getName().startsWith("org.codehaus.groovy.runtime.callsite");
        } catch (Exception e) {
            return false; // Being conservative here...
        }
    }

    public static boolean isJacocoField(CtField ctField) {
        String methodName = ctField.getName();
        return methodName.startsWith("$jacoco");
    }
    
    public static boolean isGeneratedField(CtField ctField) {
        String methodName = ctField.getName();
        return methodName.startsWith("$$");
    }

    public static boolean isJacocoMethod(CtMethod ctMethod) {
        String methodName = ctMethod.getLongName();
        return methodName.contains("$jacoco");
    }

}
