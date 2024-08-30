package com.moriset.bcephal.messenger.send.mail;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import org.springframework.beans.factory.annotation.Autowired;

import com.moriset.bcephal.messenger.properties.User;
import com.moriset.bcephal.messenger.send.mail.grid.api.LogApiProperties;

import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.Multipart;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.AddressException;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeBodyPart;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.internet.MimeMultipart;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Data
public abstract class AbstractMailService implements IMailService{

	protected Properties properties;
	
	protected ProviderName provider;	
	
	private User userMail;
	private Session session_;
	private Transport transport_;
	
	private Boolean connected;
	
	


	@Autowired
	LogApiProperties logApiProperties;
	
	
	protected DownloadFileManager downloadFileManager;
	
	public AbstractMailService() {
		provider = ProviderName.system;
	}
	
	
	public enum ProviderName{
		system("system"),microsoft("microsoft"),grid_api("grid_api");
		
		private String code;

		private ProviderName(String code) {
			this.code = code;
		}
		public String getCode() {
			return code;
		}

		@Override
		public String toString() {
			return this.code;
		}
	}
	
	public ProviderName getProvider() {
		return provider;
	}
	
	public void setProvider(ProviderName provider) {
		this.provider = provider;
	}
	
	public void setProvider(String provider) {
		if(provider != null && provider.trim().isEmpty()) {
			this.provider = ProviderName.valueOf(provider.trim());
		}
	}
	

	protected boolean attacheEvent = true;
	protected List<String> paths = null;
	
	public void sendMail(Email emailmessage) throws AddressException, MessagingException, IOException {
		if(getConnected()) {
			sendConnectedMail(emailmessage);
		}else {
			sendMailNative(emailmessage);
		}
	}
	
	public void sendMailNative(Email emailmessage) throws AddressException, MessagingException, IOException {
		log.info("try to send email to {}!", Arrays.toString(emailmessage.getAdresseReceipt()));
		log.debug("Properties : {} ", properties);
		log.debug("email : {}, pwd:{}, key: ", emailmessage.getUserMail().getFrom(),
				emailmessage.getUserMail().getPassword(),emailmessage.getUserMail().getPassword());
		
		Session session = Session.getInstance(properties, new jakarta.mail.Authenticator() {
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(emailmessage.getUserMail().getUserName(),
						emailmessage.getUserMail().getPassword());
			}
		});

		Message msg = new MimeMessage(session);
		msg.setFrom(new InternetAddress(emailmessage.getUserMail().getFrom(), false));
		
		if(emailmessage.getUserMail().getReplayToAddress() != null && emailmessage.getUserMail().getReplayToAddress().length > 0) {
			msg.setReplyTo(emailmessage.getUserMail().getReplayToAddress());
		}
		msg.addHeader("Content-type", "text/plain; charset=UTF-8");
		msg.setRecipients(Message.RecipientType.TO, emailmessage.getAdresseReceipt());
		((MimeMessage)msg).setSubject(new String(emailmessage.getSubject().getBytes(StandardCharsets.UTF_8), StandardCharsets.UTF_8),"UTF-8");
		((MimeMessage)msg).setContent(new String(emailmessage.getBody().getBytes(StandardCharsets.UTF_8), StandardCharsets.UTF_8), "text/plain; charset=UTF-8");
		msg.setSentDate(new Date());
		paths = null;
		if (emailmessage.getFilesId() != null && emailmessage.getFilesId().size() > 0 && getDownloadFileManager() != null) {
			MimeBodyPart messageBodyPart = new MimeBodyPart();
			messageBodyPart.addHeader("Content-type", "text/plain; charset=UTF-8");
			messageBodyPart.setContent(new String(emailmessage.getBody().getBytes(StandardCharsets.UTF_8), StandardCharsets.UTF_8), "text/plain; charset=UTF-8");

			MimeMultipart multiPart = new MimeMultipart();
			multiPart.addBodyPart(messageBodyPart);
			
			attacheEvent = true;
			paths = new ArrayList<String>(0);
			emailmessage.getFilesId().forEach( fileId ->{
				try {
					MimeBodyPart attachpart = new MimeBodyPart();
					paths.add(getDownloadFileManager().downloadFile(fileId));
					attachpart.attachFile(paths.get(paths.size() - 1));
					multiPart.addBodyPart(attachpart);
				} catch (IOException | MessagingException e) {
					attacheEvent = false;
				}
			});
			if(!attacheEvent) {
				throw new IOException("Cannot attache all document file on mail");
			}
			
			msg.setContent(multiPart);
		}
		try {
		Transport.send(msg);
		//writeLogMessage(emailmessage.getSubject(), new Date(), 200);
		}catch (MessagingException e) {
			log.info("email sent error: {}", e);
			//writeLogMessage(emailmessage.getSubject(), new Date(), 0);
			throw e;
		}
		if(paths != null) {
			deleteAttachFile(paths);
		}
		log.info("email sent successfully: {}", emailmessage);
	}

	private void deleteAttachFile(final List<String> paths){
		new Thread() {
			@Override
			public void run() {
				if(paths != null) {
					paths.forEach(filePath -> {
						Paths.get(filePath).toFile().delete();
					});
				}
			}
			}.start();
	}
	
