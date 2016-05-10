package com.jvoid.guice;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.jvoid.configuration.JVoidConfiguration;
import com.jvoid.configuration.JVoidConfigurationService;
import com.jvoid.exceptions.JVoidConfigurationException;

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
