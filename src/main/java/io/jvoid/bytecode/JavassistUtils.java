package io.jvoid.bytecode;

import io.jvoid.exceptions.JVoidIntrumentationException;
import javassist.CtBehavior;
import javassist.bytecode.BadBytecode;
import javassist.bytecode.CodeAttribute;
import javassist.bytecode.CodeIterator;
import javassist.bytecode.ConstPool;
import javassist.bytecode.InstructionPrinter;
import javassist.bytecode.MethodInfo;

/**
 * Some helper methods that complements Javassist functionalities.
 *
 */
public class JavassistUtils {

    private JavassistUtils() {
        super();
    }

    /**
     * This is basically the InstructionPrinter.getMethodBytecode but with a
     * CtBehaviour parameter instead of a CtMethod
     * 
     * @param behavior
     * @return
     */
    public static String getBehaviourBytecode(CtBehavior behavior) {
        MethodInfo info = behavior.getMethodInfo2();
        CodeAttribute code = info.getCodeAttribute();
        if (code == null) {
            return "";
        }

        ConstPool pool = info.getConstPool();
        StringBuilder sb = new StringBuilder(1024);

        CodeIterator iterator = code.iterator();
        while (iterator.hasNext()) {
            int pos;
            try {
                pos = iterator.next();
            } catch (BadBytecode e) {
                throw new JVoidIntrumentationException("BadBytecoode", e);
            }

            sb.append(pos + ": " + InstructionPrinter.instructionString(iterator, pos, pool) + "\n");
        }
        return sb.toString();
    }
}
