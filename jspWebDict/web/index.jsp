<%-- 
		   Document   : index
		   Created on : 17-jul-2008, 19:35:18
		   Author     : chiron
--%>
<%@page import="database.*" %>
<%@page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>

<%@ include file="WEB-INF/jspf/lang.jspf" %>

<!DOCTYPE html 
PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN"
"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">

<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en">
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
		<title>JSP Web Dictionary <% out.print(r.getString("version")); %></title>
		<link rel="stylesheet" href="css/main.css" type="text/css" />	
		<script type="text/javascript" src="js/hiper.js"></script>
		<script type="text/javascript" src="js/utils.js"></script>
	</head>
	<body onload="init();">
		<div id="header">
			<a href="index.jsp?lang=<% out.print(szLang); %>"><img 
			  id="logo" src="img/dict.jpg" alt="home" /></a>
			 <div id="title">
				<h1>JSP-Tech Web Dictionary </h1>
				<h4><% out.print(r.getString("version"));%></h4>
			</div>
		</div>
		<div id="bar">
			<ul class="inline">
				<li><a href="index.jsp?action=1&amp;lang=<% out.print(szLang); %>"><% out.print(r.getString("add")); %></a></li>
				<li><a href="index.jsp?action=2&amp;lang=<% out.print(szLang); %>"><% out.print(r.getString("rnd")); %></a></li>
				<li><a href="#" onclick="setVisibility('license')"><% out.print(r.getString("license")); %></a></li>
				<li><a href="#" onclick="setVisibility('contact')"><% out.print(r.getString("contact")); %></a></li>
				<li><a href="index.jsp?action=3&amp;lang=<% out.print(szLang); %>"><% out.print(r.getString("about")); %></a></li>
			</ul>
			<div id="contact" class="hidden">
				<em>admin947 (AT) gmail.com</em>
			</div>
		</div>
		<div id="lang">
			 <select name="lang" onchange="changeLang(this.value)">
				  <option value="">--</option>
				  <!--<option value="es_ES_an">aragon&eacute;s</option>-->
				  <option value="es">espa&ntilde;ol</option>
				  <option value="en">english</option>
				  <option value="es_ES_be">patu&eacute;s</option>
			 </select>
		</div>
		<div id="content">
			<%
	    request.setCharacterEncoding("UTF-8");

	    String szAction = request.getParameter("action");
	    int iAction;
	    if (szAction != null) {
			try {
				iAction = Integer.valueOf(szAction);
			} catch (Exception e) {
				iAction = 0;
			}
			switch (iAction) {
				case 1:
					szAction = "addword.jsp?lang=" + szLang;
					break;
				case 2:
					String redirectURL = "index.jsp?lang=" + szLang + "&id=" + Entry.getRandom();
					response.sendRedirect(redirectURL);
					break;
				case 3:
					szAction = "about.jsp?lang=" + szLang;
					break;
				default:
					szAction = "search.jsp?lang=" + szLang;
			}
	    }
	    else	szAction = "search.jsp?lang=" + szLang;
			%>
			<jsp:include page="<%=szAction %>" />
		</div>
		<div id="license" class="hidden" onclick="showDiv(this.id)">
			 <jsp:include page='<%=r.getString("licTxt") %>' />
		</div>
		<div id="counter">
			<p>
				<%
				int iCount = Entry.getSizeDB();
				String szC = MessageFormat.format(r.getString("counter"), iCount);
				out.print(szC); 
				%><br/>
				<%@ include file="WEB-INF/jspf/stats.jspf" %>
			</p>
		</div>
		<div id="footer">
			<p><% 
				String szAutor, szCopy, szLib;
				szLib = "<span style='text-decoration:underline;'>Diccionario del Benasqu&eacute;s</span>";
				szAutor = "&Aacute;ngel Ballar&iacute;n Cornel";
				szCopy = "Copyright&copy; " + szAutor + ", Zaragoza, 1978";
				out.print(MessageFormat.format(rCopy.getString("copydict"), szLib, szAutor, szCopy));
				%>
			</p>
			<div class="hidden">
				<img src="img/guayente.jpg" alt="Asociaci&oacute;n Guayente" />
				<img src="img/tomcat.gif" alt="Powered by Tomcat" />
				<img src="img/mysql.png" alt="MySQL Powered" />
				<img src="img/java.jpg" alt="Java Powered" />
			</div>
		</div>
	</body>
</html>
