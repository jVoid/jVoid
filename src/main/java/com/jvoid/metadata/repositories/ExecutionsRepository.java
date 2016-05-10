package com.jvoid.metadata.repositories;

import org.apache.commons.dbutils.ResultSetHandler;
import org.apache.commons.dbutils.handlers.BeanHandler;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.jvoid.database.MetadataDatabase;
import com.jvoid.metadata.model.JExecution;

/**
 *
 */
@Singleton
public class ExecutionsRepository extends AbstractRepository<JExecution, Long> {

    private ResultSetHandler<JExecution> objectHandler = new BeanHandler<>(JExecution.class);

    @Inject
    public ExecutionsRepository(MetadataDatabase database) {
        super(database);
    }

    @Override
    public JExecution findById(Long id) {
        return query("SELECT * FROM executions WHERE id = ?", objectHandler, id);
    }

    @Override
    public JExecution add(JExecution execution) {
        assertNull(execution.getId());
        long id = executeInsert("INSERT INTO executions (timestamp) VALUES (?)", execution.getTimestamp());
        execution.setId(id);
        return execution;
    }

}