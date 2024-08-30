package com.moriset.bcephal.sourcing.grid;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.assertj.core.api.Assertions;

import com.moriset.bcephal.domain.RunModes;
import com.moriset.bcephal.domain.dimension.Dimension;
import com.moriset.bcephal.domain.dimension.DimensionType;
import com.moriset.bcephal.grid.domain.Grille;
import com.moriset.bcephal.grid.service.GrilleService;
import com.moriset.bcephal.loader.domain.FileLoader;
import com.moriset.bcephal.loader.domain.FileLoaderColumn;
import com.moriset.bcephal.loader.domain.FileLoaderMethod;
import com.moriset.bcephal.loader.domain.FileLoaderRepository;
import com.moriset.bcephal.loader.domain.FileLoaderSource;
import com.moriset.bcephal.loader.service.FileLoaderRunData;
import com.moriset.bcephal.service.InitiationService;

public class FileLoaderFactory {
	
	public static List<FileLoader> buildFileLoaders(GrilleService grilleService, InitiationService initiationService) throws Exception {
		List<FileLoader> items = new ArrayList<>();
		items.add(buildAcquiring___0_SFPA0WK000001(grilleService));
		items.add(buildAcquiring___0_SFPA0WK0000011(grilleService));
		items.add(buildBNK001EPBfundingaccount(grilleService));
		items.add(buildINP002IssuingSACGA(grilleService));
		items.add(buildINP002IssuingSAMGA(grilleService));
		items.add(buildINP003IssuingMACMA(grilleService, initiationService));
		items.add(buildINP003IssuingMAMMA(grilleService, initiationService));
		items.add(buildINP005IssuingREC(grilleService));
		items.add(buildINP005RECMaestro(grilleService));
		items.add(buildINP102AcquiringSA(grilleService));
		items.add(buildINP103AcquiringMA(grilleService));
		items.add(buildINP105AcquiringREC(grilleService, initiationService));
		return items;
	}	

	public static FileLoader buildAcquiring___0_SFPA0WK000001(GrilleService grilleService) {
		FileLoader fileLoader = new FileLoader();
		fileLoader.setName("Acquiring___0_SFPA0WK000001");
		fileLoader.setUploadMethod(FileLoaderMethod.DIRECT_TO_GRID);
		Grille grid = grilleService.getByName("BNK001 EPB funding account");
		Assertions.assertThat(grid).isNotNull();
		fileLoader.setTargetId(grid.getId());
		fileLoader.setTargetName(grid.getName());
		fileLoader.setSource(FileLoaderSource.SERVER);
		fileLoader.setAllowBackup(false);
		fileLoader.setFileSeparator(";");
		fileLoader.setDateFormat("dd/MM/yyyy");
		fileLoader.setFileExtension(".csv");
		return fileLoader;
	}
	
	public static FileLoader buildAcquiring___0_SFPA0WK0000011(GrilleService grilleService) {
		FileLoader fileLoader = new FileLoader();
		fileLoader.setName("Acquiring___0_SFPA0WK0000011");
		fileLoader.setUploadMethod(FileLoaderMethod.DIRECT_TO_GRID);
		Grille grid = grilleService.getByName("BNK001 EPB funding account");
		Assertions.assertThat(grid).isNotNull();
		fileLoader.setTargetId(grid.getId());
		fileLoader.setTargetName(grid.getName());
		fileLoader.setSource(FileLoaderSource.SERVER);
		fileLoader.setAllowBackup(false);
		fileLoader.setFileSeparator(";");
		fileLoader.setDateFormat("dd/MM/yyyy");
		fileLoader.setFileExtension(".csv");
		return fileLoader;
	}
	
