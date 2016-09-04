/* 
 * Copyright(c) 2005 Center for E-Commerce Infrastructure Development, The
 * University of Hong Kong (HKU). All Rights Reserved.
 *
 * This software is licensed under the GNU GENERAL PUBLIC LICENSE Version 2.0 [1]
 * 
 * [1] http://www.gnu.org/licenses/old-licenses/gpl-2.0.txt
 */

package hk.hku.cecid.piazza.commons.pagelet;

import hk.hku.cecid.piazza.commons.io.IOHandler;
import hk.hku.cecid.piazza.commons.servlet.RequestListenerException;
import hk.hku.cecid.piazza.commons.servlet.http.HttpRequestAdaptor;

import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * HttpPageletAdaptor is an HTTP request adaptor which generates a page from a 
 * template pagelet. A template pagelet may contain the content, or part of the 
 * content, of the page to be generated. It also describes the layout of other 
 * pagelets in the page.
 * <p>
 * The tag in the template which declares a pagelet should follow the convention 
 * stated below:
 * <pre>
 *       &lt;!-- template-[pagelet-id] --&gt;
 * </pre>
 * <p>  
 * By default, there are two generation modes of the page.
 * <ol> 
 * <li> full - Generate the page according to the template pre-defined 
 *             by this adaptor. This is the default generation mode.
 * <li> raw  - Generate the page according to the dynamically specified pagelet.
 * </ol>
 * <p>
 * Note: All the pagelets, including the template, must be saved in UTF-8 encoding.
 * 
 * @see Pagelet
 * @see PageletStore
 * @see Template
 * 
 * @author Hugo Y. K. Lam
 *  
 */
public abstract class HttpPageletAdaptor extends HttpRequestAdaptor {

    /**
     * The name of the request parameter which specifies a pagelet ID.
     * This parameter is named 'pagelet'. 
     */
    protected static final String REQ_PARAM_PAGELET   = "pagelet";

    /**
     * The name of the request parameter which specifies the generation mode.
     * This parameter is named 'mode'. 
     */
    protected static final String REQ_PARAM_PAGEMODE = "mode";
    
    /**
     * The name of the request parameter which specifies an action.
     * This parameter is named 'action'. 
     */
    protected static final String REQ_PARAM_ACTION = "action";
    
    /**
     * Processes the page request.
     * This method determines if the generation mode is "raw". If not, it invokes 
     * processTemplate() to complete the rest of the generation.
     * 
     * @see #processTemplate(HttpServletRequest,HttpServletResponse)
     * @see hk.hku.cecid.piazza.commons.servlet.http.HttpRequestListener#processRequest(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    public String processRequest(HttpServletRequest request,
            HttpServletResponse response) throws RequestListenerException {
        try {
            String pageMode  = request.getParameter(REQ_PARAM_PAGEMODE);
            String pageletId =request.getParameter(REQ_PARAM_PAGELET);
            if ("raw".equalsIgnoreCase(pageMode) || pageletId!=null && pageMode==null) {
                Pagelet rawPage = getPagelet(pageletId);
                if (rawPage != null) {
                    response.setContentType(null);
                    IOHandler.pipe(rawPage.openStream(), response.getOutputStream());
                }
            }
            else {
                processTemplate(request, response);
            }
        }
        catch (Exception e) {
            throw new RequestListenerException("Error occurred in generating page", e);
        }
        return null;
    }
    
    /**
     * Processes the template of the generating page.
     * This method invokes getTemplate() to retrieve the template pagelet. It 
     * then generates the page according to the pagelet. The content of the 
     * template pagelet will be parsed according to the following alogirthm:
     * <ul>
     * <li>When it encounters any text other than a template tag, it invokes 
     *     processText() to generate the content.
     * <li>When it encounters a template tag in the template, it invokes 
     *     processPagelet() to generate the content.
     * <li>When it encounters any errors while invoking processPagelet(), it will
     *     invokes processError() to generate the content.
     * </ul>
     * <p>
     * 
     * @param request the servlet request.
     * @param response the servlet response.
     * @throws RequestListenerException if unable to process the template.
     * 
     * @see #getTemplate(HttpServletRequest)
     * @see #processText(TemplateElement, String, HttpServletRequest, HttpServletResponse)
     * @see #processPagelet(TemplateElement, Pagelet, HttpServletRequest, HttpServletResponse)
     * @see #processError(TemplateElement, Throwable, HttpServletRequest, HttpServletResponse)
     */
    protected void processTemplate(HttpServletRequest request,
            HttpServletResponse response) throws RequestListenerException {
        try {
            Pagelet tempPage = getTemplate(request);
            String s = IOHandler.readString(tempPage.openStream(), 
                                Charset.forName("UTF-8"));
            Template template = new Template(s);
            template.parse();
            
            while (template.hasMoreElements()) {
                TemplateElement element = template.nextElement();
                if (element.isText()) {
                    processText(element, element.getText(), request, response);
                }
                else {
                    Pagelet pagelet = getPagelet(element.getName());
                    try {
                        processPagelet(element, pagelet, request, response);
                    }
                    catch (Throwable error) {
                        processError(element, error, request, response);                    
                    }
                } 
            }
        }
        catch (Exception e) {
            throw new RequestListenerException("Error in processing template", e);
        }
    }

