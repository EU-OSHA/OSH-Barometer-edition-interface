<header>
	<% String currentPage = (String) request.getAttribute("page"); %>
	<div class="container">
		<a href="https://test-visualisation.osha.europa.eu/osh-barometer#!/" target="_blank">
			<img src="images/logo.png" alt="Baromether Logo" title="OSH BAROMETER Home">
		</a>		
		<% if(currentPage != null) {
			if(!currentPage.equals("login")) { %>
			<div class="content-header-button">
				<form action="uicontroller?page=login" method="post">
					<input type="hidden" name="logout" value="1">
					<button type="submit" class="login">Logout</button>					
				</form>
			</div>
		<% }
			} %>
	</div>
</header>