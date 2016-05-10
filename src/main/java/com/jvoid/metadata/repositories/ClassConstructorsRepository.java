package com.jvoid.metadata.repositories;

import java.util.Map;

import org.apache.commons.dbutils.ResultSetHandler;
import org.apache.commons.dbutils.handlers.BeanHandler;
import org.apache.commons.dbutils.handlers.BeanMapHandler;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.jvoid.database.MetadataDatabase;
import com.jvoid.metadata.model.JClassConstructor;

/**
 *
 */
@Singleton
public class ClassConstructorsRepository extends AbstractRepository<JClassConstructor, Long>
        implements TestRelatedEntityRepository<JClassConstructor, Long> {
    private ResultSetHandler<JClassConstructor> objectHandler = new BeanHandler<>(
            JClassConstructor.class);
    private ResultSetHandler<Map<String, JClassConstructor>> identifierMapHandler = new BeanMapHandler<>(
            JClassConstructor.class, "identifier");

    @Inject
    public ClassConstructorsRepository(MetadataDatabase database) {
        super(database);
    }

    @Override
    public JClassConstructor findById(Long id) {
        return query("SELECT * FROM class_constructors WHERE id = ?", objectHandler, id);
    }

    @Override
    public JClassConstructor add(JClassConstructor jconstructor) {
        assertNull(jconstructor.getId());
        String sql = "INSERT INTO class_constructors (classId, identifier, checksum) VALUES (?, ?, ?)";
        long id = executeInsert(sql,jconstructor.getClassId(), jconstructor.getIdentifier(), jconstructor.getChecksum());
        jconstructor.setId(id);
        return jconstructor;
    }

    @Override
    public Map<String, JClassConstructor> findByTestId(Long testId) {
        // @formatter:off
        String sql = "SELECT cc.* FROM test_classes tc "
                + " INNER JOIN classes c ON tc.classId = c.id "
                + " INNER JOIN class_constructors cc ON cc.classId = c.id WHERE tc.testId = ? ";
        // @formatter:on
        return query(sql, identifierMapHandler, testId);
    }

    @Override
    public Map<String, JClassConstructor> findByExecutionIdAndRelatedToTestId(Long executionId,
            Long testId) {
        // @formatter:off
        String sql = "SELECT cc.* FROM class_constructors cc "
                + " INNER JOIN classes c ON cc.classId = c.id "
                + " WHERE c.executionId = ? "
                + " AND cc.identifier IN ("
                + " SELECT cc.identifier FROM test_classes tc "
                + "  INNER JOIN classes c ON c.id = tc.classId "
                + "  INNER JOIN class_constructors cc ON cc.classId = c.id "
                + "  WHERE tc.testId = ? "
                + "  GROUP BY cc.identifier ) ";
        // @formatter:on
        return query(sql, identifierMapHandler, executionId, testId);
    }
}