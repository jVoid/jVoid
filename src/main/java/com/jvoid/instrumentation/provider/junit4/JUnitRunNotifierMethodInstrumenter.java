package com.jvoid.instrumentation.provider.junit4;

import java.util.HashMap;
import java.util.Map;

import com.jvoid.instrumentation.JVoidInstrumentationHelperHolder;
import com.jvoid.instrumentation.provider.api.MethodInstrumenter;

import javassist.CannotCompileException;
import javassist.CtMethod;

/**
 * Method instrumenter for the JUnit 4 {@code RunNotifier}. It enables the tracking
 * of the status of the test, and the cooperation with the JUnit lifecycle.
 * Note: this is used indirectly by the Spock instrumentation provider as well.
 *
 */
public class JUnitRunNotifierMethodInstrumenter implements MethodInstrumenter {

    private final static Map<String, String> detectTestStatusMap = new HashMap<>();

    static {
        detectTestStatusMap.put("fireTestFailure", ".detectTestStatusFailure();");
        detectTestStatusMap.put("fireTestAssumptionFailed", ".detectTestStatusFailure();");
        detectTestStatusMap.put("fireTestIgnored", ".detectTestStatusSkip();");
        detectTestStatusMap.put("fireTestFinished", ".detectTestStatusComplete();");
    }

    @Override
    public void instrument(CtMethod ctMethod) throws CannotCompileException {
        String methodName = ctMethod.getName();
        if (detectTestStatusMap.containsKey(methodName)) {
            StringBuilder sb = new StringBuilder(1024);
            sb.append(JVoidInstrumentationHelperHolder.helperGetterRef() +
                      detectTestStatusMap.get(methodName));
            ctMethod.insertBefore(sb.toString());
        }
    }

    @Override
    public boolean shouldInstrument(CtMethod ctMethod) {
        return true;
    }

}
