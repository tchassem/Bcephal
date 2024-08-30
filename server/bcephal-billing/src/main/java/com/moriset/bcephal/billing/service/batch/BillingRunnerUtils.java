/**
 * 
 */
package com.moriset.bcephal.billing.service.batch;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.moriset.bcephal.billing.domain.BillingModel;
import com.moriset.bcephal.billing.domain.BillingModelEnrichmentItem;
import com.moriset.bcephal.billing.domain.BillingModelGroupingItem;
import com.moriset.bcephal.billing.domain.BillingModelParameter;
import com.moriset.bcephal.billing.domain.BillingModelPivot;
import com.moriset.bcephal.billing.domain.BillingModelTemplate;
import com.moriset.bcephal.billing.service.BillingEventRepositoryService;
import com.moriset.bcephal.billing.service.BillingJoinService;
import com.moriset.bcephal.domain.BillingParameterCodes;
import com.moriset.bcephal.domain.dimension.Attribute;
import com.moriset.bcephal.domain.dimension.DimensionType;
import com.moriset.bcephal.domain.dimension.Measure;
import com.moriset.bcephal.domain.parameter.Parameter;
import com.moriset.bcephal.domain.parameter.ParameterType;
import com.moriset.bcephal.domain.universe.UniverseParameters;
import com.moriset.bcephal.domain.universe.UniverseSourceType;
import com.moriset.bcephal.grid.domain.Grille;
import com.moriset.bcephal.grid.domain.GrilleDataFilter;
import com.moriset.bcephal.grid.domain.Join;
import com.moriset.bcephal.grid.domain.JoinColumn;
import com.moriset.bcephal.grid.domain.JoinGrid;
import com.moriset.bcephal.grid.repository.JoinColumnRepository;
import com.moriset.bcephal.grid.service.GrilleService;
import com.moriset.bcephal.grid.service.InputGridQueryBuilder;
import com.moriset.bcephal.grid.service.JoinFilter;
import com.moriset.bcephal.grid.service.JoinQueryBuilder;
import com.moriset.bcephal.grid.service.SpotService;
import com.moriset.bcephal.repository.ParameterRepository;
import com.moriset.bcephal.service.UniverseGenerator;
import com.moriset.bcephal.utils.BcephalException;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Moriset
 *
 */
@Service
@Data
@Slf4j
public class BillingRunnerUtils {
	
	@Autowired
	BillingJoinService billingJoinService;
	
	@Autowired
	BillingEventRepositoryService billingEventRepositoryService;
	
	
	@Autowired
	GrilleService gridService;	
	
	@Autowired
	JoinColumnRepository joinColumnRepository;
	
	@Autowired
	ParameterRepository parameterRepository;
	
	@Autowired
	SpotService spotService;
	
	@PersistenceContext
	EntityManager entityManager;
	