	public static FileLoader buildBNK001EPBfundingaccount(GrilleService grilleService) {
		FileLoader fileLoader = new FileLoader();
		fileLoader.setName("BNK001 EPB funding account");
		fileLoader.setUploadMethod(FileLoaderMethod.DIRECT_TO_GRID);
		Grille grid = grilleService.getByName("BNK001 EPB funding account");
		Assertions.assertThat(grid).isNotNull();
		fileLoader.setTargetId(grid.getId());
		fileLoader.setTargetName(grid.getName());
		fileLoader.setSource(FileLoaderSource.SERVER);
		fileLoader.setAllowBackup(false);
		fileLoader.setFileSeparator(";");
		fileLoader.setDateFormat("dd/MM/yyyy");
		fileLoader.setFileExtension(".csv");
		return fileLoader;
	}
	
	public static FileLoader buildINP002IssuingSACGA(GrilleService grilleService) {
		FileLoader fileLoader = new FileLoader();
		fileLoader.setName("INP002 Issuing SA - CGA");
		fileLoader.setUploadMethod(FileLoaderMethod.DIRECT_TO_GRID);
		Grille grid = grilleService.getByName("INP002 Issuing SA");
		Assertions.assertThat(grid).isNotNull();
		fileLoader.setTargetId(grid.getId());
		fileLoader.setTargetName(grid.getName());
		fileLoader.setSource(FileLoaderSource.SERVER);
		fileLoader.setAllowBackup(false);
		fileLoader.setFileSeparator(";");
		fileLoader.setDateFormat("dd/MM/yyyy");
		fileLoader.setFileExtension(".csv");
		return fileLoader;
	}
	
	public static FileLoader buildINP002IssuingSAMGA(GrilleService grilleService) {
		FileLoader fileLoader = new FileLoader();
		fileLoader.setName("INP002 Issuing SA - MGA");
		fileLoader.setUploadMethod(FileLoaderMethod.DIRECT_TO_GRID);
		Grille grid = grilleService.getByName("INP002 Issuing SA");
		Assertions.assertThat(grid).isNotNull();
		fileLoader.setTargetId(grid.getId());
		fileLoader.setTargetName(grid.getName());
		fileLoader.setSource(FileLoaderSource.SERVER);
		fileLoader.setAllowBackup(false);
		fileLoader.setFileSeparator(";");
		fileLoader.setDateFormat("dd/MM/yyyy");
		fileLoader.setFileExtension(".csv");
		return fileLoader;
	}
	
