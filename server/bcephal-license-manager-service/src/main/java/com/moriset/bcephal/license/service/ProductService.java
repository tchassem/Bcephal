package com.moriset.bcephal.license.service;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.moriset.bcephal.license.domain.BcephalException;
import com.moriset.bcephal.license.domain.BrowserDataFilter;
import com.moriset.bcephal.license.domain.ListChangeHandler;
import com.moriset.bcephal.license.domain.Product;
import com.moriset.bcephal.license.domain.ProductBrowserData;
import com.moriset.bcephal.license.domain.ProductFunctionality;
import com.moriset.bcephal.license.domain.ProductParameter;
import com.moriset.bcephal.license.repository.ProductFunctionalityRepository;
import com.moriset.bcephal.license.repository.ProductParameterRepository;
import com.moriset.bcephal.license.repository.ProductRepository;
import com.moriset.bcephal.utils.FunctionalityCodes;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;

@Data
@Slf4j
@EqualsAndHashCode(callSuper = false)
@Service
public class ProductService extends MainObjectService<Product, ProductBrowserData>{

	@Autowired
	ProductRepository productRepository;
	
	@Autowired
	ProductParameterRepository productParameterRepository;
	
	@Autowired
	ProductFunctionalityRepository productFunctionalityRepository;
	
	@Override
	public ProductRepository getRepository() {
		return productRepository;
	}
	
	
	@Override
	@Transactional
	public Product save(Product product, Locale locale) {
		log.debug("Try to save Product : {}", product);
		if (getRepository() == null) {
			return product;
		}
		try {
			if (product == null) {
				String message = getMessageSource().getMessage("unable.to.save.null.object",
						new Object[] { product }, locale);
				throw new BcephalException(HttpStatus.BAD_REQUEST.value(), message);
			}
			ListChangeHandler<ProductFunctionality> functionalityList = product.getFunctionalityListChangeHandler();
			ListChangeHandler<ProductParameter> parameterList = product.getParameterListChangeHandler();
			if(product.getCode() == null) {
				product.setCode("" + System.currentTimeMillis());
			}
			product.setModificationDate(new Timestamp(System.currentTimeMillis()));
			product = getRepository().save(product);
			var p = product;
			parameterList.getNewItems().forEach(item -> {
				log.trace("Try to save product parameter : {}", item);
				item.setProduct(p);
				productParameterRepository.save(item);
				log.trace("Product parameter saved : {}", item.getId());
			});
			parameterList.getUpdatedItems().forEach(item -> {
				log.trace("Try to save product parameter : {}", item);
				item.setProduct(p);
				productParameterRepository.save(item);
				log.trace("Product parameter saved : {}", item.getId());
			});
			parameterList.getDeletedItems().forEach(item -> {
				if (item.getId() != null) {
					log.trace("Try to delete product parameter : {}", item);
					productParameterRepository.deleteById(item.getId());
					log.trace("Product parameter deleted : {}", item.getId());
				}
			});
			
			
			functionalityList.getNewItems().forEach(item -> {
				log.trace("Try to save product functionality : {}", item);
				item.setProduct(p);
				productFunctionalityRepository.save(item);
				log.trace("Product functionality saved : {}", item.getId());
			});
			functionalityList.getUpdatedItems().forEach(item -> {
				log.trace("Try to save product functionality : {}", item);
				item.setProduct(p);
				productFunctionalityRepository.save(item);
				log.trace("Product functionality saved : {}", item.getId());
			});
			functionalityList.getDeletedItems().forEach(item -> {
				if (item.getId() != null) {
					log.trace("Try to delete product functionality : {}", item);
					productFunctionalityRepository.deleteById(item.getId());
					log.trace("Product functionality deleted : {}", item.getId());
				}
			});			
			log.debug("Product saved! ID : {}; Name : {} ", product.getId(), product.getName());
			product.setParameters(product.getParameterListChangeHandler().getItems());
			product.setFunctionalities(product.getFunctionalityListChangeHandler().getItems());
			return product;
		} 
		catch (BcephalException e) {
			throw e;
		} 
		catch (Exception e) {
			log.error("Unexpected error while saving product : {}", product.getName(), e);
			String message = getMessageSource().getMessage("unable.to.save.product", new Object[] { product }, locale);
			throw new BcephalException(message + " " + e.getMessage());
		}
	}
	
	@Override
	public void delete(Product product) {
		log.debug("Try to delete Product : {}", product);	
		if(product == null || product.getId() == null) {
			return;
		}
		
		ListChangeHandler<ProductFunctionality> functionalityList = product.getFunctionalityListChangeHandler();
		ListChangeHandler<ProductParameter> parameterList = product.getParameterListChangeHandler();
		
		functionalityList.getItems().forEach( item -> {
			if(item.getId() != null) {
				log.trace("Try to delete product functionality : {}", item);
				productFunctionalityRepository.deleteById(item.getId());
				log.trace("Product functionality deleted : {}", item.getId());
			}
		});
		functionalityList.getDeletedItems().forEach( item -> {
			if(item.getId() != null) {
				log.trace("Try to delete product functionality : {}", item);
				productFunctionalityRepository.deleteById(item.getId());
				log.trace("Product functionality deleted : {}", item.getId());
			}
		});
		
		parameterList.getItems().forEach( item -> {
			if(item.getId() != null) {
				log.trace("Try to delete product parameter : {}", item);
				productParameterRepository.deleteById(item.getId());
				log.trace("Product parameter deleted : {}", item.getId());
			}
		});
		parameterList.getDeletedItems().forEach( item -> {
			if(item.getId() != null) {
				log.trace("Try to delete product parameter : {}", item);
				productParameterRepository.deleteById(item.getId());
				log.trace("Product parameter deleted : {}", item.getId());
			}
		});
			
		getRepository().deleteById(product.getId());
		
		log.debug("Product successfully to delete : {} ", product);
	    return;	
	}
	

	@Override
	protected Product getNewItem() {
		Product product = new Product();
		String baseName = "Product ";
		int i = 1;
		product.setName(baseName + i);
		while (getByName(product.getName()) != null) {
			i++;
			product.setName(baseName + i);
		}
		return product;
	}

	@Override
	protected String getBrowserFunctionalityCode() {
		return FunctionalityCodes.LICENSE_MANAGER_PRODUCT;
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
	protected ProductBrowserData getNewBrowserData(Product item) {
		return new ProductBrowserData(item);
	}

	@Override
	protected Specification<Product> getBrowserDatasSpecification(BrowserDataFilter filter, Locale locale,
			List<Long> hidedObjectIds) {
		return (root, query, cb) -> {
			RequestQueryBuilder<Product> qBuilder = new RequestQueryBuilder<Product>(root, query, cb);
			qBuilder.select(ProductBrowserData.class, root.get("id"), root.get("name"), root.get("code"),
					root.get("description"), root.get("creationDate"), root.get("modificationDate"));
			if (filter != null && StringUtils.hasText(filter.getCriteria())) {
				qBuilder.addLikeCriteria("name", filter.getCriteria());
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
