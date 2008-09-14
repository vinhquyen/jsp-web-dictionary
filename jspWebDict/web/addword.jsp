<%-- 
	 Document   : addword
	 Created on : 14-ago-2008, 11:44:54
	 Author     : chiron
--%>

<%@page import="database.*" %>
<%@page import="java.text.MessageFormat" %>
<%@page import="java.util.ResourceBundle" %>

<% ResourceBundle r = ResourceBundle.getBundle("resources/main"); %>

<h2><% out.print(r.getString("add")); %>:</h2>
<form action="insert.jsp" method="post"> <!-- FIXME: method="get" || -->
	<p> <label for="word"><% out.print(r.getString("word")); %></label>
		<%
	    String szWord	= request.getParameter("word");
	    String szMorf	= request.getParameter("morfology");
	    String szDef	= request.getParameter("def");
	    String szEx		= request.getParameter("ex");

	    if (szWord == null)	szWord = "";
	    if (szMorf == null) szMorf = "";
	    if (szDef == null)	szDef = "";
	    if (szEx == null)	szEx = "";
	    
		%>
		<input type="text" name="word" value="<% out.print(szWord);%>"/>
	</p>
	 <!-- TODO: implement using AJAX!!!! -->
	<p> <label><% out.print(r.getString("morf")); %></label><br/>		
		<!-- location.href is an adress that allows to keep form values -->
		<select name="morfology" onchange="location.href='index.jsp?action=1&word='+word.value+'&morfology='+this.value+'&def='+def.value;+'&ex='+ex.value">
		<%
			for (String m : Entry.getMorfologies()) {
				String szValue = "<option value=\"" + m + "\"";
				if (szMorf.compareTo(m) == 0)
					szValue += ("selected=\"selected\"");
				szValue += ">" + m + "</option>";
				out.println(szValue);
			}
		%>
		</select>
		<% if (szMorf.compareTo("v.") == 0) {%>
		<div id="verb">
			<p><% out.print(r.getString("vProp")); %><br/>
				<input type='checkbox' name="tr." /><label><% out.print(r.getString("vTr")); %></label>
				<input type='checkbox' name="int."/><label><% out.print(r.getString("vIntr")); %></label>
				<input type='checkbox' name="r."/><label><% out.print(r.getString("vRef")); %></label>
			</p>
		</div>
		<% }%>
	</p>
	<p>
		<label for="Definition"><% out.print(r.getString("def")); %></label><br/>
		<textarea cols="32" rows="4" name="def"><% out.print(szDef); %></textarea>
	</p>
	<p>
		<label for="Examples"><% out.print(r.getString("exUse")); %></label><br/>
		<textarea cols="32" rows="4" name="ex"><% out.print(szEx); %></textarea>
	</p>
	<p>
		<input type="submit" value="Add word"/>
		<input type="reset" value="Clear form"/>
	</p>
</form>