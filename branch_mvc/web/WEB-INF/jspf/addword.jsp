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
String szOp = request.getParameter("op");


/**********************************
*   Try to insert the new word    *
**********************************/
if(szOp != null && szOp.equalsIgnoreCase("add")) {
    String paramEncoding = application.getInitParameter("parameter-encoding");
    request.setCharacterEncoding(paramEncoding); //"UTF-8" --> Defined in web.xml

    String wrd = request.getParameter("word");
    String mrf = request.getParameter("morfology");
   
    
    String[] arrayDef = request.getParameterValues("def");
    

    /** TODO: check data in View Tier */
    out.println("<p>Word: " + wrd + "<br/>");
    out.println("Morf: "	+ mrf + "<br/>");
    out.print("Definitions: <ol>");
    for(String aux : arrayDef) out.println("<li>"+aux+"</li>");
    out.println("</ol>");

    // TODO: Is the user logged? (and is granted)
    if(userLogged /*request.getParameter("secret") != null*/) {
        String res = null;
        try {
            res = Entry.addWord(wrd, mrf, arrayDef);
            //DEBUG out.println("Ahora se añadiría la palabra");
        }catch (Exception e){
            InOut.printError(e, out);
            res = "";
        }
        if(res == null)
            out.println("<h3>Word added succesfully</h3>");
        else
            InOut.printError(new Exception(res), out);
    }
    else {
        %>
         <h3 style="color:red;">Sorry but you are not allowed to add the word</h3>
          <p>Please contact with the webmaster to get more information.</p>
        <%
    }
/********************************
*   Get the data of new word    *
********************************/
} else {
    //response.setCharacterEncoding("UTF-8");
    // TODO --> encoding when method="post"...
%>
<h3><%= r.getString("add")%>:</h3>
<form id="addForm" action="index.jsp" method="get"  accept-charset="UTF-8">
    <p> <label for="word"><%= r.getString("word")%><em>*</em></label>
        <input type="text" name="word" class="required" size="25"/>
    </p>
    <p>
        <label><%= r.getString("morf")%></label>
        <input type="text" name="morfology" size="25"/>
    </p>
    <p id="definitions">
        <label for="Definition"><%= r.getString("def")%><em>*</em></label>
        <textarea cols="24" rows="6" name="def" class="required" minlength="10"></textarea>
        <a href="#" onclick="addDefinition()">[+]</a>
    </p>
    <p>
        <input class="submit" type="submit" value="<%= r.getString("addword")%>"/>
        <!--<input type="reset" value="<%= r.getString("clear")%>"/>-->
    </p>
    <input type="hidden" name="action" value="1" />
    <input type="hidden" name="op" value="add" />
    <input type="hidden" name="numDef" value="1" />
</form>
<%} %>
