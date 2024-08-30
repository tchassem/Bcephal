/**
 * 
 */
package com.moriset.bcephal.loader.service;

import java.math.BigDecimal;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import org.apache.commons.csv.CSVRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.moriset.bcephal.domain.universe.UniverseExternalSourceType;
import com.moriset.bcephal.domain.universe.UniverseParameters;
import com.moriset.bcephal.domain.universe.UniverseSourceType;
import com.moriset.bcephal.grid.domain.Grille;
import com.moriset.bcephal.grid.domain.GrilleColumn;
import com.moriset.bcephal.grid.service.GrilleService;
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
@Service
@Data
@Slf4j
@EqualsAndHashCode(callSuper = false)
public class FileLoaderRunnerForGrid extends FileLoaderRunner {

	protected Grille grid;

	@Autowired
	protected GrilleService grilleService;

	@Override
	protected List<Object> buildLineToWrite(String line, String fileName, String separator, long lineNbr,
			FileLoaderLogItem logItem) throws Exception {
		if (line.endsWith(separator)) {
			line += " ";
		}
		String[] input = line.split(separator);
		List<Object> output = new ArrayList<>(0);
		output.add(username);
		output.add(UniverseSourceType.INPUT_GRID);
		output.add(this.grid.getId());
		output.add(this.grid.getName());
		output.add(UniverseExternalSourceType.FILE_LOADER);
		output.add(fileName);
		output.add(getData().getLoader().getId());
		output.add(true);

		int size = input.length;

		for (GrilleColumn column : this.grid.getColumns()) {
			if (this.isStopped()) {
				break;
			}
			String cell = size > column.getPosition() ? input[column.getPosition()] : "";
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
							throw new BcephalException("Line: " + lineNbr + "\nColumn: " + (column.getPosition() + 1)
									+ "\n" + e.getMessage());
						}
						throw new BcephalException("Line: " + lineNbr + "\nColumn: " + (column.getPosition() + 1)
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
		}

		if (StringUtils.hasText(this.loaderFileColumn)) {
			output.add(fileName);
		}
		if (StringUtils.hasText(this.loaderNbrColumn)) {
			output.add(this.loadNbr != null ? this.loadNbr : "");
		}
		if (StringUtils.hasText(this.operationCodeColumn)) {
			output.add(this.operationCode != null ? this.operationCode : "");
		}

		return output;
	}
	
	
	
