/* 
 * Copyright(c) 2005 Center for E-Commerce Infrastructure Development, The
 * University of Hong Kong (HKU). All Rights Reserved.
 *
 * This software is licensed under the Apache License Version 2.0 [1]
 * 
 * [1] http://www.apache.org/licenses/LICENSE-2.0.txt
 */

package hk.hku.cecid.piazza.corvus.admin.menu;

import java.util.List;
import java.util.Vector;


/**
 * A MenuComponent represents a menu-type component having the following 
 * characteristics:
 * <ul>
 * <li>A sequence number which indicates the menu position.
 * <li>A name or caption of the menu.
 * <li>A description of the menu.
 * <li>A link of the menu.
 * <li>A parent menu.
 * <li>A list of child menus.
 * <li>Visibility.
 * </ul>
 * 
 * @author Hugo Y. K. Lam
 *
 */
public class MenuComponent implements Comparable {

    private int seqNo;
    
    private String id;
    
    private String name;
    
    private String description;

    private String link;
    
    private MenuComponent parent;
    
    private boolean visible = true;
    
    private List children = new Vector(); 
    
    /**
     * Gets the child menus.
     * 
     * @return the child menus.
     */
    public List getChildren() {
        return children;
    }
    
    /**
     * Sets the child menus.
     * 
     * @param children the child menus.
     */
    public void setChildren(List children) {
        this.children = children;
    }
    
    /**
     * Adds a child menu.
     * 
     * @param child the child menu to be added.
     */
    public void addChild(MenuComponent child) {
        if (children != null) {
            children.add(child);
        }
    }
    
    /**
     * Gets the description of this menu component.
     * 
     * @return the description of this menu component.
     */
    public String getDescription() {
        return description;
    }
    
    /**
     * Sets the description of this menu component.
     * 
     * @param description the description of this menu component.
     */
    public void setDescription(String description) {
        this.description = description;
    }
    
    /**
     * Gets the ID of this menu component.
     * 
     * @return the ID of this menu component.
     */
    public String getId() {
        return id;
    }
    
    /**
     * Sets the ID of this menu component.
     * 
     * @param id the ID of this menu component.
     */
    public void setId(String id) {
        this.id = id;
    }
    
    /**
     * Gets the link of this menu component.
     * 
     * @return the link of this menu component.
     */
    public String getLink() {
        return link;
    }
    
    /**
     * Sets the link of this menu component.
     * 
     * @param link the link of this menu component.
     */
    public void setLink(String link) {
        this.link = link;
    }
    
    /**
     * Gets the name of this menu component.
     * 
     * @return the name of this menu component.
     */
    public String getName() {
        return name;
    }
    
    /**
     * Sets the name of this menu component.
     * 
     * @param name the name of this menu component.
     */
    public void setName(String name) {
        this.name = name;
    }
    
    /**
     * Gets the parent menu component.
     * 
     * @return the parent menu component.
     */
    public MenuComponent getParent() {
        return parent;
    }
    
    /**
     * Sets the parent menu component.
     * 
     * @param parent the parent menu component.
     */
    public void setParent(MenuComponent parent) {
        this.parent = parent;
    }
    
    /**
     * Gets the sequence number of this menu component.
     * 
     * @return the sequence number of this menu component.
     */
    public int getSeqNo() {
        return seqNo;
    }
    
    /**
     * Sets the sequence number of this menu component.
     * 
     * @param seqNo the sequence number of this menu component.
     */
    public void setSeqNo(int seqNo) {
        this.seqNo = seqNo;
    }
    
    /**
     * Checks if this menu component is visible.
     * 
     * @return true if this menu component is visible.
     */
    public boolean isVisible() {
        return visible;
    }
    
    /**
     * Sets the visibility of this menu component.
     * 
     * @param visible true if this menu component is visible.
     */
    public void setVisible(boolean visible) {
        this.visible = visible;
    }
    
    /**
     * Compares this menu component to another menu component with their
     * sequence numbers. If the sequence numbers are the same, their IDs will be
     * compared. 
     * 
     * @see java.lang.Comparable#compareTo(java.lang.Object)
     */
    public int compareTo(Object o) {
        MenuComponent m = (MenuComponent)o;
        if (this.getSeqNo() > m.getSeqNo()) {
            return 1;
        }
        else if (this.getSeqNo() == m.getSeqNo()) {
            if (this.getId()!=null && m.getId()==null) {
                return 1; 
            }
            else if (this.getId()==null && m.getId()==null) {
                return 0; 
            } 
            else if (this.getId()==null && m.getId()!=null) {
                return -1; 
            } 
            else {
                return this.getId().compareTo(m.getId());
            }
        }
        else {
            return -1;
        }
    }
}
