/**
 * 
 */
package com.moriset.bcephal.billing.service.action;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.moriset.bcephal.alarm.domain.Alarm;
import com.moriset.bcephal.alarm.domain.AlarmAudience;
import com.moriset.bcephal.alarm.domain.AlarmAudienceType;
import com.moriset.bcephal.alarm.domain.AlarmInvoiceConsolidation;
import com.moriset.bcephal.alarm.domain.AlarmInvoiceTrigger;
import com.moriset.bcephal.alarm.domain.EmailType;
import com.moriset.bcephal.alarm.service.AlarmVariables;
import com.moriset.bcephal.billing.domain.InvoiceStatus;
import com.moriset.bcephal.billing.domain.InvoiceType;
import com.moriset.bcephal.billing.repository.BillingRunOutcomeRepository;
import com.moriset.bcephal.billing.service.batch.BillingContext;
import com.moriset.bcephal.config.messaging.MessengerClient;
import com.moriset.bcephal.domain.AlarmMessage;
import com.moriset.bcephal.domain.Document;
import com.moriset.bcephal.domain.MailSendingStatus;
import com.moriset.bcephal.domain.RunModes;
import com.moriset.bcephal.domain.dimension.Attribute;
import com.moriset.bcephal.domain.dimension.DimensionType;
import com.moriset.bcephal.domain.universe.UniverseSourceType;
import com.moriset.bcephal.grid.domain.Grille;
import com.moriset.bcephal.grid.domain.GrilleCategory;
import com.moriset.bcephal.grid.domain.GrilleColumn;
import com.moriset.bcephal.grid.domain.GrilleDataFilter;
import com.moriset.bcephal.grid.domain.GrilleType;
import com.moriset.bcephal.grid.service.GrilleService;
import com.moriset.bcephal.grid.service.InvoiceGridQueryBuilder;
import com.moriset.bcephal.repository.DocumentRepository;
import com.moriset.bcephal.security.domain.UserData;
import com.moriset.bcephal.security.service.SecurityService;
import com.moriset.bcephal.service.condition.ConditionExpressionEvaluator;
import com.moriset.bcephal.utils.BcephalException;

