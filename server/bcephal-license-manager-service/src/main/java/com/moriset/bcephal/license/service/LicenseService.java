package com.moriset.bcephal.license.service;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.moriset.bcephal.license.domain.BcephalException;
import com.moriset.bcephal.license.domain.BrowserDataFilter;
import com.moriset.bcephal.license.domain.EditorData;
import com.moriset.bcephal.license.domain.EditorDataFilter;
import com.moriset.bcephal.license.domain.License;
import com.moriset.bcephal.license.domain.LicenseBrowserData;
import com.moriset.bcephal.license.domain.LicenseFunctionality;
import com.moriset.bcephal.license.domain.LicenseParameter;
import com.moriset.bcephal.license.domain.ListChangeHandler;
import com.moriset.bcephal.license.domain.Product;
import com.moriset.bcephal.license.domain.ProductFunctionality;
import com.moriset.bcephal.license.repository.LicenseFunctionalityRepository;
import com.moriset.bcephal.license.repository.LicenseParameterRepository;
import com.moriset.bcephal.license.repository.LicenseRepository;
import com.moriset.bcephal.license.repository.ProductRepository;
import com.moriset.bcephal.utils.FunctionalityCodes;

import jakarta.servlet.http.HttpSession;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;

@Data
@Slf4j
@EqualsAndHashCode(callSuper = false)
@Service
public class LicenseService extends MainObjectService<License, LicenseBrowserData>{

	@Autowired
	LicenseRepository licenseRepository;
	
	@Autowired
	ProductRepository productRepository;
	
	@Autowired
	LicenseParameterRepository licenseParameterRepository;
	
	@Autowired
	LicenseFunctionalityRepository licenseFunctionalityRepository;
	
	@Override
	public LicenseRepository getRepository() {
		return licenseRepository;
	}
	
	
	@Override
	@Transactional
	public License save(License license, Locale locale) {
		log.debug("Try to save license : {}", license);
		if (getRepository() == null) {
			return license;
		}
		try {
			if (license == null) {
				String message = getMessageSource().getMessage("unable.to.save.null.object",
						new Object[] { license }, locale);
				throw new BcephalException(HttpStatus.BAD_REQUEST.value(), message);
			}
			ListChangeHandler<LicenseFunctionality> functionalityList = license.getFunctionalityListChangeHandler();
			ListChangeHandler<LicenseParameter> parameterList = license.getParameterListChangeHandler();
			if(license.getCode() == null) {
				license.setCode("" + System.currentTimeMillis());
			}
			
			license.setModificationDate(new Timestamp(System.currentTimeMillis()));
			if(license.getProductId() != null) {
				license.setProduct(new Product());
				license.getProduct().setId(license.getProductId());
			}
			license = getRepository().save(license);
			var p = license;
			parameterList.getNewItems().forEach(item -> {
				log.trace("Try to save license parameter : {}", item);
				item.setLicense(p);
				item.initValue();
				licenseParameterRepository.save(item);
				log.trace("License parameter saved : {}", item.getId());
			});
			parameterList.getUpdatedItems().forEach(item -> {
				log.trace("Try to save license parameter : {}", item);
				item.setLicense(p);
				item.initValue();
				licenseParameterRepository.save(item);
				log.trace("License parameter saved : {}", item.getId());
			});
			parameterList.getDeletedItems().forEach(item -> {
				if (item.getId() != null) {
					log.trace("Try to delete license parameter : {}", item);
					licenseParameterRepository.deleteById(item.getId());
					log.trace("License parameter deleted : {}", item.getId());
				}
			});
			
			
			functionalityList.getNewItems().forEach(item -> {
				log.trace("Try to save license functionality : {}", item);
				item.setLicense(p);
				licenseFunctionalityRepository.save(item);
				log.trace("License functionality saved : {}", item.getId());
			});
			functionalityList.getUpdatedItems().forEach(item -> {
				log.trace("Try to save license functionality : {}", item);
				item.setLicense(p);
				licenseFunctionalityRepository.save(item);
				log.trace("License functionality saved : {}", item.getId());
			});
			functionalityList.getDeletedItems().forEach(item -> {
				if (item.getId() != null) {
					log.trace("Try to delete license functionality : {}", item);
					licenseFunctionalityRepository.deleteById(item.getId());
					log.trace("License functionality deleted : {}", item.getId());
				}
			});			
			log.debug("License saved! ID : {}; Name : {} ", license.getId(), license.getName());
			return license;
		} 
		catch (BcephalException e) {
			throw e;
		} 
		catch (Exception e) {
			log.error("Unexpected error while saving license : {}", license.getName(), e);
			String message = getMessageSource().getMessage("unable.to.save.license", new Object[] { license }, locale);
			throw new BcephalException(message + " " + e.getMessage());
		}
	}
	
