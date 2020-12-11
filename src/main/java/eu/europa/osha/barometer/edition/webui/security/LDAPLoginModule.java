package eu.europa.osha.barometer.edition.webui.security;

import java.util.Hashtable;
import java.util.Map;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.*;
import javax.naming.ldap.InitialLdapContext;
import javax.security.auth.Subject;
import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.NameCallback;
import javax.security.auth.callback.PasswordCallback;
import javax.security.auth.callback.UnsupportedCallbackException;
import javax.security.auth.login.FailedLoginException;
import javax.security.auth.login.LoginException;
import javax.security.auth.spi.LoginModule;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.sun.security.auth.UserPrincipal;

public class LDAPLoginModule implements LoginModule {
	
	/** The Constant LOGGER. */
	private static final Logger LOGGER = LogManager.getLogger(LDAPLoginModule.class);

    /** JNDI Provider */
    public final String USER_PROVIDER = "user.provider.url";
    public final String GROUP_PROVIDER = "group.provider.url";
    
    // configurable options
    private boolean debug = true;
    private boolean strongDebug = true;
    private String userProvider;
    private String groupProvider;
    private boolean useFirstPass = false;
    private boolean tryFirstPass = false;
    private boolean storePass = false;
    private boolean clearPass = false;
    
    // the authentication status
    private boolean succeeded = false;
    private boolean commitSucceeded = false;
    
    // username, password, and JNDI context
    private String username;
    private char[] password;
    DirContext ctx;
    private UserPrincipal userPrincipal;
    
    // initial state
    private Subject subject;
    private CallbackHandler callbackHandler;
    private Map sharedState;
    private Map options;
    private static final String USER_UID = "sAMAccountName";
    private static final String NAME = "javax.security.auth.login.name";
    private static final String PWD = "javax.security.auth.login.password";

    /**
     * Initialize this LoginModule.
     *
     * @param subject the Subject to be authenticated.
     *
     * @param callbackHandler a CallbackHandler for communicating
     *			with the end user (prompting for usernames and
     *			passwords, for example). 
     *
     * @param sharedState shared LoginModule state. 
     *
     * @param options options specified in the login
     *			Configuration for this particular
     *			LoginModule.
     */
    public void initialize(Subject subject, CallbackHandler callbackHandler, Map sharedState, Map options) {
    	LOGGER.info("Initializing Login Module...");
        this.subject = subject;
        this.callbackHandler = callbackHandler;
        this.sharedState = sharedState;
        this.options = options;

        // Initialize any configured options
        debug = "true".equalsIgnoreCase((String) options.get("debug"));
        strongDebug = "true".equalsIgnoreCase((String) options.get("strongDebug"));
        userProvider = (String) options.get(USER_PROVIDER);
        groupProvider = (String) options.get(GROUP_PROVIDER);
        tryFirstPass = "true".equalsIgnoreCase((String) options.get("tryFirstPass"));
        useFirstPass = "true".equalsIgnoreCase((String) options.get("useFirstPass"));
        storePass = "true".equalsIgnoreCase((String) options.get("storePass"));
        clearPass = "true".equalsIgnoreCase((String) options.get("clearPass"));
    }

    /**
     * Prompt for username and password.
     * Verify the password against the relevant name service.
     *
     * @return true always, since this LoginModule
     *		should not be ignored.
     *
     * @exception FailedLoginException if the authentication fails.
     *
     * @exception LoginException if this LoginModule
     *		is unable to perform the authentication.
     */
    public boolean login() throws LoginException {

        if (userProvider == null) {
            throw new LoginException("Error: Unable to locate JNDI user provider");
        }
        if (groupProvider == null) {
            throw new LoginException("Error: Unable to locate JNDI group provider");
        }

        // attempt the authentication
        if (tryFirstPass) {

            try {
                // attempt the authentication by getting the
                // username and password from shared state
                attemptAuthentication(true);

                // authentication succeeded
                succeeded = true;
                if (debug) {
                	LOGGER.info("[LdapLoginModule] tryFirstPass succeeded");
                }
                return true;
            } catch (LoginException le) {
                // authentication failed -- try again below by prompting
                cleanState();
                if (debug) {
                	LOGGER.error("[LdapLoginModule] tryFirstPass failed with: "	+ le.toString());
                }
            }

        } else if (useFirstPass) {

            try {
                // attempt the authentication by getting the
                // username and password from shared state
                attemptAuthentication(true);

                // authentication succeeded
                succeeded = true;
                if (debug) {
                	LOGGER.info("[LdapLoginModule] useFirstPass succeeded");
                }
                return true;
            } catch (LoginException le) {
                // authentication failed
                cleanState();
                if (debug) {
                	LOGGER.error("[LdapLoginModule] useFirstPass succeeded");
                }
                throw le;
            }
        }

        // attempt the authentication by prompting for the username and pwd
        try {
            attemptAuthentication(false);

            // authentication succeeded
            succeeded = true;
            if (debug) {
            	LOGGER.info("[LdapLoginModule] regular authentication succeeded");
            }
            return true;
        } catch (LoginException le) {
            cleanState();
            if (debug) {
            	LOGGER.error("[LdapLoginModule] regular authentication failed"  + le.toString());
            }
            throw le;
        }
    }

