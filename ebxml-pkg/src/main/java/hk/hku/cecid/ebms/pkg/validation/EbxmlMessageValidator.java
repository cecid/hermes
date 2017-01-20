/* =====
 *
 * $Header: /home/cvsroot/ebxml-pkg/src/hk/hku/cecid/ebms/pkg/validation/EbxmlMessageValidator.java,v 1.1 2005/07/28 09:36:24 dcmsze Exp $
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
/** 
 * Class for validating an ebXML message. 
 * <code>EbxmlValidationException</code> will be thrown in case of error.
 * 
 * @author  Frankie Lam
 * @version $Revision: 1.1 $
 */
public class EbxmlMessageValidator {

    public void validate(EbxmlMessage ebxmlMessage) 
        throws EbxmlValidationException{
        new HeaderValidator().validate(ebxmlMessage);
        new BodyValidator().validate(ebxmlMessage);
    }
}
