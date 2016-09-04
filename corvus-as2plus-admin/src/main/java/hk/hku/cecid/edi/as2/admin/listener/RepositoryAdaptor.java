/* 
 * Copyright(c) 2005 Center for E-Commerce Infrastructure Development, The
 * University of Hong Kong (HKU). All Rights Reserved.
 *
 * This software is licensed under the Apache License Version 2.0 [1]
 * 
 * [1] http://www.apache.org/licenses/LICENSE-2.0.txt
 */

package hk.hku.cecid.edi.as2.admin.listener;

import hk.hku.cecid.edi.as2.AS2PlusProcessor;
import hk.hku.cecid.edi.as2.dao.MessageDAO;
import hk.hku.cecid.edi.as2.dao.MessageDVO;
import hk.hku.cecid.edi.as2.dao.RepositoryDAO;
import hk.hku.cecid.edi.as2.dao.RepositoryDVO;
import hk.hku.cecid.piazza.commons.io.IOHandler;
import hk.hku.cecid.piazza.commons.servlet.RequestListenerException;
import hk.hku.cecid.piazza.commons.servlet.http.HttpRequestAdaptor;

import java.io.ByteArrayInputStream;
import java.util.Iterator;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author Donahue Sze
 *  
 */
public class RepositoryAdaptor extends HttpRequestAdaptor {

    /*
     * (non-Javadoc)
     * 
     * @see hk.hku.cecid.piazza.commons.servlet.http.HttpRequestListener#processRequest(javax.servlet.http.HttpServletRequest,
     *      javax.servlet.http.HttpServletResponse)
     */
    public String processRequest(HttpServletRequest request,
            HttpServletResponse response) throws RequestListenerException {

        try {
            String messageId = request.getParameter("message_id");
            String messageBox = request.getParameter("message_box");
            if (request.getParameter("is_download_receipt") != null) {
                if (request.getParameter("is_download_receipt")
                        .equalsIgnoreCase("true")) {
                    MessageDAO receiptDAO = (MessageDAO) AS2PlusProcessor.getInstance().getDAOFactory()
                            .createDAO(MessageDAO.class);
                    Iterator i = receiptDAO.findMessageByOriginalMessageID(
                            messageId, messageBox).iterator();

                    if (i.hasNext()) {
                        MessageDVO data = (MessageDVO) i.next();
                        messageId = data.getMessageId();
                        messageBox = data.getMessageBox();
                    } else {
                        AS2PlusProcessor.getInstance().getLogger()
                                .debug("Unable to find the receipt for message ID: "
                                        + messageId);
                        return null;
                    }
                }
            }

            RepositoryDAO repositoryDAO = (RepositoryDAO) AS2PlusProcessor.getInstance().getDAOFactory()
                    .createDAO(RepositoryDAO.class);
            RepositoryDVO repositoryDAOData = (RepositoryDVO) repositoryDAO
                    .createDVO();
            repositoryDAOData.setMessageId(messageId);
            repositoryDAOData.setMessageBox(messageBox);
            if (repositoryDAO.retrieve(repositoryDAOData)) {
                response.setCharacterEncoding(null);
                response.setContentType("application/download");
                response.setHeader("Content-Disposition",
                        "attachment;filename=\""
                                + repositoryDAOData.getMessageId() + ".as2\"");

                ByteArrayInputStream bis = new ByteArrayInputStream(
                        repositoryDAOData.getContent());

                IOHandler.pipe(bis, response.getOutputStream());
                bis.close();
            }

        } catch (Exception e) {
            AS2PlusProcessor.getInstance().getLogger().debug(
                    "Unable to process the pagelet request", e);
        }
        return null;
    }

}