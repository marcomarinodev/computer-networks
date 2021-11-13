package com.company;

public class Transaction {
    static int MAX_TRANSACTIONS_SIZE = 20;
    String dateString;
    Causal causal;

    Transaction() {
        dateString = createRandomDate();
        causal = Causal.getRandomCausal();
    }

    static int createRandomIntBetween(int start, int end) {
        return start + (int) Math.round(Math.random() * (end - start));
    }

    static String createRandomDate() {
        int day = createRandomIntBetween(1, 28);
        int month = createRandomIntBetween(1, 12);
        int year = createRandomIntBetween(1970, 2021);
        int hour = createRandomIntBetween(1, 24);
        int minute = createRandomIntBetween(1, 60);
        int seconds = createRandomIntBetween(1, 60);

        return hour + ":" + minute + ":" + seconds + " " + day + "/" + month + "/" + year;
    }
}
