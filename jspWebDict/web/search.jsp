<%-- 
	 Document   : search
	 Created on : 12-ago-2008, 13:00:54
	 Author     : chiron
--%>
<%@page import="database.*" %>
<%@page import="java.util.*" %>

<div id="search">
	<form action="index.jsp">
	  <p>
		<label>Search: </label>
		<input type="text" name="word" />
		<input type="submit" value="Go!"/>
	  </p>
	</form>
</div>
<div class="result">
<%
	request.setCharacterEncoding("UTF-8");
	
	LinkedList<Entry> laux = null;
	String szId = request.getParameter("id");
	String szWord = request.getParameter("word");
	
	if(szWord == null && szId == null)
		return;
	
	if(szWord != null) { /** get defition by WORD */
		szWord = szWord.toLowerCase();
		out.print("<h4>Looking for: " + szWord + "</h4>");
		try {
			laux = Entry.getDefinition(szWord);
		} catch (Exception e) {
			out.print("<p class='error'>");
			out.print(e.toString());
			if(laux == null)
			    out.print("<h3>Lista nula</h3>");
			out.print("</p>");
			return;
		} 
	}
	else {	/** get definition by ID */
	    try {
			int id = Integer.valueOf(szId);
			laux = Entry.getDefinition(id, null);
	    } catch (Exception e) {
			out.print("<p class='error'>");
			e.printStackTrace();
			out.print("</p>");
			return;
	    }
	}
	/** Find lexicographically nearest words */
	if (laux == null || laux.isEmpty()) {
			out.print("<p>Definition of word ''<em>" + szWord + "</em>'' not FOUND<br/>");
			out.print("<a href='index.jsp?action=add&word=" + szWord + "'>Add definition</a>");
			laux = Entry.getNearWords(szWord);
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
			szWord = laux.getFirst().getWord().toLowerCase();
	    
		out.print("<h3>" + szWord + "</h3>");
		out.println("<ol>");
		for (Entry e : laux) {
		    out.print("<li>");
		    out.println("<span class='morf'>" + e.getMorfologia() +
			    "</span> " + e.getDefinicion() + "</li>");
		    
		    ArrayList<String> aEx = e.getExamples();
		    if(aEx != null && !aEx.isEmpty()) {
				out.println("<ul>");
				for(String szExample : aEx)
					out.println("<li>" + szExample + "</li>");
				out.println("</ul>");
		    }
		}
		out.println("</ol>");
		//out.print("<p>Number of definitions: " + laux.size() + "</p>"); //DEBUG
	}
%> 
</div>