/* ===== 
 *
 * $Header: /home/cvsroot/ebxml-pkg/src/hk/hku/cecid/ebms/pkg/Acknowledgment.java,v 1.1 2005/07/28 09:36:24 dcmsze Exp $
 *
 * Code authored by:
 *
 * cyng [2002-03-21]
 *
 * Code reviewed by:
 *
 * username [YYYY-MM-DD]
 *
 * Remarks:
 *
 * =====
 */

package hk.hku.cecid.ebms.pkg;

import hk.hku.cecid.ebms.pkg.validation.SOAPValidationException;

import java.util.ArrayList;
import java.util.Iterator;

import javax.xml.soap.Name;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPException;
import javax.xml.soap.Text;
/**
 * An ebXML <code>Acknowledgment</code> in the SOAP Header of a
 * <code>HeaderContainer</code> [ebMSS 6.3.2].
 *
 * @author cyng
 * @version $Revision: 1.1 $
 */
public class Acknowledgment extends HeaderElement {

    /** <code>Acknowledgment</code> element name */
    static final String ACKNOWLEDGMENT = "Acknowledgment";

    private final String timestamp;

    private final String refToMessageId;

    private ExtensionElement from;

    private final ArrayList fromPartyIds;

    private final ArrayList signatureReferences;

    /** 
     * Constructs an <code>Acknowledgment</code> with the given mandatory
     * fields.
     *
     * @param soapEnvelope      <code>SOAPEnvelope</code>, the header container
     *                          of which the <code>Acknowledgement</code> is 
     *                          attached to.
     * @param timestamp         The time the message service handler received
     *                          the message to be acknowledged.
     * @param refToMessage      The message this acknowledgement response 
     *                          is referring to.
     * @param fromPartyId       The identifier of the party generating the
     *                          acknowledgement response message.
     * @param fromPartyIdType   PartyId type
     *
     * @throws SOAPException 
    */
    Acknowledgment(SOAPEnvelope soapEnvelope, String timestamp,
                   EbxmlMessage refToMessage, String fromPartyId,
                   String fromPartyIdType) throws SOAPException {
        super(soapEnvelope, ACKNOWLEDGMENT);
        this.timestamp = timestamp;
        this.refToMessageId = refToMessage.getMessageId();
        setActor(ACTOR_TO_PARTY_MSH_URN);
        addChildElement(MessageHeader.ELEMENT_TIMESTAMP, timestamp);
        addChildElement(MessageHeader.ELEMENT_REF_TO_MESSAGE_ID,
                        refToMessageId);
        fromPartyIds = new ArrayList();
        if (fromPartyId != null) {
            from = addChildElement(MessageHeader.ELEMENT_FROM);
            addFromPartyId(fromPartyId, fromPartyIdType);
        }
        else {
            from = null;
        }
        signatureReferences = new ArrayList();
    }

    /** 
     * Constructs an <code>Acknowledgement</code> object by parsing the given
     * <code>SOAPElement</code>.
     * 
     * @param soapEnvelope      <code>SOAPEnvelope</code>, the header container
     *                          of which the <code>Acknowledgement</code> is 
     *                          attached to.
     * @param soapElement       <code>SOAPElement</code> containing
     *                          acknowledgement response.
     *
     * @exception SOAPException
     */
    Acknowledgment(SOAPEnvelope soapEnvelope, SOAPElement soapElement)
        throws SOAPException {
        super(soapEnvelope, soapElement);
        Iterator childElements = getChildElements
            (MessageHeader.ELEMENT_TIMESTAMP);
        if (childElements.hasNext()) {
            timestamp = ((SOAPElement) childElements.next()).getValue();
        }
        else {
            throw new SOAPValidationException
                (SOAPValidationException.SOAP_FAULT_CLIENT,
                 "Missing <" + NAMESPACE_PREFIX_EB + ":" + MessageHeader.
                 ELEMENT_TIMESTAMP + "> in <" + NAMESPACE_PREFIX_EB + ":" +
                 ACKNOWLEDGMENT + ">!");
        }
        childElements = getChildElements
            (MessageHeader.ELEMENT_REF_TO_MESSAGE_ID);
        if (childElements.hasNext()) {
            refToMessageId = ((SOAPElement) childElements.next()).getValue();
        }
        else {
            throw new SOAPValidationException
                (SOAPValidationException.SOAP_FAULT_CLIENT,
                 "Missing <" + NAMESPACE_PREFIX_EB + ":" + MessageHeader.
                 ELEMENT_REF_TO_MESSAGE_ID + "> in <" + NAMESPACE_PREFIX_EB
                 + ":" + ACKNOWLEDGMENT + ">!");
        }

        fromPartyIds = new ArrayList();
        childElements = getChildElements(MessageHeader.ELEMENT_FROM);
        if (childElements.hasNext()) {
            from = new ExtensionElementImpl
                (soapEnvelope, (SOAPElement) childElements.next());

            if (childElements.hasNext()) {
                throw new SOAPValidationException(SOAPValidationException.
                     SOAP_FAULT_CLIENT, "More than one <" + NAMESPACE_PREFIX_EB
                     + ":" + MessageHeader.ELEMENT_FROM + "> in <" +
                     NAMESPACE_PREFIX_EB + ":" + ACKNOWLEDGMENT + ">!");
            }

            Iterator partyIds = from.getChildElements
                (MessageHeader.ELEMENT_PARTY_ID);
            if (partyIds.hasNext()) {
                while (partyIds.hasNext()) {
                    ExtensionElement partyId = new ExtensionElementImpl
                        (soapEnvelope, (SOAPElement) partyIds.next());
                    fromPartyIds.add(new MessageHeader.PartyId(partyId.
                        getValue(), partyId.getAttributeValue
                        (MessageHeader.ATTRIBUTE_TYPE)));
                }
            }
            else {
                throw new SOAPValidationException(SOAPValidationException.
                     SOAP_FAULT_CLIENT, "Missing <" + NAMESPACE_PREFIX_EB +
                     ":" + MessageHeader.ELEMENT_PARTY_ID + "> in <" +
                     NAMESPACE_PREFIX_EB + ":" + MessageHeader.ELEMENT_FROM +
                     ">!");
            }
        }
        else {
            from = null;
        }

        signatureReferences = new ArrayList();
        childElements = getChildElements(soapEnvelope.createName
            (SignatureReference.SIGNATURE_REFERENCE, Signature.
             NAMESPACE_PREFIX_DS, Signature.NAMESPACE_URI_DS));
        while (childElements.hasNext()) {
            signatureReferences.add(new SignatureReference
                (soapEnvelope, (SOAPElement) childElements.next()));
        }
    }

