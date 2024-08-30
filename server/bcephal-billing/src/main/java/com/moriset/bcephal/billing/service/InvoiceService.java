/**
 * 
 */
package com.moriset.bcephal.billing.service;

import java.sql.Timestamp;
import java.util.List;
import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Order;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.moriset.bcephal.billing.domain.Invoice;
import com.moriset.bcephal.billing.domain.InvoiceBrowserData;
import com.moriset.bcephal.billing.domain.InvoiceItem;
import com.moriset.bcephal.billing.domain.InvoiceItemInfo;
import com.moriset.bcephal.billing.domain.InvoiceType;
import com.moriset.bcephal.billing.repository.BillTemplateRepository;
import com.moriset.bcephal.billing.repository.InvoiceItemInfoRepository;
import com.moriset.bcephal.billing.repository.InvoiceItemRepository;
import com.moriset.bcephal.billing.repository.InvoiceRepository;
import com.moriset.bcephal.domain.EditorData;
import com.moriset.bcephal.domain.ListChangeHandler;
import com.moriset.bcephal.domain.filters.BrowserDataFilter;
import com.moriset.bcephal.domain.filters.ColumnFilter;
import com.moriset.bcephal.repository.MainObjectRepository;
import com.moriset.bcephal.security.service.SecurityService;
import com.moriset.bcephal.security.service.UserSessionLogService;
import com.moriset.bcephal.service.MainObjectService;
import com.moriset.bcephal.service.RequestQueryBuilder;
import com.moriset.bcephal.utils.BcephalException;
import com.moriset.bcephal.utils.FunctionalityCodes;

import jakarta.persistence.criteria.Predicate;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;

/**
 * @author MORISET-6
 *
 */
@Service
@Slf4j
public class InvoiceService extends MainObjectService<Invoice, InvoiceBrowserData>{

	@Autowired
	InvoiceRepository invoiceRepository;
	
	@Autowired
	InvoiceItemRepository invoiceItemRepository;
	
	@Autowired
	InvoiceItemInfoRepository invoiceItemInfoRepository;
	
	@Autowired
	BillTemplateRepository billTemplateRepository;
	
	@Autowired
	SecurityService securityService;
	
	@Autowired
	UserSessionLogService logService;
	
	@Override
	protected String getBrowserFunctionalityCode() {
		return FunctionalityCodes.BILLING_RUN_INVOICE;
	}

	@Override
	protected List<Long> getHidedObjectId(Long profileId, String functionalityCode, String projectCode) {
		return securityService.getHideProfileById(profileId, functionalityCode, projectCode);
	}
	
	@Override
	public void saveUserSessionLog(String username,Long clientId, String projectCode, String usersession, Long objectId, String functionalityCode,
			String rightLevel,Long profileId) {
		logService.saveUserSessionLog(username,clientId,projectCode, usersession, objectId, functionalityCode,rightLevel, profileId);
	}
		
	protected InvoiceType getInvoiceType() {
		return InvoiceType.INVOICE;
	}
	
	@Override
	public MainObjectRepository<Invoice> getRepository() {
		return invoiceRepository;
	}
	
	@Override
	protected void initEditorData(EditorData<Invoice> data, HttpSession session, Locale locale) throws Exception {
		data.setTemplates(billTemplateRepository.findAllAsNameables());
	}
		
