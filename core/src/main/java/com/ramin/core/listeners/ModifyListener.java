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


import com.day.cq.wcm.api.*;
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
import java.util.Iterator;
import java.util.Map;


@Component(service = EventHandler.class,
           immediate = true,
           property = {
                   Constants.SERVICE_DESCRIPTION + "=modify listener",
                   EventConstants.EVENT_TOPIC + "=com/day/cq/wcm/core/page"
                   //EventConstants.EVENT_TOPIC + "=org/apache/sling/api/resource/Resource/CHANGED",//"com/day/cq/wcm/core/page"
                  // EventConstants.EVENT_FILTER+"=(modifications:dn:=/content/searchpaths/*)"
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

    private Resource getParentPage(Resource childRes){
        String pathToPage = "";
        Resource pRes = childRes;
        while(pRes!=null){
            ValueMap readMap = pRes.getValueMap();
            String nodeType = (String) readMap.get("jcr:primaryType");
            if(nodeType.equals("cq:Page")){
                //logger.info("It's cq:Page");
                return pRes;
            }else{
                //logger.info("It's not cq:Page");
            }
            pRes=pRes.getParent();
        }
        return null;

    }

     public void handleEvent(final Event event) {
        logger.info("["+counts+"]handle event: "+event);
        counts++;
        PageEvent pEv = PageEvent.fromEvent(event);
        String eventPath = "";
        ResourceResolver resolver = null;
        try {
            if (pEv != null && pEv.isLocal()) {
                Iterator<PageModification> modificationsIterator = pEv.getModifications();
                while (modificationsIterator.hasNext()) {
                    PageModification modification = modificationsIterator.next();
                    if (PageModification.ModificationType.MODIFIED.equals(modification.getType())) {
                        eventPath = modification.getPath();
                        logger.info("get attr path="+eventPath);
                        Map<String, Object> param = new HashMap<String, Object>();
                        param.put(ResourceResolverFactory.SUBSERVICE, serviceNameForLogin);


                        resolver = resolverFactory.getServiceResourceResolver(param);
                        Session session = resolver.adaptTo(Session.class);

                        PageManager pageManager = resolver.adaptTo(PageManager.class);
                        Page currentPage = pageManager.getPage(eventPath);
                        logger.info("currentPage="+currentPage);
                        Revision rev = pageManager.createRevision(currentPage);
                        logger.info("rev="+rev);

                        Resource resource = resolver.getResource(eventPath);
                    }
                }
            }






            //resolver.commit();
        }
        catch(Exception exs){
            logger.info(exs.toString());
        }finally{
            if(resolver != null && resolver.isLive()){
                resolver.close();
            }
        }



    }
}

