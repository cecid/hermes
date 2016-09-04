/* 
 * Copyright(c) 2005 Center for E-Commerce Infrastructure Development, The
 * University of Hong Kong (HKU). All Rights Reserved.
 *
 * This software is licensed under the GNU GENERAL PUBLIC LICENSE Version 2.0 [1]
 * 
 * [1] http://www.gnu.org/licenses/old-licenses/gpl-2.0.txt
 */

package hk.hku.cecid.piazza.commons.util;

import hk.hku.cecid.piazza.commons.module.ComponentException;
import hk.hku.cecid.piazza.commons.module.PersistentComponent;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.StringTokenizer;
import java.util.Vector;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.io.DocumentSource;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;

/**
 * PropertyTree is an implementation of a PropertySheet.
 * It represents a property sheet with a tree structure and
 * is actually backed by a Document object. 
 * 
 * @see org.dom4j.Document
 * 
 * @author Hugo Y. K. Lam
 *  
 */
public class PropertyTree extends PersistentComponent implements PropertySheet {

    private Document dom;

    /**
     * Creates a new instance of PropertyTree. 
     */
    public PropertyTree() {
        dom = DocumentHelper.createDocument();
    }

    /**
     * Creates a new instance of PropertyTree.
     *  
     * @param url the url of the properties source.
     * @throws ComponentException if the properties could not be loaded from the specified url.
     */
    public PropertyTree(URL url) throws ComponentException {
        super(url);
    }
    
    /**
     * Creates a new instance of PropertyTree.
     *  
     * @param node the root node of the properties source.
     * @throws ComponentException if the properties could not be constructed from the specified node.
     */
    public PropertyTree(org.w3c.dom.Node node) throws ComponentException {
        try {
            TransformerFactory factory = TransformerFactory.newInstance();
            Transformer transformer = factory.newTransformer();
            ByteArrayOutputStream baos = new ByteArrayOutputStream(); 
            transformer.transform(new DOMSource(node), new StreamResult(baos));
            dom = new SAXReader().read(new ByteArrayInputStream(baos.toByteArray()));
        }
        catch (Exception e) {
            throw new ComponentException("Unable to construct from the given node", e);
        }
    }

    /**
     * Creates a new instance of PropertyTree.
     *  
     * @param ins the input stream of the properties source.
     * @throws ComponentException if the properties could not be loaded from the specified input stream.
     */
    public PropertyTree(InputStream ins) throws ComponentException {
        try {
            dom = new SAXReader().read(ins);
        } catch (Exception e) {
            throw new ComponentException("Unable to read from input stream", e);
        }
    }

    /**
     * Checks if the specified xpath exists in this property tree.
     * 
     * @param xpath the property xpath.
     * @return true if the specified xpath exists in this property tree.
     * @see hk.hku.cecid.piazza.commons.util.PropertySheet#containsKey(java.lang.String)
     */
    public boolean containsKey(String xpath) {
        return getProperty(xpath) != null;
    }

    /**
     * Gets a property with the specified xpath.
     * If the xpath refers to more than one properpty, the first one will be returned.
     * 
     * @param xpath the property xpath.
     * @return the property with the specified xpath.
     * @see hk.hku.cecid.piazza.commons.util.PropertySheet#getProperty(java.lang.String)
     */
    public String getProperty(String xpath) {
        Node node = getPropertyNode(xpath);
        return node == null ? null : node.getStringValue();
    }

    /**
     * Gets a property with the specified xpath.
     * If the xpath refers to more than one properpty, the first one will be returned.
     * 
     * @param xpath the property xpath.
     * @param def the default value.
     * @return the property with the specified xpath.
     * @see hk.hku.cecid.piazza.commons.util.PropertySheet#getProperty(java.lang.String,
     *      java.lang.String)
     */
    public String getProperty(String xpath, String def) {
        Node node = getPropertyNode(xpath);
        return node == null ? def : node.getStringValue();
    }

    /**
     * Gets a list of properties with the specified xpath.
     * 
     * @param xpath the properties xpath.
     * @return the properties with the specified xpath.
     * @see hk.hku.cecid.piazza.commons.util.PropertySheet#getProperties(java.lang.String)
     */
    public String[] getProperties(String xpath) {
        List nodes = getPropertyNodes(xpath);
        String[] nodeValues = new String[nodes.size()];

        for (int i = 0; i < nodes.size(); i++) {
            nodeValues[i] = ((Node) nodes.get(i)).getStringValue();
        }
        return nodeValues;
    }

