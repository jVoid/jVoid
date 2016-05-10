package io.jvoid.test.configuration;

import org.junit.Test;
import org.junit.runner.RunWith;

import com.google.inject.Inject;

import io.jvoid.configuration.JVoidConfigurationService;
import io.jvoid.exceptions.JVoidConfigurationException;
import io.jvoid.guice.JVoidModule;
import io.jvoid.test.JVoidTestRunner;
import io.jvoid.test.UseModules;

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
