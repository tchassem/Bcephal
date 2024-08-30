package com.moriset.bcephal.dashboard.service;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Paths;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.moriset.bcephal.dashboard.domain.DashboardReport;
import com.moriset.bcephal.dashboard.domain.DashboardReportEditorData;
import com.moriset.bcephal.dashboard.domain.DashboardReportField;
import com.moriset.bcephal.dashboard.domain.DashbordReportType;
import com.moriset.bcephal.dashboard.domain.UniverseDynamicFilter;
import com.moriset.bcephal.dashboard.repository.ChartPropertiesRepository;
import com.moriset.bcephal.dashboard.repository.DashboardReportFieldRepository;
import com.moriset.bcephal.dashboard.repository.DashboardReportRepository;
import com.moriset.bcephal.dashboard.repository.PivotTablePropertiesRepository;
import com.moriset.bcephal.domain.BrowserData;
import com.moriset.bcephal.domain.BrowserDataPage;
import com.moriset.bcephal.domain.DataSourceType;
import com.moriset.bcephal.domain.EditorData;
import com.moriset.bcephal.domain.EditorDataFilter;
import com.moriset.bcephal.domain.ListChangeHandler;
import com.moriset.bcephal.domain.dimension.CalculatedMeasure;
import com.moriset.bcephal.domain.dimension.DimensionType;
import com.moriset.bcephal.domain.filters.AttributeFilter;
import com.moriset.bcephal.domain.filters.AttributeFilterItem;
import com.moriset.bcephal.domain.filters.BrowserDataFilter;
import com.moriset.bcephal.domain.filters.FilterVerb;
import com.moriset.bcephal.domain.filters.PeriodFilter;
import com.moriset.bcephal.domain.filters.PeriodFilterItem;
import com.moriset.bcephal.domain.filters.UniverseFilter;
import com.moriset.bcephal.grid.domain.Grille;
import com.moriset.bcephal.grid.domain.GrilleColumn;
import com.moriset.bcephal.grid.domain.GrilleDataFilter;
import com.moriset.bcephal.grid.domain.GrilleExportDataType;
import com.moriset.bcephal.grid.domain.GrilleRowType;
import com.moriset.bcephal.grid.domain.GrilleType;
import com.moriset.bcephal.grid.domain.Join;
import com.moriset.bcephal.grid.domain.JoinColumn;
import com.moriset.bcephal.grid.domain.MaterializedGrid;
import com.moriset.bcephal.grid.domain.MaterializedGridColumn;
import com.moriset.bcephal.grid.domain.MaterializedGridDataFilter;
import com.moriset.bcephal.grid.domain.SmartMaterializedGrid;
import com.moriset.bcephal.grid.repository.JoinColumnRepository;
import com.moriset.bcephal.grid.repository.MaterializedGridRepository;
import com.moriset.bcephal.grid.service.DashboardReportMaterializedGridQueryBuilder;
import com.moriset.bcephal.grid.service.DashboardReportQueryBuilder;
import com.moriset.bcephal.grid.service.DashboardReportQueryBuilderV2;
import com.moriset.bcephal.grid.service.DataSourcableService;
import com.moriset.bcephal.grid.service.GridCsvExporter;
import com.moriset.bcephal.grid.service.GridExporter;
import com.moriset.bcephal.grid.service.GridJsonExporter;
import com.moriset.bcephal.grid.service.JoinCsvExporter;
import com.moriset.bcephal.grid.service.JoinExporter;
import com.moriset.bcephal.grid.service.JoinFilter;
import com.moriset.bcephal.grid.service.JoinJsonExporter;
import com.moriset.bcephal.grid.service.JoinPublishedQueryBuilder;
import com.moriset.bcephal.grid.service.JoinQueryBuilder;
import com.moriset.bcephal.grid.service.JoinService;
import com.moriset.bcephal.grid.service.MaterializedCsvExporter;
import com.moriset.bcephal.grid.service.MaterializedExporter;
import com.moriset.bcephal.grid.service.MaterializedGridQueryBuilder;
import com.moriset.bcephal.grid.service.MaterializedJsonExporter;
import com.moriset.bcephal.grid.service.SpotService;
import com.moriset.bcephal.repository.dimension.CalculatedMeasureRepository;
import com.moriset.bcephal.security.service.SecurityService;
import com.moriset.bcephal.security.service.UserSessionLogService;
import com.moriset.bcephal.service.RequestQueryBuilder;
import com.moriset.bcephal.service.filters.UniverseFilterService;
import com.moriset.bcephal.utils.BcephalException;
import com.moriset.bcephal.utils.FunctionalityCodes;
import com.moriset.bcephal.utils.UTF8Control;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import net.sf.jasperreports.engine.JRParameter;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

@Service
@Slf4j
public class DashboardReportService extends DataSourcableService<DashboardReport, BrowserData> {

	@Autowired
	DashboardReportRepository dashboardReportRepository;
	@Autowired
	DashboardReportFieldRepository dashboardReportFieldRepository;

	@Autowired
	PivotTablePropertiesRepository pivotTablePropertiesRepository;

	@Autowired
	ChartPropertiesRepository chartPropertiesRepository;

	@Autowired
	UniverseFilterService universeFilterService;

	@Autowired
	UniverseDynamicFilterService dynamicFilterService;

	@Autowired
	DashboardReportFieldService dashboardReportFieldService;
	
	@Autowired
	CalculatedMeasureRepository calculatedMeasureRepository;

	@PersistenceContext
	EntityManager session;

	@Autowired
	SecurityService securityService;

	@Autowired
	UserSessionLogService logService;

	@Autowired 
	MaterializedGridRepository materializedGridRepository;
	
	@Autowired
	JoinColumnRepository joinColumnRepository;
	
	@Autowired
	JoinService joinService;	

	@Autowired
	SpotService spotService;
	
	@Override
	protected String getBrowserFunctionalityCode() {
		return FunctionalityCodes.REPORTING_CHART;
	}

