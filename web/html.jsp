<%--
  Created by IntelliJ IDEA.
  User: TINY
  Date: 2019/4/3
  Time: 22:29
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" trimDirectiveWhitespaces="true" language="java" %>
<html>
<head>
    <title>html</title>
    <link rel="stylesheet" type="text/css" href="editor/css/editor.preview.css"/>
    <link rel="stylesheet" type="text/css" href="editor/css/style.css"/>
    <link href='http://fonts.googleapis.com/css?family=Arizonia' rel='stylesheet' type='text/css' />
    <%--<script src="editor/jquery-min.js" type="text/javascript" charset="utf-8"></script>--%>
    <%--<script src="editor/lib/flowchart.min.js" type="text/javascript" charset="utf-8"></script>--%>
    <%--<script src="editor/lib/jquery.flowchart.min.js" type="text/javascript" charset="utf-8"></script>--%>
    <%--<script src="editor/lib/raphael.min.js" type="text/javascript" charset="utf-8"></script>--%>
    <%--<script src="editor/lib/marked.min.js" type="text/javascript" charset="utf-8"></script>--%>
    <%--<script src="editor/lib/underscore.min.js" type="text/javascript" charset="utf-8"></script>--%>
    <%--<script src="editor/lib/sequence-diagram.min.js" type="text/javascript" charset="utf-8"></script>--%>
    <%--<script src="editor/lib/prettify.min.js" type="text/javascript" charset="utf-8"></script>--%>
    <%--<script src="editor/editor-md.min.js" type="text/javascript" charset="utf-8"></script>--%>

    <script src="editor/jquery-min.js"></script>
    <script src="editor/lib/marked.min.js"></script>
    <script src="editor/lib/prettify.min.js"></script>

    <script src="editor/lib/raphael.min.js"></script>
    <script src="editor/lib/underscore.min.js"></script>
    <script src="editor/lib/sequence-diagram.min.js"></script>
    <script src="editor/lib/flowchart.min.js"></script>
    <script src="editor/lib/jquery.flowchart.min.js"></script>

    <script src="editor/editor-md.min.js"></script>


    <script src="scripts/global.js" type="text/javascript" charset="UTF-8"></script>
</head>
<body>

<%--<label for="markdown"></label><textarea id="markdown" >--%>
    <%--## Java Web笔记之WEB-INF资源访问--%>

    <%--### 关于WEB-INF--%>
    <%--有过`Java Web`开发经验的都知道`WEB-INF`是安全目录，所谓的安全目录就是防止用户能直接通过地址栏来访问我们的网站资源。试想如果没有一个这样的访问机制，假设某位用户知道了我们的网站目录结构，那么他将可以通过地址栏输入地址直接随意地获取我们的网站资源。我们知道很多网站都有登录功能，部分服务比如下载文件等需要我们登录才能获取，但如果以上情况发生了，那这个功能还有什么意义呢？--%>

    <%--### WEB-INF带来的困扰--%>
    <%--确实Java的这一机制给我们的网站安全性带来了很大提升，但是有利便有弊，有点不足的地方便是给我们开发者带来了一定的麻烦。如上所说，该目录下的资源不可以通过地址栏输入的方式获取，但有时候我们在`JSP`文件上需要静态的使用例如图片、`JSP`或是HTML等文件，如果这些文件是置于WEB-INF的同级目录下那还好办，但如果是在WEB-INF目录下，那么就算你有两个同级的`JSP`文件，想给他们加个`<a>`标签进行跳转，你得到的将是一个`404`错误！--%>

    <%--### 问题引入--%>
    <%--在开发网站的后台文章编辑功能时用的是`Editor.md`(一款`Markdown`编辑器)，编辑器支持文章左编辑右预览，在做图片上传的时候我把图片上传到了`WEB-INF`目录下，这就导致虽然图片上传成功并且得到了正确的图片路径，也没有办法显示图片，虽然图片或许也没有放到`WEB-INF`的必要，但是遇到问题我还是不想轻易放弃，百度了好久都没有结果，不得不说很多东西还是得自己去想。--%>

    <%--### 解决方案--%>
    <%--相信遇到这种问题的人不在少数，如何能够像编写传统的静态网页那样在直接引用资源的同时还能保证资源安全性呢？我的最终解决方案是使用`Servlet`拦截图片预览请求，通过`Java`程序读取图片内容并使用`Response`获取输出流输出图片，而前端的代码访问图片资源时只需将`src`稍微改一下便好。--%>

    <%--**代码如下：**--%>
    <%--```java--%>
    <%--@WebServlet(name = "previewService", urlPatterns = "/preview")--%>
    <%--public class ImageAccessor extends HttpServlet {--%>
    <%--@Override--%>
    <%--public void doGet(HttpServletRequest request, HttpServletResponse response)--%>
    <%--throws IOException {--%>
    <%--//要预览的图片的路径，如果有目录则也包含了目录信息。为了避免一个文件夹下的图片过多有时目录要动态决定。--%>
    <%--final String previewURI = request.getParameter("uri");--%>
    <%--//图片资源的路径前缀，这个一般是固定的--%>
    <%--final String prefixPath = "WEB-INF/upload/images/";--%>
    <%--//图片资源的完整路径--%>
    <%--Path picturePath = Paths.get(getServletContext()--%>
    <%--.getRealPath(prefixPath + previewURI));--%>
    <%--//所请求的图片资源存在--%>
    <%--if (Files.exists(picturePath)) {--%>
    <%--byte[] bytes = Files.readAllBytes(picturePath);--%>
    <%--OutputStream outputStream = response.getOutputStream();--%>
    <%--outputStream.write(bytes);--%>
    <%--outputStream.flush();--%>
    <%--outputStream.close();--%>
    <%--} } }--%>
    <%--```--%>

    <%--**前端页面引用：**--%>
    <%--```html--%>
    <%--// 这是一个GET请求，完整URL为：localhost:8080/preview?--%>
    <%--// uri=33.png，由于上面定义了urlPatterns为/preview，--%>
    <%--// 所有类似localhost:8080/preview*的请求都会被拦截处理，--%>
    <%--// uri要指定基于WEB-INF/upload/images/的图片名称。--%>
    <%--<img src="/preview?uri=33.png" />--%>
    <%--```--%>

    <%--**效果如下：**--%>

    <%--![](/preview?uri=02bae4a9-df25-48c2-ab83-bab9603ae168.jpg)--%>

    <%--PS：为了防止图片命名冲突，要用能够生成唯一字串的`UUID`类为图片生成一个全局唯一的名称：`UUID.randomUUID()`，简单起见图片后缀可从给定文件名截取。--%>
