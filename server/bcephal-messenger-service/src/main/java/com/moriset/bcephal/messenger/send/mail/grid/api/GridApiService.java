//package com.moriset.bcephal.messenger.sendEmail.gridApi;
//
//import java.io.IOException;
//import java.net.URI;
//import java.nio.charset.StandardCharsets;
//import java.nio.file.Paths;
//import java.util.Collections;
//import java.util.Date;
//
//import javax.mail.MessagingException;
//import javax.mail.internet.AddressException;
//
//import org.apache.log4j.DailyRollingFileAppender;
//import org.apache.log4j.FileAppender;
//import org.apache.log4j.RollingFileAppender;
//import org.apache.log4j.SimpleLayout;
//import org.apache.logging.log4j.Level;
//import org.apache.logging.log4j.LogManager;
//import org.apache.logging.log4j.Logger;
//import org.apache.logging.log4j.core.LoggerContext;
//import org.apache.logging.log4j.core.appender.ConsoleAppender;
//import org.apache.logging.log4j.core.config.Configurator;
//import org.apache.logging.log4j.core.config.builder.api.AppenderComponentBuilder;
//import org.apache.logging.log4j.core.config.builder.api.ComponentBuilder;
//import org.apache.logging.log4j.core.config.builder.api.ConfigurationBuilder;
//import org.apache.logging.log4j.core.config.builder.api.ConfigurationBuilderFactory;
//import org.apache.logging.log4j.core.config.builder.api.LayoutComponentBuilder;
//import org.apache.logging.log4j.core.config.builder.api.RootLoggerComponentBuilder;
//import org.apache.logging.log4j.core.config.builder.impl.BuiltConfiguration;
//import org.apache.logging.log4j.core.layout.PatternLayout;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Qualifier;
//import org.springframework.boot.context.properties.ConfigurationProperties;
//import org.springframework.http.HttpHeaders;
//import org.springframework.http.HttpMethod;
//import org.springframework.http.MediaType;
//import org.springframework.http.RequestEntity;
//import org.springframework.http.ResponseEntity;
//import org.springframework.stereotype.Component;
//import org.springframework.util.MultiValueMap;
//import org.springframework.web.client.RestTemplate;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.moriset.bcephal.messenger.sendEmail.AbstractMailService;
//import com.moriset.bcephal.messenger.sendEmail.Email;
//
//@ConfigurationProperties(prefix = "bcephal.notification")
//@Component
//public class GridApiService extends AbstractMailService {
//	public static final String RESTNAME = "GridApiService_";
//	@Autowired
//	@Qualifier(RESTNAME)
//	protected RestTemplate restTemplate;
//	
//	@Autowired
//	GridApiProperties gridApiProperties;
//	
//	@Autowired
//	ObjectMapper mapper;
//	
//	
//	private org.slf4j.Logger logger = LoggerFactory.getLogger(GridApiService.class);
//	private org.apache.log4j.Logger log;
//	
//	
//	public void sendMail(Email emailmessage) throws AddressException, MessagingException, IOException {
//		Personalizations mail = new Personalizations();
//		mail.content.type = "text/plain";
//		mail.content.value = emailmessage.getBody();
//		mail.from.email = emailmessage.getUserMail().getEmail();
//		mail.from.name = emailmessage.getUserMail().getEmail();
//		int length = emailmessage.getAdresseReceipt().length;
//		while(length > 0) {
//			EmailInfos info = new EmailInfos();
//			info.email = emailmessage.getAdresseReceipt()[length - 1].toString();
//			info.name = emailmessage.getAdresseReceipt()[length - 1].toString();
//			mail.to.add(info);
//			length--;
//		}
//		try {
//			ResponseEntity<String> response = post(gridApiProperties.getUrl(), "{\"personalizations\":["+ mapper.writeValueAsString(mail) + "]}");
//			writeLogMessage(emailmessage.getSubject(), new Date(), response.getStatusCodeValue());
//		} catch (Exception e) {
//			e.printStackTrace();
//			//logger.error(e.getMessage());
//			writeLogMessage(emailmessage.getSubject(), new Date(), 0);
//		}
//	}
//	
//	private MultiValueMap<String, String> getAuthheaders() throws Exception {
//		HttpHeaders headers = new HttpHeaders();
//		headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
//		headers.setContentType(MediaType.APPLICATION_JSON);
//		headers.setBearerAuth(gridApiProperties.getKey());
//		return headers;
//	}
//
//
//	protected ResponseEntity<String> post(String path, String body) throws Exception {
//		MultiValueMap<String, String> headers = getAuthheaders();
//		RequestEntity<String> requestEntity = new RequestEntity<>(new String(body.getBytes(StandardCharsets.UTF_8), StandardCharsets.UTF_8), headers, HttpMethod.POST, new URI(path));
//		ResponseEntity<String> responseEntity = restTemplate.exchange(requestEntity, String.class);
//		return responseEntity;
//	}
//	
//	
//	private void writeLogMessage(String invoiceNumber, Date date, int status ) {
//		if(status == 200 || status == 201) {
//			log.info(String.format("%s; %s; %s;", invoiceNumber,date,"SENDED"));
//		}else {
//			log.info(String.format("%s; %s; %s;", invoiceNumber,date,"FAILED"));
//		}
//	}
//	
//	public org.apache.log4j.Logger getLogger() {
//		org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(GridApiService.class);
//		org.apache.log4j.PatternLayout layout2 = new org.apache.log4j.PatternLayout(); 
//		DailyRollingFileAppender dRollingFile = null;
//		    try {
//		        dRollingFile = new DailyRollingFileAppender(layout2, Paths.get(String.format("%s/messenger-log/", gridApiProperties.getLogPath()),"request-status.log").toString(), "yyyy-MM-dd-HH-mm-ss");
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
//		log = getLogger();
//	}
//	
////	public void initLog() {
////		Logger log;
////		ConfigurationBuilder<BuiltConfiguration> builder = ConfigurationBuilderFactory.newConfigurationBuilder();
////		builder.setStatusLevel(Level.DEBUG);
////		builder.setConfigurationName("RollingBuilder");
////		//builder.setPackages("com.moriset.bcephal.messenger.sendEmail.gridApi");
////		
////		builder.addProperty("filename", Paths.get(String.format("%s/messenger-log/request-status2.log", gridApiProperties.getLogPath())).toString());
////		builder.addProperty("RollingFileName", Paths.get(String.format("%s/messenger-log/archive/",gridApiProperties.getLogPath()) , "request-status-%d{MM-dd-yy-HH-mm-ss}-%i.log.gz").toString());
////		
////		// create a console appender
////		AppenderComponentBuilder consoleappenderBuilder = builder.newAppender("Stdout", "CONSOLE")
////				.addAttribute("target", ConsoleAppender.Target.SYSTEM_OUT);
////		consoleappenderBuilder.add(builder.newLayout("PatternLayout")
////		    .addAttribute("pattern", "%m MDC%X%n"));
////		//.addAttribute("pattern", "%d [%t] %-5level: %msg%n%throwable"));
////	    builder.add(consoleappenderBuilder);
////		
////		// create a rolling file appender
////		LayoutComponentBuilder layoutBuilder = builder.newLayout("PatternLayout")//%d %p %C{1.} [%t] %m%n ----- "%d [%t] %-5level: %msg%n"
////		    .addAttribute("pattern", "%d %p %C{1.} [%t] %m%n");
////		ComponentBuilder triggeringPolicy = builder.newComponent("Policies")
////		   // .addComponent(builder.newComponent("CronTriggeringPolicy").addAttribute("schedule", "0 0 0 * * ?"))
////		    .addComponent(builder.newComponent("SizeBasedTriggeringPolicy").addAttribute("size", "100M"));
////		AppenderComponentBuilder appenderBuilder  = builder.newAppender("RollingTo", "RollingFile")
////		    .addAttribute("fileName", "${fileName}")
////		    .addAttribute("filePattern", "${RollingFileName}")
////		    .add(layoutBuilder)
////		    .addComponent(triggeringPolicy);
////		builder.add(appenderBuilder);
////
////		AppenderComponentBuilder file  = builder.newAppender("log", "FileAppender")	
////				.addAttribute("fileName", "${fileName}")
////				.add(layoutBuilder); 
////		builder.add(file);
////		
////		// create the new logger
////		builder.add(builder.newLogger("com.moriset", Level.INFO)
////		    .add(builder.newAppenderRef("log"))
////		    .addAttribute("additivity", false));
////		
////		builder.add(builder.newLogger("com.moriset.RollingTo", Level.INFO)
////			    .add(builder.newAppenderRef("RollingTo"))
////			    .addAttribute("additivity", false));
////		
////		RootLoggerComponentBuilder rootLogger = builder.newRootLogger();
////		rootLogger.add(builder.newAppenderRef("log"));
////		//rootLogger.add(builder.newAppenderRef("RollingTo"));
////		rootLogger.add(builder.newAppenderRef("Stdout"));
//////		builder.add(builder.newRootLogger(Level.DEBUG)
//////		    .add(builder.newAppenderRef("rolling")));
////		builder.add(rootLogger);
////		LoggerContext ctx = Configurator.initialize(builder.build());
////		//log = ctx.getLogger(GridApiService.class.getName());
////		log = ctx.getRootLogger();
////		log.info("************************Test Logging dynamic configuration*****************************************");
////		
////		log.info(builder.toXmlConfiguration());
////		ctx.getLogger("com.moriset").info("++++++++++++++++++++++++++Test Logging dynamic configuration++++++++++++");
////		ctx.getLogger("com.moriset.RollingTo").info("++++++++++++++++++++++++++Test Logging dynamic configuration++++++++++++");
////		
////		initLog2();
////	}
////	
//	
////	
////	
////	public void initLog2() {
////		org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(GridApiService.class);
////		SimpleLayout layout = new SimpleLayout();
////		//org.apache.log4j.PatternLayout layout2 = new org.apache.log4j.PatternLayout(org.apache.log4j.PatternLayout.TTCC_CONVERSION_PATTERN); 
////		org.apache.log4j.PatternLayout layout2 = new org.apache.log4j.PatternLayout(); 
////		FileAppender appender = null;
////		RollingFileAppender rollingFile = null;
////		DailyRollingFileAppender dRollingFile = null;
////		    try {
//////		        appender = new FileAppender(layout2, Paths.get(String.format("%s/messenger-log/request-status3.log", gridApiProperties.getLogPath())).toString(), true);
//////		        appender.setEncoding("UTF-8");
////		        logger.addAppender(appender);
////		        dRollingFile = new DailyRollingFileAppender(layout2, Paths.get(String.format("%s/messenger-log/", gridApiProperties.getLogPath()),"request-status3.log").toString(), "%d{MM-dd-yy-HH-mm-ss}");
////		        dRollingFile.setEncoding("UTF-8");
////		        dRollingFile.setBufferSize(1024*1024);		        
////		        logger.addAppender(dRollingFile);
////		        
////		        logger.setLevel((org.apache.log4j.Level) org.apache.log4j.Level.INFO);
////		        logger.info("**4444444444444444444444444444444****Test Logging dynamic configuration**5555555555555555555555555555555***");
////		        logger.info("**4444444444444444444444444444444****Test Logging dynamic77777777 configuration**5555555555555555555555555555555***");
////		    }
////		    catch(IOException e) {
////		        e.printStackTrace();
////		        logger.error("Printing ERROR Statements",e);
////		    }
////	}
////	
//}
