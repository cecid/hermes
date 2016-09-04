/*
 * Copyright(c) 2002 Center for E-Commerce Infrastructure Development, The
 * University of Hong Kong (HKU). All Rights Reserved.
 *
 * This software is licensed under the Academic Free License Version 1.0
 *
 * Academic Free License
 * Version 1.0
 *
 * This Academic Free License applies to any software and associated 
 * documentation (the "Software") whose owner (the "Licensor") has placed the 
 * statement "Licensed under the Academic Free License Version 1.0" immediately 
 * after the copyright notice that applies to the Software. 
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy 
 * of the Software (1) to use, copy, modify, merge, publish, perform, 
 * distribute, sublicense, and/or sell copies of the Software, and to permit 
 * persons to whom the Software is furnished to do so, and (2) under patent 
 * claims owned or controlled by the Licensor that are embodied in the Software 
 * as furnished by the Licensor, to make, use, sell and offer for sale the 
 * Software and derivative works thereof, subject to the following conditions: 
 *
 * - Redistributions of the Software in source code form must retain all 
 *   copyright notices in the Software as furnished by the Licensor, this list 
 *   of conditions, and the following disclaimers. 
 * - Redistributions of the Software in executable form must reproduce all 
 *   copyright notices in the Software as furnished by the Licensor, this list 
 *   of conditions, and the following disclaimers in the documentation and/or 
 *   other materials provided with the distribution. 
 * - Neither the names of Licensor, nor the names of any contributors to the 
 *   Software, nor any of their trademarks or service marks, may be used to 
 *   endorse or promote products derived from this Software without express 
 *   prior written permission of the Licensor. 
 *
 * DISCLAIMERS: LICENSOR WARRANTS THAT THE COPYRIGHT IN AND TO THE SOFTWARE IS 
 * OWNED BY THE LICENSOR OR THAT THE SOFTWARE IS DISTRIBUTED BY LICENSOR UNDER 
 * A VALID CURRENT LICENSE. EXCEPT AS EXPRESSLY STATED IN THE IMMEDIATELY 
 * PRECEDING SENTENCE, THE SOFTWARE IS PROVIDED BY THE LICENSOR, CONTRIBUTORS 
 * AND COPYRIGHT OWNERS "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR 
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, 
 * FITNESS FOR A PARTICULAR PURPOSE AND NON-INFRINGEMENT. IN NO EVENT SHALL THE 
 * LICENSOR, CONTRIBUTORS OR COPYRIGHT OWNERS BE LIABLE FOR ANY CLAIM, DAMAGES 
 * OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, 
 * ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE. 
 *
 * This license is Copyright (C) 2002 Lawrence E. Rosen. All rights reserved. 
 * Permission is hereby granted to copy and distribute this license without 
 * modification. This license may not be modified without the express written 
 * permission of its copyright owner. 
 */

/* ===== 
 *
 * $Header: /home/cvsroot/ebxml-pkg/src/hk/hku/cecid/ebms/pkg/AttachmentDataSource.java,v 1.1 2005/07/28 09:36:24 dcmsze Exp $
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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.activation.DataSource;
import javax.mail.MessagingException;
import javax.mail.internet.MimeUtility;
/**
 * This is an implementation of <code>javax.activation.DataSource</code>
 * that encapsulates attachment data in a SOAP message.
 *
 * @author cyng
 * @version $Revision: 1.1 $
 */
public class AttachmentDataSource implements DataSource {

    /** 
     * Content type of the attachment.
     */
    private String contentType;

    /**
     * Content-Transfer-Encoding of the attachment.
     */
    private String encoding;

    /** 
     * Name of the attachment.
     */
    private String name;

    /** 
     * Attachment data.
     */
    private byte[] data;

    private long offset;

    private long length;
    
    private DataSource dataSource;

    /** 
     * Constructs an <code>AttachmentDataSource</code> object from an array
     * of binary data.
     * 
     * @param data              Byte array containing data for the
     *                          <code>AttachmentDataSource</code>
     * @param contentType       Content type of the data.
     */
    public AttachmentDataSource(byte[] data, String contentType) {
        this(data, contentType, null);
    }

