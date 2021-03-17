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

@WebServlet
(
    name = "controllerindicatorload",
    description = "Servlet that receives Ajax calls",
    urlPatterns = {"/indicatorload"}
)
public class IndicatorLoad extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static final Logger LOGGER = LogManager.getLogger(IndicatorLoad.class);
	
	public void service(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException
    {
		LOGGER.info("App flow arrives to IndicatorLoad Ajax service");
		Gson g = new Gson();
        String returningData = "";
        String section = req.getParameter("section");
        ArrayList<HashMap<String,String>> indicatorList = null;

        indicatorList = MethodologyBusiness.getIndicatorsBySection(section);
        LOGGER.info("indicatorList length: "+indicatorList.size());
        
        req.setAttribute("sectionSelected", section);
        
        returningData = g.toJson(indicatorList);        
		res.setContentType("text/plain");
        res.setCharacterEncoding("UTF-8");
        LOGGER.info("Converting Arraylist to Json");
        res.getWriter().write(returningData);
        LOGGER.info("Wrote Json in http response");
        res.getWriter().flush();
    }
}
