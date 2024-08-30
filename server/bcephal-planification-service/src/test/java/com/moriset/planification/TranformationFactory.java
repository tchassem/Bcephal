package com.moriset.planification;



import static org.assertj.core.api.Assertions.assertThatObject;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.util.StringUtils;

import com.moriset.bcephal.domain.DataSourceType;
import com.moriset.bcephal.domain.EditorData;
import com.moriset.bcephal.domain.dimension.DimensionType;
import com.moriset.bcephal.domain.dimension.Measure;
import com.moriset.bcephal.domain.dimension.Model;
import com.moriset.bcephal.domain.dimension.Period;
import com.moriset.bcephal.domain.filters.PeriodGranularity;
import com.moriset.bcephal.domain.filters.PeriodOperator;
import com.moriset.bcephal.domain.filters.PeriodValue;
import com.moriset.bcephal.grid.service.GrilleService;
import com.moriset.bcephal.grid.service.MaterializedGridService;
import com.moriset.bcephal.planification.domain.routine.TransformationRoutine;
import com.moriset.bcephal.planification.domain.routine.TransformationRoutineField;
import com.moriset.bcephal.planification.domain.routine.TransformationRoutineItem;
import com.moriset.bcephal.planification.domain.routine.TransformationRoutineSourceType;
import com.moriset.bcephal.planification.service.TransformationRoutineService;
import com.moriset.bcephal.service.InitiationService;

public class TranformationFactory{
	
	
	  public static List<String> names(){ List<String> items = new ArrayList<>();
	  items.add("INP002 MCI affiliate invoice"); 
		/*
		 * items.
		 * add("BIL310 Scheme fee - dedicated fee not allocated to a billable member");
		 * items.add("EOM001 End of month input"); items.add("STA001 MCI PML Invoices");
		 * items.add("STA002 MCI affiliate invoice");
		 */
	  
	  return items; }
	 
	
	
	public static List<TransformationRoutine> buildTransformationRoutines(List<EditorData<TransformationRoutine>> editorTranGrids, GrilleService grilleService, TransformationRoutineService routineService,
			MaterializedGridService materializedGridService,InitiationService initiationService) throws Exception{
		List<TransformationRoutine> items =  new ArrayList<>();
		items.add(buildENR002_Affiliate_invoice_member_ID(grilleService,routineService, materializedGridService, initiationService));
		items.add(buildMAP001_Member_bank_name(grilleService, routineService,materializedGridService, initiationService));
		items.add(buildREC001_REC_account_name(grilleService,routineService,materializedGridService, initiationService));
		items.add(buildRoutine_1(grilleService,routineService,materializedGridService, initiationService));
		items.add(buildRoutine_2(grilleService,routineService,materializedGridService, initiationService));
		items.add(buildRoutine_3(grilleService,routineService,materializedGridService, initiationService));
		items.add(buildRoutine_4(grilleService,routineService,materializedGridService, initiationService));
		items.add(buildRoutine_5(grilleService,routineService,materializedGridService, initiationService));
		items.add(buildRoutine_6(grilleService,routineService,materializedGridService, initiationService));
		return items;
	}

	public static TransformationRoutine buildENR002_Affiliate_invoice_member_ID(GrilleService grilleService, TransformationRoutineService routineService,
			MaterializedGridService materializedGridService,InitiationService initiationService) throws Exception {
		TransformationRoutine routine = new TransformationRoutine();
		routine.setName("ENR002 Affiliate invoice - member ID");
		routine.setActive(true);
		routine.setConfirmAction(false);
		routine.getItemListChangeHandler()
				.addNew(buildTransformationRoutineItem(null,0, true, true,DataSourceType.MATERIALIZED_GRID, null, null,
						null, null, "Item 1",
						DimensionType.ATTRIBUTE,routineService, materializedGridService, grilleService, initiationService));
		
				return routine;
	}
	
	

	public static TransformationRoutine buildMAP001_Member_bank_name(GrilleService grilleService, TransformationRoutineService routineService,
			MaterializedGridService materializedGridService,InitiationService initiationService) throws Exception {
		TransformationRoutine routine = new TransformationRoutine();
		routine.setName("MAP001 Member bank name");
		routine.setActive(true);
		routine.setConfirmAction(true);
		routine.getItemListChangeHandler()
				.addNew(buildTransformationRoutineItem(null,0, true,true,DataSourceType.UNIVERSE,  null, null,
						null, null, "Item 1",
						DimensionType.ATTRIBUTE,routineService, materializedGridService, grilleService, initiationService));
		routine.getItemListChangeHandler()
		.addNew(buildTransformationRoutineItem(null,1, true,true,DataSourceType.UNIVERSE,  null, null,
				null, null, "Item 2",
				DimensionType.ATTRIBUTE,routineService, materializedGridService, grilleService, initiationService));
		
		
		return routine;
	}
	
