<%@page import="java.util.Calendar"%>
<%@page import="java.util.ArrayList"%>
<%@page import="java.util.HashMap"%>
<%@include file="includes/head.jsp"%>
<%@include file="includes/header.jsp"%>
	<div class="container" id="container-eurostat-quantitative">
		<%@include file="includes/breadcrumb.jsp"%>
		<h1>Quantitative data from Eurostat</h1>

		<%@include file="includes/alerts.jsp"%>

        <form action="user?page=quantitative_eurostat" method="post" enctype="multipart/form-data">
			<label>Indicator</label>
			<% ArrayList<HashMap<String,String>> indicatorsList = (ArrayList<HashMap<String,String>>) request.getAttribute("indicatorsList"); 
			String indicatorEurostat = (String) request.getAttribute("indicatorEurostat");
			if(indicatorEurostat == null){
				indicatorEurostat = "31";
			}
			%>
			<select id="indicatorEurostat" name="indicatorEurostat" onchange="changeYearCombos()">
			<% for (HashMap<String,String> data : indicatorsList) { %>
				<option value="<%=data.get("indicator_id")%>" <%= data.get("indicator_id").equals(indicatorEurostat) ? "selected": "" %> >
					<%=data.get("indicator_name") %>
				</option>
			<% } %>
			</select>
			<div class="clear-content"></div>
			<label>Year</label>
			<!-- FOR INDICATORS Income per capita, Income per capita EURO, Non-fatal work accidents and Fatal work accidents -->
			<div id="yearFromContainer" class="conten-input">
				<%int year = Calendar.getInstance().get(Calendar.YEAR);%>
				<% String yearFromSelected = (String) request.getAttribute("yearFrom");
				String yearToSelected = (String) request.getAttribute("yearTo");
				String oneYearSelected = (String) request.getAttribute("oneYear");
				if(yearFromSelected == null){
					yearFromSelected = year+"-01-01";
				}
				if(yearToSelected == null){
					yearToSelected = year+"-12-31";
				}
				if(oneYearSelected == null){
					oneYearSelected = year+"-01-01";
				}
				%>
				<label class="help-text">From:</label>
				<select name="yearFrom">
					<% for (int i = year; i >= 2010; i--) { %>
					<option value="<%=i%>-01-01" <%= yearFromSelected.equals(i+"-01-01") ? "selected": "" %> ><%=i%></option>
					<% } %>
				</select>
			</div>
			<div id="yearToContainer" class="conten-input">
				<label class="help-text">To:</label>
				<select name="yearTo">
					<% for (int i = year; i >= 2010; i--) { %>
					<option value="<%=i%>-12-31" <%= yearToSelected.equals(i+"-12-31") ? "selected": "" %> ><%=i%></option>
					<% } %>
				</select>
			</div>
			<div id="oneYearContainer" class="conten-input">
				<!-- FOR THE REST OF THE INDICATORS -->
				<select name="oneYear" >
					<% for (int i = year; i >= 2013; i--) { %>
					<option value="<%=i%>-01-01" <%= oneYearSelected.equals(i+"-01-01") ? "selected": "" %> ><%=i%></option>
					<% } %>
				</select>
			</div>
			<div class="clear-content"></div>
			<label>File</label>
			<label id="templateUsage" class="help-text">The template should be "EU-OSHA_OIE_Eurostat_Income_per_capita_YYYY-MM-DD"</label>
			<input type="file" name="quantitativeEurostatFile" required accept="application/vnd.openxmlformats-officedocument.spreadsheetml.sheet">
			<!-- <button>Download Excel templates</button> -->
			<span class="with-tooltip"><a class="href-link" href="files/Eurostat_Quantitative_Templates.zip">Download Excel templates</a><span class="tootip-box">Download the Eurostat Excel templates</span></span>
			<div class="clear-content"></div>
			<div class="conten-button">
				<button name="clearButton" value="Clear" >Clear</button>
				<button type="submit" name="formSent" value="Submit">Submit</button>
			</div>
		</form>
	</div>
<%@include file="includes/footer.jsp"%>