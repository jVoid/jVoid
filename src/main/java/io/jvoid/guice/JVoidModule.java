package io.jvoid.guice;

import com.google.inject.AbstractModule;
import com.google.inject.Singleton;

import io.jvoid.configuration.JVoidConfiguration;

/**
 * Guice module for JVoid
 */
public class JVoidModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(JVoidConfiguration.class).toProvider(JVoidConfigurationProvider.class).in(Singleton.class);
    }

}
