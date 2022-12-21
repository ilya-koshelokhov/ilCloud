package coursework.koshelokhov.control;

import coursework.koshelokhov.model.FilesystemNode;
import coursework.koshelokhov.model.Utils;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import java.io.File;
import java.io.IOException;
import java.util.Enumeration;

public class Create extends HttpServlet {

    private String basePath;
    private FilesystemNode user;

    @Override
    public void init(ServletConfig config) throws ServletException {
        basePath = config.getServletContext().getInitParameter("FILE_UPLOAD_PATH");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        String creating = request.getParameter("creating");
        if(creating != null && creating.equals("file")){
            createFile(request, response);
        }
        else if(creating != null && creating.equals("directory")){
            createDirectory(request, response);
        }
        else {
            System.out.println("Create::doPost::Wrong request: parameter $creating$ == " + creating);
        }

    }

    private void createFile(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {

        String pathToUpload = request.getParameter("path");
        Enumeration<String> params = request.getParameterNames();
        boolean isShared = false;

        String sharedArg = request.getParameter("shared");
        if (sharedArg != null ){
            isShared = true;
            pathToUpload = pathToUpload.substring("/shared".length());
            System.out.println("Create::CreateFile:: uploading shared file == TRUE");
        }

        System.out.println("Create::CreateFile:: NEW FILE PATH:" + pathToUpload);

        Part filePart = request.getPart("file");
        if (filePart == null){
            System.out.println("Create::CreateFile:: NO FILE PARTS!!!!!1");
            return;
        }
        String fileName = filePart.getSubmittedFileName();
        if (fileName == null || fileName.isEmpty()){
            System.out.println("Create::CreateFile:: No filename");
            return;
        }
        String currentUser = (String)request.getSession().getAttribute("username");

        int currentUid = (int)request.getSession().getAttribute("uid");
        String targetUserStr = pathToUpload.split("/")[1];
        String relativePath = pathToUpload.substring(targetUserStr.length()+1);
        System.out.println("Create::CreateFile::Current user name: "+currentUser);
        System.out.println("Create::CreateFile::Relative file path: "+relativePath);


        String fullPathToNewFile  = basePath +"/"+targetUserStr + "/files" + relativePath + "/" + fileName;
        System.out.println("Create::CreateFile::Full path to newfile" + fullPathToNewFile);
        try{
            if (isShared){
                FilesystemNode targetRoot = Utils.load(basePath+"/"+targetUserStr+"/tree.serialized");
                FilesystemNode personalRoot = Utils.load(basePath+"/"+currentUser+"/shared.serialized");
                System.out.println("Create::CreateFile::Target root check:"+targetRoot.toStringTerminal());
                System.out.println("Create::CreateFile::Personal root check:"+personalRoot.toStringTerminal());
                FilesystemNode personalDir = getTargetDir(pathToUpload, personalRoot);
                String originalPath = personalDir.getFullName();
                if (originalPath == null || originalPath.isEmpty()){
                    originalPath = "/"+targetUserStr+"/";
                }
                System.out.println("Create::CreateFile::target fullName: "+personalDir.getFullName());
                System.out.println("Create::CreateFile::target file original path"+originalPath);
                FilesystemNode targetDir = getTargetDir(originalPath, targetRoot);
                fullPathToNewFile = basePath + "/" + targetUserStr + "/files" + originalPath.substring(targetUserStr.length()+1) +"/"+fileName;
                System.out.println("Create::CreateFile::Full path to newFile was changed to: "+fullPathToNewFile);

                System.out.println("Create::CreateFile::Target dir check:"+targetDir.toStringTerminal());
                System.out.println("Create::CreateFile::Personal dir check:"+personalDir.toStringTerminal());

                FilesystemNode newFile = targetDir.getChild(fileName);
                boolean dirWritable = targetDir.getPermissions().isGroupWritable(currentUid);


                if (newFile == null){
                    if (dirWritable){
                        newFile = new FilesystemNode(fileName, currentUid);
                        newFile.setFile();
                    } else {
                        System.out.println("Create::CreateFile:Can't create file. Dir is not writable for: "+currentUid);
                        response.setStatus(400);
                        return;
                    }
                } else {
                    boolean fileWritable = newFile.getPermissions().isGroupWritable(currentUid);
                    if (!fileWritable){
                        System.out.println("Create::CreateFile:Can't update file. File is not writable for: "+currentUid);
                        response.setStatus(400);
                        return;
                    }
                }
                targetDir.addChild(newFile);
                personalDir.addChild(newFile);
                filePart.write(fullPathToNewFile);
                Utils.save(personalRoot, basePath+"/"+currentUser+"/shared.serialized");
                Utils.save(targetRoot, basePath+"/"+targetUserStr+"/tree.serialized");
            

            } else {
                HttpSession session = request.getSession();
                FilesystemNode targetRoot = (FilesystemNode) session.getAttribute("tree");
                if (targetRoot == null){
                    System.out.println("Create::CreateFile::R O O T  I S  N U L L");
                }


                System.out.println("Create::CreateFile::Target root check:"+targetRoot.toStringTerminal());
                FilesystemNode targetDir = getTargetDir(pathToUpload, targetRoot);
                System.out.println("Create::CreateFile::Target dir check:"+targetDir.toStringTerminal());

                FilesystemNode newFile = targetDir.getChild(fileName);
                if (newFile == null){
                    newFile = new FilesystemNode(fileName, currentUid);
                    newFile.setFile();
                }
                targetDir.addChild(newFile);
                filePart.write(fullPathToNewFile);
                System.out.println("Create::CreateFile::Target dir check:"+targetDir.toStringTerminal());
                Utils.save(targetRoot, basePath+"/"+currentUser+"/tree.serialized");
                FilesystemNode rootCheck  = (FilesystemNode) request.getSession(false).getAttribute("root");
                System.out.println("Create::CreateFile::Target dir check:"+targetDir.toStringTerminal());
            }
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    private void createDirectory(HttpServletRequest request, HttpServletResponse response) {

        String username = (String) request.getSession().getAttribute("username");
        if (username == null) {
            System.out.println("CREATE:DOGET:ERRORR null username");
        } else {
            System.out.println("CREATE:DOGET:request for DIR Creation from: " + username);
        }
        user = (FilesystemNode) request.getSession().getAttribute("tree");
        if (user == null) {
            System.out.println("CREATE:DOGET:ERRORR user: null username");
        } else {
            System.out.println("CREATE:DOGET:treeCheck:" + user.getName());
        }
        int uid = (int) request.getSession().getAttribute("uid");
        if (uid == 0) {
            System.out.println("CREATE:DOGET:ERRORR null uid");
        } else {
            System.out.println("CREATE:DOGET:UID FOR DIR: " + String.valueOf(uid));
        }

        response.setContentType("text/html");

        String path = request.getParameter("path");
        if (path == null) {
            System.out.println("ERROR:CREATE PATH IS NULL");
        }
        else{

            if (path.isEmpty()) {
                System.out.println("Empty path was fixed");
                path = "/";
            }

            String dirname = request.getParameter("dirname");
            if (dirname == null || dirname.isEmpty()) {
                dirname = "/";
            }


            FilesystemNode target = user.findNode(path);
            FilesystemNode child = new FilesystemNode(dirname, uid);
            target.addChild(child);

            String userStr = path.split("/")[1];
            String userPath = path.substring(userStr.length() + 1);

            System.out.println("DIRPath reconstruct check user: " + userStr);
            System.out.println("DIRPath reconstruct check userPath: " + userPath);

            String fullPath = basePath + "/" + userStr + "/files" + userPath + "/" + dirname;

            System.out.println("CREATING DIR: " + fullPath);
            System.out.format("CREATE::DOPOST:CReating dir with uid %s\n", uid);

            new File(fullPath).mkdirs();

            System.out.println("New folder created:" + child.getFullName());
            System.out.println(target.toStringTerminal());

            Utils.save(user, basePath + "/" + username + "/tree.serialized");
        }
    }
     
    FilesystemNode getTargetDir(String dirPath, FilesystemNode root){
        String [] splitPath = dirPath.split("/");
        System.out.print("Create::getTargetDir:: path check: ");
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

}


