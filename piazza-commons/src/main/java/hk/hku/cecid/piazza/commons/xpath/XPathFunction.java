/* 
 * Copyright(c) 2005 Center for E-Commerce Infrastructure Development, The
 * University of Hong Kong (HKU). All Rights Reserved.
 *
 * This software is licensed under the GNU GENERAL PUBLIC LICENSE Version 2.0 [1]
 * 
 * [1] http://www.gnu.org/licenses/old-licenses/gpl-2.0.txt
 */

package hk.hku.cecid.piazza.commons.xpath;
import java.util.Vector;

/**
 * XPathFunction is an interface of any customized XPath functions.
 * 
 * @author Hugo Y. K. Lam
 *
 */
public interface XPathFunction {

    /**
     * Invoked when evaluating its corresponding function in the XPath.
     * 
     * @param args the arguments specifed in the XPath.
     * @return the result after execution.
     */
    public Object execute(Vector args);
}
