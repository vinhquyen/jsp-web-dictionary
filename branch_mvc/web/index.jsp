<%-- 
                   Document   : index
                   Created on : 17-jul-2008, 19:35:18
                   Author     : chiron
--%>
<%@page import="database.*" %>
<%@page import="java.util.Calendar" %>
<%@page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>

<%@ include file="WEB-INF/jspf/lang.jspf" %>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN"
"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">

<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="<%=r.getLocale()%>" lang="<%=r.getLocale()%>">
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
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
        <link rel="stylesheet" href="css/main.css" type="text/css" />
        <link rel="stylesheet" href="css/jquery.alerts.css" type="text/css" />

        <!-- [Captura doble-click]
        <script type="text/javascript" src="js/hiper.js"></script>-->
        <script src="js/jquery-1.4.2.min.js" type="text/javascript"></script>
        <script src="js/jquery.ui.draggable.js" type="text/javascript"></script>
        <script src="js/jquery.alerts.js" type="text/javascript"></script>
        <script src="js/jquery.validate.pack.js" type="text/javascript"></script>
        <script src="js/utils.js" type="text/javascript"></script>
    </head>
    <body onload="init();">
        <div id="bounding_box">
            <%@ include file="WEB-INF/jspf/header.jspf" %>
            <div id="content" style="position:relative">
                <%
        String szAction = request.getParameter("action");
        int iAction = 0;
        if (szAction != null) {
            try {
                iAction = Integer.valueOf(szAction);
            } catch (Exception e) {
                iAction = 0;
            }
            switch (iAction) {
                case 1:
                    szAction = "WEB-INF/jspf/search.jsp";
                    break;
                case 2:
                    String redirectURL = "index.jsp?id=" + Entry.getRandom();
                    response.sendRedirect(redirectURL);
                    break;
                case 3:
                    szAction = "WEB-INF/jspf/about.jsp";
                    break;
                case 4:
                    if (userLogged) {
                        szAction = "WEB-INF/jspf/modifyword.jsp";
                        break;
                    } else { 
                        szAction="error_handler.jsp?code=401";
                        //szAction = "WEB-INF/jspf/login.jsp?service=iAction";
                        break;
                    }
                case 5:
                    if (userLogged) {
                        szAction = "WEB-INF/jspf/delete_word.jsp";
                        break;
                    } else {
                        szAction="error_handler.jsp?code=401";
                        //szAction = "WEB-INF/jspf/login.jsp?service=iAction";
                        break;
                    }
                case 6:
                    if(User.moduleEnabled()) {
                        String logAction = request.getParameter("log_action");
                        if(!request.isSecure() && logAction == null) {
                            InOut.switchSecureMode(request, response);
                        }
                        szAction = "WEB-INF/jspf/login.jsp";
                        break;
                    }
                default:
                    szAction = "WEB-INF/jspf/search.jsp";
            }
        } else {
            szAction = "WEB-INF/jspf/search.jsp";
        }
                %>
                <jsp:include page="<%=szAction %>" />
            </div><!-- End of CONTENT -->
            <%@ include file="WEB-INF/jspf/footer.jspf" %>
        </div><!-- End of Bounding Box -->
    </body>
</html>
