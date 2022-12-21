<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Registration</title>
</head>
<body>
    <div style="margin: auto;width: 50%;">
        <div style="margin: auto;width: 50%;">
            <h1>Register</h1>
            <form action="./register" method="post">
                <div class="container">
                    <label>Username : </label>
                    <input type="text" placeholder="Enter Username" name="username" required>
                    <br>
                    <label>Password : </label>
                    <input type="password" placeholder="Enter Password" name="password" required>
                    <button type="submit">Register</button>
                </div>
            </form>
            <h2>Already registered? <a href="./login">Login</a> </h2>
        </div>
    </div>
</body>
</html>
