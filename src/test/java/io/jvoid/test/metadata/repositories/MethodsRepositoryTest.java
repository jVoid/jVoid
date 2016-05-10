package io.jvoid.test.metadata.repositories;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import javax.inject.Inject;

import org.junit.Test;

import io.jvoid.metadata.model.JMethod;
import io.jvoid.metadata.model.JTest;
import io.jvoid.metadata.repositories.MethodsRepository;
import io.jvoid.metadata.repositories.TestsRepository;

public class MethodsRepositoryTest extends AbstractTestRelatedEntityRepositoryTest<JMethod, Long, MethodsRepository> {

    @Inject
    private MethodsRepository methodsRepository;

    @Inject
    private TestsRepository testsRepository;

    @Override
    protected void linkToTest(JMethod jmethod, JTest jtest) {
        testsRepository.linkMethod(jtest, jmethod.getId());
    }

    @Override
    protected JMethod prepareForCreation() {
        return createJMethod();
    }

    @Override
    protected MethodsRepository getRepo() {
        return methodsRepository;
    }

    @Test // TODO: Centralize this kind of test
    public void testFindByIdentifierAndExecutionId() {
        JMethod entity1old = getRepo().add(prepareForCreation());
        JMethod entity2old = getRepo().add(prepareForCreation());
        assertNotEquals(entity1old.getIdentifier(), entity2old.getIdentifier());
        Long oldExecutionId = jvoidExecutionContext.getCurrentExecutionId();
        setupNewExecution();
        Long newExecutionId = jvoidExecutionContext.getCurrentExecutionId();
        JMethod entity1new = prepareForCreation();
        entity1new.setIdentifier(entity1old.getIdentifier());
        entity1new = getRepo().add(entity1new);
        JMethod entity2new = prepareForCreation();
        entity2new.setIdentifier(entity2old.getIdentifier());
        entity2new = getRepo().add(entity2new);
        assertEquals(entity1old, getRepo().findByIdenfifierAndExecutionId(entity1old.getIdentifier(), oldExecutionId));
        assertEquals(entity1new, getRepo().findByIdenfifierAndExecutionId(entity1old.getIdentifier(), newExecutionId));
        assertEquals(entity2old, getRepo().findByIdenfifierAndExecutionId(entity2old.getIdentifier(), oldExecutionId));
        assertEquals(entity2new, getRepo().findByIdenfifierAndExecutionId(entity2old.getIdentifier(), newExecutionId));
    }

}
