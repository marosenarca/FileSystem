/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Main;

import static com.sun.glass.ui.Cursor.setVisible;
import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Scanner;
import java.util.Stack;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JToolBar;
import javax.swing.KeyStroke;

/**
 *
 * @author MaRose
 */
public class FileSystem implements Serializable {

    private static final Logger LOGGER = Logger.getLogger(Tree.class.getName());
    private static final RegexTester reTester = new RegexTester();

    private Tree tree;

    public FileSystem() {
        init();
    }

    public void init() {
        this.tree = new Tree();
//        loadTree();
    }

    public void mkdir(String dir) {
        if (dir.indexOf("/") < 0) {
            if (!tree.searchDirectory(this.tree.getCurrentNode(), dir, 1)) {
                Directory newNode = new Directory(dir, this.tree.getCurrentNode());
                this.tree.insert(newNode, this.tree.getCurrentNode());
//                LOGGER.log(Level.INFO, "Subdirectory {0} successfully added in {1}.", new Object[]{dir, this.getCurrentDir()});
                System.out.println(MessageFormat.format("Subdirectory {0} successfully added in {1}.", new Object[]{dir, this.getCurrentDir()}));
            } else {
//                LOGGER.log(Level.INFO, "A subdirectory {0} already exists in {1}.", new Object[]{dir, this.getCurrentDir()});
                System.out.println(MessageFormat.format("A subdirectory {0} already exists in {1}.", new Object[]{dir, this.getCurrentDir()}));
            }
        } else {
            String[] path;
            if (dir.indexOf("/") == 0) {
                path = dir.substring(1).split("/");
            } else {
                path = dir.split("/");
            }

            int n = path.length;

            Directory dr;
            switch (path[0]) {
                case "root":
                    dr = this.tree.getRoot();
                    break;
                case "..":
                    dr = this.tree.getCurrentNode()._getParent();
                    break;
                default:
                    dr = this.tree.getCurrentNode();
                    break;
            }

            int start = (path[0].equalsIgnoreCase("root") || path[0].equalsIgnoreCase("..")) ? 1 : 0;
            for (int i = start; i < n - 1; i++) {

                if (path[i].equalsIgnoreCase("..")) {
                    dr = dr._getParent();
                } else if (this.tree.searchDirectory(dr, path[i], 1)) {
                    dr = this.tree.getDirectory(dr, path[i]);
                } else {
                    Directory newDr = new Directory(path[i], dr);
                    dr._addSubdirectory(newDr);
                    dr = newDr;
                }
            }
            if (!this.tree.searchDirectory(dr, path[n - 1], 1)) {
                Directory newDr = new Directory(path[n - 1], dr);
                dr._addSubdirectory(newDr);
//                LOGGER.log(Level.INFO, "Directory {0} successfully added in {1}.", new Object[]{dir, this.getCurrentDir()});
                System.out.println(MessageFormat.format("Directory {0} successfully added in {1}.", new Object[]{dir, this.getCurrentDir()}));
            } else {
//                LOGGER.log(Level.INFO, "A subdirectory {0} already exists in {1}.", new Object[]{dir, dr._getPath()});
                System.out.println(MessageFormat.format("A subdirectory {0} already exists in {1}.", new Object[]{dir, dr._getPath()}));
            }
        }
    }

