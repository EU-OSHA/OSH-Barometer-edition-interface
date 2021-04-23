package eu.europa.osha.barometer.edition.webui.security;

import java.io.IOException;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import org.apache.directory.api.ldap.model.cursor.EntryCursor;
import org.apache.directory.api.ldap.model.cursor.SearchCursor;
import org.apache.directory.api.ldap.model.entry.Attribute;
import org.apache.directory.api.ldap.model.entry.Entry;
import org.apache.directory.api.ldap.model.exception.LdapException;
import org.apache.directory.api.ldap.model.message.Response;
import org.apache.directory.api.ldap.model.message.SearchRequest;
import org.apache.directory.api.ldap.model.message.SearchRequestImpl;
import org.apache.directory.api.ldap.model.message.SearchResultEntry;
import org.apache.directory.api.ldap.model.message.SearchScope;
import org.apache.directory.api.ldap.model.name.Dn;
import org.apache.directory.ldap.client.api.LdapConnection;
import org.apache.directory.ldap.client.api.LdapConnectionConfig;
import org.apache.directory.ldap.client.api.LdapNetworkConnection;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class LDAPConnectionService {

	/** The Constant LOGGER. */
	private static final Logger LOGGER = LogManager.getLogger(LDAPConnectionService.class);
	private static ResourceBundle configurationData = ResourceBundle.getBundle("eu.europa.osha.barometer.edition.webui.conf.configuration");
	
	private static LdapConnectionConfig setLDAPConnectionConfiguration () {
		LdapConnectionConfig config = new LdapConnectionConfig();
		config.setLdapHost( configurationData.getString("ldap.host"));
		config.setLdapPort( Integer.parseInt(configurationData.getString("ldap.port") ));
		config.setName( configurationData.getString("ldap.username") );
		config.setCredentials( configurationData.getString("ldap.password") );
		return config;
	}
	
	private static LdapConnectionConfig setLDAPConnectionConfiguration (String user, String password) {
		LdapConnectionConfig config = new LdapConnectionConfig();
		config.setLdapHost( configurationData.getString("ldap.host"));
		config.setLdapPort( Integer.parseInt(configurationData.getString("ldap.port") ));
		config.setName( user );
		config.setCredentials( password );
		return config;
	}
	
	/**
	 * Creates connection to an LDAP
	 * @param url String url of the LDAP to create connection with
	 * @return LdapConnection the connection created
	 */
	public static LdapConnection getConnection() {
//		LdapConnection connection = new LdapNetworkConnection(url);
		LdapConnectionConfig config = setLDAPConnectionConfiguration();
		LdapConnection connection = new LdapNetworkConnection(config);
		LOGGER.info("Connection: "+connection);
		
		try {
			connection.bind();
			if(connection.isConnected()) {
				LOGGER.info("Connection established to the OSHA LDAP Service.");
			} else {
				LOGGER.error("Error while trying create connection to the given LDAP.");
			}
		} catch (LdapException e) {
			LOGGER.error("Error while trying to set connection to the LDAP Service. "+"Exception: "+e.getClass().getName());
			LOGGER.error("Message: "+e.getMessage());
			e.printStackTrace();
		}
		return connection;
	}
	
//	public static boolean setAuthenticationCredentials(LdapConnection con) {
//		try {
////			con.bind(configurationData.getString("ldap.username"), configurationData.getString("ldap.password"));
//			con.bind();
//			return true;
//		} catch (LdapException e) {
//			LOGGER.error("Error while trying to set auth credentials in the LDAP Service. "+e.getMessage());
//			e.printStackTrace();
//			return false;
//		}
//	}
	
	/**
	 * 
	 * @param con LdapConnection
	 * @param user String 
	 * @param password String 
	 * @return boolean true if the bind was successful, false if there was an error while binding
	 */
	public static boolean login (LdapConnection con, String user, String password) {
		boolean foundPassword = false;
		boolean foundUser = false;
		try {
			LOGGER.info("User: "+user+" password: "+password);
			
			String encryptedPassword = "";
					
	        MessageDigest messageDigest = MessageDigest.getInstance("SHA-1");
	        messageDigest.reset();
	        messageDigest.update(password.getBytes(StandardCharsets.UTF_8));
	        encryptedPassword = String.format("%040x", new BigInteger(1, messageDigest.digest()));
	        LOGGER.info("The SHA1 of "+password+"is: "+encryptedPassword);

	        LOGGER.info("-----------------------------------------------------------------");
	        LOGGER.info("First search request with scope subtree and filter user and encrypted pswd");
	        //Search by memberUid and userPassword
			SearchRequest req = new SearchRequestImpl();
			req.setScope(SearchScope.SUBTREE);
//			req.setScope(SearchScope.OBJECT);
			req.setBase(new Dn(configurationData.getString("ldap.base.dn")));
			req.setFilter("(memberUid="+user+")(userPassword="+encryptedPassword+")");
			req.addAttributes("*");
			
			SearchCursor searchCursor = con.search( req );
			Dn existinUserDn = null;
			LOGGER.info("Searching user in server... "+searchCursor);
			
			while ( searchCursor.next() && !foundUser )
		    {
				LOGGER.info("While searchCursor... ");
		        Response response = searchCursor.get();
//		        LOGGER.info("response: "+response);
		        LOGGER.info("-----------------------------------------------------------------");
		        
		        // Process the SearchResultEntry
		        if ( response instanceof SearchResultEntry )
		        {
		        	LOGGER.info("response instanceof SearchResultEntry: "+(response instanceof SearchResultEntry));
		            Entry resultEntry = ( ( SearchResultEntry ) response ).getEntry();
		            LOGGER.info("resultEntry: "+resultEntry );
		            existinUserDn = resultEntry.getDn();
		            LOGGER.info("existinUserDn: "+existinUserDn);

		            foundUser = resultEntry.contains("memberUid",user);
		            if(foundUser) {
		            	LOGGER.info("USER FOUND!!!");
		            } else {
		            	LOGGER.info("USER NOT FOUND");
		            }
		            
//		            boolean compareMemberUid = con.compare(existinUserDn, "memberUid", user);
//	            	if(compareMemberUid) {
//	            		LOGGER.info("MEMBER UID FOUND!!!");
//	            	} else {
//	            		LOGGER.info("MEMBER UID NOT FOUND");
//	            	}
//	            	boolean comparePasswordEncrypted = con.compare(existinUserDn, "userPassword", encryptedPassword);
//	            	if(comparePasswordEncrypted) {
//	            		LOGGER.info("PASSWORD ENCRYPTED FOUND!!!");
//	            	} else {
//	            		LOGGER.info("PASSWORD ENCRYPTED NOT FOUND");
//	            	}
//	            	boolean comparePasswordNonEncrypted = con.compare(existinUserDn, "userPassword", password);
//	            	if(comparePasswordNonEncrypted) {
//	            		LOGGER.info("PASSWORD NOT ENCRYPTED FOUND!!!");
//	            	} else {
//	            		LOGGER.info("PASSWORD NOT ENCRYPTED NOT FOUND");
//	            	}
	            	
	            	
//		            boolean passwordFound = resultEntry.contains("userPassword",encryptedPassword);
//		            if(passwordFound) {
//		            	LOGGER.info("PASSWORD FOUND!!!");
//		            } else {
//		            	LOGGER.info("PASSWORD NOT FOUND");
//		            }
//		            Attribute psw = resultEntry.get("userPassword");
//		            if(psw != null) {
//		            	LOGGER.info("userPassword attribute: "+psw.getString());
//		            }		            
		        }
		    }
			
			//ldap.people.dn
			if(foundUser) {
				foundPassword = false;
	        	req = new SearchRequestImpl();
	        	req.setBase(new Dn(configurationData.getString("ldap.people.dn")));
	        	req.setFilter("(memberUid="+user+")(userPassword="+encryptedPassword+")");
				req.addAttributes("*");
				searchCursor = con.search( req );
				LOGGER.info("Searching user in server... "+searchCursor);
				
				while ( searchCursor.next() && !foundPassword )
			    {
					LOGGER.info("While searchCursor... ");
			        Response response = searchCursor.get();
			        LOGGER.info("response: "+response);
			        LOGGER.info("-----------------------------------------------------------------");
			        
			        // Process the SearchResultEntry
			        if ( response instanceof SearchResultEntry )
			        {
			        	LOGGER.info("response instanceof SearchResultEntry: "+(response instanceof SearchResultEntry));
			        	Entry resultEntry = ( ( SearchResultEntry ) response ).getEntry();
			            LOGGER.info("resultEntry: "+resultEntry );
			            foundPassword = resultEntry.contains("userPassword",password);
			            if(foundPassword) {
			            	LOGGER.info("PASSWORD NOT ENCRYPTED FOUND!!!");
			            	foundPassword = true;
			            } else {
			            	LOGGER.info("PASSWORD NOT ENCRYPTED NOT FOUND");
			            }
			            foundPassword = resultEntry.contains("userPassword",encryptedPassword);
			            if(foundPassword) {
			            	LOGGER.info("PASSWORD ENCRYPTED FOUND!!!");
			            	foundPassword = true;
			            } else {
			            	LOGGER.info("PASSWORD ENCRYPTED NOT FOUND");
			            }
			        }
			    }
			}

			
//			Entry test = con.lookup(new Dn(configurationData.getString("ldap.base.dn")));
//			LOGGER.info("test Entry: "+test );
			
//			if(foundUser) {
//				LOGGER.info("Verify user password");
//				boolean passwordVerified = verifyPassword(existinUserDn,password);
//				
//				if(passwordVerified) {
//					LOGGER.info("USER PASSWORD CORRECT");
//					return true;
//				}else {
//					LOGGER.info("USER PASSWORD INCORRECT");
//					return false;
//				}
//			}
			
			return foundPassword;
		} catch (Exception e) {
			LOGGER.error("Error while trying to search user in the LDAP Service. "+"Exception: "+e.getClass().getName());
			LOGGER.error("Message: "+e.getMessage());
			e.printStackTrace();
			return false;
		}
	}
	
	public static boolean verifyPassword (Dn userDn, String password) throws IOException {
		String username = "";
		if(userDn != null) {
			username = userDn.toString();
		}
		
		LdapConnectionConfig config = setLDAPConnectionConfiguration(username,password);
		LdapConnection con = new LdapNetworkConnection(config);
		try {
			con.bind();
			if(con.isConnected()) {
				return true;
			} else {
				return false;
			}
		} catch (LdapException e) {
			LOGGER.error("Error while trying to verify users password in the LDAP Service. "+"Exception: "+e.getClass().getName());
			LOGGER.error("Message: "+e.getMessage());
			e.printStackTrace();
			return false;
		} finally {
			con.close();
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
			LOGGER.error("Error while trying to log out from the LDAP Service. "+"Exception: "+e.getClass().getName());
			LOGGER.error("Message: "+e.getMessage());
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
			LOGGER.error("Error while trying close LDAP Connection. "+"Exception: "+e.getClass().getName());
			LOGGER.error("Message: "+e.getMessage());
			e.printStackTrace();
		}
	}
	
}
