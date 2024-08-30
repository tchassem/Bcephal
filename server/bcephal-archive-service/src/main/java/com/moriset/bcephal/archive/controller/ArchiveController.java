package com.moriset.bcephal.archive.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.moriset.bcephal.archive.domain.Archive;
import com.moriset.bcephal.archive.domain.ArchiveBrowserData;
import com.moriset.bcephal.archive.service.ArchiveService;
import com.moriset.bcephal.controller.BaseController;

@RestController
@RequestMapping("/archive")
public class ArchiveController extends BaseController<Archive, ArchiveBrowserData> {

	@Autowired
	ArchiveService archiveService;

	@Override
	protected ArchiveService getService() {
		return archiveService;
	}
}
