package com.company;

import java.util.Random;

public enum Causal {
    WIRE_TRANSFER("wire-transfer"),
    ACCREDITATION("accreditation"),
    POSTAL("postal"),
    F24("f24"),
    BANCOMAT("bancomat")
    ;

    private final String description;

    Causal(final String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return description;
    }

    public static Causal getRandomCausal() {
        Random random = new Random();
        return values()[random.nextInt(values().length)];
    }
}
