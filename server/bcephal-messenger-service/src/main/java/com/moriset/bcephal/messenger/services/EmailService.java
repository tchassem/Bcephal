/**
 * 
 */
package com.moriset.bcephal.messenger.services;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.moriset.bcephal.messenger.model.AlarmMessageLogToSend;
import com.moriset.bcephal.messenger.send.mail.DownloadFileManager;

import jakarta.annotation.PostConstruct;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.Multipart;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.Session;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeBodyPart;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.internet.MimeMultipart;

/**
 * @author Moriset
 *
 */
@Component
@ConfigurationProperties(prefix = "bcephal.notification")
public class EmailService {

	protected Properties properties;
	protected UserMailProperties userMail;
	
	@Autowired
    private JavaMailSender mailSender;
	
	@Autowired(required=false)
	protected DownloadFileManager downloadFileManager;

	protected boolean attacheEvent = true;
	protected List<String> paths = null;
	
	public Properties getProperties() {
		return properties;
	}

	public void setProperties(Properties properties) {
		this.properties = properties;
	}
	
	public UserMailProperties getUserMail() {
		return userMail;
	}
	
	public void setUserMail(UserMailProperties userMail) {
		this.userMail = userMail;
	}
	
	@PostConstruct
	public void init(){
	    JavaMailSenderImpl ms = (JavaMailSenderImpl) mailSender;
	    Session session = Session.getInstance(properties, new jakarta.mail.Authenticator() {
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(userMail.getUserName(),userMail.getPassword());
			}
		});
	    ms.setSession(session);
	    ms.setDefaultEncoding("UTF-8");
	}
	
    public void sendSimpleMessage(AlarmMessageLogToSend emailmessage) throws Exception {
        MimeMessage message = mailSender.createMimeMessage();
        InternetAddress address = new InternetAddress(userMail.getFrom(), false);
        address.setPersonal(userMail.getDisplayName());
        message.setFrom(address);  
		if(userMail.getReplayToAddress() != null && userMail.getReplayToAddress().length > 0) {
			message.setReplyTo(userMail.getReplayToAddress());
		}
		message.addHeader("Content-type", "text/plain; charset=UTF-8");
		message.addHeader("X-SMTPAPI", emailmessage.getHeaders());
		message.setRecipients(Message.RecipientType.TO, emailmessage.getAdresseReceipt());
		if(StringUtils.hasText(emailmessage.getCcContacts())) {
			message.setRecipients(Message.RecipientType.CC, emailmessage.getCcContacts().replace(";", ","));
		}
		message.setSubject(new String(emailmessage.getTitle().getBytes(StandardCharsets.UTF_8), StandardCharsets.UTF_8),"UTF-8");
		if( emailmessage.getContent() != null) {
			message.setContent(new String(emailmessage.getContent().getBytes(StandardCharsets.UTF_8), StandardCharsets.UTF_8), "text/plain; charset=UTF-8");
		}
		message.setSentDate(new Date());
		
        // attach file
        if (emailmessage.getFilesId() != null && emailmessage.getFilesId().size() > 0 && downloadFileManager != null) {
        	
			MimeBodyPart messageBodyPart = new MimeBodyPart();
			messageBodyPart.addHeader("Content-type", "text/plain; charset=UTF-8");
			if( emailmessage.getContent() != null) {
				messageBodyPart.setContent(new String(emailmessage.getContent().getBytes(StandardCharsets.UTF_8), StandardCharsets.UTF_8), "text/plain; charset=UTF-8");
			}

			Multipart multiPart = new MimeMultipart();
			multiPart.addBodyPart(messageBodyPart);			
			paths = new ArrayList<String>(0);
			emailmessage.getFilesId().forEach( fileId ->{
				try {
					MimeBodyPart attachpart = new MimeBodyPart();
					paths.add(downloadFileManager.downloadFile(fileId));
					attachpart.attachFile(paths.get(paths.size() - 1));
					multiPart.addBodyPart(attachpart);
				} catch (IOException | MessagingException e) {
					attacheEvent = false;
				}
			});
			if(!attacheEvent) {
				throw new IOException("Cannot attache all document file on mail");
			}			
			message.setContent(multiPart);
		}
		mailSender.send(message);
		
    }
    
}
