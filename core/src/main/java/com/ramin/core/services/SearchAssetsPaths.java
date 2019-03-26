package com.ramin.core.services;

import org.apache.sling.api.resource.ResourceResolver;
import org.osgi.service.component.annotations.Component;

import javax.jcr.Session;


@Component(
        immediate=true,
        service = { ISearchAssetsPaths.class },
        name = "SearchAssetsPathsServiceComp",
        property = {
                "myOwnProperty=SearchAssets"
        }
)
public class SearchAssetsPaths implements ISearchAssetsPaths{

    ResourceResolver resolver;
    public void configure(ResourceResolver resolver){

        this.resolver=resolver;

    }
    public  String getPaths(String usedSearchPath){
        String message="";
        Session session=null;


        try {
            session = resolver.adaptTo(Session.class);
            javax.jcr.query.QueryManager queryManager = session.getWorkspace().getQueryManager();


            String sqlStatement="SELECT * FROM [dam:Asset] as s"+" WHERE ISDESCENDANTNODE(s,\'"+usedSearchPath+"\')";

            javax.jcr.query.Query query = queryManager.createQuery(sqlStatement,"JCR-SQL2");


            javax.jcr.query.QueryResult result = query.execute();

            javax.jcr.NodeIterator nodeIter = result.getNodes();
            if(nodeIter.getSize()==0){
                message="No data";
            }
            while ( nodeIter.hasNext() ){
                javax.jcr.Node node = nodeIter.nextNode();
                message+=""+node.getPath()+"\n";

            }



        }catch (Exception ex){
            ex.printStackTrace();
            message+="exception get paths "+ex;

        }
        return message;
    }

}
