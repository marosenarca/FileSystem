/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Main;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Hashtable;
import java.util.Stack;
import java.util.logging.Logger;

/**
 *
 * @author MaRose
 */
class Tree implements Serializable {

    private static final Logger LOGGER = Logger.getLogger(Tree.class.getName());
    private Directory root, current;

    public Tree() {
        this.root = new Directory("root");
        this.current = this.root;
    }

    public void insert(Node n, Directory prt) {
        n._setParent(prt);
        if (n.isDirectory()) {
            prt._addSubdirectory((Directory) n);
        } else {
            prt._addFile((CustomFile) n);
        }
    }

    public boolean searchDirectory(Directory start, String n, int isDir) {
        switch (isDir) {
            case 1:
                return start._containsSubdirectory(n);
            case 0:
                return (start._containsSubdirectory(n) || start._containsFile(n));
            case -1:
                return start._containsFile(n);
            default:
                return false;
        }
    }

    public ArrayList<String> fullSearch(Directory start, String n, ArrayList<String> arr) {
        Hashtable<String, Directory> dirs = start._getSubdirectories();
        Hashtable<String, CustomFile> fils = start._getFiles();

        for (CustomFile f : fils.values()) {
            if (f._getName().equalsIgnoreCase(n) || f.toString().equalsIgnoreCase(n)) {
                String fp = f._getPath();
                if (!arr.contains(fp)) {
                    arr.add(fp);
                }
            }
        }

        for (Directory d : dirs.values()) {
            if (d.toString().equalsIgnoreCase(n)) {
                String dp = d._getPath();
                if (!arr.contains(dp)) {
                    arr.add(dp);
                }
            }
            arr = fullSearch(d, n, arr);
        }
        return arr;
    }

    public Directory getDirectory(Directory parent, String n) {
        return parent._getSubdirectory(n);
    }

    public void remove(Directory start, String n, int isDir) {
        switch (isDir) {
            case 1:
                if (start._containsSubdirectory(n)) {
                    start._removeSubdirectory(n);
                }
                break;
            case 0:
                remove(start, n, 1);
                remove(start, n, -1);
                break;
            case -1:
                if (start._containsFile(n)) {
                    start._removeFile(n);
                }
                break;
            default:
                break;
        }
    }

    public void removeAll(Directory start, String re) {
        Hashtable<String, CustomFile> tmpc = start._getFiles();
        tmpc.keySet().stream().forEach((s) -> {
            if (s.matches(re)) {
                start._removeFile(s);
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
        ArrayList<String> ch = n._listAll();
        for (String s : ch) {
            System.out.println(s);
        }
    }

    public void listAll(Directory curr, String re) {
        ArrayList<Node> all = new ArrayList<>(curr._getSubdirectories().values());
        all.addAll(curr._getFiles().values());
//        ArrayList<String> ch = new ArrayList<>();

        for (Node n : all) {
            if (n.toString().matches(re)) //                ch.add(n._getDetails());
            {
                System.out.println(n._getDetails());
            }
        }
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

    public boolean equals(Node n) {
        return this.desc.equals(n.desc);
    }

    @Override
    public abstract String toString();
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

    public void _seContent(String s) {
        this.content = s;
    }
    
    public String _getContent() {
        return this.content;
    }

    public Date _getDateModified() {
        return this.modified;
    }

    public String _getType() {
        return "DOC";
    }

    public String _getName() {
        return this.desc.toString();
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

    public String _getExt() {
        return this.type;
    }

    @Override
    public String toString() {
        return this.desc.toString().concat("." + this.type);
    }
}

class Directory extends Node {

    protected DateFormat dateFormat = new SimpleDateFormat("MMMM dd, yyyy, hh:mm:ss a");

    private Hashtable<String, Directory> subdirectories;
    private Hashtable<String, CustomFile> files;

    public Directory(String n) {
        this(new Descriptor(n, true, new Date()), null, new Hashtable<>(), new Hashtable<>());
    }

    public Directory(String n, Directory prt) {
        this(new Descriptor(n, true, new Date()), prt, new Hashtable<>(), new Hashtable<>());
    }

    public Directory(Descriptor d, Directory prt, Hashtable<String, Directory> chd, Hashtable<String, CustomFile> chf) {
        this.desc = d;
        this.parent = prt;
        this.subdirectories = chd;
        this.files = chf;
    }

    public void _addSubdirectory(Directory ch) {
        String k = ch.toString();
        if (this.subdirectories.containsKey(k)) {
            Directory old = this.subdirectories.get(k);
            if (old.equals(ch)) {
                this.subdirectories.replace(k, old, ch);
            } else {
                this.subdirectories.put(k, ch);
            }
        } else {
            this.subdirectories.put(k, ch);
        }
    }

    public void _addFile(CustomFile ch) {
        String k = ch.toString();
        if (this.files.containsKey(k)) {
            CustomFile old = this.files.get(k);
            if (old.equals(ch)) {
                this.files.replace(k, old, ch);
            } else {
                this.files.put(k, ch);
            }
        } else {
            this.files.put(k, ch);
        }
    }

    public void _removeSubdirectory(String ch) {
        if (this.subdirectories.containsKey(ch)) {
            this.subdirectories.remove(ch);
        }
    }

    public void _removeFile(String f) {
        if (this.files.containsKey(f)) {
            this.files.remove(f);
        }
    }

    public void _removeChild(String n, boolean isDir) {
        if (this.subdirectories.containsKey(n) && isDir) {
            this.subdirectories.remove(n);
        } else if (this.files.containsKey(n) && !isDir) {
            this.files.remove(n);
        }
    }

    public boolean _containsSubdirectory(String ch) {
        return this.subdirectories.containsKey(ch);
    }

    public boolean _containsFile(String ch) {
        if (this.files.containsKey(ch)) {
            CustomFile f = this.files.get(ch);
            if (f.toString().equalsIgnoreCase(ch)) {
                return true;
            }
        }
        return false;
    }

    public Hashtable<String, Directory> _getSubdirectories() {
        return this.subdirectories;
    }

    public Hashtable<String, CustomFile> _getFiles() {
        return this.files;
    }

    public CustomFile _getFile(String fname) {
        if (this.files.containsKey(fname)) {
            return this.files.get(fname);
        }
        return null;
    }

    public Directory _getSubdirectory(String n) {
        if (this.subdirectories.containsKey(n)) {
            return this.subdirectories.get(n);
        }
        return null;
    }

    public ArrayList<String> _listAll() {
        ArrayList<String> ret = new ArrayList<>();
        ArrayList<Node> ch = new ArrayList<>(this.subdirectories.values());
        ch.addAll(this.files.values());

        Collections.sort(ch, new Comparator() {
            @Override
            public int compare(Object o1, Object o2) {
                int res = o1.toString().compareTo(o2.toString());
                if (res == 0) {
                    return o1.hashCode() < o2.hashCode() ? -1 : 1;
                } else {
                    return res;
                }
            }
        });

        for (Node n : ch) {
            ret.add(n._getDetails());
        }

        return ret;
    }

    public boolean isEmpty() {
        return (this.files.isEmpty() && this.subdirectories.isEmpty());
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

    @Override
    public String toString() {
        return this.desc.toString();
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

    @Override
    public String toString() {
        return this.name;
    }

    public Date _dateCreated() {
        return this.dtCreated;
    }

    public boolean equals(Descriptor d) {
        return (this.name.equalsIgnoreCase(d.toString()) && (this.isDirectory == d.isDirectory));
    }

}
