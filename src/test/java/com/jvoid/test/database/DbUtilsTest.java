package com.jvoid.test.database;

import java.util.Arrays;
import java.util.Collections;

import org.apache.commons.dbutils.handlers.ScalarHandler;
import org.junit.Test;

import com.jvoid.database.DbUtils;
import com.jvoid.exceptions.JVoidDataAccessException;
import com.jvoid.test.AbstractJVoidTest;

public class DbUtilsTest extends AbstractJVoidTest {

    @Test(expected = JVoidDataAccessException.class)
    public void testInvalidUpdateStatement() {
        DbUtils.executeUpdate(metadataDatabase, "UPDATE no_table(id) SET id=1");
    }

    @Test(expected = JVoidDataAccessException.class)
    public void testInvalidInsertStatement() {
        DbUtils.executeInsert(metadataDatabase, "INSERT INTO no_table(id) VALUES (1)");
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void testInvalidMultipleInsertStatement() {
        DbUtils.executeMultipleInserts(metadataDatabase, Arrays.asList(
                                        "INSERT INTO no_table(id) VALUES (1)",
                                        "INSERT INTO no_table(id) VALUES (2)"),
                                       Collections.<Object[]>emptyList());
    }

    @Test(expected = JVoidDataAccessException.class)
    public void testInvalidMultipleInsertStatement2() {
        DbUtils.executeMultipleInserts(metadataDatabase, Arrays.asList(
                "INSERT INTO no_table(id) VALUES (1)",
                "INSERT INTO no_table(id) VALUES (2)"),
                Arrays.asList(new Object[0], new Object[0]));
    }

    @Test(expected = JVoidDataAccessException.class)
    public void testInvalidQuery() {
        DbUtils.query(metadataDatabase, "SELECT id FROM no_table", new ScalarHandler<Long>());
    }

}
