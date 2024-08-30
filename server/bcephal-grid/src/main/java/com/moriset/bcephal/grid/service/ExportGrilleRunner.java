package com.moriset.bcephal.grid.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.moriset.bcephal.grid.domain.GrilleExportData;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class ExportGrilleRunner {

	@Autowired
	GrilleService grilleService;
	
	public List<String> export(GrilleExportData data) {
		List<String> paths = new ArrayList<>();
		try {
			if (data.getFilter().getGrid() != null) {
				buildGrid(data);
				paths = grilleService.export(data, null);
			}
		} catch (Exception e) {
			log.error("unexpected error while running file loader : {}", data.getFilter() != null && data.getFilter().getGrid() != null ? data.getFilter().getGrid().getName() : "", e);			
		}
		return paths;
	}
	
	protected void buildGrid(GrilleExportData data) {		
		data.getFilter().setGrid(grilleService.getById(data.getFilter().getGrid().getId()));
	}
	
}
