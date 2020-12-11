package eu.europa.osha.barometer.edition.webui.security;

import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

import javax.naming.Context;
import javax.security.auth.login.AppConfigurationEntry;
import javax.security.auth.login.AppConfigurationEntry.LoginModuleControlFlag;
import javax.security.auth.login.Configuration;

public final class ConfigurationImpl extends Configuration {

    public static final String LDAP_CONFIGURATION_NAME = "ldap";
    
    private static ResourceBundle configurationData = ResourceBundle.getBundle("eu.europa.osha.barometer.edition.webui.conf.configuration");

    public AppConfigurationEntry[] getAppConfigurationEntry(String name) {
        if (name.equals(LDAP_CONFIGURATION_NAME)) {
            Map options = new HashMap();

            String ldapProviderUrl = configurationData.getString("ldap.provider.url");
            String ldapBaseDn = configurationData.getString("ldap.base.dn");
            String ldapUserDn = configurationData.getString("ldap.user.dn");
            String ldapGroupDn = configurationData.getString("ldap.group.dn");

            options.put("debug", "true");
            options.put("useSharedState", "true");
            options.put("tryFirstPass", "true");
            options.put("useFirstPass", "true");
            options.put("storePass", "true");
			options.put(Context.INITIAL_CONTEXT_FACTORY, configurationData.getString("ldap.context.factory"));
            options.put(Context.SECURITY_AUTHENTICATION, "simple");
            options.put(Context.PROVIDER_URL, ldapProviderUrl);
            options.put("ldap.base.dn", ldapBaseDn);
            options.put("ldap.user.dn", ldapUserDn);
            options.put("user.provider.url", ldapProviderUrl + ldapUserDn + "," + ldapBaseDn);
            options.put("group.provider.url", ldapProviderUrl + ldapGroupDn + "," + ldapBaseDn);
            
            //options.put(Context.SECURITY_PRINCIPAL, configurationData.getString("ldap.principal"));
            //options.put(Context.SECURITY_CREDENTIALS, configurationData.getString("ldap.password"));

            AppConfigurationEntry ldapEntry = new AppConfigurationEntry("eu.europa.osha.barometer.edition.security.LDAPLoginModule",
                    LoginModuleControlFlag.REQUIRED, options);
            AppConfigurationEntry[] entries = new AppConfigurationEntry[]{ldapEntry};
            return entries;
        } else {
            return null;
        }
    }
}