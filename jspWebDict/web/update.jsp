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
			String mrf = request.getParameter("morfology");
			String def = request.getParameter("def");	
			String exs = request.getParameter("ex");
			
			out.println("<p>Word: " + wrd + "<br/>");
			out.println("Def: "		+ def + "<br/>");
			out.println("Morf: "	+ mrf + "<br/>");
			out.println("Examples: "+ exs + "</p>" );
			//TODO: request.getRemoteHost(); auth por host?
			// request.getRemoteAddr();
			
			if(request.getParameter("secret") != null) {
				String res = null;
				try {
					res = Entry.updateWord(wrd, mrf, def, exs);
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
				  <p>Please contact with the webmaster to get more
				   information.</p>
				<%
			}
		 %>
		  <p><a href="javascript:history.go(-1);">Go Back</a></p>
    </body>
</html>
