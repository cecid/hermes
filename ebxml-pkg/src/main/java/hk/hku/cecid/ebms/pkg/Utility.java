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
 * $Header: /home/cvsroot/ebxml-pkg/src/hk/hku/cecid/ebms/pkg/Utility.java,v 1.1 2005/07/28 09:36:24 dcmsze Exp $
 *
 * Code authored by:
 *
 * kcyee [2002-12-06]
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

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.TimeZone;

/**
 * Utility class for use by both clients and server.
 *
 * @author kcyee
 * @version $Revision: 1.1 $
 */
public class Utility {

    /**
     * Message ID counter which is used to guarantee that every message ID
     * generated is unique.
     */
    private static int messageIdCounter;

    /**
     * Lock for Message ID counter.
     */
    private static Object messageIdCounterLock;

    /**
     * Upper bound of message counter beyond which the counter would be wrapped
     * around.
     */
    private static final int MAX_MESSAGE_ID_COUNTER = 999999;

    // Set up message counter
    {
        messageIdCounterLock = new Object();
        messageIdCounter = 0;
    }

    /**
     * Generate globally unique message ID according to the specified date and
     * the information in ebXML message. Required fields are:
     * <ul>
     * <li>To/PartyId element [ebMSS 3.1.1.1]</li>
     * <li>CPAId [ebMSS 3.1.2]</li>
     * <li>Service element [ebMSS 3.1.4]</li>
     * <li>Action element [ebMSS 3.1.5]</li>
     * </ul>
     *
     * @param date          Date to be used to generate message ID.
     * @param ebxmlMessage  ebXML message containing required fields.
     *
     * @return Message ID derived from the given information.
     *
     * @exception Exception
     */
    public static String generateMessageId(Date date, EbxmlMessage ebxmlMessage)
        throws Exception {
        final Iterator toPartyIds = ebxmlMessage.getToPartyIds();
        if (!toPartyIds.hasNext()) {
            throw new Exception(
                "Missing ToPartyId in EbxmlMessage!");
        }
        final String toPartyId = ((MessageHeader.PartyId) toPartyIds.next()).
            getId();

        final String cpaId = ebxmlMessage.getCpaId();
        if (cpaId == null) {
            throw new Exception(
                "Missing CPAId in EbxmlMessage!");
        }
        final String service = ebxmlMessage.getService();
        if (service == null) {
            throw new Exception(
                "Missing Service in EbxmlMessage!");
        }
        final String action = ebxmlMessage.getAction();
        if (action == null) {
            throw new Exception(
                "Missing Action in EbxmlMessage!");
        }
        return generateMessageId(date, toPartyId, cpaId, service, action);
    }

    /**
     * Generate message ID from the specified date, ToPartyId, CPAId,
     * service name and action string.
     *
     * @param date          Date from which message ID is generated.
     * @param toPartyId     Party ID of the recipient [ebMSS 3.1.1.1].
     * @param cpaId         CPAId [ebMSS 3.1.2].
     * @param service       Service name [ebMSS 3.1.4].
     * @param action        Action name [ebMSS 3.1.5].
     *
     * @return Message ID derived from the given information.
     */
    public static String generateMessageId(Date date, String toPartyId, 
                                           String cpaId, String service, 
                                           String action) {
        final Calendar c =
            Calendar.getInstance(TimeZone.getTimeZone(Constants.TIME_ZONE));
        c.setTime(date);
        String year = String.valueOf(c.get(Calendar.YEAR));
        for ( ; year.length()<4 ; year = "0" + year);
        String month = String.valueOf(c.get(Calendar.MONTH)+1);
        for ( ; month.length()<2 ; month = "0" + month);
        String day = String.valueOf(c.get(Calendar.DAY_OF_MONTH));
        for ( ; day.length()<2 ; day = "0" + day);
        String hour = String.valueOf(c.get(Calendar.HOUR_OF_DAY));
        for ( ; hour.length()<2 ; hour = "0" + hour);
        String minute = String.valueOf(c.get(Calendar.MINUTE));
        for ( ; minute.length()<2 ; minute = "0" + minute);
        String second = String.valueOf(c.get(Calendar.SECOND));
        for ( ; second.length()<2 ; second = "0" + second);
        String milliSecond = String.valueOf(c.get(Calendar.MILLISECOND));
        for ( ; milliSecond.length()<3 ; milliSecond = "0" + milliSecond);
        int localCounter;
        synchronized(messageIdCounterLock) {
            if (messageIdCounter > MAX_MESSAGE_ID_COUNTER) {
                messageIdCounter = 0;
            }
            else {
                messageIdCounter++;
            }
            localCounter = messageIdCounter;
        }
        final StringBuffer localPart = new StringBuffer();
        localPart.append(year).append(month).append(day).append("-");
        localPart.append(hour).append(minute).append(second)
            .append(milliSecond).append("-").append(cpaId).append(".");
        localPart.append(service).append(".").append(action).append(".");
        localPart.append(String.valueOf(localCounter));

        String domain = "unknownDomain";
        try {
            InetAddress localAddr = InetAddress.getLocalHost();
            domain = localAddr.getHostAddress();
        }
        catch (UnknownHostException e) {}

        final String messageId = localPart.toString() + "@" + domain;

        return messageId;
    }

