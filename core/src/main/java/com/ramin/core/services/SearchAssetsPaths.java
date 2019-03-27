package com.ramin.core.services;

import com.day.cq.search.QueryBuilder;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.osgi.service.component.annotations.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.Session;
import java.util.ArrayList;
import java.util.List;


@Component(
        immediate=true,
        service = { ISearchAssetsPaths.class },
        name = "SearchAssetsPathsServiceComp",
        property = {
                "myOwnProperty=SearchAssets"
        }
)
public class SearchAssetsPaths implements ISearchAssetsPaths, ISearchPaths{
    private static final Logger logger = LoggerFactory.getLogger(SearchAssetsPaths.class);

    public List<String> getPaths(Resource resource, String usedSearchPath){
        logger.info("assets call");
        ArrayList<String> ret=new ArrayList<>();

        ResourceResolver resolver = resource.getResourceResolver();



        try {
            Session session = resolver.adaptTo(Session.class);
            javax.jcr.query.QueryManager queryManager = session.getWorkspace().getQueryManager();

            String sqlStatement="SELECT * FROM [dam:Asset] as s"+" WHERE ISDESCENDANTNODE(s,\'"+usedSearchPath+"\')";
            javax.jcr.query.Query query = queryManager.createQuery(sqlStatement,"JCR-SQL2");
            javax.jcr.query.QueryResult result = query.execute();

            javax.jcr.NodeIterator nodeIter = result.getNodes();

            while ( nodeIter.hasNext() ){
                javax.jcr.Node node = nodeIter.nextNode();
                ret.add(node.getPath());


            }


        }catch (Exception ex){
            logger.error(ex.toString());

        }

        return ret;

    }

}