    public void rmdir(String dir) {
        if (dir.indexOf("/") < 0) {
            if (!tree.searchDirectory(this.tree.getCurrentNode(), dir, 1)) {
//                LOGGER.log(Level.INFO, "A subdirectory {0} does not exist in {1}.", new Object[]{dir, this.getCurrentDir()});
                System.out.println(MessageFormat.format("A subdirectory {0} does not exist in {1}.", new Object[]{dir, this.getCurrentDir()}));

            } else {
                this.tree.remove(this.tree.getCurrentNode(), dir, 1);
//                LOGGER.log(Level.INFO, "Subdirectory {0} successfully deleted from {1}.", new Object[]{dir, this.getCurrentDir()});
                System.out.println(MessageFormat.format("Subdirectory {0} successfully deleted from {1}.", new Object[]{dir, this.getCurrentDir()}));
            }
        } else {
            String[] path;
            if (dir.indexOf("/") == 0) {
                path = dir.substring(1).split("/");
            } else {
                path = dir.split("/");
            }
            int n = path.length;

            Directory dr;
            switch (path[0]) {
                case "root":
                    dr = this.tree.getRoot();
                    break;
                case "..":
                    dr = this.tree.getCurrentNode()._getParent();
                    break;
                default:
                    dr = this.tree.getCurrentNode();
                    break;
            }

            int start = (path[0].equalsIgnoreCase("root") || path[0].equalsIgnoreCase("..")) ? 1 : 0;
            boolean flag = true;
            for (int i = start; i < n - 1; i++) {
                if (path[i].equalsIgnoreCase("..")) {
                    dr = dr._getParent();
                } else if (this.tree.searchDirectory(dr, path[i], 1)) {
                    dr = this.tree.getDirectory(dr, path[i]);
                } else {
                    flag = false;
//                    LOGGER.log(Level.INFO, "A subdirectory {0} does not exist in {1}.", new Object[]{path[i], dr._getPath()});
                    System.out.println(MessageFormat.format("A subdirectory {0} does not exist in {1}.", new Object[]{path[i], dr._getPath()}));
                    break;
                }
            }
            if (flag) {
                this.tree.remove(dr, path[n - 1], 1);
                dr._getParent()._addSubdirectory(dr);
//                LOGGER.log(Level.INFO, "Subdirectory {0} successfully deleted from {1}.", new Object[]{dir, dr._getPath()});
                System.out.println(MessageFormat.format("Subdirectory {0} successfully deleted from {1}.", new Object[]{dir, dr._getPath()}));
            }
        }
    }

    public void cd(String dir) {
        if (dir.indexOf("/") < 0) {
            if (tree.searchDirectory(this.tree.getCurrentNode(), dir, 1)) {
                Directory tmp = this.tree.getCurrentNode();
                this.tree.setCurrent(this.tree.getDirectory(tmp, dir));
            } else if (dir.equalsIgnoreCase("..")) {
                if (!this.tree.getCurrentNode().toString().equalsIgnoreCase("root")) {
                    Directory tmp = this.tree.getCurrentNode();
                    this.tree.setCurrent(tmp._getParent());
                }
            } else {
//                LOGGER.log(Level.INFO, "A subdirectory {0} does not exist in {1}.", new Object[]{dir, this.getCurrentDir()});
                System.out.println(MessageFormat.format("A subdirectory {0} does not exist in {1}.", new Object[]{dir, this.getCurrentDir()}));
            }
        } else {
            String[] path;
            if (dir.indexOf("/") == 0) {
                path = dir.substring(1).split("/");
            } else {
                path = dir.split("/");
            }
            int n = path.length;

            Directory dr;
            switch (path[0]) {
                case "root":
                    dr = this.tree.getRoot();
                    break;
                case "..":
                    dr = this.tree.getCurrentNode()._getParent();
                    break;
                default:
                    dr = this.tree.getCurrentNode();
                    break;
            }

            int start = (path[0].equalsIgnoreCase("root") || path[0].equalsIgnoreCase("..")) ? 1 : 0;
            boolean flag = true;
            for (int i = start; i < n; i++) {
                Directory tmp = path[i].equalsIgnoreCase("..") ? dr._getParent() : dr._getSubdirectory(path[i]);
                if (tmp != null) {
                    dr = tmp;
                } else {
//                    LOGGER.log(Level.INFO, "A subdirectory {0} does not exist in {1}.", new Object[]{path[i], dr._getPath()});
                    System.out.println(MessageFormat.format("A subdirectory {0} does not exist in {1}.", new Object[]{path[i], dr._getPath()}));
                    flag = false;
                    break;
                }
            }
            if (flag) {
                this.tree.setCurrent(dr);
            }
        }
    }

