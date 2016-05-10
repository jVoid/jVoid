package com.jvoid.test.metadata.repositories;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import javax.inject.Inject;

import org.junit.Test;

import com.jvoid.metadata.model.JTest;
import com.jvoid.metadata.repositories.TestsRepository;

public class TestsRepositoryTest extends AbstractBaseRepositoryTest<JTest, Long, TestsRepository> {

    @Inject
    private TestsRepository testsRepository;

    @Override
    protected JTest prepareForCreation() {
        return createJTest();
    }

    @Override
    protected TestsRepository getRepo() {
        return testsRepository;
    }

    @Test
    public void testUpdate() {
        JTest jtest = getRepo().add(prepareForCreation());
        JTest fetchedJTest = getRepo().findById(jtest.getId());
        assertEquals(jtest, fetchedJTest);

        setupNewExecution();
        jtest.setIdentifier("new identifier");
        jtest.setExecutionId(jvoidExecutionContext.getCurrentExecutionId());
        jtest.setRunStatus(JTest.RUN_STATUS_RUN);
        assertNotEquals(jtest, fetchedJTest);

        getRepo().update(jtest);
        fetchedJTest = getRepo().findById(jtest.getId());
        assertEquals(jtest, fetchedJTest);
    }

    @Test
    public void testFindLatestExecutedByIdenfifier1() {
        JTest jtest1oldNotExecuted = prepareForCreation();
        final String identifier = jtest1oldNotExecuted.getIdentifier();
        JTest jtest1oldExecuted = prepareForCreation();
        jtest1oldExecuted.setIdentifier(identifier);
        jtest1oldExecuted.setRunStatus(JTest.RUN_STATUS_RUN);

        jtest1oldNotExecuted = getRepo().add(jtest1oldNotExecuted);
        jtest1oldExecuted = getRepo().add(jtest1oldExecuted);

        setupNewExecution();

        JTest jtest1newNotExecuted = prepareForCreation();
        jtest1newNotExecuted.setIdentifier(identifier);
        JTest jtest1newExecuted = prepareForCreation();
        jtest1newExecuted.setIdentifier(identifier);
        jtest1newExecuted.setRunStatus(JTest.RUN_STATUS_RUN);

        jtest1newNotExecuted = getRepo().add(jtest1newNotExecuted);
        jtest1newExecuted = getRepo().add(jtest1newExecuted);

        assertEquals(jtest1newExecuted, getRepo().findLatestExecutedByIdenfifier(identifier));
    }

    @Test
    public void testFindLatestExecutedByIdenfifier2() {
        JTest t1 = prepareForCreation();
        final String identifier = t1.getIdentifier();
        t1.setRunStatus(JTest.RUN_STATUS_FAILED);
        t1 = getRepo().add(t1);

        setupNewExecution();

        JTest t2 = prepareForCreation();
        t2.setIdentifier(identifier);
        t2.setRunStatus(JTest.RUN_STATUS_RUN);
        t2 = getRepo().add(t2);

        setupNewExecution();

        JTest t3 = prepareForCreation();
        t3.setIdentifier(identifier);
        t3.setRunStatus(JTest.RUN_STATUS_SKIPPED);
        t3 = getRepo().add(t3);

        setupNewExecution();

        JTest t4 = prepareForCreation();
        t4.setIdentifier(identifier);
        t4.setRunStatus(JTest.RUN_STATUS_FAILED);
        t4 = getRepo().add(t4);

        setupNewExecution();

        assertEquals(t4, getRepo().findLatestExecutedByIdenfifier(identifier));
    }

    @Test // TODO: Centralize this kind of test
    public void testFindByIdentifierAndExecutionId() {
        JTest entity1old = getRepo().add(prepareForCreation());
        JTest entity2old = getRepo().add(prepareForCreation());
        assertNotEquals(entity1old.getIdentifier(), entity2old.getIdentifier());
        Long oldExecutionId = jvoidExecutionContext.getCurrentExecutionId();
        setupNewExecution();
        Long newExecutionId = jvoidExecutionContext.getCurrentExecutionId();
        JTest entity1new = prepareForCreation();
        entity1new.setIdentifier(entity1old.getIdentifier());
        entity1new = getRepo().add(entity1new);
        JTest entity2new = prepareForCreation();
        entity2new.setIdentifier(entity2old.getIdentifier());
        entity2new = getRepo().add(entity2new);
        assertEquals(entity1old, getRepo().findByIdenfifierAndExecutionId(entity1old.getIdentifier(), oldExecutionId));
        assertEquals(entity1new, getRepo().findByIdenfifierAndExecutionId(entity1old.getIdentifier(), newExecutionId));
        assertEquals(entity2old, getRepo().findByIdenfifierAndExecutionId(entity2old.getIdentifier(), oldExecutionId));
        assertEquals(entity2new, getRepo().findByIdenfifierAndExecutionId(entity2old.getIdentifier(), newExecutionId));
    }
}
