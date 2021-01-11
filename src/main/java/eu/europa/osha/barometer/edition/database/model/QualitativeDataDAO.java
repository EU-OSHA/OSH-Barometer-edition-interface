package eu.europa.osha.barometer.edition.database.model;

import java.util.ArrayList;
import java.util.HashMap;

public class QualitativeDataDAO {
	private static QualitativeDataDAO INSTANCE = null;
    private static final String url = "eu.europa.osha.barometer.edition.webui.conf.database_statements";

    private QualitativeDataDAO () { super(); }

    public static synchronized QualitativeDataDAO getInstance()
    {
        if(INSTANCE == null)
        {
            INSTANCE = new QualitativeDataDAO();
        }
        return INSTANCE;
    }
    
    
    public ArrayList<HashMap<String,String>> getSectionsForDatasetUpdate(){
    	ArrayList<HashMap<String, String>> list = new ArrayList<HashMap<String, String>>();
    	try {
    		String query = "database.select.getSectionsForDatasetUpdate";
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
    
    public ArrayList<HashMap<String,String>> getChartsBySection(String id){
    	ArrayList<HashMap<String, String>> list = new ArrayList<HashMap<String, String>>();
    	try {
    		String query = "database.select.getChartsBySection";
    		String[] queryParams = {id};
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
    
    public ArrayList<HashMap<String,String>> getDatasetsForIndicator(String id){
    	ArrayList<HashMap<String, String>> list = new ArrayList<HashMap<String, String>>();
    	try {
    		String query = "database.select.getDatasetsForIndicator";
    		String[] queryParams = {id};
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
    
    public boolean updateIndicatorsDataset(String chart_id, String indicatorId, String datasetId) {
    	boolean updatedCorrectly = false;
    	try {
	    	String query = "database.update.updateIndicatorsDataset";
	    	String[] queryParams = {datasetId, indicatorId, chart_id};
	        JDBCDataSourceOperations.launchQuery(query, queryParams, url);
	        updatedCorrectly = true;
    	} catch (Exception e) {
    		System.out.println(e.getMessage());
            updatedCorrectly = false;
    	}
    	return updatedCorrectly;
    }
}
