/* 
 * Copyright(c) 2005 Center for E-Commerce Infrastructure Development, The
 * University of Hong Kong (HKU). All Rights Reserved.
 *
 * This software is licensed under the GNU GENERAL PUBLIC LICENSE Version 2.0 [1]
 * 
 * [1] http://www.gnu.org/licenses/old-licenses/gpl-2.0.txt
 */

package hk.hku.cecid.piazza.commons.util;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import javax.mail.Header;
import javax.mail.internet.InternetHeaders;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.soap.MimeHeader;
import javax.xml.soap.MimeHeaders;


/**
 * The Headers class represents the headers of an HTTP URL connection or of the 
 * HTTP servlet request and response.
 * 
 * @author Hugo Y. K. Lam
 *
 */
public class Headers {

    private Object request, response;

    /**
     * Creates a new instance of Headers.
     * 
     * @param connection the HTTP connection for getting or setting the headers.
     */
    public Headers(HttpURLConnection connection) {
        this.request  = connection;
        this.response = connection;
    }
    
    /**
     * Creates a new instance of Headers.
     * 
     * @param request the servlet request for getting the headers.
     * @param response the servlet response for setting the headers.
     */
    public Headers(HttpServletRequest request, HttpServletResponse response) {
        this.request = request;
        this.response = response;
    }

    /**
     * Gets the mime headers from the underlying request object.
     * 
     * @return the mime headers.
     */
    public MimeHeaders getMimeHeaders() {
        MimeHeaders headers = new MimeHeaders();
        addHeaders(headers);
        return headers;
    }

    /**
     * Gets the Internet headers from the underlying request object.
     * 
     * @return the Internet headers.
     */
    public InternetHeaders getInternetHeaders() {
        InternetHeaders headers = new InternetHeaders();
        addHeaders(headers);
        return headers;
    }
    
    /**
     * Gets the headers as an input stream from the underlying request object.
     * 
     * @return an input stream of the headers.
     */
    public InputStream getInputStreamHeaders() {
        StringWriter headers = new StringWriter();
        addHeaders(headers);
        headers.write("\r\n");
        return new ByteArrayInputStream(headers.toString().getBytes());
    }
    
    /**
     * Adds headers from the underlying request object to the given headers object. 
     * 
     * @param headers the headers object.
     */
    private void addHeaders(Object headers) {
        if (request instanceof HttpURLConnection) {
            HttpURLConnection connection = (HttpURLConnection)request;
            Map headerFields = connection.getHeaderFields();
            Iterator headerNames = headerFields.keySet().iterator();
            while (headerNames.hasNext()) {
                String headerName = (String) headerNames.next();
                Iterator headerValues = ((List)headerFields.get(headerName)).iterator();
                addHeaders(headers, headerName, ArrayUtilities.toArray(headerValues));
            }
        }
        else if (request instanceof HttpServletRequest) {
            HttpServletRequest servletRequest = (HttpServletRequest)request;
            Enumeration headerNames = servletRequest.getHeaderNames();
            while (headerNames.hasMoreElements()) {
                String headerName = (String) headerNames.nextElement();
                Enumeration headerValues = servletRequest.getHeaders(headerName);
                addHeaders(headers, headerName, ArrayUtilities.toArray(headerValues));
            }
        }
    }
    
    /**
     * Adds the given name-values header to the given headers object. 
     * 
     * @param headers the headers object.
     * @param name the header name.
     * @param values the header values.
     */
    private void addHeaders(Object headers, String name, Object[] values) {
        if (name != null) {
            for (int i=0; i<values.length; i++) {
                if (headers instanceof StringWriter) {
                    StringWriter outs = (StringWriter)headers;
                    outs.write(name+": "+values[i]+"\r\n");
                    continue;
                }
                StringTokenizer subvalues = new StringTokenizer(
                        values[i].toString(), ",");
                while (subvalues.hasMoreTokens()) {
                    String subvalue = subvalues.nextToken().trim();
                    if (headers instanceof MimeHeaders) {
                        ((MimeHeaders)headers).addHeader(name, subvalue);
                    }
                    else if (headers instanceof InternetHeaders) {
                        ((InternetHeaders)headers).addHeader(name, subvalue);
                    }
                }
            }
        }
    }
    
    /**
     * Puts the mime headers into the underlying response object.
     * 
     * @param headers the mime headers.
     */
    public void putMimeHeaders(MimeHeaders headers) {
        putHeaders(headers);
    }
    
    /**
     * Puts the Internet headers into the underlying response object.
     * 
     * @param headers the Internet headers.
     */
    public void putInternetHeaders(InternetHeaders headers) {
        putHeaders(headers);
    }

    /**
     * Puts the given headers object into the underlying response object.
     * 
     * @param headers the headers object.
     */
    private void putHeaders(Object headers) {
        if (headers instanceof MimeHeaders) {
            MimeHeaders mimeHeaders = (MimeHeaders)headers;
            for (Iterator iterator = mimeHeaders.getAllHeaders(); iterator
                    .hasNext();) {
                MimeHeader header = (MimeHeader) iterator.next();
                String[] values = mimeHeaders.getHeader(header.getName());
                putHeaders(header.getName(), values);
            }
        }
        else if (headers instanceof InternetHeaders) {
            InternetHeaders internetHeaders = (InternetHeaders)headers; 
            for (Enumeration enumeration = internetHeaders.getAllHeaders(); enumeration
                    .hasMoreElements();) {
                Header header = (Header) enumeration.nextElement();
                String[] values = internetHeaders.getHeader(header.getName());
                putHeaders(header.getName(), values);
            }
        }
    }
    
    /**
     * Puts the given name-values header into the underlying response object.
     * 
     * @param name the header name.
     * @param values the header values.
     */
    private void putHeaders(String name, String[] values) {
        StringBuffer stringbuffer = new StringBuffer();
        for (int i = 0; i < values.length;) {
            if (i != 0) {
                stringbuffer.append(',');
            }
            stringbuffer.append(values[i++]);
        }
        String value = stringbuffer.toString();
        
        if (response instanceof HttpServletResponse) {
            ((HttpServletResponse)response).setHeader(name, value);
        }
        else if (response instanceof HttpURLConnection) {
            ((HttpURLConnection)response).addRequestProperty(name, value);
        }
    }
}
