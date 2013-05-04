<%-- 
    Document   : ajax_handler
    Created on : 16-jun-2010, 2:47:13
    Author     : chiron
--%>
<%@page import="database.*" %>
<%@page contentType="text/html" pageEncoding="UTF-8"%>

<%
    int w_id;
    String id  = request.getParameter("id");
    String wrd = request.getParameter("word");
    String mrf = request.getParameter("morfology");
    try {
        w_id = Entry.existDefinition(id, wrd, mrf);
        } catch (Exception e) {
            InOut.printError(e, out);
            w_id = 0;
        }
    %>

<%=w_id %>

