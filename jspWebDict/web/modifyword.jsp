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

<h2><% out.print(r.getString("add")); %>:</h2>
<form action="update.jsp" method="get">
	<p> <label for="word"><% out.print(r.getString("word")); %></label>
		<input type="text" name="word" value="<% out.print(e.getWord()); %>"/>
	</p>
	<p>
		<label><% out.print(r.getString("morf")); %></label>
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
			<p><% out.print(r.getString("vProp")); %><br/>
				<input type='checkbox' name="tr." /><label><% out.print(r.getString("vTr")); %></label>
				<input type='checkbox' name="int."/><label><% out.print(r.getString("vIntr")); %></label>
				<input type='checkbox' name="r."/><label><% out.print(r.getString("vRef")); %></label>
			</p>
		</div>
	</p>
	<!-- Definitions -->
	<div id="definitions">
		<p>
			<label for="Definition"><% out.print(r.getString("def")); %></label>
			<a class="add" href="#" onclick="addNode('def')"><% out.print(r.getString("addnode")); %></a><br/>
			<textarea cols="32" rows="4" name="def"><% out.print(e.getDefinition()); %></textarea>
		</p>
	</div>
	<!-- Examples -->
	<div id="examples">
		<p>
			<label for="Examples"><% out.print(r.getString("exUse")); %></label>
			<a class="add" href="#" onclick="addNode('ex')"><% out.print(r.getString("addnode")); %></a><br/>
			<textarea cols="32" rows="4" name="ex"></textarea>
		</p>
	</div>
	<p>
		<input type="submit" value="<% out.print(r.getString("addword")); %>"/>
		<input type="reset" value="<% out.print(r.getString("clear")); %>"/>
	</p>
</form>