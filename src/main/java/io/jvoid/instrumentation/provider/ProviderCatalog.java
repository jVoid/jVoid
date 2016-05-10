package io.jvoid.instrumentation.provider;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import io.jvoid.instrumentation.provider.api.InstrumentationProvider;
import io.jvoid.instrumentation.provider.app.AppInstrumentationProvider;
import io.jvoid.instrumentation.provider.junit4.JUnitInstrumentationProvider;
import io.jvoid.instrumentation.provider.spock.SpockInstrumentationProvider;
import javassist.CtClass;

/**
 * Catalog of all the instrumentation providers that the JVoid agent will apply
 * at runtime for classes instrumentation.
 *
 */
@Singleton
public class ProviderCatalog {

    private List<InstrumentationProvider> registeredProviders = new ArrayList<>();

    // This is still "hardcoded" providers. Make it discover the providers in
    // the classpath automatically
    @Inject
    public void initializeProviders(AppInstrumentationProvider appInstrumentationProvider,
            SpockInstrumentationProvider spockInstrumentationProvider,
            JUnitInstrumentationProvider junitInstrumentationProvider) {
        registeredProviders.add(appInstrumentationProvider);
        registeredProviders.add(spockInstrumentationProvider);
        registeredProviders.add(junitInstrumentationProvider);
    }

    public void addProvider(InstrumentationProvider provider) {
        registeredProviders.add(provider);
    }

    public Set<InstrumentationProvider> getProvidersForClass(CtClass ctClass) {
        Set<InstrumentationProvider> providers = new LinkedHashSet<>();
        for (InstrumentationProvider provider : registeredProviders) {
            if (provider.matches(ctClass)) {
                providers.add(provider);
            }
        }
        return providers;
    }

}
