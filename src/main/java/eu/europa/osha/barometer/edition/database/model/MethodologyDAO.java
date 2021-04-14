package eu.europa.osha.barometer.edition.database.model;

import java.util.ArrayList;
import java.util.HashMap;

public class MethodologyDAO {
	private static MethodologyDAO INSTANCE = null;
    private static final String url = "eu.europa.osha.barometer.edition.webui.conf.database_statements";

    private MethodologyDAO () { super(); }

    public static synchronized MethodologyDAO getInstance()
    {
        if(INSTANCE == null)
        {
            INSTANCE = new MethodologyDAO();
        }
        return INSTANCE;
    }
    
    public ArrayList<HashMap<String,String>> getSectionsMethodology(){
    	ArrayList<HashMap<String, String>> list = new ArrayList<HashMap<String, String>>();
    	try {
    		String query = "database.select.getSectionsMethodology";
    		String[] queryParams = {};
    		ArrayList<HashMap<String, String>> result = JDBCDataSourceOperations.launchSelect(query, queryParams, url);
    		if (result.size() > 0)
            {
                list = (ArrayList<HashMap<String, String>>) result.clone();
            }

    	}catch(Exception e) {
    		e.printStackTrace();
    	}
    	return list;
    }
    
    public ArrayList<HashMap<String,String>> getIndicatorsBySection(String section){
    	ArrayList<HashMap<String, String>> list = new ArrayList<HashMap<String, String>>();
    	try {
    		String query = "database.select.getIndicatorsBySection";
    		String[] queryParams = {section};
    		ArrayList<HashMap<String, String>> result = JDBCDataSourceOperations.launchSelect(query, queryParams, url);
    		if (result.size() > 0)
            {
                list = (ArrayList<HashMap<String, String>>) result.clone();
            }

    	}catch(Exception e) {
    		e.printStackTrace();
    	}
    	return list;
    }
    
    public ArrayList<HashMap<String,String>> getLiteralsMethodology(String section, String indicator){
    	ArrayList<HashMap<String, String>> list = new ArrayList<HashMap<String, String>>();
    	try {
    		String [] quantitative_columns = {"indicator_name_displayed_literal_id", "description_literal_id", "source_methodology_literal_id", 
    				"specific_table_literal_id", "url_literal_id", "filtering_options_literal_id", "reference_year_literal_id", 
    				"last_update_literal_id", "coverage_literal_id", "unit_measure_literal_id", "calculations_literal_id", "visualisation_literal_id", 
    				"additional_comments_literal_id"};
    		String [] qualitative_columns = {"indicator_name_displayed_literal_id", "description_literal_id", "source_methodology_literal_id", 
    				"reference_year_literal_id", "last_update_literal_id", "coverage_literal_id", "visualisation_literal_id", "additional_comments_literal_id"};
    		String [] column_names_quantitative = {"Indicator Name", "Description", "Data Source", "Specific table/Question", "URL/source file", "Filtering options applied", 
    				"Reference year/period", "Last update", "Coverage", "Unit of measure", "Calculations on the data source", "Visualisation", 
    				"Additional comments" };
    		String [] column_names_qualitative = {"Indicator Name", "Description", "Data Source", "Reference year/period", 
    				"Last update", "Coverage", "Visualisation", "Additional comments" };
    		String [] qualitativeSection = {"38", "41", "40", "42", "44"};
    		String indicatorQuantitative = "285";
    		
    		StringBuilder query =  new StringBuilder();
    		boolean isQualitative = false;
    		
    		for(int i=0;i<qualitativeSection.length;i++) {
    			if(section.equals(qualitativeSection[i])) {
    				isQualitative = true;
    				break;
    			}
    		}
    		
    		if(section.equals("42") && indicator.equals(indicatorQuantitative)) {
    			isQualitative = false;
    		}
    	
    		if(!isQualitative) {
    			for(int i=0;i<quantitative_columns.length;i++) {
        			query.append("(SELECT m.indicator_id, m.section_id, t.id as translation_id, t.text as published_text, ");
        			query.append("t.draft_text as updated_text, \"");
        			query.append(column_names_quantitative[i]);
        			query.append("\" as literal_type ");
        			query.append("FROM methodology m, translation t ");
        			query.append("WHERE t.language=\"EN\" ");
        			query.append("AND t.literal_id = m.");
        			query.append(quantitative_columns[i]);
        			query.append(" AND m.section_id = ");
        			query.append(section);
        			query.append(" AND m.indicator_id = ");
        			query.append(indicator);
        			if(i == quantitative_columns.length-1) {
        				query.append(")");
        			}else {
        				query.append(")UNION ALL");
        			}
        		}
    		} else { 
    			for(int i=0;i<qualitative_columns.length;i++) {
        			query.append("(SELECT m.indicator_id, m.section_id, t.id as translation_id, t.text as published_text, ");
        			query.append("t.draft_text as updated_text, \"");
        			query.append(column_names_qualitative[i]);
        			query.append("\" as literal_type ");
        			query.append("FROM methodology m, translation t ");
        			query.append("WHERE t.language=\"EN\" ");
        			query.append("AND t.literal_id = m.");
        			query.append(qualitative_columns[i]);
        			query.append(" AND m.section_id = ");
        			query.append(section);
        			query.append(" AND m.indicator_id = ");
        			query.append(indicator);
        			if(i == qualitative_columns.length-1) {
        				query.append(")");
        			}else {
        				query.append(")UNION ALL");
        			}
        		}
    		}
    		
    		String[] queryParams = {};
    		ArrayList<HashMap<String, String>> result = JDBCDataSourceOperations.launchPreparedQuery(query.toString());
    		if (result.size() > 0)
            {
                list = (ArrayList<HashMap<String, String>>) result.clone();
            }

    	}catch(Exception e) {
    		e.printStackTrace();
    	}
    	return list;
    }
    
    public boolean updateDraftText(String updated_text, String translation_id) {
    	boolean updatedCorrectly = false;
    	try {
	    	String query = "database.update.updateDraftText";
	    	String[] queryParams = {updated_text, translation_id};
	        JDBCDataSourceOperations.launchQuery(query, queryParams, url);
	        updatedCorrectly = true;
    	} catch (Exception e) {
    		System.out.println(e.getMessage());
            updatedCorrectly = false;
    	}
    	return updatedCorrectly;
    }
    
    public boolean undoDraftText(String translation_id) {
    	boolean updatedCorrectly = false;
    	try {
	    	String query = "database.update.undoDraftText";
	    	String[] queryParams = {translation_id};
	        JDBCDataSourceOperations.launchQuery(query, queryParams, url);
	        updatedCorrectly = true;
    	} catch (Exception e) {
    		System.out.println(e.getMessage());
            updatedCorrectly = false;
    	}
    	return updatedCorrectly;
    }
    
    public boolean publishLiteral(String translation_id, String updated_text) {
    	boolean updatedCorrectly = false;
    	try {
	    	String query = "database.update.publishLiteral";
	    	String[] queryParams = {updated_text, translation_id};
	        JDBCDataSourceOperations.launchQuery(query, queryParams, url);
	        updatedCorrectly = true;
    	} catch (Exception e) {
    		System.out.println(e.getMessage());
            updatedCorrectly = false;
    	}
    	return updatedCorrectly;
    }
}
