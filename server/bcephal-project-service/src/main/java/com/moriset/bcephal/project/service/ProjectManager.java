/**
 * 
 */
package com.moriset.bcephal.project.service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

import javax.sql.DataSource;

import org.apache.catalina.session.StandardSessionFacade;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import com.moriset.bcephal.domain.ListChangeHandler;
import com.moriset.bcephal.domain.Persistent;
import com.moriset.bcephal.domain.socket.TaskProgressListener;
import com.moriset.bcephal.multitenant.jpa.CurrentTenantIdentifierResolverImpl;
import com.moriset.bcephal.multitenant.jpa.DataBaseUtils;
import com.moriset.bcephal.multitenant.jpa.HikariProperties;
import com.moriset.bcephal.multitenant.jpa.MultiTenantInterceptor;
import com.moriset.bcephal.multitenant.jpa.TenantContext;
import com.moriset.bcephal.project.archive.BackupProjectUtil;
import com.moriset.bcephal.project.archive.ImportProjectData;
import com.moriset.bcephal.project.archive.ImportProjectException;
import com.moriset.bcephal.project.archive.PostgresProperties;
import com.moriset.bcephal.project.archive.ProjectFileUtil;
import com.moriset.bcephal.security.domain.Project;
import com.moriset.bcephal.security.domain.ProjectBlock;
import com.moriset.bcephal.security.domain.ProjectBrowserData;
import com.moriset.bcephal.security.domain.SimpleArchive;
import com.moriset.bcephal.security.domain.UserWorkspace;
import com.moriset.bcephal.security.service.ProjectBlockService;
import com.moriset.bcephal.security.service.ProjectService;
import com.moriset.bcephal.security.service.SubscriptionService;
import com.moriset.bcephal.utils.BcephalException;
import com.moriset.bcephal.utils.FileUtil;
import com.moriset.bcephal.utils.RequestParams;
import com.moriset.bcephal.utils.SpecialCharUtils;
import com.moriset.bcephal.utils.TextLoader;
import com.moriset.bcephal.utils.ZipUnzip;
import com.zaxxer.hikari.HikariDataSource;

import jakarta.servlet.http.HttpSession;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;
import net.bytebuddy.utility.RandomString;

/**
 * Project service
 * 
 * @author Joseph Wambo
 *
 */
@Service
@Slf4j
public class ProjectManager {

	@Autowired
	MessageSource messageSource;

	@Autowired
	ProjectService projectService;

	@Autowired
	SubscriptionService subscriptionService;

	@Autowired
	ProjectBlockService projectBlockService;

	@Autowired
	DataSourceProperties properties;

	@Autowired
	HikariProperties hikari;

	@Autowired
	ProjectFileUtil projectFileUtil;

	@Autowired
	Map<String, DataSource> bcephalDataSources;

	@Autowired
	PostgresProperties postgresProperties;

	@Autowired
	RestTemplate restTemplate;

	@Autowired
	RestTemplate loadBalancedRestTemplate;
	
	private String securityServiceUri = "lb://security-service";
	/**
	 * Creates a new project.
	 * 
	 * @param projectName The name of the project to create
	 * @param locale      User locale
	 * @param clientId
	 * @return Created project
	 */
//	private HttpHeaders getHttpHeaders(HttpSession session, Locale locale) {
//		HttpHeaders requestHeaders = new HttpHeaders();
//		//String cId = (HttpHeaders.COOKIE+ "__").toLowerCase();
//		String cId = HttpHeaders.COOKIE.toLowerCase();
//		String remoteAddress = "remote_address";
//		List<String> remoteAddressValue = null;
//		List<String> items = Arrays.asList("bc-profile", "bc-client", "BC-PROFILE", "BC-CLIENT", "authorization", HttpHeaders.AUTHORIZATION, cId,HttpHeaders.COOKIE);
//		if (session instanceof StandardSessionFacade) {
//			HttpHeaders requestHeaders2 = (HttpHeaders) session.getAttribute(MultiTenantInterceptor.CUSTOM_HEADER_NAME);
//			if (requestHeaders2 == null) {
//				requestHeaders2 = new HttpHeaders();
//
//			} else {
//				remoteAddressValue = requestHeaders2.get(remoteAddress);
//				requestHeaders2.forEach((key, value) -> {
//					if (items.contains(key)) {
//						if ("authorization".equalsIgnoreCase(key)) {
//							requestHeaders.add(HttpHeaders.AUTHORIZATION, value.get(0));
//						} else if (cId.equalsIgnoreCase(key)) {
//							requestHeaders.add(HttpHeaders.COOKIE, value.get(0));
//						} else
//						{
//							requestHeaders.add(key, value.get(0));
//						}
//					}
//				});
//			}
//		}
//		requestHeaders.set(MultiTenantInterceptor.TENANT_HEADER_NAME, TenantContext.getCurrentTenant());
//		requestHeaders.set(RequestParams.LANGUAGE, locale.getLanguage());
//		if(remoteAddressValue != null && remoteAddressValue.size() > 0) {
//			UriComponents uriComponents;
//			try {
//				uriComponents = UriComponentsBuilder.newInstance()
//						.uri(new URI(remoteAddressValue.get(0))).build();
//				restTemplate.setUriTemplateHandler(
//						  new DefaultUriBuilderFactory(String.format("%s://%s:%s", uriComponents.getScheme(),uriComponents.getHost(),uriComponents.getPort())));
//			} catch (URISyntaxException e) {
//				e.printStackTrace();
//			}
//		}
//		return requestHeaders;
//	}
	
