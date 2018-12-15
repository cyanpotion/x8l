package com.xenoamess.x8l;

import java.io.*;

public class X8lDemo {

    public static void main(String args[]) {
        FileReader reader = null;
        try {
            reader = new FileReader(X8lDemo.class.getResource("/README.md").getFile());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        X8lTree tree = new X8lTree(reader);
        tree.debug = true;
        tree.parse();
        System.out.println("BuildFinished");
        tree.show();

        new File("out").mkdirs();

        try {
            tree.output(new FileWriter("out/output.x8l"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        tree.trim();
        try {
            tree.output(new FileWriter("out/outputTrim.x8l"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
