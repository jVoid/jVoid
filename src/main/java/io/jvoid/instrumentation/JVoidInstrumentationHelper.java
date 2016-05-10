package io.jvoid.instrumentation;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import io.jvoid.execution.JVoidExecutionContext;
import io.jvoid.metadata.model.JTest;
import io.jvoid.metadata.repositories.TestsRepository;

/**
 * All the injected code used to instrument runtime classes will refer to this
 * class to perform various tasks.
 *
 */
@Singleton
public class JVoidInstrumentationHelper {

    private TestRelatedCodeModificationChecker testRelatedCodeModificationChecker;

    private TestsRepository testsRepository;
    private JVoidExecutionContext jvoidExecutionContext;

    /**
     *
     * @param testRelatedCodeModificationChecker
     * @param testsRepository
     * @param jvoidExecutionContext
     */
    @Inject
    public JVoidInstrumentationHelper(TestRelatedCodeModificationChecker testRelatedCodeModificationChecker,
            TestsRepository testsRepository, JVoidExecutionContext jvoidExecutionContext) {
        super();
        this.testRelatedCodeModificationChecker = testRelatedCodeModificationChecker;
        this.testsRepository = testsRepository;
        this.jvoidExecutionContext = jvoidExecutionContext;
    }

    /**
     *
     * @param testIdentifier
     * @return
     */
    public boolean wasTestRelatedCodeModified(String testIdentifier) {
        return testRelatedCodeModificationChecker.wasTestRelatedCodeModified(testIdentifier);
    }

    /**
     *
     */
    // Errors are also considered failures
    public void detectTestStatusFailure() {
        JTest currentTest = jvoidExecutionContext.getRunningTest();
        currentTest.setRunStatus(JTest.RUN_STATUS_FAILED);
        testsRepository.update(currentTest);
    }

    /**
     *
     */
    public void detectTestStatusSkip() {
        JTest currentTest = jvoidExecutionContext.getRunningTest();
        currentTest.setRunStatus(JTest.RUN_STATUS_SKIPPED);
        testsRepository.update(currentTest);
    }

    /**
     *
     */
    public void detectTestStatusComplete() {
        JTest currentTest = jvoidExecutionContext.getRunningTest();
        if (currentTest.getRunStatus().equals(JTest.RUN_STATUS_RUNNING)) {
            currentTest.setRunStatus(JTest.RUN_STATUS_RUN);
            testsRepository.update(currentTest);
        }
    }

    /**
     *
     * @param jmethodId
     * @param jclassId
     */
    public void bindMethodToCurrentRunningTest(long jmethodId, long jclassId) {
        JTest currentRunningTest = jvoidExecutionContext.getRunningTest();
        if (currentRunningTest == null) {
            return;
        }

        testsRepository.linkMethodAndClass(currentRunningTest, jmethodId, jclassId);
    }

    /**
     *
     * @param testIdentifier
     * @return
     */
    public JTest beginTest(String testIdentifier) {
        Long executionId = jvoidExecutionContext.getCurrentExecutionId();

        // Check whether the test has already started
        JTest jtest = testsRepository.findByIdenfifierAndExecutionId(testIdentifier, executionId);
        if (jtest != null) {
            if (jtest.getRunStatus().equals(JTest.RUN_STATUS_RUNNING)) {
                return jtest; // Test already started...
            }
            // else: we are in an inconsistent state...
            throw new RuntimeException("The test supposed to be running is in an inconsistent state");
        }

        jtest = new JTest();
        jtest.setIdentifier(testIdentifier);
        jtest.setExecutionId(executionId);
        jtest.setRunStatus(JTest.RUN_STATUS_RUNNING);

        jtest = testsRepository.add(jtest);

        jvoidExecutionContext.setRunningTest(jtest);

        return jtest;
    }

}
