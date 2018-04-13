package com.aem.community.core.customlogin;


/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import org.apache.felix.jaas.LoginModuleFactory;
import org.apache.jackrabbit.oak.spi.security.authentication.external.impl.ExternalLoginModule;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ConfigurationPolicy;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.metatype.annotations.Designate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.security.auth.spi.LoginModule;

//import org.apache.jackrabbit.oak.spi.security.authentication.external.impl.jmx.SyncMBeanImpl;
//import org.apache.jackrabbit.oak.spi.security.authentication.external.impl.jmx.SynchronizationMBean;
//import org.mindtree.testsite.core.authentication.impl.jmx.CustomSyncMBeanImpl;
//import org.mindtree.testsite.core.authentication.impl.jmx.SynchronizationMBean;

//import com.google.common.collect.ImmutableMap;

/**
 * Implements a LoginModuleFactory that creates {@link CustomExternalLoginModule}s and allows to configure login modules
 * via OSGi config.
 */
@Component(
        name = "Apache Jackrabbit Oak Custom External Login Module",
        service = LoginModuleFactory.class,
        configurationPolicy = ConfigurationPolicy.REQUIRE
)
@Designate(ocd = Configuration.class, factory = true)
public class CustomExternalLoginModuleFactory implements LoginModuleFactory {

    private static final Logger log = LoggerFactory.getLogger(CustomExternalLoginModuleFactory.class);

    private Configuration config;
   /* @Reference
    private SyncManager syncManager;

    @Reference
    private ExternalIdentityProviderManager idpManager;

    @Reference
    private Repository repository;*/

//    /**
//     * default configuration for the login modules
//     */
//    private ConfigurationParameters osgiConfig;

    /**
     * default configuration for the login modules
     */
//    private ConfigurationParameters osgiConfig;


    /**
     * Activates the LoginModuleFactory service
     *
     * @param config the component context
     */
    @SuppressWarnings("UnusedDeclaration")
    @Activate
    private void activate(final Configuration config) {
        //noinspection unchecked
        this.config = config;
        log.info("######## Inside Custom External Login Module Factory #######");
        String idpName = config.getIDPName();
        String sncName = config.getSyncHandler();

//        Whiteboard whiteboard = new OsgiWhiteboard(context.getBundleContext());
        try {
            // CustomSyncMBeanImpl bean = new CustomSyncMBeanImpl(repository, syncManager, sncName, idpManager, idpName);
            /*Hashtable<String, String> table = new Hashtable<String, String>();
            table.put("type", "UserManagement");
            table.put("name", "External Identity Synchronization Management");
            table.put("handler", ObjectName.quote(sncName));
            table.put("idp", ObjectName.quote(idpName));
            mbeanRegistration = whiteboard.register(SynchronizationMBean.class, bean, ImmutableMap.of(
                    "jmx.objectname",
                    new ObjectName("org.apache.jackrabbit.oak", table))
            );*/
        } catch (Exception e) { //MalformedObjectNameException
            log.error("Unable to register SynchronizationMBean.", e);
        }
    }

    @SuppressWarnings("UnusedDeclaration")
    @Deactivate
    private void deactivate() {
        /*if (mbeanRegistration != null) {
            mbeanRegistration.unregister();
            mbeanRegistration = null;
        }*/
    }

    /**
     * {@inheritDoc}
     *
     * @return a new {@link ExternalLoginModule} instance.
     */
    @Override
    public LoginModule createLoginModule() {
        return new CustomExternalLoginModule(config);
    }

}
