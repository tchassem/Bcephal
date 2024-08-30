package com.moriset.bcephal.initiation;

import java.sql.Timestamp;

import com.moriset.bcephal.domain.ListChangeHandler;
import com.moriset.bcephal.initiation.domain.Attribute;
import com.moriset.bcephal.initiation.domain.AttributeValue;
import com.moriset.bcephal.initiation.domain.CalendarCategory;
import com.moriset.bcephal.initiation.domain.Entity;
import com.moriset.bcephal.initiation.domain.Measure;
import com.moriset.bcephal.initiation.domain.Model;
import com.moriset.bcephal.initiation.domain.PeriodName;

public class InitiationFactory {
	
	
	public PeriodName buildMainPeriod() {
		return PeriodName.builder().name("Main period").childrenListChangeHandler(buildPeriods()).build();
	}

	private ListChangeHandler<PeriodName> buildPeriods() {
		ListChangeHandler<PeriodName> periodes = new ListChangeHandler<>();
		int position = 0;
		periodes.addNew(PeriodName.builder().name("Value date").position(position++)
				.childrenListChangeHandler(new ListChangeHandler<>()).build());
		periodes.addNew(PeriodName.builder().name("Worldline Report Date").position(position++)
				.childrenListChangeHandler(new ListChangeHandler<>()).build());
		periodes.addNew(PeriodName.builder().name("Processor date").parent(null).position(position++)
				.childrenListChangeHandler(new ListChangeHandler<>()).build());
		periodes.addNew(PeriodName.builder().name("TPPN Period").parent(null).position(position++)
				.childrenListChangeHandler(new ListChangeHandler<>()).build());
		periodes.addNew(PeriodName.builder().name("Entry date").parent(null).position(position++)
				.childrenListChangeHandler(new ListChangeHandler<>()).build());
		periodes.addNew(PeriodName.builder().name("Associated payment date").parent(null).position(position++)
				.childrenListChangeHandler(new ListChangeHandler<>()).build());
		PeriodName SpecificDate = PeriodName.builder().name("Specific date").position(position++)
				.childrenListChangeHandler(new ListChangeHandler<>()).build();
		periodes.addNew(SpecificDate);
		PeriodName RecoDate = PeriodName.builder().name("Reco Date").position(position++)
				.childrenListChangeHandler(new ListChangeHandler<>()).build();
		periodes.addNew(RecoDate);
		PeriodName PrecingDate = PeriodName.builder().name("Precing Date").position(position++)
				.childrenListChangeHandler(new ListChangeHandler<>()).build();
		periodes.addNew(PrecingDate);
		/*
		 * PeriodName BillingDate =
		 * PeriodName.builder().name("Billing Date").position(position++).
		 * childrenListChangeHandler(new ListChangeHandler<>()).build();
		 * periodes.addNew(BillingDate);
		 */
		periodes.addNew(PeriodName.builder().name("Invoice validation date").position(position++)
				.childrenListChangeHandler(new ListChangeHandler<>()).build());
		periodes.addNew(PeriodName.builder().name("Invoice date").position(position++)
				.childrenListChangeHandler(new ListChangeHandler<>()).build());
		periodes.addNew(PeriodName.builder().name("Due date").position(position++)
				.childrenListChangeHandler(new ListChangeHandler<>()).build());
		periodes.addNew(PeriodName.builder().name("Billing event date").position(position++)
				.childrenListChangeHandler(new ListChangeHandler<>()).build());
		periodes.addNew(PeriodName.builder().name("Billing run date").position(position++)
				.childrenListChangeHandler(new ListChangeHandler<>()).build());
		periodes.addNew(PeriodName.builder().name("invoice date").position(position++)
				.childrenListChangeHandler(new ListChangeHandler<>()).build());

		position = 0;
		SpecificDate.getChildrenListChangeHandler().addNew(PeriodName.builder().name("Scheme Date").parent(SpecificDate)
				.position(position++).childrenListChangeHandler(new ListChangeHandler<>()).build());
		SpecificDate.getChildrenListChangeHandler().addNew(PeriodName.builder().name("MT942 Date").parent(SpecificDate)
				.position(position++).childrenListChangeHandler(new ListChangeHandler<>()).build());
		SpecificDate.getChildrenListChangeHandler()
				.addNew(PeriodName.builder().name("MT942 Entry Date").parent(SpecificDate).position(position++)
						.childrenListChangeHandler(new ListChangeHandler<>()).build());
		SpecificDate.getChildrenListChangeHandler()
				.addNew(PeriodName.builder().name("VSS Fund Transfert Date").parent(SpecificDate).position(position++)
						.childrenListChangeHandler(new ListChangeHandler<>()).build());
		SpecificDate.getChildrenListChangeHandler()
				.addNew(PeriodName.builder().name("Native value date").parent(SpecificDate).position(position++)
						.childrenListChangeHandler(new ListChangeHandler<>()).build());
		SpecificDate.getChildrenListChangeHandler()
				.addNew(PeriodName.builder().name("Associated advisement date").parent(SpecificDate)
						.position(position++).childrenListChangeHandler(new ListChangeHandler<>()).build());
		SpecificDate.getChildrenListChangeHandler()
				.addNew(PeriodName.builder().name("eWL report date").parent(SpecificDate).position(position++)
						.childrenListChangeHandler(new ListChangeHandler<>()).build());

		SpecificDate.getChildrenListChangeHandler()
				.addNew(PeriodName.builder().name("Pentaho date").parent(SpecificDate).position(position++)
						.childrenListChangeHandler(new ListChangeHandler<>()).build());
		SpecificDate.getChildrenListChangeHandler()
				.addNew(PeriodName.builder().name("Scheme Value Date").parent(SpecificDate).position(position++)
						.childrenListChangeHandler(new ListChangeHandler<>()).build());
		SpecificDate.getChildrenListChangeHandler()
		.addNew(PeriodName.builder().name("Scheme report date").parent(SpecificDate).position(position++)
				.childrenListChangeHandler(new ListChangeHandler<>()).build());
		SpecificDate.getChildrenListChangeHandler()
				.addNew(PeriodName.builder().name("Scheme invoice date").parent(SpecificDate).position(position++)
						.childrenListChangeHandler(new ListChangeHandler<>()).build());

		position = 0;
		RecoDate.getChildrenListChangeHandler().addNew(PeriodName.builder().name("Reco date R1").parent(RecoDate)
				.position(position++).childrenListChangeHandler(new ListChangeHandler<>()).build());
		RecoDate.getChildrenListChangeHandler().addNew(PeriodName.builder().name("Reco date R2").parent(RecoDate)
				.position(position++).childrenListChangeHandler(new ListChangeHandler<>()).build());
		RecoDate.getChildrenListChangeHandler().addNew(PeriodName.builder().name("Reco date R3").parent(RecoDate)
				.position(position++).childrenListChangeHandler(new ListChangeHandler<>()).build());
		RecoDate.getChildrenListChangeHandler().addNew(PeriodName.builder().name("Reco date R4").parent(RecoDate)
				.position(position++).childrenListChangeHandler(new ListChangeHandler<>()).build());
		RecoDate.getChildrenListChangeHandler().addNew(PeriodName.builder().name("Reco date R5").parent(RecoDate)
				.position(position++).childrenListChangeHandler(new ListChangeHandler<>()).build());
		RecoDate.getChildrenListChangeHandler().addNew(PeriodName.builder().name("Reco date R6").parent(RecoDate)
				.position(position++).childrenListChangeHandler(new ListChangeHandler<>()).build());
		RecoDate.getChildrenListChangeHandler().addNew(PeriodName.builder().name("Reco date R7").parent(RecoDate)
				.position(position++).childrenListChangeHandler(new ListChangeHandler<>()).build());

		position = 0;
		PrecingDate.getChildrenListChangeHandler().addNew(PeriodName.builder().name("Precing start date")
				.parent(PrecingDate).position(position++).childrenListChangeHandler(new ListChangeHandler<>()).build());
		PrecingDate.getChildrenListChangeHandler().addNew(PeriodName.builder().name("Precing end date")
				.parent(PrecingDate).position(position++).childrenListChangeHandler(new ListChangeHandler<>()).build());

		position = 0;
		/*
		 * BillingDate.getChildrenListChangeHandler().addNew(PeriodName.builder().
		 * name("Billing event date").parent(BillingDate).position(position++).
		 * childrenListChangeHandler(new ListChangeHandler<>()).build());
		 * BillingDate.getChildrenListChangeHandler().addNew(PeriodName.builder().
		 * name("Invoice validation date").parent(BillingDate).position(position++).
		 * childrenListChangeHandler(new ListChangeHandler<>()).build());
		 * BillingDate.getChildrenListChangeHandler().addNew(PeriodName.builder().
		 * name("Invoice date").parent(BillingDate).position(position++).
		 * childrenListChangeHandler(new ListChangeHandler<>()).build());
		 * BillingDate.getChildrenListChangeHandler().addNew(PeriodName.builder().
		 * name("Due date").parent(BillingDate).position(position++).
		 * childrenListChangeHandler(new ListChangeHandler<>()).build());
		 * BillingDate.getChildrenListChangeHandler().addNew(PeriodName.builder().
		 * name("Billing run date").parent(BillingDate).position(position++).
		 * childrenListChangeHandler(new ListChangeHandler<>()).build());
		 */

		return periodes;
	}