    /**
     * Gets a two-dimensional list of properties with the specified xpaths.
     * The first xpath will define the first dimension of the list while 
     * the second xpath will define the second dimension. E.g.
     * <p>
     * <pre>
     * &lt;!- Properties content --&gt;
     * &lt;application&gt; 
     *   &lt;listener&gt;
     *     &lt;id&gt;MyListener&lt;/id&gt;
     *     &lt;name&gt;My Listener&lt;/name&gt;
     *   &lt;/listener&gt;
     *   &lt;listener&gt;
     *     &lt;id&gt;MyListener2&lt;/id&gt;
     *     &lt;name&gt;My Listener 2&lt;/name&gt;
     *   &lt;/listener&gt;
     * &lt;/application&gt;
     * 
     * First xpath: /application/listener
     * Second xpath: ./id|./name
     * 
     * Returned array: 
     * {{"MyListener","My Listener"},{"MyListener2","My Listener 2"}} 
     * </pre>
     * </p>
     * 
     * @param xpath the first xpath.
     * @param xpath2 the second xpath.
     * @return a two-dimensional list of properties with the specified xpaths.
     * @see hk.hku.cecid.piazza.commons.util.PropertySheet#getProperties(java.lang.String,
     *      java.lang.String)
     */
    public String[][] getProperties(String xpath, String xpath2) {
        List nodes = getPropertyNodes(xpath);
        String[][] nodeValues = new String[nodes.size()][];

        for (int i = 0; i < nodes.size(); i++) {
            List nodes2 = ((Node) nodes.get(i)).selectNodes(xpath2);
            nodeValues[i] = new String[nodes2.size()];
            for (int j = 0; j < nodes2.size(); j++) {
                nodeValues[i][j] = ((Node) nodes2.get(j)).getStringValue();
            }
        }
        return nodeValues;
    }

    /**
     * Creates a Properties object which stores the properties retrieved by the specified xpath.
     * 
     * @param xpath the properties xpath.
     * @return a Properties object which stores the retrieved properties.
     * @see hk.hku.cecid.piazza.commons.util.PropertySheet#createProperties(java.lang.String)
     */
    public Properties createProperties(String xpath) {
        Properties newProps = new Properties();
        List nodes = getPropertyNodes(xpath);

        for (int i = 0; i < nodes.size(); i++) {
            Node node = (Node) nodes.get(i);

            String key = node.getName();
            int prefixIndex = key.indexOf(':');
            if (prefixIndex != -1) {
                key = key.substring(prefixIndex + 1);
            }

            String tmpkey = ((Element)node).attributeValue("name");
            String tmpvalue = ((Element)node).attributeValue("value");
            String tmptype = ((Element)node).attributeValue("type");
            String value = node.getStringValue();
            if (tmpkey != null) {
                key = tmpkey.trim();
            }
            if (tmpvalue!=null) {
                value = tmpvalue;
            }
            if (tmptype != null) {
                String type = tmptype.trim();
                if (!"".equals(type)) {
                    key = type + ":" + key;
                }
            }
            
            if (value != null) {
                newProps.setProperty(key, value);
            }
        }
        return newProps;
    }

    /**
     * Counts the number of properties with the specified xpath.
     * 
     * @param xpath the properties xpath.
     * @return the number of properties with the specified xpath.
     */
    public int countProperties(String xpath) {
        return getPropertyNodes(xpath).size();
    }
    
    
    /**
     * Sets a property value with the specified key.
     * 
     * @param xpath the property xpath.
     * @param value the property value.
     * @return true if the operation is successful. false otherwise.
     * @see hk.hku.cecid.piazza.commons.util.PropertySheet#setProperty(java.lang.String,
     *      java.lang.String)
     */
    public boolean setProperty(String xpath, String value) {
        Node node = getPropertyNode(xpath);
        boolean result;

        if (node == null) {
            result = addProperty(xpath, value);
        }
        else {
            try {
                if (value == null) {
                    node.detach();
                }
                else {
                    node.setText(value);
                }
                result = true;
            }
            catch (Exception e) {
                result = false;
            }
        }
        return result;
    }

    /**
     * Removes a property with the specified xpath.
     * If the xpath refers to more than one properpty, the first one will be removed.
     * 
     * @param xpath the property xpath.
     * @return true if the operation is successful. false otherwise.
     * @see hk.hku.cecid.piazza.commons.util.PropertySheet#removeProperty(java.lang.String)
     */
    public boolean removeProperty(String xpath) {
        return setProperty(xpath, null);
    }

