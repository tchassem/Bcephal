package com.moriset.bcephal.billing.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.moriset.bcephal.billing.service.batch.Party;
import com.moriset.bcephal.domain.BillingParameterCodes;
import com.moriset.bcephal.domain.BrowserDataPage;
import com.moriset.bcephal.domain.EditorData;
import com.moriset.bcephal.domain.EditorDataFilter;
import com.moriset.bcephal.domain.Nameable;
import com.moriset.bcephal.domain.parameter.Parameter;
import com.moriset.bcephal.domain.parameter.ParameterType;
import com.moriset.bcephal.grid.domain.GrilleColumnCategory;
import com.moriset.bcephal.grid.domain.MaterializedGrid;
import com.moriset.bcephal.grid.domain.MaterializedGridColumn;
import com.moriset.bcephal.grid.domain.MaterializedGridDataFilter;
import com.moriset.bcephal.grid.service.MaterializedGridService;
import com.moriset.bcephal.repository.ParameterRepository;

import jakarta.servlet.http.HttpSession;

@Service("BillingCompanyRepositoryService")
public class BillingCompanyRepositoryService extends MaterializedGridService {

	@Autowired
	ParameterRepository parameterRepository;

	@Override
	public EditorData<MaterializedGrid> getEditorData(EditorDataFilter filter, HttpSession session, Locale locale) throws Exception {
		EditorData<MaterializedGrid> data = new EditorData<>();
		data.setItem(getBillingCompanyRepository());		
		data.setModels(getInitiationService().getModels(session, locale));
		data.setPeriods(getInitiationService().getPeriods(session, locale));
		data.setMeasures(getInitiationService().getMeasures(session, locale));
		data.setCalendarCategories(getInitiationService().getCalendarsAsNameable(session, locale));
		data.setSpots(getInitiationService().getSpotsAsNameable(session, locale));
		return data;
	}
	
	public MaterializedGrid getBillingCompanyRepository() {
		Parameter parameter = parameterRepository.findByCodeAndParameterType(BillingParameterCodes.BILLING_COMPANY_REPOSITORY_GRID, ParameterType.MAT_GRID);
		if(parameter != null && parameter.getLongValue() != null) {
			return getById(parameter.getLongValue());
		}
		return null;
	}
	
	
	public List<Nameable> getCompanies() {
		List<Nameable> companies = new ArrayList<>();
		MaterializedGrid grid = getBillingCompanyRepository();
		if(grid != null) {
			MaterializedGridColumn nameColumn = grid.getColumnByRole(GrilleColumnCategory.BILLING_ROLE_NAME);			
			MaterializedGridDataFilter filter = new MaterializedGridDataFilter();
			filter.setGrid(grid);
			filter.setAllowRowCounting(false);
			filter.setPage(0);
			filter.setPageSize(100);
			filter.setShowAll(true);
			BrowserDataPage<Object[]> page = searchRows(filter, null);
			for(Object[] data : page.getItems()) {
				Number id = (Number)data[data.length - 1];
				String name = nameColumn != null ? (String)data[nameColumn.getPosition()] : null;
				companies.add(new Nameable(id.longValue(), name));
			}
		}
		return companies;
	}
	
	public Party getCompany(String code) {
		Party company = new Party();
		MaterializedGrid grid = getBillingCompanyRepository();
		if(grid != null && StringUtils.hasText(code)) {	
			Long id = Long.valueOf(code);
			MaterializedGridDataFilter filter = new MaterializedGridDataFilter();
			filter.setIds(new ArrayList<>());
			filter.getIds().add(id);
			filter.setGrid(grid);
			filter.setAllowRowCounting(false);
			filter.setPage(0);
			filter.setPageSize(1);
			filter.setShowAll(false);
			BrowserDataPage<Object[]> page = searchRows(filter, null);
			if(page.getItems().size() > 0) {
				Object[] data = page.getItems().get(0);
				int dataLength = data.length;
				MaterializedGridColumn column = grid.getColumnByRole(GrilleColumnCategory.BILLING_ROLE_ID);
				company.number = column != null && dataLength > column.getPosition() ? (String)data[column.getPosition()] : null;
				
				column = grid.getColumnByRole(GrilleColumnCategory.BILLING_ROLE_NAME);
				company.name = column != null && dataLength > column.getPosition() ? (String)data[column.getPosition()] : null;
				
				column = grid.getColumnByRole(GrilleColumnCategory.BILLING_VAT_NBR);
				company.vatNumber = column != null && dataLength > column.getPosition() ? (String)data[column.getPosition()] : null;
				
				column = grid.getColumnByRole(GrilleColumnCategory.BILLING_LEGAL_FORM);
				company.legalForm = column != null && dataLength > column.getPosition() ? (String)data[column.getPosition()] : null;
				
				column = grid.getColumnByRole(GrilleColumnCategory.BILLING_LANGUAGE);
				company.language = column != null && dataLength > column.getPosition() ? (String)data[column.getPosition()] : null;
				
				column = grid.getColumnByRole(GrilleColumnCategory.BILLING_STREET);
				company.street = column != null && dataLength > column.getPosition() ? (String)data[column.getPosition()] : null;
				
				column = grid.getColumnByRole(GrilleColumnCategory.BILLING_POSTAL_CODE);
				company.postalcode = column != null && dataLength > column.getPosition() ? (String)data[column.getPosition()] : null;
				
				column = grid.getColumnByRole(GrilleColumnCategory.BILLING_CITY);
				company.city = column != null && dataLength > column.getPosition() ? (String)data[column.getPosition()] : null;
				
				column = grid.getColumnByRole(GrilleColumnCategory.BILLING_COUNTRY);
				company.country = column != null && dataLength > column.getPosition() ? (String)data[column.getPosition()] : null;
				
				column = grid.getColumnByRole(GrilleColumnCategory.BILLING_EMAIL);
				company.email = column != null && dataLength > column.getPosition() ? (String)data[column.getPosition()] : null;
				
				column = grid.getColumnByRole(GrilleColumnCategory.BILLING_PHONE);
				company.phone = column != null && dataLength > column.getPosition() ? (String)data[column.getPosition()] : null;
				
//				column = grid.getColumnByRole(GrilleColumnCategory.BILLING_PHONE);
//				company.doingBusinessAs = column != null && dataLength > column.getPosition() ? (String)data[column.getPosition()] : null;
//				
			}
		}
		return company;
	}
	
	
}
