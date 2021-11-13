package com.company;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class BankAccount {
    String name;
    List<Transaction> transactions;

    BankAccount() {
        name = new NameGenerator().createRandomName();

        // generate transactions
        int transactionsSize = ThreadLocalRandom.current().nextInt(Transaction.MAX_TRANSACTIONS_SIZE);
        transactions = new ArrayList<>();
        for (int j = 0; j < transactionsSize; j++) {
            transactions.add(new Transaction());
        }
    }

    static List<BankAccount> generateBankAccounts(int size) {
        List<BankAccount> bankAccounts = new ArrayList<>();

        for (int i = 0; i < size; i++) {
            BankAccount ba = new BankAccount();
            bankAccounts.add(ba);
        }

        return bankAccounts;
    }

}
