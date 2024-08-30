package com.moriset.bcephal.sourcing.grid;

import java.sql.Timestamp;

import com.moriset.bcephal.domain.DataSourceType;
import com.moriset.bcephal.domain.Variable;
import com.moriset.bcephal.domain.dimension.DimensionType;
import com.moriset.bcephal.domain.dimension.Spot;
import com.moriset.bcephal.grid.domain.FileLoaderColumnSoft;
import com.moriset.bcephal.grid.domain.Grille;
import com.moriset.bcephal.grid.domain.GrilleColumn;
import com.moriset.bcephal.grid.domain.GrilleDimension;
import com.moriset.bcephal.grid.domain.GrilleType;
import com.moriset.bcephal.grid.domain.Join;
import com.moriset.bcephal.grid.domain.JoinColumn;
import com.moriset.bcephal.grid.domain.JoinColumnCalculateItem;
import com.moriset.bcephal.grid.domain.JoinColumnConcatenateItem;
import com.moriset.bcephal.grid.domain.JoinColumnConditionItem;
import com.moriset.bcephal.grid.domain.JoinColumnConditionItemOperand;
import com.moriset.bcephal.grid.domain.JoinColumnField;
import com.moriset.bcephal.grid.domain.JoinColumnProperties;
import com.moriset.bcephal.grid.domain.JoinCondition;
import com.moriset.bcephal.grid.domain.JoinConditionItem;
import com.moriset.bcephal.grid.domain.JoinGrid;
import com.moriset.bcephal.grid.domain.JoinGridType;
import com.moriset.bcephal.grid.domain.JoinKey;
import com.moriset.bcephal.grid.domain.JoinLog;
import com.moriset.bcephal.grid.domain.JoinUnionKey;
import com.moriset.bcephal.grid.domain.JoinUnionKeyItem;
import com.moriset.bcephal.grid.domain.MaterializedGrid;
import com.moriset.bcephal.grid.domain.MaterializedGridColumn;
import com.moriset.bcephal.grid.domain.SmartGrille;
import com.moriset.bcephal.grid.domain.SmartJoin;
import com.moriset.bcephal.grid.domain.SmartMaterializedGrid;
import com.moriset.bcephal.grid.domain.form.Form;
import com.moriset.bcephal.grid.domain.form.FormModel;
import com.moriset.bcephal.grid.domain.form.FormModelButton;
import com.moriset.bcephal.grid.domain.form.FormModelButtonAction;
import com.moriset.bcephal.grid.domain.form.FormModelField;
import com.moriset.bcephal.grid.domain.form.FormModelFieldCalculateItem;
import com.moriset.bcephal.grid.domain.form.FormModelFieldCategory;
import com.moriset.bcephal.grid.domain.form.FormModelFieldConcatenateItem;
import com.moriset.bcephal.grid.domain.form.FormModelFieldReference;
import com.moriset.bcephal.grid.domain.form.FormModelFieldReferenceCondition;
import com.moriset.bcephal.grid.domain.form.FormModelFieldType;
import com.moriset.bcephal.grid.domain.form.FormModelFieldValidationItem;
import com.moriset.bcephal.grid.domain.form.FormModelMenu;
import com.moriset.bcephal.grid.domain.form.FormModelSpot;
import com.moriset.bcephal.grid.domain.form.FormModelSpotItem;
import com.moriset.bcephal.loader.domain.FileLoaderColumn;

public class CreateFactory {
	
	public static Variable buildVariable() {
		Variable variable = new Variable();
		variable.setCreationDate(new Timestamp(System.currentTimeMillis()));
		variable.setName(String.format("Variable%s", System.currentTimeMillis()));
		variable.setDescription("Description ..");
		return variable;
	}
	
	public static Spot buildSpot() {
		Spot spot = new Spot();
		spot.setMeasureId(23L);
		spot.setFilter(null);
		spot.setCreationDate(new Timestamp(System.currentTimeMillis()));
		spot.setName(String.format("Spot%s", System.currentTimeMillis()));
		spot.setDescription("Description ..");
		return spot;
	}
	
	public static MaterializedGrid buildMaterialize() {
		MaterializedGrid materialize_grid = new MaterializedGrid();
		materialize_grid.setName("Materialize grid 0");
		materialize_grid.setDescription("Materialize Description ..");
		materialize_grid.setUserFilter(null);
		materialize_grid.setAdminFilter(null);
		materialize_grid.setCreationDate(new Timestamp(System.currentTimeMillis()));
		return materialize_grid;
	}
	
	public static Grille buildGrille() {
		Grille grille = new Grille();
		grille.setUserFilter(null);
		grille.setAdminFilter(null);
		grille.setType(GrilleType.INPUT);
		grille.setCreationDate(new Timestamp(System.currentTimeMillis()));
		grille.setName(String.format("Grille%s", System.currentTimeMillis()));
		grille.setDescription("Grille Description ..");
		return grille;
	}
	
