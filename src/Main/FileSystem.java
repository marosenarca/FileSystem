/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Main;

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
        if (dir.indexOf("/root") != 0) {
            if (!tree.searchDirectory(this.tree.getCurrentNode(), dir)) {
                Directory newNode = new Directory(dir, this.tree.getCurrentNode());
                this.tree.insert(newNode, this.tree.getCurrentNode());
                LOGGER.log(Level.INFO, "Subdirectory {0} successfully added in {1}.", new Object[]{dir, this.getCurrentDir()});
                listAll();
            } else {
                LOGGER.log(Level.INFO, "A subdirectory {0} already exists in {1}.", new Object[]{dir, this.getCurrentDir()});
            }
        } else {
            String[] path = dir.substring(1).split("/");
            int n = path.length;
            Directory dr = this.tree.getRoot();
            for (int i = 1; i < n - 1; i++) {
                if (this.tree.searchDirectory(dr, path[i])) {
                    dr = this.tree.getDirectory(dr, path[i]);
                } else {
                    Directory newDr = new Directory(path[i], dr);
                    dr._addChild(newDr);
                    dr = newDr;
                }
            }
            if (!this.tree.searchDirectory(dr, path[n - 1])) {
                Directory newDr = new Directory(path[n - 1], dr);
                dr._addChild(newDr);
            } else {
                LOGGER.log(Level.INFO, "A subdirectory {0} already exists in {1}.", new Object[]{dir, dr._getPath()});
            }
            this.listAll(dr);
        }
    }

    public void rmdir(String dir) {
        if (dir.indexOf("/root") != 0) {
            if (!tree.searchDirectory(this.tree.getCurrentNode(), dir)) {
                LOGGER.log(Level.INFO, "A subdirectory {0} does not exist in {1}.", new Object[]{dir, this.getCurrentDir()});

            } else {
                this.tree.remove(this.tree.getCurrentNode(), dir);
                LOGGER.log(Level.INFO, "Subdirectory {0} successfully deleted from {1}.", new Object[]{dir, this.getCurrentDir()});
                listAll();
            }
        } else {
            String[] path = dir.substring(1).split("/");
            int n = path.length;
            Directory dr = this.tree.getRoot();
            boolean flag = true;
            for (int i = 1; i < n - 1; i++) {
                if (this.tree.searchDirectory(dr, path[i])) {
                    dr = this.tree.getDirectory(dr, path[i]);
                } else {
                    flag = false;
                    LOGGER.log(Level.INFO, "A subdirectory {0} does not exist in {1}.", new Object[]{path[i], dr._getPath()});
                    break;
                }
            }
            if (flag) {
                this.tree.remove(dr, path[n - 1]);
                dr._getParent()._addChild(dr);
                LOGGER.log(Level.INFO, "Subdirectory {0} successfully deleted from {1}.", new Object[]{dir, dr._getPath()});
            }
            this.listAll(dr);
        }
    }

    public void cdir(String dir) {
        if (dir.indexOf("/root") != 0) {
            if (tree.searchDirectory(this.tree.getCurrentNode(), dir)) {
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
            Directory dr = this.tree.getRoot();
            boolean flag = true;
            for (int i = 1; i < n; i++) {
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
        if (str.indexOf("/root") != 0) {
            String[] file = str.split(".", 1);
            if (!tree.searchDirectory(this.tree.getCurrentNode(), file[0])) {
                CustomFile f = new CustomFile(this.tree.getCurrentNode(), file[0], null, "doc");
                this.tree.insert(f, this.tree.getCurrentNode());
                LOGGER.log(Level.INFO, "File {0} successfully added in {1}.", new Object[]{file[0], this.getCurrentDir()});
                listAll();
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
            Directory dr = this.tree.getRoot();
            for (int i = 1; i < n - 1; i++) {
                if (this.tree.searchDirectory(dr, path[i])) {
                    dr = this.tree.getDirectory(dr, path[i]);
                } else {
                    Directory newDr = new Directory(path[i], dr);
                    dr._addChild(newDr);
                    dr = newDr;
                }
            }
            if (!this.tree.searchDirectory(dr, path[n - 1])) {
                String[] file = path[n - 1].split(".", 1);
                CustomFile f = new CustomFile(dr, file[0], null, "doc");
                dr._addChild(f);
            } else {
                LOGGER.log(Level.INFO, "File {0} already exists in {1}.", new Object[]{path[n - 1], dr._getPath()});
            }
            this.listAll(dr);
        }
    }

    public void rm(String file) {
        if (reTester.isValid(file)) {
            // TODO:
            if (this.tree.getCurrentNode()._getChildren().isEmpty()) {
                LOGGER.log(Level.INFO, "Current directory {0} is empty.", new Object[]{this.getCurrentDir()});
            } else {
                this.tree.removeAll(this.tree.getCurrentNode(), file);
                LOGGER.log(Level.INFO, "Files {0} successfully removed in {1}.", new Object[]{file, this.getCurrentDir()});
                listAll();
            }
        } else if (file.indexOf("/root") != 0) {
            if (!tree.searchDirectory(this.tree.getCurrentNode(), file)) {
                LOGGER.log(Level.INFO, "File {0} does not exist in current directory {1}.", new Object[]{file, this.getCurrentDir()});
            } else {
                this.tree.remove(this.tree.getCurrentNode(), file);
                LOGGER.log(Level.INFO, "File {0} successfully removed from directory {1}.", new Object[]{file, this.getCurrentDir()});
                listAll();
            }
        } else {
            String[] path = file.substring(1).split("/");
            int n = path.length;
            Directory dr = this.tree.getRoot();
            boolean flag = true;
            for (int i = 1; i < n - 1; i++) {
                if (this.tree.searchDirectory(dr, path[i])) {
                    dr = this.tree.getDirectory(dr, path[i]);
                } else {
                    flag = false;
                    LOGGER.log(Level.INFO, "A directory {0} does not exist.", new Object[]{path[i]});
                    break;
                }
            }
            if (flag) {
                this.tree.remove(dr, path[n - 1]);
                dr._getParent()._addChild(dr);
                LOGGER.log(Level.INFO, "Subdirectory {0} successfully removed from {1}.", new Object[]{path[n - 1], dr._getPath()});
            }
            this.listAll(dr);
        }
    }

    public void rn(String newF, String oldF) {
        if (this.tree.searchDirectory(this.tree.getCurrentNode(), oldF)) {
            if (!this.tree.searchDirectory(this.tree.getCurrentNode(), newF)) {
                Node f = this.tree.getCurrentNode()._get(oldF);
                f.rename(newF);
                this.tree.getCurrentNode()._removeChild(oldF);
                this.tree.insert(f, this.tree.getCurrentNode());
            } else {
                LOGGER.log(Level.INFO, "{0} already exists in {1}.", new Object[]{newF, this.getCurrentDir()});
            }
        } else {
            LOGGER.log(Level.INFO, "{0} does not exist in current directory {1}.", new Object[]{oldF, this.getCurrentDir()});
        }
    }

    public void move(String f, String p, boolean cut) {
        if (this.tree.getCurrentNode()._contains(f)) {
            Node no = this.tree.getCurrentNode()._get(f);

            String[] path = p.substring(1).split("/");
            int l = path.length;
            Directory dr = this.tree.getRoot();
            for (int i = 1; i < l; i++) {
                if (this.tree.searchDirectory(dr, path[i])) {
                    dr = this.tree.getDirectory(dr, path[i]);
                } else {
                    Directory newDr = new Directory(path[i], dr);
                    dr._addChild(newDr);
                    dr = newDr;
                }
            }
            if (!dr._contains(f)) {
                no._setParent(dr);
                dr._addChild(no);
                if (cut) {
                    this.tree.getCurrentNode()._removeChild(f);
                }
            } else {
                LOGGER.log(Level.INFO, "Directory {0} already contains {1}. {1} not moved.", new Object[]{dr.toString(), f});
            }
            System.out.println("in directory " + p + ":");
            this.listAll(dr);
        } else {
            LOGGER.log(Level.INFO, "{0} does not exist in current directory {1}.", new Object[]{f, this.getCurrentDir()});
        }
    }

    public String show(String f) {
        if (this.tree.getCurrentNode()._contains(f)) {
            
        } else {
            LOGGER.log(Level.INFO, "{0} does not exist in current directory {1}.", new Object[]{f, this.getCurrentDir()});
        }
        return null;
    }

    public void listAll() {
        System.out.println("in current directory " + this.tree.getCurrentNode().toString() + ": ");
        Directory n = this.tree.getCurrentNode();
        if (n._getChildren().size() == 0) {
            LOGGER.log(Level.INFO, "This directory is empty.");
        } else {
            this.tree.list(n);
        }
    }

    public void listAll(Directory n) {
        if (n._getChildren().size() == 0) {
            LOGGER.log(Level.INFO, "This directory is empty.");
        } else {
            this.tree.list(n);
        }
    }

    public void listAll(String re) {
        // TODO:
        Directory n = this.tree.getCurrentNode();
        if (n._getChildren().size() == 0) {
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

    public static void main(String[] args) {
        FileSystem sys = new FileSystem();
        while (true) {
            System.out.print("> ");
            Scanner sc = new Scanner(System.in);
            String[] inp = sc.nextLine().split("\\s+");
            int l = inp.length;
            if ((l == 2)) {
                switch (inp[0]) {
                    case "mkdir":
                        sys.mkdir(inp[1]);
                        System.out.println("#" + sys.getCurrentDir());
                        break;
                    case "cd":
                        sys.cdir(inp[1]);
                        System.out.println("#" + sys.getCurrentDir());
                        break;
                    case "rmdir":
                        sys.rmdir(inp[1]);
                        System.out.println("#" + sys.getCurrentDir());
                        break;
                    case "ls":
                        sys.listAll(inp[1]);
                        System.out.println("#" + sys.getCurrentDir());
                        break;
                    case "edit":
                        sys.edit(inp[1]);
                        System.out.println("#" + sys.getCurrentDir());
                        break;
                    case "rm":
                        sys.rm(inp[1]);
                        System.out.println("#" + sys.getCurrentDir());
                        break;
                    default:
                        break;
                }
            } else if (l == 1) {
                switch (inp[0]) {
                    case "ls":
                        sys.listAll();
                        System.out.println("#" + sys.getCurrentDir());
                        break;
                    default:
                        break;
                }
            } else if (l == 3) {
                switch (inp[0]) {
                    case "rn":
                        sys.rn(inp[2], inp[1]);
                        sys.listAll();
                        System.out.println("#" + sys.getCurrentDir());
                        break;
                    case "mv":
                        sys.move(inp[1], inp[2], true);
                        sys.listAll();
                        System.out.println("#" + sys.getCurrentDir());
                        break;
                    case "cp":
                        sys.move(inp[1], inp[2], false);
                        sys.listAll();
                        System.out.println("#" + sys.getCurrentDir());
                        break;
                    default:
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
