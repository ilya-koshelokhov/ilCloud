<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Shared list</title>
    <link rel="stylesheet" type="text/css" href="style.css">
    <meta charset="UTF-8">
</head>
<body onload="setup()">
    <div id="requestPath"  value="listshared" /></div>
    <div class="top-bar">
        <div class="top-bar-right">
            <ul>
                <li><a href="./listshared" class="active">Shared</a> </li>
                <li><a href="listing">${sessionScope.username}#${sessionScope.uid}</a></li>
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

    <div id="listing-context" class="context-menu listing-context">
        <ul>
            <li class="context-menu__item" data-action="Upload_file_to_shared">
                Upload file
            </li>
        </ul>
    </div>
    <div id="file-context" class="context-menu file-context">
        <ul>
            <li class="context-menu__item" data-action="Update_file">
                Update file
            </li>
            <li class="context-menu__item" data-action="Download_shared_file">
                Download file
            </li>
        </ul>
    </div>

    <div  id="file-uploader" class="modal">
        <div id="drop-area">
            <span class="close" id="uploader-closer">&times;</span>
            <form id="upload-form">
                <img src="${pageContext.request.contextPath}/icons/upload.png" id="upload-image">
                <div>
                    <input id="file-input" type="file" name="" onchange="handleFiles(this.files)">
                    <label for="file-input">Select file</label>
                    <span>or drop it here</span>
                </div>
                <div id="files-to-upload-list"></div>
            </form>
        </div>
    </div>
    <div id="treePage"></div>
</body>
</html>

<script src="scripts.js"></script>
