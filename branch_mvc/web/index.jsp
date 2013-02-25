<%-- 
                   Document   : index
                   Created on : 17-jul-2008, 19:35:18
                   Author     : chiron
--%>
<%@page import="database.*" %>
<%@page import="java.util.Calendar" %>
<%@page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>

<%@ include file="WEB-INF/jspf/lang.jspf" %>
<%@ include file="WEB-INF/jspf/controller.jspf" %>

<!DOCTYPE html>
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="<%=r.getLocale()%>" lang="<%=r.getLocale()%>">
    <head>
        <meta charset="UTF-8" />
        <jsp:include page="WEB-INF/jspf/meta_tags.jspf" />

        <title><%=r.getString("title") %> <%=InOut.generateTitle(request) %>
        </title>

    <% /* links language alternate */
     String[] aLanguages = {"an", "ca", "en", "es"};
     for (String auxLng : aLanguages) { %>
        <link rel="alternate" lang="<%=auxLng%>" href="?lang=<%=auxLng%>" /><%
     }
    %>

        <link rel="icon" href="img/favicon.png" type="image/png"/>
        <link rel="stylesheet" href="css/s2dict-central.css" type="text/css"/>
        <link rel="stylesheet" href="css/helpers.css" type="text/css"/>
        <link rel="stylesheet" href="css/ui-darkness/jquery-ui-1.10.1.custom.min.css" />

    </head>
    <body>
        <div id="wrapper">
            <%@ include file="WEB-INF/jspf/header.jspf" %>
            <div id="main-container">
                <div id="container_960" class="container_16">
                    <%@ include file="WEB-INF/jspf/menu.jspf" %>
                    <div class="grid_13">
                      <%@ include file="WEB-INF/jspf/search_form.jspf" %>
                      <div id="main-content">
                        <jsp:include page="<%=szAction %>" />
                        <%@ include file="WEB-INF/jspf/search.jspf" %>
                      </div>
                    </div>
                </div>
                <div id="pilcrow"></div>
            </div><!-- end of main-container -->
            <%@ include file="WEB-INF/jspf/footer.jspf" %>
        </div><!-- end of wrapper -->
        <script src="js/jquery-1.9.1.js" type="text/javascript"></script>
        <script src="js/jquery-ui-1.10.1.custom.min.js" type="text/javascript"></script>
        <script src="js/utils.js"></script>
        <script>
            $(document).ready(function(){
                /*cBox.*/init();
            });
        </script>
    </body>
</html>
