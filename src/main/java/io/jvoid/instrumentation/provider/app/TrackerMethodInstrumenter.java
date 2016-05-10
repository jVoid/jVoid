package io.jvoid.instrumentation.provider.app;

import com.google.inject.Inject;

import io.jvoid.configuration.JVoidConfiguration;
import io.jvoid.execution.JVoidExecutionContext;
import io.jvoid.instrumentation.JVoidInstrumentationHelperHolder;
import io.jvoid.instrumentation.provider.ProviderUtil;
import io.jvoid.instrumentation.provider.api.MethodInstrumenter;
import io.jvoid.metadata.checksum.CodeChecksummer;
import io.jvoid.metadata.model.JClass;
import io.jvoid.metadata.model.JMethod;
import io.jvoid.metadata.repositories.MethodsRepository;
import javassist.CannotCompileException;
import javassist.CtMethod;
import lombok.extern.slf4j.Slf4j;

/**
 * The {@code MethodInstrumenter} for the application classes. For each method,
 * we notify of its execution under the running test. In that way we are going to
 * be able to check whether a certain test involves this method, re-executing the
 * test in case the method checksum changes.
 *
 */
@Slf4j
public class TrackerMethodInstrumenter implements MethodInstrumenter {

    private CodeChecksummer codeChecksummer;

    private ProviderUtil providerUtil;

    private MethodsRepository methodsRepository;

    private JVoidExecutionContext jvoidExecutionContext;

    private JVoidConfiguration jvoidConfiguration;

    private AppInstrumentationJClassHolder jClassHolder;


    /**
     *
     * @param codeChecksummer
     * @param providerUtil
     * @param methodsRepository
     * @param jvoidExecutionContext
     * @param jvoidConfiguration
     * @param jClassHolder
     */
    @Inject
    public TrackerMethodInstrumenter(CodeChecksummer codeChecksummer, ProviderUtil providerUtil,
            MethodsRepository methodsRepository, JVoidExecutionContext jvoidExecutionContext,
            JVoidConfiguration jvoidConfiguration, AppInstrumentationJClassHolder jClassHolder) {
        this.codeChecksummer = codeChecksummer;
        this.providerUtil = providerUtil;
        this.methodsRepository = methodsRepository;
        this.jvoidExecutionContext = jvoidExecutionContext;
        this.jvoidConfiguration = jvoidConfiguration;
        this.jClassHolder = jClassHolder;
    }

    /**
     *
     */
    @Override
    public void instrument(CtMethod method) throws CannotCompileException {
        // If the method ha no body it is irrelevant to instrument it. 
        // It will be an interface method... the actual implementations will be instrumented :)
        if (method.isEmpty()) {
            return;
        }
        final Long currentExecutionId = jvoidExecutionContext.getCurrentExecutionId();

        String methodIdentifier = providerUtil.getMethodIdentifier(method);
        String checksum = codeChecksummer.checksum(method);

        JMethod jMethod = new JMethod();
        jMethod.setIdentifier(methodIdentifier);
        jMethod.setChecksum(checksum);
        jMethod.setExecutionId(currentExecutionId);
        jMethod = methodsRepository.add(jMethod);

        JClass jClass = jClassHolder.getJClassUnderInstrumentation();

        // Insert code to bind the method checksum to the current test execution
        String code = JVoidInstrumentationHelperHolder.helperGetterRef() + ".bindMethodToCurrentRunningTest("
                + jMethod.getId() + "L, " + jClass.getId() + "L);";
        try {
            method.insertBefore(code);
        } catch (Exception e) {
            log.error("Failed to instrument method '" + methodIdentifier + "': " + e.getMessage(), e);
            throw e;
        }
    }

    /**
     *
     */
    @Override
    public boolean shouldInstrument(CtMethod ctMethod) {
        if (jvoidConfiguration.heuristicExcludeCglib() && AppHeuristicHelper.isCGLIBProxy(ctMethod)) {
            return false;
        }
        if (jvoidConfiguration.heuristicExcludeJacoco() && AppHeuristicHelper.isJacocoMethod(ctMethod)) {
            return false;
        }
        String methodName = ctMethod.getLongName();
        String includes = jvoidConfiguration.includes();
        String excludes = jvoidConfiguration.excludes();
        boolean instrumentMethod = true;
        if (excludes != null && !excludes.isEmpty()) {
            instrumentMethod = !methodName.matches(excludes);
        }
        if (!instrumentMethod && includes != null && !includes.isEmpty()) {
            instrumentMethod = methodName.matches(includes);
        }
        return instrumentMethod;
    }

}
