package io.jvoid.execution;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import io.jvoid.metadata.model.JExecution;
import io.jvoid.metadata.model.JTest;
import io.jvoid.metadata.repositories.ExecutionsRepository;

/**
 * The {@code JVoidExecutionContext} keeps track of a current JVoid execution
 * or session, that is defined basically by an execution of the tests of the project.
 * It keeps track of the current execution ID and of various runtime parameters like
 * the current running test.
 *
 */
@Singleton
public class JVoidExecutionContext {

    private ExecutionsRepository executionsRepository;

    private JExecution currentExecution;

    private ThreadLocal<JTest> runningTestHolder = new ThreadLocal<>();

    @Inject
    public JVoidExecutionContext(ExecutionsRepository executionsRepository) {
        super();
        this.executionsRepository = executionsRepository;
        this.runningTestHolder.set(null);
    }

    public Long getCurrentExecutionId() {
        return currentExecution == null ? null : currentExecution.getId();
    }

    public JExecution getCurrentExecution() {
        return currentExecution;
    }

    public void setCurrentExecution(JExecution currentExecution) {
        this.currentExecution = currentExecution;
    }

    public void startNewExecution() {
        JExecution execution = new JExecution();
        execution.setTimestamp(System.currentTimeMillis());
        execution = executionsRepository.add(execution);
        currentExecution = execution;
    }

    public JTest getRunningTest() {
        return runningTestHolder.get();
    }

    public synchronized void setRunningTest(JTest runningTest) {
        runningTestHolder.set(runningTest);
    }
}
