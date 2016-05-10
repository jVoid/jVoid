package io.jvoid.database;

import java.sql.Connection;
import java.sql.SQLException;

import org.flywaydb.core.Flyway;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import io.jvoid.configuration.JVoidConfiguration;
import io.jvoid.exceptions.JVoidDataAccessException;

/**
 * This class manages the data-source for accessing the particular database used with JVoid.
 *
 */
@Singleton
public class MetadataDatabase {

    private JVoidConfiguration configuration;

    private HikariDataSource ds;

    /**
     *
     * @param configuration
     */
    @Inject
    public MetadataDatabase(JVoidConfiguration configuration) {
        this.configuration = configuration;
    }

    /**
     *
     */
    @Inject
    public void startup() {
        if (ds != null && !ds.isClosed()) {
            return;
        }

        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(configuration.dbUrl());
        config.setUsername(configuration.dbUsername());
        config.setPassword(configuration.dbPassword());
        config.addDataSourceProperty("cachePrepStmts", "true");
        config.addDataSourceProperty("prepStmtCacheSize", "250");
        config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");

        ds = new HikariDataSource(config);

        initializeDatabase();
    }

    /**
     *
     */
    public void shutdown() {
        if (ds != null) {
            ds.close();
            ds = null;
        }
    }

    /**
     *
     */
    private void initializeDatabase() {
        Flyway flyway = new Flyway();
        flyway.setDataSource(ds);
        flyway.migrate();
    }

    /**
     *
     * @return A connection from the pool
     */
    public Connection getConnection() {
        try {
            return ds.getConnection();
        } catch (SQLException e) {
            throw new JVoidDataAccessException(e);
        }
    }
}
