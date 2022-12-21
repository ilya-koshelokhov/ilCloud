package coursework.koshelokhov.control;

import coursework.koshelokhov.model.FilesystemNode;
import coursework.koshelokhov.model.Utils;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.FileNotFoundException;
import java.util.Enumeration;

public class Download extends HttpServlet {

    private String fileUploadPath;

    @Override
    public void init(ServletConfig config) throws ServletException {
        fileUploadPath = config.getServletContext().getInitParameter("FILE_UPLOAD_PATH");
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        String filename             = request.getParameter("file");
        String username             = (String) request.getSession().getAttribute("username");
        Enumeration<String> params  = request.getParameterNames();

        boolean isShared = false;
        while (params.hasMoreElements()){
            if (params.nextElement().equals("shared")){
                isShared = true;
                filename = filename.substring(7);
                System.out.println("Download:shared == TRUE, path == " + filename);
                break;
            }
        }

        String userStr= filename.split("/")[1];
        String userPath = filename.substring(userStr.length() + 1);
        System.out.println("Download reconstruct check user: " + userStr);
        System.out.println("Download reconstruct check userPath: " + userPath);
        String fullPath = fileUploadPath+"/"+userStr+"/files" + userPath;

        if (filename == null)
            System.out.println("Download::ERROR file null");
        else
            System.out.println("Download:DOGET:requested Download for " + fullPath);

        if (username == null)
            System.out.println("DOWNLOAD:DOGET:ERROR null username");
        else
            System.out.println("DOWNLOAD:DOGET:request for file download from: "+ username);

        FilesystemNode root = null;
        if (isShared){
            try {
                root = Utils.load(fileUploadPath + "/" + username + "/shared.serialized");
                if(root != null){

                    for (String shared : filename.split("/")){
                        if(!shared.equals("/") && !shared.isEmpty()){
                            root = root.getChild(shared);
                        }
                    }

                    fullPath = fileUploadPath+"/"+userStr+"/files" + root.getFullName().substring(userStr.length() + 1);
                    System.out.println("DOWNLOAD::DO_GET::correct full path is: " + fullPath);
                    root = Utils.load(fileUploadPath + "/" + userStr + "/tree.serialized");
                }
                else {
                    System.out.println("All goes fuck!");
                }

            }
            catch (ClassNotFoundException e) {
                e.printStackTrace();
            }

            System.out.println("USER WHO SHARE:"  +userStr);
        }
        else{
            root = (FilesystemNode)request.getSession().getAttribute("tree");
        }


        assert root != null;
        if (root.getChild(username) == null)
            System.out.println("DOWNLOAD:DOGET:ERROR root: null username");
        else
            System.out.println("DOWNLOAD:DOGET:treeCheck:"+root.getName());
        File file = null;
        FileInputStream inStream = null;
        OutputStream outStream = null;
        try{
            file = new File(fullPath);
            if (file == null)
                System.out.println("DOWNLOAD:DOGET:file not found:" + fullPath);

            inStream = new FileInputStream(file);
            String mimeType = "application/octet-stream";
            response.setContentType(mimeType);
            response.setContentLength((int) file.length());

            String headerKey = "Content-Disposition";
            String headerValue = String.format("attachment; filename=\"%s\"", file.getName());
            response.setHeader(headerKey, headerValue);

            outStream = response.getOutputStream();

            byte[] buffer = new byte[4096];
            int bytesRead = -1;

            while ((bytesRead = inStream.read(buffer)) != -1) {
                outStream.write(buffer, 0, bytesRead);
            }
        } catch (FileNotFoundException e){

            response.setStatus(404);
            response.getWriter().println("File not found");

        } finally {
            inStream.close();
            outStream.close();
        }

    }

}