	@Override
	protected List<Object> buildLineToWrite(CSVRecord line, String fileName, String separator, long lineNbr,
			FileLoaderLogItem logItem) throws Exception {
		
		List<Object> output = new ArrayList<>(0);
		output.add(username);
		output.add(UniverseSourceType.INPUT_GRID);
		output.add(this.grid.getId());
		output.add(this.grid.getName());
		output.add(UniverseExternalSourceType.FILE_LOADER);
		output.add(fileName);
		output.add(getData().getLoader().getId());
		output.add(true);

		
		int size = line.size();
		
		for (GrilleColumn column : this.grid.getColumns()) {
			if (this.isStopped()) {
				break;
			}
			String cell = size > column.getPosition() ? line.get(column.getPosition()) : "";
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
						throw new BcephalException("Line: " + lineNbr + "\nColumn: " + (column.getPosition() + 1)
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
		}

		if (StringUtils.hasText(this.loaderFileColumn)) {
			output.add(fileName);
		}
		if (StringUtils.hasText(this.loaderNbrColumn)) {
			output.add(this.loadNbr != null ? this.loadNbr : "");
		}
		if (StringUtils.hasText(this.operationCodeColumn)) {
			output.add(this.operationCode != null ? this.operationCode : "");
		}

		return output;
	}
	
	

	@Override
	protected List<Object> buildExcelLineToWrite(List<Object> line, String fileName, long lineNbr,
			FileLoaderLogItem logItem) throws Exception {
		List<Object> output = new ArrayList<>(0);
		output.add(username);
		output.add(UniverseSourceType.INPUT_GRID);
		output.add(this.grid.getId());
		output.add(this.grid.getName());
		output.add(UniverseExternalSourceType.FILE_LOADER);
		output.add(fileName);
		output.add(getData().getLoader().getId());
		output.add(true);

		int size = line.size();
//		if (size < this.grid.getColumns().size()) {
//			throw new BcephalException("Line: " + lineNbr + "\nWrong column count! Number of columns found: " + size
//					+ ". Number of columns expected: " + this.grid.getColumns().size());
//		}

		for (GrilleColumn column : this.grid.getColumns()) {
			if (this.isStopped()) {
				break;
			}
			Object cell = size > column.getPosition() ? line.get(column.getPosition()) : null;

			if (cell == null) {
				Object value = getDefaultValue(column);
				output.add(value);
			} else {
				if (column.isPeriod()) {
					try {
						if (cell instanceof String) {
							String date = formatDate((String) cell);
							output.add(date);
						} else {
							String date = formatDate((Date) cell);
							output.add(date);
						}
					} catch (Exception e) {
						log.trace("Unable to parse date!", e);
						throw new BcephalException("Cell [Line: {} ; Column: {}] \nUnparseable date : {}", lineNbr,
								(column.getPosition() + 1), cell);
					}
				} else if (column.isMeasure()) {
					try {
						if (cell instanceof String) {
							if(!StringUtils.hasText(cell.toString().trim())) {
								output.add("");
							}
							else {	
								
								try {
									Number value = formatDecimal(cell.toString());
									output.add(value);
								} catch (Exception e) {
									throw new BcephalException("Cell [Line: {} ; Column: {}] \nUnparseable amount : {}",
											lineNbr, (column.getPosition() + 1), cell);
								}
								
//								Number amount = DecimalFormat.getInstance().parse(cell.toString());
//								String pattern = "####.##############";
//								DecimalFormat df = new DecimalFormat(pattern);
//								try {
//									Number value = (Number) df.parse(df.format(amount));
//									output.add(value);
//								} catch (Exception e) {
//									throw new BcephalException("Cell [Line: {} ; Column: {}] \nUnparseable amount : {}",
//											lineNbr, (column.getPosition() + 1), cell);
//								}
							}
						}
						else if (cell instanceof Double) {
							output.add(cell);
						} else {
							try {
								Number value = formatDecimal(cell.toString());
								output.add(value);
							} catch (Exception e) {
								throw new BcephalException("Cell [Line: {} ; Column: {}] \nUnparseable amount : {}",
										lineNbr, (column.getPosition() + 1), cell);
							}
							
//							Number amount = DecimalFormat.getInstance().parse(cell.toString());
//							String pattern = "####.##############";
//							DecimalFormat df = new DecimalFormat(pattern);
//							try {
//								Double value = (Double) df.parse(df.format(amount));
//								output.add(value);
//							} catch (Exception e) {
//								throw new BcephalException("Cell [Line: {} ; Column: {}] \nUnparseable amount : {}",
//										lineNbr, (column.getPosition() + 1), cell);
//							}
						}
					} catch (Exception e) {
						log.trace("Unable to parse amount!", e);
						throw new BcephalException("Cell [Line: {} ; Column: {}] \nUnparseable amount : {}", lineNbr,
								(column.getPosition() + 1), cell);
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
		}

		if (StringUtils.hasText(this.loaderFileColumn)) {
			output.add(fileName);
		}
		if (StringUtils.hasText(this.loaderNbrColumn)) {
			output.add(this.loadNbr != null ? this.loadNbr : "");
		}
		if (StringUtils.hasText(this.operationCodeColumn)) {
			output.add(this.operationCode != null ? this.operationCode : "");
		}

		return output;
	}

	protected Object getDefaultValue(GrilleColumn column) {
		Object value = null;
		if (column.isApplyDefaultValueToFutureLine()) {
			value = column.getDefaultStringValue();
			if (column.isPeriod()) {
				// value = new
				// InputGridFindReplaceQueryBuilder(null).buildPeriodVal(column.getDefaultStringValue());
				// String date = formatDate(column.getDefaultDateValue());
			} else if (column.isMeasure()) {
				value = column.getDefaultDecimalValue();
			}
		}
		return value != null ? value : "";
	}

	@Override
	protected String buildCsvFileColumns() {
		String columns = UniverseParameters.USERNAME + "," + UniverseParameters.SOURCE_TYPE + ","
				+ UniverseParameters.SOURCE_ID + "," + UniverseParameters.SOURCE_NAME + ","
				+ UniverseParameters.EXTERNAL_SOURCE_TYPE + "," + UniverseParameters.EXTERNAL_SOURCE_REF + ","
				+ UniverseParameters.EXTERNAL_SOURCE_ID + "," + UniverseParameters.ISREADY;
		for (GrilleColumn col : this.grid.getColumns()) {
			String name = col.getUniverseTableColumnName();
			columns += "," + name;
		}
		if (StringUtils.hasText(this.loaderFileColumn)) {
			columns += "," + this.loaderFileColumn;
		}
		if (StringUtils.hasText(this.loaderNbrColumn)) {
			columns += "," + this.loaderNbrColumn;
		}
		if (StringUtils.hasText(this.operationCodeColumn)) {
			columns += "," + this.operationCodeColumn;
		}
		return columns;
	}

	@Override
	protected void loadTarget(Long id) throws Exception {
		log.debug("Try to load grid : {}", id);
		if (this.isStopped()) {
			return;
		}
		this.grid = grilleService.getById(id);
		if (this.grid != null) {
			this.grid.setColumns(this.grid.getColumnListChangeHandler().getItems());
			this.grid.getColumns().sort(Comparator.comparing(GrilleColumn::getPosition));
			log.debug("Grid found : {}", this.grid.getName());
		} else {
			throw new BcephalException(
					"Unable to run files loader : " + getData().getLoader().getName() + ". Unkown grid : " + id);
		}
	}

	@Override
	protected void ValidateDatas() throws Exception {
		if (this.isStopped()) {
			return;
		}
		if (StringUtils.hasText(this.loaderFileColumn) || StringUtils.hasText(this.loaderNbrColumn)) {
			for (GrilleColumn column : this.grid.getColumns()) {
				if (column.isAttribute()) {
					if (this.loaderFileColumn != null
							&& this.loaderFileColumn.equals(column.getUniverseTableColumnName())) {
						throw new BcephalException("Unable to run files loader : " + getData().getLoader().getName()
								+ "." + "\nPlease remove column '" + column.getName() + "' from grid '"
								+ this.grid.getName() + "'");
					} else if (this.loaderNbrColumn != null
							&& this.loaderNbrColumn.equals(column.getUniverseTableColumnName())) {
						throw new BcephalException("Unable to run files loader : " + getData().getLoader().getName()
								+ "." + "\nPlease remove column '" + column.getName() + "' from grid '"
								+ this.grid.getName() + "'");
					} else if (this.operationCodeColumn != null
							&& this.operationCodeColumn.equals(column.getUniverseTableColumnName())) {
						throw new BcephalException("Unable to run files loader : " + getData().getLoader().getName()
								+ "." + "\nPlease remove column '" + column.getName() + "' from grid '"
								+ this.grid.getName() + "'");
					}
				}
			}
		}
	}

}
