/**
 * 4 avr. 2024 - ReconciliationUnionModelGridService.java
 *
 */
package com.moriset.bcephal.reconciliation.service;

import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.moriset.bcephal.domain.BrowserData;
import com.moriset.bcephal.grid.domain.Grille;
import com.moriset.bcephal.grid.domain.GrilleType;
import com.moriset.bcephal.grid.domain.UnionGrid;
import com.moriset.bcephal.grid.service.GrilleService;
import com.moriset.bcephal.grid.service.UnionGridService;
import com.moriset.bcephal.reconciliation.domain.ReconciliationUnionModelGrid;
import com.moriset.bcephal.reconciliation.repository.ReconciliationUnionModelGridRepository;
import com.moriset.bcephal.repository.PersistentRepository;
import com.moriset.bcephal.service.PersistentService;
import com.moriset.bcephal.utils.BcephalException;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Emmanuel Emmeni
 *
 */
@Service
@Slf4j
public class ReconciliationUnionModelGridService extends PersistentService<ReconciliationUnionModelGrid, BrowserData> {

	@Autowired
	ReconciliationUnionModelGridRepository reconciliationUnionModelGridRepository;
	@Autowired
	UnionGridService unionGridService;
	
	@Autowired
	GrilleService grilleService;
	
	@Override
	public PersistentRepository<ReconciliationUnionModelGrid> getRepository() {
		return reconciliationUnionModelGridRepository;
	} 
	
	@Transactional
	public ReconciliationUnionModelGrid save(ReconciliationUnionModelGrid reconciliationUnionModelGrid, Locale locale) {
		log.debug("Try to  Save reconciliationUnionModelGrid : {}", reconciliationUnionModelGrid);		
		try {	
			if(reconciliationUnionModelGrid == null) {
				String message = getMessageSource().getMessage("unable.to.save.null.reconciliation.union.model.grid", new Object[]{reconciliationUnionModelGrid} , locale);
				throw new BcephalException(message);
			}
			if(reconciliationUnionModelGrid.isUnion()) {
				reconciliationUnionModelGrid.getGrid().setGridType(GrilleType.RECONCILIATION);
				UnionGrid unionGrid = unionGridService.save(reconciliationUnionModelGrid.getGrid(), locale);
				reconciliationUnionModelGrid.setGrid(unionGrid);
				
				Grille grille = reconciliationUnionModelGrid.getGrille();
				if(grille != null) {
					grilleService.delete(grille);
					reconciliationUnionModelGrid.setGrille(null);
				}
			}
			else {
				reconciliationUnionModelGrid.getGrille().setType(GrilleType.RECONCILIATION);
				Grille grille = grilleService.save(reconciliationUnionModelGrid.getGrille(), locale);
				reconciliationUnionModelGrid.setGrille(grille);
				
				UnionGrid unionGrid = reconciliationUnionModelGrid.getGrid();
				if(unionGrid != null) {
					unionGridService.delete(unionGrid);
					reconciliationUnionModelGrid.setGrid(null);
				}
			}
			
			
			reconciliationUnionModelGrid = getRepository().save(reconciliationUnionModelGrid);
			Long id = reconciliationUnionModelGrid.getId();
			
			
			log.debug("reconciliationUnionModelGrid saved : {} ", id);
	        return reconciliationUnionModelGrid;	
		}
		catch (BcephalException e) {
			throw e;
		}
		catch (Exception e) {
			log.error("Unexpected error while save reconciliationUnionModelGrid : {}", reconciliationUnionModelGrid, e);
			String message = getMessageSource().getMessage("unable.to.save.reconciliation.union.model.grid", new Object[]{reconciliationUnionModelGrid} , locale);
			throw new BcephalException(HttpStatus.INTERNAL_SERVER_ERROR.value(), message);
		}
	}
	
	
	@Override
	public void delete(ReconciliationUnionModelGrid reconciliationUnionModelGrid) {
		log.debug("Try to delete reconciliationUnionModelGrid : {}", reconciliationUnionModelGrid);	
		if(reconciliationUnionModelGrid == null || reconciliationUnionModelGrid.getId() == null) {
			return;
		}
		if(reconciliationUnionModelGrid.getGrid() != null) {
			unionGridService.delete(reconciliationUnionModelGrid.getGrid());
		}		
		if(reconciliationUnionModelGrid.getGrille() != null) {
			grilleService.delete(reconciliationUnionModelGrid.getGrille());
		}
		getRepository().deleteById(reconciliationUnionModelGrid.getId());
		log.debug("reconciliationUnionModelGrid successfully to delete : {} ", reconciliationUnionModelGrid);
	    return;	
	}

}
