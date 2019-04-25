package com.ramin.core.filters;

import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.engine.EngineConstants;
import org.osgi.framework.Constants;
import org.osgi.service.component.annotations.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.servlet.*;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.Enumeration;

@Component(service = Filter.class,
           name = "MensFilter",
           property = {
                   Constants.SERVICE_DESCRIPTION + "=Mens filter",
                   EngineConstants.SLING_FILTER_SCOPE + "=" + EngineConstants.FILTER_SCOPE_REQUEST,
                   Constants.SERVICE_RANKING + ":Integer=-700",
                   "sling.filter.pattern=/content/.*men.*.html"
           })
public class MensFilter implements Filter {
    private FilterConfig filterConfig;
    private final Logger logger = LoggerFactory.getLogger(getClass());
    private final static String mensConfigCustom="no usable run mode for it";
    private final static String sourceForReplace="men";
    private final static String propertyNameReplacement="mensConfig";

    @Override
    public void doFilter(final ServletRequest request, final ServletResponse response, final FilterChain filterChain)
            throws IOException, ServletException {
        logger.info("doFilter");
        ServletResponse oldResponse = new CharResponseWrapper((SlingHttpServletResponse) response);
        filterChain.doFilter(request, oldResponse);
        String oldResponseString = oldResponse.toString();
        String mensConfigVal=filterConfig.getInitParameter(propertyNameReplacement);
        String replacedText = oldResponseString.replace(sourceForReplace, mensConfigVal==null ? mensConfigCustom : mensConfigVal);
        response.resetBuffer();
        Writer resultResponseWriter=response.getWriter();
        ((PrintWriter) resultResponseWriter).println(replacedText);
    }

    @Override
    public void init(FilterConfig filterConfig) {
        this.filterConfig=filterConfig;
        Enumeration en = filterConfig.getInitParameterNames();
        while(en.hasMoreElements()){
            logger.info("mens filter init params: "+(String)en.nextElement());
        }
    }

    @Override
    public void destroy() {
    }
}
