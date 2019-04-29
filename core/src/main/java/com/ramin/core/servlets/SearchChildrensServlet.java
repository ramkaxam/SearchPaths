package com.ramin.core.servlets;

import com.day.cq.search.PredicateGroup;
import com.day.cq.search.Query;
import com.day.cq.search.QueryBuilder;
import com.day.cq.search.result.Hit;
import com.day.cq.search.result.SearchResult;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.servlets.HttpConstants;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.commons.json.JSONObject;
import org.osgi.framework.Constants;
import org.osgi.service.component.annotations.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.servlet.Servlet;
import javax.servlet.ServletException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@Component(service=Servlet.class,
           name="ChildrenPages",
           property={
                   Constants.SERVICE_DESCRIPTION + "=ChildrenPages",
                   "sling.servlet.methods=" + HttpConstants.METHOD_GET,
                   "sling.servlet.resourceTypes="+ "searchpaths/components/structure/page",
                   "sling.servlet.selectors=" + "listchildren"
           })
public class SearchChildrensServlet extends SlingSafeMethodsServlet {
    private static final Logger logger = LoggerFactory.getLogger(SearchChildrensServlet.class);

    private static final long serialVersionUid = 1L;

    @Override
    protected void doGet(final SlingHttpServletRequest req,
            final SlingHttpServletResponse resp) throws ServletException, IOException {
        final Resource resource = req.getResource();
        resp.setContentType("text/plain");
        ResourceResolver resolver = resource.getResourceResolver();
        QueryBuilder builder = resolver.adaptTo(QueryBuilder.class);
        Session session = resolver.adaptTo(Session.class);
        Map<String, String> map = new HashMap<String, String>();
        map.put("path", resource.getParent().getPath());
        map.put("type", "cq:Page");
        Query query = builder.createQuery(PredicateGroup.create(map), session);
        SearchResult result = query.getResult();
        ArrayList<JSONObject> arrJson=new ArrayList<>();
        JSONObject retArrPages = new JSONObject();
        try{
            for(int i=0; i<result.getHits().size() ;i++){
                Hit hit = result.getHits().get(i);
                String path="";
                String obtainedTitle="";
                String pageTempl="";
                try {
                    path = hit.getPath();
                    obtainedTitle = hit.getResource().getChild("jcr:content").adaptTo(ValueMap.class).get("jcr:title").toString();
                    pageTempl = hit.getResource().getChild("jcr:content").adaptTo(ValueMap.class).get("cq:template").toString();
                    JSONObject onePage = new JSONObject();
                    onePage.put("path", path);
                    onePage.put("title", obtainedTitle);
                    onePage.put("template", pageTempl);
                    arrJson.add(onePage);
                } catch (RepositoryException e) {
                    logger.error(e.toString());
                    break;
                }
                retArrPages.put("childArr",arrJson);
            }
        }catch(Exception ex){
            logger.error(ex.toString());
        }
        resp.getWriter().write(retArrPages.toString());
    }
}
