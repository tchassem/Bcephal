package com.moriset.bcephal.settings.service;

import java.util.List;
import java.util.Locale;

import jakarta.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.moriset.bcephal.domain.BLabel;
import com.moriset.bcephal.domain.BLabels;
import com.moriset.bcephal.domain.BrowserData;
import com.moriset.bcephal.domain.EditorDataFilter;
import com.moriset.bcephal.domain.ListChangeHandler;
import com.moriset.bcephal.repository.BLabelRepository;
import com.moriset.bcephal.service.PersistentService;
import com.moriset.bcephal.settings.domain.BLabelEditorData;
import com.moriset.bcephal.utils.BcephalException;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;

@Service
@Data
@Slf4j
@EqualsAndHashCode(callSuper = false)
public class BLabelService extends PersistentService<BLabel, BrowserData> {

	@Autowired
	BLabelRepository bLabelRepository;

	@Override
	public BLabelRepository getRepository() {
		return bLabelRepository;
	}

	public BLabelEditorData getEditorData(EditorDataFilter filter, HttpSession session, Locale locale) throws Exception {
		log.debug("Call of get editor data");
		BLabelEditorData data = new BLabelEditorData();
		BLabels labels = getLabels(locale.getLanguage(), locale);
		data.setItem(labels);
		return data;
	}

	@Transactional
	public BLabel save(BLabel bLabel, Locale locale) {
		log.debug("Try to Save parameters");
		try {
			if (bLabel == null) {
				String message = getMessageSource().getMessage("unable.to.save.null.parameter", new Object[] {}, locale);
				throw new BcephalException(HttpStatus.BAD_REQUEST.value(), message);
			}
			getRepository().save(bLabel);
			log.debug("Parameters saved ");
			return bLabel;
		} catch (BcephalException e) {
			throw e;
		} catch (Exception e) {
			log.error("Unexpected error while save parameters", e);
			String message = getMessageSource().getMessage("unable.to.save.parameters", new Object[] {}, locale);
			throw new BcephalException(HttpStatus.INTERNAL_SERVER_ERROR.value(), message);
		}
	}

	@Transactional
	public BLabels save(BLabels labels, Locale locale) {
		log.debug("Try to Save list of labels");
		try {
			labels.getLabelItemListChangeHandler().getNewItems().forEach( item -> {
				if(item.getId() == null) {
					log.trace("Try to save label : {}", item);
					getRepository().save(item);
					log.trace("Label saved : {}", item.getCode());
				}
			});
			labels.getLabelItemListChangeHandler().getUpdatedItems().forEach( item -> {
				if(item.getId() != null) {
					log.trace("Try to update label : {}", item);
					getRepository().save(item);
					log.trace("Label updated : {}", item.getCode());
				}
			});
			labels.getLabelItemListChangeHandler().getDeletedItems().forEach( item -> {
				if(item.getId() != null) {
					log.trace("Try to delete label : {}", item);
					getRepository().deleteById(item.getId());
					log.trace("Label deleted");
				}
			});
			return labels;
		} catch (BcephalException e) {
			throw e;
		} catch (Exception e) {
			log.error("Unexpected error while save labels", e);
			String message = getMessageSource().getMessage("unable.to.save.labels", new Object[] {}, locale);
			throw new BcephalException(HttpStatus.INTERNAL_SERVER_ERROR.value(), message);
		}
	}

	public BLabels getLabels(String lang, Locale locale) {
		log.debug("Try to retrieve list of labels");
		try {
			BLabels labels = new BLabels();
			labels.setLang(lang);
			List<BLabel> items = getRepository().findAllByLangOrderByCode(lang);
			labels.setLabelItemListChangeHandler(new ListChangeHandler<>(items));
			return labels;
		} catch (BcephalException e) {
			throw e;
		} catch (Exception e) {
			log.error("Unexpected error while retrieving labels", e);
			String message = getMessageSource().getMessage("unable.to.retrieve.labels", new Object[] {}, locale);
			throw new BcephalException(HttpStatus.INTERNAL_SERVER_ERROR.value(), message);
		}
	}

}
