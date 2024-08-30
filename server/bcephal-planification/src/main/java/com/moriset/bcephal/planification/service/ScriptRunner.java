package com.moriset.bcephal.planification.service;

import java.sql.Timestamp;

import org.hibernate.exception.DataException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.moriset.bcephal.domain.RunModes;
import com.moriset.bcephal.domain.RunStatus;
import com.moriset.bcephal.domain.socket.TaskProgressListener;
import com.moriset.bcephal.planification.domain.script.Script;
import com.moriset.bcephal.planification.domain.script.ScriptLog;
import com.moriset.bcephal.planification.repository.ScriptLogRepository;
import com.moriset.bcephal.utils.BcephalException;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Component
@Getter
@Setter
@NoArgsConstructor
@Slf4j
public class ScriptRunner {

	private Script script;
	private ScriptLog scriptLog;
	private TaskProgressListener listener;
	
	private boolean stopped;

	String operationCode;
	String username = "B-CEPHAL";
	RunModes mode;
	
	
	@PersistenceContext
	EntityManager entityManager;
	
	@Autowired
	ScriptLogRepository logRepository;
		
	
	public void run() {		
		log.debug("Try to run Script : {}", script.getName());		
		try {	
			scriptLog = initLog();				
			if(listener != null) {
				listener.start(3);
			}
			if(listener != null) {
				listener.nextStep(1);
			}
			runScript();
			endLog(null);			
		} 
		catch (Exception e) {
			log.error("Script : {}		Run fail !", script.getName(), e);
			String message = "Unable to run script. \nUnexpected error!";
			if (e instanceof BcephalException) {
				message = e.getMessage();
			}
			else if(e instanceof DataException && ((DataException)e).getSQLException() != null) {
				message = ((DataException)e).getSQLException().getMessage();
			}
			else {
				message = "Unable to run script. " + e.getMessage();
			}			
			endLog(message);
			if (getListener() != null) {
				getListener().error(message, true);
			}
		} finally {

		}
	}

	@Transactional(propagation = Propagation.REQUIRES_NEW)
	private void runScript() {
		Query query = entityManager.createNativeQuery(script.getScript());
		query.executeUpdate();
	}
	
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	private ScriptLog initLog() {
		ScriptLog scriptLog = new ScriptLog();
		scriptLog.setScriptId(script.getId());
		scriptLog.setScriptName(script.getName());
		scriptLog.setUsername(username);
		scriptLog.setMode(mode != null ? mode : RunModes.A);
		scriptLog.setStatus(RunStatus.IN_PROGRESS);
		scriptLog.setStartDate(new Timestamp(System.currentTimeMillis()));
		scriptLog = logRepository.save(scriptLog);
		return scriptLog;
	}

	@Transactional(propagation = Propagation.REQUIRES_NEW)
	private void endLog(String message) {
		try {
			scriptLog.setMessage(message);
			scriptLog.setStatus(RunStatus.ENDED);
			if (StringUtils.hasText(message)) {
				scriptLog.setStatus(RunStatus.ERROR);
			}
			if(scriptLog.getStartDate() == null) {
				scriptLog.setStartDate(new Timestamp(System.currentTimeMillis()));
			}
			scriptLog.setEndDate(new Timestamp(System.currentTimeMillis()));
			logRepository.save(scriptLog);
		}
		catch (Exception e) {
			log.error("Script : {}		Unable to save log.", script.getName(), e);
		}
	}

	
}
