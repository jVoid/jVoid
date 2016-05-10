package io.jvoid.test.instrumentation;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.apache.commons.dbutils.handlers.ScalarHandler;
import org.junit.Test;

import com.google.inject.Inject;

import io.jvoid.database.DbUtils;
import io.jvoid.instrumentation.JVoidInstrumentationHelper;
import io.jvoid.instrumentation.TestRelatedCodeModificationChecker;
import io.jvoid.metadata.model.JClass;
import io.jvoid.metadata.model.JExecution;
import io.jvoid.metadata.model.JMethod;
import io.jvoid.metadata.model.JTest;
import io.jvoid.metadata.repositories.ClassesRepository;
import io.jvoid.metadata.repositories.MethodsRepository;
import io.jvoid.metadata.repositories.TestsRepository;
import io.jvoid.test.AbstractJVoidTest;

public class JVoidInstrumentationHelperTest extends AbstractJVoidTest {

    @Inject
    JVoidInstrumentationHelper helper;

    @Inject
    private TestsRepository testsRepository;

    @Inject
    private MethodsRepository methodsRepository;

    @Inject
    private ClassesRepository classesRepository;


    @Test
    public void testBeginTest() {
        final String METHOD_IDENTIFIER = "my_method_identifier";
        JExecution currentExecution = setupCurrentExecution();
        helper.beginTest(METHOD_IDENTIFIER);
        JTest begunTest = testsRepository.findByIdenfifierAndExecutionId(METHOD_IDENTIFIER,
                currentExecution.getId());
        assertNotNull(begunTest);
        assertEquals(METHOD_IDENTIFIER, begunTest.getIdentifier());
        assertEquals(currentExecution.getId(), begunTest.getExecutionId());
        assertFalse(JTest.RUN_STATUS_RUN.equals(begunTest.getRunStatus()));
    }

    @Test
    public void testDetectTestStatusFailure() {
        final String METHOD_IDENTIFIER = "my_method_identifier";
        JExecution currentExecution = setupCurrentExecution();
        helper.beginTest(METHOD_IDENTIFIER);
        JTest begunTest = testsRepository.findByIdenfifierAndExecutionId(METHOD_IDENTIFIER,
                currentExecution.getId());
        assertNotNull(begunTest);
        assertEquals(METHOD_IDENTIFIER, begunTest.getIdentifier());
        assertEquals(currentExecution.getId(), begunTest.getExecutionId());
        assertTrue(JTest.RUN_STATUS_RUNNING.equals(begunTest.getRunStatus()));

        helper.detectTestStatusFailure();
        begunTest = testsRepository.findByIdenfifierAndExecutionId(METHOD_IDENTIFIER,
                currentExecution.getId());
        assertNotNull(begunTest);
        assertEquals(begunTest, jvoidExecutionContext.getRunningTest());
        assertEquals(METHOD_IDENTIFIER, begunTest.getIdentifier());
        assertEquals(currentExecution.getId(), begunTest.getExecutionId());
        assertTrue(JTest.RUN_STATUS_FAILED.equals(begunTest.getRunStatus()));
    }

    @Test
    public void testDetectTestStatusSkip() {
        final String METHOD_IDENTIFIER = "my_method_identifier";
        JExecution currentExecution = setupCurrentExecution();
        helper.beginTest(METHOD_IDENTIFIER);
        JTest begunTest = testsRepository.findByIdenfifierAndExecutionId(METHOD_IDENTIFIER,
                currentExecution.getId());
        assertNotNull(begunTest);
        assertEquals(METHOD_IDENTIFIER, begunTest.getIdentifier());
        assertEquals(currentExecution.getId(), begunTest.getExecutionId());
        assertTrue(JTest.RUN_STATUS_RUNNING.equals(begunTest.getRunStatus()));

        helper.detectTestStatusSkip();
        begunTest = testsRepository.findByIdenfifierAndExecutionId(METHOD_IDENTIFIER,
                currentExecution.getId());
        assertNotNull(begunTest);
        assertEquals(begunTest, jvoidExecutionContext.getRunningTest());
        assertEquals(METHOD_IDENTIFIER, begunTest.getIdentifier());
        assertEquals(currentExecution.getId(), begunTest.getExecutionId());
        assertTrue(JTest.RUN_STATUS_SKIPPED.equals(begunTest.getRunStatus()));
    }

    @Test
    public void testDetectTestStatusComplete() {
        final String METHOD_IDENTIFIER = "my_method_identifier";
        JExecution currentExecution = setupCurrentExecution();
        helper.beginTest(METHOD_IDENTIFIER);
        JTest begunTest = testsRepository.findByIdenfifierAndExecutionId(METHOD_IDENTIFIER,
                currentExecution.getId());
        assertNotNull(begunTest);
        assertEquals(METHOD_IDENTIFIER, begunTest.getIdentifier());
        assertEquals(currentExecution.getId(), begunTest.getExecutionId());
        assertTrue(JTest.RUN_STATUS_RUNNING.equals(begunTest.getRunStatus()));

        helper.detectTestStatusComplete();
        begunTest = testsRepository.findByIdenfifierAndExecutionId(METHOD_IDENTIFIER,
                currentExecution.getId());
        assertNotNull(begunTest);
        assertEquals(begunTest, jvoidExecutionContext.getRunningTest());
        assertEquals(METHOD_IDENTIFIER, begunTest.getIdentifier());
        assertEquals(currentExecution.getId(), begunTest.getExecutionId());
        assertTrue(JTest.RUN_STATUS_RUN.equals(begunTest.getRunStatus()));
    }

