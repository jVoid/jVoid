package com.jvoid.database;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.ResultSetHandler;
import org.apache.commons.dbutils.handlers.ScalarHandler;

import com.jvoid.exceptions.JVoidDataAccessException;

/**
 * Simple utility class that makes it easy to interact with a relational DB using
 * apache dbutils API.
 *
 */
public class DbUtils {

    private static final QueryRunner queryRunner = new QueryRunner();

    private DbUtils() {
        super();
    }

    /**
     *
     * @param database
     * @param sql
     * @param parameters
     */
    public static void executeUpdate(MetadataDatabase database, String sql, Object... parameters) {
        try (Connection conn = database.getConnection()) {
            QueryRunner query = new QueryRunner();
            query.update(conn, sql, parameters);
        } catch (SQLException e) {
            throw new JVoidDataAccessException(e);
        }
    }

    /**
     *
     * @param database
     * @param sql
     * @param parameters
     * @return
     */
    public static Long executeInsert(MetadataDatabase database, String sql, Object... parameters) {
        try (Connection conn = database.getConnection()) {
            ResultSetHandler<Long> rsh = new ScalarHandler<>();
            return queryRunner.insert(conn, sql, rsh, parameters);
        } catch (SQLException e) {
            throw new JVoidDataAccessException(e);
        }
    }

    /**
     *
     * @param database
     * @param sqlList
     * @param parametersList
     * @return
     */
    public static List<Long> executeMultipleInserts(MetadataDatabase database, List<String> sqlList, List<Object[]> parametersList) {
        try (Connection conn = database.getConnection()) {
            List<Long> results = new ArrayList<>();
            for (int i = 0; i < sqlList.size(); i++) {
                Object[] parameters = parametersList.get(i);
                String sql = sqlList.get(i);
                
                ResultSetHandler<Long> rsh = new ScalarHandler<>();
                Long id = queryRunner.insert(conn, sql, rsh, parameters);
                results.add(id);
            }
            return results;
        } catch (SQLException e) {
            throw new JVoidDataAccessException(e);
        }
    }

    /**
     *
     * @param database
     * @param sql
     * @param resultSetHandler
     * @param parameters
     * @return
     */
    public static <T> T query(MetadataDatabase database, String sql, ResultSetHandler<T> resultSetHandler, Object... parameters) {
        try (Connection conn = database.getConnection()) {
            return queryRunner.query(conn, sql, resultSetHandler, parameters);
        } catch (SQLException e) {
            throw new JVoidDataAccessException(e);
        }
    }
}