    /** 
     * Constructs an <code>AttachmentDataSource</code> object from an array
     * of binary data, and assign a name to the data source.
     * 
     * @param data              Byte array containing data for the
     *                          <code>AttachmentDataSource</code>
     * @param contentType       Content type of the data.
     * @param name              Name assigned to the 
     *                          <code>AttachmentDataSource</code>
     */
    public AttachmentDataSource(byte[] data, String contentType, String name) {
        this(data, contentType, null, name);
    }

    /** 
     * Constructs an <code>AttachmentDataSource</code> object from an array
     * of binary data, and assign a name to the data source.
     * 
     * @param data              Byte array containing data for the
     *                          <code>AttachmentDataSource</code>
     * @param contentType       Content type of the data.
     * @param encoding          Content-Transfer-Encoding of the data.
     * @param name              Name assigned to the 
     *                          <code>AttachmentDataSource</code>
     */
    public AttachmentDataSource(byte[] data, String contentType,
                                String encoding, String name) {
        this.contentType = contentType;
        this.encoding = encoding;
        if (name == null) {
            this.name = AttachmentDataSource.class.getName();
        }
        else {
            this.name = name;
        }
        this.data = data;
        offset = 0;
        length = data.length;
    }

    /** 
     * Constructs an <code>AttachmentDataSource</code> object from a file.
     * 
     * @param fileName          Name of the file to be loaded.
     * @param contentType       Content type of the file.
     * @throws IOException 
     */
    public AttachmentDataSource(String fileName, String contentType)
        throws IOException {
        this(fileName, contentType, false);
    }

    /** 
     * Constructs an <code>AttachmentDataSource</code> object from a file.
     * 
     * @param fileName          Name of the file to be loaded.
     * @param contentType       Content type of the file.
     * @param loadToMem         Load all data to memory upon creation
     * @throws IOException 
     */
    public AttachmentDataSource(String fileName, String contentType,
                                boolean loadToMem)
        throws IOException {
        this(new File(fileName), contentType, loadToMem);
    }

    /** 
     * Constructs an <code>AttachmentDataSource</code> object from a
     * <code>File</code> object.
     * 
     * @param file              <code>File</code> object containing information
     *                          on the file to be loaded.
     * @param contentType       Content type of the file.
     * @throws IOException 
     */
    public AttachmentDataSource(File file, String contentType)
        throws IOException {
        this(file, contentType, false);
    }

    /** 
     * Constructs an <code>AttachmentDataSource</code> object from a
     * <code>File</code> object.
     * 
     * @param file              <code>File</code> object containing information
     *                          on the file to be loaded.
     * @param contentType       Content type of the file.
     * @param loadToMem         Load all data to memory upon creation
     * @throws IOException 
     */
    public AttachmentDataSource(File file, String contentType, 
                                boolean loadToMem)
        throws IOException {

        this.contentType = contentType;
        this.encoding = null;

        if (loadToMem) {
            this.name = file.getName();
            loadData(new FileInputStream(file));
        }
        else {
            this.name = file.getCanonicalPath();
            this.data = null;
            this.offset = 0;
            this.length = file.length();
        }
    }

    /** 
     * Constructs an <code>AttachmentDataSource</code> object from an
     * <code>InputStream</code>.
     * 
     * @param in            <code>InputStream</code> from which the data is
     *                      read and stored in the data source.
     * @param contentType   Content type of the data.
     * @throws IOException 
     */
    public AttachmentDataSource(InputStream in, String contentType)
        throws IOException {
        this(in, contentType, null);
    }

    /** 
     * Constructs an <code>AttachmentDataSource</code> object from an
     * <code>InputStream</code> with a given name.
     * 
     * @param in            <code>InputStream</code> from which the data
     *                      is read and stored in the data source.
     * @param contentType   Content type of the data.
     * @param name          Name assigned to the 
     *                      <code>AttachmentDataSource</code>.
     * @throws IOException 
     */
    public AttachmentDataSource(InputStream in, String contentType,
                                String name)
        throws IOException {
        this(in, contentType, null, name);
    }

