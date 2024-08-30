package com.moriset.bcephal.manager.service.microsoft;

import java.io.File;
import java.io.FileInputStream;

import org.json.JSONObject;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class HttpClientForSharePoint extends BasicSharePointRestClient {

	ResourceLoader resourceLoader;

	public HttpClientForSharePoint(MicrosoftSharePointProperties manager, ResourceLoader resourceLoader) {
		super(manager);
		this.resourceLoader = resourceLoader;
	}

	public void init() throws Exception {
		createdRootFolder();
	}

	public String createdFile(File file) throws Exception {
		if (file != null) {
			FileInputStream in = new FileInputStream(file);
			byte[] by = new byte[in.available()];
			in.read(by);
			in.close();
			String url = this.manager.getAddFileToFolderRestUrl(null, file.getName());
			log.debug("created File Url = {}", this.manager.decoderUrl(url));
			String response = post(url, by, false);
			if (StringUtils.hasText(response)) {
				return new JSONObject(response).getString("UniqueId");
			}
			throw new Exception(response);
		}
		throw new Exception("Resource is null");
	}

	public String createdFile(String prentPath, File file) throws Exception {
		if (!StringUtils.hasText(prentPath)) {
			return createdFile(file);
		}
		if (file != null) {
			FileInputStream in = new FileInputStream(file);
			byte[] by = new byte[in.available()];
			in.read(by);
			in.close();
			return createdFile(prentPath, by, file.getName());
		}
		throw new Exception("Resource is null");
	}
	
	
	public String createdFile(String prentPath, byte[] by, String fileName) throws Exception {
		if (by != null) {
			String url = this.manager.getAddFileToFolderRestUrl(prentPath, fileName);
			log.debug("created File Url = {}", this.manager.decoderUrl(url));
			createdDirectoryIfNotExist(prentPath);
			String response = post(url, by, false);
			if (StringUtils.hasText(response)) {
				return new JSONObject(response).getString("UniqueId");
			}
			throw new Exception(response);
		}
		throw new Exception("Resource is null");
	}

	public byte[] getFile(String parentPath, String fileName) throws Exception {
		String url = this.manager.getFile$ValueRestUrl(parentPath, fileName);
		log.debug("get file Url = {}", this.manager.decoderUrl(url));
		byte[] response = (byte[]) getObject(url, false);
		return response;
	}

	public byte[] getFileById(String fileId) throws Exception {
		String url = this.manager.getFileById$ValueUrl(fileId);
		log.debug("get File By Id Url = {}", this.manager.decoderUrl(url));
		byte[] response = (byte[]) getObject(url, false);
		return response;
	}

	public boolean deletefile(String fileId) throws Exception {
		String url = this.manager.getDeleteFileRestUrl(fileId);
		log.debug("delete file by Id url = {}", this.manager.decoderUrl(url));
		String response = delete(url);
		return Boolean.valueOf(response);
	}

	public JSONObject createdRootFolder() throws Exception {
		JSONObject value = new JSONObject();
		JSONObject __metadata = new JSONObject();
		__metadata.put("type", "SP.Folder");
		value.put("__metadata", __metadata);
		value.put("ServerRelativeUrl", this.manager.getServerRootSharePath(null));
		log.debug("created Root Folder body = {}", value);
		String url = this.manager.getCreatedFolderRestUrl();
		log.debug("created Root Folder url = {}", url);
		String response = post(url, value.toString(), false);
		if (StringUtils.hasText(response)) {
			return new JSONObject(response);
		}
		throw new Exception(response);
	}

	private void createdDirectoryIfNotExist(String path) throws Exception {
		if (!StringUtils.hasText(path)) {
			return;
		}
		JSONObject value = new JSONObject();
		JSONObject __metadata = new JSONObject();
		__metadata.put("type", "SP.Folder");
		value.put("__metadata", __metadata);
		value.put("ServerRelativeUrl", this.manager.getServerRootSharePath(path));
		log.debug("created Directory If Not Exist body = {}", value);
		String url = this.manager.getCreatedFolderRestUrl();
		log.debug("created Directory If Not Exist url = {}", url);
		String response = post(url, value.toString(), false);
		if (StringUtils.hasText(response)) {
			return;
		}
		throw new Exception(response);
	}

	public String getTitle() throws Exception {
		String response = get(this.manager.getTitleRestUrl(), false);
		if (StringUtils.hasText(response)) {
			return new JSONObject(response).getString("value");
		}
		throw new Exception(response);
	}

	public JSONObject getCurrentFolder() throws Exception {
		String response = get(this.manager.getCurrentFolderRestUrl(), false);
		if (StringUtils.hasText(response)) {
			return new JSONObject(response);
		}
		throw new Exception(response);
	}

	public JSONObject getRootFolder() throws Exception {
		String response = get(this.manager.getRootFolderRestUrl(), false);
		if (StringUtils.hasText(response)) {
			return new JSONObject(response);
		}
		throw new Exception(response);
	}

	

}
