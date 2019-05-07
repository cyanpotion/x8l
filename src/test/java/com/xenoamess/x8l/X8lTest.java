package com.xenoamess.x8l;

import java.io.*;

public class X8lTest {

    @org.junit.jupiter.api.Test
    public static void test() {
        System.out.println(Version.VERSION);
        Reader reader = new StringReader(
                "<<here goes a basic demo of x8l>\n" +
                        "<<first,a comment is like this>  \n" +
                        "< <or this this>  \n" +
                        "<<<or even this this.< in a comment need not be transcoded.>  \n" +
                        "<<use %% to transcode.for example, %>, and this is still in it.>  \n" +
                        "<<the content between the first < and the second < is treated as \"attributes\".>  \n" +
                        "<<the order of attributes is important, and node with different order of same attributes are" +
                        " different.>\n" +
                        "<<attribute can have = in it.if so, it will be departed into key and value.>\n" +
                        "<<key is the part left than the first =,and value is the rest content.>\n" +
                        "<a=b>>\n" +
                        "<<for example, in the upper node, \"a\" is a key and \"b\" is a value>\n" +
                        "<<remember, the first =.>\n" +
                        "<a=b=c>>\n" +
                        "<<that means this node's key is \"a\" and value is \"b=c\">\n" +
                        "<<if there is no \"=\" in a attribute then the whole string is the key,and \"\" is the " +
                        "value>\n" +
                        "<<spaces between attributes are treated as nothing, so does '\\r' '\\n' '\\t'>\n" +
                        "<<which means you can write it like this>\n" +
                        "<views\n" +
                        "    windowWidth=1280\n" +
                        "    windowHeight=1024\n" +
                        "    scale=2.0\n" +
                        "    fullScreen=0\n" +
                        ">>\n" +
                        "<<and it equals to >\n" +
                        "<views windowWidth=1280 windowHeight=1024 scale=2.0 fullScreen=0>>\n" +
                        "<<the content between the second < and the %> is treated as \"children\".>\n" +
                        "<<children must be nodes, and children's parent is the node which it in>\n" +
                        "<<there are 3 kinds of nodes, \"content node\", \"text node\", and \"comment node\">\n" +
                        "<<only \"content node\" have attributes and contents>\n" +
                        "so what is a text node? that is a good question.so this is text node.\n" +
                        "\n" +
                        "<be careful!> a space in text node is meaningful and cannot be deleted!>\n" +
                        "that means:\n" +
                        "<<>\n" +
                        "<< >\n" +
                        "<<\n" +
                        ">\n" +
                        "is 3 different nodes.\n" +
                        "if you want to delete all text node with \"empty char\" content,you can use trim.\n" +
                        "that is the main content.\n" +
                        "you can now run the demo and see the output of the tree of this readme.\n" +
                        "that should be helpful/\n" +
                        "thanks.\n" +
                        "            --XenoAmess\n" +
                        "            \n" +
                        "and here goes some reallife x8l files:\n" +
                        "\n" +
                        "<commonSettings\n" +
                        "    titleText=GamepadMassage\n" +
                        "    gameWindowClassName=com.xenoamess.gamepad_massage.FalseGameWindow\n" +
                        "    logoClassName=com.xenoamess.gamepad_massage.FalseWorld\n" +
                        "    titleClassName=com.xenoamess.cyan_potion.gameWindowComponents.Title\n" +
                        "    worldClassName=com.xenoamess.gamepad_massage.FalseWorld\n" +
                        ">>\n" +
                        "<views\n" +
                        "    windowWidth=1280\n" +
                        "    windowHeight=1024\n" +
                        "    scale=2.0\n" +
                        "    fullScreen=0\n" +
                        ">>\n" +
                        "<specialSettings\n" +
                        "    autoShowGameWindowAfterInit=0\n" +
                        "    noConsoleThread=1\n" +
                        ">>\n" +
                        "<debug>>\n" +
                        "<keymap using>\n" +
                        "    <GLFW_KEY_W>XENOAMESS_KEY_UP>\n" +
                        "    <GLFW_KEY_A>XENOAMESS_KEY_LEFT>\n" +
                        "    <GLFW_KEY_S>XENOAMESS_KEY_DOWN>\n" +
                        "    <GLFW_KEY_D>XENOAMESS_KEY_RIGHT>\n" +
                        "    <GLFW_KEY_UP>XENOAMESS_KEY_UP>\n" +
                        "    <GLFW_KEY_LEFT>XENOAMESS_KEY_LEFT>\n" +
                        "    <GLFW_KEY_DOWN>XENOAMESS_KEY_DOWN>\n" +
                        "    <GLFW_KEY_RIGHT>XENOAMESS_KEY_RIGHT>\n" +
                        "    <GLFW_KEY_ESCAPE>XENOAMESS_KEY_ESCAPE>\n" +
                        "    <GLFW_KEY_ENTER>XENOAMESS_KEY_ENTER>\n" +
                        "    <GLFW_KEY_SPACE>XENOAMESS_KEY_SPACE>\n" +
                        "    <GLFW_KEY_LEFT_SHIFT>XENOAMESS_KEY_LEFT_SHIFT>\n" +
                        "    <GLFW_KEY_RIGHT_SHIFT>XENOAMESS_KEY_RIGHT_SHIFT>\n" +
                        "    <GLFW_MOUSE_BUTTON_LEFT>XENOAMESS_MOUSE_BUTTON_LEFT>\n" +
                        "    <GLFW_MOUSE_BUTTON_RIGHT>XENOAMESS_MOUSE_BUTTON_RIGHT>\n" +
                        "    <GLFW_MOUSE_BUTTON_MIDDLE>XENOAMESS_MOUSE_BUTTON_MIDDLE>\n" +
                        ">\n" +
                        "<backup worldClassName=com.xenoamess.rpg_module.world.World>>\n" +
                        "<backup worldClassName=com.xenoamess.modern_alchemy.scene.ProgramScene>>\n" +
                        "<backup worldClassName=com.xenoamess.gamepad_massage.FalseWorld>>\n" +
                        "<backup gameWindowClassName=com.xenoamess.cyan_potion.GameWindow>>\n" +
                        "\n" +
                        "\n" +
                        "\n" +
                        "<merge version=1.0>\n" +
                        "    <0001>\n" +
                        "        <en>Thanks!>\n" +
                        "        <<just comment>\n" +
                        "<ch>谢谢!>\n" +
                        "    >\n" +
                        "    <<just comment>\n" +
                        "    <0002>\n" +
                        "        <en>Hello?>\n" +
                        "<<just comment>\n" +
                        "<ch>您好？>\n" +
                        "    >\n" +
                        ">\n" +
                        "\n" +
                        "<merge version=1.0>\n" +
                        "    <0001>\n" +
                        "        <en>Thanks!>\n" +
                        "        <<just comment>\n" +
                        "<ch>谢谢!>\n" +
                        "    >\n" +
                        ">"
        );

        X8lTree tree = new X8lTree(reader);
        try {
            tree.parse();
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("BuildFinished");
        tree.show();

        new File("out").mkdirs();

        try {
            tree.write(new FileWriter("out/output.x8l"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        tree.trim();
        try {
            tree.write(new FileWriter("out/outputTrim.x8l"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        tree.format();
        try {
            tree.write(new FileWriter("out/outputFormat.x8l"));
        } catch (IOException e) {
            e.printStackTrace();
        }


    }
}
