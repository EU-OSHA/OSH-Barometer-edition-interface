(function ($) {
  
$(document).ready(function(){
    $( ".close-click" ).click(function() {
      $( ".popup" ).fadeOut( "slow" );
    });
    
    $( ".view-click" ).click(function() {
      $( ".popup" ).fadeIn( "slow" );
    });
    
    $( "#yearFromContainer" ).css("display","none");
	$( "#yearToContainer" ).css("display","none");
	$( "#oneYearContainer" ).css("display","block");
	$("#templateUsage").text('The template should be "EU-OSHA_OIE_Eurostat indicator_Company size_YYYY-MM-DD.xlsx"');
    
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
			$("#templateUsage").text('The template should be "EU-OSHA_OIE_Eurostat indicator_Company size_YYYY-MM-DD.xlsx"');
		}else if(valueSelected == 32){
			$("#templateUsage").text('The template should be "EU-OSHA_OIE_Eurostat indicator_Employment per sector_YYYY/MM/DD.xlsx"');
		}else if(valueSelected == 39){
			$("#templateUsage").text('The template should be "EU-OSHA_OIE_Eurostat indicator_Employment rate T_M_F_YYYY-MM-DD.xlsx"');
		}else if(valueSelected == 36){
			$("#templateUsage").text('The template should be "EU-OSHA_OIE_Eurostat_Income_per_capita_YYYY-MM-DD.xlsx"');
		}else if(valueSelected == 279){
			$("#templateUsage").text('The template should be "EU-OSHA_OIE_Eurostat_Income_per_capita_EURO_YYYY-MM-DD.xlsx"');
		}else if(valueSelected == 53){
			$("#templateUsage").text('The template should be "EU-OSHA_OIE_Eurostat_NonFatal_Work_accidents_YYYY-MM-DD.xlsx"');
		}else{
			$("#templateUsage").text('The template should be "EU-OSHA_OIE_Eurostat_Direct value indicators_YYYY-MM-DD.xlsx"');
		}
	};
	
	changeChartsInTable = function () {
		console.log("Enters changeChartsInTable function");
		
		var optionSelected = document.getElementById("sectionId");
		var valueSelected = optionSelected.value;
		var chartIndex = 1;
				
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
					new_tbody = new_tbody.concat('<label>Chart '+chartIndex+':</label>');
					new_tbody = new_tbody.concat('<p class="table">Chart name</p>');
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
								new_tbody = new_tbody.concat('<form id="formChart'+indicator.chart_id+'" action="user?page=update_datasets" method="post">');
								new_tbody = new_tbody.concat('<input type="hidden" value="'+indicator.chart_id+'" name="chart_id">');
								new_tbody = new_tbody.concat('<input type="hidden" value="'+indicator.indicator_id+'" name="indicator_id">');
								new_tbody = new_tbody.concat('<input type="hidden" value="'+indicator.section_id+'" name="section_id">');
								new_tbody = new_tbody.concat('');
								new_tbody = new_tbody.concat('<select id="datasetChart-'+indicator.chart_id+'" name="datasetChart-'+indicator.chart_id+'">')
					
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
								new_tbody = new_tbody.concat('<td><button type="submit" name="formSent" value="Save" form="formChart'+indicator.chart_id+'">Save</button></td>');
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
	
});

})(jQuery);