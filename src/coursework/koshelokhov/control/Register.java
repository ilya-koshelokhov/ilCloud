package coursework.koshelokhov.control;

import coursework.koshelokhov.model.Checker;
import coursework.koshelokhov.model.FilesystemNode;
import coursework.koshelokhov.model.MysqlManager;
import coursework.koshelokhov.model.Utils;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.IOException;

public class Register extends HttpServlet {

    private String fileUploadPath;

    @Override
    public void init(ServletConfig config) throws ServletException {
        fileUploadPath = config.getServletContext().getInitParameter("FILE_UPLOAD_PATH");
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        RequestDispatcher requestDispatcher = request.getRequestDispatcher("views/register.jsp");
        requestDispatcher.forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        MysqlManager mysqlManager = new MysqlManager();

        String username = request.getParameter("username");
        String password = request.getParameter("password");
        String fullPath = fileUploadPath + "/" + username + "/";

        System.out.format("Requested Register with %s :: %s\n", username, password);

        if (mysqlManager.getUID(username) != 0){
            System.out.format("User with username :%s already registered\n", username);
            response.getWriter().println("ALREADY REGISTRED");
            return;
        }

        if (password == null || password.isEmpty()){
            System.out.format("empty password");
            response.getWriter().println("EMPTY PASSWORD");
            return;
        }

        if (username == null || username.isEmpty()){
            System.out.format("empty username");
            response.getWriter().println("EMPTY USERNAME");
            return;
        }

        if (!Checker.isClean(username)){
            System.out.format("Bad username");
            response.getWriter().println("BAD CHARACTESRS IN USERNAME. [a-zA-Z0-9] allowed.");
            return;
        }

        mysqlManager.addUser(username, password);

        HttpSession session = request.getSession();
        session.setAttribute("username", username);
        int uid = mysqlManager.getUID(username);

        if (uid == 0)
            System.out.println("ERROR:Register uid is NULL");

        session.setAttribute("uid", uid);
        mysqlManager.close();
        FilesystemNode root = new FilesystemNode("root", 0);

        FilesystemNode user = new FilesystemNode(username, uid);
        root.addChild(user);

        new File(fullPath+"files/").mkdirs();
        session.setAttribute("tree", root);
        FilesystemNode sharedRoot = new FilesystemNode("root", uid);
        FilesystemNode shared = new FilesystemNode("shared", uid);
        sharedRoot.addSharedChild(shared);
        Utils.save(shared, fullPath+"shared.serialized");
        Utils.save(root, fullPath+"tree.serialized");
        response.sendRedirect("./listing");
    }

}
