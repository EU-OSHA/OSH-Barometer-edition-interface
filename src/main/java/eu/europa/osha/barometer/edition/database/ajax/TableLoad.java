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

import eu.europa.osha.barometer.edition.webui.business.MethodologyBusiness;
import eu.europa.osha.barometer.edition.webui.business.QualitativeDataBusiness;
import eu.europa.osha.barometer.edition.webui.business.QualitativeMSDataBusiness;
import eu.europa.osha.barometer.edition.webui.business.UpdateLabelsBusiness;

@WebServlet
(
    name = "controllertableload",
    description = "Servlet that receives Ajax calls",
    urlPatterns = {"/tableload"}
)
public class TableLoad extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static final Logger LOGGER = LogManager.getLogger(TableLoad.class);
 
	/**
	 * Ajax service. This service is called for literal update pages retrieving new
	 * literals with their data when the parameters are changed
	 */
	public void service(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException
    {
		LOGGER.info("App flow arrives to TableLoad Ajax service");
		Gson g = new Gson();
        String returningData = "";
		String section = req.getParameter("section");
		String chart = req.getParameter("chart");
		
		String country = req.getParameter("country");
		String institution = req.getParameter("institution");
		
		String indicator = req.getParameter("indicator");
		
		String get = req.getParameter("get");
		
		if(get.equals("charts")) {
			ArrayList<HashMap<String,String>> chartsBySectionList = QualitativeDataBusiness.getChartsBySection(section);
			LOGGER.info("chartsBySectionList length: "+chartsBySectionList.size());
	        returningData = g.toJson(chartsBySectionList);
		} else if(get.equals("indicators")) {
			ArrayList<HashMap<String,String>> indicatorsByChartList = QualitativeDataBusiness.getIndicatorsByChart(chart);
			LOGGER.info("indicatorsByChartList length: "+indicatorsByChartList.size());
	        returningData = g.toJson(indicatorsByChartList);
		} else if(get.equals("datasets")) {
			String indicatorId = req.getParameter("indicator");
			ArrayList<HashMap<String,String>> datasetsList = QualitativeDataBusiness.getDatasetsForIndicator(indicatorId);
			LOGGER.info("datasetsList length: "+datasetsList.size());
			returningData = g.toJson(datasetsList);
		} else if(get.equals("literals")) {
			ArrayList<HashMap<String,String>> literalsList = UpdateLabelsBusiness.getLiteralsBySectionAndChart(section, chart);
			LOGGER.info("literalsList length: "+literalsList.size());
			returningData = g.toJson(literalsList);
		} else if(get.equals("qualitativeMS")) {
			ArrayList<HashMap<String,String>> literalsList = null;
			if(section.contains("MATRIX")) {
				literalsList = QualitativeMSDataBusiness.getMatrixPageDataByCountryAndInstitution(section, country, institution);
				LOGGER.info("literalsList for Matrix length: "+literalsList.size());
			}else{
				literalsList = QualitativeMSDataBusiness.getStrategiesPageDataByCountryAndInstitution(section, country);
				LOGGER.info("literalsList for Strategies length: "+literalsList.size());
			}
			returningData = g.toJson(literalsList);
		} else if(get.equals("methodology")) {
			ArrayList<HashMap<String,String>> literalsList = MethodologyBusiness.getLiteralsMethodology(section, indicator);
			LOGGER.info("literalsList for Methodology length: "+literalsList.size());
			returningData = g.toJson(literalsList);
		}
		
		res.setContentType("text/plain");
        res.setCharacterEncoding("UTF-8");
        LOGGER.info("Converting Arraylist to Json");
        res.getWriter().write(returningData);
        LOGGER.info("Wrote Json in http response");
        res.getWriter().flush();
    }
}
