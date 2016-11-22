/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Main;

import java.util.Hashtable;
import java.util.Scanner;
import java.util.Stack;
import java.util.logging.Level;
import java.util.logging.Logger;
import static jdk.nashorn.internal.objects.NativeJava.type;

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
        if (!tree.search(this.tree.root, dir)) {
            Directory newNode = new Directory(dir);
            this.tree.insert(newNode);
        } else {
            LOGGER.log(Level.INFO, "A subdirectory {0} already exists.", new Object[]{dir});
        }

    }

    public void cdir(String dir) {
        if (tree.search(this.tree.root, dir)) {
            Node tmp = this.tree.getCurrentNode();
            this.tree.current = (Directory) this.tree.getNode((Directory) this.tree.getCurrentNode(), dir);
        } else if(dir.equalsIgnoreCase("..")) {
            if(!this.tree.current.toString().equalsIgnoreCase("root"))
                this.tree.current = (Directory) this.tree.current._getParent();
        } else {
            LOGGER.log(Level.INFO, "A subdirectory {0} does not exist.", new Object[]{dir});
        }
    }

    public void listAll() {
        Directory curr = (Directory) this.tree.getCurrentNode();
        Hashtable<String, Node> tmpc = curr._getChildren();
        for(String ch : tmpc.keySet()) {
            System.out.println(tmpc.get(ch)._getDescriptor());
        }
    }
    
    public String getCurrentDir() {
        Stack<Node> path = new Stack<>();
        StringBuilder sb = new StringBuilder();
        Node n = this.tree.current;
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
                        System.out.println(sys.getCurrentDir());
                        break;
                    case "cd":
                        sys.cdir(inp[1]);
                        System.out.println(sys.getCurrentDir());
                    default:
                        break;
                }
            }
        }
    }
}
