/* 
 * Copyright(c) 2005 Center for E-Commerce Infrastructure Development, The
 * University of Hong Kong (HKU). All Rights Reserved.
 *
 * This software is licensed under the GNU GENERAL PUBLIC LICENSE Version 2.0 [1]
 * 
 * [1] http://www.gnu.org/licenses/old-licenses/gpl-2.0.txt
 */

package hk.hku.cecid.piazza.commons.io;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

/**
 * FileSystem encapsulates a root directory and provides accessors for querying
 * and modifying this root. 
 * 
 * @author Hugo Y. K. Lam
 *  
 */
public class FileSystem {

    /**
     * Type File.
     */
    public static int TYPE_FILE = 1;

    /**
     * Type Directory.
     */
    public static int TYPE_DIR  = 2;

    /**
     * Type All.
     */
    public static int TYPE_ALL  = (TYPE_FILE | TYPE_DIR);

    
    /**
     * The root of this file system.
     */
    private File      root;

    /**
     * Creates a new instance of FileSystem.
     * 
     * @param root the root of this file system. If root is null, it will be set
     *            to the current user directory.
     */
    public FileSystem(File root) {
        if (root == null) {
            this.root = new File(System.getProperty("user.dir"));
        }
        else {
            root = root.getAbsoluteFile();

            if (root.isDirectory() || !root.exists()) {
                this.root = root;
            }
            else {
                this.root = root.getParentFile();
            }
        }
    }

    /**
     * Retrieves a collection of file-only File objects from the 
     * root of this file system.
     * 
     * @param isRecursive true if the search should be recursive.
     * @return a collection of File objects resulted from the search.
     */
    public Collection getFiles(boolean isRecursive) {
        return getFiles(isRecursive, TYPE_FILE, null);
    }

    /**
     * Retrieves a collection of file-only File objects from the 
     * root of this file system.
     * 
     * @param isRecursive true if the search should be recursive.
     * @param pattern the filename's pattern for filtering the result. null if
     *            no filtering should be applied.
     * @return a collection of File objects resulted from the search.
     */
    public Collection getFiles(boolean isRecursive, String pattern) {
        return getFiles(isRecursive, TYPE_FILE, pattern);
    }

    /**
     * Retrieves a collection of directory-only File objects from the 
     * root of this file system.
     * 
     * @param isRecursive true if the search should be recursive.
     * @return a collection of File objects resulted from the search.
     */
    public Collection getDirectories(boolean isRecursive) {
        return getFiles(isRecursive, TYPE_DIR, null);
    }

    /**
     * Retrieves a collection of directory-only File objects from the 
     * root of this file system.
     * 
     * @param isRecursive true if the search should be recursive.
     * @param pattern the filename's pattern for filtering the result. null if
     *            no filtering should be applied.
     * @return a collection of File objects resulted from the search.
     */
    public Collection getDirectories(boolean isRecursive, String pattern) {
        return getFiles(isRecursive, TYPE_DIR, pattern);
    }

    /**
     * Retrieves a collection of File objects from the root of this file system.
     * 
     * @param isRecursive true if the search should be recursive.
     * @param type the file type to be searched.
     * @param pattern the filename's pattern for filtering the result. null if
     *            no filtering should be applied.
     * @return a collection of File objects resulted from the search.
     */
    public Collection getFiles(boolean isRecursive, int type, String pattern) {
        return this.getFiles(null, isRecursive, type, pattern);
    }

    /**
     * Retrieves a collection of File objects from the root of this file system.
     * 
     * @param c the collection into which the result will be stored.
     * @param isRecursive true if the search should be recursive.
     * @param type the file type to be searched.
     * @param pattern the filename's pattern for filtering the result. null if
     *            no filtering should be applied.
     * @return a collection of File objects resulted from the search.
     */
    public Collection getFiles(Collection c, boolean isRecursive, int type,
            String pattern) {
        return this.getFiles(c, root, isRecursive, type, pattern);
    }

