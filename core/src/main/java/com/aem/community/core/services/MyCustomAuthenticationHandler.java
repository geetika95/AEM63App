package com.aem.community.core.services;

import org.apache.sling.auth.core.AuthUtil;
import org.apache.sling.auth.core.spi.AuthenticationFeedbackHandler;
import org.apache.sling.auth.core.spi.AuthenticationHandler;
import org.apache.sling.auth.core.spi.AuthenticationInfo;
import org.apache.sling.auth.core.spi.DefaultAuthenticationFeedbackHandler;
import org.osgi.framework.Constants;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.AttributeType;
import org.osgi.service.metatype.annotations.Designate;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.Credentials;
import javax.jcr.SimpleCredentials;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component(immediate = true, enabled = true, name = "Custom Auth Handler ", service = AuthenticationHandler.class,
        property = {
                Constants.SERVICE_DESCRIPTION + "=My Custom authentication handler",
                Constants.SERVICE_RANKING + "=1100",
                AuthenticationHandler.PATH_PROPERTY + "=/content/AEM63App",
                AuthenticationHandler.TYPE_PROPERTY + "=" + HttpServletRequest.FORM_AUTH,
                "jaas.controlFlag" + "=sufficient",
                "jaas.realmName" + "=jackrabbit.oak",
                "jaas.ranking" + "=1000"
        })
@Designate(ocd = MyCustomAuthenticationHandler.AuthServiceConfiguration.class)
public class MyCustomAuthenticationHandler extends DefaultAuthenticationFeedbackHandler implements AuthenticationHandler, AuthenticationFeedbackHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(MyCustomAuthenticationHandler.class);

    @ObjectClassDefinition(name = "My Auth Service Configuration", description = "Service Configuration")
    public static @interface AuthServiceConfiguration {

        @AttributeDefinition(
                name = Constants.SERVICE_DESCRIPTION,
                type = AttributeType.STRING
        )
        String servicename_propertyname_description() default "My Custom Authentication Handler";

        @AttributeDefinition(
                name = AuthenticationHandler.PATH_PROPERTY,
                type = AttributeType.STRING
        )
        String servicename_propertyname_path() default "/content/AEM63App";

    }

    private static final String REQUEST_METHOD = "POST";
    private static final String USER_NAME = "j_username";
    private static final String PASSWORD = "j_password";
    static final String AUTH_TYPE = "Geetika";

    static final String REQUEST_URL_SUFFIX = "/j_mycustom_security_check";

    @Override
    public AuthenticationInfo extractCredentials(HttpServletRequest request, HttpServletResponse response) {
        LOGGER.debug("request reached here");
        if (REQUEST_METHOD.equals(request.getMethod()) && request.getRequestURI().endsWith(REQUEST_URL_SUFFIX)
                && request.getParameter(USER_NAME) != null) {

            if (!AuthUtil.isValidateRequest(request)) {
                AuthUtil.setLoginResourceAttribute(request, request.getContextPath());
            }

            SimpleCredentials creds = new SimpleCredentials(request.getParameter(USER_NAME), request.getParameter(PASSWORD).toCharArray());
            //ATTR_HOST_NAME_FROM_REQUEST can be any thing this is just an example
            creds.setAttribute("hostname-custom-geetika", request.getServerName());

            return createAuthenticationInfo(creds);
        }
        return null;
    }

    //Custom Create AuthInfo. Not required but you can create
    private AuthenticationInfo createAuthenticationInfo(Credentials creds) {
        //Note that there is different signature of this method. Use one that you need.
        AuthenticationInfo info = new AuthenticationInfo(AUTH_TYPE);
        //this you can use it later in auth process
        info.put("login-info", creds);
        return info;
    }

    @Override
    public boolean requestCredentials(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws IOException {
        LOGGER.debug("request cred called");

        return false;
    }

    @Override
    public void dropCredentials(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws IOException {

        LOGGER.debug("drop called ");

    }

    /**
     * If you see most of the method under sling authentication handler, They have request and response object available. You can use that object to get information about user (Either by reading cookie or some other way).
     */
//Important methods
//Return true if succesful
    @Override
    public boolean authenticationSucceeded(HttpServletRequest request, HttpServletResponse response,
                                           AuthenticationInfo authInfo) {

        /*
         * Note: This method is called if this handler provided credentials
         * which succeeded login into the repository
         */

        // ensure fresh authentication data
//            refreshAuthData(request, response, authInfo);

        final boolean result;
        // SLING-1847: only consider a resource redirect if this is a POST request
        // to the j_security_check URL
        if (REQUEST_METHOD.equals(request.getMethod())
                && request.getRequestURI().endsWith(REQUEST_URL_SUFFIX)) {

            if (DefaultAuthenticationFeedbackHandler.handleRedirect(request, response)) {
                // terminate request, all done in the default handler
                result = false;
            } else {
                // check whether redirect is requested by the resource parameter
                final String targetResource = AuthUtil.getLoginResource(request, null);
                if (targetResource != null) {
                    try {
                        if (response.isCommitted()) {
                            throw new IllegalStateException("Response is already committed");
                        }
                        response.resetBuffer();

                        StringBuilder b = new StringBuilder();
                        if (AuthUtil.isRedirectValid(request, targetResource)) {
                            b.append(targetResource);
                        } else if (request.getContextPath().length() == 0) {
                            b.append("/");
                        } else {
                            b.append(request.getContextPath());
                        }
                        response.sendRedirect(b.toString());
                    } catch (IOException ioe) {
                        LOGGER.error("Failed to send redirect to: " + targetResource, ioe);
                    }

                    // terminate request, all done
                    result = true;
                } else {
                    // no redirect, hence continue processing
                    result = false;
                }
            }
        } else {
            // no redirect, hence continue processing
            result = false;
        }

        // no redirect
        LOGGER.debug("inside auth succeeded {} ", result);
        return result;
    }

    //Do something when authentication failed.
    @Override
    public void authenticationFailed(HttpServletRequest request, HttpServletResponse response,
                                     AuthenticationInfo authInfo) {

        authInfo.clear();
        LOGGER.debug("login failed");
        request.setAttribute(FAILURE_REASON, "invalid_credentials");

    }


}
