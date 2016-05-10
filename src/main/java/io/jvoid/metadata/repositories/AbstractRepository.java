package io.jvoid.metadata.repositories;

import java.util.List;

import org.apache.commons.dbutils.ResultSetHandler;

import io.jvoid.database.DbUtils;
import io.jvoid.database.MetadataDatabase;

/**
 * Generic repository class extended by all the concrete JVoid repositories.
 *
 */
public abstract class AbstractRepository<T, I> implements BaseRepository<T, I> {

    protected MetadataDatabase database;

    public AbstractRepository(MetadataDatabase database) {
        this.database = database;
    }

    protected void executeUpdate(String sql, Object... parameters) {
        DbUtils.executeUpdate(database, sql, parameters);
    }

    protected Long executeInsert(String sql, Object... parameters) {
        return DbUtils.executeInsert(database, sql, parameters);
    }

    protected List<Long> executeMultipleInserts(List<String> sqlList, List<Object[]> parametersList) {
        return DbUtils.executeMultipleInserts(database, sqlList, parametersList);
    }

    protected <U> U query(String sql, ResultSetHandler<U> resultSetHandler, Object... parameters) {
        return DbUtils.query(database, sql, resultSetHandler, parameters);
    }

    protected static void assertNull(Object value) {
        if (value != null) {
            throw new RuntimeException();
        }
    }
}