    public void create(String str, boolean append) {
        CustomFile f = null;
        if (str.indexOf("/") < 0) {
            String[] file = new String[2];
            file[0] = str.substring(0, str.lastIndexOf("."));
            file[1] = str.substring(str.lastIndexOf(".") + 1);
            if (!tree.searchDirectory(this.tree.getCurrentNode(), str, -1)) {
                f = new CustomFile(this.tree.getCurrentNode(), file[0], "", file[1]);
                this.tree.insert(f, this.tree.getCurrentNode());
//                LOGGER.log(Level.INFO, "File {0} successfully added in {1}.", new Object[]{file[0], this.getCurrentDir()});
                System.out.println(MessageFormat.format("File {0} successfully added in {1}.", new Object[]{file[0], this.getCurrentDir()}));
//                ls();
            } else {
//                LOGGER.log(Level.INFO, "File {0} already exists in {1}.", new Object[]{file[0], this.getCurrentDir()});
                System.out.println(MessageFormat.format("File {0} already exists in {1}.", new Object[]{file[0], this.getCurrentDir()}));
                f = this.tree.getCurrentNode()._getFile(str);
            }
        } else {
            String[] path;
            if (str.indexOf("/") == 0) {
                path = str.substring(1).split("/");
            } else {
                path = str.split("/");
            }

            int n = path.length;

            Directory dr;
            switch (path[0]) {
                case "root":
                    dr = this.tree.getRoot();
                    break;
                case "..":
                    dr = this.tree.getCurrentNode()._getParent();
                    break;
                default:
                    dr = this.tree.getCurrentNode();
                    break;
            }

            int start = (path[0].equalsIgnoreCase("root") || path[0].equalsIgnoreCase("..")) ? 1 : 0;
            for (int i = start; i < n - 1; i++) {
                if (path[i].equalsIgnoreCase("..")) {
                    dr = dr._getParent();
                } else if (this.tree.searchDirectory(dr, path[i], -1)) {
                    dr = this.tree.getDirectory(dr, path[i]);
                } else {
                    Directory newDr = new Directory(path[i], dr);
                    dr._addSubdirectory(newDr);
                    dr = newDr;
                }
            }
            if (!this.tree.searchDirectory(dr, path[n - 1], -1)) {
                String[] file = path[n - 1].split(".", 2);
                f = new CustomFile(dr, file[0], "", file[1]);
                dr._addFile(f);
            } else {
//                LOGGER.log(Level.INFO, "{0} already exists in {1}.", new Object[]{path[n - 1], dr._getPath()});
                System.out.println(MessageFormat.format("{0} already exists in {1}.", new Object[]{path[n - 1], dr._getPath()}));
            }
        }
        editor(f, append);
    }

    public void rm(String file) {
        if (reTester.isValid(file)) {
            // TODO:
            if (this.tree.getCurrentNode()._getFiles().isEmpty()) {
//                LOGGER.log(Level.INFO, "Current directory {0} is empty.", new Object[]{this.getCurrentDir()});
                System.out.println(MessageFormat.format("Current directory {0} is empty.", new Object[]{this.getCurrentDir()}));
            } else {
                this.tree.removeAll(this.tree.getCurrentNode(), file);
//                LOGGER.log(Level.INFO, "Files {0} successfully removed in {1}.", new Object[]{file, this.getCurrentDir()});
                System.out.println(MessageFormat.format("Files {0} successfully removed in {1}.", new Object[]{file, this.getCurrentDir()}));
            }
        } else if (file.indexOf("/") < 0) {
            if (!tree.searchDirectory(this.tree.getCurrentNode(), file, -1)) {
//                LOGGER.log(Level.INFO, "File {0} does not exist in current directory {1}.", new Object[]{file, this.getCurrentDir()});
                System.out.println(MessageFormat.format("File {0} does not exist in current directory {1}.", new Object[]{file, this.getCurrentDir()}));
            } else {
                this.tree.remove(this.tree.getCurrentNode(), file, -1);
//                LOGGER.log(Level.INFO, "File {0} successfully removed from directory {1}.", new Object[]{file, this.getCurrentDir()});
                System.out.println(MessageFormat.format("File {0} successfully removed from directory {1}.", new Object[]{file, this.getCurrentDir()}));
            }
        } else {
            String[] path;
            if (file.indexOf("/") == 0) {
                path = file.substring(1).split("/");
            } else {
                path = file.split("/");
            }

            int n = path.length;

            Directory dr;
            switch (path[0]) {
                case "root":
                    dr = this.tree.getRoot();
                    break;
                case "..":
                    dr = this.tree.getCurrentNode()._getParent();
                    break;
                default:
                    dr = this.tree.getCurrentNode();
                    break;
            }

            int start = (path[0].equalsIgnoreCase("root") || path[0].equalsIgnoreCase("..")) ? 1 : 0;
            boolean flag = true;
            for (int i = start; i < n - 1; i++) {
                if (path[i].equalsIgnoreCase("..")) {
                    dr = dr._getParent();
                } else if (this.tree.searchDirectory(dr, path[i], -1)) {
                    dr = this.tree.getDirectory(dr, path[i]);
                } else {
                    flag = false;
//                    LOGGER.log(Level.INFO, "A directory {0} does not exist.", new Object[]{path[i]});
                    System.out.println(MessageFormat.format("A directory {0} does not exist.", new Object[]{path[i]}));
                    break;
                }
            }
            if (flag) {
                this.tree.remove(dr, path[n - 1], -1);
                dr._getParent()._addSubdirectory(dr);
//                LOGGER.log(Level.INFO, "{0} successfully removed from {1}.", new Object[]{path[n - 1], dr._getPath()});
                System.out.println(MessageFormat.format("{0} successfully removed from {1}.", new Object[]{path[n - 1], dr._getPath()}));
            }
        }
    }

