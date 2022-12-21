package coursework.koshelokhov.control;

import coursework.koshelokhov.model.FilesystemNode;
import coursework.koshelokhov.model.Utils;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;

public class Delete extends HttpServlet {

    private String fileUploadPath;

    @Override
    public void init(ServletConfig config) throws ServletException {
        fileUploadPath = config.getServletContext().getInitParameter("FILE_UPLOAD_PATH");
    }
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        FilesystemNode root     = (FilesystemNode)request.getSession().getAttribute("tree");
        String username         = (String)request.getSession().getAttribute("username");
        String path             = request.getParameter("name");
        String userStr          = path.split("/")[1];
        String userPath         = path.substring(userStr.length()+1);

        System.out.println("Download reconstruct check user: " + userStr);
        System.out.println("Download reconstruct check userPath: " + userPath);

        response.setContentType("text/html");//setting the content type

        String [] splitPath= path.split("/");
        FilesystemNode target = root;
        for (String it: splitPath){
            if(!it.equals("/") && !it.isEmpty()){

                target = target.getChild(it);
            }
        }
        String toDelete = target.getName();
        String fullToDelete = fileUploadPath+"/"+userStr+"/files"+userPath;

        File f = new File(fullToDelete);
        boolean success =  f.delete();

        System.out.println(fullToDelete);

        target = target.getParent();
        target.removeChild(toDelete);

        System.out.println("File DELETED" + fullToDelete + success);

        Utils.save(root, fileUploadPath+"/"+username+"/tree.serialized");
    }
}
