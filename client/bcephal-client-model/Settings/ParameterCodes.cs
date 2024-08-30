using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Bcephal.Models.Settings
{
    public class ParameterCodes
    {

        #region initiation

        public static string INITIATION = "initiation";
        public static string INITIATION_DEFAULT_MODEL = INITIATION + ".default.model";
        public static string INITIATION_DEFAULT_ENTITY = INITIATION + ".default.entity";

        public static string INITIATION_FILE_LOADER = INITIATION + ".file.loader";
        public static string INITIATION_FILE_LOADER_FILE_ATTRIBUTE = INITIATION_FILE_LOADER + ".file.attribute";
        public static string INITIATION_FILE_LOADER_LOAD_NBR_ATTRIBUTE = INITIATION_FILE_LOADER + ".load.nbr.attribute";
        public static string INITIATION_FILE_LOADER_LOAD_NBR_SEQUENCE = INITIATION_FILE_LOADER + ".load.nbr.sequence";

        #endregion


        #region Reconciliation

        public static string RECONCILIATION = "reconciliation";
        public static string RECONCILIATION_RECO_TYPE_BLOCK = RECONCILIATION + ".reco.type.entity";
        public static string RECONCILIATION_DC_ATTRIBUTE = RECONCILIATION + ".dc.attribute";
        public static string RECONCILIATION_DEBIT_VALUE = RECONCILIATION + ".debit.value";
        public static string RECONCILIATION_CREDIT_VALUE = RECONCILIATION + ".credit.value";

        public static string RECONCILIATION_USER_ATTRIBUTE = RECONCILIATION + ".user.attribute";
        public static string RECONCILIATION_AUTO_MANUAL_ATTRIBUTE = RECONCILIATION + ".auto.manual.attribute";
        public static string RECONCILIATION_AUTOMATIC_VALUE = RECONCILIATION + ".automatic.value";
        public static string RECONCILIATION_MANUAL_VALUE = RECONCILIATION + ".manual.value";

        public static string RECONCILIATION_NOTE_ATTRIBUTE = RECONCILIATION + ".note.attribute";
        public static string RECONCILIATION_ON_HOLD_ATTRIBUTE = RECONCILIATION + ".on.hold.attribute";

        public static string RECONCILIATION_MAX_DURATION_PER_LINE = RECONCILIATION + ".max.duration.per.line";

        #endregion


        #region Billing

        public static string BILLING = "billing";

        public static string BILLING_ROLE = BILLING + ".role";
        public static string BILLING_ROLE_GRID = BILLING_ROLE + ".grid";

        public static string BILLING_ROLE_CLIENT = BILLING_ROLE + ".client";
        public static string BILLING_ROLE_CLIENT_ID_ATTRIBUTE = BILLING_ROLE_CLIENT + ".id.attribute";
        public static string BILLING_ROLE_CLIENT_NAME_ATTRIBUTE = BILLING_ROLE_CLIENT + ".name.attribute";
        public static string BILLING_ROLE_CLIENT_CONTACT_ATTRIBUTE = BILLING_ROLE_CLIENT + ".contact.attribute";

        public static string BILLING_ROLE_CLIENT_DEPARTMENT_NUMBER_ATTRIBUTE = BILLING_ROLE_CLIENT + ".department.number.attribute";
        public static string BILLING_ROLE_CLIENT_DEPARTMENT_NAME_ATTRIBUTE = BILLING_ROLE_CLIENT + ".department.name.attribute";

        public static string BILLING_ROLE_BILLING_COMPANY = BILLING_ROLE + ".billing.company";
        public static string BILLING_ROLE_BILLING_COMPANY_ID_ATTRIBUTE = BILLING_ROLE_BILLING_COMPANY + ".id.attribute";
        public static string BILLING_ROLE_BILLING_COMPANY_NAME_ATTRIBUTE = BILLING_ROLE_BILLING_COMPANY + ".name.attribute";



        public static string BILLING_ROLE_LEGAL_FORM_ATTRIBUTE = BILLING_ROLE + ".legal.form.attribute";

        public static string BILLING_ROLE_ADRESS_STREET_ATTRIBUTE = BILLING_ROLE + ".adress.street.attribute";
        public static string BILLING_ROLE_ADRESS_POSTAL_CODE_ATTRIBUTE = BILLING_ROLE + ".adress.postalcode.attribute";
        public static string BILLING_ROLE_ADRESS_CITY_ATTRIBUTE = BILLING_ROLE + ".adress.city.attribute";
        public static string BILLING_ROLE_ADRESS_COUNTRY_ATTRIBUTE = BILLING_ROLE + ".adress.country.attribute";

        public static string BILLING_ROLE_PHONE_ATTRIBUTE = BILLING_ROLE + ".phone.attribute";
        public static string BILLING_ROLE_EMAIL_ATTRIBUTE = BILLING_ROLE + ".email.attribute";

        public static string BILLING_ROLE_VAT_NUMBER_ATTRIBUTE = BILLING_ROLE + ".vat.number.attribute";

        public static string BILLING_ROLE_DEFAULT_LANGUAGE_ATTRIBUTE = BILLING_ROLE + ".default.language.attribute";




        public static string BILLING_EVENT = BILLING + ".event";
        //public static string BILLING_EVENT_NUMBER_ATTRIBUTE = BILLING_EVENT + ".number.attribute";
        //public static string BILLING_EVENT_NUMBER_SEQUENCE = BILLING_EVENT + ".number.sequence";
        public static string BILLING_EVENT_NAME_ATTRIBUTE = BILLING_EVENT + ".name.attribute";
        public static string BILLING_EVENT_CATEGORY_ATTRIBUTE = BILLING_EVENT + ".category.attribute";
        public static string BILLING_EVENT_DESCRIPTION_ATTRIBUTE = BILLING_EVENT + ".description.attribute";

        public static string BILLING_EVENT_STATUS_ATTRIBUTE = BILLING_EVENT + ".status.attribute";
        public static string BILLING_EVENT_STATUS_DRAFT_VALUE = BILLING_EVENT + ".status.draft.value";
        public static string BILLING_EVENT_STATUS_FROZEN_VALUE = BILLING_EVENT + ".status.frozen.value";
        public static string BILLING_EVENT_STATUS_BILLED_VALUE = BILLING_EVENT + ".status.billed.value";

        public static string BILLING_EVENT_AM_ATTRIBUTE = BILLING_EVENT + ".am.attribute";
        public static string BILLING_EVENT_AM_AUTO_VALUE = BILLING_EVENT + ".am.auto.value";
        public static string BILLING_EVENT_AM_MANUAL_VALUE = BILLING_EVENT + ".am.manual.value";

        //public static string BILLING_EVENT_DC_ATTRIBUTE = BILLING_EVENT + ".dc.attribute";
        //public static string BILLING_EVENT_DC_DEBIT_VALUE = BILLING_EVENT + ".dc.debit.value";
        //public static string BILLING_EVENT_DC_CREDIT_VALUE = BILLING_EVENT + ".dc.credit.value";

        public static string BILLING_EVENT_DRIVER_NAME_ATTRIBUTE = BILLING_EVENT + ".driver.name.attribute";

        public static string BILLING_EVENT_TYPE_ATTRIBUTE = BILLING_EVENT + ".type.attribute";
        public static string BILLING_EVENT_TYPE_INVOICE_VALUE = BILLING_EVENT + ".type.invopice.value";
        public static string BILLING_EVENT_TYPE_CREDIT_NOTE_VALUE = BILLING_EVENT + ".type.credit.note.value";

        public static string BILLING_RUN_ATTRIBUTE = BILLING_EVENT + ".run.attribute";
        public static string BILLING_RUN_TYPE_ATTRIBUTE = BILLING_EVENT + ".run.type.attribute";

        //public static String BILLING_EVENT_BILLING_DRIVER_NAME_ATTRIBUTE = BILLING_EVENT + ".billing.driver.name.attribute";

        public static string BILLING_EVENT_BILLING_DRIVER_MEASURE = BILLING_EVENT + ".billing.driver.measure";
        public static string BILLING_EVENT_UNIT_COST_MEASURE = BILLING_EVENT + ".unit.cost.measure";
        public static string BILLING_EVENT_BILLING_AMOUNT_MEASURE = BILLING_EVENT + ".billing_amount.measure";
        public static string BILLING_EVENT_VAT_RATE_MEASURE = BILLING_EVENT + ".vat.rate.measure";

        public static string BILLING_EVENT_DATE_PERIOD = BILLING_EVENT + ".date.period";
        public static string BILLING_RUN_DATE_PERIOD = BILLING_EVENT + ".run.date.period";

        public static string BILLING_EVENT_REPOSITORY_GRID = BILLING_EVENT + ".repository.grid";
        public static string BILLING_EVENT_GRID = BILLING_EVENT + ".grid";
        public static string BILLING_EVENT_MANUEL_BILLING_EVENT_DYNAMIC_FORM = BILLING_EVENT + ".manual.billing.event.dinamic.form";


        public static string BILLING_INVOICE = BILLING + ".invoice";
        public static string BILLING_INVOICE_NUMBER_ATTRIBUTE = BILLING_INVOICE + ".number.attribute";
        public static string BILLING_INVOICE_SUB_INVOICE_NUMBER_ATTRIBUTE = BILLING_INVOICE + "sub.invoice.number.attribute";
        public static string BILLING_INVOICE_NUMBER_SEQUENCE = BILLING_INVOICE + ".number.sequence";
        public static string BILLING_INVOICE_STATUS_ATTRIBUTE = BILLING_INVOICE + ".status.attribute";
        public static string BILLING_INVOICE_STATUS_DRAFT_VALUE = BILLING_INVOICE + ".status.draft.value";
        public static string BILLING_INVOICE_STATUS_VALIDATED_VALUE = BILLING_INVOICE + ".status.validated.value";
        public static string BILLING_INVOICE_STATUS_TO_CHECK_VALUE = BILLING_INVOICE + ".status.tocheck.value";
        public static string BILLING_INVOICE_COMMUNICATION_MESSAGE_ATTRIBUTE = BILLING_INVOICE + ".communication.message.attribute";

        public static string BILLING_INVOICE_SENDING_STATUS_ATTRIBUTE = BILLING_INVOICE + ".sending.status.attribute";
        public static string BILLING_INVOICE_SENDING_STATUS_SENT_VALUE = BILLING_INVOICE + ".sending.status.sent.value";
        public static string BILLING_INVOICE_SENDING_STATUS_NOT_YET_SENT_VALUE = BILLING_INVOICE + ".sending.status.notyetsent.value";

        public static string BILLING_INVOICE_AMOUNT_WITHOUT_VAT_MEASURE = BILLING_INVOICE + ".billed.amount.without.vat.measure";
        public static string BILLING_INVOICE_VAT_AMOUNT_MEASURE = BILLING_INVOICE + ".vat.amount.measure";
        public static string BILLING_INVOICE_TOTAL_AMOUNT_MEASURE = BILLING_INVOICE + ".total.amount.measure";

        public static string BILLING_INVOICE_DATE_PERIOD = BILLING_INVOICE + ".date.period";
        public static string BILLING_INVOICE_DUE_DATE_PERIOD = BILLING_INVOICE + ".due.date.period";
        public static string BILLING_INVOICE_DUE_DATE_CALCULATION_MEASURE = BILLING_INVOICE + ".due.date.calculation.measure";


        public static string BILLING_CREDIT_NOTE = BILLING + ".credit.note";
        public static string BILLING_CREDIT_NOTE_NUMBER_ATTRIBUTE = BILLING_CREDIT_NOTE + ".number.attribute";
        public static string BILLING_CREDIT_NOTE_NUMBER_SEQUENCE = BILLING_CREDIT_NOTE + ".number.sequence";

        public static string BILLING_INVOICE_DEFAULT_TEMPLATE = BILLING_INVOICE + ".default.template";


        #endregion

    }
}
