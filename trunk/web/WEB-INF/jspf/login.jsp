<%-- any content can be specified here e.g.: --%>
<%@ page pageEncoding="UTF-8" %>
<%@page import="database.User" %>

<% if(User.moduleEnabled()) {
%>
<h3>Login</h3>
<div id="login">
    <%
	String szLogin, szError, szUser;
	szError = null;
	szLogin = request.getParameter("log_action");
	if(szLogin != null) {
		if(szLogin.compareTo("login") == 0) {
			String user = request.getParameter("user");
			String pass = request.getParameter("passwd");

            if(User.authenticate(user, pass)) {
				session.setAttribute("user", user);
                //AJAX RELOAD MENU!
                %>
                <script type="text/javascript">
                    $(document).ready(function() {
                        $("#bar").fadeOut(200);
                        $("#bar").load("index.jsp #bar ul");
                        $("#bar").fadeIn(300);
                    });
                </script>
                <%
			}
			else {
			    szError = "Error de user o password";
			}
		}
		else if(szLogin.compareTo("logout") == 0) {
			session.removeAttribute("user");
            //AJAX RELOAD MENU!
            %>
            <script type="text/javascript">
                $(document).ready(function() {
                    $("#bar").fadeOut(200);
                    $("#bar").load("index.jsp #bar ul");
                    $("#content").load("index.jsp?"+$.getUrlParams()+" #content")
                    $("#bar").fadeIn(300);
                });
            </script>
            <%
		}
	}

	szUser = (String) session.getAttribute("user");
	if (szUser != null) // User identified
	{
        %>
        Welcome back, <i> <%=szUser %> </i>
        <%
	}
	else {
        %>
			<form action="index.jsp?action=6" method="post"><div>
				<label>user: </label><input name="user" size="10" />
				<label>password: </label><input type="password" name="passwd" size="10" />
				<input type="submit" name="log_action" value="login" />
			</div></form>
	<% }
	if(szError != null) {
		out.print("<p class='error' style='display:inline;position:absolute;right:0;'>"+szError+"</p>");
	} %>
</div>
<% } else { %>
    <h3>El m&oacute;dulo de login est&aacute; desactivado.</h3>
<% } %>