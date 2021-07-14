<%@page import="java.util.Calendar"%>
<%@page import="java.util.ArrayList"%>
<%@page import="java.util.HashMap"%>
<%@include file="includes/head.jsp"%>

<div class="wrapper">
	<%@include file="includes/header.jsp"%>
	<div class="container" id="container-eurostat-quantitative">
		<%@include file="includes/breadcrumb.jsp"%>
		<h1>Quantitative data from Eurostat</h1>

		<%@include file="includes/alerts.jsp"%>
		<div id="wait-message" class="alert-success">
		    <p>The update of your data is now being processed, it can take a few minutes. Please do not reload the page just wait some time until the confirmation message is displayed</p>
		</div>
		<div id="wait-message-space" class="clear-content"></div>

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
					<%=data.get("indicator_name_2") %>
				</option>
			<% } %>
			</select>
			<div class="clear-content"></div>
			<label>Year</label>
			<label class="help-text">Reference year</label>
			<!-- FOR INDICATORS Income per capita, Income per capita EURO, Non-fatal work accidents and Fatal work accidents -->
			<div id="yearFromContainer" class="conten-input">
				<%int year = Calendar.getInstance().get(Calendar.YEAR);%>
				<% String yearFromSelected = (String) request.getAttribute("yearFrom");
				String yearToSelected = (String) request.getAttribute("yearTo");
				String oneYearSelected = (String) request.getAttribute("oneYear");
				//yearFromSelected = yearFromSelected+"-01-01";
				System.out.println("YEAR FROM: "+yearFromSelected);
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
			<div class="info-container">
				<label class="help-text"><b>Download a template to enter updated data</b></label>
				<span class="with-tooltip"><a class="href-link" href="files/Eurostat_Quantitative_Templates.zip">Download Excel templates</a><span class="tootip-box">Download the Eurostat Excel templates</span></span>
			</div>
			
			<label id="templateUsage" class="help-text">The template should be "EU-OSHA_OIE_Eurostat_Income_per_capita_YYYY-MM-DD"</label>
			
			<div class="clear-content"></div>
			<label>File</label>
			<label class="help-text">Select file to be uploaded</label>
			<input type="file" name="quantitativeEurostatFile" required accept="application/vnd.openxmlformats-officedocument.spreadsheetml.sheet">
			<!-- <button>Download Excel templates</button> -->
			
			<div class="clear-content"></div>
			<div class="conten-button">
				<button name="clearButton" value="Clear" >Clear</button>
				<button type="submit" name="formSent" value="Submit" onclick="showWaitAlert()">Submit</button>
			</div>
		</form>
	</div>
</div>
<%@include file="includes/footer.jsp"%>