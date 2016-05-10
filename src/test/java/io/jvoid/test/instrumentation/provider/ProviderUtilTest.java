package io.jvoid.test.instrumentation.provider;

import static org.junit.Assert.assertEquals;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import io.jvoid.instrumentation.provider.ProviderUtil;
import io.jvoid.test.AbstractJVoidTest;
import javassist.CtClass;
import javassist.CtConstructor;
import javassist.CtMethod;
import javassist.NotFoundException;

/*
 * It would be really nice to test the ProviderUtil methods mocking
 * CtMethod and CtConstructor. Unfortunately, those are final classes
 * and even PowerMock has errors when trying to mock them. So for these
 * reasons we test the ProviderUtil in an alternative way.
 */
public class ProviderUtilTest extends AbstractJVoidTest {

    private CtClass ctClassTestClass;
    private ProviderUtil providerUtils;

    @Override
    @Before
    public void setUp() throws NotFoundException {
        ctClassTestClass = classPool.get(TestClass.class.getName());
        providerUtils = new ProviderUtil();
    }

    @Override
    @After
    public void tearDown() {
        ctClassTestClass.prune();
    }

    @Test
    public void testGetClassIdentifier() {
        assertEquals(ctClassTestClass.getName(), providerUtils.getClassIdentifier(ctClassTestClass));
    }

    @Test
    public void testGetConstructorIdentifier() throws NotFoundException {
        CtMethod ctMethodCiao = ctClassTestClass.getDeclaredMethod("ciao");
        assertEquals(ctMethodCiao.getLongName(), providerUtils.getMethodIdentifier(ctMethodCiao));
    }

    @Test
    public void testGetMethodIdentifier() throws NotFoundException {
        CtClass ctClassString = classPool.get(String.class.getName());
        CtConstructor cnstr = ctClassTestClass.getDeclaredConstructor(new CtClass[] { ctClassString });
        assertEquals(cnstr.getLongName(), providerUtils.getConstructorIdentifier(cnstr));
    }

    // Class to parse as a CtClass under ProviderUtil test
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
