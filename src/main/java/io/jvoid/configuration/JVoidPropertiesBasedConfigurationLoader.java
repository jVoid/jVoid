package io.jvoid.configuration;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Properties;

import io.jvoid.exceptions.JVoidConfigurationException;

/**
 * Specific loader for the {@code JVoidConfiguration} that builds the configuration up
 * in a hierarchical fashion from multiple properties files.
 *
 */
public class JVoidPropertiesBasedConfigurationLoader {

    private static final String CONFIG_FILE_NAME = "jvoid.config";

    public synchronized PropertyBasedConfiguration load(String parameterConfigPath) throws JVoidConfigurationException {
        Properties properties = new Properties();
        properties.putAll(loadFromClasspath());
        properties.putAll(loadFromHomeDir());
        properties.putAll(loadFromWorkingDir());
        if (parameterConfigPath != null && !parameterConfigPath.isEmpty()) {
            properties.putAll(loadFromParameterValue(parameterConfigPath));
        }

        return new PropertyBasedConfiguration(properties);
    }

    private Map<? extends Object, ? extends Object> loadFromParameterValue(String parameterConfigPath) throws JVoidConfigurationException {
        return loadFromDir(parameterConfigPath);
    }

    private Map<? extends Object, ? extends Object> loadFromWorkingDir() throws JVoidConfigurationException {
        String path = System.getProperty("user.dir") + File.separator + CONFIG_FILE_NAME;
        return loadFromDir(path);
    }

    private Map<? extends Object, ? extends Object> loadFromHomeDir() throws JVoidConfigurationException {
        String path = System.getProperty("user.home") + File.separator + CONFIG_FILE_NAME;
        return loadFromDir(path);
    }

    private Map<? extends Object, ? extends Object> loadFromClasspath() throws JVoidConfigurationException {
        Properties properties = new Properties();
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        InputStream classPathConfiguration = classLoader.getResourceAsStream(CONFIG_FILE_NAME);
        if (classPathConfiguration != null) {
            try {
                properties.load(classPathConfiguration);
            } catch (IOException e) {
                throw new JVoidConfigurationException("Unable to read builtin configuration", e);
            }
        }

        return properties;
    }

    private Map<? extends Object, ? extends Object> loadFromDir(String path) throws JVoidConfigurationException {
        Properties properties = new Properties();
        File file = new File(path);
        if (file.exists()) {
            FileInputStream fis;
            try {
                fis = new FileInputStream(file);
                properties.load(fis);
            } catch (IOException e) {
                throw new JVoidConfigurationException("Unable to read configuration from " + path, e);
            }
        }
        return properties;
    }
}
