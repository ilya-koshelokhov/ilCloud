package coursework.koshelokhov.model;

import java.util.ArrayList;

public class UserGroups implements java.io.Serializable{

    private ArrayList<Integer> groups;

    public UserGroups(int userGroup){
        groups = new ArrayList<>();
        groups.add(userGroup);
    }

    public UserGroups(UserGroups other) throws RuntimeException{
        if(other != null) {
            this.groups = new ArrayList<>(other.groups);
        } else {
            throw new RuntimeException("Parameter in copy constructor of UserGroups is null.");
        }
    }

    public void addGroup(int group){
        if(!groups.contains(group)) {
            groups.add(group);
        }
    }

    public ArrayList<Integer> getGroups(){
        return groups;
    }

    public void removeGroup(int group){
        groups.remove(group);
    }

    public boolean contain(int g){
        return (groups.contains(g));
    }

    @Override
    public String toString(){
        StringBuilder out = new StringBuilder();
        for (Integer it:groups){
            out.append(String.valueOf(it));
            out.append(", ");
        }
        out.delete(out.length() - 2, out.length());
        return out.toString();
    }
}
