package eu.europa.osha.barometer.edition.webui.controller;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.ResourceBundle;

import javax.security.auth.Subject;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.login.LoginContext;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.http.Part;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.sun.security.auth.UserPrincipal;

import eu.europa.osha.barometer.edition.webui.bean.User;
import eu.europa.osha.barometer.edition.webui.business.QuantitativeDataBusiness;
import eu.europa.osha.barometer.edition.webui.security.ConfigurationImpl;
import eu.europa.osha.barometer.edition.webui.security.PassiveCallbackHandler;

/**
 * This class is the controller of the web interface. Pages and redirections are known through the use of the request
 * "page" parameter, which will tell the servlet where to go. For each new page there will be an "else if" in the
 * service method. Please remember to use request parameters and attributes to keep the necessary values between pages.
 */
@WebServlet
(
	name = "controller",
	description = "MVC controller servlet",
	urlPatterns = {"/uicontroller"}
)
@MultipartConfig
public class BarometerUIController extends HttpServlet{

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;

	/** The Constant LOGGER. */
	private static final Logger LOGGER = LogManager.getLogger(BarometerUIController.class);
	
	private static ResourceBundle configurationData = ResourceBundle.getBundle("eu.europa.osha.barometer.edition.webui.conf.configuration");

	private static final String USERNAME = "admin";
	private static final String PASSWORD = "admin";

