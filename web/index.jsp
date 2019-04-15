<%--
  Created by IntelliJ IDEA.
  User: TINY
  Date: 2019/4/2
  Time: 9:35
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Title</title>
</head>
<body>
<%--<jsp:forward page="WEB-INF/views/editor.jsp"/>--%>
<%
    request.getRequestDispatcher("WEB-INF/views/editor.jsp").forward(request, response);
//    response.sendRedirect("WEB-INF/pages/test.jsp");
%>
</body>
</html>
