package com.jvoid.metadata.repositories;

import java.util.Map;

import org.apache.commons.dbutils.ResultSetHandler;
import org.apache.commons.dbutils.handlers.BeanHandler;
import org.apache.commons.dbutils.handlers.BeanMapHandler;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.jvoid.database.MetadataDatabase;
import com.jvoid.metadata.model.JMethod;

/**
 *
 */
@Singleton
public class MethodsRepository extends AbstractRepository<JMethod, Long>
        implements TestRelatedEntityRepository<JMethod, Long> {
    private ResultSetHandler<JMethod> objectHandler = new BeanHandler<>(JMethod.class);
    private ResultSetHandler<Map<String, JMethod>> identifierMapHandler = new BeanMapHandler<>(
            JMethod.class, "identifier");

    @Inject
    public MethodsRepository(MetadataDatabase database) {
        super(database);
    }

    @Override
    public JMethod findById(Long id) {
        return query("SELECT * FROM methods WHERE id = ?", objectHandler, id);
    }

    @Override
    public JMethod add(JMethod jMethod) {
        assertNull(jMethod.getId());
        String sql = "INSERT INTO methods (executionId, identifier, checksum) VALUES (?, ?, ?)";
        long id = executeInsert(sql, jMethod.getExecutionId(), jMethod.getIdentifier(), jMethod.getChecksum());
        jMethod.setId(id);
        return jMethod;
    }

    public JMethod findByIdenfifierAndExecutionId(String identifier, long executionId) {
        return query("SELECT m.* FROM methods m WHERE m.identifier = ? AND m.executionId = ?",
                objectHandler, identifier, executionId);
    }

    @Override
    public Map<String, JMethod> findByTestId(Long testId) {
        String sql = "SELECT m.* FROM test_methods tm INNER JOIN methods m ON m.id = tm.methodId WHERE tm.testId = ?";
        return query(sql, identifierMapHandler, testId);
    }

    @Override
    public Map<String, JMethod> findByExecutionIdAndRelatedToTestId(Long executionId, Long testId) {
        // @formatter:off
        String sql = "SELECT m.* "
                + "FROM methods m "
                + "WHERE m.executionId = ? "
                + " AND m.identifier IN (" + " SELECT m.identifier "
                + " FROM test_methods tm "
                + "  INNER JOIN methods m ON m.id = tm.methodId "
                + " WHERE tm.testId = ? "
                + " GROUP BY m.identifier ) ";
        // @formatter:on
        return query(sql, identifierMapHandler, executionId, testId);
    }
}