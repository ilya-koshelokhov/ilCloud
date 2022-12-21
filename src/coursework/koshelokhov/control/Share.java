package coursework.koshelokhov.control;

import coursework.koshelokhov.model.FilesystemNode;
import coursework.koshelokhov.model.MysqlManager;
import coursework.koshelokhov.model.Permissions;
import coursework.koshelokhov.model.Utils;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Enumeration;

public class Share extends HttpServlet {

    private String fileUploadPath;

    @Override
    public void init(ServletConfig config) throws ServletException {
        fileUploadPath = config.getServletContext().getInitParameter("FILE_UPLOAD_PATH");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        String filename = request.getParameter("filename");
        String userToShare = request.getParameter("user");
        boolean writable = false;
        Enumeration<String> params = request.getParameterNames();
        while (params.hasMoreElements()){
            if (params.nextElement().equals("writable")){
                writable = true;
                break;
            }
        }

        HttpSession session = request.getSession();
        int uid = (int) session.getAttribute("uid");  //TEST
        String username = (String) session.getAttribute("username");
        String userPath = fileUploadPath + "/" + username;
        FilesystemNode root = (FilesystemNode) session.getAttribute("tree");

        if ( filename == null || filename.isEmpty()){
            filename = "/" + username + "/";
        }

        String [] splitPath= filename.split("/");

        FilesystemNode target = root;
        System.out.println(target.toStringTerminal());

        for (String it: splitPath){
            if(!it.equals("/") && !it.isEmpty()){
                target = target.getChild(it);
                System.out.println(it);
            }
        }

        MysqlManager mysqlManager = new MysqlManager();
        if (target == null){
            response.getWriter().println("ERROR: Can't obtain Node for" + filename);
        }
        else{
            if (userToShare == null){
                response.getWriter().println("ERROR: user didn't specified" +filename);
            }
            else{
                Permissions p = target.getPermissions();

                int userToShareUID = mysqlManager.getUID(userToShare);
                if (userToShareUID == 0){
                    System.out.println("No such user: " + userToShare);
                    response.setStatus(400);
                    return;
                }

                System.out.println("SHARE::SHARING FOR "+userToShare);

                if (writable){
                    System.out.println("SHARE:Processing writtable change");
                    target.getPermissions().addGroupWritable(userToShareUID);
                    System.out.println("SHARE::is group writable for uuid"+userToShareUID);
                    System.out.println("SHARE::is group writable "+String.valueOf(target.getPermissions().isGroupWritable(userToShareUID)));
                    Utils.save(root, fileUploadPath + "/" + username + "/tree.serialized");
                }

                String whereToSave = fileUploadPath + "/" + userToShare + "/shared.serialized";
                FilesystemNode userShared =null;
                try {
                    userShared = Utils.load(whereToSave);
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
                FilesystemNode uploadUserfolder = userShared.getChild(username);
                if (uploadUserfolder == null){
                    uploadUserfolder = new FilesystemNode(username, uid);
                    userShared.addSharedChild(uploadUserfolder);
                }
                uploadUserfolder.addSharedChild(target);

                if (!Utils.save(userShared, whereToSave)){
                    System.out.println("ERROR:SHARE: failed to save");
                }

                mysqlManager.close();

            }
            response.getWriter().println("Target Node " + target.getName());
            response.getWriter().println("Target Node fullname" + target.getFullName());

        }

        response.setContentType("text/html");
        response.getWriter().println("Userpath: " + userPath);
        response.getWriter().println("Filename: " + filename);
    }
}
