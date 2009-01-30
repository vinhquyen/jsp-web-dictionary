<%-- 
	 Document   : addword
	 Created on : 14-ago-2008, 11:44:54
	 Author     : chiron
--%>
<%@ page import="database.*" %>
<%@ page import="java.text.MessageFormat" %>
<%@ page import="java.util.*" %>

<%@ include file="WEB-INF/jspf/lang.jspf" %>

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

<h2><%= r.getString("add") %>:</h2>
<form action="update.jsp" method="get">
	<p> <label for="word"><%= r.getString("word") %></label>
		<input type="text" name="word" value="<%=e.getWord() %>"/>
	</p>
	<p>
		<label><%= r.getString("morf") %></label>
		<select name="morfology" onblur="displayVerb(this.value);" onchange="javascript:displayVerb(this.value);">
		<%
      String szMorf = e.getMorfology();
			for (String m : Entry.getMorfologies()) {
				String szValue = "<option value=\"" + m + "\"";
				
				if (szMorf.compareTo(m)==0)
          szValue += "selected='true'";
          
        szValue += ">" + m + "</option>";
				out.println(szValue);
			}
		%>
		</select>
		<div id="verb" class="hidden">
			<p><%= r.getString("vProp") %><br/>
				<input type='checkbox' name="tr." /><label><%= r.getString("vTr") %></label>
				<input type='checkbox' name="int."/><label><%= r.getString("vIntr") %></label>
				<input type='checkbox' name="r."/><label><%= r.getString("vRef") %></label>
			</p>
		</div>
	</p>
	<!-- Definitions -->
	<div id="definitions">
		<p>
			<label for="Definition"><%= r.getString("def") %></label>
			<!--<a class="add" href="#" onclick="addNode('def')"><%= r.getString("addnode") %></a>-->
            <br/>
			<textarea cols="32" rows="4" name="def"><%= e.getDefinition() %></textarea>
		</p>
	</div>
	<!-- Examples
	<div id="examples">
		<p>
			<label for="Examples"><%= r.getString("exUse") %></label>
			<a class="add" href="#" onclick="addNode('ex')"><%= r.getString("addnode") %></a><br/>
			<textarea cols="32" rows="4" name="ex"></textarea>
		</p>
	</div>  -->
	<p>
		<input type="submit" value="<%= r.getString("addword") %>"/>
		<input type="reset" value="<%= r.getString("clear") %>"/>
	</p>
</form>