package eu.europa.osha.barometer.edition.database.model;

import java.util.ArrayList;
import java.util.HashMap;

public class QuantitativeDataDAO {
	private static QuantitativeDataDAO INSTANCE = null;
    private static final String url = "eu.europa.osha.barometer.edition.webui.conf.database_statements";

    private QuantitativeDataDAO () { super(); }

    public static synchronized QuantitativeDataDAO getInstance()
    {
        if(INSTANCE == null)
        {
            INSTANCE = new QuantitativeDataDAO();
        }
        return INSTANCE;
    }
    
    /**
     * Function that calls DB to get the Eurostat Indicators for Quantitative data for Eurostat page
     * @return ArrayList<HashMap<String,String>> list of Eurostat indicators
     */
    public ArrayList<HashMap<String,String>> getIndicatorsForEurostat(){
    	ArrayList<HashMap<String, String>> list = new ArrayList<HashMap<String, String>>();
    	try {
    		String query = "database.select.getIndicatorsForEurostat";
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