	@Override
	protected List<Long> getHidedObjectId(Long profileId, String functionalityCode, String projectCode) {
		List<Long> items = securityService.getHideProfileById(profileId, functionalityCode, projectCode);
		List<Long> items2 = securityService.getHideProfileById(profileId, FunctionalityCodes.REPORTING_PIVOT_TABLE,
				projectCode);
		List<Long> items3 = securityService.getHideProfileById(profileId, FunctionalityCodes.REPORTING_REPORT_GRID,
				projectCode);
		if (items2.size() > 0) {
			items.addAll(items2);
		}
		if (items3.size() > 0) {
			items.addAll(items3);
		}
		return items;
	}

	@Override
	public void saveUserSessionLog(String username, Long clientId, String projectCode, String usersession,
			Long objectId, String functionalityCode, String rightLevel,Long profileId) {
		logService.saveUserSessionLog(username, clientId, projectCode, usersession, objectId, functionalityCode,
				rightLevel, profileId);
	}

	@Override
	public String getFunctionalityCode(Long entityId) {
		if (entityId == null) {
			return getBrowserFunctionalityCode();
		}
		DashboardReport entity = getById(entityId);
		if (entity == null) {
			return getBrowserFunctionalityCode();
		}
		if (DashbordReportType.CHART.equals(entity.getReportType())) {
			return getBrowserFunctionalityCode();
		}
		return FunctionalityCodes.REPORTING_PIVOT_TABLE;
	}

	@Override
	public DashboardReportRepository getRepository() {
		return dashboardReportRepository;
	}

	@Override
	protected DashboardReport getNewItem() {
		DashboardReport dashboardReport = new DashboardReport();
		int i = 0;
		String baseName = "DashboardReport";
		dashboardReport.setName(baseName);

		while (getByName(dashboardReport.getName()) != null) {
			i++;
			dashboardReport.setName(baseName + i);
		}
		return dashboardReport;
	}

	protected DashboardReport getNewItem(String type) {
		DashboardReport dashboardReport = new DashboardReport();
		int i = 0;
		String baseName = "DashboardReport";
		if (StringUtils.hasText(type)) {
			DashbordReportType rType = DashbordReportType.valueOf(type);
			if (DashbordReportType.CHART.equals(rType)) {
				baseName = "Chart ";
			} else {
				if (DashbordReportType.PIVOT_TABLE.equals(rType)) {
					baseName = "Pivot Grid ";
				}
			}
		}
		dashboardReport.setName(baseName);
		while (getByName(dashboardReport.getName()) != null) {
			i++;
			dashboardReport.setName(baseName + i);
		}
		return dashboardReport;
	}

	@Override
	public EditorData<DashboardReport> getEditorData(EditorDataFilter filter, HttpSession session, Locale locale)
			throws Exception {
		DashboardReportEditorData data = new DashboardReportEditorData();
		if (filter.isNewData()) {
			data.setItem(getNewItem(filter.getSubjectType()));
			data.getItem().setDataSourceType(filter.getDataSourceType());
			data.getItem().setDataSourceId( filter.getDataSourceId());
		} else {
			data.setItem(getById(filter.getId()));
		}
		if(data.getItem() != null) {
			if(data.getItem().getDataSourceType().isMaterializedGrid()) {
				String dName = initEditorDataForMaterializedGrid(data, data.getItem().getDataSourceId(), session, locale);
				data.getItem().setDataSourceName(dName);
			}
			else if(data.getItem().getDataSourceType().isJoin()) {
				String dName = initEditorDataForJoin(data, data.getItem().getDataSourceId(), session, locale);
				data.getItem().setDataSourceName(dName);
			}
			else {
				initEditorData(data, session, locale);
			}
		}		
		data.setMatGrids(materializedGridRepository.findGenericAllAsNameables());		
		return data;
	}
	
	@Override
	protected void setDataSourceName(DashboardReport item) {
		if(item != null && item.getDataSourceType() == DataSourceType.MATERIALIZED_GRID && item.getDataSourceId() != null) {
			Optional<SmartMaterializedGrid> response = smartMaterializedGridRepository.findById(item.getDataSourceId());				
			SmartMaterializedGrid grid = response.isPresent() ? response.get() : null;
			item.setDataSourceName(grid != null ? grid.getName() : null);
		}
	}

	@Override
	protected BrowserData getNewBrowserData(DashboardReport item) {
		return new BrowserData(item);
	}

	@Override
	protected Specification<DashboardReport> getBrowserDatasSpecification(BrowserDataFilter filter, Locale locale,
			List<Long> hidedObjectIds) {
		return (root, query, cb) -> {
			RequestQueryBuilder<DashboardReport> qBuilder = new RequestQueryBuilder<DashboardReport>(root, query, cb);
			qBuilder.select(BrowserData.class);
			if (filter != null && filter.getReportType() != null) {
				qBuilder.addEquals("reportType", DashbordReportType.valueOf(filter.getReportType()));
			}
			if (filter != null && filter.getPublished() != null) {
				qBuilder.addEquals("published", filter.getPublished());
			}
			
			if (filter != null && filter.getDataSourceType() != null) {
				qBuilder.addEquals("dataSourceType", filter.getDataSourceType());
				if (filter.getDataSourceId() != null) {
					qBuilder.addEquals("dataSourceId", filter.getDataSourceId());
				}
			}

			if (filter != null && StringUtils.hasText(filter.getCriteria())) {
				qBuilder.addLikeCriteria("name", filter.getCriteria());
			}
			if (filter.getColumnFilters() != null) {
				build(filter.getColumnFilters());
				filter.getColumnFilters().getItems().forEach(filte -> {
					build(filte);
				});
				qBuilder.addFilter(filter.getColumnFilters());
			}
			return qBuilder.build();
		};
	}

	public ArrayNode getRows(Long reportOid, Locale locale) {
		DashboardReport report = getById(reportOid);
		if (report != null) {
			return getRows(report, locale);
		}
		return new ObjectMapper().createArrayNode();
	}