	@Autowired
	UniverseGenerator universeGenerator;

	
	@Transactional
	public Join prepareData(BillingModel billingModel, String repositoryView, Locale locale) throws Exception {
		log.debug("Try to run billing model : {}. Prepare data....", billingModel.getName());
		Join join = RefreshBillingJoin(billingModel, locale);	        	
//		        if(context.invoiceDueDateCalculationOid != null) {
//		        	dueDateCalculationView = "BILLING_DUE_DATE_VIEWS_" + (this.billingModel.getOid() != null ? this.billingModel.getOid() : System.currentTimeMillis());
//		        }
			
		List<BillingModelPivot> pivots = billingModel.getPivotListChangeHandler().getItems();
		Collections.sort(pivots, new Comparator<BillingModelPivot>() {
			@Override
			public int compare(BillingModelPivot o1, BillingModelPivot o2) {
				return o1.getPosition() - o2.getPosition();
			}
		});
		billingModel.setPivots(pivots);
		
		List<BillingModelGroupingItem> groups = billingModel.getGroupingItemListChangeHandler().getItems();
		Collections.sort(groups, new Comparator<BillingModelGroupingItem>() {
			@Override
			public int compare(BillingModelGroupingItem o1, BillingModelGroupingItem o2) {
				return o1.getPosition() - o2.getPosition();
			}
		});
		billingModel.setGroupingItems(groups);		
		
		List<BillingModelParameter> parameters = billingModel.getParameterListChangeHandler().getItems();
		Collections.sort(parameters, new Comparator<BillingModelParameter>() {
			@Override
			public int compare(BillingModelParameter o1, BillingModelParameter o2) {
				return o1.getPosition() - o2.getPosition();
			}
		});
		billingModel.setParameters(parameters);
		
		List<BillingModelEnrichmentItem> enrichmentItems = billingModel.getEnrichmentItemListChangeHandler().getItems();
		Collections.sort(enrichmentItems, new Comparator<BillingModelEnrichmentItem>() {
			@Override
			public int compare(BillingModelEnrichmentItem o1, BillingModelEnrichmentItem o2) {
				return o1.getPosition() - o2.getPosition();
			}
		});
		billingModel.setEnrichmentItems(enrichmentItems);
		
		List<BillingModelTemplate> templates = billingModel.getBillingModelTemplatesListChangeHandler().getItems();
		Collections.sort(templates, new Comparator<BillingModelTemplate>() {
			@Override
			public int compare(BillingModelTemplate o1, BillingModelTemplate o2) {
				return o1.getPosition() - o2.getPosition();
			}
		});
		billingModel.setBillingModelTemplates(templates);
		
		if(enrichmentItems.size() > 0) {
			Grille beRepo = billingEventRepositoryService.getBillingEventRepository();
			JoinGrid beRepoJoinGrid = null;
			if(beRepo != null) {
				beRepoJoinGrid = join.getGriByGridId(beRepo.getId());
			}
			
			for(BillingModelEnrichmentItem item : enrichmentItems) {
				if(item.getSourceType() == DimensionType.BILLING_EVENT){
					if(beRepo != null && beRepoJoinGrid != null) {
						item.column = join.getColumn(beRepoJoinGrid.getGridType(), beRepo.getId(), item.getSourceId());
						//item.column = beRepo.getColumnById(item.getSourceId());
					}
					else {
						throw new BcephalException("Billing event repository is not found!");
					}
				}				
			}
		}
		
		
		
		try {
			JoinFilter filter = new JoinFilter();
			filter.setJoin(join);
			filter.setFilter(billingModel.getFilter());
			
//			boolean addInvoiceFilter = false;
//			boolean addCreditNoteFilter = false;
//			for(BillingModelItem item : billingModel.getItemListChangeHandler().getItems()) {
//				if(item.getItemType() == BillingModelItemType.EVENT_TYPE && StringUtils.hasText(item.getName())) {					
//					addInvoiceFilter = addInvoiceFilter || item.getName().equalsIgnoreCase("INVOICE");
//					addCreditNoteFilter = addCreditNoteFilter || item.getName().equalsIgnoreCase("CREDIT NOTE");
//				}
//			}
//			if((addInvoiceFilter && !addCreditNoteFilter)|| (!addInvoiceFilter && addCreditNoteFilter)) {
//				Long billingEventTypeId = getParameterLongValue(BillingParameterCodes.BILLING_EVENT_TYPE_ATTRIBUTE, ParameterType.ATTRIBUTE);
//				String invoiceValue = getParameterStringValue(BillingParameterCodes.BILLING_EVENT_TYPE_INVOICE_VALUE, ParameterType.ATTRIBUTE_VALUE);
//				String creditNoteValue = getParameterStringValue(BillingParameterCodes.BILLING_EVENT_TYPE_CREDIT_NOTE_VALUE, ParameterType.ATTRIBUTE_VALUE);
//				String value = addInvoiceFilter ? invoiceValue : creditNoteValue;
//				if(billingEventTypeId != null && StringUtils.hasText(value)) {
//					AttributeFilterItem filterItem = new AttributeFilterItem();
//					filterItem.setDataSourceId(null);
//					filterItem.setDataSourceType(DataSourceType.UNIVERSE);
//					filterItem.setDimensionId(billingEventTypeId);
//					filterItem.setDimensionType(DimensionType.ATTRIBUTE);
//					filterItem.setOperator(AttributeOperator.EQUALS);
//					filterItem.setFilterVerb(FilterVerb.AND);
//					filterItem.setValue(value);
//					
//					filter.getJoin().setGridUserFilter(new UniverseFilter());
//					filter.getJoin().getGridUserFilter().setAttributeFilter(new AttributeFilter());				
//					filter.getJoin().getGridUserFilter().getAttributeFilter().addItem(filterItem);
//				}				
//			}
			
			billingJoinService.loadFilterClosures(filter);	
			JoinQueryBuilder builder = new JoinQueryBuilder(filter,joinColumnRepository, spotService);
			builder.setAddMainGridOid(true);
			
			log.debug("Billing model : {} - Try drop table : {} ", billingModel.getName(), repositoryView);
			String dropSql = "DROP TABLE IF EXISTS " + repositoryView;	
			log.trace("Drop query : {}", dropSql);			
			Query query = entityManager.createNativeQuery(dropSql);
			query.executeUpdate();
			log.debug("Billing model : {} - table dropped! {}", billingModel.getName(), repositoryView);
			
			log.debug("Billing model : {} - Try create table : {} ", billingModel.getName(), repositoryView);
			String primarySql = builder.buildQuery();			
//			if(billingModel.getFilter() != null) {
//				String filterSql = new ReportGridQueryBuilder(null).buildUniverseFilterWherePart(billingModel.getFilter());
//				log.trace("Filter sql : {}", filterSql);	
//				if (StringUtils.hasText(filterSql)) {					
//					primarySql = " SELECT * FROM (" + primarySql + ") AS B WHERE " + filterSql;
//				}
//			}			
			log.trace("Primary query : {}", primarySql);	
			String createSql = "CREATE TABLE ".concat(repositoryView).concat(" AS ").concat(primarySql);
			log.trace("Create query : {}", createSql);	
			query = entityManager.createNativeQuery(createSql);
			for (String key : builder.getParameters().keySet()) {
				query.setParameter(key, builder.getParameters().get(key));
			}
			query.executeUpdate();
			log.debug("Billing model : {} - Table created : {}", billingModel.getName(), repositoryView);
			
//			if(!StringUtils.isBlank(dueDateCalculationView)){
//				log.debug("Billing model : {} - Try drop view : {} ", billingModel.getName(), dueDateCalculationView);
//				dropSql = "DROP TABLE IF EXISTS " + dueDateCalculationView;	
//				query = entityManager.createNativeQuery(dropSql);
//				query.executeUpdate();
//				log.debug("Billing model : {} - View dropped! {}", billingModel.getName(), dueDateCalculationView);
//				
//				log.debug("Billing model : {} - Try create view : {} ", billingModel.getName(), dueDateCalculationView);
//				log.debug("Billing model : {} - Try build view query : {} ", billingModel.getName(), dueDateCalculationView);
//				
//				String clientNumberCol = new Attribute(context.clientNumberOid, "").getUniverseTableColName();
//				String calculationCol = new Measure(context.invoiceDueDateCalculationOid, "").getUniverseTableColName();
//				primarySql = MessageFormat.format("SELECT {0} AS client, {1} AS measure FROM {2} WHERE {0} IS NOT NULL AND {1} IS NOT NULL AND {3} = true AND {4} = {5}", 
//						clientNumberCol, calculationCol, UniverseTableParameters.UNIVERSE_TABLE_NAME, 
//						UniverseTableParameters.ISREADY, UniverseTableParameters.SOURCE_TYPE, "'" + UniverseSourceType.INPUT_GRID.toString() + "'");
//				
//				log.debug("Billing model : {} - View query builded! {}", billingModel.getName(), dueDateCalculationView);
//				log.debug("QSL = {}", primarySql);
//				createSql = "CREATE TABLE ".concat(dueDateCalculationView).concat(" AS ").concat(primarySql);
//				query = entityManager.createNativeQuery(createSql);
//				query.executeUpdate();
//				log.debug("Billing model : {} - View created! {}", billingModel.getName(), dueDateCalculationView);
//			}
			
			
			
//			JoinColumn column = join.getColumn(context.clientNumberOid, ParameterType.SCOPE);
//			String col = column != null ? column.getDbColAliasName() : null;//new Attribute(context.clientNumberOid, null).getUniverseTableColName();
//			
//			String index = "BILLING_REPO_VIEWS_CLIENT_INDEX" + (this.billingModel.getId() != null ? this.billingModel.getId() : System.currentTimeMillis());
//			String indexSql = "CREATE INDEX IF NOT EXISTS ".concat(index).concat(" ON ").concat(repositoryView).concat(" (").concat(col).concat(")");
//			query = entityManager.createNativeQuery(indexSql);
//			query.executeUpdate(); 
									
			
						
//			Object[] billingCompanyData = getBillingCompany(billingCompanyFilter);
//			billingCompany = Party.BuildBillingCompany(context, billingCompanyData);
			return join;
		}
		catch (Exception e) {
			log.error("Billing model : {} - ERROR : ", billingModel.getName(), e);
			if(e instanceof BcephalException) {
				throw e;
			}
			throw new BcephalException("Unable to preapare data for billing : \n" + e.getMessage(), e);
		}
		finally {
			
		}
	}
	