	public static FileLoader buildINP003IssuingMACMA(GrilleService grilleService, InitiationService initiationService) {
		FileLoader fileLoader = new FileLoader();
		fileLoader.setName("INP003 Issuing MA - CMA");
		fileLoader.setUploadMethod(FileLoaderMethod.DIRECT_TO_GRID);
		Grille grid = grilleService.getByName("INP003 Issuing MA");
		Assertions.assertThat(grid).isNotNull();
		fileLoader.setTargetId(grid.getId());
		fileLoader.setTargetName(grid.getName());
		fileLoader.setSource(FileLoaderSource.SERVER);
		fileLoader.getColumnListChangeHandler().addNew(buildFileLoaderColumn("Unique Report ID", 0, DimensionType.ATTRIBUTE, initiationService));
		fileLoader.getColumnListChangeHandler().addNew(buildFileLoaderColumn("Advisement ID", 1, DimensionType.ATTRIBUTE, initiationService));
		fileLoader.getColumnListChangeHandler().addNew(buildFileLoaderColumn("Sponsor Unique Report ID", 2, DimensionType.ATTRIBUTE, initiationService));
		fileLoader.getColumnListChangeHandler().addNew(buildFileLoaderColumn("Operation ID", 3, DimensionType.ATTRIBUTE, initiationService));
		fileLoader.getColumnListChangeHandler().addNew(buildFileLoaderColumn("Account ID", 4, DimensionType.ATTRIBUTE, initiationService));
		fileLoader.getColumnListChangeHandler().addNew(buildFileLoaderColumn("Scheme Value Date", 5, DimensionType.PERIOD, initiationService));
		fileLoader.getColumnListChangeHandler().addNew(buildFileLoaderColumn("Member Bank ID", 6, DimensionType.ATTRIBUTE, initiationService));
		fileLoader.getColumnListChangeHandler().addNew(buildFileLoaderColumn("PML ID", 7, DimensionType.ATTRIBUTE, initiationService));
		fileLoader.getColumnListChangeHandler().addNew(buildFileLoaderColumn("Scheme ID", 8, DimensionType.ATTRIBUTE, initiationService));
		fileLoader.getColumnListChangeHandler().addNew(buildFileLoaderColumn("Scheme Platform ID", 9, DimensionType.ATTRIBUTE, initiationService));
		fileLoader.getColumnListChangeHandler().addNew(buildFileLoaderColumn("Currency ID", 10, DimensionType.ATTRIBUTE, initiationService));
		fileLoader.getColumnListChangeHandler().addNew(buildFileLoaderColumn("D-C", 11, DimensionType.ATTRIBUTE, initiationService));
		fileLoader.getColumnListChangeHandler().addNew(buildFileLoaderColumn("Posting amount", 12, DimensionType.MEASURE, initiationService));
		fileLoader.getColumnListChangeHandler().addNew(buildFileLoaderColumn("Message", 13, DimensionType.ATTRIBUTE, initiationService));
		fileLoader.getColumnListChangeHandler().addNew(buildFileLoaderColumn("Worldline Report Date", 14, DimensionType.PERIOD, initiationService));
		fileLoader.getColumnListChangeHandler().addNew(buildFileLoaderColumn("Wordline report time", 15, DimensionType.ATTRIBUTE, initiationService));
		fileLoader.getColumnListChangeHandler().addNew(buildFileLoaderColumn("Scheme report date", 16, DimensionType.PERIOD, initiationService));
		fileLoader.getColumnListChangeHandler().addNew(buildFileLoaderColumn("Scheme Cycle", 17, DimensionType.ATTRIBUTE, initiationService));
		fileLoader.getColumnListChangeHandler().addNew(buildFileLoaderColumn("Scheme PML ID", 18, DimensionType.ATTRIBUTE, initiationService));
		fileLoader.getColumnListChangeHandler().addNew(buildFileLoaderColumn("Brocker ID", 19, DimensionType.ATTRIBUTE, initiationService));
		fileLoader.getColumnListChangeHandler().addNew(buildFileLoaderColumn("Processor Name", 20, DimensionType.ATTRIBUTE, initiationService));
		fileLoader.getColumnListChangeHandler().addNew(buildFileLoaderColumn("Native value date", 21, DimensionType.PERIOD, initiationService));
		fileLoader.getColumnListChangeHandler().addNew(buildFileLoaderColumn("PML Type", 22, DimensionType.ATTRIBUTE, initiationService));
		fileLoader.getColumnListChangeHandler().addNew(buildFileLoaderColumn("Product Type", 23, DimensionType.ATTRIBUTE, initiationService));
		fileLoader.getColumnListChangeHandler().addNew(buildFileLoaderColumn("Check", 24, DimensionType.MEASURE, initiationService));
		fileLoader.setAllowBackup(false);
		fileLoader.setFileSeparator(";");
		fileLoader.setDateFormat("dd/MM/yyyy");
		fileLoader.setFileExtension(".csv");
		return fileLoader;
	}

