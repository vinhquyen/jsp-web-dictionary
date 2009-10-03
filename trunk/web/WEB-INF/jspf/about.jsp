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
        Surge como una necesidad de mantener viva una lengua, o
        lo que es lo mismo, una cultura y una manera de ver y hacer las cosas.
        Una lengua se mantiene viva gracias a la gente, a sus hablantes, por 
        ello su difusi&oacute;n es fundamental para que continue formando parte 
        de nuestra vida. Las nuevas tecnolog&iacute;as nos proporcionan potentes
        m&eacute;todos para contribuir a esta labor, de aqu&iacute; el origen de
        este portal. Para m&aacute;s informaci&oacute;n puede leer el
        <a href="#" onclick="setVisibility('manifest')">manifiesto</a> (en benasqués).
    </p>
    <p>
        <%
        String szGuayen = "<a href='http://www.guayente.org'>Asociaci&oacute;n Guayente</a>";
        out.print(MessageFormat.format(rAbout.getString("author")+" ", szGuayen));
        
        String mail = "<strong>admin947<img src='img/arroba.gif' alt='@' style='border:0; height:10px; width:12px; vertical-align:middle;' />gmail.com</strong>";
        out.print(MessageFormat.format(rAbout.getString("colaborate"), mail));
        %>
    </p>

    <p>Su c&oacute;digo fuente est&aacute; disponible en: 
        <a href="http://jsp-web-dictionary.googlecode.com">http://jsp-web-dictionary.googlecode.com</a>
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
    <div id="manifest" class="hidden" onclick="setVisibility('manifest')">
        <jsp:include page="manifest.jsp" />
    </div>
</div>
