package com.ramin.core.services;

import com.day.cq.search.QueryBuilder;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;

import javax.jcr.Session;
import java.util.List;

public interface ISearchPdfPaths {
    List<String> getPaths(Resource resource, String usedSearchPath);
}
