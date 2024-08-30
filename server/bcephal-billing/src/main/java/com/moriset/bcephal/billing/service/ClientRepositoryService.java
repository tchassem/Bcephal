/**
 * 
 */
package com.moriset.bcephal.billing.service;

import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.moriset.bcephal.domain.BillingParameterCodes;
import com.moriset.bcephal.domain.EditorData;
import com.moriset.bcephal.domain.EditorDataFilter;
import com.moriset.bcephal.domain.parameter.Parameter;
import com.moriset.bcephal.domain.parameter.ParameterType;
import com.moriset.bcephal.grid.domain.Grille;
import com.moriset.bcephal.grid.domain.GrilleType;
import com.moriset.bcephal.grid.service.GrilleService;
import com.moriset.bcephal.repository.ParameterRepository;

import jakarta.servlet.http.HttpSession;

/**
 * @author Joseph Wambo
 *
 */
@Service("ClientRepositoryService")
public class ClientRepositoryService extends GrilleService {
	
	@Autowired
	ParameterRepository parameterRepository;

	@Override
	public EditorData<Grille> getEditorData(EditorDataFilter filter, HttpSession session, Locale locale) throws Exception {
		EditorData<Grille> data = new EditorData<>();
		data.setItem(getClientRepository());		
		data.setModels(getInitiationService().getModels(session, locale));
		data.setPeriods(getInitiationService().getPeriods(session, locale));
		data.setMeasures(getInitiationService().getMeasures(session, locale));
		data.setCalendarCategories(getInitiationService().getCalendarsAsNameable(session, locale));
		data.setSpots(getInitiationService().getSpotsAsNameable(session, locale));
		return data;
	}
	
	public Grille getClientRepository() {
		Parameter parameter = parameterRepository.findByCodeAndParameterType(BillingParameterCodes.BILLING_CLIENT_REPOSITORY_GRID, ParameterType.GRID);
		if(parameter != null && parameter.getLongValue() != null) {
			return getById(parameter.getLongValue());
		}
		return null;
	}
	
	@Override
	protected Grille getNewItem() {
		Grille grid = new Grille();
		grid.setType(GrilleType.CLIENT_REPOSITORY);
		String baseName = "Client repository ";
		int i = 1;
		grid.setName(baseName + i);
		while(getByName(grid.getName()) != null) {
			i++;
			grid.setName(baseName + i);
		}
		return grid;
	}
	
	@Override
	protected GrilleType getGrilleType() {		
		return GrilleType.CLIENT_REPOSITORY;
	}

}
