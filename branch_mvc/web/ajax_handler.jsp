<%-- 
    Document   : ajax_handler
    Created on : 16-jun-2010, 2:47:13
    Author     : chiron
--%>
<%@page import="database.*" %>
<%@page contentType="text/html" pageEncoding="UTF-8"%>

<%
    int unique = 0;
    boolean isUnique;
    String wrd = request.getParameter("word");
    String mrf = request.getParameter("morfology");
    try {
        isUnique = !Entry.existDefinition(wrd, mrf);
        } catch (Exception e) {
            InOut.printError(e, out);
            isUnique = false;
        }
    if(isUnique)
        unique = 1;
    %>

<%=unique %>