	@Override
	public void delete(License license) {
		log.debug("Try to delete license : {}", license);	
		if(license == null || license.getId() == null) {
			return;
		}
		
		ListChangeHandler<LicenseFunctionality> functionalityList = license.getFunctionalityListChangeHandler();
		ListChangeHandler<LicenseParameter> parameterList = license.getParameterListChangeHandler();
		
		functionalityList.getItems().forEach( item -> {
			if(item.getId() != null) {
				log.trace("Try to delete license functionality : {}", item);
				licenseFunctionalityRepository.deleteById(item.getId());
				log.trace("License functionality deleted : {}", item.getId());
			}
		});
		functionalityList.getDeletedItems().forEach( item -> {
			if(item.getId() != null) {
				log.trace("Try to delete license functionality : {}", item);
				licenseFunctionalityRepository.deleteById(item.getId());
				log.trace("License functionality deleted : {}", item.getId());
			}
		});
		
		parameterList.getItems().forEach( item -> {
			if(item.getId() != null) {
				log.trace("Try to delete license parameter : {}", item);
				licenseParameterRepository.deleteById(item.getId());
				log.trace("License parameter deleted : {}", item.getId());
			}
		});
		parameterList.getDeletedItems().forEach( item -> {
			if(item.getId() != null) {
				log.trace("Try to delete license parameter : {}", item);
				licenseParameterRepository.deleteById(item.getId());
				log.trace("License parameter deleted : {}", item.getId());
			}
		});
			
		getRepository().deleteById(license.getId());
		
		log.debug("License successfully to delete : {} ", license);
	    return;	
	}
	
	@Override
	public EditorData<License> getEditorData(EditorDataFilter filter, HttpSession session, Locale locale)
			throws Exception {
		EditorData<License> data = new EditorData<>();
		if (filter.isNewData()) {
			data.setItem(getNewItem());
			if(filter.getSecondId() != null) {
				Optional<Product> result = productRepository.findById(filter.getSecondId());
				if(result.isPresent()) {
					data.getItem().initialize(result.get());
				}
			}
		} else {
			data.setItem(getById(filter.getId()));
		}
		initEditorData(data, session, locale);
		return data;
	}
	
	@Override
	protected void initEditorData(EditorData<License> data, HttpSession session, Locale locale) throws Exception {
		if(data.getItem().getProduct() != null) {
			for(ProductFunctionality functionality : data.getItem().getProduct().getFunctionalityListChangeHandler().getItems()) {
				data.getFunctionalities().add(functionality.getCode());
			}
		}
	}

	@Override
	protected License getNewItem() {
		License license = new License();
		String baseName = "License ";
		int i = 1;
		license.setName(baseName + i);
		while (getByName(license.getName()) != null) {
			i++;
			license.setName(baseName + i);
		}
		return license;
	}

	@Override
	protected String getBrowserFunctionalityCode() {
		return FunctionalityCodes.LICENSE_MANAGER_LICENSE;
	}

	@Override
	public void saveUserSessionLog(String username, Long clientId, String projectCode, String usersession,
			Long objectId, String functionalityCode, String rightLevel, Long profileId) {
		
	}

	@Override
	protected List<Long> getHidedObjectId(Long profileId, String functionalityCode, String projectCode) {
		return new ArrayList<>();
	}

	@Override
	protected LicenseBrowserData getNewBrowserData(License item) {
		return new LicenseBrowserData(item);
	}

	@Override
	protected Specification<License> getBrowserDatasSpecification(BrowserDataFilter filter, Locale locale,
			List<Long> hidedObjectIds) {
		return (root, query, cb) -> {
			RequestQueryBuilder<License> qBuilder = new RequestQueryBuilder<License>(root, query, cb);
			qBuilder.select(LicenseBrowserData.class, root.get("id"), root.get("name"), root.get("code"),
					root.get("description"), root.get("creationDate"), root.get("modificationDate"));
			if (filter != null && StringUtils.hasText(filter.getCriteria())) {
				qBuilder.addLikeCriteria("name", filter.getCriteria());
			}
			if (filter != null && filter.getGroupId() != null) {
				qBuilder.add(qBuilder.Equals("product", filter.getGroupId(), "id"));
			}
			qBuilder.addNoTInObjectId(hidedObjectIds);
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

}
