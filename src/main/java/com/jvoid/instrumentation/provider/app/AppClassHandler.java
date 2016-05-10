package com.jvoid.instrumentation.provider.app;

import com.google.inject.Inject;
import com.jvoid.configuration.JVoidConfiguration;
import com.jvoid.execution.JVoidExecutionContext;
import com.jvoid.instrumentation.provider.ProviderUtil;
import com.jvoid.instrumentation.provider.api.ClassHandler;
import com.jvoid.metadata.checksum.CodeChecksummer;
import com.jvoid.metadata.model.JClass;
import com.jvoid.metadata.model.JClassConstructor;
import com.jvoid.metadata.model.JClassStaticBlock;
import com.jvoid.metadata.repositories.ClassConstructorsRepository;
import com.jvoid.metadata.repositories.ClassStaticBlocksRepository;
import com.jvoid.metadata.repositories.ClassesRepository;

import javassist.CannotCompileException;
import javassist.CtClass;
import javassist.CtConstructor;
import javassist.NotFoundException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;


/**
 * Handle all the tracked application classes that are semantically under test
 * or will affect test executions. In particular, it will keep track of all matching
 * application classes and store the metadata in the persistence storage used by
 * JVoid given the current configuration.
 *
 */
@Slf4j
@AllArgsConstructor(onConstructor=@__({@Inject}))
public class AppClassHandler implements ClassHandler {

    private JVoidConfiguration jVoidConfiguration;
    
    private CodeChecksummer codeChecksummer;

    private ProviderUtil providerUtils;

    private ClassesRepository classesRepository;

    private ClassConstructorsRepository classConstructorsRepository;

    private ClassStaticBlocksRepository classStaticBlocksRepository;

    private JVoidExecutionContext jvoidExecutionContext;

    private AppInstrumentationJClassHolder jClassHolder;


    /**
     *
     */
    @Override
    public void handleClass(CtClass clazz) throws CannotCompileException {
        String identifier = providerUtils.getClassIdentifier(clazz);
        String checksum = codeChecksummer.checksum(clazz);

        if (jVoidConfiguration.heuristicExcludeCglib() && AppHeuristicHelper.isCGLIBProxy(clazz)) {
            return;
        }

        JClass jclass = new JClass();
        jclass.setIdentifier(identifier);
        jclass.setChecksum(checksum);
        jclass.setExecutionId(jvoidExecutionContext.getCurrentExecutionId());

        try {
            if (clazz.getSuperclass() != null) {
                CtClass superClass = clazz.getSuperclass();
                String superClassIdentifier = providerUtils.getClassIdentifier(superClass);
                jclass.setSuperClassIdentifier(superClassIdentifier);
            }
        } catch (NotFoundException e) {
            log.trace("Class Not found while getting superclass for app class handling", e);
        }

        jclass = classesRepository.add(jclass);

        // Constructors
        for (CtConstructor constructor : clazz.getDeclaredConstructors()) {
            String constructorIdentifier = providerUtils.getConstructorIdentifier(constructor);
            String constructorChecksum = codeChecksummer.checksum(constructor);

            JClassConstructor jconstructor = new JClassConstructor();
            jconstructor.setClassId(jclass.getId());
            jconstructor.setIdentifier(constructorIdentifier);
            jconstructor.setChecksum(constructorChecksum);

            classConstructorsRepository.add(jconstructor);
        }

        // Static Blocks
        if (clazz.getClassInitializer() != null) {
            CtConstructor staticInitializer = clazz.getClassInitializer();

            String staticInitializerIdentifier = providerUtils.getConstructorIdentifier(staticInitializer);
            String staticInitializerChecksum = codeChecksummer.checksum(staticInitializer);

            JClassStaticBlock jClassStaticBlock = new JClassStaticBlock();
            jClassStaticBlock.setClassId(jclass.getId());
            jClassStaticBlock.setIdentifier(staticInitializerIdentifier);
            jClassStaticBlock.setChecksum(staticInitializerChecksum);

            classStaticBlocksRepository.add(jClassStaticBlock);
        }

        jClassHolder.setJClassUnderInstrumentation(jclass);
    }

}