	public Invoice modifyInvoice(Invoice invoice, boolean reprint, Locale locale) {
		log.debug("Try to  modify invoice : {}", invoice);		
		try {	
			if(invoice == null) {
				String message = getMessageSource().getMessage("unable.to.save.null.invoice", new Object[]{invoice} , locale);
				throw new BcephalException(message);
			}
			if(!StringUtils.hasLength(invoice.getName())) {
				String message = getMessageSource().getMessage("unable.to.save.invoice.with.empty.name", new String[]{invoice.getName()} , locale);
				throw new BcephalException(HttpStatus.INTERNAL_SERVER_ERROR.value(), message);
			}			
			validateBeforeSave(invoice, locale);				
			invoice = saveInvoice(invoice, locale);					
			log.debug("Invoice saved : {} ", invoice.getId());
	        return invoice;	
		}
		catch (BcephalException e) {
			throw e;
		}
		catch (Exception e) {
			log.error("Unexpected error while save invoice : {}", invoice, e);
			String message = getMessageSource().getMessage("unable.to.save.invoice", new Object[]{invoice} , locale);
			throw new BcephalException(HttpStatus.INTERNAL_SERVER_ERROR.value(), message);
		}
	}
	
	
	@Override
	public Invoice save(Invoice invoice, Locale locale) {
		log.debug("Try to  Save invoice : {}", invoice);		
		try {	
			if(invoice == null) {
				String message = getMessageSource().getMessage("unable.to.save.null.invoice", new Object[]{invoice} , locale);
				throw new BcephalException(message);
			}
			if(!StringUtils.hasLength(invoice.getName())) {
				String message = getMessageSource().getMessage("unable.to.save.invoice.with.empty.name", new String[]{invoice.getName()} , locale);
				throw new BcephalException(HttpStatus.INTERNAL_SERVER_ERROR.value(), message);
			}			
			validateBeforeSave(invoice, locale);				
			invoice = saveInvoice(invoice, locale);					
			log.debug("Invoice saved : {} ", invoice.getId());
	        return invoice;	
		}
		catch (BcephalException e) {
			throw e;
		}
		catch (Exception e) {
			log.error("Unexpected error while save invoice : {}", invoice, e);
			String message = getMessageSource().getMessage("unable.to.save.invoice", new Object[]{invoice} , locale);
			throw new BcephalException(HttpStatus.INTERNAL_SERVER_ERROR.value(), message);
		}
	}
	
	@Override
	public void delete(Invoice invoice) {
		log.debug("Try to delete invoice : {}", invoice);	
		if(invoice == null || invoice.getId() == null) {
			return;
		}		
		deleteInvoice(invoice);
		log.debug("Invoice successfully to delete : {} ", invoice);
	    return;	
	}
	
	
	private Invoice saveInvoice(Invoice invoice, Locale locale) {
		List<Invoice> children = invoice.getChildren();
		ListChangeHandler<InvoiceItem> items = invoice.getItemListChangeHandler();
		ListChangeHandler<InvoiceItemInfo> infos = invoice.getInfoListChangeHandler();	
		
		invoice.setModificationDate(new Timestamp(System.currentTimeMillis()));
		invoice = invoiceRepository.save(invoice);
		Invoice id = invoice;
		items.getNewItems().forEach( item -> {
			log.trace("Try to save invoice item : {}", item);
			item.setInvoice(id);
			save(item);
			log.trace("Invoice item saved : {}", item.getId());
		});
		items.getUpdatedItems().forEach( item -> {
			log.trace("Try to save invoice item : {}", item);
			item.setInvoice(id);
			save(item);
			log.trace("Invoice item saved : {}", item.getId());
		});
		items.getDeletedItems().forEach( item -> {
			if(item.getId() != null) {
				log.trace("Try to delete invoice item : {}", item);					
				delete(item);
				log.trace("Invoice item deleted : {}", item.getId());
			}
		});
		
		children.forEach( item -> {
			log.trace("Try to save invoice child : {}", item);
			item.setParent(id);
			saveInvoice(item, locale);	
			log.trace("Invoice child saved : {}", item.getId());
		});
						
		infos.getNewItems().forEach( info -> {
			log.trace("Try to save invoice item info : {}", info);
			info.setInvoice(id);
			invoiceItemInfoRepository.save(info);
			log.trace("Invoice item info saved : {}", info.getId());
		});
		infos.getUpdatedItems().forEach( info -> {
			log.trace("Try to save invoice item info : {}", info);
			info.setInvoice(id);
			invoiceItemInfoRepository.save(info);
			log.trace("Invoice item info saved : {}", info.getId());
		});
		infos.getDeletedItems().forEach( info -> {
			if(info.getId() != null) {
				log.trace("Try to delete invoice item info : {}", info);					
				invoiceItemInfoRepository.deleteById(info.getId());
				log.trace("Invoice item info deleted : {}", info.getId());
			}
		});
		
        return invoice;	
	}
	
