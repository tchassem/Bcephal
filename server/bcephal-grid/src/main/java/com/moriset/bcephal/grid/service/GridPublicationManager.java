/**
 * 
 */
package com.moriset.bcephal.grid.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.moriset.bcephal.domain.BillingParameterCodes;
import com.moriset.bcephal.domain.ReconciliationParameterCodes;
import com.moriset.bcephal.domain.dimension.DimensionType;
import com.moriset.bcephal.domain.parameter.Parameter;
import com.moriset.bcephal.domain.parameter.ParameterType;
import com.moriset.bcephal.grid.domain.Grille;
import com.moriset.bcephal.grid.domain.GrilleColumn;
import com.moriset.bcephal.grid.domain.GrilleDataFilter;
import com.moriset.bcephal.grid.domain.GrilleDimension;
import com.moriset.bcephal.grid.domain.JoinGridType;
import com.moriset.bcephal.grid.repository.GrilleRepository;
import com.moriset.bcephal.grid.repository.JoinGridRepository;
import com.moriset.bcephal.repository.ParameterRepository;
import com.moriset.bcephal.utils.BcephalException;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Joseph Wambo
 *
 */
@Component
@Data
@Slf4j
public class GridPublicationManager {
	
	@PersistenceContext 
	EntityManager entityManager;
	
	@Autowired 
	GrilleRepository grilleRepository;
	
	@Autowired 
	JoinGridRepository joinGridRepository;
		
	@Autowired 
	ParameterRepository parameterRepository;
	
	

	@Transactional
	public void publish(Grille grid) throws Exception {
		if(grid == null) {
			throw new BcephalException("Unable to publish null spot!");
		}
		log.info("Try to publish grid : {}", grid.getName());
		try {
			loadClosures(grid);
			String sql = createQuery(grid);
			log.trace("Publication query : {}", sql);
			Query query = entityManager.createNativeQuery(sql);
			query.executeUpdate();
			
			grid.setPublished(true);
			grilleRepository.save(grid);
			log.info("Grid : {} published!", grid.getName());
		} catch (Exception e) {
			log.error("Unable to publish grid : {}", grid.getName(), e);
			throw new BcephalException("Unable to publish grid : " + grid.getName(), e);
		}		
	}
	
	@Transactional
	public void refresh(Grille grid) throws Exception {
		if(grid == null) {
			throw new BcephalException("Unable to publish null spot!");
		}
		log.info("Try to refresh grid publication : {}", grid.getName());
		try {	
			loadClosures(grid);		
			String sql = refreshQuery(grid);
			log.trace("Refresh query : {}", sql);
			Query query = entityManager.createNativeQuery(sql);
			query.executeUpdate();
			log.info("Grid : {} refreshed!", grid.getName());
		} catch (Exception e) {
			log.error("Unable to refresh grid : {}", grid.getName(), e);
			throw new BcephalException("Unable to refresh grid : " + grid.getName(), e);
		}
	}

	@Transactional
	public void unpublish(Grille grid) throws Exception {
		if(grid == null) {
			throw new BcephalException("Unable to publish null spot!");
		}
		log.debug("Try to unpublish grid : {}", grid.getName());
		try {
			canUnpublish(grid);
			String sql = dropQuery(grid);
			log.trace("Unpublication query : {}", sql);
			Query query = entityManager.createNativeQuery(sql);
			query.executeUpdate();

			grid.setPublished(false);
			grilleRepository.save(grid);
			log.debug("Grid : {} unpublished!", grid.getName());
		}  
		catch (BcephalException ex) {
			throw ex;
		}
		catch (Exception e) {
			log.error("Unable to unpublish grid : {}", grid.getName(), e);
			throw new BcephalException("Unable to unpublish grid : " + grid.getName(), e);
		}
	}
	
	
	@SuppressWarnings("unchecked")
	private void canUnpublish(Grille grid) {
		log.debug("Chech if reset publlication is allowed for grid : {}", grid.getName());
		try {			
			String sql = "SELECT j.name FROM BCP_JOIN_GRID g LEFT JOIN BCP_JOIN j ON g.joinId = j.id WHERE j.published = :published AND g.gridId = :gridId AND g.gridType = :gridType";
			log.trace("Query : {}", sql);
			Query query = entityManager.createNativeQuery(sql);
			query.setParameter("published", true);
			query.setParameter("gridId", grid.getId());
			query.setParameter("gridType", grid.isReport() ? JoinGridType.REPORT_GRID.name() : JoinGridType.GRID.name());
			List<String> joins = query.getResultList();
			if(joins.size() > 0) {
				String message = "You have to reset materialization of following joins berore resetings publication of grid : ";
				String coma = "\n- ";
				for(String join : joins) {
					message += coma + join + ", ";
				}
				throw new BcephalException(message);
			}
		}
		catch (Exception e) {
			throw e;
		}
	}

