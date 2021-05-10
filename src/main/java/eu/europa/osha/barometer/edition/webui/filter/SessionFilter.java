package eu.europa.osha.barometer.edition.webui.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import eu.europa.osha.barometer.edition.webui.bean.User;

@WebFilter("/user")
public class SessionFilter implements Filter {
	private static final Logger LOGGER = LogManager.getLogger(SessionFilter.class);
	
	public void init(FilterConfig filterConfig) throws ServletException {
	}

	public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain)
			throws IOException, ServletException {
		HttpServletRequest request = (HttpServletRequest)servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        request.setCharacterEncoding("UTF-8");

        String page = request.getParameter("page");
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");
        
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        
        try {
        	//If user is logged he can access any page
        	if(user != null) {
        		filterChain.doFilter(request, response);
        	} else {
        		//If user is not logged but page is login he can access login page
        		if(page!=null){
        			if(page.equals("login")){
                        filterChain.doFilter(request, response);
                    } else {
                    	if(username != null && password != null){
                            filterChain.doFilter(request, response);
                        } else {
                            LOGGER.error("User is not logged in. Redirecting...");
                            response.sendRedirect(request.getContextPath() + "/user?page=login");
                        }
                    }
        		}else {
        			LOGGER.error("Page error. Redirecting...");
                    response.sendRedirect(request.getContextPath() + "/user?page=login");
        		}        		
        	}
        }catch(Exception e) {
        	LOGGER.error("AN ERROR HAS OCCURRED.");
        	e.printStackTrace();
        }
	}
	
	public void destroy() {
	}

}
