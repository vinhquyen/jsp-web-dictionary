<%-- 
                   Document   : index
                   Created on : 17-jul-2008, 19:35:18
                   Author     : chiron
--%>
<%@page import="database.*" %>
<%@page import="java.util.Calendar" %>
<%@page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>

<%@ include file="WEB-INF/jspf/lang.jspf" %>

<%  /** Show the word searched at the browser title bar **/
        String szTitle = request.getParameter("word");
        String idWord = request.getParameter("id");
        if (szTitle == null) {
            if (idWord != null) {
                try {
                    int id = Integer.parseInt(idWord);
                    szTitle = Entry.getDefinition(id).getWord() + " -";
                } catch (Exception e) {
                    szTitle = "";
                }
            } else {
                szTitle = "";
            }
        } else {
            szTitle += " -";
        }

%>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN"
"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">

<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="<%=r.getLocale()%>" lang="<%=r.getLocale()%>">
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
        <jsp:include page="WEB-INF/jspf/meta_tags.jspf" />

        <title><%=szTitle%> <%= r.getString("title")%> / JSP Web Dictionary
        <%=rConf.getString("version")%></title>

    <% /* links language alternate */
     String[] aLanguages = {"an", "ca", "en", "es"};
     for (String auxLng : aLanguages) { %>
        <link rel="alternate" lang="<%=auxLng%>" href="?lang=<%=auxLng%>" /><%
     }
    %>

        <link rel="stylesheet" href="css/main.css" type="text/css" />
        <link rel="icon" href="img/favicon.png" type="image/png"/>

        <!-- [Captura doble-click]
        <script type="text/javascript" src="js/hiper.js"></script>-->
        <script type="text/javascript" src="js/jquery-1.4.2.min.js"></script>
        <script type="text/javascript" src="js/jquery.validate.pack.js"></script>
        <script type="text/javascript" src="js/utils.js"></script>
    </head>
    <body onload="init();">
        <div id="bounding_box">
            <div id="login"><%@ include file="WEB-INF/jspf/login.jspf" %></div>
            <div id="header">
                <!--       <img id="backgr" src="img/bg.png" alt    ="Header background" /> -->
                <div id="lang">
                    <select name="lang" onchange="changeLang(this.value)">
                        <option value="">[<%=r.getString("lng")%>]</option>
                        <option value="an">aragon&eacute;s</option>
                        <option value="an_BE">benasqu&eacute;s</option>
                        <option value="ca">catal&agrave;</option>
                        <option value="en">english</option>
                        <option value="es">espa&ntilde;ol</option>
                    </select>
                </div>
                <div id="title">
                    <h1><a href="index.jsp"><img
                            id="logo" src="img/dict.jpg" alt="home" /></a>
                        <%= r.getString("title")%>
                    </h1>
                    <h4>JSP-Tech Web Dictionary <%= rConf.getString("version")%></h4>
                </div>
                <div id="bar">
                    <ul id="navigator">
                        <li><a href="index.jsp"><img id="home" alt="<%= r.getString("home")%>" src="img/home.png"/> <%= r.getString("home")%></a></li>
                        <% if (userLogged) {%>
                        <li><a href="index.jsp?action=4&mod=add"><%= r.getString("addword")%></a></li>
                        <% }%>
                        <li><a href="index.jsp?action=2"><%=r.getString("rnd")%></a></li>
                        <li><a id="tog-license" href="#"><%=r.getString("license")%></a></li>
                        <li><a id="tog-contact" href="#"><%=r.getString("contact")%></a></li>
                        <li><a href="index.jsp?action=3"><%=r.getString("about")%></a></li>
                        <li><strong><a id="tog-help" href="#"><%=r.getString("help")%></a></strong></li>
                    </ul>
                    <div id="contact" class="hidden">
                        <em>admin947<img src="img/arroba.gif" alt="@" class="arroba" />gmail.com</em>
                    </div>
                </div>
            </div> <!-- End of HEADER -->
            <div id="content">
                <%
        String paramEncoding = application.getInitParameter("parameter-encoding");
        request.setCharacterEncoding(paramEncoding); //"UTF-8" --> Defined in web.xml

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
                        szAction = "WEB-INF/jspf/modifyword.jsp?id=" + request.getParameter("id");
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
            <div id="footer">
                <!-- texty: allows modifications withouth a new publication (In example: small news, etc) -->
     <% /** TODO remove false from if below */
        if (false && request.getParameter("action") == null && request.getParameter("id") == null &&
                (request.getParameter("word") == null || request.getParameter("word").isEmpty())) {%>
                <script type="text/javascript"
                        src="http://texty.com/cms/syndicate/25bb24b5-4279-4334-839f-59c04b376eab.js"></script>

                <% }%>
                <!-- end texty -->
                <div id="help" class="hidden" onclick="$('#help').hide(400)">
                    <% String szHelpDiv = "WEB-INF/jspf/" + r.getString("help_div");%>
                    <jsp:include page="<%=szHelpDiv%>" />
                </div>
                <div id="counter">
                    <%
        int iCount = Entry.getSizeDB();
        String szC = MessageFormat.format(r.getString("counter"), iCount);
        out.print(szC);
                    %>
                </div>
                <div id="license" class="hidden" onclick="$('#license').hide(400);">
                    <p><img id="gpl_logo" src="img/gplv3.png" alt="GPL v3 logo"/>
                        Copyright&copy; Santiago Lamora Subir&aacute; <%=Calendar.getInstance().get(Calendar.YEAR)%>
                    </p>

                    <jsp:include page='<%=r.getString("licTxt") %>' />
                </div>
                <!-- %@include file="WEB-INF/jspf/stats.jspf"  %> TODO UNCOMMENT -->

                <p><%
        String szAutor, szCopy, szLib;
        szLib = "<span style='text-decoration:underline;'>Diccionario del Benasqu&eacute;s</span>";
        szAutor = "&Aacute;ngel Ballar&iacute;n Cornel";
        szCopy = "Copyright&copy; " + szAutor + ", Zaragoza, 1978";
        out.print(MessageFormat.format(rCopy.getString("copydict"), szLib, szAutor, szCopy));
                    %>
                </p>
            </div>
        </div><!-- End of Bounding Box -->
    </body>
</html>