	public static GrilleColumn buildGrilleColumn() {
		GrilleColumn column = new GrilleColumn();
		column.setName(String.format("GrilleColumn%s", System.currentTimeMillis()));
		return column;
	}
	
	
	public static MaterializedGridColumn buildMaterializedGridColumn() {
		MaterializedGridColumn column = new MaterializedGridColumn();
		column.setName(String.format("GrilleColumn%s", System.currentTimeMillis()));
		return column;
	}
	
	public static FormModelFieldReferenceCondition buildFormModelFieldReferenceCondition() {
		FormModelFieldReferenceCondition condition = new FormModelFieldReferenceCondition();
		condition.setStringValue(String.format("Condition%s", System.currentTimeMillis()));
		return condition;
	}
	
	public static FormModelFieldConcatenateItem buildFormModelFieldConcatenateItem() {
		FormModelFieldConcatenateItem concatenateItem = new FormModelFieldConcatenateItem();
		concatenateItem.setStringValue(String.format("Condition%s", System.currentTimeMillis()));
		return concatenateItem;
	}
	
	public static FormModelFieldCalculateItem buildFormModelFieldCalculateItem() {
		FormModelFieldCalculateItem calculateItem = new FormModelFieldCalculateItem();
		calculateItem.setSign("+");
		return calculateItem;
	}
	
	public static FormModelButton buildFormModelButton() {
		FormModelButton buttom = new FormModelButton();
		buttom.setName(String.format("FormModelButton%s", System.currentTimeMillis()));
		buttom.setActive(false);
		return buttom;
	}
	
	public static FormModelButtonAction buildFormModelButtonAction() {
		FormModelButtonAction action = new FormModelButtonAction();
		action.setStringValue(String.format("FormModelButtonAction%s", System.currentTimeMillis()));
		return action;
	}

	public static GrilleDimension buildGrilleDimension() {
		GrilleDimension dimension = new GrilleDimension();
		dimension.setName(String.format("Dimension%s", System.currentTimeMillis()));
		return dimension;
	}

	public static SmartGrille buildSmartGrille() {
		SmartGrille grille = new SmartGrille();
		grille.setType(GrilleType.INPUT);
		grille.setName(String.format("SmartGrille%s", System.currentTimeMillis()));
		return grille;
	}

	public static SmartJoin buildSmartJoin() {
		SmartJoin join = new SmartJoin();
		join.setName(String.format("SmartJoin%s", System.currentTimeMillis()));
		return join;
	}
	
	public static SmartMaterializedGrid buildSmartMaterializedGrid() {
		SmartMaterializedGrid smart = new SmartMaterializedGrid();
		smart.setName(String.format("SmartMaterializedGrid%s", System.currentTimeMillis()));
		return smart;
	}
	
	public static JoinGrid buildJoinGrid() {
		JoinGrid join = new JoinGrid();
		join.setName(String.format("JoinGrid%s", System.currentTimeMillis()));
		return join;
	}
	
	public static JoinKey buildJoinKey() {
		JoinKey key = new JoinKey();
		key.setGridType1(JoinGridType.JOIN);
		return key;
	}
	
	public static JoinLog buildJoinLog() {
		JoinLog log = new JoinLog();
		log.setName(String.format("JoinLog%s", System.currentTimeMillis()));
		log.setCreationDate(new Timestamp(System.currentTimeMillis()));
		return log;
	}
	
	public static Join buildJoin() {
		Join join = new Join();
		join.setName(String.format("Join%s", System.currentTimeMillis()));
		join.setCreationDate(new Timestamp(System.currentTimeMillis()));
		join.setActive(true);
		join.setFilter(null);
		join.setAdminFilter(null);
		join.setCronExpression("1 * * * * ");
		return join;
	}
	
	public static JoinUnionKeyItem buildJoinUnionKeyItem() {
		JoinUnionKeyItem joinUnionKey = new JoinUnionKeyItem();
		joinUnionKey.setGridType(JoinGridType.GRID);
		return joinUnionKey;
	}
	
	public static JoinUnionKey buildJoinUnionKey() {
		JoinUnionKey key = new JoinUnionKey();
		key.setGridType(JoinGridType.JOIN);
		return key;
	}
	
	public static JoinColumn buildJoinColumn() {
		JoinColumn column = new JoinColumn();
		column.setName(String.format("JoinColumn%s", System.currentTimeMillis()));
		return column;
	}
	
	public static JoinColumnProperties buildJoinColumnProperties() {
		JoinColumnProperties properties = new JoinColumnProperties();
		properties.setColumnId(254L);
		return properties;
	}
	
	public static JoinCondition buildJoinCondition() {
		JoinCondition condition = new JoinCondition();
		condition.setVerb("AND");
		condition.setComparator("=");
		return condition;
	}
	
	public static JoinConditionItem buildJoinConditionItem() {
		JoinConditionItem item = new JoinConditionItem();
		item.setStringValue("demo value ...");
		return item;
	}
	
