package hk.hku.cecid.piazza.corvus.core.main.admin.hc.module;

import hk.hku.cecid.ebms.spa.EbmsProcessor;
import hk.hku.cecid.ebms.spa.dao.InboxDAO;
import hk.hku.cecid.ebms.spa.dao.InboxDVO;
import hk.hku.cecid.ebms.spa.dao.OutboxDAO;
import hk.hku.cecid.ebms.spa.dao.OutboxDVO;
import hk.hku.cecid.edi.as2.AS2Processor;
import hk.hku.cecid.edi.as2.dao.MessageDAO;
import hk.hku.cecid.edi.as2.dao.MessageDVO;
import hk.hku.cecid.edi.as2.dao.MessageDataSourceDAO;
import hk.hku.cecid.edi.as2.dao.RepositoryDAO;
import hk.hku.cecid.edi.as2.dao.RepositoryDVO;
import hk.hku.cecid.piazza.commons.dao.DAOException;
import hk.hku.cecid.piazza.commons.dao.Transaction;
import hk.hku.cecid.piazza.commons.mail.SmtpMail;
import hk.hku.cecid.piazza.commons.mail.SmtpMailException;
import hk.hku.cecid.piazza.commons.mail.SmtpMailProperties;
import hk.hku.cecid.piazza.commons.module.ActiveTask;
import hk.hku.cecid.piazza.commons.servlet.http.HttpDispatcherContext;
import hk.hku.cecid.piazza.commons.util.PropertyTree;
import hk.hku.cecid.piazza.corvus.core.main.admin.AdminMainProcessor;
import hk.hku.cecid.piazza.corvus.core.main.admin.hc.util.AdminProperties;
import hk.hku.cecid.piazza.corvus.core.main.admin.hc.util.AdminPropertiesException;

import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.List;

import javax.mail.Address;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;

import com.sun.mail.smtp.SMTPMessage;

public class SchedulerTask implements ActiveTask {

	// private int retried;

	private AdminProperties props;

	/**
	 * Status messages
	 */
	private static final String IN_PROGESS = "processing";

	private static final String COMPLETE = "success";

	private static final String FAILED = "failed";

	/**
	 * Default values
	 */
	private static final int DEFAULT_CUTOFF = 3;

	private static final int DEFAULT_DAY = Calendar.SUNDAY;

	private static final String DEFAULT_TIME = "00.00.00";

	/**
	 * Formats
	 */
	public static final String DATE_FORMAT = "y.M.d.k.m.s";

	public static final String TIME_FORMAT = "k.m.s";

