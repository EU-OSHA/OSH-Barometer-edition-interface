package eu.europa.osha.barometer.edition.webui.business;

import java.util.ArrayList;
import java.util.HashMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import eu.europa.osha.barometer.edition.database.model.QualitativeDataDAO;

public class QualitativeDataBusiness {
	private static final Logger LOGGER = LogManager.getLogger(QualitativeDataBusiness.class);

	public static ArrayList<HashMap<String,String>> getSectionsForDatasetUpdate() {
		LOGGER.info("Accessing data DAO in order to get Sections for dataset update page.");
		QualitativeDataDAO dataDAO = QualitativeDataDAO.getInstance();
		ArrayList<HashMap<String,String>> dataList = dataDAO.getSectionsForDatasetUpdate();
		LOGGER.info("Retrieved data from database. Data list length: "+dataList.size());
		return dataList;
	}
	
	public static ArrayList<HashMap<String,String>> getChartsBySection(String id) {
		LOGGER.info("Accessing data DAO in order to get Charts by section for dataset update page.");
		QualitativeDataDAO dataDAO = QualitativeDataDAO.getInstance();
		ArrayList<HashMap<String,String>> dataList = dataDAO.getChartsBySection(id);
		LOGGER.info("Retrieved data from database. Data list length: "+dataList.size());
		for(HashMap<String, String> row: dataList){
			if(row.get("chart_id").equals("20088")) {
				row.put("chart_name", row.get("chart_name").concat(" EURO"));
			} else if(row.get("chart_id").equals("20014")) {
				row.put("chart_name", row.get("chart_name").concat(" (PPS)"));
			} 
        }
		return dataList;
	}
	
	public static ArrayList<HashMap<String,String>> getDatasetsForIndicator(String id) {
		LOGGER.info("Accessing data DAO in order to get available datasets for dataset update page.");
		QualitativeDataDAO dataDAO = QualitativeDataDAO.getInstance();
		ArrayList<HashMap<String,String>> dataList = dataDAO.getDatasetsForIndicator(id);
		LOGGER.info("Retrieved data from database. Data list length: "+dataList.size());
		return dataList;
	}
	
	public static boolean updateIndicatorsDataset(String chart_id, String indicator_id, String dataset_id) {
		boolean updatedCorrectly = false;
		QualitativeDataDAO dataDAO = QualitativeDataDAO.getInstance();
		updatedCorrectly = dataDAO.updateIndicatorsDataset(chart_id, indicator_id, dataset_id);
		return updatedCorrectly;
	}
}
