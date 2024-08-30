/**
 * 
 */
package com.moriset.bcephal.project.controller;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.sql.DataSource;

import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.context.MessageSource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.moriset.bcephal.config.messaging.ConsumerListener;
import com.moriset.bcephal.config.messaging.Infos;
import com.moriset.bcephal.config.messaging.MessengerClient;
import com.moriset.bcephal.domain.BcephalVersion;
import com.moriset.bcephal.domain.BrowserDataPage;
import com.moriset.bcephal.domain.EditorData;
import com.moriset.bcephal.domain.EditorDataFilter;
import com.moriset.bcephal.domain.filters.BrowserDataFilter;
import com.moriset.bcephal.multitenant.jpa.MultiTenantInterceptor;
import com.moriset.bcephal.project.archive.ProjectFileUtil;
import com.moriset.bcephal.project.service.ProjectManager;
import com.moriset.bcephal.security.domain.Project;
import com.moriset.bcephal.security.domain.ProjectBlock;
import com.moriset.bcephal.security.domain.ProjectBrowserData;
import com.moriset.bcephal.security.domain.UserWorkspace;
import com.moriset.bcephal.security.service.ProjectService;
import com.moriset.bcephal.utils.BCephalParameterTest;
import com.moriset.bcephal.utils.BcephalException;
import com.moriset.bcephal.utils.RequestParams;
import com.moriset.bcephal.utils.socket.WebSocketDataTransfert;

import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;

/**
 * Project controller. REST resources for project.
 * 
 * @author Joseph Wambo
 *
 */
@RestController
@RequestMapping("/projects")
@Slf4j
public class ProjectController {

	@Autowired
	MessageSource messageSource;

	@Autowired
	ProjectManager projectManager;

	@Autowired
	ProjectService projectService;

	@Autowired
	Map<String, DataSource> bcephalDataSources;

	@Autowired
	DataSourceProperties properties;

	@Autowired
	ProjectFileUtil projectFileUtil;

	@Autowired
	ObjectMapper mapper;
	
	
	@GetMapping("/hello")
	@Override
	public String toString() {	
		return "ProjectController...";
	}

	@PostMapping("/editor-data")
	public ResponseEntity<?> getEditorData(@RequestBody EditorDataFilter filter,
			@RequestHeader("Accept-Language") java.util.Locale locale) {

		log.debug("Call of get editor data");
		try {
			EditorData<Project> data = projectService.getEditorData(filter, locale);
			log.debug("Found : {}", data.getItem());
			return ResponseEntity.status(HttpStatus.OK).body(data);
		} catch (BcephalException e) {
			log.debug(e.getMessage());
			return ResponseEntity.status(e.getStatusCode()).body(e.getMessage());
		} catch (Exception e) {
			log.error("Unexpected error while retrieving editor data : {}", filter, e);
			String message = messageSource.getMessage("unable.to.get.editor.data", new Object[] { filter }, locale);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(message);
		}
	}

	@PostMapping("/save")
	public ResponseEntity<?> save(@RequestBody Project project,
			@RequestHeader(RequestParams.BC_CLIENT) Long clientId, @RequestHeader(RequestParams.LANGUAGE) java.util.Locale locale) {
		log.debug("Call of save project : {}", project);
		try {
			project.setSubscriptionId(clientId);
			projectService.save(project);
			log.debug("project : {}", project);
			return ResponseEntity.status(HttpStatus.OK).body(project);
		} catch (BcephalException e) {
			log.debug(e.getMessage());
			return ResponseEntity.status(e.getStatusCode()).body(e.getMessage());
		} catch (Exception e) {
			log.error("Unexpected error while save project : {}", project, e);
			String message = messageSource.getMessage("unable.to.save.project", new Object[] { project }, locale);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(message);
		}
	}

