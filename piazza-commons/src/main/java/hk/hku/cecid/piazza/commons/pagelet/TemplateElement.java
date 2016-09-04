/* 
 * Copyright(c) 2005 Center for E-Commerce Infrastructure Development, The
 * University of Hong Kong (HKU). All Rights Reserved.
 *
 * This software is licensed under the GNU GENERAL PUBLIC LICENSE Version 2.0 [1]
 * 
 * [1] http://www.gnu.org/licenses/old-licenses/gpl-2.0.txt
 */

package hk.hku.cecid.piazza.commons.pagelet;

/**
 * A TemplateElement represents a composing element of a template.
 * There are two types of template elements:
 * <ol>
 * <li>Text
 * <li>Template Tag
 * </ol>
 *
 * @see Template
 *  
 * @author Hugo Y. K. Lam
 *
 */
public class TemplateElement {    

    private String name;
    
    private String text;
    
    /**
     * Creates a new instance of TemplateElement which represents a template tag
     * element.
     * 
     * @param name the element name. 
     */
    public TemplateElement(String name) {
        this(name, false);
    }
    
    /**
     * Creates a new instance of TemplateElement.
     * 
     * @param s the element name or text if it is a text element.
     * @param isText true if this element is a text element.
     */
    public TemplateElement(String s, boolean isText) {
        if (isText) {
            text = s;
        }
        else {
            name = s;
        }
    }

    /**
     * Gets the element name.
     * 
     * @return the element name or null if it is a text element.
     */
    public String getName() {
        return name;
    }
    
    /**
     * Gets the text of this element.
     * 
     * @return the text of this element or null if it is a template tag element.
     */
    public String getText() {
        return text == null? "":text;
    }
    
    /**
     * Checks if this element is a text element.
     * 
     * @return true if this element is a text element.
     */
    public boolean isText() {
        return name == null;
    }
    
    /**
     * Returns a string representation of this element. 
     * 
     * @return the text of this element if it is a text element or the element 
     *         name otherwise.
     * @see java.lang.Object#toString()
     */
    public String toString() {
        return isText()? getText():getName();
    }
}