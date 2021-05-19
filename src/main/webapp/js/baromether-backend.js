(function ($) {
  
$(document).ready(function(){
    $( ".close-click" ).click(function() {
    	$( ".popup" ).fadeOut( "slow" );
    });

    $( ".close-click" ).click(function() {
      $( ".popup-warning" ).fadeOut( "slow" );
    });

    $( ".close-click" ).click(function() {
      $( ".popup-confirm" ).fadeOut( "slow" );
    });
    
    $( ".view-click" ).click(function() {
      $( ".popup" ).fadeIn( "slow" );
    });
    
    $( "#yearFromContainer" ).css("display","none");
	$( "#yearToContainer" ).css("display","none");
	$( "#oneYearContainer" ).css("display","block");
	$("#templateUsage").text('The template should be "EU-OSHA_OIE_Eurostat indicator_Company size_YYYY-MM-DD.xlsx"');

	/** Function called in Quantitative Eurostat page to change the year dropdowns depending on the selected indicator */
    changeYearCombos =  function(){
		//console.log("Entra en changeYearCombos");
		var optionSelected = document.getElementById("indicatorEurostat");
		var valueSelected = optionSelected.value;
		if(valueSelected == 36 || valueSelected == 279 || valueSelected == 53
			|| valueSelected == 54){
			$( "#yearFromContainer" ).css("display","block");
			$( "#yearToContainer" ).css("display","block");
			$( "#oneYearContainer" ).css("display","none");
		}else{
			$( "#yearFromContainer" ).css("display","none");
			$( "#yearToContainer" ).css("display","none");
			$( "#oneYearContainer" ).css("display","block");
		}
		
		if(valueSelected == 31){
			$("#templateUsage").text('The template should be "EU-OSHA_OIE_Eurostat_indicator_Company_size_YYYY-MM-DD.xlsx"');
		}else if(valueSelected == 32){
			$("#templateUsage").text('The template should be "EU-OSHA_OIE_Eurostat_indicator_Employment_per_sector_YYYY-MM-DD.xlsx"');
		}else if(valueSelected == 39){
			$("#templateUsage").text('The template should be "EU-OSHA_OIE_Eurostat_indicator_Employment_rate_T_M_F_YYYY-MM-DD.xlsx"');
		}else if(valueSelected == 36){
			$("#templateUsage").text('The template should be "EU-OSHA_OIE_Eurostat_Income_per_capita_YYYY-MM-DD.xlsx"');
		}else if(valueSelected == 279){
			$("#templateUsage").text('The template should be "EU-OSHA_OIE_Eurostat_Income_per_capita_EURO_YYYY-MM-DD.xlsx"');
		}else if(valueSelected == 53){
			$("#templateUsage").text('The template should be "EU-OSHA_OIE_Eurostat_NonFatal_Work_accidents_YYYY-MM-DD.xlsx"');
		}else if(valueSelected == 54){
			$("#templateUsage").text('The template should be "EU-OSHA_OIE_Eurostat_Fatal_Work_accidents_YYYY-MM-DD.xlsx"');
		}else{
			$("#templateUsage").text('The template should be "EU-OSHA_OIE_Eurostat_Direct_value_indicators_YYYY-MM-DD.xlsx"');
		}
	};
	
	if($('#container-eurostat-quantitative').length > 0){
		changeYearCombos();
	};
	
	/** Function called in Update datasets page. If the dataset selected has been changed by the user,
	* it enables Save button for the current row 
	* @event event where is triggered the current function
	* @currentDatasetId var current dataset id 
	* @indicatorId var current indicator id 
	* */
	enableDatasetTableSaveButton = function(event, currentDatasetId, indicatorId) {
		console.log('Enters in enableDatasetTableSaveButton');
		var selectedDatasetId = parseInt(event.currentTarget.options[event.currentTarget.selectedIndex].value);
		if(currentDatasetId != selectedDatasetId) {
			$('#buttonForm-'+indicatorId).prop('disabled', false);
			$('#buttonForm-'+indicatorId).removeClass('disabled');
		}else{
			$('#buttonForm-'+indicatorId).prop('disabled', true);
			$('#buttonForm-'+indicatorId).addClass('disabled');
		}
	};
	
	/** Function called in Update datasets page. If the selected section from the dropdown is changed,
	* multiple ajax calls are triggered in order to change charts, indicators and datasets 
	*/
	changeChartsInTable = function () {
		console.log("Enters changeChartsInTable function");
		
		var optionSelected = document.getElementById("sectionId");
		var valueSelected = optionSelected.value;
		var chartIndex = 1;
		
		/* CHARTS */
		$.get({
			url: 'tableload',
			data: {
				section: valueSelected,
				get: 'charts'
			},
			success: function(chartResponse) {
		        var chartList = JSON.parse(chartResponse);
		        var new_tbody = "";
		        $('div#tablesContainer').empty();
		        chartList.forEach(function(chart){
					new_tbody = new_tbody.concat('<label>Chart '+chartIndex+': <b>'+chart.chart_name+'</b>:</label>');
					new_tbody = new_tbody.concat('<table>');
					new_tbody = new_tbody.concat('<thead>');
					new_tbody = new_tbody.concat('<tr>');
					new_tbody = new_tbody.concat('<th>Name</th>');
					new_tbody = new_tbody.concat('<th>Dataset</th>');
					new_tbody = new_tbody.concat('<th>Actions</th>');
					new_tbody = new_tbody.concat('</tr>');
					new_tbody = new_tbody.concat('</thead>');
					new_tbody = new_tbody.concat('<tbody id="chartTableBody'+chart.chart_id+'">');
					
					/* INDICATORS */
					$.get({
						url: 'tableload',
						data: {
							section: valueSelected,
							chart: chart.chart_id,
							get: 'indicators'
						},
						success: function(indicatorResponse) {
					        var indicatorList = JSON.parse(indicatorResponse);
					        //var new_tbody = "";
					        //$('tbody#chartTableBody'+chart.chart_id).empty();
					        indicatorList.forEach(function(indicator){
					            new_tbody = new_tbody.concat('<tr>');
					            new_tbody = new_tbody.concat('<td>'+indicator.chart_name+'</td>');
					            new_tbody = new_tbody.concat('<td>');
								new_tbody = new_tbody.concat('<form id="formChart'+indicator.indicator_id+'" action="user?page=update_datasets" method="post">');
								new_tbody = new_tbody.concat('<input type="hidden" value="'+indicator.chart_id+'" name="chart_id">');
								new_tbody = new_tbody.concat('<input type="hidden" value="'+indicator.indicator_id+'" name="indicator_id">');
								new_tbody = new_tbody.concat('<input type="hidden" value="'+chart.section_id+'" name="section_id">');
								new_tbody = new_tbody.concat('');
								new_tbody = new_tbody.concat('<select id="datasetChart-'+indicator.indicator_id+'" name="datasetChart-'+indicator.indicator_id+'" ')
								new_tbody = new_tbody.concat('onchange="enableDatasetTableSaveButton(event, '+indicator.dataset_id+', '+indicator.indicator_id+')">');
					
								/* DATASETS */
								$.get({
									url: 'tableload',
									data: {
										section: valueSelected,
										get: 'datasets',
										indicator: indicator.indicator_id
									},
									success: function(datasetResponse){
										var datasetList = JSON.parse(datasetResponse);
										datasetList.forEach(function(dataset){
											var year_from = dataset.dataset_year_from.substring(0,4);
											//console.log("year_from: "+year_from);
											var year_to = "";
											var selectedOrNot = (indicator.dataset_id == dataset.dataset_id) ? "selected": "";
											//console.log("selectedOrNot: "+selectedOrNot);
											if(dataset.dataset_year_to != null){
												year_to = dataset.dataset_year_to.substring(0,4);
											}
											//console.log("year_to: "+year_to);
											new_tbody = new_tbody.concat('<option value="'+dataset.dataset_id+'" '+selectedOrNot+' >');
											new_tbody = new_tbody.concat(dataset.dataset_name +' '+year_from);
											if(dataset.dataset_year_to != null) {
												new_tbody = new_tbody.concat(' - '+year_to);
											}
											new_tbody = new_tbody.concat('</option>');
										});
									},		
									async: false
								});
								new_tbody = new_tbody.concat('</select>');
								new_tbody = new_tbody.concat('</form>');
								new_tbody = new_tbody.concat('</td>');
								new_tbody = new_tbody.concat('<td><button id="buttonForm-'+indicator.indicator_id+'" class="disabled" type="submit" name="formSent" value="Save" form="formChart'+indicator.indicator_id+'" disabled>');
								new_tbody = new_tbody.concat('Save</button></td>');
								new_tbody = new_tbody.concat('</tr>');
					        });
						},
						async: false
					});
						
					new_tbody = new_tbody.concat('</tbody>');
					new_tbody = new_tbody.concat('</table>');
					chartIndex++;
		        });
				//console.log(new_tbody);
		        $('div#tablesContainer').html(new_tbody);
			},
			async: true
		});
	}
	
	/** Function called in Country reports for MS page. If selected section from the section dropdown is changed
	* the country dropdown changes with the countries related to that new section.
	* @valueDefault var boolean for the current selected section if it is the default one or not
	*/
	changeCountryDisplay = function(valueDefault) {
		console.log("Enters changeCountryDisplay function");
		var sectionSelected = document.getElementById("section");
		var valueSelected = null;
		
		if(valueDefault == 'true'){
			valueSelected = 'osh_authorities';
		}else{
			valueSelected = sectionSelected.value;
		}
		
		$.get({
			url: 'countrydisplay',
			data: {
				section: valueSelected
			},
			success: function(countryResponse) {
		        var countryList = JSON.parse(countryResponse);
		        var new_tbody = "";
		        $('#country').empty();
		        countryList.forEach(function(country){
					if(country.real_name == 'Austria'){
						new_tbody = new_tbody.concat('<option value="'+country.real_name+'" selected>');
					}else{
						new_tbody = new_tbody.concat('<option value="'+country.real_name+'" >');
					}
					
					new_tbody = new_tbody.concat(country.country_name);
					new_tbody = new_tbody.concat('</option>');
		        });
		        $('#country').html(new_tbody);
			},
			async: true
		});
	}
	
	/** Function called in Update labels page when the current section changes.
	* Reloads the chart dropdown with the charts of the new selected section
	* If the new section has not any related chart, the chart dropdown is disabled.
	*/
	disableChartSelect = function() {
		console.log("Enters disableChartSelect function");
		var sectionSelected = document.getElementById("sectionSelect");
		var valueSelected = sectionSelected.value;
		
		$.get({
			url: 'chartload',
			data: {
				section: valueSelected
			},
			success: function(chartResponse) {
		        var chartList = JSON.parse(chartResponse);
		        var new_tbody = "";
		        $('#chartSelect').empty();
					new_tbody = new_tbody.concat('<option selected value="0" >');
					new_tbody = new_tbody.concat('No chart selected');
					new_tbody = new_tbody.concat('</option>');
		        chartList.forEach(function(chart){
					new_tbody = new_tbody.concat('<option value="'+chart.chart_id+'"');
					if((valueSelected == "17" && chart.chart_id != "20013") || valueSelected == "21"){
						new_tbody = new_tbody.concat('disabled');
					}
					new_tbody = new_tbody.concat('>');
					new_tbody = new_tbody.concat(chart.chart_name);
					new_tbody = new_tbody.concat('</option>');
		        });
		        $('#chartSelect').html(new_tbody);
				if(chartList.length > 0) {
					$('#chartSelect').prop('disabled', false);
				} else {
					$('#chartSelect').prop('disabled', true);
				}
			},
			async: true
		});
		
		$("#chartSelect").val("0");
		loadLiteralsTable('literals');
	}
	
	/** Function called in Update labels, Qualitative data for MS and Methodology pages.
	* This function retrieves with an ajax call a list with the literals depending on the
	* parameters selected in the corresponding dropdowns of each page. The new list is
	* painted in the JSP.
	*/
	loadLiteralsTable = function(page) {
		console.log("Enters loadLiteralsTable function");
		var section = document.getElementById("sectionSelect");
		var sectionSelected = section.value;
		
		var chart = document.getElementById("chartSelect");
		var chartSelected = 0;
		
		if(chart != null){
			chartSelected = chart.value;
		}
		
		var country = document.getElementById("countrySelect");
		var countrySelected = 0;
		
		if(country != null){
			countrySelected = country.value;
		}
		
		var institution = document.getElementById("institutionSelect");
		var institutionSelected = 0;
		
		if(institution != null){
			institutionSelected = institution.value;
		}
		
		var indicator = document.getElementById("indicatorSelect");
		var indicatorSelected = 0;
		
		if(indicator != null){
			indicatorSelected = indicator.value;
		}
		
		$.get({
			url: 'tableload',
			data: {
				section: sectionSelected,
				chart: chartSelected,
				country : countrySelected,
				institution: institutionSelected,
				indicator: indicatorSelected,
				//get: 'literals'
				get: page
			},
			success: function(literalsResponse) {
		        var literalsList = JSON.parse(literalsResponse);
		        var new_tbody = "";
				var index = 0;
				var literalListSize = literalsList.length;
				$('input[name="literalListSize"]').val(literalListSize);
		        $('#literalListBody').empty();

				if(page == 'qualitativeMS'){
					$('div#matrix-page-table').empty();
				}

		        literalsList.forEach(function(literal){
					new_tbody = new_tbody.concat('<tr>');
					new_tbody = new_tbody.concat('<td>');
					new_tbody = new_tbody.concat('<input ');
					if(literal.updated_text != null || literal.updated_text != undefined){
						if(literal.escaped_updated_text == literal.escaped_published_text){
							new_tbody = new_tbody.concat('disabled');
						}
					}else{
						new_tbody = new_tbody.concat('disabled');
					}
					new_tbody = new_tbody.concat(' id="check-'+index+'" type="checkbox" onchange="checkTextChanges()" name="publishCheck_'+index+'">');
					new_tbody = new_tbody.concat('<input type="hidden" value="'+literal.translation_id+'" name="translation_id_'+index+'" id="translation_id_'+index+'">');
					//new_tbody = new_tbody.concat('<input type="hidden" value="'+literal.updated_text+'" name="updated_text_'+index+'">');
					new_tbody = new_tbody.concat('<input type="hidden" value="'+sectionSelected+'" name="section_'+index+'">');
                    //new_tbody = new_tbody.concat('<input type="hidden" value="'+chartSelected+'" name="chart_'+index+'">');
					if(page == 'literals'){
						new_tbody = new_tbody.concat('<input type="hidden" value="'+chartSelected+'" name="chart_'+index+'">');
					}else if(page == 'qualitativeMS'){
						new_tbody = new_tbody.concat('<input type="hidden" value="'+countrySelected+'" name="country_'+index+'">');
						new_tbody = new_tbody.concat('<input type="hidden" value="'+institutionSelected+'" name="institution_'+index+'">');
					}else{
						new_tbody = new_tbody.concat('<input type="hidden" value="'+indicatorSelected+'" name="indicator_'+index+'">');
					}

					new_tbody = new_tbody.concat('<input type="hidden" value="'+literal.escaped_updated_text+'" name="escaped_updated_text_'+index+'" id="escaped_updated_text-'+index+'">');
					new_tbody = new_tbody.concat('<input type="hidden" value="'+literal.escaped_published_text+'" name="escaped_published_text_'+index+'" id="escaped_published_text-'+index+'">');
					new_tbody = new_tbody.concat('</td>');
					if(page != 'qualitativeMS' || (literal.page == "STRATEGY" || literal.page == "STRATEGY_ENFOR_CAPACITY")){
						new_tbody = new_tbody.concat('<td>');
						if(literal.literal_type != null && literal.literal_type != ""){
							new_tbody = new_tbody.concat(literal.literal_type.replace('_', ' '));
						}
						new_tbody = new_tbody.concat('</td>');
					}
					
					if(literal.published_text != "null"){
						new_tbody = new_tbody.concat('<td><span id="span_published_text_'+index+'">'+literal.published_text+'</span></td>');
					}else{
						new_tbody = new_tbody.concat('<td><span id="span_published_text_'+index+'"></span></td>');
					}
					
					//new_tbody = new_tbody.concat('<td><span id="span_published_text_'+index+'">'+literal.published_text+'</span></td>');
					if(literal.updated_text != null || literal.updated_text != undefined){
						new_tbody = new_tbody.concat('<td><span class="span_updated_text" id="span_updated_text_'+index+'">'+literal.updated_text+'</span></td>');
					}else{
						new_tbody = new_tbody.concat('<td><span class="span_updated_text" id="span_updated_text_'+index+'"></span></td>');	
					}
					//new_tbody = new_tbody.concat('<td><button class="view-click" onclick="editModal('+index+')">Edit</button>');
					new_tbody = new_tbody.concat('<td><a class="href-link" href="#" onclick="editModal(\''+index+'\',\''+literal.literal_type+'\')">Edit</a> ');
					
					if(literal.updated_text != null && literal.updated_text != literal.escaped_published_text){
						//new_tbody = new_tbody.concat('<button onclick="undoPopup('+index+')" class="">Undo</button>');
						new_tbody = new_tbody.concat('<a class="href-link" href="#" onclick="undoPopup(\''+index+'\')">Undo</a>');
					}else{
						//new_tbody = new_tbody.concat('<button onclick="undoPopup('+index+')" class="disabled">Undo</button>');
						new_tbody = new_tbody.concat('<a class="href-link disabled" href="#" onclick="undoPopup(\''+index+'\')">Undo</a>');
					}
					new_tbody = new_tbody.concat('</td>');
					new_tbody = new_tbody.concat('</tr>');

					index++;
		        });
		        $('#literalListBody').html(new_tbody);
			},
			async: true
		}).done(function(data){
				//if(page == 'qualitativeMS'){
					enableUpdateAllButton();
				//}
			});
		
		if($('div#qualitative-member-states').length > 0){
			enableUpdateAllButton();
		}

	}
	
	enableUpdateAllButton = function() {
		//console.log('Enters in enableUpdateAllButton');
		var text_updates = $('.span_updated_text');
		//console.log('text_updates length: '+text_updates.length);
		$('#updateAllButton').addClass('disabled');
        for (var i = 0; i < text_updates.length; i++) {
            if(text_updates[i].textContent != ""){
                if($('#updateAllButton').hasClass('disabled')){
                    $('#updateAllButton').removeClass('disabled');
                }
                break;
            }
        }
	};
	
	if($('div#update-labels').length > 0 || $('div#qualitative-member-states').length > 0 || $('div#methodology').length > 0){
		/*var text = "";
		var literal_type = $("#literal_type")[0].value;
		if(literal_type != undefined){
			if(literal_type == "HEADER" || literal_type == "KEY MESSAGE"
			|| literal_type.includes("INTRO TEXT") || literal_type.includes("INTROTEXT")
			|| literal_type.includes("INTRO_TEXT") || literal_type.includes("HEADER")
			|| literal_type == "CHART FOOTER"){
				text = CKEDITOR.instances.updatedTextEditor.getData();
			}else{
				text = $("#updatedTextEditor_default")[0].textContent;
			}
		}*/
		
		$("#updatedTextEditor_default").bind('input propertychange', function() {
		//$("#updatedTextEditor_default").keypress(function() {
			// Does some stuff and logs the event to the console
			var text = $("#updatedTextEditor_default").val();
			if(text != null && text != "" && text != $('#publishedText')[0].innerHTML){
			//if(text != null && text != "" && text != $('#publishedText')[0].textContent){
				if($('#modalSaveButton').hasClass('disabled')){
					$('#modalSaveButton').removeClass('disabled');
				}
			}else{
				if(!$('#modalSaveButton').hasClass('disabled')){
					$('#modalSaveButton').addClass('disabled');
				}
			}
		});		
		
		CKEDITOR.instances.updatedTextEditor.on('change', function() {
			if($('div#qualitative-member-states').length > 0){
				text = CKEDITOR.instances.updatedTextEditor.getData();
			}else{
				var literal_type = $("#literal_type")[0].value;
				if(literal_type != undefined){
					if(literal_type == "HEADER" || literal_type == "KEY MESSAGE"
					|| literal_type.includes("INTRO TEXT") || literal_type.includes("INTROTEXT")
					|| literal_type.includes("INTRO_TEXT") || literal_type.includes("HEADER")
					|| literal_type == "CHART FOOTER" || literal_type != "Indicator Name"){
						text = CKEDITOR.instances.updatedTextEditor.getData();
						//text = text.substring(0, text.lastIndexOf("\n"));
					}else{
						text = $("#updatedTextEditor_default").val();
					}
				}
			}
			
			
			//if(text != null && text != "" && text != $('#publishedText')[0].value.trim()){
			if(text != null && text != "" && text != $('#publishedText')[0].innerHTML){
			//if(text != null && text != "" && text != $('#publishedText')[0].textContent){
				if($('#modalSaveButton').hasClass('disabled')){
					$('#modalSaveButton').removeClass('disabled');
				}
			}else{
				if(!$('#modalSaveButton').hasClass('disabled')){
					$('#modalSaveButton').addClass('disabled');
				}
			}
		});
		CKEDITOR.instances.updatedTextEditor.on('key', function() {
			if($('div#qualitative-member-states').length > 0){
				text = CKEDITOR.instances.updatedTextEditor.getData();
			}else{
				var literal_type = $("#literal_type")[0].value;
				if(literal_type != undefined){
					if(literal_type == "HEADER" || literal_type == "KEY MESSAGE"
					|| literal_type.includes("INTRO TEXT") || literal_type.includes("INTROTEXT")
					|| literal_type.includes("INTRO_TEXT") || literal_type.includes("HEADER")
					|| literal_type == "CHART FOOTER" || literal_type != "Indicator Name"){
						text = CKEDITOR.instances.updatedTextEditor.getData();
						//text = text.substring(0, text.lastIndexOf("\n"));
					}else{
						text = $("#updatedTextEditor_default").val();
					}
				}
			}
			
			//if(text != null && text != "" && text != $('#publishedText')[0].value.trim()){
			if(text != null && text != "" && text != $('#publishedText')[0].innerHTML){
			//if(text != null && text != "" && text != $('#publishedText')[0].textContent){
				if($('#modalSaveButton').hasClass('disabled')){
					$('#modalSaveButton').removeClass('disabled');
				}
			}else{
				if(!$('#modalSaveButton').hasClass('disabled')){
					$('#modalSaveButton').addClass('disabled');
				}
			}
		});
		
		$('div#wait-message').css("display","none");
		$('div#wait-message-space').css("display","none");
		
		var text_updates = $('.span_updated_text');
		for (var i = 0; i < text_updates.length; i++) {
			if(text_updates[i].textContent != ""){
				if($('#updateAllButton').hasClass('disabled')){
					$('#updateAllButton').removeClass('disabled');
				}
				break;
			}
		}
		enableUpdateAllButton();
	}
	
	if($('div#quantitative-page').length > 0 || $('div#container-eurostat-quantitative').length > 0){
		$('div#wait-message').css("display","none");
		$('div#wait-message-space').css("display","none");
	}
	
	/** Function called in Update labels, Qualitative data for MS and Methodology pages.
	* It prepares the modal window to edit an specific literal.
	* @index var index of the current literal on the table
	*/
	editModal = function(index, literal_type/*, published_text, updated_text*/){
		console.log("Arrives to editModal");
		
		var section = document.getElementById("sectionSelect");
		var sectionSelected = section.value;
		
		var chart = document.getElementById("chartSelect");
		var chartSelected = 0;
		
		if(chart != null){
			chartSelected = chart.value;
		}
		
		var country = document.getElementById("countrySelect");
		var countrySelected = 0;
		
		if(country != null){
			countrySelected = country.value;
		}
		
		var institution = document.getElementById("institutionSelect");
		var institutionSelected = 0;
		
		if(institution != null){
			institutionSelected = institution.value;
		}
		
		var indicator = document.getElementById("indicatorSelect");
		var indicatorSelected = 0;
		
		if(indicator != null){
			indicatorSelected = indicator.value;
		}
		
		var translation_id = $("#translation_id_"+index).val();
		
		var published_text = $("#span_published_text_"+index)[0].innerHTML;
		var updated_text = $("#span_updated_text_"+index)[0].textContent;
		var escaped_published_text = $("#escaped_published_text-"+index).val();
		var escaped_updated_text = $("#escaped_updated_text-"+index).val();
		
		if($('div#update-labels').length > 0){
			$("#edit-popup input#literal_type").val(literal_type);
			if(literal_type == "HEADER" || literal_type == "KEY MESSAGE"
				|| literal_type.includes("INTRO TEXT") || literal_type.includes("INTROTEXT")
				|| literal_type.includes("INTRO_TEXT") || literal_type.includes("HEADER")
				/*|| literal_type == "OVERALL OP_HEADER" || literal_type == "MENTAL RISKS_HEADER"
				|| literal_type == "PHYSICAL RISKS_HEADER"*/
				//|| literal_type == "CHART FOOTER"
				|| literal_type == "CHART FOOTER"){
				$("#edit-popup #updatedTextEditor_default").css("display","none");
				$("#edit-popup #updatedTextEditor_default").css("visibility","hidden");
				$("#edit-popup #cke_updatedTextEditor").css("display","block");
				//cke_updatedTextEditor
			}else{
				$("#edit-popup #updatedTextEditor_default").css("display","block");
				$("#edit-popup #updatedTextEditor_default").css("visibility","visible");
				$("#edit-popup #cke_updatedTextEditor").css("display","none");
			}
		}
		
		if($('div#methodology').length > 0){
			console.log("literal type: "+literal_type);
			$("#edit-popup input#literal_type").val(literal_type);
			if(literal_type == "Indicator Name"){
				$("#edit-popup #updatedTextEditor_default").css("display","block");
				$("#edit-popup #updatedTextEditor_default").css("visibility","visible");
				$("#edit-popup #cke_updatedTextEditor").css("display","none");
			}else{
				$("#edit-popup #updatedTextEditor_default").css("display","none");
				$("#edit-popup #updatedTextEditor_default").css("visibility","hidden");
				$("#edit-popup #cke_updatedTextEditor").css("display","block");
			}
		}
		
		if(updated_text != null && updated_text != ""){
			if(escaped_published_text != escaped_updated_text){
			//if(published_text != updated_text){
				$("#edit-popup #updatedTextEditor").text(escaped_updated_text);
				if($('div#update-labels').length > 0 || $('div#methodology').length > 0){
					$("#edit-popup #updatedTextEditor_default").val(escaped_updated_text);
				}
				CKEDITOR.instances.updatedTextEditor.setData(escaped_updated_text);
			}else{
				$("#edit-popup #updatedTextEditor").text(escaped_published_text);
				if($('div#update-labels').length > 0 || $('div#methodology').length > 0){
					$("#edit-popup #updatedTextEditor_default").val(escaped_published_text);
				}
				CKEDITOR.instances.updatedTextEditor.setData(escaped_published_text);
			}
		}else{
			$("#edit-popup #updatedTextEditor").text(escaped_published_text.trim());
			if($('div#update-labels').length > 0 || $('div#methodology').length > 0){
				$("#edit-popup #updatedTextEditor_default").val(escaped_published_text.trim());
			}
			//CKEDITOR.instances.updatedTextEditor.setData(escaped_published_text);
			
			CKEDITOR.instances.updatedTextEditor.setData(published_text.trim());
		}
		
		//$(".popup input#literal_id").val(literal_id);
		$("#edit-popup input#translation_id").val(translation_id);
		$("#edit-popup input#popUpSection").val(sectionSelected);
		if(chart != null){
			$("#edit-popup input#popUpChart").val(chartSelected);
		}
		if(country != null){
			$("#edit-popup input#popUpCountry").val(countrySelected);
		}
		if(institution != null){
			$("#edit-popup input#popUpInstitution").val(institutionSelected);
		}
		if(indicator != null){
			$("#edit-popup input#popUpIndicator").val(indicatorSelected);
		}
		
		$("#edit-popup p#publishedText").html(published_text.trim());
		//$("#edit-popup #publishedText").val(published_text.trim());
		
		$("#edit-popup").css("display","block");
		enableUpdateAllButton();
	};
	
	/** Function called to after exiting the edit modal when closing it or
	* saving a literal.
	*/
	disableSaveButton = function(){
		CKEDITOR.instances.updatedTextEditor.setData("");
		$("#updatedTextEditor_default").val("");
		if(!$('#modalSaveButton').hasClass('disabled')){
			$('#modalSaveButton').addClass('disabled');
		}
	};
	
	/** Function called in Update labels, Qualitative data for MS and Methodology pages.
	* It prepares the modal window to undo a literal draft change
	* @index var index of the current literal on the table
	*/
	undoPopup = function(index/*, published_text, updated_text*/){
		console.log("Arrives to undoPopup");
		var section = document.getElementById("sectionSelect");
		var sectionSelected = section.value;
		
		var chart = document.getElementById("chartSelect");
		var chartSelected = 0;
		
		if(chart != null){
			chartSelected = chart.value;
		}
		
		var country = document.getElementById("countrySelect");
		var countrySelected = 0;
		
		if(country != null){
			countrySelected = country.value;
		}
		
		var institution = document.getElementById("institutionSelect");
		var institutionSelected = 0;
		
		if(institution != null){
			institutionSelected = institution.value;
		}
		
		var indicator = document.getElementById("indicatorSelect");
		var indicatorSelected = 0;
		
		if(indicator != null){
			indicatorSelected = indicator.value;
		}
		
		var translation_id = $("#translation_id_"+index).val();
		
		
		var published_text = $("#span_published_text_"+index)[0].innerHTML;
		var updated_text = $("#span_updated_text_"+index)[0].innerHTML;
		
		//var published_text = $("#span_published_text_"+index)[0].textContent;
		//var updated_text = $("#span_updated_text_"+index)[0].textContent;
		
		if(updated_text != "null" && updated_text != ""){
            if(updated_text != published_text){
                $('#modalUndoButton').removeClass('disabled');
            }else{
                $('#modalUndoButton').addClass('disabled');
            }
        }else{
            $('#modalUndoButton').addClass('disabled');
        }
		
		$("#undo-popup input#undo_translation_id").val(translation_id);
		$("#undo-popup input#popUpUndoSection").val(sectionSelected);
		if(chart != null){
			$("#undo-popup input#popUpUndoChart").val(chartSelected);
		}
		if(country != null){
			$("#undo-popup input#popUpUndoCountry").val(countrySelected);
		}
		if(institution != null){
			$("#undo-popup input#popUpUndoInstitution").val(institutionSelected);
		}
		if(indicator != null){
			$("#undo-popup input#popUpUndoIndicator").val(indicatorSelected);
		}
		
		$("#undo-popup").css("display","block");
	};
	
	/** Function called in Update labels, Qualitative data for MS and Methodology pages.
	* It controls the functionality of the chekboxes and the publish button in order to
	* enable or disable them depending on the literal updates
	*/
	checkTextChanges = function(/*translation_id, published_text, updated_text*/) { 
		console.log("Arrives checkTextChanges");
		var checks = $('input[type=checkbox]');
		
		var i = 0;
		var valid = false;
		var update_text_i = "null";
		var published_text_i = "null";
		while (i < checks.length && !valid) {
			if(checks[i].checked == true){
				var checkId = checks[i].id.substring(checks[i].id.indexOf('-')+1);
				//update_text_i = $("#span_updated_text_"+checkId)[0].textContent;
				//published_text_i = $("#span_published_text_"+checkId)[0].textContent;
				update_text_i = $("#span_updated_text_"+checkId)[0].innerHTML;
				published_text_i = $("#span_published_text_"+checkId)[0].innerHTML;
				if(update_text_i != "null" && update_text_i != "" && update_text_i != published_text_i){
					valid = true;
				}
			}
			i++;
        }
		
		if(valid){
			$('#publishButton').removeClass('disabled');
		}else{
			if(!$('#publishButton').hasClass('disabled')){
				$('#publishButton').addClass('disabled');
			}
		}
	};
	
	/** Function that displays the confirmation modal after clicking on publish button
	*/
	openConfirmationModal = function() {
		$("#confirm-popup").css("display","block");
	}
	
	/** Function that shows a message when an ETL process is executed
	*/
	showWaitAlert = function(){
		//console.log('Enters showWaitAlert function');
		$('div#wait-message').css("display","block");
		$('div#wait-message-space').css("display","block");
	}
	
	/**
	*/
	loadingScreen = function(){
		//console.log('Enters loadingScreen function');
		//$('div.loading-screen').css('display', 'block');
	}
	
	/** Function to reset to default fields in Country reports for MS page
	*/
	resetFields = function(){
		//console.log('Enters resetFields');
		$('#section').val('osh_authorities');
		changeCountryDisplay('true');
	}
	
	loadCountriesQualitativeMS = function(){
		//console.log("Enters loadCountriesAndInstitution function");
		var sectionSelected = document.getElementById("sectionSelect");
		var valueSelected = sectionSelected.value;
		
		$("#countrySelect").val('AT');
		
		$.get({
			url: 'countrydisplay',
			data: {
				section: valueSelected
			},
			success: function(countryResponse) {
		        var countryList = JSON.parse(countryResponse);
		        var new_tbody = "";
		        $('#countrySelect').empty();
		        countryList.forEach(function(country){
					if(country.country_code == 'AT'){
						new_tbody = new_tbody.concat('<option value="'+country.country_code+'" selected>');
					}else{
						new_tbody = new_tbody.concat('<option value="'+country.country_code+'" >');
					}
					
					if(country.country_code == 'EU28'){
						new_tbody = new_tbody.concat(country.country_code);
					}else{
						new_tbody = new_tbody.concat('('+country.country_code+') '+country.country_name);
					}
					new_tbody = new_tbody.concat('</option>');
		        });
		        $('#countrySelect').html(new_tbody);
			},
			async: true
		});
		
		if($('div#qualitative-member-states').length > 0){
			loadInstitutionsMS(valueSelected);
			if(valueSelected == "STRATEGY" || valueSelected == "STRATEGY_ENFOR_CAPACITY"){
				$('div#strategies-page-table').css('display', 'block');
				$('div#matrix-page-table').css('display', 'none')
				loadLiteralsTable('qualitativeMS');
				console.log("Enters loadCountriesQualitativeMS function: "+valueSelected);
			}else{
				$('div#strategies-page-table').css('display', 'none');
				$('div#matrix-page-table').css('display', 'block');
				loadLiteralsMatrixPage();
				console.log("Enters loadCountriesQualitativeMS function: "+valueSelected);
			}
		}
	}
	
	loadLiteralsMatrixPage = function (){
		console.log("Enters loadLiteralsMatrixPage function");
		var section = document.getElementById("sectionSelect");
		var sectionSelected = section.value;
		
		var country = document.getElementById("countrySelect");
		var countrySelected = country.value;
		
		var institution = document.getElementById("institutionSelect");
		var institutionSelected = institution.value;
		
		$.get({
			url: 'matrixpagesize',
			data: {
				section: sectionSelected,
				country : countrySelected,
				institution: institutionSelected
			},
			success: function(countResponse){
				var countList = JSON.parse(countResponse);
				var index = 0;
				var new_tbody = "";
				$('div#matrix-page-table').empty();
				//$('div#strategies-page-table').empty();
				$('#literalListBody').empty();
				countList.forEach(function(count){
					new_tbody = new_tbody.concat('<table class="literals columns-4">');
					new_tbody = new_tbody.concat('<thead><tr>');
					new_tbody = new_tbody.concat('<th>Publish</th>');
					new_tbody = new_tbody.concat('<th>Published text</th>');
					new_tbody = new_tbody.concat('<th>Updated text</th>');
					new_tbody = new_tbody.concat('<th>Actions</th>');
					new_tbody = new_tbody.concat('</tr></thead>');
					new_tbody = new_tbody.concat('<tbody id="matrixListBody_"'+count.data_id+'>');
					//new_tbody = new_tbody.concat('');
					//new_tbody = new_tbody.concat('');
					
					$.get({
						url: 'tableload',
						data: {
							section: sectionSelected,
							country : countrySelected,
							institution: institutionSelected,
							get: 'qualitativeMS'
						},
						success: function(literalsResponse) {
							//if(data.get("data_id").equals(count.get("data_id")))
					        var literalsList = JSON.parse(literalsResponse);
							//var index = 0;
							//var new_tbody = "";
							var literalListSize = literalsList.length;
							$('input[name="literalListSize"]').val(literalListSize);
					        //$('#matrixListBody_'+count.data_id).empty();
					        literalsList.forEach(function(literal){
								if(literal.data_id == count.data_id){
									new_tbody = new_tbody.concat('<tr>');
									new_tbody = new_tbody.concat('<td>');
									new_tbody = new_tbody.concat('<input ');
									if(literal.updated_text != null || literal.updated_text != undefined){
										if(literal.escaped_updated_text == literal.escaped_published_text){
											new_tbody = new_tbody.concat('disabled');
										}
									}else{
										new_tbody = new_tbody.concat('disabled');
									}
									new_tbody = new_tbody.concat(' id="check-'+index+'" type="checkbox" onchange="checkTextChanges()" name="publishCheck_'+index+'">');
									new_tbody = new_tbody.concat('<input type="hidden" value="'+literal.translation_id+'" name="translation_id_'+index+'" id="translation_id_'+index+'">');
									new_tbody = new_tbody.concat('<input type="hidden" value="'+sectionSelected+'" name="section_'+index+'">');
									/*if(page == 'literals'){
										new_tbody = new_tbody.concat('<input type="hidden" value="'+chartSelected+'" name="chart_'+index+'">');
									}else if(page == 'qualitativeMS'){*/
									new_tbody = new_tbody.concat('<input type="hidden" value="'+countrySelected+'" name="country_'+index+'">');
									new_tbody = new_tbody.concat('<input type="hidden" value="'+institutionSelected+'" name="institution_'+index+'">');
									/*}else{
										new_tbody = new_tbody.concat('<input type="hidden" value="'+indicatorSelected+'" name="indicator_'+index+'">');
									}*/
									
									new_tbody = new_tbody.concat('<input type="hidden" value="'+literal.escaped_updated_text+'" name="escaped_updated_text_'+index+'" id="escaped_updated_text-'+index+'">');
									new_tbody = new_tbody.concat('<input type="hidden" value="'+literal.escaped_published_text+'" name="escaped_published_text_'+index+'" id="escaped_published_text-'+index+'">');
									new_tbody = new_tbody.concat('</td>');
									/*if(page != 'qualitativeMS' || (literal.page == "STRATEGY" || literal.page == "STRATEGY_ENFOR_CAPACITY")){
										new_tbody = new_tbody.concat('<td>');
										if(literal.literal_type != null && literal.literal_type != ""){
											new_tbody = new_tbody.concat(literal.literal_type.replace('_', ' '));
										}
										new_tbody = new_tbody.concat('</td>');
									}*/
									
									if(literal.published_text != "null"){
										new_tbody = new_tbody.concat('<td><span id="span_published_text_'+index+'">'+literal.published_text+'</span></td>');
									}else{
										new_tbody = new_tbody.concat('<td><span id="span_published_text_'+index+'"></span></td>');
									}
									
									if(literal.updated_text != null || literal.updated_text != undefined){
										new_tbody = new_tbody.concat('<td><span class="span_updated_text" id="span_updated_text_'+index+'">'+literal.updated_text+'</span></td>');
									}else{
										new_tbody = new_tbody.concat('<td><span class="span_updated_text" id="span_updated_text_'+index+'"></span></td>');	
									}
									new_tbody = new_tbody.concat('<td><a class="href-link" href="#" onclick="editModal(\''+index+'\')">Edit</a> ');
									
									if(literal.updated_text != null && literal.updated_text != literal.escaped_published_text){
										new_tbody = new_tbody.concat('<a class="href-link" href="#" onclick="undoPopup(\''+index+'\')">Undo</a>');
									}else{
										new_tbody = new_tbody.concat('<a class="href-link disabled" href="#" onclick="undoPopup(\''+index+'\')">Undo</a>');
									}
									new_tbody = new_tbody.concat('</td>');
									new_tbody = new_tbody.concat('</tr>');
									index++;
								}
					        });
					        //$('#literalListBody').html(new_tbody);
						},
						async: false
					});
					
					new_tbody = new_tbody.concat('</tbody>');
					new_tbody = new_tbody.concat('</table>');
				});
				$('div#matrix-page-table').html(new_tbody);
			},
			async: true
		}).done(function(data){
				enableUpdateAllButton();
			});
	}
	
	loadInstitutionsMS = function(sectionSelected){
		//console.log("Enters loadInstitutionsMS function");
		var new_tbody = "";
		$('#institutionSelect').empty();
		$('#institutionSelect').css('display', 'block');
		$('#institution_type_label').css('display', 'block');
		if(sectionSelected == 'MATRIX_AUTHORITY') {
			$('#institution_type_label').text("Institution type");
			new_tbody = new_tbody.concat('<option selected value="osh_authority">OSH authority</option>');
			new_tbody = new_tbody.concat('<option value="compensation_insurance">Compensation and insurance body</option>');
			new_tbody = new_tbody.concat('<option value="prevention_institute">Prevention institute</option>');
			new_tbody = new_tbody.concat('<option value="standardisation_body">Standardisation body</option>');
		}else if(sectionSelected == 'MATRIX_STRATEGY') {
			$('#institution_type_label').text("Challenges");
			new_tbody = new_tbody.concat('<option selected value="implementation_record">Implementation record</option>');
			new_tbody = new_tbody.concat('<option value="prevention_diseases">Prevention of work-related diseases</option>');
			new_tbody = new_tbody.concat('<option value="tackling_demographic">Tackling demographic change</option>');
		}else if(sectionSelected == 'MATRIX_STATISTICS') {
			$('#institution_type_label').text("Category");
			new_tbody = new_tbody.concat('<option selected value="osh_statistics">OSH statistics</option>');
			new_tbody = new_tbody.concat('<option value="surveys">Surveys</option>');
			new_tbody = new_tbody.concat('<option value="research_institutes">Research Institutes</option>');
		} else {
			$('#institution_type_label').css('display', 'none');
			$('#institutionSelect').css('display', 'none');
		}
		
		$('#institutionSelect').html(new_tbody);
		
		//loadLiteralsTable('qualitativeMS');
		//loadLiteralsMatrixPage();
	}
	
	loadLiterals = function(){
		console.log("Enters loadLiterals function");
		var section = document.getElementById("sectionSelect");
		var sectionSelected = section.value;
		if(sectionSelected == "STRATEGY" || sectionSelected == "STRATEGY_ENFOR_CAPACITY"){
			loadLiteralsTable('qualitativeMS');
		}else{
			loadInstitutionsMS(sectionSelected);
			loadLiteralsMatrixPage();
		}
	}
	
	loadIndicatorsBySection = function() {
		//console.log("Enters in loadIndicatorsBySection");
		var sectionSelected = document.getElementById("sectionSelect");
		var valueSelected = sectionSelected.value;
		
		$.get({
			url: 'indicatorload',
			data: {
				section: valueSelected
			},
			success: function(indicatorResponse) {
		        var indicatorList = JSON.parse(indicatorResponse);
		        var new_tbody = "";
		        $('#indicatorSelect').empty();
				var index = 0;
		        indicatorList.forEach(function(indicator){
					new_tbody = new_tbody.concat('<option value="'+indicator.indicator_id+'" ');
					if(index == 0){
						new_tbody = new_tbody.concat(' selected');
					}
					new_tbody = new_tbody.concat(' >');
					new_tbody = new_tbody.concat(indicator.indicator_name_2);
					new_tbody = new_tbody.concat('</option>');
					index++;
		        });
		        $('#indicatorSelect').html(new_tbody);
			},
			async: false
		});
		
		loadLiteralsTable('methodology');
	};
	
	if($('div#qualitative-member-states').length > 0){
		var section = document.getElementById("sectionSelect");
		var sectionSelected = section.value;
		if(sectionSelected == "STRATEGY" || sectionSelected == "STRATEGY_ENFOR_CAPACITY"){
			$('div#strategies-page-table').css('display', 'block');
			$('div#matrix-page-table').css('display', 'none')
			$('#institutionSelect').css('display', 'none');
			$('#institution_type_label').css('display', 'none');
		}else{
			$('div#strategies-page-table').css('display', 'none');
			$('div#matrix-page-table').css('display', 'block')
			$('#institutionSelect').css('display', 'block');
			$('#institution_type_label').css('display', 'block');
		}
		enableUpdateAllButton();
	}
});

})(jQuery);