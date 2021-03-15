<%@page import="java.util.ArrayList"%>
<%@page import="java.util.HashMap"%>
<%@include file="includes/head.jsp"%>

<div class="wrapper">
	<%@include file="includes/header.jsp"%>
	<div class="container" id="qualitative-member-states">
		<%@include file="includes/breadcrumb.jsp"%>
		<h1>Qualitative data from Member States, EU and International</h1>
	
		<%@include file="includes/alerts.jsp"%>
		<div id="wait-message" class="alert-success">
		    <p>The update of your data is now being processed, it can take a few minutes.</p>
		</div>
		<div id="wait-message-space" class="clear-content"></div>
		
		<div class="conten-input">
			<label>Section</label>
			<select id="sectionSelect" name="section" onchange="loadCountriesQualitativeMS()">
			<%String sectionSelected = (String) request.getAttribute("sectionSelected"); %>
				<option value="MATRIX_AUTHORITY" <%=sectionSelected.equals("MATRIX_AUTHORITY") ? "selected" : ""%> >OSH Authorities</option>
				<option value="STRATEGY" <%=sectionSelected.equals("STRATEGY") ? "selected" : ""%> >Structure of each National strategy</option>
				<option value="MATRIX_STRATEGY" <%=sectionSelected.equals("MATRIX_STRATEGY") ? "selected" : ""%> >Responses of national strategies to EU challenges</option>
				<option value="STRATEGY_ENFOR_CAPACITY" <%=sectionSelected.equals("STRATEGY_ENFOR_CAPACITY") ? "selected" : ""%> >Enforcement capacity</option>
				<option value="MATRIX_STATISTICS" <%=sectionSelected.equals("MATRIX_STATISTICS") ? "selected" : ""%> >OSH Statistics, surveys and research</option>
			</select>
		</div>
		
		<div class="conten-input">
			<label>Country</label>
			<% ArrayList<HashMap<String,String>> countryList = (ArrayList<HashMap<String,String>>) request.getAttribute("countryList"); 
			String countrySelected = (String) request.getAttribute("countrySelected");%>
			<select id="countrySelect" name="country" onchange="loadLiterals()">
				<% for(HashMap<String,String> country : countryList) {  %>
					<option value="<%=country.get("country_code")%>" <%=countrySelected.equals(country.get("country_code")) ? "selected" : ""%> >
						<%= (!country.get("country_code").equals("EU28")) ? "("+country.get("country_code")+") "+country.get("country_name") : country.get("country_code") %>
					</option>
				<% } %>
			</select>
		</div>
		
		<% String institutionSelected = (String) request.getAttribute("institutionSelected");
		/*boolean isMatrixPage = (boolean) request.getAttribute("isMatrixPage");
		System.out.println("isMatrixPage: "+isMatrixPage);*/
		 /*if(sectionSelected.equals("MATRIX_AUTHORITY") || sectionSelected.equals("MATRIX_STRATEGY")
				|| sectionSelected.equals("MATRIX_STATISTICS")) {*/ %>
		
		<div class="conten-input">
			<label id="institution_type_label">Institution type</label>
			<select id="institutionSelect" name="institution" onchange="loadLiteralsMatrixPage()">
				<% if(sectionSelected.equals("MATRIX_AUTHORITY")) { %>
					<option <%="osh_authority".equals(institutionSelected) ? "selected" : "" %> value="osh_authority">OSH authority</option>
					<option <%="compensation_insurance".equals(institutionSelected) ? "selected" : "" %> value="compensation_insurance">Compensation and insurance body</option>
					<option <%="prevention_institute".equals(institutionSelected) ? "selected" : "" %> value="prevention_institute">Prevention institute</option>
					<option <%="standardisation_body".equals(institutionSelected) ? "selected" : "" %> value="standardisation_body">Standardisation body</option>
				<% } else if(sectionSelected.equals("MATRIX_STRATEGY")) { %>
					<option <%="implementation_record".equals(institutionSelected) ? "selected" : "" %> value="implementation_record">Implementation record</option>
					<option <%="prevention_diseases".equals(institutionSelected) ? "selected" : "" %> value="prevention_diseases">Prevention of work-related diseases</option>
					<option <%="tackling_demographic".equals(institutionSelected) ? "selected" : "" %> value="tackling_demographic">Tackling demographic change</option>
				<% } else if(sectionSelected.equals("MATRIX_STATISTICS")) { %>
					<option <%="osh_statistics".equals(institutionSelected) ? "selected" : "" %> value="osh_statistics">OSH statistics</option>
					<option <%="surveys".equals(institutionSelected) ? "selected" : "" %> value="surveys">Surveys</option>
					<option <%="research_institutes".equals(institutionSelected) ? "selected" : "" %> value="research_institutes">Research Institutes</option>
				<% } %>
			</select>
		</div>
		
		<% //} %>
		
		<div class="clear-content"></div>
		<form id="labels-form" action="user?page=qualitative_data_member_states" method="post">
		
			<% ArrayList<HashMap<String,String>> literalList = (ArrayList<HashMap<String,String>>) request.getAttribute("literalsList");%>
			<input type="hidden" value="<%=literalList.size() %>" name="literalListSize">
			<!-- --------------------------------------------STRATEGIES PAGE----------------------------------------- -->
			<div id="strategies-page-table">
				<table class="literals columns-4">
					<thead>
						<tr>
						  	<th>Publish</th>
						  	<th>Indicator Name</th>
						    <th>Published text</th>
							<th>Updated text</th>
							<th>Actions</th>
						</tr>
					</thead>
					<tbody id="literalListBody">
						<% int index = 0;
						boolean updated = false;%>
						<% if(sectionSelected.equals("STRATEGY_ENFOR_CAPACITY") || sectionSelected.equals("STRATEGY")){ %>
						<% for (HashMap<String,String> data : literalList) { %>
						<% updated = false; 
						if(data.get("escaped_updated_text") != null){
							if(!data.get("escaped_updated_text").equals(data.get("escaped_published_text"))){
								updated = true;
							}
						}%>
						<tr>
							<td>
								<input <%=updated ? "" : "disabled" %> id="check-<%=index%>" type="checkbox" onchange="checkTextChanges()" name="publishCheck_<%=index%>" >
								<input type="hidden" value="<%=data.get("translation_id")%>" name="translation_id_<%=index %>" id="translation_id_<%=index %>">
								<!-- <input type="hidden" value="<%=data.get("updated_text")%>" name="updated_text_<%=index %>">-->
								<input type="hidden" value="<%=sectionSelected%>" name="section_<%=index %>">
		                        <input type="hidden" value="<%=countrySelected%>" name="country_<%=index %>">
		                        <input type="hidden" value="<%=institutionSelected%>" name="institution_<%=index %>">
		                        <input type="hidden" value="<%=data.get("escaped_updated_text")%>" name="escaped_updated_text_<%=index %>" id="escaped_updated_text-<%=index%>">
		                        <input type="hidden" value="<%=data.get("escaped_published_text")%>" name="escaped_published_text_<%=index %>" id="escaped_published_text-<%=index%>">
							</td>
							<td><%=data.get("literal_type") != null ? data.get("literal_type").replace("_", " ") : ""%></td>
							<td><span id="span_published_text_<%=index%>"><%=data.get("published_text")%></span></td>
							<td><span class="span_updated_text" id="span_updated_text_<%=index%>"><%=data.get("escaped_updated_text") != null ? data.get("updated_text") : ""%></span></td>
							<td>
							<a class="href-link" href="#" onclick='editModal("<%=index%>")'>Edit</a> <a class="href-link <%=(data.get("escaped_updated_text") != null && !data.get("escaped_published_text").equals(data.get("escaped_updated_text"))) ? "" : "disabled"%>" href="#" onclick='undoPopup("<%=index%>")'>Undo</a>
							<!-- <button class="view-click" onclick='editModal("<%=index%>")'>Edit</button>
							<button onclick="undoPopup('<%=index%>')" class="<%=(data.get("escaped_updated_text") != null && !data.get("escaped_published_text").equals(data.get("escaped_updated_text"))) ? "" : "disabled"%>">Undo</button> -->
							</td> 
						</tr>
						<% index++; %>
						<% } %>
						<% } %>
					</tbody>
				</table>
			</div>

