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
    
    /**
     * Function that calls DB to get all the sections for Dataset Update page
     * @return ArrayList<HashMap<String,String>> list of all the sections for the page
     */
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
    
    /**
     * Function that retrieves from DB all the charts relative to the selected section
     * @param idSection String ID of the section selected
     * @return ArrayList<HashMap<String,String>> list containing all the charts related to the selected section
     */
    public ArrayList<HashMap<String,String>> getChartsBySection(String idSection){
    	ArrayList<HashMap<String, String>> list = new ArrayList<HashMap<String, String>>();
    	try {
    		String query = "database.select.getChartsBySection";
    		String[] queryParams = {idSection};
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
     * Function that retrieves all the indicators relative to the selected chart
     * @param idChart String ID of the chart selected
     * @return ArrayList<HashMap<String,String>> list of indicators related to the selected chart
     */
    public ArrayList<HashMap<String,String>> getIndicatorsByChart(String idChart){
    	ArrayList<HashMap<String, String>> list = new ArrayList<HashMap<String, String>>();
    	try {
    		String query = "database.select.getIndicatorsByChart";
    		String[] queryParams = {idChart};
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
     * Function that retrieves from DB the possible datasets relative to a specific indicator
     * @param id String ID of the selected indicator
     * @return ArrayList<HashMap<String,String>> list of dataset related to the selected indicator
     */
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
    
    /**
     * Function that updates in DB the selected dataset for a specific indicator
     * @param chart_id String ID of the chart selected
     * @param indicatorId String ID of the indicator selected
     * @param datasetId String ID of the dataset selected
     * @return boolean true if the indicators dataset has been updated, false if it was not updated
     */
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
