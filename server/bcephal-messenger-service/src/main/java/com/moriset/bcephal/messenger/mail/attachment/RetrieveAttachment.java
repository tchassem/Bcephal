/**
 * 
 */
package com.moriset.bcephal.messenger.mail.attachment;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import org.springframework.beans.factory.annotation.Value;

import jakarta.mail.Flags;
import jakarta.mail.Folder;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.Multipart;
import jakarta.mail.Part;
import jakarta.mail.Session;
import jakarta.mail.Store;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeBodyPart;
import jakarta.mail.search.AndTerm;
import jakarta.mail.search.ComparisonTerm;
import jakarta.mail.search.FlagTerm;
import jakarta.mail.search.FromTerm;
import jakarta.mail.search.ReceivedDateTerm;
import jakarta.mail.search.SearchTerm;

/**
 * @author EMMENI Emmanuel
 *
 */
public class RetrieveAttachment {

	private final static String IMAP_SERVER = "imap.gmail.com";
	private final static String IMAP_PORT = "993";
	private final static String PROVIDER = "imap";

	@Value("${moriset.user.email}")
	private String email;

	@Value("${moriset.user.pwd}")
	private String pwd;

	@Value("${moriset.user.sender}")
	private String sender;

	@Value("${moriset.emails.attachments}")
	private String attachmentsDir;

	public RetrieveAttachment() {
		
	}
	
	private Properties getProperties(String protocol, String host, String port) {
		
        Properties properties = new Properties();
 
        properties.put(String.format("mail.%s.host", protocol), host);
        properties.put(String.format("mail.%s.port", protocol), port);
 
        properties.setProperty(String.format("mail.%s.socketFactory.class", protocol), "javax.net.ssl.SSLSocketFactory");
        properties.setProperty(String.format("mail.%s.socketFactory.fallback", protocol), "false");
        properties.setProperty(String.format("mail.%s.socketFactory.port", protocol), String.valueOf(port));
 
        return properties;
    }
 
    /**
	 * @throws Exception 
	 * 
	 */
	public void loadAttachments() throws Exception {
		
		if (!new File(attachmentsDir).exists()) {
			new File(attachmentsDir).mkdirs();
		}

		Properties properties = getProperties(PROVIDER, IMAP_SERVER, IMAP_PORT);

		Session session = Session.getDefaultInstance(properties);

		Store store = session.getStore(PROVIDER);
		store.connect(email, pwd);

		Folder folder = store.getFolder("inbox");
		folder.open(Folder.READ_WRITE);
		
		SearchTerm unseenFlagTerm = new FlagTerm(new Flags(Flags.Flag.SEEN), false);
        SearchTerm fromTerm = new FromTerm(new InternetAddress(sender));
        SearchTerm newerThan = new ReceivedDateTerm(ComparisonTerm.GE, new Date());
        SearchTerm[] filters = { unseenFlagTerm, fromTerm, newerThan };
        SearchTerm searchTerm = new AndTerm(filters);

		Message[] message = folder.search(searchTerm);

		for (int j = 0; j < message.length; j++) {			
			downloadAttachments(message[j]);
		}
	  
		folder.close(true);
		store.close();  
	}
	
	public List<String> downloadAttachments(Message message) throws IOException, MessagingException {
		
	    List<String> downloadedAttachments = new ArrayList<String>();
	    Multipart multiPart = (Multipart) message.getContent(); // message.setFlag(Flags.Flag.SEEN, true); Already set by getContent or getInputStream 
	    
	    int numberOfParts = multiPart.getCount();
	    for (int partCount = 0; partCount < numberOfParts; partCount++) {
	        MimeBodyPart part = (MimeBodyPart) multiPart.getBodyPart(partCount);
	        if (Part.ATTACHMENT.equalsIgnoreCase(part.getDisposition())) {
	            String file = part.getFileName();
	            part.saveFile(attachmentsDir + File.separator + part.getFileName());
	            downloadedAttachments.add(file);
	        }
	    }
	    return downloadedAttachments;
	} 

}