	@GetMapping("/{id}")
	public ResponseEntity<?> getById(@PathVariable("id") Long id,
			@RequestHeader("Accept-Language") java.util.Locale locale) {
		log.debug("Call of get by ID : {}", id);
		try {
			Optional<Project> result = projectService.findById(id);
			if (!result.isEmpty()) {
				Project project = result.isEmpty() ? null : result.get();
				log.debug("Found project : {}", project);
				return ResponseEntity.status(HttpStatus.OK).body(project);
			}
			throw new BcephalException(HttpStatus.NOT_FOUND.value(), "Project not found: " + id);
		} catch (BcephalException e) {
			log.debug(e.getMessage());
			return ResponseEntity.status(e.getStatusCode()).body(e.getMessage());
		} catch (Exception e) {
			log.error("Unexpected error while retrieving project with id : {}", id, e);
			String message = messageSource.getMessage("unable.to.retieve.project.by.id", new Object[] { id }, locale);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(message);
		}
	}
	
	@GetMapping("/version")
	public ResponseEntity<?> getBcephalVersion() {
		log.debug("Call of get bcephal version ");
		return ResponseEntity.status(HttpStatus.OK).body(new BcephalVersion());
	}

	/**
	 * 
	 * @param locale
	 * @return
	 */
	@GetMapping("/user-workspace")
	public ResponseEntity<?> getUserWorkspaceNative(JwtAuthenticationToken principal,
			@RequestHeader(RequestParams.BC_CLIENT) Long subscriptionId,
			@RequestHeader(RequestParams.BC_PROFILE) Long profileId,
			@RequestHeader(RequestParams.LANGUAGE) java.util.Locale locale) {
		log.debug("Call of getUserWorkspace...");
		return getUserWorkspace(principal, subscriptionId, profileId, locale);
	}

	/**
	 * 
	 * @param subscriptionId
	 * @param locale
	 * @return
	 */
	@GetMapping("/user-workspace/{subscriptionId}")
	public ResponseEntity<?> getUserWorkspace(JwtAuthenticationToken principal,
			@PathVariable("subscriptionId") Long subscriptionId,
			@RequestHeader(RequestParams.BC_PROFILE) Long profileId,
			@RequestHeader(RequestParams.LANGUAGE) java.util.Locale locale) {
		log.debug("Call of getUserWorkspace...");
		try {
			UserWorkspace workspace = projectManager.getUserWorkspace(subscriptionId, profileId, principal.getName(), locale);
			return ResponseEntity.status(HttpStatus.OK).body(workspace);
		} catch (BcephalException e) {
			log.debug(e.getMessage());
			return ResponseEntity.status(e.getStatusCode()).body(e.getMessage());
		} catch (Exception e) {
			log.error("Unexpected error while loading user workqpace...", e);
			String message = messageSource.getMessage("unable.to.load.user.workspace", new String[] {}, locale);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(message);
		}
	}

	/**
	 * Opens project.
	 * 
	 * @param projectName The project name
	 * @param locale      User locale
	 * @return
	 */
	@PostMapping("/open")
	public ResponseEntity<?> openProject(@RequestBody String projectCode,
			@RequestParam(RequestParams.BC_CLIENT) Long clientId_,
			@RequestHeader(RequestParams.LANGUAGE) java.util.Locale locale, JwtAuthenticationToken principal, HttpSession session) {
		log.debug("Call of openProject : {}", projectCode);
		try {
			long clientId = Long.valueOf(clientId_);
			String opened = projectManager.openProject(projectCode, clientId, locale, principal.getName());
			projectManager.updateUserSessionLog(projectCode, locale, principal.getName(), clientId,session);
			
			return ResponseEntity.status(HttpStatus.OK).header(MultiTenantInterceptor.TENANT_HEADER_NAME, opened)
					.body(opened);
		} catch (BcephalException e) {
			log.debug(e.getMessage());
			return ResponseEntity.status(e.getStatusCode()).body(e.getMessage());
		} catch (Exception e) {
			log.error("Unexpected error while opening project : {}", projectCode, e);
			String message = messageSource.getMessage("unable.to.open.project", new String[] { projectCode }, locale);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(message);
		}
	}