import jakarta.jms.JMSException;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Data
@Slf4j
@Component
@Scope(scopeName= ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class BillingAlarmRunner {
		
	@Autowired(required = false)
	MessengerClient messengerClient;
	
	@Autowired
	SecurityService securityService;
	
	@Autowired
	ConditionExpressionEvaluator evaluator;
	
	@PersistenceContext
	EntityManager entityManager;
	
	@Autowired
	GrilleService grilleService;
	
	@Autowired
	BillingRunOutcomeRepository billingRunOutcomeRepository;
	
	@Autowired
	DocumentRepository documentRepository;

	String invoiceView;        
    String contactView;
    String universeInvoiceView;
	
	
	public int sendAlarm(Alarm alarm, String username, RunModes mode, String projectCode, Long clientID, 
			List<Long> ids, boolean isForRunOutcome, boolean isFromRepository, BillingContext context) throws Exception {		
		log.debug("Try to send invoices : {}", alarm.getName());
		log.debug("Alarm : {}  - Try to evaluate condition...", alarm.getName());	
		int count = 0;
		List<Long> invoiceIds = isForRunOutcome ? new ArrayList<>() : ids;
		List<String> runNbrs = isForRunOutcome && ids.size() > 0? billingRunOutcomeRepository.findRunNbrsByIds(ids) : new ArrayList<>();
		List<Client> clients = buildViews(alarm, invoiceIds, runNbrs, context);
		if(clients.size() == 0) {
			log.debug("There is no invoice to send!");
		}
		
		if(evaluator.isValidCondExpr(alarm.getCondition())) {	
			log.debug("Alarm : {}  - Condition is valid!", alarm.getName());
			if(alarm.isSendEmail()) {
				for(Client client : clients) {
					List<String> audiences = buildAudiences(alarm, client, true, false);
					String ccAudiences = buildCcAudiences(alarm, client, true, false);
					int c = buildAndSendMessage(alarm, client, audiences, ccAudiences, AlarmMessage.MAIL, username, mode, projectCode, clientID, context);
					count += c;
				}				
			}			
		}
		else {
			log.debug("Alarm : {}  - Condition is not valid!", alarm.getName());
			throw new BcephalException("Unable to send alarm : Condition is not valid!");
		}
		return count;
	}
	
	private List<String> buildAudiences(Alarm alarm, Client client, boolean forMail, boolean forSms){
		List<String> audiences = new ArrayList<>();
		if(StringUtils.hasText(client.getEmail())) {
			audiences.add(client.getEmail());
		}
		for(AlarmAudience audience : alarm.getAudienceListChangeHandler().getItems()) {
			if(audience.getEmailType() != EmailType.CC) {
				List<String> address = getAudienceAddress(audience, forMail, forSms);
				if(address.size() > 0) {
					for(String a : address) {
						if(StringUtils.hasText(a)) {
							audiences.add(a);
						}
					}
				}			
				else {
					log.debug("Wrong audience address : {}", audience);
				}
			}
		}
		return audiences;
	}
	
	private String buildCcAudiences(Alarm alarm, Client client, boolean forMail, boolean forSms){
		String audiences = "";
		String coma = "";	
		if(StringUtils.hasText(client.getEmailCc())) {
			audiences = client.getEmailCc();
			coma = ";";
		}
		for(AlarmAudience audience : alarm.getAudienceListChangeHandler().getItems()) {
			if(audience.getEmailType() == EmailType.CC) {
				List<String> address = getAudienceAddress(audience, forMail, forSms);
				if(address.size() > 0) {
					for(String a : address) {
						if(StringUtils.hasText(a)) {
							audiences += coma + a;
							coma = ";";
						}
					}
				}			
				else {
					log.debug("Wrong audience address : {}", audience);
				}
			}
		}
		return audiences;
	}
	
	private List<String> getAudienceAddress(AlarmAudience audience, boolean forMail, boolean forSms) {
		List<String> address = new ArrayList<>();
		if(audience.getAudienceType() == AlarmAudienceType.FREE) {
			address.add(forMail ? audience.getEmail() : forSms ? audience.getPhone() : null);
		}
		else if(audience.getAudienceType() == AlarmAudienceType.USER && audience.getUserOrProfilId() != null) {
			UserData user = securityService.getUserById(audience.getUserOrProfilId());
			if(user != null) {
				address.add(user.getEmail());
			}
			else {
				log.debug("User not found : {}", audience.getUserOrProfilId());
			}
		}
		else if(audience.getAudienceType() == AlarmAudienceType.PROFILE && audience.getUserOrProfilId() != null) {
			List<UserData> users = securityService.getUsersByProfileId(audience.getUserOrProfilId());
			if(users.size() > 0) {
				for(UserData user : users) {
					address.add(user.getEmail());
				}
			}
			else {
				log.debug("User not found : {}", audience.getUserOrProfilId());
			}
		}
		return address;
	}
		
	private int buildAndSendMessage(Alarm alarm, Client client, List<String> audiences, String ccAudiences, String type, String username, RunModes mode, 
			String projectCode, Long clientId, BillingContext context) {
		int count = 0;
		if (audiences != null && !audiences.isEmpty()) {
			log.debug("Alarm : {}  - {} audience : {}", alarm.getName(), type, audiences);
			List<Invoice> invoices = getInvoiceByClient(client.getNumber());
			if(alarm.getInvoiceConsolidation() == AlarmInvoiceConsolidation.ONE_MAIL_PER_CLIENT) {
				AlarmMessage msg = buildAlarmMessage(alarm, client, invoices, audiences, ccAudiences, type, username, mode, projectCode, clientId);
				sendAlertMessage(msg);
				updateSentStatus(invoices, context);
				count += invoices.size();
			}
			else {
				for(Invoice invoice : invoices) {
					List<Invoice> refs = new ArrayList<>();
					refs.add(invoice);
					AlarmMessage msg = buildAlarmMessage(alarm, client, refs, audiences, ccAudiences, type, username, mode, projectCode, clientId);
					sendAlertMessage(msg);
					updateSentStatus(refs, context);
					count += refs.size();
				}
			}
		}
		else {
			log.debug("Alarm : {}  - There is no {} audience!", alarm.getName());
		}
		return count;
	}
	
	protected void updateSentStatus(List<Invoice> invoices, BillingContext context) {	
		List<Number> ids = new ArrayList<>();
		String sql = context.getSendingInvoiceSQL();
		Query query = getEntityManager().createNativeQuery(sql);
		for(Invoice invoice : invoices) {
			ids.add(invoice.getId());
			
			int index = 1;
			query.setParameter(index++, context.invoiceMailStatusSentValue);
			query.setParameter(index++, invoice.getNumber());
			query.setParameter(index++, invoice.isCreditNote() ? UniverseSourceType.CREDIT_NOTE.toString() : UniverseSourceType.INVOICE.toString());	
			query.setParameter(index++, invoice.getNumber());
			query.executeUpdate();
		}
		
		sql = "UPDATE BCP_BILLING_INVOICE SET mailStatus = :status WHERE ID IN :ids ";
		query = getEntityManager().createNativeQuery(sql);
		query.setParameter("status", MailSendingStatus.SENT.name());
		query.setParameter("ids", ids);
		query.executeUpdate();
	}

	private void sendAlertMessage(AlarmMessage msg) {
		log.debug("Try to send an alert message");
		
		try {
			if (msg.getMessageType().equalsIgnoreCase(AlarmMessage.MAIL)) {
				messengerClient.SendMail(msg);
			} else {
				messengerClient.SendSms(msg);
			}
		} catch (JsonProcessingException e) {
			log.error("JSON formatting error: {}", e.getMessage());
		} catch (JMSException e) {
			log.error("Message error: {}", e.getMessage());
		}
		
		log.debug("Message has been successfully sent.");
	}
	
	/**
	 * 
	 * @param audiences
	 * @param messageType
	 * @return
	 */
	private AlarmMessage buildAlarmMessage(Alarm alarm, Client client, List<Invoice> invoices, List<String> audiences, String ccAudiences, String messageType, String username, RunModes mode, String projectCode, Long clientId) {
		log.debug("Building message to send.");		
		AlarmMessage message = new AlarmMessage();
		message.setUsername(username);
		message.setMode(mode);
		message.setAutomaticMailModelName(alarm.getName());
		message.setCategory(alarm.getCategory() != null ? alarm.getCategory().name() : null);
		message.setProjectCode(projectCode);
		message.setClientCode(clientId != null ? clientId.toString(): null);
		if (messageType.equalsIgnoreCase(AlarmMessage.MAIL)) {
			message.setTitle(replaceMessageKeys(alarm.getEmailTitle(), username, client, invoices));
			message.setContent(replaceMessageKeys(alarm.getEmail(), username, client, invoices));
			message.setFilesId(null);
		}
		message.setMessageType(messageType);
		message.setContacts(audiences);	
		message.setCcContacts(ccAudiences);	
		
		String ref1 = "";
		String coma = "";
		List<String> files = new ArrayList<>();
		for(Invoice invoice : invoices) {
			ref1 += coma + invoice.getNumber();
			coma = " ; ";
			files.add(invoice.getFile());
			if(StringUtils.hasText(invoice.getOtherFiles())) {
				files.add(invoice.getOtherFiles());
			}
			
			try {
				List<Document> docs = documentRepository.findBySubjectTypeAndSubjectId(coma, invoice.getId().longValue());
				for(Document doc : docs) {
					if(StringUtils.hasText(doc.getCode())) {
						files.add(doc.getCode());
					}
				}
			}
			catch (Exception e) {
				log.error("", e);
			}
		}
		
		message.setReference1(ref1);
		message.setFilesId(files);
		
		return message;
	}
	
	private String replaceMessageKeys(String message, String username, Client client, List<Invoice> invoices) {	
		if(StringUtils.hasText(message)) {
			AlarmVariables alarmKeys = new AlarmVariables();
			Date date = new Date();
			message = message.replace(alarmKeys.USER, username);
			message = message.replace(alarmKeys.CURRENT_TIME, new SimpleDateFormat("HH:mm:ss").format(date));
			message = message.replace(alarmKeys.CURRENT_DATE, new SimpleDateFormat("yyyy/MM/dd").format(date));
			message = message.replace(alarmKeys.CURRENT_DATE_TIME, new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(date));	
			
			message = message.replace(alarmKeys.INVOICE_DATE, new SimpleDateFormat("dd/MM/yyyy").format(invoices.get(0).getDate()));
			message = message.replace(alarmKeys.INVOICE_DUE_DATE, invoices.get(0).getDueDate() != null ? new SimpleDateFormat("dd/MM/yyyy").format(invoices.get(0).getDueDate()) : " ");
			message = message.replace(alarmKeys.INVOICE_PERIOD_FROM, new SimpleDateFormat("dd/MM/yyyy").format(invoices.get(0).getDate()));
			message = message.replace(alarmKeys.INVOICE_PERIOD_TO, new SimpleDateFormat("dd/MM/yyyy").format(invoices.get(0).getDate2()));
			
			message = message.replace(alarmKeys.INVOICE_DAY, new SimpleDateFormat("dd").format(invoices.get(0).getDate()));
			message = message.replace(alarmKeys.INVOICE_MONTH, new SimpleDateFormat("MM").format(invoices.get(0).getDate()));
			message = message.replace(alarmKeys.INVOICE_MONTH_NAME, new SimpleDateFormat("MMMM").format(invoices.get(0).getDate()));
			message = message.replace(alarmKeys.INVOICE_YEAR, new SimpleDateFormat("yyyy").format(invoices.get(0).getDate()));
			
			
			message = message.replace(alarmKeys.INVOICE_REFERENCE, invoices.get(0).getNumber());
			message = message.replace(alarmKeys.INVOICE_DESCRIPTION, invoices.get(0).getDescription());
			message = message.replace(alarmKeys.CLIENT_ID, client.getNumber() != null ? client.getNumber() : "");
			message = message.replace(alarmKeys.CLIENT_NAME, client.getName() != null ? client.getName() : "");
			
			message = message.replace(alarmKeys.CONTACT_TITLE, client.getContactTitle() != null ? client.getContactTitle() : "");
			message = message.replace(alarmKeys.CONTACT_FIRSTNAME, client.getContactFirstname() != null ? client.getContactFirstname() : "");
			message = message.replace(alarmKeys.CONTACT_LASTNAME, client.getContactLastname() != null ? client.getContactLastname() : "");
			
			
		}
		return message;
	}

	
	
	protected List<Client> buildViews(Alarm alarm, List<Long> invoiceIds, List<String> runNbrs, BillingContext context) throws Exception {
		
        invoiceView = "INVOICE_MAIL_REPO_VIEW_" + (alarm.getId() != null ? alarm.getId() + "_" + System.currentTimeMillis() : System.currentTimeMillis());        
        contactView = "CONTACT_MAIL_VIEW_" + (alarm.getId() != null ? alarm.getId() + "_" + System.currentTimeMillis() : System.currentTimeMillis());
        universeInvoiceView = "UNIVERSE_INVOICE_VIEW_" + (alarm.getId() != null ? alarm.getId() + "_" + System.currentTimeMillis() : System.currentTimeMillis());
        		
		try{
			GrilleDataFilter filter = new GrilleDataFilter();
			filter.setGrid(buildInvoiceGrid(alarm, context));
			filter.setIds(invoiceIds);
			grilleService.loadFilterClosures(filter);	
			InvoiceGridQueryBuilder builder = new InvoiceGridQueryBuilder(filter);
			String sql = builder.buildQuery();
			
			
			
			log.debug("Automatic Mail Run : {} - Try drop view : {} ", alarm.getName(), invoiceView);
			String dropSql = "DROP MATERIALIZED VIEW IF EXISTS " + invoiceView + " CASCADE";	
			Query query = entityManager.createNativeQuery(dropSql);
			query.executeUpdate();
			log.debug("Automatic Mail Run : {} - View dropped! {}", alarm.getName(), invoiceView);
			
			log.debug("Automatic Mail Run : {} - Try drop view : {} ", alarm.getName(), contactView);
			dropSql = "DROP MATERIALIZED VIEW IF EXISTS " + contactView;	
			query = entityManager.createNativeQuery(dropSql);
			query.executeUpdate();
			log.debug("Automatic Mail Run : {} - View dropped! {}", alarm.getName(), contactView);
			
			
			log.debug("Automatic Mail Run : {} - Try drop view : {} ", alarm.getName(), universeInvoiceView);
			dropSql = "DROP MATERIALIZED VIEW IF EXISTS " + universeInvoiceView;	
			query = entityManager.createNativeQuery(dropSql);
			query.executeUpdate();
			log.debug("Automatic Mail Run : {} - View dropped! {}", alarm.getName(), universeInvoiceView);					
			
						
			
						
			log.debug("Automatic Mail Run : {} - Try create view : {} ", alarm.getName(), universeInvoiceView);
			log.debug("Automatic Mail Run : {} - View query builded! {}", alarm.getName(), universeInvoiceView);
			log.trace("QSL = {}", sql);
			String createSql = "CREATE MATERIALIZED VIEW ".concat(universeInvoiceView).concat(" AS ").concat(sql);
			query = entityManager.createNativeQuery(createSql);
			log.trace("Automatic Mail Run : {} - Query created for view {}", alarm.getName(), universeInvoiceView);
			query.executeUpdate();
			log.trace("Automatic Mail Run : {} - Query executed for view {}", alarm.getName(), universeInvoiceView);
			log.debug("Automatic Mail Run : {} - View created! {}", alarm.getName(), universeInvoiceView);
			
			
			log.debug("Automatic Mail Run : {} - Try create view : {} ", alarm.getName(), invoiceView);
			
			
			String invoiceSql = buildInvoiceSql(alarm, context, new ArrayList<>(), runNbrs, universeInvoiceView);
			log.debug("Automatic Mail Run : {} - View query builded! {}", alarm.getName(), invoiceView);
			log.trace("QSL = {}", invoiceSql);
			createSql = "CREATE MATERIALIZED VIEW ".concat(invoiceView).concat(" AS ").concat(invoiceSql);
			query = entityManager.createNativeQuery(createSql);
			log.trace("Automatic Mail Run : {} - Query created for view {}", alarm.getName(), invoiceView);
			query.executeUpdate();
			log.trace("Automatic Mail Run : {} - Query executed for view {}", alarm.getName(), invoiceView);
			log.debug("Automatic Mail Run : {} - View created! {}", alarm.getName(), invoiceView);
			
						
			
			log.debug("Automatic Mail Run : {} - Try create view : {} ", alarm.getName(), contactView);
			log.debug("Automatic Mail Run : {} - Try build view query : {} ", alarm.getName(), contactView);
			
			String contactSql = "SELECT DISTINCT clientNumber, clientName, clientEmail, clientEmailCc, contactTitle, contactFirstname, contactLastname FROM ".concat(invoiceView);
			
			log.debug("Automatic Mail Run : {} - View query builded! {}", alarm.getName(), contactView);
			log.trace("QSL = {}", createSql);
			createSql = "CREATE MATERIALIZED VIEW ".concat(contactView).concat(" AS ").concat(contactSql);
			query = entityManager.createNativeQuery(createSql);
			query.executeUpdate();
			log.debug("Automatic Mail Run : {} - View created! {}", alarm.getName(), contactView);
					
				
			log.debug("Automatic Mail Run : {} - Try read clients", alarm.getName());
			String countSql = "SELECT clientNumber, clientName, clientEmail, clientEmailCc, contactTitle, contactFirstname, contactLastname FROM ".concat(contactView);						
			query = entityManager.createNativeQuery(countSql);
			@SuppressWarnings("unchecked")
			List<Object[]> results = query.getResultList();			
			List<Client> clients = new ArrayList<>();
			if(results.size() == 1) {
				clients.add(new Client(results.get(0)));
			}
			else {
				for(Object[] data : results) {
					clients.add(new Client(data));
				}
			}
			log.debug("Automatic Mail Run : {} - clients readed : {} ", alarm.getName(), clients.size());
			
			return clients;			
		}
		catch (Exception e) {
			log.error("Automatic Mail Run : {} - ERROR : ", alarm.getName(), e);
			throw new BcephalException("Unable to create views", e);
		}
		finally {
			
		}
	}
	
	protected List<Invoice> getInvoiceByClient(String clientNumber){
		log.debug("Automatic Mail Run : {} - Try read invoice by client : {}", clientNumber);
		String countSql = "SELECT id, type, reference, file, otherFiles, invoiceDate, invoiceDate2, billingDate, dueDate, description  FROM ".concat(invoiceView).concat(" WHERE clientNumber = :clientNumber");
		Query query = entityManager.createNativeQuery(countSql);
		query.setParameter("clientNumber", clientNumber);
		@SuppressWarnings("unchecked")
		List<Object[]> results = query.getResultList();			
		List<Invoice> invoices = new ArrayList<>();
		if(results.size() == 1) {
			invoices.add(new Invoice(results.get(0)));
		}
		else {
			for(Object[] data : results) {
				invoices.add(new Invoice(data));
			}
		}	
		
		log.debug("Automatic Mail Run : {} - Ivoices readed : {} ", clientNumber, invoices.size());
		
		return invoices;		
	}
	
	
	private String buildInvoiceSql(Alarm alarm, BillingContext context, List<Long> invoiceIds, List<String> runNbrs, String universeInvoiceView) {
		String col = new Attribute(context.invoiceRefId).getUniverseTableColumnName();
		String primarySql = "SELECT invoice.id, invoice.reference, invoice.description, invoice.clientNumber, invoice.clientName, invoice.clientEmail, invoice.clientEmailCc, invoice.status, invoice.contactTitle, invoice.contactFirstname, invoice.contactLastname, "
				+ "invoice.amountWithoutVat, invoice.vatAmount, invoice.file, invoice.otherFiles, invoice.fileName, invoice.invoiceDate, invoice.invoiceDate2, invoice.billingDate, invoice.dueDate, invoice.communicationMessage, type " 
				+ " FROM " + universeInvoiceView + " univ "
				+ " LEFT JOIN BCP_BILLING_INVOICE invoice ON invoice.reference = univ." + col;
						
		String where = " WHERE ";
		primarySql += where + "invoice.reference IS NOT NULL";
		where = " AND ";
				
		if(alarm.getInvoiceTrigger() == AlarmInvoiceTrigger.INVOICE_VALIDATED 
				|| alarm.getInvoiceTrigger() == AlarmInvoiceTrigger.INVOICE_VALIDATED_AND_SENT
				|| alarm.getInvoiceTrigger() == AlarmInvoiceTrigger.INVOICE_VALIDATED_AND_NOT_YET_SENT) {
			primarySql += where + "invoice.status = '" + InvoiceStatus.VALIDATED.name() + "'";
			where = " AND ";
		}	
		if(alarm.getInvoiceTrigger() == AlarmInvoiceTrigger.INVOICE_NOT_YET_VALIDATED) {
			primarySql += where + "invoice.status != '" + InvoiceStatus.VALIDATED.name() + "'";
			where = " AND ";
		}
		if(alarm.getInvoiceTrigger() == AlarmInvoiceTrigger.INVOICE_VALIDATED_AND_NOT_YET_SENT) {
			primarySql += where + "(invoice.mailStatus IS NULL OR invoice.mailStatus != '" + MailSendingStatus.SENT.name() + "')";
			where = " AND ";
		}
		if(alarm.getInvoiceTrigger() == AlarmInvoiceTrigger.INVOICE_VALIDATED_AND_SENT) {
			primarySql += where + "invoice.mailStatus = '" + MailSendingStatus.SENT.name() + "'";
			where = " AND ";
		}
		
					
		if(runNbrs != null && runNbrs.size() > 0) {
			primarySql += where + "(";
			String or = "";
			for(String nbr : runNbrs) {
				primarySql += or + "invoice.runNumber = '" + nbr + "'";
				or = " OR ";
			}
			primarySql += ")";
			where = " AND ";
		}
		
		if(invoiceIds != null && invoiceIds.size() > 0) {
			primarySql += where + "(";
			String or = "";
			for(Long id : invoiceIds) {
				primarySql += or + "invoice.id = " + id;
				or = " OR ";
			}
			primarySql += ")";
			where = " AND ";
		}
		
		return primarySql;
	}
	
	
	private Grille buildInvoiceGrid(Alarm alarm, BillingContext context) {
		Grille grid = new Grille();
		grid.setCategory(GrilleCategory.SYSTEM);
		grid.setName("Invoices");
		grid.setType(GrilleType.INVOICE_REPOSITORY);
		
		addColum(grid, "Reference", context.invoiceRefId, DimensionType.ATTRIBUTE, true);
		addColum(grid, "Client ID", context.clientIdId, DimensionType.ATTRIBUTE, false);
		addColum(grid, "Client name", context.clientNameId, DimensionType.ATTRIBUTE, false);		
		addColum(grid, "Amount without VAT", context.invoiceAmountWithoutVatId, DimensionType.MEASURE, false);
		addColum(grid, "VAT amount", context.invoiceVatAmountId, DimensionType.MEASURE, false);	
		addColum(grid, "Total amount", context.invoiceTotalAmountId, DimensionType.MEASURE, false);
		addColum(grid, "Status", context.invoiceStatusId, DimensionType.ATTRIBUTE, true);
		addColum(grid, "Send status", context.invoiceMailStatusId, DimensionType.ATTRIBUTE, true);
		addColum(grid, "Run n°", context.billingRunId, DimensionType.ATTRIBUTE, false);
		addColum(grid, "Creation date", context.billingRunDateId, DimensionType.PERIOD, false);
		addColum(grid, "Description", context.invoiceDescriptionId, DimensionType.ATTRIBUTE, false);
		
		grid.setUserFilter(alarm.getFilter());
		
		return grid;
	}
	
	protected void addColum(Grille grid, String name, Long dimensionId, DimensionType type,
			boolean mandatory) {
		int position = grid.getColumnListChangeHandler().getItems().size();
		GrilleColumn column = buildColumn(name, dimensionId, type, position, mandatory);
		grid.getColumnListChangeHandler().addNew(column);
	}
	
	protected GrilleColumn buildColumn(String name, Long dimensionId, DimensionType type, int position, boolean mandatory) {
		GrilleColumn column = new GrilleColumn();
		column.setName(name);
		column.setType(type);
		column.setDimensionId(dimensionId);
		column.setPosition(position);
		column.setMandatory(mandatory);
		column.getFormat().setUsedSeparator(true);
		return column;
	}
	
	@Data
	@AllArgsConstructor
	public class Client {
		
		String number;
		String name;
		String email;
		String emailCc;
		String contactTitle;
		String contactFirstname;
		String contactLastname;
		
		public Client(Object[] data) {
			number = (String)data[0];
			name = (String)data[1];
			email = (String)data[2];
			emailCc = (String)data[3];
			contactTitle = data.length > 4 ? (String)data[4] : "";
			contactFirstname = data.length > 5 ? (String)data[5] : "";
			contactLastname = data.length > 6 ? (String)data[6] : "";
		}
		
	}
	
	@Data
	@AllArgsConstructor
	public class Invoice {
		
		Number id;
		String type;
		String number;
		String description;
		String file;
		private String otherFiles;
		Date date;
		Date date2;
		Date billingDate;
		Date dueDate;
		
		public Invoice(Object[] data) {
			id = (Number)data[0];
			type = (String)data[1];
			number = (String)data[2];
			file = (String)data[3];
			otherFiles = (String)data[4];
			date = (Date)data[5];
			date2 = (Date)data[6];
			billingDate = (Date)data[7];		
			dueDate = (Date)data[8];		
			description = (String)data[9];		
			if(date2 == null) {
				date2 = date;
			}
		}
		
		
		@JsonIgnore
		public boolean isInvoice() {
			return getType() != null && getType().equals(InvoiceType.INVOICE.name());
		}
		
		@JsonIgnore
		public boolean isCreditNote() {
			return getType() != null && getType().equals(InvoiceType.CREDIT_NOTE.name());
		}
	}

}


