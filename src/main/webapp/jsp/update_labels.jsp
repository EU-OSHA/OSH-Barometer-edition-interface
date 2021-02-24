<%@page import="java.util.ArrayList"%>
<%@page import="java.util.HashMap"%>
<%@page import="eu.europa.osha.barometer.edition.webui.utils.EscapeHtmlTags"%>
<%@include file="includes/head.jsp"%>

<div class="wrapper">
	<%@include file="includes/header.jsp"%>
	<div class="container" id="update-labels">
		<%@include file="includes/breadcrumb.jsp"%>
		<h1>Explanations related to the quantitative data</h1>
		<%@include file="includes/alerts.jsp"%>
		<div id="wait-message" class="alert-success">
		    <p>Literal changes have been saved. Before changing another literal, please wait a couple of minutes for the system to process last changes.</p>
		</div>
		<div id="wait-message-space" class="clear-content"></div>
		<div class="conten-input">
			<label>Section</label>
			<% ArrayList<HashMap<String,String>> sectionList = (ArrayList<HashMap<String,String>>) request.getAttribute("sectionList"); 
			String sectionSelected = (String) request.getAttribute("sectionSelected");
			%>
			<select id="sectionSelect" name="section" onchange="disableChartSelect()">
			<% for (HashMap<String,String> data : sectionList) { %>
				<option value="<%=data.get("section_id")%>" <%=sectionSelected.equals(data.get("section_id")) ? "selected" : "" %>>
					<%= data.get("section_name").replace("_", " ") %>
				</option>
			<% } %>
			</select>
		</div>
		<div class="conten-input">
			<label>Chart</label>
			<% ArrayList<HashMap<String,String>> chartList = (ArrayList<HashMap<String,String>>) request.getAttribute("chartList");
			int chartLength = chartList.size();
			String chartSelected = (String) request.getAttribute("chartSelected");
			%>
			<select id="chartSelect" name="chart" <%= chartLength > 0 ? "" : "disabled" %> onchange="loadLiteralsTable()">
				<option value="0" <%= chartSelected.equals("0") ? "selected": "" %>>No chart selected</option>
			<% for (HashMap<String,String> data : chartList) { %>
				<option value="<%=data.get("chart_id")%>" <%= chartSelected.equals(data.get("chart_id")) ? "selected": "" %> >
					<%=data.get("chart_name")%>
				</option>
			<% } %>
			</select>
		</div>
		<div class="clear-content"></div>
		<form id="labels-form" action="user?page=update_labels" method="post">
			<% ArrayList<HashMap<String,String>> literalList = (ArrayList<HashMap<String,String>>) request.getAttribute("literalList");%>
			
			<input type="hidden" value="<%=literalList.size() %>" name="literalListSize">
			<table class="literals columns-5">
				<thead>
					<tr>
					  	<th>Publish</th>
					  	<th>Literal type</th>
					    <th>Published text</th>
						<th>Updated text</th>
						<th>Actions</th>
					</tr>
				</thead>
				<tbody id="literalListBody">	
					<% int index = 0;
					boolean updated = false;%>			
					<% for (HashMap<String,String> data : literalList) { 
						updated = false; %>
					<% if(data.get("escaped_updated_text") != null){
						if(!data.get("escaped_updated_text").equals(data.get("escaped_published_text"))){
							updated = true;
						}
					}%>
					<tr>
						<td>
							<input <%=updated ? "" : "disabled" %> id="check-<%=index%>" type="checkbox" onchange="checkTextChanges()" name="publishCheck_<%=index%>" >
							<input type="hidden" value="<%=data.get("translation_id")%>" name="translation_id_<%=index %>" id="translation_id_<%=index %>">
							<input type="hidden" value="<%=data.get("updated_text")%>" name="updated_text_<%=index %>">
							<input type="hidden" value="<%=sectionSelected%>" name="section_<%=index %>">
	                        <input type="hidden" value="<%=chartSelected%>" name="chart_<%=index %>">
	                        <!-- <input type="hidden" value="" name="lastForm">-->
	                        <input type="hidden" value="<%=data.get("escaped_updated_text")%>" name="escaped_updated_text_<%=index %>" id="escaped_updated_text-<%=index%>">
	                        <input type="hidden" value="<%=data.get("escaped_published_text")%>" name="escaped_published_text_<%=index %>" id="escaped_published_text-<%=index%>">
						</td>
						<td><%=data.get("literal_type") != null ? data.get("literal_type").replace("_", " ") : ""%></td>
						<td><span id="span_published_text_<%=index%>"><%=data.get("published_text")%></span></td>
						<td><span id="span_updated_text_<%=index%>"><%=data.get("escaped_updated_text") != null ? data.get("updated_text") : ""%></span></td>
						<td>
						<a class="href-link" href="#" onclick='editModal("<%=index%>")'>Edit</a> <a class="href-link <%=(data.get("escaped_updated_text") != null && !data.get("escaped_published_text").equals(data.get("escaped_updated_text"))) ? "" : "disabled"%>" href="#" onclick='undoPopup("<%=index%>")'>Undo</a>
						<!-- <button class="view-click" onclick='editModal("<%=index%>")'>Edit</button>
						<button onclick="undoPopup('<%=index%>')" class="<%=(data.get("escaped_updated_text") != null && !data.get("escaped_published_text").equals(data.get("escaped_updated_text"))) ? "" : "disabled"%>">Undo</button> -->
						</td> 
					</tr>
					<% index++; %>
					<% } %>
				</tbody>
			</table>
		</form>
		<button id="publishButton" class="disabled" onclick="openConfirmationModal()">Publish</button>
		<div class="clear-content"></div>
		
		<!-- MODALS -->
		<div id="edit-popup" class="popup">
			<div class="close close-click" onclick="disableSaveButton()">x</div>
			<label>Published text:</label>
			<div id="publishedContainer" readonly class="textarea disabled"><p id="publishedText"></p></div>
			<form id="formPopUp" action="user?page=update_labels" method="post" onsubmit="showWaitAlert()">
				<input type="hidden" value="" name="translation_id" id="translation_id">
				<input type="hidden" value="" name="section" id="popUpSection">
				<input type="hidden" value="" name="chart" id="popUpChart">
				<label>Updated text:</label>
				<textarea class="textarea" name="updatedTextEditor" id="updatedTextEditor" oninput="enableSaveButton()"></textarea>
			</form>
			<p></p>
			<button class="disabled close-click" id="modalSaveButton" type="submit" name="formSent" value="saveDraft" form="formPopUp">Save</button>
			<button class="close-click" id="modalCancelButton" onclick="disableSaveButton()">Cancel</button>
		</div>
		<div id="undo-popup" class="popup-warning">
			<div class="close close-click">x</div>
			<form id="formUndoPopUp" action="user?page=update_labels" method="post" onsubmit="showWaitAlert()">
				<input type="hidden" value="" name="translation_id" id="undo_translation_id">
				<input type="hidden" value="" name="section" id="popUpUndoSection">
				<input type="hidden" value="" name="chart" id="popUpUndoChart">
			</form>
			<p>Are you sure you want to undo the changes? The information in the 'Updated text' column will be lost.</p>
			<button class="disabled close-click" id="modalUndoButton" type="submit" name="formSent" value="undoUpdate" form="formUndoPopUp">Confirm</button>
			<button class="close-click" id="modalUndoCancelButton">Cancel</button>
		</div>
		<div id="confirm-popup" class="popup-confirm">
			<div class="close close-click">x</div>
			<p>Have you checked your new text/data in this <a href="https://test-visualisation.osha.europa.eu/osh-barometer#!/" target="_blank">URL</a> test environment and it is ok, press the 'Publish' button and the text/data will be updated in the dataset. To be able to see it in production environment, please request the corresponding deployment to the developers.</p>
			<button class="close-click" id="modalConfirmButton" type="submit" name="formSent" value="confirmUpdate" form="labels-form">Confirm</button>
			<button class="close-click" id="modalConfirmCancelButton">Cancel</button>
		</div>
	</div>
</div>
<script>
	CKEDITOR.replace( 'updatedTextEditor' );
</script>
<%@include file="includes/footer.jsp"%>