	@PostMapping("/search")
	public ResponseEntity<?> search(@RequestBody BrowserDataFilter filter,
			@RequestHeader(RequestParams.BC_CLIENT) Long clientId,
			@RequestHeader(RequestParams.BC_PROFILE) Long profileId,
			@RequestHeader("Accept-Language") java.util.Locale locale) {

		log.debug("Call of search");
		try {
			BrowserDataPage<ProjectBrowserData> page = projectService.search(filter, clientId, profileId, locale);
			log.debug("Projects found : {}", page.getCurrentPage());
			return ResponseEntity.status(HttpStatus.OK).body(page);
		} catch (BcephalException e) {
			log.debug(e.getMessage());
			return ResponseEntity.status(e.getStatusCode()).body(e.getMessage());
		} catch (Exception e) {
			log.error("Unexpected error while search projects by filter : {}", filter, e);
			String message = messageSource.getMessage("unable.to.search.projects.by.filter", new Object[] { filter },
					locale);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(message);
		}
	}

	/**
	 * 
	 * @param subscriptionId
	 * @param locale
	 * @return
	 */
	@GetMapping("/{subscriptionId}/{default-project}")
	public ResponseEntity<?> getProjects(JwtAuthenticationToken principal,
			@PathVariable("subscriptionId") Long subscriptionId,
			@PathVariable("default-project") Boolean defaultProject,
			@RequestHeader(RequestParams.BC_PROFILE) Long profileId,
			@RequestHeader(RequestParams.LANGUAGE) java.util.Locale locale) {
		log.debug("Call of getProjects...");
		try {
			List<ProjectBrowserData> projects = projectService.findBySubcriptionAndDefault(subscriptionId, profileId,
					defaultProject, principal.getName());
			return ResponseEntity.status(HttpStatus.OK).body(projects);
		} catch (BcephalException e) {
			log.debug(e.getMessage());
			return ResponseEntity.status(e.getStatusCode()).body(e.getMessage());
		} catch (Exception e) {
			log.error("Unexpected error while loading projects...", e);
			String message = messageSource.getMessage("unable.to.search.projects", new String[] {}, locale);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(message);
		}
	}

	/**
	 * Retrieves new project name.
	 * 
	 * @param subscriptionId
	 * @param locale         User locale
	 * @return New project name
	 */
	@GetMapping("/new-project-name")
	@ResponseStatus(HttpStatus.OK)
	public ResponseEntity<?> getNewProjectName(@RequestParam(RequestParams.BC_CLIENT) Long subscriptionId,
			@RequestHeader(RequestParams.LANGUAGE) java.util.Locale locale) {
		log.trace("Call getNewProjectName for client: {}", subscriptionId);
		try {
			String name = projectManager.buildNewProjectName(subscriptionId, locale);
			;
			return ResponseEntity.status(HttpStatus.OK).body(name);
		} catch (BcephalException e) {
			log.debug(e.getMessage());
			return ResponseEntity.status(e.getStatusCode()).body(e.getMessage());
		} catch (Exception e) {
			log.error("Unexpected error while computing new project name", e);
			String message = messageSource.getMessage("unable.to.retrieve.new.project.name", new String[] {}, locale);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(message);
		}
	}

	/**
	 * Creates a new project.
	 * 
	 * @param projectName The name of the project to create
	 * @param locale      User locale
	 * @return Created project
	 */
	@PostMapping("/create")
	@ResponseStatus(HttpStatus.CREATED)
	public ResponseEntity<?> createProject(@RequestParam(RequestParams.BC_CLIENT) Long subscriptionId,
			@RequestHeader(RequestParams.BC_PROFILE) Long profileId,
			@RequestBody Project project, @RequestHeader(RequestParams.LANGUAGE) java.util.Locale locale
			,HttpSession session,@RequestHeader HttpHeaders headers) {
		log.trace("Call createProject : {}", project.getName());
		try {
			session.setAttribute(MultiTenantInterceptor.CUSTOM_HEADER_NAME, headers);
			project.setName(projectManager.normalizeProjectName(project.getName()));
			project = projectManager.createProject(profileId,session, subscriptionId, project, locale, null);
			if(!BCephalParameterTest.PROJECT_CODE.equals(project.getName())) {
				projectManager.SaveRightToProject(project, profileId, session, locale);
			}
			return ResponseEntity.status(HttpStatus.CREATED).body(project);
		} catch (BcephalException e) {
			log.debug(e.getMessage());
			return ResponseEntity.status(e.getStatusCode()).body(e.getMessage());
		} catch (Exception e) {
			log.error("Unexpected error while creating the project : {}", project.getName(), e);
			String message = messageSource.getMessage("unable.to.create.project", new String[] { project.getName() },
					locale);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(message);
		}
	}

