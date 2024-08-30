package com.moriset.bcephal.messenger.send.sms;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.HashMap;

import org.apache.activemq.command.ActiveMQMapMessage;
import org.apache.activemq.command.ActiveMQObjectMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.moriset.bcephal.messenger.model.AlarmMessage;
import com.moriset.bcephal.messenger.model.BuildMessage;

import jakarta.jms.BytesMessage;
import jakarta.jms.MapMessage;
import jakarta.jms.Message;
import jakarta.jms.ObjectMessage;
import jakarta.jms.StreamMessage;
import jakarta.jms.TextMessage;

@Component
@ConfigurationProperties(prefix = "bcephal.notification")
public class ReceiveMessageSmsImpl implements BuildMessage {

	private Logger logger;
	private String from;
	private String type;
	private SmsSupplierType smsSupplierType;
	private Sms bulksmsonline;
	private Sms bulksms;

	public SmsSupplierType getSmsSupplierType() {
		return smsSupplierType;
	}

	public void setSmsSupplierType(SmsSupplierType smsSupplierType) {
		this.smsSupplierType = smsSupplierType;
	}

	public Sms getBulksmsonline() {
		return bulksmsonline;
	}

	public void setBulksmsonline(Sms bulksmsonline) {
		this.bulksmsonline = bulksmsonline;
	}

	public Sms getBulksms() {
		return bulksms;
	}

	public void setBulksms(Sms bulksms) {
		this.bulksms = bulksms;
	}

	@Autowired
	BulksmsonlineService bulksmsonlineService;

	@Autowired
	BulkSmsService bulkSmsService;

	public ReceiveMessageSmsImpl() {
		logger = LoggerFactory.getLogger(ReceiveMessageSmsImpl.class);
	}

	public String getFrom() {
		return from;
	}

	public void setFrom(String from) {
		this.from = from;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	@Override
	public void work(Message message) throws Exception {

		if (message instanceof TextMessage) {
			TextMessage txtMsg = (TextMessage) message;
			logger.trace("Received message TEXT from Destination : {}", txtMsg.getText());

			ObjectMapper mapper = new ObjectMapper();
			AlarmMessage msg = mapper.readValue(txtMsg.getText(), AlarmMessage.class);

			if (msg.getTitle() == null) {
				if (smsSupplierType == null || smsSupplierType.isBulkSmsOnline()) {
					Sms sms = bulksmsonline.getCopy(msg.getContacts(), msg.getContent());
					bulksmsonlineService.sendSms(sms);
				} else {
					Sms sms = bulksms.getCopy(msg.getContacts(), msg.getContent());
					bulkSmsService.sendSms(sms);
				}
			}
		} else if (message instanceof StreamMessage) {
			StreamMessage smsg = (StreamMessage) message;
			int count = -1;
			OutputStream output = null;
			BufferedOutputStream bos = null;
			try {
				output = new FileOutputStream(smsg.getStringProperty("FILE.NAME"));
				bos = new BufferedOutputStream(output);
				byte[] buffer = new byte[10 * 1024 * 1024];
				while ((count = smsg.readBytes(buffer)) > 0) {
					bos.write(buffer, 0, count);
				}
				bos.flush();
			} finally {
				if (bos != null) {
					bos.close();
				}
				if (output != null) {
					output.close();
				}
			}
		} else if (message instanceof ObjectMessage) {
			ActiveMQObjectMessage smsgo = (ActiveMQObjectMessage) message;
			Object object = smsgo.getObject();
			logger.trace("Received message OBJECT3 from Destination : {}", object);

		} else if (message instanceof BytesMessage) {
			BytesMessage smsg = BytesMessage.class.cast(message);
			byte[] resultbytes = new byte[Long.valueOf(smsg.getBodyLength()).intValue()];
			smsg.readBytes(resultbytes);
			String values = new String(resultbytes);
			logger.trace("Received message BYTE from Destination : {}", values);
		} else if (message instanceof MapMessage) {
			ActiveMQMapMessage sourceMap = ActiveMQMapMessage.class.cast(message);
			HashMap<String, Object> map = (HashMap<String, Object>) sourceMap.getContentMap();
			logger.trace("Received message BYTE from Destination : {}", map);
		}
	}

}