	public static FileLoader buildINP003IssuingMAMMA(GrilleService grilleService, InitiationService initiationService) {
		FileLoader fileLoader = new FileLoader();
		fileLoader.setName("INP003 Issuing MA - MMA");
		fileLoader.setUploadMethod(FileLoaderMethod.DIRECT_TO_GRID);
		Grille grid = grilleService.getByName("INP003 Issuing MA");
		Assertions.assertThat(grid).isNotNull();
		fileLoader.setTargetId(grid.getId());
		fileLoader.setTargetName(grid.getName());
		fileLoader.setSource(FileLoaderSource.SERVER);
		fileLoader.getColumnListChangeHandler().addNew(buildFileLoaderColumn("Unique report ID", 0, DimensionType.ATTRIBUTE, initiationService));
		fileLoader.getColumnListChangeHandler().addNew(buildFileLoaderColumn("Advisement ID", 1, DimensionType.ATTRIBUTE, initiationService));
		fileLoader.getColumnListChangeHandler().addNew(buildFileLoaderColumn("Sponsor Unique Report ID", 2, DimensionType.ATTRIBUTE, initiationService));
		fileLoader.getColumnListChangeHandler().addNew(buildFileLoaderColumn("Operation ID", 3, DimensionType.ATTRIBUTE, initiationService));
		fileLoader.getColumnListChangeHandler().addNew(buildFileLoaderColumn("Account ID", 4, DimensionType.ATTRIBUTE, initiationService));
		fileLoader.getColumnListChangeHandler().addNew(buildFileLoaderColumn("Scheme Value date", 5, DimensionType.PERIOD, initiationService));
		fileLoader.getColumnListChangeHandler().addNew(buildFileLoaderColumn("Member Bank ID", 6, DimensionType.ATTRIBUTE, initiationService));
		fileLoader.getColumnListChangeHandler().addNew(buildFileLoaderColumn("PML ID", 7, DimensionType.ATTRIBUTE, initiationService));
		fileLoader.getColumnListChangeHandler().addNew(buildFileLoaderColumn("Scheme ID", 8, DimensionType.ATTRIBUTE, initiationService));
		fileLoader.getColumnListChangeHandler().addNew(buildFileLoaderColumn("Scheme Platform ID", 9, DimensionType.ATTRIBUTE, initiationService));
		fileLoader.getColumnListChangeHandler().addNew(buildFileLoaderColumn("Currency ID", 10, DimensionType.ATTRIBUTE, initiationService));
		fileLoader.getColumnListChangeHandler().addNew(buildFileLoaderColumn("D-C", 11, DimensionType.ATTRIBUTE, initiationService));
		fileLoader.getColumnListChangeHandler().addNew(buildFileLoaderColumn("Posting amount", 12, DimensionType.MEASURE, initiationService));
		fileLoader.getColumnListChangeHandler().addNew(buildFileLoaderColumn("Message", 13, DimensionType.ATTRIBUTE, initiationService));
		fileLoader.getColumnListChangeHandler().addNew(buildFileLoaderColumn("Worldline Report Date", 14, DimensionType.PERIOD, initiationService));
		fileLoader.getColumnListChangeHandler().addNew(buildFileLoaderColumn("Wordline report time", 15, DimensionType.ATTRIBUTE, initiationService));
		fileLoader.getColumnListChangeHandler().addNew(buildFileLoaderColumn("Scheme report date", 16, DimensionType.PERIOD, initiationService));
		fileLoader.getColumnListChangeHandler().addNew(buildFileLoaderColumn("Scheme cycle", 17, DimensionType.ATTRIBUTE, initiationService));
		fileLoader.getColumnListChangeHandler().addNew(buildFileLoaderColumn("Scheme PML ID", 18, DimensionType.ATTRIBUTE, initiationService));
		fileLoader.getColumnListChangeHandler().addNew(buildFileLoaderColumn("Brocker ID", 19, DimensionType.ATTRIBUTE, initiationService));
		fileLoader.getColumnListChangeHandler().addNew(buildFileLoaderColumn("Processor Name", 20, DimensionType.ATTRIBUTE, initiationService));
		fileLoader.getColumnListChangeHandler().addNew(buildFileLoaderColumn("Native value date", 21, DimensionType.PERIOD, initiationService));
		fileLoader.getColumnListChangeHandler().addNew(buildFileLoaderColumn("PML Type", 22, DimensionType.ATTRIBUTE, initiationService));
		fileLoader.getColumnListChangeHandler().addNew(buildFileLoaderColumn("Product Type", 23, DimensionType.ATTRIBUTE, initiationService));
		fileLoader.getColumnListChangeHandler().addNew(buildFileLoaderColumn("Check", 24, DimensionType.MEASURE, initiationService));
		fileLoader.setAllowBackup(false);
		fileLoader.setFileSeparator(";");
		fileLoader.setDateFormat("dd/MM/yyyy");
		fileLoader.setFileExtension(".csv");
		return fileLoader;
	}
	
