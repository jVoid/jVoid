package com.jvoid.test.instrumentation.provider.app;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.Collection;

import org.apache.commons.dbutils.handlers.BeanListHandler;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.google.inject.Inject;
import com.jvoid.configuration.JVoidConfiguration;
import com.jvoid.database.DbUtils;
import com.jvoid.instrumentation.provider.ProviderUtil;
import com.jvoid.instrumentation.provider.app.AppClassHandler;
import com.jvoid.instrumentation.provider.app.AppInstrumentationJClassHolder;
import com.jvoid.metadata.checksum.CodeChecksummer;
import com.jvoid.metadata.model.JClass;
import com.jvoid.metadata.model.JClassConstructor;
import com.jvoid.metadata.model.JClassStaticBlock;
import com.jvoid.metadata.repositories.ClassConstructorsRepository;
import com.jvoid.metadata.repositories.ClassStaticBlocksRepository;
import com.jvoid.metadata.repositories.ClassesRepository;
import com.jvoid.test.AbstractJVoidTest;
import com.jvoid.test.instrumentation.provider.app.fake.AbstractAppFakeClass;

import javassist.CannotCompileException;
import javassist.CtClass;

public class AppClassHandlerTest extends AbstractJVoidTest {

    private AppClassHandler appClassHandler;

    @Inject
    private JVoidConfiguration jVoidConfiguration;
    
    @Inject
    private ProviderUtil providerUtils;

    @Inject
    private ClassesRepository classesRepository;

    @Inject
    private ClassStaticBlocksRepository classStaticBlocksRepository;

    @Inject
    private ClassConstructorsRepository classConstructorsRepository;

    @Inject
    private AppInstrumentationJClassHolder jClassHolder;

    @Inject
    private CodeChecksummer codeChecksummer;

    private CtClass ctFakeAppClass;

    @Override
    @Before
    public void setUp() throws Exception {
        super.setUp();
        ctFakeAppClass = classPool.get("com.jvoid.test.instrumentation.provider.app.fake.FakeAppClass");

        appClassHandler = new AppClassHandler(jVoidConfiguration, codeChecksummer, providerUtils, classesRepository, classConstructorsRepository,
                classStaticBlocksRepository, jvoidExecutionContext, jClassHolder);
    }

    @Override
    @After
    public void tearDown() {
        ctFakeAppClass.prune();
    }

    @Test
    public void testHandleClass() throws CannotCompileException {
        setupCurrentExecution();
        appClassHandler.handleClass(ctFakeAppClass);

        // Check the class has been saved properly
        String classIdentifier = providerUtils.getClassIdentifier(ctFakeAppClass);
        JClass savedClass = classesRepository.findByIdenfifierAndExecutionId(classIdentifier, getCurrentExecutionId());
        assertNotNull(savedClass);
        assertEquals(AbstractAppFakeClass.class.getName(), savedClass.getSuperClassIdentifier());

        // Check the constructors have been saved correctly
        Collection<JClassConstructor> cnstrs = findCnstrByClassId(savedClass.getId());
        assertEquals(3, cnstrs.size());

        // Check the static initializer has been saved
        Collection<JClassStaticBlock> sblocks = findStaticBlockByClassId(savedClass.getId());
        assertEquals(1, sblocks.size());
    }

    private Collection<JClassConstructor> findCnstrByClassId(Long classId) {
        String sql = "SELECT cc.* FROM classes c " + "INNER JOIN class_constructors cc ON cc.classId = c.id " + "WHERE c.id = ?";
        return DbUtils.query(metadataDatabase, sql, new BeanListHandler<>(JClassConstructor.class), classId);
    }

    private Collection<JClassStaticBlock> findStaticBlockByClassId(Long classId) {
        String sql = "SELECT sb.* FROM classes c " + "INNER JOIN class_static_blocks sb ON sb.classId = c.id " + "WHERE c.id = ?";
        return DbUtils.query(metadataDatabase, sql, new BeanListHandler<>(JClassStaticBlock.class), classId);
    }

}