    public void rn(String newF, String oldF) {
        if (this.tree.searchDirectory(this.tree.getCurrentNode(), oldF, 0)) {
            if (!this.tree.searchDirectory(this.tree.getCurrentNode(), newF, 0)) {
                Node f = this.tree.getCurrentNode()._containsFile(oldF)
                        ? this.tree.getCurrentNode()._getFile(oldF) : this.tree.getCurrentNode()._getSubdirectory(oldF);
                if (f.isDirectory()) {
                    f.rename(newF);
                } else {
                    CustomFile fl = this.tree.getCurrentNode()._getFile(oldF);
                    String n = newF.indexOf("." + fl._getExt()) < 0 ? newF : newF.substring(0, newF.indexOf("." + fl._getExt()));
                    f.rename(n);
                }
                this.tree.getCurrentNode()._removeChild(oldF, f.isDirectory());
                this.tree.insert(f, this.tree.getCurrentNode());
            } else {
//                LOGGER.log(Level.INFO, "{0} already exists in {1}.", new Object[]{newF, this.getCurrentDir()});
                System.out.println(MessageFormat.format("{0} already exists in {1}.", new Object[]{newF, this.getCurrentDir()}));
            }
        } else {
//            LOGGER.log(Level.INFO, "{0} does not exist in current directory {1}.", new Object[]{oldF, this.getCurrentDir()});
            System.out.println(MessageFormat.format("{0} does not exist in current directory {1}.", new Object[]{oldF, this.getCurrentDir()}));
        }
    }

