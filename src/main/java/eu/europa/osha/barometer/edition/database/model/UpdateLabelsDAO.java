package eu.europa.osha.barometer.edition.database.model;

import java.util.ArrayList;
import java.util.HashMap;

public class UpdateLabelsDAO {
	
	private static UpdateLabelsDAO INSTANCE = null;
    private static final String url = "eu.europa.osha.barometer.edition.webui.conf.database_statements";

    private UpdateLabelsDAO () { super(); }

    public static synchronized UpdateLabelsDAO getInstance()
    {
        if(INSTANCE == null)
        {
            INSTANCE = new UpdateLabelsDAO();
        }
        return INSTANCE;
    }

    /**
     * Function that calls DB to retrieve all the sections for BAROMETER DVT
     * @return ArrayList<HashMap<String,String>> list containing the sections data
     */
    public ArrayList<HashMap<String,String>> getSectionList(){
    	ArrayList<HashMap<String, String>> list = new ArrayList<HashMap<String, String>>();
    	try {
    		String query = "database.select.getSectionList";
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
    
    /**
     * Function that retrieves from DB all the charts contained in an specific BAROMETER section
     * @param section String ID of the section
     * @return ArrayList<HashMap<String,String>> list with the relative charts of that section
     */
    public ArrayList<HashMap<String,String>> getChartsBySectionUpdateLabels(String section){
    	ArrayList<HashMap<String, String>> list = new ArrayList<HashMap<String, String>>();
    	try {
    		String query = "database.select.getChartsBySectionUpdateLabels";
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
    
    /**
     * Function that retrieves from DB the literals from the selected section and chart. If no chart is selected,
     * it retrieves all the literals of the section selected
     * @param section_id String ID of the section
     * @param chart_id String ID of the chart
     * @return ArrayList<HashMap<String,String>> list with all the literals contained in selected section and chart
     */
    public ArrayList<HashMap<String,String>> getLiteralsBySectionAndChart(String section_id, String chart_id) {
    	ArrayList<HashMap<String, String>> list = new ArrayList<HashMap<String, String>>();
    	try {
    		//String query = "database.select.getSectionList";
    		StringBuilder query =  new StringBuilder();
    		query.append("SELECT l.id as literal_id, t.id as translation_id, l.chart_id as chart_id, ");
    		query.append("l.section_id as section_id, t.text as published_text, t.draft_text as updated_text, ");
    		query.append("l.type as literal_type ");
    		query.append("FROM literal l, translation t ");
    		query.append("WHERE l.section_id = ");
    		query.append(section_id);
    		if(chart_id.equals("0")) {
    			//query.append(" AND l.chart_id IS NULL ");
    		} else {
    			query.append(" AND l.chart_id = ");
    			query.append(chart_id);
    		}
    		query.append(" AND t.language = 'EN' ");
    		query.append("AND t.literal_id = l.id ");
    		query.append("AND l.type NOT IN('REG_SECONDARY', 'REG_DEROGATIONS', 'REG_TRANSITIONAL', 'REG_RA', 'REG_EXEMPTIONS', 'REG_SMES', ");
    		query.append("'STRATEGY_BASIC INFO', 'STRATEGY_BACKGROUND', 'STRATEGY_CHARACTERISTICS', 'STRATEGY_DETAILS', ");
    		query.append("'STRATEGY_ACTORS', 'STRATEGY_RESOURCES', 'STRATEGY_EVALUATION', 'STRATEGY_EU', 'MATRIX_OB', 'MATRIX_DET', ");
    		query.append("'STRATEGY_RESOURCES & STRATEGY_EVALUATION', 'METHODOLOGY_TEXT') ");
    		String[] queryParams = {};
    		ArrayList<HashMap<String, String>> result = JDBCDataSourceOperations.launchPreparedQuery(query.toString());
    		//ArrayList<HashMap<String, String>> result = JDBCDataSourceOperations.launchSelect(query, queryParams, url);
    		if (result.size() > 0)
            {
                list = (ArrayList<HashMap<String, String>>) result.clone();
            }

    	} catch(Exception e) {
    		e.printStackTrace();
    	}
    	return list;
    }
    
    /**
     * Function that updates a literals draft_text 
     * @param updated_text String new text for draft_text field in DB
     * @param translation_id String ID of the literal to update
     * @return boolean true if the literal has been updated in DB, false in case it failed
     */
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
    
    /**
     * Function that deletes the draft text of a literal
     * @param translation_id String ID of the literal to update
     * @return boolean true if the operation has been undone in DB, false in case it failed
     */
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
    
    /**
     * Function that updates the publish text field of a literal and deletes its draft text field
     * @param translation_id String ID of the literal to update
     * @param updated_text String text that will replace the published text in DB
     * @return boolean true if the literal has been updated in DB, false in case it failed
     */
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
