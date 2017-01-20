/* ===== 
 *
 * $Header: /home/cvsroot/ebxml-pkg/src/hk/hku/cecid/ebms/pkg/Description.java,v 1.1 2005/07/28 09:36:24 dcmsze Exp $
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

/**
 * A <code>Description</code> inside a <code>Reference</code>
 *
 * @author cyng
 * @version $Revision: 1.1 $
 */

public class Description {

    static final String DESCRIPTION = "Description";

    private final String description;

    private final String lang;

    Description(String description, String lang) {
        this.description = description;
        this.lang = lang;
    }

    public String getDescription() {
        return description;
    }

    public String getLang() {
        return lang;
    }
}
