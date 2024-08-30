package com.moriset.bcephal.messenger.send.mail.microsoft;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import com.moriset.bcephal.messenger.send.mail.AbstractMailService;
import com.moriset.bcephal.messenger.send.mail.Email;

import jakarta.mail.Address;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.AddressException;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;


@ConfigurationProperties(prefix = "bcephal.notification")
@Component
@Slf4j
@Data
@EqualsAndHashCode(callSuper = false)
public class MicrosoftMailGraphNativeService extends AbstractMailService {

	MicrosoftGraphRestClient microsoftRestClient;

	public MicrosoftMailGraphNativeService(MicrosoftGraphRestClient microsoftRestClient) {
		super();
		this.microsoftRestClient = microsoftRestClient;
	}

	public void sendMail(Email emailmessage) throws AddressException, MessagingException, IOException {
		log.info("try to send email to {}!", Arrays.toString(emailmessage.getAdresseReceipt()));
		log.debug("Properties : {} ", properties);
		log.trace("email : {}, pwd:{} ", emailmessage.getUserMail().getFrom(),
				emailmessage.getUserMail().getPassword());
		try {
			microsoftRestClient.post(microsoftRestClient.manager.getSenderMailUrl(), getBodyMessage(emailmessage),
					true);
		} catch (Exception e) {
			throw new IOException(e);
		}
	}

	private String getBodyMessage(Email emailmessage) throws JSONException, IOException {
		JSONObject body = new JSONObject();
		JSONObject __message = new JSONObject();
		JSONObject __messageCore = new JSONObject();
		__messageCore.put("contentType", "Text");
		__messageCore.put("content", emailmessage.getBody());
		__message.put("body", __messageCore);
		__message.put("subject", emailmessage.getSubject());
		__message.put("toRecipients", getToRecipients(emailmessage));
		__message.put("attachments", getAttachments(emailmessage));
		// __message.put("from", getFrom("gdupuis@finflag.com"));
		body.put("saveToSentItems", true);
		body.put("message", __message);
		return body.toString();
	}

	private List<JSONObject> getToRecipients(Email emailmessage) {
		List<JSONObject> toRecipients = new ArrayList<>(0);
		for (Address item : emailmessage.getAdresseReceipt()) {
			JSONObject value = new JSONObject();
			JSONObject ite = new JSONObject();
			ite.put("address", item.toString());
			value.put("emailAddress", ite);
			toRecipients.add(value);
		}
		return toRecipients;
	}

	
	private List<JSONObject> getAttachments(Email emailmessage) throws IOException {
		List<JSONObject> toRecipients = new ArrayList<>(0);
		if (emailmessage.getFilesId() != null && emailmessage.getFilesId().size() > 0  && getDownloadFileManager() != null) {
			paths = new ArrayList<String>(0);
			attacheEvent = true;
			emailmessage.getFilesId().forEach( fileId ->{
				try {
					paths.add(getDownloadFileManager().downloadFile(fileId));
				} catch (IOException e) {
					attacheEvent = false;
				}
			});
			if(!attacheEvent) {
				throw new IOException("Cannot attache all document file on mail");
			}
			paths.forEach(path -> {
				JSONObject value = new JSONObject();
				value.put("@odata.type", "#microsoft.graph.fileAttachment");
				value.put("name", "attachment.txt");
				value.put("contentType", "text/plain");
				try {
					value.put("contentBytes", ConvertBase64(Files.readAllBytes(Paths.get(path))));
				} catch (JSONException | IOException e) {
					e.printStackTrace();
				}
				toRecipients.add(value);
			});
		}
		return toRecipients;
	}

	private String ConvertBase64(byte[] in) {
		return Base64.getEncoder().encodeToString(in);
	}
}
