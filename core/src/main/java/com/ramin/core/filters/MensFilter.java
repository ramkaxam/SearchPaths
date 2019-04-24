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
package com.ramin.core.filters;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.wrappers.SlingHttpServletResponseWrapper;
import org.apache.sling.engine.EngineConstants;
import org.osgi.framework.Constants;
import org.osgi.service.component.annotations.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.CharArrayWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;

@Component(service = Filter.class,
           property = {
                   Constants.SERVICE_DESCRIPTION + "=Mens filter",
                   EngineConstants.SLING_FILTER_SCOPE + "=" + EngineConstants.FILTER_SCOPE_REQUEST,
                   Constants.SERVICE_RANKING + ":Integer=-700",
                   "sling.filter.pattern=/content/.*"

           })
public class MensFilter implements Filter {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    public void doFilter(final ServletRequest request, final ServletResponse response, final FilterChain filterChain)
            throws IOException, ServletException {
        final SlingHttpServletRequest slingRequest = (SlingHttpServletRequest) request;

        ServletResponse oldResponse = new CharResponseWrapper((SlingHttpServletResponse) response);
        filterChain.doFilter(request, oldResponse);
        String oldResponseString = oldResponse.toString();

        String replacedText = oldResponseString.replace("mens","mensauth");



        response.resetBuffer();
        Writer resultResponseWriter=response.getWriter();

        ((PrintWriter) resultResponseWriter).println(replacedText);


        logger.info("Ramin here222");


    }

    @Override
    public void init(FilterConfig filterConfig) {
    }

    @Override
    public void destroy() {
    }

}

class CharResponseWrapper extends SlingHttpServletResponseWrapper {
    protected CharArrayWriter charWriter;

    protected PrintWriter writer;

    protected boolean getOutputStreamCalled;

    protected boolean getWriterCalled;

    public CharResponseWrapper(SlingHttpServletResponse response) {
        super(response);

        charWriter = new CharArrayWriter();
    }

    public ServletOutputStream getOutputStream() throws IOException {
        if (getWriterCalled) {
            throw new IllegalStateException("getWriter already called");
        }

        getOutputStreamCalled = true;
        return super.getOutputStream();
    }

    public PrintWriter getWriter() throws IOException {
        if (writer != null) {
            return writer;
        }
        if (getOutputStreamCalled) {
            throw new IllegalStateException("getOutputStream already called");
        }
        getWriterCalled = true;
        writer = new PrintWriter(charWriter);
        return writer;
    }

    public String toString() {
        String s = null;

        if (writer != null) {
            s = charWriter.toString();
        }
        return s;
    }
}