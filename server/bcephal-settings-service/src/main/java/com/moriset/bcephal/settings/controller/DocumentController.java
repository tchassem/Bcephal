package com.moriset.bcephal.settings.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.moriset.bcephal.controller.BaseController;
import com.moriset.bcephal.domain.Document;
import com.moriset.bcephal.domain.DocumentBrowserData;
import com.moriset.bcephal.service.DocumentService;
import com.moriset.bcephal.service.MainObjectService;


@RestController
@RequestMapping("/settings/document")
public class DocumentController extends BaseController<Document, DocumentBrowserData>{
	
	@Autowired
	DocumentService documentService;
	
	@Override
	protected MainObjectService<Document, DocumentBrowserData> getService() {
		return documentService;
	}

}
