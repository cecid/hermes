package hk.hku.cecid.piazza.commons.soap;

import hk.hku.cecid.piazza.commons.io.IOHandler;
import hk.hku.cecid.piazza.commons.servlet.RequestListenerException;
import hk.hku.cecid.piazza.commons.util.Instance;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Iterator;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.namespace.QName;
import javax.xml.soap.Name;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPBodyElement;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * WebServicesAdaptor is a SOAPHttpAdaptor which handles web services request.
 * This adaptor can only handles document style web services. It invokes either
 * one of the following methods in the subclass for handling the request:
 * <ol>
 * <li> public Element[] serviceRequested(Element[] bodies)
 * <li> public SOAPElement[] serviceRequested(SOAPElement[] bodies)
 * <li> public void serviceRequested(SOAPMessage req, SOAPMessage resp)
 * <li> public void serviceRequested(WebServicesRequest req, WebServicesResponse
 * resp)
 * </ol>
 * 
 * @author Hugo Y. K. Lam
 *  
 */
public abstract class WebServicesAdaptor extends SOAPHttpAdaptor {

    private static final String  SERVICE_METHOD          = "serviceRequested";

    private static final Class[] SM_DOM_ELEM_PARA_TYPES  = new Class[]{Element[].class};

    private static final Class[] SM_SOAP_ELEM_PARA_TYPES = new Class[]{SOAPElement[].class};

    private static final Class[] SM_SOAP_MSG_PARA_TYPES  = new Class[]{SOAPMessage.class, SOAPMessage.class         };

    private static final Class[] SM_WS_MSG_PARA_TYPES    = new Class[]{WebServicesRequest.class, WebServicesResponse.class};

    /**
     * Prcoesses the SOAP request.
     * 
     * @param request the SOAP request.
     * @param response the SOAP response.
     * @throws SOAPRequestException if there is any error in processing the SOAP
     *             request.
     * @see hk.hku.cecid.piazza.commons.soap.SOAPRequestListener#processRequest(hk.hku.cecid.piazza.commons.soap.SOAPRequest,
     *      hk.hku.cecid.piazza.commons.soap.SOAPResponse)
     */
    public void processRequest(SOAPRequest request, SOAPResponse response)
            throws SOAPRequestException {

        try {
            /*
             * Extract the body elements from the SOAP request.
             */
            SOAPMessage requestMessage = request.getMessage();
            SOAPBody requestBody = requestMessage.getSOAPPart().getEnvelope()
                    .getBody();

            Iterator requestBodyChild = requestBody.getChildElements();
            ArrayList requestBodies = new ArrayList();
            while (requestBodyChild.hasNext()) {
                Object requestBodyElement = requestBodyChild.next();
                if (requestBodyElement instanceof SOAPBodyElement) {
                    requestBodies.add(requestBodyElement);
                }
            }
            SOAPBodyElement[] requestBodyElements = (SOAPBodyElement[]) requestBodies
                    .toArray(new SOAPBodyElement[]{});

            Instance thisInstance = new Instance(this);

            /*
             * Prepare the response message.
             */
            SOAPMessage responseMessage = response.getMessage();
            SOAPBody responseBody = responseMessage.getSOAPPart().getEnvelope()
                    .getBody();

            /*
             * Invoke the method serviceRequest(SOAPElement[] bodies).
             */
            if (thisInstance.isMethodExist(SERVICE_METHOD,
                    SM_SOAP_ELEM_PARA_TYPES)) {
                SOAPElement[] responseBodyElements = (SOAPElement[]) thisInstance
                        .invoke(SERVICE_METHOD, SM_SOAP_ELEM_PARA_TYPES,
                                new Object[]{requestBodyElements});

                addChild(responseBody, responseBodyElements);
            }
            /*
             * Invoke the method serviceRequest(Element[] bodies).
             */
            else if (thisInstance.isMethodExist(SERVICE_METHOD,
                    SM_DOM_ELEM_PARA_TYPES)) {
                Element[] responseBodyElements = (Element[]) thisInstance
                        .invoke(SERVICE_METHOD, SM_DOM_ELEM_PARA_TYPES,
                                new Object[]{requestBodyElements});

                addChild(responseBody, responseBodyElements);
            }
            /*
             * Invoke the method serviceRequest(SOAPMessage req, SOAPMessage
             * resp).
             */
            else if (thisInstance.isMethodExist(SERVICE_METHOD,
                    SM_SOAP_MSG_PARA_TYPES)) {
                thisInstance.invoke(SERVICE_METHOD, SM_SOAP_MSG_PARA_TYPES,
                        new Object[]{request.getMessage(),
                                response.getMessage()});
            }
            /*
             * Invoke the method serviceRequest(WebServicesRequest req,
             * WebServicesResponse resp).
             */
            else if (thisInstance.isMethodExist(SERVICE_METHOD,
                    SM_WS_MSG_PARA_TYPES)) {
                WebServicesRequest req = new WebServicesRequest(request);
                WebServicesResponse resp = new WebServicesResponse(response);
                req.setBodies(requestBodyElements);
                thisInstance.invoke(SERVICE_METHOD, SM_WS_MSG_PARA_TYPES,
                        new Object[]{req, resp});

                addChild(responseBody, resp.getBodies());
            }
            /*
             * Initiate a SOAP fault if no service method found.
             */
            else {
                throw new SOAPRequestException("Unable to find service method");
            }
        }
        catch (Exception e) {
            throw new SOAPRequestException("Error in processing SOAP request",
                    e);
        }
    }
    