    public void cp(String f, String p) {
        if (this.tree.searchDirectory(this.tree.getCurrentNode(), f, 0)) {
            Node no = this.tree.getCurrentNode()._containsFile(f)
                    ? this.tree.getCurrentNode()._getFile(f) : this.tree.getCurrentNode()._getSubdirectory(f);
            if (p.indexOf("/") < 0) {
                if (!this.tree.searchDirectory(this.tree.getCurrentNode(), p, 0)) {
                    String n = p.lastIndexOf(".") > 0 ? p.substring(0, p.lastIndexOf(".")) : p;
                    if (no.isDirectory()) {
                        Directory d = this.tree.getCurrentNode()._getSubdirectory(f);
                        this.tree.insert(new Directory(new Descriptor(n, true, new Date()), this.tree.getCurrentNode(), d._getSubdirectories(), d._getFiles()), this.tree.getCurrentNode());
                    } else {
                        CustomFile fl = this.tree.getCurrentNode()._getFile(f);
                        this.tree.insert(new CustomFile(new Descriptor(n, false, new Date()), this.tree.getCurrentNode(), new Date(), fl._getContent(), fl._getExt()), this.tree.getCurrentNode());
                    }
                    System.out.println(MessageFormat.format("{1} successfully copied as {0}.", new Object[]{p, f}));
                } else {
                    System.out.println(MessageFormat.format("{0} already exists in current directory. copy failed.", new Object[]{p}));
                }
            } else {
                String[] path;
                if (p.indexOf("/") == 0) {
                    path = p.substring(1).split("/");
                } else {
                    path = p.split("/");
                }

                int l = path.length;

                Directory dr;
                switch (path[0]) {
                    case "root":
                        dr = this.tree.getRoot();
                        break;
                    case "..":
                        dr = this.tree.getCurrentNode()._getParent();
                        break;
                    default:
                        dr = this.tree.getCurrentNode();
                        break;
                }

                int start = (path[0].equalsIgnoreCase("root") || path[0].equalsIgnoreCase("..")) ? 1 : 0;
                for (int i = start; i < l - 1; i++) {
                    if (path[i].equalsIgnoreCase("..")) {
                        dr = dr._getParent();
                    } else if (this.tree.searchDirectory(dr, path[i], 0)) {
                        dr = this.tree.getDirectory(dr, path[i]);
                    } else {
                        Directory newDr = new Directory(path[i], dr);
                        dr._addSubdirectory(newDr);
                        dr = newDr;
                    }
                }

                String fn = path[l - 1];
                fn = fn.lastIndexOf(".") > 0 ? fn.substring(0, fn.lastIndexOf(".")) : fn;
                if (!this.tree.searchDirectory(dr, fn, 0)) {
                    if (no.isDirectory()) {
                        Directory d = this.tree.getCurrentNode()._getSubdirectory(f);
                        this.tree.insert(new Directory(new Descriptor(fn, true, new Date()), dr, d._getSubdirectories(), d._getFiles()), dr);
                        dr._addSubdirectory(d);
                    } else {
                        CustomFile fl = this.tree.getCurrentNode()._getFile(f);
                        this.tree.insert(new CustomFile(new Descriptor(fn, false, new Date()), dr, new Date(), fl._getContent(), fl._getExt()), dr);
                        dr._addFile(fl);
                    }
                    System.out.println(MessageFormat.format("{1} successfully copied as {0} in {2}.", new Object[]{path[l - 1], f, dr.toString()}));
                } else {
//                LOGGER.log(Level.INFO, "Directory {0} already contains {1}. {1} not moved.", new Object[]{dr.toString(), f});
                    System.out.println(MessageFormat.format("{1} already exists in {0}. copy failed.", new Object[]{dr.toString(), f}));
                }
            }
        } else {
//            LOGGER.log(Level.INFO, "{0} does not exist in current directory {1}.", new Object[]{f, this.getCurrentDir()});
            System.out.println(MessageFormat.format("{0} does not exist in current directory {1}.", new Object[]{f, this.getCurrentDir()}));
        }
    }