	public static JoinColumnField buildJoinColumnField() {
		JoinColumnField field = new JoinColumnField();
		field.setStringValue("demo value ...");
		return field;
	}
	
	public static JoinColumnConditionItem buildJoinColumnConditionItem() {
		JoinColumnConditionItem item = new JoinColumnConditionItem();
		item.setPosition(1);
		return item;
	}
	
	public static JoinColumnConditionItemOperand buildJoinColumnConditionItemOperand() {
		JoinColumnConditionItemOperand item = new JoinColumnConditionItemOperand();
		item.setPosition(1);
		return item;
	}
	
	public static JoinColumnConcatenateItem buildJoinColumnConcatenateItem() {
		JoinColumnConcatenateItem item = new JoinColumnConcatenateItem();
		item.setPosition(1);
		return item;
	}
	
	public static FileLoaderColumn buildFileLoaderColumn() {
		FileLoaderColumn fileLoaderColumn = new FileLoaderColumn();
		fileLoaderColumn.setFileColumn("Name");
		fileLoaderColumn.setPosition(2);
		fileLoaderColumn.setDimensionId(14L);
		fileLoaderColumn.setType(DimensionType.FREE);
		return fileLoaderColumn;
	}
	
	public static JoinColumnCalculateItem buildJoinColumnCalculateItem() {
		JoinColumnCalculateItem item = new JoinColumnCalculateItem();
		item.setPosition(1);
		return item;
	}
	
	public static FileLoaderColumnSoft buildFileLoaderColumnSoft() {
		FileLoaderColumnSoft columnSoft = new FileLoaderColumnSoft();
		columnSoft.setLoader(4L);
		columnSoft.setGrilleColumn(2L);
		return columnSoft;
	}
	
	public static FormModelSpot buildFormModelSpot() {
		FormModelSpot formModelSpot = new FormModelSpot();
		formModelSpot.setMeasureId(2L);
		formModelSpot.setFormula("SUM");
		formModelSpot.setDataSourceType(DataSourceType.UNIVERSE);
		return formModelSpot;
	}
	
	public static FormModelSpotItem buildFormModelSpotItem() {
		FormModelSpotItem formModelSpotItem = new FormModelSpotItem();
		formModelSpotItem.setStringValue("String value");
		return formModelSpotItem;
	} 
	
	public static FormModel buildFormModel() {
		FormModel formModel = new FormModel();
		formModel.setName(String.format("FormModel%s", System.currentTimeMillis()));
		formModel.setDescription(String.format("Description%s", System.currentTimeMillis()));
		formModel.setCreationDate(new Timestamp(System.currentTimeMillis()));
		return formModel;
	}
	
	public static FormModelMenu buildFormModelMenu() {
		FormModelMenu menu = new FormModelMenu();
		menu.setName(String.format("FormModelMenu%s", System.currentTimeMillis()));
		menu.setListMenuName(String.format("ListMenuName%s", System.currentTimeMillis()));
		return menu;
	}
	
	public static FormModelFieldValidationItem buildFormModelFieldValidationItem() {
		FormModelFieldValidationItem validationItem = new FormModelFieldValidationItem();
		validationItem.setStringValue(String.format("FormModelFieldValidationItem%s", System.currentTimeMillis()));
		validationItem.setErrorMessage(String.format("ErrorMessage%s", System.currentTimeMillis()));
		validationItem.setActive(false);
		return validationItem;
	}
	
	public static FormModelField buildFormModelField() {
		FormModelField formModelField = new FormModelField();
		formModelField.setName(String.format("FormModelField%s", System.currentTimeMillis()));
		formModelField.setDescription(String.format("Description%s", System.currentTimeMillis()));
		formModelField.setCategory(FormModelFieldCategory.MAIN);
		formModelField.setType(FormModelFieldType.SPOT);
		return formModelField;
	}
	
//	public static FormModelField buildFormModelFieldDetail() {
//		FormModelField formModelField = new FormModelField();
//		formModelField.setName(String.format("FormModelField%s", System.currentTimeMillis()));
//		formModelField.setDescription(String.format("Description%s", System.currentTimeMillis()));
//		formModelField.setCategory(FormModelFieldCategory.DETAILS);
//		formModelField.setType(FormModelFieldType.SPOT);
//		return formModelField;
//	}
	
	public static FormModelFieldReference buildFormModelFieldReference() {
		FormModelFieldReference fieldReference = new FormModelFieldReference();
		fieldReference.setFormula("SUM");
		fieldReference.setSourceId(2L);
		return fieldReference;
	}
	
	public static Form buildForm() {
		Form form = new Form();
		form.setName(String.format("Form%s", System.currentTimeMillis()));
		form.setDescription(String.format("Description%s", System.currentTimeMillis()));
		form.setCreationDate(new Timestamp(System.currentTimeMillis()));
		return form;
	}
}
