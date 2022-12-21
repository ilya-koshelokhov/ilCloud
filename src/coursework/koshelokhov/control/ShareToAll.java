package coursework.koshelokhov.control;

import coursework.koshelokhov.model.FilesystemNode;
import coursework.koshelokhov.model.MysqlManager;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

public class ShareToAll extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        HttpSession session = request.getSession();
        String filename = request.getParameter("filename");
        String username = (String) session.getAttribute("username");
        int uid = (int) session.getAttribute("uid");
        FilesystemNode root = (FilesystemNode)session.getAttribute("tree");

        String file = request.getParameter("filename");
        System.out.println("SHARETOALL:request to share" + file);

        if ( file == null || file.isEmpty()){
            System.out.println("SHARETOALL: file is null");
            response.setStatus(418);
            return;
        }
        String [] splitPath= file.split("/");

        FilesystemNode target = root;
        System.out.println(target.toStringTerminal());

        for (String it: splitPath){
            if(!it.equals("/") && !it.isEmpty()){
                target = target.getChild(it);
                System.out.println(it);
            }
        }
        System.out.println("SHARE_TO_AAL: attempt to share: " + target.getFullName());
        MysqlManager mysqlManager = new MysqlManager();
        FilesystemNode publicRoot = new FilesystemNode("public",0);

        publicRoot.addPublicChild(target);

        String hash = mysqlManager.addPublicFile(username, filename, publicRoot);
        mysqlManager.close();
        if (hash != null){
            response.getWriter().println(hash);
        }
    }
}
