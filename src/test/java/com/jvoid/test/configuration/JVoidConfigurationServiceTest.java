package com.jvoid.test.configuration;

import org.junit.Test;
import org.junit.runner.RunWith;

import com.google.inject.Inject;
import com.jvoid.configuration.JVoidConfigurationService;
import com.jvoid.exceptions.JVoidConfigurationException;
import com.jvoid.guice.JVoidModule;
import com.jvoid.test.JVoidTestRunner;
import com.jvoid.test.UseModules;

@UseModules({ JVoidModule.class })
@RunWith(JVoidTestRunner.class)
public class JVoidConfigurationServiceTest {

    @Inject
    JVoidConfigurationService confService;

    @Test(expected = JVoidConfigurationException.class)
    public void testInvalidConfigurationParameter() throws JVoidConfigurationException {
        confService.loadConfiguration("./src/test/resources/invalid-jvoid.config");
    }

}
