<%
    String errorMessage = (String) request.getAttribute("errorMessage");
    if(errorMessage != null){
%>
<div id="error-message" class="alert-danger">
    <span><%=errorMessage%></span>
    <span class="closebtn" onclick="closeAlert('error')">x</span>
    <!-- <div class="close close-click" onclick="disableSaveButton()">x</div> -->
</div>
<div id="alert-danger-space" class="clear-content"></div>
<%
    }
    String confirmationMessage = (String) request.getAttribute("confirmationMessage");
    if(confirmationMessage != null){
%>
	<div id="sucess-message" class="alert-success">
	    <span><%=confirmationMessage%></span>
	    <span class="closebtn" onclick="closeAlert('success')">x</span>
	</div>
	<div id="alert-success-space" class="clear-content"></div>
<% } %>


<script>
	closeAlert = function(type){
		//console.log('Enters in closeAlert function.');
		if(type == 'error'){
			$('#error-message').css("display","none");
			$('#alert-danger-space').css("display","none");
			<%errorMessage = null;%>
		}else{
			$('#sucess-message').css("display","none");
			$('#alert-success-space').css("display","none");
			<%confirmationMessage = null;%>
		}
	}
</script>