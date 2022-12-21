package coursework.koshelokhov.control;

import coursework.koshelokhov.model.FilesystemNode;
import coursework.koshelokhov.model.MysqlManager;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;

public class ListPublic extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        String hashid = request.getParameter("id");
        if (hashid.length() != 32){
            System.out.println("LISTPUBLIC:: Illegal hashid: "+hashid);
            response.setStatus(418);
            response.getWriter().println("Incorrect hashid");
            return;
        }

        HttpSession session = request.getSession();
        MysqlManager mysqlm = new MysqlManager();
        FilesystemNode tree = mysqlm.getPublicNodeByHash(hashid);
        if (tree == null){
            response.setStatus(404);
            response.getWriter().println("TREE IS NULL");
            return;
        }
        session.setAttribute("publicTree", tree);
        session.setAttribute("guest", true);
        session.setAttribute("hash", hashid);

        request.getRequestDispatcher("views/listPublic.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        FilesystemNode tree = (FilesystemNode)request.getSession().getAttribute("publicTree");
        FilesystemNode root = new FilesystemNode("root", 0);
        root.addChild(tree);
        if (tree == null){
            response.setStatus(500);
            response.getWriter().println("Tree is null");
            return;
        }
        System.out.println("publicListing:DOGET:treeCheck:"+tree.toStringTerminal());
        response.setContentType("text/html");//setting the content type
        PrintWriter pw = response.getWriter();//get the stream to write the data

        String dir = request.getParameter("dir");

        if (dir == null || dir.isEmpty()){
            dir = "/public";
        }

        String [] splittedPath= dir.split("/");
        for (String string : splittedPath) {
            System.out.println(string);
        }

        FilesystemNode target = root;
        for (String it: splittedPath){
            if(!it.equals("/") && !it.isEmpty()){
                target = target.getChild(it);
                System.out.println("ListingParse: "+it);
            }
        }

        if(!target.isFile()){

            pw.println("<div id=\"currentDir\"  value=\"" + target.getPublicFullName() +"\" /></div>");

            pw.println("<div class=\"file-listing\">");

            pw.print("<a class=\"go-back\" onclick=\"getPage('");
            if(target.getPublicParent() != null){
                pw.print(target.getPublicParent().getPublicFullName());
            }
            else {
                pw.print(target.getParent().getPublicFullName());
            }
            pw.println("')\">..</a><br>");

            for(FilesystemNode child : target.getChildrenMap().values()){
                String pathToFile = target.getPublicFullName()+"/" + child.getName();
                if(child.isFile()){
                    pw.print("<div class=\"file\" onclick=\"downloadFile('" + pathToFile+ "', true, true)\">");
                }
                else {
                    pw.print("<div class=\"folder clickable-folder\" onclick=\"getPage('");
                    pw.print(pathToFile);
                    pw.print("')\" data-path=\"publiczip\" data-id=\"");
                    pw.print(pathToFile);
                    pw.print("\">");
                }
                pw.println(child.getName());
                pw.println("</div><br>");
            }
        }
        pw.close();
    }
}
