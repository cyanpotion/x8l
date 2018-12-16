import com.xenoamess.x8l.X8lTree;

import java.io.*;

public class X8lDemo {

    public static void main(String args[]) {
        FileReader reader = null;
        try {
            reader = new FileReader(new File("README.md"));
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
