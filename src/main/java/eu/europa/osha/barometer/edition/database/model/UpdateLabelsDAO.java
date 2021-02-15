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
    
    public ArrayList<HashMap<String,String>> getLiteralsBySectionAndChart(String section_id, String chart_id){
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
    			query.append(" AND l.chart_id IS NULL ");
    		} else {
    			query.append(" AND l.chart_id = ");
    			query.append(chart_id);
    		}
    		query.append(" AND t.language = 'EN' ");
    		query.append("AND t.literal_id = l.id ");
    		String[] queryParams = {};
    		ArrayList<HashMap<String, String>> result = JDBCDataSourceOperations.launchPreparedQuery(query.toString());
    		//ArrayList<HashMap<String, String>> result = JDBCDataSourceOperations.launchSelect(query, queryParams, url);
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
