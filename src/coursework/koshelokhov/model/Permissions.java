package coursework.koshelokhov.model;

public class Permissions implements java.io.Serializable{ //rwx    r:100 - 4;   w:010 - 2; rw:110 - 6;       rwx:111 -7;
    private boolean type;   // File==true, directory==false
    private UserGroups groups;
    private final int ownerId;
    private int owner;      //7
    private int group;      //4
    private int other;      //0

    public Permissions(boolean isFile, int owner, int group, int other, UserGroups groups, int uid){

        this.type = isFile;
        this.owner = owner;
        this.group = group;
        this.other = other;
        this.groups = groups;
        ownerId = uid;
    }

    public Permissions(int uid){

        this(false, 7, 4, 0, new UserGroups(uid), uid);
    }

    public Permissions(Permissions other) throws RuntimeException {
        if(other != null) {
            this.type = other.type;
            this.owner = other.owner;
            this.group = other.group;
            this.other = other.other;
            this.ownerId = other.ownerId;
            this.groups = new UserGroups(other.groups);
        } else {
            throw new RuntimeException("Parameter in copy constructor of Permissions is null.");
        }
    }

    public void setIsFile(){ this.type = true; }

    public void setIsDirectory(){ this.type = false; }

    public void addGroup(String group){
        int val = 0;
        try{
            val = Integer.parseInt(group);
        }
        catch (NumberFormatException ex){
            ex.printStackTrace();
        }
        groups.addGroup(val);
    }

    public void addGroupWritable(int g){ groups.addGroup(g); }

    public void removeGroup(int group) {groups.removeGroup(group);}

    public void setOwner(int owner) { this.owner = owner; }

    public void setGroup(int group) { this.group = group; }

    public void setOther(int other) { this.other = other; }

    public boolean isFile() {return this.type;}

    public String getTypeString() { return type ? "F" : "D"; }

    public int getOwner() { return owner;}

    public int getGroup() {return group;}

    public int getOther() {return other;}

    public static String procMask(int rwx){
        StringBuilder bf = new StringBuilder();
        bf.append(((rwx & 4) == 4) ? "r" : "-");
        bf.append(((rwx & 2) == 2) ? "w" : "-");
        bf.append(((rwx & 1) == 1) ? "x" : "-");
        return bf.toString();
    }

    public void setGroupWritable(){ group |= 2; }

    public boolean isGroupWritable(int g){

        if (groups.contain(g)){
            System.out.println("ISGROUPWRITABLE: groups contains "+g);
            return true;
        }
        return false;
    }

    @Override
    public String toString(){
        StringBuilder bf = new StringBuilder();
        bf.append(procMask(owner));
        bf.append(" ");
        bf.append(procMask(group));
        bf.append(" ");
        bf.append(procMask(other));
        bf.append(" ");
        bf.append("User: ");
        bf.append(ownerId);
        bf.append(" ");
        bf.append("Groups: ");
        bf.append(groups.toString());
        return bf.toString();
    }
}



