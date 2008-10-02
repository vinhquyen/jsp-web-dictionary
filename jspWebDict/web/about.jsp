<%-- 
	Document   : about
	Created on : 14-ago-2008, 16:37:03
	Author     : chiron
--%>

<%@ include file="WEB-INF/jspf/lang.jspf" %>

<div>
	<p>
	<% 
		String szGuayen = "<a href='http://www.guayente.org'>Asociaci&oacute;n Guayente</a>";
		out.print(MessageFormat.format(rAbout.getString("author"), szGuayen));
	%>
	</p>
 
 <p><%
		String mail = "<a href='mailto:admin947(AT)gmail.com'>admin947(AT)gmail.com</a>";
		out.print(MessageFormat.format(rAbout.getString("colaborate"), mail));
	 %></p>
</div>
