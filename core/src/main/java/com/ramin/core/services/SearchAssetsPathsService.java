package com.ramin.core.services;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.osgi.service.component.annotations.*;
import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.Designate;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.jcr.Session;
import java.util.ArrayList;
import java.util.List;


@Component(
        immediate=true,
        service = { ISearchPaths.class},
        name = "SearchAssetsPathsServiceComp",
        configurationPolicy = ConfigurationPolicy.OPTIONAL,
        property = {
                "myOwnProperty=SearchAssets"
        }
)
@Designate(ocd = SearchAssetsPathsService.Config.class)
public class SearchAssetsPathsService implements ISearchPaths{
    private static final Logger logger = LoggerFactory.getLogger(SearchAssetsPathsService.class);
    private static final String templSqlStatement="SELECT * FROM [dam:Asset] as s WHERE ISDESCENDANTNODE(s,'%s')"+"AND (NAME() LIKE '%%.jpg' OR NAME() LIKE '%%.jpeg')";

    @ObjectClassDefinition(
            name = "Configuration Name SearchAssetsPathsService",
            description="Description of SearchAssetsPathsService"
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
        logger.info("assets getPaths call");
        ArrayList<String> ret=new ArrayList<>();
        ResourceResolver resolver = resource.getResourceResolver();
        try {
            Session session = resolver.adaptTo(Session.class);
            javax.jcr.query.QueryManager queryManager = session.getWorkspace().getQueryManager();
            String statRes=String.format(templSqlStatement,usedSearchPath);
            logger.info("JCR Execute query:"+statRes);
            javax.jcr.query.Query query = queryManager.createQuery(statRes,"JCR-SQL2");
            javax.jcr.query.QueryResult result = query.execute();
            javax.jcr.NodeIterator nodeIter = result.getNodes();
            int i=0;
            while ( nodeIter.hasNext() && i<config.max_size() ){
                javax.jcr.Node node = nodeIter.nextNode();
                ret.add(node.getPath());
                i++;
            }
        }catch (Exception ex){
            logger.error(ex.toString());
        }
        return ret;
    }
}