    /**
     * Abstract method to commit the authentication process (phase 2).
     *
     * This method is called if the LoginContext's
     * overall authentication succeeded
     * (the relevant REQUIRED, REQUISITE, SUFFICIENT and OPTIONAL LoginModules
     * succeeded).
     *
     * If this LoginModule's own authentication attempt
     * succeeded (checked by retrieving the private state saved by the
     * login method), then this method associates a UserPrincipal
     * with the Subject located in the
     * >LoginModule.  If this LoginModule's own
     * authentication attempted failed, then this method removes
     * any state that was originally saved.
     *
     * @exception LoginException if the commit fails
     *
     * @return true if this LoginModule's own login and commit
     *		attempts succeeded, or false otherwise.
     */
    public boolean commit() throws LoginException {

        if (succeeded == false) {
            return false;
        } else {
            if (subject.isReadOnly()) {
                cleanState();
                throw new LoginException("Subject is Readonly");
            }
            // add Principals to the Subject
            if (!subject.getPrincipals().contains(userPrincipal)) {
                subject.getPrincipals().add(userPrincipal);
            }

            if (debug) {
            	LOGGER.info("[LdapLoginModule] added UserPrincipal to Subject");
            }
        }
        // in any case, clean out state
        cleanState();
        commitSucceeded = true;
        return true;
    }

    /**
     * This method is called if the LoginContext's
     * overall authentication failed.
     * (the relevant REQUIRED, REQUISITE, SUFFICIENT and OPTIONAL LoginModules
     * did not succeed).
     *
     * If this LoginModule's own authentication attempt
     * succeeded (checked by retrieving the private state saved by the
     * login and commit methods),
     * then this method cleans up any state that was originally saved.
     *
     * @exception LoginException if the abort fails.
     *
     * @return false if this LoginModule's own login and/or commit attempts
     *		failed, and true otherwise.
     */
    public boolean abort() throws LoginException {
        if (debug) {
        	LOGGER.error("[LdapLoginModule]: aborted authentication failed");
        }

        if (succeeded == false) {
            return false;
        } else if (succeeded == true && commitSucceeded == false) {

            // Clean out state
            succeeded = false;
            cleanState();

            userPrincipal = null;
        } else {
            // overall authentication succeeded and commit succeeded,
            // but someone else's commit failed
            logout();
        }
        return true;
    }

    /**
     * Logout a user.
     *
     * This method removes the Principals
     * that were added by the commit method.
     *
     * @exception LoginException if the logout fails.
     *
     * @return true in all cases since this LoginModule
     *		should not be ignored.
     */
    public boolean logout() throws LoginException {
        if (subject.isReadOnly()) {
            cleanState();
            throw new LoginException("Subject is Readonly");
        }
        subject.getPrincipals().remove(userPrincipal);

        // clean out state
        cleanState();
        succeeded = false;
        commitSucceeded = false;

        userPrincipal = null;
        if (debug) {
        	LOGGER.info("[LdapLoginModule]: Logged out Subject");
        }
        return true;
    }

