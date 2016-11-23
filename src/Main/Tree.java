/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Main;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Hashtable;
import java.util.Stack;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author MaRose
 */
class Tree {

    private static final Logger LOGGER = Logger.getLogger(Tree.class.getName());
    private Directory root, current;

    public Tree() {
        this.root = new Directory("root");
        this.current = this.root;
    }

    public void insert(Node n, Directory prt) {
        n._setParent(prt);
        prt._addChild(n);
    }

    public boolean searchDirectory(Directory start, String n) {
        Hashtable<String, Node> tmpc = start._getChildren();

        for (String chk : tmpc.keySet()) {
            if (chk.equalsIgnoreCase(n)) {
                return true;
            }
        }
        return false;
    }

    public Directory deepSearch(Directory start, String n) {
        Hashtable<String, Node> tmpc = start._getChildren();

        for (String chk : tmpc.keySet()) {
            Node ch = tmpc.get(chk);
            if (ch.isDirectory()) {
                if (chk.equalsIgnoreCase(n)) {
                    return (Directory) ch;
                } else {
                    return deepSearch((Directory) ch, n);
                }
            }
        }
        return null;
    }

    public Directory getDirectory(Directory parent, String n) {
        Hashtable<String, Node> tmpc = parent._getChildren();
        for (String chk : tmpc.keySet()) {
            Node ch = tmpc.get(chk);
            if (ch.isDirectory()) {
                if (ch.toString().equalsIgnoreCase(n)) {
                    return (Directory) ch;
                }
//                return getDirectory((Directory) ch, n);
            }
        }
        return null;
    }

    public void remove(Directory start, String n) {
        Hashtable<String, Node> tmpc = start._getChildren();

        if (tmpc.containsKey(n.toString())) {
            start._removeChild(n);
        }
    }

    public void removeAll(Directory start, String re) {
        Hashtable<String, Node> tmpc = start._getChildren();
        tmpc.keySet().stream().forEach((s) -> {
            if (s.endsWith(re)) {
                start._removeChild(s);
            }
        });
    }

    public Directory getCurrentNode() {
        return this.current;
    }

    public Directory getRoot() {
        return this.root;
    }

    public void setCurrent(Directory d) {
        this.current = d;
    }

    public void setRoot(Directory d) {
        this.root = d;
    }

    public void list(Directory n) {
        Hashtable<String, Node> tmpc = n._getChildren();
        tmpc.keySet().stream().forEach((s) -> {
            System.out.println(tmpc.get(s)._getDetails());
        });
    }

    public void listAll(Directory curr, String re) {
        Hashtable<String, Node> tmpc = curr._getChildren();
        tmpc.keySet().stream().forEach((s) -> {
            if (s.matches(re)) {
                System.out.println(tmpc.get(s)._getDetails());
            }
        });
    }
}

abstract class Node {

    protected DateFormat dateFormat = new SimpleDateFormat("MMMM dd, yyyy, hh:mm:ss a");
    protected Descriptor desc;
    protected Directory parent;

    public void _setParent(Directory prt) {
        this.parent = prt;
    }

    public void _setDetails(Descriptor desc) {
        this.desc = desc;
    }

    public Directory _getParent() {
        return this.parent;
    }

    public Descriptor _getDescriptor() {
        return this.desc;
    }

    public Date _getDateCreated() {
        return this.desc._dateCreated();
    }

    public abstract String _getDetails();

    public abstract boolean isDirectory();

    public String _getPath() {
        Stack<Node> path = new Stack<>();
        StringBuilder sb = new StringBuilder();
        Node n = this;
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

    public void rename(String name) {
        this.desc._setName(name);
    }

    @Override
    public String toString() {
        return this.desc._toString();
    }
}

class CustomFile extends Node {

    protected DateFormat dateFormat = new SimpleDateFormat("MMMM dd, yyyy, hh:mm:ss a");

    private String content, type;
    private Date modified;

    public CustomFile(Directory prt, String n, String con, String type) {
        this(new Descriptor(n, false, new Date()), prt, new Date(), con, type);
    }

    public CustomFile(Descriptor d, Directory prt, Date mf, String c, String type) {
        this.desc = d;
        this.parent = prt;
        this.content = c;
        this.type = type;
    }

    public String _getContent() {
        return this.content.isEmpty() ? "This file is empty" : this.content;
    }

    public Date _getDateModified() {
        return this.modified;
    }

    public String _getType() {
        return "DOC";
    }

    @Override
    public String _getDetails() {
        StringBuilder sb = new StringBuilder(dateFormat.format(this.desc._dateCreated())).append("\t");
        sb.append("<" + this.type.toUpperCase() + ">").append("\t").append(this.toString());
        return sb.toString();
    }

    @Override
    public boolean isDirectory() {
        return false;
    }

    public String _getFile() {
        return this.toString() + "." + this.type;
    }
}

class Directory extends Node {

    protected DateFormat dateFormat = new SimpleDateFormat("MMMM dd, yyyy, hh:mm:ss a");

    private Hashtable<String, Node> children;

    public Directory(String n) {
        this(new Descriptor(n, true, new Date()), null, new Hashtable<>());
    }

    public Directory(String n, Directory prt) {
        this(new Descriptor(n, true, new Date()), prt, new Hashtable<>());
    }

    public Directory(Descriptor d, Directory prt, Hashtable<String, Node> chd) {
        this.desc = d;
        this.parent = prt;
        this.children = chd;
    }

    public void _addChild(Node ch) {
        String k = ch.toString();
        if (this.children.containsKey(k)) {
            Node old = this.children.get(k);
            this.children.replace(k, old, ch);
        } else {
            this.children.put(k, ch);
        }
    }

    public void _removeChild(String ch) {
        if (this.children.containsKey(ch)) {
            this.children.remove(ch);
        }
    }

    public boolean _contains(String ch) {
        return this.children.containsKey(ch);
    }

    public Hashtable<String, Node> _getChildren() {
        return this.children;
    }

    public CustomFile _getFile(String fname) {
        if (this.children.containsKey(fname)) {
            Node fl = this.children.get(fname);
            if (!fl.isDirectory()) {
                return (CustomFile) fl;
            }
        }
        return null;
    }

    public Node _get(String n) {
        if (this.children.containsKey(n)) {
            Node fl = this.children.get(n);
            return fl;
        }
        return null;
    }

    @Override
    public String _getDetails() {
        StringBuilder sb = new StringBuilder(dateFormat.format(this.desc._dateCreated())).append("\t");
        sb.append("<DIR>").append("\t").append(this.toString());
        return sb.toString();
    }

    @Override
    public boolean isDirectory() {
        return true;
    }

}

class Descriptor {

    private String name;
    private boolean isDirectory;
    private Date dtCreated;

    public Descriptor(String n, boolean isDir, Date d) {
        this.name = n;
        this.isDirectory = isDir;
        this.dtCreated = d;
    }

    public Descriptor(boolean isDir, Date d) {
        this(isDir ? "New Folder" : "New File", isDir, d);
    }

    public void _setName(String n) {
        this.name = n;
    }

    public String _toString() {
        return this.name;
    }

    public Date _dateCreated() {
        return this.dtCreated;
    }
}
