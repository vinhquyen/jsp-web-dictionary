<%-- 
	 Document   : addword
	 Created on : 14-ago-2008, 11:44:54
	 Author     : chiron
--%>
<%@ page import="database.*" %>
<%@ page import="java.text.MessageFormat" %>
<%@ page import="java.util.*" %>

<%@ include file="lang.jspf" %>

<%
  Entry e;
  String szId = request.getParameter("id"); 
  try {
        int id = Integer.valueOf(szId);
        e = Entry.getDefinition(id);
  } catch (Exception ex) {
      InOut.printError(ex, out);
    return;
  }
%>

<h3><%= r.getString("modifyword") %>:</h3>
<form action="index.jsp" method="get"  accept-charset="UTF-8">
    <input type="hidden" name="id" value="<%=szId %>" />
	<p> <label for="word"><%= r.getString("word") %></label>
		<input type="text" name="word" value="<%=e.getWord() %>"/>
	</p>
	<p>
        <label><%= r.getString("morf")%></label>
        <input type="text" name="morfology" value="<%=e.getMorfology() %>"/>
    </p>
	<!-- Definitions -->
	<div id="definitions">
		<p>
			<label for="Definition"><%= r.getString("def") %></label>
            <br/>
			<textarea cols="80" rows="8" name="def"><%= e.getDefinition() %></textarea>
		</p>
	</div>
	<p>
		<input type="submit" value="<%= r.getString("modifyword") %>"/>
		<input type="reset" value="<%= r.getString("clear") %>"/>

        <input type="hidden" name="action" value="4" />
        <input type="hidden" name="op" value="update"/>
	</p>
</form>