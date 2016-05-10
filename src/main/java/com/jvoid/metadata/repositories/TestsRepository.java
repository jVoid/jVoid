package com.jvoid.metadata.repositories;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.dbutils.ResultSetHandler;
import org.apache.commons.dbutils.handlers.BeanHandler;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.jvoid.database.MetadataDatabase;
import com.jvoid.metadata.model.JTest;

/**
 *
 */
@Singleton
public class TestsRepository extends AbstractRepository<JTest, Long> {

    private ResultSetHandler<JTest> objectHandler = new BeanHandler<>(JTest.class);

    @Inject
    public TestsRepository(MetadataDatabase database) {
        super(database);
    }

    @Override
    public JTest findById(Long id) {
        return query("SELECT * FROM tests WHERE id = ?", objectHandler, id);
    }

    @Override
    public JTest add(JTest jtest) {
        assertNull(jtest.getId());
        String sql = "INSERT INTO tests (executionId, identifier, runStatus) VALUES (?, ?, ?)";
        long id = executeInsert(sql, jtest.getExecutionId(), jtest.getIdentifier(), jtest.getRunStatus());
        jtest.setId(id);
        return jtest;
    }

    public JTest update(JTest jtest) {
        String sql = "UPDATE tests SET executionId = ?, identifier = ?, runStatus = ? WHERE id = ?";
        executeUpdate(sql, jtest.getExecutionId(), jtest.getIdentifier(), jtest.getRunStatus(), jtest.getId());
        return jtest;
    }
    
    public void linkMethodAndClass(JTest test, long jmethodId, long classId) {
        List<String> sqlStms = new ArrayList<>();
        List<Object[]> parametersList = new ArrayList<>();
        sqlStms.add("INSERT INTO test_methods (testId, methodId) VALUES (?, ?) ON DUPLICATE KEY UPDATE testId=VALUES(testId), methodId=VALUES(methodId)");
        parametersList.add( new Object[]{test.getId(), jmethodId});
        sqlStms.add("INSERT INTO test_classes (testId, classId) VALUES (?, ?) ON DUPLICATE KEY UPDATE testId=VALUES(testId), classId=VALUES(classId)");
        parametersList.add( new Object[]{test.getId(), classId});
        
        executeMultipleInserts(sqlStms, parametersList);
    }
    
    public void linkMethod(JTest test, Long jmethodId) {
        String sql = "INSERT INTO test_methods (testId, methodId) VALUES (?, ?) ON DUPLICATE KEY UPDATE testId=VALUES(testId), methodId=VALUES(methodId)";
        executeInsert(sql, test.getId(), jmethodId);
    }

    public void linkClass(JTest test, Long jclassId) {
        String sql = "INSERT INTO test_classes (testId, classId) VALUES (?, ?) ON DUPLICATE KEY UPDATE testId=VALUES(testId), classId=VALUES(classId)";
        executeInsert(sql, test.getId(), jclassId);
    }

    public JTest findByIdenfifierAndExecutionId(String identifier, long executionId) {
        return query("SELECT t.* FROM tests t WHERE t.identifier = ? AND t.executionId = ?", objectHandler, identifier, executionId);
    }

    public JTest findLatestExecutedByIdenfifier(String identifier) {
        // @formatter:off
        String sql = "SELECT t.* " +
                     "FROM tests t " +
                     "WHERE t.identifier = ? " +
                     " AND (t.runStatus = 'FAILED' OR t.runStatus = 'RUN') " +
                     " AND t.executionId = (" +
                     "  SELECT MAX(t.executionId) FROM tests t " +
                     "  WHERE t.identifier = ? " +
                     "   AND (t.runStatus = 'FAILED' OR t.runStatus = 'RUN') " +
                     " )";
        // @formatter:on
        return query(sql, objectHandler, identifier, identifier);
    }

}