package coursework.koshelokhov.model;

import java.io.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Utils {

    public static String hashing(String username, String path){

        String ans = null;

        try {
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            md5.update((username+path).getBytes());
            byte[] digest = md5.digest();
            ans = toChars(digest);
        }
        catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        return ans;
    }

    public static boolean save(FilesystemNode nodeToSave, String fileName) {

        try{
            if (fileName == null || fileName.isEmpty()){
                fileName = "DefaultFilename.serialized";
            }

            ObjectOutputStream objStream = new ObjectOutputStream(new FileOutputStream(fileName));
            objStream.writeObject(nodeToSave);
            objStream.flush();
            objStream.close();

        } catch (IOException e){
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public static FilesystemNode load(String fileName) throws IOException, ClassNotFoundException{

        ObjectInputStream objectInputStream = new ObjectInputStream(new FileInputStream(fileName));

        FilesystemNode result = (FilesystemNode) objectInputStream.readObject();
        objectInputStream.close();

        System.out.println(result);

        return result;
    }

    private static String toChars(byte[] inBytes){

        char[] hexSymbols = "0123456789abcdef".toCharArray();
        char[] ans = new char[inBytes.length * 2];

        for (int i = 0; i < inBytes.length; i++){
            int b = inBytes[i] & 0xFF;
            ans[i *2 ] = hexSymbols[b >>> 4];
            ans[i * 2 + 1] = hexSymbols[b & 0x0f];
        }

        return new String(ans);
    }

}
