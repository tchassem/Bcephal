package com.moriset.bcephal.project.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

import com.moriset.bcephal.security.domain.Functionality;

@Component
public class FuntionalityCode {

	private final String functionality = "functionality";
	private final String billing = ".billing";
	private final String event = ".event";
	private final String administration = ".administration";
	private final String run = ".run";
	private final String data = ".data";
	private final String dashboarding = ".dashboarding";
	private final String file = ".file";
	private final String manager = ".manager";
	private final String alarm = ".alarm";
	private final String archive = ".archive";
	private final String group = ".group";
	private final String help = ".help";
	private final String initiation = ".initiation";
	private final String access = ".access";
	private final String attribute = ".attribute";
	private final String save = ".save";
	private final String load = ".load";
	private final String project = ".project";
	private final String backup = ".backup";
	private final String reconciliation = ".reconciliation";
	private final String automatic = ".automatic";
	private final String scheduling = ".scheduling";
	private final String scheduled = ".scheduled";
	private final String log = ".log";
	private final String reporting = ".reporting";
	private final String calculated = ".calculated";
	private final String measure = ".measure";
	private final String calendar = ".calendar";
	private final String dashboard = ".dashboard";
	private final String report = ".report";
	private final String structured = ".structured";
	private final String settings = ".settings";
	private final String sever = ".sever";
	private final String sourcing = ".sourcing";
	private final String input = ".input";
	private final String grid = ".grid";
	private final String table = ".table";
	private final String fileloader = ".fileloader";
	private final String form = ".form";
	private final String multiple = ".multiple";
	private final String files = ".files";
	private final String transformation = ".transformation";
	private final String tree = ".tree";
	private final String rights = ".rights";
	private final String model = ".model";
	private final String period = ".period";
	private final String manual = ".manual";

	@Autowired
	MessageSource messageSource;

	public List<Functionality> getAllfuntionalities(Locale locale) {
		List<Functionality> functionalities = new ArrayList<>(0);
		functionalities.addAll(addInitiation(locale));
		functionalities.addAll(addSourcing(locale));
		functionalities.addAll(addTransformationData(locale));
		functionalities.addAll(addLoad(locale));
		functionalities.addAll(addReporting(locale));
		functionalities.addAll(addDashboarding(locale));
		functionalities.addAll(addReconciliation(locale));
		functionalities.addAll(addBilling(locale));
		functionalities.addAll(addDataManager(locale));
		functionalities.addAll(addAdministration(locale));
		functionalities.addAll(addSettings(locale));
		functionalities.addAll(addProject(locale));
		functionalities.addAll(addFile(locale));
		functionalities.addAll(addHelp(locale));
		functionalities.addAll(addOther(locale));

		return functionalities;
	}

	private List<Functionality> addInitiation(Locale locale) {
		List<Functionality> functionalities = new ArrayList<>(0);
		String parentcode = functionality.concat(initiation);
		functionalities.add(newFunctionality(parentcode, locale));
		functionalities.add(newFunctionality(parentcode.concat(model), true, true, locale));
		functionalities.add(newFunctionality(parentcode.concat(measure), false, true, locale));
		functionalities.add(newFunctionality(parentcode.concat(period), false, true, locale));
		functionalities.add(newFunctionality(parentcode.concat(calendar), false, true, locale));
		functionalities.add(newFunctionality(parentcode.concat(access).concat(rights), locale));
		functionalities.add(newFunctionality(parentcode.concat(attribute).concat(rights), locale));
		return functionalities;
	}