    /**
     * Processes the text part of the generating page by simply generating the 
     * text to the output.
     *  
     * @param element the template element which represents the text.
     * @param text the text to be processed.
     * @param request the servlet request.
     * @param response the servlet response.
     * @throws RequestListenerException if unable to process the text.
     * 
     * @see #processTemplate(HttpServletRequest, HttpServletResponse)
     */
    protected void processText(TemplateElement element, String text, HttpServletRequest request,
            HttpServletResponse response) throws RequestListenerException {
        try {
            IOHandler.writeString(text, response.getWriter());
        }
        catch (IOException e) {
            throw new RequestListenerException("Error in processing text", e);
        }
    }
    
    /**
     * Processes the pagelet of the generating page by reading the content from 
     * the pagelet and generates it to the output.
     *   
     * @param element the template element which represents the pagelet.
     * @param pagelet the pagelet to be processed.
     * @param request the servlet request.
     * @param response the servlet response.
     * @throws RequestListenerException if unable to process the pagelet.
     * 
     * @see #processTemplate(HttpServletRequest, HttpServletResponse)
     */

    protected void processPagelet(TemplateElement element, Pagelet pagelet, HttpServletRequest request,
            HttpServletResponse response) throws RequestListenerException {
        try {
            if (pagelet != null) {
                InputStreamReader reader = new InputStreamReader(
                        pagelet.openStream(), Charset.forName("UTF-8"));
                IOHandler.pipe(reader, response.getWriter());
                reader.close();
                reader = null;
            }
        }
        catch (Exception e) {
            throw new RequestListenerException("Error in processing pagelet", e);
        }
    }
    
    /**
     * Processes the error generated when processing a pagelet.
     * 
     * @param element the template element which represents the pagelet.
     * @param error the error generated by the pagelet process.
     * @param request the servlet request.
     * @param response the servlet response.
     * @throws RequestListenerException if unable to process the pagelet error.
     * 
     * @see #processTemplate(HttpServletRequest, HttpServletResponse)
     */
    protected void processError(TemplateElement element, Throwable error, HttpServletRequest request, 
            HttpServletResponse response) throws RequestListenerException {
        try {
            processPagelet(element, getErrorPagelet(request), request, response);
        }
        catch (Exception e) {
            throw new RequestListenerException("Error in processing pagelet error: "+e, error);
        }
    }
    
    /**
     * Gets the template pagelet. 
     * This method invokes getPagelet() with a parameter "&pagelet-template".
     * 
     * @param request the servlet request.
     * @return the template pagelet.
     */
    protected Pagelet getTemplate(HttpServletRequest request) {
        return getPagelet("&pagelet-template");
    }

    /**
     * Gets the error pagelet. 
     * This method invokes getPagelet() with a parameter "&pagelet-error".
     * 
     * @param request the servlet request.
     * @return the template pagelet.
     */
    protected Pagelet getErrorPagelet(HttpServletRequest request) {
        return getPagelet("&pagelet-error");
    }

    /**
     * Gets the pagelet from the underlying pagelet store.
     * If the specified ID is preceded by an ampersand, it indicates the pagelet
     * id should be firstly treated as a listener parameter name which has a 
     * value of the target pagelet ID. In this case, if there is a corresponding
     * listener parameter, its value will be used as the target pagelet ID. 
     * Otherwise, the specified id will be matched against the underlying pagelet 
     * store as if there is no ampersand indicator.  
     * 
     * @param id the pagelet ID.
     * @return the corresponding pagelet or null if not found.
     */
    protected Pagelet getPagelet(String id) {
        if (id != null && id.startsWith("&")) {
            id = id.substring(1);
            String refId = getParameters().getProperty(id);
            if (refId != null) {
                id = refId;
            }
        }
        return getPageletStore().getPagelet(id);
    }

    /**
     * Gets the pagelet store which holds all the registered pagelets of this
     * adaptor.
     * 
     * @return a pagelet store.
     */
    protected abstract PageletStore getPageletStore();
}