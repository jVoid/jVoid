package io.jvoid.instrumentation.provider.spock;

import io.jvoid.instrumentation.JVoidInstrumentationHelperHolder;
import io.jvoid.instrumentation.provider.api.MethodInstrumenter;
import javassist.CannotCompileException;
import javassist.CtMethod;

/**
 * Tracker method for Spock tests. It notifies JVoid that a test is going
 * to be executed.
 *
 */
public class RunFeatureMethodInstrumenter implements MethodInstrumenter {

    @Override
    public void instrument(CtMethod method) throws CannotCompileException {
        // Right now is only called for the runFeature method
        StringBuilder sb = new StringBuilder(1024);
        // Get the String identifier of the spec
        sb.append("String featureId = currentFeature.getFeatureMethod().getDescription().toString();\n");
        sb.append(JVoidInstrumentationHelperHolder.helperGetterRef() + ".beginTest(featureId);\n");
        sb.append("if (!" + JVoidInstrumentationHelperHolder.helperGetterRef() + ".wasTestRelatedCodeModified(featureId)) {\n");
        sb.append("    supervisor.featureSkipped(currentFeature); return;\n");
        sb.append("}\n");
        method.insertBefore(sb.toString());
    }

    @Override
    public boolean shouldInstrument(CtMethod ctMethod) {
        return true;
    }

}
