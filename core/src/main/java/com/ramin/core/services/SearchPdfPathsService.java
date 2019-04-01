package com.ramin.core.services;

import com.day.cq.search.PredicateGroup;
import com.day.cq.search.Query;
import com.day.cq.search.QueryBuilder;
import com.day.cq.search.result.Hit;
import com.day.cq.search.result.SearchResult;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.osgi.service.component.annotations.*;
import org.osgi.service.metatype.annotations.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Component(
        immediate=true,
        service = { ISearchPaths.class},
        name = "SearchPdfPathsServiceComp",
        configurationPolicy = ConfigurationPolicy.OPTIONAL,
        property = {
                "myOwnProperty=SearchPdf"
        }
)
@Designate(ocd = SearchPdfPathsService.Config.class)
public class SearchPdfPathsService implements ISearchPaths{
    private static final Logger logger = LoggerFactory.getLogger(SearchPdfPathsService.class);


    @ObjectClassDefinition(
            name = "Configuration Name SearchPdfPathsServiceComp",
            description="Description of SearchPdfPathsServiceComp"
    )
    @interface Config{
        @AttributeDefinition(
                name = "Max paths amount size",
                description = "Max paths amount size",
                min = "1",
                max = "100",
                required = false,
                cardinality = 0
        )
        int max_size() default 5;

    }
    private Config config;


    @Activate
    @Modified
    protected void activate(Config config){
        logger.info("Activate"+" My prop max size: "+config.max_size());
        this.config=config;
    }


    @Deactivate
    protected void deactivate(Config config){
        logger.info("Deactivate"+" My prop max size: "+config.max_size());

    }


    public List<String> getPaths(Resource resource, String usedSearchPath){
        logger.info("Pdf getPaths call ");
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
                 for(int i=0; i<result.getHits().size() && i<config.max_size() ;i++){
                    Hit hit = result.getHits().get(i);
                    String path="";
                    try {
                        path = hit.getPath();
                    } catch (RepositoryException e) {
                        logger.error(e.toString());
                        break;
                    }
                    ret.add(path);
                }
        }catch(Exception ex){
                logger.error(ex.toString());
        }
        return ret;
    }
    public int getMaxAmount(){
        return config.max_size();
    }
}