	@PostMapping("/update")
	@ResponseStatus(HttpStatus.OK)
	public ResponseEntity<?> updateProject(@RequestParam(RequestParams.BC_CLIENT) Long subscriptionId,
			@RequestBody Project project, @RequestHeader(RequestParams.LANGUAGE) java.util.Locale locale) {
		log.trace("Call update Project : {}", project.getName());
		try {
			project.setName(projectManager.normalizeProjectName(project.getName()));
			project = projectManager.updateProject(subscriptionId, project, locale);
			return ResponseEntity.status(HttpStatus.CREATED).body(project);
		} catch (BcephalException e) {
			log.debug(e.getMessage());
			return ResponseEntity.status(e.getStatusCode()).body(e.getMessage());
		} catch (Exception e) {
			log.error("Unexpected error while updating the project : {}", project.getName(), e);
			String message = messageSource.getMessage("unable.to.update.project", new String[] { project.getName() },
					locale);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(message);
		}
	}

	/**
	 * Renames a project.
	 * 
	 * @param projectId      The code of the project to rename
	 * @param newProjectName The new project name
	 * @param locale         User locale
	 * @return Renamed project
	 */
	@PostMapping("/rename/{subscriptionId}/{projectId}")
	@ResponseStatus(HttpStatus.OK)
	public ResponseEntity<?> renameProject(@PathVariable("projectId") Long projectId,
			@PathVariable("subscriptionId") Long clientId, @RequestBody String newProjectName,
			@RequestHeader(RequestParams.LANGUAGE) java.util.Locale locale) {
		log.debug("Call of renameProject : {}", projectId);
		try {
			newProjectName = projectManager.normalizeProjectName(newProjectName);
			Project project = projectManager.renameProject(projectId, newProjectName, clientId, locale);
			return ResponseEntity.status(HttpStatus.OK).body(project);
		} catch (BcephalException e) {
			log.debug(e.getMessage());
			return ResponseEntity.status(e.getStatusCode()).body(e.getMessage());
		} catch (Exception e) {
			log.error("Unexpected error while renaming the project : {}", projectId, e);
			String message = messageSource.getMessage("unable.to.rename.project", new Object[] { projectId }, locale);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(message);
		}
	}

	/**
	 * Deletes a project.
	 * 
	 * @param projectCode The code of the project to delete
	 * @param locale      User locale
	 * @return Deletion status
	 */
	@DeleteMapping("/delete/{projectId}")
	@ResponseStatus(HttpStatus.OK)
	public ResponseEntity<?> deleteProject(@PathVariable("projectId") Long projectId,
			@RequestHeader(RequestParams.LANGUAGE) java.util.Locale locale) {
		log.debug("Call of deleteProject id : {}", projectId);
		try {
			String projectCode = projectService.findById(projectId).get().getCode();
			if(receiverClient != null) {
				receiverClient.SendMessage(new Infos(projectCode, ConsumerListener.CLOSE_PROJECT));
			}
			Thread.sleep(1000);
			boolean deleted = projectManager.deleteProject(projectId, locale);
			return ResponseEntity.status(HttpStatus.OK).body(deleted);
		} catch (BcephalException e) {
			log.debug(e.getMessage());
			return ResponseEntity.status(e.getStatusCode()).body(e.getMessage());
		} catch (Exception e) {
			log.error("Unexpected error while deleting the project : {}", projectId, e);
			String message = messageSource.getMessage("unable.to.delete.project", new Object[] { projectId }, locale);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(message);
		}
	}
	
	
	@PostMapping("/delete-project-if-exist")
	@ResponseStatus(HttpStatus.OK)
	public ResponseEntity<?> deleteProjectIfExixt(
			@RequestBody String projectCode,
			@RequestHeader(RequestParams.BC_CLIENT) Long subscriptionId,
			@RequestHeader(RequestParams.LANGUAGE) java.util.Locale locale) {
		log.info("deleteProjectIfExixt : {}", projectCode);
		try {
			List<ProjectBrowserData> response = projectService.findBySubcriptionAndName(subscriptionId, projectCode);
			log.info("Size : {}", response.size());
			if(response.size() > 0) {				
				if(receiverClient != null) {
					receiverClient.SendMessage(new Infos(projectCode, ConsumerListener.CLOSE_PROJECT));
				}
				Thread.sleep(1000);
				boolean deleted = projectManager.deleteProject(response.get(0).getId(), locale);
				return ResponseEntity.status(HttpStatus.OK).body(deleted);
			}
			return ResponseEntity.status(HttpStatus.OK).body(true);
		} catch (BcephalException e) {
			log.debug(e.getMessage());
			return ResponseEntity.status(e.getStatusCode()).body(e.getMessage());
		} catch (Exception e) {
			log.error("Unexpected error while deleting the project : {}", projectCode, e);
			String message = messageSource.getMessage("unable.to.delete.project", new Object[] { projectCode }, locale);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(message);
		}
	}


