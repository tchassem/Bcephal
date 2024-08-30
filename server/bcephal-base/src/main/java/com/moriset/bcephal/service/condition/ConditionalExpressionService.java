package com.moriset.bcephal.service.condition;

import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.moriset.bcephal.domain.BrowserData;
import com.moriset.bcephal.domain.ListChangeHandler;
import com.moriset.bcephal.domain.condition.ConditionalExpression;
import com.moriset.bcephal.domain.condition.ConditionalExpressionItem;
import com.moriset.bcephal.repository.PersistentRepository;
import com.moriset.bcephal.repository.condition.ConditionalExpressionItemRepository;
import com.moriset.bcephal.repository.condition.ConditionalExpressionRepository;
import com.moriset.bcephal.service.PersistentService;
import com.moriset.bcephal.utils.BcephalException;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class ConditionalExpressionService extends PersistentService<ConditionalExpression, BrowserData> {

	@Autowired
	ConditionalExpressionRepository conditionalExpressionRepository;
	
	@Autowired
	ConditionalExpressionItemRepository conditionalExpressionItemRepository;

	
	@Override
	public PersistentRepository<ConditionalExpression> getRepository() {
		return conditionalExpressionRepository;
	}
	
	@Override
	public ConditionalExpression save(ConditionalExpression expression, Locale locale) {
		log.debug("Try to  Save expression : {}", expression);
		try {
			if (expression == null) {
				String message = getMessageSource().getMessage("unable.to.save.null.expression", new Object[] { expression },
						locale);
				throw new BcephalException(message);
			}
			
			ListChangeHandler<ConditionalExpressionItem> items = expression.getItemListChangeHandler();			
			expression = super.save(expression, locale);
			ConditionalExpression id = expression;
			
			items.getNewItems().forEach(item -> {
				item.setExpression(id);
				conditionalExpressionItemRepository.save(item);
			});
			items.getUpdatedItems().forEach(item -> {
				item.setExpression(id);
				conditionalExpressionItemRepository.save(item);
			});
			items.getDeletedItems().forEach(item -> {
				if(item.getId() != null) {
					conditionalExpressionItemRepository.deleteById(item.getId());
				}
			});			
			log.debug("Expression successfully to save : {} ", expression);				
			return expression;
		} catch (BcephalException e) {
			throw e;
		} catch (Exception e) {
			log.error("Unexpected error while save expression : {}", expression, e);
			String message = getMessageSource().getMessage("unable.to.save.expression", new Object[] { expression }, locale);
			throw new BcephalException(HttpStatus.INTERNAL_SERVER_ERROR.value(), message);
		}
	}
	
	@Override
	public void delete(ConditionalExpression expression) {
		if(expression == null || expression.getId() == null) {
			return;
		}
		expression.getItemListChangeHandler().getItems().forEach(item -> {
			if(item.getId() != null) {
				conditionalExpressionItemRepository.deleteById(item.getId());
			}
		});
		expression.getItemListChangeHandler().getOriginalList().forEach(item -> {
			if(item.getId() != null) {
				conditionalExpressionItemRepository.deleteById(item.getId());
			}
		});
		expression.getItemListChangeHandler().getDeletedItems().forEach(item -> {
			if(item.getId() != null) {
				conditionalExpressionItemRepository.deleteById(item.getId());
			}
		});		
		getRepository().deleteById(expression.getId());		
		log.debug("Expression successfully to delete : {} ", expression);
	}

}
