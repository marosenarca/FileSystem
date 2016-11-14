/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Main;

import java.util.Hashtable;
import java.util.logging.Level;
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

    public void _insert(Node n) {
        String type = n._getDetails()._isDirectory() ? "directtory" : "file";
        n._setParent(this.current);
        this.current._addChild(n);

        if (n._getDetails()._isDirectory()) {
            this.current = (Directory) n;
        }

//        LOGGER.log(Level.INFO, "Successfully added new {0}: \"{1}\".", new Object[]{type, n._toString()});
    }

    public boolean _search(Directory start, Node n) {
        Hashtable<String, Node> tmpc = start._getChildren();

        if (tmpc.containsKey(n._toString())) {
            return true;
        } else {
            for (String chk : tmpc.keySet()) {
                Node ch = tmpc.get(chk);
                if (ch._getDetails()._isDirectory()) {
                    return _search((Directory) ch, n);
                }
            }
        }
        return false;
    }

    public void _delete(Directory start, Node n) {
        Hashtable<String, Node> tmpc = start._getChildren();

        if (tmpc.containsKey(n._toString())) {
            start._removeChild(n);
        } else {
            for (String chk : tmpc.keySet()) {
                Node ch = tmpc.get(chk);
                if (ch._getDetails()._isDirectory()) {
                    _delete((Directory) ch, n);
                }
            }
        }
    }

}

class Node {

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

    public Descriptor _getDetails() {
        return this.desc;
    }

    public String _toString() {
        return this.desc._toString();
    }
}

class File extends Node {
    //
}

class Directory extends Node {

    private Hashtable<String, Node> children;

    public Directory(String n) {
        this(new Descriptor(n, true), null, new Hashtable<>());
    }

    public Directory(String n, Node prt) {
        this(new Descriptor(n, true), prt, new Hashtable<>());
    }

    public Directory(Descriptor d, Node prt, Hashtable<String, Node> chd) {
        this.desc = d;
        this.parent = prt;
        this.children = chd;
    }

    public void _addChild(Node ch) {
        String k = ch._toString();
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

    public Descriptor(String n, boolean isDir) {
        this.name = n;
        this.isDirectory = isDir;
    }

    public Descriptor(boolean isDir) {
        this.isDirectory = isDir;
        this.name = isDir ? "New Folder" : "New File";
    }

    public String _toString() {
        return this.name;
    }

    public boolean _isDirectory() {
        return this.isDirectory;
    }

}
