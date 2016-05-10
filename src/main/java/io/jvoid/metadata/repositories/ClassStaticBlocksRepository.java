package io.jvoid.metadata.repositories;

import java.util.Map;

import org.apache.commons.dbutils.ResultSetHandler;
import org.apache.commons.dbutils.handlers.BeanHandler;
import org.apache.commons.dbutils.handlers.BeanMapHandler;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import io.jvoid.database.MetadataDatabase;
import io.jvoid.metadata.model.JClassStaticBlock;

/**
 *
 */
@Singleton
public class ClassStaticBlocksRepository extends AbstractRepository<JClassStaticBlock, Long>
        implements TestRelatedEntityRepository<JClassStaticBlock, Long> {
    private ResultSetHandler<JClassStaticBlock> objectHandler = new BeanHandler<>(
            JClassStaticBlock.class);
    private ResultSetHandler<Map<String, JClassStaticBlock>> identifierMapHandler = new BeanMapHandler<>(
            JClassStaticBlock.class, "identifier");

    @Inject
    public ClassStaticBlocksRepository(MetadataDatabase database) {
        super(database);
    }

    @Override
    public JClassStaticBlock findById(Long id) {
        return query("SELECT * FROM class_static_blocks WHERE id = ?", objectHandler, id);
    }

    @Override
    public JClassStaticBlock add(JClassStaticBlock jstaticblock) {
        assertNull(jstaticblock.getId());
        String sql = "INSERT INTO class_static_blocks (classId, identifier, checksum) VALUES (?, ?, ?)";
        long id = executeInsert(sql, jstaticblock.getClassId(), jstaticblock.getIdentifier(), jstaticblock.getChecksum());
        jstaticblock.setId(id);
        return jstaticblock;
    }

    @Override
    public Map<String, JClassStaticBlock> findByTestId(Long testId) {
        // @formatter:off
        String sql = "SELECT csb.* FROM test_classes tc "
                + " INNER JOIN classes c ON tc.classId = c.id "
                + " INNER JOIN class_static_blocks csb ON csb.classId = c.id "
                + "WHERE tc.testId = ?";
        // @formatter:on
        return query(sql, identifierMapHandler, testId);
    }

    @Override
    public Map<String, JClassStaticBlock> findByExecutionIdAndRelatedToTestId(Long executionId,
            Long testId) {
        // @formatter:off
        String sql = "SELECT csb.* FROM class_static_blocks csb "
                + " INNER JOIN classes c ON csb.classId = c.id " + " WHERE c.executionId = ? "
                + " AND csb.identifier IN (" 
                + "  SELECT csb.identifier "
                + "  FROM test_classes tc "
                + "   INNER JOIN classes c ON c.id = tc.classId "
                + "   INNER JOIN class_static_blocks csb ON csb.classId = c.id "
                + "  WHERE tc.testId = ? "
                + "  GROUP BY csb.identifier ) ";
        // @formatter:on
        return query(sql, identifierMapHandler, executionId, testId);
    }

}