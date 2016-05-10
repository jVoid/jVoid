package io.jvoid.metadata.checksum;

import com.google.inject.Inject;

import io.jvoid.configuration.JVoidConfiguration;
import io.jvoid.instrumentation.provider.app.AppHeuristicHelper;
import javassist.CtClass;
import javassist.CtField;
import javassist.NotFoundException;
import lombok.extern.slf4j.Slf4j;

/**
 *
 */
@Slf4j
class CtClassChecksummer extends AbstractChecksummer<CtClass> {

    @Inject
    public CtClassChecksummer(JVoidConfiguration jVoidConfiguration) {
        super(jVoidConfiguration);
    }

    @Override
    public String checksum(CtClass clazz) {
        StringBuilder data = new StringBuilder(1024);

        handleInterfaces(clazz, data);
        
        handleSuperClass(clazz, data);

        handleAnnotations(clazz, data);

        handleFields(clazz, data);
        
        return computeChecksum(data.toString().getBytes());
    }

    private void handleFields(CtClass clazz, StringBuilder data) {
        // This is not ideal, this should be in the actual method checksum to prevent a change to a class to invalidade all the tests that use it
        // However we can hope on our users design skills so that the target classes state will be relevant to *all* methods :)
        for (CtField field : clazz.getDeclaredFields()) {
            if ( (jVoidConfiguration.heuristicExcludeJacoco() && AppHeuristicHelper.isJacocoField(field)) ||
                 (AppHeuristicHelper.isGeneratedField(field))
               ){
                continue;
            }

            for (Object annotation : field.getAvailableAnnotations()) {
                data.append(annotation.toString());
            }
            try {
                data.append(field.getName());
                data.append(field.getType().getName());
            } catch (NotFoundException e) {
                log.trace("Class not found while getting field type for class checksum", e);
            }
            data.append(field.getModifiers());

            // if the field is a constant of primitive type or string this will not be null
            if (field.getConstantValue() != null) {
                data.append(field.getConstantValue().toString());
            }
        }
    }

    private void handleAnnotations(CtClass clazz, StringBuilder data) {
        for (Object annotation : clazz.getAvailableAnnotations()) {
            data.append(annotation.toString());
        }
    }

    private void handleSuperClass(CtClass clazz, StringBuilder data) {
        try {
            if (clazz.getSuperclass() != null) {
                CtClass objectClazz = clazz.getClassPool().get("java.lang.Object");
                if (!objectClazz.equals(clazz.getSuperclass())) {
                    data.append(checksum(clazz.getSuperclass()));
                }
            }
        } catch (NotFoundException e) {
            log.trace("Class not found while getting superclass for class checksum", e);
        }
    }

    private void handleInterfaces(CtClass clazz, StringBuilder data) {
        try {
            for (CtClass itfClazz : clazz.getInterfaces()) {
                data.append(checksum(itfClazz));
            }
        } catch (NotFoundException e) {
            log.trace("Class not found while getting interfaces for class checksum", e);
        }
    }

}
