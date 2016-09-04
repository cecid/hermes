/*
 * Copyright(c) 2002 Center for E-Commerce Infrastructure Development, The
 * University of Hong Kong (HKU). All Rights Reserved.
 *
 * This software is licensed under the Academic Free License Version 1.0
 *
 * Academic Free License
 * Version 1.0
 *
 * This Academic Free License applies to any software and associated
 * documentation (the "Software") whose owner (the "Licensor") has placed the
 * statement "Licensed under the Academic Free License Version 1.0" immediately
 * after the copyright notice that applies to the Software.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of the Software (1) to use, copy, modify, merge, publish, perform,
 * distribute, sublicense, and/or sell copies of the Software, and to permit
 * persons to whom the Software is furnished to do so, and (2) under patent
 * claims owned or controlled by the Licensor that are embodied in the Software
 * as furnished by the Licensor, to make, use, sell and offer for sale the
 * Software and derivative works thereof, subject to the following conditions:
 *
 * - Redistributions of the Software in source code form must retain all
 *   copyright notices in the Software as furnished by the Licensor, this list
 *   of conditions, and the following disclaimers.
 * - Redistributions of the Software in executable form must reproduce all
 *   copyright notices in the Software as furnished by the Licensor, this list
 *   of conditions, and the following disclaimers in the documentation and/or
 *   other materials provided with the distribution.
 * - Neither the names of Licensor, nor the names of any contributors to the
 *   Software, nor any of their trademarks or service marks, may be used to
 *   endorse or promote products derived from this Software without express
 *   prior written permission of the Licensor.
 *
 * DISCLAIMERS: LICENSOR WARRANTS THAT THE COPYRIGHT IN AND TO THE SOFTWARE IS
 * OWNED BY THE LICENSOR OR THAT THE SOFTWARE IS DISTRIBUTED BY LICENSOR UNDER
 * A VALID CURRENT LICENSE. EXCEPT AS EXPRESSLY STATED IN THE IMMEDIATELY
 * PRECEDING SENTENCE, THE SOFTWARE IS PROVIDED BY THE LICENSOR, CONTRIBUTORS
 * AND COPYRIGHT OWNERS "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NON-INFRINGEMENT. IN NO EVENT SHALL THE
 * LICENSOR, CONTRIBUTORS OR COPYRIGHT OWNERS BE LIABLE FOR ANY CLAIM, DAMAGES
 * OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE,
 * ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE.
 *
 * This license is Copyright (C) 2002 Lawrence E. Rosen. All rights reserved.
 * Permission is hereby granted to copy and distribute this license without
 * modification. This license may not be modified without the express written
 * permission of its copyright owner.
 */

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
