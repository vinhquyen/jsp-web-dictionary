<%-- 
    Document   : error_handler
    Created on : 03-may-2010, 22:59:27
    Author     : chiron
--%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page isErrorPage="true" %>
<%@page import="java.util.Enumeration" %>
<%@page import="java.util.Date" %>
<%@page import="java.io.StringWriter" %>
<%@page import="java.io.PrintWriter" %>

<%@ include file="WEB-INF/jspf/lang.jspf" %>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN"
"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">

<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>JSP Web Dictionary <%=rConf.getString("version")%> - ERROR</title>
    </head>
    <body>
        <h1>Sorry</h1>
        <p>We have some problems to handle your request,
            please try again later</p>


        <% if (exception != null) { %>
        La excepci&oacute;n causante del error ha sido:
        <%
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            exception.printStackTrace(pw);
            out.println(sw.toString().replace("\n", "<br/>"));
            sw.close();
            pw.close();
        %>

        <% }

            Enumeration sesAttrib;
            String szAttrib;
            session = request.getSession(true); // Create the session (if not exists)
        %>
        <h4>
            Sesi&oacute;n activa desde: <%=new Date(session.getCreationTime()) %> <br/>
            &Uacute;ltimo acceso: <%=new Date(session.getLastAccessedTime()) %> <br/>
            Contenido:
        </h4>
        <ul>
        <%
          sesAttrib = session.getAttributeNames(); // Leemos todos los objetos de sesion
          while (sesAttrib.hasMoreElements()) {
            szAttrib = (String) sesAttrib.nextElement();
        %>
            <li><%=szAttrib%> = <%=session.getAttribute(szAttrib)%> </li>
        <%
          } // del while
        %>
        </ul>
    </body>
</html>
