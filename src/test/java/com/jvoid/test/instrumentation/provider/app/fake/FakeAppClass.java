package com.jvoid.test.instrumentation.provider.app.fake;

/*
 * Just to have a fake test class to pretend to be part of our
 * application under test.
 * This is used also to verify the AppClassHandler
 */
@FakeAppAnnotation
public class FakeAppClass extends AbstractAppFakeClass implements FakeAppInterface {

    final static int value;

    // The static initializer we want to track
    static {
        value = 0;
    }

    // Some constructors we want to track
    public FakeAppClass() {
    }

    public FakeAppClass(String str) {
    }

    public FakeAppClass(String str, Long val) {
    }

    // A method... uuuhhh...
    @FakeAppAnnotation
    public void hello() {
    }

}
