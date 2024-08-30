/**
 * 
 */
package com.moriset.bcephal.grid.service;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;

import com.moriset.bcephal.grid.domain.Join;
import com.moriset.bcephal.grid.domain.JoinColumn;
import com.moriset.bcephal.grid.repository.JoinColumnRepository;
import com.moriset.bcephal.grid.repository.JoinRepository;
import com.moriset.bcephal.utils.BcephalException;

import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Data
@Slf4j
public class JoinMaterializationManager {
	
	private Join join;
	
	private EntityManager entityManager;
	
	protected JoinRepository joinRepository;
	
	protected JoinColumnRepository joinColumnRepository;
	
	@Autowired
	SpotService spotService;
	
	public void publish() throws Exception {
		BuildDimensions();
		
		JoinFilter filter = new JoinFilter();	
		filter.setJoin(join);
        filter.setPage(1);
        filter.setPageSize(1);
        filter.setShowAll(true);           
		
        JoinMaterializationQueryBuilder builder = new JoinMaterializationQueryBuilder(filter, joinColumnRepository, spotService);
		String sql = builder.buildQuery();
		sql = "CREATE MATERIALIZED VIEW IF NOT EXISTS " + tableName() + " AS " + sql;
		log.trace("Query : {}", sql);			
		for (String key : builder.parameters.keySet()) {
			Object data = builder.parameters.get(key);
			if ( data instanceof BigDecimal) {
				sql = sql.replace(":" + key, ((BigDecimal)data).toPlainString());
			}
			else if ( data instanceof Date) {					
				sql = sql.replace(":" + key, "'" +  new SimpleDateFormat("yyyy-MM-dd").format(data) + "'");
			}
			else {
				sql = sql.replace(":" + key, "'" +  data + "'");
			}
		}
		
		log.trace("Materialization query : {}", sql);
		
		Query query = entityManager.createNativeQuery(sql);			
		query.executeUpdate();
		join.setPublished(true);
		joinRepository.save(join);		
		
	}
	
	public void refresh() throws Exception {
		log.info("Try to refresh join : {}", join.getName());
		try {
			String sql = refreshQuery();
			log.trace("Refresh query : {}", sql);
			Query query = entityManager.createNativeQuery(sql);
			query.executeUpdate();
			log.info("Join : {} refreshed!", join.getName());
		} catch (Exception e) {
			log.error("Unable to refresh join : {}", join.getName(), e);
			throw new BcephalException("Unable to refresh join : " + join.getName(), e);
		} 
	}

	public void reset() throws Exception {
		log.info("Try to reset join materialization : {}", join.getName());
		try {
			String sql = dropQuery();
			log.trace("Unpublication query : {}", sql);
			Query query = entityManager.createNativeQuery(sql);
			query.executeUpdate();

			join.setPublished(false);
			joinRepository.save(join);
			
			log.info("Join : {} materialization resetd!", join.getName());
		} catch (Exception e) {
			log.error("Unable to reset join materialization : {}", join.getName(), e);
			throw new BcephalException("Unable to reset join materialization : " + join.getName(), e);
		} 
	}
	
	private String tableName() {
		return join.getMaterializationTableName();
	}

	private String dropQuery() {
		return "DROP MATERIALIZED VIEW IF EXISTS " + tableName();
	}

	private String refreshQuery() {
		return "REFRESH MATERIALIZED VIEW " + tableName();
	}
	
	
	protected void BuildDimensions() {	
		for(JoinColumn column : join.getColumns()) {
			if(column.isUsedForPublication() && column.getPublicationDimensionId() == null) {	
				tryToBuildDimension(column);
			}			
		}
	}
	
	
	private void tryToBuildDimension(JoinColumn column) {
//		if(column.isAttribute()) {
//			AttributeService service = new AttributeService(userSession);
//			Attribute attribute = service.getByName(column.getName());
//			if(attribute == null) {
//				Entity entity = buildEntity();				
//				attribute = new Attribute(null, column.getName());
//				attribute.setPosition(entity.getAttributeListChangeHandler().getItems().size());
//				attribute.setEntity(entity);
//				attribute = service.save(attribute);
//			}
//			column.setPublicationDimensionName(attribute.getName());
//			column.setPublicationDimensionOid(attribute.getOid());
//		}
//		else if(column.isMeasure()) {
//			MeasureService service = new MeasureService(userSession);
//			Measure measure = service.getByName(column.getName());
//			if(measure == null) {
//				measure = new Measure(null, column.getName());
//				int position = service.getAll().size();
//				measure.setPosition(position);
//				measure = service.save(measure);
//			}
//			column.setPublicationDimensionName(measure.getName());
//			column.setPublicationDimensionOid(measure.getOid());
//		}
//		else if(column.isPeriod()) {
//			PeriodNameService service = new PeriodNameService(userSession);
//			PeriodName period = service.getByName(column.getName());
//			if(period == null) {
//				period = new PeriodName(null, column.getName());
//				int position = service.getAll().size();
//				period.setPosition(position);
//				period = service.save(period);
//			}
//			column.setPublicationDimensionName(period.getName());
//			column.setPublicationDimensionOid(period.getOid());
//		}
	}
	
//	private Entity buildEntity() {
//		Entity entity = new EntityService(userSession).getByName(join.getName());
//		if(entity == null) {
//			ParameterService parameterService = new ParameterService(userSession);
//			Parameter parameter = parameterService.getParameters(InitiationParameterCodes.INITIATION_DEFAULT_MODEL, com.moriset.misp.model.base.ParameterType.MODEL);			
//			if(parameter == null) {
//				parameter = new Parameter(InitiationParameterCodes.INITIATION_DEFAULT_MODEL, com.moriset.misp.model.base.ParameterType.MODEL);
//				Model model = new ModelService(userSession).getByName(join.getName());
//				if(model == null) {
//					model = new Model(join.getName());
//				}	
//				entity = new Entity(join.getName());
//				entity.setPosition(model.getEntityListChangeHandler().getItems().size());
//				model.getEntityListChangeHandler().addNew(entity);
//				model = new ModelService(userSession).save(model);
//				parameter.setValueOid(model.getOid());
//				parameter = parameterService.save(parameter);
//			}
//			else {
//				Model model = new ModelService(userSession).getByOid(parameter.getValueOid());
//				entity = new Entity(join.getName());
//				entity.setPosition(model.getEntityListChangeHandler().getItems().size());
//				model.getEntityListChangeHandler().addNew(entity);
//				model = new ModelService(userSession).save(model);
//			}
//		}
//		return entity;
//	}
	
}
