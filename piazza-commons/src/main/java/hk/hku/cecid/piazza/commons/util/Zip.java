/* 
 * Copyright(c) 2005 Center for E-Commerce Infrastructure Development, The
 * University of Hong Kong (HKU). All Rights Reserved.
 *
 * This software is licensed under the GNU GENERAL PUBLIC LICENSE Version 2.0 [1]
 * 
 * [1] http://www.gnu.org/licenses/old-licenses/gpl-2.0.txt
 */

package hk.hku.cecid.piazza.commons.util;

import hk.hku.cecid.piazza.commons.io.FileSystem;
import hk.hku.cecid.piazza.commons.io.IOHandler;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

/**
 * Zip is a tool which can compress a set of files contained in a directory into
 * a zip file. Likewise, it can also extract a set of files compressed in a zip
 * file to a directory.
 * 
 * @author Hugo Y. K. Lam
 *  
 */
public final class Zip {

    /**
     * Creates a new instance of Zip.
     */
    private Zip() {
    }

    /**
     * Compresses a set of files contained in a directory into a zip file.
     * 
     * @param toFile the zip file.
     * @param dir the directory which contains the files to be compressed.
     * @throws UtilitiesException if there is any error in the compression.
     */
    public static void compress(File toFile, File dir)
            throws UtilitiesException {
        try {
            if (dir == null || !dir.exists() || !dir.isDirectory()) {
                throw new UtilitiesException("Invalid input directory: " + dir);
            }

            FileOutputStream fos = new FileOutputStream(toFile);
            ZipOutputStream outs = new ZipOutputStream(fos);

            FileSystem fs = new FileSystem(dir);
            Iterator allFiles = fs.getFiles(true).iterator();

            while (allFiles.hasNext()) {
                File srcFile = (File) allFiles.next();
                String filepath = srcFile.getAbsolutePath();
                String dirpath = dir.getAbsolutePath();
                String entryName = filepath.substring(dirpath.length() + 1)
                        .replace('\\', '/');
                ZipEntry zipEntry = new ZipEntry(entryName);
                zipEntry.setTime(srcFile.lastModified());
                FileInputStream ins = new FileInputStream(srcFile);
                outs.putNextEntry(zipEntry);
                IOHandler.pipe(ins, outs);
                outs.closeEntry();
                ins.close();
            }

            outs.close();

        }
        catch (Exception e) {
            throw new UtilitiesException("Unable to compress zip file: "
                    + toFile, e);
        }
    }

    /**
     * Extracts a set of files compressed in a zip file to a directory.
     * 
     * @param fromFile the zip file.
     * @param dir the directory to which the files to be extracted. Current user
     *            directory will be chosen if it is null.
     * @throws UtilitiesException if there is any error in the extraction.
     */
    public static void extract(File fromFile, File dir)
            throws UtilitiesException {
        try {
            if (dir == null) {
                dir = new File(System.getProperty("user.dir"));
            }
            else if (!dir.exists()) {
                dir.mkdirs();
            }

            if (!dir.isDirectory()) {
                throw new UtilitiesException("Invalid output directory: " + dir);
            }

            ZipFile zipFile = new ZipFile(fromFile);
            ArrayList fileEntries = new ArrayList();

            Enumeration zipEntries = zipFile.entries();
            while (zipEntries.hasMoreElements()) {
                ZipEntry zipEntry = (ZipEntry) zipEntries.nextElement();
                if (zipEntry.isDirectory()) {
                    new File(dir, zipEntry.getName()).mkdirs();
                }
                else {
                    fileEntries.add(zipEntry);
                }
            }

            Iterator allFiles = fileEntries.iterator();
            while (allFiles.hasNext()) {
                ZipEntry fileEntry = (ZipEntry) allFiles.next();
                File destFile = new File(dir, fileEntry.getName());
                destFile.setLastModified(fileEntry.getTime());
                destFile.getParentFile().mkdirs();
                FileOutputStream outs = new FileOutputStream(destFile);
                InputStream ins = zipFile.getInputStream(fileEntry);
                IOHandler.pipe(ins, outs);
                ins.close();
                outs.close();
            }

            zipFile.close();
        }
        catch (Exception e) {
            throw new UtilitiesException("Unable to extract zip file: "
                    + fromFile, e);
        }
    }
}