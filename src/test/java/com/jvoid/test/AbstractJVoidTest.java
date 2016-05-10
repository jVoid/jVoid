package com.jvoid.test;

import org.junit.After;
import org.junit.Before;
import org.junit.runner.RunWith;

import com.google.inject.Inject;
import com.jvoid.database.DbUtils;
import com.jvoid.database.MetadataDatabase;
import com.jvoid.execution.JVoidExecutionContext;
import com.jvoid.guice.JVoidModule;
import com.jvoid.instrumentation.JVoidInstrumentationHelper;
import com.jvoid.instrumentation.JVoidInstrumentationHelperHolder;
import com.jvoid.metadata.model.JExecution;
import com.jvoid.metadata.repositories.ExecutionsRepository;

import javassist.ClassPool;

@UseModules({ JVoidModule.class })
@RunWith(JVoidTestRunner.class)
public abstract class AbstractJVoidTest {

    protected static final String DEFAULT_TEST_CONFIGURATION_FILE = "./src/test/resources/test-jvoid.config";

    private static ThreadLocal<Long> currentExecutionId = new ThreadLocal<>(); // Parallel tests?

    protected static final ClassPool classPool;

    @Inject
    protected MetadataDatabase metadataDatabase;

    @Inject
    protected JVoidInstrumentationHelper instrumentationHelper;

    @Inject
    protected ExecutionsRepository executionsRepository;

    @Inject
    protected JVoidExecutionContext jvoidExecutionContext;

    static {
        // See ClassPool JavaDoc for memory consumption issues
        ClassPool.doPruning = true;
        classPool = ClassPool.getDefault();
    }

    @Before
    public void setUp() throws Exception {
        // Make sure the database is initialized
        metadataDatabase.startup();

        // Makes sure that the database is clean, then restarts it to apply the migrations.
        // To be sure that each test runs in its own clean db environment.
        // TODO: Can this be more elegant?
        DbUtils.executeUpdate(metadataDatabase, "DROP ALL OBJECTS");
        metadataDatabase.shutdown();
        metadataDatabase.startup();

        currentExecutionId.set(0L);
    }

    @After
    public void tearDown() {
        metadataDatabase.shutdown();
        JVoidInstrumentationHelperHolder.getInstance().set(instrumentationHelper);
    }

    /*
     * Utility methods for current JExecution
     */
    protected Long getCurrentExecutionId() {
        return currentExecutionId.get();
    }

    protected synchronized JExecution setupCurrentExecution() {
        return setupCurrentExecution(null);
    }

    protected synchronized JExecution setupCurrentExecution(Long timestamp) {
        JExecution currentExecution = new JExecution();
        currentExecution.setTimestamp(timestamp == null ? System.currentTimeMillis() : timestamp);
        currentExecution = executionsRepository.add(currentExecution);
        currentExecutionId.set(currentExecution.getId());
        jvoidExecutionContext.setCurrentExecution(currentExecution);
        return currentExecution;
    }
}
