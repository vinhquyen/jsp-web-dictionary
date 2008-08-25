<%-- 
    Document   : insert
    Created on : 29-jul-2008, 0:59:46
    Author     : chiron
--%>
<%@page import="database.*" %>
<%@page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>

<!DOCTYPE html 
     PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN"
     "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">

<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en">
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
        <title>New Word Inserted</title>
		<link rel="stylesheet" href="css/main.css" type="text/css" />
    </head>
    <body>
        <h2>Inserting new word!</h2>
		<%
			request.setCharacterEncoding("UTF-8");
			//NOTA: the term always is saved in lowerCase format!
			String wrd = request.getParameter("word").toLowerCase();
			String def = request.getParameter("def");
			String mrf = request.getParameter("morfology");
			
			out.println("<p>Word: " + wrd + "<br/>");
			out.println("Def: " + def + "<br/>");
			out.println("Morfology: " + mrf + "</p>");
			
			String res = null;
			try {
				res = Entry.addWord(wrd, mrf, def);
			}catch (Exception e){
			    InOut.printError(e, out);
			    return;
			}
			if(res == null)
			    out.println("<h3>Word added succesfully</h3>");
			else
			    out.println("<h3>ERROR: " + res + "</h3>");
		 %>
    </body>
</html>
