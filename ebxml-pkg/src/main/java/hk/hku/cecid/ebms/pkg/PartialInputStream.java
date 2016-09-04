/*
 *  Copyright(c) 2002 Center for E-Commerce Infrastructure Development, The
 *  University of Hong Kong (HKU). All Rights Reserved.
 *
 *  This software is licensed under the Academic Free License Version 1.0
 *
 *  Academic Free License
 *  Version 1.0
 *
 *  This Academic Free License applies to any software and associated
 *  documentation (the "Software") whose owner (the "Licensor") has placed the
 *  statement "Licensed under the Academic Free License Version 1.0" immediately
 *  after the copyright notice that applies to the Software.
 *
 *  Permission is hereby granted, free of charge, to any person obtaining a copy
 *  of the Software (1) to use, copy, modify, merge, publish, perform,
 *  distribute, sublicense, and/or sell copies of the Software, and to permit
 *  persons to whom the Software is furnished to do so, and (2) under patent
 *  claims owned or controlled by the Licensor that are embodied in the Software
 *  as furnished by the Licensor, to make, use, sell and offer for sale the
 *  Software and derivative works thereof, subject to the following conditions:
 *
 *  - Redistributions of the Software in source code form must retain all
 *  copyright notices in the Software as furnished by the Licensor, this list
 *  of conditions, and the following disclaimers.
 *  - Redistributions of the Software in executable form must reproduce all
 *  copyright notices in the Software as furnished by the Licensor, this list
 *  of conditions, and the following disclaimers in the documentation and/or
 *  other materials provided with the distribution.
 *  - Neither the names of Licensor, nor the names of any contributors to the
 *  Software, nor any of their trademarks or service marks, may be used to
 *  endorse or promote products derived from this Software without express
 *  prior written permission of the Licensor.
 *
 *  DISCLAIMERS: LICENSOR WARRANTS THAT THE COPYRIGHT IN AND TO THE SOFTWARE IS
 *  OWNED BY THE LICENSOR OR THAT THE SOFTWARE IS DISTRIBUTED BY LICENSOR UNDER
 *  A VALID CURRENT LICENSE. EXCEPT AS EXPRESSLY STATED IN THE IMMEDIATELY
 *  PRECEDING SENTENCE, THE SOFTWARE IS PROVIDED BY THE LICENSOR, CONTRIBUTORS
 *  AND COPYRIGHT OWNERS "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *  FITNESS FOR A PARTICULAR PURPOSE AND NON-INFRINGEMENT. IN NO EVENT SHALL THE
 *  LICENSOR, CONTRIBUTORS OR COPYRIGHT OWNERS BE LIABLE FOR ANY CLAIM, DAMAGES
 *  OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE,
 *  ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE.
 *
 *  This license is Copyright (C) 2002 Lawrence E. Rosen. All rights reserved.
 *  Permission is hereby granted to copy and distribute this license without
 *  modification. This license may not be modified without the express written
 *  permission of its copyright owner.
 */
/*
 *  =====
 *
 *  $Header: /home/cvsroot/ebxml-pkg/src/hk/hku/cecid/ebms/pkg/PartialInputStream.java,v 1.1 2005/07/28 09:36:24 dcmsze Exp $
 *
 *  Code authored by:
 *
 *  cyng [2002-07-29]
 *
 *  Code reviewed by:
 *
 *  username [YYYY-MM-DD]
 *
 *  Remarks:
 *
 *  =====
 */
package hk.hku.cecid.ebms.pkg;
import java.io.IOException;
import java.io.InputStream;
/**
 * This is an implementation of <code>java.io.InputStream</code> that
 * represents part of data read from an <code>InputStream</code>.
 *
 * @author cyng
 * @version $Revision: 1.1 $
 */
class PartialInputStream extends InputStream {
    private final InputStream in;

    private final long length;

    private long currentRead;

    /**
     * Constructor for the PartialInputStream object
     *
     * @param in the origninal input stream
     * @param offset the offset the read data from the stream.
     * @param length the length of the data to be read
     * @exception IOException Description of the Exception
     */
    PartialInputStream(InputStream in, long offset, long length)
             throws IOException {
        this.in = in;
        this.length = length;
        long skip = in.skip(offset);
        long actualSkip = skip;
        while (actualSkip >= 0 && skip < offset) {
            actualSkip = in.skip(offset - skip);
            if (actualSkip > 0) {
                skip += actualSkip;
            }
        }
        currentRead = 0;
    }

    /**
     * close the stream
     *
     * @exception IOException thrown when IO error occur during closing
     */
    public void close() throws IOException {
        in.close();
    }

    /**
     * read a byte from the stream
     *
     * @return the byte in int value from the stream.
     * @exception IOException thrown when IO error occur during reading.
     */
    public int read() throws IOException {
        if (currentRead < length) {
            int r = in.read();
            if (r != -1) {
                currentRead++;
            }
            return r;
        } else {
            return -1;
        }
    }
}

