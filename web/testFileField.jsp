<%--
  Created by IntelliJ IDEA.
  User: TINY
  Date: 2019/4/5
  Time: 17:39
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Title</title>
    <script src="editor/jquery-min.js" type="text/javascript" charset="utf-8"></script>

</head>
<body>
<form action="${pageContext.request.contextPath}/upload"
      target="target" method="post" id="image_form" enctype="multipart/form-data">
    <input type="file" name="image">
    <%--<input type="submit" name="submit" id="submit" value="提交">--%>
    <button id="select" type="submit">选择图片</button>
</form>
<div id="status"></div>
<iframe name="target" hidden></iframe>
<script type="text/javascript">
    // $("#select").click(function () {
    //     $("#image_form").submit();
    //     $.ajax({
    //         url: "/testUpload?action=ajax_upload",
    //         type: "get",
    //         // data: $("#image_form").serialize(),
    //         dataType: "json",
    //         success: function (data) {
    //             alert(JSON.stringify(data))
    //         },
    //         error: function (data) {
    //             alert("error" + JSON.stringify(data))
    //         }
    //     });
    //     // $("#file").click();
    // });
    //
    $("#image_form").submit(function () {
        const option = {
            url: "/testUpload?action=upload_status",
            type: "get",
            dataType: "text",
            success: function (data) {
                // alert(data);
                if ('100%' === data) {
                    clearInterval(statusInterval);
                    $.ajax({
                        url: "/testUpload?action=get_url",
                        type: "get",
                        dataType: "json",
                        success: function (data) {
                            alert(JSON.stringify(data));
                        }
                    })
                }
                $("#status").text(data)
            }
        };
        const statusInterval = setInterval(function () {
            $.ajax(option)
        }, 200)


    });
</script>
</body>
</html>
