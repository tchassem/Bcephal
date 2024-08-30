/**
 * 
 */
package com.moriset.bcephal.reconciliation.service;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.moriset.bcephal.domain.RunModes;
import com.moriset.bcephal.domain.dimension.DimensionType;
import com.moriset.bcephal.domain.dimension.Measure;
import com.moriset.bcephal.domain.filters.FilterVerb;
import com.moriset.bcephal.grid.domain.GrilleColumn;
import com.moriset.bcephal.reconciliation.domain.ReconciliationCondition;
import com.moriset.bcephal.reconciliation.domain.ReconciliationData;
import com.moriset.bcephal.reconciliation.domain.ReconciliationModelSide;
import com.moriset.bcephal.reconciliation.domain.WriteOffField;
import com.moriset.bcephal.utils.BcephalException;

import jakarta.persistence.Query;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Scope(scopeName= ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class AutoRecoBothCumulatedRunner extends AutoRecoMethodRunner {

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public void run() throws Exception {
		log.debug("Auto Reco : {} - Running....", this.reco.getName());
		int count = 0;
		try{									
			log.trace("Auto Reco : {} - Build views....", this.reco.getName());
			count = buildViews();
			log.trace("Auto Reco : {} - Views builded!", this.reco.getName());
			
			
			List<Object[]> groups = new ArrayList<Object[]>();
			int commonsAttSize = getReco().getConditions().size();
			if(commonsAttSize > 0) {
				log.trace("Auto Reco : {} - Build commons attribute....", this.getReco().getName());
				groups = buildCommonsView();
				log.trace("Auto Reco : {} - Commons attribute builded!", this.getReco().getName());
			}
			else {
				groups.add(new Object[] {});
			}
			count = groups.size();
			
			if(listener != null) {
	        	listener.startSubInfo(count);
	        }
			if(stop) { return;}
	        boolean useCreditDebit = this.getReco().getModel().isUseDebitCredit();			
			
	        if(stop) { return;}	  
	        
	        
	        for(Object g : groups) {				
				Object[] group = commonsAttSize == 1 ? new Object[] {g} : (Object[])g;
				BigDecimal primaryAmount = BigDecimal.ZERO;
		        BigDecimal secondaryAmount = BigDecimal.ZERO;
		        ReconciliationData data = buildReconciliationData();
		        data.getLeftids().clear();
		        data.getRightids().clear();
		        data.getPrimaryids().clear();
		        data.getSecondaryids().clear();
				
		        int pcount = getPrimaryGridCountRows(group);
		        int scount = getSecondaryGridCountRows(group);
				int pageSize = 1000;
				int pageCount = ((int) pcount / pageSize) + ((pcount % pageSize) > 0 ? 1 : 0);
				int page = 0;
								
				List<Object[]> rows = getPrimaryGridNextRows(group, page, pageSize);
				while(!rows.isEmpty() && pcount > 0 && pageCount >= page && !stop) { 
		        	if(stop) { return;}
		        	for(Object[] row : rows) { 
		        		pcount--;
		        		if(stop) { return;}
		        		Long oid = Long.parseLong(row[0].toString());
		        		BigDecimal amount = row[1] != null ? new BigDecimal(row[1].toString()) : BigDecimal.ZERO;		        		
		        		data.getLeftids().add(oid);	
		        		data.getPrimaryids().add(oid);	
		        		log.trace("[{} - {} - {}]", (pcount+1), oid, amount);
		        		
		        		if(useCreditDebit) {
	                		String dc = (String)row[2];
		                	if(StringUtils.hasText(dc) && dc.equals(primaryBuilder.debitValue)) {
		                		amount = BigDecimal.ZERO.subtract(amount);
		                	}
	                	}
		                primaryAmount = primaryAmount.add(amount);
		        	}
		        	if(pcount > 0) { 
		        		if(stop) { return;}
		        		page++;
		        		rows = getPrimaryGridNextRows(group, page, pageSize);
		        	}
				}
				
				pageSize = 1000;
				pageCount = ((int) scount / pageSize) + ((scount % pageSize) > 0 ? 1 : 0);
				page = 0;
				
				secondaryRowIds = new ArrayList<>();
				rows = getSecondaryGridNextRows(group, page, pageSize);
				while(!rows.isEmpty() && scount > 0 && pageCount >= page && !stop) { 
		        	if(stop) { return;}
		        	for(Object[] row : rows) { 
		        		scount--;
		        		if(stop) { return;}
		        		Long oid = Long.parseLong(row[0].toString());
		        		BigDecimal amount = row[1] != null ? new BigDecimal(row[1].toString()) : BigDecimal.ZERO;		        		
		        		data.getRightids().add(oid);	
		        		data.getSecondaryids().add(oid);	
		        		secondaryRowIds.add(oid);
		        		log.trace("[{} - {} - {}]", (scount+1), oid, amount);
		        					        		
		        		if(useCreditDebit) {
	                		String dc = (String)row[2];
		                	if(StringUtils.hasText(dc) && dc.equals(secondaryBuilder.debitValue)) {
		                		amount = BigDecimal.ZERO.subtract(amount);
		                	}
	                	}        		
		                
		                secondaryAmount = secondaryAmount.add(amount);
		        	}
		        	if(scount > 0) { 
		        		if(stop) { return;}
		        		page++;
		        		rows = getSecondaryGridNextRows(group, page, pageSize);
		        	}
				}
				
				boolean valid = validateAmount(primaryAmount, secondaryAmount, null, data);
				if(valid) {
            		if(stop) { return;}
            		boolean isOk = BigDecimal.ZERO.compareTo(data.getBalanceAmount()) == 0 || this.getReco().getModel().isAllowWriteOff();			                		
            		if(isOk) {
            			buildWriteOffData(data);
            			buildEnrichmentData(data);            			
            			log.trace("{} => {}", primaryAmount, secondaryAmount);            			
            			if(stop) { return;}
            			reconciliate(data, RunModes.A, true);
            		}
            		else {
            			log.debug("Unable to reconciliate. Write off is not allowed!");
            		}			                		
            	}  
				
				if(listener != null) {
                	listener.nextSubInfoStep(1);
                }    			
			}
	        
	        if(transactionCount > 0) {
	        	reconciliationModelService.reconciliateAndCommit(datas, "B-CEPHAL",  RunModes.A);			
				transactionCount = 0;
				datas.clear();
			}
	        
	        runScript();	        
		}
		catch (Exception e) {
			if(e instanceof BcephalException) {
				throw e;
			}
			String message = "Unable to run Auto Reco : " + this.reco.getName() + "\n" + e.getMessage();			
			throw new BcephalException(message);
		}
		finally {
			dropViews();
		}		        
	}
	
	@SuppressWarnings("unchecked")
	private List<Object[]> buildCommonsView() throws Exception {
		try {			
			for(ReconciliationCondition item : reco.getSortedConditions()) {
				item.setValue(null);
				GrilleColumn column = item.getSide1().isLeft() ? reco.getModel().getLeftGrid().getColumnById(item.getColumnId1()) : reco.getModel().getRigthGrid().getColumnById(item.getColumnId1());
				if(column == null) {
					throw new BcephalException("Wrong condition at position " + (item.getPosition() + 1));
				}
				item.setColumn1(column);
				item.setColumn2(item.getSide2().isLeft() ? reco.getModel().getLeftGrid().getColumnById(item.getColumnId2()) : reco.getModel().getRigthGrid().getColumnById(item.getColumnId2()));
				if(item.getColumn2() == null) {
					throw new BcephalException("Wrong condition at position " + (item.getPosition() + 1));
				}
				
				item.setPrimaryDbColumn(column != null ? column.getUniverseTableColumnName() : null);
				
				ReconciliationModelSide secondaryside = reco.isLeftGridPrimary() ? ReconciliationModelSide.RIGHT : ReconciliationModelSide.LEFT;
				GrilleColumn secondarycolumn = null;
				if(secondaryside == item.getSide1()) {
					secondarycolumn = secondaryside.isLeft() ? reco.getModel().getLeftGrid().getColumnById(item.getColumnId1()) : reco.getModel().getRigthGrid().getColumnById(item.getColumnId1());
					item.setSecondaryToPrimary(true);
				}
				else {
					secondarycolumn = secondaryside.isLeft() ? reco.getModel().getLeftGrid().getColumnById(item.getColumnId2()) : reco.getModel().getRigthGrid().getColumnById(item.getColumnId2());
					item.setSecondaryToPrimary(false);
				}
				//item.setDbColumn(secondarycolumn != null ? secondarycolumn.getUniverseTableColumnName() : null);
				item.setSecondaryDbColumn(secondarycolumn != null ? secondarycolumn.getUniverseTableColumnName() : null);
				item.setDimensionType(item.getColumn1() != null ? item.getColumn1().getType(): item.getColumn2() != null ? item.getColumn2().getType() : null);
			}
						
			String columns = "";
			String coma = "";
			int position = 1;
			for(GrilleColumn column : reco.getConditionColumns(ReconciliationModelSide.LEFT)) {
				String col = column.getUniverseTableColumnName();
				columns = columns.concat(coma).concat(col).concat(" AS col" + position++);
				coma = ",";
			}			
			String countSql = "SELECT DISTINCT ".concat(columns).concat(" FROM ").concat(primaryView);
			Query query = getEntityManager().createNativeQuery(countSql);
			List<Object[]> result = query.getResultList();
			return result;			
		}
		catch (Exception e) {
			if(e instanceof BcephalException) {
				throw e;
			} 
			throw new BcephalException("Unable to create views", e);
		}
	}
	
	
	private int getPrimaryGridCountRows(Object[] group) throws Exception {
		String sql = "SELECT Count(1) FROM ".concat(primaryView)
				.concat(buildPrimaryWherePart(group));
		log.trace("primary grid row count query : {}", sql);
        Query query = getEntityManager().createNativeQuery(sql);
	    Number n =(Number)query.getSingleResult();
	    return n.intValue();
	}
		
	@SuppressWarnings("unchecked")
	private List<Object[]> getPrimaryGridNextRows(Object[] group, int page, int size) throws Exception {
		String col = new Measure(getReco().getPrimaryMeasureId()).getUniverseTableColumnName();
		String  cd = primaryBuilder.useCreditDebit ? ",DC" : "";
		String sql = "SELECT ".concat("id,").concat(col).concat(cd)
				.concat(" FROM ").concat(primaryView)
				.concat(buildPrimaryWherePart(group))
				.concat(" ORDER BY id ASC ");
		log.trace("primary grid row query : {}", sql);
        Query query = getEntityManager().createNativeQuery(sql);
        int firstItem = page * size;
        query.setFirstResult(firstItem);
		query.setMaxResults(size);		
        List<Object[]> rows = query.getResultList();
		return rows;
	}
		
	

	private int getSecondaryGridCountRows(Object[] group) throws Exception {
		String sql = "SELECT Count(1) FROM ".concat(SecondaryView)
				.concat(buildSecondaryWherePart(group));
		log.trace("Secondary grid row count query : {}", sql);
        Query query = getEntityManager().createNativeQuery(sql);
        Number n =(Number)query.getSingleResult();        
		return n.intValue();
	}
	
	
	@SuppressWarnings("unchecked")
	private List<Object[]> getSecondaryGridNextRows(Object[] group, int page, int size) throws Exception {
		String col = new Measure(getReco().getSecondaryMeasureId()).getUniverseTableColumnName();
		String  cd = primaryBuilder.useCreditDebit ? ",DC" : "";
		String sql = "SELECT ".concat("id,").concat(col).concat(cd)
				.concat(" FROM ").concat(SecondaryView)
				.concat(buildSecondaryWherePart(group))
				.concat(" ORDER BY id ASC ");
		log.trace("Secondary grid row query : {}", sql);
        Query query = getEntityManager().createNativeQuery(sql);
        int firstItem = page * size;
        query.setFirstResult(firstItem);
		query.setMaxResults(size);		
        List<Object[]> rows = query.getResultList();
		return rows;
	}
	
	
	
	private String buildPrimaryWherePart(Object[] group) throws Exception {		
		String sql = "";
		int i = 0;		
		
		for(ReconciliationCondition item : reco.getSortedConditions()) {			
			if(item.getDimensionType() == DimensionType.ATTRIBUTE) {
				item.setValue(group[i]);		
			}
			if (item.getDimensionType() == DimensionType.PERIOD) {
				Date date = null;
				Object ob = group[i];
				if(ob instanceof Date) {
					date = (Date)ob;
				}
				else if(ob != null) {
					try {
						date = new SimpleDateFormat("dd/MM/yyyy").parse(ob.toString());
					} catch (ParseException e) {
						e.printStackTrace();
						i++;
						continue;
					}
				}
				item.setValue(date);
			}
			i++;		
			ReconciliationCondition copy = item.copy();		
			if(item.getDimensionType().isPeriod()) {
				copy.setOperator("=");
				copy.setPeriodCondition(null);
				copy.setDateNumber(0);
			}
			String s = copy.getSql(item.getPrimaryDbColumn(), item.getValue(), item.getDimensionType());
			if(StringUtils.hasText(s)) {
				boolean isFirst = item.getPosition() == 0;
				String verb = org.springframework.util.StringUtils.hasText(item.getVerb()) ? " " + item.getVerb() + " " : " AND ";
				
				if(FilterVerb.ANDNO.name().equals(verb)) {
					verb = isFirst ? "NOT" : "AND_NOT";
				}
				else if(FilterVerb.ORNO.name().equals(verb)) {
					verb = isFirst ? "NOT" : "OR_NOT";
				}
				else {
					verb = isFirst ? "" : verb;
				}
				if("AND_NOT".equals(verb) || "OR_NOT".equals(verb)) {
					verb = verb.replace("_", " ");
					s = "(" + s + ")";
				}
				if(!StringUtils.hasText(sql)) {
					sql = " WHERE ";
				}
				sql += " " + verb + " " + s;				
			}
		}
		return sql;
	}
	
	private String buildSecondaryWherePart(Object[] group) throws Exception {		
		String sql = "";
		int i = 0;
		
		for(ReconciliationCondition item : reco.getSortedConditions()) {			
			if(item.getDimensionType() == DimensionType.ATTRIBUTE) {
				item.setValue(group[i]);					
			}
			if (item.getDimensionType() == DimensionType.PERIOD) {
				Date date = null;
				Object ob = group[i];
				if(ob instanceof Date) {
					date = (Date)ob;
				}
				else if(ob != null){
					try {
						date = new SimpleDateFormat("dd/MM/yyyy").parse(ob.toString());
					} catch (ParseException e) {
						e.printStackTrace();
						i++;
						continue;
					}
				}
				item.setValue(date);
			}
			i++;			
			String s = item.getSql(item.getSecondaryDbColumn(), item.getValue(), item.getDimensionType());
			if(StringUtils.hasText(s)) {
				boolean isFirst = item.getPosition() == 0;
				String verb = org.springframework.util.StringUtils.hasText(item.getVerb()) ? " " + item.getVerb() + " " : " AND ";
				
				if(FilterVerb.ANDNO.name().equals(verb)) {
					verb = isFirst ? "NOT" : "AND_NOT";
				}
				else if(FilterVerb.ORNO.name().equals(verb)) {
					verb = isFirst ? "NOT" : "OR_NOT";
				}
				else {
					verb = isFirst ? "" : verb;
				}
				if("AND_NOT".equals(verb) || "OR_NOT".equals(verb)) {
					verb = verb.replace("_", " ");
					s = "(" + s + ")";
				}
				if(!StringUtils.hasText(sql)) {
					sql = " WHERE ";
				}
				sql += " " + verb + " " + s;	
			}
		}	
		
		return sql;
	}
	
	@Override		
	protected void buildWriteOffField(ReconciliationData data, WriteOffField field) {
//		WriteOffField item = field.getACopy();
//		if(item.getWriteOffFieldType() == WriteOffFieldType.ATTRIBUTE && item.getAttributeField() != null) {
//			if(item.getDefaultValueType() == WriteOffFieldValueType.LIST_OF_VALUES) {
//				for(WriteOffFieldValue value : field.getWriteOffFieldValues()) {
//					if(value.isDefaultValue()) {
//						item.value = new AttributeValue();
//						item.value.name = value.getAttributeValue();
//						//item.value.setAttribute(item.getAttributeField());
//						data.writeOffFields.add(item);
//					}
//				}
//			}
//			else {
//				List<Long> oids = new ArrayList<>(0);
//				if(item.getDefaultValueType() == WriteOffFieldValueType.LEFT_SIDE) {					
//					if(this.reco.getPrimaryGrid() == this.reco.getFilter().getLeftGrid()) {
//						oids.addAll(data.primaryids);
//					}
//					else {
//						oids.addAll(data.secondaryids);
//					}
//				}
//				else if(item.getDefaultValueType() == WriteOffFieldValueType.RIGHT_SIDE) {
//					if(this.reco.getPrimaryGrid() == this.reco.getFilter().getLeftGrid()) {
//						oids.addAll(data.secondaryids);
//					}
//					else {
//						oids.addAll(data.primaryids);
//					}
//				}
//				if(!oids.isEmpty()) {
//					ReconciliationFilterTemplateService service = new ReconciliationFilterTemplateService(userSession);
//					String value = service.getWriteOffFieldAttributeValue(item.getAttributeField(), oids);
//					item.value = new AttributeValue();
//					item.value.name = value;
//					data.writeOffFields.add(item);
//				}
//			}
//		}
//		
//		else if(item.getWriteOffFieldType() == WriteOffFieldType.PERIOD && item.getPeriodField() != null) {
//			List<Long> oids = new ArrayList<>(0);
//			if(item.getDefaultValueType() == WriteOffFieldValueType.LEFT_SIDE) {					
//				if(this.reco.getPrimaryGrid() == this.reco.getFilter().getLeftGrid()) {
//					oids.addAll(data.primaryids);
//				}
//				else {
//					oids.addAll(data.secondaryids);
//				}
//			}
//			else if(item.getDefaultValueType() == WriteOffFieldValueType.RIGHT_SIDE) {
//				if(this.reco.getPrimaryGrid() == this.reco.getFilter().getLeftGrid()) {
//					oids.addAll(data.secondaryids);
//				}
//				else {
//					oids.addAll(data.primaryids);
//				}
//			}
//			if(oids.size() > 0) {
//				ReconciliationFilterTemplateService service = new ReconciliationFilterTemplateService(userSession);
//				Date value = service.getWriteOffFieldPeriodValue(item.getPeriodField(), oids);
//				item.date = value;
//				data.writeOffFields.add(item);
//			}
//		}
	}
	
}
