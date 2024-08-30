package com.moriset.bcephal.project.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.moriset.bcephal.project.service.FunctionalityManager;
import com.moriset.bcephal.security.domain.FunctionalityBlockGroup;
import com.moriset.bcephal.security.domain.FunctionalityWorkspace;
import com.moriset.bcephal.security.service.FunctionalityBlockGroupService;
import com.moriset.bcephal.utils.BcephalException;

import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/functionalities")
@Slf4j
public class FunctionalityController {

	@Autowired
	private FunctionalityManager functionalityManager;

	@Autowired
	private FunctionalityBlockGroupService functionalityBlockGroupService;

	@Autowired
	MessageSource messageSource;

	/**
	 * 
	 * @param locale
	 * @return
	 */
	@GetMapping("/functionality-workspace")
	public ResponseEntity<?> getfunctionalitiesWorkspace(JwtAuthenticationToken principal,
			@RequestHeader("Accept-Language") java.util.Locale locale) {
		log.debug("Call of getfunctionalitiesWorkspace...");
		return getFunctionalityWorkspace(principal, null, locale);
	}

	/**
	 * 
	 * @param projectId
	 * @param locale
	 * @return
	 */
	@GetMapping("/functionality-workspace/{projectId}")
	public ResponseEntity<?> getFunctionalityWorkspace(JwtAuthenticationToken principal,
			@PathVariable("projectId") Long projectId, @RequestHeader("Accept-Language") java.util.Locale locale) {
		log.debug("Call of getFunctionalityWorkspace...");
		try {
			FunctionalityWorkspace workspace = functionalityManager.getFunctionalityWorkspace(projectId,
					principal.getName(), locale);
			return ResponseEntity.status(HttpStatus.OK).body(workspace);
		} catch (BcephalException e) {
			log.debug(e.getMessage());
			return ResponseEntity.status(e.getStatusCode()).body(e.getMessage());
		} catch (Exception e) {
			log.error("Unexpected error while loading functionality workspace...", e);
			String message = messageSource.getMessage("unable.to.load.user.workspace", new String[] {}, locale);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(message);
		}
	}

	@PostMapping("/save-functionality-block-group")
	public ResponseEntity<?> saveGroup(JwtAuthenticationToken principal, @RequestParam("BC-CLIENT") Long projectId,
			@RequestBody FunctionalityBlockGroup group, @RequestHeader("Accept-Language") java.util.Locale locale) {
		log.debug("Call of saveFunctionalityBlockGroup : {}", group);
		try {
			FunctionalityBlockGroup block = functionalityBlockGroupService.save(group, projectId, principal.getName());
			return ResponseEntity.status(HttpStatus.OK).body(block);
		} catch (BcephalException e) {
			log.debug(e.getMessage());
			return ResponseEntity.status(e.getStatusCode()).body(e.getMessage());
		} catch (Exception e) {
			log.error("Unexpected error to save functionality Block group : {}", group.getName(), e);
			String message = messageSource.getMessage("unable.to.save.functionqlity.block.group",
					new String[] { group.getName() }, locale);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(message);
		}
	}

	@PostMapping("/get-functionality-block-group-by-id/{id}")
	public ResponseEntity<?> GetGroupById(JwtAuthenticationToken principal, @RequestParam("BC-CLIENT") Long projectId,
			@PathVariable("id") Long groupId, @RequestHeader("Accept-Language") java.util.Locale locale) {
		log.debug("Call of getFunctionalityBlockGroup : {}", groupId);
		try {
			FunctionalityBlockGroup block = functionalityBlockGroupService.GetById(groupId);
			return ResponseEntity.status(HttpStatus.OK).body(block);
		} catch (BcephalException e) {
			log.debug(e.getMessage());
			return ResponseEntity.status(e.getStatusCode()).body(e.getMessage());
		} catch (Exception e) {
			log.error("Unexpected error to get functionality Block group : {}", groupId, e);
			String message = messageSource.getMessage("unable.to.get.functionqlity.block.group",
					new String[] { groupId.toString() }, locale);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(message);
		}
	}