	private List<Functionality> addSourcing(Locale locale) {
		List<Functionality> functionalities = new ArrayList<>(0);
		String parentcode = functionality.concat(sourcing);
		String fileloader_ = parentcode.concat(fileloader);
		functionalities.add(newFunctionality(parentcode, locale));

		functionalities.add(newFunctionality(parentcode.concat(input).concat(grid), true, true, locale));
		functionalities.add(newFunctionality(parentcode.concat(input).concat(table), true, true, locale));

		functionalities.add(newFunctionality(fileloader_, true, true, locale));

		functionalities.add(newFunctionality(parentcode.concat(".accessories"), locale));
		functionalities
				.add(newFunctionality(parentcode.concat(automatic).concat(input).concat(grid), true, true, locale));
		functionalities
				.add(newFunctionality(parentcode.concat(automatic).concat(input).concat(table), true, true, locale));
		functionalities.add(newFunctionality(parentcode.concat(automatic).concat(".target"), true, true, locale));
		functionalities.add(newFunctionality(parentcode.concat(".design"), locale));
		functionalities.add(newFunctionality(parentcode.concat(".dynamic.form"), true, true, locale));

		functionalities.add(newFunctionality(fileloader_.concat(log), locale));
		functionalities.add(newFunctionality(fileloader_.concat(run), locale));
		functionalities.add(newFunctionality(fileloader_.concat(scheduling), locale));
		functionalities
				.add(newFunctionality(fileloader_.concat(scheduling).concat(scheduled).concat(fileloader), locale));
		functionalities.add(newFunctionality(parentcode.concat(form), true, true, locale));
		functionalities.add(newFunctionality(parentcode.concat(form).concat(".creation"), locale));
		functionalities.add(newFunctionality(parentcode.concat(form).concat(".designer"), locale));

		functionalities.add(newFunctionality(parentcode.concat(".link"), locale));
		functionalities.add(newFunctionality(parentcode.concat(multiple).concat(files).concat(".upload"), locale));
		functionalities.add(newFunctionality(parentcode.concat(".target"), true, true, locale));
		return functionalities;
	}

	private List<Functionality> addTransformationData(Locale locale) {
		List<Functionality> functionalities = new ArrayList<>(0);
		String parentcode = functionality.concat(transformation);
		functionalities.add(newFunctionality(parentcode, locale));
		functionalities.addAll(addTransformationTree(parentcode, locale));
		functionalities.addAll(addCombinateTransformationTree(parentcode, locale));
		return functionalities;
	}

	private List<Functionality> addLoad(Locale locale) {
		List<Functionality> functionalities = new ArrayList<>(0);
		String parentcode = functionality.concat(load);
		functionalities.add(newFunctionality(parentcode, locale));
		functionalities.add(newFunctionality(parentcode.concat(".clear.tables.and.grids"), locale));
		functionalities.add(newFunctionality(parentcode.concat(log), locale));
		functionalities.add(newFunctionality(parentcode.concat(".tables.and.grids"), locale));
		return functionalities;
	}

	private List<Functionality> addReporting(Locale locale) {
		List<Functionality> functionalities = new ArrayList<>(0);
		String parentcode = functionality.concat(reporting);
		functionalities.add(newFunctionality(parentcode, locale));
		functionalities.add(newFunctionality(parentcode.concat(calculated).concat(measure), true, true, locale));
		functionalities.add(newFunctionality(parentcode.concat(dashboard).concat(report).concat(".pivot.table"), true,
				true, locale));
		functionalities.add(
				newFunctionality(parentcode.concat(dashboard).concat(report).concat(".chart"), true, true, locale));
		functionalities.add(newFunctionality(parentcode.concat(dashboard).concat(report).concat(".drill.down.grid"),
				true, true, locale));
		functionalities.add(newFunctionality(parentcode.concat(report).concat(grid), true, true, locale));
		functionalities.add(newFunctionality(parentcode.concat(report).concat(".publication"), true, true, locale));
		functionalities.add(newFunctionality(parentcode.concat(report).concat(".spreadsheet"), true, true, locale));
		functionalities.add(newFunctionality(parentcode.concat(structured).concat(report), locale));
		return functionalities;
	}

	private List<Functionality> addDashboarding(Locale locale) {
		List<Functionality> functionalities = new ArrayList<>(0);
		String parentcode = functionality.concat(dashboarding);
		functionalities.add(newFunctionality(parentcode, locale));
		functionalities.add(newFunctionality(parentcode.concat(alarm), true, true, locale));
		functionalities.add(newFunctionality(parentcode.concat(alarm).concat(scheduled), locale));
		functionalities.add(newFunctionality(parentcode.concat(dashboard), true, true, locale));
		return functionalities;
	}

