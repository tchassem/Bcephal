/**
 * 
 */
package com.moriset.bcephal.billing.service.batch;

import java.io.File;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

import org.hibernate.query.TypedParameterValue;
import org.hibernate.type.StandardBasicTypes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.moriset.bcephal.billing.domain.BillTemplate;
import com.moriset.bcephal.billing.domain.BillingModel;
import com.moriset.bcephal.billing.domain.BillingModelAppendicyType;
import com.moriset.bcephal.billing.domain.BillingModelEnrichmentItem;
import com.moriset.bcephal.billing.domain.BillingRunOutcome;
import com.moriset.bcephal.billing.domain.Invoice;
import com.moriset.bcephal.billing.domain.InvoiceItem;
import com.moriset.bcephal.billing.domain.InvoiceStatus;
import com.moriset.bcephal.billing.repository.BillTemplateRepository;
import com.moriset.bcephal.billing.service.BillTemplateService;
import com.moriset.bcephal.billing.service.BillingCompanyRepositoryService;
import com.moriset.bcephal.billing.service.BillingFileUploaderService;
import com.moriset.bcephal.billing.service.BillingModelService;
import com.moriset.bcephal.billing.service.BillingRunOutcomeService;
import com.moriset.bcephal.billing.service.InvoiceService;
import com.moriset.bcephal.domain.BillingParameterCodes;
import com.moriset.bcephal.domain.DataSourceType;
import com.moriset.bcephal.domain.RunModes;
import com.moriset.bcephal.domain.VariableValue;
import com.moriset.bcephal.domain.dimension.DimensionType;
import com.moriset.bcephal.domain.parameter.IncrementalNumber;
import com.moriset.bcephal.domain.socket.TaskProgressListener;
import com.moriset.bcephal.domain.universe.UniverseSourceType;
import com.moriset.bcephal.grid.domain.GrilleDataFilter;
import com.moriset.bcephal.grid.domain.GrilleExportData;
import com.moriset.bcephal.grid.domain.GrilleExportDataType;
import com.moriset.bcephal.grid.domain.Join;
import com.moriset.bcephal.grid.service.ExportGrilleRunner;
import com.moriset.bcephal.grid.service.GrilleService;
import com.moriset.bcephal.repository.filters.IncrementalNumberRepository;
import com.moriset.bcephal.utils.BcephalException;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Joseph Wambo
 *
 */
