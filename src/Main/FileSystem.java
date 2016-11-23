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

/**
 *
 * @author MaRose
 */
public class FileSystem {

    private static final Logger LOGGER = Logger.getLogger(Tree.class.getName());

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
                LOGGER.log(Level.INFO, "Subdirectory {0} successfully added.", new Object[]{dir});
                listAll();
            } else {
                LOGGER.log(Level.INFO, "A subdirectory {0} already exists.", new Object[]{dir});
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
                LOGGER.log(Level.INFO, "A directory {0} already exists.", new Object[]{dir});
            }
            this.listAll(dr);
        }
    }

    public void rmdir(String dir) {
        if (dir.indexOf("/root") != 0) {
            if (!tree.searchDirectory(this.tree.getCurrentNode(), dir)) {
                LOGGER.log(Level.INFO, "A subdirectory {0} does not exist.", new Object[]{dir});

            } else {
                this.tree.remove(this.tree.getCurrentNode(), dir);
                LOGGER.log(Level.INFO, "Subdirectory {0} successfully removed.", new Object[]{dir});
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
                    LOGGER.log(Level.INFO, "A directory {0} does not exist.", new Object[]{path[i]});
                    break;
                }
            }
            if(flag) {
                this.tree.remove(dr, path[n-1]);
                dr._getParent()._addChild(dr);
                LOGGER.log(Level.INFO, "Subdirectory {0} successfully removed.", new Object[]{dir});
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
                LOGGER.log(Level.INFO, "A subdirectory {0} does not exist.", new Object[]{dir});
            }
        } else {
            String[] path = dir.substring(1).split("/");
            int n = path.length;
            Directory dr = this.tree.getRoot();
            boolean flag = true;
            for (int i = 1; i < n; i++) {
                Directory tmp = this.tree.getDirectory(dr, path[i]);
                if (tmp != null) {
                    System.out.println(path[i] + " exists.");
                    dr = tmp;
                } else {
                    LOGGER.log(Level.INFO, "A subdirectory {0} does not exist.", new Object[]{path[i]});
                    flag = false;
                    break;
                }
            }
            if (flag) {
                this.tree.setCurrent(dr);
            }
        }
    }

    public void listAll() {
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

            if ((inp.length == 2)) {
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
                    default:
                        break;
                }
            } else if (inp.length == 1) {
                switch (inp[0]) {
                    case "ls":
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
