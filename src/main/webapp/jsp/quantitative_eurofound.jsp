<%@page import="java.util.Calendar"%>
<%@include file="includes/head.jsp"%>
<%@include file="includes/header.jsp"%>
	<div class="container" id="quantitative-page">
		<%@include file="includes/breadcrumb.jsp"%>
		<h1>Quantitative data from Eurofound</h1>
		
		<%@include file="includes/alerts.jsp"%>
		
		<form action="user?page=quantitative_eurofound" method="post" enctype="multipart/form-data">
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
			<!-- File input -->
			<label>File</label> 
			<input type="file" name="quantitativeEurofoundFile" required accept="application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"> 
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