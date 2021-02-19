package eu.europa.osha.barometer.edition.webui.controller;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
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

import org.apache.commons.lang3.SystemUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.sun.security.auth.UserPrincipal;

import eu.europa.osha.barometer.edition.webui.bean.User;
import eu.europa.osha.barometer.edition.webui.business.CountryReportBusiness;
import eu.europa.osha.barometer.edition.webui.business.QualitativeDataBusiness;
import eu.europa.osha.barometer.edition.webui.business.QuantitativeDataBusiness;
import eu.europa.osha.barometer.edition.webui.business.UpdateLabelsBusiness;
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
	urlPatterns = {"/user"}
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
	
	private static String UPDATE_DATASETS_DEFAULT_SECTION_ID = "17";
	private static String COUNTRY_REPORTS_DEFAULT_SECTION_ID = "osh_authorities";
	private static String COUNTRY_REPORTS_DEFAULT_COUNTRY = "Austria";
	private static String FILE_EXTENSION_QUANTITATIVE_DATA = ".xlsx";
	
	private static String COMPANY_SIZE_TEMPLATE = "EU-OSHA_OIE_Eurostat_indicator_Company_size";
	private static String EMPLOYMENT_PER_SECTOR_TEMPLATE = "EU-OSHA_OIE_Eurostat_indicator_Employment_per_sector";
	private static String EMPLOYMENT_RATE_TEMPLATE = "EU-OSHA_OIE_Eurostat_indicator_Employment_rate_T_M_F";
	private static String INCOME_PER_CAPITA_TEMPLATE = "EU-OSHA_OIE_Eurostat_Income_per_capita";
	private static String INCOME_PER_CAPITA_EURO_TEMPLATE = "EU-OSHA_OIE_Eurostat_Income_per_capita_EURO";
	private static String NON_FATAL_WORK_ACCIDENTS_TEMPLATE = "EU-OSHA_OIE_Eurostat_NonFatal_Work_accidents";
	private static String FATAL_WORK_ACCIDENTS_TEMPLATE = "EU-OSHA_OIE_Eurostat_Fatal_Work_accidents";
	private static String GENERAL_TEMPLATE = "EU-OSHA_OIE_Eurostat_Direct_value_indicators";
	
	private static String QUANTITATIVE_EUROSTAT_DEFAULT_YEAR_FROM = "2010";
	
	private static String DEFAULT_SECTION_UPDATE_LABELS = "37";
	private static String DEFAULT_CHART_UPDATE_LABELS = "0";
	
	private static String FILE_EXTENSION_COUNTRY_REPORT = ".pdf";

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
		
		String confirmationMessage = null;
		String errorMessage = null;

		LOGGER.info("Current page: " + page);

		//According to the parameter, the controller will redirect to the proper page, which can be:
		if(page != null && page.length() > 0) {
			if (page.equals("login")) {
				LOGGER.info("Accessing login page");
				String logout = req.getParameter("logout");
				if (logout != null) {
					LOGGER.info("Logging out from OSH Barometer Edition Tool.");
					//LDAP LOGOUT
					try {
						User user = (User) session.getAttribute("user");
						CallbackHandler callbackHandler = new PassiveCallbackHandler(user.getUsername(), user.getPassword());
						Subject subject = null;
					
						LoginContext lc = new LoginContext(ConfigurationImpl.LDAP_CONFIGURATION_NAME, 
								subject, callbackHandler, new ConfigurationImpl());
						lc.logout();
					} catch(Exception e) {
						LOGGER.error("AN ERROR HAS OCCURRED WHILE LOGGING OUT.");
						e.printStackTrace();
					} finally {
						nextURL = "/jsp/login.jsp";
					}

					/* TEMPORAL LOGOUT */
					session.removeAttribute("user");
				} else {
					if(session.getAttribute("user") != null) {
						res.sendRedirect(req.getContextPath() + "/user?page=home");
					}
				}
				nextURL = "/jsp/login.jsp";
			}else if(page.equals("home")) {
				LOGGER.info("Accessing home page");
				errorMessage = null;
				boolean loginCorrect = false;
				String username = req.getParameter("username");
				String password = req.getParameter("password");
				
				String temporalLogin = req.getParameter("temporalLogin");
				
				if(session.getAttribute("user") != null) {
					loginCorrect = true;
				} else {
					/* TEMPORAL LOGIN */
					if(temporalLogin != null) {
						LOGGER.info("Username and password correct.");
						loginCorrect = true;
						User userTemporal = new User(username, password);
						session.setAttribute("user", userTemporal);
					} else {
						//LDAP LOGIN
						//Connect to LDAP to check if user/mail and password exist
						try {
							CallbackHandler callbackHandler = new PassiveCallbackHandler(username, password);
							Subject subject = null;
	
							String user = null;
						
							LoginContext lc = new LoginContext(ConfigurationImpl.LDAP_CONFIGURATION_NAME, 
									subject, callbackHandler, new ConfigurationImpl());
							lc.login();

							subject = lc.getSubject();

							for(Iterator it = subject.getPrincipals(UserPrincipal.class).iterator(); it.hasNext();) {
								UserPrincipal userPrincipal = (UserPrincipal) it.next();
								LOGGER.info("Authenticated: "+userPrincipal.getName());
								username = userPrincipal.getName();
							}

						}catch(Exception e) {
							LOGGER.error("ERROR WHILE AUTHENTICATING");
							e.printStackTrace();
						} finally{
							nextURL = "/jsp/login.jsp";
						}
					}
					
//					if(username != null) {
//						if(password != null) {
//							if(username.equals(USERNAME) && password.equals(PASSWORD)) {
//								LOGGER.info("Username and password correct.");
//								loginCorrect = true;
//								User user = new User(username, password);
//								session.setAttribute("user", user);
//							} else {
//								LOGGER.error("Login failed. Username or password not correct.");
//							}
//						} else {
//							LOGGER.error("Login failed. Password is empty.");
//						}
//					}
				}

//				if(loginCorrect) {
//					LOGGER.info("Login correct. Accessing to OSH Barometer homepage.");
//					nextURL = "/jsp/home.jsp";
//				}else {
//					LOGGER.error("Login failed. Returning to login page.");
//					errorMessage = "Sorry, unrecognized username or password.";
//					nextURL = "/jsp/login.jsp";
//				}

				//boolean loginCorrect = true;
				if(loginCorrect) {
					LOGGER.info("Login correct. Redirecting to OSH Barometer homepage.");
					User user = new User(username, password);
					session.setAttribute("user", user);
					nextURL = "/jsp/home.jsp";
				} else {
					LOGGER.error("Login failed. Returning to login page.");
					nextURL = "/jsp/login.jsp";
					errorMessage = "Incorrect user/mail or password. Please try again.";
				}
				session.removeAttribute("section");
				session.removeAttribute("chart");
				sendAlertsToUser(req, confirmationMessage, errorMessage);
			} else if (page.equals("quantitative_eurofound")) {
				nextURL = "/jsp/quantitative_eurofound.jsp";
				LOGGER.info("Arriving to Quantitative Data from Eurofound.");
				String submit = req.getParameter("formSent");
				errorMessage = null;
				confirmationMessage = null;
				String scriptDirectory = null;
				String outputDirectory = null;
				String inputDirectory = null;
				String jobDirectory = null;
				String spoonLogsDirectory = null;
				String command = null;
				String logOutputDirectory = null;
				StringBuilder resultStringBuilder = null;
				
				if(submit != null) {
					String year = req.getParameter("year");
					LOGGER.info("Year: "+year);
					Part file = req.getPart("quantitativeEurofoundFile");
					if(file != null) {
						String fileName = Paths.get(file.getSubmittedFileName()).getFileName().toString();
						String eurofoundDataFileName = configurationData.getString("file.eurofound.name");
						String fileExtension = fileName.substring(fileName.indexOf('.'));
						
						if(fileExtension.equals(FILE_EXTENSION_QUANTITATIVE_DATA)) {
							LOGGER.info("File name: "+fileName);
							InputStream fileContent = file.getInputStream();						
							OutputStream out = null;
							
						    try {
						    	spoonLogsDirectory = configurationData.getString("directory.etl") + configurationData.getString("directory.etl.logs");
						    	LOGGER.info("spoonLogsDirectory: "+spoonLogsDirectory);
						    	jobDirectory = configurationData.getString("directory.etl") + configurationData.getString("directory.etl.job.eurofound");
						    	LOGGER.info("jobDirectory: "+jobDirectory);
						    	inputDirectory =  configurationData.getString("directory.etl")+configurationData.getString("directory.quantitative_file.eurofound.input");
						    	LOGGER.info("inputDirectory: "+inputDirectory);
						    	outputDirectory = jobDirectory+configurationData.getString("directory.etl.quantitative_file.eurofound.output");
						    	LOGGER.info("outputDirectory: "+outputDirectory);
						    	scriptDirectory = configurationData.getString("directory.etl")+configurationData.getString("directory.script");
						    	LOGGER.info("scriptDirectory: "+scriptDirectory);
						    	logOutputDirectory = jobDirectory+configurationData.getString("directory.quantitative_file.eurofound.log");
						    	LOGGER.info("logOutputDirectory: "+logOutputDirectory);
						        out = new FileOutputStream(new File(inputDirectory + eurofoundDataFileName + fileExtension));
						        
						        resultStringBuilder = new StringBuilder();

						        int read = 0;
						        final byte[] bytes = new byte[1024];

						        while ((read = fileContent.read(bytes)) != -1) {
						            out.write(bytes, 0, read);
						        }
						        LOGGER.info("File "+fileName+" being uploaded to " + inputDirectory);
						        
						        if(SystemUtils.IS_OS_WINDOWS) {
						        	LOGGER.info("WINDOWS: Running script: "+scriptDirectory+"eurofound_quantitative_script.bat");
						        	command = "cmd /c start \"\" "+scriptDirectory+"eurofound_quantitative_script.bat " 
											+ eurofoundDataFileName+fileExtension+" "+year+" "+inputDirectory+" "+outputDirectory
											+ " > " + scriptDirectory + "script_log_eurofound.txt 2>&1";
						        	LOGGER.info("WINDOWS: command to execute: "+command);
						        	Process p = Runtime.getRuntime().exec(command);
									LOGGER.info("Waiting for script to end...");
									p.waitFor();
									LOGGER.info("Script process ended.");
									LOGGER.info("WINDOWS: File "+fileName+" moved to directory " + outputDirectory);								
								} else {
									LOGGER.info("LINUX: Running script: "+scriptDirectory+"eurofound_quantitative_script.sh");
									command = "sh "+scriptDirectory+"eurofound_quantitative_script.sh " 
											+ eurofoundDataFileName+fileExtension+" "+year+" "+inputDirectory+" "+outputDirectory+" "
											+configurationData.getString("directory.etl.job.eurofound")+" "+configurationData.getString("directory.etl")
											+" "+spoonLogsDirectory;
									LOGGER.info("LINUX: command to execute: "+command);
									Process p = Runtime.getRuntime().exec(command);
									LOGGER.info("Waiting for script to end...");
									p.waitFor();
									LOGGER.info("Script process ended.");
							        File logFile = new File(logOutputDirectory+"log.txt");
							        LOGGER.info("Log file exists in "+logOutputDirectory+"log.txt? "+logFile.exists());
							        if(logFile.exists()) {
										InputStream inputStream = new FileInputStream(logOutputDirectory+"log.txt");
										if (inputStream != null) {
										    try (BufferedReader br
										      = new BufferedReader(new InputStreamReader(inputStream))) {
										        String line;
										        while ((line = br.readLine()) != null) {
										            resultStringBuilder.append(line).append("\n");
										        }
										        LOGGER.info("resultStringBuilder: "+resultStringBuilder);
										    }
										    logFile.delete();
										}
							        }
							        
							        File etlReadingFile = new File(outputDirectory+eurofoundDataFileName+fileExtension);
							        if(etlReadingFile.exists()) {
							        	etlReadingFile.delete();
							        	LOGGER.info("Excel file deleted after processing it");
							        }else {
							        	LOGGER.info("Excel file not found");
							        }
							        jobDirectory = configurationData.getString("directory.etl")+configurationData.getString("directory.etl.job.literals");
							        command = "sh "+scriptDirectory+"literals.sh " + jobDirectory + " " + configurationData.getString("directory.etl")
									+ " " + spoonLogsDirectory;
							        //command = "sh "+scriptDirectory+"literals.sh";
									LOGGER.info("LINUX: command to execute: "+command);
									Process literalCreator = Runtime.getRuntime().exec(command);
									LOGGER.info("Waiting for script to end...");
									literalCreator.waitFor();
									LOGGER.info("Script process ended.");
									
									try {
										copyFilesToDVT();
									} catch (Exception e) {
										LOGGER.error("ERROR WHILE MOVING FILES TO DVT. "+e.getMessage());
										e.printStackTrace();
										errorMessage = "An error has occurred while processing literals";
									}
								}
						        
						        if(resultStringBuilder.length() > 0) {
						        	LOGGER.info("resultStringBuilder has data");
						        	if(resultStringBuilder.toString().contains("SUCCESS")) {
						        		LOGGER.info("LOG FILE SAYS TEST IS SUCCESSFUL");
						        		LOGGER.info(resultStringBuilder.toString());
						        		confirmationMessage = "The data has been correctly saved.";
							        } else if (resultStringBuilder.toString().contains("ERROR")) {
							        	errorMessage = "An error has occurred while processing excel file";
							        	LOGGER.error("LOG FILE SAYS TEST HAS ERRORS");
							        	LOGGER.error(resultStringBuilder.toString());
							        }
						        } else {
						        	LOGGER.error("resultStringBuilder has NO data");
						        	errorMessage = "An error has occurred in the process.";
						        }
						    } catch (FileNotFoundException fne) {
						        LOGGER.error("Problems during file upload. Error: "+fne.getMessage());
						        fne.printStackTrace();
						        errorMessage = "An error has occurred while processing excel file";
						    } catch (Exception e) {
						    	LOGGER.error("An error has occurred. Error: "+e.getMessage());
						    	e.printStackTrace();					    
						    } finally {
						        if (out != null) {
						            out.close();
						        }
						        if (fileContent != null) {
						        	fileContent.close();
						        }
						    }
						} else {
							errorMessage = "The type of the file uploaded is not valid. The File type should be .xlsx";
						}
					}
					
					sendAlertsToUser(req, confirmationMessage, errorMessage);
					req.setAttribute("year", year);
				}
			} else if (page.equals("quantitative_eurostat")) {
				LOGGER.info("Arriving to Quantitative Data from Eurostat.");
				nextURL = "/jsp/quantitative_eurostat.jsp";
				ArrayList<HashMap<String,String>> indicatorsList = QuantitativeDataBusiness.getIndicatorsForEurostat();
				errorMessage = null;
				confirmationMessage = null;
				String yearFrom = null;
				String scriptDirectory = null;
				String outputDirectory = null;
				String inputDirectory = null;
				String jobDirectory = null;
				String spoonLogsDirectory = null;
				String logOutputDirectory = null;
				boolean validation = true;
				
				StringBuilder resultStringBuilder = null;
				
				String command = null;
				String submit = req.getParameter("formSent");
				if(submit != null) {
					String indicatorEurostat = req.getParameter("indicatorEurostat");
					
					String yearTo = null;
					String oneYear = null;
					Part file = req.getPart("quantitativeEurostatFile");
					String fileName = Paths.get(file.getSubmittedFileName()).getFileName().toString();
					String eurostatDataFileName = configurationData.getString("file.eurostat.name");
					String fileExtension = fileName.substring(fileName.indexOf('.'));
					
					if(!fileExtension.equals(FILE_EXTENSION_QUANTITATIVE_DATA)) {
						validation = false;
						errorMessage = "The type of the file uploaded is not valid. The File type should be .xlsx";
					}
					
					if(indicatorEurostat.equals("36") || indicatorEurostat.equals("279") || indicatorEurostat.equals("53")
							|| indicatorEurostat.equals("54")) {
						yearFrom = req.getParameter("yearFrom");
						yearTo = req.getParameter("yearTo");
						LOGGER.info("year from: "+yearFrom+", year to: "+yearTo);
					} else {
						oneYear = req.getParameter("oneYear");
						LOGGER.info("year from: "+oneYear+", year to: NULL");
					}
					
					if (yearFrom != null && yearTo != null) {
						try {
							Date date_from = new SimpleDateFormat("yyyy-MM-dd").parse(yearFrom);
							Date date_to = new SimpleDateFormat("yyyy-MM-dd").parse(yearTo);
							
							if(date_from.after(date_to)) {
								errorMessage = "'Year To' field should be later than 'Year From' field";
								validation = false;
							}
						} catch (ParseException e) {
							LOGGER.error("'Year To' field should be later than 'Year From' field");
							e.printStackTrace();
						}
					}
					
					//Validate that the template uploaded is coherent to the indicator selected in the form
					if(indicatorEurostat.equals("31")){
						if(!fileName.contains(COMPANY_SIZE_TEMPLATE)) {
							validation = false;
							errorMessage = "Template should be: "+COMPANY_SIZE_TEMPLATE+"_YYYY-MM-DD.xlsx";
						}
					}else if(indicatorEurostat.equals("32")){
						if(!fileName.contains(EMPLOYMENT_PER_SECTOR_TEMPLATE)) {
							validation = false;
							errorMessage = "Template should be: "+EMPLOYMENT_PER_SECTOR_TEMPLATE+"_YYYY-MM-DD.xlsx";
						}
					}else if(indicatorEurostat.equals("39")){
						if(!fileName.contains(EMPLOYMENT_RATE_TEMPLATE)) {
							validation = false;
							errorMessage = "Template should be: "+EMPLOYMENT_RATE_TEMPLATE+"_YYYY-MM-DD.xlsx";
						}
					}else if(indicatorEurostat.equals("36")){
						if(!fileName.contains(INCOME_PER_CAPITA_TEMPLATE)) {
							validation = false;
							errorMessage = "Template should be: "+INCOME_PER_CAPITA_TEMPLATE+"_YYYY-MM-DD.xlsx";
						}
					}else if(indicatorEurostat.equals("279")){
						if(!fileName.contains(INCOME_PER_CAPITA_EURO_TEMPLATE)) {
							validation = false;
							errorMessage = "Template should be: "+INCOME_PER_CAPITA_EURO_TEMPLATE+"_YYYY-MM-DD.xlsx";
						}
					}else if(indicatorEurostat.equals("53")){
						if(!fileName.contains(NON_FATAL_WORK_ACCIDENTS_TEMPLATE)) {
							validation = false;
							errorMessage = "Template should be: "+NON_FATAL_WORK_ACCIDENTS_TEMPLATE+"_YYYY-MM-DD.xlsx";
						}
					}else if(indicatorEurostat.equals("54")){
						if(!fileName.contains(FATAL_WORK_ACCIDENTS_TEMPLATE)) {
							validation = false;
							errorMessage = "Template should be: "+FATAL_WORK_ACCIDENTS_TEMPLATE+"_YYYY-MM-DD.xlsx";
						}
					}else{
						if(!fileName.contains(GENERAL_TEMPLATE)) {
							validation = false;
							errorMessage = "Template should be: "+GENERAL_TEMPLATE+"_YYYY-MM-DD.xlsx";
						}
					}
					
					if(validation) {
						LOGGER.info("File name: "+fileName);
						InputStream fileContent = file.getInputStream();						
						OutputStream out = null;
						
						try {
							jobDirectory = configurationData.getString("directory.etl")+configurationData.getString("directory.etl.job.eurostat");
							LOGGER.info("jobDirectory: "+jobDirectory);
							spoonLogsDirectory = configurationData.getString("directory.etl")+configurationData.getString("directory.etl.logs");
							LOGGER.info("spoonLogsDirectory: "+spoonLogsDirectory);
							inputDirectory = configurationData.getString("directory.etl")+configurationData.getString("directory.quantitative_file.eurostat.input");
							LOGGER.info("inputDirectory: "+inputDirectory);
							outputDirectory = jobDirectory+configurationData.getString("directory.etl.quantitative_file.eurostat.output");
							LOGGER.info("outputDirectory: "+outputDirectory);
					    	scriptDirectory = configurationData.getString("directory.etl")+configurationData.getString("directory.script");
					    	LOGGER.info("scriptDirectory: "+scriptDirectory);
					    	logOutputDirectory = jobDirectory+configurationData.getString("directory.quantitative_file.eurostat.log");
					    	LOGGER.info("logOutputDirectory: "+logOutputDirectory);
					        out = new FileOutputStream(new File(inputDirectory + eurostatDataFileName + fileExtension));
					        resultStringBuilder = new StringBuilder();

					        int read = 0;
					        final byte[] bytes = new byte[1024];

					        while ((read = fileContent.read(bytes)) != -1) {
					            out.write(bytes, 0, read);
					        }
					        LOGGER.info("File "+fileName+" being uploaded to " + inputDirectory);
					        
					        if(SystemUtils.IS_OS_WINDOWS) {
					        	if(oneYear != null) {
					        		command = "cmd /c start \"\" " + scriptDirectory + "eurostat_quantitative_script.bat "
											+ eurostatDataFileName + fileExtension + " " + indicatorEurostat + " " 
											+" "+inputDirectory+" "+outputDirectory + " " + oneYear
											+ " > " + scriptDirectory + "script_log_eurostat.txt 2>&1";
					        		Runtime.getRuntime().exec(command);
					        		LOGGER.info("WINDOWS: command to execute: "+command);
					        	}
					        	
					        	if (yearFrom != null && yearTo != null) {
					        		command = "cmd /c start \"\" "+scriptDirectory + "eurostat_quantitative_script.bat "
											+ eurostatDataFileName + fileExtension + " " + indicatorEurostat 
											+" "+inputDirectory+" "+outputDirectory+ " " + yearFrom + " " + yearTo
											+ " > " + scriptDirectory + "script_log_eurostat.txt 2>&1";
					        		Runtime.getRuntime().exec(command);
					        		LOGGER.info("WINDOWS: command to execute: "+command);
					        	}
					        	confirmationMessage = "The data has been correctly saved.";
							} else {
								Process p = null;
								if(oneYear != null) {
									LOGGER.info("ONLY YEAR FROM SELECTED");
					        		command = "sh " + scriptDirectory + "eurostat_quantitative_script.sh "
											+ eurostatDataFileName + fileExtension + " " + indicatorEurostat 
											+" "+inputDirectory+" "+outputDirectory + " " + oneYear + " NULL "
											+ configurationData.getString("directory.etl.job.eurostat")
											+ " " + configurationData.getString("directory.etl") + " " + spoonLogsDirectory;
					        		p = Runtime.getRuntime().exec(command);
					        		LOGGER.info("LINUX: command to execute: "+command);
					        	}
					        	
					        	if (yearFrom != null && yearTo != null) {
					        		LOGGER.info("YEAR FROM AND YEAR TO SELECTED");
					        		command = "sh "+scriptDirectory + "eurostat_quantitative_script.sh "
											+ eurostatDataFileName + fileExtension + " " + indicatorEurostat
											+" "+inputDirectory+" "+outputDirectory+ " " + yearFrom + " " + yearTo
											+ " " + configurationData.getString("directory.etl.job.eurostat") + " "
											+ " " + configurationData.getString("directory.etl") + " " + spoonLogsDirectory;
					        		p = Runtime.getRuntime().exec(command);
					        		LOGGER.info("LINUX: command to execute: "+command);
					        	}
					        	
					        	LOGGER.info("Waiting for script to end...");
								p.waitFor();
								LOGGER.info("Script process ended.");
						        File logFile = new File(logOutputDirectory+"log.txt");
						        LOGGER.info("Log file exists in "+logOutputDirectory+"log.txt? "+logFile.exists());
						        if(logFile.exists()) {
									InputStream inputStream = new FileInputStream(logOutputDirectory+"log.txt");
									if (inputStream != null) {
									    try (BufferedReader br
									      = new BufferedReader(new InputStreamReader(inputStream))) {
									        String line;
									        while ((line = br.readLine()) != null) {
									            resultStringBuilder.append(line).append("\n");
									        }
									        LOGGER.info("resultStringBuilder: "+resultStringBuilder);
									    }
									    
									    logFile.delete();
									}
						        }
						        
						        File etlReadingFile = new File(outputDirectory+eurostatDataFileName+fileExtension);
						        if(etlReadingFile.exists()) {
						        	etlReadingFile.delete();
						        	LOGGER.info("Excel file deleted after processing it");
						        }
							}
					        
					        LOGGER.info("Result String Builder: "+resultStringBuilder.toString());
					        
					        if(resultStringBuilder.length() > 0) {
					        	LOGGER.info("resultStringBuilder has data");
					        	if(resultStringBuilder.toString().contains("SUCCESS")) {
					        		LOGGER.info("LOG FILE SAYS TEST IS SUCCESSFUL");
					        		confirmationMessage = "The data has been correctly saved.";
						        } else if (resultStringBuilder.toString().contains("ERROR")) {
						        	errorMessage = "An error has occurred while processing excel file";
						        	LOGGER.error("LOG FILE SAYS TEST HAS ERRORS");
						        }
					        	LOGGER.info(resultStringBuilder.toString());
					        } else {
					        	LOGGER.error("resultStringBuilder has NO data");
					        	errorMessage = "An error has occurred while processing excel file";
					        }
					        
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
					req.setAttribute("yearTo", yearTo);
					req.setAttribute("oneYear", oneYear);
					req.setAttribute("indicatorEurostat", indicatorEurostat);
				}
				if(yearFrom == null) {
					yearFrom = QUANTITATIVE_EUROSTAT_DEFAULT_YEAR_FROM;
				}
				
				req.setAttribute("yearFrom", yearFrom);
				req.setAttribute("indicatorsList", indicatorsList);
				sendAlertsToUser(req, confirmationMessage, errorMessage);
			} else if (page.equals("update_labels")) {
				LOGGER.info("Arriving to Update labels Form.");
				nextURL = "/jsp/update_labels.jsp";
				String saveButton = req.getParameter("formSent");
				String section = req.getParameter("section");
				String chart = req.getParameter("chart");
				String totalRows = req.getParameter("literalListSize");
				String translation_id = null;
				String updated_text = null;
				String checked = null;
				
				confirmationMessage = null;
				errorMessage = null;
				//Save updated text in draft_text column in table translation
				if (saveButton != null) {
					translation_id = req.getParameter("translation_id"); 
					String updatedTextEditor = req.getParameter("updatedTextEditor");
					boolean textUpdated = false;
					if(saveButton.equals("saveDraft")) {
						textUpdated = UpdateLabelsBusiness.updateDraftText(updatedTextEditor, translation_id);
						
						if(!textUpdated) {
							errorMessage = "Updated text could not be saved";
						}
					}else if(saveButton.equals("undoUpdate")) {
						textUpdated = UpdateLabelsBusiness.undoDraftText(translation_id);
						
						if(!textUpdated) {
							errorMessage = "Updated text could not be deleted";
						}
					}else if(saveButton.equals("confirmUpdate")) {
						int literalListSize = 0;
						if(totalRows != null) {
							literalListSize = Integer.parseInt(totalRows);
						}
						for(int i=0; i<literalListSize; i++) {
							checked = req.getParameter("publishCheck_"+i);
							if(checked != null) {
								translation_id = req.getParameter("translation_id_"+i);
								updated_text = req.getParameter("updated_text_"+i);
								section = req.getParameter("section_"+i);
								chart = req.getParameter("chart_"+i);
								
								boolean updatedLiteral = UpdateLabelsBusiness.publishLiteral(translation_id, updated_text);
								LOGGER.info("Literal with id: "+translation_id+" updated in database: "+updatedLiteral);
							}
						}
					}
					
					try {
						String jobDirectory = configurationData.getString("directory.etl")+configurationData.getString("directory.etl.job.literals");
						LOGGER.info("jobDirectory: "+jobDirectory);
						String spoonLogsDirectory = configurationData.getString("directory.etl")+configurationData.getString("directory.etl.logs");
						LOGGER.info("spoonLogsDirectory: "+spoonLogsDirectory);
						String scriptDirectory = configurationData.getString("directory.etl")+configurationData.getString("directory.script");
						LOGGER.info("scriptDirectory: "+scriptDirectory);
						String command = "sh "+scriptDirectory+"literals.sh " + jobDirectory + " " + configurationData.getString("directory.etl")
							+ " " + spoonLogsDirectory;
						LOGGER.info("LINUX: command to execute: "+command);
						Process p = Runtime.getRuntime().exec(command);
						LOGGER.info("Waiting for script to end...");
						p.waitFor();
						LOGGER.info("Script process ended.");
						
						try {
							copyFilesToDVT();
						} catch (Exception e) {
							LOGGER.error("ERROR WHILE MOVING FILES TO DVT. "+e.getMessage());
							e.printStackTrace();
							errorMessage = "An error has occurred while processing literals";
						}
							
					} catch (Exception e) {
						LOGGER.error("An error has occurred while creating json file for literals.");
						LOGGER.error("Error: "+e.getMessage());
						e.printStackTrace();
						errorMessage = "An error has occurred while processing literals";
					}			
					
//					session.setAttribute("section",section);
//					session.setAttribute("chart",chart);
				}
				
//				section = (String) session.getAttribute("section");
//				chart = (String) session.getAttribute("chart");
				
				if(section == null) {
					section = DEFAULT_SECTION_UPDATE_LABELS;
				}
				
				if(chart == null) {
					chart = DEFAULT_CHART_UPDATE_LABELS;
				}
								
				sendAlertsToUser(req, confirmationMessage, errorMessage);
				
				ArrayList<HashMap<String,String>> sectionList = UpdateLabelsBusiness.getSectionList();
				ArrayList<HashMap<String,String>> chartList = QualitativeDataBusiness.getChartsBySection(section);
				ArrayList<HashMap<String,String>> literalList = UpdateLabelsBusiness.getLiteralsBySectionAndChart(section, chart);				
				
				req.setAttribute("sectionList", sectionList);
				req.setAttribute("chartList", chartList);
				req.setAttribute("sectionSelected", section);
				req.setAttribute("chartSelected", chart);
				req.setAttribute("literalList", literalList);
				
//				session.removeAttribute("section");
//				session.removeAttribute("chart");
			} else if (page.equals("country_reports_member_states")) {
				LOGGER.info("Arriving to Country Reports for Member States.");
				nextURL = "/jsp/country_reports_member_states.jsp";
				String submit = req.getParameter("formSent");
				String section = req.getParameter("section_id");
				String country = req.getParameter("country");
				ArrayList<HashMap<String,String>> countryList = null;
				String outputDirectory = null;
				StringBuilder filename = new StringBuilder();
				confirmationMessage = null;
				errorMessage = null;
				
				if(submit != null) {
					Part file = req.getPart("pdfFile");
					String submittedFileName = Paths.get(file.getSubmittedFileName()).getFileName().toString();
					String fileExtension = submittedFileName.substring(submittedFileName.indexOf('.'));
					if(fileExtension.equals(FILE_EXTENSION_COUNTRY_REPORT)) {
						if(section != null) {
							if(section.equals("osh_authorities")) {
								LOGGER.info("Uploading osh authorities pdf file");
								outputDirectory = configurationData.getString("directory.barometer")+configurationData.getString("directory.country_reports.osh_authorities.output");
								LOGGER.info("outputDirectory: "+outputDirectory);
								countryList = CountryReportBusiness.getOshAuthoritiesCountries();
								filename.append("OSH authorities - ");
								if(country.equals("European Union")) {
									filename.append("EU28");
								} else if(country.equals("Czechia")) {
									filename.append("Czech Republic");
								} else {
									filename.append(country);
								}
							} else if(section.equals("national_strategies")) {
								LOGGER.info("Uploading national strategies pdf file");
								outputDirectory = configurationData.getString("directory.barometer")+configurationData.getString("directory.country_reports.national_strategies.output");
								LOGGER.info("outputDirectory: "+outputDirectory);
								countryList = CountryReportBusiness.getNationalStrategiesCountries();
								filename.append("National-Strategies-Mapping_");
								if(country.equals("Germany")) {
									filename.append("2017_Germany");
								} else if(country.equals("Czechia")) {
									filename.append("Czech Republic");
								} else {
									filename.append(country);
								}
							} else if(section.equals("social_dialogue")) {
								LOGGER.info("Uploading social dialogue pdf file");
								outputDirectory = configurationData.getString("directory.barometer")+configurationData.getString("directory.country_reports.social_dialogue.output");
								LOGGER.info("outputDirectory: "+outputDirectory);
								countryList = CountryReportBusiness.getSocialDialogueCountries();
								filename.append("Social_Dialogue_");
								filename.append(country);
							}
						}
						
						LOGGER.info("Updating file: "+filename);
						
						String fileName = Paths.get(file.getSubmittedFileName()).getFileName().toString();
						InputStream fileContent = file.getInputStream();						
						OutputStream out = null;
						
						try {
							//outputDirectory = configurationData.getString("directory.country_reports.osh_authorities.output");
					        out = new FileOutputStream(new File(outputDirectory + filename.toString() + ".pdf"));

					        int read = 0;
					        final byte[] bytes = new byte[1024];

					        while ((read = fileContent.read(bytes)) != -1) {
					            out.write(bytes, 0, read);
					        }
					        LOGGER.info("File "+fileName+" being uploaded to " + outputDirectory);
					        confirmationMessage = "The Country report pdf has been correctly saved. You can see the changes in staging environment <a href=\"https://test-visualisation.osha.europa.eu/osh-barometer#!/\" target=\"_blank\">here</a>.";
						} catch(Exception e) {
							LOGGER.error("An error has occurred while uploading the pdf.");
							e.printStackTrace();
							errorMessage = "An error has occurred while uploading the pdf.";
						}
					} else { 
						errorMessage = "File extension is not .pdf";
					}
				} else {
					country = COUNTRY_REPORTS_DEFAULT_COUNTRY;
					section = COUNTRY_REPORTS_DEFAULT_SECTION_ID;
					countryList = CountryReportBusiness.getOshAuthoritiesCountries();
				}
				
				if(country == null) {
					country = COUNTRY_REPORTS_DEFAULT_COUNTRY;
				}
				
				sendAlertsToUser(req, confirmationMessage, errorMessage);
				
				if(section.equals(COUNTRY_REPORTS_DEFAULT_SECTION_ID)) {
					countryList = CountryReportBusiness.getOshAuthoritiesCountries();
				} else if(section.equals("national_strategies")) {
					countryList = CountryReportBusiness.getNationalStrategiesCountries();
				} else {
					countryList = CountryReportBusiness.getSocialDialogueCountries();
				}

				req.setAttribute("countrySelected", country);
				req.setAttribute("section_id", section);
				req.setAttribute("countryList", countryList);
			} else if(page.equals("qualitative_data_member_states")) {
				LOGGER.info("Arriving to Qualitative Form for Member States.");
				nextURL = "/jsp/qualitative_data_member_states.jsp";
				//TODO functionality of qualitative for member states
			} else if (page.equals("methodology_data")) {
				LOGGER.info("Arriving to Methodology Data Page.");
				//TODO functionality for page methodology_data
			} else if (page.equals("update_datasets")) {
				LOGGER.info("Arriving to Update year / period of the DVT's data Form.");
				nextURL = "/jsp/update_datasets.jsp";
				errorMessage = null;
				confirmationMessage = null;
				String sectionId = req.getParameter("section_id");
				String formSent = req.getParameter("formSent");
				ArrayList<HashMap<String,String>> chartsBySectionList = null;
				ArrayList<HashMap<String,String>> sectionList = QualitativeDataBusiness.getSectionsForDatasetUpdate();
				if (formSent != null) {
					String chart_id =  req.getParameter("chart_id");
					String indicator_id =  req.getParameter("indicator_id");
					String datasetChart =  req.getParameter("datasetChart-"+indicator_id);
					boolean datasetUpdated = false;
					datasetUpdated = QualitativeDataBusiness.updateIndicatorsDataset(chart_id, indicator_id, datasetChart);
					
					if(datasetUpdated) {
						confirmationMessage = "The data has been correctly saved.";
					} else {
						errorMessage = "An error has occurred while updating the database.";
					}
					
					sendAlertsToUser(req, confirmationMessage, errorMessage);
				}
				
				if(sectionId != null) {
					chartsBySectionList = QualitativeDataBusiness.getChartsBySection(sectionId);
				} else {
					chartsBySectionList = QualitativeDataBusiness.getChartsBySection(UPDATE_DATASETS_DEFAULT_SECTION_ID);
				}
				
				req.setAttribute("sectionList", sectionList);
				req.setAttribute("chartsBySectionList", chartsBySectionList);
				req.setAttribute("sectionSelected", sectionId);
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
	
	private void copyFilesToDVT() throws Exception {
		FileInputStream instream = null;
		FileOutputStream outstream = null;
		String jsonDirectory = configurationData.getString("directory.etl")+configurationData.getString("directory.etl.job.literals")
			+configurationData.getString("directory.etl.literals.output");
		LOGGER.info("jsonDirectory: "+jsonDirectory);
		String literalsDirectory = configurationData.getString("directory.barometer")+configurationData.getString("directory.model.files");
		LOGGER.info("literalsDirectory: "+literalsDirectory);
		File linksFileInput = new File(jsonDirectory+"Links-List.txt");
		LOGGER.info("linksFileInput: "+linksFileInput);
		File linksFileOutput = new File(literalsDirectory+"Links-List.txt");
		LOGGER.info("linksFileOutput: "+linksFileOutput);
		File draftLiteralsFileInput = new File(jsonDirectory+"Draft_Literals.json");
		LOGGER.info("draftLiteralsFileInput: "+draftLiteralsFileInput);
		File draftLiteralsFileOutput = new File(literalsDirectory+"Literals.json");
		LOGGER.info("draftLiteralsFileOutput: "+draftLiteralsFileOutput);
//		File publishedLiteralsFileInput = new File(jsonDirectory+"Published_Literals.json");
//		LOGGER.info("publishedLiteralsFileInput: "+publishedLiteralsFileInput);
//		File publishedLiteralsFileOutput = new File(literalsDirectory+"Published_Literals.json");
//		LOGGER.info("publishedLiteralsFileOutput: "+publishedLiteralsFileOutput);
		
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
	    LOGGER.info("Links File ended reading. Closing streams");
	    
	    //Draft literals
		instream = new FileInputStream(draftLiteralsFileInput);
		outstream = new FileOutputStream(draftLiteralsFileOutput);
	    buffer = new byte[1024];
	    
	    while ((length = instream.read(buffer)) > 0){
	    	outstream.write(buffer, 0, length);
	    }
	    instream.close();
	    outstream.close();
	    LOGGER.info("Draft Literals File ended reading. Closing streams");
	    
//		instream = new FileInputStream(publishedLiteralsFileInput);
//		outstream = new FileOutputStream(publishedLiteralsFileOutput);
//	    buffer = new byte[1024];
//	    //Published literals
//	    while ((length = instream.read(buffer)) > 0){
//	    	outstream.write(buffer, 0, length);
//	    }
//	    instream.close();
//	    outstream.close();
//	    LOGGER.info("Published Literals File ended reading. Closing streams");
	}
	
	private void sendAlertsToUser(HttpServletRequest req, String confirmation, String error) {
		if(error != null) {
			req.setAttribute("errorMessage", error);
		}					
		if(confirmation != null) {
			req.setAttribute("confirmationMessage", confirmation);
		}
		
		req.setAttribute("errorMessage", error);
		req.setAttribute("confirmationMessage", confirmation);
	}

	/**
	 * This method destroys the servlet. It can be empty. ALWAYS destroy everything (in the end, at least).
	 */
	@Override
	public void destroy() {}
}