<%--</a></textarea>--%>

<div id="article_view">
    <label for="display_article_content"></label>
    <textarea id="display_article_content" style="display: none"></textarea>
</div>
<script type="text/javascript">

    $(function () {
        //先对容器初始化，在需要展示的容器中创建textarea隐藏标签，
        // $("#container").html('<textarea id="content" style="display:none;"></textarea>');
        // $("#article_view").html("<textarea id=\"display_article_content\" style=\"display: none;\"></textarea>");
        // const markdown_content = $("#markdown").val();//获取需要转换的内容
        // $("#display_article_content").val(markdown_content);//将需要转换的内容加到转换后展示容器的textarea隐藏标签中
        $("#display_article_content").val("## Java Web笔记之WEB-INF资源访问\n" +
            "\n" +
            "### 关于WEB-INF\n" +
            "    有过`Java Web`开发经验的都知道`WEB-INF`是安全目录，所谓的安全目录就是防止用户能直接通过地址栏来访问我们的网站资源。试想如果没有一个这样的访问机制，假设某位用户知道了我们的网站目录结构，那么他将可以通过地址栏输入地址直接随意地获取我们的网站资源。我们知道很多网站都有登录功能，部分服务比如下载文件等需要我们登录才能获取，但如果以上情况发生了，那这个功能还有什么意义呢？\n" +
            "\n" +
            "### WEB-INF带来的困扰\n" +
            "    确实Java的这一机制给我们的网站安全性带来了很大提升，但是有利便有弊，有点不足的地方便是给我们开发者带来了一定的麻烦。如上所说，该目录下的资源不可以通过地址栏输入的方式获取，但有时候我们在`JSP`文件上需要静态的使用例如图片、`JSP`或是HTML等文件，如果这些文件是置于WEB-INF的同级目录下那还好办，但如果是在WEB-INF目录下，那么就算你有两个同级的`JSP`文件，想给他们加个`<a>`标签进行跳转，你得到的将是一个`404`错误！\n" +
            "\n" +
            "### 问题引入\n" +
            "    在开发网站的后台文章编辑功能时用的是`Editor.md`(一款`Markdown`编辑器)，编辑器支持文章左编辑右预览，在做图片上传的时候我把图片上传到了`WEB-INF`目录下，这就导致虽然图片上传成功并且得到了正确的图片路径，也没有办法显示图片，虽然图片或许也没有放到`WEB-INF`的必要，但是遇到问题我还是不想轻易放弃，百度了好久都没有结果，不得不说很多东西还是得自己去想。\n" +
            "\n" +
            "### 解决方案\n" +
            "    相信遇到这种问题的人不在少数，如何能够像编写传统的静态网页那样在直接引用资源的同时还能保证资源安全性呢？我的最终解决方案是使用`Servlet`拦截图片预览请求，通过`Java`程序读取图片内容并使用`Response`获取输出流输出图片，而前端的代码访问图片资源时只需将`src`稍微改一下便好。\n" +
            "\n" +
            "**代码如下：**\n" +
            "```java\n" +
            "    @WebServlet(name = \"previewService\", urlPatterns = \"/preview\")\n" +
            "    public class ImageAccessor extends HttpServlet {\n" +
            "    @Override\n" +
            "    public void doGet(HttpServletRequest request, HttpServletResponse response)\n" +
            "    throws IOException {\n" +
            "    //要预览的图片的路径，如果有目录则也包含了目录信息。为了避免一个文件夹下的图片过多有时目录要动态决定。\n" +
            "    final String previewURI = request.getParameter(\"uri\");\n" +
            "    //图片资源的路径前缀，这个一般是固定的\n" +
            "    final String prefixPath = \"WEB-INF/upload/images/\";\n" +
            "    //图片资源的完整路径\n" +
            "    Path picturePath = Paths.get(getServletContext()\n" +
            "    .getRealPath(prefixPath + previewURI));\n" +
            "    //所请求的图片资源存在\n" +
            "    if (Files.exists(picturePath)) {\n" +
            "    byte[] bytes = Files.readAllBytes(picturePath);\n" +
            "    OutputStream outputStream = response.getOutputStream();\n" +
            "    outputStream.write(bytes);\n" +
            "    outputStream.flush();\n" +
            "    outputStream.close();\n" +
            "    } } }\n" +
            "```\n" +
            "\n" +
            "**前端页面引用：**\n" +
            "```html\n" +
            "    // 这是一个GET请求，完整URL为：localhost:8080/preview?\n" +
            "    // uri=33.png，由于上面定义了urlPatterns为/preview，\n" +
            "    // 所有类似localhost:8080/preview*的请求都会被拦截处理，\n" +
            "    // uri要指定基于WEB-INF/upload/images/的图片名称。\n" +
            "    <img src=\"/preview?uri=33.png\" />\n" +
            "```\n" +
            "\n" +
            "**效果如下：**\n" +
            "\n" +
            "![](/preview?uri=02bae4a9-df25-48c2-ab83-bab9603ae168.jpg)\n" +
            "\n" +
            "PS：为了防止图片命名冲突，要用能够生成唯一字串的`UUID`类为图片生成一个全局唯一的名称：`UUID.randomUUID()`，简单起见图片后缀可从给定文件名截取。\n" +
            "</a>" + "###科学公式 TeX(KaTeX)\n" +
            "                    \n" +
            "$$E=mc^2$$\n" +
            "\n" +
            "行内的公式$$E=mc^2$$行内的公式，行内的$$E=mc^2$$公式。\n" +
            "\n" +
            "$$\\(\\sqrt{3x-1}+(1+x)^2\\)$$\n" +
            "                    \n" +
            "$$\\sin(\\alpha)^{\\theta}=\\sum_{i=0}^{n}(x^i + \\cos(f))$$\n" +
            "\n" +
            "$$X^2 > Y$$\n" +
            "\n" +
            "#####上标和下标\n" +
            "\n" +
            "上标：X&lt;sup&gt;2&lt;/sup&gt;\n" +
            "\n" +
            "下标：O&lt;sub&gt;2&lt;/sub&gt;\n" +
            "\n" +
            "##### 代码块里包含的过滤标签及属性不会被过滤\n" +
            "\n" +
            "```html\n" +
            "&lt;style type=\"text/style\"&gt;\n" +
            "body{background:red;}\n" +
            "&lt;/style&gt;\n" +
            "\n" +
            "&lt;script type=\"text/javscript\"&gt;\n" +
            "alert(\"script\");\n" +
            "&lt;/script&gt;\n" +
            "\n" +
            "&lt;iframe height=498 width=510 src=\"http://player.youku.com/embed/XMzA0MzIwMDgw\" frameborder=0 allowfullscreen&gt;&lt;/iframe&gt;\n" +
            "```\n" +
            "\n" +
            "#####Style\n" +
            "\n" +
            "&lt;style&gt;\n" +
            "body{background:red;}\n" +
            "&lt;/style&gt;\n" +
            "\n" +
            "&lt;style type=\"text/style\"&gt;\n" +
            "body{background:red;}\n" +
            "&lt;/style&gt;\n" +
            "\n" +
            "#####Script\n" +
            "\n" +
            "&lt;script&gt;\n" +
            "alert(\"script\");\n" +
            "&lt;/script&gt;\n" +
            "\n" +
            "&lt;script type=\"text/javscript\"&gt;\n" +
            "alert(\"script\");\n" +
            "&lt;/script&gt;");//将需要转换的内容加到转换后展示容器的textarea隐藏标签中


        //转换开始,第一个参数是上面的div的id
        editormd.markdownToHTML("article_view", {
            htmlDecode: "style,script,iframe", //可以过滤标签解码
            emoji: true,
            // markdown: markdown,
            // htmlDecode: true,
            taskList:true,
            tex: true,				 // 默认不解析
            flowChart:true,			// 默认不解析
            sequenceDiagram:true,  // 默认不解析
        });
    });
</script>
</body>
</html>
