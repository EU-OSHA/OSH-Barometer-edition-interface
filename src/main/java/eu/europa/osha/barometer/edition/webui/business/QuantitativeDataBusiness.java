package eu.europa.osha.barometer.edition.webui.business;

import java.util.ArrayList;
import java.util.HashMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import eu.europa.osha.barometer.edition.database.model.QuantitativeDataDAO;

public class QuantitativeDataBusiness {
	private static final Logger LOGGER = LogManager.getLogger(QuantitativeDataBusiness.class);
	
	/**
	 * Function that calls DAO to get a list of the Eurostat Indicators
	 * @return ArrayList<HashMap<String,String>> list with the Eurostat indicators
	 */
	public static ArrayList<HashMap<String,String>> getIndicatorsForEurostat(){
		LOGGER.info("Accessing data DAO in order to get Eurostat Indicators");
		QuantitativeDataDAO dataDAO = QuantitativeDataDAO.getInstance();
		ArrayList<HashMap<String,String>> dataList = dataDAO.getIndicatorsForEurostat();
		for(HashMap<String, String> row: dataList){
			if(row.get("indicator_id").equals("279")) {
				row.put("indicator_name", row.get("indicator_name").concat(" EURO"));
			} else if(row.get("indicator_id").equals("36")) {
				row.put("indicator_name", row.get("indicator_name").concat(" (PPS)"));
			} 
        }
		LOGGER.info("Retrieved data from database. Data list length: "+dataList.size());
		return dataList;
	}
}