//	private void writeLogMessage(String invoiceNumber, Date date, int status ) {
//		if(status == 200 || status == 201) {
//			log2.info(String.format("%s; %s; %s;", invoiceNumber,date,"SENDED"));
//		}else {
//			log2.info(String.format("%s; %s; %s;", invoiceNumber,date,"FAILED"));
//		}
//	}
	
//	public org.apache.log4j.Logger getLogger() {
//		org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(AbstractMailService.class);
//		org.apache.log4j.PatternLayout layout2 = new org.apache.log4j.PatternLayout(); 
//		DailyRollingFileAppender dRollingFile = null;
//		    try {
//		        dRollingFile = new DailyRollingFileAppender(layout2, Paths.get(String.format("%s/messenger-log/", logApiProperties.getLogPath()),"request-status.log").toString(), "yyyy-MM-dd-HH-mm-ss");
//		        dRollingFile.setEncoding("UTF-8");
//		        dRollingFile.setBufferSize(1024*1024);		        
//		        logger.addAppender(dRollingFile);
//		        logger.setLevel((org.apache.log4j.Level) org.apache.log4j.Level.INFO);
//		    }
//		    catch(IOException e) {
//		        e.printStackTrace();
//		        logger.error("Printing ERROR Statements",e);
//		    }
//		    return logger;
//	}
//	public void initLog() {
//		log2 = getLogger();
//	}
	
	private void getTransport() throws AddressException, MessagingException, IOException {
		log.debug("email : {}, pwd:{}, key: ", userMail.getEmail(),	userMail.getPassword(), userMail.getKey());	
		if(session_ == null) {
			session_ = Session.getInstance(properties, null);	
		}
		if(transport_ == null && session_ != null) {
			transport_ = session_.getTransport("smtp");
	        transport_.connect(getServer(), userMail.getUserName(), userMail.getPassword());
	        addRuntimeShutdownHook(transport_);
		}else {
			if (transport_ != null && !transport_.isConnected()) {
				transport_.connect(getServer(), userMail.getUserName(), userMail.getPassword());
			}
		}
		if (transport_ == null) {
			throw new IOException("transport_ is null");
		}
	}
	
	private String getServer() {
		return properties.getProperty("mail.smtp.host");
	}
	
	private static void addRuntimeShutdownHook(final Transport transport_) {
		Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
			@Override
			public void run() {
				if (transport_.isConnected()) {
					try {
						transport_.close();
					} catch (Exception e) {
						System.out.println("Error while stopping Transport smtp: " + e.getMessage());
					}
				}
			}
		}));
	}
	
	private void sendConnectedMail(Email emailmessage) throws AddressException, MessagingException, IOException {
		getTransport();
		log.info("try to send email to {}!", Arrays.toString(emailmessage.getAdresseReceipt()));
		log.debug("Properties : {} ", properties);
		Message msg = new MimeMessage(session_);
		msg.setFrom(new InternetAddress(emailmessage.getUserMail().getFrom(), false));
		
		if(emailmessage.getUserMail().getReplayToAddress() != null && emailmessage.getUserMail().getReplayToAddress().length > 0) {
			msg.setReplyTo(emailmessage.getUserMail().getReplayToAddress());
		}
		msg.addHeader("Content-type", "text/plain; charset=UTF-8");
		msg.setRecipients(Message.RecipientType.TO, emailmessage.getAdresseReceipt());
		((MimeMessage)msg).setSubject(new String(emailmessage.getSubject().getBytes(StandardCharsets.UTF_8), StandardCharsets.UTF_8),"UTF-8");
		((MimeMessage)msg).setContent(new String(emailmessage.getBody().getBytes(StandardCharsets.UTF_8), StandardCharsets.UTF_8), "text/plain; charset=UTF-8");
		msg.setSentDate(new Date());
		paths = null;
		if (emailmessage.getFilesId() != null && emailmessage.getFilesId().size() > 0 && getDownloadFileManager() != null) {
			MimeBodyPart messageBodyPart = new MimeBodyPart();
			messageBodyPart.addHeader("Content-type", "text/plain; charset=UTF-8");
			messageBodyPart.setContent(new String(emailmessage.getBody().getBytes(StandardCharsets.UTF_8), StandardCharsets.UTF_8), "text/plain; charset=UTF-8");

			Multipart multiPart = new MimeMultipart();
			multiPart.addBodyPart(messageBodyPart);
			
			attacheEvent = true;
			paths = new ArrayList<String>(0);
			emailmessage.getFilesId().forEach( fileId ->{
				try {
					MimeBodyPart attachpart = new MimeBodyPart();
					paths.add(getDownloadFileManager().downloadFile(fileId));
					attachpart.attachFile(paths.get(paths.size() - 1));
					multiPart.addBodyPart(attachpart);
				} catch (IOException | MessagingException e) {
					attacheEvent = false;
				}
			});
			if(!attacheEvent) {
				throw new IOException("Cannot attache all document file on mail");
			}
			msg.setContent(multiPart);
		}
		try {
			msg.saveChanges();
			transport_.sendMessage(msg, msg.getAllRecipients());
			//writeLogMessage(emailmessage.getSubject(), new Date(), 200);
		}catch (MessagingException e) {
			log.info("email sent error: {}", e);
			//writeLogMessage(emailmessage.getSubject(), new Date(), 0);
			throw e;
		}
		if(paths != null) {
			deleteAttachFile(paths);
		}
		log.info("email sent successfully: {}", emailmessage);
	}


}
