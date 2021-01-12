<%@page import="java.util.ArrayList"%>
<%@page import="java.util.HashMap"%>
<%@page import="eu.europa.osha.barometer.edition.webui.business.QualitativeDataBusiness"%>
<%@include file="includes/head.jsp"%>
<%@include file="includes/header.jsp"%>
	<div class="container">
		<%@include file="includes/breadcrumb.jsp"%>
		<h1>Update year / period of the DVT's data</h1>
		<%
            String errorMessage = (String) request.getAttribute("errorMessage");
            if(errorMessage != null){
        %>
        <div class="alert-danger">
            <p><%=errorMessage%></p>
        </div>
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
		<label>Section:</label>
		<% ArrayList<HashMap<String,String>> sectionList = (ArrayList<HashMap<String,String>>) request.getAttribute("sectionList");
			String sectionSelected = (String) request.getAttribute("sectionSelected");
			if(sectionSelected == null){
				sectionSelected = "17";
			}
		%>
		<select id="sectionId" name="section" onchange="changeChartsInTable()">
			<% for (HashMap<String,String> data : sectionList) { %>
				<option value="<%=data.get("section_id")%>" <%= data.get("section_id").equals(sectionSelected) ? "selected": "" %>>
					<%=data.get("section_name").replace("_", " ") %>
				</option>
			<% } %>
		</select>
		<div class="clear-content"></div>
		<label>Chart 1:</label>
		<p class="table">Map Chart</p>
		<table>
			<thead>
				<tr>
			    <th>Name</th>
				<th>Dataset</th>
				<th>Actions</th>
			  </tr>
			</thead>
			<tbody id="updateDatasetsTableBody">
				<% ArrayList<HashMap<String,String>> chartsBySectionList = (ArrayList<HashMap<String,String>>) request.getAttribute("chartsBySectionList");
				ArrayList<HashMap<String,String>> datasetList; %>
				<% for (HashMap<String,String> chart : chartsBySectionList) { %>
				<tr>
					<td><%=chart.get("chart_name") %></td>
					<td>
					<form id="formChart<%=chart.get("chart_id")%>" action="user?page=update_datasets" method="post">
						<input type="hidden" value="<%=chart.get("chart_id")%>" name="chart_id">
						<input type="hidden" value="<%=chart.get("indicator_id")%>" name="indicator_id">
						<input type="hidden" value="<%=chart.get("section_id")%>" name="section_id">
						<% datasetList = (ArrayList<HashMap<String,String>>) QualitativeDataBusiness.getDatasetsForIndicator(chart.get("indicator_id")); %>
						<select id="datasetChart-<%=chart.get("chart_id")%>" name="datasetChart-<%=chart.get("chart_id")%>">
						<% for(HashMap<String,String> datasetData : datasetList){ %>
						<% String year_from = datasetData.get("dataset_year_from").substring(0,4); 
							String year_to = "";
							if(datasetData.get("dataset_year_to") != null){
								year_to = datasetData.get("dataset_year_to").substring(0,4);
							}
						%>
							<option value="<%=datasetData.get("dataset_id")%>" <%= chart.get("dataset_id").equals(datasetData.get("dataset_id")) ? "selected": "" %>>
								<%=datasetData.get("dataset_name") %> 
								<%=year_from%>
								<% if(datasetData.get("dataset_year_to") != null) { %>
									- 
									<%=year_to%>
								<% } %>
							</option>
						<% } %>
						</select>
					</form>
					</td>
					<td><button type="submit" name="formSent" value="Save" form="formChart<%=chart.get("chart_id")%>">Save</button></td>
				</tr>
				<% } %>
			</tbody>
		</table>
	</div>
<%@include file="includes/footer.jsp"%>