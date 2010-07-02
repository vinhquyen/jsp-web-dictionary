<%-- 
    Document   : delete
    Created on : 14-jun-2010, 17:01:50
    Author     : chiron
--%>
<%@page import="database.*" %>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ include file="lang.jspf" %>
<%
    if(userLogged) {
        int res;
        String szId = request.getParameter("id");

        if(szId == null || szId.isEmpty()) {
            InOut.printError(InOut.ERROR_PARAM, "Illegal Parameter Error", out);
        }
        else {
            try {
                res = Entry.deleteWord(szId);
            } catch (Exception ex) {
                InOut.printError(ex, out);
                return;
            }
            if( res == -1) {
                InOut.statusInfo(out);
            } else {
                InOut.printInfoMsg("La palabra " + szId + " ha sido eliminada correctamente.", out);
            }
        }

    } else { %>
        <p class="error">You not have rights to perform this action!</p> <%
    } %>