	@Autowired
	ObjectMapper objectMapper;
	
	@Autowired(required = false)
	MessengerClient receiverClient;

	/**
	 * Closes project.
	 * 
	 * @param projectCode The project name
	 * @param locale      User locale
	 * @return
	 */
	@PostMapping("/close")
	public ResponseEntity<?> closeProject(@RequestBody String projectCode,
			@RequestHeader(RequestParams.LANGUAGE) java.util.Locale locale) {
		log.debug("Call of closeProject : {}", projectCode);
		try {
			if (projectCode != null && projectCode.contains("\"")) {
				projectCode = objectMapper.readValue(projectCode, String.class);
			}
			boolean opened = projectManager.closeProject(projectCode, locale);
			if(receiverClient != null) {
				receiverClient.SendMessage(new Infos(projectCode, ConsumerListener.CLOSE_PROJECT));
			}
			Thread.sleep(100);
			return ResponseEntity.status(HttpStatus.OK).body(opened);
		} catch (BcephalException e) {
			log.debug(e.getMessage());
			return ResponseEntity.status(e.getStatusCode()).body(e.getMessage());
		} catch (Exception e) {
			log.error("Unexpected error while closing project : {}", projectCode, e);
			String message = messageSource.getMessage("unable.to.close.project", new String[] { projectCode }, locale);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(message);
		}
	}

	/**
	 * Saves project block
	 * 
	 * @param principal
	 * @param projectBlock
	 * @param locale
	 * @return
	 */
	@PostMapping("/save-project-block")
	public ResponseEntity<?> saveProjectBlock(JwtAuthenticationToken principal,
			@RequestParam(RequestParams.BC_CLIENT) Long subscriptionId, @RequestBody ProjectBlock projectBlock,
			@RequestHeader(RequestParams.LANGUAGE) java.util.Locale locale) {
		log.debug("Call of saveProjectBlock : {}", projectBlock);
		try {
			ProjectBlock block = projectManager.saveProjectBlock(projectBlock, subscriptionId, principal.getName(),
					locale);
			return ResponseEntity.status(HttpStatus.OK).body(block);
		} catch (BcephalException e) {
			log.debug(e.getMessage());
			return ResponseEntity.status(e.getStatusCode()).body(e.getMessage());
		} catch (Exception e) {
			log.error("Unexpected error to save project Block : {}", projectBlock, e);
			String message = messageSource.getMessage("unable.to.save.project.block",
					new String[] { projectBlock.getName() }, locale);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(message);
		}
	}

	@PostMapping("/save-project-blocks")
	public ResponseEntity<?> saveProjectBlocks(JwtAuthenticationToken principal,
			@RequestParam(RequestParams.BC_CLIENT) Long subscriptionId, @RequestBody List<ProjectBlock> projectBlocks,
			@RequestHeader(RequestParams.LANGUAGE) java.util.Locale locale) {
		log.debug("Call of saveProjectBlocks : {}", projectBlocks);
		try {
			projectManager.saveProjectBlocks(projectBlocks, subscriptionId, principal.getName(), locale);
			return ResponseEntity.status(HttpStatus.OK).body(true);
		} catch (BcephalException e) {
			log.debug(e.getMessage());
			return ResponseEntity.status(e.getStatusCode()).body(e.getMessage());
		} catch (Exception e) {
			log.error("Unexpected error to save project Blocks : {}", projectBlocks, e);
			String message = messageSource.getMessage("unable.to.save.project.blocks",
					new Object[] { projectBlocks.size() }, locale);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(message);
		}
	}

