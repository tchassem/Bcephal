package com.moriset.bcephal.etl.service;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.Properties;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Order;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.moriset.bcephal.domain.BrowserDataPage;
import com.moriset.bcephal.domain.EditorData;
import com.moriset.bcephal.domain.EditorDataFilter;
import com.moriset.bcephal.domain.Persistent;
import com.moriset.bcephal.domain.filters.BrowserDataFilter;
import com.moriset.bcephal.domain.filters.ColumnFilter;
import com.moriset.bcephal.etl.domain.Email;
import com.moriset.bcephal.etl.domain.EmailAccount;
import com.moriset.bcephal.etl.domain.EmailAttachment;
import com.moriset.bcephal.etl.domain.EmailFilter;
import com.moriset.bcephal.etl.repository.EmailAccountRepository;
import com.moriset.bcephal.service.RequestQueryBuilder;
import com.moriset.bcephal.utils.BcephalException;

import jakarta.mail.Address;
import jakarta.mail.Flags;
import jakarta.mail.Folder;
import jakarta.mail.FolderClosedException;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.Multipart;
import jakarta.mail.Part;
import jakarta.mail.Session;
import jakarta.mail.Store;
import jakarta.mail.event.MessageCountAdapter;
import jakarta.mail.event.MessageCountEvent;
import jakarta.mail.internet.MimeBodyPart;
import jakarta.mail.search.BodyTerm;
import jakarta.mail.search.FlagTerm;
import jakarta.mail.search.FromStringTerm;
import jakarta.mail.search.SentDateTerm;
import jakarta.mail.search.SubjectTerm;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class EmailAccountService extends Persistent {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Autowired
	private EmailAccountRepository emailAccountRepository;

	@Autowired
	protected MessageSource messageSource;

	public EmailAccountRepository getRepository() {
		return emailAccountRepository;
	}

	public String getFunctionalityCode() {
		return getBrowserFunctionalityCode();
	}

	@Transactional
	public void delete(List<Long> ids, Locale locale) {
		log.debug("Try to delete : {} entities", ids.size());
		if (getRepository() == null) {
			return;
		}
		try {
			if (ids == null || ids.size() == 0) {
				String message = messageSource.getMessage("unable.to.delete.empty.list", new Object[] { ids }, locale);
				throw new BcephalException(message);
			}
			deleteByIds(ids);
			log.debug("{} entities successfully deleted", ids.size());
		} catch (BcephalException e) {
			throw e;
		} catch (Exception e) {
			log.error("Unexpected error while delete entities : {}", ids.size(), e);
			String message = messageSource.getMessage("unable.to.delete", new Object[] { ids.size() }, locale);
			throw new BcephalException(HttpStatus.INTERNAL_SERVER_ERROR.value(), message);
		}
	}

	public void deleteByIds(List<Long> ids) {
		log.debug("Try to delete by ids : {}", ids);
		if (getRepository() == null || ids == null) {
			return;
		}
		ids.forEach(item -> {
			deleteById(item);
		});
	}

	public void deleteById(Long id) {
		log.debug("Try to delete by id : {}", id);
		if (getRepository() == null || id == null) {
			return;
		}
		EmailAccount item = getRepository().getReferenceById(id);
		delete(item);
		log.debug("Entity successfully deleted : {}", id);
	}

	public void delete(EmailAccount item) {
		log.debug("Try to delete : {}", item);
		if (item == null || item.getId() == null) {
			return;
		}
		getRepository().deleteById((Long) item.getId());
		log.debug("Entity successfully deleted : {}", item);
	}

	public List<Email> downloadPop3(EmailAccount emailAccount, String downloadDir) throws Exception {

		List<Email> emails = new ArrayList<Email>();

		// Create empty properties
		Properties props = new Properties();

		// Get the session
		Session session = Session.getInstance(props, null);

		// Get the store
		Store store = session.getStore("imaps");
		store.connect(emailAccount.getServe_host(), emailAccount.getUserName(), emailAccount.getPassword());

		int msgCount;
		try {

			long start = System.nanoTime();
			Folder folder = store.getFolder("INBOX");
			folder.open(Folder.READ_WRITE);

			do {

				msgCount = folder.getMessageCount();

				Flags seen = new Flags(Flags.Flag.SEEN);
				FlagTerm unseenFlagTerm = new FlagTerm(seen, false);
				Message messages[] = folder.search(unseenFlagTerm);

				// add filter
				if (emailAccount.getEmailFilters() != null && emailAccount.getEmailFilters().size() > 0) {
					EmailFilter filter = emailAccount.getEmailFilters().get(0);
					if (filter != null) {

						if (filter.getAttributeFilter().isSubject()
								|| filter.getAttributeFilter().isExpeditorAndSubject()
								|| filter.getAttributeFilter().isExpeditorAndSubjectAndAttachment()
								|| filter.getAttributeFilter().isSubjectAndAttachment()) {
							SubjectTerm subTerm = new SubjectTerm(filter.subjectPattern());
							messages = folder.search(subTerm, messages);
						}
						if (filter.getAttributeFilter().isExpeditor()
								|| filter.getAttributeFilter().isExpeditorAndSubject()
								|| filter.getAttributeFilter().isExpeditorAndSubjectAndAttachment()) {
							FromStringTerm subTerm = new FromStringTerm(filter.subjectPattern());
							messages = folder.search(subTerm, messages);
						}
						if (filter.getAttributeFilter().isAttachment()
								|| filter.getAttributeFilter().isExpeditorAndAttachment()
								|| filter.getAttributeFilter().isExpeditorAndSubjectAndAttachment()) {
							BodyTerm subTerm = new BodyTerm(filter.subjectPattern());
							messages = folder.search(subTerm, messages);
						}

						if (filter.getAttributeFilter().isSendDate()
								|| filter.getAttributeFilter().isExpeditorAndAttachment()
								|| filter.getAttributeFilter().isExpeditorAndSubjectAndAttachment()) {
							int comparator = SentDateTerm.EQ;
							if (StringUtils.hasText(filter.getComparator()) && filter.getComparator().equals(">")) {
								comparator = SentDateTerm.GT;
							} else if (StringUtils.hasText(filter.getComparator())
									&& filter.getComparator().equals(">=")) {
								comparator = SentDateTerm.GE;
							} else if (StringUtils.hasText(filter.getComparator())
									&& filter.getComparator().equals("<")) {
								comparator = SentDateTerm.LT;
							} else if (StringUtils.hasText(filter.getComparator())
									&& filter.getComparator().equals("<=")) {
								comparator = SentDateTerm.LE;
							}

							SentDateTerm subTerm = new SentDateTerm(comparator, new Date());
							messages = folder.search(subTerm, messages);
						}
					}
				}

				emails.addAll(processEmail(messages, downloadDir));
			} while (folder.getMessageCount() != msgCount);

			// add listener
			folder.addMessageCountListener(new MessageCountAdapter() {
				@Override
				public void messagesAdded(MessageCountEvent ev) {

					Message[] messages = ev.getMessages();

					try {

						emails.addAll(processEmail(messages, downloadDir));
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});

			// wait for new messages
			ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
			Runnable pokeInbox = () -> {
				long end = System.nanoTime();
				long execution = end - start;
				System.out.println("Call scheduler at time: " + execution + " nanoseconds");

				if (folder.isOpen()) {

					System.out.println("sheduler" + execution);
//	    		    	try {
//							((com.sun.mail.imap.IMAPFolder) folder).idle();
//						} catch (MessagingException e) {
//							e.printStackTrace();
//						}
				}
				try {
					folder.getMessageCount();
				} catch (MessagingException ex) {
					ex.printStackTrace();
				}
			};
			scheduler.scheduleAtFixedRate(pokeInbox, 0, 5, TimeUnit.MINUTES);
			long end = System.nanoTime();
			long execution = end - start;
			System.out.println("Execution time: " + execution + " nanoseconds");

		} catch (FolderClosedException e) {
			e.printStackTrace();
			System.out.println("error connection was dropped");
			downloadPop3(emailAccount, downloadDir);
		}
		return emails;
	}

	public List<Email> processEmail(Message[] messages, String downloadDir) throws Exception {
		List<Email> emails = new ArrayList<Email>();
//			  int messageId = (messages.length);
		for (int i = 0; i < messages.length; i++) {
			Email email = new Email();
			email.from = messages[i].getFrom()[0].toString();

			email.sentDate = messages[i].getReceivedDate();
			Address[] ccArray = null;
			try {
				ccArray = messages[i].getRecipients(Message.RecipientType.CC);
			} catch (Exception e) {
				ccArray = null;
			}
			if (ccArray != null) {
				for (Address c : ccArray) {
					email.cc.add(c.toString());
				}
			}
			// subject
			email.subject = messages[i].getSubject();

			// received date
			if (messages[i].getReceivedDate() != null) {
				email.received = messages[i].getReceivedDate();
			} else {
				email.received = new Date();
			}

			// body and attachments
			email.body = "";
			Object content = messages[i].getContent();
			if (content instanceof java.lang.String) {

				email.body = (String) content;

			} else if (content instanceof Multipart) {

				Multipart mp = (Multipart) content;

				for (int j = 0; j < mp.getCount(); j++) {

					Part part = mp.getBodyPart(j);
					String disposition = part.getDisposition();
					if (disposition == null) {

						MimeBodyPart mbp = (MimeBodyPart) part;
						if (mbp.isMimeType("text/plain")) {
							// Plain
							email.body += (String) mbp.getContent();
						}
					} else if ((disposition != null) && (disposition.equalsIgnoreCase(Part.ATTACHMENT)
							|| disposition.equalsIgnoreCase(Part.INLINE))) {
						// } else if ((disposition != null) &&
						// (email.from.equals("mdjuikom@moriset.com")) ) {
						// } else if (disposition != null ) {
						// Check if plain
//				       MimeBodyPart mbp = (MimeBodyPart) part;
//						String attachFiles = "";
						EmailAttachment attachment = new EmailAttachment();
						attachment.name = decodeName(part.getFileName());
//						String fileName = part.getFileName();
//						attachFiles += fileName + ", ";
						File savedir = new File(downloadDir);
						savedir.mkdirs();
						// File savefile = File.createTempFile( "emailattach", ".atch", savedir);
						File savefile = new File(downloadDir, attachment.name);
						attachment.path = savefile.getAbsolutePath();
						attachment.size = saveFile(savefile, part);
						email.attachments.add(attachment);
						emails.add(email);

					}
				}
			}
		}
		return emails;
	}

	private static String decodeName(String name) throws Exception {
		if (name == null || name.length() == 0) {
			return "unknown";
		}
		String ret = java.net.URLDecoder.decode(name, "UTF-8");

		// also check for a few other things in the string:
		ret = ret.replaceAll("=\\?utf-8\\?q\\?", "");
		ret = ret.replaceAll("\\?=", "");
		ret = ret.replaceAll("=20", " ");

		return ret;
	}

	private static int saveFile(File saveFile, Part part) throws Exception {

		BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(saveFile));

		byte[] buff = new byte[4096];
		InputStream is = part.getInputStream();
		int ret = 0, count = 0;
		while ((ret = is.read(buff)) > 0) {
			bos.write(buff, 0, ret);
			count += ret;
		}
		bos.close();
		is.close();
		return count;
	}

	public EmailAccount getById(Long id, String projectCode) {
		Optional<EmailAccount> emailAccount = emailAccountRepository.findById(id);
		if (!emailAccount.isPresent()) {
			throw new BcephalException(HttpStatus.NOT_FOUND.value(), "this objet not exist");
		}

		return emailAccount.get();
	}

	public EditorData<EmailAccount> getEditorData(EditorDataFilter filter, HttpSession session, Locale locale) {
		EditorData<EmailAccount> data = new EditorData<EmailAccount>();
		data.setItem(new EmailAccount());
		// data.getItem().getChildren().setOriginalList(getEmailAccounts(locale));
		return data;
	}

	public List<EmailAccount> getEmailAccounts(java.util.Locale locale) {
		log.debug("Try to  retrieve Calendars.");
		if (getRepository() == null) {
			return null;
		}
		return getRepository().findAll();
	}

	public EmailAccount save(EmailAccount emailAccount, Locale locale, String projectCode) {
		log.debug("Try to  Save user : {}", emailAccount);

		try {
			if (emailAccount == null) {
				String message = messageSource.getMessage("unable.to.save.null.object", new Object[] { emailAccount },
						locale);
				throw new BcephalException(message);
			}

			emailAccount = getRepository().save(emailAccount);

			log.debug("Client successfully saved : {} ", emailAccount);
			return emailAccount;
		} catch (BcephalException e) {
			throw e;
		} catch (Exception e) {
			log.error("Unexpected error while save client : {}", emailAccount, e);
			String message = messageSource.getMessage("unable.to.save.client", new Object[] { emailAccount }, locale);
			throw new BcephalException(HttpStatus.INTERNAL_SERVER_ERROR.value(), message);
		}
	}

	public BrowserDataPage<EmailAccount> search(BrowserDataFilter filter, java.util.Locale locale, Long profileId,
			String projectCode) {
		if (filter.getPage() == 0) {
			filter.setPage(1);
		}
		BrowserDataPage<EmailAccount> page = new BrowserDataPage<EmailAccount>();
		page.setPageSize(filter.getPageSize());

//		String functionalityCode = getBrowserFunctionalityCode();
//		List<Long> hidedObjectIds = new ArrayList<>(0);
//		if(StringUtils.hasText(functionalityCode)) {
//			hidedObjectIds =  getHidedObjectId(profileId,functionalityCode,projectCode);
//		}

		Specification<EmailAccount> specification = getBrowserDatasSpecification(filter, locale);
		if (filter.isShowAll()) {
			List<EmailAccount> items = emailAccountRepository.findAll(specification,
					getBrowserDatasSort(filter, locale));
			page.setItems(items);

			page.setCurrentPage(1);
			page.setPageCount(1);
			page.setTotalItemCount(items.size());

			if (page.getCurrentPage() > page.getPageCount()) {
				page.setCurrentPage(page.getPageCount());
			}
			page.setPageFirstItem(1);
			page.setPageLastItem(page.getPageFirstItem() + page.getItems().size() - 1);
		} else {
			Page<EmailAccount> oPage = emailAccountRepository.findAll(specification,
					getPageable(filter, getBrowserDatasSort(filter, locale)));
			page.setItems(oPage.getContent());

			page.setCurrentPage(filter.getPage() > 0 ? filter.getPage() : 1);
			page.setPageCount(oPage.getTotalPages());
			page.setTotalItemCount(Long.valueOf(oPage.getTotalElements()).intValue());

			if (page.getCurrentPage() > page.getPageCount()) {
				page.setCurrentPage(page.getPageCount());
			}
			page.setPageFirstItem(((page.getCurrentPage() - 1) * page.getPageSize()) + 1);
			page.setPageLastItem(page.getPageFirstItem() + page.getItems().size() - 1);
		}

		return page;
	}

	protected String getBrowserFunctionalityCode() {
		return "";
	}

	protected List<Long> getHidedObjectId(Long profileId, String functionalityCode, String projectCode) {
		return new ArrayList<>();
	}

	public Sort getBrowserDatasSort(BrowserDataFilter filter, java.util.Locale locale) {
		if (filter.getColumnFilters() != null) {

			if (filter.getColumnFilters().isSortFilter()) {
				build(filter.getColumnFilters());
				if (filter.getColumnFilters().getLink() != null
						&& filter.getColumnFilters().getLink().equals(BrowserDataFilter.SortByDesc)) {

					if (!filter.getColumnFilters().isJoin()) {
						return Sort.by(Order.desc(filter.getColumnFilters().getName()));
					} else {
						String name = filter.getColumnFilters().getJoinName() + "_"
								+ filter.getColumnFilters().getName();
						return Sort.by(Order.desc(name));
					}
				} else {
					if (!filter.getColumnFilters().isJoin()) {
						return Sort.by(Order.asc(filter.getColumnFilters().getName()));
					} else {
						String name = filter.getColumnFilters().getJoinName() + "_"
								+ filter.getColumnFilters().getName();
						return Sort.by(Order.asc(name));
					}
				}
			} else {
				if (filter.getColumnFilters().getItems() != null && filter.getColumnFilters().getItems().size() > 0) {
					for (ColumnFilter columnFilter : filter.getColumnFilters().getItems()) {
						if (columnFilter.isSortFilter()) {
							build(columnFilter);
							if (columnFilter.getLink() != null
									&& columnFilter.getLink().equals(BrowserDataFilter.SortByDesc)) {
								if (!columnFilter.isJoin()) {
									return Sort.by(Order.desc(columnFilter.getName()));
								} else {
									String name = columnFilter.getJoinName() + "_" + columnFilter.getName();
									return Sort.by(Order.desc(name));
								}
							} else {
								if (!columnFilter.isJoin()) {
									return Sort.by(Order.asc(columnFilter.getName()));
								} else {
									String name = columnFilter.getJoinName() + "_" + columnFilter.getName();
									return Sort.by(Order.asc(name));
								}
							}
						}
					}
				}
			}
		}
		return Sort.by(Order.asc("id"));
	}

	public Pageable getPageable(BrowserDataFilter filter, Sort sort) {
		Pageable paging = PageRequest.of(filter.getPage() - 1, filter.getPageSize());
		if (sort != null) {
			paging = PageRequest.of(filter.getPage() - 1, filter.getPageSize(), sort);
		}
		return paging;
	}

//	public List<EmailAccount> buildBrowserData(List<EmailAccount> contents) {
//		List<EmailAccount> items = new ArrayList<EmailAccount>(0);
//		if (contents != null) {
//			contents.forEach(item -> {
//				EmailAccount element = getNewBrowserData(item);
//				if (element != null) {
//					items.add(element);
//				}
//			});
//		}
//		return items;
//	}

	protected EmailAccount getNewBrowserData(EmailAccount item) {
		return item;
	}

	public Specification<EmailAccount> getBrowserDatasSpecification(BrowserDataFilter filter, java.util.Locale locale) {
		return (root, query, cb) -> {
			RequestQueryBuilder<EmailAccount> qBuilder = new RequestQueryBuilder<EmailAccount>(root, query, cb);
//			qBuilder.select(JobDataExecution.class, root.get("id"),root.get("version"),root.get("instance"),
//					root.get("status"),root.get("exitCode"),root.get("message"),root.get("creationDate"),
//					root.get("startDate"),root.get("endDate"),root.get("modificationDate"));
			qBuilder.select(EmailAccount.class);

			if (filter.getColumnFilters() != null) {
				build(filter.getColumnFilters());
				filter.getColumnFilters().getItems().forEach(filte -> {
					build(filte);
				});
				qBuilder.addFilter(filter.getColumnFilters());
			}
			return qBuilder.build();
		};
	}

	protected void build(ColumnFilter columnFilter) {
		if ("serve_host".equalsIgnoreCase(columnFilter.getName())) {
			columnFilter.setName("serve_host");
			columnFilter.setType(String.class);

		} else if ("password".equalsIgnoreCase(columnFilter.getName())) {
			columnFilter.setName("password");
			columnFilter.setType(String.class);
		} else if ("userName".equalsIgnoreCase(columnFilter.getName())) {
			columnFilter.setName("userName");
			columnFilter.setType(String.class);
		}
//		else if ("serve_host".equalsIgnoreCase(columnFilter.getName())) {
//			columnFilter.setName("serve_host");
//			columnFilter.setType(String.class);
//		} 
//		else if ("server_port".equalsIgnoreCase(columnFilter.getName())) {
//			columnFilter.setName("server_port");
//			columnFilter.setType(String.class);
//		} 
//		else if ("propertiesImpl".equalsIgnoreCase(columnFilter.getName())) {
//			columnFilter.setName("propertiesImpl");
//			columnFilter.setType(String.class);
//		} 
//		else if ("cycle".equalsIgnoreCase(columnFilter.getName())) {
//			columnFilter.setName("cycle");
//			columnFilter.setType(Boolean.class);
//		}
//		else if ("size".equalsIgnoreCase(columnFilter.getName())) {
//			columnFilter.setName("size");
//			columnFilter.setType(Integer.class);
//		}
//		else if ("prefix".equalsIgnoreCase(columnFilter.getName())) {
//			columnFilter.setName("prefix");
//			columnFilter.setType(String.class);
//		}
//		else if ("suffix".equalsIgnoreCase(columnFilter.getName())) {
//			columnFilter.setName("suffix");
//			columnFilter.setType(String.class);
//		}
	}

	@Override
	public Long getId() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setId(Long val) {
		// TODO Auto-generated method stub

	}

	@Override
	public Persistent copy() {
		// TODO Auto-generated method stub
		return null;
	}

}
