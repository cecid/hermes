/* =====
 *
 * $Header: /home/cvsroot/ebxml-pkg/src/hk/hku/cecid/ebms/pkg/validation/HeaderValidator.java,v 1.1 2005/07/28 09:36:24 dcmsze Exp $
 *
 * Code authored by:
 *
 * frankielam [2002-11-14]
 *
 * Code reviewed by:
 *
 * username [YYYY-MM-DD]
 *
 * Remarks:
 *
 * =====
 */
package hk.hku.cecid.ebms.pkg.validation;

import hk.hku.cecid.ebms.pkg.EbxmlMessage;
import hk.hku.cecid.ebms.pkg.MessageHeader;
import hk.hku.cecid.ebms.pkg.PayloadContainer;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import java.net.URI;
import java.net.URISyntaxException;

/** 
 * Class for validating the header of an ebXML message. 
 * <code>EbxmlValidationException</code> will be thrown in case of error.
 * 
 * @author  Frankie Lam
 * @version $Revision: 1.1 $
 */
public class HeaderValidator {

    /** 
     * Validate the header of an ebXML message.
     * 
     * @param ebxmlMessage  The header of the <code>EbxmlMessage</code> object
     *                      to be validated.
     */
    public void validate(EbxmlMessage ebxmlMessage) 
        throws EbxmlValidationException {

        try {
            // Validate Party IDs
            validatePartyId(ebxmlMessage.getFromPartyIds());
            validatePartyId(ebxmlMessage.getToPartyIds());

            // Validate Service name 
            validateService(ebxmlMessage.getService(), 
                            ebxmlMessage.getServiceType());

            // Validate payload
            validatePayload(ebxmlMessage);
        }
        catch (EbxmlValidationException e) {
            e.setRefToMessage(ebxmlMessage);
            throw e;
        }
    }

    /** 
     * Validates the list of party IDs. The following rules are enforced:
     * <ul>
     * <li>Attribute "type" MUST be unique within the list of party IDs.</li>
     * <li>The party ID without the "type" attribute MUST be a URI.</li>
     * </ul>
     * 
     * @param partyIds 
     */
    protected void validatePartyId(Iterator partyIds) 
        throws EbxmlValidationException {
        Set typeSet = new HashSet();
        while (partyIds.hasNext()) {
            MessageHeader.PartyId partyId = 
                (MessageHeader.PartyId)partyIds.next();
            String type = partyId.getType();
            if (typeSet.contains(type)) {
                throw new EbxmlValidationException(
                    EbxmlValidationException.EBXML_ERROR_INCONSISTENT,
                    EbxmlValidationException.SEVERITY_ERROR,
                    "Duplicate type attribute in Party ID", null);
            }
            else {
                typeSet.add(type);
                if (type == null || type.length() == 0) {
                    if (validateURI(partyId.getId()) == false) {
                        throw new EbxmlValidationException(
                            EbxmlValidationException.EBXML_ERROR_INCONSISTENT,
                            EbxmlValidationException.SEVERITY_ERROR,
                            "Party ID without a type attribute must be a URI",
                            null);
                    }
                }
            }
        }
    }

    /** 
     * Validate service element. The following rules are enforced:
     * - Service attribute must be a URI in the absence of type attribute.
     * 
     * @param service 
     */
    protected void validateService(String service, String serviceType) 
        throws EbxmlValidationException {
        if (serviceType == null && validateURI(service) == false) {
            throw new EbxmlValidationException(
                EbxmlValidationException.EBXML_ERROR_INCONSISTENT,
                EbxmlValidationException.SEVERITY_ERROR,
                "Service without a type attribute must be a URI", null);
        }
    }

    /**
     * Validate payload. The following checks are applied:
     * - All payload references with "cid:" as the prefix must exist.
     * - All payload references must be a valid URI.
     */
    protected void validatePayload(EbxmlMessage ebxmlMessage)
        throws EbxmlValidationException {

        // Check payload references
        String payloadInError = ebxmlMessage.getPayloadInError();
        if (payloadInError != null) {
            throw new EbxmlValidationException(
                EbxmlValidationException.EBXML_ERROR_MIME_PROBLEM,
                EbxmlValidationException.SEVERITY_ERROR,
                "A manifest entry refers to non-existent payload",
                payloadInError);
        }

        // Check if all payload references are valid URIs
        Iterator it = ebxmlMessage.getPayloadContainers();
        while (it.hasNext()) {
            PayloadContainer payload = (PayloadContainer)it.next();
            String href = payload.getHref();
            if (!validateURI(href)) {
                throw new EbxmlValidationException(
                    EbxmlValidationException.EBXML_ERROR_MIME_PROBLEM,
                    EbxmlValidationException.SEVERITY_ERROR,
                    "The xlink:href element of a Manifest/Reference element "
                    + "must be a URI.", null);
            }
        }
    }

    /** 
     * Check if a URI is valid.
     * 
     * @param uri   URI in string format to be validated.
     * @return true if the URI is valid; false otherwise.
     */
    protected boolean validateURI(String uri) {
        try {
			new URI(uri);
	        return true;
		} catch (URISyntaxException e) {
			return false;
		}
    }
}
