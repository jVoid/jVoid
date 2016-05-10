package com.jvoid.test;

import java.util.ArrayList;
import java.util.List;

import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.model.InitializationError;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class JVoidTestRunner extends BlockJUnit4ClassRunner {

    private transient Injector injector;

    public JVoidTestRunner(Class<?> klass) throws InitializationError {
        super(klass);
        Class<?>[] moduleClasses = getModulesFor(klass);
        if (moduleClasses != null) {
            injector = createInjectorFor(moduleClasses);
        }
    }

    private Class<?>[] getModulesFor(final Class<?> klass) throws InitializationError {
        final UseModules annotation = klass.getAnnotation(UseModules.class);
        if (annotation == null) {
            log.info("No @UseModules annotation in unit test '{}'...", klass.getName());
            return null;
        }
        return annotation.value();
    }

    private Injector createInjectorFor(final Class<?>[] classes) throws InitializationError {
        final List<Module> modules = new ArrayList<>(classes.length);
        for (final Class<?> moduleClass : classes) {
            try {
                Module module = (Module) moduleClass.newInstance();
                modules.add(module);
            } catch (final ReflectiveOperationException exception) {
                throw new InitializationError(exception);
            }
        }
        return Guice.createInjector(modules);
    }

    @Override
    public final Object createTest() throws Exception {
        final Object theTest = super.createTest();
        injector.injectMembers(theTest);
        return theTest;
    }
}
