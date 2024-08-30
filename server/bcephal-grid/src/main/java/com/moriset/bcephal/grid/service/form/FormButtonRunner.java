package com.moriset.bcephal.grid.service.form;

import java.util.Optional;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.moriset.bcephal.domain.RunModes;
import com.moriset.bcephal.domain.socket.TaskProgressListener;
import com.moriset.bcephal.grid.domain.form.Form;
import com.moriset.bcephal.grid.domain.form.FormButtonActionData;
import com.moriset.bcephal.grid.domain.form.FormModel;
import com.moriset.bcephal.grid.domain.form.FormModelButton;
import com.moriset.bcephal.grid.domain.form.FormModelButtonAction;
import com.moriset.bcephal.grid.repository.form.FormModelButtonRepository;
import com.moriset.bcephal.grid.repository.form.FormModelRepository;
import com.moriset.bcephal.utils.BcephalException;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.servlet.http.HttpSession;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Component
@Data
@Slf4j
@Scope(scopeName = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class FormButtonRunner implements BeanFactoryAware {
	
	FormButtonActionData data;
	private RunModes mode;
	private String projectCode;
	private Long clientId;
	private Long profileId;
	private TaskProgressListener listener;
	
	@PersistenceContext
	EntityManager entityManager;		
	
	@Autowired
	FormService formService;
	
	@Autowired
	FormModelRepository formModelRepository;
	
	@Autowired
	FormModelButtonRepository formModelButtonRepository;
	
	String sessionId;

	String username = "B-CEPHAL";
	
	HttpSession session;
	
	boolean stop;
	
	

	public FormButtonRunner() {
		
	}

	@Override
	public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
		
	}
	
	public void run() {	
		try {		
			if(getListener() != null) {				
				this.getListener().createInfo(this.data.getModelId(), "Run form button");
			}
			Optional<FormModel> model = formModelRepository.findById(this.data.getModelId());
			if(model.isEmpty()) {
				throw new BcephalException("Unkown form model : " + this.data.getModelId());
			}
			Optional<FormModelButton> button = formModelButtonRepository.findById(this.data.getButtonId());
			if(button.isEmpty()) {
				throw new BcephalException("Unkown button : " + this.data.getButtonId());
			}
			this.data.setModel(model.get());
			this.data.setButton(button.get());
			this.data.getButton().sortActions();
			this.data.getModel().getFields().forEach(field -> {
				field.setDataSourceType(this.data.getModel().getDataSourceType());
				field.setDataSourceId(this.data.getModel().getDataSourceId());
			});
			log.debug("Button : {}", this.data.getButton().getName());
			if(getListener() != null) {				
				this.getListener().start(5);
			}
			
			if(getListener() != null) {	
				this.getListener().nextStep(1);
			}
			if(stop) {
				throw new BcephalException("Stopped by user.");
			}
			int count = this.data.getButton().getActions().size();
			if(getListener() != null) {				
				this.getListener().start(count + 3);
				this.getListener().nextStep(1);
			}			
			int current = 0;
			while(current < count) {
				if(stop) {
					throw new BcephalException("Stopped by user.");
				}
				FormModelButtonAction action = this.data.getButton().getActions().get(current++);
				run(action);
				if(stop) {
					throw new BcephalException("Stopped by user.");
				}
				if(getListener() != null) {	
					this.getListener().nextStep(1);
				}				
			}			
			if(stop) {
				throw new BcephalException("Stopped by user.");
			}
			if(getListener() != null) {	
				Number id = (Number)this.data.getForm().getMainData()[this.data.getForm().getMainData().length - 1];
				getListener().getInfo().setId(id != null ? id.longValue() : null);
				this.getListener().end();
			}
		}
		catch (Exception e) {
			log.error("Button : {} : unexpected error", this.data.getButton().getName(), e);
			if(getListener() != null) {	
				this.getListener().error(e.getMessage(), true);
			}
		}
		finally {
			
		}
	}
	
	private void run(FormModelButtonAction action) throws Exception {
		log.debug("Button : {}  -  Action : {} - {} - ({})", this.data.getButton().getName(), action.getPosition() + 1, action.getType(), "");
		
		try {	
			if(action.getType().isValue() || action.getType().isValueScheduler()) {
				if(stop) {
					throw new BcephalException("Stopped by user.");
				}
				Form form = formService.runAction(data.getForm(), data.getModel(), action, clientId, profileId, projectCode);
				if(form != null) {
					data.setForm(form);
				}
			}
			if(action.getType().isScheduler() || action.getType().isValueScheduler()) {
				if(stop) {
					throw new BcephalException("Stopped by user.");
				}
			}
		}
		catch (Exception e) {
			if(e instanceof BcephalException) {
				throw e;
			}
			log.error("Button  -  Action : {} - {} - ({})", data.getButton().getName(), action.getPosition() + 1, action.getType(), "", e);
			throw new BcephalException("Unexpected error... Button  -  Action : {} - {} - ({}) ", data.getButton().getName(), action.getPosition() + 1, action.getType(), "", e);
		}
		finally {
			
		}
	}
		

	public void cancel() {
		this.stop = true;
	}	

}
