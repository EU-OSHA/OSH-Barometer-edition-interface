<%@page import="java.util.Calendar"%>
<%@include file="includes/head.jsp"%>
<%@include file="includes/header.jsp"%>
	<div class="container" id="quantitative-page">
		<%@include file="includes/breadcrumb.jsp"%>
		<h1>Quantitative data from Eurofound</h1>
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
		<form action="user?page=quantitative_eurofound" method="post" enctype="multipart/form-data">
			<!-- Year input -->
			<label>Year</label>
			<%int year = Calendar.getInstance().get(Calendar.YEAR);%>
			<!-- Values 20XX-01-01 -->
			<select name="year">
				<% for (int i = year; i >= 2015; i--) { %>
				<option value="<%=i%>-01-01"><%=i%></option>
				<% } %>
			</select>
			<div class="clear-content"></div>
			<!-- File input -->
			<label>File</label> 
			<input type="file" name="quantitativeEurofoundFile" required accept=".csv, application/vnd.openxmlformats-officedocument.spreadsheetml.sheet, application/vnd.ms-excel"> 
			<!-- <button class="upload">Upload</button> -->
			<!-- <button>Download Excel template</button> -->
			<!-- <a class="href-link" href="files/EU-OSHA_OSH BAROMETER_EUROFOUND Indicator List_YYYYMMDD.xlsx">Download Excel templates</a> -->
			<span class="with-tooltip"><a class="href-link" href="files/EU-OSHA_OSH BAROMETER_EUROFOUND Indicator List_YYYYMMDD.xlsx">Download Excel templates</a><span class="tootip-box">Download the Eurofound Excel template</span></span>
			<div class="clear-content"></div>
			<div class="conten-button">
				<button name="clearButton" value="Clear" >Clear</button>
				<button type="submit" name="formSent" value="Submit">Submit</button>
			</div>
		</form>
	</div>
<%@include file="includes/footer.jsp"%>