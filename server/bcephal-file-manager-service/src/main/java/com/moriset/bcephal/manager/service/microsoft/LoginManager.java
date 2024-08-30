//package com.moriset.misp.document.service.microsoft;
//
//import java.io.IOException;
//import java.io.InputStream;
//import java.io.OutputStream;
//import java.io.OutputStreamWriter;
//import java.io.StringReader;
//import java.io.Writer;
//import java.net.HttpURLConnection;
//import java.net.URL;
//import java.net.URLConnection;
//
//import javax.xml.parsers.DocumentBuilder;
//import javax.xml.parsers.DocumentBuilderFactory;
//import javax.xml.parsers.ParserConfigurationException;
//import javax.xml.xpath.XPath;
//import javax.xml.xpath.XPathExpressionException;
//import javax.xml.xpath.XPathFactory;
//
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.w3c.dom.Document;
//import org.xml.sax.InputSource;
//import org.xml.sax.SAXException;
//
//public class LoginManager {
//	public final String sts = "https://login.microsoftonline.com/extSTS.srf";
//	public final String loginContextPath = "/_forms/default.aspx?wa=wsignin1.0";
//	private String sharepointContext = "https://";
//	private final String reqXML = "<?xml version=\"1.0\" encoding=\"utf-8\" ?>"
//			+ "<s:Envelope xmlns:s=\"http://www.w3.org/2003/05/soap-envelope\" xmlns:a=\"http://www.w3.org/2005/08/addressing\" "
//			+ "xmlns:u=\"http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-utility-1.0.xsd\"><s:Header>"
//			+ "<a:Action s:mustUnderstand=\"1\">http://schemas.xmlsoap.org/ws/2005/02/trust/RST/Issue</a:Action>"
//			+ "<a:ReplyTo><a:Address>http://www.w3.org/2005/08/addressing/anonymous</a:Address></a:ReplyTo>"
//			+ "<a:To s:mustUnderstand=\"1\">https://login.microsoftonline.com/extSTS.srf</a:To>"
//			+ "<o:Security s:mustUnderstand=\"1\" xmlns:o=\"http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd\">"
//			+ "<o:UsernameToken><o:Username>[username]</o:Username><o:Password>[password]</o:Password></o:UsernameToken></o:Security></s:Header>"
//			+ "<s:Body><t:RequestSecurityToken xmlns:t=\"http://schemas.xmlsoap.org/ws/2005/02/trust\"><wsp:AppliesTo "
//			+ "xmlns:wsp=\"http://schemas.xmlsoap.org/ws/2004/09/policy\"><a:EndpointReference><a:Address>[endpoint]</a:Address>"
//			+ "</a:EndpointReference></wsp:AppliesTo><t:KeyType>http://schemas.xmlsoap.org/ws/2005/05/identity/NoProofKey</t:KeyType>"
//			+ "<t:RequestType>http://schemas.xmlsoap.org/ws/2005/02/trust/Issue</t:RequestType>"
//			+ "<t:TokenType>urn:oasis:names:tc:SAML:1.0:assertion</t:TokenType></t:RequestSecurityToken></s:Body></s:Envelope>";
//	protected MicrosoftSharePointProperties manager;
//	protected Logger logger;
//	
//	public LoginManager(MicrosoftSharePointProperties manager) {
//		logger = LoggerFactory.getLogger(getClass());
//		this.manager = manager;
//	}
//	private String generateSAML() {
//		String saml = reqXML
//				.replace("[username]", manager.getUserName());
//		saml = saml.replace("[password]", manager.getUserpwd() );
//		saml = saml.replace("[endpoint]", manager.getAuth2LoginFormUrl());
//		return saml;
//	}
//	public String login() {
//		String token;
//		try {
//			token = requestToken();
//			String cookie = submitToken(token);
//			return cookie;
//		} catch (Exception e) {
//			e.printStackTrace();
//			logger.debug("{}",e);
//		}		
//		return "";
//	}
//	
//	private String requestToken() throws XPathExpressionException, SAXException,
//		ParserConfigurationException, IOException {
//		String saml = generateSAML();
//		URL u = new URL(sts);
//		URLConnection uc = u.openConnection();
//		HttpURLConnection connection = (HttpURLConnection) uc;
//		connection.setDoOutput(true);
//		connection.setDoInput(true);
//		connection.setRequestMethod("POST");		
//		connection.addRequestProperty("Content-Type","text/xml; charset=utf-8");
//		OutputStream out = connection.getOutputStream();
//		Writer wout = new OutputStreamWriter(out);
//		wout.write(saml);
//		wout.flush();
//		wout.close();
//		InputStream in = connection.getInputStream();
//		int c;
//		StringBuilder sb = new StringBuilder("");
//		while ((c = in.read()) != -1)
//			sb.append((char) (c));
//		in.close();
//		String result = sb.toString();
//		String token = extractToken(result);
//		logger.info(token);
//		return token;
//	}
//	
//	private String extractToken(String result) throws SAXException, IOException, ParserConfigurationException, XPathExpressionException {		
//        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
//        DocumentBuilder db = dbf.newDocumentBuilder();
//        Document document = db.parse(new InputSource(new StringReader(result)));
//        XPathFactory xpf = XPathFactory.newInstance();
//        XPath xp = xpf.newXPath();
//        String token = xp.evaluate("//BinarySecurityToken/text()", document.getDocumentElement());
//        logger.info(token);
//        return token;
//	}
//	
//	private String submitToken(String token) throws IOException {
//		String url = manager.getAuth2LoginFormUrl();
//		URL u = new URL(url);
//		URLConnection uc = u.openConnection();
//		HttpURLConnection connection = (HttpURLConnection) uc;
//		connection.setDoOutput(true);
//		connection.setDoInput(true);
//		connection.setRequestMethod("POST");
//		connection.addRequestProperty("Accept", "application/x-www-form-urlencoded");
//		connection.addRequestProperty("User-Agent", "Mozilla/5.0 (compatible; MSIE 9.0; Windows NT 6.1; Win64; x64; Trident/5.0)");
//		connection.addRequestProperty("Content-Type","text/xml; charset=utf-8");
//		connection.setInstanceFollowRedirects(false);
//
//		OutputStream out = connection.getOutputStream();
//		Writer wout = new OutputStreamWriter(out);
//
//		wout.write(token);
//
//		wout.flush();
//		wout.close();
//
//		InputStream in = connection.getInputStream();
//		
//		//http://www.exampledepot.com/egs/java.net/GetHeaders.html
//		
//	    for (int i=0; ; i++) {
//	        String headerName = connection.getHeaderFieldKey(i);
//	        String headerValue = connection.getHeaderField(i);
//	        logger.info("header: {} : {}" ,headerName, headerValue);
//	        if (headerName == null && headerValue == null) {
//	            // No more headers
//	            break;
//	        }
//	        if (headerName == null) {
//	            // The header value contains the server's HTTP version
//	        }
//	    }
//		String headerName = connection.getHeaderField("set-cookie");
//		logger.info("headerName");
//		logger.info(headerName);
//		int c;
//		StringBuilder sb = new StringBuilder("");
//		while ((c = in.read()) != -1)
//			sb.append((char) (c));
//		in.close();
//		String result = sb.toString();
//		logger.info(result);
//
//		return headerName;
//	}
//
//}