    /**
     * Attempt authentication
     *
     * @param getPasswdFromSharedState boolean that tells this method whether
     *		to retrieve the password from the sharedState.
     */
    private void attemptAuthentication(boolean getPasswdFromSharedState)
            throws LoginException {
        // first get the username and password
        getUsernamePassword(getPasswdFromSharedState);

        try {
        	
            // get the user's passwd entry from the user provider URL
            InitialContext iCtx = new InitialLdapContext(new Hashtable(options), null);
            ctx = (DirContext) iCtx.lookup(userProvider);
//            ctx = (DirContext) iCtx.lookup("java:comp/env/matrixLdap");

            /*
            SearchControls controls = new SearchControls
            (SearchControls.ONELEVEL_SCOPE,
            0,
            5000,
            new String[] { USER_PWD },
            false,
            false);
             */

            SearchControls controls = new SearchControls();
            controls.setSearchScope(SearchControls.SUBTREE_SCOPE);
            NamingEnumeration ne = ctx.search("",
                    "(" + USER_UID + "=" + username + ")",
                    controls);
            if (ne.hasMore()) {
                SearchResult result = (SearchResult) ne.next();
                Attributes attributes = result.getAttributes();
                
                // check the password
                if (verifyPassword(new String(password)) == true) {

                    // authentication succeeded
                    if (debug) {
                    	LOGGER.info("[LdapLoginModule]: attemptAuthentication() succeeded");
                    }

                } else {
                    // authentication failed
                    if (debug) {
                    	LOGGER.error("[LdapLoginModule]: attemptAuthentication() failed");
                    }
                    throw new FailedLoginException("Login incorrect");
                }

                // save input as shared state only if
                // authentication succeeded
                if (storePass
                        && !sharedState.containsKey(NAME)
                        && !sharedState.containsKey(PWD)) {
                    sharedState.put(NAME, username);
                    sharedState.put(PWD, password);
                }

                // create the user principal
                userPrincipal = new UserPrincipal(username);

                // get the UID
                Attribute uid = attributes.get(USER_UID);
                String uidNumber = (String) uid.get();
                if (debug && uidNumber != null) {
                	LOGGER.info("[LdapLoginModule]: user: " + username + " has UID: " + uidNumber);
                }
            } else {
                // bad username
                if (debug) {
                	LOGGER.error("[LdapLoginModule]: User not found");
                }
                throw new FailedLoginException("User not found");
            }
        } catch (NamingException ne) {
            // bad username
            if (debug) {
            	LOGGER.error("[LdapLoginModule]: User not found");
                ne.printStackTrace();
            }
            throw new FailedLoginException("User not found");
        }

        // authentication succeeded
    }

    /**
     * Get the username and password.
     * This method does not return any value.
     * Instead, it sets global name and password variables.
     *
     * Also note that this method will set the username and password
     * values in the shared state in case subsequent LoginModules
     * want to use them via use/tryFirstPass.
     *
     * @param getPasswdFromSharedState boolean that tells this method whether
     *		to retrieve the password from the sharedState.
     */
    private void getUsernamePassword(boolean getPasswdFromSharedState)
            throws LoginException {

        if (getPasswdFromSharedState) {
            // use the password saved by the first module in the stack
            username = (String) sharedState.get(NAME);
            password = (char[]) sharedState.get(PWD);
            return;
        }

        // prompt for a username and password
        if (callbackHandler == null) {
            throw new LoginException("Error: no CallbackHandler available "
                    + "to garner authentication information from the user");
        }

        String protocol = userProvider.substring(0, userProvider.indexOf(":"));

        Callback[] callbacks = new Callback[2];
        callbacks[0] = new NameCallback(protocol + " username: ");
        callbacks[1] = new PasswordCallback(protocol + " password: ", false);

        try {
            callbackHandler.handle(callbacks);
            username = ((NameCallback) callbacks[0]).getName();
            char[] tmpPassword = ((PasswordCallback) callbacks[1]).getPassword();
            password = new char[tmpPassword.length];
            System.arraycopy(tmpPassword, 0,
                    password, 0, tmpPassword.length);
            ((PasswordCallback) callbacks[1]).clearPassword();

        } catch (java.io.IOException ioe) {
            throw new LoginException(ioe.toString());
        } catch (UnsupportedCallbackException uce) {
            throw new LoginException("Error: " + uce.getCallback().toString()
                    + " not available to garner authentication information "
                    + "from the user");
        }

        // print debugging information
        if (strongDebug) {
        	LOGGER.info("[LdapLoginModule] user entered username: " + username);
            System.out.print("[LdapLoginModule] user entered password: ");
            for (int i = 0; i < password.length; i++) {
                System.out.print(password[i]);
            }
            System.out.println();
        }
    }

    /**
     * Verify a password against the LDAP password
     */
    private boolean verifyPassword(String password) throws LoginException {
    	LOGGER.info("[LdapLoginModule] Verifying password... ");
        String userDn = (String) options.get("ldap.user.dn");
        String baseDn = (String) options.get("ldap.base.dn");
        try {
            Hashtable environment = new Hashtable(options);
//            environment.put(Context.SECURITY_PRINCIPAL, USER_UID + "=" + username + "," + userDn + "," + baseDn);
            //TODO Not sure if in this case should I use @agency or not
            environment.put(Context.SECURITY_PRINCIPAL, username + "@agency");
            environment.put(Context.SECURITY_CREDENTIALS, password);
            DirContext authContext = new InitialDirContext(environment);
            return true;
        } catch (Exception ex) {
        	LOGGER.error("[LdapLoginModule] Error while verifying password.");
            throw new LoginException(ex.getMessage());
        }
    }

    /**
     * Clean out state because of a failed authentication attempt
     */
    private void cleanState() {
    	LOGGER.info("[LdapLoginModule] Cleaning state... ");
        username = null;
        if (password != null) {
            for (int i = 0; i < password.length; i++) {
                password[i] = ' ';
            }
            password = null;
        }
        ctx = null;

        if (clearPass) {
            sharedState.remove(NAME);
            sharedState.remove(PWD);
        }
    }
}