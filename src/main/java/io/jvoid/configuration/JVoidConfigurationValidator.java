package io.jvoid.configuration;

import io.jvoid.exceptions.JVoidConfigurationException;

/**
 * Helper class used to validate the {@code JVoidConfiguration}.
 *
 */
public class JVoidConfigurationValidator {

    public void validate(JVoidConfiguration config) throws JVoidConfigurationException {
        if (config.basePackage() == null || config.basePackage().isEmpty()) {
            throw new JVoidConfigurationException("Configuration needs a 'app.package' property.");
        }
    }
}