	private void deleteInvoice(Invoice invoice) {
		List<Invoice> children = invoice.getChildren();
		ListChangeHandler<InvoiceItem> items = invoice.getItemListChangeHandler();		
		items.getItems().forEach( item -> {
			if(item.getId() != null) {
				log.trace("Try to delete invoice item : {}", item);					
				delete(item);
				log.trace("Invoice item deleted : {}", item.getId());
			}
		});
		items.getDeletedItems().forEach( item -> {
			if(item.getId() != null) {
				log.trace("Try to delete invoice item : {}", item);					
				delete(item);
				log.trace("Invoice item deleted : {}", item.getId());
			}
		});
		
		children.forEach( item -> {
			log.trace("Try to save invoice child : {}", item);
			deleteInvoice(item);	
			log.trace("Invoice child saved : {}", item.getId());
		});
		
		
		ListChangeHandler<InvoiceItemInfo> infos = invoice.getInfoListChangeHandler();	
		infos.getItems().forEach( info -> {
			if(info.getId() != null) {
				log.trace("Try to delete invoice item info : {}", info);					
				invoiceItemInfoRepository.deleteById(info.getId());
				log.trace("Invoice item info deleted : {}", info.getId());
			}
		});
		infos.getDeletedItems().forEach( info -> {
			if(info.getId() != null) {
				log.trace("Try to delete invoice item info : {}", info);					
				invoiceItemInfoRepository.deleteById(info.getId());
				log.trace("Invoice item info deleted : {}", info.getId());
			}
		});
		
		invoiceRepository.deleteById(invoice.getId());
	}

	private void save(InvoiceItem item) {
		ListChangeHandler<InvoiceItemInfo> infos = item.getInfoListChangeHandler();		
		item = invoiceItemRepository.save(item);
		InvoiceItem id = item;
		infos.getNewItems().forEach( info -> {
			log.trace("Try to save invoice item info : {}", info);
			info.setItem(id);
			invoiceItemInfoRepository.save(info);
			log.trace("Invoice item info saved : {}", info.getId());
		});
		infos.getUpdatedItems().forEach( info -> {
			log.trace("Try to save invoice item info : {}", info);
			info.setItem(id);
			invoiceItemInfoRepository.save(info);
			log.trace("Invoice item info saved : {}", info.getId());
		});
		infos.getDeletedItems().forEach( info -> {
			if(info.getId() != null) {
				log.trace("Try to delete invoice item info : {}", info);					
				invoiceItemInfoRepository.deleteById(info.getId());
				log.trace("Invoice item info deleted : {}", info.getId());
			}
		});
	}

	private void delete(InvoiceItem item) {
		ListChangeHandler<InvoiceItemInfo> infos = item.getInfoListChangeHandler();	
		infos.getItems().forEach( info -> {
			if(info.getId() != null) {
				log.trace("Try to delete invoice item info : {}", info);					
				invoiceItemInfoRepository.deleteById(info.getId());
				log.trace("Invoice item info deleted : {}", info.getId());
			}
		});
		infos.getDeletedItems().forEach( info -> {
			if(info.getId() != null) {
				log.trace("Try to delete invoice item info : {}", info);					
				invoiceItemInfoRepository.deleteById(info.getId());
				log.trace("Invoice item info deleted : {}", info.getId());
			}
		});
		invoiceItemRepository.deleteById(item.getId());
	}

	@Override
	protected Invoice getNewItem() {
		Invoice invoice = new Invoice();
		invoice.setType(InvoiceType.INVOICE);
		String baseName = "Invoice ";
		int i = 1;
		invoice.setName(baseName + i);
		while(getByName(invoice.getName()) != null) {
			i++;
			invoice.setName(baseName + i);
		}
		return invoice;
	}

	@Override
	protected InvoiceBrowserData getNewBrowserData(Invoice item) {
		return new InvoiceBrowserData(item);
	}

	@Override
	protected Specification<Invoice> getBrowserDatasSpecification(BrowserDataFilter filter, Locale locale, List<Long> hidedObjectIds) {
		return (root, query, cb) -> {
			RequestQueryBuilder<Invoice> qBuilder = new RequestQueryBuilder<Invoice>(root, query, cb);
		    qBuilder.select(InvoiceBrowserData.class);	
		    InvoiceType invoicetype = getInvoiceType();
		    if (invoicetype != null) {
		    	Predicate predicate = qBuilder.getCriteriaBuilder().equal(root.get("type"), invoicetype);
		    	qBuilder.add(predicate);
		    }	
		    qBuilder.addNoTInObjectId(hidedObjectIds);
		    if(filter.getColumnFilters() != null) {
		    	build(filter.getColumnFilters());
		    	filter.getColumnFilters().getItems().forEach(filte ->{
		    		build(filte);
		    	});
				qBuilder.addFilter(filter.getColumnFilters());
			}
	        return qBuilder.build();
		};
	}
	
