package eu.europa.osha.barometer.edition.database.ajax;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ResourceBundle;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

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
	private static ResourceBundle configurationData = ResourceBundle.getBundle("eu.europa.osha.barometer.edition.webui.conf.configuration");

	public void service(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException
    {
		LOGGER.info("App flow arrives to SubmitMultipleForms Ajax service");
		String translationId = req.getParameter("translation_id");
		String updatedText = req.getParameter("updated_text");
        String section = req.getParameter("section");
        String chart = req.getParameter("chart");
        
        String lastForm = req.getParameter("lastForm");
        
        HttpSession session = req.getSession();
        session.setAttribute("section", section);
        session.setAttribute("chart", chart);
		
		boolean updatedLiteral = UpdateLabelsBusiness.publishLiteral(translationId, updatedText);
		LOGGER.info("Literal with id: "+translationId+" updated in database: "+updatedLiteral);
		
		if(lastForm != null) {
			LOGGER.info("Calling ETL to update published literals.");
			String scriptDirectory = configurationData.getString("directory.etl")+configurationData.getString("directory.script");
			String command = "sh "+scriptDirectory+"literals.sh";
			LOGGER.info("LINUX: command to execute: "+command);
			Process p = Runtime.getRuntime().exec(command);
			
			try {
				FileInputStream instream = null;
				FileOutputStream outstream = null;
				LOGGER.info("Waiting for script to end...");
				p.waitFor();
				LOGGER.info("Script process ended.");
				String jsonDirectory = configurationData.getString("directory.etl")+configurationData.getString("directory.etl.literals.output");
				String literalsDirectory = configurationData.getString("directory.barometer")+configurationData.getString("directory.model.files");
				File linksFileInput = new File(jsonDirectory+"Links-List.txt");
				File linksFileOutput = new File(literalsDirectory+"Links-List.txt");
				File draftLiteralsFileInput = new File(jsonDirectory+"Draft_Literals.json");
				File draftLiteralsFileOutput = new File(literalsDirectory+"Literals.json");
//				File publishedLiteralsFileInput = new File(jsonDirectory+"Published_Literals.json");
//				File publishedLiteralsFileOutput = new File(literalsDirectory+"Published_Literals.json");
				try {
					//Links
					instream = new FileInputStream(linksFileInput);
					outstream = new FileOutputStream(linksFileOutput);
		    	    byte[] buffer = new byte[1024];
		    	    
		    	    int length;
		    	    while ((length = instream.read(buffer)) > 0){
		    	    	outstream.write(buffer, 0, length);
		    	    }
		    	    instream.close();
		    	    outstream.close();
		    	    
		    	  //Draft literals
					instream = new FileInputStream(draftLiteralsFileInput);
					outstream = new FileOutputStream(draftLiteralsFileOutput);
		    	    buffer = new byte[1024];
		    	    
		    	    while ((length = instream.read(buffer)) > 0){
		    	    	outstream.write(buffer, 0, length);
		    	    }
		    	    instream.close();
		    	    outstream.close();
		    	    
//					instream = new FileInputStream(publishedLiteralsFileInput);
//					outstream = new FileOutputStream(publishedLiteralsFileOutput);
//		    	    buffer = new byte[1024];
//		    	    //Published literals
//		    	    while ((length = instream.read(buffer)) > 0){
//		    	    	outstream.write(buffer, 0, length);
//		    	    }
//		    	    instream.close();
//		    	    outstream.close();
				} catch (Exception e) {
					LOGGER.info("An error has occurred while creating literals file.");
				    e.printStackTrace();
				}
			} catch (Exception e) {
				LOGGER.info("An error has occurred while creating literals file.");
				e.printStackTrace();
			}
		}
    }
}
