<%-- 
                        Document   : about
                        Created on : 14-ago-2008, 16:37:03
                        Author     : chiron
--%>

<%@ include file="lang.jspf" %>

<div id="about">
    <h3>Acerca de...</h3>
    <h4>... el benasqu&eacute;s o patu&eacute;s</h4>
    <p><a href="http://an.wikipedia.org/wiki/Benasqu%C3%A9s">Wikipedia</a>
    </p>
    <h4>... este proyecto</h4>
    <p>
        El proyecto surge como una necesidad de mantener viva una lengua, o 
        lo que es lo mismo, una cultura y una manera de ver y hacer las cosas.
        Una lengua se mantiene viva gracias a la gente, a sus hablantes, por 
        ello su difusi&oacute;n es fundamental para que continue formando parte 
        de nuestra vida.<br/>
        Las nuevas tecnolog&iacute;as nos proporcionan potentes m&eacute;todos
        para contribuir a esta labor, de aqu&iacute; el origen de este portal.
    </p>
    <p>
        <%
        String szGuayen = "<a href='http://www.guayente.org'>Asociaci&oacute;n Guayente</a>";
        out.print(MessageFormat.format(rAbout.getString("author"), szGuayen));
        %>
    </p>
    
    <p><%
        String mail = "<a href='mailto:admin947(AT)gmail.com'>admin947(AT)gmail.com</a>";
        out.print(MessageFormat.format(rAbout.getString("colaborate"), mail));
        %>
    </p>

    <p>Su c&oacute;digo fuente est&aacute; disponible en:
        <i>http://code.google.com/p/jsp-web-dictionary/</i>
    </p>

    <h3>Agradecimientos</h3>
    <ul>
        <li>A Mar&iacute;a Jos&eacute; Subir&aacute;, Carmen Cast&aacute;n y
            Jos&eacute; Antonio Saura Rami; por la traducci&oacute;n al 
        patu&eacute;s</li>
        <li>A Dabi, por la traducci&oacute;n al aragon&eacute;s</li>
        <li>A &Aacute;ngel Ballar&iacute;n, por su trabajo realizado para crear
            un diccionario que, junto a los vocablos, recoge costumbres y 
            tradiciones del valle de Benasque</li>
    </ul>
    <div id="logos">
          <img src="img/guayen.png" alt="Asociaci&oacute;n Guayente" />
          <img src="img/tomcat.gif" alt="Powered by Tomcat" />
          <img src="img/mysql.png" alt="MySQL Powered" />
          <img src="img/java.jpg" alt="Java Powered" />
    </div>
</div>
