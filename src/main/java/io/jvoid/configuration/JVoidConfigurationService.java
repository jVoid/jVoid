package io.jvoid.configuration;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import io.jvoid.exceptions.JVoidConfigurationException;
import lombok.extern.slf4j.Slf4j;

/**
 * Basic service to load and get the {@code JVoidConfiguration}.
 *
 */
@Slf4j
@Singleton
public class JVoidConfigurationService {

    private JVoidPropertiesBasedConfigurationLoader loader;

    private JVoidConfigurationValidator validator;
    
    private JVoidConfiguration configuration;

    @Inject
    public JVoidConfigurationService(JVoidPropertiesBasedConfigurationLoader loader, JVoidConfigurationValidator validator) {
        this.loader = loader;
        this.validator = validator;
    }

    public synchronized void loadConfiguration(String parameterConfigurationPath) throws JVoidConfigurationException {
        if (configuration == null) {
            configuration = loader.load(parameterConfigurationPath);
            try {
                validator.validate(configuration);
            } catch (JVoidConfigurationException e) {
                log.error("Invalid configuration: " + e.getMessage(), e);
                throw e;
            }
        }
    }

    public JVoidConfiguration getConfiguration() throws JVoidConfigurationException {
        if (configuration == null) {
            loadConfiguration(null);
        }
        return configuration;
    }

}
