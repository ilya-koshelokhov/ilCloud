package coursework.koshelokhov.model;


public class Checker {
    public static boolean isClean(String s){
        if (s == null){
            return false; //////
        }
        for(char c : s.toCharArray()){
            if(!(Character.toString(c).toLowerCase()).matches("[a-zA-Z0-9]")){
                return false;
            }
        }
        return true;
    }

    public static boolean isCleanPassword(String s){
        for(char c : s.toCharArray()){
            if(!(Character.toString(c).toLowerCase()).matches("[a-zA-Z0-9!@#$%\\^&*+]")){
                return false;
            }
        }
        return true;
    }
}
