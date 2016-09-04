/* 
 * Copyright(c) 2005 Center for E-Commerce Infrastructure Development, The
 * University of Hong Kong (HKU). All Rights Reserved.
 *
 * This software is licensed under the GNU GENERAL PUBLIC LICENSE Version 2.0 [1]
 * 
 * [1] http://www.gnu.org/licenses/old-licenses/gpl-2.0.txt
 */

package hk.hku.cecid.piazza.commons.test.utils;

import java.io.File;
import java.io.FileFilter;

/**
 * @author Patrick Yip
 *
 */
public class ResFilter implements FileFilter {
	private boolean includeSVN;
	
	public ResFilter(boolean includeSVN){
		this.includeSVN = includeSVN;
	}
	
	/* (non-Javadoc)
	 * @see java.io.FileFilter#accept(java.io.File)
	 */
	public boolean accept(File file) {
		// TODO Auto-generated method stub
		if((includeSVN == false && file.getName().equals(".svn")) || file.getName().equals(ResSetter.RES_TMP_DIR))
			return false;
		else
			return true;
	}

}
