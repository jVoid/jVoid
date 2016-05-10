package com.jvoid.test.metadata.repositories;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import com.google.inject.Inject;
import com.jvoid.metadata.model.JEntity;
import com.jvoid.metadata.model.JTest;
import com.jvoid.metadata.repositories.TestRelatedEntityRepository;
import com.jvoid.metadata.repositories.TestsRepository;

public abstract class AbstractTestRelatedEntityRepositoryTest<T extends JEntity<ID>, ID, R extends TestRelatedEntityRepository<T, ID>>
        extends AbstractBaseRepositoryTest<T, ID, R> {

    protected abstract void linkToTest(T entity, JTest jtest);

    @Inject
    private TestsRepository testsRepository;

    @Test
    public void testFindByTestId() {
        JTest jtest = testsRepository.add(createJTest());

        T entity1 = prepareForCreation();
        T entity2 = prepareForCreation();

        getRepo().add(entity1);
        getRepo().add(entity2);

        linkToTest(entity1, jtest);
        linkToTest(entity2, jtest);

        List<T> entities = new ArrayList<>(getRepo().findByTestId(jtest.getId()).values());

        assertEquals(2, entities.size());
        int e1idx = entities.get(0).getId().equals(entity1.getId()) ? 0 : 1;
        int e2idx = 1 - e1idx;
        assertEquals(entity1, entities.get(e1idx));
        assertEquals(entity2, entities.get(e2idx));
    }

    @Test
    public void testFindByExecutionIdAndRelatedToTestId() {
        jvoidExecutionContext.getCurrentExecutionId();
        JTest jtest = testsRepository.add(createJTest());
        setupNewExecution();
        Long newExecutionId = jvoidExecutionContext.getCurrentExecutionId();

        T entity1new = prepareForCreation();
        T entity2new = prepareForCreation();

        JTest jtest2 = testsRepository.add(createJTest());

        entity1new = getRepo().add(entity1new);
        linkToTest(entity1new, jtest);

        entity2new = getRepo().add(entity2new);

        linkToTest(entity1new, jtest2);
        linkToTest(entity2new, jtest2);

        Map<String, T> entities = getRepo().findByExecutionIdAndRelatedToTestId(newExecutionId,
                jtest.getId());
        assertTrue(entities.size() == 1);
        assertTrue(entities.values().iterator().next().getId() == entity1new.getId());
    }

}
