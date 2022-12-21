<%--
  Created by IntelliJ IDEA.
  User: evgeny
  Date: 01.02.2022
  Time: 18:36
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Files listing</title>
    <link rel="stylesheet" type="text/css" href="style.css">
    <meta charset="UTF-8">
</head>
<body onload="setup()">

    <div class="top-bar">
        <div class="top-bar-right">
            <ul>
                <li class="centred"><a href="./listshared">Shared</a> </li>
                <li class="centred"><a class="active" href="listing">${sessionScope.username}#${sessionScope.uid}</a></li>
                <li class="centred"><a href="./logout">LOGOUT</a></li>
            </ul>
        </div>
        <div class="top-bar-left">
            <ul>
                <li class="centred">ilCloud</li>
            </ul>
        </div>
    </div>

<%--    <a class="go-back" onclick="getPage('')" >..</a><br>--%>

    <div id="listing-context" class="context-menu listing-context">
        <ul>
            <li class="context-menu__item" data-action="Create_directory">
                Create directory
            </li>
            <li class="context-menu__item" data-action="Upload_file">
                Upload file
            </li>
            <li class="context-menu__item" data-action="Share_current_directory">
                Share
            </li>
        </ul>
    </div>
    <div id="file-context" class="context-menu file-context">
        <ul>
            <li class="context-menu__item" data-action="Update_file">
                Update file
            </li>
            <li class="context-menu__item" data-action="Download_file">
                Download file
            </li>
            <li class="context-menu__item" data-action="Share_file">
                Share
            </li>
            <li class="context-menu__item" data-action="Delete_file">
                Delete file
            </li>
        </ul>
    </div>
    <div id="directory-context" class="context-menu directory-context">
        <ul>
            <li class="context-menu__item" data-action="Share_directory">
                Share
            </li>
            <li class="context-menu__item" data-action="Download_directory">
                Download directory
            </li>
            <li class="context-menu__item" data-action="Delete_directory">
                Delete directory
            </li>
        </ul>
    </div>

    <div id="myModal" class="modal">
        <!-- Modal content -->
        <div class="modal-content">
            <span class="close" id="share-closer">&times;</span>
            <div id="fileNameToShare" value=""></div>
            <ul class="share-mod-switcher">
                <li>
                    <h3>
                        <input type="radio" name="share-mode" id="share1" onclick="showToUser()" checked>
                        <label for="share1"> Add user to share</label>
                    </h3>
                    <div class="hidden-div" id="share-with-user-div">
                        <label for="userToShare">username: </label>
                        <input id="userToShare" type="text"><br>
                        <label for="writable">Writable:</label>
                        <input id="writable" type="checkbox" checked>
                        <button id="shareButton" onclick="share()">Share</button>
                    </div>
                </li>
                <li>
                    <h3>
                        <input type="radio" name="share-mode" id="share2" onclick="showToLink()" checked>
                        <label for="share2"> Share by link</label>
                    </h3>
                    <div class="hidden-div" id="share-by-link-div">
                        <div class="link-holder">
                            <a href="#" id="random-link">To generate link click on "Share"</a>
                            <button id="shareAllButton" onclick="shareToAll()">Share</button>
                        </div>
                    </div>
                </li>
            </ul>
        </div>

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

    <div id="requestPath" value="listing" /></div>

    <iframe name="dummy" style="display:none;"></iframe>
    <div id="treePage"></div>
</body>
</html>

<script src="scripts.js"></script>
