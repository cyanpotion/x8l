/*
 * MIT License
 *
 * Copyright (c) 2019 XenoAmess
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.xenoamess.x8l;

import com.xenoamess.x8l.dealers.JsonDealer;
import com.xenoamess.x8l.dealers.X8lDealer;
import com.xenoamess.x8l.dealers.XmlDealer;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

public class X8lTest {

    @Test
    public void prepareTest() throws IOException {
        prepare();
    }

    public X8lTree prepare() throws IOException {
        System.out.println(PackageVersion.VERSION);
        //noinspection ResultOfMethodCallIgnored
        new File("out").mkdir();
        String inputString = "<<here goes a basic demo of x8l>\n" +
                "<<first,a comment is like this>  \n" +
                "< <or this this>  \n" +
                "<<<or even this this.< in a comment need not be transcoded.>  \n" +
                "<<use %% to transcode.for example, %>, and this is still in it.>  \n" +
                "<<the content between the first < and the second < is treated as \"attributes\".>  \n" +
                "<<the order of attributes is important, and node with different order of same attributes" +
                " are" +
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
                "    value=valueA% valueB" +
                ">>\n" +
                "<<and it equals to >\n" +
                "<views windowWidth=1280 windowHeight=1024 scale=2.0 fullScreen=0>>\n" +
                "<<the content between the second < and the %> is treated as \"children\".>\n" +
                "<<children must be nodes, and children's parent is the node which it in>\n" +
                "<<there are 3 kinds of nodes, \"content node\", \"text node\", and \"comment node\">\n" +
                "<<only \"content node\" have attributes and contents>\n" +
                "so what is a text node? that is a good question.so this is text node.\n" +
                "\n" +
                "<be careful> a space in text node is meaningful and cannot be deleted!>\n" +
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
                "and here goes some real life x8l files:\n" +
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
                "    <a0001>\n" +
                "        <en>Thanks!>\n" +
                "        <<just comment>\n" +
                "<ch>testA>\n" +
                "    >\n" +
                "    <<just comment>\n" +
                "    <a0002>\n" +
                "        <en>Hello?>\n" +
                "<<just comment>\n" +
                "<ch>testB>\n" +
                "    >\n" +
                ">\n" +
                "\n" +
                "<merge version=1.0>\n" +
                "    <a0001>\n" +
                "        <en>Thanks!>\n" +
                "        <<just comment>\n" +
                "<ch>testC>\n" +
                "    >\n" +
                ">";

        try (Writer writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("out/input.x8l")))) {
            writer.write(inputString);
        }

        return X8lTree.load(inputString);
    }

    @Test
    public void test() throws Exception {
        prepare();

        X8lTree tree = prepare();
        System.out.println("BuildFinished");
        tree.show();

        //noinspection ResultOfMethodCallIgnored
        new File("out").mkdirs();

        X8lTree.save(new File("./out/output.x8l"), tree);


        tree.trim();

        try (Writer writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("out/outputTrim.x8l")))) {
            tree.write(writer);
        }

        tree.format();

        try (Writer writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("out/outputFormat.x8l")))) {
            tree.write(writer);
        }

        try (Writer writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("out/outputFormat.xml")))) {
            tree.write(writer, XmlDealer.INSTANCE);
        }

        XmlTester.test("<note a=\"1\" b=\"2\">\n" +
                "    <to>Tove</to>\n" +
                "    <from>Janifer</from>\n" +
                "    <!-- aaa -->\n" +
                "    <heading>Reminder</heading>\n" +
                "    <body>Don't forget me this weekend!</body>\n" +
                "</note>");

        X8lTree tree4 = X8lTree.load(new File("./out/input.x8l"));
        X8lTree tree41 = X8lTree.load(new File("./out/input.x8l"));
        assertEquals(tree4, tree41);
        assertNotEquals(null, tree4);
        assertEquals(tree4.getLanguageDealer(), X8lDealer.INSTANCE);
        System.out.println(tree4.toString());
        tree4.setLanguageDealer(JsonDealer.INSTANCE);
        System.out.println(tree4.toString());
        tree4.setLanguageDealer(XmlDealer.INSTANCE);
        System.out.println(tree4.toString());
        System.out.println(tree4.hashCode());
        tree4.append(tree4);
        System.out.println(tree4.toString());
        tree4.close();
        assertEquals(new X8lTree(tree4), tree4);
    }

    @Test
    public void testSerializable() throws IOException, ClassNotFoundException {
        X8lTree tree = prepare();

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ObjectOutputStream obs = new ObjectOutputStream(out);
        obs.close();

        obs.writeObject(tree);
        ByteArrayInputStream ios = new ByteArrayInputStream(out.toByteArray());
        ObjectInputStream ois = new ObjectInputStream(ios);
        X8lTree cloneTree = (X8lTree) ois.readObject();
        String diff = StringUtils.difference(tree.toString(), cloneTree.toString());
        assertEquals("", diff);

        assertEquals(tree.toString(), cloneTree.toString());
        assertEquals(tree, cloneTree);

        assertEquals(tree.getRoot().toString(), cloneTree.getRoot().toString());
        assertEquals(tree.getRoot(), cloneTree.getRoot());

        ois.close();
    }

    @Test
    public void untranscodeTest() {
        assertEquals(X8lTree.untranscode("%%%<%>% "), "%<> ");
    }

    @Test
    public void applyToAllNodesTest() throws IOException {
        X8lTree tree = prepare();
        tree.applyToAllNodes(AbstractTreeNode::toString);
        for (ContentNode au : tree.getRoot().getContentNodesFromChildren()) {
            au.applyToAllNodes(AbstractTreeNode::toString);
        }
    }
}
