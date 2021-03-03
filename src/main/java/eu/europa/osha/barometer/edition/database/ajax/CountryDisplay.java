package eu.europa.osha.barometer.edition.database.ajax;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.gson.Gson;

import eu.europa.osha.barometer.edition.webui.business.CountryReportBusiness;

@WebServlet
(
    name = "controllercountrydisplay",
    description = "Servlet that receives Ajax calls",
    urlPatterns = {"/countrydisplay"}
)
public class CountryDisplay extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static final Logger LOGGER = LogManager.getLogger(CountryDisplay.class);
	
	/**
	 * Service for ajax. This service is called to retrieve in the http response
	 * the country list for Country reports for MS depending on the selected section
	 */
	public void service(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException
    {
		LOGGER.info("App flow arrives to CountryDisplay Ajax service");
		Gson g = new Gson();
        String returningData = "";
        String section = req.getParameter("section");
        ArrayList<HashMap<String,String>> countryList = null;
        if(section.equals("osh_authorities")) {
        	countryList = CountryReportBusiness.getOshAuthoritiesCountries();
        	LOGGER.info("countryList OSH Authorities length: "+countryList.size());
        } else if(section.equals("national_strategies")) {
        	countryList = CountryReportBusiness.getNationalStrategiesCountries();
        	LOGGER.info("countryList National Strategies length: "+countryList.size());
        } else if(section.equals("social_dialogue")) {
        	countryList = CountryReportBusiness.getSocialDialogueCountries();
        	LOGGER.info("countryList Social Dialogue length: "+countryList.size());
        }
        
        returningData = g.toJson(countryList);        
		res.setContentType("text/plain");
        res.setCharacterEncoding("UTF-8");
        LOGGER.info("Converting Arraylist to Json");
        res.getWriter().write(returningData);
        LOGGER.info("Wrote Json in http response");
        res.getWriter().flush();
    }
}
