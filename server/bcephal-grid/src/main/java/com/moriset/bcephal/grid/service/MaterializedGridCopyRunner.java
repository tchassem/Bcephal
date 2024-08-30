package com.moriset.bcephal.grid.service;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

import org.springframework.util.StringUtils;

import com.moriset.bcephal.grid.domain.MaterializedGrid;
import com.moriset.bcephal.grid.domain.MaterializedGridColumn;
import com.moriset.bcephal.grid.domain.MaterializedGridCopyData;
import com.moriset.bcephal.utils.BcephalException;

import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Data
@Slf4j
public class MaterializedGridCopyRunner {

	private MaterializedGridCopyData data;
	
	private boolean stop;
	
	private EntityManager entityManager;
	
	private MaterializedGridService service;
	
	
	
	public int run() {
		int response = -1;
		try {			
			log.debug("Materialized grid : {}  -  Ty to copy rows...", data.getFilter().getGrid().getName());
//			if(listener != null) {				
//				listener.crateSaveInfo(data.getFilter().getGrid());
//				listener.onRunStarted(5);
//			}
			
			orderColumns(data.getFilter().getGrid());
			
			MaterializedGrid targetGrid = null;			
			if(data.isCreateNewGrid()) {
				targetGrid = data.getFilter().getGrid().copy();
				targetGrid.setName(getNewGridName());
				targetGrid = service.save(targetGrid, Locale.ENGLISH);
				targetGrid = service.publish(targetGrid, Locale.ENGLISH);
			}
			else {
				targetGrid = service.getById(data.getTargetGridId());
			}
			
			if(targetGrid == null) {
				throw new BcephalException("Unknow target grid!");
			}
			orderColumns(targetGrid);
						
			try {
				MaterializedGridCopyQueryBuilder builder = new MaterializedGridCopyQueryBuilder(data, targetGrid);
				String sql = builder.buildQuery();
				log.info("Copy query : \n{}", sql);
				Query query = entityManager.createNativeQuery(sql);
//				for (String key : builder.parameters.keySet()) {
//					query.setParameter(key, builder.parameters.get(key));
//				}
				response = query.executeUpdate();
				log.info("Materialized grid rows copy : {}", response);
				if(data.isDeleteOriginalRows()) {
					sql = builder.buildDeleteQuery();
					log.info("Delete query : \n{}", sql);
					query = entityManager.createNativeQuery(sql);
//					for (String key : builder.parameters.keySet()) {
//						query.setParameter(key, builder.parameters.get(key));
//					}
					query.executeUpdate();
				}
			} catch (Exception ex) {
				String error = "Unable to copy materialized grid rows";
				log.error(error, ex);
				throw new BcephalException(error);
			}
		}
		catch (Exception e) {			
			log.error("Materialized grid : {} : unexpected error", data.getFilter().getGrid().getName(), e);
		}
		finally {
//			if(listener != null) {	
//				if(message != null) {
//					listener.onError(message);
//				}
//				else {
//					listener.onRunEnded();
//				}
//			}
		}
		return response;
	}
	
	
	private void orderColumns(MaterializedGrid grid) throws Exception {
		if (grid != null) {			
			List<MaterializedGridColumn> columns = grid.getColumnListChangeHandler().getItems();
			Collections.sort(columns, new Comparator<MaterializedGridColumn>() {
				@Override
				public int compare(MaterializedGridColumn value1, MaterializedGridColumn value2) {
					if(data.isIdentifyColumnByName()) {
						return value1.getName().compareTo(value2.getName());
					}
					return value1.getPosition() - value2.getPosition();
				}
			});
			grid.setColumns(columns);			
		}
	}
	
	private String getNewGridName() {
		String baseName = StringUtils.hasText(data.getNewGridName()) ? data.getNewGridName() : data.getFilter().getGrid().getName();
		String name = baseName;
		int index = 1;
		while(service.getByName(name) != null) {
			name = baseName + index++;
		}
		return name;		
	}
	
	public void cancel() {
		this.stop = true;
	}	
	
}
