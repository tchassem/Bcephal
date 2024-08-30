package com.moriset.bcephal.manager.service;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;
import org.springframework.stereotype.Component;

@PropertySources({ @PropertySource("system.properties")})
@Component
public class LocalSystemProperties implements FileManagerProperties {

	private ProviderName provider;

	private String basePath;

	public LocalSystemProperties() {
		provider = ProviderName.system;
	}

	@Override
	public ProviderName getProvider() {
		return provider;
	}

	@Override
	public void setProvider(ProviderName provider) {
		this.provider = provider;
	}

	public String getLocationPath() {
		return basePath;
	}

	public void setLocationPath(String basePath) {
		this.basePath = basePath;
	}
	
	public void  init() {
		if(getLocationPath() == null || getLocationPath().trim().isEmpty()) {
			Path path = Paths.get(System.getProperty("user.home"),".bcephal","8","file-manager-service");
			if(path != null && !path.toFile().exists()) {
				path.toFile().mkdirs();
			}
			try {
				setLocationPath(path.toAbsolutePath().normalize().toFile().getCanonicalPath());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}else if(getLocationPath().contains("{user.home}")){
			String pathString = getLocationPath().replace("{user.home}", System.getProperty("user.home"));
			Path path = Paths.get(pathString);
			try {
				setLocationPath(path.toAbsolutePath().normalize().toFile().getCanonicalPath());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}	
	}
}
