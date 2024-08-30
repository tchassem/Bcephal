package com.moriset.bcephal.scheduler.service;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Optional;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.moriset.bcephal.config.MultiTenantJpaSecurityConfiguration;
import com.moriset.bcephal.domain.socket.TaskProgressListener;
import com.moriset.bcephal.project.archive.BackupProjectUtil;
import com.moriset.bcephal.project.archive.PostgresProperties;
import com.moriset.bcephal.project.archive.ProjectFileUtil;
import com.moriset.bcephal.security.domain.Project;
import com.moriset.bcephal.security.domain.SimpleArchive;
import com.moriset.bcephal.security.repository.SimpleArchiveRepository;
import com.moriset.bcephal.security.service.ProjectService;
import com.moriset.bcephal.utils.BcephalException;
import com.moriset.bcephal.utils.FileUtil;
import com.moriset.bcephal.utils.TextLoader;
import com.moriset.bcephal.utils.ZipUnzip;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Service
@Data
@Slf4j
@Transactional(value = MultiTenantJpaSecurityConfiguration.SECURITY_TRANSACTION_MANAGER)
public class ProjectBackupRunner {
	
	@Autowired
	MessageSource messageSource;
	
	@Autowired
	ProjectFileUtil projectFileUtil;
	
	@Autowired
	ProjectService projectService;
	
	@Autowired
	SimpleArchiveRepository simpleArchiveRepository;
	
	@Autowired
	DataSourceProperties properties;
	
	@Autowired
	PostgresProperties postgresProperties;
	
	String username = "B-CEPHAL";
	
	Long clientId;
	
	private String projectCode;
	
	private Integer maxArchiveCount;
	
	private TaskProgressListener listener;

	public SimpleArchive backupProject(java.util.Locale locale) throws BcephalException {
		if (getListener() != null) {
			getListener().createInfo(null, projectCode);
		}
		if (getListener() != null) {
			getListener().start(6);
		}
		
		String postgresDumpCmdPath = postgresProperties.getDump();
		log.debug("Dump Bin Path : {}", postgresDumpCmdPath);
		if(!org.springframework.util.StringUtils.hasText(postgresDumpCmdPath)) {
			throw new BcephalException("Dump Bin Path is NULL!");
		}
		
		Optional<Project> projectOptional = projectService.findByCode(projectCode);
		if(projectOptional.isEmpty()) {
			throw new BcephalException("Project not found : " + projectCode);
		}
		if (getListener() != null) {
			getListener().nextStep(1);
		}
		Project project = projectOptional.get();
		SimpleArchive archive = new SimpleArchive();
		archive.setProjectCode(projectCode);
		archive.setProjectId(project.getId());
		archive.setFileName(project.getName());
		archive.setArchiveMaxCount(maxArchiveCount != null ? maxArchiveCount : Integer.MAX_VALUE);
		archive.setClientId(clientId);
		archive.setUserName(username);
		archive.setCreationDate(new Timestamp(System.currentTimeMillis()));
		archive.setDescription("Project: " + project.getName() 
		+ "\nDate: " + new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(archive.getCreationDate())
		+ "\nAutomatic");
		archive.setName(project.getName() + "_" + new SimpleDateFormat("yyyyMMdd_HHmmss").format(archive.getCreationDate()));
		
		String fileName = archive.getName();		
		if (fileName.contains(".bcp")) {
			fileName = fileName.substring(0, fileName.indexOf(".bcp"));
		} else {
			archive.setName(fileName + ".bcp");
		}
		archive.setRepository(FilenameUtils.concat(FilenameUtils.concat(projectFileUtil.getArchiveDir(), project.getName()), fileName));
		File repos = new File(archive.getRepository()).getParentFile();
		if (!repos.exists()) {
			repos.mkdirs();
		}
		log.debug("Archive path : {}", archive.getRepository());

		if (repos.exists() && archive.getArchiveMaxCount() != null) {
			while (repos.listFiles().length > 0 && repos.listFiles().length >= archive.getArchiveMaxCount()) {
				try {
					File older = FileUtil.getOlderSubFile(repos);
					if (older != null) {
						log.debug("Try to delete old archive : {}", older.toPath());
						FileUtils.forceDelete(older);
						Files.deleteIfExists(older.toPath());
					}
				} catch (Exception e) {
					log.debug("Unable to delete older archive!", e);
				}
			}
			try {
				File from = new File(FilenameUtils.concat(projectFileUtil.getDataDir(), archive.getFileName()));
				File to = Paths.get(archive.getRepository()).toFile();
				if (!to.exists()) {
					to.mkdirs();
				}
				if (!from.exists()) {
					from.mkdirs();
					log.debug("Project folder successfully updated: {} - {}", project.getName(), from);
					log.debug("Try to build bcephal project file identity: {}", project.getName());
					projectFileUtil.writeBcephalProjectFile(from.getCanonicalPath().toString(), project.getCode(), project.getName());
					log.debug("Bcephal project file identity successfully updated: {}", project.getName());
					log.debug("Project successfully updated : \n- Name : {}\n- Code : {}", project.getName(),
							project.getCode());
				}
				if (getListener() != null) {
					getListener().nextStep(1);
				}
				FileUtils.copyDirectory(from, to);
				String path = FilenameUtils.concat(archive.getRepository(), "archive.bcp");
				TextLoader textLoader = new TextLoader(path);
				textLoader.writeln(archive.getName());
				textLoader.writeln(archive.getFileName());
				textLoader.writeln("" + new SimpleDateFormat("dd-MM-yyy hh:mm:ss").format(new Date()));
				textLoader.writeln(archive.getDescription());
				textLoader.close();

				log.debug("Try to backup database");
				String backupFilePath = FilenameUtils.concat(archive.getRepository(), "dump.bcp");
				BackupProjectUtil util = new BackupProjectUtil(postgresProperties);
				util.backup(backupFilePath, properties.getUsername(), properties.getPassword(),
						project.getCode(), properties.determineUrl());
				Thread.sleep(1000);
				if (getListener() != null) {
					getListener().nextStep(1);
				}

				log.debug("Try to ZIP archive...");
				//String achiveFile = FilenameUtils.concat(projectFileUtil.getArchiveDir(), archive.getName());
				String achiveFile = FilenameUtils.concat(repos.getPath(), archive.getName());
				ZipUnzip.zipFile(to, new File(achiveFile));
				Thread.sleep(1000);
				if (getListener() != null) {
					getListener().nextStep(1);
				}
				try {
					FileUtils.deleteDirectory(to);
				} catch (Exception e) {

				}
				archive.setRemotePath(achiveFile);
				archive = simpleArchiveRepository.save(archive);
				if (getListener() != null) {
					getListener().nextStep(1);
				}
				return archive;

			} catch (Exception e) {
				log.debug("{}", e);
				e.printStackTrace();
				String message = messageSource.getMessage("file.not.found", new String[] { fileName }, locale);
				if (getListener() != null) {
					getListener().error(message, true);
				}
				throw new BcephalException(message);
			}
		}
		String message = messageSource.getMessage("unable.to.backup.project", new String[] { fileName }, locale);
		throw new BcephalException(message);
	}

}