    /**
     * Retrieves a collection of File objects from a specified directory.
     * 
     * @param c the collection into which the result will be stored.
     * @param rootDir the directory to be searched.
     * @param isRecursive true if the search should be recursive.
     * @param type the file type to be searched.
     * @param pattern the filename's pattern for filtering the result. null if
     *            no filtering should be applied.
     * @return a collection of File objects resulted from the search.
     */
    private Collection getFiles(Collection c, File rootDir,
            boolean isRecursive, int type, String pattern) {
        File[] subfiles = rootDir.listFiles();
        if (subfiles == null || subfiles.length == 0) {
            return Collections.EMPTY_LIST;
        }
        else {
            if (c == null) {
                c = new ArrayList();
            }
            for (int i = 0; i < subfiles.length; i++) {
                if (subfiles[i].isDirectory()) {
                    if ((TYPE_DIR & type) == TYPE_DIR) {
                        if (accept(subfiles[i], pattern)) {
                            c.add(subfiles[i]);
                        }
                    }
                    if (isRecursive) {
                        getFiles(c, subfiles[i], isRecursive, type, pattern);
                    }
                }
                else {
                    if ((TYPE_FILE & type) == TYPE_FILE) {
                        if (accept(subfiles[i], pattern)) {
                            c.add(subfiles[i]);
                        }
                    }
                }
            }
            return c;
        }
    }

    /**
     * Checks if the specified file should be accepted according to the given
     * pattern.
     * 
     * @param f the file to be checked.
     * @param pattern the filename's pattern to be checked against.
     * @return true if the specified file should be accepted.
     */
    private boolean accept(File f, String pattern) {
        if (pattern == null || f.getName().matches(pattern)) {
            return true;
        }
        else {
            return false;
        }
    }

    /**
     * Removes the files, including the directories, under the root directory of
     * this file system recursively. The operation ceases when it encounters any 
     * error in removing any file. If the operation is successful, the root 
     * directory itself will be removed as well.
     * 
     * @return true if and only if all the files are removed successfully.
     */
    public boolean remove() {
        return remove(true);
    }
    
    /**
     * Removes the files, including the directories, under the root of this file  
     * system recursively. The operation ceases when it encounters any error 
     * in removing any file.
     * 
     * @param isSelfRemoved true if the root directory itself should be removed.
     * @return true if and only if all the files are removed successfully.
     */
    public boolean remove(boolean isSelfRemoved) {
        return remove(root, isSelfRemoved, false);
    }
    
    /**
     * Removes the files, including the directories, under the specified 
     * directory recursively. The operation ceases when it encounters any error 
     * in removing any file.
     * 
     * @param rootDir the directory under which the files will be removed. 
     * @param isSelfRemoved true if the root directory itself should be removed.
     * @param isDeferAllowed true if the files can be removed on exit when necessary.
     * @return true if and only if all the files are removed successfully.
     */
    private boolean remove(File rootDir, boolean isSelfRemoved, boolean isDeferAllowed) {
        File[] subfiles = rootDir.listFiles();
        if (subfiles != null) {
            for (int i=0; i<subfiles.length; i++) {
                if (subfiles[i].isDirectory()) {
                    if (!remove(subfiles[i], true, isDeferAllowed)) {
                        if (!isDeferAllowed) {
                            return false;
                        }
                    }
                }
                else {
                    if (!subfiles[i].delete()) {
                        if (isDeferAllowed) {
                            subfiles[i].deleteOnExit();
                        }
                        else {
                            return false;
                        }
                    }
                }
            }
        }
        if (isSelfRemoved) {
            if (!rootDir.delete()) {
                if (isDeferAllowed) {
                    rootDir.deleteOnExit() ;
                }
                else {
                    return false;
                }
            }
        }
        return true;
    }
    
    /**
     * Removes the files, including the directories, under the root directory of
     * this file system recursively. If there are files that cannot be removed
     * immediately, the files will be deleted on exit. This method will also
     * remove the root directory itself. 
     */
    public void purge() {
        purge(true);
    }
    
    /**
     * Removes the files, including the directories, under the root directory of
     * this file system recursively. If there are files that cannot be removed
     * immediately, the files will be deleted on exit.
     * 
     * @param isSelfRemoved true if the root directory itself should be removed.
     */
    public void purge(boolean isSelfRemoved) {
        remove(root, isSelfRemoved, true);
    }
    
    /**
     * Gets the root of this file system.
     * 
     * @return the root of this file system.
     */
    public File getRoot() {
        return root;
    }
    
    /**
     * Checks if the root of this file system exists.
     * 
     * @return true if the root of this file system exists.
     */
    public boolean exists() {
        return root.exists();
    }
    
    /**
     * Returns the absolute path of the root of this file system.
     * 
     * @return the absolute path of the root of this file system.
     * @see java.lang.Object#toString()
     */
    public String toString() {
        return root.getAbsolutePath();
    }
}