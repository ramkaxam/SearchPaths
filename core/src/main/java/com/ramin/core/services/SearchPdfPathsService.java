package com.ramin.core.services;
import com.day.cq.search.PredicateGroup;
import com.day.cq.search.Query;
import com.day.cq.search.QueryBuilder;
import com.day.cq.search.result.Hit;
import com.day.cq.search.result.SearchResult;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.models.annotations.injectorspecific.OSGiService;
import org.apache.sling.models.annotations.injectorspecific.SlingObject;
import org.osgi.service.component.annotations.*;

import javax.inject.Inject;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import java.util.HashMap;
import java.util.Map;



@Component(
        immediate=true,
        service = { ISearchPdfPaths.class },
        name = "SearchPdfPathsServiceComp",
        property = {
                "myOwnProperty=SearchPdf"
        }
)
public class SearchPdfPathsService implements ISearchPdfPaths{
    QueryBuilder builder;

    ResourceResolver resolver;



    public void configure(QueryBuilder builder, ResourceResolver resolver){
        this.builder=builder;

        this.resolver=resolver;
    }







    public String getPaths(String usedSearchPath){
        Session session;
        String message="";



        session = resolver.adaptTo(Session.class);
        Map<String, String> map = new HashMap<String, String>();

        map.put("path", usedSearchPath);
        map.put("nodename", "*.pdf");

        Query query = builder.createQuery(PredicateGroup.create(map), session);

        SearchResult result = query.getResult();
        int hitsPerPage = result.getHits().size();

        if(hitsPerPage==0){
            message="No data";
        }else{

            try{
           // message+=" hits="+hitsPerPage;

            for (Hit hit : result.getHits()) {
                String path="";
                try {
                    path = hit.getPath();
                } catch (RepositoryException e) {
                    message+="[!repoexceptopn!]";
                }
                message+=path+"\n";


            }
        }catch(Exception ex){
                message+=ex;
            }
        }






        return message;
    }

}
