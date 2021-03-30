<%@include file="includes/head.jsp"%>

<div class="wrapper">
	<%@include file="includes/header.jsp"%>
	<div class="container">
		<%@include file="includes/alerts.jsp"%>
		<h1>Log in</h1>
		<div class="login">
			<form action="user?page=home" method="post">
				<label>Email:<span class="mandatory">*</span></label> 
				<input type="text" name="username" autofocus required placeholder="username or e-mail" title="Complete this field"> 
				<label class="help-text">Enter your username</label>
				<div class="clear-content"></div>
				<label>Password:<span class="mandatory">*</span>
				</label> 
					<input type="password" name="password" required placeholder="password" title="Complete this field"> 
				<label class="help-text">Enter the password that accompanies your username</label>
				<div class="clear-content"></div>
				<button>Log in</button>
			</form>
		</div>
		<div class="clear-content"></div>
		<div>
			<p style="font-size: 13px;">If LDAP Login fails click here to proceed to the home page</p>
			<form action="user?page=home" method="post">
				<input type="hidden" name="temporalLogin" value="true">
				<input type="hidden" name="username" value="admin">
				<input type="hidden" name="password" value="admin">
				<button>Log in</button>
			</form>
		</div>
	</div>
</div>
<%@include file="includes/footer.jsp"%>