package com.moriset.bcephal.dashboard.service;

import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.moriset.bcephal.dashboard.domain.DashboardReportField;
import com.moriset.bcephal.dashboard.repository.DashboardReportFieldPropertiesRepository;
import com.moriset.bcephal.dashboard.repository.DashboardReportFieldRepository;
import com.moriset.bcephal.domain.BrowserData;
import com.moriset.bcephal.service.PersistentService;
import com.moriset.bcephal.utils.BcephalException;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class DashboardReportFieldService extends PersistentService<DashboardReportField, BrowserData> {

	@Autowired
	DashboardReportFieldPropertiesRepository dashboardReportFieldPropertiesRepository;
	
	@Autowired
	DashboardReportFieldRepository dashboardReportFieldRepository;
	
	@Override
	public DashboardReportFieldRepository getRepository() {
		return dashboardReportFieldRepository;
	}

	@Override
	@Transactional
	public DashboardReportField save(DashboardReportField dashboardReportField, Locale locale) {
		log.debug("Try to  Save Dashboard Report Field : {}", dashboardReportField);
		try {
			if (dashboardReportField == null) {
				String message = getMessageSource().getMessage("unable.to.save.null.dashboard.report",
						new Object[] { dashboardReportField }, locale);
				throw new BcephalException(HttpStatus.BAD_REQUEST.value(), message);
			}
			if (!StringUtils.hasLength(dashboardReportField.getName())) {
				String message = getMessageSource().getMessage("unable.to.save.dashboard.report.with.empty.name",
						new String[] { dashboardReportField.getName() }, locale);
				throw new BcephalException(HttpStatus.BAD_REQUEST.value(), message);
			}

			if (dashboardReportField.getProperties() != null) {
				dashboardReportField.setProperties(
						dashboardReportFieldPropertiesRepository.save(dashboardReportField.getProperties()));
			}
			dashboardReportField = dashboardReportFieldRepository.save(dashboardReportField);
			log.debug(" Dashboard Report Field saved : {} ", dashboardReportField.getId());
			return dashboardReportField;
		} catch (BcephalException e) {
			throw e;
		} catch (Exception e) {
			log.error("Unexpected error while save  Dashboard Report Field : {}", dashboardReportField, e);
			String message = getMessageSource().getMessage("unable.to.save.dashboard.report.field", new Object[] { dashboardReportField },
					locale);
			throw new BcephalException(HttpStatus.INTERNAL_SERVER_ERROR.value(), message);
		}
	}

	@Override
	@Transactional
	public void delete(DashboardReportField dashboardReportField) {
		log.debug("Try to delete dashboard report field : {}", dashboardReportField);
		if (dashboardReportField == null || dashboardReportField.getId() == null) {
			return;
		}
		
		if (dashboardReportField.getProperties() != null && dashboardReportField.getProperties().getId() != null) {
			dashboardReportFieldPropertiesRepository.deleteById(dashboardReportField.getProperties().getId());
		}
		dashboardReportFieldRepository.deleteById(dashboardReportField.getId());
		log.debug("dashboard report field successfully to delete : {} ", dashboardReportField);
		return;
	}

}