    /** 
     * Constructs an <code>AttachmentDataSource</code> object from an
     * <code>InputStream</code> with the specified Content-Transfer-Encoding
     * and name.
     * 
     * @param in            <code>InputStream</code> from which the data
     *                      is read and stored in the data source.
     * @param contentType   Content type of the data.
     * @param encoding      Content-Transfer-Encoding of the data.
     * @param name          Name assigned to the 
     *                      <code>AttachmentDataSource</code>.
     * @throws IOException 
     */
    public AttachmentDataSource(InputStream in, String contentType,
                                String encoding, String name)
        throws IOException {
        this.contentType = contentType;
        this.encoding = encoding;
        if (name == null) {
            this.name = AttachmentDataSource.class.getName();
        }
        else {
            this.name = name;
        }
        loadData(in);
    }

    /** 
     * Constructs an <code>AttachmentDataSource</code> object using the data
     * of <code>length</code> in a file starting from <code>offset</code>
     * with the specified Content-Type.
     * 
     * @param fileName          Name of the file to be loaded.
     * @param offset            Offset from the start of the file.
     * @param length            Length of data to be read.
     * @param contentType       Content type of the data.
     */
    public AttachmentDataSource(String fileName, long offset, long length,
                                String contentType)
        throws IOException {
        this(fileName, offset, length, contentType, false);
    }

    /** 
     * Constructs an <code>AttachmentDataSource</code> object using the data
     * of <code>length</code> in a file starting from <code>offset</code>
     * with the specified Content-Type.
     * 
     * @param fileName          Name of the file to be loaded.
     * @param offset            Offset from the start of the file.
     * @param length            Length of data to be read.
     * @param contentType       Content type of the data.
     * @param loadToMem         Load all data to memory upon creation
     */
    public AttachmentDataSource(String fileName, long offset, long length,
                                String contentType, boolean loadToMem)
        throws IOException {
        this(fileName, offset, length, contentType, null, loadToMem);
    }

    /** 
     * Constructs an <code>AttachmentDataSource</code> object using the data
     * of <code>length</code> in a file starting from <code>offset</code>.
     * with the specified Content-Type and Content-Transfer-Encoding.
     * 
     * @param fileName          Name of the file to be loaded.
     * @param offset            Offset from the start of the file.
     * @param length            Length of data to be read.
     * @param contentType       Content type of the data.
     * @param encoding          Content-Transfer-Encoding of the data.
     */
    public AttachmentDataSource(String fileName, long offset, long length,
                                String contentType, String encoding)
        throws IOException {
        this(fileName, offset, length, contentType, encoding, false);
    }

    /** 
     * Constructs an <code>AttachmentDataSource</code> object using the data
     * of <code>length</code> in a file starting from <code>offset</code>.
     * with the specified Content-Type and Content-Transfer-Encoding.
     * 
     * @param fileName          Name of the file to be loaded.
     * @param offset            Offset from the start of the file.
     * @param length            Length of data to be read.
     * @param contentType       Content type of the data.
     * @param encoding          Content-Transfer-Encoding of the data.
     * @param loadToMem         Load all data to memory upon creation
     */
    public AttachmentDataSource(String fileName, long offset, long length,
                                String contentType, String encoding, 
                                boolean loadToMem)
        throws IOException {
        if (loadToMem) {
            final File file = new File(name);
            if (file.length() < (offset + length)) {
                throw new IOException("Premature end-of-file: file name=" +
                    name + " | file length=" + file.length() + " | offset=" +
                    offset + " | length to be read=" + length);
            }
            final FileInputStream fis = new FileInputStream(name);
            final InputStream is = new PartialInputStream(fis, offset, length);
            loadData(is);
        }
        else {
            this.name = fileName;
            this.data = null;
            this.offset = offset;
            this.length = length;
        }
        this.contentType = contentType;
        this.encoding = encoding;
    }
    
