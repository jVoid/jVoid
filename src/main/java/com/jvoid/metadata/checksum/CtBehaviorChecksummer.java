package com.jvoid.metadata.checksum;

import com.google.inject.Inject;
import com.jvoid.configuration.JVoidConfiguration;

import javassist.CtBehavior;

/**
 *
 */
class CtBehaviorChecksummer extends AbstractCtBehaviorChecksummer<CtBehavior> {

    @Inject
    public CtBehaviorChecksummer(JVoidConfiguration jVoidConfiguration) {
        super(jVoidConfiguration);
    }

}
