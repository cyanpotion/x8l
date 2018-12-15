package com.xenoamess.x8l;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class X8lTester {

    public static void main(String args[]) {
        FileReader reader = null;
        try {
            reader = new FileReader(X8lTester.class.getResource("/README.md").getFile());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        X8lTree tree = new X8lTree(reader);
        tree.debug = true;
        tree.parse();
        System.out.println("BuildFinished");
        tree.show();
    }
}
