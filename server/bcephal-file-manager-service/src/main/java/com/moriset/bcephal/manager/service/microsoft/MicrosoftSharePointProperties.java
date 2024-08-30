package com.moriset.bcephal.manager.service.microsoft;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.moriset.bcephal.manager.service.AddressProperties;

import lombok.Data;


@PropertySources({ @PropertySource("microsoft.properties")})
@ConfigurationProperties(prefix = "document.manager")
@Component
@Data
public class MicrosoftSharePointProperties implements AddressProperties {
	
	public  final static String sts = "https://login.microsoftonline.com/extSTS.srf";
	private final String shareDocumentBaseFr = "Documents partages";
	private final String shareDocumentFileBaseFr = "Documents";
	String domain;
	String shareDocument;
	
	
    private ProviderName provider;
	
	private String baseSite;
	
	private String host;
	private String port;
	
	private String login;
	private String password;
	
	
	public MicrosoftSharePointProperties() {
		provider = ProviderName.microsoft;
	}

	public String getBase() {
		return String.format("https://%s/%s/_api/web", getDomain(), nomalizserPathDocuments(getBaseSite()));
	}

	public String getHostName() {
		return String.format("https://%s/%s", getDomain(), nomalizserPathDocuments(getBaseSite()));
	}

	public String getSharepointContext() {
		return String.format("https://%s", getDomain());
	}

	public String getAuth2LoginFormUrl() {
		return String.format("%s/%s", getSharepointContext(), "_forms/default.aspx?wa=wsignin1.0");
	}

	public String getContextinfo() {
		return String.format("%s/%s", getHostName(), "/_api/contextinfo");
	}

	public String getSiteListRestUrl() {
		return String.format("%s/lists", getBase());
	}

	public String getTitleRestUrl() {
		return String.format("%s/title", getBase());
	}

	public String getSiteListNameRestUrl(String name) {
		return String.format("%s/lists/getbytitle%s", getBase(), encoderUrl(String.format("('%s')", name)));
	}

	public String getFolderByIdUrl(String uniqueId) {
		return String.format("%s/lists/GetFolderById%s", getBase(), encoderUrl(String.format("('%s')", uniqueId)));
	}

	public String getFileByIdUrl(String uniqueId) {
		return String.format("%s/GetFileById%s", getBase(), encoderUrl(String.format("('%s')", uniqueId)));
	}
	
	public String getFileById$ValueUrl(String uniqueId) {
		String encodePart3 = encoderUrl("$value");
		return String.format("%s/GetFileById%s/%s", getBase(), encoderUrl(String.format("('%s')", uniqueId)),encodePart3);
	}

	public String getCreatedFolderRestUrl() {
		return String.format("%s/folders", getBase());
	}

	public String getCurrentFolderRestUrl() {
		return String.format("%s/GetFolderByServerRelativeUrl%s", getBase(), encoderUrl("('')"));
	}

	public String getRootFolderRestUrl() {
		return String.format("%s/GetFolderByServerRelativeUrl%s", getBase(),
				encoderUrl(String.format("('%s')", getNormalizerBasePath())));
	}

	public String getFolderRestUrl(String folderName) {
		String req = encoderUrl(String.format("$filter=(Title eq '%s')", folderName));
		return String.format("%s/Items?%s", getSiteListNameRestUrl(getBaseDocuments()), req);
	}

	public String getFile$ValueRestUrl(String folderPathID, String fileName) {
		folderPathID = nomalizserPathDocuments(folderPathID);
		String rootFolder = encoderUrl(String.format("('%s')", getShareDocument()));
		fileName = nomalizserPathDocuments(fileName);
		if (StringUtils.hasText(folderPathID)) {
			String encodePart2 = encoderUrl(String.format("('%s')", fileName));
			String encodePart3 = encoderUrl("$value");
			String result = buildFolder(folderPathID);
			if (StringUtils.hasText(result)) {
				return String.format("%s/RootFolder/folders%s/%s/Files%s/%s",
						getSiteListNameRestUrl(shareDocumentFileBaseFr), rootFolder, result, encodePart2, encodePart3);
			} else {
				return String.format("%s/RootFolder/folders%s/Files%s/%s",
						getSiteListNameRestUrl(shareDocumentFileBaseFr), rootFolder, encodePart2, encodePart3);
			}
		}
		String encodePart2 = encoderUrl(String.format("('%s')", fileName));
		String encodePart3 = encoderUrl("$value");
		return String.format("%s/RootFolder/folders%s/Files%s/%s", getSiteListNameRestUrl(getBaseDocuments()),
				rootFolder, encodePart2, encodePart3);
	}

