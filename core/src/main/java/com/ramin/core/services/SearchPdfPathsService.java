package com.ramin.core.services;
import org.osgi.service.component.annotations.*;

@Component(
        immediate=true,
        service = { ISearchPdfPaths.class },
        name = "SearchPdfPathsServiceComp",
        property = {
                "myOwnProperty=Ramin"
        }
)
public class SearchPdfPathsService implements ISearchPdfPaths{
    public String getPaths(){
        return "/one /two";
    }
}