	private List<Functionality> addReconciliation(Locale locale) {
		List<Functionality> functionalities = new ArrayList<>(0);
		String parentcode = functionality.concat(reconciliation);
		functionalities.add(newFunctionality(parentcode, locale));
		functionalities.add(newFunctionality(parentcode.concat(automatic).concat(reconciliation), true, true, locale));
		functionalities.add(newFunctionality(parentcode.concat(automatic).concat(reconciliation).concat(log), locale));
		functionalities.add(newFunctionality(parentcode.concat(automatic).concat(reconciliation).concat(run), locale));
		functionalities
				.add(newFunctionality(parentcode.concat(automatic).concat(reconciliation).concat(scheduling), locale));
		functionalities.add(newFunctionality(parentcode.concat(automatic).concat(reconciliation).concat(scheduling)
				.concat(scheduled).concat(".auto.reco"), locale));
		functionalities.add(newFunctionality(parentcode.concat(".clear"), locale));
		functionalities.add(newFunctionality(parentcode.concat(".configuration"), locale));
		functionalities.add(newFunctionality(parentcode.concat(".filter"), true, true, locale));
		functionalities.add(newFunctionality(parentcode.concat(run), locale));
		return functionalities;
	}

	private List<Functionality> addBilling(Locale locale) {
		List<Functionality> functionalities = new ArrayList<>(0);
		String parentcode = functionality.concat(billing);
		functionalities.add(newFunctionality(parentcode, locale));

		functionalities.add(newFunctionality(parentcode.concat(event).concat(".repository"), false, true, locale));
		functionalities.add(newFunctionality(parentcode.concat(model), true, true, locale));
		functionalities.add(newFunctionality(parentcode.concat(model).concat(".template"), true, true, locale));

		functionalities.add(newFunctionality(parentcode.concat(run).concat(".outcome"), true, false, locale));
		functionalities.add(newFunctionality(parentcode.concat(run).concat(".invoice"), true, false, locale));
		functionalities.add(newFunctionality(parentcode.concat(run).concat(".credit.note"), true, false, locale));

		functionalities.add(newFunctionality(parentcode.concat(".client.repository"), locale));
		functionalities.add(newFunctionality(parentcode.concat(".company.repository"), locale));
		functionalities.add(newFunctionality(parentcode.concat(event), locale));
		functionalities.add(newFunctionality(parentcode.concat(event).concat(manual), locale));
		functionalities
				.add(newFunctionality(parentcode.concat(event).concat(manual).concat(".credit.note.event"), locale));
		functionalities.add(newFunctionality(parentcode.concat(event).concat(manual).concat(".invoice.event"), locale));

		functionalities.add(newFunctionality(parentcode.concat(".invoice"), locale));
		functionalities.add(newFunctionality(parentcode.concat(".models"), locale));
		functionalities.add(newFunctionality(parentcode.concat(model).concat(scheduled), locale));
		functionalities.add(newFunctionality(parentcode.concat(run), locale));
		functionalities.add(newFunctionality(parentcode.concat(run).concat(".status"), locale));
		return functionalities;
	}

	private List<Functionality> addDataManager(Locale locale) {
		List<Functionality> functionalities = new ArrayList<>(0);
		String parentcode = functionality.concat(data).concat(manager);
		functionalities.add(newFunctionality(parentcode, locale));
		functionalities.add(newFunctionality(parentcode.concat(archive), true, false, locale));
		functionalities.add(newFunctionality(parentcode.concat(archive).concat(".config"), true, true, locale));
		functionalities.add(newFunctionality(parentcode.concat(archive).concat(".logs"), true, false, locale));
		return functionalities;
	}

	private List<Functionality> addAdministration(Locale locale) {
		List<Functionality> functionalities = new ArrayList<>(0);
		String parentcode = functionality.concat(administration);
		functionalities.add(newFunctionality(parentcode, locale));
		functionalities.add(newFunctionality(parentcode.concat(".connected.user.profile"), locale));
		functionalities.add(newFunctionality(parentcode.concat(dashboard), locale));
		functionalities.add(newFunctionality(parentcode.concat(".profil"), locale));
		functionalities.add(newFunctionality(parentcode.concat(".role"), locale));
		functionalities.add(newFunctionality(parentcode.concat(".subscription"), locale));
		functionalities.add(newFunctionality(parentcode.concat(".user"), locale));
		return functionalities;
	}

