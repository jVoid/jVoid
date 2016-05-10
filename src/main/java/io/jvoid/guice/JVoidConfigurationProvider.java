package io.jvoid.guice;

import com.google.inject.Inject;
import com.google.inject.Provider;

import io.jvoid.configuration.JVoidConfiguration;
import io.jvoid.configuration.JVoidConfigurationService;
import io.jvoid.exceptions.JVoidConfigurationException;

/**
 * Guice provider for the current @{code JVoidConfiguration}
 *
 */
public class JVoidConfigurationProvider implements Provider<JVoidConfiguration> {

    private JVoidConfigurationService configurationService;

    @Inject
    public JVoidConfigurationProvider(JVoidConfigurationService configurationService) {
        this.configurationService = configurationService;
    }

    @Override
    public JVoidConfiguration get() {
        try {
            return configurationService.getConfiguration();
        } catch (JVoidConfigurationException e) {
            return null;
        }
    }

}