	@GetMapping("/delete-project-block/{projectBlockId}")
	public ResponseEntity<?> deleteProjectBlock(@PathVariable("projectBlockId") Long projectBlockId,
			@RequestHeader(RequestParams.LANGUAGE) java.util.Locale locale) {
		log.debug("Call of deleteProjectBlock : {}", projectBlockId);
		try {
			projectManager.deleteProjectBlock(projectBlockId, locale);
			return ResponseEntity.status(HttpStatus.OK).body(true);
		} catch (BcephalException e) {
			log.debug(e.getMessage());
			return ResponseEntity.status(e.getStatusCode()).body(e.getMessage());
		} catch (Exception e) {
			log.error("Unexpected error while delete project block on workqpace...", e);
			String message = messageSource.getMessage("unable.to.delete.load.user.workspace", new String[] {}, locale);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(message);
		}
	}

	@GetMapping("/exists-by-name")
	public ResponseEntity<?> existsByName(@RequestParam("value") String projectName,
			@RequestHeader(RequestParams.LANGUAGE) java.util.Locale locale) {
		log.debug("Call of existsByName : {}", projectName);
		try {
			boolean exist = projectService.existsByName(projectName);
			return ResponseEntity.status(HttpStatus.OK).body(exist);
		} catch (BcephalException e) {
			log.debug(e.getMessage());
			return ResponseEntity.status(e.getStatusCode()).body(e.getMessage());
		} catch (Exception e) {
			log.error("Unexpected error while checking if project exist by name...", e);
			String message = messageSource.getMessage("unable.to.check.project.exist.name", new String[] {}, locale);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(message);
		}
	}

	@Autowired
	ResourceLoader resourceLoader;

	@GetMapping
	public ResponseEntity<?> hello() {
		return ResponseEntity.ok("B-CEPHAL : Project REST controller.");
	}
	
	@PostMapping
	public ResponseEntity<?> helloPost(
			@RequestParam(RequestParams.BC_CLIENT) Long subscriptionId,
			@RequestHeader(RequestParams.BC_PROFILE) Long profileId,
			@RequestHeader(RequestParams.LANGUAGE) java.util.Locale locale,
			@RequestBody Project project
			) {
		return ResponseEntity.ok("B-CEPHAL : Project REST controller.");
	}

	@Value("${bcephal.project.temp-dir}")
	protected String tempDir;

	@PostMapping(path = "/upload-by-data/resume", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity<?> uploadWithResume(@RequestPart("file") MultipartFile file) throws Exception {
		try {
			WebSocketDataTransfert dataTransfert = mapper.readValue(file.getBytes(), WebSocketDataTransfert.class);
			WebSocketDataTransfert reponseData = new WebSocketDataTransfert();
			reponseData.setName(dataTransfert.getName());
			if (dataTransfert.getDecision() != null && dataTransfert.getDecision().isNew()) {
				Path path2 = Paths.get(tempDir, dataTransfert.getFolder(),
						dataTransfert.getName());
				if (!path2.getParent().toFile().exists()) {
					path2.getParent().toFile().mkdirs();	
					try {
						path2.getParent().toFile().deleteOnExit();
						} catch (Exception e) {
						}
				}
				try {
					path2.toFile().deleteOnExit();
					} catch (Exception e) {
					}
				reponseData.setRemotePath(path2.getParent().normalize().toString());				
			}else {
				reponseData.setRemotePath(dataTransfert.getRemotePath());
			}
			if (dataTransfert.getData().length > 0) {
				Path path2 = Paths.get(reponseData.getRemotePath(), dataTransfert.getName());
				log.debug(" Download data from {}", path2.toString());
				FileUtils.writeByteArrayToFile(path2.toFile(), dataTransfert.getData(), true);
			}
			return ResponseEntity.ok(reponseData);
		} catch (IOException e) {
			log.error("io exception", e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
	}
}
