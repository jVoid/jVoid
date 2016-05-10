package com.jvoid.test.instrumentation;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.google.inject.Inject;
import com.jvoid.instrumentation.JVoidInstrumentationHelper;
import com.jvoid.instrumentation.TestRelatedCodeModificationChecker;
import com.jvoid.instrumentation.provider.ProviderUtil;
import com.jvoid.metadata.checksum.CodeChecksummer;
import com.jvoid.metadata.model.ChecksumAware;
import com.jvoid.metadata.model.JClass;
import com.jvoid.metadata.model.JClassConstructor;
import com.jvoid.metadata.model.JExecution;
import com.jvoid.metadata.model.JMethod;
import com.jvoid.metadata.model.JTest;
import com.jvoid.metadata.repositories.ClassConstructorsRepository;
import com.jvoid.metadata.repositories.ClassStaticBlocksRepository;
import com.jvoid.metadata.repositories.ClassesRepository;
import com.jvoid.metadata.repositories.MethodsRepository;
import com.jvoid.metadata.repositories.TestsRepository;
import com.jvoid.test.AbstractJVoidTest;
import com.jvoid.test.instrumentation.provider.app.fake.FakeAppAnnotation;
import com.rits.cloning.Cloner;

import javassist.CtClass;
import javassist.CtConstructor;
import javassist.CtMethod;
import javassist.CtNewConstructor;
import javassist.CtNewMethod;

public class TestRelatedCodeModificationCheckerTest extends AbstractJVoidTest {

    private static final String MOCK_TEST_IDENTIFIER = "mock_test_identifier";

    Cloner cloner = new Cloner();

    @Inject
    TestRelatedCodeModificationChecker checker;

    @Inject
    JVoidInstrumentationHelper helper;

    @Inject
    TestsRepository testsRepository;

    @Inject
    ClassesRepository classesRepository;

    @Inject
    MethodsRepository methodsRepository;

    @Inject
    ClassConstructorsRepository classConstructorsRepository;

    @Inject
    ClassStaticBlocksRepository classStaticBlocksRepository;

    @Inject
    CodeChecksummer checksummer;

    @Inject
    ProviderUtil providerUtil;

    // CtClass(es) used in the test
    /*
     * Class hierarchy change test
     */
    private static CtClass ctSuperAbstractTestRelatedClass;
    private static CtClass ctAbstractTestRelatedClass;

    private static CtClass ctTestSuperclassChangeOld;
    private static CtClass ctTestSuperclassChangeNew;

    /*
     * Method change test
     */
    private static CtClass ctTestClassMethodChange;
    private static CtMethod changedMethodOld;
    private static CtMethod changedMethodNew;

    private static CtClass ctTestConstructorChange;
    private static CtConstructor changedConstructorOld;
    private static CtConstructor changedConstructorNew;

    private static CtClass ctSameClass;
    private static CtMethod sameMethodOld;
    private static CtMethod sameMethodNew;

    @BeforeClass
    public static void configureCtClasses() throws Exception {
        ctSuperAbstractTestRelatedClass = classPool
                .get(SuperAbstractTestRelatedClass.class.getName());
        ctAbstractTestRelatedClass = classPool.get(AbstractTestRelatedClass.class.getName());

        // We need to forge a class at runtime, because reloading the same class with changes in the bytecode is really tricky :)
        ctTestSuperclassChangeOld = classPool.makeClass("TestSuperclassChange",
                ctSuperAbstractTestRelatedClass);
        ctTestSuperclassChangeNew = classPool.makeClass("TestSuperclassChange",
                ctAbstractTestRelatedClass);
        ctTestSuperclassChangeOld.toClass(); // ONLY ONE OF THEM!

        ctTestClassMethodChange = classPool.makeClass("TestClassMethodChange");
        changedMethodOld = CtNewMethod.make("public int tempMethod() { int i = 0; i++; return i; }", ctTestClassMethodChange);
        changedMethodNew = CtNewMethod.make("public int tempMethod() { int i = 0; i += 2; return i; }", ctTestClassMethodChange);
        ctTestClassMethodChange.toClass();

        ctTestConstructorChange = classPool.makeClass("TestConstructorMethodChange");
        changedConstructorOld = CtNewConstructor.make("public TestConstructorMethodChange() {  }", ctTestConstructorChange);
        changedConstructorNew = CtNewConstructor.make("public TestConstructorMethodChange() { throw new RuntimeException(); }", ctTestConstructorChange);
        ctTestConstructorChange.toClass();

        ctSameClass = classPool.makeClass("SameClass");
        sameMethodOld = CtNewMethod.make("public int tempMethod() { int i = 0; i += 2; return i; }", ctSameClass);
        sameMethodNew = CtNewMethod.make("public int tempMethod() { int i = 0; i += 2; return i; }", ctSameClass);
        ctSameClass.toClass();
    }

