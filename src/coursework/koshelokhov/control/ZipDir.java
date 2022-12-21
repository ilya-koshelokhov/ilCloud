package coursework.koshelokhov.control;

import coursework.koshelokhov.model.FilesystemNode;
import coursework.koshelokhov.model.Utils;
import coursework.koshelokhov.model.Zip;
import coursework.koshelokhov.model.ZipShared;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import java.io.File;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Enumeration;

public class ZipDir extends HttpServlet {

    private String basePath;
    private FilesystemNode user;

    @Override
    public void init(ServletConfig config) throws ServletException {
        basePath = config.getServletContext().getInitParameter("FILE_UPLOAD_PATH");
    }

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        HttpSession session = request.getSession();


        FilesystemNode root = (FilesystemNode) session.getAttribute("tree");
        String username  = (String) request.getSession().getAttribute("username");

        if (root == null){
            System.out.println("ZipDir::doGet:: root is NULL !!!");
            return;
        }
        System.out.println("ZipDir::doGet::root contents: ");
        for (String filename : root.getChildrenMap().keySet()){
            System.out.println("\t"+filename);
        }
        String dirpath = request.getParameter("dirpath");
        if (dirpath == null){
            System.out.println("ZipDir::doGet:: dirpath is NULL !!!");
            response.setStatus(400);
            return;
        }
        String shared = request.getParameter("shared");
        if (username == null){
            System.out.println("ZipDir::doGet::username is null");
            return;
        }

        if (shared != null && shared.equals("shared")){
            try{
                System.out.println("ZipDir::doGet::shared tree file: " +basePath + "/" + username + "/shared.serialized");
                root = Utils.load(basePath + "/" + username + "/shared.serialized");
                System.out.println("ZipDir::doGet::shared root contents: ");
                for (String filename : root.getChildrenMap().keySet()){
                    System.out.println("\t"+filename);
                }
            } catch (ClassNotFoundException e){
                e.printStackTrace();
            }
        }
        String mimeType = "application/octet-stream";
        response.setContentType(mimeType);

        String headerKey = "Content-Disposition";
        String[] splittedDirPath = dirpath.split("/");

        String headerValue = String.format("attachment; filename=\"%s\"", splittedDirPath[splittedDirPath.length -1] + ".zip");
        response.setHeader(headerKey, headerValue);

        if (dirpath != null && !dirpath.isEmpty()){
            ByteArrayOutputStream zipped;
            if (shared != null && shared.equals("shared")){
                zipped = zipShared(dirpath, root);
            } else{
                zipped = zipPrivate(dirpath, root);
            }
            response.getOutputStream().write(zipped.toByteArray());
            response.setContentLength((int) zipped.toByteArray().length);
            response.setStatus(200);
        } else {
            response.setStatus(400);
        }
    }
    
    private ByteArrayOutputStream zipShared(String dirpath, FilesystemNode root){
        ZipShared zipper = new ZipShared();
        return zipper.zipDir(dirpath, root, basePath);

    }

    private ByteArrayOutputStream zipPrivate(String dirpath, FilesystemNode root){
        Zip zipper = new Zip();
        return zipper.zipDir(dirpath, root, basePath);
    }
}
