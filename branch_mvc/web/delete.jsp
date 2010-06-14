<%-- 
    Document   : delete
    Created on : 14-jun-2010, 17:01:50
    Author     : chiron
--%>
<%@page import="database.*" %>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ include file="WEB-INF/jspf/lang.jspf" %>
<%
    if(userLogged) {
        String res, szId;
        res = "";
        szId = request.getParameter("id");

        if(szId != null && szId.length() > 0) {
            res = Entry.deleteWord(szId);
            if(res == "")
                res = "La palabra " + szId + " ha sido eliminada correctamente.";

        } else res = "Invalid word Identifier format!";
%>

    <p id="msg" class="ok">
        <%=res %>
    </p>

<%
} else { %>
    <p id="msg" class="error">You not have rights to perform this action!</p>
 <%
} %>