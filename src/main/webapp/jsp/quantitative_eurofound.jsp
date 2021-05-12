<%@page import="java.util.Calendar"%>
<%@include file="includes/head.jsp"%>
<div class="loading-screen">
	<div class="loader"></div>
	<div class="message">
		<span>This process can take several minutes. A new process cannot be executed again until a message appears indicating if the current process
		has failed or succeeded.</span>
	</div>
</div>
<div class="wrapper" id="quantitative-page">
	<%@include file="includes/header.jsp"%>
	<div class="container">
	<%@include file="includes/breadcrumb.jsp"%>
		<h1>Quantitative data from Eurofound</h1>
		
		<%@include file="includes/alerts.jsp"%>
		<div id="wait-message" class="alert-success">
			<p>The update of your data is now being processed, it can take a few minutes. Please do not reload the page just wait some time until the confirmation message is displayed</p>
		</div>
		<div id="wait-message-space" class="clear-content"></div>
		
		<form action="user?page=quantitative_eurofound" method="post" enctype="multipart/form-data" onsubmit="loadingScreen()">
			<!-- Year input -->
			<label>Year</label>
			<%int year = Calendar.getInstance().get(Calendar.YEAR);%>
			<% String yearSelected = (String) request.getAttribute("year"); 
			System.out.println("yearSelected: "+yearSelected);
				if(yearSelected == null){
					yearSelected = year+"-01-01";
				}%>
			<!-- Values 20XX-01-01 -->
			<select name="year">
				<% for (int i = year; i >= 2015; i--) { %>
				<option value="<%=i%>-01-01" <%= yearSelected.equals(i+"-01-01") ? "selected": "" %> ><%=i%></option>
				<% } %>
			</select>
			<div class="clear-content"></div>
			<div>
				<label class="help-text"><b>Download a template to enter updated data</b></label>
				<span class="with-tooltip">
					<a class="href-link" href="files/EU-OSHA_OSH BAROMETER_EUROFOUND Indicator List_YYYYMMDD.xlsx">Download Excel template</a>
					<span class="tootip-box">Download the Eurofound Excel template</span>
				</span>
			</div>
			<div class="clear-content"></div>
			<!-- File input -->
			<label>File</label> 
			<input type="file" name="quantitativeEurofoundFile" required accept="application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"> 
			<!-- <button class="upload">Upload</button> -->
			<!-- <button>Download Excel template</button> -->
			<!-- <a class="href-link" href="files/EU-OSHA_OSH BAROMETER_EUROFOUND Indicator List_YYYYMMDD.xlsx">Download Excel templates</a> -->
			<div class="clear-content"></div>
			<div class="conten-button">
				<button name="clearButton" value="Clear" >Clear</button>
				<button type="submit" name="formSent" value="Submit" onclick="showWaitAlert()">Submit</button>
			</div>
		</form>
	</div>
</div>
<%@include file="includes/footer.jsp"%>