    @AfterClass
    public static void pruneCtClasses() {
        ctSuperAbstractTestRelatedClass.prune();
        ctAbstractTestRelatedClass.prune();
        ctTestSuperclassChangeOld.prune();
        ctTestSuperclassChangeNew.prune();
        ctTestClassMethodChange.prune();
        ctTestConstructorChange.prune();
        ctSameClass.prune();
    }

    // begin wasModified(String testIdentifier) tests

    @Test
    public void testFirstTestExecutionAlwaysModified() {
        JExecution currentExecution = setupCurrentExecution();
        helper.beginTest(MOCK_TEST_IDENTIFIER);
        assertEquals(JTest.RUN_STATUS_RUNNING,
                jvoidExecutionContext.getRunningTest().getRunStatus());

        assertTrue(checker.wasTestRelatedCodeModified(MOCK_TEST_IDENTIFIER));
        JTest created = testsRepository.findByIdenfifierAndExecutionId(MOCK_TEST_IDENTIFIER,
                currentExecution.getId());
        assertNotNull(created);
    }

    @Test
    public void testClassCoveredByTestNotPresentAnymoreCausesTheTestToBeExecuted() {
        JExecution previousExecution = setupCurrentExecution();
        JTest previousTest = helper.beginTest(MOCK_TEST_IDENTIFIER);
        previousTest.setRunStatus(JTest.RUN_STATUS_RUN);
        previousTest = testsRepository.update(previousTest);
        JClass previouslyCoveredClass = new JClass();
        previouslyCoveredClass.setChecksum("aaaabbbbccccdddd");
        previouslyCoveredClass.setExecutionId(previousExecution.getId());
        previouslyCoveredClass.setIdentifier("com/jvoid/ClassIsNotInTheClasspath");
        previouslyCoveredClass = classesRepository.add(previouslyCoveredClass);
        testsRepository.linkClass(previousTest, previouslyCoveredClass.getId());

        setupCurrentExecution();
        helper.beginTest(MOCK_TEST_IDENTIFIER);
        assertTrue(checker.wasTestRelatedCodeModified(MOCK_TEST_IDENTIFIER));
    }

    @Test
    public void testMethodChangeCausesTheTestToBeExecuted() throws Exception {
        JExecution previousExecution = setupCurrentExecution();
        JTest previousTest = helper.beginTest(MOCK_TEST_IDENTIFIER);
        previousTest.setRunStatus(JTest.RUN_STATUS_RUN);
        previousTest = testsRepository.update(previousTest);

        JClass previouslyCoveredClass = new JClass();
        previouslyCoveredClass.setChecksum("same_checksum"); // Pretend the classes are the same
        previouslyCoveredClass.setExecutionId(previousExecution.getId());
        previouslyCoveredClass.setIdentifier(providerUtil.getClassIdentifier(ctTestClassMethodChange));
        previouslyCoveredClass = classesRepository.add(previouslyCoveredClass);

        JMethod previouslyCoveredMethod = new JMethod();
        previouslyCoveredMethod.setChecksum(checksummer.checksum(changedMethodOld));
        previouslyCoveredMethod.setExecutionId(previousExecution.getId());
        previouslyCoveredMethod.setIdentifier("testMethod");
        previouslyCoveredMethod = methodsRepository.add(previouslyCoveredMethod);

        helper.bindMethodToCurrentRunningTest(previouslyCoveredMethod.getId(),
                previouslyCoveredClass.getId());

        testsRepository.linkClass(previousTest, previouslyCoveredClass.getId());

        JExecution currentExecution = setupCurrentExecution();
        helper.beginTest(MOCK_TEST_IDENTIFIER);

        JClass currentCoveredClass = new JClass();
        currentCoveredClass.setChecksum("same_checksum");
        currentCoveredClass.setExecutionId(currentExecution.getId());
        currentCoveredClass.setIdentifier(providerUtil.getClassIdentifier(ctTestClassMethodChange));
        currentCoveredClass = classesRepository.add(currentCoveredClass);

        JMethod currentCoveredMethod = new JMethod();
        currentCoveredMethod.setChecksum(checksummer.checksum(changedMethodNew));
        currentCoveredMethod.setExecutionId(currentExecution.getId());
        currentCoveredMethod.setIdentifier("testMethod");
        currentCoveredMethod = methodsRepository.add(currentCoveredMethod);

        helper.bindMethodToCurrentRunningTest(currentCoveredMethod.getId(),
                currentCoveredClass.getId());

        assertTrue(checker.wasTestRelatedCodeModified(MOCK_TEST_IDENTIFIER));
    }

