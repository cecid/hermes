package hk.hku.cecid.piazza.corvus.core.main.admin.hc.module;

import java.util.Iterator;
import java.util.List;

import hk.hku.cecid.edi.as2.AS2Processor;
import hk.hku.cecid.edi.as2.dao.MessageDAO;
import hk.hku.cecid.edi.as2.dao.MessageDVO;
import hk.hku.cecid.edi.as2.dao.MessageDataSourceDAO;
import hk.hku.cecid.edi.as2.dao.RepositoryDAO;
import hk.hku.cecid.edi.as2.dao.RepositoryDVO;
import hk.hku.cecid.piazza.commons.dao.DAOException;
import hk.hku.cecid.piazza.commons.dao.Transaction;
import hk.hku.cecid.piazza.corvus.core.main.admin.AdminMainProcessor;

public class NeoSchedulerTask extends SchedulerTask{

	@Override
	protected Transaction cleanAS2(int months) throws Exception {
		try {
			MessageDAO dao = (MessageDAO) AS2Processor.core.dao
					.createDAO(MessageDAO.class);
			Transaction tr = ((MessageDataSourceDAO) dao).getFactory()
					.createTransaction();
			RepositoryDAO repDao = (RepositoryDAO) AS2Processor.core.dao
					.createDAO(RepositoryDAO.class);
			dao.setTransaction(tr);
			repDao.setTransaction(tr);
			tr.begin();

			List list = dao.findMessagesBeforeTime(months);
			AS2PlusLogging(Integer.toString(list.size())
					+ " messages will be removed.");
			AS2PlusLogging("Initializing...");

			Iterator itr = list.iterator();
			MessageDVO dvo;
			RepositoryDVO repDvo;

			while (itr.hasNext()) {
				dvo = (MessageDVO) itr.next();
				repDvo = (RepositoryDVO) repDao.createDVO();
				repDvo.setMessageId(dvo.getMessageId());
				repDvo.setMessageBox(dvo.getMessageBox());
				repDao.remove(repDvo);
				dao.remove(dvo);
			}
			return tr;
		} catch (DAOException e) {
			AS2PlusError("Error encountered while cleaning.");
			throw new Exception("Error encountered while cleaning AS2.", e);
		}
	}
	
	/**
	 * Convenience method for logging AS2 errors.
	 * 
	 * @param s
	 */
	private void AS2PlusError(String s) {
		AdminMainProcessor.core.log.error("AS2Plus Cleaning: " + s);
	}

	/**
	 * Convenience method for logging AS2.
	 * 
	 * @param s
	 */
	private void AS2PlusLogging(String s) {
		AdminMainProcessor.core.log.info("AS2Plus Cleaning: " + s);
	}
}
