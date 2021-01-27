package eu.europa.osha.barometer.edition.database.model;

import java.util.ArrayList;
import java.util.HashMap;

public class CountryReportDAO {
	private static CountryReportDAO INSTANCE = null;
    private static final String url = "eu.europa.osha.barometer.edition.webui.conf.database_statements";

    private CountryReportDAO () { super(); }

    public static synchronized CountryReportDAO getInstance()
    {
        if(INSTANCE == null)
        {
            INSTANCE = new CountryReportDAO();
        }
        return INSTANCE;
    }
    
    public ArrayList<HashMap<String,String>> getOshAuthoritiesCountries(){
    	ArrayList<HashMap<String, String>> list = new ArrayList<HashMap<String, String>>();
    	try {
    		String query = "database.select.getOshAuthoritiesCountries";
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
    
    public ArrayList<HashMap<String,String>> getNationalStrategiesCountries(){
    	ArrayList<HashMap<String, String>> list = new ArrayList<HashMap<String, String>>();
    	try {
    		String query = "database.select.getNationalStrategiesCountries";
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
    
    public ArrayList<HashMap<String,String>> getSocialDialogueCountries(){
    	ArrayList<HashMap<String, String>> list = new ArrayList<HashMap<String, String>>();
    	try {
    		String query = "database.select.getSocialDialogueCountries";
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
}
