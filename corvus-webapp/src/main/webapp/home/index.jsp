<%@page import="hk.hku.cecid.piazza.commons.Sys"%>
<%@page import="hk.hku.cecid.piazza.corvus.core.Kernel"%>
<%@page import="java.util.Properties"%>
<%@page contentType="text/html;charset=UTF-8"%>
<html>
<head>
	<title>Hermes2+</title>
</head>
<body>
<h2>Welcome to Hermes2+ Business Messsaging Gateway</h2>
<hr>
<p>
&gt; Hermes2+ has started up <%=Kernel.getInstance().hasErrors()? "with errors":"successfully"%>.
<p>
&gt; System name: <%=Sys.main.getName()%>
<p>
&gt; System version: <%=Sys.main.getVersion()%>

<!-- Java System Properties -->
<!--
<%System.getProperties().list(new java.io.PrintWriter(out));%>
-->
</body>
</html>