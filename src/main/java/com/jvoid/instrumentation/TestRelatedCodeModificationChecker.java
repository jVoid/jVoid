package com.jvoid.instrumentation;

import java.util.Map;
import java.util.Map.Entry;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.jvoid.execution.JVoidExecutionContext;
import com.jvoid.metadata.model.ChecksumAware;
import com.jvoid.metadata.model.JClass;
import com.jvoid.metadata.model.JClassConstructor;
import com.jvoid.metadata.model.JClassStaticBlock;
import com.jvoid.metadata.model.JMethod;
import com.jvoid.metadata.model.JTest;
import com.jvoid.metadata.repositories.ClassConstructorsRepository;
import com.jvoid.metadata.repositories.ClassStaticBlocksRepository;
import com.jvoid.metadata.repositories.ClassesRepository;
import com.jvoid.metadata.repositories.MethodsRepository;
import com.jvoid.metadata.repositories.TestsRepository;

import lombok.extern.slf4j.Slf4j;

/**
 * Helper class used to determine whether the code related to a particular
 * test has been modified or not. In case, we'll need to re-execute the test.
 * In case no modifications happened, then we can safely skip the test.
 *
 */
@Slf4j
@Singleton
public class TestRelatedCodeModificationChecker {

    private ClassStaticBlocksRepository classStaticBlocksRepository;
    private ClassConstructorsRepository classConstructorsRepository;
    private MethodsRepository methodsRepository;
    private ClassesRepository classesRepository;
    private TestsRepository testsRepository;
    private JVoidExecutionContext jvoidExecutionContext;

    @Inject
    public TestRelatedCodeModificationChecker(
            ClassStaticBlocksRepository classStaticBlocksRepository,
            ClassConstructorsRepository classConstructorsRepository,
            MethodsRepository methodsRepository, ClassesRepository classesRepository,
            TestsRepository testsRepository, JVoidExecutionContext jvoidExecutionContext) {
        this.classStaticBlocksRepository = classStaticBlocksRepository;
        this.classConstructorsRepository = classConstructorsRepository;
        this.methodsRepository = methodsRepository;
        this.classesRepository = classesRepository;
        this.testsRepository = testsRepository;
        this.jvoidExecutionContext = jvoidExecutionContext;
    }

    /**
     *
     * @param testIdentifier
     * @return
     */
    public boolean wasTestRelatedCodeModified(String testIdentifier) {
        // We look for a previous completed execution of the current feature (identified by
        // `testIdentifier`). In case we don't find any, we definitely want to execute it.
        JTest previouslyExecutedTest = testsRepository
                .findLatestExecutedByIdenfifier(testIdentifier);
        if (previouslyExecutedTest == null) {
            // 1st test execution. yay! let's run it! Note that somebody must have called the
            // `beginTest` method for this `testIdentifier` setting up properly the currentTest.
            return true;
        }

        if (previouslyExecutedTest.getRunStatus().equals(JTest.RUN_STATUS_FAILED)) {
            return true;
        }

        Long currentExecutionId = jvoidExecutionContext.getCurrentExecutionId();
        Long previouslyExecutedTestId = previouslyExecutedTest.getId();

        Map<String, JClass> jclassesPrevious = classesRepository
                .findByTestId(previouslyExecutedTestId);

        // Force to reload the affected classes, to allow to recompute the
        // checksum from the agent. Otherwise, if we don't reference the classes
        // the JVM doesn't load them and we do not get the new checksum
        // computation.
        // This step writes on the DB all the current execution classes/methods/etc.
        // and their checksums, in such a way we can actually compare them with the
        // previous execution.
        try {
            for (JClass jclass : jclassesPrevious.values()) {
                Class.forName(jclass.getIdentifier());
            }
        } catch (ClassNotFoundException e) {
            // If a class is not found then we can assume that the code the
            // test touches changed a lot. So, let's not skip it.
            log.trace("Not skipping test because class no longer present", e);
            return true;
        }

        Map<String, JClass> jclassesCurrent = classesRepository
                .findByExecutionIdAndRelatedToTestId(currentExecutionId, previouslyExecutedTestId);

        boolean modified = checksumsModified(jclassesCurrent, jclassesPrevious);

        // Checking for methods modifications
        if (!modified) {
            Map<String, JMethod> jmethodsPrevious = methodsRepository
                    .findByTestId(previouslyExecutedTestId);
            Map<String, JMethod> jmethodsCurrent = methodsRepository
                    .findByExecutionIdAndRelatedToTestId(currentExecutionId,
                            previouslyExecutedTestId);
            modified = checksumsModified(jmethodsCurrent, jmethodsPrevious);
        }

        // Checking for constructors modifications
        if (!modified) {
            Map<String, JClassConstructor> jconstructorsPrevious = classConstructorsRepository
                    .findByTestId(previouslyExecutedTestId);
            Map<String, JClassConstructor> jconstructorsCurrent = classConstructorsRepository
                    .findByExecutionIdAndRelatedToTestId(currentExecutionId,
                            previouslyExecutedTestId);
            modified = checksumsModified(jconstructorsCurrent, jconstructorsPrevious);
        }

        // Checking for static blocks modifications
        if (!modified) {
            Map<String, JClassStaticBlock> jClassStaticBlocksPrevious = classStaticBlocksRepository
                    .findByTestId(previouslyExecutedTestId);
            Map<String, JClassStaticBlock> jClassStaticBlocksCurrent = classStaticBlocksRepository
                    .findByExecutionIdAndRelatedToTestId(currentExecutionId,
                            previouslyExecutedTestId);
            modified = checksumsModified(jClassStaticBlocksCurrent, jClassStaticBlocksPrevious);
        }

        return modified;
    }

    /**
     *
     * @param currentExecution
     * @param previousExecution
     * @return
     */
    public static boolean checksumsModified(Map<String, ? extends ChecksumAware> currentExecution,
            Map<String, ? extends ChecksumAware> previousExecution) {
        if (previousExecution.size() != currentExecution.size()) {
            return true;
        }
        for (Entry<String, ? extends ChecksumAware> entry : currentExecution.entrySet()) {
            ChecksumAware current = entry.getValue();
            ChecksumAware previous = previousExecution.get(entry.getKey());
            if (previous == null || !current.getChecksum().equals(previous.getChecksum())) {
                return true;
            }
        }
        return false;
    }
}
