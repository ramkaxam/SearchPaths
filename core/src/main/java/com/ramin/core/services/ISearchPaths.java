package com.ramin.core.services;

import org.apache.sling.api.resource.Resource;

import java.util.List;

public interface ISearchPaths {
    List<String> getPaths(Resource resource, String usedSearchPath);
    int getMaxAmount();
}
