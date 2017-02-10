/* =====
 *
 * $Header: /home/cvsroot/ebxml-pkg/src/hk/hku/cecid/ebms/pkg/Constants.java,v 1.1 2005/07/28 09:36:24 dcmsze Exp $
 *
 * Code authored by:
 *
 * frankielam [2003-01-07]
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
 * This class serves as a bag containing all the constants that fall in the
 * following criteria:
 *
 * (1) Related to property settings
 * (2) Public strings
 * (3) Previously public strings that are declared multiple times in various
 *     locations.
 *
 * @author  Frankie Lam
 * @version $Revision: 1.1 $
 */
public class Constants {

    /**
     * HTTP Header attribute specifying content type
     */
    public static final String CONTENT_TYPE = "Content-Type";

    /**
     * HTTP Header attribute specifying content length.
     */
    public static final String CONTENT_LENGTH = "Content-Length";

    /**
     * HTTP Header attribute specifying content id.
     */
    public static final String CONTENT_ID = "Content-Id";

    /**
     * HTTP Header attribute specifying content transfer encoding.
     */
    public static final String CONTENT_TRANSFER_ENCODING =
        "Content-Transfer-Encoding";

    public static final String DEFAULT_CONTENT_TRANSFER_ENCODING = "binary";

    /**
     * MIME boundary.
     */
    public static final String MIME_BOUNDARY = "boundary";

    /**
     * Prefix to be applied to separate different parts of MIME data.
     */
    public static final String MIME_BOUNDARY_PREFIX = "--";

    /**
     * HTTP content type specifying xml data.
     */
    public static final String TEXT_XML_TYPE = "text/xml";

    /**
     * multipart/related content type of MIME message.
     */
    public static final String MULTIPART_RELATED = "multipart/related";

    /**
     * HTTP content type for multi-part data, which is used when the ebXML
     * message contains payload data.
     */
    public static final String MULTIPART_RELATED_TYPE =
        MULTIPART_RELATED
            + "; type=\""
            + TEXT_XML_TYPE
            + "\"; "
            + MIME_BOUNDARY
            + "=";

    /**
     * Content type character set attribute.
     */
    public static final String CHARACTER_SET = "charset";

    /**
     * Default XML character encoding.
     */
    public static final String CHARACTER_ENCODING = "UTF-8";

    /** 
     * CRLF
     */
    public static final String CRLF = "\r\n";

    /*
     * XML tags for Exports
     */

    /**
     * Reference time zone.
     */
    public static final String TIME_ZONE = "GMT";

    /*
     * Default values for Directory Manager
     */

    /**
     * Content type start attribute.
     */
    public static final String START = "start";

    /**
     * HTTP content type specifying binary data, which is a serialized command
     * object in <code>MessageServiceHandler</code>.
     */
    public static final String SERIALIZABLE_OBJECT = "application/octet-stream";


}