	private String createQuery(Grille grid) {
		GrilleDataFilter filter = new GrilleDataFilter();	
		filter.setGrid(BuildGridCopy(grid));
        filter.setShowAll(true);       
        filter.setDataSourceType(grid.getDataSourceType());
        filter.setDataSourceId(grid.getDataSourceId());
        InputGridQueryBuilder builder = filter.getGrid().isReporting() ? new ReportGridPublicationQueryBuilder(filter) 
        		: new InputGridPublicationQueryBuilder(filter);	
        if(filter.getGrid().isReport()) {
        	((ReportGridQueryBuilder)builder).setParameterRepository(parameterRepository);
        }
        String sql = builder.buildQuery();
		return "CREATE MATERIALIZED VIEW IF NOT EXISTS " + tableName(grid) + " AS " + sql;
	}
	
	private String refreshQuery(Grille grid) {
		return "REFRESH MATERIALIZED VIEW " + tableName(grid);
	}
	
	private String dropQuery(Grille grid) {
		return "DROP MATERIALIZED VIEW IF EXISTS " + tableName(grid);
	}
	
	private String tableName(Grille grid) {
		return grid.getPublicationTableName();
	}

	public void loadClosures(Grille grid) {
		List<GrilleColumn> columns = grid.getColumnListChangeHandler().getItems();
		Collections.sort(columns, new Comparator<GrilleColumn>() {
			@Override
			public int compare(GrilleColumn o1, GrilleColumn o2) {
				return o1.getPosition() - o2.getPosition();
			}
		});
		grid.setColumns(columns);
		columns.forEach(column -> {
			column.setDataSourceType(grid.getDataSourceType());
			column.setDataSourceId(grid.getDataSourceId());
			});
	}
	
	
	protected Grille BuildGridCopy(Grille grid) {
		Grille copy = new Grille();
		copy.setId(grid.getId());
		copy.setName(grid.getName());
		copy.setType(grid.getType());
		copy.setDataSourceType(grid.getDataSourceType());
		copy.setDataSourceId(grid.getDataSourceId());
		//copy.setUseLink(false);
		copy.setConsolidated(grid.isConsolidated());				
		copy.setAdminFilter(grid.getAdminFilter());		
		copy.setColumns(new ArrayList<GrilleColumn>());
		for (GrilleColumn col : grid.getColumnListChangeHandler().getItems()) {
			copy.getColumns().add(col);
		}
		for (GrilleDimension c : grid.getDimensionListChangeHandler().getItems()) {
			addColumnIfNotExists(copy, c.getType(), c.getDimensionId(), c.getName());
		}
		
		if(grid.isReconciliation() || grid.isPostingEntryRepo() || grid.isBookingRepo()) {
			Parameter parameter = parameterRepository.findByCodeAndParameterType(ReconciliationParameterCodes.RECONCILIATION_DC_ATTRIBUTE, ParameterType.ATTRIBUTE);
			if(parameter != null && parameter.getLongValue() != null) {
				addColumnIfNotExists(copy, DimensionType.ATTRIBUTE, parameter.getLongValue(), "D/C");
			}
		}
		
		if(grid.isBillingEventRepo()) {
			Parameter parameter = parameterRepository.findByCodeAndParameterType(BillingParameterCodes.BILLING_EVENT_STATUS_ATTRIBUTE, ParameterType.ATTRIBUTE);
			if(parameter != null && parameter.getLongValue() != null) {
				addColumnIfNotExists(copy, DimensionType.ATTRIBUTE, parameter.getLongValue(), "Billing event status");
			}
		}
		if(grid.isClientRepo()) {
			Parameter parameter = parameterRepository.findByCodeAndParameterType(BillingParameterCodes.BILLING_ROLE_STATUS_ATTRIBUTE, ParameterType.ATTRIBUTE);
			if(parameter != null && parameter.getLongValue() != null) {
				addColumnIfNotExists(copy, DimensionType.ATTRIBUTE, parameter.getLongValue(), "Status");
			}
		}
		
		return copy;
	}
	

	protected void addColumnIfNotExists(Grille grid, DimensionType type, Long dimensionId, String name) {
		List<GrilleColumn> columns = grid.getColumns(type, dimensionId);
		if(columns.size() == 0) {
			GrilleColumn col = new GrilleColumn();
			col.setPosition(grid.getColumns().size());
			col.setType(type);
			col.setName(name);
			col.setDimensionName(name);
			col.setDimensionId(dimensionId);
			grid.getColumns().add(col);
		}	
	}
	
	
	
}