	private HttpHeaders getHttpHeaders_in(HttpSession session, Locale locale) {
		HttpHeaders requestHeaders = new HttpHeaders();
		String cId = HttpHeaders.COOKIE.toLowerCase();
		List<String> items = Arrays.asList("bc-profile", "bc-client", "BC-PROFILE", "BC-CLIENT", "authorization", HttpHeaders.AUTHORIZATION, cId,HttpHeaders.COOKIE);
		if (session instanceof StandardSessionFacade) {
			HttpHeaders requestHeaders2 = (HttpHeaders) session.getAttribute(MultiTenantInterceptor.CUSTOM_HEADER_NAME);
			if (requestHeaders2 == null) {
				requestHeaders2 = new HttpHeaders();

			} else {
				requestHeaders2.forEach((key, value) -> {
					if (items.contains(key)) {
						if ("authorization".equalsIgnoreCase(key)) {
							requestHeaders.add(HttpHeaders.AUTHORIZATION, value.get(0));
						} else if (cId.equalsIgnoreCase(key)) {
							requestHeaders.add(HttpHeaders.COOKIE, value.get(0));
						} else
						{
							requestHeaders.add(key, value.get(0));
						}
					}
				});
			}
		}
		requestHeaders.set(MultiTenantInterceptor.TENANT_HEADER_NAME, TenantContext.getCurrentTenant());
		requestHeaders.set(RequestParams.LANGUAGE, locale.getLanguage());		
		return requestHeaders;
	}

	@SuppressWarnings("serial")
	@Data
	@EqualsAndHashCode(callSuper = false)
	@AllArgsConstructor
	public class ProfileProject extends Persistent {
		private Long id;
		private Long profileId;
		private Long projectId;
		private String projectCode;
		private Long clientId;

		@Override
		public Persistent copy() {
			return null;
		}
	}

	public void SaveRightToProject(Project proj, Long profileId, HttpSession session, Locale locale) {
		log.debug("Call of SaveRightToProject");
		try {
			String path = "/profiles/save-access-rights/" + proj.getId();
			HttpHeaders requestHeaders = getHttpHeaders_in(session, locale);//getHttpHeaders(session, locale);

			
			
			log.trace("SaveRightToProject request requestHeaders : {}", requestHeaders);
			ListChangeHandler<ProfileProject> profils = new ListChangeHandler<ProfileProject>();
			profils.addNew(new ProfileProject(null, profileId, proj.getId(), proj.getCode(), proj.getSubscriptionId()));
			
			HttpEntity<ListChangeHandler<ProfileProject>> requestEntity = new HttpEntity<>(profils, requestHeaders);
			
			//ResponseEntity<String> response = restTemplate.exchange(path, HttpMethod.POST, requestEntity, String.class);
			
			ResponseEntity<String> response = loadBalancedRestTemplate.exchange(securityServiceUri + path, HttpMethod.POST ,requestEntity, String.class);
			
			
			
			if (response.getStatusCode() != HttpStatus.OK) {
				log.error(
						"Unable to save right to project : {} . save access rights call faild with :\n- Http status : {}\n- Message : {}",
						proj.getName(), response.getStatusCode(), response.toString());
				throw new BcephalException("Unable to save right to project {}.", proj.getName());
			}
			String result = response.getBody();
			log.trace("result readed : {}", result);

			if (!(result != null && result.contains("itemListChangeHandler"))) {
				log.error("Unable to save right to project {} .Message : {}", proj.getName(), result);
				throw new BcephalException("Unable to save right to project {}.", proj.getName());
			}
		} catch (BcephalException e) {
			throw e;
		} catch (Exception e) {
			log.error("Unable to read {} : {}", proj.getName(), e);
			throw new BcephalException("Unable to read {} : {} : {}", proj.getName());
		}
	}

	public String normalizeProjectName(String name) {
		while (StringUtils.hasText(name) && name.endsWith(" ")) {
			name = name.substring(0, name.length() - 1);
		}
		return name;
	}

