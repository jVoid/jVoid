package com.jvoid.test.metadata.repositories;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;

import org.apache.commons.dbutils.ResultSetHandler;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.google.inject.Inject;
import com.jvoid.database.DbUtils;
import com.jvoid.metadata.model.JClass;
import com.jvoid.metadata.model.JEntity;
import com.jvoid.metadata.model.JExecution;
import com.jvoid.metadata.model.JMethod;
import com.jvoid.metadata.model.JTest;
import com.jvoid.metadata.repositories.BaseRepository;
import com.jvoid.metadata.repositories.ExecutionsRepository;
import com.jvoid.test.AbstractJVoidTest;

public abstract class AbstractBaseRepositoryTest<T extends JEntity<ID>, ID, R extends BaseRepository<T, ID>> extends AbstractJVoidTest {

    @Inject
    private ExecutionsRepository executionsRepository;

    protected abstract T prepareForCreation();

    protected abstract R getRepo();

    @Override
    @Before
    public void setUp() throws Exception {
        super.setUp();
        // Cause the setup
        setupNewExecution();
    }

    @Override
    @After
    public void tearDown() {
        metadataDatabase.shutdown();
    }

    @Test
    public void testFindById() {
        T entity1 = prepareForCreation();
        entity1 = getRepo().add(entity1);
        T fetchedEntity1 = getRepo().findById(entity1.getId());
        assertEquals(entity1, fetchedEntity1);

        T entity2 = prepareForCreation();
        entity2 = getRepo().add(entity2);
        T fetchedEntity2 = getRepo().findById(entity2.getId());
        assertEquals(entity2, fetchedEntity2);

        assertNotEquals(fetchedEntity1, fetchedEntity2);
    }

    @Test
    public void testSaveOperation() {
        T entity1 = prepareForCreation();
        T returnedEntity1 = getRepo().add(entity1);
        assertNotNull(returnedEntity1);
        assertSame(entity1, returnedEntity1);
        assertNotNull(returnedEntity1.getId());

        T entity2 = prepareForCreation();
        T returnedEntity2 = getRepo().add(entity2);
        assertNotNull(returnedEntity2);
        assertSame(entity2, returnedEntity2);
        assertNotNull(returnedEntity2.getId());

        assertNotEquals(returnedEntity1.getId(), returnedEntity2.getId());

        T savedEntity1 = getRepo().findById(entity1.getId());
        T savedEntity2 = getRepo().findById(entity2.getId());

        assertEquals(returnedEntity1, savedEntity1);
        assertEquals(returnedEntity2, savedEntity2);
    }

    protected Long executeInsert(String sql, Object... parameters) {
        return DbUtils.executeInsert(metadataDatabase, sql, parameters);
    }

    protected <U> U query(String sql, ResultSetHandler<U> resultSetHandler, Object... parameters) {
        return DbUtils.query(metadataDatabase, sql, resultSetHandler, parameters);
    }

    protected long randomId() {
        return System.nanoTime();
    }

    protected String randomStr() {
        return "" + Math.random();
    }

    protected JExecution setupNewExecution() {
        JExecution currentExecution = new JExecution();
        currentExecution.setTimestamp(System.nanoTime());
        currentExecution = executionsRepository.add(currentExecution);
        jvoidExecutionContext.setCurrentExecution(currentExecution);
        return currentExecution;
    }

    protected JTest createJTest() {
        JTest jtest = new JTest();
        jtest.setExecutionId(jvoidExecutionContext.getCurrentExecutionId());
        jtest.setIdentifier(randomStr());
        return jtest;
    }

    protected JClass createJClass() {
        JClass jclass = new JClass();
        jclass.setExecutionId(jvoidExecutionContext.getCurrentExecutionId());
        jclass.setIdentifier(randomStr());
        jclass.setSuperClassIdentifier(randomStr());
        jclass.setChecksum(randomStr());
        return jclass;
    }

    protected JMethod createJMethod() {
        JMethod jmethod = new JMethod();
        jmethod.setExecutionId(jvoidExecutionContext.getCurrentExecutionId());
        jmethod.setIdentifier(randomStr());
        jmethod.setChecksum(randomStr());
        return jmethod;
    }
}
