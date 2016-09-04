/*
 * Created on Nov 2, 2004
 *
 */
package hk.hku.cecid.ebms.admin.listener;

import hk.hku.cecid.ebms.spa.EbmsProcessor;
import hk.hku.cecid.ebms.spa.dao.RepositoryDAO;
import hk.hku.cecid.ebms.spa.dao.RepositoryDVO;
import hk.hku.cecid.piazza.commons.io.IOHandler;
import hk.hku.cecid.piazza.commons.servlet.RequestListenerException;
import hk.hku.cecid.piazza.commons.servlet.http.HttpRequestAdaptor;

import java.io.ByteArrayInputStream;

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

        String messageId = request.getParameter("message_id");
        String messageBox = request.getParameter("message_box");

        try {
            RepositoryDAO repositoryDAO = (RepositoryDAO) EbmsProcessor.core.dao
                    .createDAO(RepositoryDAO.class);
            RepositoryDVO repositoryDVO = (RepositoryDVO) repositoryDAO
                    .createDVO();
            repositoryDVO.setMessageId(messageId);
            repositoryDVO.setMessageBox(messageBox);
            boolean hasExist = repositoryDAO.findRepository(repositoryDVO);

            if (hasExist) {
                response.setCharacterEncoding(null);
                response.setContentType("application/download");
                response
                        .setHeader("Content-Disposition",
                                "attachment;filename=\""
                                        + repositoryDVO.getMessageId()
                                        + ".ebxml\"");

                ByteArrayInputStream bis = new ByteArrayInputStream(
                        repositoryDVO.getContent());

                IOHandler.pipe(bis, response.getOutputStream());
                bis.close();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}