    public AttachmentDataSource(DataSource dataSource, long offset, long length,
            String contentType, String encoding, boolean loadToMem)
                    throws IOException {
        if (loadToMem) {
            InputStream fis = dataSource.getInputStream();
            final InputStream is = new PartialInputStream(fis, offset, length);
            loadData(is);
            this.contentType = contentType;
            this.encoding = encoding;
        }
        else {
            this.dataSource = dataSource;
            this.contentType = contentType;
            this.encoding = encoding;
            this.data = null;
            this.offset = offset;
            this.length = length;
        }
    }

    private void loadData(InputStream is) throws IOException {
        try {
            final byte[] buffer = new byte[4096];
            final ByteArrayOutputStream out = new ByteArrayOutputStream();
            for (int c=is.read(buffer) ; c!=-1 ; c=is.read(buffer))
                out.write(buffer, 0, c);
            this.data = out.toByteArray();
            this.offset = 0;
            this.length = data.length;
            out.close();
        } catch (IOException e) {
            throw e;
        } finally {
            if (is != null) {
                is.close();
            }
        }
    }

    /** 
     * Gets the content type of data stored in this 
     * <code>AttachmentDataSource</code>.
     * 
     * @return Content type of data stored in data source.
     */
    public String getContentType() {
        return contentType;
    }

    /** 
     * Gets <code>InputStream</code> from which data in the
     * <code>AttachmentDataSource</code> can be read.
     * 
     * @return An <code>InputStream</code>.
     * @throws IOException 
     */
    public InputStream getInputStream() throws IOException {
        final InputStream is;
        if (data != null) {
            is = new ByteArrayInputStream(data);
        }
        else {
            InputStream fis = null;
            if (dataSource != null) {
                fis = dataSource.getInputStream();
            } else {
                final File file = new File(name);
                if (file.length() < (offset + length)) {
                    throw new IOException("Premature end-of-file: file name=" +
                        name + " | file length=" + file.length() + " | offset=" +
                        offset + " | length to be read=" + length);
                }
                fis = new FileInputStream(name);
            }
            is = new PartialInputStream(fis, offset, length);
        }

        if (encoding == null) {
            return is;
        }
        else {
            try {
                return MimeUtility.decode(is, encoding);
            }
            catch (MessagingException e) {
                throw new IOException(e.getMessage());
            }
        }
    }

    /** 
     * Gets the name of the <code>AttachmentDataSource</code>.
     * 
     * @return Name of data source.
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name of the <code>AttachmentDataSource</code>.
     *
     * @param name              Name of data source.
     */
    public void setName(String name) {
        this.name = name;
    }

    /** 
     * This method should never be called. It implements the 
     * <code>getOutputStream()</code> method of the 
     * <code>javax.activation.DataSource</code> interface and 
     * <code>IOException</code> will be thrown as the result of invocation.
     * 
     * @return This method always result in <code>IOException</code>; it never
     *         returns normally.
     * @throws IOException 
     */
    public OutputStream getOutputStream() throws IOException {
        throw new IOException("Program bug! " + AttachmentDataSource.class.
            getName() + ".getOutputStream() should not be invoked!");
    }

    /** 
     * Gets a byte array of data in this data source.
     * 
     * @return A byte array of data.
     */
    public byte[] getByteArray() throws IOException {
        if (data != null) {
            return data;
        }
        else {
            final ByteArrayOutputStream out = new ByteArrayOutputStream();
            InputStream fis = null;
            try {
                if (dataSource != null) {
                    fis = dataSource.getInputStream();
                } else {
                    fis = new FileInputStream(name);
                }
                final PartialInputStream in = new PartialInputStream
                    (fis, offset, length);
                final byte[] buffer = new byte[4096];
                for (int c=in.read(buffer) ; c!=-1 ; c=in.read(buffer))
                    out.write(buffer, 0, c);
                return out.toByteArray();
            } catch (IOException e) {
                throw e;
            } finally {
                if (fis != null) {
                    try {
                        out.close();
                        fis.close();
                    } catch (IOException e) {
                        throw e;
                    }
                }
            }
        }
    }

    /**
     * Gets the length of data in this data source
     *   
     * @return length of data
     */
    public long getLength() {
        if (data != null) {
            return data.length;
        }
        else {
            return length;
        }
    }
}
