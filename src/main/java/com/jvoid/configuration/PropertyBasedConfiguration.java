package com.jvoid.configuration;

import java.util.Properties;

/**
 * A {@code JVoidConfiguration} based on simple properties file.
 *
 */
public class PropertyBasedConfiguration implements JVoidConfiguration {

    private Properties properties;

    public PropertyBasedConfiguration(Properties properties) {
        if (properties == null) {
            throw new IllegalArgumentException("Backing properties needed for the configuration");
        }

        this.properties = properties;
    }

    @Override
    public String dbUrl() {
        return properties.getProperty("db.url");
    }

    @Override
    public String dbUsername() {
        return properties.getProperty("db.username");
    }

    @Override
    public String dbPassword() {
        return properties.getProperty("db.password");
    }

    @Override
    public String basePackage() {
        return properties.getProperty("app.package");
    }

    @Override
    public String excludes() {
        return properties.getProperty("app.excludes");
    }

    @Override
    public String includes() {
        return properties.getProperty("app.includes");
    }

    @Override
    public Boolean heuristicExcludeCglib() {
        return Boolean.valueOf(properties.getProperty("app.heuristic.excludeCglib"));
    }

    @Override
    public Boolean heuristicExcludeJacoco() {
        return Boolean.valueOf(properties.getProperty("app.heuristic.excludeJacoco"));
    }

    @Override
    public Boolean heuristicExcludeGroovyCallSite() {
        return Boolean.valueOf(properties.getProperty("app.heuristic.excludeGroovyCallSite"));
    }

}