    @Test
    public void testSameMethodCausesTheTestToBeSkipped() throws Exception {
        JExecution previousExecution = setupCurrentExecution();
        JTest previousTest = helper.beginTest(MOCK_TEST_IDENTIFIER);
        previousTest.setRunStatus(JTest.RUN_STATUS_RUN);
        previousTest = testsRepository.update(previousTest);

        JClass previouslyCoveredClass = new JClass();
        previouslyCoveredClass.setChecksum("same_checksum"); // Pretend the classes are the same
        previouslyCoveredClass.setExecutionId(previousExecution.getId());
        previouslyCoveredClass.setIdentifier(providerUtil.getClassIdentifier(ctSameClass));
        previouslyCoveredClass = classesRepository.add(previouslyCoveredClass);

        JMethod previouslyCoveredMethod = new JMethod();
        previouslyCoveredMethod.setChecksum(checksummer.checksum(sameMethodOld));
        previouslyCoveredMethod.setExecutionId(previousExecution.getId());
        previouslyCoveredMethod.setIdentifier("testMethod");
        previouslyCoveredMethod = methodsRepository.add(previouslyCoveredMethod);

        helper.bindMethodToCurrentRunningTest(previouslyCoveredMethod.getId(),
                previouslyCoveredClass.getId());

        testsRepository.linkClass(previousTest, previouslyCoveredClass.getId());

        JExecution currentExecution = setupCurrentExecution();
        helper.beginTest(MOCK_TEST_IDENTIFIER);

        JClass currentCoveredClass = new JClass();
        currentCoveredClass.setChecksum("same_checksum");
        currentCoveredClass.setExecutionId(currentExecution.getId());
        currentCoveredClass.setIdentifier(providerUtil.getClassIdentifier(ctSameClass));
        currentCoveredClass = classesRepository.add(currentCoveredClass);

        JMethod currentCoveredMethod = new JMethod();
        currentCoveredMethod.setChecksum(checksummer.checksum(sameMethodNew));
        currentCoveredMethod.setExecutionId(currentExecution.getId());
        currentCoveredMethod.setIdentifier("testMethod");
        currentCoveredMethod = methodsRepository.add(currentCoveredMethod);

        helper.bindMethodToCurrentRunningTest(currentCoveredMethod.getId(),
                currentCoveredClass.getId());

        assertFalse(checker.wasTestRelatedCodeModified(MOCK_TEST_IDENTIFIER));
    }

    @Test
    public void testConstructorChangeCausesTheTestToBeExecuted() throws Exception {
        JExecution previousExecution = setupCurrentExecution();
        JTest previousTest = helper.beginTest(MOCK_TEST_IDENTIFIER);
        previousTest.setRunStatus(JTest.RUN_STATUS_RUN);
        previousTest = testsRepository.update(previousTest);

        JClass previouslyCoveredClass = new JClass();
        previouslyCoveredClass.setChecksum("same_checksum"); // Pretend the classes are the same
        previouslyCoveredClass.setExecutionId(previousExecution.getId());
        previouslyCoveredClass.setIdentifier(providerUtil.getClassIdentifier(ctTestConstructorChange));
        previouslyCoveredClass = classesRepository.add(previouslyCoveredClass);

        JClassConstructor previouslyCoveredCnstr = new JClassConstructor();
        previouslyCoveredCnstr.setChecksum(checksummer.checksum(changedConstructorOld));
        previouslyCoveredCnstr.setClassId(previouslyCoveredClass.getId());
        previouslyCoveredCnstr.setIdentifier(providerUtil.getConstructorIdentifier(changedConstructorOld));
        previouslyCoveredCnstr = classConstructorsRepository.add(previouslyCoveredCnstr);

        testsRepository.linkClass(previousTest, previouslyCoveredClass.getId());

        JExecution currentExecution = setupCurrentExecution();
        helper.beginTest(MOCK_TEST_IDENTIFIER);

        JClass currentCoveredClass = new JClass();
        currentCoveredClass.setChecksum("same_checksum");
        currentCoveredClass.setExecutionId(currentExecution.getId());
        currentCoveredClass.setIdentifier(providerUtil.getClassIdentifier(ctTestConstructorChange));
        currentCoveredClass = classesRepository.add(currentCoveredClass);

        JClassConstructor currentCoveredCnstr = new JClassConstructor();
        currentCoveredCnstr.setChecksum(checksummer.checksum(changedConstructorNew));
        currentCoveredCnstr.setClassId(currentCoveredClass.getId());
        currentCoveredCnstr.setIdentifier(providerUtil.getConstructorIdentifier(changedConstructorNew));
        currentCoveredCnstr = classConstructorsRepository.add(currentCoveredCnstr);

        assertTrue(checker.wasTestRelatedCodeModified(MOCK_TEST_IDENTIFIER));
    }

