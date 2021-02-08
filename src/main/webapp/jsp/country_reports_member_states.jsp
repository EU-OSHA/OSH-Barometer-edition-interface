<%@page import="java.util.ArrayList"%>
<%@page import="java.util.HashMap"%>
<%@include file="includes/head.jsp"%>

<div class="wrapper">
	<%@include file="includes/header.jsp"%>
	<div class="container" id="country-report-member-states">
		<%@include file="includes/breadcrumb.jsp"%>
		<h1>Country Report from Member States</h1>
		<%@include file="includes/alerts.jsp"%>
		<form action="user?page=country_reports_member_states" method="post" enctype="multipart/form-data">
			<div class="conten-input">
				<label>Section</label>
				<% String section_id = (String) request.getAttribute("section_id"); %>
				<select id="section" name="section_id" onchange="changeCountryDisplay()">
					<option value="osh_authorities" <%="osh_authorities".equals(section_id) ? "selected": "" %> >OSH Authorities</option>
					<option value="national_strategies" <%="national_strategies".equals(section_id) ? "selected": "" %> >National strategies</option>
					<option value="social_dialogue" <%="social_dialogue".equals(section_id) ? "selected": "" %>>Social Dialogue</option>
				</select>
			</div>
			
			<div class="conten-input">
				<label>Country</label>
				<% ArrayList<HashMap<String,String>> countryList = (ArrayList<HashMap<String,String>>) request.getAttribute("countryList");
				String countrySelected = (String) request.getAttribute("countrySelected");
				%>
				<select id="country" name="country">
					<% for (HashMap<String,String> data : countryList) { %>
					<option value="'<%=data.get("country_name")%>'" <%= data.get("country_name").equals(countrySelected) ? "selected": "" %> >
						<%=data.get("country_name")%>
					</option>
					<% } %>
				</select>
			</div>
			
			<div class="clear-content"></div>
			<!-- File input -->
			<input type="file" name="pdfFile" required accept="application/pdf">
			
			<div class="clear-content"></div>
			<div class="conten-button">
				<button name="clearButton" value="Clear" >Clear</button>
				<button type="submit" name="formSent" value="Submit">Submit</button>
			</div>
		</form>
	</div>
</div>
<%@include file="includes/footer.jsp"%>