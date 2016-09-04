/* 
 * Copyright(c) 2005 Center for E-Commerce Infrastructure Development, The
 * University of Hong Kong (HKU). All Rights Reserved.
 *
 * This software is licensed under the GNU GENERAL PUBLIC LICENSE Version 2.0 [1]
 * 
 * [1] http://www.gnu.org/licenses/old-licenses/gpl-2.0.txt
 */

package hk.hku.cecid.piazza.commons.soap;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.xml.soap.Name;

/**
 * SOAPFaultException represents a SOAP fault in a SOAP request.
 * 
 * @author Hugo Y. K. Lam
 */
public class SOAPFaultException extends SOAPRequestException {

    public static String SOAP_FAULT_CLIENT           = "Client";

    public static String SOAP_FAULT_SERVER           = "Server";

    public static String SOAP_FAULT_VERSION_MISMATCH = "VersionMismatch";

    public static String SOAP_FAULT_MUST_UNDERSTAND  = "MustUnderstand";

    private String       faultCode;

    private String       faultString;

    private String       faultActor;

    private Collection detailEntries = new ArrayList();

    private Map details = new HashMap();
    

    /**
     * Creates a new instance of SOAPFaultException.
     * 
     * @param code the fault code.
     * @param message the fault string.
     */
    public SOAPFaultException(String code, String message) {
        this(code, message, (String) null);
    }

    /**
     * Creates a new instance of SOAPFaultException.
     * 
     * @param code the fault code.
     * @param message the fault string.
     * @param actor the fault actor.
     */
    public SOAPFaultException(String code, String message, String actor) {
        super(message);
        init(code, message, actor);
    }

    /**
     * Creates a new instance of SOAPFaultException.
     * 
     * @param code the fault code.
     * @param message the fault string.
     * @param cause the cause of this exception.
     */
    public SOAPFaultException(String code, String message, Throwable cause) {
        super(message, cause);
        init(code, message, null);
    }

    /**
     * Creates a new instance of SOAPFaultException.
     * 
     * @param code the fault code.
     * @param message the fault string.
     * @param actor the fault actor.
     * @param cause the cause of this exception.
     */
    public SOAPFaultException(String code, String message, String actor,
            Throwable cause) {
        super(message, cause);
        init(code, message, actor);
    }

    /**
     * Initializes this SOAP fault exception.
     * 
     * @param code the fault code.
     * @param message the fault string.
     * @param actor the fault actor.
     */
    protected void init(String code, String message, String actor) {
        this.faultCode = code == null ? SOAP_FAULT_SERVER : code;
        this.faultString = message == null ? "Unknown error" : message;
        this.faultActor = actor;
    }

    /**
     * Gets the fault code.
     * 
     * @return the fault code.
     */
    public String getFaultCode() {
        return faultCode;
    }

    /**
     * Gets the fault string.
     * 
     * @return the fault string.
     */
    public String getFaultString() {
        return faultString;
    }

    /**
     * Gets the fault actor.
     * 
     * @return the fault actor.
     */
    public String getFaultActor() {
        return faultActor;
    }

    /**
     * Adds a SOAP fault detail entry.
     * 
     * @param name the entry name.
     * @param message the message of the detail entry.
     */
    public void addDetailEntry(Name name, Object message) {
        if (name != null) {
            detailEntries.add(name);
            details.put(name.getQualifiedName(), message == null ? "" : message);
        }
    }

    /**
     * Checks if there are any SOAP fault detail entries.
     * 
     * @return true if there are any SOAP fault detail entries.
     */
    public boolean hasDetailEntries() {
        return !detailEntries.isEmpty();
    }

    /**
     * Gets the SOAP fault detail entry names.
     * 
     * @return the SOAP fault detail entry Name objects.
     */
    public Iterator getDetailEntryNames() {
        return detailEntries.iterator();
    }

    /**
     * Gets the SOAP fault detail entry value with the specified name.
     * 
     * @return the SOAP fault detail entry value.
     */
    public Object getDetailEntryValue(Name name) {
        return details.get(name.getQualifiedName());
    }
}