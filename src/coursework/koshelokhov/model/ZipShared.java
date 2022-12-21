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

public class ZipShared{
    List<String> fileList = new ArrayList<String>();
    List<String> rootFileListPrefixes = new ArrayList<String>();
    public ByteArrayOutputStream zipDir(String dirPath, FilesystemNode root, String fileUploadPath){

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        String targetSharedDirName = dirPath.substring("/shared".length());
        String[] splittedDirPath = dirPath.split("/");
        String targetDirRootName = splittedDirPath[splittedDirPath.length -1];
        String username = targetSharedDirName.split("/")[1];
        String pathToUserDir = fileUploadPath + "/" + username + "/files";
        String filePrefix = pathToUserDir;
        System.out.println("ZipShared::zipDir::targetSharedDirName length: "+targetSharedDirName.split("/").length);
        FilesystemNode target = getTargetDir(targetSharedDirName, root);
        if (targetSharedDirName.split("/").length == 2){
            System.out.println("ZipShared::zipDir::All shared files requested ");
            HashMap<String, FilesystemNode> childs = root.getChild(username).getChildrenMap();
            filePrefix += childs.entrySet().iterator().next().getValue().getFullName().substring(username.length()+1);
            for (String filename: childs.keySet()){
                System.out.println("ZipShared::zipDit::fullfilename for "+filename+": "+childs.get(filename).getFullName());
                System.out.println("ZipShared::zipDit::sharedfullfilename for "+filename+": "+childs.get(filename).getSharedFullName());
                FilesystemNode child = childs.get(filename);
                String childFullName = child.getFullName();
                readFilesInDir(child);
                rootFileListPrefixes.add(childFullName.substring(username.length()+1));
            }
            System.out.println("ZipShared::zipDit::Prefixes:");
            for (String s: rootFileListPrefixes){
                System.out.println(s);
            }
            System.out.println("ZipShared::zipDit::filenames:");
            for (String s: fileList){
                System.out.println("\t"+s);
            }

        } else{
            System.out.println("ZipShared::zipDir::target fullname: "+target.getFullName());
            readFilesInDir(target);
            filePrefix += target.getFullName().substring(username.length()+1);
        }
        System.out.println("ZipShared::zipDir::targetSharedDirName: "+targetSharedDirName);

        System.out.println("ZipShared::zipDir::target fullname: "+target.getFullName());

        System.out.println("ZipShared::zipDir::file prefix: "+filePrefix);
        System.out.println("ZipShared::zipDir::files in target dir: ");
        try{
            ZipOutputStream zos = new ZipOutputStream(bos);

            for (String name : fileList){
                System.out.println("\t"+name);
            }
            System.out.println("ZipShared::zipDir::files in target dir with fulll path: ");
            for (String name : fileList){
                String pathToFile = fileUploadPath+"/"+username+"/files"+name.substring(username.length()+1);
                String savePath = targetDirRootName+"/" + pathToFile.substring(filePrefix.length()+1);
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
            System.out.println(filename);
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