	public String getAddFileToFolderRestUrl(String folderPath, String fileName) {
		String encodePart1 = getBaseDocuments();
		fileName = nomalizserPathDocuments(fileName);
		String rootFolder = encoderUrl(String.format("('%s')", getShareDocument()));
		String encodePart2 = encoderUrl(String.format("(url='%s',overwrite=true)", fileName));
		if (StringUtils.hasText(folderPath)) {
			encodePart2 = encoderUrl(String.format("(url='%s',overwrite=true)", fileName));
			String result = buildFolder(folderPath);
			if (StringUtils.hasText(result)) {
				return String.format("%s/RootFolder/folders%s/%s/files/add%s",
						getSiteListNameRestUrl(shareDocumentFileBaseFr), rootFolder, result, encodePart2);
			} else {
				return String.format("%s/RootFolder/folders%s/files/add%s",
						getSiteListNameRestUrl(shareDocumentFileBaseFr), rootFolder, encodePart2);
			}
		}
		return String.format("%s/RootFolder/folders%s/files/add%s", getSiteListNameRestUrl(encodePart1), rootFolder,
				encodePart2);
	}

	private String buildFolder(String folderPath) {
		if (!StringUtils.hasText(folderPath)) {
			return null;
		}
		String result = "";
		String encodePart1 = "";
		if (folderPath.contains("/")) {
			String[] dir = folderPath.split("/");
			for (String path : dir) {
				if (StringUtils.hasText(path)) {
					encodePart1 = encoderUrl(String.format("('%s')", path));
					if (!StringUtils.hasText(result)) {
						result = String.format("%sfolders%s", result, encodePart1);
					} else {
						result = String.format("%s/folders%s", result, encodePart1);
					}
				}
			}
		} else {
			encodePart1 = encoderUrl(String.format("('%s')", folderPath));
			result = String.format("folders%s", encodePart1);
		}
		return result;
	}

	public String getDeleteFileRestUrl(String uniqueId) {
		return String.format("%s", getFileByIdUrl(uniqueId));
	}

	public void setShareDocument(String shareDocument) {
		this.shareDocument = shareDocument;
	}
	
	public String getShareDocument() {
		if(!StringUtils.hasText(this.shareDocument)) {
			this.shareDocument =  "Bcephal-SharePoint-Data";
		}
		return this.shareDocument;
	}

	private String nomalizserPathDocuments(String path) {
		if (StringUtils.hasText(path) && path.trim().startsWith("/")) {
			path = path.substring(path.indexOf("/"));
			if (path.length() > 1) {
				path = path.substring(1);
			}
		}
		if (StringUtils.hasText(path) && path.trim().endsWith("/")) {
			path = path.substring(0, path.lastIndexOf("/"));
		}
		return path;
	}

	public String getServerPath(String path) {
		path = nomalizserPathDocuments(path);
		String path_ = getBaseDocuments();
		if (StringUtils.hasText(path)) {
			path_ = getBaseDocuments().concat("/").concat(path);
		}
		return getNormalizerBasePath(path_);
	}

	public String getServerRootPath(String path) {
		path = nomalizserPathDocuments(path);
		String path_ = getBaseDocuments().concat("/").concat(getShareDocument());
		if (StringUtils.hasText(path)) {
			path_ = getBaseDocuments().concat("/").concat(getShareDocument()).concat("/").concat(path);
		}
		return getNormalizerBasePath(path_);
	}
	
	public String getServerRootSharePath(String path) {
		path = nomalizserPathDocuments(path);
		String path_ = shareDocumentBaseFr.concat("/").concat(getShareDocument());
		if (StringUtils.hasText(path)) {
			path_ = shareDocumentBaseFr.concat("/").concat(getShareDocument()).concat("/").concat(path);
		}
		return getNormalizerBasePath(path_);
	}

	

	public String getBaseDocuments() {
		return shareDocumentFileBaseFr;
	}

	public String getNormalizerBasePath() {
		return normalizedPath(getBaseSite());
	}

	public String getNormalizerBasePath(String path) {
		if (!StringUtils.hasText(path)) {
			return getNormalizerBasePath();
		}
		return getNormalizerBasePath().concat("/").concat(path);
	}

	private String normalizedPath(String path) {
		if (!StringUtils.hasText(path)) {
			return path;
		}
		if (!path.startsWith("/")) {
			path = "/".concat(path);
		}
		return path;
	}

	private String encoderUrl(String url) {
		try {
			return URLEncoder.encode(url, StandardCharsets.UTF_8.toString());
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return url;
	}

	public String decoderUrl(String url) {
		try {
			return URLDecoder.decode(url, StandardCharsets.UTF_8.toString());
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return url;
	}


	@Override
	public void init() {
		
	}
	
	@Override
	public String getUserName() {
		return this.login;
	}

	@Override
	public void setUserName(String userName) {
		this.login =userName;
	}

	@Override
	public String getUserpwd() {
		return this.password;
	}

	@Override
	public void setUserpwd(String userpwd) {
		this.password = userpwd;
	}

}
