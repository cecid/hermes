/* 
 * Copyright(c) 2005 Center for E-Commerce Infrastructure Development, The
 * University of Hong Kong (HKU). All Rights Reserved.
 *
 * This software is licensed under the GNU GENERAL PUBLIC LICENSE Version 2.0 [1]
 * 
 * [1] http://www.gnu.org/licenses/old-licenses/gpl-2.0.txt
 */

package hk.hku.cecid.ebms.spa.task;

import hk.hku.cecid.ebms.spa.EbmsProcessor;
import hk.hku.cecid.ebms.spa.handler.MessageServiceHandler;
import hk.hku.cecid.piazza.commons.module.ActiveTaskList;
import hk.hku.cecid.piazza.commons.net.ConnectionException;
import hk.hku.cecid.piazza.commons.net.MailReceiver;

import java.util.List;
import java.util.Vector;

import javax.mail.Flags;
import javax.mail.Folder;
import javax.mail.Message;

/**
 * @author Donahue Sze
 * 
 */
public class MailCollector extends ActiveTaskList {

    /*
     * (non-Javadoc)
     * 
     * @see hk.hku.cecid.piazza.commons.module.ActiveTaskList#getTaskList()
     */
    public List getTaskList() {

        MessageServiceHandler msh = MessageServiceHandler.getInstance();

        List messageList = new Vector();

        MailReceiver pop = new MailReceiver(msh.popProtocol, msh.popHost,
                msh.popUsername, msh.popPassword);

        if (!msh.popPort.equalsIgnoreCase("")) {
            pop.addProperty("mail.pop3.port", msh.popPort);
        }

        try {
            pop.connect();
            Folder folder = pop.openFolder(msh.popFolder);
            Message[] messages = folder.getMessages();
            if (messages.length > 0) {
                EbmsProcessor.core.log.info("Found " + messages.length
                        + " message(s) in mail box");
            }
            for (int messageIndex = 0; messageIndex < messages.length; messageIndex++) {

                Message message = messages[messageIndex];
                MailTask mailTask = new MailTask(message);

                messageList.add(mailTask);
                messages[messageIndex].setFlag(Flags.Flag.DELETED, true);
            }
            folder.close(true);
        } catch (Exception e) {
            EbmsProcessor.core.log.error(
                    "Error in collecting message from mail box", e);
        } finally {
            try {
                pop.disconnect();
            } catch (ConnectionException e1) {
                EbmsProcessor.core.log.error(
                        "Error in disconnection of pop server", e1);
            }
        }

        return messageList;
    }

}