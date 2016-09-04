/* 
 * Copyright(c) 2005 Center for E-Commerce Infrastructure Development, The
 * University of Hong Kong (HKU). All Rights Reserved.
 *
 * This software is licensed under the GNU GENERAL PUBLIC LICENSE Version 2.0 [1]
 * 
 * [1] http://www.gnu.org/licenses/old-licenses/gpl-2.0.txt
 */

package hk.hku.cecid.piazza.commons.pagelet.xslt;

import hk.hku.cecid.piazza.commons.pagelet.Pagelet;
import hk.hku.cecid.piazza.commons.pagelet.TemplateElement;
import hk.hku.cecid.piazza.commons.servlet.RequestListenerException;

import javax.servlet.http.HttpServletRequest;
import javax.xml.transform.Source;

/**
 * BorderLayoutPageletAdaptor is an HTTP-XSLT pagelet adaptor which supports a 
 * border layout of pagelets.
 * <p>
 * A border layout can be divided into five regions:
 * <ol>
 * <li>north
 * <li>east
 * <li>west
 * <li>south
 * <li>center
 * </ol>
 * <p>
 * Each region maps to a pagelet which has an ID same as the region's name.
 *
 * @author Hugo Y. K. Lam
 *  
 */
public abstract class BorderLayoutPageletAdaptor extends HttpXsltPageletAdaptor {
    
    /**
     * Determines the border layout region that the given template element 
     * refers to and invokes the corresponding method for retrieving the
     * transformation source.  
     *
     * @see #getNorthSource(HttpServletRequest)
     * @see #getEastSource(HttpServletRequest)
     * @see #getWestSource(HttpServletRequest)
     * @see #getSouthSource(HttpServletRequest) 
     * @see hk.hku.cecid.piazza.commons.pagelet.xslt.HttpXsltPageletAdaptor#getPageletSource(hk.hku.cecid.piazza.commons.pagelet.TemplateElement, hk.hku.cecid.piazza.commons.pagelet.Pagelet, javax.servlet.http.HttpServletRequest)
     */
    protected Source getPageletSource(TemplateElement element, Pagelet pagelet,
            HttpServletRequest request) throws RequestListenerException {
        String elementName = element.getName();
        if (elementName != null && elementName.startsWith("&")) {
            elementName = elementName.substring(1); 
        }
        if ("north".equalsIgnoreCase(elementName)) {
            return getNorthSource(request);
        }
        else if ("east".equalsIgnoreCase(elementName)) {
            return getEastSource(request);
        }
        else if ("west".equalsIgnoreCase(elementName)) {
            return getWestSource(request);
        }
        else if ("south".equalsIgnoreCase(elementName)) {
            return getSouthSource(request);
        }
        else if ("center".equalsIgnoreCase(elementName)) {
            return getCenterSource(request);
        }
        else {
            return super.getPageletSource(element, pagelet, request);
        }
    }

    /**
     * Gets the transformation source for the center region.
     * 
     * @param request the servlet request.
     * @return the transformation source or null by default.
     */
    protected Source getCenterSource(HttpServletRequest request) {
        return null;
    }

    /**
     * Gets the transformation source for the south region.
     * 
     * @param request the servlet request.
     * @return the transformation source or null by default.
     */
    protected Source getSouthSource(HttpServletRequest request) {
        return null;
    }

    /**
     * Gets the transformation source for the west region.
     * 
     * @param request the servlet request.
     * @return the transformation source or null by default.
     */
    protected Source getWestSource(HttpServletRequest request) {
        return null;
    }

    /**
     * Gets the transformation source for the east region.
     * 
     * @param request the servlet request.
     * @return the transformation source or null by default.
     */
    protected Source getEastSource(HttpServletRequest request) {
        return null;
    }

    /**
     * Gets the transformation source for the north region.
     * 
     * @param request the servlet request.
     * @return the transformation source or null by default.
     */
    protected Source getNorthSource(HttpServletRequest request) {
        return null;
    }
}