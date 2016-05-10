package io.jvoid.metadata.checksum;

import com.google.inject.Inject;

import io.jvoid.configuration.JVoidConfiguration;
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
