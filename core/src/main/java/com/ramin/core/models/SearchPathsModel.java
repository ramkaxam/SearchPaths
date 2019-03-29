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
import com.ramin.core.services.*;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.models.annotations.Default;
import org.apache.sling.models.annotations.Filter;
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

    @Inject @Named("usedPathForSearch") @Default(values="/content/dam")
    private String usedPathForSearch;

    @Inject @Named("selectedTypeForSearch") @Default(values="Assets")
    private String selectedTypeForSearch;

    @OSGiService(filter="(component.name=SearchPdfPathsServiceComp)")
    private ISearchPaths searchPdfService;

    @OSGiService(filter="(component.name=SearchAssetsPathsServiceComp)")
    private ISearchPaths searchAssetsService;

    @Self
    private Resource resource;

    private List<String> pathsResult=new ArrayList<>();

    @PostConstruct
    protected void init() {
        logger.info(" doing request");
        switch (selectedTypeForSearch){
            case "Assets":
                pathsResult=searchAssetsService.getPaths(resource,usedPathForSearch);
                break;
            default: case "PDF":
                pathsResult=searchPdfService.getPaths(resource,usedPathForSearch);
                break;
        }
    }



    public List<String> getPathsResult(){
        return pathsResult;

    }

    public String getUsedPathForSearch() {
        return usedPathForSearch;
    }

    public String getSelectedTypeForSearch() {
        return selectedTypeForSearch;
    }

}
