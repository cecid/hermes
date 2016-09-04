/* 
 * Copyright(c) 2005 Center for E-Commerce Infrastructure Development, The
 * University of Hong Kong (HKU). All Rights Reserved.
 *
 * This software is licensed under the GNU GENERAL PUBLIC LICENSE Version 2.0 [1]
 * 
 * [1] http://www.gnu.org/licenses/old-licenses/gpl-2.0.txt
 */

package hk.hku.cecid.piazza.commons.xpath;
import java.util.Hashtable;
import java.util.Vector;

import javax.xml.transform.TransformerException;

import com.sun.org.apache.xpath.internal.ExtensionsProvider;
import com.sun.org.apache.xpath.internal.functions.FuncExtFunction;

/**
 * XPathFunctionsProvider implements the Apache XPath Extensions Provider. It is 
 * solely used by the XPathExecutor in building an Apache XPath Context. 
 * 
 * @author Hugo Y. K. Lam
 *
 */
class XPathFunctionsProvider implements ExtensionsProvider {

    private Hashtable functions = new Hashtable();
    
    /**
     * Checks if the given element is available.
     * 
     * @param ns the namespace of the element.
     * @param elemName the element name.
     * @return false in any way.
     * @see org.apache.xpath.ExtensionsProvider#elementAvailable(java.lang.String, java.lang.String)
     */
    public boolean elementAvailable(String ns, String elemName) {
        return false;
    }

    /**
     * Checks if the given function is available.
     * 
     * @param ns the namespace of the function.
     * @param funcName the function name.
     * @return true if the given function is available.
     * @see org.apache.xpath.ExtensionsProvider#functionAvailable(java.lang.String, java.lang.String)
     */
    public boolean functionAvailable(String ns, String funcName) {
        return getFunction(ns, funcName) != null;
    }
    
    /**
     * Registers a function to this provider.
     * 
     * @param ns the namespace of the function.
     * @param funcName the function name.
     * @param func the function implementation.
     */
    public void regsiterFunction(String ns, String funcName, XPathFunction func) {
        String name = getFunctionFullname(ns, funcName);
        if (name != null && func != null) {
            functions.put(name, func);
        }
    }
   
    /**
     * Gets a function from this provider by its name.
     * 
     * @param ns the namespace of the function.
     * @param funcName the function name.
     * @return the function corresponding to the given name or null if it is not available.
     */
    private XPathFunction getFunction(String ns, String funcName) {
        String name = getFunctionFullname(ns, funcName);
        if (name == null) {
            return null;
        }
        else {
            return (XPathFunction)functions.get(name);
        }
    }
    
    /**
     * Gets the full name of a function by its namespace and function name.
     * 
     * @param ns the namespace of the function.
     * @param funcName the function name.
     * @return the full name of the function.
     */
    private String getFunctionFullname(String ns, String funcName) {
        if (funcName==null || "".equals((funcName=funcName.trim()))) {
            return null;
        }
        if (ns==null || "".equals((ns=ns.trim()))) {
            return funcName;
        }
        else {
            return "("+ns+"):"+funcName;
        }
    }

    /**
     * Executes the extension function.
     * 
     * @param ns the namespace of the function.
     * @param funcName the function name.
     * @param argVec the function arguments.
     * @param methodKey the method key.
     * @return the execution result.
     * @throws TransformerException if the function executed with errors or the function is not available.
     * @see org.apache.xpath.ExtensionsProvider#extFunction(java.lang.String, java.lang.String, java.util.Vector, java.lang.Object)
     */
    public Object extFunction(String ns, String funcName, Vector argVec, Object methodKey) throws TransformerException {
        XPathFunction func = getFunction(ns, funcName);
        if (func != null) {
            try {
                return func.execute(argVec);
            }
            catch (Exception e) {
                throw new TransformerException("Function '"+funcName+"' of namespace '"+ns+"' executed with error", e);
            }
        }
        else {
            throw new TransformerException("Function '"+funcName+"' of namespace '"+ns+"' is not available");
        }
    }

	public Object extFunction(FuncExtFunction funcExtFunction, Vector vec) throws TransformerException {
		// return null;
		throw new TransformerException("Function '"+funcExtFunction.getFunctionName()+"' of namespace '"+funcExtFunction.getNamespace()+"' is not available");
	}
    
    
}