	@SuppressWarnings("unchecked")
	public ArrayNode getRows(DashboardReport report, Locale locale) {
		try {
			List<DashboardReportField> fields = report.getFieldListChangeHandler().getItems();
			Collections.sort(fields, new Comparator<DashboardReportField>() {
				@Override
				public int compare(DashboardReportField field1, DashboardReportField field2) {
					return field1.getPosition() - field2.getPosition();
				}
			});
			String sql = null;
			if(report.getDataSourceType().isMaterializedGrid()) {
				MaterializedGridDataFilter filter = buildMaterializedGridDataFilter(report, fields);
				DashboardReportMaterializedGridQueryBuilder builder = new DashboardReportMaterializedGridQueryBuilder(filter);
				builder.setCalculatedMeasureRepository(calculatedMeasureRepository);
				builder.setConsolidate(true);
				sql = builder.buildQuery();
			}
			else if(report.getDataSourceType().isJoin()) {
				JoinFilter filter = buildJoinFilter(report, fields);
				joinService.loadFilterClosures(filter);
				boolean ispublished = filter.getJoin() != null && filter.getJoin().isPublished();
				JoinQueryBuilder builder = ispublished ? new JoinPublishedQueryBuilder(filter, joinColumnRepository, spotService) 
						: new JoinQueryBuilder(filter, joinColumnRepository, spotService);
				builder.setAddMainGridOid(false);
				sql = builder.buildQuery();
			}
			else {
				GrilleDataFilter filter = buildGrilleDataFilter(report, fields);
				DashboardReportQueryBuilderV2 builder = new DashboardReportQueryBuilderV2(filter);
				builder.setCalculatedMeasureRepository(calculatedMeasureRepository);
				sql = builder.buildQuery();
			}
			
			log.trace("Dashboard report query : {}", sql);
			List<Object[]> datas = new ArrayList<>(0);
			if (StringUtils.hasText(sql)) {
				Query query = session.createNativeQuery(sql);
				datas = query.getResultList();
			}
			ArrayNode node = new DashboardReportJsonBuilder().build(fields, datas);
			return node;
		} catch (Exception ex) {
			log.error("Unable to retrieve dashboard report rows of dashboard : {}", report, ex);
			if(ex instanceof BcephalException) {
				throw (BcephalException)ex;
			}
			String message = getMessageSource().getMessage("unable.to.get.rows.dashboard.data", new Object[] { report },
					locale);
			throw new BcephalException(HttpStatus.INTERNAL_SERVER_ERROR.value(), message);
		}
	}
	

	private JoinFilter buildJoinFilter(DashboardReport report, List<DashboardReportField> fields) {
		JoinFilter filter = new JoinFilter();
		filter.setOrderAsc(true);
		Join join = joinService.getById(report.getDataSourceId());
		filter.setJoin(join);
		List<JoinColumn> columns = new ArrayList<>(0);		
		int position = 0;
		for (DashboardReportField field : fields) {
			field.setDashboardReport(report);
			JoinColumn column = join.getColumnByOid(field.getDimensionId());
			column.setPosition(position++);
			columns.add(column);
		}
		filter.getJoin().setColumns(columns);

		if (filter.getJoin() != null) {
			if(filter.getJoin().getFilter() == null) {
				filter.getJoin().setFilter(new UniverseFilter());
			}
			if(filter.getJoin().getAdminFilter() == null) {
				filter.getJoin().setAdminFilter(new UniverseFilter());
			}
			filter.getJoin().getFilter().add(report.getUserFilter());
			filter.getJoin().getAdminFilter().add(report.getAdminFilter());
			
			filter.getJoin().setFilter(report.getUserFilter());
			filter.getJoin().setAdminFilter(report.getAdminFilter());
			filter.getJoin().setGridUserFilter(report.getGridUserFilter());
			filter.getJoin().setGridAdminFilter(report.getGridAdminFilter());
		}
		UniverseDynamicFilter universeDynamicFilter = report.getCurrentDynamicFilter();
		if (universeDynamicFilter != null) {
			if (filter.getJoin().getFilter().getAttributeFilter() == null) {
				filter.getJoin().getFilter().setAttributeFilter(new AttributeFilter());
			}
			if (filter.getJoin().getFilter().getPeriodFilter() == null) {
				filter.getJoin().getFilter().setPeriodFilter(new PeriodFilter());
			}
			universeDynamicFilter.setItems(universeDynamicFilter.getSortedItems());
			universeDynamicFilter.getItems().forEach(item -> {
				if (item.getDimensionType().isAttribute()) {
					AttributeFilterItem filterItem = new AttributeFilterItem();
					filterItem.setDataSourceType(DataSourceType.JOIN);
					filterItem.setDataSourceId(join.getId());
					filterItem.setDimensionId(item.getDimensionId());
					filterItem.setDimensionType(item.getDimensionType());
					filterItem.setValue(item.getValues());
					filterItem.setFilterVerb(FilterVerb.AND);
					filter.getJoin().getFilter().getAttributeFilter().addItem(filterItem);
				} else if (item.getDimensionType().isPeriod()) {
					PeriodFilterItem filterItem = new PeriodFilterItem();
					filterItem.setDataSourceType(DataSourceType.JOIN);
					filterItem.setDataSourceId(join.getId());
					filterItem.setDimensionId(item.getDimensionId());
					filterItem.setDimensionType(item.getDimensionType());
					filterItem.setOperator(item.getStartDateValue().getDateOperator());
					filterItem.setValue(item.getStartDateValue().getDateValue());
					filterItem.setComparator(">=");
					filterItem.setFilterVerb(FilterVerb.AND);
					filter.getJoin().getFilter().getPeriodFilter().addItem(filterItem);

					filterItem = new PeriodFilterItem();
					filterItem.setDataSourceType(DataSourceType.JOIN);
					filterItem.setDataSourceId(join.getId());
					filterItem.setDimensionId(item.getDimensionId());
					filterItem.setDimensionType(item.getDimensionType());
					filterItem.setOperator(item.getEndDateValue().getDateOperator());
					filterItem.setValue(item.getEndDateValue().getDateValue());
					filterItem.setComparator("<=");
					filterItem.setFilterVerb(FilterVerb.AND);
					filter.getJoin().getFilter().getPeriodFilter().addItem(filterItem);
				}
			});			
		}
		return filter;
	}

