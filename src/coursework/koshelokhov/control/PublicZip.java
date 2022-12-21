package coursework.koshelokhov.control;

import coursework.koshelokhov.model.FilesystemNode;
import coursework.koshelokhov.model.MysqlManager;
import coursework.koshelokhov.model.ZipPublic;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.io.IOException;
import java.io.PrintWriter;



public class PublicZip extends HttpServlet {

    private String fileUploadPath;

    @Override
    public void init(ServletConfig config) throws ServletException {
        fileUploadPath = config.getServletContext().getInitParameter("FILE_UPLOAD_PATH");
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        FilesystemNode root = new FilesystemNode("root", 0);
        PrintWriter pw;
        String error = "";
        if (root == null){
            error = "Root is null";
            System.out.println("ZipPublic::doGet:: "+error);
        }
        FilesystemNode tree = (FilesystemNode)request.getSession().getAttribute("publicTree");
        root.addChild(tree);

        String hash = (String) request.getSession().getAttribute("hash");
        if (hash == null ){
            error = "hash is null";
            System.out.println("ZipPublic::doGet:: "+error);
        }

        String dirpath = request.getParameter("dirpath");
        if (dirpath == null || dirpath.isEmpty()){
            error = "dirpath is null";
            System.out.println("ZipPublic::doGet:: "+error);
        }
        if (!error.isEmpty()){
            pw = response.getWriter();
            response.setStatus(400);
            pw.println(error);
            return;
        }
        String [] splittedDirPath = dirpath.split("/");

        MysqlManager mysqlm = new MysqlManager();
        ZipPublic zip = new ZipPublic(fileUploadPath);
        String basePath = mysqlm.getPublicPathByHash(hash);
        mysqlm.close();
        if (basePath == null || basePath.isEmpty()){
            System.out.println("PublicZip::zip: ERROR: No path for hash: "+hash);
            error = "ERROR: No path for hash: "+hash;
        }
        System.out.println("PublicZip::doGet::base path: "+basePath);
        ByteArrayOutputStream zipped = zip.zip(dirpath.substring("/public".length()), root, basePath);
        String mimeType = "application/octet-stream";
        response.setContentType(mimeType);

        String headerKey = "Content-Disposition";

        String headerValue = String.format("attachment; filename=\"%s\"", splittedDirPath[splittedDirPath.length -1] + ".zip");
        response.setHeader(headerKey, headerValue);
        if (zipped == null ||  zipped.size() <= 0){
            error = "Null or empty ByteArray";
        }

        if (!error.isEmpty()){
            pw = response.getWriter();
            response.setStatus(400);
            pw.println(error);
            return;
        } else {
            response.getOutputStream().write(zipped.toByteArray());
            response.setContentLength((int) zipped.toByteArray().length);
            response.setStatus(200);
        }
        
    }

}
