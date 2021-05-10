<%@page import="eu.europa.osha.barometer.edition.webui.bean.User"%>
<header>
	<% String currentPage = (String) request.getAttribute("page");
		User sessionUser = (User) request.getSession().getAttribute("user");
	%>
	<div class="container">
		<a href="https://pre-visualisation.osha.europa.eu/osh-barometer#!/" target="_blank">
			<img src="images/logo.png" alt="Baromether Logo" title="OSH BAROMETER Home">
		</a>		
		<% if(currentPage != null) {
			if(!currentPage.equals("login") && sessionUser != null) { %>
			<div class="content-header-button">
				<form action="user?page=login" method="post">
					<input type="hidden" name="logout" value="1">
					<button type="submit" class="login">Logout</button>					
				</form>
			</div>
		<% }
		} %>
	</div>
</header>