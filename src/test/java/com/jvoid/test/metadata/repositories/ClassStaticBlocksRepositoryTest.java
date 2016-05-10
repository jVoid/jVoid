package com.jvoid.test.metadata.repositories;

import javax.inject.Inject;

import com.jvoid.metadata.model.JClass;
import com.jvoid.metadata.model.JClassStaticBlock;
import com.jvoid.metadata.model.JTest;
import com.jvoid.metadata.repositories.ClassStaticBlocksRepository;
import com.jvoid.metadata.repositories.ClassesRepository;
import com.jvoid.metadata.repositories.TestsRepository;

public class ClassStaticBlocksRepositoryTest
        extends AbstractTestRelatedEntityRepositoryTest<JClassStaticBlock, Long, ClassStaticBlocksRepository> {

    @Inject
    private ClassesRepository classesRepository;

    @Inject
    private TestsRepository testsRepository;

    @Inject
    private ClassStaticBlocksRepository classStaticBlocksRepository;

    @Override
    protected void linkToTest(JClassStaticBlock entity, JTest jtest) {
        JClass jclass = classesRepository.findById(entity.getClassId());
        testsRepository.linkClass(jtest, jclass.getId());
    }

    @Override
    protected JClassStaticBlock prepareForCreation() {
        JClass jclass = classesRepository.add(createJClass());
        JClassStaticBlock sb = new JClassStaticBlock();
        sb.setClassId(jclass.getId());
        sb.setChecksum(randomStr());
        sb.setIdentifier(randomStr());
        return sb;
    }

    @Override
    protected ClassStaticBlocksRepository getRepo() {
        return classStaticBlocksRepository;
    }

}
