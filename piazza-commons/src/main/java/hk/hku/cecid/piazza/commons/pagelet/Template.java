/* 
 * Copyright(c) 2005 Center for E-Commerce Infrastructure Development, The
 * University of Hong Kong (HKU). All Rights Reserved.
 *
 * This software is licensed under the GNU GENERAL PUBLIC LICENSE Version 2.0 [1]
 * 
 * [1] http://www.gnu.org/licenses/old-licenses/gpl-2.0.txt
 */

package hk.hku.cecid.piazza.commons.pagelet;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A Template is a parser which tokenizes a given template into a list of
 * template elements. There are two types of template elements:
 * <ol>
 * <li>Text
 * <li>Template Tag
 * </ol>
 * <p>
 * Hence, a template is composed of a mixture of text and template tags.
 * A template tag is recognized by the following pattern in the content:
 * <code>&lt;!--[\\s]*template-((.*?))[\\s]*--&gt;</code>
 * 
 * @see TemplateElement
 * 
 * @author Hugo Y. K. Lam
 *
 */
public class Template {

    private String template;
    
    private Iterator iterator;
    
    /**
     * Creates a new instance of Template.
     * 
     * @param s the template.
     */
    public Template(String s) {
        template = s==null? "": s;
    }
    
    /**
     * Parses the template content and initializes this template.
     */
    public void parse() {
        List elements = new ArrayList();

        String regexp = "<!--[\\s]*template-((.*?))[\\s]*-->";
        Pattern p = Pattern.compile(regexp, Pattern.CASE_INSENSITIVE);
        Matcher m = p.matcher(template);
        
        int cur = 0;
        for (int i=0; m.find(); i++) {
            if (m.start()>cur) {
                elements.add(new TemplateElement(template.substring(cur, m.start()), true));
            }
            elements.add(new TemplateElement(m.group(1)));
            cur = m.end();
        }
        if (cur < template.length()) {
            elements.add(new TemplateElement(template.substring(cur, template.length()), true));
        }
        
        iterator = elements.iterator();
    }
    
    /**
     * Checks if there are anymore elements in this template. This method will 
     * always return false if this template is not yet parsed. More, each 
     * invocation of nextElement() may also affect the result of this method.  
     * 
     * @return true if there are more elements in this template.
     * 
     * @see #nextElement()
     */
    public boolean hasMoreElements() {
        return iterator != null && iterator.hasNext();
    }
    
    /**
     * Gets the next template element. This method will always return null if 
     * this template is not yet parsed. 
     * 
     * @return the next template element.
     */
    public TemplateElement nextElement() {
        return iterator == null? null:(TemplateElement)iterator.next();
    }
    
    /**
     * Returns a string representation of this template. This method simply 
     * returns the original template content.
     * 
     * @return a string representation of this template.
     * @see java.lang.Object#toString()
     */
    public String toString() {
        return template;
    }
}
