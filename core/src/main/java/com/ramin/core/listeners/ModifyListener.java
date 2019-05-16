/*
 *  Copyright 2015 Adobe Systems Incorporated
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package com.ramin.core.listeners;


import org.apache.sling.api.SlingConstants;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.event.jobs.JobManager;
import org.osgi.framework.Constants;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventConstants;
import org.osgi.service.event.EventHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.Node;
import javax.jcr.Session;
import javax.jcr.version.Version;
import javax.jcr.version.VersionManager;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


@Component(service = EventHandler.class,
           immediate = true,
           property = {
                   Constants.SERVICE_DESCRIPTION + "=modify listener",
                   EventConstants.EVENT_TOPIC + "=org/apache/sling/api/resource/Resource/CHANGED",
                   EventConstants.EVENT_FILTER+"=(path=/content/searchpaths/*)"
           })

public class ModifyListener implements EventHandler {
    private static long counts=0;

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private static String observedPath = "/content/searchpaths";
    @Reference
    private JobManager jobManager;

    @Reference
    private ResourceResolverFactory resolverFactory;

    private static final String serviceNameForLogin = "modifyListenService";


    public void handleEvent(final Event event) {
        counts++;
        String eventPath = (String)event.getProperty(SlingConstants.PROPERTY_PATH);
        logger.info("\n\n"+"["+counts+"]"+"go into handle event: "+eventPath);

        Map<String, Object> param = new HashMap<String, Object>();
        param.put(ResourceResolverFactory.SUBSERVICE, serviceNameForLogin);
        ResourceResolver resolver = null;
        try {
            resolver = resolverFactory.getServiceResourceResolver(param);
            Session session = resolver.adaptTo(Session.class);
            VersionManager vManager = session.getWorkspace().getVersionManager();

            //String pathToNode = "/content/searchpaths/en";
            Resource resource = resolver.getResource(eventPath);

            ValueMap readMap = resource.getValueMap();
            String nodeType = (String) readMap.get("jcr:primaryType");

            if(nodeType.equals("cq:Page")){
                logger.info("It's cq:Page");
            }else{
                logger.info("It's not cq:Page");
            }

            Node nd = resource.adaptTo(Node.class);

//            try {
//                nd.addMixin("mix:versionable");
//            }catch(Exception ex){
//
//            }
//
//            session.save();
//            Version firstVersion = vManager.checkin(nd.getPath());
//            logger.info("Version: "+firstVersion);


//            Resource resource = resolver.getResource(eventPath);
//            boolean isItPage = false;
//            ValueMap readMap = resource.getValueMap();
//            String nodeType = (String) readMap.get("jcr:primaryType");
//            logger.info("res="+resource.getPath()+" isItPage="+isItPage+" "+nodeType);


            //resolver.commit();
        }
        catch(Exception exs){
            logger.info(exs.toString());
        }finally{
            if(resolver != null && resolver.isLive()){
                resolver.close();
            }
        }


        logger.info("Resource event: {} at: {}", event.getTopic(),eventPath );
    }
}

