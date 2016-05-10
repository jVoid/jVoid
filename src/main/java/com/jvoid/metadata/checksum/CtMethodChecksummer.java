package com.jvoid.metadata.checksum;

import com.google.inject.Inject;
import com.jvoid.configuration.JVoidConfiguration;

import javassist.CtMethod;
import javassist.NotFoundException;
import lombok.extern.slf4j.Slf4j;

/**
 *
 */
@Slf4j
class CtMethodChecksummer extends AbstractCtBehaviorChecksummer<CtMethod> {

    @Inject
    public CtMethodChecksummer(JVoidConfiguration jVoidConfiguration) {
        super(jVoidConfiguration);
    }

    @Override
    public String getExtraComponents(CtMethod method) {
        StringBuilder dataToChecksum = new StringBuilder(1024);
        try {
            if (method.getReturnType() != null) {
                dataToChecksum.append(method.getReturnType().getName());
            }
        } catch (NotFoundException e) {
            log.trace("CtClass not found when adding return type to method checksum", e);
        }
        return dataToChecksum.toString();
    }

}
