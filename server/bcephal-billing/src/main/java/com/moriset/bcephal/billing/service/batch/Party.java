package com.moriset.bcephal.billing.service.batch;

public class Party {

	public String number;
	public String internalNumber;
	public String departmentNumber;
	public String departmentInternalNumber;
	public String name;
	public String doingBusinessAs;
	public String vatNumber;
	public String legalForm;
	public String language;
	public String street;
	public String postalcode;
	public String city;
	public String country;
	public String email;
	public String emailCc;
	public String phone;
	public String contactTitle;
	public String contactFirstname;
	public String contactLastname;
	
	
	public String getName() {
		if(name == null) {
			name = "";
		}
		return name;
	}
	
	
	public static Party BuildClient(BillingContext context, Object[] datas) {
		Party party = new Party();
		party.number = context.clientIdPosition != null ? (String)datas[context.clientIdPosition] : "";
		party.internalNumber = context.clientInternalNumberPosition != null ? (String)datas[context.clientInternalNumberPosition] : "";
		party.departmentNumber = context.clientDepartmentNumberPosition != null ? (String)datas[context.clientDepartmentNumberPosition] : "";
		party.departmentInternalNumber = context.clientDepartmentInternalNumberPosition != null ? (String)datas[context.clientDepartmentInternalNumberPosition] : "";
    	party.name = context.clientNamePosition != null ? (String)datas[context.clientNamePosition] : "";
    	party.doingBusinessAs = context.clientDoingBusinessAsPosition != null ? (String)datas[context.clientDoingBusinessAsPosition] : "";
    	party.street = context.clientAdressStreetPosition != null ? (String)datas[context.clientAdressStreetPosition] : "";
    	party.postalcode = context.clientAdressPostalCodePosition != null ? (String)datas[context.clientAdressPostalCodePosition] : "";
    	party.city = context.clientAdressCityPosition != null ? (String)datas[context.clientAdressCityPosition] : "";
    	party.country = context.clientAdressCountryPosition != null ? (String)datas[context.clientAdressCountryPosition] : "";
    	party.phone = context.clientPhonePosition != null ?(String)datas[context.clientPhonePosition] : "";
    	party.email = context.clientEmailPosition != null ?(String)datas[context.clientEmailPosition] : "";
    	party.emailCc = context.clientEmailCcPosition != null ?(String)datas[context.clientEmailCcPosition] : "";
    	party.vatNumber = context.clientVatNumberPosition != null ? (String)datas[context.clientVatNumberPosition] : "";
    	party.legalForm = context.clientLegalFormPosition != null ? (String)datas[context.clientLegalFormPosition] : "";
    	party.language = context.clientLanguagePosition != null ? (String)datas[context.clientLanguagePosition] : "";
    	party.contactLastname = context.clientContactLastNamePosition != null ? (String)datas[context.clientContactLastNamePosition] : "";
    	party.contactTitle = context.clientContactTitlePosition != null ? (String)datas[context.clientContactTitlePosition] : "";
    	party.contactFirstname = context.clientContactFirstnamePosition != null ? (String)datas[context.clientContactFirstnamePosition] : "";
		return party;
	}
	
}
