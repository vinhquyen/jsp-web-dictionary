<%-- 
	 Document   : search
	 Created on : 12-ago-2008, 13:00:54
	 Author     : chiron
--%>
<%@page import="database.*" %>
<%@page import="java.text.MessageFormat" %>
<%@page import="java.util.*" %>

<%@ include file="WEB-INF/jspf/lang.jspf" %>

<div id="search">
	<form action="index.jsp" accept-charset="utf-8">
	  <p>
		<label><% out.print(r.getString("searchLab")); %></label>
		<input id="input_search" type="text" name="word" tabindex="1" />
		<input type="submit" value="<% out.print(r.getString("submit"));%>"/>
		<input type="hidden" name="lang" value="<% out.print(szLang); %>"/>
	  </p>
	</form>
</div>
<div class="result">
<%
	request.setCharacterEncoding("UTF-8");
	
	ArrayList<LinkedList<Entry>> aDef= null;
	String szId = request.getParameter("id");
	String szWord = request.getParameter("word");
	
	if(szWord == null && szId == null) {
	    out.print("</div>");
		return;
	}
	if(szWord != null) { /** get defition by WORD */
		szWord = szWord.toLowerCase();
		// out.print("<h4>Looking for: " + szWord + "</h4>"); //DEBUG
		try {
			aDef = Entry.getDefinition(szWord);
		} catch (Exception e) {
		    InOut.printError(e, out);
			return;
		} 
	}
	else {	/** get definition by ID */
	    try {
			int id = Integer.valueOf(szId);
			aDef = new ArrayList<LinkedList<Entry>>();
			aDef.add(Entry.getDefinition(id));
	    } catch (Exception e) {
			InOut.printError(e, out);
			return;
	    }
	}
	/** Find lexicographically nearest words */
	if (aDef == null || aDef.isEmpty()) {
			String notF = MessageFormat.format(r.getString("notFound"), "''<em>" + szWord + "</em>'' ");
			out.print("<p>" + notF + "<br/>");
			out.print("<a href='index.jsp?action=1&word=" + szWord + "'>" + r.getString("addDef") + "</a>");
			LinkedList<Entry> laux = Entry.getNearWords(szWord);
			out.println("<div id='nearW'>");
			for(Entry e : laux) {
			    out.print("<li>");
			    out.print("<a href='index.jsp?id="+ e.getId() +"'>" + e.getWord() + "</a>");
			    out.println("</li>");
			}
			out.println("</div>");
	}
	/** Show the definions of the word*/
	else {
	    if(szWord == null)
			szWord = aDef.get(0).getFirst().getWord().toLowerCase();
	    
	    int i = 1;
	    for(LinkedList<Entry> laux : aDef) {
			out.print("<h3>" + szWord);
			if(aDef.size() > 1)
			    out.print("<sup>" + i + "</sup>");
			out.println("</h3>");
			InOut.printWordDef(laux, out);
			i++;
		}
		//out.print("<p>Number of definitions: " + laux.size() + "</p>"); //DEBUG
	}
%> 
</div>