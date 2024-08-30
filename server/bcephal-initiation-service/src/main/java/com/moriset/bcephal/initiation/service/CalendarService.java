/**
 * 
 */
package com.moriset.bcephal.initiation.service;

import java.util.List;
import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.moriset.bcephal.domain.BrowserData;
import com.moriset.bcephal.domain.EditorData;
import com.moriset.bcephal.domain.EditorDataFilter;
import com.moriset.bcephal.domain.ListChangeHandler;
import com.moriset.bcephal.initiation.domain.CalendarCategory;
import com.moriset.bcephal.initiation.domain.CalendarDay;
import com.moriset.bcephal.initiation.repository.CalendarDayRepository;
import com.moriset.bcephal.initiation.repository.CalendarRepository;
import com.moriset.bcephal.service.PersistentService;
import com.moriset.bcephal.utils.BcephalException;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Joseph Wambo
 *
 */
@Service
@Slf4j
public class CalendarService extends PersistentService<CalendarCategory, BrowserData> {

	@Autowired
	CalendarRepository calendarRepository;

	@Autowired
	CalendarDayRepository dayRepository;

	@Override
	public CalendarRepository getRepository() {
		return calendarRepository;
	}

	public EditorData<CalendarCategory> getEditorData(EditorDataFilter filter, Locale locale) {
		EditorData<CalendarCategory> data = new EditorData<CalendarCategory>();
		data.setItem(new CalendarCategory());
		data.getItem().getChildren().setOriginalList(getCalendars(locale));
		return data;
	}

	public List<CalendarCategory> getCalendars(java.util.Locale locale) {
		log.debug("Try to  retrieve Calendars.");
		if (getRepository() == null) {
			return null;
		}
		return getRepository().findAll();
	}

	@Transactional
	public CalendarCategory saveRoot(CalendarCategory calendar, Locale locale) {
		log.debug("Try to  Save calendar : {}", calendar);
		try {
			if (calendar == null) {
				String message = messageSource.getMessage("unable.to.save.null.calendar", new Object[] { "" }, locale);
				throw new BcephalException(message);
			}
			List<CalendarCategory> newItems = calendar.getChildren().getNewItems();
			List<CalendarCategory> updatedItems = calendar.getChildren().getUpdatedItems();
			List<CalendarCategory> deletedItems = calendar.getChildren().getDeletedItems();
			if (newItems != null) {
				newItems.forEach(item -> {
					save(item, locale);
				});
			}
			if (updatedItems != null) {
				updatedItems.forEach(item -> {
					save(item, locale);
				});
			}
			if (deletedItems != null) {
				deletedItems.forEach(item -> {
					delete(item, locale);
				});
			}

			ListChangeHandler<CalendarDay> days = calendar.getDayListChangeHandler();
			calendar = super.save(calendar, locale);
			saveDays(calendar, locale, days);
			log.debug("Calendar successfully saved : {} ", calendar);
			return calendar;
		} catch (BcephalException e) {
			throw e;
		} catch (Exception e) {
			log.error("Unexpected error while save calendar : {}", calendar, e);
			String message = messageSource.getMessage("unable.to.save.model", new Object[] { calendar }, locale);
			throw new BcephalException(HttpStatus.INTERNAL_SERVER_ERROR.value(), message);
		}
	}

	public CalendarCategory save(CalendarCategory calendar, Locale locale) {
		log.debug("Try to  Save calendar : {}", calendar);
		try {
			if (calendar == null) {
				String message = messageSource.getMessage("unable.to.save.null.calendar", new Object[] { "" }, locale);
				throw new BcephalException(message);
			}
			if (!StringUtils.hasText(calendar.getName())) {
				String message = messageSource.getMessage("unable.to.save.calendar.with.empty.name",
						new String[] { calendar.getName() }, locale);
				throw new BcephalException(message);
			}
			ListChangeHandler<CalendarDay> days = calendar.getDayListChangeHandler();
			calendar = super.save(calendar, locale);
			saveDays(calendar, locale, days);
			log.debug("Calendar successfully saved : {} ", calendar);
			return calendar;
		} catch (BcephalException e) {
			throw e;
		} catch (Exception e) {
			log.error("Unexpected error while save calendar : {}", calendar, e);
			String message = messageSource.getMessage("unable.to.save.model", new Object[] { calendar }, locale);
			throw new BcephalException(HttpStatus.INTERNAL_SERVER_ERROR.value(), message);
		}
	}

	public void delete(CalendarCategory calendar, Locale locale) {
		log.debug("Try to  delete calendar : {}", calendar);
		try {
			if (calendar == null || calendar.getId() == null) {
				String message = messageSource.getMessage("unable.to.delete.null.calendar.name",
						new Object[] { calendar }, locale);
				throw new BcephalException(message);
			}
			calendar.getDayListChangeHandler().getItems().forEach(item -> {
				dayRepository.delete(item);
			});
			calendar.getDayListChangeHandler().getDeletedItems().forEach(item -> {
				dayRepository.delete(item);
			});
			if (calendar.getId() != null) {
				deleteById(calendar.getId());
			}

			log.debug("Calendar successfully to delete : {} ", calendar);
			return;
		} catch (BcephalException e) {
			throw e;
		} catch (Exception e) {
			log.error("Unexpected error while delete calendar : {}", calendar, e);
			String message = messageSource.getMessage("unable.to.delete.calendar", new Object[] { calendar }, locale);
			throw new BcephalException(HttpStatus.INTERNAL_SERVER_ERROR.value(), message);
		}
	}

	private void saveDays(CalendarCategory calendar, Locale locale, ListChangeHandler<CalendarDay> listHandler) {
		List<CalendarDay> newItems = listHandler.getNewItems();
		List<CalendarDay> updatedItems = listHandler.getUpdatedItems();
		List<CalendarDay> deletedItems = listHandler.getDeletedItems();
		CalendarCategory id = calendar;
		if (newItems != null) {
			newItems.forEach(item -> {
				item.setCategory(id);
				dayRepository.save(item);
			});
		}
		if (updatedItems != null) {
			updatedItems.forEach(item -> {
				item.setCategory(id);
				dayRepository.save(item);
			});
		}
		if (deletedItems != null) {
			deletedItems.forEach(item -> {
				dayRepository.delete(item);
			});
		}
	}

}