	public Model buildModel() {
		Model model = new Model();
		model.setCreationDate(new Timestamp(System.currentTimeMillis()));
		model.setModificationDate(new Timestamp(System.currentTimeMillis()));
		model.setName("modelName");
		return model;
	}

	public Attribute BuildAttribute() {
		Attribute attribute = new Attribute();
		attribute.setName("newDefaultValue");
		attribute.setChildrenListChangeHandler(null);
		return attribute;
	}

	public ListChangeHandler<Model> BuildModels() {

		ListChangeHandler<Model> models = new ListChangeHandler<>();
		ListChangeHandler<Entity> billingModelEntities = new ListChangeHandler<>();
		ListChangeHandler<Entity> binSponsorEntities = new ListChangeHandler<>();
		ListChangeHandler<Entity> ContractAndOrderEntities = new ListChangeHandler<>();
		ListChangeHandler<Entity> PricingAndFeesEntities = new ListChangeHandler<>();
		ListChangeHandler<Entity> SupplierInvoiceEntities = new ListChangeHandler<>();

		int position = 0;
		Model billingModel = Model.builder().name("Billing model").entityListChangeHandler(billingModelEntities)
				.position(position++).build();
		models.addNew(billingModel);
		Model binSponsorModel = Model.builder().name("Bin Sponsor").entityListChangeHandler(binSponsorEntities)
				.position(position++).build();
		models.addNew(binSponsorModel);
		Model ContractAndOrderModel = Model.builder().name("Contract And Order")
				.entityListChangeHandler(ContractAndOrderEntities).position(position++).build();
		models.addNew(ContractAndOrderModel);
		Model PricingAndFeesModel = Model.builder().name("Pricing and fees")
				.entityListChangeHandler(PricingAndFeesEntities).position(position++).build();
		models.addNew(PricingAndFeesModel);
		Model SupplierInvoiceModel = Model.builder().name("Supplier Invoice")
				.entityListChangeHandler(SupplierInvoiceEntities).position(position++).build();
		models.addNew(SupplierInvoiceModel);

		// position = 0;

		ListChangeHandler<Attribute> AddresslAttribList = new ListChangeHandler<>();
		ListChangeHandler<Attribute> Billing_CompanyAttribList = new ListChangeHandler<>();
		ListChangeHandler<Attribute> Billing_EventAttribList = new ListChangeHandler<>();
		ListChangeHandler<Attribute> ClientAttribList = new ListChangeHandler<>();
		ListChangeHandler<Attribute> InvoceAttribList = new ListChangeHandler<>();

		ListChangeHandler<Attribute> ProductAttribList = new ListChangeHandler<>();

		ListChangeHandler<Attribute> AdvisementAttribList = new ListChangeHandler<>();
		ListChangeHandler<Attribute> BankAttribList = new ListChangeHandler<>();
		ListChangeHandler<Attribute> CoreAttribList = new ListChangeHandler<>();
		ListChangeHandler<Attribute> Bill210_affiliate_biling_eventList = new ListChangeHandler<>();
		ListChangeHandler<Attribute> FreezeAttribList = new ListChangeHandler<>();
		ListChangeHandler<Attribute> MemberBankAttribList = new ListChangeHandler<>();
		ListChangeHandler<Attribute> NeutralizationAttribList = new ListChangeHandler<>();
		ListChangeHandler<Attribute> NoteAttribList = new ListChangeHandler<>();
		ListChangeHandler<Attribute> PML_ID_AttribList = new ListChangeHandler<>();
		ListChangeHandler<Attribute> PR_AttribList = new ListChangeHandler<>();
		ListChangeHandler<Attribute> ProcessorAttribList = new ListChangeHandler<>();
		ListChangeHandler<Attribute> REC_AttribList = new ListChangeHandler<>();
		ListChangeHandler<Attribute> ReconciliationAttribList = new ListChangeHandler<>();
		ListChangeHandler<Attribute> SchemeAttribList = new ListChangeHandler<>();
		ListChangeHandler<Attribute> System_AttribList = new ListChangeHandler<>();
		ListChangeHandler<Attribute> ContractAndOrderAttribList = new ListChangeHandler<>();
		ListChangeHandler<Attribute> PricingAttribList = new ListChangeHandler<>();
		ListChangeHandler<Attribute> SchemeInvoiceAttribList = new ListChangeHandler<>();

		billingModelEntities.addNew(Entity.builder().name("Address and Contact details")
				.attributeListChangeHandler(AddresslAttribList).build());

		Entity Billing_Company = Entity.builder().name("Billing Company")
				.attributeListChangeHandler(Billing_CompanyAttribList).build();
		billingModelEntities.addNew(Billing_Company);

		billingModelEntities.addNew(
				Entity.builder().name("Billing event").attributeListChangeHandler(Billing_EventAttribList).build());

		billingModelEntities
				.addNew(Entity.builder().name("Client").attributeListChangeHandler(ClientAttribList).build());

		billingModelEntities
				.addNew(Entity.builder().name("Invoice").attributeListChangeHandler(InvoceAttribList).build());

		
		  billingModelEntities.addNew(Entity.builder().name("Product")
		  .attributeListChangeHandler(ProductAttribList).build());
		 
		 
		binSponsorEntities
				.addNew(Entity.builder().name("Advisement").attributeListChangeHandler(AdvisementAttribList).build());

		binSponsorEntities.addNew(Entity.builder().name("Bank").attributeListChangeHandler(BankAttribList).build());

		binSponsorEntities.addNew(Entity.builder().name("Core").attributeListChangeHandler(CoreAttribList).build());

		binSponsorEntities.addNew(Entity.builder().name("Freeze").attributeListChangeHandler(FreezeAttribList).build());

		binSponsorEntities
				.addNew(Entity.builder().name("Member Bank").attributeListChangeHandler(MemberBankAttribList).build());

		binSponsorEntities.addNew(
				Entity.builder().name("Neutralization").attributeListChangeHandler(NeutralizationAttribList).build());

		binSponsorEntities.addNew(Entity.builder().name("Note").attributeListChangeHandler(NoteAttribList).build());

		binSponsorEntities
				.addNew(Entity.builder().name("PML ID").attributeListChangeHandler(PML_ID_AttribList).build());

		binSponsorEntities.addNew(Entity.builder().name("PR").attributeListChangeHandler(PR_AttribList).build());

		binSponsorEntities
				.addNew(Entity.builder().name("Processor").attributeListChangeHandler(ProcessorAttribList).build());

		binSponsorEntities.addNew(Entity.builder().name("REC").attributeListChangeHandler(REC_AttribList).build());

		binSponsorEntities.addNew(
				Entity.builder().name("Reconciliation").attributeListChangeHandler(ReconciliationAttribList).build());

		binSponsorEntities.addNew(Entity.builder().name("Scheme").attributeListChangeHandler(SchemeAttribList).build());

		binSponsorEntities
				.addNew(Entity.builder().name("System").attributeListChangeHandler(System_AttribList).build());

		ContractAndOrderEntities
				.addNew(Entity.builder().name("Order").attributeListChangeHandler(ContractAndOrderAttribList).build());

		PricingAndFeesEntities
				.addNew(Entity.builder().name("Pricing ").attributeListChangeHandler(PricingAttribList).build());

		SupplierInvoiceEntities.addNew(
				Entity.builder().name("Scheme Invoice ").attributeListChangeHandler(SchemeInvoiceAttribList).build());

		position = 0;

		AddresslAttribList.addNew(Attribute.builder().valueListChangeHandler(new ListChangeHandler<>())
				.childrenListChangeHandler(new ListChangeHandler<>()).name("City").position(position++).build());

		AddresslAttribList.addNew(Attribute.builder().valueListChangeHandler(new ListChangeHandler<>())
				.childrenListChangeHandler(new ListChangeHandler<>()).name("Country").position(position++).build());
		AddresslAttribList.addNew(Attribute.builder().valueListChangeHandler(new ListChangeHandler<>())
				.childrenListChangeHandler(new ListChangeHandler<>()).name("Wordline report time").position(position++).build());

		AddresslAttribList.addNew(Attribute.builder().valueListChangeHandler(new ListChangeHandler<>())
				.childrenListChangeHandler(new ListChangeHandler<>()).name("Email").position(position++).build());
		AddresslAttribList.addNew(Attribute.builder().valueListChangeHandler(new ListChangeHandler<>())
				.childrenListChangeHandler(new ListChangeHandler<>()).name("Phone").position(position++).build());
		AddresslAttribList.addNew(Attribute.builder().valueListChangeHandler(new ListChangeHandler<>())
				.childrenListChangeHandler(new ListChangeHandler<>()).name("Postal code").position(position++).build());
		AddresslAttribList.addNew(Attribute.builder().valueListChangeHandler(new ListChangeHandler<>())
				.childrenListChangeHandler(new ListChangeHandler<>()).name("Street, number and box")
				.position(position++).build());
		/*
		 * AddresslAttribList.addNew( Attribute.builder().valueListChangeHandler(new
		 * ListChangeHandler<>()) .childrenListChangeHandler(new
		 * ListChangeHandler<>()).name("Email cc").position(position++).build());
		 */
		Billing_CompanyAttribList.addNew(Attribute.builder().valueListChangeHandler(new ListChangeHandler<>())
				.childrenListChangeHandler(new ListChangeHandler<>()).name("Billing company ID").position(position++)
				.build());
		Billing_CompanyAttribList.addNew(Attribute.builder().valueListChangeHandler(new ListChangeHandler<>())
				.childrenListChangeHandler(new ListChangeHandler<>()).name("Billing company name").position(position++)
				.build());

		Billing_EventAttribList.addNew(Attribute.builder().valueListChangeHandler(new ListChangeHandler<>())
				.childrenListChangeHandler(new ListChangeHandler<>()).name("Billing event A/M").position(position++)
				.build());
		Billing_EventAttribList.addNew(Attribute.builder().valueListChangeHandler(new ListChangeHandler<>())
				.childrenListChangeHandler(new ListChangeHandler<>()).name("Billing event description")
				.position(position++).build());
		Billing_EventAttribList.addNew(Attribute.builder().valueListChangeHandler(new ListChangeHandler<>())
				.childrenListChangeHandler(new ListChangeHandler<>()).name("Billing event status").position(position++)
				.build());
		Billing_EventAttribList.addNew(Attribute.builder().valueListChangeHandler(new ListChangeHandler<>())
				.childrenListChangeHandler(new ListChangeHandler<>()).name("Billing event type").position(position++)
				.build());
		Billing_EventAttribList.addNew(Attribute.builder().valueListChangeHandler(new ListChangeHandler<>())
				.childrenListChangeHandler(new ListChangeHandler<>()).name("Billing run").position(position++).build());
		Billing_EventAttribList.addNew(Attribute.builder().valueListChangeHandler(new ListChangeHandler<>())
				.childrenListChangeHandler(new ListChangeHandler<>()).name("Billing event ID").position(position++)
				.build());
		Billing_EventAttribList.addNew(Attribute.builder().valueListChangeHandler(new ListChangeHandler<>())
				.childrenListChangeHandler(new ListChangeHandler<>()).name("Billing event name").position(position++)
				.build());
		Billing_EventAttribList.addNew(Attribute.builder().valueListChangeHandler(new ListChangeHandler<>())
				.childrenListChangeHandler(new ListChangeHandler<>()).name("Billing event category ID")
				.position(position++).build());
		Billing_EventAttribList.addNew(Attribute.builder().valueListChangeHandler(new ListChangeHandler<>())
				.childrenListChangeHandler(new ListChangeHandler<>()).name("Billing event category name")
				.position(position++).build());
		Billing_EventAttribList.addNew(Attribute.builder().valueListChangeHandler(new ListChangeHandler<>())
				.childrenListChangeHandler(new ListChangeHandler<>()).name("Billing group 1").position(position++)
				.build());
		Billing_EventAttribList.addNew(Attribute.builder().valueListChangeHandler(new ListChangeHandler<>())
				.childrenListChangeHandler(new ListChangeHandler<>()).name("Billing group 2").position(position++)
				.build());
		Billing_EventAttribList.addNew(Attribute.builder().valueListChangeHandler(new ListChangeHandler<>())
				.childrenListChangeHandler(new ListChangeHandler<>()).name("Billing event nature").position(position++)
				.build());
		Billing_EventAttribList.addNew(Attribute.builder().valueListChangeHandler(new ListChangeHandler<>())
				.childrenListChangeHandler(new ListChangeHandler<>()).name("Billing event category")
				.position(position++).build());

		ClientAttribList.addNew(Attribute.builder().valueListChangeHandler(new ListChangeHandler<>())
				.childrenListChangeHandler(new ListChangeHandler<>()).name("Client contact name").position(position++)
				.build());
		ClientAttribList.addNew(Attribute.builder().valueListChangeHandler(new ListChangeHandler<>())
				.childrenListChangeHandler(new ListChangeHandler<>()).name("Client ID").position(position++).build());
		ClientAttribList.addNew(Attribute.builder().valueListChangeHandler(new ListChangeHandler<>())
				.childrenListChangeHandler(new ListChangeHandler<>()).name("Client name").position(position++).build());
		ClientAttribList.addNew(Attribute.builder().valueListChangeHandler(new ListChangeHandler<>())
				.childrenListChangeHandler(new ListChangeHandler<>()).name("Department name").position(position++)
				.build());
		ClientAttribList.addNew(Attribute.builder().valueListChangeHandler(new ListChangeHandler<>())
				.childrenListChangeHandler(new ListChangeHandler<>()).name("Department number").position(position++)
				.build());
		ClientAttribList.addNew(Attribute.builder().valueListChangeHandler(new ListChangeHandler<>())
				.childrenListChangeHandler(new ListChangeHandler<>()).name("Language").position(position++).build());
		ClientAttribList.addNew(Attribute.builder().valueListChangeHandler(new ListChangeHandler<>())
				.childrenListChangeHandler(new ListChangeHandler<>()).name("Legal form").position(position++).build());
		ClientAttribList.addNew(Attribute.builder().valueListChangeHandler(new ListChangeHandler<>())
				.childrenListChangeHandler(new ListChangeHandler<>()).name("VAT number").position(position++).build());
		ClientAttribList.addNew(Attribute.builder().valueListChangeHandler(new ListChangeHandler<>())
				.childrenListChangeHandler(new ListChangeHandler<>()).name("Client status").position(position++)
				.build());

		ClientAttribList.addNew(Attribute.builder().valueListChangeHandler(new ListChangeHandler<>())
				.childrenListChangeHandler(new ListChangeHandler<>()).name("Client contact title").position(position++)
				.build());
		ClientAttribList.addNew(Attribute.builder().valueListChangeHandler(new ListChangeHandler<>())
				.childrenListChangeHandler(new ListChangeHandler<>()).name("Contact firstname").position(position++)
				.build());
		ClientAttribList.addNew(Attribute.builder().valueListChangeHandler(new ListChangeHandler<>())
				.childrenListChangeHandler(new ListChangeHandler<>()).name("Contact title").position(position++)
				.build());

		InvoceAttribList.addNew(Attribute.builder().valueListChangeHandler(new ListChangeHandler<>())
				.childrenListChangeHandler(new ListChangeHandler<>()).name("Communication message").position(position++)
				.build());
		InvoceAttribList.addNew(Attribute.builder().valueListChangeHandler(new ListChangeHandler<>())
				.childrenListChangeHandler(new ListChangeHandler<>()).name("Invoice number").position(position++)
				.build());
		InvoceAttribList.addNew(Attribute.builder().valueListChangeHandler(new ListChangeHandler<>())
				.childrenListChangeHandler(new ListChangeHandler<>()).name("Invoice status").position(position++)
				.build());
		InvoceAttribList.addNew(Attribute.builder().valueListChangeHandler(new ListChangeHandler<>())
				.childrenListChangeHandler(new ListChangeHandler<>()).name("Invoice type").position(position++)
				.build());
		InvoceAttribList.addNew(Attribute.builder().valueListChangeHandler(new ListChangeHandler<>())
				.childrenListChangeHandler(new ListChangeHandler<>()).name("Sending Status").position(position++)
				.build());
		InvoceAttribList.addNew(Attribute.builder().valueListChangeHandler(new ListChangeHandler<>())
				.childrenListChangeHandler(new ListChangeHandler<>()).name("Sub invoice number").position(position++)
				.build());

		/*
		 * InvoceAttribList.addNew( Attribute.builder().valueListChangeHandler(new
		 * ListChangeHandler<>()) .childrenListChangeHandler(new
		 * ListChangeHandler<>()).name("Invoice description").position(position++).build
		 * ()); InvoceAttribList.addNew( Attribute.builder().valueListChangeHandler(new
		 * ListChangeHandler<>()) .childrenListChangeHandler(new
		 * ListChangeHandler<>()).name("Invoice pivot").position(position++).build());
		 */
		ProductAttribList.addNew(Attribute.builder().valueListChangeHandler(new ListChangeHandler<>())
				.childrenListChangeHandler(new ListChangeHandler<>()).name("Product ID").position(position++).build());
		ProductAttribList.addNew(Attribute.builder().valueListChangeHandler(new ListChangeHandler<>())
				.childrenListChangeHandler(new ListChangeHandler<>()).name("Product name").position(position++)
				.build());
		ProductAttribList.addNew(Attribute.builder().valueListChangeHandler(new ListChangeHandler<>())
				.childrenListChangeHandler(new ListChangeHandler<>()).name("Product Type").position(position++)
				.build());

		AdvisementAttribList.addNew(Attribute.builder().valueListChangeHandler(new ListChangeHandler<>())
				.childrenListChangeHandler(new ListChangeHandler<>()).name("Unique Report ID").position(position++)
				.build());
		AdvisementAttribList.addNew(Attribute.builder().valueListChangeHandler(new ListChangeHandler<>())
				.childrenListChangeHandler(new ListChangeHandler<>()).name("Advisement ID").position(position++)
				.build());
		AdvisementAttribList.addNew(Attribute.builder().valueListChangeHandler(new ListChangeHandler<>())
				.childrenListChangeHandler(new ListChangeHandler<>()).name("Sponsor Unique Report ID")
				.position(position++).build());
		AdvisementAttribList.addNew(Attribute.builder().valueListChangeHandler(new ListChangeHandler<>())
				.childrenListChangeHandler(new ListChangeHandler<>()).name("Operation ID").position(position++)
				.build());
		AdvisementAttribList.addNew(Attribute.builder().valueListChangeHandler(new ListChangeHandler<>())
				.childrenListChangeHandler(new ListChangeHandler<>()).name("Advisement Account ID").position(position++)
				.build());
		AdvisementAttribList.addNew(Attribute.builder().valueListChangeHandler(new ListChangeHandler<>())
				.childrenListChangeHandler(new ListChangeHandler<>()).name("Scheme Cycle").position(position++)
				.build());
		AdvisementAttribList.addNew(Attribute.builder().valueListChangeHandler(new ListChangeHandler<>())
				.childrenListChangeHandler(new ListChangeHandler<>()).name("Advisement message").position(position++)
				.build());
		AdvisementAttribList.addNew(Attribute.builder().valueListChangeHandler(new ListChangeHandler<>())
				.childrenListChangeHandler(new ListChangeHandler<>()).name("Processor Report Time").position(position++)
				.build());
		AdvisementAttribList.addNew(Attribute.builder().valueListChangeHandler(new ListChangeHandler<>())
				.childrenListChangeHandler(new ListChangeHandler<>()).name("Advisment nbr").position(position++)
				.build());
		AdvisementAttribList.addNew(Attribute.builder().valueListChangeHandler(new ListChangeHandler<>())
				.childrenListChangeHandler(new ListChangeHandler<>()).name("TPPN").position(position++).build());
		AdvisementAttribList.addNew(Attribute.builder().valueListChangeHandler(new ListChangeHandler<>())
				.childrenListChangeHandler(new ListChangeHandler<>()).name("Scheme product type").position(position++)
				.build());
		AdvisementAttribList.addNew(Attribute.builder().valueListChangeHandler(new ListChangeHandler<>())
				.childrenListChangeHandler(new ListChangeHandler<>()).name("Account ID").position(position++).build());

		BankAttribList.addNew(Attribute.builder().valueListChangeHandler(new ListChangeHandler<>())
				.childrenListChangeHandler(new ListChangeHandler<>()).name("Transaction N°").position(position++)
				.build());
		BankAttribList.addNew(Attribute.builder().valueListChangeHandler(new ListChangeHandler<>())
				.childrenListChangeHandler(new ListChangeHandler<>()).name("Bank Account N°").position(position++)
				.build());
		BankAttribList.addNew(Attribute.builder().valueListChangeHandler(new ListChangeHandler<>())
				.childrenListChangeHandler(new ListChangeHandler<>()).name("Statement N°").position(position++)
				.build());
		BankAttribList.addNew(Attribute.builder().valueListChangeHandler(new ListChangeHandler<>())
				.childrenListChangeHandler(new ListChangeHandler<>()).name("Sequence N°").position(position++).build());
		BankAttribList.addNew(Attribute.builder().valueListChangeHandler(new ListChangeHandler<>())
				.childrenListChangeHandler(new ListChangeHandler<>()).name("SwiftTxCode").position(position++).build());
		BankAttribList.addNew(Attribute.builder().valueListChangeHandler(new ListChangeHandler<>())
				.childrenListChangeHandler(new ListChangeHandler<>()).name("Counterpart Name").position(position++)
				.build());
		BankAttribList.addNew(Attribute.builder().valueListChangeHandler(new ListChangeHandler<>())
				.childrenListChangeHandler(new ListChangeHandler<>()).name("Counterpart Account N°")
				.position(position++).build());
		BankAttribList.addNew(Attribute.builder().valueListChangeHandler(new ListChangeHandler<>())
				.childrenListChangeHandler(new ListChangeHandler<>()).name("Financial Account ID").position(position++)
				.build());
		BankAttribList.addNew(Attribute.builder().valueListChangeHandler(new ListChangeHandler<>())
				.childrenListChangeHandler(new ListChangeHandler<>()).name("MT942 Message").position(position++)
				.build());
		BankAttribList.addNew(Attribute.builder().valueListChangeHandler(new ListChangeHandler<>())
				.childrenListChangeHandler(new ListChangeHandler<>()).name("Message").position(position++)
				.build());
		BankAttribList.addNew(Attribute.builder().valueListChangeHandler(new ListChangeHandler<>())
				.childrenListChangeHandler(new ListChangeHandler<>()).name("MT942 Type").position(position++).build());
		BankAttribList.addNew(Attribute.builder().valueListChangeHandler(new ListChangeHandler<>())
				.childrenListChangeHandler(new ListChangeHandler<>()).name("MT942 Code").position(position++).build());
		BankAttribList.addNew(Attribute.builder().valueListChangeHandler(new ListChangeHandler<>())
				.childrenListChangeHandler(new ListChangeHandler<>()).name("Ultimate Beneficiary Name")
				.position(position++).build());
		BankAttribList.addNew(Attribute.builder().valueListChangeHandler(new ListChangeHandler<>())
				.childrenListChangeHandler(new ListChangeHandler<>()).name("Ultimate Beneficiary ID")
				.position(position++).build());

		Bill210_affiliate_biling_eventList.addNew(Attribute.builder().valueListChangeHandler(new ListChangeHandler<>())
				.childrenListChangeHandler(new ListChangeHandler<>()).name("D/C - do not use - to delete")
				.position(position++).build());
		Bill210_affiliate_biling_eventList.addNew(Attribute.builder().valueListChangeHandler(new ListChangeHandler<>())
				.childrenListChangeHandler(new ListChangeHandler<>()).name("Member bank ID - do not use - to delete")
				.position(position++).build());

		CoreAttribList.addNew(Attribute.builder().valueListChangeHandler(new ListChangeHandler<>())
				.childrenListChangeHandler(new ListChangeHandler<>()).name("Currency ID").position(position++).build());
		CoreAttribList.addNew(Attribute.builder().valueListChangeHandler(new ListChangeHandler<>())
				.childrenListChangeHandler(new ListChangeHandler<>()).name("D-C").position(position++).build());
		/*
		 * CoreAttribList.addNew( Attribute.builder().valueListChangeHandler(new
		 * ListChangeHandler<>()) .childrenListChangeHandler(new
		 * ListChangeHandler<>()).name("Currency Name").position(position++).build());
		 * CoreAttribList.addNew( Attribute.builder().valueListChangeHandler(new
		 * ListChangeHandler<>()) .childrenListChangeHandler(new
		 * ListChangeHandler<>()).name("Native DC").position(position++).build());
		 * CoreAttribList.addNew( Attribute.builder().valueListChangeHandler(new
		 * ListChangeHandler<>()) .childrenListChangeHandler(new
		 * ListChangeHandler<>()).name("Status").position(position++).build());
		 */
		FreezeAttribList.addNew(Attribute.builder().valueListChangeHandler(new ListChangeHandler<>())
				.childrenListChangeHandler(new ListChangeHandler<>()).name("F01").position(position++).build());
		FreezeAttribList.addNew(Attribute.builder().valueListChangeHandler(new ListChangeHandler<>())
				.childrenListChangeHandler(new ListChangeHandler<>()).name("F02").position(position++).build());
		FreezeAttribList.addNew(Attribute.builder().valueListChangeHandler(new ListChangeHandler<>())
				.childrenListChangeHandler(new ListChangeHandler<>()).name("F03").position(position++).build());
		FreezeAttribList.addNew(Attribute.builder().valueListChangeHandler(new ListChangeHandler<>())
				.childrenListChangeHandler(new ListChangeHandler<>()).name("F04").position(position++).build());
		FreezeAttribList.addNew(Attribute.builder().valueListChangeHandler(new ListChangeHandler<>())
				.childrenListChangeHandler(new ListChangeHandler<>()).name("F05").position(position++).build());
		FreezeAttribList.addNew(Attribute.builder().valueListChangeHandler(new ListChangeHandler<>())
				.childrenListChangeHandler(new ListChangeHandler<>()).name("F06").position(position++).build());
		FreezeAttribList.addNew(Attribute.builder().valueListChangeHandler(new ListChangeHandler<>())
				.childrenListChangeHandler(new ListChangeHandler<>()).name("F07").position(position++).build());

		MemberBankAttribList.addNew(Attribute.builder().valueListChangeHandler(new ListChangeHandler<>())
				.childrenListChangeHandler(new ListChangeHandler<>()).name("Member Bank ID").position(position++)
				.build());
		MemberBankAttribList.addNew(Attribute.builder().valueListChangeHandler(new ListChangeHandler<>())
				.childrenListChangeHandler(new ListChangeHandler<>()).name("Brocker ID").position(position++).build());
		MemberBankAttribList.addNew(Attribute.builder().valueListChangeHandler(new ListChangeHandler<>())
				.childrenListChangeHandler(new ListChangeHandler<>()).name("Member bank name").position(position++)
				.build());

		NeutralizationAttribList.addNew(Attribute.builder().valueListChangeHandler(new ListChangeHandler<>())
				.childrenListChangeHandler(new ListChangeHandler<>()).name("Neu01").position(position++).build());
		NeutralizationAttribList.addNew(Attribute.builder().valueListChangeHandler(new ListChangeHandler<>())
				.childrenListChangeHandler(new ListChangeHandler<>()).name("Neu02").position(position++).build());
		NeutralizationAttribList.addNew(Attribute.builder().valueListChangeHandler(new ListChangeHandler<>())
				.childrenListChangeHandler(new ListChangeHandler<>()).name("Neu03").position(position++).build());
		NeutralizationAttribList.addNew(Attribute.builder().valueListChangeHandler(new ListChangeHandler<>())
				.childrenListChangeHandler(new ListChangeHandler<>()).name("Neu04").position(position++).build());
		NeutralizationAttribList.addNew(Attribute.builder().valueListChangeHandler(new ListChangeHandler<>())
				.childrenListChangeHandler(new ListChangeHandler<>()).name("Neu05").position(position++).build());
		NeutralizationAttribList.addNew(Attribute.builder().valueListChangeHandler(new ListChangeHandler<>())
				.childrenListChangeHandler(new ListChangeHandler<>()).name("Neu06").position(position++).build());
		NeutralizationAttribList.addNew(Attribute.builder().valueListChangeHandler(new ListChangeHandler<>())
				.childrenListChangeHandler(new ListChangeHandler<>()).name("Neu07").position(position++).build());

		NoteAttribList.addNew(Attribute.builder().valueListChangeHandler(new ListChangeHandler<>())
				.childrenListChangeHandler(new ListChangeHandler<>()).name("Note R01").position(position++).build());
		NoteAttribList.addNew(Attribute.builder().valueListChangeHandler(new ListChangeHandler<>())
				.childrenListChangeHandler(new ListChangeHandler<>()).name("Note R02").position(position++).build());
		NoteAttribList.addNew(Attribute.builder().valueListChangeHandler(new ListChangeHandler<>())
				.childrenListChangeHandler(new ListChangeHandler<>()).name("Note R03").position(position++).build());
		NoteAttribList.addNew(Attribute.builder().valueListChangeHandler(new ListChangeHandler<>())
				.childrenListChangeHandler(new ListChangeHandler<>()).name("Note R04").position(position++).build());
		NoteAttribList.addNew(Attribute.builder().valueListChangeHandler(new ListChangeHandler<>())
				.childrenListChangeHandler(new ListChangeHandler<>()).name("Note R05").position(position++).build());
		NoteAttribList.addNew(Attribute.builder().valueListChangeHandler(new ListChangeHandler<>())
				.childrenListChangeHandler(new ListChangeHandler<>()).name("Note R06").position(position++).build());
		NoteAttribList.addNew(Attribute.builder().valueListChangeHandler(new ListChangeHandler<>())
				.childrenListChangeHandler(new ListChangeHandler<>()).name("Note R07").position(position++).build());

		PML_ID_AttribList.addNew(Attribute.builder().valueListChangeHandler(new ListChangeHandler<>())
				.childrenListChangeHandler(new ListChangeHandler<>()).name("PML ID").position(position++).build());
		PML_ID_AttribList.addNew(Attribute.builder().valueListChangeHandler(new ListChangeHandler<>())
				.childrenListChangeHandler(new ListChangeHandler<>()).name("Scheme PML ID").position(position++)
				.build());
		/*
		 * PML_ID_AttribList.addNew( Attribute.builder().valueListChangeHandler(new
		 * ListChangeHandler<>()) .childrenListChangeHandler(new
		 * ListChangeHandler<>()).name("PML Name").position(position++).build());
		 */
		PR_AttribList.addNew(Attribute.builder().valueListChangeHandler(new ListChangeHandler<>())
				.childrenListChangeHandler(new ListChangeHandler<>()).name("PR01").position(position++).build());
		PR_AttribList.addNew(Attribute.builder().valueListChangeHandler(new ListChangeHandler<>())
				.childrenListChangeHandler(new ListChangeHandler<>()).name("PR02").position(position++).build());
		PR_AttribList.addNew(Attribute.builder().valueListChangeHandler(new ListChangeHandler<>())
				.childrenListChangeHandler(new ListChangeHandler<>()).name("PR03").position(position++).build());
		PR_AttribList.addNew(Attribute.builder().valueListChangeHandler(new ListChangeHandler<>())
				.childrenListChangeHandler(new ListChangeHandler<>()).name("PR04").position(position++).build());
		PR_AttribList.addNew(Attribute.builder().valueListChangeHandler(new ListChangeHandler<>())
				.childrenListChangeHandler(new ListChangeHandler<>()).name("PR05").position(position++).build());
		PR_AttribList.addNew(Attribute.builder().valueListChangeHandler(new ListChangeHandler<>())
				.childrenListChangeHandler(new ListChangeHandler<>()).name("PR06").position(position++).build());
		PR_AttribList.addNew(Attribute.builder().valueListChangeHandler(new ListChangeHandler<>())
				.childrenListChangeHandler(new ListChangeHandler<>()).name("PR07").position(position++).build());

		ProcessorAttribList.addNew(Attribute.builder().valueListChangeHandler(new ListChangeHandler<>())
				.childrenListChangeHandler(new ListChangeHandler<>()).name("Processor ID").position(position++)
				.build());
		ProcessorAttribList.addNew(Attribute.builder().valueListChangeHandler(new ListChangeHandler<>())
				.childrenListChangeHandler(new ListChangeHandler<>()).name("Processor Name").position(position++)
				.build());

		REC_AttribList.addNew(Attribute.builder().valueListChangeHandler(new ListChangeHandler<>())
				.childrenListChangeHandler(new ListChangeHandler<>()).name("REC Account ID").position(position++)
				.build());
		REC_AttribList.addNew(Attribute.builder().valueListChangeHandler(new ListChangeHandler<>())
				.childrenListChangeHandler(new ListChangeHandler<>()).name("REC Account name").position(position++)
				.build());
		REC_AttribList.addNew(Attribute.builder().valueListChangeHandler(new ListChangeHandler<>())
				.childrenListChangeHandler(new ListChangeHandler<>()).name("REC Type Name").position(position++)
				.build());
		REC_AttribList.addNew(Attribute.builder().valueListChangeHandler(new ListChangeHandler<>())
				.childrenListChangeHandler(new ListChangeHandler<>()).name("REC Type ID").position(position++).build());

		ReconciliationAttribList.addNew(Attribute.builder().valueListChangeHandler(new ListChangeHandler<>())
				.childrenListChangeHandler(new ListChangeHandler<>()).name("R01").position(position++).build());
		ReconciliationAttribList.addNew(Attribute.builder().valueListChangeHandler(new ListChangeHandler<>())
				.childrenListChangeHandler(new ListChangeHandler<>()).name("R02").position(position++).build());
		ReconciliationAttribList.addNew(Attribute.builder().valueListChangeHandler(new ListChangeHandler<>())
				.childrenListChangeHandler(new ListChangeHandler<>()).name("R03").position(position++).build());
		ReconciliationAttribList.addNew(Attribute.builder().valueListChangeHandler(new ListChangeHandler<>())
				.childrenListChangeHandler(new ListChangeHandler<>()).name("R04").position(position++).build());
		ReconciliationAttribList.addNew(Attribute.builder().valueListChangeHandler(new ListChangeHandler<>())
				.childrenListChangeHandler(new ListChangeHandler<>()).name("R05").position(position++).build());
		ReconciliationAttribList.addNew(Attribute.builder().valueListChangeHandler(new ListChangeHandler<>())
				.childrenListChangeHandler(new ListChangeHandler<>()).name("R06").position(position++).build());
		ReconciliationAttribList.addNew(Attribute.builder().valueListChangeHandler(new ListChangeHandler<>())
				.childrenListChangeHandler(new ListChangeHandler<>()).name("R07").position(position++).build());

		SchemeAttribList.addNew(Attribute.builder().valueListChangeHandler(new ListChangeHandler<>())
				.childrenListChangeHandler(new ListChangeHandler<>()).name("Scheme ID").position(position++).build());
		SchemeAttribList.addNew(Attribute.builder().valueListChangeHandler(new ListChangeHandler<>())
				.childrenListChangeHandler(new ListChangeHandler<>()).name("Scheme Platform ID").position(position++)
				.build());
		SchemeAttribList.addNew(Attribute.builder().valueListChangeHandler(new ListChangeHandler<>())
				.childrenListChangeHandler(new ListChangeHandler<>()).name("PML Type").position(position++).build());

		System_AttribList.addNew(Attribute.builder().valueListChangeHandler(new ListChangeHandler<>())
				.childrenListChangeHandler(new ListChangeHandler<>()).name("ETL Load File Name").position(position++)
				.build());
		System_AttribList.addNew(Attribute.builder().valueListChangeHandler(new ListChangeHandler<>())
				.childrenListChangeHandler(new ListChangeHandler<>()).name("ETL Load nbr").position(position++)
				.build());
		System_AttribList.addNew(Attribute.builder().valueListChangeHandler(new ListChangeHandler<>())
				.childrenListChangeHandler(new ListChangeHandler<>()).name("User name").position(position++).build());
		System_AttribList.addNew(Attribute.builder().valueListChangeHandler(new ListChangeHandler<>())
				.childrenListChangeHandler(new ListChangeHandler<>()).name("Auto Manual Reco").position(position++)
				.build());
		System_AttribList.addNew(Attribute.builder().valueListChangeHandler(new ListChangeHandler<>())
				.childrenListChangeHandler(new ListChangeHandler<>()).name("Reco name").position(position++).build());
		System_AttribList.addNew(Attribute.builder().valueListChangeHandler(new ListChangeHandler<>())
				.childrenListChangeHandler(new ListChangeHandler<>()).name("Data source").position(position++).build());
		/*
		 * System_AttribList.addNew( Attribute.builder().valueListChangeHandler(new
		 * ListChangeHandler<>()) .childrenListChangeHandler(new
		 * ListChangeHandler<>()).name("Reconciliation YES NO").position(position++).
		 * build()); System_AttribList.addNew(
		 * Attribute.builder().valueListChangeHandler(new ListChangeHandler<>())
		 * .childrenListChangeHandler(new
		 * ListChangeHandler<>()).name("Join run nbr").position(position++).build());
		 */
		ContractAndOrderAttribList.addNew(Attribute.builder().valueListChangeHandler(new ListChangeHandler<>())
				.childrenListChangeHandler(new ListChangeHandler<>()).name("Order Status").position(position++)
				.build());
		ContractAndOrderAttribList.addNew(Attribute.builder().valueListChangeHandler(new ListChangeHandler<>())
				.childrenListChangeHandler(new ListChangeHandler<>()).name("Order ID").position(position++).build());

		PricingAttribList.addNew(Attribute.builder().valueListChangeHandler(new ListChangeHandler<>())
				.childrenListChangeHandler(new ListChangeHandler<>()).name("Pricing status").position(position++)
				.build());
		PricingAttribList.addNew(Attribute.builder().valueListChangeHandler(new ListChangeHandler<>())
				.childrenListChangeHandler(new ListChangeHandler<>()).name("Volume fee type").position(position++)
				.build());
		PricingAttribList.addNew(Attribute.builder().valueListChangeHandler(new ListChangeHandler<>())
				.childrenListChangeHandler(new ListChangeHandler<>()).name("Volume fee range").position(position++)
				.build());
		PricingAttribList.addNew(Attribute.builder().valueListChangeHandler(new ListChangeHandler<>())
				.childrenListChangeHandler(new ListChangeHandler<>()).name("Fee type").position(position++).build());

		SchemeInvoiceAttribList.addNew(Attribute.builder().valueListChangeHandler(new ListChangeHandler<>())
				.childrenListChangeHandler(new ListChangeHandler<>()).name("Scheme Invoice number").position(position++)
				.build());
		SchemeInvoiceAttribList.addNew(Attribute.builder().valueListChangeHandler(new ListChangeHandler<>())
				.childrenListChangeHandler(new ListChangeHandler<>()).name("Scheme Invoice Entity ID")
				.position(position++).build());
		SchemeInvoiceAttribList.addNew(Attribute.builder().valueListChangeHandler(new ListChangeHandler<>())
				.childrenListChangeHandler(new ListChangeHandler<>()).name("Scheme Invoice ID").position(position++)
				.build());
		SchemeInvoiceAttribList.addNew(Attribute.builder().valueListChangeHandler(new ListChangeHandler<>())
				.childrenListChangeHandler(new ListChangeHandler<>()).name("Scheme Invoice Type").position(position++)
				.build());
		SchemeInvoiceAttribList.addNew(Attribute.builder().valueListChangeHandler(new ListChangeHandler<>())
				.childrenListChangeHandler(new ListChangeHandler<>()).name("Year").position(position++).build());
		SchemeInvoiceAttribList.addNew(Attribute.builder().valueListChangeHandler(new ListChangeHandler<>())
				.childrenListChangeHandler(new ListChangeHandler<>()).name("Month").position(position++).build());
		SchemeInvoiceAttribList.addNew(Attribute.builder().valueListChangeHandler(new ListChangeHandler<>())
				.childrenListChangeHandler(new ListChangeHandler<>()).name("UOM").position(position++).build());

		return models;
	}

