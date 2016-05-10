package com.jvoid.test.bytecode;

import static org.junit.Assert.assertEquals;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.jvoid.bytecode.JavassistUtils;
import com.jvoid.test.AbstractJVoidTest;

import javassist.CtClass;
import javassist.CtMethod;
import javassist.NotFoundException;
import javassist.bytecode.InstructionPrinter;

public class JavassistUtilsTest extends AbstractJVoidTest {

    private CtClass ctClassTestClass;

    @Override
    @Before
    public void setUp() throws Exception {
        super.setUp();
        ctClassTestClass = classPool.get(TestClass.class.getName());
    }

    @Override
    @After
    public void tearDown() {
        ctClassTestClass.prune();
    }

    @Test
    public void testGetBehaviourBytecodeCtMethod() throws NotFoundException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintStream ps = new PrintStream(baos);
        InstructionPrinter instrPrinter = new InstructionPrinter(ps);
        CtMethod ctMethodCiao = ctClassTestClass.getDeclaredMethod("ciao");
        instrPrinter.print(ctMethodCiao);
        String instrPrinterStr = baos.toString();
        assertEquals(instrPrinterStr, JavassistUtils.getBehaviourBytecode(ctMethodCiao));
    }

    @SuppressWarnings("unused")
    private static final class TestClass {
        public TestClass(String param) {
            super();
        }

        public String ciao() {
            return "ciao";
        }
    }
}