<!-- ---------------------------------------------- MATRIX PAGE ---------------------------------------------------------- -->
			<div id="matrix-page-table">
				<% ArrayList<HashMap<String,String>> matrixPageCount = (ArrayList<HashMap<String,String>>) request.getAttribute("matrixPageCount"); 
				//System.out.println("index: "+index);
				//System.out.println("matrixPageCount: "+matrixPageCount);
				if(matrixPageCount != null){
				for (HashMap<String,String> count : matrixPageCount) { %>
				
				<table class="literals columns-4">
					<thead>
						<tr>
						  	<th>Publish</th>
						    <th>Published text</th>
							<th>Updated text</th>
							<th>Actions</th>
						</tr>
					</thead>
					<tbody id="matrixListBody_<%=count.get("data_id")%>">
						<% /*int index = 0;
						boolean*/ updated = false;%>			
						<% for (HashMap<String,String> data : literalList) { 
						//System.out.println("data.get(\"data_id\"): "+data.get("data_id")+" count.get(\"data_id\"): "+count.get("data_id")
						//	+" are equal? "+data.get("data_id").equals(count.get("data_id")));
						if(data.get("data_id").equals(count.get("data_id"))){
							updated = false; 
							if(data.get("escaped_updated_text") != null){
								if(!data.get("escaped_updated_text").equals(data.get("escaped_published_text"))){
									updated = true;
								}
							}%>
						<tr>
							<td>
								<input <%=updated ? "" : "disabled" %> id="check-<%=index%>" type="checkbox" onchange="checkTextChanges()" name="publishCheck_<%=index%>" >
								<input type="hidden" value="<%=data.get("translation_id")%>" name="translation_id_<%=index %>" id="translation_id_<%=index %>">
								<!-- <input type="hidden" value="<%=data.get("updated_text")%>" name="updated_text_<%=index %>">-->
								<input type="hidden" value="<%=sectionSelected%>" name="section_<%=index %>">
		                        <input type="hidden" value="<%=countrySelected%>" name="country_<%=index %>">
		                        <input type="hidden" value="<%=institutionSelected%>" name="institution_<%=index %>">
		                        <input type="hidden" value="<%=data.get("escaped_updated_text")%>" name="escaped_updated_text_<%=index %>" id="escaped_updated_text-<%=index%>">
		                        <input type="hidden" value="<%=data.get("escaped_published_text")%>" name="escaped_published_text_<%=index %>" id="escaped_published_text-<%=index%>">
							</td>
							<td><span id="span_published_text_<%=index%>"><%=data.get("published_text")%></span></td>
							<td><span class="span_updated_text" id="span_updated_text_<%=index%>"><%=data.get("escaped_updated_text") != null ? data.get("updated_text") : ""%></span></td>
							<td>
							<a class="href-link" href="#" onclick='editModal("<%=index%>")'>Edit</a> <a class="href-link <%=(data.get("escaped_updated_text") != null && !data.get("escaped_published_text").equals(data.get("escaped_updated_text"))) ? "" : "disabled"%>" href="#" onclick='undoPopup("<%=index%>")'>Undo</a>
							<!-- <button class="view-click" onclick='editModal("<%=index%>")'>Edit</button>
							<button onclick="undoPopup('<%=index%>')" class="<%=(data.get("escaped_updated_text") != null && !data.get("escaped_published_text").equals(data.get("escaped_updated_text"))) ? "" : "disabled"%>">Undo</button> -->
							</td> 
						</tr>
						<% index++; %>
						<% } %>
						<% } %>
					</tbody>
				</table>
				<% }
				}%>
			</div>
		</form>
		<button id="publishButton" class="disabled" onclick="openConfirmationModal()">Publish</button>
        <button id="updateAllButton" class="disabled" type="submit" name="formSent" value="updateAll" form="labels-form"
            title="Click here to launch the ETL process and update all the saved changes displayed in the 'Updated text' column" onclick="showWaitAlert()"
        >Update all</button>
		<div class="clear-content"></div>
		
		<!-- MODALS -->
		<div id="edit-popup" class="popup">
			<div class="close close-click" onclick="disableSaveButton()">x</div>
			<label>Published text:</label>
			<div id="publishedContainer" readonly class="textarea disabled"><p id="publishedText"></p></div>
			<form id="formPopUp" action="user?page=qualitative_data_member_states" method="post">
				<input type="hidden" value="" name="translation_id" id="translation_id">
				<input type="hidden" value="" name="section" id="popUpSection">
				<input type="hidden" value="" name="country" id="popUpCountry">
				<input type="hidden" value="" name="institution" id="popUpInstitution">
				<label>Updated text:</label>
				<textarea class="textarea" name="updatedTextEditor" id="updatedTextEditor" oninput="enableSaveButton()"></textarea>
			</form>
			<p></p>
			<button class="disabled close-click" id="modalSaveButton" type="submit" name="formSent" value="saveDraft" form="formPopUp">Save</button>
			<button class="close-click" id="modalCancelButton" onclick="disableSaveButton()">Cancel</button>
		</div>
		<div id="undo-popup" class="popup-warning">
			<div class="close close-click">x</div>
			<form id="formUndoPopUp" action="user?page=qualitative_data_member_states" method="post">
				<input type="hidden" value="" name="translation_id" id="undo_translation_id">
				<input type="hidden" value="" name="section" id="popUpUndoSection">
				<input type="hidden" value="" name="country" id="popUpUndoCountry">
				<input type="hidden" value="" name="institution" id="popUpUndoInstitution">
			</form>
			<p>Are you sure you want to undo the changes? The information in the 'Updated text' column will be lost.</p>
			<button class="disabled close-click" id="modalUndoButton" type="submit" name="formSent" value="undoUpdate" form="formUndoPopUp">Confirm</button>
			<button class="close-click" id="modalUndoCancelButton">Cancel</button>
		</div>
		<div id="confirm-popup" class="popup-confirm">
			<div class="close close-click">x</div>
			<p>Have you checked your new text/data in this <a href="https://test-visualisation.osha.europa.eu/osh-barometer#!/" target="_blank">URL</a> test environment and it is ok, press the 'Publish' button and the text/data will be updated in the dataset. To be able to see it in production environment, please request the corresponding deployment to the developers.</p>
			<button class="close-click" id="modalConfirmButton" type="submit" name="formSent" value="confirmUpdate" form="labels-form" onclick="showWaitAlert()">Confirm</button>
			<button class="close-click" id="modalConfirmCancelButton">Cancel</button>
		</div>
	</div>
</div>
<script>
	CKEDITOR.replace( 'updatedTextEditor' );
</script>
<%@include file="includes/footer.jsp"%>