	public Measure buildMeasure() {
		return Measure.builder().name("measureTest").childrenListChangeHandler(buildMainMeasures()).build();

	}

	public ListChangeHandler<Measure> buildMainMeasures() {
		ListChangeHandler<Measure> measures = new ListChangeHandler<>();
		int position = 0;

		Measure Posting_amount = Measure.builder().name("Posting amount").position(position)
				.childrenListChangeHandler(new ListChangeHandler<>()).build();
		measures.addNew(Posting_amount);

		Measure Financial_amount = Measure.builder().name("Financial amount").position(position++)
				.childrenListChangeHandler(new ListChangeHandler<>()).build();
		measures.addNew(Financial_amount);

		Measure Reco_measure = Measure.builder().name("Reco measure").position(position++)
				.childrenListChangeHandler(new ListChangeHandler<>()).build();
		measures.addNew(Reco_measure);
		Measure Invoice_amount = Measure.builder().name("Invoice amount (excl VAT)").position(position++)
				.childrenListChangeHandler(new ListChangeHandler<>()).build();
		measures.addNew(Invoice_amount);
		Measure Invoice_total_amount = Measure.builder().name("Invoice total amount").position(position++)
				.childrenListChangeHandler(new ListChangeHandler<>()).build();
		measures.addNew(Invoice_total_amount);
		Measure VAT_amount = Measure.builder().name("VAT amount").position(position++)
				.childrenListChangeHandler(new ListChangeHandler<>()).build();
		measures.addNew(VAT_amount);
		Measure Due_date_calculation = Measure.builder().name("Due date calculation").position(position++)
				.childrenListChangeHandler(new ListChangeHandler<>()).build();
		measures.addNew(Due_date_calculation);

		Measure Billing_driver = Measure.builder().name("Billing driver").position(position++)
				.childrenListChangeHandler(new ListChangeHandler<>()).build();
		measures.addNew(Billing_driver);

		Measure Unit_cost = Measure.builder().name("Unit cost").position(position++)
				.childrenListChangeHandler(new ListChangeHandler<>()).build();
		measures.addNew(Unit_cost);
		Measure Billing_amount = Measure.builder().name("Billing amount").position(position++)
				.childrenListChangeHandler(new ListChangeHandler<>()).build();
		measures.addNew(Billing_amount);
		Measure VAT_rate = Measure.builder().name("VAT rate").position(position++)
				.childrenListChangeHandler(new ListChangeHandler<>()).build();
		measures.addNew(VAT_rate);
		Measure PricingMeasure = Measure.builder().name("Pricing-measure").position(position++)
				.childrenListChangeHandler(new ListChangeHandler<>()).build();
		measures.addNew(PricingMeasure);

		Measure Amount_exl = Measure.builder().name("Amount exl. Tax").position(position++)
				.childrenListChangeHandler(new ListChangeHandler<>()).build();
		measures.addNew(Amount_exl);
		Measure SpecificMeasure = Measure.builder().name("Specific measure").position(position++)
				.childrenListChangeHandler(new ListChangeHandler<>()).build();
		measures.addNew(SpecificMeasure);

		Measure Driver = Measure.builder().name("Driver").position(position++)
				.childrenListChangeHandler(new ListChangeHandler<>()).build();
		measures.addNew(Driver);

		Measure Check = Measure.builder().name("Check").position(position++)
				.childrenListChangeHandler(new ListChangeHandler<>()).build();
		measures.addNew(Check);

		Measure Rate0 = Measure.builder().name("Rate0").position(position++)
				.childrenListChangeHandler(new ListChangeHandler<>()).build();
		measures.addNew(Rate0);

		Measure Amount_incl_tax = Measure.builder().name("Amount incl. tax ").position(position++)
				.childrenListChangeHandler(new ListChangeHandler<>()).build();
		measures.addNew(Amount_incl_tax);

		position = 0;
		Reco_measure.getChildrenListChangeHandler().addNew(Measure.builder().name("Remaining amount R1")
				.position(position).parent(Reco_measure).childrenListChangeHandler(new ListChangeHandler<>()).build());
		Reco_measure.getChildrenListChangeHandler()
				.addNew(Measure.builder().name("Remaining amount R2").position(position++).parent(Reco_measure)
						.childrenListChangeHandler(new ListChangeHandler<>()).build());
		Reco_measure.getChildrenListChangeHandler()
				.addNew(Measure.builder().name("Reconciled amount R5").position(position++).parent(Reco_measure)
						.childrenListChangeHandler(new ListChangeHandler<>()).build());

		Reco_measure.getChildrenListChangeHandler()
				.addNew(Measure.builder().name("Reconciled amount R4").position(position++).parent(Reco_measure)
						.childrenListChangeHandler(new ListChangeHandler<>()).build());
		Reco_measure.getChildrenListChangeHandler()
				.addNew(Measure.builder().name("Reconciled amount R7").position(position++).parent(Reco_measure)
						.childrenListChangeHandler(new ListChangeHandler<>()).build());
		Reco_measure.getChildrenListChangeHandler()
				.addNew(Measure.builder().name("Reconciled amount R3").position(position++).parent(Reco_measure)
						.childrenListChangeHandler(new ListChangeHandler<>()).build());
		Reco_measure.getChildrenListChangeHandler()
				.addNew(Measure.builder().name("Reconciled amount R6").position(position++).parent(Reco_measure)
						.childrenListChangeHandler(new ListChangeHandler<>()).build());
		Reco_measure.getChildrenListChangeHandler()
				.addNew(Measure.builder().name("Reconciled amount R2").position(position++).parent(Reco_measure)
						.childrenListChangeHandler(new ListChangeHandler<>()).build());
		Reco_measure.getChildrenListChangeHandler()
				.addNew(Measure.builder().name("Reconciled amount R1").position(position++).parent(Reco_measure)
						.childrenListChangeHandler(new ListChangeHandler<>()).build());
		Reco_measure.getChildrenListChangeHandler()
				.addNew(Measure.builder().name("Remaining amount R7").position(position++).parent(Reco_measure)
						.childrenListChangeHandler(new ListChangeHandler<>()).build());
		Reco_measure.getChildrenListChangeHandler()
				.addNew(Measure.builder().name("Remaining amount R6").position(position++).parent(Reco_measure)
						.childrenListChangeHandler(new ListChangeHandler<>()).build());
		Reco_measure.getChildrenListChangeHandler()
				.addNew(Measure.builder().name("Remaining amount R5").position(position++).parent(Reco_measure)
						.childrenListChangeHandler(new ListChangeHandler<>()).build());
		Reco_measure.getChildrenListChangeHandler()
				.addNew(Measure.builder().name("Remaining amount R4").position(position++).parent(Reco_measure)
						.childrenListChangeHandler(new ListChangeHandler<>()).build());
		Reco_measure.getChildrenListChangeHandler()
				.addNew(Measure.builder().name("Remaining amount R3").position(position++).parent(Reco_measure)
						.childrenListChangeHandler(new ListChangeHandler<>()).build());

		position = 0;
		PricingMeasure.getChildrenListChangeHandler().addNew(Measure.builder().name("Fee amount").position(position)
				.parent(PricingMeasure).childrenListChangeHandler(new ListChangeHandler<>()).build());
		PricingMeasure.getChildrenListChangeHandler()
				.addNew(Measure.builder().name("Range minimum amount").position(position++).parent(PricingMeasure)
						.childrenListChangeHandler(new ListChangeHandler<>()).build());
		PricingMeasure.getChildrenListChangeHandler()
				.addNew(Measure.builder().name("Range maximum amount").position(position++).parent(PricingMeasure)
						.childrenListChangeHandler(new ListChangeHandler<>()).build());
		PricingMeasure.getChildrenListChangeHandler()
				.addNew(Measure.builder().name("Minimum fee amount").position(position++).parent(PricingMeasure)
						.childrenListChangeHandler(new ListChangeHandler<>()).build());

		position = 0;
		SpecificMeasure.getChildrenListChangeHandler().addNew(Measure.builder().name("Count").position(position)
				.parent(SpecificMeasure).childrenListChangeHandler(new ListChangeHandler<>()).build());

		SpecificMeasure.getChildrenListChangeHandler()
				.addNew(Measure.builder().name("VSS amount D").position(position++).parent(SpecificMeasure)
						.childrenListChangeHandler(new ListChangeHandler<>()).build());

		SpecificMeasure.getChildrenListChangeHandler()
				.addNew(Measure.builder().name("VSS amount C").position(position++).parent(SpecificMeasure)
						.childrenListChangeHandler(new ListChangeHandler<>()).build());

		SpecificMeasure.getChildrenListChangeHandler()
				.addNew(Measure.builder().name("M109_Amount Reconciliation").position(position++)
						.parent(SpecificMeasure).childrenListChangeHandler(new ListChangeHandler<>()).build());

		return measures;

	}

	public Model buildFullModel() {
		Model model = buildModel("B-Cephal Model", 0);
		model.getEntityListChangeHandler().addNew(buildEntity(null));
		return model;
	}

	public PeriodName buildPeriod(String name, int position) {
		return PeriodName.builder().name(name).position(position).build();
	}

	Measure buildMeasure(String name, int position) {
		return Measure.builder().name(name).position(position).build();
	}

	Attribute buildAttribute(String name, int position) {
		return Attribute.builder().name(name).position(position).build();
	}

	AttributeValue buildAttributeValue(String name, int position) {
		return AttributeValue.builder().name(name).position(position).build();
	}

	Model buildModel(String name, int position) {
		return Model.builder().name(name).position(position).build();
	}

	Entity buildEntity(String name) {
		return Entity.builder().name(name).build();
	}

	public Entity buildEntity() {
		Entity entity = new Entity();
		entity.setName("entityName");
		return entity;
	}

	public CalendarCategory buildCalendarCategory() {
		CalendarCategory calendarCategory = new CalendarCategory();
		calendarCategory.setName("calendarName");

		return calendarCategory;
	}
}