	public static TransformationRoutine buildREC001_REC_account_name(GrilleService grilleService, TransformationRoutineService routineService,
			MaterializedGridService materializedGridService,InitiationService initiationService) throws Exception {
		TransformationRoutine routine = new TransformationRoutine();
		routine.setName("REC001 REC account name");
		routine.setActive(true);
		routine.setConfirmAction(false);
		routine.getItemListChangeHandler()
		.addNew(buildTransformationRoutineItem(null,0, true,true,DataSourceType.UNIVERSE,  null, null,
				null, null, "Item 1",
				DimensionType.ATTRIBUTE,routineService, materializedGridService, grilleService, initiationService));
		
		routine.getItemListChangeHandler()
		.addNew(buildTransformationRoutineItem(null,1, true,true,DataSourceType.UNIVERSE,  null, null,
				null, null, "Item 2",
				DimensionType.ATTRIBUTE,routineService, materializedGridService, grilleService, initiationService));
		
		
		return routine;
	}
	
	public static TransformationRoutine buildRoutine_1(GrilleService grilleService, TransformationRoutineService routineService,
			MaterializedGridService materializedGridService,InitiationService initiationService) throws Exception {
		TransformationRoutine routine = new TransformationRoutine();
		routine.setName("Routine 1");
		routine.setActive(true);
		routine.setConfirmAction(false);
		routine.getItemListChangeHandler()
		.addNew(buildTransformationRoutineItem(null,0, true,true,DataSourceType.UNIVERSE,  null, null,
				null, null, "Item 1",
				DimensionType.ATTRIBUTE,routineService, materializedGridService, grilleService, initiationService));
		
		
		return routine;
	}
	public static TransformationRoutine buildRoutine_2(GrilleService grilleService, TransformationRoutineService routineService,
			MaterializedGridService materializedGridService,InitiationService initiationService) throws Exception {
		TransformationRoutine routine = new TransformationRoutine();
		routine.setName("Routine 1");
		routine.setActive(true);
		routine.setConfirmAction(false);
		routine.getItemListChangeHandler()
		.addNew(buildTransformationRoutineItem(null,0, true,false,DataSourceType.UNIVERSE,  null, null,
				null, null, "Item 1",
				DimensionType.ATTRIBUTE,routineService, materializedGridService, grilleService, initiationService));
		
		
		return routine;
	}
	
	public static TransformationRoutine buildRoutine_3(GrilleService grilleService, TransformationRoutineService routineService,
			MaterializedGridService materializedGridService,InitiationService initiationService) throws Exception {
		TransformationRoutine routine = new TransformationRoutine();
		routine.setName("Routine 1");
		routine.setActive(true);
		routine.setConfirmAction(false);
		routine.getItemListChangeHandler()
		.addNew(buildTransformationRoutineItem(null,0, true,true,DataSourceType.UNIVERSE,  null, null,
				null, null, "Item 1",
				DimensionType.ATTRIBUTE,routineService, materializedGridService, grilleService, initiationService));
		
		
		return routine;
	}
	public static TransformationRoutine buildRoutine_4(GrilleService grilleService, TransformationRoutineService routineService,
			MaterializedGridService materializedGridService,InitiationService initiationService) throws Exception {
		TransformationRoutine routine = new TransformationRoutine();
		routine.setName("Routine 1");
		routine.setActive(true);
		routine.setConfirmAction(false);
		routine.getItemListChangeHandler()
		.addNew(buildTransformationRoutineItem(null,0, true,false,DataSourceType.UNIVERSE,  null, null,
				null, null, "Item 1",
				DimensionType.MEASURE,routineService, materializedGridService, grilleService, initiationService));
		
		
		return routine;
	}
	
	public static TransformationRoutine buildRoutine_5(GrilleService grilleService, TransformationRoutineService routineService,
			MaterializedGridService materializedGridService,InitiationService initiationService) throws Exception {
		TransformationRoutine routine = new TransformationRoutine();
		routine.setName("Routine 1");
		routine.setActive(true);
		routine.setConfirmAction(false);
		routine.getItemListChangeHandler()
		.addNew(buildTransformationRoutineItem(null,0, true,true,DataSourceType.UNIVERSE,  null, null,
				null, null, "Item 1",
				DimensionType.ATTRIBUTE,routineService, materializedGridService, grilleService, initiationService));
		
		return routine;
	}
	