	/**
	 * The main method of the controller in charge of the redirections. Use a "service" method, so it can handle both
	 * get and post.
	 *
	 * @param req, HttpServletRequest containing all the data sent by the browser.
	 * @param res, HttpServletResponse containing all the data to be sent to the browser.
	 * @throws ServletException, ServletException thrown when there is a communication problem.
	 * @throws IOException, IOException thrown when there is a SERIOUS communication problem.
	 */
	public void service(HttpServletRequest req, HttpServletResponse res)
			throws ServletException, IOException {
		//The context of the servlet (a servlet context is basically its environment: its main route, the environment
		// variables, status of the system it runs on, etc).
		ServletContext application = getServletContext();
		//This will hold the path to the JSP we are going to.
		String nextURL = "";
		//In this block of if/elses, the application will get the "page" parameter. According to its value, the
		// controller will send the user to the correct page (please remember to keep necessary attributes and
		// parameters, the application will not do it on its own) or to an error page, if something is wrong.
		//Getting the parameter that tell us the page.
		String page = req.getParameter("page");
		//Getting the current session
		HttpSession session = req.getSession();

		LOGGER.info("Current page: " + page);

		//According to the parameter, the controller will redirect to the proper page, which can be:
		if(page != null && page.length() > 0) {
			if (page.equals("login")) {
				LOGGER.info("Accessing login page");
				String logout = req.getParameter("logout");
				if (logout != null) {
					LOGGER.info("Logging out from OSH Barometer Edition Tool.");
					//TODO remove current session

					/* TEMPORAL LOGOUT */
					session.removeAttribute("user");
				} else {
					if(session.getAttribute("user") != null) {
						res.sendRedirect(req.getContextPath() + "/uicontroller?page=home");
					}
				}
				nextURL = "/jsp/login.jsp";
			}else if(page.equals("home")) {
				LOGGER.info("Accessing home page");
				String errorMessage = null;
				boolean loginCorrect = false;
				String username = req.getParameter("username");
				String password = req.getParameter("password");
				
				if(session.getAttribute("user") != null) {
					loginCorrect = true;
				} else {
					/* TEMPORAL LOGIN */
					if(username != null) {
						if(password != null) {
							if(username.equals(USERNAME) && password.equals(PASSWORD)) {
								LOGGER.info("Username and password correct.");
								loginCorrect = true;
								User user = new User(username, password);
								session.setAttribute("user", user);
							} else {
								LOGGER.error("Login failed. Username or password not correct.");
							}
						} else {
							LOGGER.error("Login failed. Password is empty.");
						}
					}
				}

				if(loginCorrect) {
					LOGGER.info("Login correct. Accessing to OSH Barometer homepage.");
					nextURL = "/jsp/home.jsp";
				}else {
					LOGGER.error("Login failed. Returning to login page.");
					errorMessage = "Sorry, unrecognized username or password.";
					nextURL = "/jsp/login.jsp";
				}

				//TODO Connect to LDAP to check if user/mail and password exist
				//				CallbackHandler callbackHandler = new PassiveCallbackHandler(username, password);
				//				Subject subject = null;
				//
				//				String user = null;
				//				try {
				//					LoginContext lc = new LoginContext(ConfigurationImpl.LDAP_CONFIGURATION_NAME, 
				//							subject, callbackHandler, new ConfigurationImpl());
				//					lc.login();
				//
				//					subject = lc.getSubject();
				//
				//					for(Iterator it = subject.getPrincipals(UserPrincipal.class).iterator(); it.hasNext();) {
				//						UserPrincipal userPrincipal = (UserPrincipal) it.next();
				//						LOGGER.info("Authenticated: "+userPrincipal.getName());
				//						username = userPrincipal.getName();
				//					}
				//
				//				}catch(Exception e) {
				//					LOGGER.error("ERROR WHILE AUTHENTICATING");
				//					e.printStackTrace();
				//				}

				//				boolean loginCorrect = true;
				//				if(loginCorrect) {
				//					LOGGER.info("Login correct. Redirecting to OSH Barometer homepage.");
				//					//TODO create user session
				//					nextURL = "/jsp/home.jsp";
				//				}else {
				//					LOGGER.error("Login failed. Returning to login page.");
				//					nextURL = "/jsp/login.jsp";
				//					errorMessage = "Incorrect user/mail or password. Please try again.";
				//				}

				if(errorMessage != null) {
					req.setAttribute("errorMessage", errorMessage);
				}
			} else if (page.equals("quantitative_eurofound")) {
				nextURL = "/jsp/quantitative_eurofound.jsp";
				LOGGER.info("Arriving to Quantitative Data from Eurofound.");
				String submit = req.getParameter("formSent");
				String errorMessage = null;
				String confirmationMessage = null;
				String scriptDirectory = null;
				String outputDirectory = null;
				
				if(submit != null) {
					String year = req.getParameter("year");
					LOGGER.info("Year: "+year);
					Part file = req.getPart("quantitativeEurofoundFile");
					if(file != null) {
						String fileName = Paths.get(file.getSubmittedFileName()).getFileName().toString();
						String fileNameWOExtension = fileName.substring(0, fileName.indexOf('.'));
						
						LOGGER.info("File name: "+fileName);
						InputStream fileContent = file.getInputStream();
						//TODO copy file to a determined location and call script to launch ETL
						String profile = configurationData.getString("profile.name");
						
						OutputStream out = null;
					    try {
					    	outputDirectory = configurationData.getString("directory.quantitative_file.eurofound.output");
					    	scriptDirectory = configurationData.getString("directory.script");
					        out = new FileOutputStream(new File(outputDirectory + fileName));

					        int read = 0;
					        final byte[] bytes = new byte[1024];

					        while ((read = fileContent.read(bytes)) != -1) {
					            out.write(bytes, 0, read);
					        }
					        LOGGER.info("File "+fileName+" being uploaded to " + outputDirectory);
					        
					        if(profile.equals("localhost")) {
								//TODO call .bat script to run ETL
								Runtime.getRuntime().exec("cmd /c start \"\" "+scriptDirectory+"eurofound_quantitative_script.bat "+fileNameWOExtension+" "+year);
							} else {
								//TODO call .sh script to run ETL
							}
					        confirmationMessage = "The data has been correctly saved.";
					    } catch (FileNotFoundException fne) {
					        LOGGER.error("Problems during file upload. Error: "+fne.getMessage());
					        fne.printStackTrace();
					        errorMessage = "An error has occurred while processing excel file.";
					    } finally {
					        if (out != null) {
					            out.close();
					        }
					        if (fileContent != null) {
					        	fileContent.close();
					        }
					    }
					}
					
					if(errorMessage != null) {
						req.setAttribute("errorMessage", errorMessage);
					}
					
					if(confirmationMessage != null) {
						req.setAttribute("confirmationMessage", confirmationMessage);
					}
				}
			} else if (page.equals("quantitative_eurostat")) {
				nextURL = "/jsp/quantitative_eurostat.jsp";
				ArrayList<HashMap<String,String>> indicatorsList = QuantitativeDataBusiness.getIndicatorsForEurostat();
				//TODO functionality for page quantitative_eurostat
				String errorMessage = null;
				String confirmationMessage = null;
				String scriptDirectory = null;
				String outputDirectory = null;
				String submit = req.getParameter("formSent");
				if(submit != null) {
					String indicatorEurostat = req.getParameter("indicatorEurostat");
					String yearFrom = req.getParameter("yearFrom");
					String yearTo = req.getParameter("yearTo");
					Part file = req.getPart("quantitativeEurostatFile");
					String fileName = Paths.get(file.getSubmittedFileName()).getFileName().toString();
					String fileNameWOExtension = fileName.substring(0, fileName.indexOf('.'));
					
					//TODO process year types that can be received (from and to or unique year)
					
					LOGGER.info("File name: "+fileName);
					InputStream fileContent = file.getInputStream();
					//TODO copy file to a determined location and call script to launch ETL
					String profile = configurationData.getString("profile.name");
					
					OutputStream out = null;
					
					try {
						outputDirectory = configurationData.getString("directory.quantitative_file.eurostat.output");
				    	scriptDirectory = configurationData.getString("directory.script");
				        out = new FileOutputStream(new File(outputDirectory + fileName));

				        int read = 0;
				        final byte[] bytes = new byte[1024];

				        while ((read = fileContent.read(bytes)) != -1) {
				            out.write(bytes, 0, read);
				        }
				        LOGGER.info("File "+fileName+" being uploaded to " + outputDirectory);
				        
				        if(profile.equals("localhost")) {
							//TODO call .bat script to run ETL
							Runtime.getRuntime().exec("cmd /c start \"\" "+scriptDirectory+"eurostat_quantitative_script.bat "+fileNameWOExtension+" ");
						} else {
							//TODO call .sh script to run ETL
						}
				        confirmationMessage = "The data has been correctly saved.";
					} catch(Exception e) {
						LOGGER.error("An error has occurred while processing file uploaded.");
						errorMessage = "An error has occurred while processing excel file.";
						e.printStackTrace();
					} finally {
				        if (out != null) {
				            out.close();
				        }
				        if (fileContent != null) {
				        	fileContent.close();
				        }
					}
				}
				req.setAttribute("indicatorsList", indicatorsList);
				if(errorMessage != null) {
					req.setAttribute("errorMessage", errorMessage);
				}
				
				if(confirmationMessage != null) {
					req.setAttribute("confirmationMessage", confirmationMessage);
				}
			} else if (page.equals("update_labels")) {
				nextURL = "/jsp/update_labels.jsp";
				//TODO functionality for page update_labels
			}
			//If there is no proper "page" attribute, just error.
		} else {
			LOGGER.error("No proper 'page' attribute");
			nextURL = "/jsp/login.jsp";
		}

		req.setAttribute("page", page);

		//Once the page is known, send the user to it.
		RequestDispatcher dispatcher = getServletContext().getRequestDispatcher(nextURL);
		dispatcher.forward(req, res);
	}
	
	private void processUploadedExcels(Part file, String fileName) {
		//TODO after the process is finished for both cases optimise code in here
	}

	/**
	 * This method destroys the servlet. It can be empty. ALWAYS destroy everything (in the end, at least).
	 */
	@Override
	public void destroy() {}
}
