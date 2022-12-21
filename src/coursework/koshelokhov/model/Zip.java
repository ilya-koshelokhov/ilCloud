package coursework.koshelokhov.model;

import coursework.koshelokhov.model.FilesystemNode;

import java.util.zip.*;
import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.io.File;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.FileInputStream;
import java.io.IOException;

public class Zip{
    List<String> fileList = new ArrayList<String>();
    public ByteArrayOutputStream zipDir(String dirPath, FilesystemNode root, String fileUploadPath){

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        FilesystemNode target = getTargetDir(dirPath, root);

        String userStr = target.getFullName().split("/")[1];
        String pathToUserFolder = fileUploadPath + "/"+userStr +"/files";
        String[] splittedDirPath = dirPath.split("/");
        String zipDirName = splittedDirPath[splittedDirPath.length - 1];

        System.out.println("Zip::ZipDir::request dir path: "+dirPath);
        System.out.println("Zip::ZipDir:: target full path:" + target.getFullName());
        System.out.println("Zip::ZipDir:username: "+userStr);
        System.out.println("Zip::ZipDir:user folder: "+pathToUserFolder);
        System.out.println("Zip::ZipDir:zipDirName: "+zipDirName);
        System.out.println("Zip::ZipDir:fileList:");

        readFilesInDir(target);
        try{
            ZipOutputStream zos = new ZipOutputStream(bos);
            for (String file : fileList){
                System.out.println("\t"+file);

                String relativePath = file.substring(dirPath.length()+1);
                String absolutePath = pathToUserFolder+dirPath.substring(userStr.length()+1)+"/"+relativePath;
                String localFilePath = zipDirName+"/"+file.substring(dirPath.length()+1);
                System.out.println("File: "+absolutePath);
                System.out.println("Save as: "+localFilePath);
                addFileToZip(absolutePath, zos, localFilePath);
            }
            zos.close();
            bos.close();
        } catch (IOException e){
            e.printStackTrace();
        }
        return bos;
    }

    

    FilesystemNode getTargetDir(String dirPath, FilesystemNode root){
        String [] splitPath = dirPath.split("/");
        System.out.print("Zip::getTargetDir:: path check: ");
        for (String string : splitPath) {
            System.out.print(string+" / ");
        }
        System.out.println();

        FilesystemNode target = root;
        for (String it : splitPath){
            if(!it.equals("/") && !it.isEmpty()){
                target = target.getChild(it);
            }
        }
        return target;
    }

    private void readFilesInDir(FilesystemNode root){
        HashMap<String, FilesystemNode> childs = root.getChildrenMap();
        for (String filename : childs.keySet()){
            FilesystemNode file = childs.get(filename);
            if (file.isFile()){
                System.out.println("F: "+file.getFullName());
                fileList.add(file.getFullName());
            } else {
                fileList.add(file.getFullName()+"/");
                System.out.println("D: "+file.getFullName()+"/");
                readFilesInDir(file);
            }
        }

    }


    private void addFileToZip(String filePath, ZipOutputStream zos, String localFilePath) throws IOException{

        File f = new File(filePath);
        ZipEntry ze = new ZipEntry(localFilePath);
        zos.putNextEntry(ze);
        if (!f.isDirectory()){
            FileInputStream fis = new FileInputStream(f);
            byte[] fileBuffer = new byte [128];
            int buffLen;
            while ((buffLen = fis.read(fileBuffer)) > 0){
                zos.write(fileBuffer, 0, buffLen);
            }
            fis.close();
        }
    }
}
