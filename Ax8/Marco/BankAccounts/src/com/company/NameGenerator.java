package com.company;

import java.util.concurrent.ThreadLocalRandom;

public class NameGenerator {
    
    private String firstNames[];
    private String secondNames[];
    private int size;

    NameGenerator() {
        firstNames = new String[] {
            "Marco",
            "Leonard",
            "Zach",
            "Francesco",
            "Paul"
        };

        secondNames = new String[] {
            "LaVine",
            "Young",
            "Antetokounmpo",
            "George",
            "Kostas"
        };

        size = firstNames.length;
    }

    public String createRandomName() {
        int randomIndexFirstName = ThreadLocalRandom.current().nextInt();
        int randomIndexSecondName = ThreadLocalRandom.current().nextInt();

        return firstNames[Math.abs(randomIndexFirstName % size)] + " " + secondNames[Math.abs(randomIndexSecondName % size)];
    }

}
