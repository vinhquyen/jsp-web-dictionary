<%-- 
         Document   : search
         Created on : 12-ago-2008, 13:00:54
         Author     : chiron
--%>
<%@page import="database.*" %>
<%@page import="java.text.MessageFormat" %>
<%@page import="java.util.*" %>

<%@ include file="lang.jspf" %>

<div id="search">
    <form  id="s_word" action="index.jsp" accept-charset="utf-8">
        <p>
            <label><%= r.getString("searchLab")%></label>
            <input id="input_search" type="text" name="word" tabindex="1" />
            <input type="image" src="img/arrow.gif" alt="<%= r.getString("submit")%>"
            style="position:relative; top:8px;"/>
        </p>
    </form>
</div>
<div class="result">
    <%
        request.setCharacterEncoding("UTF-8");

        ArrayList<Entry> aDef = null;
        String szId = request.getParameter("id");
        String szWord = request.getParameter("word");
        int idWord = 0;
        boolean userLogged = (session.getAttribute("user") != null);

        if (szWord != null || szId != null) {
            if (szId != null) {
        /** get definition by ID */
            try {
                idWord = Integer.valueOf(szId);
                aDef = new ArrayList<Entry>();
                aDef.add(Entry.getDefinition(idWord));
            } catch (Exception e) {
                InOut.printError(e, out);
                return;
            }
        }
        else { // szWord != null
            /** get defition by WORD */
            szWord = szWord.toLowerCase().trim();
            try {
                aDef = Entry.getDefinition(szWord);
            } catch (Exception e) {
                InOut.printError(e, out);
                return;
            }
        }
        // TODO: sustituir por JSLT !! o algo mas elegante
        /** Find lexicographically nearest words */
        if (aDef == null || aDef.isEmpty()) {
                //szWord = InOut.userInputParser(szWord, out);
                szWord = InOut.userInputParser(szWord);
                String notF = MessageFormat.format(r.getString("notFound"), "''<em>" + szWord + "</em>'' ");
             %>
                <p><%=notF %><br/>
             <% if( userLogged ) { %>
                    <a href='index.jsp?action=1&word=<%=szWord %>'><%=r.getString("addDef") %></a>
                <% }
            %> </p> <%
            
            LinkedList<Entry> laux = Entry.getNearWords(szWord);
            if( !laux.isEmpty()) { %>
                <div id='nearW'>
                    <h4><%=r.getString("nearWord") %></h4>
                    <ul>
                <%
                for (Entry e : laux) { %>
                      <li>
                        <a href='index.jsp?id=<%=e.getId()%>'><%=e.getWord() %></a>
                      </li>
                <% } %>
                    </ul>
                </div>
         <% }
        }
        /** Show the definions of the word*/
        else {
            int i = 1;
            for(Entry e : aDef) {
                szWord = e.getWord().toLowerCase();
                idWord = e.getId();
            %>
                <h3><%=szWord %><% if(aDef.size()>1) {%> <sup><%=i %></sup> <% }
                      if (userLogged) { %>
                      <sup style="font-size:65%;font-weight:100;">
                          <a href="index.jsp?action=4&id=<%=idWord %>">modificar</a>
                      </sup>
                    <% } %>
                </h3>
                <p><% InOut.printWordDef(e, out); %></p><%
                i++;
            }
        }
    }
    %>
</div>