<%-- 
		   Document   : index
		   Created on : 17-jul-2008, 19:35:18
		   Author     : chiron
--%>
<%@page import="database.*" %>
<%@page import="java.util.Locale" %>
<%@page import="java.util.ResourceBundle" %>
<%@page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>

<%
	Locale curr;
	String szLang = request.getParameter("lang");
	if(szLang == null || szLang.length() == 0)
	    curr = Locale.getDefault();
	else
	    curr = new Locale(szLang);
	
	ResourceBundle r = ResourceBundle.getBundle("resources/main", curr); 
%>

<!DOCTYPE html 
PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN"
"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">

<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en">
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
		<title>JSP Web Dictionary <% out.print(r.getString("version")); %></title>
		<link rel="stylesheet" href="css/main.css" type="text/css" />
		<script>
			 var show = true;
			 function showDiv(id) {
				 var obj = document.getElementById(id);
				 if(show) {
					 obj.style.display = "block";
					 obj.style.visibility = "visible"; 
					 show = false;
				 }
				 else {
					 obj.style.display = "none";
					 obj.style.visibility = "hidden"; 
					 show = true;
				 }
			 }
		</script>
		<script type="text/javascript" src="js/hiper.js"></script>
	</head>
	<body>
		<div id="header">
			<h1><a href="index.jsp" style="border: 0;"><img src="img/dict.jpg" alt="home" width="120px"
													   style="vertical-align:middle;filter:alpha(opacity=80);-moz-opacity:.8;opacity:.8;"/></a>
			JSP-Tech Web Dictionary</h1>
		</div>
		<div id="bar">
			<ul class="inline">
				<li><a href="index.jsp?action=1"><% out.print(r.getString("add")); %></a></li>
				<li><a href="index.jsp?action=2"><% out.print(r.getString("rnd")); %></a></li>
				<li><a href="#" onclick="showDiv('license')"><% out.print(r.getString("license")); %></a></li>
				<li><a href="#" onclick="showDiv('contact')"><% out.print(r.getString("contact")); %></a></li>
				<li><a href="index.jsp?action=3"><% out.print(r.getString("about")); %></a></li>
			</ul>
			<div id="contact" class="hidden">
				<em>admin947 (AT) gmail.com</em>
			</div>
		</div>
		<div id="lang">
			 <select name="lang" onchange="location.href=location.href+'?lang='+this.value">
				  <option value="">--</option>
				  <option value="es">es</option>
				  <option value="en">en</option>
				  <option value="ar">patu&eacute;s</option>
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
					szAction = "addword.jsp";
					break;
				case 2:
					String redirectURL = "index.jsp?id=" + Entry.getRandom();
					response.sendRedirect(redirectURL);
					break;
				case 3:
					szAction = "about.jsp";
					break;
				default:
					szAction = "search.jsp";
			}
	    }
	    else	szAction = "search.jsp";
			%>
			<jsp:include page="<%=szAction %>" />
		</div>
		<div id="license" class="hidden" onclick="showDiv(this.id)">
			<p> <img src="img/gplv3.png" alt="GPL v3 logo" style="float:left; padding-right: 5px;"/>
				This program is free software: you can redistribute it and/or modify
				it under the terms of the GNU General Public License as published by
				the Free Software Foundation, either version 3 of the License, or
			(at your option) any later version.</p>
			
			<p>This program is distributed in the hope that it will be useful,
				but WITHOUT ANY WARRANTY; without even the implied warranty of
				MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
			GNU General Public License for more details.</p>
			
			<p>You should have received a <a href="COPYING.txt">copy 
				of the GNU General Public License</a> along with this program.<br/>  
				 If not, see <a href="http://www.gnu.org/licenses/">http://www.gnu.org/licenses/</a>.</p>
		</div>
		<div class="hidden" id="footer">
			<img src="img/guayente.jpg" alt="Asociaci&oacute;n Guayente" />
			<img src="img/tomcat.gif" alt="Powered by Tomcat" />
			<img src="img/mysql.png" alt="MySQL Powered" />
			<img src="img/java.jpg" alt="Java Powered" />
		</div>
	</body>
</html>
