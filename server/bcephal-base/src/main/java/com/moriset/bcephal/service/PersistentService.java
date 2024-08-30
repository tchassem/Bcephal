package com.moriset.bcephal.service;

import java.util.List;
import java.util.Locale;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.moriset.bcephal.domain.IPersistent;
import com.moriset.bcephal.repository.PersistentRepository;
import com.moriset.bcephal.utils.BcephalException;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@Data
public abstract class PersistentService<P extends IPersistent, B> {

	public abstract PersistentRepository<P> getRepository();

	@Autowired
	protected MessageSource messageSource;

	/**
	 * get entity by name
	 * 
	 * @param id
	 * @return
	 */
	public P getById(Long id) {
		log.debug("Try to get by id : {}", id);
		if (getRepository() == null && id != null) {
			return null;
		}
		Optional<P> item = getRepository().findById(id);
		if (item.isPresent()) {
			return item.get();
		}
		return null;
	}

	/**
	 * delete entity by id
	 * 
	 * @param id
	 * @param locale
	 */
	public void deleteById(Long id) {
		log.debug("Try to delete by id : {}", id);
		if (getRepository() == null || id == null) {
			return;
		}
		P item = getRepository().getReferenceById(id);
		delete(item);
		log.debug("Entity successfully deleted : {}", id);
	}

	public void delete(P item) {
		log.debug("Try to delete : {}", item);
		if (item == null || item.getId() == null) {
			return;
		}
		getRepository().deleteById((Long) item.getId());
		log.debug("Entity successfully deleted : {}", item);
	}

	/**
	 * delete entities by ids
	 * 
	 * @param ids
	 * @param locale
	 */
	public void deleteByIds(List<Long> ids) {
		log.debug("Try to delete by ids : {}", ids);
		if (getRepository() == null || ids == null) {
			return;
		}
		ids.forEach(item -> {
			deleteById(item);
		});
	}

	public P save(P entity, Locale locale) {
		log.debug("Try to Save entity : {}", entity);
		if (getRepository() == null) {
			return entity;
		}
		try {
			if (entity == null) {
				String message = messageSource.getMessage("unable.to.save.null.object", new Object[] { entity },
						locale);
				throw new BcephalException(message);
			}
			entity = getRepository().save(entity);
			log.debug("entity successfully saved : {}", entity);
			return entity;
		} catch (BcephalException e) {
			throw e;
		} catch (Exception e) {
			log.error("Unexpected error while save entity : {}", entity, e);
			String message = messageSource.getMessage("unable.to.save.model", new Object[] { entity }, locale);
			throw new BcephalException(HttpStatus.INTERNAL_SERVER_ERROR.value(), message);
		}
	}

}
