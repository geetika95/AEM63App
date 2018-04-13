package com.aem.community.core.customlogin;

import org.apache.felix.jaas.LoginModuleFactory;
import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.AttributeType;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;

@ObjectClassDefinition(name = "Custom login module factory configuration")
public @interface Configuration {
    @AttributeDefinition(name = LoginModuleFactory.JAAS_RANKING,
            description = "Specifying the ranking (i.e. sort order) of this login module entry. The entries are sorted " +
                    "in a descending order (i.e. higher value ranked configurations come first).",
            type = AttributeType.INTEGER,
            defaultValue = "50")
    int getJAASRanking() default 50;

    @AttributeDefinition(name = LoginModuleFactory.JAAS_CONTROL_FLAG,
            description = "Property specifying whether or not a LoginModule is REQUIRED, REQUISITE, SUFFICIENT or " +
                    "OPTIOcusNAL. Refer to the JAAS configuration documentation for more details around the meaning of " +
                    "these flags.",
            type = AttributeType.STRING)
    String getJAASControlFlag() default "SUFFICIENT";

    @AttributeDefinition(name = LoginModuleFactory.JAAS_REALM_NAME,
            description = "The realm name (or application name) against which the LoginModule  is be registered. If no " +
                    "realm name is provided then LoginModule is registered with a default realm as configured in " +
                    "the Felix JAAS configuration.",
            type = AttributeType.STRING)
    String getJAASRealm();

    @AttributeDefinition(name = CustomExternalLoginModule.PARAM_IDP_NAME,
            description = "Name of the identity provider (for example: 'ldap').",
            type = AttributeType.STRING)
    String getIDPName() default "ldap";

    @AttributeDefinition(name = CustomExternalLoginModule.PARAM_SYNC_HANDLER_NAME,
            description = "Name of the sync handler.",
            type = AttributeType.STRING)
    String getSyncHandler() default "default";
}

