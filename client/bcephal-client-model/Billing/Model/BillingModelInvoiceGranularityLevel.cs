using System;
using System.Collections.Generic;
using System.Collections.ObjectModel;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Bcephal.Models.Billing.Model
{
    public class BillingModelInvoiceGranularityLevel
    {

        public static BillingModelInvoiceGranularityLevel EVENT = new BillingModelInvoiceGranularityLevel("EVENT", "Event");
        public static BillingModelInvoiceGranularityLevel CATEGORY = new BillingModelInvoiceGranularityLevel("CATEGORY", "Category");
        public static BillingModelInvoiceGranularityLevel NO_CONSOLIDATION = new BillingModelInvoiceGranularityLevel("NO_CONSOLIDATION", "No Consolidation");
        public static BillingModelInvoiceGranularityLevel CUSTOM = new BillingModelInvoiceGranularityLevel("CUSTOM", "Custom");

        public String label;
        public String code;

        public BillingModelInvoiceGranularityLevel(String code, String label)
        {
            this.code = code;
            this.label = label;
        }

        public String getCode()
        {
            return code;
        }


        public override String ToString()
        {
            return label;
        }

        public static BillingModelInvoiceGranularityLevel GetByCode(String code)
        {
            if (code == null) return null;
            if (EVENT.code.Equals(code)) return EVENT;
            if (CATEGORY.code.Equals(code)) return CATEGORY;
            if (NO_CONSOLIDATION.code.Equals(code)) return NO_CONSOLIDATION;
            if (CUSTOM.code.Equals(code)) return CUSTOM;
            return null;
        }

        public static ObservableCollection<BillingModelInvoiceGranularityLevel> GetLevels()
        {
            ObservableCollection<BillingModelInvoiceGranularityLevel> conditions = new ObservableCollection<BillingModelInvoiceGranularityLevel>();
            conditions.Add(EVENT);
            conditions.Add(CATEGORY);
            conditions.Add(NO_CONSOLIDATION);
            conditions.Add(CUSTOM);
            return conditions;
        }

    }
}