    /**
     * Adds child elements to the given SOAP body according to the their types.
     * 
     * @param body the SOAP body.
     * @param elements the child elements to be added.
     * @throws SOAPException if unable to add any of the elements.
     */
    private void addChild(SOAPBody body, Element[] elements) throws SOAPException {
        if (elements!=null) {
            for (int i = 0; i < elements.length; i++) {
                if (elements[i] instanceof SOAPElement) {
                    body.addChildElement((SOAPElement)elements[i]);
                }
                else {
                    body.appendChild(body.getOwnerDocument().importNode(elements[i], true));
                }
            }
        }
    }

    /**
     * Responses with a web services descriptor if the request's query is "wsdl"
     * or the request is a "get" method.
     * 
     * @param request the servlet request.
     * @param response the servlet response.
     * @return true if the request is not a "wsdl" request, false otherwise.
     * @throws RequestListenerException if there is any error in processing the
     *             request.
     * @see hk.hku.cecid.piazza.commons.servlet.http.HttpRequestListener#doStartRequest(javax.servlet.http.HttpServletRequest,
     *      javax.servlet.http.HttpServletResponse)
     */
    public boolean doStartRequest(HttpServletRequest request,
            HttpServletResponse response) throws RequestListenerException {
        if ("get".equalsIgnoreCase(request.getMethod())
                || "wsdl".equalsIgnoreCase(request.getQueryString())) {
            OutputStream outs;
            try {
                outs = response.getOutputStream();
            }
            catch (Exception e) {
                throw new RequestListenerException(
                        "Error in retrieving the servlet output stream", e);
            }

            response.setContentType("text/xml;utf-8");
            processDescriptorRequest(outs);
            return false;
        }
        else {
            return true;
        }
    }

    /**
     * Processes the WSDL request. Override to provide specific descriptor.
     * 
     * @param outs the output stream to which the descriptor should be written.
     * @throws RequestListenerException if there is error in processing the WSDL
     *             request.
     */
    protected void processDescriptorRequest(OutputStream outs)
            throws RequestListenerException {
        try {
            String descriptor = this.getClass().getName().replace('.', '/')
                    + ".wsdl";
            InputStream ins = this.getClass().getClassLoader()
                    .getResourceAsStream(descriptor);
            if (ins != null) {
                IOHandler.pipe(ins, outs);
            }
            else {
                String wsdl = "<?xml version=\"1.0\" encoding=\"utf-8\"?>"
                        + "<wsdl>WSDL not available.</wsdl>";
                outs.write(wsdl.getBytes());
            }
        }
        catch (Exception e) {
            throw new RequestListenerException(
                    "Error in processing WSDL request", e);
        }
    }

    /**
     * Gets the text value of the element with the specified node name in the 
     * given elements.
     * 
     * @param elements the elements containing the target node.
     * @param nodename the target node name.
     * @return the text value of the specified node.
     */
    protected String getText(Element[] elements, String nodename) {
        for (int i=0; elements !=null && i < elements.length; i++) {
            String localname = elements[i].getLocalName();
            if (localname.equals(nodename)) {
                String text = new String();
                NodeList nl = elements[i].getChildNodes();
                for (int j = 0; j < nl.getLength(); j++) {
                    Node node = nl.item(j);
                    if (node != null) {
                        text = text + node.getNodeValue();
                    }
                }
                text = text.trim();
                if (text.equals("")) {
                    return null;
                }
                else {
                    return text;
                }
            }
        }
        return null;
    }
    
    /**
     * Creates a text element.
     * 
     * @param name the element name.
     * @param value the element value.
     * @param namespace the namespace.
     * @return the newly created text element.
     * @throws SOAPException if unable to create the element. 
     */
    protected SOAPElement createText(String name, String value, String namespace) throws SOAPException {
        return createElement(name, value, namespace, "xsd:string");
    }
    
    /**
     * Creates a SOAP element.
     * 
     * @param name the element name.
     * @param value the element value.
     * @param namespace the namespace.
     * @param xsdType the XSD data type.
     * @return the newly created element.
     * @throws SOAPException if unable to create the element. 
     */
    protected SOAPElement createElement(String name, String value, String namespace, String xsdType) throws SOAPException {
            Name elementName = super.soapFactory.createName(name, "",
                namespace);
            Name elementType = super.soapFactory.createName("type", "",
                "http://www.w3.org/2001/XMLSchema-instance");
    
            SOAPElement element = super.soapFactory.createElement(elementName);
            element.addNamespaceDeclaration("xsd", "http://www.w3.org/2001/XMLSchema");
            element.addAttribute(elementType, xsdType);
            element.addTextNode(value);
            return element;
    }
    
    protected boolean isElement(SOAPElement element, String local, String namespace) {
    	return new QName(namespace, local).equals(element.getElementQName());
    }
    
    protected SOAPElement createElement(String local, String namespace) throws SOAPException {
    	return super.soapFactory.createElement(soapFactory.createName(local, "", namespace));
    }
    
    protected SOAPElement createElement(String local, String namespace, String value) throws SOAPException {
        SOAPElement element = createElement(local, namespace);
        element.setValue(value);
        return element;
    }
    
    protected SOAPElement[] getChildElementArray(SOAPElement element) {
    	ArrayList<SOAPElement> arrayList = new ArrayList<SOAPElement>();
    	Iterator<SOAPElement> iter1 = element.getChildElements();
		while (iter1.hasNext()) {
            Node node = (Node)iter1.next();

            // Kenneth Wong [20170811] : Only add the ELEMENT_NODE to the childArray, and discard the TEXT_NODE.
            if (node.getNodeType()==Node.ELEMENT_NODE)
			    arrayList.add((SOAPElement)node);
        }   

		SOAPElement[] childArray = (SOAPElement[])arrayList.toArray(new SOAPElement[]{});
		return childArray;
    }
}