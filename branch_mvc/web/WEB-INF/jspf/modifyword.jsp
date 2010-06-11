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
                    e = Entry.getDefinition(id);
                } catch (Exception ex) {
                    InOut.printError(ex, out);
                    return;
                }
            } else {szTitle=""; e = new Entry(); return; } //Avoid XSS
%>

<h3><%=szTitle%>:</h3>
<form id="addForm" action="index.jsp" method="get"  accept-charset="UTF-8">
    <input type="hidden" name="id" value="<%=szId%>" />
    <p> <label for="word"><%= r.getString("word")%><em>*</em></label>
        <input type="text" name="word" class="required" size="25" value="<%=e.getWord()%>"/>
    </p>
    <p>
        <label><%= r.getString("morf")%></label>
        <input type="text" name="morfology"  size="25" value="<%=e.getMorfology()%>"/>
    </p>
    <!-- Definitions -->
    <p id="definitions">
        <label for="Definition"><%= r.getString("def")%><em>*</em></label>
        <textarea cols="24" rows="6" name="def" class="required" minlength="10"><%=e.getDefinition().get(0)%></textarea>
        <a href="#" onclick="addDefinition()">[+]</a>

        <% for (int i = 1; i < e.getDefinition().size(); i++) {%>
        <div style="margin-top:5px;" id="<%=i%>"><label>&nbsp;</label>
            <textarea cols="24" rows="6" name="def" minlength="10"><%=e.getDefinition().get(i)%></textarea>
            <a style="color:red; margin-left:2px;" href="#" onclick="delDefinition(<%=i%>)">[x]</a>
        </div>
        <% }%>

    </p>
    <p  style="margin-left:14em;">
        <input type="submit" value="<%=szTitle%>"/>
        <!--<input type="reset" value="<%= r.getString("clear")%>"/>-->
    </p>
    <input type="hidden" name="action" value="4" />
    <input type="hidden" name="op" value="<%=szMod%>"/>
</form>

<%
    } else {
        /***************************************************
         *   Try to apply the modifications to thw word    *
         **************************************************/
        String id  = request.getParameter("id");
        String wrd = request.getParameter("word");
        String mrf = request.getParameter("morfology");
        String[] arrayDef = request.getParameterValues("def");


        /** TODO: check data in View Tier */
        //Enumeration eParamNames = request.getParameterNames();
        //while(eParamNames.hasMoreElements())
        //   out.println(eParamNames.nextElement()+"<br/>");
        /*** DEBUG MODE ***/
        out.println("<p>Id: " + id + "<br/>");
        out.println("Word: " + wrd + "<br/>");
        out.println("Morf: " + mrf + "<br/>");
        out.print("Definitions: <ol>");
        for (String aux : arrayDef) {
            out.println("<li>" + aux + "</li>");
        }
        out.println("</ol>");
        /****END DEBUGG MODE *****/
        if (userLogged /*request.getParameter("secret") != null*/) {
            String res = null;
            try {
                if (szOp.equalsIgnoreCase("modify")) { /* TODO */
                    //DEBUG res = Entry.updateWord(id, wrd, mrf, arrayDef);
                    out.println("Ahora se modificaría la palabra"); //DEBUG
                } else if (szOp.equalsIgnoreCase("add")) {
                    //DEBUG res = Entry.addWord(wrd, mrf, arrayDef);
                    out.println("Ahora se añadiría la palabra"); //DEBUG
                }
            } catch (Exception ex) {
                InOut.printError(ex, out);
                res = "";
            }
            if (res == null) {
                out.println("<h3>Word added/modified succesfully</h3>");
            } else {
                InOut.printError(new Exception(res), out);
            }
        } else {%>
        <h3 class="error">Sorry but you are not allowed to add/modify a word</h3>
        <p>You must be logged to perform this action.</p> <%
        }
    }    /** END if szOp */
%>