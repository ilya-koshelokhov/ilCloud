<%@ page import="javax.servlet.http.HttpSession" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Public shared</title>
    <link rel="stylesheet" type="text/css" href="style.css">
</head>
<body onload="setup();">
    <div id="requestPath"  value="listpublic" /></div>
    <div class="top-bar">
        <div class="top-bar-right">
            <ul>
                <%
                    Boolean login = (Boolean) session.getAttribute("guest");
                %>
                <li><a href="./listshared">Shared</a> </li>
                <li><a class="active" href="listing">${sessionScope.username}#${sessionScope.uid}</a></li>
                <li><a href="./logout">LOGOUT</a></li>
            </ul>
        </div>
        <div class="top-bar-left">
            <ul>
                <li>ilCloud</li>
            </ul>
        </div>
    </div>
        <div id="directory-context" class="context-menu directory-context">
        <ul>
            <li class="context-menu__item" data-action="Download_directory">
                Download directory
            </li>
        </ul>
    </div>
    <iframe name="dummy" style="display:none;"></iframe>
    <div id="treePage"></div>
</body>
</html>
<script src="scripts.js"></script>