	private MaterializedGridDataFilter buildMaterializedGridDataFilter(DashboardReport report, List<DashboardReportField> fields) {
		MaterializedGridDataFilter filter = new MaterializedGridDataFilter();
		filter.setOrderAsc(true);
		filter.setGrid(new MaterializedGrid(report.getDataSourceId()));
		filter.getGrid().setPublished(true);
		filter.getGrid().setColumns(new ArrayList<>(0));
		int position = 0;
		for (DashboardReportField field : fields) {
			field.setDashboardReport(report);
			MaterializedGridColumn column = buildMaterializedGridColumn(field, position++);
			filter.getGrid().getColumns().add(column);
		}

		if (filter.getGrid() != null) {
			filter.getGrid().setUserFilter(report.getUserFilter());
			filter.getGrid().setAdminFilter(report.getAdminFilter());
			filter.getGrid().setGridUserFilter(report.getGridUserFilter());
			filter.getGrid().setGridAdminFilter(report.getGridAdminFilter());
		}
		UniverseDynamicFilter universeDynamicFilter = report.getCurrentDynamicFilter();
		if (universeDynamicFilter != null) {
			if (filter.getGrid().getUserFilter().getAttributeFilter() == null) {
				filter.getGrid().getUserFilter().setAttributeFilter(new AttributeFilter());
			}
			if (filter.getGrid().getUserFilter().getPeriodFilter() == null) {
				filter.getGrid().getUserFilter().setPeriodFilter(new PeriodFilter());
			}
			universeDynamicFilter.setItems(universeDynamicFilter.getSortedItems());
			universeDynamicFilter.getItems().forEach(item -> {
				if (item.getDimensionType().isAttribute()) {
					AttributeFilterItem filterItem = new AttributeFilterItem();
					filterItem.setDataSourceType(DataSourceType.MATERIALIZED_GRID);
					filterItem.setDataSourceId(filter.getGrid().getId());
					filterItem.setDimensionId(item.getDimensionId());
					filterItem.setDimensionType(item.getDimensionType());
					filterItem.setValue(item.getValues());
					filterItem.setFilterVerb(FilterVerb.AND);
					filter.getGrid().getUserFilter().getAttributeFilter().addItem(filterItem);
				} else if (item.getDimensionType().isPeriod()) {
					PeriodFilterItem filterItem = new PeriodFilterItem();
					filterItem.setDataSourceType(DataSourceType.MATERIALIZED_GRID);
					filterItem.setDataSourceId(filter.getGrid().getId());
					filterItem.setDimensionId(item.getDimensionId());
					filterItem.setDimensionType(item.getDimensionType());
					filterItem.setOperator(item.getStartDateValue().getDateOperator());
					filterItem.setValue(item.getStartDateValue().getDateValue());
					filterItem.setComparator(">=");
					filterItem.setFilterVerb(FilterVerb.AND);
					filter.getGrid().getUserFilter().getPeriodFilter().addItem(filterItem);

					filterItem = new PeriodFilterItem();
					filterItem.setDataSourceType(DataSourceType.MATERIALIZED_GRID);
					filterItem.setDataSourceId(filter.getGrid().getId());
					filterItem.setDimensionId(item.getDimensionId());
					filterItem.setDimensionType(item.getDimensionType());
					filterItem.setOperator(item.getEndDateValue().getDateOperator());
					filterItem.setValue(item.getEndDateValue().getDateValue());
					filterItem.setComparator("<=");
					filterItem.setFilterVerb(FilterVerb.AND);
					filter.getGrid().getUserFilter().getPeriodFilter().addItem(filterItem);
				}
			});
		}
		return filter;
	}

	private MaterializedGridColumn buildMaterializedGridColumn(DashboardReportField field, int position) {
		MaterializedGridColumn column = new MaterializedGridColumn();
		column.setPublished(true);
		column.setId(field.getDimensionId());
		column.setType(field.getType());
		column.setPosition(position);
		column.setName(field.getName());
		if (field.getProperties() != null) {
			column.setGroupBy(field.getProperties().getGroupBy());
			column.setFormat(field.getProperties().getFormat());
			column.setDimensionFunction(field.getProperties().getDimensionFunction());
		}
		if(field.getType().isCalculatedMeasure() && field.getDimensionId() != null) {
			Optional<CalculatedMeasure> result = calculatedMeasureRepository.findById(field.getDimensionId());	
			column.setCalculatedMeasure(result.isPresent() ? result.get() : null);
			if(column.getCalculatedMeasure() != null) {
				column.getCalculatedMeasure().sortItems();
			}
		}
		if(field.getType().isPeriod()) {
			//column.setOrderAsc(true);	
		}
		return column;
	}

