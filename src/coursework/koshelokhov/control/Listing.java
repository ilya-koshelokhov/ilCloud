package coursework.koshelokhov.control;

import coursework.koshelokhov.model.FilesystemNode;

import coursework.koshelokhov.model.Utils;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;

public class Listing extends HttpServlet {

    private String fileUploadPath;

    @Override
    public void init(ServletConfig config) throws ServletException {
        fileUploadPath = config.getServletContext().getInitParameter("FILE_UPLOAD_PATH");
    }
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        HttpSession session = request.getSession();

        Map.Entry<String, FilesystemNode> val = ((FilesystemNode)session.getAttribute("tree")).getChildrenMap().entrySet().iterator().next();

        RequestDispatcher requestDispatcher = request.getRequestDispatcher("views/listing.jsp");
        requestDispatcher.forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        HttpSession session = request.getSession();
        FilesystemNode root = (FilesystemNode) session.getAttribute("tree");
        String username = (String)session.getAttribute("username");
        String dir = request.getParameter("dir");

        System.out.println("Listing:DOGET:treeCheck:"+root.getName());

        response.setContentType("text/html");//setting the content type
        PrintWriter pw=response.getWriter();//get the stream to write the data

        if (dir == null || dir.isEmpty()){
            dir = "/" + username + "/";
        }

        System.out.println("tree:\n"+root.toStringTerminal());

        String [] splitPath= dir.split("/");
        for (String string : splitPath) {
            System.out.println(string);
        }

        FilesystemNode target = root;

        for (String it : splitPath){
            if(!it.equals("/") && !it.isEmpty()){
                target = target.getChild(it);
                System.out.println("LISTING::doPOST::CREATEPARSE: " + it);
            }
        }

        Map.Entry<String, FilesystemNode> userDirectory = ((FilesystemNode)session.getAttribute("tree")).getChildrenMap().entrySet().iterator().next();
        StringBuffer sb = new StringBuffer();
        getAside(sb, userDirectory.getValue(), target, "", "");
        pw.println("<aside>");
        pw.println(sb);
        pw.println("</aside>");

        if(!target.isFile()){

            String fullName = target.getFullName();
            pw.println("<div class=\"current-directory\">");
            pw.println(fullName.replace("/", " > "));
            pw.println("</div>");

            pw.print("<div id=\"currentDir\" value=\"");
            pw.print(fullName);
            pw.print("\" /></div>");

            pw.println("<div class=\"file-listing\" data-id=\"" + target.getFullName() + "\" >");

            pw.print("<a class=\"go-back\" onclick=\"getPage('");
            pw.print(target.getParent().getFullName());
            pw.println("')\"> ..</a><br>");

            for(FilesystemNode child : target.getChildrenMap().values()) {

                if (child.isFile()) {
                    pw.print("<div class=\"file\" onclick=\"downloadFile('");
                    pw.print(child.getFullName());
                    pw.println("', false, false)\" data-id=\"" + child.getFullName() + "\">");
                }
                else {
                    pw.print("<div class=\"folder clickable-folder\" onclick=\"getPage('");
                    pw.print(child.getFullName());
                    pw.println("')\" data-id=\"" + child.getFullName() + "\">");
                }
                pw.println(child.getName());
                pw.println("</div><br>");
            }
        }
        pw.close();
        System.out.println("Listing:ContextParam"+fileUploadPath);
    }

    private void getAside(StringBuffer sb, FilesystemNode node, FilesystemNode target, String prefix, String childrenPrefix){

        if(node == null || node.isFile() || sb == null)
            return;

        int count = 0;
        sb.append("<div onclick=\"getPage('");
        sb.append(node.getFullName());
        sb.append("')\" id=\"");
        sb.append(node.getFullName());
        sb.append("\"");
        sb.append(node == target ? "class=\"active_directory\">" : ">");
        sb.append(prefix);
        sb.append(node.getName());
        sb.append("</div>");

        for(FilesystemNode child : node.getChildrenMap().values()){
            count++;

            if (count >= node.ChildrenSize()){
                getAside(sb, child, target, childrenPrefix + "&#9492;&#9472;&#9472;", childrenPrefix + "&#160;&#160;&#160;&#160;&#160;");  //└──
            }
            else{
                getAside(sb, child, target, childrenPrefix + "&#9500;&#9472;&#9472;", childrenPrefix + "&#9474;&#160;&#160;&#160;&#160;"); //├──  │
            }
        }
    }
}
