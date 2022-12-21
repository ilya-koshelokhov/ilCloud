<%@ page contentType="text/html;charset=UTF-8" language="java" session="false" %>
<html>
<head>
    <title>Login</title>
</head>
<body>
    <div style="margin: auto;width: 50%;">
        <div style="margin: auto;width: 50%;">

            <h1>Login</h1>
            <% String error = (String) request.getAttribute("error");%>
            <%if (error == null || error.isEmpty()){%>
                <%--HIDE --%>
            <%}else{%>
                <%=error%>
            <%}%>
            <form action="./login" method="post">
                <div class="container" >
                    <label for="username">Username : </label>
                    <input type="text" placeholder="Enter Username" name="username" id ="username" required>
                    <br>
                    <label for="password">Password : </label>
                    <input type="password" placeholder="Enter Password" name="password" id="password" required>
                    <button type="submit">Login</button>
                </div>
            </form>
            <h2>Not Registered?  <a href="./register">Register</a> </h2>
        </div>
    </div>
</body>
</html>