    @Test
    public void testClassHierarchyChangeCausesTheTestToBeExecuted() {
        JExecution previousExecution = setupCurrentExecution();
        JTest previousTest = helper.beginTest(MOCK_TEST_IDENTIFIER);
        previousTest.setRunStatus(JTest.RUN_STATUS_RUN);
        previousTest = testsRepository.update(previousTest);

        JClass previouslyCoveredClass = new JClass();
        previouslyCoveredClass.setChecksum(checksummer.checksum(ctTestSuperclassChangeOld));
        previouslyCoveredClass.setExecutionId(previousExecution.getId());
        previouslyCoveredClass.setIdentifier("TestSuperclassChange");
        previouslyCoveredClass = classesRepository.add(previouslyCoveredClass);

        testsRepository.linkClass(previousTest, previouslyCoveredClass.getId());

        JExecution currentExecution = setupCurrentExecution();
        helper.beginTest(MOCK_TEST_IDENTIFIER);

        JClass currentCoveredClass = new JClass();
        currentCoveredClass.setChecksum(checksummer.checksum(ctTestSuperclassChangeNew));
        currentCoveredClass.setExecutionId(currentExecution.getId());
        currentCoveredClass.setIdentifier("TestSuperclassChange");
        currentCoveredClass = classesRepository.add(currentCoveredClass);

        assertTrue(checker.wasTestRelatedCodeModified(MOCK_TEST_IDENTIFIER));
    }
    // end wasModified(String testIdentifier) tests

    @Test
    public void testChecksumsModified() {
        // Empty maps => no code is modified
        assertFalse(TestRelatedCodeModificationChecker.checksumsModified(
                Collections.<String, ChecksumAware> emptyMap(),
                Collections.<String, ChecksumAware> emptyMap()));

        JMethod method1 = new JMethod();
        method1.setChecksum("method1Checksum");
        method1.setExecutionId(jvoidExecutionContext.getCurrentExecutionId());
        method1.setIdentifier("method1Identifier");

        JMethod method1Modified = cloner.deepClone(method1);
        method1Modified.setChecksum("method1Checksum_changed");

        JMethod method2 = new JMethod();
        method2.setChecksum("method2Checksum");
        method2.setExecutionId(jvoidExecutionContext.getCurrentExecutionId());
        method2.setIdentifier("method2Identifier");

        Map<String, ChecksumAware> previousExecution = new HashMap<>();
        previousExecution.put(method1.getIdentifier(), method1);
        previousExecution.put(method2.getIdentifier(), method2);

        Map<String, ChecksumAware> currentExecution = new HashMap<>();
        currentExecution.put(method1.getIdentifier(), method1);

        // Different map size => checksum must be changed
        assertTrue(TestRelatedCodeModificationChecker.checksumsModified(currentExecution,
                previousExecution));

        // Same size and same checksum => no changes
        currentExecution.put(method2.getIdentifier(), method2);
        assertFalse(TestRelatedCodeModificationChecker.checksumsModified(currentExecution,
                previousExecution));

        // Some method changed => checksum is changed
        currentExecution.put(method1.getIdentifier(), method1Modified);
        assertTrue(TestRelatedCodeModificationChecker.checksumsModified(currentExecution,
                previousExecution));
    }

    /*
     * Classes to test the class hierarchy
     */
    public abstract static class SuperAbstractTestRelatedClass {
        public SuperAbstractTestRelatedClass() {
        }
    }

    public abstract static class AbstractTestRelatedClass extends SuperAbstractTestRelatedClass {
        public AbstractTestRelatedClass() {
        }
    }

    @SuppressWarnings("unused")
    public static class TestRelatedClass extends AbstractTestRelatedClass {
        public int common = 1;
        @Deprecated
        private Long secret = 3L;
        private final String ciao = "ciao";
        @FakeAppAnnotation
        protected final boolean eheh = true;
    }
}
