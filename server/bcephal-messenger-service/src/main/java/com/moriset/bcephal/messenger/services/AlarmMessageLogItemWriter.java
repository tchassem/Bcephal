/**
 * 
 */
package com.moriset.bcephal.messenger.services;

import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import com.moriset.bcephal.messenger.BcephalMessengerServiceApplication;
import com.moriset.bcephal.messenger.model.AlarmMessageLogFail;
import com.moriset.bcephal.messenger.model.AlarmMessageLogSuccess;
import com.moriset.bcephal.messenger.model.AlarmMessageLogToSend;
import com.moriset.bcephal.messenger.model.AlarmMessageStatus;
import com.moriset.bcephal.messenger.repository.AlarmMessageLogFailRepository;
import com.moriset.bcephal.messenger.repository.AlarmMessageLogSuccessRepository;
import com.moriset.bcephal.messenger.repository.AlarmMessageLogToSendRepository;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Moriset
 *
 */
@Slf4j
public class AlarmMessageLogItemWriter implements ItemWriter<AlarmMessageLogToSend> {

	@Autowired
	AlarmMessageLogToSendRepository alarmMessageLogToSendRepository;
	
	@Autowired
	AlarmMessageLogSuccessRepository alarmMessageLogSuccessRepository;
	
	@Autowired
	AlarmMessageLogFailRepository alarmMessageLogFailRepository;
	
	@Autowired
	StatisticsService statisticsService;
	
	@Value("${bcephal.messenger.error.action:NONE}")
	String action;
	
	@Override
	public void write(Chunk<? extends AlarmMessageLogToSend> items) throws Exception {
		log.debug("Try to write {} items", items.size());
		for(AlarmMessageLogToSend message : items) {
			write(message);
		}
		log.debug("{} items writed!", items.size());
	}
	
	private void write(AlarmMessageLogToSend item) throws Exception {
		log.debug("Try to write message : {}", item.getId());
		try {
			boolean isPrensent = alarmMessageLogToSendRepository.findById(item.getId()).isPresent();
			if(isPrensent) {
				if(item.getStatus() == AlarmMessageStatus.SENDED || item.getStatus() == AlarmMessageStatus.SENT) {
					log.debug("Save sent message : {}", item.getId());
					alarmMessageLogSuccessRepository.save(new AlarmMessageLogSuccess(item));
					log.debug("Delete pending message : {}", item.getId());
					alarmMessageLogToSendRepository.deleteById(item.getId());
				}
				else if(item.getStatus() == AlarmMessageStatus.FAIL) {
					log.debug("Save fail message : {}", item.getId());
					alarmMessageLogFailRepository.save(new AlarmMessageLogFail(item));
					log.debug("Delete pending message : {}", item.getId());
					alarmMessageLogToSendRepository.deleteById(item.getId());
				}
				else {
					log.debug("Save pending message : {}", item.getId());
					alarmMessageLogToSendRepository.save(item);
				}
				log.debug("{} writed!", item);
			}
			else {
				log.debug("Message : {} is no longer prensent in pending messages list", item.getId());
			}
		}
		catch (Exception e) {
			String message = "Unable to write message status! System will restart/stop to avoid duplicate mail.";
			message += "\n\nDetails : \n" + e.getMessage();
			message += "\n\n" + e.toString();
			log.error(message, e);
			statisticsService.sendError("B-cephal messenger : Error notification", message);
			if("RESTART".equalsIgnoreCase(action)) {
				log.trace("Try to restart application...");
				BcephalMessengerServiceApplication.restart();
			}
			else if("STOP".equalsIgnoreCase(action)) {
				log.trace("Try to stop application...");
				BcephalMessengerServiceApplication.stop(-1);
			}
		}
	}

	
	
}
