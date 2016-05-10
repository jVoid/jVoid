package com.jvoid.instrumentation.provider.junit4;

import com.jvoid.instrumentation.JVoidInstrumentationHelperHolder;
import com.jvoid.instrumentation.provider.api.MethodInstrumenter;

import javassist.CannotCompileException;
import javassist.CtMethod;

/**
 * Tracker method for JUnit 4 tests. It notifies JVoid that a test is going
 * to be executed.
 *
 */
public class RunChildMethodInstrumenter implements MethodInstrumenter {

    @Override
    public void instrument(CtMethod ctMethod) throws CannotCompileException {
        if (ctMethod.getName().equals("runChild")) {
            StringBuilder sb = new StringBuilder(1024);
            sb.append("org.junit.runners.model.FrameworkMethod __jvoid_frameworkMethod = (org.junit.runners.model.FrameworkMethod) $1;");
            sb.append("org.junit.runner.notification.RunNotifier __jvoid_notifier = (org.junit.runner.notification.RunNotifier) $2;");
            sb.append("java.lang.reflect.Method __jvoid_javaMethod = __jvoid_frameworkMethod.getMethod();\n");
            sb.append("String featureId = (__jvoid_javaMethod.getDeclaringClass().getName() + \"#\" + __jvoid_javaMethod.getName());\n");
            sb.append(JVoidInstrumentationHelperHolder.helperGetterRef() + ".beginTest(featureId);\n");
            sb.append("if (!" + JVoidInstrumentationHelperHolder.helperGetterRef() + ".wasTestRelatedCodeModified(featureId)) {\n");
            sb.append("    org.junit.runner.Description __jvoid_description = describeChild(__jvoid_frameworkMethod);\n");
            sb.append("    __jvoid_notifier.fireTestIgnored(__jvoid_description);\n");
            sb.append("    return;\n");
            sb.append("}\n");
            ctMethod.insertBefore(sb.toString());
        }
    }

    @Override
    public boolean shouldInstrument(CtMethod ctMethod) {
        return true;
    }

}
