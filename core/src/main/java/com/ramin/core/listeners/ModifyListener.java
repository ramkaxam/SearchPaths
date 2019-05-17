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

import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.osgi.framework.Constants;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventConstants;
import org.osgi.service.event.EventHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.regex.Pattern;


@Component(service = EventHandler.class,
           immediate = true,
           property = {
                   Constants.SERVICE_DESCRIPTION + "=modify listener",
                   EventConstants.EVENT_TOPIC + "=com/day/cq/wcm/core/page"
           })
public class ModifyListener implements EventHandler {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    private static String observedPath = "/content/searchpaths";
    private static final String serviceNameForLogin = "modifyListenService";

    private static long counts=0;

    @Reference
    private ResourceResolverFactory resolverFactory;


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
                        final Pattern pattern = Pattern.compile("/content/searchpaths.*");
                        if (!pattern.matcher(eventPath).matches()) {
                            logger.info("invalid event path:"+eventPath);
                        }
                        else{
                            Map<String, Object> param = new HashMap<String, Object>();
                            param.put(ResourceResolverFactory.SUBSERVICE, serviceNameForLogin);
                            resolver = resolverFactory.getServiceResourceResolver(param);
                            PageManager pageManager = resolver.adaptTo(PageManager.class);
                            Page currentPage = pageManager.getPage(eventPath);
                            logger.info("currentPage="+currentPage);
                            Revision rev = pageManager.createRevision(currentPage);
                            logger.info("rev="+rev);
                        }
                    }
                }
            }
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

