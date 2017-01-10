package hk.hku.cecid.piazza.commons.xpath;
import java.util.Vector;

/**
 * XPathFunction is an interface of any customized XPath functions.
 * 
 * @author Hugo Y. K. Lam
 *
 */
public interface XPathFunction {

    /**
     * Invoked when evaluating its corresponding function in the XPath.
     * 
     * @param args the arguments specifed in the XPath.
     * @return the result after execution.
     */
    public Object execute(Vector args);
}
