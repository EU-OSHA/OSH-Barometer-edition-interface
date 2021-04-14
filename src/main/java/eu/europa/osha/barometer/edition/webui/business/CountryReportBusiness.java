package eu.europa.osha.barometer.edition.webui.business;

import java.util.ArrayList;
import java.util.HashMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import eu.europa.osha.barometer.edition.database.model.CountryReportDAO;

public class CountryReportBusiness {
	private static final Logger LOGGER = LogManager.getLogger(CountryReportBusiness.class);
	
	/**
	 * Calls DAO to get OSH Authorities countries for Country Report for MS page
	 * @return ArrayList<HashMap<String,String>> list of the osh authorities countries
	 */
	public static ArrayList<HashMap<String,String>> getOshAuthoritiesCountries(){
		LOGGER.info("Accessing data DAO in order to get available countries for OSH Authorities");
		CountryReportDAO dataDAO = CountryReportDAO.getInstance();
		ArrayList<HashMap<String,String>> dataList = dataDAO.getOshAuthoritiesCountries();
		LOGGER.info("Retrieved data from database. Data list length: "+dataList.size());
		return dataList;
	}
	
	/**
	 * Calls DAO to get the National Strategies countries for Country Report for MS page
	 * @return ArrayList<HashMap<String,String>> list of national strategies countries
	 */
	public static ArrayList<HashMap<String,String>> getNationalStrategiesCountries(){
		LOGGER.info("Accessing data DAO in order to get available countries for National Strategies");
		CountryReportDAO dataDAO = CountryReportDAO.getInstance();
		ArrayList<HashMap<String,String>> dataList = dataDAO.getNationalStrategiesCountries();
		LOGGER.info("Retrieved data from database. Data list length: "+dataList.size());
		return dataList;
	}
	
	/**
	 * Calls DAO to get all Social Dialogue countries for Country Report for MS page
	 * @return ArrayList<HashMap<String,String>> list of social dialogue countries
	 */
	public static ArrayList<HashMap<String,String>> getSocialDialogueCountries(){
		LOGGER.info("Accessing data DAO in order to get available countries for Social Dialogue");
		CountryReportDAO dataDAO = CountryReportDAO.getInstance();
		ArrayList<HashMap<String,String>> dataList = dataDAO.getSocialDialogueCountries();
		LOGGER.info("Retrieved data from database. Data list length: "+dataList.size());
		return dataList;
	}
}
