<%@ page import="java.net.URLConnection" %>
<%@ page import="java.net.URL" %>
<%@ page import="java.text.NumberFormat" %><%--
  Created by IntelliJ IDEA.
  User: TINY
  Date: 2019/4/10
  Time: 22:46
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" trimDirectiveWhitespaces="true"
         language="java" %>
<html>
<head>
    <title>Compression Test</title>
</head>
<body>
<%
    final String url = "http://www.baidu.com";
//    final String url = "http://www.bilibili.com/";
    URLConnection connection = new URL(url)
            .openConnection();
    connection.setRequestProperty("Accept-Encoding", "gzip");
//    connection.setRequestProperty("Accept-Encoding", "identity");

    long compressedLength = connection.getContentLength();

    URLConnection conn = new URL(url).openConnection();
    long commonLength = conn.getContentLength();

    double rate = (double) compressedLength / commonLength;

    out.println("<table>");
    out.println("<tr>");
    out.println("<td>");
    out.println("网址");
    out.println("</td>");
    out.println("<td>");
    out.println("压缩前");
    out.println("</td>");
    out.println("<td>");
    out.println("压缩后");
    out.println("</td>");
    out.println("<td>");
    out.println("压缩比率");
    out.println("</td>");
    out.println("</tr>");
    out.println("<br>");
    out.println("<td>");
    out.println(url);
    out.println("</td>");
    out.println("<td>");
    out.println(commonLength);
    out.println("</td>");
    out.println("<td>");
    out.println(compressedLength);
    out.println("</td>");
    out.println("<td>");
    out.println(NumberFormat.getPercentInstance().format(rate));
    out.println("</td>");
    out.println("</tr>");
    out.println("</table>");
%>
</body>
</html>
