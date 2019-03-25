package com.ramin.core.services;

import com.day.cq.search.QueryBuilder;
import org.apache.sling.api.resource.ResourceResolver;

import javax.jcr.Session;

public interface ISearchPdfPaths {
    void configure(QueryBuilder builder, Session session, ResourceResolver resolver);
    String getPaths(String usedSearchPath);
}
