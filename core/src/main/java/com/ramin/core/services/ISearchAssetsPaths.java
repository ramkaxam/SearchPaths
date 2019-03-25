package com.ramin.core.services;

import com.day.cq.search.QueryBuilder;
import org.apache.sling.api.resource.ResourceResolver;

public interface ISearchAssetsPaths {
    void configure(ResourceResolver resolver);
    String getPaths(String usedSearchPath);
}
