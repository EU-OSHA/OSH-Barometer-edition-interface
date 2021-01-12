<%@page import="java.util.Calendar"%>
<footer>
	<%int yearFooter = Calendar.getInstance().get(Calendar.YEAR);%>
	<div class="container">
		© <%=yearFooter %> EU-OSHA | <a href="https://osha.europa.eu/" target="_blank">an
			agency of the European Union</a>
	</div>
</footer>
</body>
</html>