	public String prepareDataDueDateCalculationView(BillingContext context, BillingModel billingModel) {
		String dueDateCalculationView = null;
		 if(context.invoiceDueDateCalculationId != null) {
	        	dueDateCalculationView = "BILLING_DUE_DATE_VIEWS_" + (billingModel.getId() != null ? billingModel.getId() : System.currentTimeMillis());
	     }
		 if(StringUtils.hasText(dueDateCalculationView)){
				log.debug("Billing model : {} - Try drop view : {} ", billingModel.getName(), dueDateCalculationView);
				String dropSql = "DROP TABLE IF EXISTS " + dueDateCalculationView;	
				Query query = entityManager.createNativeQuery(dropSql);
				query.executeUpdate();
				log.debug("Billing model : {} - View dropped! {}", billingModel.getName(), dueDateCalculationView);
				
				log.debug("Billing model : {} - Try create view : {} ", billingModel.getName(), dueDateCalculationView);
				log.debug("Billing model : {} - Try build view query : {} ", billingModel.getName(), dueDateCalculationView);
				
				String clientNumberCol = new Attribute(context.clientIdId, "").getUniverseTableColumnName();
				String calculationCol = new Measure(context.invoiceDueDateCalculationId, "").getUniverseTableColumnName();
				String primarySql = MessageFormat.format("SELECT {0} AS client, {1} AS measure FROM {2} WHERE {0} IS NOT NULL AND {1} IS NOT NULL AND {3} = true AND {4} = {5}", 
						clientNumberCol, calculationCol, UniverseParameters.UNIVERSE_TABLE_NAME, 
						UniverseParameters.ISREADY, UniverseParameters.SOURCE_TYPE, "'" + UniverseSourceType.INPUT_GRID.toString() + "'");
				
				log.debug("Billing model : {} - View query builded! {}", billingModel.getName(), dueDateCalculationView);
				log.debug("QSL = {}", primarySql);
				String createSql = "CREATE TABLE ".concat(dueDateCalculationView).concat(" AS ").concat(primarySql);
				query = entityManager.createNativeQuery(createSql);
				query.executeUpdate();
				log.debug("Billing model : {} - View created! {}", billingModel.getName(), dueDateCalculationView);
			}
		 return dueDateCalculationView;
			
	}
	
