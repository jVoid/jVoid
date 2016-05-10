package com.jvoid.metadata.checksum;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import javassist.CtBehavior;
import javassist.CtClass;
import javassist.CtMethod;

/**
 *
 */
@Singleton
public class CodeChecksummer {

    private CtClassChecksummer ctClassChecksummer;
    private CtBehaviorChecksummer ctBehaviorChecksummer;
    private CtMethodChecksummer ctMethodChecksummer;

    @Inject
    public CodeChecksummer(CtClassChecksummer ctClassChecksummer, CtBehaviorChecksummer ctBehaviorChecksummer, CtMethodChecksummer ctMethodChecksummer) {
        this.ctClassChecksummer = ctClassChecksummer;
        this.ctBehaviorChecksummer = ctBehaviorChecksummer;
        this.ctMethodChecksummer = ctMethodChecksummer;
    }

    public String checksum(CtClass input) {
        return ctClassChecksummer.checksum(input);
    }

    public String checksum(CtMethod input) {
        return ctMethodChecksummer.checksum(input);
    }

    public String checksum(CtBehavior input) {
        return ctBehaviorChecksummer.checksum(input);
    }
}
