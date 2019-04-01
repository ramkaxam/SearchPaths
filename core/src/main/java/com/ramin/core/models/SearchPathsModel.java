package com.ramin.core.models;

import com.ramin.core.services.*;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.Default;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.OSGiService;
import org.apache.sling.models.annotations.injectorspecific.Self;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;
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
        logger.info("doing request");
        switch (selectedTypeForSearch){
            case "Assets":
                pathsResult=searchAssetsService.getPaths(resource,usedPathForSearch);
                break;
            default: case "PDF":
                pathsResult=searchPdfService.getPaths(resource,usedPathForSearch);
                break;
        }
    }

    public List<String> getPathsResult(){ return pathsResult; }

    public String getUsedPathForSearch() {
        return usedPathForSearch;
    }

    public String getSelectedTypeForSearch() {
        return selectedTypeForSearch;
    }

    public int getMaxAmount(){
        return selectedTypeForSearch.equals("Assets") ? searchAssetsService.getMaxAmount() : searchPdfService.getMaxAmount();
    }
}
