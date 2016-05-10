package io.jvoid.instrumentation;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;
import java.util.Set;

import com.google.inject.Inject;

import io.jvoid.exceptions.JVoidIntrumentationException;
import io.jvoid.instrumentation.provider.ProviderCatalog;
import io.jvoid.instrumentation.provider.api.ClassHandler;
import io.jvoid.instrumentation.provider.api.InstrumentationProvider;
import io.jvoid.instrumentation.provider.api.MethodInstrumenter;
import javassist.ByteArrayClassPath;
import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.NotFoundException;
import lombok.extern.slf4j.Slf4j;

/**
 * The {@code JVoidClassFileTransformer} takes care of instrumenting the
 * classes. It instruments differently the test runner classes from the normal
 * classes and the filtered out classes.
 *
 */
@Slf4j
public class JVoidClassFileTransformer implements ClassFileTransformer {

    private static final ClassPool classPool;

    private final ProviderCatalog providerCatalog;

    static {
        classPool = ClassPool.getDefault();
    }

    /**
     *
     * @param providerCatalog
     */
    @Inject
    public JVoidClassFileTransformer(ProviderCatalog providerCatalog) {
        this.providerCatalog = providerCatalog;
    }

    /**
     *
     */
    @Override
    public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined,
            ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException {
        if (className == null) {
            return null;
        }
        try {
            String sanitizedClassName = className.replace('/', '.');

            classPool.insertClassPath(new ByteArrayClassPath(sanitizedClassName, classfileBuffer));
            CtClass ctClass = getCtClass(sanitizedClassName);

            Set<InstrumentationProvider> providers = providerCatalog.getProvidersForClass(ctClass);

            // This is important to avoid to modify classes part of the java.lang.* or
            // not related to the application under test. There might be otherwise some
            // error related to Javassist transformation. See:
            // http://stackoverflow.com/questions/33038650/javassist-side-effects-of-classpool-makeclass?answertab=active#tab-top
            if (providers.isEmpty()) {
                return null;
            }

            for (InstrumentationProvider provider : providers) {
                tranformWithProvider(ctClass, provider);
            }

            byte[] bytecode = ctClass.toBytecode();

            ctClass.defrost();
            ctClass.prune();
            ctClass.detach();

            return bytecode;
        } catch (Exception e) {
            log.error("Failed to transform class '" + className + "': " + e.getMessage(), e);
            throw new JVoidIntrumentationException("Failed to instrument class: " + className, e);
        }
    }

    /**
     *
     * @param ctClass
     * @param provider
     * @throws CannotCompileException
     */
    private void tranformWithProvider(CtClass ctClass, InstrumentationProvider provider) throws CannotCompileException {
        for (ClassHandler classHandler : provider.getClassHandlers(ctClass)) {
            classHandler.handleClass(ctClass);
        }
        for (CtMethod ctMethod : ctClass.getDeclaredMethods()) {
            for (MethodInstrumenter instrumenter : provider.getMethodInstrumenters(ctMethod)) {
                if (instrumenter.shouldInstrument(ctMethod)) {
                    instrumenter.instrument(ctMethod);
                }
            }
        }
    }

    /**
     *
     * @param sanitizedClassName
     * @return
     */
    private CtClass getCtClass(String sanitizedClassName) {
        try {
            return classPool.get(sanitizedClassName);
        } catch (NotFoundException e) {
            throw new JVoidIntrumentationException("Class not found:" + sanitizedClassName, e);
        } catch (Exception e) {
            throw new JVoidIntrumentationException(e.getLocalizedMessage(), e);
        }
    }

}
