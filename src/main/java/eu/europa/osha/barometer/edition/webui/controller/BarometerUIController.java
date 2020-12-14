package eu.europa.osha.barometer.edition.webui.controller;

import java.io.IOException;
import java.util.Iterator;

import javax.security.auth.Subject;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.login.LoginContext;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.sun.security.auth.UserPrincipal;

import eu.europa.osha.barometer.edition.webui.bean.User;
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
public class BarometerUIController extends HttpServlet{

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;

	/** The Constant LOGGER. */
	private static final Logger LOGGER = LogManager.getLogger(BarometerUIController.class);
	
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

	/**
	 * This method destroys the servlet. It can be empty. ALWAYS destroy everything (in the end, at least).
	 */
	@Override
	public void destroy() {}
}
