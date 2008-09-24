<%-- 
	 Document   : addword
	 Created on : 14-ago-2008, 11:44:54
	 Author     : chiron
--%>

<%@page import="database.*" %>
<%@page import="java.text.MessageFormat" %>
<%@page import="java.util.ResourceBundle" %>

<% ResourceBundle r = ResourceBundle.getBundle("resources/main"); %>

<script>
	function displayVerb(morf) {
		if(morf == "v.") {
			showDiv("verb", true);
		}
		else {
			showDiv("verb", false); 
		}
	}
</script>

<h2><% out.print(r.getString("add")); %>:</h2>
<form action="insert.jsp" method="get">
	<p> <label for="word"><% out.print(r.getString("word")); %></label>
		<input type="text" name="word"/>
	</p>
	<p>
		<label><% out.print(r.getString("morf")); %></label><br/>
		<select name="morfology" onblur="displayVerb(this.value);" onchange="javascript:displayVerb(this.value);">
		<%
			for (String m : Entry.getMorfologies()) {
				String szValue = "<option value=\"" + m + "\"";
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
	<p>
		<label for="Definition"><% out.print(r.getString("def")); %></label><br/>
		<textarea cols="32" rows="4" name="def"></textarea>
	</p>
	<p>
		<label for="Examples"><% out.print(r.getString("exUse")); %></label><br/>
		<textarea cols="32" rows="4" name="ex"></textarea>
	</p>
	<p>
		<input type="submit" value="Add word"/>
		<input type="reset" value="Clear form"/>
	</p>
</form>