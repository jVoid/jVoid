package io.jvoid.metadata.checksum;

import com.google.inject.Inject;

import io.jvoid.bytecode.JavassistUtils;
import io.jvoid.configuration.JVoidConfiguration;
import javassist.CtBehavior;
import javassist.CtClass;
import javassist.NotFoundException;
import lombok.extern.slf4j.Slf4j;

/**
 *
 */
@Slf4j
abstract class AbstractCtBehaviorChecksummer<S extends CtBehavior> extends AbstractChecksummer<S> {

    @Inject
    public AbstractCtBehaviorChecksummer(JVoidConfiguration jVoidConfiguration) {
        super(jVoidConfiguration);
    }

    @Override
    public String checksum(S behavior) {
        StringBuilder data = new StringBuilder(1024);

        data.append(behavior.getLongName());

        try {
            for (CtClass exceptionTypes : behavior.getExceptionTypes()) {
                data.append(exceptionTypes.getName());
            }
        } catch (NotFoundException e) {
            log.trace("CtClass not found when adding parameter types to behaviour checksum", e);
        }
        
        for (Object[] parameterAnnotations : behavior.getAvailableParameterAnnotations()) {
            for (Object annotation : parameterAnnotations) {
                data.append(annotation.toString());
            }
        }
        
        data.append(JavassistUtils.getBehaviourBytecode(behavior));
        
        for (Object annotation : behavior.getAvailableAnnotations()) {
            data.append(annotation.toString());
        }
        
        data.append(getExtraComponents(behavior));
        return computeChecksum(data.toString().getBytes());
    }

    protected String getExtraComponents(S input) {
        return "";
    }
}