	public Project createProject(Long profileId, HttpSession session, Long subscriptionId, Project project,
			java.util.Locale locale,TaskProgressListener listener) throws BcephalException {
		String projectName = project.getName();
		log.debug("Try to create project : {}", projectName);
		String dbName = null;
		DataSource Defalutsource = bcephalDataSources.get(CurrentTenantIdentifierResolverImpl.DEFAULT_TENANT);
		try {
			if (!StringUtils.hasText(projectName)) {
				String message = messageSource.getMessage("unable.to.create.project.with.empty.name",
						new String[] { projectName }, locale);
				throw new BcephalException(message);
			}
			if(listener != null) {
				listener.nextStep(1);
			}
			List<ProjectBrowserData> projects = projectService.findBySubcriptionAndName(subscriptionId, projectName);
			if (projects != null && projects.size() > 0) {
				log.trace("An other project exist with this name : {}.", projectName);
				String message = messageSource.getMessage("can.not.create.exist.project", new String[] { projectName },
						locale);
				throw new BcephalException(HttpStatus.FOUND.value(), message);
			}

			if(project.isAllowCodeBuilder()) {
				log.trace("Build project Code.");
				project.setCode(buildNewProjectCode(projectName, subscriptionId));
			}
			if (project.getSubscriptionId() == null) {
				project.setSubscriptionId(subscriptionId);
			}
			if(listener != null) {
				listener.nextStep(1);
			}
			dbName = project.getCode();
			log.trace("Project code : {}.", dbName);
			DataBaseUtils utils = new DataBaseUtils(properties, Defalutsource, hikari);
			if (utils.isDbExists(dbName)) {
				log.trace("Unable to create project : {}. There is a DB with the same name: {}", projectName, dbName);
				String message = messageSource.getMessage("unable.to.create.project", new String[] { projectName },
						locale);
				throw new BcephalException(HttpStatus.NOT_FOUND.value(), message);
			}
			if(listener != null) {
				listener.nextStep(1);
			}
			utils.createDb(dbName);
			if(listener != null) {
				listener.nextStep(2);
			}
			buildDataSource(dbName);
			if(listener != null) {
				listener.nextStep(3);
			}
			project.setDbname(dbName);

			log.debug("Try to save project POJO : {}", project.getName());
			projectService.save(project);
			log.debug("Project POJO successfully saved : {} - {}", project.getName(), project.getId());
			if(listener != null) {
				listener.nextStep(1);
			}
			log.debug("Try to build project folder: {}", project.getName());
			String projectPath = FilenameUtils.concat(projectFileUtil.getDataDir(), projectName);
			File file = Paths.get(projectPath).toFile();
			if (!file.exists()) {
				file.mkdirs();
			}
			log.debug("Project folder successfully created: {} - {}", project.getName(), projectPath);

			log.debug("Try to build bcephal project file identity: {}", project.getName());
			projectFileUtil.writeBcephalProjectFile(projectPath, project.getCode(), project.getName());
			log.debug("Bcephal project file identity successfully created: {}", project.getName());
			if(listener != null) {
				listener.nextStep(1);
			}
			log.debug("Project successfully created : \n- Name : {}\n- Code : {}", project.getName(),
					project.getCode());
			// SaveRightToProject(project, profileId, session, locale);
			return project;
		} catch (BcephalException e) {
			log.error("Unable to create project", e);
			DataBaseUtils utils = new DataBaseUtils(properties, Defalutsource, hikari);
			try {
				if (StringUtils.hasText(dbName) && utils.isDbExists(dbName)) {
					utils.dropDb(dbName);
				}
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
			throw e;
		} catch (Exception e) {
			DataBaseUtils utils = new DataBaseUtils(properties, Defalutsource, hikari);
			try {
				if (StringUtils.hasText(dbName) && utils.isDbExists(dbName)) {
					utils.dropDb(dbName);
				}
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
			log.error("Unexpected error while creating project : {}", projectName, e);
			String message = messageSource.getMessage("unable.to.create.project", new String[] { projectName }, locale);
			throw new BcephalException(HttpStatus.INTERNAL_SERVER_ERROR.value(), message);
		}
	}

	/**
	 * Renames a project.
	 * 
	 * @param projectId      The Id of the project to rename
	 * @param newProjectName The new project name
	 * @param locale         User locale
	 * @return Renamed project
	 */
	public Project renameProject(Long projectId, String newProjectName, Long clientId, java.util.Locale locale)
			throws BcephalException {
		log.debug("Try to rename project : {}", projectId);
		try {
			if (projectId == null) {
				String message = messageSource.getMessage("unable.to.rename.project.with.empty.code",
						new Object[] { projectId }, locale);
				throw new BcephalException(message);
			}
			if (!StringUtils.hasText(newProjectName)) {
				String message = messageSource.getMessage("unable.to.rename.project.with.empty.name",
						new String[] { newProjectName }, locale);
				throw new BcephalException(message);
			}
			Optional<ProjectBrowserData> projects = projectService.findBySubcriptionAndProjectId(clientId, projectId);
			if (projects != null && projects.isEmpty()) {
				log.trace("Cannot find the project to rename with the code: {}.", projectId);
				String message = messageSource.getMessage("can.not.rename.exist.project", new Object[] { projectId },
						locale);
				throw new BcephalException(HttpStatus.FOUND.value(), message);
			}
			String projectName = projects.get().getName();
			Project project = projectService.rename(projects.get().getId(), newProjectName).get();
			projectService.save(project);
			String projectPath = FilenameUtils.concat(projectFileUtil.getDataDir(), projectName);
			Path source = Paths.get(projectPath);
			if (source.toFile().exists()) {
				Files.move(source, source.resolveSibling(newProjectName));
			} else {
				source.resolveSibling(newProjectName).toFile().mkdirs();
			}
			log.debug("Project successfully renamed from : {} to : {}", projectId, newProjectName);
			return project;
		} catch (BcephalException e) {
			throw e;
		} catch (Exception e) {
			log.error("Unexpected error while renaming project : {}", projectId, e);
			String message = messageSource.getMessage("unable.to.rename.project", new Object[] { projectId }, locale);
			throw new BcephalException(HttpStatus.INTERNAL_SERVER_ERROR.value(), message);
		}
	}

	/**
	 * Deletes a project.
	 * 
	 * @param projectId The code of the project to delete
	 * @param locale    User locale
	 * @return Renamed project
	 */
	public boolean deleteProject(Long projectId, java.util.Locale locale) throws BcephalException {
		log.debug("Try to delete project : {}", projectId);
		try {
			if (projectId == null) {
				String message = messageSource.getMessage("unable.to.delete.project.with.empty.code",
						new Object[] { projectId }, locale);
				throw new BcephalException(message);
			}
			log.debug("Project successfully deleted : {}", projectId);
			Optional<Project> project = projectService.findById(projectId);
			String projectName = "";
			if (project != null && project.isEmpty()) {
				String message = messageSource.getMessage("unable.to.found.to.delete.project",
						new Object[] { projectId }, locale);
				throw new BcephalException(message);
			}
			projectName = project.get().getName();
			DataSource Defalutsource = bcephalDataSources.get(CurrentTenantIdentifierResolverImpl.DEFAULT_TENANT);
			DataBaseUtils utils = new DataBaseUtils(properties, Defalutsource, hikari);
			if (utils.countConnectionOfDB(project.get().getCode()) > 0) {
				String message = messageSource.getMessage("unable.to.delete.project.with.user.connected.code",
						new String[] { projectName }, locale);
				throw new BcephalException(message);
			}
			utils.closeAllConnexionsToDb(project.get().getCode());
			utils.dropDb(project.get().getCode());
			String projectPath = FilenameUtils.concat(projectFileUtil.getDataDir(), projectName);
			File file = new File(projectPath);
			if(file.exists() && !projectPath.endsWith(".")) {
				FileUtils.deleteDirectory(file);
			}
			int deleted = projectService.delete(project.get().getCode());
			projectBlockService.deleteByCode(project.get().getCode());
			log.trace("Delete status : {}", deleted);
			return true;
		} catch (BcephalException e) {
			throw e;
		} catch (Exception e) {
			log.error("Unexpected error while deleting project : {}", projectId, e);
			String message = messageSource.getMessage("unable.to.delete.project", new Object[] { projectId }, locale);
			throw new BcephalException(HttpStatus.INTERNAL_SERVER_ERROR.value(), message);
		}
	}

	/**
	 * Opens project.
	 * 
	 * @param projectCode The project code
	 * @param locale      User locale
	 * @param username
	 * @return
	 */
	public String openProject(String projectCode, Long clientId, java.util.Locale locale, String username)
			throws BcephalException {
		log.debug("Try to open project with code : {}", projectCode);
		if (!StringUtils.hasText(projectCode)) {
			String message = messageSource.getMessage("unable.to.find.project.with.empty.code",
					new String[] { projectCode }, locale);
			throw new BcephalException(message);
		}
		String tenantId = projectCode;
		try {
			buildDataSource(tenantId);
			log.debug("Project successfully opened : {}", projectCode);
			return tenantId;
		} catch (Exception e) {
			this.bcephalDataSources.remove(tenantId);
			TenantContext.setCurrentTenant(null);
			log.error("Unexpected error while opening project : {}", projectCode, e);
			String message = messageSource.getMessage("unable.to.open.project", new String[] { projectCode }, locale);
			throw new BcephalException(HttpStatus.INTERNAL_SERVER_ERROR.value(), message);
		}
	}

	/**
	 * Closes project.
	 * 
	 * @param projectCode The project code
	 * @param locale      User locale
	 * @return
	 */
	public boolean closeProject(String projectCode, java.util.Locale locale) throws BcephalException {
		log.debug("Try to close project with code : {}", projectCode);
		try {
			if (!StringUtils.hasText(projectCode)) {
				String message = messageSource.getMessage("unable.to.find.project.with.empty.code",
						new String[] { projectCode }, locale);
				throw new BcephalException(message);
			}
			String tenantId = TenantContext.getCurrentTenant();
			DataSource datasource = this.bcephalDataSources.remove(tenantId);
			if (datasource != null) {
				((HikariDataSource) datasource).close();
			}
			TenantContext.setCurrentTenant(null);
			log.debug("Project successfully Closed Project : {}", projectCode);
			return true;
		} catch (Exception e) {
			log.error("Unexpected error while closing project : {}", projectCode, e);
			String message = messageSource.getMessage("unable.to.close.project", new String[] { projectCode }, locale);
			throw new BcephalException(HttpStatus.INTERNAL_SERVER_ERROR.value(), message);
		}

	}

	public String backupProject(SimpleArchive archive, java.util.Locale locale) throws BcephalException {
		String fileName = archive.getName();
		if (!StringUtils.hasText(fileName)) {
			String message = messageSource.getMessage("unable.to.backup.project.with.empty.file.name",
					new String[] { fileName }, locale);
			throw new BcephalException(message);
		}
		if (fileName.contains(".bcp")) {
			fileName = fileName.substring(0, fileName.indexOf(".bcp"));
		} else {
			archive.setName(fileName + ".bcp");
		}
		archive.setRepository(FilenameUtils.concat(projectFileUtil.getArchiveDir(), fileName));
		File repos = new File(archive.getRepository()).getParentFile();
		if (!repos.exists()) {
			repos.mkdirs();
		}

		if (repos.exists() && archive.getArchiveMaxCount() != null) {
			while (repos.getParentFile().listFiles().length >= archive.getArchiveMaxCount() - 1) {
				try {
					File older = FileUtil.getOlderSubFile(repos);
					if (older != null) {
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
					Optional<Project> projects = projectService.findById(archive.getProjectId());
					if (projects.isPresent()) {
						Project project = projects.get();
						log.debug("Project folder successfully updated: {} - {}", project.getName(), from);
						log.debug("Try to build bcephal project file identity: {}", project.getName());
						projectFileUtil.writeBcephalProjectFile(from.getCanonicalPath().toString(), project.getCode(), project.getName());
						log.debug("Bcephal project file identity successfully updated: {}", project.getName());
						log.debug("Project successfully updated : \n- Name : {}\n- Code : {}", project.getName(),
								project.getCode());
					}
				}
				FileUtils.copyDirectory(from, to);
				String path = FilenameUtils.concat(archive.getRepository(), "archive.bcp");
				TextLoader textLoader = new TextLoader(path);
				textLoader.writeln(archive.getName());
				textLoader.writeln(archive.getFileName());
				textLoader.writeln("" + new SimpleDateFormat("dd-MM-yyy hh:mm:ss").format(new Date()));
				textLoader.writeln(archive.getDescription());
				textLoader.close();

				String backupFilePath = FilenameUtils.concat(archive.getRepository(), "dump.bcp");
				BackupProjectUtil util = new BackupProjectUtil(postgresProperties);
				Optional<Project> project = projectService.findByCode_(archive.getProjectCode());
				if (project.isPresent()) {
					util.backup(backupFilePath, properties.getUsername(), properties.getPassword(),
							project.get().getCode(), properties.determineUrl());
					Thread.sleep(1000);
				}

				String achiveFile = FilenameUtils.concat(projectFileUtil.getArchiveDir(), archive.getName());
				ZipUnzip.zipFile(to, new File(achiveFile));
				Thread.sleep(1000);
				try {
					FileUtils.deleteDirectory(to);
				} catch (Exception e) {

				}
				return achiveFile;

			} catch (Exception e) {
				log.debug("{}", e);
				e.printStackTrace();
				String message = messageSource.getMessage("file.not.found", new String[] { fileName }, locale);
				throw new BcephalException(message);
			}
		}
		String message = messageSource.getMessage("unable.to.backup.project", new String[] { fileName }, locale);
		throw new BcephalException(message);
	}

	@Transactional
	public Project importProject(ImportProjectData data, Long subscriptionId, Long profileId, HttpSession session,
			java.util.Locale locale) throws NoSuchMessageException, IOException, BcephalException {
		data.setFilePath(FilenameUtils.separatorsToSystem(data.getFilePath()));
		if (!StringUtils.hasText(data.getFilePath())) {
			log.error("Unable to import project: path is empty");
			String message = messageSource.getMessage("unable.to.import.empty.project",
					new String[] { data.getNewProjectName() }, locale);
			throw new BcephalException(message);
		}
		java.io.File from = new java.io.File(data.getFilePath());
		if (!from.exists()) {
			log.error("Unable to import project: File not found {}", data.getFilePath());
			String message = messageSource.getMessage("unable.to.import.project.file.not.found",
					new String[] { data.getNewProjectName() }, locale);
			throw new BcephalException(message);
		}

		String destination = FilenameUtils.concat(FilenameUtils.getFullPath(data.getFilePath()),
				FilenameUtils.getBaseName(data.getFilePath()));
		java.io.File to = new java.io.File(destination);

		try {
			ZipUnzip.unZipFile(from, to);
		} catch (Exception e) {
			try {
				FileUtils.deleteDirectory(from);
				FileUtils.deleteDirectory(to);
			} catch (Exception ex) {
			}
			log.error("Unable to unzip project file", e);
			String message = messageSource.getMessage("unable.to.import.project.unzip", new String[] { from.getName() },
					locale);
			throw new BcephalException(message);
		}
		String projectCode = null;
		DataSource Defalutsource = bcephalDataSources.get(CurrentTenantIdentifierResolverImpl.DEFAULT_TENANT);
		DataBaseUtils utils = new DataBaseUtils(properties, Defalutsource, hikari);
		try {
			java.io.File archive = new java.io.File(FilenameUtils.concat(destination, "archive.bcp"));
			if (!archive.exists()) {
				log.error("Archive file not found!");
				String message = messageSource.getMessage("unable.to.import.project.archive.not.found",
						new String[] { archive.getName() }, locale);
				throw new BcephalException(message);
			}
			@SuppressWarnings("rawtypes")
			List lines = FileUtils.readLines(archive, "UTF-8");
			// Object line = lines.get(1);
			String name = lines.get(0).toString().trim();
			if (lines.size() > 1) {
				name = lines.get(1).toString().trim();
			}
			log.info("Try to import project : {}", name);

			java.io.File dump = new java.io.File(FilenameUtils.concat(destination, "dump.bcp"));
			if (!dump.exists()) {
				log.error("Dump file not found!");
				String message = messageSource.getMessage("unable.to.import.project.Dump.not.found",
						new String[] { dump.getName() }, locale);
				throw new BcephalException(message);
			}

			projectCode = projectFileUtil.readBcephalProjectFileCode(destination);
			boolean generatNewProjectCode = false;
			List<Project> existingProjectByName = projectService.findByName(name);
			Optional<ProjectBrowserData> existingProjectByCode = projectService.findByCode(projectCode);
			boolean existProgectName = existingProjectByName != null && existingProjectByName.size() > 0;
			boolean existProgectCode = existingProjectByCode != null && existingProjectByCode.isPresent();
			boolean alreadyExist = existProgectName || existProgectCode;
			Long subscriptionIdExist = subscriptionId;

			boolean existProgectCode2 = false;
			try {
				if (!alreadyExist && StringUtils.hasText(projectCode)) {
					existProgectCode2 = utils.isDbExists(projectCode);
					alreadyExist = alreadyExist || existProgectCode2;
				}
			} catch (Exception e2) {
				log.error("Unable to delete Database: {}", e2);
			}

			if (alreadyExist) {
				if (data.isRenameProjectToImport() || data.isRenameExistingProject()
						|| data.isOverwriteExistingProject()) {
					log.debug("Duplicated project : another project named {} already exists", name);
					if (data.isRenameProjectToImport() || data.isRenameExistingProject()) {
						if (!StringUtils.hasText(data.getNewProjectName())) {
							log.error("Unable to import project by empty New Name");
							String message = messageSource.getMessage("unable.to.import.project.empty.new.name",
									new String[] { dump.getName() }, locale);
							throw new BcephalException(message);
						}
					}
					if (data.isRenameProjectToImport()) {
						name = data.getNewProjectName();
						generatNewProjectCode = true;
					} else if (data.isRenameExistingProject() && alreadyExist) {
						Long projectId = null;
						if (existProgectName) {
							projectId = existingProjectByName.get(0).getId();
							subscriptionIdExist = existingProjectByName.get(0).getSubscriptionId();
						} else if (existProgectCode) {
							projectId = existingProjectByCode.get().getId();
							subscriptionIdExist = existingProjectByCode.get().getSubscriptionId();
						}
						if (existProgectCode2) {
							try {
								utils.dropDb(projectCode);
							} catch (Exception e) {
								log.error("{}", e);
							}
						} else {
							renameProject(projectId, data.getNewProjectName(), subscriptionIdExist, locale);
						}
						generatNewProjectCode = true;
					} else if (data.isOverwriteExistingProject() && alreadyExist) {
						try {
							if (existProgectName) {
								deleteProject(existingProjectByName.get(0).getId(), locale);
							} else if (existProgectCode) {
								deleteProject(existingProjectByCode.get().getId(), locale);
							} else if (existProgectCode2) {
								try {
									utils.dropDb(projectCode);
								} catch (Exception e) {
									log.error("{}", e);
								}
							}
						} catch (Exception e) {
							String message = messageSource.getMessage("unable.to.import.project.cannot.override",
									new String[] { name }, locale);
							throw new BcephalException(message);
						}
					}
				} else {
					log.error("Duplicated project : another project named {} already exists", name);
					String message = messageSource.getMessage("unable.to.import.project.dupplicate",
							new String[] { dump.getName() }, locale);
					throw new BcephalException(message);
				}
			}

			String projectPath = projectFileUtil.getDataDir();
			projectPath = FilenameUtils.concat(projectPath, name);
			projectPath = FilenameUtils.separatorsToSystem(projectPath);
			java.io.File projectDir = new java.io.File(projectPath);
			if (projectDir.exists()) {
				try {
					FileUtils.forceDelete(projectDir);
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}

			Project project = new Project();
			project.setId(project.getId());
			project.setCode(projectCode);
			project.setName(name);
			project.setDescription(name);
			project.setSubscriptionId(subscriptionId);
			if (generatNewProjectCode) {
				project.setCode(buildNewProjectCode(name, subscriptionId));
			}
			projectCode = project.getCode();
			BackupProjectUtil util = new BackupProjectUtil(postgresProperties);
			try {
				util.importDb(dump.getPath(), properties, project.getCode());
				project.setDbname(project.getCode());
				try {
					FileUtils.forceDelete(Paths.get(dump.getPath()).toFile());
				} catch (Exception ex) {
				}
				try {
					FileUtils.forceDelete(Paths.get(archive.getPath()).toFile());
				} catch (Exception ex) {
				}
			} catch (ImportProjectException e) {
				try {
					if (StringUtils.hasText(projectCode) && utils.isDbExists(projectCode)) {
						utils.dropDb(projectCode);
					}
				} catch (Exception e2) {
					log.error("Unable to delete Database: {}", e2);
				}
				log.error("Unable to import project: {} {}", e, e.getMessage());
				String message = messageSource.getMessage("unable.to.import.project", new String[] { dump.getName() },
						locale);
				throw new BcephalException(message);
			}

			java.io.File src = new java.io.File(destination);
			FileUtils.copyDirectory(src, projectDir);

			try {
				project = projectService.save(project);
				log.debug("Try to build bcephal project file identity: {}", project.getName());
				projectFileUtil.writeBcephalProjectFile(projectPath, project.getCode(), project.getName());
				log.debug("Bcephal project file identity successfully created: {}", project.getName());
			} catch (Exception e) {
				log.error("Unable to set client to project {}", name, e);
				throw e;
			}
			List<Project> importedProject = projectService.findByName(name);
			if (importedProject != null && importedProject.size() > 0) {
				return importedProject.get(0);
			}
			return null;
		} catch (Exception e) {
			e.printStackTrace();
			try {
				if (StringUtils.hasText(projectCode) && utils.isDbExists(projectCode)) {
					utils.dropDb(projectCode);
				}
			} catch (Exception e2) {
				log.error("Unable to delete Database: {}", e2);
			}
			log.error("Unable to import project", e);
			String message = messageSource.getMessage("unable.to.import.project", new String[] { from.getName() },
					locale);
			throw new BcephalException(message);
		} finally {

			try {
				FileUtils.forceDelete(to);
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
	}

	public SimpleArchive buildManualImportProjectSimpleArchive(SimpleArchive archive) throws Exception {
		if (!StringUtils.hasText(archive.getRemotePath())) {
			log.error("Unable to import project: path is empty");
			String message = messageSource.getMessage("unable.to.import.empty.project",
					new String[] { archive.getName() }, archive.getLocale());
			throw new BcephalException(message);
		}
		java.io.File from = new java.io.File(FilenameUtils.separatorsToSystem(archive.getRemotePath()));
		if (!from.exists()) {
			log.error("File not found {}", archive.getRemotePath());
			String message = messageSource.getMessage("unable.to.import.project.file.not.found",
					new String[] { archive.getRemotePath() }, archive.getLocale());
			throw new BcephalException(message);
		}

		archive.setRepository(FilenameUtils.separatorsToSystem(archive.getRemotePath()));
		String destination = FilenameUtils.concat(FilenameUtils.getFullPath(archive.getRemotePath()),
				FilenameUtils.getBaseName(archive.getRemotePath() + "0"));
		java.io.File to = new java.io.File(destination);

		try {
			ZipUnzip.unZipFile(from, to);
		} catch (Exception e) {
			try {
				FileUtils.deleteDirectory(from);
				FileUtils.deleteDirectory(to);
			} catch (Exception ex) {
			}
			log.error("Unable to unzip project file", e);
			String message = messageSource.getMessage("unable.to.import.project.unzip", new String[] { from.getName() },
					archive.getLocale());
			throw new BcephalException(message);
		}

		try {
			java.io.File archiveFile = new java.io.File(FilenameUtils.concat(destination, "archive.bcp"));
			if (!archiveFile.exists()) {
				log.error("Archive file not found!");
				String message = messageSource.getMessage("unable.to.import.project.archive.not.found",
						new String[] { archive.getName() }, archive.getLocale());
				throw new BcephalException(message);
			}
			@SuppressWarnings("rawtypes")
			List lines = FileUtils.readLines(archiveFile, "UTF-8");
			String fileName = lines.get(0).toString().trim();
			String name = lines.get(1).toString().trim();
			archive.setFileName(fileName);
			archive.setName(name);
			java.io.File projectFile = new java.io.File(FilenameUtils.concat(destination, "project.bcephal"));
			if (!projectFile.exists()) {
				log.error("Archive file not found!");
				String message = messageSource.getMessage("unable.to.import.project.file.properties.not.found",
						new String[] { archive.getName() }, archive.getLocale());
				throw new BcephalException(message);
			}
			@SuppressWarnings("rawtypes")
			List projectFileLines = FileUtils.readLines(projectFile, "UTF-8");
			String code = projectFileLines.get(0).toString().trim();
			archive.setProjectCode(code);
			log.info("Try to import project : Name: {}, code: {}", name, code);
			try {
				FileUtils.deleteDirectory(to);
			} catch (Exception ex) {
			}
			return archive;
		} catch (Exception ex) {
			throw ex;
		}
	}

	@SuppressWarnings("unused")
	public boolean checkIfProjectExists(SimpleArchive archive) throws Exception {
		// boolean generatNewProjectCode = false;
		boolean existingProjectName = projectService.existsByName(archive.getFileName());
		boolean existingProjectCode = projectService.existsByCode(archive.getProjectCode());
		boolean alreadyExist = existingProjectName || existingProjectCode;
		DataSource Defalutsource = bcephalDataSources.get(CurrentTenantIdentifierResolverImpl.DEFAULT_TENANT);
		DataBaseUtils utils = new DataBaseUtils(properties, Defalutsource, hikari);
		try {
			if (!alreadyExist && StringUtils.hasText(archive.getProjectCode())) {
				alreadyExist = utils.isDbExists(archive.getProjectCode());
			}
		} catch (Exception e2) {
			log.error("Unable to delete Database: {}", e2);
		}
		if (alreadyExist) {
			log.error("Duplicated project : another project named {} already exists", archive.getFileName());
			return true;
		}

		archive.setRepository(FilenameUtils.separatorsToSystem(archive.getRepository()));
		if (!StringUtils.hasText(archive.getRepository())) {
			log.error("Unable to import project: path is empty");
			String message = messageSource.getMessage("unable.to.import.empty.project",
					new String[] { archive.getName() }, archive.getLocale());
			throw new BcephalException(message);
		}
		java.io.File from = new java.io.File(archive.getRepository());
		if (!from.exists()) {
			log.error("Unable to import project: File not found {}", archive.getRepository());
			String message = messageSource.getMessage("unable.to.import.project.file.not.found",
					new String[] { archive.getName() }, archive.getLocale());
			throw new BcephalException(message);
		}

		String destination = FilenameUtils.concat(FilenameUtils.getFullPath(archive.getRepository()),
				FilenameUtils.getBaseName(archive.getRepository()));
		java.io.File to = new java.io.File(destination);

		try {
			ZipUnzip.unZipFile(from, to);
		} catch (Exception e) {
			try {
				FileUtils.deleteDirectory(from);
				FileUtils.deleteDirectory(to);
			} catch (Exception ex) {
			}
			log.error("Unable to unzip project file", e);
			String message = messageSource.getMessage("unable.to.import.project.unzip", new String[] { from.getName() },
					archive.getLocale());
			throw new BcephalException(message);
		}

		try {
			java.io.File archiveFile = new java.io.File(FilenameUtils.concat(destination, "archive.bcp"));
			if (!archiveFile.exists()) {
				log.error("Archive file not found!");
				String message = messageSource.getMessage("unable.to.import.project.archive.not.found",
						new String[] { archive.getName() }, archive.getLocale());
				throw new BcephalException(message);
			}
			@SuppressWarnings("rawtypes")
			List lines = FileUtils.readLines(archiveFile, "UTF-8");
			// Object line = lines.get(1);
			String fileName = lines.get(0).toString().trim();
			String name = lines.get(1).toString().trim();
			log.info("Try to import project : {}", name);

			java.io.File dump = new java.io.File(FilenameUtils.concat(destination, "dump.bcp"));
			if (!dump.exists()) {
				log.error("Dump file not found!");
				String message = messageSource.getMessage("unable.to.import.project.Dump.not.found",
						new String[] { dump.getName() }, archive.getLocale());
				throw new BcephalException(message);
			}

			String projectCode = projectFileUtil.readBcephalProjectFileCode(destination);

			existingProjectName = projectService.existsByName(name);
			existingProjectCode = projectService.existsByCode(projectCode);
			alreadyExist = existingProjectName || existingProjectCode;
			try {
				if (!alreadyExist && StringUtils.hasText(projectCode)) {
					alreadyExist = utils.isDbExists(projectCode);
				}
			} catch (Exception e2) {
				log.error("Unable to delete Database: {}", e2);
			}
			return alreadyExist;

		} catch (Exception ex) {
			throw ex;
		}
	}

	/**
	 * Load user workspace
	 * 
	 * @param locale
	 * @return The user workspace
	 */
	public UserWorkspace getUserWorkspace(Long subscriptionId, Long profileId, String username,
			java.util.Locale locale) {
		UserWorkspace workspace = new UserWorkspace();
//		if (subscriptionId == null) {
//			List<SubscriptionBrowserData> subcriptions = subscriptionService.findDefaultSubscription();
//			if (subcriptions != null && !subcriptions.isEmpty()) {
//				workspace.setDefaultClient(subcriptions.get(0));
//				subscriptionId = workspace.getDefaultClient().getId();
//			}
//		}
		workspace.setClients(subscriptionService.findAll());
		workspace.setAvailableProjects(projectService.findBySubcription(subscriptionId, profileId));
		workspace.setDefaultProjects(
				projectService.findBySubcriptionAndDefault(subscriptionId, profileId, true, username));

		return workspace;
	}

	/**
	 * Builds new project name
	 * 
	 * @param subscriptionId
	 * @param locale
	 * @return
	 */
	public String buildNewProjectName(Long subscriptionId, Locale locale) {
		String base = "Project";
		int i = 1;
		String name = base.concat(" " + i++);
		while (projectService.findBySubcriptionAndName(subscriptionId, name).size() > 0) {
			name = base.concat(" " + i++);
		}
		return name;
	}

	/**
	 * Retrieves project blocks by subscription ant user
	 * 
	 * @param subscriptionId Subscription
	 * @param username       Connected user name
	 * @param locale         User locale
	 * @return List of project block
	 */
	public List<ProjectBlock> getProjectBlocksBySubscriptionAndUser(Long subscriptionId, String username,
			Locale locale) {
		log.debug("Try to retrieve project blocks by subscription: {} and user : {}", subscriptionId, username);
		return projectBlockService.findBySubcriptionIdAndUsername(subscriptionId, username);
	}

	public ProjectBlock saveProjectBlock(ProjectBlock projectBlock, Long subcriptionId, String username,
			Locale locale) {
		return projectBlockService.save(projectBlock, subcriptionId, username);
	}

	public void saveProjectBlocks(List<ProjectBlock> projectBlocks, Long subscriptionId, String username,
			Locale locale) {
		projectBlockService.save(projectBlocks, subscriptionId, username);
	}

	/**
	 * delete project block by id
	 * 
	 * @param projectBlockId
	 * @param locale
	 */
	public void deleteProjectBlock(Long projectBlockId, Locale locale) {
		log.debug("Try to delete project block by ID: {}", projectBlockId);
		projectBlockService.deleteById(projectBlockId);
	}

	/**
	 * Builds data source
	 * 
	 * @param tenantId Database identifier
	 */
	private void buildDataSource(String tenantId) {
		log.debug("Try to build data source for tenant : {}", tenantId);
		try {
			if (!this.bcephalDataSources.containsKey(tenantId)) {
				DataSource Defalutsource = bcephalDataSources.get(CurrentTenantIdentifierResolverImpl.DEFAULT_TENANT);
				DataBaseUtils utils = new DataBaseUtils(properties, Defalutsource, hikari);
				DataSource source = utils.buildDataSource(tenantId);
				utils.updateDbSchema(source, "classpath:sql");
				this.bcephalDataSources.put(tenantId, source);
			}
			TenantContext.setCurrentTenant(tenantId);
			log.debug("Data base {} : data source successfully builded!", tenantId);
		} catch (Exception e) {
			log.error("Unable to build data source for tenant: {}", tenantId, e);
			throw e;
		}
	}

	/**
	 * Builds unique code for a project.
	 * 
	 * @param name The project name
	 * @return the builded code
	 */
	private String buildNewProjectCode(String name, Long subscriptionId) {
		log.debug("Build code for project : {}", name);
		String code = "P-" + subscriptionId + "-" + SpecialCharUtils.replaceAllSpecialChars(name, "") + "-"
				+ new RandomString(3).nextString();
		log.debug("Project code successfully builded : {}", code);
		return code;
	}

	public Project updateProject(Long subscriptionId, Project project, Locale locale) {
		String projectName = project.getName();
		project.setSubscriptionId(subscriptionId);
		log.debug("Try to update project : {}", projectName);
		try {
			if (project.getId() == null) {
				String message = messageSource.getMessage("unable.to.rename.project.with.empty.code",
						new Object[] { project.getId() }, locale);
				throw new BcephalException(message);
			}
			if (!StringUtils.hasText(projectName)) {
				String message = messageSource.getMessage("unable.to.rename.project.with.empty.name",
						new String[] { projectName }, locale);
				throw new BcephalException(message);
			}
			Optional<Project> projects = projectService.findById(project.getId());
			String OldprojectName = projects.get().getName();
			projectService.save(project);
			if (!projectName.equals(OldprojectName)) {
				String projectPath = FilenameUtils.concat(projectFileUtil.getDataDir(), OldprojectName);
				Path source = Paths.get(projectPath);
				if (!source.toFile().exists()) {
					source.toFile().mkdirs();
					log.debug("Project folder successfully updated: {} - {}", project.getName(), projectPath);
					log.debug("Try to build bcephal project file identity: {}", project.getName());
					projectFileUtil.writeBcephalProjectFile(projectPath, project.getCode(), project.getName());
					log.debug("Bcephal project file identity successfully updated: {}", project.getName());
					log.debug("Project successfully updated : \n- Name : {}\n- Code : {}", project.getName(),
							project.getCode());
				}
				Files.move(source, source.resolveSibling(projectName));
			}
			log.debug("Project successfully renamed from : {} to : {}", project.getId(), projectName);
			return project;
		} catch (BcephalException e) {
			throw e;
		} catch (Exception e) {
			log.error("Unexpected error while renaming project : {}", project.getId(), e);
			String message = messageSource.getMessage("unable.to.rename.project", new Object[] { project.getId() },
					locale);
			throw new BcephalException(HttpStatus.INTERNAL_SERVER_ERROR.value(), message);
		}
	}

	public void updateUserSessionLog(String projectCode, java.util.Locale locale, String username, Long clientId, HttpSession session) {
		log.debug("Search project by code {}", projectCode);
		String path = "/users/session-logs/update-user-sesion-log/" + username + "/" + clientId;
		HttpHeaders requestHeaders = getHttpHeaders_in(session, locale);//getHttpHeaders(session, locale);
		log.info("UpdateUserSessionLog request requestHeaders : {}", requestHeaders);
		Project project = projectService.findByCode_(projectCode).get();
		HttpEntity<Project> requestEntity = new HttpEntity<>(project, requestHeaders);
		//ResponseEntity<String> response = restTemplate.exchange(path, HttpMethod.POST, requestEntity, String.class);
		ResponseEntity<String> response = loadBalancedRestTemplate.exchange(securityServiceUri + path, HttpMethod.POST ,requestEntity, String.class);
		
		if (response.getStatusCode() != HttpStatus.OK) {
			log.error("Unable to update user_session_log {}", project);
		}
	}
}
