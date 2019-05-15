package com.ramin.core.services;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.apache.sling.event.jobs.Job;
import org.apache.sling.event.jobs.consumer.JobConsumer;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ConfigurationPolicy;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.Node;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component(
        immediate=true,
        service = {JobConsumer.class},
        name = "MyJobConsumerService",
        configurationPolicy = ConfigurationPolicy.OPTIONAL,
        property = {
         JobConsumer.PROPERTY_TOPICS+"=jobs/logger/removenode"
        }
)
public class MyJobConsumer implements JobConsumer {
    private final Logger logger = LoggerFactory.getLogger(getClass());
    private static final String logRemoveNodeName = "remNode";
    private static final String pathForLogNodes = "/var/log/removedNodes";
    private static final String serviceNameForLogin = "readService";

    @Reference
    private ResourceResolverFactory resolverFactory;


    public JobResult process(final Job job) {
        logger.info("Job for creating log node");
        String nodePath = (String)job.getProperty("nodePath");
        String nodeName = (String)job.getProperty("nodeName");
        doJob(nodePath, nodeName);
        return JobResult.OK;
    }

    public void doJob(String nodePath, String nodeName){
        Map<String, Object> param = new HashMap<String, Object>();
        param.put(ResourceResolverFactory.SUBSERVICE, serviceNameForLogin);
        ResourceResolver resolver = null;
        try {
            resolver = resolverFactory.getServiceResourceResolver(param);
            DateFormat dateFormat = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss");
            Date date = new Date();
            String strDateTimeNow = dateFormat.format(date);
            Resource resVarLog = resolver.getResource(pathForLogNodes);
            Node resVarLogNode = resVarLog.adaptTo(Node.class);
            Node newLogNode = resVarLogNode.addNode(logRemoveNodeName+"_"+strDateTimeNow,"nt:unstructured");
            logger.info("new log node="+newLogNode);
            newLogNode.setProperty("nodePath",nodePath);
            newLogNode.setProperty("nodeName",nodeName);
            resolver.commit();
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