	@GetMapping("/delete-functionality-block-group/{functionalityBlockGroupId}")
	public ResponseEntity<?> deleteGroup(@PathVariable("functionalityBlockGroupId") Long id,
			@RequestHeader("Accept-Language") java.util.Locale locale) {
		log.debug("Call of deletefunctionalityBlockGroup : {}", id);
		try {
			functionalityBlockGroupService.delete(id);
			return ResponseEntity.status(HttpStatus.OK).body(true);
		} catch (BcephalException e) {
			log.debug(e.getMessage());
			return ResponseEntity.status(e.getStatusCode()).body(e.getMessage());
		} catch (Exception e) {
			log.error("Unexpected error while delete functionality block group", e);
			String message = messageSource.getMessage("unable.to.delete.functionality.block.group", new String[] {},
					locale);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(message);
		}
	}

//	/**
//	 * Saves functionality block
//	 * 	
//	 * @param principal
//	 * @param functionalityBlock
//	 * @param locale
//	 * @return
//	 */
//	@PostMapping("/save-functionality-block")
//	public ResponseEntity<?> saveFunctionalityBlock(JwtAuthenticationToken principal, @RequestParam("BC-CLIENT") Long projectId, @RequestBody FunctionalityBlock functionalityBlock
//			, @RequestHeader("Accept-Language") java.util.Locale locale){
//		log.debug("Call of saveFunctionalityBlock : {}", functionalityBlock);		
//		try {
//			FunctionalityBlock block = functionalityManager.saveFunctionalityBlock(functionalityBlock, projectId, principal.getName(), locale);
//	        return ResponseEntity.status(HttpStatus.OK).body(block);	
//		}
//		catch (BcephalException e) {
//			log.debug(e.getMessage());
//			return ResponseEntity.status(e.getStatusCode()).body(e.getMessage());
//		}
//		catch (Exception e) {
//			log.error("Unexpected error to save functionality Block : {}", functionalityBlock, e);
//			String message = messageSource.getMessage("unable.to.save.functionqlity.block", new String[]{functionalityBlock.getName()} , locale);
//			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(message);
//		}
//	}
//	
//	@PostMapping("/save-functionality-blocks")
//	public ResponseEntity<?> saveFunctionalityBlocks(JwtAuthenticationToken principal, @RequestParam("BC-CLIENT") Long projectId, @RequestBody List<FunctionalityBlock> functionalityBlocks, @RequestHeader("Accept-Language") java.util.Locale locale){
//		log.debug("Call of saveFunctionalityBlocks : {}", functionalityBlocks);		
//		try {
//			functionalityManager.saveFunctionalityBlocks(functionalityBlocks, projectId, principal.getName(), locale);
//	        return ResponseEntity.status(HttpStatus.OK).body(true);	
//		}
//		catch (BcephalException e) {
//			log.debug(e.getMessage());
//			return ResponseEntity.status(e.getStatusCode()).body(e.getMessage());
//		}
//		catch (Exception e) {
//			log.error("Unexpected error to save functionality Blocks : {}", functionalityBlocks, e);
//			String message = messageSource.getMessage("unable.to.save.functionqlity.blocks", new Object[]{functionalityBlocks.size()} , locale);
//			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(message);
//		}
//	}
//	
//	
//	@GetMapping("/delete-functionality-block/{functionalityBlockId}")
//	public ResponseEntity<?> deleteProjectBlock(@PathVariable("functionalityBlockId") Long functionalityBlockId, @RequestHeader("Accept-Language") java.util.Locale locale){
//		log.debug("Call of deletefunctionalityBlock : {}", functionalityBlockId);		
//		try {	
//			functionalityManager.deleteProjectBlock(functionalityBlockId, locale);
//	        return ResponseEntity.status(HttpStatus.OK).body(true);	
//		}
//		catch (BcephalException e) {
//			log.debug(e.getMessage());
//			return ResponseEntity.status(e.getStatusCode()).body(e.getMessage());
//		}
//		catch (Exception e) {
//			log.error("Unexpected error while delete block functionality on workspace...", e);
//			String message = messageSource.getMessage("unable.to.delete.functionality.block.workspace", new String[]{} , locale);
//			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(message);
//		}
//	}

}