	public static TransformationRoutine buildRoutine_6(GrilleService grilleService, TransformationRoutineService routineService,
			MaterializedGridService materializedGridService,InitiationService initiationService) throws Exception {
		TransformationRoutine routine = new TransformationRoutine();
		routine.setName("Routine 1");
		routine.setActive(true);
		routine.setConfirmAction(false);
		routine.getItemListChangeHandler()
		.addNew(buildTransformationRoutineItem(null,0, true,false,DataSourceType.UNIVERSE,  null, null,
				null, null, "Item 1",
				DimensionType.ATTRIBUTE,routineService, materializedGridService, grilleService, initiationService));
		
		
		return routine;
	}
	
	
	public static TransformationRoutineItem buildTransformationRoutineItem(EditorData<TransformationRoutine> editorData,int position, boolean active, boolean applyOnlyIfEmpty,
			DataSourceType dataSourceType, TransformationRoutineSourceType sourceType,
			 PeriodValue periodValue, BigDecimal decimalValue, String dimensionName,
			String grilleName, DimensionType type, TransformationRoutineService routineService,MaterializedGridService matgridService,
			GrilleService grilleService, InitiationService initiationService) throws Exception {
		
		TransformationRoutineItem routineItem = new TransformationRoutineItem();
		TransformationRoutineField field = buildTransformationRoutineField(sourceType, periodValue,
				decimalValue, type);
		assertThatObject(field).isNotNull();
		if (dataSourceType.equals(DataSourceType.MATERIALIZED_GRID)) {
			Long idGrid = matgridService.getByName(grilleName).getId();
			
			
			
			 List<Model> models =  editorData.getModels();
			 List<Measure> measures = editorData.getMeasures();
			 List<Period> periods = editorData.getPeriods();
			
			if(type.isAttribute()) {
				Optional<com.moriset.bcephal.domain.dimension.Attribute> val = models.get(0).getEntities().get(0)
						.getAttributes()
						.stream().filter(x-> x != null && StringUtils.hasText(x.getName())&& x.getName().equals(dimensionName))
						.findFirst();
				if(val.isPresent()) {
					routineItem.setTargetDimensionId(val.get().getId());
				}
			}else
				if(type.isMeasure()) {
					 Optional<Measure> val = measures
							.stream().filter(x-> x != null && StringUtils.hasText(x.getName())&& x.getName().equals(dimensionName))
							.findFirst();
					if(val.isPresent()) {
						routineItem.setTargetDimensionId(val.get().getId());
					}
				}else
					if(type.isPeriod()) {
						 Optional<Period> val = periods
								.stream().filter(x-> x != null && StringUtils.hasText(x.getName())&& x.getName().equals(dimensionName))
								.findFirst();
						if(val.isPresent()) {
							routineItem.setTargetDimensionId(val.get().getId());
						}
					}


			routineItem.setTargetGridId(idGrid);

		} else {

			Long idGrid = grilleService.getByName(grilleName).getId();
			Long idColumn = (Long) initiationService.getDimension(type, dimensionName, true, null, null).getId();

			routineItem.setTargetGridId(idGrid);
			routineItem.setTargetDimensionId((idColumn));
		}

		routineItem.setType(type);
		routineItem.setPosition(position);
		routineItem.setSourceField(field);
		routineItem.setApplyOnlyIfEmpty(applyOnlyIfEmpty);
		routineItem.setActive(active);
		return routineItem;
	}

	public static TransformationRoutineField buildTransformationRoutineField(TransformationRoutineSourceType sourceType,
			PeriodValue periodValue, BigDecimal decimalValue, DimensionType replaceDimensionType) {
		TransformationRoutineField field = new TransformationRoutineField();
		field.setSourceType(sourceType);
		field.setReplaceDimensionType(replaceDimensionType);
		field.setPeriodValue(periodValue);
		field.setDecimalValue(decimalValue);
		return field;
	}
	
	public static PeriodValue buildPeriodValue(int dateNumber,String dateSign,PeriodGranularity dateGranularity,PeriodOperator dateOperator,Date dateValue) {
		PeriodValue periodValue = new PeriodValue();
		periodValue.setDateNumber(dateNumber);
		periodValue.setDateSign(dateSign);
		periodValue.setDateGranularity(dateGranularity);
		periodValue.setDateOperator(dateOperator);
		periodValue.setDateValue(dateValue);
		return periodValue;
	}

}
