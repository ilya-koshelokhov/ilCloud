package coursework.koshelokhov.model;

import coursework.koshelokhov.model.Permissions;

import java.util.HashMap;

public class FilesystemNode implements java.io.Serializable {

    private FilesystemNode parent;
    private final String name;
    private Permissions permissions;
    private HashMap<String, FilesystemNode> children;

    private FilesystemNode sharedParent;
    private FilesystemNode publicParent;


    public FilesystemNode(String name, int uid) {

        this.name = name;
        this.permissions = new Permissions(uid);

        this.parent = null;
        this.sharedParent = null;
        this.publicParent = null;
        this.children = new HashMap<>();
    }

    public FilesystemNode(String name, String uid) {

        this.name = name;
        this.parent = null;
        this.sharedParent = null;
        this.publicParent = null;
        this.children = new HashMap<>();

        try {
            this.permissions = new Permissions(Integer.parseInt(uid));
        } catch (NumberFormatException e) {
            e.printStackTrace();
            this.permissions = new Permissions(0);
        }

    }

    // Copy constructor
    public FilesystemNode(FilesystemNode other) {

        if (other != null) {
            this.name = other.name;
            this.permissions = new Permissions(other.permissions);
            this.parent = other.parent;
            this.sharedParent = other.sharedParent;
            this.publicParent = other.publicParent;
            this.children = new HashMap<>();
            for (FilesystemNode node : other.children.values()) {
                FilesystemNode insert = new FilesystemNode(node);
                insert.setParent(this);
                this.children.put(insert.name, insert);
            }
        } else {
            throw new RuntimeException("Parameter in copy constructor of FilesystemNode is null.");
        }
    }

    public void setFile() {
        permissions.setIsFile();
    }

    public void setDirectory() {
        permissions.setIsDirectory();
    }

    public boolean isFile() {
        return permissions.isFile();
    }

    public void setParent(FilesystemNode parent) {
        this.parent = parent;
    }

    public void setPublicParent(FilesystemNode parent) {
        this.publicParent = parent;
    }

    public void setSharedParent(FilesystemNode parent) {
        this.sharedParent = parent;
    }

    public Permissions getPermissions() {
        return permissions;
    }

    public HashMap<String, FilesystemNode> getChildrenMap() {
        return new HashMap<>(this.children);
    }

    public FilesystemNode getParent() {
        return parent;
    }

    public String getName() {
        return name;
    }

    public String getFullName() {

        StringBuilder sb = new StringBuilder();

        FilesystemNode parent = this;

        while (parent != null && parent.getParent() != null) {
            sb.insert(0, parent.getName());
            sb.insert(0, "/");
            parent = parent.getParent();
        }

        return sb.toString();
    }

    public void addChild(FilesystemNode child) {

        if (child != null && !children.containsKey(child.getName()) && !this.isFile()) {
            child.setParent(this);
            children.put(child.getName(), child);
        }
    }

    public void addPublicChild(FilesystemNode child) {
        if (child != null && !children.containsKey(child.getName()) && !this.isFile()) {
            child.setPublicParent(this);
            children.put(child.getName(), child);
        }
    }

    public void addSharedChild(FilesystemNode child) {
        if (child != null && !children.containsKey(child.getName()) && !this.isFile()) {
            child.setSharedParent(this);
            children.put(child.getName(), child);
        }
    }

    public void removeChild(String childName) {
        children.remove(childName);
    }

    public FilesystemNode getChild(String childName) {
        return children.get(childName);
    }

    public int ChildrenSize() {
        int count = 0;
        for (FilesystemNode child : children.values()) {
            if (!child.isFile())
                count++;
        }
        return count;
    }

    public void printToHTML(StringBuffer buf, String prefix, String childrenPrefix) {
        if (buf == null) {
            return;
        }
        int c = 0;
        buf.append(prefix);
        buf.append(name);
        buf.append(" ");

        buf.append(isFile() ? "F " : "D ");
        buf.append(permissions.toString());
        buf.append(" ");
        buf.append("<br>");

        for (FilesystemNode it : children.values()) {

            c += 1;

            if (c >= children.size()) {
                it.printToHTML(buf, childrenPrefix + "&#9492;&#9472;&#9472;", childrenPrefix + "&#160;&#160;&#160;");  //└──
            } else {
                it.printToHTML(buf, childrenPrefix + "&#9500;&#9472;&#9472;", childrenPrefix + "&#9474;&#160;&#160;"); //├──  │
            }

        }
    }

    public void printToTerminal(StringBuffer buf, String prefix, String childrenPrefix) {

        if (buf == null) {
            buf = new StringBuffer();
        }

        int c = 0;

        buf.append(prefix);
        buf.append(name);
        buf.append(" ");
        buf.append(permissions.getTypeString());
        buf.append("\n");

        for (FilesystemNode it : children.values()) {

            c += 1;

            if (c >= children.size()) {
                it.printToTerminal(buf, childrenPrefix + "└──", childrenPrefix + "   ");  //└──
            } else {
                it.printToTerminal(buf, childrenPrefix + "├──", childrenPrefix + "│  "); //├──  │
            }

        }
    }

    public FilesystemNode findNode(String relativePath) {

        String[] splitPath = relativePath.split("/");
        FilesystemNode target = this;

        for (String it : splitPath) {

            if (!it.equals("/") && !it.isEmpty()) { 

                target = target.getChild(it);
                System.out.println("PROC: " + it);
                if (target == null) {
                    return null;
                }
            }
        }
        return target;
    }

    @Override
    public String toString() {
        StringBuffer buf = new StringBuffer(50);
        printToHTML(buf, "", "");
        return buf.toString();
    }

    public String toStringTerminal() {
        StringBuffer buf = new StringBuffer(50);
        printToTerminal(buf, "", "");
        return buf.toString();
    }

    public FilesystemNode getSharedParent() {
        return sharedParent;
    }

    public FilesystemNode getPublicParent() {
        return publicParent;
    }

    public String getSharedFullName() {
        FilesystemNode par = this;
        StringBuffer sb = new StringBuffer();

        while (par != null && par.getSharedParent() == null) {
            sb.insert(0, par.getName());
            sb.insert(0, "/");
            par = par.getParent();
        }
        while (par != null && par.getSharedParent() != null) {
            sb.insert(0, par.getName());
            sb.insert(0, "/");
            par = par.getSharedParent();
        }
        return sb.toString();
    }

    public String getPublicFullName() {

        FilesystemNode par = this;
        if (getParent() == null || getParent().getParent() == null) {  //
            System.out.println("Null parent, return: /public");
            return "/public";
        }

        StringBuilder sb = new StringBuilder();
        sb.append("/");
        sb.append(getName());

        while (par.getPublicParent() == null) {
            par = par.getParent();
            sb.insert(0, par.getName());
            sb.insert(0, "/");

            System.out.println("PFN: " + par.getName());
        }

        while (par.getPublicParent() != null) {
            par = par.getPublicParent();
            sb.insert(0, par.getName());
            sb.insert(0, "/");

            System.out.println("PPFN: " + par.getName());
        }

        System.out.println("GetPublicFullName" + sb);
        return sb.toString();
    }
}