	public GrilleDataFilter buildGrilleDataFilter(DashboardReport report, List<DashboardReportField> fields) {
		GrilleDataFilter filter = new GrilleDataFilter();
		filter.setGrid(new Grille());
		filter.getGrid().setDataSourceType(report.getDataSourceType());
		filter.getGrid().setDataSourceId(report.getDataSourceId());
		filter.getGrid().setRowType(GrilleRowType.ALL);
		filter.getGrid().setType(GrilleType.REPORT);
		filter.getGrid().setConsolidated(true);
		filter.setOrderAsc(true);
		filter.getGrid().setColumns(new ArrayList<>(0));
		int position = 0;
		for (DashboardReportField field : fields) {
			field.setDashboardReport(report);
			GrilleColumn column = buildGrilleColumn(field, position++);
			filter.getGrid().getColumns().add(column);
		}
		
		if (filter.getGrid() != null) {
			filter.getGrid().setUserFilter(report.getUserFilter());
			filter.getGrid().setAdminFilter(report.getAdminFilter());
			filter.getGrid().setGridUserFilter(report.getGridUserFilter());
			filter.getGrid().setGridAdminFilter(report.getGridAdminFilter());
		}
		UniverseDynamicFilter universeDynamicFilter = report.getCurrentDynamicFilter();
		if (universeDynamicFilter != null) {

			if (filter.getGrid().getUserFilter().getAttributeFilter() == null) {
				filter.getGrid().getUserFilter().setAttributeFilter(new AttributeFilter());
			}
			if (filter.getGrid().getUserFilter().getPeriodFilter() == null) {
				filter.getGrid().getUserFilter().setPeriodFilter(new PeriodFilter());
			}
			universeDynamicFilter.setItems(universeDynamicFilter.getSortedItems());
			universeDynamicFilter.getItems().forEach(item -> {
				if (item.getDimensionType().isAttribute()) {
					AttributeFilterItem filterItem = new AttributeFilterItem();
					filterItem.setDimensionId(item.getDimensionId());
					filterItem.setDimensionType(item.getDimensionType());
					filterItem.setValue(item.getValues());
					filterItem.setFilterVerb(FilterVerb.AND);
					filter.getGrid().getUserFilter().getAttributeFilter().addItem(filterItem);
				} else if (item.getDimensionType().isPeriod()) {
					PeriodFilterItem filterItem = new PeriodFilterItem();
					filterItem.setDimensionId(item.getDimensionId());
					filterItem.setDimensionType(item.getDimensionType());
					filterItem.setOperator(item.getStartDateValue().getDateOperator());
					filterItem.setValue(item.getStartDateValue().getDateValue());
					filterItem.setComparator(">=");
					filterItem.setFilterVerb(FilterVerb.AND);
					filter.getGrid().getUserFilter().getPeriodFilter().addItem(filterItem);

					filterItem = new PeriodFilterItem();
					filterItem.setDimensionId(item.getDimensionId());
					filterItem.setDimensionType(item.getDimensionType());
					filterItem.setOperator(item.getEndDateValue().getDateOperator());
					filterItem.setValue(item.getEndDateValue().getDateValue());
					filterItem.setComparator("<=");
					filterItem.setFilterVerb(FilterVerb.AND);
					filter.getGrid().getUserFilter().getPeriodFilter().addItem(filterItem);
				}
			});
		}
		return filter;
	}

	private GrilleColumn buildGrilleColumn(DashboardReportField field, int position) {
		GrilleColumn column = new GrilleColumn();
		column.setDimensionId(field.getDimensionId());
		column.setType(field.getType().isPeriod() ? DimensionType.PERIOD
				: field.getType().isMeasure() ? DimensionType.MEASURE 
				: field.getType().isCalculatedMeasure() ? DimensionType.CALCULATED_MEASURE : DimensionType.ATTRIBUTE);
		column.setPosition(position);
		column.setName(field.getName());
		if (field.getProperties() != null) {
			column.setGroupBy(field.getProperties().getGroupBy());
			column.setFormat(field.getProperties().getFormat());
			column.setDimensionFunction(field.getProperties().getDimensionFunction());
		}
		if(field.getType().isCalculatedMeasure() && field.getDimensionId() != null) {
			Optional<CalculatedMeasure> result = calculatedMeasureRepository.findById(field.getDimensionId());	
			column.setCalculatedMeasure(result.isPresent() ? result.get() : null);
			if(column.getCalculatedMeasure() != null) {
				column.getCalculatedMeasure().sortItems();
			}
		}
		if(field.getType().isPeriod()) {
			//column.setOrderAsc(true);	
		}
		return column;
	}

	@Override
	@Transactional
	public DashboardReport save(DashboardReport dashboardReport, Locale locale) {
		log.debug("Try to  Save Grid : {}", dashboardReport);
		try {
			if (dashboardReport == null) {
				String message = getMessageSource().getMessage("unable.to.save.null.dashboard.report",
						new Object[] { dashboardReport }, locale);
				throw new BcephalException(message);
			}
			if (!StringUtils.hasLength(dashboardReport.getName())) {
				String message = getMessageSource().getMessage("unable.to.save.dashboard.report.with.empty.name",
						new String[] { dashboardReport.getName() }, locale);
				throw new BcephalException(message);
			}

			if (dashboardReport.getChartProperties() != null) {
				dashboardReport
						.setChartProperties(chartPropertiesRepository.save(dashboardReport.getChartProperties()));
			}

			if (dashboardReport.getPivotTableProperties() != null) {
				dashboardReport.setPivotTableProperties(
						pivotTablePropertiesRepository.save(dashboardReport.getPivotTableProperties()));
			}

			if (dashboardReport.getUserFilter() != null) {
				dashboardReport.setUserFilter(universeFilterService.save(dashboardReport.getUserFilter()));
			}

			if (dashboardReport.getAdminFilter() != null) {
				dashboardReport.setAdminFilter(universeFilterService.save(dashboardReport.getAdminFilter()));
			}

			if (dashboardReport.getOthersDimensions() != null) {
				dashboardReport.setOthersDimensions(universeFilterService.save(dashboardReport.getOthersDimensions()));
			}
			if (dashboardReport.getDynamicFilter() != null) {
				dashboardReport.setDynamicFilter(dynamicFilterService.save(dashboardReport.getDynamicFilter(), locale));
			}

			ListChangeHandler<DashboardReportField> items = dashboardReport.getFieldListChangeHandler();

			dashboardReport.setModificationDate(new Timestamp(System.currentTimeMillis()));
			validateBeforeSave(dashboardReport, locale);
			dashboardReport = dashboardReportRepository.save(dashboardReport);
			DashboardReport id = dashboardReport;
			items.getNewItems().forEach(item -> {
				log.trace("Try to save Dashboard Report Field : {}", item);
				item.setDashboardReport(id);
				dashboardReportFieldService.save(item, locale);
				log.trace(" Dashboard Report Field saved : {}", item.getId());
			});
			items.getUpdatedItems().forEach(item -> {
				log.trace("Try to save  Dashboard Report Field : {}", item);
				item.setDashboardReport(id);
				dashboardReportFieldService.save(item, locale);
				log.trace(" Dashboard Report Fieldsaved : {}", item.getId());
			});
			items.getDeletedItems().forEach(item -> {
				if (item.getId() != null) {
					log.trace("Try to delete  Dashboard Report Field : {}", item);
					dashboardReportFieldService.delete(item);
					log.trace(" Dashboard Report Field deleted : {}", item.getId());
				}
			});

			log.debug("Dashboard saved : {} ", dashboardReport.getId());
			return dashboardReport;
		} catch (BcephalException e) {
			throw e;
		} catch (Exception e) {
			log.error("Unexpected error while save dashboard : {}", dashboardReport, e);
			String message = getMessageSource().getMessage("unable.to.save.dashboard", new Object[] { dashboardReport },
					locale);
			throw new BcephalException(HttpStatus.INTERNAL_SERVER_ERROR.value(), message);
		}
	}

