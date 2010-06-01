<%-- 
                        Document   : about
                        Created on : 14-ago-2008, 16:37:03
                        Author     : chiron
--%>

<%@ include file="lang.jspf" %>

<div id="about">
    <h3><%=rAbout.getString("h3_about")%>...</h3>
    <h4>... <%=rAbout.getString("h4_bnq")%></h4>

    <p>
        <a href="http://an.wikipedia.org/wiki/Benasqu%C3%A9s" rel="external">
        <%=rAbout.getString("wiki")%></a> (<%=r.getString("lng_be")%>)
    </p>
    <% String szURI = "about/" + rAbout.getString("uri_about") + ".jspf";%>
    <jsp:include page="<%=szURI %>" />



    <p>
        <%=MessageFormat.format(rAbout.getString("author") + " ", rAbout.getString("uri_guayen"))%>
        <%
        String mail = "<strong>admin947<img src='img/arroba.gif' alt='@' class='arroba'" +
                " />gmail.com</strong>";%>
        <%=MessageFormat.format(rAbout.getString("colaborate"), mail)%>

    </p>

    <p> <%=rAbout.getString("src")%>
        <a href="http://jsp-web-dictionary.googlecode.com" rel="external" title="[new window]">http://jsp-web-dictionary.googlecode.com</a>
    </p>

    <!-- Contributors -->
    <% szURI = "about/" + rAbout.getString("uri_contrib") + ".jspf"; %>
    <jsp:include page="<%=szURI %>" />

    <!-- Logos sponsors -->
    <div id="logos">
        <img src="img/guayen.png" alt="Asociaci&oacute;n Guayente" />
        <img src="img/tomcat.gif" alt="Powered by Tomcat" />
        <img src="img/mysql.png" alt="MySQL Powered" />
        <img src="img/java.jpg" alt="Java Powered" />
    </div>

    <!-- Benasque's manifest -->
    <jsp:include page="manifest.jsp" />
</div>