	public static FileLoader buildINP005IssuingREC(GrilleService grilleService) {
		FileLoader fileLoader = new FileLoader();
		fileLoader.setName("INP005 Issuing REC");
		fileLoader.setUploadMethod(FileLoaderMethod.DIRECT_TO_GRID);
		Grille grid = grilleService.getByName("INP005 Issuing REC");
		Assertions.assertThat(grid).isNotNull();
		fileLoader.setTargetId(grid.getId());
		fileLoader.setTargetName(grid.getName());
		fileLoader.setSource(FileLoaderSource.SERVER);fileLoader.setAllowBackup(false);
		fileLoader.setFileSeparator(";");
		fileLoader.setDateFormat("dd/MM/yyyy");
		fileLoader.setFileExtension(".csv");
		return fileLoader;
	}
	
	public static FileLoader buildINP005RECMaestro(GrilleService grilleService) {
		FileLoader fileLoader = new FileLoader();
		fileLoader.setName("INP005 REC Maestro");
		fileLoader.setUploadMethod(FileLoaderMethod.DIRECT_TO_GRID);
		Grille grid = grilleService.getByName("INP005 REC Maestro");
		Assertions.assertThat(grid).isNotNull();
		fileLoader.setTargetId(grid.getId());
		fileLoader.setTargetName(grid.getName());
		fileLoader.setSource(FileLoaderSource.SERVER);
		fileLoader.setAllowBackup(false);
		fileLoader.setFileSeparator(";");
		fileLoader.setDateFormat("dd/MM/yyyy");
		fileLoader.setFileExtension(".csv");
		return fileLoader;
	}
	
	public static FileLoader buildINP102AcquiringSA(GrilleService grilleService) {
		FileLoader fileLoader = new FileLoader();
		fileLoader.setName("INP102 Acquiring SA");
		fileLoader.setUploadMethod(FileLoaderMethod.DIRECT_TO_GRID);
		Grille grid = grilleService.getByName("INP102 Acquiring SA");
		Assertions.assertThat(grid).isNotNull();
		fileLoader.setTargetId(grid.getId());
		fileLoader.setTargetName(grid.getName());
		fileLoader.setSource(FileLoaderSource.SERVER);
		fileLoader.setAllowBackup(false);
		fileLoader.setFileSeparator(";");
		fileLoader.setDateFormat("dd/MM/yyyy");
		fileLoader.setFileExtension(".csv");
		return fileLoader;
	}
	
	public static FileLoader buildINP103AcquiringMA(GrilleService grilleService) {
		FileLoader fileLoader = new FileLoader();
		fileLoader.setName("INP103 Acquiring MA");
		fileLoader.setUploadMethod(FileLoaderMethod.DIRECT_TO_GRID);
		Grille grid = grilleService.getByName("INP103 Acquiring MA");
		Assertions.assertThat(grid).isNotNull();
		fileLoader.setTargetId(grid.getId());
		fileLoader.setTargetName(grid.getName());
		fileLoader.setSource(FileLoaderSource.SERVER);
		fileLoader.setAllowBackup(false);
		fileLoader.setFileSeparator(";");
		fileLoader.setDateFormat("dd/MM/yyyy");
		fileLoader.setFileExtension(".csv");
		return fileLoader;
	}
	
