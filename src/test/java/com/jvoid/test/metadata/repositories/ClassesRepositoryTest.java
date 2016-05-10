package com.jvoid.test.metadata.repositories;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import javax.inject.Inject;

import org.junit.Test;

import com.jvoid.metadata.model.JClass;
import com.jvoid.metadata.model.JTest;
import com.jvoid.metadata.repositories.ClassesRepository;
import com.jvoid.metadata.repositories.TestsRepository;

public class ClassesRepositoryTest extends AbstractTestRelatedEntityRepositoryTest<JClass, Long, ClassesRepository> {

    @Inject
    private ClassesRepository classesRepository;

    @Inject
    private TestsRepository testsRepository;

    @Override
    protected void linkToTest(JClass jclass, JTest jtest) {
        testsRepository.linkClass(jtest, jclass.getId());
    }

    @Override
    protected JClass prepareForCreation() {
        return createJClass();
    }

    @Override
    protected ClassesRepository getRepo() {
        return classesRepository;
    }

    @Test // TODO: Centralize this kind of test
    public void testFindByIdentifierAndExecutionId() {
        JClass entity1old = getRepo().add(prepareForCreation());
        JClass entity2old = getRepo().add(prepareForCreation());
        assertNotEquals(entity1old.getIdentifier(), entity2old.getIdentifier());
        Long oldExecutionId = jvoidExecutionContext.getCurrentExecutionId();
        setupNewExecution();
        Long newExecutionId = jvoidExecutionContext.getCurrentExecutionId();
        JClass entity1new = prepareForCreation();
        entity1new.setIdentifier(entity1old.getIdentifier());
        entity1new = getRepo().add(entity1new);
        JClass entity2new = prepareForCreation();
        entity2new.setIdentifier(entity2old.getIdentifier());
        entity2new = getRepo().add(entity2new);
        assertEquals(entity1old, getRepo().findByIdenfifierAndExecutionId(entity1old.getIdentifier(), oldExecutionId));
        assertEquals(entity1new, getRepo().findByIdenfifierAndExecutionId(entity1old.getIdentifier(), newExecutionId));
        assertEquals(entity2old, getRepo().findByIdenfifierAndExecutionId(entity2old.getIdentifier(), oldExecutionId));
        assertEquals(entity2new, getRepo().findByIdenfifierAndExecutionId(entity2old.getIdentifier(), newExecutionId));
    }

}
