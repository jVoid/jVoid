package com.jvoid.test.configuration;

import java.util.Properties;

import org.junit.Assert;
import org.junit.Test;

import com.jvoid.configuration.PropertyBasedConfiguration;

public class PropertyBasedConfigurationTest {

    private static final String TEST_VALUE = "test_value";

    @Test
    public void testInvalidProperties() {
        Properties properties = new Properties();
        properties.setProperty("db.location", TEST_VALUE);
        try {
            new PropertyBasedConfiguration(null);
            Assert.fail();
        } catch (IllegalArgumentException e) {
        }
    }

    @Test
    public void testDbUrl() {
        Properties properties = new Properties();
        properties.setProperty("db.url", TEST_VALUE);
        PropertyBasedConfiguration config = new PropertyBasedConfiguration(properties);
        Assert.assertEquals(TEST_VALUE, config.dbUrl());
    }

    @Test
    public void testDbUsername() {
        Properties properties = new Properties();
        properties.setProperty("db.username", TEST_VALUE);
        PropertyBasedConfiguration config = new PropertyBasedConfiguration(properties);
        Assert.assertEquals(TEST_VALUE, config.dbUsername());
    }

    @Test
    public void testDbPassword() {
        Properties properties = new Properties();
        properties.setProperty("db.password", TEST_VALUE);
        PropertyBasedConfiguration config = new PropertyBasedConfiguration(properties);
        Assert.assertEquals(TEST_VALUE, config.dbPassword());
    }

    @Test
    public void testBasePackage() {
        Properties properties = new Properties();
        properties.setProperty("app.package", TEST_VALUE);
        PropertyBasedConfiguration config = new PropertyBasedConfiguration(properties);
        Assert.assertEquals(TEST_VALUE, config.basePackage());
    }

    @Test
    public void testIncludes() {
        Properties properties = new Properties();
        properties.setProperty("app.includes", TEST_VALUE);
        PropertyBasedConfiguration config = new PropertyBasedConfiguration(properties);
        Assert.assertEquals(TEST_VALUE, config.includes());
    }

    @Test
    public void testExcludes() {
        Properties properties = new Properties();
        properties.setProperty("app.excludes", TEST_VALUE);
        PropertyBasedConfiguration config = new PropertyBasedConfiguration(properties);
        Assert.assertEquals(TEST_VALUE, config.excludes());
    }

}
