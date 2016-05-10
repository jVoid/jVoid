package io.jvoid.test.metadata.repositories;

import javax.inject.Inject;

import io.jvoid.metadata.model.JExecution;
import io.jvoid.metadata.repositories.ExecutionsRepository;

public class ExecutionsRepositoryTest extends AbstractBaseRepositoryTest<JExecution, Long, ExecutionsRepository> {

    @Inject
    private ExecutionsRepository executionsRepository;

    @Override
    protected JExecution prepareForCreation() {
        JExecution jexec = new JExecution();
        jexec.setTimestamp(System.nanoTime());
        return jexec;
    }

    @Override
    protected ExecutionsRepository getRepo() {
        return executionsRepository;
    }

}
