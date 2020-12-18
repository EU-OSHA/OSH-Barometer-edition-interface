<%@page import="java.util.ArrayList"%>
<%@page import="java.util.HashMap"%>
<%@include file="includes/head.jsp"%>
<%@include file="includes/header.jsp"%>
	<div class="container">
		<%@include file="includes/breadcrumb.jsp"%>
		<h1>Update year / period of the DVT's data</h1>
		<label>Section:</label>
		<% ArrayList<HashMap<String,String>> sectionList = (ArrayList<HashMap<String,String>>) request.getAttribute("sectionList"); %>
		<select name="section" onchange="changeChartsInTable()">
			<% for (HashMap<String,String> data : sectionList) { %>
				<option value="<%=data.get("section_id")%>"><%=data.get("section_name") %></option>
			<% } %>
		</select>
		<div class="clear-content"></div>
		<label>Chart 1:</label>
		<p class="table">Map Chart</p>
		<table>
		  <tr>
		    <th>Name</th>
			<th>Dataset</th>
			<th>Actions</th>
		  </tr>
		  <% ArrayList<HashMap<String,String>> chartsBySectionList = (ArrayList<HashMap<String,String>>) request.getAttribute("chartsBySectionList"); %>
		  <% for (HashMap<String,String> chart : chartsBySectionList) { %>
			  <!-- TODO put somewhere the chart id to update later the dataset -->
			  <tr>
				<td><%=chart.get("chart_name") %></td>
				<td>
					<select>
						<option>
							Eurostat 2019
						</option>
						<option>
							Eurostat 2017
						</option>
				</select>
				</td>
				<td><button>Save</button></td>
			  </tr>
		  <% } %>
		  <!-- <tr>
			<td>Median age of population</td>
			<td>
				<select>
					<option>
						Eurostat 2019
					</option>
					<option>
						Eurostat 2017
					</option>
			</select>
			</td>
			<td><button>Save</button></td>
		  </tr>
		  <tr>
			<td>Ageing workers (55 to 64) employment rate</td>
			<td>
				<select>
					<option>
						Eurostat 2018
					</option>
					<option>
						Eurostat 2017
					</option>
			</select>
			</td>
			<td><button>Save</button></td>
		  </tr>
		   <tr>
			<td>Total, male and female employment rate</td>
			<td>
				<select>
					<option>
						Eurostat 2018
					</option>
					<option>
						Eurostat 2017
					</option>
			</select>
			</td>
			<td><button>Save</button></td>
		  </tr>
		   <tr>
			<td>Unemployment rate</td>
			<td>
				<select>
					<option>
						Eurostat 2018
					</option>
					<option>
						Eurostat 2017
					</option>
			</select>
			</td>
			<td><button>Save</button></td>
		  </tr>-->
		</table>
	</div>
<%@include file="includes/footer.jsp"%>