    /**
     * Gets all the existing property xpaths.
     * 
     * @return all the existing property xpaths.
     * @see hk.hku.cecid.piazza.commons.util.PropertySheet#propertyNames()
     */
    public Enumeration propertyNames() {
        Iterator nodes = getPropertyNodes("//*[count(./*)=0]").iterator();
        Vector propNames = new Vector();

        while (nodes.hasNext()) {
            Node node = (Node) nodes.next();
            propNames.addElement(node.getUniquePath());
        }

        return propNames.elements();
    }

    /**
     * Loads the properties from the specified url location.
     * 
     * @param url the url of the properties source.
     * @throws Exception if the operation is unsuccessful. 
     * @see hk.hku.cecid.piazza.commons.module.PersistentComponent#loading(java.net.URL)
     */
    protected void loading(URL url) throws Exception {
        SAXReader reader = new SAXReader();
        dom = reader.read(url);
    }

    /**
     * Stores the properties to the specified url location.
     * 
     * @param url the url of the properties source.
     * @throws Exception if the operation is unsuccessful. 
     * @see hk.hku.cecid.piazza.commons.module.PersistentComponent#storing(java.net.URL)
     */
    protected void storing(URL url) throws Exception {
        XMLWriter writer = new XMLWriter(new FileOutputStream(Convertor.toFile(url)),
                OutputFormat.createPrettyPrint());
        writer.write(dom);
        writer.close();
    }

    /**
     * Gets a property node with the specified xpath.
     * If the xpath refers to more than one properpty node, the first one will be returned.
     * 
     * @param xpath the property xpath.
     * @return the property node with the specified xpath.
     */
    protected Node getPropertyNode(String xpath) {
        try {
            return dom.selectSingleNode(xpath);
        }
        catch (Exception e) {
            return null;
        }
    }

    /**
     * Gets a list of property nodes with the specified xpath.
     * 
     * @param xpath the properties xpath.
     * @return the property nodes with the specified xpath.
     */
    protected List getPropertyNodes(String xpath) {
        try {
            return dom.selectNodes(xpath);
        }
        catch (Exception e) {
            return Collections.EMPTY_LIST;
        }
    }

    /**
     * Adds a property to this property tree.
     * 
     * @param xpath the property xpath.
     * @param value the property value.
     * @return true if the operation is successful. false otherwise.
     */
    protected boolean addProperty(String xpath, String value) {
        try {
            if (xpath != null) {
                // retrieve the root element in the document
                Element curElement = dom.getRootElement();
                String rootElementName = curElement == null ? "" : curElement
                        .getName();

                // basic check for the path validity
                xpath = xpath.trim();
                if (xpath.startsWith("//") || xpath.startsWith("../")) {
                    return false;
                }
                else if (xpath.startsWith("./")) {
                    xpath = xpath.substring(2);
                }
                else if (xpath.startsWith("/" + rootElementName + "/")) {
                    xpath = xpath.substring(2 + rootElementName.length());
                }
                else if (xpath.equals("/" + rootElementName)) {
                    xpath = "";
                }
                else if (xpath.startsWith("/") && curElement != null) {
                    return false;
                }

                // set the value to the root directly if there is no
                // sub-elements specified
                if ("".equals(xpath) && curElement != null) {
                    curElement.setText(value);
                    return true;
                }

                StringTokenizer pathElements = new StringTokenizer(xpath, "/");

                // loop the elements specified in the path
                while (pathElements.hasMoreElements()) {
                    // retrieve the element name
                    String elementName = pathElements.nextToken().trim();

                    if (!"".equals(elementName)) {
                        // parse the element name
                        StringTokenizer elementNameCombo = new StringTokenizer(
                                elementName, "[]");
                        boolean isIndexing = elementNameCombo.countTokens() > 1;

                        // get all elements with the specified name
                        elementName = elementNameCombo.nextToken();
                        List nextElements = curElement == null ? Collections.EMPTY_LIST
                                : curElement.elements(elementName);

                        // assume the element referring to the first child
                        int elementPosition = 0;

                        // get the element position specified in the path, if
                        // any
                        if (isIndexing) {
                            try {
                                elementPosition = Integer
                                        .parseInt(elementNameCombo.nextToken()) - 1;
                            }
                            catch (Exception e) {
                                // assume the element referring to a new child
                                // being appended at the end
                                elementPosition = nextElements.size();
                            }
                        }

                        // adjust the element position if it is out of range
                        if (elementPosition < 0) {
                            elementPosition = 0;
                        }
                        else if (elementPosition > nextElements.size()) {
                            elementPosition = nextElements.size();
                        }

                        Element nextElement;

                        if (elementPosition < nextElements.size()) {
                            nextElement = (Element) nextElements
                                    .get(elementPosition);
                        }
                        else {
                            if (curElement == null) {
                                nextElement = DocumentHelper
                                        .createElement(elementName);
                                dom.setRootElement(nextElement);
                            }
                            else {
                                nextElement = curElement
                                        .addElement(elementName);
                            }
                        }

                        /*
                        // DEBUG (Print out the status in this turn of loop)
                        Debugger.print("curElement: " + curElement
                                + ", targetElement: " + elementName
                                + ", targetElementPosition: " + elementPosition
                                + ", targetExists: "
                                + (elementPosition < nextElements.size())
                                + ", targetElementSizes: "
                                + nextElements.size() + ", nextElement: "
                                + nextElement);
                        // DEBUG (Dump all child elements of the current
                        // element)
                        if (curElement != null) {
                            Debugger.print(curElement.elements());
                        }
                        // END OF DEBUG
                         */

                        // move to the next element
                        curElement = nextElement;

                        // set the value if it is the last element
                        if (!pathElements.hasMoreElements()) {
                            curElement.setText(value);
                            return true;
                        }
                    }
                }
            }
            return false;
        }
        catch (Exception e) {
            /*Sys.main.log.error("Error adding property '" + xpath + "' with value '"
                    + value + "'", e);*/
            return false;
        }
    }

