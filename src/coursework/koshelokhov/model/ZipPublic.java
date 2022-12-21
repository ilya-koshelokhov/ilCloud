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

public class ZipPublic{
    List<String> fileList = new ArrayList<String>();
    String fileUploadPath;
    public ZipPublic(String fileUploadPath){

        this.fileUploadPath = fileUploadPath;
    }

    public ByteArrayOutputStream zip(String dirPath, FilesystemNode publicRoot, String basePath){

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        String[] splittedDirPath = dirPath.split("/");
        String targetDirRootName = splittedDirPath[splittedDirPath.length -1];

        FilesystemNode root = publicRoot.getChild("public");
        System.out.println("ZipPublic::zip:"+dirPath);
        HashMap<String, FilesystemNode> childs = root.getChildrenMap();
        System.out.println("ZipPublic::zip::root content:");
        for (String filename: childs.keySet()){
            System.out.println("ZipPublic::zip::entry: "+filename);
        }
        FilesystemNode target = getTargetDir(dirPath, root);
        readFilesInDir(target);
        String username = target.getFullName().split("/")[1];
        String localPathoToDir = target.getFullName().substring(username.length()+1);
        String pathToUserDir = fileUploadPath + "/" + username + "/files";
        String filePrefix = pathToUserDir;

        String zipDirName = splittedDirPath[splittedDirPath.length-1];

        System.out.println("ZipPublic::zip::Username: "+username);
        System.out.println("ZipPublic::zip::localPathoToDir: "+localPathoToDir);
        System.out.println("ZipPublic::zip::Path to user dir: "+pathToUserDir);
        System.out.println("ZipPublic::zip::Root directory name inside zip: "+zipDirName);
        System.out.println("ZipPublic::zip::Found entries: ");
        try{
            ZipOutputStream zos = new ZipOutputStream(bos);
            for (String name: fileList){
                String pathToFile = fileUploadPath+"/"+username+"/files"+name.substring(username.length()+1);
                String savePath = targetDirRootName+"/" + name.substring(localPathoToDir.length()+1+username.length()+1);
                System.out.println("Path to file: "+ pathToFile);
                System.out.println("Files: "+pathToFile+"\tSave as "+savePath);
                addFileToZip(pathToFile, zos, savePath);
            }
            zos.close();
            bos.close();
        } catch (Exception e){
            e.printStackTrace();
        }
        
        
        return bos;
    }


    FilesystemNode getTargetDir(String dirPath, FilesystemNode root){
        String [] splitPath = dirPath.split("/");
        System.out.print("ZipShared::getTargetDir:: path check: ");
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
        if (root.isFile()){
            fileList.add(root.getFullName());
            return;
        }
        HashMap<String, FilesystemNode> childs = root.getChildrenMap();
        for (String filename : childs.keySet()){
            FilesystemNode file = childs.get(filename);
            if (file.isFile()){
                fileList.add(file.getFullName());
            } else {
                fileList.add(file.getFullName()+"/");
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
