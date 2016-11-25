/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Main;

import java.util.ArrayList;
import java.util.Scanner;
import java.util.Stack;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

/**
 *
 * @author MaRose
 */
public class FileSystem {

    private static final Logger LOGGER = Logger.getLogger(Tree.class.getName());
    private static final RegexTester reTester = new RegexTester();

    private Tree tree;

    public FileSystem() {
        init();
    }

    public void init() {
        this.tree = new Tree();
    }

    public void mkdir(String dir) {
        if (dir.indexOf("/") != 0) {
            if (!tree.searchDirectory(this.tree.getCurrentNode(), dir, 1)) {
                Directory newNode = new Directory(dir, this.tree.getCurrentNode());
                this.tree.insert(newNode, this.tree.getCurrentNode());
                LOGGER.log(Level.INFO, "Subdirectory {0} successfully added in {1}.", new Object[]{dir, this.getCurrentDir()});
            } else {
                LOGGER.log(Level.INFO, "A subdirectory {0} already exists in {1}.", new Object[]{dir, this.getCurrentDir()});
            }
        } else {
            String[] path = dir.substring(1).split("/");
            int n = path.length;
            Directory dr = path[0].equalsIgnoreCase("root")
                    ? this.tree.getRoot() : this.tree.getCurrentNode();
            int start = path[0].equalsIgnoreCase("root") ? 1 : 0;
            for (int i = start; i < n - 1; i++) {
                if (this.tree.searchDirectory(dr, path[i], 1)) {
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
                LOGGER.log(Level.INFO, "Directory {0} successfully added in {1}.", new Object[]{dir, this.getCurrentDir()});
            } else {
                LOGGER.log(Level.INFO, "A subdirectory {0} already exists in {1}.", new Object[]{dir, dr._getPath()});
            }
        }
    }

    public void rmdir(String dir) {
        if (dir.indexOf("/") != 0) {
            if (!tree.searchDirectory(this.tree.getCurrentNode(), dir, 1)) {
                LOGGER.log(Level.INFO, "A subdirectory {0} does not exist in {1}.", new Object[]{dir, this.getCurrentDir()});

            } else {
                this.tree.remove(this.tree.getCurrentNode(), dir, 1);
                LOGGER.log(Level.INFO, "Subdirectory {0} successfully deleted from {1}.", new Object[]{dir, this.getCurrentDir()});
                ls();
            }
        } else {
            String[] path = dir.substring(1).split("/");
            int n = path.length;
            Directory dr = path[0].equalsIgnoreCase("root")
                    ? this.tree.getRoot() : this.tree.getCurrentNode();
            int start = path[0].equalsIgnoreCase("root") ? 1 : 0;
            boolean flag = true;
            for (int i = start; i < n - 1; i++) {
                if (this.tree.searchDirectory(dr, path[i], 1)) {
                    dr = this.tree.getDirectory(dr, path[i]);
                } else {
                    flag = false;
                    LOGGER.log(Level.INFO, "A subdirectory {0} does not exist in {1}.", new Object[]{path[i], dr._getPath()});
                    break;
                }
            }
            if (flag) {
                this.tree.remove(dr, path[n - 1], 1);
                dr._getParent()._addSubdirectory(dr);
                LOGGER.log(Level.INFO, "Subdirectory {0} successfully deleted from {1}.", new Object[]{dir, dr._getPath()});
            }
        }
    }

    public void cd(String dir) {
        if (dir.indexOf("/") != 0) {
            if (tree.searchDirectory(this.tree.getCurrentNode(), dir, 1)) {
                Directory tmp = this.tree.getCurrentNode();
                this.tree.setCurrent(this.tree.getDirectory(tmp, dir));
            } else if (dir.equalsIgnoreCase("..")) {
                if (!this.tree.getCurrentNode().toString().equalsIgnoreCase("root")) {
                    Directory tmp = this.tree.getCurrentNode();
                    this.tree.setCurrent(tmp._getParent());
                }
            } else {
                LOGGER.log(Level.INFO, "A subdirectory {0} does not exist in {1}.", new Object[]{dir, this.getCurrentDir()});
            }
        } else {
            String[] path = dir.substring(1).split("/");
            int n = path.length;
            Directory dr = path[0].equalsIgnoreCase("root")
                    ? this.tree.getRoot() : this.tree.getCurrentNode();
            int start = path[0].equalsIgnoreCase("root") ? 1 : 0;
            boolean flag = true;
            for (int i = start; i < n; i++) {
                Directory tmp = this.tree.getDirectory(dr, path[i]);
                if (tmp != null) {
                    dr = tmp;
                } else {
                    LOGGER.log(Level.INFO, "A subdirectory {0} does not exist in {1}.", new Object[]{path[i], dr._getPath()});
                    flag = false;
                    break;
                }
            }
            if (flag) {
                this.tree.setCurrent(dr);
            }
        }
    }

    public void edit(String str) {
        if (str.indexOf("/") != 0) {
            String[] file = new String[2];
            file[0] = str.substring(0, str.lastIndexOf("."));
            file[1] = str.substring(str.lastIndexOf(".") + 1);

            if (!tree.searchDirectory(this.tree.getCurrentNode(), str, -1)) {
                //TODO: prompt text editor
                CustomFile f = new CustomFile(this.tree.getCurrentNode(), file[0], "", file[1]);
                this.tree.insert(f, this.tree.getCurrentNode());
                LOGGER.log(Level.INFO, "File {0} successfully added in {1}.", new Object[]{file[0], this.getCurrentDir()});
                ls();
            } else {
                // TODO:
                LOGGER.log(Level.INFO, "File {0} already exists in {1}.", new Object[]{file[0], this.getCurrentDir()});
                CustomFile f = this.tree.getCurrentNode()._getFile(str);
                StringBuilder s = new StringBuilder(f._getContent());
                System.out.println("========================= ( EDIT ) ====================");
                System.out.println("Enter 'q' or 'Q' if your done editing./n");
                System.out.print(f._getContent() + " ");
            }
        } else {
            String[] path = str.substring(1).split("/");
            int n = path.length;
            Directory dr = path[0].equalsIgnoreCase("root")
                    ? this.tree.getRoot() : this.tree.getCurrentNode();
            int start = path[0].equalsIgnoreCase("root") ? 1 : 0;
            for (int i = start; i < n - 1; i++) {
                if (this.tree.searchDirectory(dr, path[i], -1)) {
                    dr = this.tree.getDirectory(dr, path[i]);
                } else {
                    Directory newDr = new Directory(path[i], dr);
                    dr._addSubdirectory(newDr);
                    dr = newDr;
                }
            }
            if (!this.tree.searchDirectory(dr, path[n - 1], -1)) {
                String[] file = path[n - 1].split(".", 2);
                CustomFile f = new CustomFile(dr, file[0], "", file[1]);
                dr._addFile(f);
            } else {
                LOGGER.log(Level.INFO, "{0} already exists in {1}.", new Object[]{path[n - 1], dr._getPath()});
            }
        }
    }

    public void rm(String file) {
        if (reTester.isValid(file)) {
            // TODO:
            if (this.tree.getCurrentNode()._getFiles().isEmpty()) {
                LOGGER.log(Level.INFO, "Current directory {0} is empty.", new Object[]{this.getCurrentDir()});
            } else {
                this.tree.removeAll(this.tree.getCurrentNode(), file);
                LOGGER.log(Level.INFO, "Files {0} successfully removed in {1}.", new Object[]{file, this.getCurrentDir()});
            }
        } else if (file.indexOf("/") != 0) {
            if (!tree.searchDirectory(this.tree.getCurrentNode(), file, -1)) {
                LOGGER.log(Level.INFO, "File {0} does not exist in current directory {1}.", new Object[]{file, this.getCurrentDir()});
            } else {
                this.tree.remove(this.tree.getCurrentNode(), file, -1);
                LOGGER.log(Level.INFO, "File {0} successfully removed from directory {1}.", new Object[]{file, this.getCurrentDir()});
            }
        } else {
            String[] path = file.substring(1).split("/");
            int n = path.length;
            Directory dr = path[0].equalsIgnoreCase("root")
                    ? this.tree.getRoot() : this.tree.getCurrentNode();
            int start = path[0].equalsIgnoreCase("root") ? 1 : 0;
            boolean flag = true;
            for (int i = start; i < n - 1; i++) {
                if (this.tree.searchDirectory(dr, path[i], -1)) {
                    dr = this.tree.getDirectory(dr, path[i]);
                } else {
                    flag = false;
                    LOGGER.log(Level.INFO, "A directory {0} does not exist.", new Object[]{path[i]});
                    break;
                }
            }
            if (flag) {
                this.tree.remove(dr, path[n - 1], -1);
                dr._getParent()._addSubdirectory(dr);
                LOGGER.log(Level.INFO, "{0} successfully removed from {1}.", new Object[]{path[n - 1], dr._getPath()});
            }
        }
    }

    public void rn(String newF, String oldF) {
        if (this.tree.searchDirectory(this.tree.getCurrentNode(), oldF, 0)) {
            if (!this.tree.searchDirectory(this.tree.getCurrentNode(), newF, 0)) {
                Node f = this.tree.getCurrentNode()._containsFile(oldF)
                        ? this.tree.getCurrentNode()._getFile(oldF) : this.tree.getCurrentNode()._getSubdirectory(oldF);
                f.rename(newF);
                this.tree.getCurrentNode()._removeChild(oldF, f.isDirectory());
                this.tree.insert(f, this.tree.getCurrentNode());
            } else {
                LOGGER.log(Level.INFO, "{0} already exists in {1}.", new Object[]{newF, this.getCurrentDir()});
            }
        } else {
            LOGGER.log(Level.INFO, "{0} does not exist in current directory {1}.", new Object[]{oldF, this.getCurrentDir()});
        }
    }

    public void mv(String f, String p, boolean cut) {
        if (this.tree.searchDirectory(this.tree.getCurrentNode(), f, 0)) {
            Node no = this.tree.getCurrentNode()._containsFile(f)
                    ? this.tree.getCurrentNode()._getFile(f) : this.tree.getCurrentNode()._getSubdirectory(f);

            String[] path = p.substring(1).split("/");
            int l = path.length;
            Directory dr = path[0].equalsIgnoreCase("root")
                    ? this.tree.getRoot() : this.tree.getCurrentNode();
            int start = path[0].equalsIgnoreCase("root") ? 1 : 0;
            for (int i = start; i < l; i++) {
                if (this.tree.searchDirectory(dr, path[i], 0)) {
                    dr = this.tree.getDirectory(dr, path[i]);
                } else {
                    Directory newDr = new Directory(path[i], dr);
                    dr._addSubdirectory(newDr);
                    dr = newDr;
                }
            }
            if (!this.tree.searchDirectory(dr, f, 0)) {
                no._setParent(dr);
                if (no.isDirectory()) {
                    dr._addSubdirectory((Directory) no);
                } else {
                    dr._addFile((CustomFile) no);
                }
                if (cut) {
                    this.tree.getCurrentNode()._removeChild(f, no.isDirectory());
                }
            } else {
                LOGGER.log(Level.INFO, "Directory {0} already contains {1}. {1} not moved.", new Object[]{dr.toString(), f});
            }
            System.out.println("in directory " + p + ":");
            this.ls(dr);
        } else {
            LOGGER.log(Level.INFO, "{0} does not exist in current directory {1}.", new Object[]{f, this.getCurrentDir()});
        }
    }

    public String show(String f) {
        if (this.tree.getCurrentNode()._containsFile(f)) {

        } else {
            LOGGER.log(Level.INFO, "{0} does not exist in current directory {1}.", new Object[]{f, this.getCurrentDir()});
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
            LOGGER.log(Level.INFO, "{0} does not exist in the file system.", new Object[]{c});
        }
    }

    public void ls() {
        System.out.println("in current directory " + this.tree.getCurrentNode().toString() + ": ");
        Directory n = this.tree.getCurrentNode();
        if (n.isEmpty()) {
            LOGGER.log(Level.INFO, "This directory is empty.");
        } else {
            this.tree.list(n);
        }
    }

    public void ls(Directory n) {
        if (n.isEmpty()) {
            LOGGER.log(Level.INFO, "This directory is empty.");
        } else {
            this.tree.list(n);
        }
    }

    public void ls(String re) {
        // TODO:
        Directory n = this.tree.getCurrentNode();
        if (n.isEmpty()) {
            LOGGER.log(Level.INFO, "This directory is empty.");
        } else {
            try {
                this.tree.listAll(this.tree.getCurrentNode(), re);
            } catch (Exception e) {
                LOGGER.log(Level.INFO, "{0}", new Object[]{e});
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

}

class Main {
    private static final Logger LOGGER = Logger.getLogger(Tree.class.getName());
    public static void main(String[] args) {
        FileSystem sys = new FileSystem();
        while (true) {
            System.out.print("#" + sys.getCurrentDir());
            System.out.print("> ");
            Scanner sc = new Scanner(System.in);
            String[] inp = sc.nextLine().split("\\s+");
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
                    case "edit":
                        sys.edit(inp[1]);
                        break;
                    case "rm":
                        sys.rm(inp[1]);
                        break;
                    case "whereis":
                        sys.whereis(inp[1]);
                        break;
                    default:
                        LOGGER.log(Level.INFO, "{0} is not recognized as a command.", new Object[]{inp[0]});
                        break;
                }
            } else if (l == 1) {
                switch (inp[0]) {
                    case "ls":
                        sys.ls();
                        break;
                    default:
                        LOGGER.log(Level.INFO, "{0} is not recognized as a command.", new Object[]{inp[0]});
                        break;
                }
            } else if (l == 3) {
                switch (inp[0]) {
                    case "rn":
                        sys.rn(inp[2], inp[1]);
                        sys.ls();
                        break;
                    case "mv":
                        sys.mv(inp[1], inp[2], true);
                        sys.ls();
                        break;
                    case "cp":
                        sys.mv(inp[1], inp[2], false);
                        sys.ls();
                        break;
                    default:
                        LOGGER.log(Level.INFO, "{0} is not recognized as a command.", new Object[]{inp[0]});
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
