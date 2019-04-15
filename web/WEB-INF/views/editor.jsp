<%@ page contentType="text/html;charset=UTF-8" trimDirectiveWhitespaces="true"
         language="java" %>
<html>
<head>
    <title>Editor</title>
    <link href="${pageContext.request.contextPath}/base?url=editor/css/editor.css"
          rel="stylesheet" type="text/css"/>
    <link href="${pageContext.request.contextPath}/base?url=editor/css/editor.preview.css"
          rel="stylesheet" type="text/css"/>
    <link href="${pageContext.request.contextPath}/base?url=styles/global.css"
          rel="stylesheet" type="text/css"/>

    <script src="${pageContext.request.contextPath}/base?url=editor/jquery-min.js"
            type="text/javascript" charset="utf-8"></script>
    <script src="${pageContext.request.contextPath}/base?url=editor/editor-md.min.js"
            type="text/javascript" charset="utf-8"></script>
    <script src="${pageContext.request.contextPath}/base?url=scripts/global.js"
            type="text/javascript" charset="UTF-8"></script>
</head>
<body>
<div id="article_option-bar" >
    <%--<jsp:useBean id="article" class="com.tinysand.system.models.Article" scope="session" />--%>

    <form id="article_data_form" method="post" enctype="text/plain"
          action="${pageContext.request.contextPath}/article">
        <label for="article_title" >文章标题</label>
        <input type="text" name="article_title" id="article_title" required
               maxlength="100" placeholder="请输入文章标题"
               title="请输入文章标题，少于100字"/>

        <label for="article_label">文章标签</label>
        <input type="text" name="article_label" id="article_label"
               maxlength="200" placeholder="用'#'分割不同的文章标签"
               title="至少指定一个文章标签，用'#'分割不同标签" required/>

        <label for="category_selection">文章分类</label>
        <select id="category_selection" name="category" required>
            <option >Java</option>
            <option >PHP</option>
            <option >JavaScript</option>
        </select>

        <label for="title_image_path"></label>
        <input type="text" name="title_image" id="title_image_path" hidden/>

        <label for="essay_flag"></label>
        <input type="text" name="essay" id="essay_flag" value="true" hidden/>

        <label for='markdown_content'></label>
        <textarea id='markdown_content' name='article_content' readonly hidden></textarea>
    </form>

    <form action="${pageContext.request.contextPath}/upload" method="post"
          enctype="multipart/form-data" id="title_image_form" target="standby">
        <input type="file" name="title_image" id="image_selection" hidden/>

        <div id="display_upload_status"></div>
        <button id="select_image" type="button" onclick='$("#image_selection")
        .click();return false'
                title="选择文章标题图片">选择图片</button>
        <button id="upload_image" type="submit" title="上传文章标题图片">
            上传图片</button>

        <iframe name="standby" hidden src="javascript:void(0)" frameborder="0" ></iframe>
    </form>

    <div id="article_button_group">
        <button id="article_saved" type="button" title="保存文章">保存文章</button>
        <button id="article_publish" type="button" title="发布文章">发布文章</button>
    </div>

</div>
<div id="editor">
    <!-- 书写与实时显示的textarea -->
    <label for="editor-markdown-doc"></label>
    <textarea id="editor-markdown-doc" name="editor-markdown-doc"
              style="display:none;"></textarea>
    <!-- 用于后端获取md稳当内容，Java中：request.getParameter("editor-html-code")。  -->
    <label for="editor-html-code"></label>
    <textarea id="editor-html-code" name="editor-html-code"
              style="display:none;"></textarea>
</div>
</body>
</html>
