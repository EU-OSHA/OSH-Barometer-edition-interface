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
import javax.servlet.http.HttpSession;

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
        String section = req.getParameter("section");
        String chart = req.getParameter("chart");
        
        HttpSession session = req.getSession();
        session.setAttribute("section", section);
        session.setAttribute("chart", chart);
		
		boolean updatedLiteral = UpdateLabelsBusiness.publishLiteral(translationId, updatedText);
		LOGGER.info("Literal with id: "+translationId+" updated in database: "+updatedLiteral);
    }
}