    public void mv(String f, String p) {
        if (this.tree.searchDirectory(this.tree.getCurrentNode(), f, 0)) {
            Node no = this.tree.getCurrentNode()._containsFile(f)
                    ? this.tree.getCurrentNode()._getFile(f) : this.tree.getCurrentNode()._getSubdirectory(f);
            if (p.indexOf("/") < 0) {
                Directory d;
                if (this.tree.getCurrentNode()._containsSubdirectory(p)) {
                    d = this.tree.getCurrentNode()._getSubdirectory(p);

                } else {
                    d = new Directory(p, this.tree.getCurrentNode());
                    d._setParent(this.tree.getCurrentNode());
                }
                no._setParent(d);
                this.tree.insert(no, d);
                this.tree.getCurrentNode()._addSubdirectory(d);
                this.tree.remove(this.tree.getCurrentNode(), f, 0);
            } else {
                String[] path;
                if (p.indexOf("/") == 0) {
                    path = p.substring(1).split("/");
                } else {
                    path = p.split("/");
                }

                int l = path.length;

                Directory dr;
                switch (path[0]) {
                    case "root":
                        dr = this.tree.getRoot();
                        break;
                    case "..":
                        dr = this.tree.getCurrentNode()._getParent();
                        break;
                    default:
                        dr = this.tree.getCurrentNode();
                        break;
                }

                int start = (path[0].equalsIgnoreCase("root") || path[0].equalsIgnoreCase("..")) ? 1 : 0;
                for (int i = start; i < l - 1; i++) {
                    if (path[i].equalsIgnoreCase("..")) {
                        dr = dr._getParent();
                    } else if (this.tree.searchDirectory(dr, path[i], 0)) {
                        dr = this.tree.getDirectory(dr, path[i]);
                    } else {
                        Directory newDr = new Directory(path[i], dr);
                        dr._addSubdirectory(newDr);
                        dr = newDr;
                    }
                }

                String fn = path[l - 1];
                Directory d;
                if (dr._containsSubdirectory(fn)) {
                    d = dr._getSubdirectory(fn);

                } else {
                    d = new Directory(fn, dr);
                }
                no._setParent(d);
                this.tree.insert(no, d);
                dr._addSubdirectory(d);
                this.tree.remove(this.tree.getCurrentNode(), f, 0);
            }
            System.out.println(MessageFormat.format("{1} successfully moved to {0}.", new Object[]{p, f}));
        } else {
//            LOGGER.log(Level.INFO, "{0} does not exist in current directory {1}.", new Object[]{f, this.getCurrentDir()});
            System.out.println(MessageFormat.format("{0} does not exist in current directory {1}.", new Object[]{f, this.getCurrentDir()}));
        }
    }

    public String show(String f) {
        if (this.tree.getCurrentNode()._containsFile(f)) {

        } else {
//            LOGGER.log(Level.INFO, "{0} does not exist in current directory {1}.", new Object[]{f, this.getCurrentDir()});
            System.out.println(MessageFormat.format("{0} does not exist in current directory {1}.", new Object[]{f, this.getCurrentDir()}));
        }
        return null;
    }

    public void whereis(String c) {
        ArrayList<String> locations = this.tree.fullSearch(this.tree.getRoot(), c, new ArrayList<>());
        if (!locations.isEmpty()) {
            for (String s : locations) {
                System.out.println(s);
            }
        } else {
//            LOGGER.log(Level.INFO, "{0} does not exist in the file system.", new Object[]{c});
            System.out.println(MessageFormat.format("{0} does not exist in the file system.", new Object[]{c}));
        }
    }

    public void ls() {
        System.out.println("in current directory " + this.tree.getCurrentNode().toString() + ": ");
        Directory n = this.tree.getCurrentNode();
        if (n.isEmpty()) {
//            LOGGER.log(Level.INFO, "This directory is empty.");
            System.out.println("This directory is empty.");
        } else {
            this.tree.list(n);
        }
    }

    public void ls(Directory n) {
        if (n.isEmpty()) {
//            LOGGER.log(Level.INFO, "This directory is empty.");
            System.out.println("This directory is empty.");
        } else {
            this.tree.list(n);
        }
    }

    public void ls(String re) {
        // TODO:
        Directory n = this.tree.getCurrentNode();
        if (n.isEmpty()) {
//            LOGGER.log(Level.INFO, "This directory is empty.");
            System.out.println("This directory is empty.");
        } else {
            try {
                this.tree.listAll(this.tree.getCurrentNode(), re);
            } catch (Exception e) {
//                LOGGER.log(Level.INFO, "{0}", new Object[]{e});
                System.out.println(e);
            }
        }
    }

    public String getCurrentDir() {
        Stack<Node> path = new Stack<>();
        StringBuilder sb = new StringBuilder();
        Node n = this.tree.getCurrentNode();
        while (n != null) {
            path.add(n);
            n = n._getParent();
        }
        boolean flag = false;
        while (!path.isEmpty()) {
            if (flag) {
                sb.append("\\");
            }
            sb.append(path.pop().toString());
            flag = true;
        }
        return sb.toString();
    }

    public void editor(CustomFile f, boolean append) {
        String cont = f._getContent();
    }

}

class Main {

    private static final Logger LOGGER = Logger.getLogger(Tree.class.getName());

