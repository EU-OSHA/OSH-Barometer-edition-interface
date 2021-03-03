package eu.europa.osha.barometer.edition.webui.business;

import java.util.ArrayList;
import java.util.HashMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import eu.europa.osha.barometer.edition.database.model.QualitativeDataDAO;

public class QualitativeDataBusiness {
	private static final Logger LOGGER = LogManager.getLogger(QualitativeDataBusiness.class);

	/**
	 * Function that calls DAO to get all sections related to Update Datasets page
	 * @return ArrayList<HashMap<String,String>> list of sections
	 */
	public static ArrayList<HashMap<String,String>> getSectionsForDatasetUpdate() {
		LOGGER.info("Accessing data DAO in order to get Sections for dataset update page.");
		QualitativeDataDAO dataDAO = QualitativeDataDAO.getInstance();
		ArrayList<HashMap<String,String>> dataList = dataDAO.getSectionsForDatasetUpdate();
		LOGGER.info("Retrieved data from database. Data list length: "+dataList.size());
		return dataList;
	}
	
	/**
	 * Function that calls DAO to get all charts related to a section
	 * @param id String ID of the selected section
	 * @return ArrayList<HashMap<String,String>> list of charts related to the selected section
	 */
	public static ArrayList<HashMap<String,String>> getChartsBySection(String id) {
		LOGGER.info("Accessing data DAO in order to get Charts by section for dataset update page.");
		QualitativeDataDAO dataDAO = QualitativeDataDAO.getInstance();
		ArrayList<HashMap<String,String>> dataList = dataDAO.getChartsBySection(id);
		LOGGER.info("Retrieved data from database. Data list length: "+dataList.size());
		return dataList;
	}
	
	/**
	 * Function that calls DAO to get all the indicators related to a chart
	 * @param id String ID of the selected chart
	 * @return ArrayList<HashMap<String,String>> list of indicators related to the selected chart
	 */
	public static ArrayList<HashMap<String,String>> getIndicatorsByChart(String id) {
		LOGGER.info("Accessing data DAO in order to get Indicators by chart for dataset update page.");
		QualitativeDataDAO dataDAO = QualitativeDataDAO.getInstance();
		ArrayList<HashMap<String,String>> dataList = dataDAO.getIndicatorsByChart(id);
		LOGGER.info("Retrieved data from database. Data list length: "+dataList.size());
		return dataList;
	}
	
	/**
	 * Function that calls DAO to get all datasets related to a specific indicator
	 * @param id String ID of the selected indicator
	 * @return ArrayList<HashMap<String,String>> list of dataset relatd to the selected indicator
	 */
	public static ArrayList<HashMap<String,String>> getDatasetsForIndicator(String id) {
		LOGGER.info("Accessing data DAO in order to get available datasets for dataset update page.");
		QualitativeDataDAO dataDAO = QualitativeDataDAO.getInstance();
		ArrayList<HashMap<String,String>> dataList = dataDAO.getDatasetsForIndicator(id);
		LOGGER.info("Retrieved data from database. Data list length: "+dataList.size());
		return dataList;
	}
	
	/**
	 * Function that calls DAO to update an indicators dataset
	 * @param chart_id String ID of the selected indicator
	 * @param indicator_id String ID of the selected indicator
	 * @param dataset_id String ID of the selected dataset
	 * @return boolean true if the indicators dataset has been updated, false if it was not updated
	 */
	public static boolean updateIndicatorsDataset(String chart_id, String indicator_id, String dataset_id) {
		boolean updatedCorrectly = false;
		QualitativeDataDAO dataDAO = QualitativeDataDAO.getInstance();
		updatedCorrectly = dataDAO.updateIndicatorsDataset(chart_id, indicator_id, dataset_id);
		return updatedCorrectly;
	}
}
