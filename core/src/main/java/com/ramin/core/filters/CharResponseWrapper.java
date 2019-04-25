package com.ramin.core.filters;

import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.wrappers.SlingHttpServletResponseWrapper;
import javax.servlet.ServletOutputStream;
import java.io.CharArrayWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class CharResponseWrapper extends SlingHttpServletResponseWrapper {
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