    @Test
    public void testDetectTestStatusCompleteDoesntOverrideFailure() {
        final String METHOD_IDENTIFIER = "my_method_identifier";
        JExecution currentExecution = setupCurrentExecution();
        helper.beginTest(METHOD_IDENTIFIER);
        JTest begunTest = testsRepository.findByIdenfifierAndExecutionId(METHOD_IDENTIFIER,
                currentExecution.getId());
        assertNotNull(begunTest);
        assertEquals(METHOD_IDENTIFIER, begunTest.getIdentifier());
        assertEquals(currentExecution.getId(), begunTest.getExecutionId());
        assertTrue(JTest.RUN_STATUS_RUNNING.equals(begunTest.getRunStatus()));

        helper.detectTestStatusFailure();
        helper.detectTestStatusComplete();
        begunTest = testsRepository.findByIdenfifierAndExecutionId(METHOD_IDENTIFIER,
                currentExecution.getId());
        assertNotNull(begunTest);
        assertEquals(begunTest, jvoidExecutionContext.getRunningTest());
        assertEquals(METHOD_IDENTIFIER, begunTest.getIdentifier());
        assertEquals(currentExecution.getId(), begunTest.getExecutionId());
        assertTrue(JTest.RUN_STATUS_FAILED.equals(begunTest.getRunStatus()));
    }

    @Test
    public void testDetectTestStatusCompleteDoesntOverrideSkip() {
        final String METHOD_IDENTIFIER = "my_method_identifier";
        JExecution currentExecution = setupCurrentExecution();
        helper.beginTest(METHOD_IDENTIFIER);
        JTest begunTest = testsRepository.findByIdenfifierAndExecutionId(METHOD_IDENTIFIER,
                currentExecution.getId());
        assertNotNull(begunTest);
        assertEquals(METHOD_IDENTIFIER, begunTest.getIdentifier());
        assertEquals(currentExecution.getId(), begunTest.getExecutionId());
        assertTrue(JTest.RUN_STATUS_RUNNING.equals(begunTest.getRunStatus()));

        helper.detectTestStatusSkip();
        helper.detectTestStatusComplete();
        begunTest = testsRepository.findByIdenfifierAndExecutionId(METHOD_IDENTIFIER,
                currentExecution.getId());
        assertNotNull(begunTest);
        assertEquals(begunTest, jvoidExecutionContext.getRunningTest());
        assertEquals(METHOD_IDENTIFIER, begunTest.getIdentifier());
        assertEquals(currentExecution.getId(), begunTest.getExecutionId());
        assertTrue(JTest.RUN_STATUS_SKIPPED.equals(begunTest.getRunStatus()));
    }

    @Test
    public void testBindMethodToCurrentRunningTest() {
        final String TEST_IDENTIFIER = "my_runnning_test_id";
        final String CLASS_IDENTIFIER = "my_class_id";
        final String METHOD_IDENTIFIER = "my_method_id";

        JExecution currentExecution = setupCurrentExecution();

        JClass mockClass = new JClass();
        mockClass.setChecksum("aaaaaa");
        mockClass.setExecutionId(currentExecution.getId());
        mockClass.setIdentifier(CLASS_IDENTIFIER);
        mockClass = classesRepository.add(mockClass);

        JMethod mockMethod = new JMethod();
        mockMethod.setChecksum("mmmmmmmm");
        mockMethod.setExecutionId(currentExecution.getId());
        mockMethod.setIdentifier(METHOD_IDENTIFIER);
        mockMethod = methodsRepository.add(mockMethod);

        JTest mockRunningTest = new JTest();
        mockRunningTest.setExecutionId(currentExecution.getId());
        mockRunningTest.setIdentifier(TEST_IDENTIFIER);
        mockRunningTest = testsRepository.add(mockRunningTest);

        jvoidExecutionContext.setRunningTest(mockRunningTest);

        helper.bindMethodToCurrentRunningTest(mockRunningTest.getId(), mockClass.getId());

        Long classBoundTestId = DbUtils.query(metadataDatabase,
                "SELECT testId FROM test_classes tc WHERE tc.classId = ?",
                new ScalarHandler<Long>(), mockClass.getId());
        assertEquals(mockRunningTest.getId(), classBoundTestId);

        Long methodBoundTestId = DbUtils.query(metadataDatabase,
                "SELECT testId FROM test_methods tm WHERE tm.methodId = ?",
                new ScalarHandler<Long>(), mockMethod.getId());
        assertEquals(mockRunningTest.getId(), methodBoundTestId);
    }

    /*
     * We assume that this method will always do a simple delegation to the
     * TestRelatedCodeModificationChecker, so we test specifically that condition.
     * In case our strategy changes, we need to update this test accordingly.
     */
    @Test
    public void testWasTestRelatedCodeModified() {
        final String TEST_IDENTIFIER_MODIFIED = "test_id1";
        final String TEST_IDENTIFIER_NOT_MODIFIED = "test_id2";

        TestRelatedCodeModificationChecker mockChecker = mock(
                TestRelatedCodeModificationChecker.class);
        when(mockChecker.wasTestRelatedCodeModified(TEST_IDENTIFIER_MODIFIED)).thenReturn(true);
        when(mockChecker.wasTestRelatedCodeModified(TEST_IDENTIFIER_NOT_MODIFIED)).thenReturn(false);
        
        JVoidInstrumentationHelper helper = new JVoidInstrumentationHelper(mockChecker, null, null);

        assertTrue(helper.wasTestRelatedCodeModified(TEST_IDENTIFIER_MODIFIED));
        assertFalse(helper.wasTestRelatedCodeModified(TEST_IDENTIFIER_NOT_MODIFIED));
    }
}