	@Override
	@Transactional
	public void delete(DashboardReport dashboardReport) {
		log.debug("Try to delete dashboard report : {}", dashboardReport);
		if (dashboardReport == null || dashboardReport.getId() == null) {
			return;
		}
		ListChangeHandler<DashboardReportField> items = dashboardReport.getFieldListChangeHandler();
		items.getItems().forEach(item -> {
			if (item.getId() != null) {
				log.trace("Try to delete dashboard report field : {}", item);
				dashboardReportFieldService.delete(item);
				log.trace("Dashboard report field deleted : {}", item.getId());
			}
		});
		if (dashboardReport.getChartProperties() != null && dashboardReport.getChartProperties().getId() != null) {
			chartPropertiesRepository.deleteById(dashboardReport.getChartProperties().getId());
		}

		if (dashboardReport.getPivotTableProperties() != null
				&& dashboardReport.getPivotTableProperties().getId() != null) {
			pivotTablePropertiesRepository.deleteById(dashboardReport.getPivotTableProperties().getId());
		}
		if (dashboardReport.getUserFilter() != null) {
			universeFilterService.delete(dashboardReport.getUserFilter());
		}

		if (dashboardReport.getAdminFilter() != null) {
			universeFilterService.delete(dashboardReport.getAdminFilter());
		}
		if (dashboardReport.getOthersDimensions() != null) {
			universeFilterService.delete(dashboardReport.getOthersDimensions());
		}
		dashboardReportRepository.deleteById(dashboardReport.getId());
		log.debug("dashboard report successfully to delete : {} ", dashboardReport);
		return;
	}

	@Autowired
	ResourceLoader resourceLoader;

	public String exportToPdf(GrilleDataFilter filter, String fileName, Locale locale) {

		fileName = fileName.replaceAll("/", "_");
		fileName = fileName.replaceAll("\\\\", "_");
		try {
			String billName = fileName.concat("___" + System.currentTimeMillis()).concat(".").concat("pdf");
			String tempDirPath = System.getProperty("java.io.tmpdir");
			File billFile = Paths.get(tempDirPath, "bcephal", billName).toFile();
			if (billFile != null && !billFile.exists()) {
				if (billFile.getParentFile() != null && !billFile.getParentFile().exists()) {
					billFile.getParentFile().mkdirs();
				}
			}

			if (billFile == null) {
				return null;
			}
			Resource reportTemplate = resourceLoader.getResource("classpath:report/report_chart.jasper");

			String path = reportTemplate.getFile().getParentFile().toURI().toURL().toExternalForm();
			JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(Arrays.asList(filter));
			Map<String, Object> parameters = new HashMap<>();
			parameters.put("createdBy", "Bcephal");
			parameters.put("SUBREPORT_DIR", path);
			URL resUrl = null;
			try {
				resUrl = Paths.get(path).toUri().toURL();
				parameters.put(JRParameter.REPORT_LOCALE, locale);
				parameters.put(JRParameter.REPORT_RESOURCE_BUNDLE, ResourceBundle.getBundle("messages", locale,
						new URLClassLoader(new URL[] { resUrl }), new UTF8Control()));
				parameters.put("net.sf.jasperreports.default.pdf.encoding", "UTF-8");
			} catch (Exception e) {
				log.trace("Unable to find locale resource bundle {}.", e);
			}
			JasperPrint jasperPrint = JasperFillManager.fillReport(reportTemplate.getInputStream(), parameters,
					dataSource);
			JasperExportManager.exportReportToPdfFile(jasperPrint, billFile.getPath());
			log.trace("Bill printed : {}", billFile.getPath());
			return billFile.getPath();
		} catch (Exception e) {
			throw new BcephalException("Unable to print bill.", e);
		}
	}
	
	
	
	
	
	private BrowserDataPage<Object[]> SearchRowDatas(BrowserDataFilter filter, String sqlCount, String sql){
		BrowserDataPage<Object[]> page = new BrowserDataPage<Object[]>();
		page.setPageSize(filter.getPageSize());
			Integer count = 0;
			if(filter.isAllowRowCounting()) {
				log.trace("Count query : {}", sqlCount);
				Query queryCount = this.session.createNativeQuery(sqlCount);
				Number number = (Number)queryCount.getSingleResult();
				count = number.intValue();
				
				if(filter.isShowAll()) {
					page.setTotalItemCount(count);
					page.setPageCount(1);
					page.setCurrentPage(1);
					page.setPageFirstItem(1);
				}
				else {
					page.setTotalItemCount(count);
					page.setPageCount(((int)count/filter.getPageSize()) + ((count % filter.getPageSize()) > 0 ? 1 : 0));
					page.setCurrentPage(filter.getPage() > 0 ? filter.getPage() : 1);
					if(page.getCurrentPage() > page.getPageCount()) {
						page.setCurrentPage(page.getPageCount());
					}
					page.setPageFirstItem(((page.getCurrentPage() - 1) * page.getPageSize()) + 1);
				}
			} 
			else {
				page.setTotalItemCount(1);
				page.setPageCount(1);
				page.setCurrentPage(filter.getPage() > 0 ? filter.getPage() : 1);
				page.setPageFirstItem(((page.getCurrentPage() - 1) * page.getPageSize()) + 1);
			}
						
			log.trace("Search query : {}", sql);
			Query query = this.session.createNativeQuery(sql);
			if(!filter.isShowAll()) {
				query.setFirstResult(page.getPageFirstItem() - 1);
				query.setMaxResults(page.getPageSize());
			}
			@SuppressWarnings("unchecked")
			List<Object[]> objects = query.getResultList();
			
			page.setPageLastItem(page.getPageFirstItem() + objects.size() -1);
			page.setItems(objects); 
			return page;
	}
	
