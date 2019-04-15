
<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%
    String path = request.getContextPath();
    String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path;
%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
    <base href="<%=basePath%>">

    <title>My JSP 'index.jsp' starting page</title>
    <meta http-equiv="pragma" content="no-cache">
    <meta http-equiv="cache-control" content="no-cache">
    <meta http-equiv="expires" content="0">
    <meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
    <meta http-equiv="description" content="This is my page">

    <script type="text/javascript" src="<%=basePath%>/js/jquery-1.4.4.min.js"></script>
    <style type="text/css">
        iframe{
            border:none;
            width:0;
            height:0;
        }
        #progress{
            display:none;
        }
        #p_out{
            width:300px;
            height:12px;
            margin:10px 0 0 0;
            padding:1px;
            font-size:10px;
            border:solid #6b8e23 1px;
        }
        #p_in{
            width:0%;
            height:100%;
            background-color:#6b8e23;
            margin:0;
            padding:0;
        }
        #dis{
            margin:0;
            padding:0;
            text-align:center;
            font-size:12px;
            height:12px;
            width:300px;
        }
    </style>
    <script type="text/javascript">
        var inter = null;
        function formSubmit(){
            $("#progress").show();
            inter = window.setInterval("callback();", 100);//每隔100毫秒执行callback
            document.getElementById('dis').innerHTML = "初始化数据...";
            document.getElementById('p_in').style.width = "0%";
            document.form.submit();
        }
        function callback(){
            $.ajax({
                url: "<%=basePath%>/uploadStatus",
                type: "POST",
                async:false,
                data: {
                },
                error:function(errorMsg){
                },
                success: function(data){
                    document.getElementById('dis').innerHTML = '已上传：'+data;
                    document.getElementById('p_in').style.width = data;
                    if(data.indexOf("100%")!=-1){
                        uploadSuccess();
                    }
                }
            });
        }
        function uploadSuccess(){
            clearInterval(inter);
            document.getElementById('dis').innerHTML = "上传成功!";
            document.getElementById('p_in').style.width = "100%";
        }
    </script>

</head>

<body>
<form action="<%=basePath%>/upload" method="post" enctype="multipart/form-data" target="progress_iframe" name="form" >
    <input type="file" name="file" ><input type="button" onclick="formSubmit();" value="提交">
</form>
<iframe frameborder="0" id="progress_iframe" name="progress_iframe" src="javascript:void(0)"></iframe>
<div id="progress">
    <div id="p_out"><div id="p_in">
    </div></div>
    <div id="dis"></div>
</div>
</body>
</html>
