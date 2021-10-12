package com.company;

public class Main {

    public static void main(String[] args) {
        if(args.length < 3)
            throw new IllegalArgumentException();
        int n_clienti = Integer.parseInt(args[0]); //numero di clienti totale
        int n_sportelli = Integer.parseInt(args[1]); //numero di sportelli attivi
        int cap_code_sport = Integer.parseInt(args[2]); //capacità della seconda coda, quella per gli sportelli

        UfficioPostale up = new UfficioPostale(n_sportelli, cap_code_sport, n_clienti);
        up.officeActivity();
    }
}
