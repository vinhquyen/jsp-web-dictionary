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
        String szMod, szId, szOp, szTitle;

        szMod = request.getParameter("mod");
        szId = request.getParameter("id");
        szOp = request.getParameter("op");

        if (szOp == null) {
            if(szMod == null) return;
            /********************************
             *   Get the data of new word    *
             ********************************/
            if (szMod.equalsIgnoreCase("add")) {
                szTitle = r.getString("addword");
                e = new Entry();
            } else if(szMod.equalsIgnoreCase("modify")) {
                szTitle = r.getString("modifyword");
                try {
                    int id = Integer.valueOf(szId);
                    e = Entry.searchDefinition(id);
                } catch (Exception ex) {
                    InOut.printError(ex, out);
                    return;
                }
            } else {szTitle=""; e = new Entry(); return; } //Avoid XSS
%>

    <h3><%=szTitle%>:</h3>
    <form id="addForm" action="index.jsp" method="post"  accept-charset="UTF-8">
        <p> <label class="name"><%= r.getString("word")%><em>*</em></label>
            <input id="word" type="text" name="word" class="required" size="25" value="<%=e.getWord()%>"/>
            <label id="info_word" class="info hidden">
                Tenga en cuenta que el par {t�rmino, morfolog�a} no puede repetirse.
            </label>
        </p>
        <p>
            <label class="name"><%= r.getString("morf")%></label>
            <input style="float:left" id="morfology" type="text" name="morfology"  size="25" value="<%=e.getMorfology()%>"/>
            <label id="info_morf" class="info hidden">
                Ejemplos: v. = verbo, f. = femenino; m. = masculino, adj. = adjetivo; adv. = adverbio;
                prep. = preposici&oacute;n; conj. = conjunci&oacute;n; pron. = pronombre; loc. = locuci&oacute;n;
            </label>
        </p><br clear="left"/>
        <!-- Definitions -->
        <p id="definitions">
            <label class="name"><%= r.getString("def")%><em>*</em></label>
            <textarea cols="24" rows="6" name="def" class="required"><%=e.getDefinition().get(0)%></textarea>
            <a href="#" onclick="addDefinition()">[+]</a>

            <% for (int i = 1; i < e.getDefinition().size(); i++) {%>
            <div style="margin-top:5px;" id="<%=i%>"><label>&nbsp;</label>
                <textarea class="ta_def" cols="24" rows="6" name="def"><%=e.getDefinition().get(i)%></textarea>
                <a style="color:red; margin-left:2px;position:absolute;top:0;right:0" href="#" onclick="delDefinition(<%=i%>)">[x]</a>
            </div>
            <% }%>

        </p>
        <p  style="margin-left:14em;">
            <input type="submit" value="<%=szTitle%>"/>
            <!--<input type="reset" value="<%= r.getString("clear")%>"/>-->
            <input type="hidden" id="id" name="id" value="<%=szId%>" />
            <input type="hidden" name="action" value="4" />
            <input type="hidden" name="op" value="<%=szMod%>"/>
        </p>
    </form>
    <p>* Indica campo obligatorio.</p>
<!--<div id="validate" ondblclick="$('#validate').load('ajax_handler.jsp?id='+$('#id').val()+'&amp;word='+$('#word').val()+'&amp;morfology='+$('#morfology').val())"
 style="border:1px dashed black; min-height:1em;"></div>-->
<%
    } else {
        /***************************************************
         *   Try to apply the modifications to the word    *
         **************************************************/
        String id  = request.getParameter("id");
        String wrd = request.getParameter("word").trim();
        String mrf = request.getParameter("morfology").trim();
        String[] arrayDef = request.getParameterValues("def");

        if (userLogged) {
            int res = 0;
            try {
                if (szOp.equalsIgnoreCase("modify")) {
                    res = Entry.modifyWord(id, wrd, mrf, arrayDef);
                    //out.println("Ahora se modificar�a la palabra"); //DEBUG
                } else if (szOp.equalsIgnoreCase("add")) {
                    res = Entry.addWord(wrd, mrf, arrayDef);
                    //out.println("Ahora se a�adir�a la palabra"); //DEBUG
                }
            } catch (Exception ex) {
                InOut.printError(ex, out);
                res = 0;
            }
            //if (res == null || res.length() == 0) { //TODO decide the @return Entry.add/update/delete
            if (res != -1 ) {
                out.println("<h3 id='msg' class='ok'>Word " + szOp + " succesfully</h3>");
                /*** DEBUG MODE ***/
                out.println("<p>Id: " + res + "<br/>");
                out.println("Word: " + wrd + "<br/>");
                out.println("Morf: " + mrf + "<br/>");
                out.print("Definitions: <ol>");
                for (String aux : arrayDef) {
                    if(!aux.length() == 0)
                        out.println("<li>" + aux + "</li>");
                }
                out.println("</ol>");
                /****END DEBUGG MODE *****/
            } else {
                //InOut.printError(new Exception(res), out);
                InOut.statusInfo(out); // TODO: test me
            }
        } else {%>
        <h3 class="error">Sorry but you are not allowed to add/modify a word</h3>
        <p>You must be logged to perform this action.</p> <%
        }
    }    /** END if szOp */
%>