	protected void build(ColumnFilter columnFilter) {		
		if ("clientInternalNumber".equalsIgnoreCase(columnFilter.getName())) {
			columnFilter.setName("clientInternalNumber");
			columnFilter.setType(String.class);
		} else if ("clientName".equalsIgnoreCase(columnFilter.getName())) {
			columnFilter.setName("clientName");
			columnFilter.setType(String.class);
		} else if ("clientNumber".equalsIgnoreCase(columnFilter.getName())) {
			columnFilter.setName("clientNumber");
			columnFilter.setType(String.class);
		} else if ("invoiceDate".equalsIgnoreCase(columnFilter.getName())) {
			columnFilter.setName("invoiceDate");
			columnFilter.setType(String.class);
		} else if ("clientDoingBusinessAs".equalsIgnoreCase(columnFilter.getName())) {
			columnFilter.setName("clientDoingBusinessAs");
			columnFilter.setType(String.class);
		} else if ("merchantNumber".equalsIgnoreCase(columnFilter.getName())) {
			columnFilter.setName("merchantNumber");
			columnFilter.setType(String.class);
		}else if ("runNumber".equalsIgnoreCase(columnFilter.getName())) {
			columnFilter.setName("runNumber");
			columnFilter.setType(String.class);
		}else if ("orderReference".equalsIgnoreCase(columnFilter.getName())) {
			columnFilter.setName("orderReference");
			columnFilter.setType(String.class);
		}else if ("validationReference".equalsIgnoreCase(columnFilter.getName())) {
			columnFilter.setName("validationReference");
			columnFilter.setType(String.class);
		}else if ("draftReference".equalsIgnoreCase(columnFilter.getName())) {
			columnFilter.setName("draftReference");
			columnFilter.setType(String.class);
		}else if ("reference".equalsIgnoreCase(columnFilter.getName())) {
			columnFilter.setName("reference");
			columnFilter.setType(String.class);
		} else if ("type".equalsIgnoreCase(columnFilter.getName())) {
			columnFilter.setName("type");
			columnFilter.setType(InvoiceType.class);
		} else if ("version".equalsIgnoreCase(columnFilter.getName())) {
			columnFilter.setName("version");
			columnFilter.setType(InvoiceType.class);
		} else if ("amountWithoutVat".equalsIgnoreCase(columnFilter.getName())) {
			columnFilter.setName("amountWithoutVat");
			columnFilter.setType(InvoiceType.class);
		} else if ("vatAmount".equalsIgnoreCase(columnFilter.getName())) {
			columnFilter.setName("vatAmount");
			columnFilter.setType(InvoiceType.class);
		} else if ("status".equalsIgnoreCase(columnFilter.getName())) {
			columnFilter.setName("status");
			columnFilter.setType(InvoiceType.class);
		}
				
		super.build(columnFilter);
	}

	
	@Override
	protected Sort getBrowserDatasSort(BrowserDataFilter filter, java.util.Locale locale) {
		if(filter.getColumnFilters() != null) {
			
			if(filter.getColumnFilters().isSortFilter()) {
	    		build(filter.getColumnFilters());
	    		if(filter.getColumnFilters().getLink() != null && filter.getColumnFilters().getLink().equals(BrowserDataFilter.SortByDesc)) {
	    			return Sort.by(Order.desc(filter.getColumnFilters().getName()));
	    		}else {
	    			return Sort.by(Order.asc(filter.getColumnFilters().getName()));
	    		}
	    	}else {
	    		if(filter.getColumnFilters().getItems() != null && filter.getColumnFilters().getItems().size() > 0) {
	    			for(ColumnFilter columnFilter : filter.getColumnFilters().getItems()){
	    				if(columnFilter.isSortFilter()) {
	    		    		build(columnFilter);
	    		    		if(columnFilter.getLink() != null && columnFilter.getLink().equals(BrowserDataFilter.SortByDesc)) {
	    		    			return Sort.by(Order.desc(columnFilter.getName()));
	    		    		}else {
	    		    			return Sort.by(Order.asc(columnFilter.getName()));
	    		    		}
	    		    	}
	    			}
	    		}
	    	}
    	}
		return Sort.by(Order.desc("id"));
	}

	

	

}