@Service
@Data
@Slf4j
@Scope(scopeName = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class BillingRunner {
	
	Long billingModelId;
	BillingModel billingModel;
	RunModes mode;
	TaskProgressListener listener;	
	String sessionId;
	String username = "B-CEPHAL";
	HttpHeaders httpHeaders;
	Locale locale = Locale.ENGLISH;
	String repositoryColumns = "";
	String repositoryView; 
	boolean stopped;
		
	@Autowired
	BillingModelService billingModelService;
	
	@Autowired
	BillingRunnerUtils billingRunnerUtils;
	
	@Autowired
	InvoiceService invoiceService;
	
	@Autowired
	BillingFileUploaderService billlingFileUploaderService;
	
	@Autowired
	BillingRunOutcomeService billingRunOutcomeService;
	
	@Autowired
	IncrementalNumberRepository incrementalNumberRepository;
	
	@Autowired
	BillTemplateRepository billTemplateRepository;
	
	@Autowired
	BillTemplateService billTemplateService;
	
	@Autowired
	BillingCompanyRepositoryService billingCompanyRepositoryService;
	
	@Autowired
	ExportGrilleRunner exportGrilleRunner;
	
	@Autowired
	GrilleService grilleService;
	
	@PersistenceContext
	EntityManager entityManager;
	
	
	@Value("${bcephal.language}")
	String defaultLanguage;
	
	@Value("${bcephal.communication.message.code:MMHyySSS}")
	String communicationMessageCode;
	
	@Value("${bcephal.communication.message.code.length:10}")
	int communicationMessageCodeLength;
	
		
	@Transactional
	public void run() {	
		String dueDateCalculationView = null;
		try {			
			if(billingModelId == null) {
				throw new BcephalException("Billing model ID is NULL!");
			}
			billingModel = billingModelService.getById(billingModelId);
			if(billingModel == null) {
				throw new BcephalException("Billing model not found : " + billingModelId);
			}
			log.debug("Try to run billing model : {}", billingModel.getName());
			if(listener != null) {
				listener.createInfo(billingModel.getId(), billingModel.getName());
				listener.start(51);
			}
			
			if(stopped) {
				throw new BcephalException("Stoppped by user!");
			}
	        repositoryView = "BILLING_REPO_VIEWS_" + (this.billingModel.getId() != null ? this.billingModel.getId() : System.currentTimeMillis()); 
			Join join = billingRunnerUtils.prepareData(billingModel, repositoryView, locale);
			if(listener != null) {
				listener.nextStep(1);
			}
			
			if(stopped) {
				throw new BcephalException("Stoppped by user!");
			}
			repositoryColumns = billingRunnerUtils.readTableColumns(billingModel, repositoryView);
			if(listener != null) {
				listener.nextStep(1);
			}
			
			if(stopped) {
				throw new BcephalException("Stoppped by user!");
			}
			
			BillingContext context = billingRunnerUtils.buildContext(repositoryView, join, billingModel);
			if(listener != null) {
				listener.nextStep(1);
			}
			if(context.invoiceDueDateCalculationId != null) {
				dueDateCalculationView = billingRunnerUtils.prepareDataDueDateCalculationView(context, billingModel);
				if(listener != null) {
					listener.nextStep(1);
				}
			}
			
			if(stopped) {
				throw new BcephalException("Stoppped by user!");
			}
			List<String> clients = billingRunnerUtils.readClients(billingModel, repositoryView, context);
			if(listener != null) {
				listener.nextStep(1);
			}
			int clientCount = clients.size();
			log.debug("Client count : {}", clientCount);
			if(clientCount == 0) {
				throw new BcephalException("No billing event found for this billing model!");
			}
			if(listener != null) {
				listener.start(clientCount + 1);
			}
			
			BillingRunOutcome billingRunOutcome = new BillingRunOutcome();
			billingRunOutcome.setRunNumber(new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()));
			billingRunOutcome.setMode(mode);
			billingRunOutcome.setUsername(username);
			billingRunOutcome.setStatus(InvoiceStatus.DRAFT);
			
			Party billingCompany = billingCompanyRepositoryService.getCompany(billingModel.getBillingCompanyCode());
			
			for(String clientId : clients) {
				if(stopped) {
					throw new BcephalException("Stoppped by user!");
				}
				log.debug("******************************\nClient : {}", clientId);
				if(listener != null) {
					listener.createSubInfo(null, clientId);
					listener.startSubInfo(10);
				}
				
				List<Object[]> events = billingRunnerUtils.readEvents(clientId, billingModel, repositoryView, context);
				if(events.size() > 0) {
					if(stopped) {
						throw new BcephalException("Stoppped by user!");
					}
					log.trace("Read client....");
					Party client = Party.BuildClient(context, events.get(0));
					client.number = clientId;
					log.trace("Client reaed!");
					BillingRunnerForOneClient process = new BillingRunnerForOneClient();
					process.setBillingModel(billingModel);
					process.setContext(context);
					process.setClientId(clientId);
					process.setListener(listener);
					process.setClient(client);
					process.setBillingRunnerUtils(billingRunnerUtils);
					process.setRepositoryView(repositoryView);
					process.setBillTemplateRepository(billTemplateRepository);
					process.setSession(entityManager);
					process.setDueDateCalculationView(dueDateCalculationView);
					
					process.setBillingCompany(billingCompany);
					process.run(events);
					saveIncoices(process, billingRunOutcome);
				}				
								
				if(listener != null) {
					listener.nextStep(1);
				}
			}
			
//			JobParametersBuilder builder = new JobParametersBuilder();
//			builder.addDate("runDate", new Date());
//			JobExecution execution = jobLauncher.run(job(), builder.toJobParameters());			
//			log.debug("Job status : {}", execution.getStatus());
			billingRunnerUtils.RefreshBillingJoin(billingModel, locale);
			if(listener != null) {
				listener.nextStep(1);
			}
		}
		catch (Exception e) {
			log.error("", e);
			if(listener != null) {
				listener.error(e.getMessage(), false);
			}
		}	
		finally {
			billingRunnerUtils.disposeData(repositoryView,dueDateCalculationView);
			if(listener != null) {
				listener.end();;
			}
		}
	}
	
	public void stop() {
		stopped = true;
	}
	
		
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	private void saveIncoices(BillingRunnerForOneClient process, BillingRunOutcome billingRunOutcome) throws Exception {	
		IncrementalNumber invoiceSequence = getInvoiceNbrSequence(billingModel.getInvoiceSequenceId());
		IncrementalNumber validationSequence = getInvoiceNbrSequence(billingModel.getInvoiceValidationSequenceId());
		
        log.trace("Invoice count = ", process.getInvoices().keySet().size());
        for(String key : process.getInvoices().keySet()) {
        	Invoice invoice = process.getInvoices().get(key);	
        	invoice.setRunNumber(billingRunOutcome.getRunNumber());
        	invoice.buildBillingElements();
        	printInvoice(invoice, invoiceSequence, validationSequence);
        	if(listener != null) {
	        	listener.nextSubInfoStep(1);
	        }
        }
        
		try { 
			for(String key : process.getInvoices().keySet()) {
	        	List<Long> oids = process.getEvents().get(key);
	        	int nbr = oids.size();
	        	log.trace("{} = {} event(s)", key, nbr);
	        	Invoice invoice = process.getInvoices().get(key);
	        	saveInvoiceWithoutCommit(invoice, oids, invoiceSequence, validationSequence, process.context);
	        	if(invoice.isInvoice()) {
	        		billingRunOutcome.setInvoiceCount(billingRunOutcome.getInvoiceCount() + 1);
	        		billingRunOutcome.setInvoiceAmount(billingRunOutcome.getInvoiceAmount().add(invoice.getBillingTotalAmount()));
	    		}
	    		else { 
	    			billingRunOutcome.setCreditNoteCount(billingRunOutcome.getCreditNoteCount() + 1);
	    			billingRunOutcome.setCreditNoteAmount(billingRunOutcome.getCreditNoteAmount().add(invoice.getBillingTotalAmount()));
	    		}
	        	billingRunOutcome = billingRunOutcomeService.save(billingRunOutcome, locale);
	        	if(listener != null) {
		        	listener.nextSubInfoStep(1);
		        }
	        	exportReports(invoice);
	        }
		}
		catch (BcephalException e) {
			throw e;
		}
		catch (Exception ex) {
			log.error("Unable to save invoices.", ex);			
			String message = "Unable to save invoices.";
			throw new BcephalException(message);
		} 
	}
	
	
	private void exportReports(Invoice invoice) {
		if(billingModel.getAppendicyType() == BillingModelAppendicyType.GRID && billingModel.getAppendicyGridId() != null) {
			GrilleExportData data = new GrilleExportData();
			data.setType(GrilleExportDataType.CSV);
			GrilleDataFilter filter = new GrilleDataFilter();
			data.setFilter(filter);
			filter.setGrid(grilleService.getById(billingModel.getAppendicyGridId()));
		
			filter.setDataSourceType(filter.getGrid() != null ? filter.getGrid().getDataSourceType() : DataSourceType.UNIVERSE);
			filter.setDataSourceId(filter.getGrid() != null ? filter.getGrid().getDataSourceId() : null);
			
			List<VariableValue> values = new ArrayList<>();
			values.add(VariableValue.builder().name(BillingParameterCodes.VARIABLE_INVOICE_NBR).ignoreCase(true).stringValue(invoice.getReference()).build());
			values.add(VariableValue.builder().name(BillingParameterCodes.VARIABLE_CLIENT_ID).ignoreCase(true).stringValue(invoice.getClientNumber()).build());
			if(filter.getGrid().getAdminFilter() !=null) {
				filter.getGrid().getAdminFilter().setVariableValues(values);
			}
			if(filter.getGrid().getUserFilter() !=null) {
				filter.getGrid().getUserFilter().setVariableValues(values);
			}
						
			List<String> paths = exportGrilleRunner.export(data);
			try {
				String coma = "";
				invoice.setOtherFiles("");
				for(String path : paths) {
					String fileID = billlingFileUploaderService.uploadFileToFileManager(invoice, path, httpHeaders, locale);
					invoice.setOtherFiles(invoice.getOtherFiles() + coma + fileID);
					coma = Invoice.FILE_SEPARATOR;
					log.trace("Report file stored: {}", fileID);
				}
			}
			catch (Exception e) {
				throw new BcephalException("Unable to save report file. File manager service is not available!");
			}	
			
			for(String path : paths) {
				try {
					new File(path).delete();
				}
				catch (Exception e) {
					log.trace("Unable to delete temp file : {}", path, e);
				}
			}
		}		
	}

	private void printInvoice(Invoice invoice, IncrementalNumber sequence, IncrementalNumber validationSequence) throws Exception {
		
		if(!StringUtils.hasText(invoice.getReference())) {
			if(billingModel.isValidateGeneratedInvoices()) {
				invoice.setStatus(InvoiceStatus.VALIDATED);
				IncrementalNumber s = validationSequence != null ? validationSequence : sequence;
				if(s != null) {
					String reference = s.buildNextValue();
					invoice.setReference(reference);
					invoice.setValidationReference(reference);
				}
				else {
					String reference = new SimpleDateFormat("yyyy").format(invoice.getInvoiceDate()) + "/" + System.currentTimeMillis();
					invoice.setReference(reference);
					invoice.setValidationReference(reference);
				}
			}
			else if(sequence != null) {
				String reference = sequence.buildNextValue();
				invoice.setReference(reference);
				invoice.setDraftReference(reference);
			}
			else {
				String reference = new SimpleDateFormat("yyyy").format(invoice.getInvoiceDate()) + "/" + System.currentTimeMillis();
				invoice.setReference(reference);
				invoice.setDraftReference(reference);
			}
		}			
		
		invoice.buildVatItems();
		if(billingModel.isBuildCommunicationMessage()) {
			buildCommunicationMessage(invoice);
		}
		for(InvoiceItem item : invoice.getItemListChangeHandler().getItems()) {
			item.setBillingAmount(item.getAmount());
			item.setBillingQuantity(item.getQuantity());
			item.setBillingUnitCost(item.getUnitCost());
			item.setBillingVatRate(item.getVatRate());	
		}				
		invoice.setBillingAmountWithoutVat(invoice.getAmountWithoutVat());
		invoice.setBillingVatAmount(invoice.getVatAmount());
		
		String fileName = invoice.buildFileName();
		if(!StringUtils.hasText(fileName)) {
			fileName = invoice.getName();
		}
		invoice.setFileName(fileName);
		invoice.setName(fileName);
		
		invoice.buildDescription();
								
		String path = printBill(invoice);	
		log.trace("Invoice printed: {}", path);
		try {
			String fileID = billlingFileUploaderService.uploadFileToFileManager(invoice, path, httpHeaders, locale);
			invoice.setFile(fileID);
			log.trace("PDF stored: {}", fileID);
		}
		catch (Exception e) {
			log.error("", e);
			throw new BcephalException("Unable to save PDF. File manager service is not available!", e);
		}		
		if(invoice.getFile() != null) {
			try {
				new File(path).delete();
			}
			catch (Exception e) {
				log.debug("Unable to delete temp file : {}", path, e);
			}	
		}
	}
	
	@SuppressWarnings("unused")
	public String printBill(Invoice invoice) throws Exception {
		String code = invoice.getBillTemplateCode();		
		if(StringUtils.hasText(code)) {
//			code = BillingRunnerClientPerClient.DEFAULT_BILL_TEMPLATE_CODE;
		}
		InvoicePrinter printer = null;//this.printers.get(code);
		if(printer == null) {
			BillTemplate template = getBillTemplate(code);
        	printer = new InvoicePrinter(template, "");
			printer.setBillingModelLabels(template.getLabelListChangeHandler().getItems());
			printer.setDefaultLang(defaultLanguage);
//			this.printers.put(code, printer);
		}
		
		if(printer != null) {
			String path = printer.print(invoice);
			return path;
		}
		return null;
	}

	private BillTemplate getBillTemplate(String code) {
		BillTemplate template = billTemplateRepository.findByCode(code);
		return template;
	}
	
	
	private void buildCommunicationMessage(Invoice invoice) {
		log.trace("Building communication message for invoice : '{}'", invoice.getReference());
		String clientId = invoice.getClientNumber();
		if(!StringUtils.hasText(clientId)) {
			log.error("Unable to build communication message for invoice : '{}'. Client number is not defined!", invoice.getReference());
			return;
		}
		String number = clientId;
//		if(clientId.length() >= 4) {
//			number = clientId.substring(clientId.length()-4, clientId.length());
//		}
		
		number = new SimpleDateFormat(communicationMessageCode).format(new Date());
		if(number.startsWith("0")) {
			number = "1" + number.substring(1, number.length());
		}
		//number = number + new SimpleDateFormat("MMyyyy").format(invoice.getInvoiceDate());
		while(number.length() < communicationMessageCodeLength) {
			number = number + "0";
		}
		
		long check = modulo97(number);
		String checkString = check < 10 ? "0" + check : "" + check;
		String message = number.substring(0, 3) + "/"
				+ number.substring(3, 7) + "/"
				+ number.substring(7, number.length()) + checkString;		
		invoice.setCommunicationMessage(message);
		log.trace("Communication message for invoice : '{}' = {}", invoice.getReference(), message);
	}
	
	private long modulo97(String number) {
		try {
			Long d = Long.valueOf(number);
			return d % 97;
		}
		catch (Exception e) {
			log.error("Unable to build modulo 97 : {}", number, e);
		}
		return 0;
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private void saveInvoiceWithoutCommit(Invoice invoice, List<Long> ids, IncrementalNumber sequence, IncrementalNumber validationSequence, BillingContext context) throws Exception {		
		try {					
			invoice = invoiceService.save(invoice, locale);
			if(sequence != null) {
				incrementalNumberRepository.save(sequence);
			}
			if(validationSequence != null) {
				incrementalNumberRepository.save(validationSequence);
			}
			if(ids.size() > 0) {
				String sql = context.getSaveInvoiceSQL();	
				sql += " WHERE id IN (";
				String coma = "";
				for(Long id : ids) {
					sql += coma + id;	
					coma = ",";
				}
				sql += ")";				
				Query query = entityManager.createNativeQuery(sql);
				query.setParameter(1, context.billingEventStatusBilledValue);
				query.setParameter(2, invoice.getReference());
				query.setParameter(3, invoice.getRunNumber());
				query.executeUpdate();
								
				sql = context.getInsertInvoiceSQL(billingModel);
				int i = 1;
				query = entityManager.createNativeQuery(sql);
				query.setParameter(i++, invoice.getId());
				query.setParameter(i++, invoice.getReference());
				query.setParameter(i++, invoice.isCreditNote() ? UniverseSourceType.CREDIT_NOTE.toString() : UniverseSourceType.INVOICE.toString());
				query.setParameter(i++, true);
				query.setParameter(i++, invoice.getReference());
				query.setParameter(i++, new TypedParameterValue(StandardBasicTypes.STRING, invoice.getClientNumber()));
				query.setParameter(i++, invoice.getAmountWithoutVat());
				query.setParameter(i++, new TypedParameterValue(StandardBasicTypes.BIG_DECIMAL, invoice.getVatAmount()));
				query.setParameter(i++, invoice.getTotalAmount());
				//query.setParameter(i++,  new TypedParameterValue(StandardBasicTypes.DATE,invoice.getInvoiceDate()));
				query.setParameter(i++, new TypedParameterValue(StandardBasicTypes.DATE, invoice.getBillingDate()));
				query.setParameter(i++, new TypedParameterValue(StandardBasicTypes.DATE, invoice.getDueDate()));
				query.setParameter(i++, invoice.getStatus().toString());
				query.setParameter(i++, invoice.isCreditNote() ? context.creditNoteValue.toString() : context.invoiceValue.toString());
				if(context.invoiceCommunicationMessageId != null) {
					query.setParameter(i++, StringUtils.hasText(invoice.getCommunicationMessage()) ? invoice.getCommunicationMessage() : "");
				}
				if(context.invoiceDescriptionId != null) {
					query.setParameter(i++, StringUtils.hasText(invoice.getDescription()) ? invoice.getDescription() : "");
				}
				if(context.billingRunId != null) {
					query.setParameter(i++, StringUtils.hasText(invoice.getRunNumber()) ? invoice.getRunNumber() : "");
				}
				if(context.billingRunTypeId != null) {
					query.setParameter(i++, getMode() != null ? getMode().name() : "");
				}
				if(context.billingRunDateId != null) {
					query.setParameter(i++, new TypedParameterValue(StandardBasicTypes.DATE, invoice.getCreationDate()));
				}
				if(context.invoiceMailStatusId != null) {
					query.setParameter(i++, StringUtils.hasText(context.invoiceMailStatusNotSendValue) ? context.invoiceMailStatusNotSendValue : "");
				}
				
				
				for(BillingModelEnrichmentItem item : billingModel.getEnrichmentItems()) {
					String col = item.getUniverseTableColumnName();
					if(col != null) {
						if(item.getSourceType() == DimensionType.BILLING_EVENT) {
							col = item.column.getDbColAliasName();
							Object value = getRepoValue(col, ids, context, invoice.getClientNumber());
							query.setParameter(i++, value);
						}
						else{
							query.setParameter(i++, item.getValue());
						}
					}
				}
				
				if(context.clientNameId != null) {
					query.setParameter(i++, StringUtils.hasText(invoice.getClientName()) ? invoice.getClientName() : "");
				}
				if(context.clientDepartmentNumberId != null) {
					query.setParameter(i++, StringUtils.hasText(invoice.getDepartmentNumber()) ? invoice.getDepartmentNumber() : "");	
				}
				if(context.clientLanguageId != null) {
					query.setParameter(i++, StringUtils.hasText(invoice.getClientLanguage()) ? invoice.getClientLanguage() : "");	
				}					
				if(context.clientAdressStreetId != null) {
					query.setParameter(i++, StringUtils.hasText(invoice.getClientAdressStreet()) ? invoice.getClientAdressStreet() : "");	
				}		
				if(context.clientAdressPostalCodeId != null) {
					query.setParameter(i++, StringUtils.hasText(invoice.getClientAdressPostalCode()) ? invoice.getClientAdressPostalCode() : "");	
				}		
				if(context.clientAdressCityId != null) {
					query.setParameter(i++, StringUtils.hasText(invoice.getClientAdressCity()) ? invoice.getClientAdressCity() : "");	
				}		
				if(context.clientAdressCountryId != null) {
					query.setParameter(i++, StringUtils.hasText(invoice.getClientAdressCountry()) ? invoice.getClientAdressCountry() : "");
				}		
				if(context.clientVatNumberId != null) {
					query.setParameter(i++, StringUtils.hasText(invoice.getClientVatNumber()) ? invoice.getClientVatNumber() : "");
				}		
				if(context.clientLegalFormId != null) {
					query.setParameter(i++, StringUtils.hasText(invoice.getClientLegalForm()) ? invoice.getClientLegalForm() : "");
				}		
				if(context.clientPhoneId != null) {
					query.setParameter(i++, StringUtils.hasText(invoice.getClientPhone()) ? invoice.getClientPhone() : "");
				}		
				if(context.clientEmailId != null) {
					query.setParameter(i++, StringUtils.hasText(invoice.getClientEmail()) ? invoice.getClientEmail() : "");	
				}	
				if(context.clientEmailCcId != null) {
					query.setParameter(i++, StringUtils.hasText(invoice.getClientEmailCc()) ? invoice.getClientEmailCc() : "");	
				}	
				if(context.clientInternalNumberId != null) {
					query.setParameter(i++, StringUtils.hasText(invoice.getClientInternalNumber()) ? invoice.getClientInternalNumber() : "");	
				}		
				if(context.clientDoingBusinessAsId != null) {
					query.setParameter(i++, StringUtils.hasText(invoice.getClientDoingBusinessAs()) ? invoice.getClientDoingBusinessAs() : "");	
				}
				
				if(context.clientContactTitleId != null) {
					query.setParameter(i++, StringUtils.hasText(invoice.getContactTitle()) ? invoice.getContactTitle() : "");
				}
				if(context.clientContactFirstnameId != null) {
					query.setParameter(i++, StringUtils.hasText(invoice.getContactFirstname()) ? invoice.getContactFirstname() : "");
				}
				if(context.clientContactLastnameId != null) {
					query.setParameter(i++, StringUtils.hasText(invoice.getContactLastname()) ? invoice.getContactLastname() : "");
				}
				
				
				query.executeUpdate();								
			}
		} catch (Exception ex) {
			log.error("Unable to save invoice : {}.", invoice.getName(), ex);			
			String message = MessageFormat.format("Unable to save invoice : {0}.", invoice.getName());
			if(invoice.getFile() != null) {
				try {
					billlingFileUploaderService.deleteFile(invoice.getFile(), httpHeaders, locale);
					log.trace("PDF deleted: {}", invoice.getFile());
					invoice.setFile(null);				
				}
				catch (Exception e) {
					log.trace("", e);
				}	
			}
			if(ex instanceof BcephalException) {
				message += " " + ex.getMessage();					
			}
			throw new BcephalException(message);
		} 	
	}
	
	private Object getRepoValue(String col, List<Long> oids, BillingContext context, String clientNumber) {
		String part = " WHERE ID IN (";
		String coma = "";
		for(Long oid : oids) {
			part += coma + oid;	
			coma = ",";
		}
		part += ")";			
		
		String sql = "SELECT ".concat(col).concat(" FROM ").concat(repositoryView)
				.concat(part).concat(" ORDER BY ID");
		Query query = entityManager.createNativeQuery(sql);
		query.setFirstResult(0);
		query.setMaxResults(1);
		Object object = query.getSingleResult();
		return object;
	}
	
//	private Object getRepoValue(String col, List<Long> oids, BillingContext context, String clientNumber) {
//		String clientCol = new Attribute(context.clientIdId, "").getUniverseTableColumnName();
//		
//		String part = " AND oid IN (";
//		String coma = "";
//		for(Long oid : oids) {
//			part += coma + oid;	
//			coma = ",";
//		}
//		part += ")";			
//		
//		String sql = "SELECT ".concat(col).concat(" FROM ").concat(repositoryView)
//				.concat(" WHERE " + clientCol + " = ?").concat(part).concat(" ORDER BY OID");
//		Query query = entityManager.createNativeQuery(sql);
//		query.setParameter(1, clientNumber);
//		query.setFirstResult(0);
//		query.setMaxResults(1);
//		Object object = query.getSingleResult();
//		return object;
//	}
	
	protected IncrementalNumber getInvoiceNbrSequence(Long sequenceId) {
		IncrementalNumber invoiceSequence = null;
		if(sequenceId != null) {
			Optional<IncrementalNumber> result = incrementalNumberRepository.findById(sequenceId);
			invoiceSequence = result.isPresent() ? result.get() : null;
		}
		return invoiceSequence;
	}
	
	
}