	public String readTableColumns(BillingModel billingModel, String repositoryView) throws Exception {
		try {
			log.debug("Billing model : {} - Try read table columns...", billingModel.getName());		
			String repositoryColumns = "";
			String coma = "";
			String scheme = "MISP.";
			for(String column : universeGenerator.getTableColumns(scheme, repositoryView)) {
				repositoryColumns += coma + column;
				coma = ",";
			}				
			log.trace("Billing model : {} - table Columns : {}", billingModel.getName(), repositoryColumns);
			return repositoryColumns;			
		}
		catch (Exception e) {
			log.error("Billing model : {} - ERROR : ", billingModel.getName(), e);
			if(e instanceof BcephalException) {
				throw e;
			}
			throw new BcephalException("Unable to read table columns : \n" + e.getMessage(), e);
		}
	}
		
	public List<String> readClients(BillingModel billingModel, String repositoryView, BillingContext context) throws Exception {
		try {
			log.debug("Billing model : {} - Try read clients...", billingModel.getName());	
			String col = context.clientIdColumnName;
			String sql = "SELECT DISTINCT " + col + " FROM ".concat(repositoryView).concat(" WHERE " + col + " IS NOT NULL");
			Query query = entityManager.createNativeQuery(sql);
			@SuppressWarnings("unchecked")
			List<String> clients = query.getResultList();			
			log.debug("Billing model : {} - Clients readed : {} ", billingModel.getName(), clients.size());
			log.debug("Try to run billing model : {}. Data prepared!", billingModel.getName());
			return clients;			
		}
		catch (Exception e) {
			log.error("Billing model : {} - ERROR : ", billingModel.getName(), e);
			if(e instanceof BcephalException) {
				throw e;
			}
			throw new BcephalException("Unable to read clients : \n" + e.getMessage(), e);
		}
	}
	
	public List<Object[]> readEvents(String clientId, BillingModel billingModel, String repositoryView, BillingContext context) throws Exception {
		try {
			log.debug("Billing model : {} - Try read events for client : {}", billingModel.getName(), clientId);	
			String col = context.clientIdColumnName;
			String sql = "SELECT * FROM ".concat(repositoryView).concat(" WHERE " + col + " = :client");
			Query query = entityManager.createNativeQuery(sql);
			query.setParameter("client", clientId);
			@SuppressWarnings("unchecked")
			List<Object[]> events = query.getResultList();			
			log.debug("Billing model : {} - Events read : {} ", billingModel.getName(), events.size());
			return events;			
		}
		catch (Exception e) {
			log.error("Billing model : {} - ERROR : ", billingModel.getName(), e);
			if(e instanceof BcephalException) {
				throw e;
			}
			throw new BcephalException("Unable to read events for client : " + clientId, e);
		}
	}
	

	public boolean templateMatchs(BillingModelTemplate template, Long eventOid, String repositoryView) {
		try {			
			String sql = "SELECT count(1) FROM ".concat(repositoryView).concat(" WHERE ID = :id");
			if(template.getFilter() != null) {	
				String whereSql = new InputGridQueryBuilder(new GrilleDataFilter()).buildUniverseFilterWherePart(template.getFilter());
				if(StringUtils.hasText(whereSql)) {
					sql += " AND " + whereSql;
				}
			}
			Query query = entityManager.createNativeQuery(sql);
			query.setParameter("id", eventOid);
			Number count = (Number)query.getSingleResult();	
			return count != null && count.intValue() > 0;			
		}
		catch (Exception e) {
			log.error("Billing template matching - ERROR : ", e);
		}
		return false;
	}
	
		
	@Transactional
	public void disposeData(String repositoryView, String dueDateCalculationView) {				
		try{
			if(StringUtils.hasText(repositoryView)) {
				String dropSql = "DROP TABLE IF EXISTS " + repositoryView;	
				Query query = entityManager.createNativeQuery(dropSql);			
				query.executeUpdate();
			}		
			
			if(StringUtils.hasText(dueDateCalculationView)) {
				String dropSql = "DROP TABLE IF EXISTS " + dueDateCalculationView;	
				Query query = entityManager.createNativeQuery(dropSql);			
				query.executeUpdate();
			}				
		}
		catch (Exception e) {
			log.debug("Unable to drop table.", e);
		}
	}
	
	public Join RefreshBillingJoin(BillingModel billingModel, Locale locale) throws Exception {	
		log.debug("Billing model : {} - Try to read billing join ", billingModel.getName());
		Join join = billingJoinService.getBillingJoin();
		if(join == null) {
			throw new BcephalException("Billing join is not found!");
		}
		log.debug("Billing model : {} - Billing join read!", billingModel.getName());
		
		//if(billingModel.isRefreshRepositories()) {
			log.debug("Billing model : {} - Try refresh billing join ", billingModel.getName());
			List<Long> ids = new ArrayList<Long>();
			for(JoinGrid joinGrid : join.getGridListChangeHandler().getItems()) {
				ids.add(joinGrid.getGridId());
			}
			gridService.refreshPublication(ids, locale);
			log.debug("Billing model : {} - Billing join refreshed ", billingModel.getName());			
		//}
		return join;
	}

