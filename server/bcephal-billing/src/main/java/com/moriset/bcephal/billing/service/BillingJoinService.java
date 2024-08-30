/**
 * 
 */
package com.moriset.bcephal.billing.service;

import java.util.List;
import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.moriset.bcephal.domain.BillingParameterCodes;
import com.moriset.bcephal.domain.EditorDataFilter;
import com.moriset.bcephal.domain.parameter.Parameter;
import com.moriset.bcephal.domain.parameter.ParameterType;
import com.moriset.bcephal.grid.domain.GrilleType;
import com.moriset.bcephal.grid.domain.Join;
import com.moriset.bcephal.grid.domain.JoinEditorData;
import com.moriset.bcephal.grid.service.JoinService;
import com.moriset.bcephal.repository.ParameterRepository;

import jakarta.servlet.http.HttpSession;

/**
 * @author Joseph Wambo
 *
 */
@Service
public class BillingJoinService extends JoinService {
	
	@Autowired
	ParameterRepository parameterRepository;

	@Override
	public JoinEditorData getEditorData(EditorDataFilter filter, HttpSession session, Locale locale) throws Exception {
		JoinEditorData data = super.getEditorData(filter, session, locale);
		data.setItem(getBillingJoin());	
		return data;
	}
	
	@Override
	protected List<GrilleType> getSmallGridTypes() {
		List<GrilleType> types = super.getSmallGridTypes();
		types.add(GrilleType.BILLING_EVENT_REPOSITORY);
		types.add(GrilleType.CLIENT_REPOSITORY);
		return types;
	}
	
	public Join getBillingJoin() {
		Parameter parameter = parameterRepository.findByCodeAndParameterType(BillingParameterCodes.BILLING_JOIN, ParameterType.JOIN);
		if(parameter != null && parameter.getLongValue() != null) {
			return getById(parameter.getLongValue());
		}
		return null;
	}
	
	@Override
	protected Join getNewItem() {
		Join join = new Join();
		String baseName = "Billing join ";
		int i = 1;
		join.setName(baseName + i);
		while(getByName(join.getName()) != null) {
			i++;
			join.setName(baseName + i);
		}
		return join;
	}
	
}