	private List<Functionality> addSettings(Locale locale) {
		List<Functionality> functionalities = new ArrayList<>(0);
		String parentcode = functionality.concat(settings);
		functionalities.add(newFunctionality(parentcode, locale));
		functionalities.add(newFunctionality(parentcode.concat(".configuration"), false, true, locale));
		functionalities.add(newFunctionality(parentcode.concat(group), locale));
		functionalities.add(newFunctionality(parentcode.concat(".incremental.number"), true, true, locale));
		functionalities.add(newFunctionality(parentcode.concat(sever).concat(".configuration"), locale));
		return functionalities;
	}

	private List<Functionality> addProject(Locale locale) {
		List<Functionality> functionalities = new ArrayList<>(0);
		String parentcode = functionality.concat(project);
		functionalities.add(newFunctionality(parentcode, locale));
		functionalities.add(newFunctionality(parentcode.concat(backup), locale));
		functionalities.add(newFunctionality(parentcode.concat(backup).concat(automatic), locale));
		functionalities.add(newFunctionality(parentcode.concat(backup).concat(".batchsourcing"), locale));
		functionalities.add(newFunctionality(parentcode.concat(backup).concat(".security"), locale));
		functionalities.add(newFunctionality(parentcode.concat(backup).concat(".simple"), locale));
		functionalities.add(newFunctionality(parentcode.concat(".edit"), locale));
		functionalities.add(newFunctionality(parentcode.concat(".import"), locale));
		functionalities.add(newFunctionality(parentcode.concat(".open"), locale));
		functionalities.add(newFunctionality(parentcode.concat(".recents"), locale));
		return functionalities;
	}

	private List<Functionality> addFile(Locale locale) {
		List<Functionality> functionalities = new ArrayList<>(0);
		String parentcode = functionality.concat(file);
		functionalities.add(newFunctionality(parentcode, locale));
		functionalities.add(newFunctionality(parentcode.concat(".exit"), locale));
		functionalities.add(newFunctionality(parentcode.concat(save), locale));
		functionalities.add(newFunctionality(parentcode.concat(save).concat(".as"), locale));
		return functionalities;
	}

	private List<Functionality> addHelp(Locale locale) {
		List<Functionality> functionalities = new ArrayList<>(0);
		String parentcode = functionality.concat(help);
		functionalities.add(newFunctionality(parentcode, locale));
		functionalities.add(newFunctionality(parentcode.concat(".about"), locale));
		functionalities.add(newFunctionality(parentcode.concat(".content"), locale));
		return functionalities;
	}

	private List<Functionality> addOther(Locale locale) {
		List<Functionality> functionalities = new ArrayList<>(0);
		String parentcode = functionality;
		functionalities.add(newFunctionality(parentcode, locale));
		functionalities.add(newFunctionality(parentcode.concat(".group"), locale));
		functionalities.add(newFunctionality(parentcode.concat(".home.page"), locale));
		functionalities.add(newFunctionality(parentcode.concat(".logout"), locale));
		return functionalities;
	}

	private List<Functionality> addTransformationTree(String parentcode_, Locale locale) {
		List<Functionality> functionalities = new ArrayList<>(0);
		String code = parentcode_;
		String parentcode = code;
		functionalities.add(newFunctionality(parentcode.concat(".combined.tree"), true, true, locale));
		functionalities.add(newFunctionality(parentcode.concat(".data"), locale));
		functionalities.add(newFunctionality(parentcode.concat(".slide"), locale));
		return functionalities;
	}

	private List<Functionality> addCombinateTransformationTree(String parentcode_, Locale locale) {
		List<Functionality> functionalities = new ArrayList<>(0);
		String code = parentcode_.concat(tree);
		String parentcode = code;
		functionalities.add(newFunctionality(code, true, true, locale));
		functionalities.add(newFunctionality(parentcode.concat(".clear"), locale));
		functionalities.add(newFunctionality(parentcode.concat(load), locale));
		functionalities.add(newFunctionality(parentcode.concat(scheduled), locale));
		return functionalities;
	}

	private Functionality newFunctionality(String code, Locale locale) {
		return newFunctionality(code, false, false, locale);
	}

	private Functionality newFunctionality(String code, boolean canView, boolean canEdit, Locale locale) {
		Functionality functionality = new Functionality();
		functionality.setCode(code);
		functionality.setCanView(canView);
		functionality.setCanEdit(canEdit);
		String message = messageSource.getMessage(code, new String[] {}, locale);
		functionality.setName(message);
		return functionality;
	}
}
