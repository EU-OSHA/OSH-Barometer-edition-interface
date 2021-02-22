package eu.europa.osha.barometer.edition.webui.business;

import java.util.ArrayList;
import java.util.HashMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import eu.europa.osha.barometer.edition.database.model.QualitativeMSDataDAO;
import eu.europa.osha.barometer.edition.database.model.UpdateLabelsDAO;
import eu.europa.osha.barometer.edition.webui.utils.EscapeHtmlTags;

public class QualitativeMSDataBusiness {
	private static final Logger LOGGER = LogManager.getLogger(QualitativeMSDataBusiness.class);
	
	public static ArrayList<HashMap<String,String>> getCountriesByMatrixPageSection(String page) {
		LOGGER.info("Accessing data DAO in order to get countries by matrix page section.");
		QualitativeMSDataDAO dataDAO = QualitativeMSDataDAO.getInstance();
		ArrayList<HashMap<String,String>> dataList = dataDAO.getCountriesByMatrixPageSection(page);
		LOGGER.info("Retrieved data from database. Data list length: "+dataList.size());
		return dataList;
	}
	
	public static ArrayList<HashMap<String,String>> getCountriesByStrategiesPageSection(String page) {
		LOGGER.info("Accessing data DAO in order to get countries by strategies page section.");
		QualitativeMSDataDAO dataDAO = QualitativeMSDataDAO.getInstance();
		ArrayList<HashMap<String,String>> dataList = dataDAO.getCountriesByStrategiesPageSection(page);
		LOGGER.info("Retrieved data from database. Data list length: "+dataList.size());
		return dataList;
	}
	
	public static ArrayList<HashMap<String,String>> getMatrixPageDataByCountryAndInstitution(String page, String country, String institution) {
		LOGGER.info("Accessing data DAO in order to get countries by strategies page section.");
		QualitativeMSDataDAO dataDAO = QualitativeMSDataDAO.getInstance();
		ArrayList<HashMap<String,String>> dataList = dataDAO.getMatrixPageDataByCountryAndInstitution(page, country, institution);
		String update_text = null;
		String published_text = null;
		for(HashMap<String,String> data : dataList) {
			update_text = data.get("updated_text");
			published_text = data.get("published_text");
			data.put("escaped_updated_text", EscapeHtmlTags.escapeHTML(update_text));
			data.put("escaped_published_text", EscapeHtmlTags.escapeHTML(published_text));
		}
		LOGGER.info("Retrieved data from database. Data list length: "+dataList.size());
		return dataList;
	}
	
	public static ArrayList<HashMap<String,String>> getStrategiesPageDataByCountryAndInstitution(String page, String country) {
		LOGGER.info("Accessing data DAO in order to get countries by strategies page section.");
		QualitativeMSDataDAO dataDAO = QualitativeMSDataDAO.getInstance();
		ArrayList<HashMap<String,String>> dataList = dataDAO.getStrategiesPageDataByCountryAndInstitution(page, country);
		String update_text = null;
		String published_text = null;
		for(HashMap<String,String> data : dataList) {
			update_text = data.get("updated_text");
			published_text = data.get("published_text");
			data.put("escaped_updated_text", EscapeHtmlTags.escapeHTML(update_text));
			data.put("escaped_published_text", EscapeHtmlTags.escapeHTML(published_text));
		}
		LOGGER.info("Retrieved data from database. Data list length: "+dataList.size());
		return dataList;
	}
	
	public static boolean updateDraftText(String updated_text, String translation_id) {
		boolean updatedCorrectly = false;
		QualitativeMSDataDAO dataDAO = QualitativeMSDataDAO.getInstance();
		updatedCorrectly = dataDAO.updateDraftText(updated_text, translation_id);
		return updatedCorrectly;
	}
	
	public static boolean undoDraftText(String translation_id) {
		boolean updatedCorrectly = false;
		QualitativeMSDataDAO dataDAO = QualitativeMSDataDAO.getInstance();
		updatedCorrectly = dataDAO.undoDraftText(translation_id);
		return updatedCorrectly;
	}
	
	public static boolean publishLiteral(String translation_id, String updated_text) {
		boolean updatedCorrectly = false;
		QualitativeMSDataDAO dataDAO = QualitativeMSDataDAO.getInstance();
		updatedCorrectly = dataDAO.publishLiteral(translation_id, updated_text);
		return updatedCorrectly;
	}
}
