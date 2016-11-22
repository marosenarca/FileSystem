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
import java.util.logging.Logger;

/**
 *
 * @author MaRose
 */
class Tree {

    private static final Logger LOGGER = Logger.getLogger(Tree.class.getName());
    Directory root, current;

    public Tree() {
        this.root = new Directory("root");
        this.current = this.root;
    }

    public void insert(Node n) {
        String type = n._getDescriptor()._isDirectory() ? "directtory" : "file";
        n._setParent(this.current);
        this.current._addChild(n);

        if (n._getDescriptor()._isDirectory()) {
            this.current = (Directory) n;
        }
    }

    public boolean search(Directory start, String n) {
        Hashtable<String, Node> tmpc = start._getChildren();

        for (String chk : tmpc.keySet()) {
            if (chk.equalsIgnoreCase(n)) {
                return true;
            }

            Node ch = tmpc.get(chk);
            if (ch._getDescriptor()._isDirectory()) {
                if (ch.toString().equalsIgnoreCase(n)) {
                    return true;
                }
                return search((Directory) ch, n);
            }
        }
        return false;
    }

    public Node getNode(Directory parent, String n) {
        Hashtable<String, Node> tmpc = parent._getChildren();
        for (String chk : tmpc.keySet()) {
            if (chk.equalsIgnoreCase(n)) {
                return tmpc.get(chk);
            }

            Node ch = tmpc.get(chk);
            if (ch._getDescriptor()._isDirectory()) {
                if (ch.toString().equalsIgnoreCase(n)) {
                    return ch;
                }
                return getNode((Directory) ch, n);
            }
        }

        return null;
    }

    public void remove(Directory start, Node n) {
        Hashtable<String, Node> tmpc = start._getChildren();

        if (tmpc.containsKey(n.toString())) {
            start._removeChild(n);
        } else {
            for (String chk : tmpc.keySet()) {
                Node ch = tmpc.get(chk);
                if (ch._getDescriptor()._isDirectory()) {
                    remove((Directory) ch, n);
                }
            }
        }
    }

    public Node getCurrentNode() {
        return this.current;
    }
}

class Node {

    protected DateFormat dateFormat = new SimpleDateFormat("MMMM dd, yyyy, hh:mm:ss a");
    protected Descriptor desc;
    protected Node parent;

    public void _setParent(Node prt) {
        this.parent = prt;
    }

    public void _setDetails(Descriptor desc) {
        this.desc = desc;
    }

    public Node _getParent() {
        return this.parent;
    }

    public Descriptor _getDescriptor() {
        return this.desc;
    }

    public Date _getDateCreated() {
        return this.desc._dateCreated();
    }

    @Override
    public String toString() {
        return this.desc._toString();
    }
}

class File extends Node {

    private String content;
    private Date modified;

    public File(Node prt, String n, String con) {
        this(new Descriptor(n, false, new Date()), prt, new Date(), con);
    }

    public File(Descriptor d, Node prt, Date mf, String c) {
        this.desc = d;
        this.parent = prt;
        this.content = c;
    }

    public String _getContent() {
        return this.content.isEmpty() ? "This file is empty" : this.content;
    }

    public Date _getDateModified() {
        return this.modified;
    }
}

class Directory extends Node {

    private Hashtable<String, Node> children;

    public Directory(String n) {
        this(new Descriptor(n, true, new Date()), null, new Hashtable<>());
    }

    public Directory(String n, Node prt) {
        this(new Descriptor(n, true, new Date()), prt, new Hashtable<>());
    }

    public Directory(Descriptor d, Node prt, Hashtable<String, Node> chd) {
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

    public void _removeChild(Node ch) {
        if (this.children.contains(ch)) {
            this.children.remove(ch);
        }
    }

    public Hashtable<String, Node> _getChildren() {
        return this.children;
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

    public String _toString() {
        return this.name;
    }

    public boolean _isDirectory() {
        return this.isDirectory;
    }

    public Date _dateCreated() {
        return this.dtCreated;
    }
}