	static Long PAGE_SIZE = (long) 100000;
	
	public List<String> export(DashboardReport report, GrilleExportDataType type, java.util.Locale locale)
			throws Exception {
		List<String> paths = new ArrayList<String>();
		boolean canSearch = true;
		Long minPageSize = (long) 25000;		
		BrowserDataFilter filter = null;
		List<DashboardReportField> fields = report.getFieldListChangeHandler().getItems();
		Collections.sort(fields, new Comparator<DashboardReportField>() {
			@Override
			public int compare(DashboardReportField field1, DashboardReportField field2) {
				return field1.getPosition() - field2.getPosition();
			}
		});
		String sqlCount = null;
		String sql = null;
		if(report.getDataSourceType().isMaterializedGrid()) {
			filter = buildMaterializedGridDataFilter(report, fields);
			MaterializedGridQueryBuilder builder = new MaterializedGridQueryBuilder((MaterializedGridDataFilter)filter);
			builder.setConsolidate(true);
			sqlCount = builder.buildCountQuery();
			sql = builder.buildQuery();
			for (MaterializedGridColumn column : ((MaterializedGridDataFilter)filter).getGrid().getColumns()) {
				((MaterializedGridDataFilter)filter).getGrid().getColumnListChangeHandler().addNew(column);
			}
		}
		else if(report.getDataSourceType().isJoin()) {
			filter = buildJoinFilter(report, fields);
			joinService.loadFilterClosures((JoinFilter)filter);
			boolean ispublished = ((JoinFilter)filter).getJoin() != null && ((JoinFilter)filter).getJoin().isPublished();
			JoinQueryBuilder builder = ispublished ? new JoinPublishedQueryBuilder((JoinFilter)filter, joinColumnRepository, spotService) 
					: new JoinQueryBuilder((JoinFilter)filter, joinColumnRepository, spotService);
			builder.setAddMainGridOid(false);
			sqlCount = builder.buildCountQuery();
			sql = builder.buildQuery();
			for (JoinColumn column : ((JoinFilter)filter).getJoin().getColumns()) {
				((JoinFilter)filter).getJoin().getColumnListChangeHandler().addNew(column);
			}
		}
		else {
			filter = buildGrilleDataFilter(report, fields);
			DashboardReportQueryBuilder builder = new DashboardReportQueryBuilder((GrilleDataFilter)filter);
			sqlCount = builder.buildCountQuery();
			sql = builder.buildQuery();
			for (GrilleColumn column : ((GrilleDataFilter)filter).getGrid().getColumns()) {
				((GrilleDataFilter)filter).getGrid().getColumnListChangeHandler().addNew(column);
			}
		}
		
		filter.setPageSize(minPageSize.intValue());
		filter.setPage(1);
		filter.setAllowRowCounting(false);
		int offste = 0;
		String path = null;
		GridExporter excelExporter = null;
		GridCsvExporter csvExporter = null;
		GridJsonExporter jsonExporter = null;
		
		JoinExporter excelJoinExporter = null;
		JoinCsvExporter csvJoinExporter = null;
		JoinJsonExporter jsonJoinExporter = null;
		
		MaterializedExporter excelMatExporter = null;
		MaterializedCsvExporter csvMatExporter = null;
		MaterializedJsonExporter jsonMatExporter = null;
		
		while (canSearch) {
			offste++;			
			BrowserDataPage<Object[]> page = SearchRowDatas(filter, sqlCount, sql);					
			if(path == null) {
				if (report.getDataSourceType().isMaterializedGrid()) {
					path = getPath(offste + " - " + ((MaterializedGridDataFilter)filter).getGrid().getName(), type);
				} else if (report.getDataSourceType().isJoin()) {
					path = getPath(offste + " - " + ((JoinFilter)filter).getJoin().getName(), type);
				} else {
					path = getPath(offste + " - " + ((GrilleDataFilter)filter).getGrid().getName(), type);
				}
			}
			boolean end = page.getItems().size() < minPageSize || (page.getPageLastItem()) % PAGE_SIZE == 0;
			if (type == GrilleExportDataType.EXCEL) {
				if(excelExporter == null) {
					if (report.getDataSourceType().isMaterializedGrid()) {
						  MaterializedGridDataFilter filter_ = ((MaterializedGridDataFilter)filter);
						  List<Long> columnsIds= filter_.getGrid().getColumns().stream().flatMap(p -> Stream.of(p.getId()))
								  .collect(Collectors.toList());
						excelMatExporter = new MaterializedExporter(filter_.getGrid(),columnsIds, filter_.isExportAllColumns());
					} else if (report.getDataSourceType().isJoin()) {
						JoinFilter filter_ = ((JoinFilter)filter);
						List<Long> columnsIds= filter_.getJoin().getColumns().stream().flatMap(p -> Stream.of(p.getId()))
								  .collect(Collectors.toList());
						excelJoinExporter = new JoinExporter(filter_.getJoin(),columnsIds, filter_.isExportAllColumns());
					} else {
						GrilleDataFilter filter_ = ((GrilleDataFilter)filter);
						List<Long> columnsIds= filter_.getGrid().getColumns().stream().flatMap(p -> Stream.of(p.getId()))
								  .collect(Collectors.toList());
						excelExporter = new GridExporter(filter_.getGrid(),columnsIds, filter_.isExportAllColumns());
					}
				}
				if (report.getDataSourceType().isMaterializedGrid()) {
					excelMatExporter.writeRowss(page.getItems(), true);
				} else if (report.getDataSourceType().isJoin()) {
					excelJoinExporter.writeRowss(page.getItems(), true);
				}else {
					excelExporter.writeRowss(page.getItems(), true);
				}
				
				if(end) {
					
					if (report.getDataSourceType().isMaterializedGrid()) {
						excelMatExporter.writeFiles(path);
					} else if (report.getDataSourceType().isJoin()) {
						excelJoinExporter.writeFiles(path);
					}else {
						excelExporter.writeFiles(path);
					}					
				}
			} else if (type == GrilleExportDataType.CSV) {
				if(csvExporter == null) {
					if (report.getDataSourceType().isMaterializedGrid()) {
						  MaterializedGridDataFilter filter_ = ((MaterializedGridDataFilter)filter);
						 List<Long> columnsIds= filter_.getGrid().getColumns().stream().flatMap(p -> Stream.of(p.getId()))
						  .collect(Collectors.toList());
						  csvMatExporter = new MaterializedCsvExporter(path, filter_.getGrid(),true,columnsIds, filter_.isExportAllColumns());
					} else if (report.getDataSourceType().isJoin()) {
						JoinFilter filter_ = ((JoinFilter)filter);
						List<Long> columnsIds= filter_.getJoin().getColumns().stream().flatMap(p -> Stream.of(p.getId()))
								  .collect(Collectors.toList());
						csvJoinExporter = new JoinCsvExporter(path, filter_.getJoin(),true,columnsIds, filter_.isExportAllColumns());
					} else {
						GrilleDataFilter filter_ = ((GrilleDataFilter)filter);
						List<Long> columnsIds= filter_.getGrid().getColumns().stream().flatMap(p -> Stream.of(p.getId()))
								  .collect(Collectors.toList());
						csvExporter = new GridCsvExporter(path, filter_.getGrid(), true, columnsIds, filter_.isExportAllColumns());
					}
				}
				if (report.getDataSourceType().isMaterializedGrid()) {
					csvMatExporter.export(page.getItems(), true);
				} else if (report.getDataSourceType().isJoin()) {
					csvJoinExporter.export(page.getItems(), true);
				}else {
					csvExporter.export(page.getItems(), true);
				}
				if(end) {
					
					if (report.getDataSourceType().isMaterializedGrid()) {
						csvMatExporter.close();
					} else if (report.getDataSourceType().isJoin()) {
						csvJoinExporter.close();
					}else {
						csvExporter.close();
					}					
				}
			} else if (type == GrilleExportDataType.JSON) {
				if(jsonExporter == null) {
					if (report.getDataSourceType().isMaterializedGrid()) {
						  MaterializedGridDataFilter filter_ = ((MaterializedGridDataFilter)filter);
						  List<Long> columnsIds= filter_.getGrid().getColumns().stream().flatMap(p -> Stream.of(p.getId()))
								  .collect(Collectors.toList());
						jsonMatExporter = new MaterializedJsonExporter(path, filter_.getGrid(),true,columnsIds, filter_.isExportAllColumns());
					} else if (report.getDataSourceType().isJoin()) {
						JoinFilter filter_ = ((JoinFilter)filter);
						List<Long> columnsIds= filter_.getJoin().getColumns().stream().flatMap(p -> Stream.of(p.getId()))
								  .collect(Collectors.toList());
						jsonJoinExporter = new JoinJsonExporter(path, filter_.getJoin(),true,columnsIds, filter_.isExportAllColumns());
					} else  {
						GrilleDataFilter filter_ = ((GrilleDataFilter)filter);
						List<Long> columnsIds= filter_.getGrid().getColumns().stream().flatMap(p -> Stream.of(p.getId()))
								  .collect(Collectors.toList());
						jsonExporter = new GridJsonExporter(path, filter_.getGrid(),true,columnsIds, filter_.isExportAllColumns());
					}
						
				}
				if (report.getDataSourceType().isMaterializedGrid()) {
					jsonMatExporter.export(page.getItems(), true);
				} else if (report.getDataSourceType().isJoin()) {
					jsonJoinExporter.export(page.getItems(), true);
				}else {
					jsonExporter.export(page.getItems(), true);
				}
				if(end) {
					
					if (report.getDataSourceType().isMaterializedGrid()) {
						jsonMatExporter.close();
					} else if (report.getDataSourceType().isJoin()) {
						jsonJoinExporter.close();
					}else {
						jsonExporter.close();
					}					
				}
			}
			if(end) {
				paths.add(path);
				path = null;
				jsonExporter = null;
				csvExporter = null;
				excelExporter = null;
								
				excelJoinExporter = null;
				csvJoinExporter = null;
				jsonJoinExporter = null;
				 
				excelMatExporter = null;
				csvMatExporter = null;
				jsonMatExporter = null;
			}
			
			if (page.getItems().size() < minPageSize ) {
				canSearch = false;
			} else {
				filter.setPage(filter.getPage() + 1);
			}
		}
		return paths;
	}
	
	
	public String getPath(String baseName, GrilleExportDataType type, int filenbr) {
		String path = FilenameUtils.concat(System.getProperty("java.io.tmpdir"), "bcephal");
		if (!new File(path).exists()) {
			new File(path).mkdirs();
		}
		String fileName = baseName + "_" + filenbr + type.getExtension(); 
		return FilenameUtils.concat(path, fileName);
	}
	
	public String getPath(String fileName, GrilleExportDataType type) {
		String path = FilenameUtils.concat(System.getProperty("java.io.tmpdir"), "bcephal");
		if (!new File(path).exists()) {
			new File(path).mkdirs();
		}
		return FilenameUtils.concat(path, getFileName(fileName, type));
	}

	public String getFileName(String fileName, GrilleExportDataType type ) {
		return fileName + System.currentTimeMillis() + type.getExtension();
	}



}
