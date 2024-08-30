package com.moriset.bcephal.etl.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;

@Controller
//@RequestMapping("sourcing-etl/email-filter")
//@Slf4j
public class EmailController {
	
//	@Autowired
//	private EmailFilterService emailFilterService;
	
	@Autowired
	MessageSource messageSource;
	
//	@PostMapping("/search")
//	public ResponseEntity<?> search(@RequestBody BrowserDataFilter filter,
//			@RequestHeader("Accept-Language") java.util.Locale locale,
//			@RequestHeader(RequestParams.BC_PROFILE) Long profileId,
//			@RequestHeader(name= RequestParams.BC_PROJECT,required = false ) String projectCode,
//			JwtAuthenticationToken principal, HttpSession session) {
//
//		log.debug("Call of search");
//		try {
//			filter.setUsername(principal.getName());
//			BrowserDataPage<EmailFilter> page = emailFilterService.search(filter, locale, profileId, projectCode);
//			log.debug("Found : {}", page.getCurrentPage());
//			return ResponseEntity.status(HttpStatus.OK).body(page);
//		} catch (BcephalException e) {
//			log.debug(e.getMessage());
//			return ResponseEntity.status(e.getStatusCode()).body(e.getMessage());
//		} catch (Exception e) {
//			log.error("Unexpected error while search entities by filter : {}", filter, e);
//			String message = messageSource.getMessage("unable.to.search.entity.by.filter", new Object[] { filter },
//					locale);
//			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(message);
//		}
//	}

}
