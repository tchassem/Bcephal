package com.moriset.bcephal.manager.service.microsoft;

import java.io.IOException;
import java.io.StringReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class BasicSharePointRestClient {

	protected final String loginFormXml = "<s:Envelope  xmlns:s=\"http://www.w3.org/2003/05/soap-envelope\" "
			+ "   xmlns:a=\"http://www.w3.org/2005/08/addressing\"> "
			+ "  <s:Header> "
			+ "    <a:Action s:mustUnderstand=\"1\">http://schemas.xmlsoap.org/ws/2005/02/trust/RST/Issue</a:Action> "
			+ "    <a:ReplyTo>" 
			+ "        <a:Address>http://www.w3.org/2005/08/addressing/anonymous</a:Address>"
			+ "    </a:ReplyTo>"
			+ "    <a:To s:mustUnderstand=\"1\">https://login.microsoftonline.com/extSTS.srf</a:To>"
			+ "    <o:Security s:mustUnderstand=\"1\""
			+ "        xmlns:o=\"http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd\">"
			+ "        <o:UsernameToken>"
			+ "				<o:Username>[username]</o:Username>"
			+ "            <o:Password>[password]</o:Password>"
			+ "        </o:UsernameToken>"
			+ "    </o:Security>"
			+ "  </s:Header>"
			+ "  <s:Body>"
			+ "    <t:RequestSecurityToken "
			+ "        xmlns:t=\"http://schemas.xmlsoap.org/ws/2005/02/trust\">"
			+ "  <wsp:AppliesTo "
			+ "            xmlns:wsp=\"http://schemas.xmlsoap.org/ws/2004/09/policy\">"
			+ "            <a:EndpointReference>" 
			+ "                <a:Address>[SharePoint domain address]</a:Address>"
			+ "            </a:EndpointReference>"
			+ "  </wsp:AppliesTo>"
			+ "        <t:KeyType>http://schemas.xmlsoap.org/ws/2005/05/identity/NoProofKey</t:KeyType>"
			+ "        <t:RequestType>http://schemas.xmlsoap.org/ws/2005/02/trust/Issue</t:RequestType>"
			+ "        <t:TokenType>urn:oasis:names:tc:SAML:1.0:assertion</t:TokenType>"
			+ "    </t:RequestSecurityToken>"
			+ "  </s:Body>"
			+ "</s:Envelope>";
	
	protected MicrosoftSharePointProperties manager;
	protected String sessionId;

	protected RestTemplate restTemplate;

	public BasicSharePointRestClient(MicrosoftSharePointProperties manager) {
		this.manager = manager;
		restTemplate = new RestTemplate();
	}

	private String extractToken(String result)
			throws SAXException, IOException, ParserConfigurationException, XPathExpressionException {
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db = dbf.newDocumentBuilder();
		Document document = db.parse(new InputSource(new StringReader(result)));
		XPathFactory xpf = XPathFactory.newInstance();
		XPath xp = xpf.newXPath();
		String token = xp.evaluate("//BinarySecurityToken/text()", document.getDocumentElement());
		log.debug(token);
		return token;
	}

	private String receiveSecurityToken()
			throws TransformerException, URISyntaxException, IOException, SharePointAuthenticationException {
		String saml = loginFormXml.replace("[username]", manager.getUserName())
				.replace("[password]", manager.getUserpwd())
				.replace("[SharePoint domain address]", manager.getAuth2LoginFormUrl());
		RequestEntity<String> requestEntity = new RequestEntity<>(saml, HttpMethod.POST,
				new URI(MicrosoftSharePointProperties.sts));
		ResponseEntity<String> responseEntity = restTemplate.exchange(requestEntity, String.class);
		String securityToken = null;
		try {
			securityToken = extractToken(responseEntity.getBody());
		} catch (XPathExpressionException | SAXException | ParserConfigurationException e) {
			e.printStackTrace();
		}

		if (!StringUtils.hasText(securityToken)) {
			throw new SharePointAuthenticationException("Unable to authenticate: empty token");
		}

		return securityToken;
	}

	private List<String> getSignInCookies(String securityToken)
			throws TransformerException, URISyntaxException, SharePointAuthenticationException {
		RequestEntity<String> requestEntity = new RequestEntity<>(securityToken, HttpMethod.POST,
				new URI(manager.getAuth2LoginFormUrl()));
		ResponseEntity<String> responseEntity = restTemplate.exchange(requestEntity, String.class);
		HttpHeaders headers = responseEntity.getHeaders();
		List<String> cookies = headers.get("Set-Cookie");
		if (CollectionUtils.isEmpty(cookies)) {
			throw new SharePointAuthenticationException("Unable to sign in: no cookies returned in response");
		}
		return cookies;
	}

	private String getFormDigestValue(List<String> cookies)
			throws IOException, URISyntaxException, TransformerException, JSONException {
		MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
		headers.add("Cookie", String.join(";", cookies));
		headers.add("Accept", "application/json;odata=verbose");
		headers.add("X-ClientService-ClientTag", "SDK-JAVA");
		RequestEntity<String> requestEntity = new RequestEntity<>(headers, HttpMethod.POST,
				new URI(manager.getContextinfo()));
		ResponseEntity<String> responseEntity = restTemplate.exchange(requestEntity, String.class);
		JSONObject json = new JSONObject(responseEntity.getBody());
		return json.getJSONObject("d").getJSONObject("GetContextWebInformation").getString("FormDigestValue");

	}

	private MultiValueMap<String, String> getAuthheaders(boolean usingToken) throws Exception {
		String securityToken = receiveSecurityToken();
		List<String> cookies = getSignInCookies(securityToken);
		String formDigestValue = getFormDigestValue(cookies);
		MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
		headers.add("Cookie", String.join(";", cookies));
		headers.add("Content-type", "application/json;odata=verbose");
		headers.add("X-RequestDigest", formDigestValue);
		if (usingToken) {
			headers.add("Authorization", "Bearer " + securityToken);
		}
		return headers;

	}

	protected String post(String path, String json, boolean usingToken) throws Exception {
		MultiValueMap<String, String> headers = getAuthheaders(usingToken);
		if (StringUtils.hasText(json)) {
			headers.add("Content-Length", "" + json.length());
		}
		RequestEntity<String> requestEntity = new RequestEntity<>(json, headers, HttpMethod.POST, new URI(path));
		ResponseEntity<String> responseEntity = restTemplate.exchange(requestEntity, String.class);
		return responseEntity.getBody();
	}

	protected String post(String path, byte[] body, boolean usingToken) throws Exception {
		MultiValueMap<String, String> headers = getAuthheaders(usingToken);
		if (body != null && body.length > 0) {
			headers.add("Content-Length", "" + body.length);
		}
		headers.add("If-Match", "*");
		RequestEntity<byte[]> requestEntity = new RequestEntity<>(body, headers, HttpMethod.POST, new URI(path));
		ResponseEntity<String> responseEntity = restTemplate.exchange(requestEntity, String.class);
		return responseEntity.getBody();
	}

	protected String delete(String path) throws Exception {
		MultiValueMap<String, String> headers = getAuthheaders(false);
		headers.add("If-Match", "*");
		headers.add("X-HTTP-Method", "DELETE");
		RequestEntity<String> requestEntity = new RequestEntity<>(headers, HttpMethod.DELETE, new URI(path));
		ResponseEntity<String> responseEntity = restTemplate.exchange(requestEntity, String.class);
		if(responseEntity.getStatusCode() == HttpStatus.OK) {
			return true + "";
		}
		return false + "";
	}

	protected String get(String path, boolean usingToken) throws Exception {
		MultiValueMap<String, String> headers = getAuthheaders(usingToken);
		RequestEntity<String> requestEntity = new RequestEntity<>(headers, HttpMethod.GET, new URI(path));
		ResponseEntity<String> responseEntity = restTemplate.exchange(requestEntity, String.class);
		return responseEntity.getBody();
	}

	protected Object getObject(String path, boolean usingToken) throws Exception {
		MultiValueMap<String, String> headers = getAuthheaders(usingToken);
		RequestEntity<String> requestEntity = new RequestEntity<>(headers, HttpMethod.GET, new URI(path));
		ResponseEntity<byte[]> responseEntity = restTemplate.exchange(requestEntity, byte[].class);
		return responseEntity.getBody();
	}
}
