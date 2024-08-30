/**
 * 
 */
package com.moriset.bcephal.loader.service;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.Data;

/**
 * @author Joseph Wambo
 *
 */
@Data
@Configuration
@EnableConfigurationProperties
@ConfigurationProperties("bcephal.file.loader")
public class FileLoaderProperties {

	private String backupDir;
	private String backupLoadedFilesFolder;
	private String backupFilesInErrorFolder;
	private String inDir;
}
