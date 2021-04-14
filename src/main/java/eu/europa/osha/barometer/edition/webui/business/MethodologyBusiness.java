package eu.europa.osha.barometer.edition.webui.business;

import java.util.ArrayList;
import java.util.HashMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import eu.europa.osha.barometer.edition.database.model.MethodologyDAO;
import eu.europa.osha.barometer.edition.database.model.QualitativeMSDataDAO;
import eu.europa.osha.barometer.edition.webui.utils.EscapeHtmlTags;

public class MethodologyBusiness {
private static final Logger LOGGER = LogManager.getLogger(MethodologyBusiness.class);
	
	public static ArrayList<HashMap<String,String>> getSectionsMethodology() {
		LOGGER.info("Accessing data DAO in order to get countries by matrix page section.");
		MethodologyDAO dataDAO = MethodologyDAO.getInstance();
		ArrayList<HashMap<String,String>> dataList = dataDAO.getSectionsMethodology();
		LOGGER.info("Retrieved data from database. Data list length: "+dataList.size());
		return dataList;
	}
	
	public static ArrayList<HashMap<String,String>> getIndicatorsBySection(String section) {
		LOGGER.info("Accessing data DAO in order to get countries by matrix page section.");
		MethodologyDAO dataDAO = MethodologyDAO.getInstance();
		ArrayList<HashMap<String,String>> dataList = dataDAO.getIndicatorsBySection(section);
		LOGGER.info("Retrieved data from database. Data list length: "+dataList.size());
		return dataList;
	}
	
	public static ArrayList<HashMap<String,String>> getLiteralsMethodology(String section, String indicator) {
		LOGGER.info("Accessing data DAO in order to get countries by matrix page section.");
		MethodologyDAO dataDAO = MethodologyDAO.getInstance();
		String update_text = null;
		String published_text = null;
		ArrayList<HashMap<String,String>> dataList = dataDAO.getLiteralsMethodology(section, indicator);
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
		MethodologyDAO dataDAO = MethodologyDAO.getInstance();
		updatedCorrectly = dataDAO.updateDraftText(updated_text, translation_id);
		return updatedCorrectly;
	}
	
	public static boolean undoDraftText(String translation_id) {
		boolean updatedCorrectly = false;
		MethodologyDAO dataDAO = MethodologyDAO.getInstance();
		updatedCorrectly = dataDAO.undoDraftText(translation_id);
		return updatedCorrectly;
	}
	
	public static boolean publishLiteral(String translation_id, String updated_text) {
		boolean updatedCorrectly = false;
		MethodologyDAO dataDAO = MethodologyDAO.getInstance();
		updatedCorrectly = dataDAO.publishLiteral(translation_id, updated_text);
		return updatedCorrectly;
	}
}
