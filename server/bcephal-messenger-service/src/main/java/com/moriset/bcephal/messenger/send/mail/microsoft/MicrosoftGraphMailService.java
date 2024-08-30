//package com.moriset.bcephal.messenger.sendEmail.microsoft;
//
//import java.io.IOException;
//import java.util.ArrayList;
//import java.util.Arrays;
//import java.util.Calendar;
//import java.util.List;
//
//import javax.mail.Address;
//import javax.mail.MessagingException;
//import javax.mail.internet.AddressException;
//
//import org.springframework.boot.context.properties.ConfigurationProperties;
//import org.springframework.stereotype.Component;
//
//import com.microsoft.graph.auth.confidentialClient.ClientCredentialProvider;
//import com.microsoft.graph.auth.enums.NationalCloud;
//import com.microsoft.graph.models.extensions.EmailAddress;
//import com.microsoft.graph.models.extensions.IGraphServiceClient;
//import com.microsoft.graph.models.extensions.ItemBody;
//import com.microsoft.graph.models.extensions.Message;
//import com.microsoft.graph.models.extensions.Recipient;
//import com.microsoft.graph.models.generated.BodyType;
//import com.microsoft.graph.requests.extensions.AttachmentCollectionPage;
//import com.microsoft.graph.requests.extensions.AttachmentCollectionResponse;
//import com.microsoft.graph.requests.extensions.GraphServiceClient;
//import com.microsoft.graph.requests.extensions.IAttachmentCollectionRequestBuilder;
//import com.moriset.bcephal.messenger.sendEmail.AbstractMailService;
//import com.moriset.bcephal.messenger.sendEmail.Email;
//
//@ConfigurationProperties(prefix = "bcephal.notification")
//@Component
//public class MicrosoftGraphMailService   extends AbstractMailService {
//	
//	MicrosoftGraphRestClient microsoftRestClient;
//	
//	
//	private final NationalCloud endpoint = NationalCloud.Global;
//    private final List<String> scopes = Arrays.asList("https://graph.microsoft.com/.default");
//    private IGraphServiceClient graphClient;
//	
//	public MicrosoftGraphMailService(MicrosoftGraphRestClient microsoftRestClient) {
//		super();
//		this.microsoftRestClient = microsoftRestClient;
//		final ClientCredentialProvider authProvider = new ClientCredentialProvider(this.microsoftRestClient.manager.getApplicationClient().getId(),
//				this.scopes,
//				this.microsoftRestClient.manager.getApplicationClient().getSecret(),
//				this.microsoftRestClient.manager.getApplicationClient().getDomain(),
//				this.endpoint);
//
//        graphClient = GraphServiceClient.builder()
//        		.authenticationProvider(authProvider)
//                .buildClient();
//        graphClient.setServiceRoot("https://graph.microsoft.com/v1.0");
//	}
//	
//	
//	public void sendMail(Email emailmessage) throws AddressException, MessagingException, IOException {
//		logger.info("try to send email to {}!", Arrays.toString(emailmessage.getAdresseReceipt()));
//		logger.debug("Properties : {} ", properties);
//		logger.debug("email : {}, pwd:{} ", emailmessage.getUserMail().getEmail(),
//				emailmessage.getUserMail().getPassword());
//		try {
//		graphClient.users(this.microsoftRestClient.manager.getEmail()).sendMail(getMessage(emailmessage) , true).buildRequest().post();
//		} catch (Exception e) {
//			throw new IOException(e);
//		}
//		
//	}
//	
//	private Message getMessage(Email emailmessage) {
//		Message message = new Message();
//		message.toRecipients = getToRecipients0(emailmessage);
//		//message.attachments = getAttachments0(emailmessage);
//		message.body = getBody(emailmessage);
//		message.subject = emailmessage.getSubject();
//		message.createdDateTime = Calendar.getInstance();
//		return message;
//	}
//	
//	
//	private ItemBody getBody(Email emailmessage) {
//		ItemBody item = new ItemBody();
//		item.contentType = BodyType.TEXT;
//		item.content = emailmessage.getBody();
//		return item;
//	}
//
//
//
//	private AttachmentCollectionPage getAttachments0(Email emailmessage) {
//		final AttachmentCollectionResponse response = null;
//		final IAttachmentCollectionRequestBuilder builder = null;
//		AttachmentCollectionPage attachmentCollectionPage = new AttachmentCollectionPage(response, builder);
//		if(emailmessage.getFile() != null && !emailmessage.getFile().trim().isEmpty()) {
//			
////			value.put("@odata.type", "#microsoft.graph.fileAttachment");
////			value.put("name", "attachment.txt");
////			value.put("contentType", "text/plain");
////			try {
////				value.put("contentBytes", ConvertBase64(Files.readAllBytes(Paths.get(emailmessage.getFile()))));
////			} catch (JSONException | IOException e) {
////				e.printStackTrace();
////			}
//		}
//		return attachmentCollectionPage;
//	}
//
//
//
//	private List<Recipient> getToRecipients0(Email emailmessage) {
//		List<Recipient> toRecipients = new ArrayList<>(0);
//		for(Address item : emailmessage.getAdresseReceipt()) {
//			Recipient value = new Recipient();
//			EmailAddress address = new EmailAddress();
//			address.address = item.toString();
//			value.emailAddress = address;
//			toRecipients.add(value);
//			
//		}
//		return toRecipients;
//	}
//
//}