	public static FileLoader buildINP105AcquiringREC(GrilleService grilleService, InitiationService initiationService) {
		FileLoader fileLoader = new FileLoader();
		fileLoader.setName("INP105 Acquiring REC");
		fileLoader.setUploadMethod(FileLoaderMethod.DIRECT_TO_GRID);
		Grille grid = grilleService.getByName("INP105 Acquiring REC");
		Assertions.assertThat(grid).isNotNull();
		fileLoader.setTargetId(grid.getId());
		fileLoader.getColumnListChangeHandler().addNew(buildFileLoaderColumn("Rec Type ID", 0, DimensionType.ATTRIBUTE, initiationService));
		fileLoader.getColumnListChangeHandler().addNew(buildFileLoaderColumn("Member Bank ID", 1, DimensionType.ATTRIBUTE, initiationService));
		fileLoader.getColumnListChangeHandler().addNew(buildFileLoaderColumn("Scheme ID", 2, DimensionType.ATTRIBUTE, initiationService));
		fileLoader.getColumnListChangeHandler().addNew(buildFileLoaderColumn("Scheme Date", 3, DimensionType.PERIOD, initiationService));
		fileLoader.getColumnListChangeHandler().addNew(buildFileLoaderColumn("Scheme cycle", 4, DimensionType.ATTRIBUTE, initiationService));
		fileLoader.getColumnListChangeHandler().addNew(buildFileLoaderColumn("Scheme Platform ID", 5, DimensionType.ATTRIBUTE, initiationService));
		fileLoader.getColumnListChangeHandler().addNew(buildFileLoaderColumn("Processor Date", 6, DimensionType.PERIOD, initiationService));
		fileLoader.getColumnListChangeHandler().addNew(buildFileLoaderColumn("TPPN Period", 7, DimensionType.PERIOD, initiationService));
		fileLoader.getColumnListChangeHandler().addNew(buildFileLoaderColumn("REC Account ID", 8, DimensionType.ATTRIBUTE, initiationService));
		fileLoader.getColumnListChangeHandler().addNew(buildFileLoaderColumn("Posting amount", 9, DimensionType.MEASURE, initiationService));
		fileLoader.getColumnListChangeHandler().addNew(buildFileLoaderColumn("D-C", 10, DimensionType.ATTRIBUTE, initiationService));
		fileLoader.getColumnListChangeHandler().addNew(buildFileLoaderColumn("Brocker ID", 11, DimensionType.ATTRIBUTE, initiationService));
		fileLoader.getColumnListChangeHandler().addNew(buildFileLoaderColumn("Value date", 12, DimensionType.PERIOD, initiationService));
		fileLoader.getColumnListChangeHandler().addNew(buildFileLoaderColumn("PML Type", 13, DimensionType.ATTRIBUTE, initiationService));
		fileLoader.getColumnListChangeHandler().addNew(buildFileLoaderColumn("REC Type Name", 14, DimensionType.ATTRIBUTE, initiationService));
		fileLoader.getColumnListChangeHandler().addNew(buildFileLoaderColumn("Product Type", 15, DimensionType.ATTRIBUTE, initiationService));
		fileLoader.getColumnListChangeHandler().addNew(buildFileLoaderColumn("Check", 16, DimensionType.MEASURE, initiationService));
		fileLoader.setTargetName(grid.getName());
		fileLoader.setSource(FileLoaderSource.SERVER);
		fileLoader.setAllowBackup(false);
		fileLoader.setFileSeparator(";");
		fileLoader.setDateFormat("dd/MM/yyyy");
		fileLoader.setFileExtension(".csv");
		return fileLoader;
	}
	
	private static FileLoaderColumn buildFileLoaderColumn(String dimensionName, int position, DimensionType type, InitiationService initiationService) {
		FileLoaderColumn column = new FileLoaderColumn();
		column.setPosition(position);
		Dimension dimension = initiationService.getDimension(type, dimensionName, true, null, null);
		Assertions.assertThat(dimension).isNotNull();
		column.setType(type);
		column.setDimensionId((Long) dimension.getId());
		column.setFileColumn(dimensionName);
		return column;
	}
	
	public static FileLoaderRunData buildFileLoaderRunData(FileLoader fileLoader) {
		FileLoaderRunData data = new FileLoaderRunData();
		data.setLoader(fileLoader);
		data.setFiles(List.of(fileLoader.getTargetName()));
		data.setRepositories(
				fileLoader.getRepositoryListChangeHandler().getItems().stream()
				.map(FileLoaderRepository::getRepositoryOnServer)
				.collect(Collectors.toList())
				);
		data.setMode(RunModes.A);
		return data;
	}
	
}
