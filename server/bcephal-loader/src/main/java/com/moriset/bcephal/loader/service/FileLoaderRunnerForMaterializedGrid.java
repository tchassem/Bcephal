/**
 * 
 */
package com.moriset.bcephal.loader.service;

import java.io.IOException;
import java.io.StringReader;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.apache.commons.csv.CSVRecord;
import org.hibernate.Session;
import org.hibernate.jdbc.Work;
import org.postgresql.PGConnection;
import org.postgresql.copy.CopyManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.moriset.bcephal.domain.dimension.DimensionType;
import com.moriset.bcephal.domain.universe.UniverseParameters;
import com.moriset.bcephal.grid.domain.GrilleColumnCategory;
import com.moriset.bcephal.grid.domain.MaterializedGrid;
import com.moriset.bcephal.grid.domain.MaterializedGridColumn;
import com.moriset.bcephal.grid.service.MaterializedGridService;
import com.moriset.bcephal.loader.domain.FileLoaderLogItem;
import com.moriset.bcephal.utils.BcephalException;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Joseph Wambo
 *
 */
@Scope(scopeName = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
@Component
@Data
@Slf4j
@EqualsAndHashCode(callSuper = false)
public class FileLoaderRunnerForMaterializedGrid extends FileLoaderRunner {

	protected MaterializedGrid grid;

	@Autowired
	protected MaterializedGridService materializedGridService;

	@Override
	protected List<Object> buildLineToWrite(String line, String fileName, String separator, long lineNbr,
			FileLoaderLogItem logItem) throws Exception {
		if (line.endsWith(separator)) {
			line += " ";
		}
		String[] input = line.split(separator);
		List<Object> output = new ArrayList<>(0);		
		List<MaterializedGridColumn> systemColumns = new ArrayList<>(0);
		List<MaterializedGridColumn> userColums = new ArrayList<>(0);
		for(MaterializedGridColumn column : this.grid.getColumns()){
			if(column.getCategory() == null || column.getCategory() == GrilleColumnCategory.USER || column.getCategory() == GrilleColumnCategory.SYSTEM) {
				userColums.add(column);
			}
			else {
				systemColumns.add(column);
			}
		}		

		int size = input.length;
		Date loadDate = new Date();
		for(MaterializedGridColumn column : systemColumns) {
			if(column.getCategory() == GrilleColumnCategory.LOAD_NBR) {
				output.add(loadNbr);
			}
			else if(column.getCategory() == GrilleColumnCategory.LOAD_DATE) {
				output.add(new SimpleDateFormat("yyyy-MM-dd").format(loadDate));
			}
			else if(column.getCategory() == GrilleColumnCategory.LOAD_USER) {
				output.add(username);
			}			
			else if(column.getCategory() == GrilleColumnCategory.LOAD_MODE) {
				String mode = getData().getMode() != null ? getData().getMode().toString() : "M";
				output.add(mode);				
			}
			else if(column.getCategory() == GrilleColumnCategory.LOAD_SOURCE_NAME) {
				output.add(fileName != null ? fileName : "");
			}
			else if(column.getCategory() == GrilleColumnCategory.OPERATION_CODE) {
				output.add(operationCode);
			}
		}
		
		int position = 0;
		for(MaterializedGridColumn column : userColums){	
			if (this.isStopped()) {
				break;
			}
			String cell = size > position ? input[position] : "";
			if (cell != null) {
				cell = cell.trim();
				if (cell != null && cell.contains("::")) {
					cell = cell.replace("::", ";");
				}
			}

			if (!StringUtils.hasText(cell.trim())) {
				Object value = getDefaultValue(column);
				output.add(value);
			} else {
				if (column.isPeriod()) {
					try {
						String date = formatDate(cell.trim());
						output.add(date);
					} catch (Exception e) {
						log.trace("Unable to parse date!", e);
						if(e instanceof BcephalException) {
							throw new BcephalException("Line: " + lineNbr + "\nColumn: " + (position + 1)
									+ "\n" + e.getMessage());
						}
						throw new BcephalException("Line: " + lineNbr + "\nColumn: " + (position + 1)
								+ "\nUnparseable date: " + cell);
					}
				} else if (column.isMeasure()) {
					if (!StringUtils.hasText(cell.trim())) {
						output.add("");
					} else {
						
						try {													
							Number amount = formatDecimal(cell.trim());
							output.add(amount);							
						} catch (ParseException e) {
							log.trace("Unable to parse amount!", e);						
							throw new BcephalException("Cell [Line: {} ; Column: {}] \nUnparseable amount : {}",
									lineNbr, (column.getPosition() + 1), cell);
						}						
					}
				} else {
					output.add(cell.trim());
				}
			}
			position++;
		}

		return output;
	}
	
	
	@Override
	protected List<Object> buildLineToWrite(CSVRecord line, String fileName, String separator, long lineNbr,
			FileLoaderLogItem logItem) throws Exception {
		
		List<Object> output = new ArrayList<>(0);
		
		List<MaterializedGridColumn> systemColumns = new ArrayList<>(0);
		List<MaterializedGridColumn> userColums = new ArrayList<>(0);
		for(MaterializedGridColumn column : this.grid.getColumns()){
			if(column.getCategory() == null || column.getCategory() == GrilleColumnCategory.USER || column.getCategory() == GrilleColumnCategory.SYSTEM) {
				userColums.add(column);
			}
			else {
				systemColumns.add(column);
			}
		}
		
		int size = line.size();		

		Date loadDate = new Date();
		for(MaterializedGridColumn column : systemColumns) {
			if(column.getCategory() == GrilleColumnCategory.LOAD_NBR) {
				output.add(loadNbr);
			}
			else if(column.getCategory() == GrilleColumnCategory.LOAD_DATE) {
				output.add(new SimpleDateFormat("yyyy-MM-dd").format(loadDate));
			}
			else if(column.getCategory() == GrilleColumnCategory.LOAD_USER) {
				output.add(username);
			}			
			else if(column.getCategory() == GrilleColumnCategory.LOAD_MODE) {
				String mode = getData().getMode() != null ? getData().getMode().toString() : "M";
				output.add(mode);			
			}
			else if(column.getCategory() == GrilleColumnCategory.LOAD_SOURCE_NAME) {
				output.add(fileName != null ? fileName : "");
			}
			else if(column.getCategory() == GrilleColumnCategory.OPERATION_CODE) {
				output.add(operationCode);
			}
		}
		
		int position = 0;
		for(MaterializedGridColumn column : userColums) {
			if (this.isStopped()) {
				break;
			}
			
			String cell = size > position ? line.get(position) : "";
			if (cell != null) {
				cell = cell.trim();
				if (cell != null && cell.contains("::")) {
					cell = cell.replace("::", ";");
				}
			}

			if (!StringUtils.hasText(cell.trim())) {
				Object value = getDefaultValue(column);
				output.add(value);
			} else {
				if (column.isPeriod()) {					
					try {
						String date = formatDate(cell.trim());
						output.add(date);
					} catch (ParseException e) {
						log.trace("Unable to parse date!", e);
						throw new BcephalException("Line: " + lineNbr + "\nColumn: " + (position + 1)
								+ "\nUnparseable date: " + cell);
					}
				} else if (column.isMeasure()) {
					if (!StringUtils.hasText(cell.trim())) {
						output.add("");
					} else {
						try {
							
							Number amount = formatDecimal(cell.trim());
							output.add(amount);							
						} catch (ParseException e) {
							log.trace("Unable to parse amount!", e);					
							throw new BcephalException("Cell [Line: {} ; Column: {}] \nUnparseable amount : {}",
									lineNbr, (column.getPosition() + 1), cell);
						}
					}
				} else {
					output.add(cell.trim());
				}
			}
			position++;
		}
		return output;
	}
	
	

	@Override
	protected List<Object> buildExcelLineToWrite(List<Object> line, String fileName, long lineNbr,
			FileLoaderLogItem logItem) throws Exception {
		
		List<Object> output = new ArrayList<>(0);
		List<MaterializedGridColumn> systemColumns = new ArrayList<>(0);
		List<MaterializedGridColumn> userColums = new ArrayList<>(0);
		for(MaterializedGridColumn column : this.grid.getColumns()){
			if(column.getCategory() == null || column.getCategory() == GrilleColumnCategory.USER || column.getCategory() == GrilleColumnCategory.SYSTEM) {
				userColums.add(column);
			}
			else {
				systemColumns.add(column);
			}
		}		

		int size = line.size();
//		if (size < userColums.size()) {
//			throw new BcephalException("Line: " + lineNbr + "\nWrong column count! Number of columns found: " + size
//					+ ". Number of columns expected: " + this.grid.getColumns().size());
//		}

		Date loadDate = new Date();
		for(MaterializedGridColumn column : systemColumns) {
			if(column.getCategory() == GrilleColumnCategory.LOAD_NBR) {
				output.add(loadNbr);
			}
			else if(column.getCategory() == GrilleColumnCategory.LOAD_DATE) {
				output.add(new SimpleDateFormat("yyyy-MM-dd").format(loadDate));
			}
			else if(column.getCategory() == GrilleColumnCategory.LOAD_USER) {
				output.add(username);
			}			
			else if(column.getCategory() == GrilleColumnCategory.LOAD_MODE) {
				String mode = getData().getMode() != null ? getData().getMode().toString() : "M";
				output.add(mode);					
			}
			else if(column.getCategory() == GrilleColumnCategory.LOAD_SOURCE_NAME) {
				output.add(fileName != null ? fileName : "");
			}
			else if(column.getCategory() == GrilleColumnCategory.OPERATION_CODE) {
				output.add(operationCode);
			}
		}
		
		int position = 0;
		for(MaterializedGridColumn column : userColums) {
			if (this.isStopped()) {
				break;
			}
			Object cell = size > position ? line.get(position) : null;

			if (cell == null) {
				Object value = getDefaultValue(column);
				output.add(value);
			} else {
				if (column.isPeriod()) {
					try {
						if (cell instanceof String) {
							String date = (String) cell;
							output.add(date);
						} else {
							String date = formatDate((Date) cell);
							output.add(date);
						}
					} catch (ParseException e) {
						log.trace("Unable to parse date!", e);
						throw new BcephalException("Cell [Line: {} ; Column: {}] \nUnparseable date : {}", lineNbr,
								(position + 1), cell);
					}
				} else if (column.isMeasure()) {
					try {
						if (cell instanceof Double) {
							output.add(cell);
						} 
						else if (cell instanceof String) {
							if(!StringUtils.hasText(cell.toString().trim())) {
								output.add("");
							}						
							else {	
								try {
									Number value = formatDecimal(cell.toString());
									output.add(value);
								} catch (Exception e) {
									throw new BcephalException("Cell [Line: {} ; Column: {}] \nUnparseable amount : {}",
											lineNbr, (position + 1), cell);
								}
//								Number amount = DecimalFormat.getInstance().parse(cell.toString());
//								String pattern = "####.##############";
//								DecimalFormat df = new DecimalFormat(pattern);
//								try {
//									Number value = (Number) df.parse(df.format(amount));
//									output.add(value);
//								} catch (Exception e) {
//									throw new BcephalException("Cell [Line: {} ; Column: {}] \nUnparseable amount : {}",
//											lineNbr, (position + 1), cell);
//								}
							}
						}
						else {
							
							try {
								Number value = formatDecimal(cell.toString());
								output.add(value);
							} catch (Exception e) {
								throw new BcephalException("Cell [Line: {} ; Column: {}] \nUnparseable amount : {}",
										lineNbr, (position + 1), cell);
							}
//							Number amount = DecimalFormat.getInstance().parse(cell.toString());
//							String pattern = "####.##############";
//							DecimalFormat df = new DecimalFormat(pattern);
//							try {
//								Number value = (Number) df.parse(df.format(amount));
//								output.add(value);
//							} catch (Exception e) {
//								throw new BcephalException("Cell [Line: {} ; Column: {}] \nUnparseable amount : {}",
//										lineNbr, (position + 1), cell);
//							}
						}
					} catch (Exception e) {
						log.trace("Unable to parse amount!", e);
						throw new BcephalException("Cell [Line: {} ; Column: {}] \nUnparseable amount : {}", lineNbr,
								(position + 1), cell);
					}
				} else {
					if(cell instanceof Double) {
						String val = new BigDecimal((Double)cell).toPlainString();
						output.add(val);
					}
					else {
						output.add(cell);
					}
				}
			}
			position++;
		}

		return output;
	}

	protected Object getDefaultValue(MaterializedGridColumn column) {
		return null;
	}

	@Override
	protected String buildCsvFileColumns() {
		String columns = "";		
		
		List<MaterializedGridColumn> systemColumns = new ArrayList<>(0);
		List<MaterializedGridColumn> userColums = new ArrayList<>(0);
		for(MaterializedGridColumn column : this.grid.getColumns()){
			if(column.getCategory() == null || column.getCategory() == GrilleColumnCategory.USER || column.getCategory() == GrilleColumnCategory.SYSTEM) {
				userColums.add(column);
			}
			else {
				systemColumns.add(column);
			}
		}	
		String coma = "";
		for(MaterializedGridColumn column : systemColumns) {
			String name = column.getDbColumnName();
			columns += coma + name;
			coma = ",";
		}
		
		for(MaterializedGridColumn column : userColums) {
			String name = column.getDbColumnName();
			columns += coma + name;
			coma = ",";
		}		
		return columns;
	}

	@Override
	protected void loadTarget(Long id) throws Exception {
		log.debug("Try to load materialized grid : {}", id);
		if (this.isStopped()) {
			return;
		}
		this.grid = materializedGridService.getById(id);
		if (this.grid != null) {
			boolean toSave = false;
			int position = this.grid.getColumnListChangeHandler().getItems().size();
			if(grid.getColumnByCategory(GrilleColumnCategory.LOAD_NBR) == null){
				toSave = true;
				grid.getColumnListChangeHandler().addNew(new MaterializedGridColumn(GrilleColumnCategory.LOAD_NBR, DimensionType.ATTRIBUTE, "Load nbr", position++));
			}
			if(grid.getColumnByCategory(GrilleColumnCategory.LOAD_DATE) == null){
				toSave = true;
				grid.getColumnListChangeHandler().addNew(new MaterializedGridColumn(GrilleColumnCategory.LOAD_DATE, DimensionType.PERIOD, "Load date", position++));
			}
			if(grid.getColumnByCategory(GrilleColumnCategory.LOAD_MODE) == null){
				toSave = true;
				grid.getColumnListChangeHandler().addNew(new MaterializedGridColumn(GrilleColumnCategory.LOAD_MODE, DimensionType.ATTRIBUTE, "Load mode", position++));
			}
			if(grid.getColumnByCategory(GrilleColumnCategory.LOAD_USER) == null){
				toSave = true;
				grid.getColumnListChangeHandler().addNew(new MaterializedGridColumn(GrilleColumnCategory.LOAD_USER, DimensionType.ATTRIBUTE, "Creator", position++));
			}
			if(grid.getColumnByCategory(GrilleColumnCategory.LOAD_SOURCE_NAME) == null){
				toSave = true;
				grid.getColumnListChangeHandler().addNew(new MaterializedGridColumn(GrilleColumnCategory.LOAD_SOURCE_NAME, DimensionType.ATTRIBUTE, "Load source", position++));
			}
			if(grid.getColumnByCategory(GrilleColumnCategory.OPERATION_CODE) == null){
				toSave = true;
				grid.getColumnListChangeHandler().addNew(new MaterializedGridColumn(GrilleColumnCategory.OPERATION_CODE, DimensionType.ATTRIBUTE, "Operation code", position++));
			}
			if(toSave) {
				materializedGridService.save(grid, Locale.ENGLISH);
				materializedGridService.publish(grid, Locale.ENGLISH);
			}
			this.grid = materializedGridService.getById(id);
			this.grid.setColumns(this.grid.getColumnListChangeHandler().getItems());
			this.grid.getColumns().sort(Comparator.comparing(MaterializedGridColumn::getPosition));
			log.debug("Materialized grid found : {}", this.grid.getName());
		} else {
			throw new BcephalException(
					"Unable to run files loader : " + getData().getLoader().getName() + ". Unkown materialized grid : " + id);
		}
	}

	@Override
	protected void ValidateDatas() throws Exception {
		
	}
	
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	protected void saveCsvFiles() throws Exception {
		if (this.isStopped()) {
			return;
		}
		getUniverseCsvGenerator().end();
		String columns = buildCsvFileColumns();
		try {
			Session session = entityManager.unwrap(Session.class);
			session.doWork(new Work() {
				@Override
				public void execute(Connection connection) throws SQLException {
					CopyManager cp = connection.unwrap(PGConnection.class).getCopyAPI();
					StringReader reader = null;
					try {
						reader =  loadCsvFromFile(getUniverseCsvGenerator().getPath());
						cp.copyIn(
								"COPY " + UniverseParameters.SCHEMA_NAME.toLowerCase()
										+ grid.getMaterializationTableName().toLowerCase() + "(" + columns + ")"
										+ " FROM STDIN csv DELIMITER ';' ENCODING 'UTF-8'",
										reader);						
					} 
					catch (IOException e) {
						e.printStackTrace();
						log.error(e.getMessage());
						connection.rollback();
						throw new SQLException(e);
					}
					finally{
						if(reader != null) {
							reader.close();
						}
					}
				}
			});
		} catch (jakarta.persistence.PersistenceException e) {
			throw new BcephalException(e.getCause().getMessage());
		} catch (Exception e) {
			throw e;
		} finally {
			try {
				getUniverseCsvGenerator().dispose();
			} catch (IOException e) {
				log.trace("Unable to delete file: {}", getUniverseCsvGenerator().getPath(), e);
			}
		}
	}

}
