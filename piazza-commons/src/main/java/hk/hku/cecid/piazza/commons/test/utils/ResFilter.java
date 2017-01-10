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