	public BillingContext buildContext(String repositoryView, Join join, BillingModel billingModel) {
		BillingContext context = new BillingContext();
		context.billingJoin = join;
		context.clientIdId = getParameterLongValue(BillingParameterCodes.BILLING_ROLE_CLIENT_ID_ATTRIBUTE, ParameterType.ATTRIBUTE);
		if(context.clientIdId == null) {
			throw new BcephalException("Client Id not found!");
		}
		JoinColumn column = context.billingJoin.getColumn(context.clientIdId, DimensionType.ATTRIBUTE);
		context.clientIdPosition = column != null ? column.getPosition() : null;
		context.clientIdColumnName = column != null ? column.getDbColAliasName() : null;
		
		context.clientNameId = getParameterLongValue(BillingParameterCodes.BILLING_ROLE_CLIENT_NAME_ATTRIBUTE, ParameterType.ATTRIBUTE);
		column = context.billingJoin.getColumn(context.clientNameId, DimensionType.ATTRIBUTE);
		context.clientNamePosition = column != null ? column.getPosition() : null;
		
		context.clientDepartmentNumberId = getParameterLongValue(BillingParameterCodes.BILLING_ROLE_CLIENT_DEPARTMENT_NUMBER_ATTRIBUTE, ParameterType.ATTRIBUTE);
		column = context.billingJoin.getColumn(context.clientDepartmentNumberId, DimensionType.ATTRIBUTE);
		context.clientDepartmentNumberPosition = column != null ? column.getPosition() : null;
		
		context.clientDepartmentNameId = getParameterLongValue(BillingParameterCodes.BILLING_ROLE_CLIENT_DEPARTMENT_NAME_ATTRIBUTE, ParameterType.ATTRIBUTE);
		column = context.billingJoin.getColumn(context.clientDepartmentNameId, DimensionType.ATTRIBUTE);
		context.clientDepartmentNamePosition = column != null ? column.getPosition() : null;
		
		context.clientLanguageId = getParameterLongValue(BillingParameterCodes.BILLING_ROLE_DEFAULT_LANGUAGE_ATTRIBUTE, ParameterType.ATTRIBUTE);
		column = context.billingJoin.getColumn(context.clientLanguageId, DimensionType.ATTRIBUTE);
		context.clientLanguagePosition = column != null ? column.getPosition() : null;
		
		
		context.clientAdressStreetId = getParameterLongValue(BillingParameterCodes.BILLING_ROLE_ADRESS_STREET_ATTRIBUTE, ParameterType.ATTRIBUTE);
		column = context.billingJoin.getColumn(context.clientAdressStreetId, DimensionType.ATTRIBUTE);
		context.clientAdressStreetPosition = column != null ? column.getPosition() : null;
		
		context.clientAdressPostalCodeId = getParameterLongValue(BillingParameterCodes.BILLING_ROLE_ADRESS_POSTAL_CODE_ATTRIBUTE, ParameterType.ATTRIBUTE);
		column = context.billingJoin.getColumn(context.clientAdressPostalCodeId, DimensionType.ATTRIBUTE);
		context.clientAdressPostalCodePosition = column != null ? column.getPosition() : null;
		
		context.clientAdressCityId = getParameterLongValue(BillingParameterCodes.BILLING_ROLE_ADRESS_CITY_ATTRIBUTE, ParameterType.ATTRIBUTE);
		column = context.billingJoin.getColumn(context.clientAdressCityId, DimensionType.ATTRIBUTE);
		context.clientAdressCityPosition = column != null ? column.getPosition() : null;
		
		context.clientAdressCountryId = getParameterLongValue(BillingParameterCodes.BILLING_ROLE_ADRESS_COUNTRY_ATTRIBUTE, ParameterType.ATTRIBUTE);
		column = context.billingJoin.getColumn(context.clientAdressCountryId, DimensionType.ATTRIBUTE);
		context.clientAdressCountryPosition = column != null ? column.getPosition() : null;
		
		context.clientVatNumberId = getParameterLongValue(BillingParameterCodes.BILLING_ROLE_VAT_NUMBER_ATTRIBUTE, ParameterType.ATTRIBUTE);
		column = context.billingJoin.getColumn(context.clientVatNumberId, DimensionType.ATTRIBUTE);
		context.clientVatNumberPosition = column != null ? column.getPosition() : null;
		
		context.clientLegalFormId = getParameterLongValue(BillingParameterCodes.BILLING_ROLE_LEGAL_FORM_ATTRIBUTE, ParameterType.ATTRIBUTE);
		column = context.billingJoin.getColumn(context.clientLegalFormId, DimensionType.ATTRIBUTE);
		context.clientLegalFormPosition = column != null ? column.getPosition() : null;
		
		context.clientPhoneId = getParameterLongValue(BillingParameterCodes.BILLING_ROLE_PHONE_ATTRIBUTE, ParameterType.ATTRIBUTE);
		column = context.billingJoin.getColumn(context.clientPhoneId, DimensionType.ATTRIBUTE);
		context.clientPhonePosition = column != null ? column.getPosition() : null;
		
		context.clientEmailId = getParameterLongValue(BillingParameterCodes.BILLING_ROLE_EMAIL_ATTRIBUTE, ParameterType.ATTRIBUTE);
		column = context.billingJoin.getColumn(context.clientEmailId, DimensionType.ATTRIBUTE);
		context.clientEmailPosition = column != null ? column.getPosition() : null;
		
		context.clientEmailCcId = getParameterLongValue(BillingParameterCodes.BILLING_ROLE_EMAIL_CC_ATTRIBUTE, ParameterType.ATTRIBUTE);
		column = context.billingJoin.getColumn(context.clientEmailCcId, DimensionType.ATTRIBUTE);
		context.clientEmailCcPosition = column != null ? column.getPosition() : null;
		
		context.clientStatusId = getParameterLongValue(BillingParameterCodes.BILLING_ROLE_STATUS_ATTRIBUTE, ParameterType.ATTRIBUTE);
		column = context.billingJoin.getColumn(context.clientStatusId, DimensionType.ATTRIBUTE);
		context.clientStatusPosition = column != null ? column.getPosition() : null;
		context.clientStatusACTIVEValue = getParameterStringValue(BillingParameterCodes.BILLING_ROLE_STATUS_ACTIVE_VALUE, ParameterType.ATTRIBUTE_VALUE);
		
		context.clientInternalNumberId = getParameterLongValue(BillingParameterCodes.BILLING_ROLE_CLIENT_INTERNAL_ID_ATTRIBUTE, ParameterType.ATTRIBUTE);
		column = context.billingJoin.getColumn(context.clientInternalNumberId, DimensionType.ATTRIBUTE);
		context.clientInternalNumberPosition = column != null ? column.getPosition() : null;
		
		context.clientDoingBusinessAsId = getParameterLongValue(BillingParameterCodes.BILLING_ROLE_CLIENT_DOING_BUSINESS_AS_ATTRIBUTE, ParameterType.ATTRIBUTE);
		column = context.billingJoin.getColumn(context.clientDoingBusinessAsId, DimensionType.ATTRIBUTE);
		context.clientDoingBusinessAsPosition = column != null ? column.getPosition() : null;
		
		context.clientDepartmentInternalNumberId = getParameterLongValue(BillingParameterCodes.BILLING_ROLE_CLIENT_DEPARTMENT_INTERNAL_NUMBER_ATTRIBUTE, ParameterType.ATTRIBUTE);
		column = context.billingJoin.getColumn(context.clientDepartmentInternalNumberId, DimensionType.ATTRIBUTE);
		context.clientDepartmentInternalNumberPosition = column != null ? column.getPosition() : null;
		
		context.clientContactLastnameId = getParameterLongValue(BillingParameterCodes.BILLING_ROLE_CLIENT_CONTACT_LASTNAME_ATTRIBUTE, ParameterType.ATTRIBUTE);
		column = context.billingJoin.getColumn(context.clientContactLastnameId, DimensionType.ATTRIBUTE);
		context.clientContactLastNamePosition = column != null ? column.getPosition() : null;
		
		context.clientContactFirstnameId = getParameterLongValue(BillingParameterCodes.BILLING_ROLE_CLIENT_CONTACT_FIRSTNAME_ATTRIBUTE, ParameterType.ATTRIBUTE);
		column = context.billingJoin.getColumn(context.clientContactFirstnameId, DimensionType.ATTRIBUTE);
		context.clientContactFirstnamePosition = column != null ? column.getPosition() : null;
		
		context.clientContactTitleId = getParameterLongValue(BillingParameterCodes.BILLING_ROLE_CLIENT_CONTACT_TITLE_ATTRIBUTE, ParameterType.ATTRIBUTE);
		column = context.billingJoin.getColumn(context.clientContactTitleId, DimensionType.ATTRIBUTE);
		context.clientContactTitlePosition = column != null ? column.getPosition() : null;
				
		
		context.billingEventTypeId = getParameterLongValue(BillingParameterCodes.BILLING_EVENT_TYPE_ATTRIBUTE, ParameterType.ATTRIBUTE);
		column = context.billingJoin.getColumn(context.billingEventTypeId, DimensionType.ATTRIBUTE);
		context.billingEventTypePosition = column != null ? column.getPosition() : null;		
		context.billingEventTypeInvoiceValue = getParameterStringValue(BillingParameterCodes.BILLING_EVENT_TYPE_INVOICE_VALUE, ParameterType.ATTRIBUTE_VALUE);
		context.billingEventTypeCreditNoteValue = getParameterStringValue(BillingParameterCodes.BILLING_EVENT_TYPE_CREDIT_NOTE_VALUE, ParameterType.ATTRIBUTE_VALUE);
		
		context.billingEventStatusId = getParameterLongValue(BillingParameterCodes.BILLING_EVENT_STATUS_ATTRIBUTE, ParameterType.ATTRIBUTE);		
		context.billingEventStatusBilledValue = getParameterStringValue(BillingParameterCodes.BILLING_EVENT_STATUS_BILLED_VALUE, ParameterType.ATTRIBUTE_VALUE);
		context.billingEventStatusDraftValue = getParameterStringValue(BillingParameterCodes.BILLING_EVENT_STATUS_DRAFT_VALUE, ParameterType.ATTRIBUTE_VALUE);
				
		context.billingEventDateId = getParameterLongValue(BillingParameterCodes.BILLING_EVENT_DATE_PERIOD, ParameterType.PERIOD);
		column = context.billingJoin.getColumn(context.billingEventDateId, DimensionType.PERIOD);
		context.billingEventDatePosition = column != null ? column.getPosition() : null;
		
		context.billingAmountId = getParameterLongValue(BillingParameterCodes.BILLING_EVENT_BILLING_AMOUNT_MEASURE, ParameterType.MEASURE);
		column = context.billingJoin.getColumn(context.billingAmountId, DimensionType.MEASURE);
		context.billingAmountPosition = column != null ? column.getPosition() : null;
		
		context.unitCostId = getParameterLongValue(BillingParameterCodes.BILLING_EVENT_UNIT_COST_MEASURE, ParameterType.MEASURE);
		column = context.billingJoin.getColumn(context.unitCostId, DimensionType.MEASURE);
		context.unitCostPosition = column != null ? column.getPosition() : null;
		
		context.vatRateId = getParameterLongValue(BillingParameterCodes.BILLING_EVENT_VAT_RATE_MEASURE, ParameterType.MEASURE);
		column = context.billingJoin.getColumn(context.vatRateId, DimensionType.MEASURE);
		context.vatRatePosition = column != null ? column.getPosition() : null;
		
		context.descriptionId = getParameterLongValue(BillingParameterCodes.BILLING_EVENT_DESCRIPTION_ATTRIBUTE, ParameterType.ATTRIBUTE);
		column = context.billingJoin.getColumn(context.descriptionId, DimensionType.ATTRIBUTE);
		context.descriptionPosition = column != null ? column.getPosition() : null;		
		
		context.driverId = getParameterLongValue(BillingParameterCodes.BILLING_EVENT_BILLING_DRIVER_MEASURE, ParameterType.MEASURE);
		column = context.billingJoin.getColumn(context.driverId, DimensionType.MEASURE);
		context.driverPosition = column != null ? column.getPosition() : null;	
		
		context.driverNameId = getParameterLongValue(BillingParameterCodes.BILLING_EVENT_DRIVER_NAME_ATTRIBUTE, ParameterType.ATTRIBUTE);
		column = context.billingJoin.getColumn(context.driverNameId, DimensionType.ATTRIBUTE);
		context.driverNamePosition = column != null ? column.getPosition() : null;	
				
		
		context.billingRunId = getParameterLongValue(BillingParameterCodes.BILLING_RUN_ATTRIBUTE, ParameterType.ATTRIBUTE);
        context.billingRunTypeId = getParameterLongValue(BillingParameterCodes.BILLING_RUN_TYPE_ATTRIBUTE, ParameterType.ATTRIBUTE);
        context.billingRunDateId = getParameterLongValue(BillingParameterCodes.BILLING_RUN_DATE_PERIOD, ParameterType.PERIOD);
        
        context.invoiceRefId = getParameterLongValue(BillingParameterCodes.BILLING_INVOICE_NUMBER_ATTRIBUTE, ParameterType.ATTRIBUTE);
        context.invoiceStatusId = getParameterLongValue(BillingParameterCodes.BILLING_INVOICE_STATUS_ATTRIBUTE, ParameterType.ATTRIBUTE);
        context.invoiceStatusDraftValue =  getParameterStringValue(BillingParameterCodes.BILLING_INVOICE_STATUS_DRAFT_VALUE, ParameterType.ATTRIBUTE_VALUE);
        context.invoiceStatusValidatedValue =  getParameterStringValue(BillingParameterCodes.BILLING_INVOICE_STATUS_VALIDATED_VALUE, ParameterType.ATTRIBUTE_VALUE);
		        
        context.invoiceMailStatusId = getParameterLongValue(BillingParameterCodes.BILLING_INVOICE_SENDING_STATUS_ATTRIBUTE, ParameterType.ATTRIBUTE);
        context.subInvoiceRefId = getParameterLongValue(BillingParameterCodes.BILLING_INVOICE_SUB_INVOICE_NUMBER_ATTRIBUTE, ParameterType.ATTRIBUTE);
        if(context.subInvoiceRefId != null) {
	        column = context.billingJoin.getColumn(context.subInvoiceRefId, DimensionType.ATTRIBUTE);
			context.subInvoiceRefPosition = column != null ? column.getPosition() : null;	
        }
        
        context.invoiceMailStatusSentValue = getParameterStringValue(BillingParameterCodes.BILLING_INVOICE_SENDING_STATUS_SENT_VALUE, ParameterType.ATTRIBUTE_VALUE);
        context.invoiceMailStatusNotSendValue = getParameterStringValue(BillingParameterCodes.BILLING_INVOICE_SENDING_STATUS_NOT_YET_SENT_VALUE, ParameterType.ATTRIBUTE_VALUE);
        
        context.invoiceTypeId = getParameterLongValue(BillingParameterCodes.BILLING_INVOICE_CREDIT_NOTE_ATTRIBUTE, ParameterType.ATTRIBUTE);
		context.invoiceValue = getParameterStringValue(BillingParameterCodes.BILLING_INVOICE_CREDIT_NOTE_INVOICE_VALUE, ParameterType.ATTRIBUTE_VALUE);
        context.creditNoteValue = getParameterStringValue(BillingParameterCodes.BILLING_INVOICE_CREDIT_NOTE_CN_VALUE, ParameterType.ATTRIBUTE_VALUE);
                
        context.invoiceCommunicationMessageId = getParameterLongValue(BillingParameterCodes.BILLING_INVOICE_COMMUNICATION_MESSAGE_ATTRIBUTE, ParameterType.ATTRIBUTE);
        
        context.invoiceDescriptionId = getParameterLongValue(BillingParameterCodes.BILLING_INVOICE_DESCRIPTION_ATTRIBUTE, ParameterType.ATTRIBUTE);
        if(context.invoiceDescriptionId != null) {
	        column = context.billingJoin.getColumn(context.invoiceDescriptionId, DimensionType.ATTRIBUTE);
			context.invoiceDescriptionPosition = column != null ? column.getPosition() : null;	
        }
                
        context.invoiceNumberGeneratorId = getParameterLongValue(BillingParameterCodes.BILLING_INVOICE_NUMBER_SEQUENCE, ParameterType.INCREMENTAL_NUMBER);
        context.creditNoteNumberGeneratorId = getParameterLongValue(BillingParameterCodes.BILLING_CREDIT_NOTE_NUMBER_SEQUENCE, ParameterType.INCREMENTAL_NUMBER);
        
        context.invoiceValidationDateId = getParameterLongValue(BillingParameterCodes.BILLING_INVOICE_VALIDATION_DATE_PERIOD, ParameterType.PERIOD);	
        context.invoiceDateId = getParameterLongValue(BillingParameterCodes.BILLING_INVOICE_DATE_PERIOD, ParameterType.PERIOD);		
		context.invoiceDueDateId = getParameterLongValue(BillingParameterCodes.BILLING_INVOICE_DUE_DATE_PERIOD, ParameterType.PERIOD);
		context.invoiceAmountWithoutVatId = getParameterLongValue(BillingParameterCodes.BILLING_INVOICE_AMOUNT_WITHOUT_VAT_MEASURE, ParameterType.MEASURE);
		context.invoiceVatAmountId = getParameterLongValue(BillingParameterCodes.BILLING_INVOICE_VAT_AMOUNT_MEASURE, ParameterType.MEASURE);		
		context.invoiceTotalAmountId = getParameterLongValue(BillingParameterCodes.BILLING_INVOICE_TOTAL_AMOUNT_MEASURE, ParameterType.MEASURE);		
		context.invoiceDueDateCalculationId = getParameterLongValue(BillingParameterCodes.BILLING_INVOICE_DUE_DATE_CALCULATION_MEASURE, ParameterType.MEASURE);
		
        
		
		for(BillingModelPivot pivot : billingModel.getPivots()) {
			if(pivot.getAttributeId() != null) {
				//column = context.billingJoin.getColumn(pivot.getAttributeId(), DimensionType.ATTRIBUTE);
				column = context.billingJoin.getColumnByOid(pivot.getAttributeId());
				if(column != null) {
					context.pivotColumnPositions.put(pivot, column.getPosition());
				}
			}
		}
		
		for(BillingModelGroupingItem group : billingModel.getGroupingItems()) {
			if(group.getAttributeId() != null) {
				//column = context.billingJoin.getColumn(group.getAttributeId(), DimensionType.ATTRIBUTE);
				column = context.billingJoin.getColumnByOid(group.getAttributeId());
				if(column != null) {
					context.groupingColumnPositions.put(group.getAttributeId(), column.getPosition());
				}
			}
		}
		
		for(BillingModelParameter parameter : billingModel.getParameters()) {
			if(parameter.getDimensionId() != null) {
				//column = context.billingJoin.getColumn(parameter.getDimensionId(), parameter.getDimensionType());
				column = context.billingJoin.getColumnByOid(parameter.getDimensionId());
				if(column != null) {
					context.parameterColumnPositions.put(parameter.getName(), column.getPosition());
					parameter.columnPosition = column.getPosition();
				}
			}
		}
		return context;
	}
	
	private Long getParameterLongValue(String code, ParameterType type) {
		Parameter parameter = parameterRepository.findByCodeAndParameterType(code, type);
		return parameter != null ? parameter.getLongValue() : null;
	}
	
	private String getParameterStringValue(String code, ParameterType type) {
		Parameter parameter = parameterRepository.findByCodeAndParameterType(code, type);
		return parameter != null ? parameter.getStringValue() : null;
	}

}