	public void execute() throws Exception {
		/**
		 * Transactions
		 */
		Transaction as2 = null;

		Transaction ebms = null;
		try {
			// load the properties
			props = loadProps();
			// check to see if housecleaning is turned on
			if (!props.isOn()) {
				return;
			}
			// check if the status is in progress
			if (props.getStatus().equals(IN_PROGESS)) {
				AdminLogging("Previous housecleaning was interrupted while in progress.");
				AdminLogging("Rescheduling housecleaning.");
				// set the status to failed
				props.setStatus(FAILED);
				props
						.setReason("Previous housecleaning was interupted while in progress.");
				// set the last run time
				props.setLastRun(getDateFormat().format(new Date()));

				// set the next run date
				Date time = getTimeFormat().parse(props.getTime());
				Calendar cal = GregorianCalendar.getInstance();
				cal.setTime(time);
				if (!setNextRunDateFromNow(props)) {
					AdminError("Could not set the next run date.");
				}
				props.write();
			}
			if (checkStartTime(props.getNextRun())) {
				// set the start status
				setStartStatus();
				if (checkNotExceeded(props.getNextRun())) {
					// halt the context listeners
					if (HttpDispatcherContext.getDefaultContext().halt()) {
						// clean the as2 database
						as2 = cleanAS2(props.getCutoff());
						// clean the ebms database
						ebms = cleanEBMS(props.getCutoff());
						// commit the transactions
						commitTx(new Transaction[] { as2, ebms });
						AdminLogging("Transactions commited.");
						// resume the context listeners
						if (HttpDispatcherContext.getDefaultContext().resume()) {
							// set the end status : success
							setEndStatus(true, props);
							props.setReason("");
						} else {
							// set the end status : fail - could not restart the
							// context listeners
							setEndStatus(false, props);
							AdminError("Unable to start up servlet.");
							props
									.setReason("House cleaning was successful but unable to restart the servlet.");
						}
					} else {
						// set the end status: failed - could not halt the
						// context listeners
						setEndStatus(false, props);
						AdminError("Listeners cannot be halted.");
						props.setReason("Listeners cannot be halted.");
					}
				} else {
					// set the end status: fail - the runtime has expired
					setEndStatus(false, props);
					AdminError("Runtime has expired.");
					props.setReason("Runtime has expired.");
				}
				// set the next run date from now
				if (!setNextRunDateFromNow(props)) {
					AdminError("Failed to update the next run date.");
				}
				props.write();
			} else {
				// return since the next run time has not been reached yet.
				return;
			}
		}
		// catch any exceptions related to malformed property entries
		catch (AdminPropertiesException e) {
			// roll back the transaction
			rollbackTx(new Transaction[] { as2, ebms });
			// resume the context listeners if they were halted
			if (HttpDispatcherContext.getDefaultContext().isHalted()) {
				HttpDispatcherContext.getDefaultContext().resume();
			}
			// set the default properties
			setDefaultProperties(props);
			// set the end status: fail
			setEndStatus(false, props);
			// log the stack trace
			stackTraceToLog(e.getCause());
			// set the reason of the exception
			props.setReason(e.getMessage());
			// set the next run date from now
			setNextRunDateFromNow(props);
		}
		// catch any other exception
		catch (Exception e) {
			// roll back transaction if the transaction has not been commited
			rollbackTx(new Transaction[] { as2, ebms });
			// resume context listeners if they are halted
			if (HttpDispatcherContext.getDefaultContext().isHalted()) {
				HttpDispatcherContext.getDefaultContext().resume();
			}
			// log the stack trace of the exception
			stackTraceToLog(e.getCause());
			// set the end status: fail
			setEndStatus(false, props);
			// set the failure reason
			props.setReason(e.getMessage());
			// set the next run date
			setNextRunDateFromNow(props);
		}
		try {
			if (!props.getEmail().equals("") && !props.getSmtp().equals("")) {
				AdminLogging("Sending email...");
				sendMail(props, props.getReason().equals(""));
			} else {
				AdminLogging("Email not set.");
			}
		} catch (Exception e) {
			AdminError("Unable to send email.");
			stackTraceToLog(e);
		}
	}

	/**
	 * Convenience method for logging errors on housecleaning.
	 * 
	 * @param s
	 */
	private void AdminError(String s) {
		AdminMainProcessor.core.log.error("Housecleaning: " + s);
	}

	/**
	 * Convenience method for logging general housecleaning info.
	 * 
	 * @param s
	 */
	private void AdminLogging(String s) {
		AdminMainProcessor.core.log.info("Housecleaning: " + s);
	}

	/**
	 * Convenience method for logging AS2 errors.
	 * 
	 * @param s
	 */
	private void AS2Error(String s) {
		AdminMainProcessor.core.log.error("AS2 Cleaning: " + s);
	}

	/**
	 * Convenience method for logging AS2.
	 * 
	 * @param s
	 */
	private void AS2Logging(String s) {
		AdminMainProcessor.core.log.info("AS2 Cleaning: " + s);
	}

