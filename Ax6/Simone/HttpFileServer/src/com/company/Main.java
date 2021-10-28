package com.company;

import java.io.IOException;

public class Main {

    public static void main(String[] args) throws IOException {
        HttpFileServer hfs = new HttpFileServer(); // attivo listen socket

        hfs.activity();
    }
}
