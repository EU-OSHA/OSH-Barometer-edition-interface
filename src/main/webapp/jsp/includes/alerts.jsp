<%
    String errorMessage = (String) request.getAttribute("errorMessage");
    if(errorMessage != null){
%>
<div class="alert-danger">
    <p><%=errorMessage%></p>
</div>
<div class="clear-content"></div>
<%
    }
    String confirmationMessage = (String) request.getAttribute("confirmationMessage");
    if(confirmationMessage != null){
%>
	<div class="alert-success">
	    <p><%=confirmationMessage%></p>
	</div>
	<div class="clear-content"></div>
<% } %>