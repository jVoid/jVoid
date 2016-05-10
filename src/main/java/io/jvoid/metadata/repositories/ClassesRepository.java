package io.jvoid.metadata.repositories;

import java.util.Map;

import org.apache.commons.dbutils.ResultSetHandler;
import org.apache.commons.dbutils.handlers.BeanHandler;
import org.apache.commons.dbutils.handlers.BeanMapHandler;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import io.jvoid.database.MetadataDatabase;
import io.jvoid.metadata.model.JClass;

/**
 *
 */
@Singleton
public class ClassesRepository extends AbstractRepository<JClass, Long>implements TestRelatedEntityRepository<JClass, Long> {
    private ResultSetHandler<JClass> objectHandler = new BeanHandler<>(JClass.class);
    private ResultSetHandler<Map<String, JClass>> identifierMapHandler = new BeanMapHandler<>(JClass.class, "identifier");

    @Inject
    public ClassesRepository(MetadataDatabase database) {
        super(database);
    }

    @Override
    public JClass findById(Long id) {
        return query("SELECT * FROM classes WHERE id = ?", objectHandler, id);
    }

    @Override
    public JClass add(JClass jclass) {
        assertNull(jclass.getId());
        String sql = "INSERT INTO classes " + "(executionId, identifier, checksum, superclassidentifier) " + "VALUES (?, ?, ?, ?)";
        long id = executeInsert(sql, jclass.getExecutionId(), jclass.getIdentifier(), jclass.getChecksum(), jclass.getSuperClassIdentifier());

        jclass.setId(id);

        return jclass;
    }

    public JClass findByIdenfifierAndExecutionId(String identifier, long executionId) {
        return query("SELECT c.* FROM classes c WHERE c.identifier = ? AND c.executionId = ?", objectHandler, identifier, executionId);
    }

    @Override
    public Map<String, JClass> findByTestId(Long testId) {
        return query("SELECT c.* FROM test_classes tc INNER JOIN classes c ON c.id = tc.classId WHERE tc.testId = ?", identifierMapHandler, testId);
    }

    @Override
    public Map<String, JClass> findByExecutionIdAndRelatedToTestId(Long executionId, Long testId) {
        // @formatter:off
        String sql = "SELECT c.* FROM classes c " + 
                     "WHERE c.executionId = ? " + 
                     " AND c.identifier IN (" + 
                     "  SELECT c.identifier " +
                     "  FROM test_classes tc " + 
                     "   INNER JOIN classes c ON c.id = tc.classId " +
                     "  WHERE tc.testId = ? " + 
                     "  GROUP BY c.identifier )";
        // @formatter:on
        return query(sql, identifierMapHandler, executionId, testId);
    }

}