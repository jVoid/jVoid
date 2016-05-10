package io.jvoid.test.configuration;

import java.util.Properties;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import io.jvoid.configuration.JVoidConfiguration;
import io.jvoid.configuration.JVoidConfigurationValidator;
import io.jvoid.configuration.JVoidPropertiesBasedConfigurationLoader;
import io.jvoid.configuration.PropertyBasedConfiguration;
import io.jvoid.exceptions.JVoidConfigurationException;

public class JVoidConfigurationValidatorTest {

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    @Test
    public void testValidateEmptyConfig() {
        Properties properties = new Properties();
        JVoidConfiguration config = new PropertyBasedConfiguration(properties);
        try {
            new JVoidConfigurationValidator().validate(config);
            Assert.fail();
        } catch (JVoidConfigurationException e) {
        }
    }

    @Test
    public void testValidateClasspathConfig() {
        try {
            JVoidConfiguration config = new JVoidPropertiesBasedConfigurationLoader().load("./src/test/resources/invalid-jvoid.config");
            new JVoidConfigurationValidator().validate(config);
            Assert.fail();
        } catch (JVoidConfigurationException e) {
        }
    }

    @Test
    public void testValidConfig() {
        String validFile = this.getClass().getClassLoader().getResource("jvoid.config").getFile();
        try {
            JVoidConfiguration config = new JVoidPropertiesBasedConfigurationLoader().load(validFile);
            new JVoidConfigurationValidator().validate(config);
        } catch (JVoidConfigurationException e) {
            Assert.fail();
        }
    }

    @Test
    public void testInValidConfig() {
        String validFile = this.getClass().getClassLoader().getResource("invalid-jvoid.config").getFile();
        try {
            JVoidConfiguration config = new JVoidPropertiesBasedConfigurationLoader().load(validFile);
            new JVoidConfigurationValidator().validate(config);
            Assert.fail();
        } catch (JVoidConfigurationException e) {
        }
    }

}
