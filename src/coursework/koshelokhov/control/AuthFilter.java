package coursework.koshelokhov.control;



import javax.servlet.*;
import javax.servlet.http.*;
//import javax.servlet.Filter;
//import javax.servlet.ServletContext;
//import javax.servlet.http.HttpServletResponse;
//import javax.servlet.http.HttpSession;
//import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Arrays;

public class AuthFilter implements Filter {

    private ServletContext context;
    public void init(FilterConfig fc){}
    public void destroy(){}
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws ServletException, IOException {

        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;

        String path = req.getServletPath();
        String[] static_files = {};
        String[] values = {"/login", "/register", "/listpublic", "/style.css", "/scripts.js", "/downloadpublic", "/icons/back.png", "/icons/file.png", "/icons/folder.png", "/icons/share.png", "/icons/upload.png", "/publiczip"};
        boolean linkInWhitelist = Arrays.stream(values).anyMatch(path::equals);
        System.out.println("FILTER:CONTAINS_VAL " + linkInWhitelist);

        HttpSession session = req.getSession(false);
        if (session == null){
            System.out.println("Oh no, SESS IS NULL");
        }

        if ((session == null || session.getAttribute("guest") != null) && !linkInWhitelist){
            System.out.println("Unauthorized access request");
            res.sendRedirect(req.getContextPath() + "/login");
        } else {
            // pass the request along the filter chain
            chain.doFilter(req, res);
        }
    }
}
