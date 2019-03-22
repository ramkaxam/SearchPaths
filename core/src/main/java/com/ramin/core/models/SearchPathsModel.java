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
package com.ramin.core.models;

import com.day.cq.search.PredicateGroup;
import com.day.cq.search.Query;
import com.day.cq.search.QueryBuilder;
import com.day.cq.search.result.Hit;
import com.day.cq.search.result.SearchResult;
import com.ramin.core.services.ISearchPdfPaths;
import com.ramin.core.services.SearchPdfPathsService;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.models.annotations.Default;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.OSGiService;
import org.apache.sling.models.annotations.injectorspecific.SlingObject;
import org.apache.sling.settings.SlingSettingsService;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import java.util.HashMap;
import java.util.Map;

@Model(adaptables=Resource.class)
public class SearchPathsModel {

    @Inject
    private SlingSettingsService settings;

    @Inject @Named("sling:resourceType") @Default(values="No resourceType")
    protected String resourceType;

    private String message;


    @Inject @Named("text") @Default(values="Prop is not setted")
    private String prop;

    @Inject @Named("myselect") @Default(values="Prop is not setted")
    private String searchType;

    @OSGiService
    private ISearchPdfPaths searchService;


    @SlingObject
    private ResourceResolver resolver;

    @Inject
    QueryBuilder builder;

    Session session;

    @PostConstruct
    protected void init() {
        message = "\tSearch Paths Model!\n";
        message += "\tText from dialog: " + prop + "\n";
        message += "\tSearch type: " + searchType + "\n";

        if(searchService==null){
        message += "\tSearch result: " +"no injected" + "\n";}else
            message += "\tSearch result: " +searchService.getPaths(builder, session, resolver) + "\n";


        message += "\tThis is instance: " + settings.getSlingId() + "\n";
        message += "\tResource type is: " + resourceType + "\n";



//        session = resolver.adaptTo(Session.class);
//        Map<String, String> map = new HashMap<String, String>();
//
//        map.put("path", "/content/searchpaths");
//        map.put("type", "cq:Page");
//
//        Query query = builder.createQuery(PredicateGroup.create(map), session);
//
//        SearchResult result = query.getResult();
//        int hitsPerPage = result.getHits().size();
//
//        message+=" hits="+hitsPerPage;
//
//        for (Hit hit : result.getHits()) {
//            String path="";
//            try {
//                path = hit.getPath();
//            } catch (RepositoryException e) {
//                message+="[!repoexceptopn!]";
//            }
//            message+=" "+path+"\n";
//
//
//        }

    }

    public String getMessage() {
        return message;
    }
}
