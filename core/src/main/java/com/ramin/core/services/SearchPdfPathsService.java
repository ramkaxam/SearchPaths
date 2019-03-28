package com.ramin.core.services;
import com.day.cq.search.PredicateGroup;
import com.day.cq.search.Query;
import com.day.cq.search.QueryBuilder;
import com.day.cq.search.result.Hit;
import com.day.cq.search.result.SearchResult;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.models.annotations.injectorspecific.OSGiService;
import org.apache.sling.models.annotations.injectorspecific.SlingObject;
import org.osgi.service.component.annotations.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;



@Component(
        immediate=true,
        service = { /*ISearchPdfPaths.class*/ ISearchPaths.class },
        name = "SearchPdfPathsServiceComp",
        property = {
                "myOwnProperty=SearchPdf"
        }
)

public class SearchPdfPathsService implements ISearchPaths{
    private static final Logger logger = LoggerFactory.getLogger(SearchPdfPathsService.class);

    public List<String> getPaths(Resource resource, String usedSearchPath){
        logger.info("Pdf calling "+SearchPdfPathsService.class.getName());
        ArrayList<String> ret=new ArrayList<>();
        ResourceResolver resolver = resource.getResourceResolver();
        QueryBuilder builder = resolver.adaptTo(QueryBuilder.class);
        Session session = resolver.adaptTo(Session.class);

        Map<String, String> map = new HashMap<String, String>();
        map.put("path", usedSearchPath);
        map.put("nodename", "*.pdf");

        Query query = builder.createQuery(PredicateGroup.create(map), session);

        SearchResult result = query.getResult();


        try{
                for (Hit hit : result.getHits()) {
                    String path="";
                    try {
                        path = hit.getPath();
                    } catch (RepositoryException e) {
                        logger.error(e.toString());
                    }
                    ret.add(path);


                }


        }catch(Exception ex){
                logger.error(ex.toString());
        }


        return ret;

    }
}
