/* 
 * Copyright(c) 2005 Center for E-Commerce Infrastructure Development, The
 * University of Hong Kong (HKU). All Rights Reserved.
 *
 * This software is licensed under the GNU GENERAL PUBLIC LICENSE Version 2.0 [1]
 * 
 * [1] http://www.gnu.org/licenses/old-licenses/gpl-2.0.txt
 */

package hk.hku.cecid.piazza.commons.xpath;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.TransformerException;

import com.sun.org.apache.xml.internal.utils.PrefixResolverDefault;
import com.sun.org.apache.xpath.internal.XPath;
import com.sun.org.apache.xpath.internal.XPathContext;
import com.sun.org.apache.xpath.internal.XPathVisitor;
import com.sun.org.apache.xpath.internal.objects.XObject;
import org.w3c.dom.Node;

/**
 * XPathExecutor is an executor which evaluates a given xpath and yields the 
 * result.
 * <p>
 * <i>Example:</i>
 * </p>
 * <pre>
 *      XPathExecutor exec = new XPathExecutor(getDocument("doc.xml"));
 * 
 *      exec.registerFunction("http://example.com", "sum", new MySum());
 * 
 *      System.out.println(exec.eval( "3 + number('3') + eg:sum(3, 4, '5.1') + /values/one  - 2"));
 * 
 *      // The result will be '17.1'
 * </pre>
 * 
 * @author Hugo Y. K. Lam
 *  
 */
public class XPathExecutor {
    
    private Node contextNode;
    private Node namespaceNode;
    private XPathFunctionsProvider functionsProvider;
    
    private XPathContext xpathSupport;
    private PrefixResolverDefault prefixResolver;
    
    /**
     * Creates a new instance of XPathExecutor.
     */
    public XPathExecutor() {
        this(null);
    }
    
    /**
     * Creates a new instance of XPathExecutor.
     * 
     * @param document the document containing the context being queried and 
     *                 the namespaces being referenced.
     */
    public XPathExecutor(Node document) {
        this(document, null);
    }
    
    /**
     * Creates a new instance of XPathExecutor.
     * 
     * @param context the document containing the context being queried.
     * @param namespaces the document containing the namespaces being referenced.
     */
    public XPathExecutor(Node context, Node namespaces) {
        this.contextNode = context==null? createDocument() : context;
        this.namespaceNode = namespaces==null? contextNode : namespaces;
        this.functionsProvider = new XPathFunctionsProvider();
        this.xpathSupport = new XPathContext(functionsProvider);
        this.prefixResolver = new PrefixResolverDefault(
                (namespaceNode.getNodeType() == Node.DOCUMENT_NODE) ? 
                        ((org.w3c.dom.Document) namespaceNode)
                        .getDocumentElement() : namespaceNode);
    }

    /**
     * Registers a function to be used in an XPath.
     * 
     * @param ns the namespace of the function.
     * @param funcName the function name.
     * @param func the function implementation.
     */
    public void registerFunction(String ns, String funcName, XPathFunction func) {
        functionsProvider.regsiterFunction(ns, funcName, func);
    }

    /**
     * Evaluates an XPath expression.
     * 
     * @param expression the XPath expression.
     * @return the evaluated result.
     * @throws TransformerException if unable to transform the expression.
     */
    public Object eval(String expression) throws TransformerException {
        return eval(expression, null);
    }

    /**
     * Evaluates an XPath expression.
     * 
     * @param expression the XPath expression.
     * @param context the document containing the context being queried.
     * @return the evaluated result.
     * @throws TransformerException if unable to transform the expression.
     */
    public Object eval(String expression, Node context) throws TransformerException {
        if (context == null) {
            context = contextNode;
        }

        XPath xpath = createXPath(expression);

        XObject result = xpath.execute(xpathSupport, 
                             xpathSupport.getDTMHandleFromNode(context), 
                             prefixResolver);
        
        if (result.getType() == XObject.CLASS_BOOLEAN) {
            return new Boolean(result.toString());
        }
        else {
            return result.object();
        }
    }

    /**
     * Evaluates an XPath expression.
     * 
     * @param expression the XPath expression.
     * @param visitor the XPath visitor.
     * @throws TransformerException if unable to transform the expression.
     */
    public void visit(String expression, XPathVisitor visitor) throws TransformerException {
        XPath xpath = createXPath(expression);

        xpath.callVisitors(xpath, visitor);
    }

    /**
     * Creates an XPath object for an XPath expression.
     * 
     * @param expression the XPath expression.
     * @return the XPath object.
     * @throws TransformerException if unable to transform the expression.
     */
    private XPath createXPath(String expression) throws TransformerException {
        return new XPath(expression, null, prefixResolver, XPath.SELECT, null);
    }
    
    /**
     * Creates a blank document.
     * 
     * @return a blank document.
     */
    private static Node createDocument() {
        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            return db.newDocument();
        }
        catch (Exception e) {
            throw new RuntimeException("Cannot construct or configure document builder", e);
        }
    }
}