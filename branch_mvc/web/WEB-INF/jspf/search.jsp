<%-- 
         Document   : search
         Created on : 12-ago-2008, 13:00:54
         Author     : chiron
--%>
<%@page import="database.*" %>
<%@page import="java.text.MessageFormat" %>
<%@page import="java.util.*" %>

<%@ include file="lang.jspf" %>

<%
    String caseInsensitive = request.getParameter("insensitive");
    String szChk = "";

    if(caseInsensitive != null && caseInsensitive.equalsIgnoreCase("on")) {
        szChk = "checked='on'";
    }
%>
<div id="search">
    <form  id="s_word" action="index.jsp" accept-charset="utf-8">
        <p>
            <label><%= r.getString("searchLab")%></label>
            <input id="input_search" type="text" name="word" tabindex="1" />
            <input type="image" src="img/search.png" alt="<%= r.getString("submit")%>"
                   style="position:relative; top:8px;" onmouseover="show('be');" onmouseout="hide('be');"/>

            <input type="submit" name="lng" value="ar" onmouseover="show(this.value);" onmouseout="hide(this.value);"/>
            <input type="submit" name="lng" value="ca" onmouseover="show(this.value);" onmouseout="hide(this.value);"/>
            <input type="submit" name="lng" value="es" onmouseover="show(this.value);" onmouseout="hide(this.value);"/>
            <input type="submit" name="lng" value="fr" onmouseover="show(this.value);" onmouseout="hide(this.value);"/>
            <input type="submit" name="cnt" value="<%=r.getString("context") %>" onmouseover="show('cnt');" onmouseout="hide('cnt');"/>
            <input type="checkbox" id="ci" name="insensitive" <%=szChk%> />
            <label style="font-size:0.8em;" onclick="setChecked('ci')" onmouseover="show('help_ci')"
                onmouseout="hide('help_ci')"><%=r.getString("accentInsensitive") %></label>
        </p>
        <p style="height:10px;position:relative;top:-12px;z-index:5;">
            <% String szCtx = r.getString("help_lng");%>
            <span id="be"  class="hidden cnt"><%= MessageFormat.format(szCtx, r.getString("lng_be")).replace(".", " ") + r.getString("by_default")%>.</span>
            <span id="ar"  class="hidden cnt"><%= MessageFormat.format(szCtx, r.getString("lng_ar"))%></span>
            <span id="ca"  class="hidden cnt"><%= MessageFormat.format(szCtx, r.getString("lng_ca"))%></span>
            <span id="es"  class="hidden cnt"><%= MessageFormat.format(szCtx, r.getString("lng_es"))%></span>
            <span id="fr"  class="hidden cnt"><%= MessageFormat.format(szCtx, r.getString("lng_fr"))%></span>
            <span id="cnt" class="hidden cnt"><%= r.getString("help_cnt")%></span>
            <span id="help_ci"  class="hidden cnt"><%= r.getString("help_ci")%></span>
        </p>
    </form>
</div>
<div class="result">
    <%
    String paramEncoding = application.getInitParameter("parameter-encoding");
    request.setCharacterEncoding(paramEncoding); //"UTF-8" --> Defined in web.xml

    ArrayList<Entry> aDef = null;
    String szId = request.getParameter("id");
    String szWord = request.getParameter("word");
    String lng = request.getParameter("lng");

    int idWord = 0;
    String szHighLight = null;

    if (szWord != null || szId != null) {
        if (szId != null) {
            /** get definition by ID */
            try {
                idWord = Integer.valueOf(szId);
                aDef = new ArrayList<Entry>();
                aDef.add(Entry.searchDefinition(idWord));
            } catch (Exception e) {
                InOut.printError(e, out);
                out.println("</div>");
                return;
            }
        } else { // szWord != null
            /** get defition by WORD */
            szWord = szWord.trim();
            //TODO if(szWord contains caracteres raros) // hacer esto de abajo...
            szWord = new String(szWord.getBytes("8859_1"), "utf-8");
            try {
                if (lng == null) {
                    lng = "be"; // Set the default language
                }
                if (request.getParameter("cnt") != null) {
                    aDef = Entry.getWordInContext(szWord);
                    szHighLight = szWord;
                } else {
                    aDef = Entry.searchDefinition(szWord, lng, caseInsensitive);
                }
            } catch (Exception e) {
                InOut.printError(e, out);
                out.println("</div>");
                return;
            }
        }
        // TODO: sustituir por JSLT !! o algo mas elegante
        /** Find lexicographically nearest words */
        if (aDef == null || aDef.size() == 0) {
            //szWord = InOut.userInputParser(szWord, out);
            szWord = InOut.userInputParser(szWord);

            if(request.getParameter("cnt") != null) { /* contextual search [no results] */ %>
                <p><%=MessageFormat.format(r.getString("notFoundCtx"), "''<em>" + szWord + "</em>''")%></p> <%
            }
            else { /* standar search */
              String notF = MessageFormat.format(r.getString("notFound"), "''<em>" + szWord + "</em>'' ");
              if( szWord.length() > 0 ) { %>
                <p><%=notF%></p> <%
              }
              LinkedList<Entry> laux = NearWordsController.getNearWords(szWord, lng);
              if (laux != null && laux.size() > 0) { %>
                <div id='nearW'>
                    <h4><%=r.getString("nearWord")%></h4>
                    <div style="float: left;"><ul>
                    <% int i = 1;
                    for (Entry e : laux) {%>
                            <li>
                                <a href='index.jsp?id=<%=e.getId()%>'><%=e.getWord()%></a>
                            </li> <%
                        if (i % 10 == 0 && i < laux.size()) {%>
                        </ul></div>
                        <div style="float:left;">
                            <ul> <%
                        }
                            i++;
                    }%>
                        </ul>
                    </div>

                </div><br style="clear:both;"/> <%
              }
            }
            
        } /** Show the definions of the word*/
        else {
            int i = 1;
            for (Entry e : aDef) {
                szWord = e.getWord();
                idWord = e.getId();

                if ( i == 1 ) {
                    out.print("<div id='col_left' class='left'>\n");
                }
            %>
                <div id="def<%=i-1 %>" class="definition">
                    <h3><%=szWord%><% if (aDef.size() > 1) {%> <sup><%=i%></sup> <% }
                      if (userLogged) {%>
                        <sup style="font-size:65%;font-weight:100;">
                            <a href="index.jsp?action=4&amp;mod=modify&amp;id=<%=idWord%>">modificar</a>
                        </sup>
                        <sup style="font-size:65%;font-weight:100;">
                            <a href="#" onclick="deleteWord('<%=idWord%>')">eliminar</a>
                        </sup><%
                      } %>
                    </h3>
                  <%
                    InOut.printWordDef(e, out, szHighLight, r);
                    i++;
                  %>
                </div><%
                
                if ( i == Math.ceil((double)aDef.size() / 2) + 1 )   { out.print("\n</div>\n<div id='col_right' class='left'>\n"); }
                if ( i == aDef.size() + 1 )     { out.print("</div>\n"); }
            } %>
            <script type='text/javascript'>adjustColsHeigh();</script><%
        }
    }
    %>
    <br style="clear:both" />
</div>