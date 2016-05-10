package com.jvoid.test;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.Instrumentation;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import com.jvoid.JVoid;
import com.jvoid.instrumentation.JVoidClassFileTransformer;

public class JVoidAgentPremainTest extends AbstractJVoidTest {

    @Override
    @Before
    public void setUp() {
        // Make sure there is no context initialized
    }

    @Test
    public void premainTest() {
        // Prepare to verify the instrumentation added
        Instrumentation mockIntrumentation = mock(Instrumentation.class);

        JVoid.premain(DEFAULT_TEST_CONFIGURATION_FILE, mockIntrumentation);

        // The configuration is loaded
        // assertTrue(!JVoidContext.getConfiguration().dbLocation().isEmpty());

        // JVoidExecutionContext jvoidExecutionContext;
        // // The execution is setup
        // JExecution execution = jvoidExecutionContext.getCurrentExecution();
        // assertNotNull(execution);
        // assertEquals(JVoidContext.getCurrentExecution(), execution);

        // The transformer is added
        ArgumentCaptor<ClassFileTransformer> argument = ArgumentCaptor.forClass(ClassFileTransformer.class);
        verify(mockIntrumentation).addTransformer(argument.capture());
        assertTrue(argument.getValue() instanceof JVoidClassFileTransformer);
    }

}
