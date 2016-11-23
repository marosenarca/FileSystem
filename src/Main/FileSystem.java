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
