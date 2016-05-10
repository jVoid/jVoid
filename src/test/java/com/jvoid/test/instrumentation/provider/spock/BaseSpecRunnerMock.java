package com.jvoid.test.instrumentation.provider.spock;

import static org.mockito.Mockito.RETURNS_DEEP_STUBS;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.spockframework.runtime.IRunSupervisor;
import org.spockframework.runtime.model.FeatureInfo;

public abstract class BaseSpecRunnerMock {
    public IRunSupervisor supervisor;
    public FeatureInfo currentFeature;

    public BaseSpecRunnerMock() {
        supervisor = mock(IRunSupervisor.class);
        currentFeature = mock(FeatureInfo.class, RETURNS_DEEP_STUBS);
        when(currentFeature.getFeatureMethod().getDescription().toString()).thenReturn(RunFeatureMethodInstrumenterTest.MOCKED_FEATURE_ID);
    }

    // public void runFeature(); Needs to be declared in a runtime-forged class,
    // because it is
    // tricky to reload classes at runtime
    public abstract void runFeature();
}