	/**
	 * Checks to see whether the current time has exceeded the parameter 'time'.
	 * 
	 * @param time
	 * @return
	 * @throws Exception
	 */
	private boolean checkNotExceeded(String time) throws Exception {
		Calendar cur = GregorianCalendar.getInstance();
		Calendar upperLimit = GregorianCalendar.getInstance();
		try {
			Date lowerLimit = getDateFormat().parse(time);
			upperLimit.setTime(lowerLimit);
			upperLimit.add(Calendar.MINUTE, 1);
			return upperLimit.after(cur);
		} catch (ParseException e) {
			AdminError("Parse error when checking the start time.");
			throw new Exception("Parse error when checking the start time.", e);
		}
	}

	/**
	 * Check whether housecleaning should perform or not.
	 * 
	 * @param time
	 * @return
	 * @throws ParseException
	 */
	private boolean checkStartTime(String time) throws Exception {
		Date cur = new Date();
		try {
			Date run = getDateFormat().parse(time);
			return cur.after(run);
		} catch (ParseException e) {
			AdminError("Parse error when checking the start time.");
			throw new Exception("Parse error when checking the start time.", e);
		}

	}

	/**
	 * Attempt to clean out the AS2 database
	 * 
	 * @param months
	 * @throws Exception
	 * @throws DAOException
	 * @throws DAOException
	 */
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
			AS2Logging(Integer.toString(list.size())
					+ " messages will be removed.");
			AS2Logging("Initializing...");

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
			AS2Error("Error encountered while cleaning.");
			throw new Exception("Error encountered while cleaning AS2.", e);
		}
	}

	/**
	 * Attempt to clean the EBMS messages.
	 * 
	 * @param months
	 * @return
	 * @throws Exception
	 * @throws Exception
	 * @throws DAOException
	 */
	protected Transaction cleanEBMS(int months) throws Exception {
		try {
			hk.hku.cecid.ebms.spa.dao.MessageDAO dao = (hk.hku.cecid.ebms.spa.dao.MessageDAO) EbmsProcessor.core.dao
					.createDAO(hk.hku.cecid.ebms.spa.dao.MessageDAO.class);
			InboxDAO inboxDao = (InboxDAO) EbmsProcessor.core.dao
					.createDAO(InboxDAO.class);
			OutboxDAO outboxDao = (OutboxDAO) EbmsProcessor.core.dao
					.createDAO(OutboxDAO.class);
			hk.hku.cecid.ebms.spa.dao.RepositoryDAO repDao = (hk.hku.cecid.ebms.spa.dao.RepositoryDAO) EbmsProcessor.core.dao
					.createDAO(hk.hku.cecid.ebms.spa.dao.RepositoryDAO.class);

			Transaction tr = ((hk.hku.cecid.ebms.spa.dao.MessageDataSourceDAO) dao)
					.getFactory().createTransaction();

			dao.setTransaction(tr);
			inboxDao.setTransaction(tr);
			outboxDao.setTransaction(tr);
			repDao.setTransaction(tr);
			tr.begin();

			List list = dao.findMessagesBeforeTime(months);
			EBMSLogging(Integer.toString(list.size())
					+ " messages will be removed.");
			EBMSLogging("Initializing...");

			Iterator itr = list.iterator();

			hk.hku.cecid.ebms.spa.dao.MessageDVO dvo;
			InboxDVO inboxDvo;
			OutboxDVO outboxDvo;
			hk.hku.cecid.ebms.spa.dao.RepositoryDVO repDvo;

			while (itr.hasNext()) {
				dvo = (hk.hku.cecid.ebms.spa.dao.MessageDVO) itr.next();
				if (dvo.getMessageBox().equals("inbox")) {
					/**
					 * delete the inbox entry
					 */
					inboxDvo = (InboxDVO) inboxDao.createDVO();
					inboxDvo.setMessageId(dvo.getMessageId());
					inboxDao.remove(inboxDvo);
				} else if (dvo.getMessageBox().equals("outbox")) {
					/**
					 * delete the outbox entry
					 */
					outboxDvo = (OutboxDVO) outboxDao.createDVO();
					outboxDvo.setMessageId(dvo.getMessageId());
					outboxDao.remove(outboxDvo);
				} else {
					EBMSError("Unknown value in MessageBox relation.");
					throw new Exception(
							"Error, unknown value in MessageBox relation.");
				}
				/**
				 * remove entry in repository
				 */
				repDvo = (hk.hku.cecid.ebms.spa.dao.RepositoryDVO) repDao
						.createDVO();
				repDvo.setMessageId(dvo.getMessageId());
				repDvo.setMessageBox(dvo.getMessageBox());
				repDao.remove(repDvo);
				/**
				 * finally remove from message table
				 */
				dao.remove(dvo);
			}
			return tr;
		} catch (DAOException e) {
			EBMSError("Error encountered while cleaning.");
			throw new Exception("Error encountered while cleaning EBmS.", e);

		}

	}

	/**
	 * Commit the transactions. If null do nothing. Note: Tx should never be
	 * null as commitTx should only be called when all txs needing commit are
	 * valid.
	 * 
	 * @param txs
	 * @throws Exception
	 */
	private void commitTx(Transaction[] txs) throws Exception {
		try {
			for (int i = 0; i < txs.length; i++) {
				if (txs[i] != null) {
					txs[i].commit();
				}
			}
		} catch (DAOException e) {
			AdminError("Exception thrown during the commit.");
			throw new Exception("Exception thrown during commit.", e);
		}
	}

	/**
	 * Convenience method to create a message associated with the session ses.
	 * If success is true, String reasonFailed is ignored. If success is false
	 * and reasonFailed is null, it will be set to "Unknown".
	 * 
	 * @param ses
	 * @param time
	 * @param attempt
	 * @param result
	 * @return
	 * @throws MessagingException
	 * @throws MessagingException
	 * @throws MessagingException
	 * @throws AddressException
	 */
	private SMTPMessage createMessage(Session ses, Date time, boolean success,
			String result, String reasonFailed) throws MessagingException {
		SMTPMessage message = new SMTPMessage(ses);

		String tag;

		if (success) {
			tag = "[Success]";
		} else {
			tag = "[Failed]";
		}
		try {
			InternetAddress from = new InternetAddress();
			from.setAddress(ses.getProperty("mail.smtp.from"));
			from.setPersonal("Hermes2 Admin");
			message.setFrom(from);
			message.setSubject(tag + "Hermes2 housecleaning");
			String theContent = "======== Report for Hermes2 Housecleaning ======== "
					+ "\n"
					+ "\n  Hermes2 housecleaning has recently been invoked"
					+ "\n  Date: " + time + "\n  Result: " + result;
			if (success) {
				message.setText(theContent);
			} else {
				if (reasonFailed == null) {
					reasonFailed = "Unknown";
				}
				message.setText(theContent + "\n  Reason for failure: "
						+ reasonFailed + "\n\n"
						+ "  Please refer to the log files for more details.");
			}
		} catch (MessagingException e) {
			AdminError("Unable to create the message.  Messaging Exception thrown.");
			stackTraceToLog(e);
			throw e;
		} catch (UnsupportedEncodingException e) {
			AdminError("Unsupported encoding in email 'from' name.");
		}
		return message;
	}

	/**
	 * Convenience method for logging errors for EbMS cleaning.
	 * 
	 * @param msg
	 */
	private void EBMSError(String msg) {
		AdminMainProcessor.core.log.error("EBmS Cleaning: " + msg);
	}

	/**
	 * Convenience method for logging general messages for EbMS cleaning.
	 * 
	 * @param msg
	 */
	private void EBMSLogging(String msg) {
		AdminMainProcessor.core.log.info("EBmS Cleaning: " + msg);
	}

	/**
	 * Return the date formatter.
	 * 
	 * @return
	 */
	private SimpleDateFormat getDateFormat() {
		return new SimpleDateFormat(DATE_FORMAT);
	}

	/**
	 * Return the time formatter.
	 * 
	 * @return
	 */
	private SimpleDateFormat getTimeFormat() {
		return new SimpleDateFormat(TIME_FORMAT);
	}

	/**
	 * Load the admin properties.
	 * 
	 * @return
	 * @throws Exception
	 */
	private AdminProperties loadProps() {
		return new AdminProperties(
				(PropertyTree) AdminMainProcessor.core.properties);
	}

	/**
	 * Rollback the transactions. If null, do nothing.
	 * 
	 * @param txs
	 * @throws Exception
	 */
	private void rollbackTx(Transaction[] txs) throws Exception {
		try {
			for (int i = 0; i < txs.length; i++) {
				if (txs[i] != null) {
					txs[i].rollback();
				}
			}
		} catch (DAOException e) {
			AdminError("Exception thrown during rollback.");
			throw new Exception("Exception thrown during rollback.", e);
		}
	}

	/**
	 * Convenience method to send the notification mail.
	 * 
	 * @param p
	 * @param success
	 * @throws Exception
	 */
	private void sendMail(AdminProperties p, boolean success) throws Exception {
		try {
			SmtpMailProperties mailProps = new SmtpMailProperties();
			mailProps.setHost(p.getSmtp());
			mailProps.setFrom(p.getEmail());
			mailProps.setUsername(p.getUsername());
			mailProps.setPassword(p.getPassword());
			mailProps.setPort(p.getPort());

			SmtpMail mail = new SmtpMail(mailProps, false);

			SMTPMessage msg;
			if (success) {
				msg = createMessage(mail.getSession(), new Date(), success, p
						.getStatus(), null);
			} else {
				msg = createMessage(mail.getSession(), new Date(), success, p
						.getStatus(), p.getReason());
			}
			mail.transportConnect();
			mail.send(msg, new Address[] { new InternetAddress(p.getEmail()) });
			mail.transportClose();
		} catch (AdminPropertiesException e) {
			AdminError("Invalid mail properties.");
			throw new Exception(
					"Invalid mail properties. Unable to send mail.", e);
		} catch (SmtpMailException e) {
			AdminError("SMTP Mail exception thrown.");
			throw new Exception("Unable to send mail.", e);
		} catch (MessagingException e) {
			AdminError("Messaging exception thrown.");
			throw new Exception(
					"Messaging exception thrown. Unable to send mail.", e);
		}
	}

	/**
	 * Set and write default properties to the properties file.
	 * 
	 * @throws Exception
	 * 
	 * @throws AdminPropertiesException
	 */
	private void setDefaultProperties(AdminProperties p) throws Exception {
		p.setOn(true);
		p.setDay(DEFAULT_DAY);
		p.setLastRun("");
		p.setCutoff(DEFAULT_CUTOFF);
		p.setStatus("");
		p.setTime(DEFAULT_TIME);
		if (p.getEmail() == null) {
			p.setEmail("");
		}
		if (p.getPassword() == null) {
			p.setPassword("");
		}
		p.setPort(25);
		if (p.getReason() == null) {
			p.setReason("The property file was invalid and had to be reset.");
		}
		if (p.getUsername() == null) {
			p.setUsername("");
		}
		if (p.getSmtp() == null) {
			p.setSmtp("");
		}

		try {
			if (!setNextRunDateFromNow(p)) {
				AdminError("Could not set the next run time.");
			}
			p.write();
			AdminLogging("Certain values were missing or null from the properties file.  Resetting default values.");
		} catch (AdminPropertiesException e) {
			AdminError("Exception occured while updating the properties.");
			throw new Exception(
					"Exception occured while updating the properties.", e);
		}
	}

	/**
	 * Set and write properties to indicate completion or failure.
	 * 
	 * @throws AdminPropertiesException
	 */
	private void setEndStatus(boolean success, AdminProperties p) {
		if (success) {
			p.setStatus(COMPLETE);
			AdminLogging("Housecleaning has completed successfully.");
		} else {
			p.setStatus(FAILED);
			AdminError("Housecleaning was unsuccessful.");
		}
	}

	/**
	 * Set and write properties to indicate initialization of housecleaning.
	 * 
	 * @throws Exception
	 * 
	 * @throws Exception
	 * 
	 */
	private void setStartStatus() throws Exception {
		props.setStatus(IN_PROGESS);
		props.setLastRun(getDateFormat().format(new Date()));

		try {
			props.write();
		} catch (AdminPropertiesException e) {
			AdminError("Exception occured while updating the properties.");
			throw new Exception(
					"Exception occured while updating the properties.", e);
		}
	}

	/**
	 * Convenience method for printing the stack trace to the log file.
	 * 
	 * @param e
	 */
	private void stackTraceToLog(Throwable e) {
		StackTraceElement[] list = e.getStackTrace();
		for (int index = 0; index < list.length; index++) {

			AdminMainProcessor.core.log.debug("Housecleaning: "
					+ list[index].toString());
		}
	}

	public boolean isRetryEnabled() {
		return false;
	}

	public long getRetryInterval() {
		return 0;
	}

	public int getMaxRetries() {
		return 0;
	}

	public void setRetried(int retried) {
		// this.retried = retried;
	}

	public void onFailure(Throwable e) {
	}

	public void onAwake() {

	}

	public boolean isSucceedFast() {
		return false;
	}

	public boolean setNextRunDateFromNow(AdminProperties properties) {

		try {
			Calendar runTime = GregorianCalendar.getInstance();
			runTime.setTime(getTimeFormat().parse(properties.getTime()));

			int day_of_week = properties.getDay();
			int hour = runTime.get(Calendar.HOUR_OF_DAY);
			int min = runTime.get(Calendar.MINUTE);
			int sec = runTime.get(Calendar.SECOND);

			// get an instance of the calendar
			Calendar newTime = GregorianCalendar.getInstance();
			int dif = day_of_week - newTime.get(Calendar.DAY_OF_WEEK);
			if (dif < 0) {
				dif = 7 + dif;
			}

			if (dif == 0) {
				if (hour < newTime.get(Calendar.HOUR_OF_DAY)
						|| min < newTime.get(Calendar.MINUTE)
						|| sec < newTime.get(Calendar.SECOND)) {
					dif = 7 + dif;
				} else {
				}
			}

			// add the difference of days
			newTime.add(Calendar.DATE, dif);
			// set the time
			newTime.set(Calendar.HOUR_OF_DAY, hour);
			newTime.set(Calendar.MINUTE, min);
			newTime.set(Calendar.SECOND, sec);

			properties.setNextRun(getDateFormat().format(newTime.getTime()));
			return true;
		} catch (AdminPropertiesException e) {
			return false;
		} catch (ParseException e) {
			return false;
		}
	}
	
	public static Date getNextRunDateFromNow(int day_of_week, int hour, int min, int sec) {

			// get an instance of the calendar
			Calendar newTime = GregorianCalendar.getInstance();
			int dif = day_of_week - newTime.get(Calendar.DAY_OF_WEEK);
			if (dif < 0) {
				dif = 7 + dif;
			}

			if (dif == 0) {
				if (hour < newTime.get(Calendar.HOUR_OF_DAY)
						|| min < newTime.get(Calendar.MINUTE)
						|| sec < newTime.get(Calendar.SECOND)) {
					dif = 7 + dif;
				} else {
				}
			}

			// add the difference of days
			newTime.add(Calendar.DATE, dif);
			// set the time
			newTime.set(Calendar.HOUR_OF_DAY, hour);
			newTime.set(Calendar.MINUTE, min);
			newTime.set(Calendar.SECOND, sec);

			return newTime.getTime();
	}
}