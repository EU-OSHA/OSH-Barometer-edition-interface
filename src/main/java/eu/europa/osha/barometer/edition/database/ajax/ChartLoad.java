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

import eu.europa.osha.barometer.edition.webui.business.UpdateLabelsBusiness;

@WebServlet
(
    name = "controllerchartload",
    description = "Servlet that receives Ajax calls",
    urlPatterns = {"/chartload"}
)
public class ChartLoad extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static final Logger LOGGER = LogManager.getLogger(ChartLoad.class);
	
	/**
	 * Service for ajax. This service will retrieve in the servlet response a list of
	 * charts for Update Labels page
     * @param req HttpServletRequest request 
     * @param res HttpServletResponse response
	 */
	public void service(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException
    {
		LOGGER.info("App flow arrives to CountryDisplay Ajax service");
		Gson g = new Gson();
        String returningData = "";
        String section = req.getParameter("section");
        ArrayList<HashMap<String,String>> chartList = null;

        //chartList = QualitativeDataBusiness.getChartsBySection(section);
        chartList = UpdateLabelsBusiness.getChartsBySectionUpdateLabels(section);
        LOGGER.info("chartList length: "+chartList.size());
        
        req.setAttribute("sectionSelected", section);
        
        returningData = g.toJson(chartList);        
		res.setContentType("text/plain");
        res.setCharacterEncoding("UTF-8");
        LOGGER.info("Converting Arraylist to Json");
        res.getWriter().write(returningData);
        LOGGER.info("Wrote Json in http response");
        res.getWriter().flush();
    }
}
