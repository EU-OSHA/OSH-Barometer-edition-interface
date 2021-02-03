package eu.europa.osha.barometer.edition.database.ajax;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import eu.europa.osha.barometer.edition.webui.business.QualitativeDataBusiness;
import eu.europa.osha.barometer.edition.webui.business.UpdateLabelsBusiness;

@WebServlet
(
    name = "controllersendmultipleforms",
    description = "Servlet that receives Ajax calls",
    urlPatterns = {"/multipleforms"}
)
public class SubmitMultipleForms extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static final Logger LOGGER = LogManager.getLogger(SubmitMultipleForms.class);

	public void service(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException
    {
		LOGGER.info("App flow arrives to SubmitMultipleForms Ajax service");
		String translationId = req.getParameter("translation_id");
		String updatedText = req.getParameter("updated_text");
		String lastForm = req.getParameter("lastForm");
		String section = req.getParameter("section");
		String chart = req.getParameter("chart");
		String page = req.getParameter("page");
		
		boolean updatedLiteral = UpdateLabelsBusiness.publishLiteral(translationId, updatedText);
		
//		if(lastForm != null) {
//			ArrayList<HashMap<String,String>> sectionList = UpdateLabelsBusiness.getSectionList();
//			ArrayList<HashMap<String,String>> chartList = QualitativeDataBusiness.getChartsBySection(section);
//			ArrayList<HashMap<String,String>> literalList = UpdateLabelsBusiness.getLiteralsBySectionAndChart(section, chart);				
//			
//			req.setAttribute("sectionList", sectionList);
//			req.setAttribute("chartList", chartList);
//			req.setAttribute("literalList", literalList);
//			req.setAttribute("sectionSelected", section);
//			req.setAttribute("chartSelected", chart);
//			req.setAttribute("page", page);
//			
//			if(updatedLiteral) {
//				req.setAttribute("confirmationMessage", "Literals updated correctly");
//			} else {
//				req.setAttribute("errorMessage", "An error has occurred while updating the literals");
//			}
//		}
    }
}
