package io.jvoid.test.metadata.repositories;

import javax.inject.Inject;

import io.jvoid.metadata.model.JClass;
import io.jvoid.metadata.model.JClassStaticBlock;
import io.jvoid.metadata.model.JTest;
import io.jvoid.metadata.repositories.ClassStaticBlocksRepository;
import io.jvoid.metadata.repositories.ClassesRepository;
import io.jvoid.metadata.repositories.TestsRepository;

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
