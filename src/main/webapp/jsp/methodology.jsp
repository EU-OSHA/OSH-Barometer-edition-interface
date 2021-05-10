<%@page import="java.util.ArrayList"%>
<%@page import="java.util.HashMap"%>
<%@page import="eu.europa.osha.barometer.edition.webui.utils.EscapeHtmlTags"%>
<%@include file="includes/head.jsp"%>

<div class="wrapper">
	<%@include file="includes/header.jsp"%>
	<div class="container" id="methodology">
		<%@include file="includes/breadcrumb.jsp"%>
		<h1>Methodology data</h1>
		<%@include file="includes/alerts.jsp"%>
		<div id="wait-message" class="alert-success">
		    <p>The update of your data is now being processed, it can take a few minutes. Please do not reload the page just wait some time until the confirmation message is displayed</p>
		</div>
		<div id="wait-message-space" class="clear-content"></div>
		
		<div class="conten-input">
			<label>Section</label>
			<% ArrayList<HashMap<String,String>> sectionList = (ArrayList<HashMap<String,String>>) request.getAttribute("sectionList"); 
			String sectionSelected = (String) request.getAttribute("sectionSelected");
			%>
			<select id="sectionSelect" name="section" onchange="loadIndicatorsBySection()">
			<% for (HashMap<String,String> section : sectionList) { %>
				<option value="<%=section.get("section_id")%>" <%=sectionSelected.equals(section.get("section_id")) ? "selected" : "" %>>
					<%= section.get("section_name") %>
				</option>
			<% } %>
			</select>
		</div>
		
		<div class="conten-input">
			<label>Indicator</label>
			<% ArrayList<HashMap<String,String>> indicatorList = (ArrayList<HashMap<String,String>>) request.getAttribute("indicatorList");
			String indicatorSelected = (String) request.getAttribute("indicatorSelected"); %>
			<select id="indicatorSelect" name="indicator" onchange="loadLiteralsTable('methodology')">
				<% for (HashMap<String,String> indicator : indicatorList) { %>
				<option value="<%=indicator.get("indicator_id")%>" <%=indicatorSelected.equals(indicator.get("indicator_id")) ? "selected" : "" %> >
					<%=indicator.get("indicator_name_2")%>
				</option>
				<% } %>
			</select>
		</div>
		
		<div class="clear-content"></div>
		
		<form id="methodology-form" action="user?page=methodology" method="post">
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
				boolean updated = false; %>
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
							<!-- <input type="hidden" value="<%=data.get("updated_text")%>" name="updated_text_<%=index %>"> -->
							<input type="hidden" value="<%=sectionSelected%>" name="section_<%=index %>">
	                        <input type="hidden" value="<%=indicatorSelected%>" name="indicator_<%=index %>">
	                        <input type="hidden" value="<%=data.get("literal_type")%>" name="literal_type_<%=index%>">
	                        <input type="hidden" value="<%=data.get("escaped_updated_text")%>" name="escaped_updated_text_<%=index %>" id="escaped_updated_text-<%=index%>">
	                        <input type="hidden" value="<%=data.get("escaped_published_text")%>" name="escaped_published_text_<%=index %>" id="escaped_published_text-<%=index%>">
						</td>
						<td><%=data.get("literal_type") != null ? data.get("literal_type").replace("_", " ") : ""%></td>
						<td><span id="span_published_text_<%=index%>"><%=data.get("published_text").equals("null") ? "" : data.get("published_text") %></span></td>
						<td><span class="span_updated_text" id="span_updated_text_<%=index%>"><%=data.get("escaped_updated_text") != null ? data.get("updated_text") : ""%></span></td>
						<td>
							<a class="href-link" href="#" onclick='editModal("<%=index%>", "<%=data.get("literal_type")%>")'>Edit</a> <a class="href-link <%=(data.get("escaped_updated_text") != null && !data.get("escaped_published_text").equals(data.get("escaped_updated_text"))) ? "" : "disabled"%>" href="#" onclick='undoPopup("<%=index%>")'>Undo</a>
						</td>
					</tr>
				<% index++;
					} %>
				</tbody>
			</table>
		</form>
		<button id="publishButton" class="disabled" onclick="openConfirmationModal()">Publish</button>
        <button id="updateAllButton" class="disabled" type="submit" name="formSent" value="updateAll" form="methodology-form"
            title="Click here to launch the ETL process and update all the saved changes displayed in the 'Updated text' column" onclick="showWaitAlert()"
        >Update all</button>
		<div class="clear-content"></div>
		
		<!-- MODALS -->
		<div id="edit-popup" class="popup">
			<div class="close close-click" onclick="disableSaveButton()">x</div>
			<label>Published text:</label>
			<div id="publishedContainer" readonly class="textarea disabled"><p id="publishedText"></p></div>
			<form id="formPopUp" action="user?page=methodology" method="post">
				<input type="hidden" value="" name="translation_id" id="translation_id">
				<input type="hidden" value="" name="section" id="popUpSection">
				<input type="hidden" value="" name="indicator" id="popUpIndicator">
				<input type="hidden" value="" name="literal_type" id="literal_type">
				<label>Updated text:</label>
				<textarea class="textarea" name="updatedTextEditor" id="updatedTextEditor" oninput="enableSaveButton()"></textarea>
				<textarea class="textarea" name="updatedTextEditor_default" id="updatedTextEditor_default"></textarea>
			</form>
			<p></p>
			<button class="disabled close-click" id="modalSaveButton" type="submit" name="formSent" value="saveDraft" form="formPopUp">Save</button>
			<button class="close-click" id="modalCancelButton" onclick="disableSaveButton()">Cancel</button>
		</div>
		<div id="undo-popup" class="popup-warning">
			<div class="close close-click">x</div>
			<form id="formUndoPopUp" action="user?page=methodology" method="post">
				<input type="hidden" value="" name="translation_id" id="undo_translation_id">
				<input type="hidden" value="" name="section" id="popUpUndoSection">
				<input type="hidden" value="" name="indicator" id="popUpUndoIndicator">
			</form>
			<p>Are you sure you want to undo the changes? The information in the 'Updated text' column will be lost.</p>
			<button class="disabled close-click" id="modalUndoButton" type="submit" name="formSent" value="undoUpdate" form="formUndoPopUp" onclick="showWaitAlert()">Confirm</button>
			<button class="close-click" id="modalUndoCancelButton">Cancel</button>
		</div>
		<div id="confirm-popup" class="popup-confirm">
			<div class="close close-click">x</div>
			<p>Have you checked your new text/data in this <a href="https://pre-visualisation.osha.europa.eu/osh-barometer#!/" target="_blank">URL</a> test environment and it is ok, press the 'Publish' button and the text/data will be updated in the dataset. To be able to see it in production environment, please request the corresponding deployment to the developers.</p>
			<button class="close-click" id="modalConfirmButton" type="submit" name="formSent" value="confirmUpdate" form="methodology-form" onclick="showWaitAlert()">Confirm</button>
			<button class="close-click" id="modalConfirmCancelButton">Cancel</button>
		</div>
	</div>
</div>

<script>
	CKEDITOR.replace( 'updatedTextEditor' );
</script>
<%@include file="includes/footer.jsp"%>