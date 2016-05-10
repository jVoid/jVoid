package com.jvoid.test.metadata.repositories;

import javax.inject.Inject;

import com.jvoid.metadata.model.JClass;
import com.jvoid.metadata.model.JClassConstructor;
import com.jvoid.metadata.model.JTest;
import com.jvoid.metadata.repositories.ClassConstructorsRepository;
import com.jvoid.metadata.repositories.ClassesRepository;
import com.jvoid.metadata.repositories.TestsRepository;

public class ClassConstructorsRepositoryTest
        extends AbstractTestRelatedEntityRepositoryTest<JClassConstructor, Long, ClassConstructorsRepository> {

    @Inject
    private ClassesRepository classesRepository;

    @Inject
    private TestsRepository testsRepository;

    @Inject
    private ClassConstructorsRepository classConstructorsRepository;

    @Override
    protected void linkToTest(JClassConstructor clCnstr, JTest jtest) {
        JClass jclass = classesRepository.findById(clCnstr.getClassId());
        testsRepository.linkClass(jtest, jclass.getId());
    }

    @Override
    protected JClassConstructor prepareForCreation() {
        JClass jclass = classesRepository.add(createJClass());
        JClassConstructor clCnstr = new JClassConstructor();
        clCnstr.setClassId(jclass.getId());
        clCnstr.setChecksum(randomStr());
        clCnstr.setIdentifier(randomStr());
        return clCnstr;
    }

    @Override
    protected ClassConstructorsRepository getRepo() {
        return classConstructorsRepository;
    }

}
