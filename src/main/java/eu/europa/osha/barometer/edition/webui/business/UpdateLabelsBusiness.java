package eu.europa.osha.barometer.edition.webui.business;

import java.util.ArrayList;
import java.util.HashMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import eu.europa.osha.barometer.edition.database.model.UpdateLabelsDAO;
import eu.europa.osha.barometer.edition.webui.utils.EscapeHtmlTags;

public class UpdateLabelsBusiness {
	private static final Logger LOGGER = LogManager.getLogger(UpdateLabelsBusiness.class);

	/**
	 * Function that calls DAO to get all sections for Update Labels page
	 * @return ArrayList<HashMap<String,String>> list of sections
	 */
	public static ArrayList<HashMap<String,String>> getSectionList(){
		LOGGER.info("Accessing data DAO in order to get available sections for update labels");
		UpdateLabelsDAO dataDAO = UpdateLabelsDAO.getInstance();
		ArrayList<HashMap<String,String>> dataList = dataDAO.getSectionList();
		LOGGER.info("Retrieved data from database. Data list length: "+dataList.size());
		return dataList;
	}
	
	/**
	 * Function that calls DAO to get all charts related to a specific section
	 * @param section String ID of the selected section
	 * @return ArrayList<HashMap<String,String>> list of charts related to a section
	 */
	public static ArrayList<HashMap<String,String>> getChartsBySectionUpdateLabels(String section){
		LOGGER.info("Accessing data DAO in order to get available sections for update labels");
		UpdateLabelsDAO dataDAO = UpdateLabelsDAO.getInstance();
		ArrayList<HashMap<String,String>> dataList = dataDAO.getChartsBySectionUpdateLabels(section);
		LOGGER.info("Retrieved data from database. Data list length: "+dataList.size());
		return dataList;
	}
	
	/**
	 * Function that calls DAO to get all literals related to a section and a chart or just to a section
	 * @param section_id String ID of the selected section
	 * @param chart_id String ID of the selected chart
	 * @return ArrayList<HashMap<String,String>> list of literals related to a specific section with or without
	 * a selected chart
	 */
	public static ArrayList<HashMap<String,String>> getLiteralsBySectionAndChart(String section_id, String chart_id) {
		LOGGER.info("Accessing data DAO in order to get available literals of the sections and charts");
		UpdateLabelsDAO dataDAO = UpdateLabelsDAO.getInstance();
		ArrayList<HashMap<String,String>> dataList = dataDAO.getLiteralsBySectionAndChart(section_id, chart_id);
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
	
	/**
	 * Function that calls DAO to add draft text to a literal
	 * @param updated_text String new text for draft_text field in DB
	 * @param translation_id String ID of the literal
	 * @return boolean true if the literal has been updated in DB, false in case it failed
	 */
	public static boolean updateDraftText(String updated_text, String translation_id) {
		boolean updatedCorrectly = false;
		UpdateLabelsDAO dataDAO = UpdateLabelsDAO.getInstance();
		updatedCorrectly = dataDAO.updateDraftText(updated_text, translation_id);
		return updatedCorrectly;
	}
	
	/**
	 * Function that calls DAO to delete the draft text of a literal
	 * @param translation_id String ID of the literal
	 * @return boolean true if the operation has been undone in DB, false in case it failed
	 */
	public static boolean undoDraftText(String translation_id) {
		boolean updatedCorrectly = false;
		UpdateLabelsDAO dataDAO = UpdateLabelsDAO.getInstance();
		updatedCorrectly = dataDAO.undoDraftText(translation_id);
		return updatedCorrectly;
	}
	
	/**
	 * Function that calls DAO to update a literals published text and deletes the draft text
	 * @param translation_id String ID of the literal
	 * @param updated_text String text that will replace the published text in DB
	 * @return boolean boolean true if the literal has been updated in DB, false in case it failed
	 */
	public static boolean publishLiteral(String translation_id, String updated_text) {
		boolean updatedCorrectly = false;
		UpdateLabelsDAO dataDAO = UpdateLabelsDAO.getInstance();
		updatedCorrectly = dataDAO.publishLiteral(translation_id, updated_text);
		return updatedCorrectly;
	}
}
