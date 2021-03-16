package eu.europa.osha.barometer.edition.webui.security;

import java.io.IOException;

import org.apache.directory.api.ldap.model.exception.LdapException;
import org.apache.directory.ldap.client.api.LdapConnection;
import org.apache.directory.ldap.client.api.LdapNetworkConnection;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class LDAPConnectionService {

	/** The Constant LOGGER. */
	private static final Logger LOGGER = LogManager.getLogger(LDAPConnectionService.class);
	
	/**
	 * Creates connection to an LDAP
	 * @param url String url of the LDAP to create connection with
	 * @return LdapConnection the connection created
	 */
	public static LdapConnection getConnection(String url) {
		LdapConnection connection = new LdapNetworkConnection(url);
		if(connection.isConnected()) {
			LOGGER.info("Connection established to the OSHA LDAP Service.");
		} else {
			LOGGER.error("Error while trying create connection to the given LDAP.");
		}
		return connection;
	}
	
	/**
	 * 
	 * @param con LdapConnection
	 * @param user String 
	 * @param password String 
	 * @return boolean true if the bind was successful, false if there was an error while binding
	 */
	public static boolean login (LdapConnection con, String user, String password) {
		try {
			con.bind(user, password);
			return true;
		} catch (LdapException e) {
			LOGGER.error("Error while trying to log in the LDAP Service. "+e.getMessage());
			e.printStackTrace();
			return false;
		}
	}
	
	/**
	 * 
	 * @param con
	 */
	public static void logout(LdapConnection con) {
		try {
			con.unBind();
		} catch (LdapException e) {
			LOGGER.error("Error while trying to log out from the LDAP Service. "+e.getMessage());
			e.printStackTrace();
		}
	}
	
	/**
	 * 
	 * @param con
	 */
	public static void closeConnection(LdapConnection con) {
		try {
			con.close();
		} catch (IOException e) {
			LOGGER.error("Error while trying close LDAP Connection. "+e.getMessage());
			e.printStackTrace();
		}
	}
	
}