    /**
     * Appends a property sheet to this property tree.
     * The specified property sheet can only be appended if it is of the PropertyTree type.
     * 
     * @param p the property sheet to be appended.
     * @return true if the operation is successful. false otherwise.
     * @see hk.hku.cecid.piazza.commons.util.PropertySheet#append(hk.hku.cecid.piazza.commons.util.PropertySheet)
     */
    public boolean append(PropertySheet p) {
        Document dom2;
        if (p instanceof PropertyTree) {
            dom2 = ((PropertyTree) p).getDOM();
        }
        else {
            return false;
        }

        Element rootElement = dom.getRootElement();
        Element rootElement2 = dom2.getRootElement();

        if (rootElement2 == null) {
            return true;
        }

        if (rootElement == null) {
            rootElement2.detach();
            dom.setRootElement(rootElement2);
            return true;
        }
        else {
            String rootElementName = rootElement.getName();
            String rootElementName2 = rootElement2.getName();

            if (rootElementName.equals(rootElementName2)) {
                Iterator elements = rootElement2.elements().iterator();
                while (elements.hasNext()) {
                    Element element = (Element) elements.next();
                    element.detach();
                    rootElement.add(element);
                }
                return true;
            }
            else {
                return false;
            }
        }
    }

    /**
     * Creates a sub-tree from this property tree.
     * 
     * @param xpath the xpath for locating the subtree.
     * @return a new property tree.
     */
    public PropertyTree subtree(String xpath) {
        Node node = getPropertyNode(xpath);
        if (node == null) {
            return new PropertyTree();
        }
        try{
            return new PropertyTree(new ByteArrayInputStream(node.asXML().getBytes("UTF-8")));
        }
        catch (Exception e) {
            return new PropertyTree();
        }
    }
    
    /**
     * Gets the docment source. 
     * 
     * @return the document source.
     */
    public Source getSource() {
        return new DocumentSource(dom);
    }
    
    /**
     * Gets the Document object which backs this property tree.
     * 
     * @return the Document object.
     */
    private Document getDOM() {
        return dom;
    }

    /**
     * Returns a W3C document representation of this property tree.
     * 
     * @return a new W3C document.
     */
    public org.w3c.dom.Document toDocument() {
        try {
            DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            return builder.parse(new ByteArrayInputStream(dom.asXML().getBytes("UTF-8")));
        }
        catch (Exception e) {
            throw new RuntimeException("Unable to convert document", e);
        }
    }

    /**
     * Returns a string representation of this property tree, which is the XML text.
     * 
     * @return a string representation of this property tree.
     * @see java.lang.Object#toString()
     */
    public String toString() {
        return dom.asXML();
    }
}