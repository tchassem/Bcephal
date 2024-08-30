/**
 * 
 */
package com.moriset.bcephal.integration.service;

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
@ConfigurationProperties("bcephal.integration")
public class IntegrationProperties {

	private String backupDir;
	private String backupLoadedFilesFolder;
	private String backupFilesInErrorFolder;
	private String inDir;
}