    public static void main(String[] args) throws FileNotFoundException {
//        CustomFile f = new CustomFile(new Descriptor("Hello", false, new Date()), null, new Date(), "HelloWorld", "txt");
//        Document d = new Document(f);
//        while (true) {
            runFileSystem();
//        }
    }

    private static void runFileSystem() throws FileNotFoundException {
        FileSystem sys = new FileSystem();
        Scanner sc = new Scanner(new File("mp3.in"));
        while (sc.hasNext()) {
            String inst = sc.nextLine();
            System.out.println("\n[" + inst + "]");
            System.out.print("$" + sys.getCurrentDir());
            System.out.print("> ");
            String[] inp = inst.split("\\s+");
            int l = inp.length;
            if ((l == 2)) {
                switch (inp[0]) {
                    case "mkdir":
                        sys.mkdir(inp[1]);
                        break;
                    case "cd":
                        sys.cd(inp[1]);
                        break;
                    case "rmdir":
                        sys.rmdir(inp[1]);
                        break;
                    case "ls":
                        sys.ls(inp[1]);
                        break;
                    case ">":
                        sys.create(inp[1], true);
                        break;
                    case ">>":
                        sys.create(inp[1], true);
                        break;
                    case "edit":
                        sys.create(inp[1], false);
                        break;
                    case "rm":
                        sys.rm(inp[1]);
                        break;
                    case "whereis":
                        sys.whereis(inp[1]);
                        break;
                    case "rn":
//                        LOGGER.log(Level.INFO, "usage: rn <old file name> <new file name>");
                        System.out.println("usage: rn <old file name> <new file name>");
//                        sys.ls();
                        break;
                    case "mv":
//                        LOGGER.log(Level.INFO, "usage: mv <file name/path> <path>");
                        System.out.println("usage: mv <file name/path> <path>");
//                        sys.ls();
                        break;
                    case "cp":
//                        LOGGER.log(Level.INFO, "usage: cp <filename> <copy filename>");
                        System.out.println("usage: cp <filename> <copy filename>");
                        break;
                    default:
//                        LOGGER.log(Level.INFO, "{0} is not recognized as a command.", new Object[]{inp[0]});
                        System.out.println(MessageFormat.format("{0} is not recognized as a command.", new Object[]{inp[0]}));
                        break;
                }
            } else if (l == 1) {
                switch (inp[0]) {
                    case "ls":
                        sys.ls();
                        break;
                    case "mkdir":
//                        LOGGER.log(Level.INFO, "usage: mkdir <directory name/path>");
                        System.out.println("usage: mkdir <directory name/path>");
                        break;
                    case "cd":
//                        LOGGER.log(Level.INFO, "usage: cd <directory name/path/..>");
                        System.out.println("usage: cd <directory name/path/..>");
                        break;
                    case "rmdir":
//                        LOGGER.log(Level.INFO, "usage: rmdir <directory name/path>");
                        System.out.println("usage: rmdir <directory name/path>");
                        break;
                    case ">":
//                        LOGGER.log(Level.INFO, "usage: > <new file name/path>");
                        System.out.println("usage: > <file name/path>");
                        break;
                    case ">>":
                        System.out.println("usage: >> <file name/path>");
                        break;
                    case "edit":
                        System.out.println("usage: edit <file name/path>");
                        break;
                    case "rm":
//                        LOGGER.log(Level.INFO, "usage: rm <file name/path>");
                        System.out.println("usage: rm <file name/path>");
                        break;
                    case "whereis":
//                        LOGGER.log(Level.INFO, "usage: whereis <name of file/directory>");
                        System.out.println("usage: whereis <name of file/directory>");
                        break;
                    case "rn":
//                        LOGGER.log(Level.INFO, "usage: rn <old file name> <new file name>");
                        System.out.println("usage: rn <old file name> <new file name>");
//                        sys.ls();
                        break;
                    case "mv":
//                        LOGGER.log(Level.INFO, "usage: mv <file name/path> <path>");
                        System.out.println("usage: mv <source_file/source_directory> <target_file/target_directoryh>");
//                        sys.ls();
                        break;
                    case "cp":
//                        LOGGER.log(Level.INFO, "usage: cp <filename> <copy filename>");
                        System.out.println("usage: cp <source_file/source_directory> <target_file/target_directory>");
                        break;
                    default:
//                        LOGGER.log(Level.INFO, "{0} is not recognized as a command.", new Object[]{inp[0]});
                        System.out.println(MessageFormat.format("{0} is not recognized as a command.", new Object[]{inp[0]}));
                        break;
                }
            } else if (l == 3) {
                switch (inp[0]) {
                    case "rn":
                        sys.rn(inp[2], inp[1]);
//                        sys.ls();
                        break;
                    case "mv":
                        sys.mv(inp[1], inp[2]);
//                        sys.ls();
                        break;
                    case "cp":
                        sys.cp(inp[1], inp[2]);
//                        sys.ls();
                        break;
                    default:
//                        LOGGER.log(Level.INFO, "{0} is not recognized as a command.", new Object[]{inp[0]});
                        System.out.println(MessageFormat.format("{0} is not recognized as a command.", new Object[]{inp[0]}));
                        break;
                }
            }
        }
    }

}

