<%@include file="includes/head.jsp"%>
<body>
	<%@include file="includes/header.jsp"%>
	<div class="container">
		<h1>Log in</h1>
		<div class="login">
			<form action="uicontroller?page=home" method="post">
				<label>Email:<span class="mandatory">*</span></label> 
				<input type="text" name="username" autofocus required placeholder="username or e-mail" title="Complete this field"> 
				<label class="help-text">Enter your username</label>
				<div class="clear-content"></div>
				<label>Password:<span class="mandatory">*</span>
				</label> 
					<input type="password" name="password" required placeholder="password" title="Complete this field"> 
				<label class="help-text">Enter the password that accompanies your username</label>
				<div class="clear-content"></div>
				<%
	                String errorMessage = (String) request.getAttribute("errorMessage");
	                if(errorMessage != null){
	            %>
	            <div>
	                <p class="error"><%=errorMessage%></p>
	            </div>
	            <%
	                }
	            %>
				<button>Log in</button>
			</form>
		</div>
	</div>
</body>
<%@include file="includes/footer.jsp"%>
</html>