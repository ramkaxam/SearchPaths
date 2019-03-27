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
import com.ramin.core.services.ISearchAssetsPaths;
import com.ramin.core.services.ISearchPdfPaths;
import com.ramin.core.services.SearchPdfPathsService;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.models.annotations.Default;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.OSGiService;
import org.apache.sling.models.annotations.injectorspecific.Self;
import org.apache.sling.models.annotations.injectorspecific.SlingObject;
import org.apache.sling.settings.SlingSettingsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import java.util.*;

@Model(adaptables=Resource.class)
public class SearchPathsModel {
    public static final Logger logger = LoggerFactory.getLogger(SearchPathsModel.class);

    private String message="";


    @Inject @Named("text") @Default(values="/content")
    private String usedSearchPath;

    @Inject @Named("myselect") @Default(values="Assets")
    private String searchType;

    @OSGiService
    private ISearchPdfPaths searchPdfService;

    @OSGiService
    private ISearchAssetsPaths searchAssetsService;

    @Self
    private Resource resource;





    private List<String> pathsResult=new ArrayList<>();


    @PostConstruct
    protected void init() {

        logger.info(" doing request");
        message+="Searching in: "+usedSearchPath+"\n";
        switch (searchType){
            case "Assets":

                message+="Searching assets:\n";
                pathsResult=searchAssetsService.getPaths(resource,usedSearchPath);
                break;
            default: case "PDF":

                message+="Searching pdfs:\n";
                pathsResult=searchPdfService.getPaths(resource,usedSearchPath);
                break;



        }





    }

    public String getMessage() {
        return message;
    }


    public List<String> getPathsResult(){

        //return new ArrayList<>(Arrays.asList(new String[]{"1","2"}));

        return pathsResult;

    }
}