class RegexTester {

    private static final Logger LOGGER = Logger.getLogger(Tree.class.getName());

    public boolean isValid(String re) {
        try {
            Pattern.compile(re);
        } catch (PatternSyntaxException exception) {
            LOGGER.log(Level.WARNING, exception.getDescription());
            return false;
        }
        return true;
    }
}

class Document extends JFrame implements ActionListener {

    private JTextArea ta;
    private int count;
    private JMenuBar menuBar;
    private JMenu fileM, editM;
    private JScrollPane scpane;
    private JMenuItem exitI, cutI, copyI, pasteI, selectI, saveI;
    private String pad;
    private JToolBar toolBar;
    private CustomFile file;

    public Document(CustomFile f) {
        super("Document");
        this.file = f;

        setSize(600, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        Container pane = getContentPane();
        pane.setLayout(new BorderLayout());

        count = 0;
        pad = " ";
        ta = new JTextArea(f._getContent()); //textarea
        menuBar = new JMenuBar(); //menubar
        fileM = new JMenu("File"); //file menu
        editM = new JMenu("Edit"); //edit menu
        scpane = new JScrollPane(ta); //scrollpane  and add textarea to scrollpane
        exitI = new JMenuItem("Exit");
        cutI = new JMenuItem("Cut");
        copyI = new JMenuItem("Copy");
        pasteI = new JMenuItem("Paste");
        selectI = new JMenuItem("Select All"); //menuitems
        saveI = new JMenuItem("Save"); //menuitems
        toolBar = new JToolBar();

        ta.setLineWrap(true);
        ta.setWrapStyleWord(true);

        setJMenuBar(menuBar);
        menuBar.add(fileM);
        menuBar.add(editM);

        fileM.add(saveI);
        fileM.add(exitI);

        editM.add(cutI);
        editM.add(copyI);
        editM.add(pasteI);
        editM.add(selectI);

        saveI.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, ActionEvent.CTRL_MASK));
        cutI.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_X, ActionEvent.CTRL_MASK));
        copyI.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C, ActionEvent.CTRL_MASK));
        pasteI.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_V, ActionEvent.CTRL_MASK));
        selectI.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_A, ActionEvent.CTRL_MASK));

        pane.add(scpane, BorderLayout.CENTER);
        pane.add(toolBar, BorderLayout.SOUTH);

        saveI.addActionListener(this);
        exitI.addActionListener(this);
        cutI.addActionListener(this);
        copyI.addActionListener(this);
        pasteI.addActionListener(this);
        selectI.addActionListener(this);

        setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        JMenuItem choice = (JMenuItem) e.getSource();
        if (choice == saveI) {
            this.file._seContent(ta.getText());
            this.hide();
        } else if (choice == exitI) {
            System.exit(0);
        } else if (choice == cutI) {
            pad = ta.getSelectedText();
            ta.replaceRange("", ta.getSelectionStart(), ta.getSelectionEnd());
        } else if (choice == copyI) {
            pad = ta.getSelectedText();
        } else if (choice == pasteI) {
            ta.insert(pad, ta.getCaretPosition());
        } else if (choice == selectI) {
            ta.selectAll();
        }
    }
}
