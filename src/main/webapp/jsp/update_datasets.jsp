<%@page import="java.util.ArrayList"%>
<%@page import="java.util.HashMap"%>
<%@page import="eu.europa.osha.barometer.edition.webui.business.QualitativeDataBusiness"%>
<%@include file="includes/head.jsp"%>

<div class="wrapper">
	<%@include file="includes/header.jsp"%>
	<div class="container">
		<%@include file="includes/breadcrumb.jsp"%>
		<h1>Update year / period of the DVT's data</h1>
		
		<%@include file="includes/alerts.jsp"%>
		
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
		
		<div id="tablesContainer">
		
		<% ArrayList<HashMap<String,String>> chartsBySectionList = (ArrayList<HashMap<String,String>>) request.getAttribute("chartsBySectionList");%>
		<% int chartIndex = 1; %>
		<% for (HashMap<String,String> data : chartsBySectionList) { %>
			<label>Chart <%=chartIndex %>: <b><%=data.get("chart_name") %></b></label>
			<table>
				<thead>
					<tr>
				    <th>Name</th>
					<th>Dataset</th>
					<th>Actions</th>
				  </tr>
				</thead>
				<tbody id="chartTableBody<%=data.get("chart_id")%>">
					<% ArrayList<HashMap<String,String>> indicatorsByChart = QualitativeDataBusiness.getIndicatorsByChart(data.get("chart_id"));
						ArrayList<HashMap<String,String>> datasetList; %>
					<% for (HashMap<String,String> indicator : indicatorsByChart) { %>
						<tr>
							<td><%=indicator.get("chart_name") %></td>
							<td>
								<form id="formChart<%=indicator.get("indicator_id")%>" action="user?page=update_datasets" method="post">
									<input type="hidden" value="<%=indicator.get("chart_id")%>" name="chart_id">
									<input type="hidden" value="<%=indicator.get("indicator_id")%>" name="indicator_id">
									<input type="hidden" value="<%=data.get("section_id")%>" name="section_id">
									<% datasetList = (ArrayList<HashMap<String,String>>) QualitativeDataBusiness.getDatasetsForIndicator(indicator.get("indicator_id")); %>
									<select id="datasetChart-<%=indicator.get("indicator_id")%>" name="datasetChart-<%=indicator.get("indicator_id")%>" 
										onchange="enableDatasetTableSaveButton(event, <%=indicator.get("dataset_id")%>, <%=indicator.get("indicator_id")%>)">
									<% for(HashMap<String,String> datasetData : datasetList){ %>
									<% String year_from = datasetData.get("dataset_year_from").substring(0,4); 
										String year_to = "";
										if(datasetData.get("dataset_year_to") != null){
											year_to = datasetData.get("dataset_year_to").substring(0,4);
										}
									%>
										<option value="<%=datasetData.get("dataset_id")%>" <%= indicator.get("dataset_id").equals(datasetData.get("dataset_id")) ? "selected": "" %>>
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
							<td><button id="buttonForm-<%=indicator.get("indicator_id")%>"  class="disabled" type="submit" name="formSent" value="Save" 
								form="formChart<%=indicator.get("indicator_id")%>" disabled>Save</button></td>
						</tr>
					<% } %>
				</tbody>
			</table>
		<% chartIndex++; %>		
		<% } %>
		</div>
	</div>
</div>
<%@include file="includes/footer.jsp"%>