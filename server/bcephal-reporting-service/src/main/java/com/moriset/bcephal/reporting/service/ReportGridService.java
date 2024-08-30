/**
 * 
 */
package com.moriset.bcephal.reporting.service;

import org.springframework.stereotype.Service;

import com.moriset.bcephal.grid.domain.Grille;
import com.moriset.bcephal.grid.domain.GrilleType;
import com.moriset.bcephal.grid.service.GrilleService;
import com.moriset.bcephal.utils.FunctionalityCodes;

/**
 * @author Joseph Wambo
 *
 */
@Service
public class ReportGridService extends GrilleService {

	@Override
	protected Grille getNewItem() {
		Grille grid = new Grille();
		grid.setType(GrilleType.REPORT);
		grid.setConsolidated(true);
		String baseName = "Report Grid ";
		int i = 1;
		grid.setName(baseName + i);
		while (getByName(grid.getName()) != null) {
			i++;
			grid.setName(baseName + i);
		}
		return grid;
	}

	@Override
	protected GrilleType getGrilleType() {
		return GrilleType.REPORT;
	}

	@Override
	protected String getBrowserFunctionalityCode() {
		return FunctionalityCodes.REPORTING_REPORT_GRID;
	}

}
