package com.ramin.core.listeners;


import org.apache.sling.api.resource.*;
import org.apache.sling.event.jobs.JobManager;
import org.apache.sling.jcr.api.SlingRepository;
import org.apache.sling.models.annotations.injectorspecific.Self;
import org.osgi.service.component.annotations.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.observation.Event;
import javax.jcr.observation.EventIterator;
import javax.jcr.observation.EventListener;
import javax.jcr.observation.ObservationManager;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


@Component(
        immediate=true,
        service = { EventListener.class},
        name = "MyRemoveRemoveListener",
        property = {
                "myOwnProperty=SEventListener"
        }
)
public class RemoveListener implements EventListener {
    @Reference
    private JobManager jobManager;

    @Reference
    private SlingRepository repository;

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final int events = Event.NODE_REMOVED;
    private final String absPath = "/content/searchpaths";
    private final boolean isDeep = true;
    private final boolean noLocal = false;
    private final String[] uuids = null;
    private final String[] nodeTypes = null;
    private Session observationSession = null;


    public void onEvent(final EventIterator events) {
        logger.info("New events!");
        while(events.hasNext()){
            try{
                Event event = events.nextEvent();
                logger.info("Event eventPath="+event.getPath()+" type="+event.getType()+" info="+event.getInfo()+" ");
                String path=event.getPath();
                String[] parts = path.split("/");
                String name = parts[parts.length-1];
                String realPath = getRealPath(path);
                addJob(name, realPath);
            }catch (RepositoryException ex){
                logger.info(ex.toString());

            }
        }

    }


    private String getRealPath(String path){
        String realPath = "";
        int len = path.length();
        int i = 0;
        for(i=len-1;!(""+path.charAt(i)).equals("/");i--);
        realPath = path.substring(0,i+1);
        return realPath;
    }


    private void addJob(String nodeName, String nodePath){
        final Map<String, Object> props = new HashMap<String, Object>();
        props.put("nodePath", nodePath);
        props.put("nodeName", nodeName);
        jobManager.addJob("jobs/logger/removenode", props);
    }


    @Activate
    @Modified
    protected void activate() throws RepositoryException {
        logger.info("Activate RemoveListener");
        observationSession = repository.loginService("eventService",null);
        final ObservationManager observationManager  = observationSession.getWorkspace().getObservationManager();
        observationManager.addEventListener(this, events, absPath, isDeep,
                uuids, nodeTypes, noLocal);
    }


    @Deactivate
    protected void deactivate() throws RepositoryException{
        logger.info("Deactivate RemoveListener");
        try {

            final ObservationManager observationManager = observationSession.getWorkspace().getObservationManager();

            if (observationManager != null) {

                observationManager.removeEventListener(this);
            }
        } finally {

            if (observationSession != null) {
                observationSession.logout();
            }
        }
    }





}
