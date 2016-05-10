package io.jvoid.instrumentation;

import com.google.inject.Singleton;

/**
 * Keeps the {@code JVoidInstrumentationHelper}. Useful for testing and replacing
 * the instance of the instrumentation helper at runtime.
 *
 */
@Singleton
public class JVoidInstrumentationHelperHolder {

    private JVoidInstrumentationHelper helper;

    private static final JVoidInstrumentationHelperHolder instance = new JVoidInstrumentationHelperHolder();

    public static JVoidInstrumentationHelperHolder getInstance() {
        return instance;
    }

    public JVoidInstrumentationHelper get() {
        return helper;
    }

    public void set(JVoidInstrumentationHelper helper) {
        this.helper = helper;
    }

    public static String helperGetterRef() {
        return JVoidInstrumentationHelperHolder.class.getName() + ".getInstance().get()";
    }
}