    /** 
     * Get the time that the message being acknowledged is received.
     * 
     * @return Timestamp expressed in UTC format.
     */
    public String getTimestamp() {
        return timestamp;
    }

    /** 
     * Get the identifier of the message being acknowledged.
     * 
     * @return Identifier of the message being acknowledged.
     */
    public String getRefToMessageId() {
        return refToMessageId;
    }

    /** 
     * Add sender's PartyID into this <code>Acknowledgement</code>
     * 
     * @param id    Sender's PartyID string.
     * @throws SOAPException 
     */
    public void addFromPartyId(String id)
        throws SOAPException {
        addFromPartyId(id, null);
    }

    /** 
     * Add sender's PartyID and its type into this <code>Acknowledgement</code>
     * 
     * @param id    Sender's PartyID string.
     * @param type  PartyID type.
     * @throws SOAPException 
     */
    public void addFromPartyId(String id, String type)
        throws SOAPException {
        if (from == null) {
            from = addChildElement(MessageHeader.ELEMENT_FROM);
        }

        ExtensionElement fromPartyId = from.
            addChildElement(MessageHeader.ELEMENT_PARTY_ID, id);
        if (type != null) {
            fromPartyId.addAttribute(MessageHeader.ATTRIBUTE_TYPE, type);
        }
        fromPartyIds.add(new MessageHeader.PartyId(id, type));
    }

    /** 
     * Get the identifiers of the party generating the acknowledgement response
     *
     * @return Iterator of <code>MessageHeader.PartyId</code>
     */
    public Iterator getFromPartyIds() {
        return fromPartyIds.iterator();
    }

    public void addSignatureReference(SignatureReference reference)
        throws SOAPException {
        SOAPElement child =
            addChildElement(getSOAPElement(), reference.getSOAPElement());
        signatureReferences.add(new SignatureReference(soapEnvelope, child));
    }

    public Iterator getSignatureReferences() {
        return signatureReferences.iterator();
    }

    private SOAPElement addChildElement(SOAPElement parent, SOAPElement child)
        throws SOAPException {
        Name childName = child.getElementName();
        Name name = soapEnvelope.createName(childName.getLocalName(),
            childName.getPrefix(), childName.getURI());
        SOAPElement newChild = parent.addChildElement(name);
        for (Iterator i=child.getAllAttributes() ; i.hasNext() ; ) {
            childName = (Name) i.next();
            name = soapEnvelope.createName(childName.getLocalName(),
                childName.getPrefix(), childName.getURI());
            newChild.addAttribute(name, child.getAttributeValue(childName));
        }

        for (Iterator i=child.getChildElements() ; i.hasNext() ; ) {
            Object node = i.next();
            if (node instanceof Text) {
                newChild.addTextNode(((Text) node).getValue());
            }
            else if (node instanceof SOAPElement) {
                addChildElement(newChild, (SOAPElement) node);
            }
        }

        return newChild;
    }
}
