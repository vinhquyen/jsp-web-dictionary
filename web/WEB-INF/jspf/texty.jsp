<%@ page pageEncoding="UTF-8" %>

<%@ include file="lang.jspf" %>

<% /** TODO: ENABLE texty */
if (rConf.getString("textyModule").equalsIgnoreCase("enabled") &&
    request.getParameter("id") == null &&
    (request.getParameter("word") == null ||
     request.getParameter("word").length() == 0)
) {%>
    <!-- texty: allows modifications withouth a new publication (In example: small news, etc) -->
    <script type="text/javascript"
            src="http://texty.com/cms/syndicate/25bb24b5-4279-4334-839f-59c04b376eab.js"></script>
    <!-- end texty -->
<% } %>