    /**
     * Get timestamp of the specified date in type "dateTime" as specified
     * in XML-Schema Part 2: Datatypes section 3.2.7. Local time is converted
     * to UTC time.
     *
     * @param date  Local date to be converted to UTC time.
     *
     * @return UTC time string in CCYY-MM-DDThh:mm:ssZ format
     */
    public static String toUTCString(Date date) {
        final Calendar c =
            Calendar.getInstance(TimeZone.getTimeZone(Constants.TIME_ZONE));
        c.setTime(date);
        String year = String.valueOf(c.get(Calendar.YEAR));
        for ( ; year.length()<4 ; year = "0" + year);
        String month = String.valueOf(c.get(Calendar.MONTH)+1);
        for ( ; month.length()<2 ; month = "0" + month);
        String day = String.valueOf(c.get(Calendar.DAY_OF_MONTH));
        for ( ; day.length()<2 ; day = "0" + day);
        String hour = String.valueOf(c.get(Calendar.HOUR_OF_DAY));
        for ( ; hour.length()<2 ; hour = "0" + hour);
        String minute = String.valueOf(c.get(Calendar.MINUTE));
        for ( ; minute.length()<2 ; minute = "0" + minute);
        String second = String.valueOf(c.get(Calendar.SECOND));
        for ( ; second.length()<2 ; second = "0" + second);
        String timeStamp = year + "-" + month + "-" + day + "T" + hour + ":"
            + minute + ":" + second + "Z";

        return timeStamp;
    }

    /**
     * Convert a string of data type "dateTime" as specified by XML-Schema
     * Part 2: Datatypes section 3.2.7 to local date/time. Only date/time
     * represented as CCYY-MM-DDThh:mm:ssZ is supported.
     *
     * @param dateTime  Date/time string in UTC.
     *
     * @return local time representation of the given UTC time string.
     */
    public static Date fromUTCString(String dateTime) {
        try {
            ArrayList parts = new ArrayList();
            int i, j;
            for (i = 0, j = 0; i < dateTime.length(); i++) {
                if ("-+:TZ.".indexOf(dateTime.charAt(i)) != -1 ||
                    i == dateTime.length() - 1) {
                    parts.add(dateTime.substring(j, i));
                    j = i + 1;
                }
            }

            // Check if all date/time components exist or not
            int count = parts.size();
            if (count < 6) return null;

            int year = Integer.parseInt((String)parts.get(0));
            int month = Integer.parseInt((String)parts.get(1));
            int day = Integer.parseInt((String)parts.get(2));
            int hour = Integer.parseInt((String)parts.get(3));
            int minute = Integer.parseInt((String)parts.get(4));
            int second = Integer.parseInt((String)parts.get(5));

            if (count == 8) {
                int hourOffset = Integer.parseInt((String)parts.get(6));
                int minOffset = Integer.parseInt((String)parts.get(7));
                if (dateTime.indexOf("+") != -1) {
                    hour -= hourOffset;
                    minute -= minOffset;
                }
                else {
                    hour += hourOffset;
                    minute += minOffset;
                }
            }

            final Calendar c =
                Calendar.getInstance(TimeZone.getTimeZone(Constants.TIME_ZONE));
            c.clear();

            // In Calendar class, January = 0
            c.set(year, month - 1, day, hour, minute, second);
            return c.getTime();

        } catch (NumberFormatException nfe) {
            return null;
        }
    }
}

