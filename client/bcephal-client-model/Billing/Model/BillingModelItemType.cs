using System;
using System.Collections.Generic;
using System.Collections.ObjectModel;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Bcephal.Models.Billing.Model
{
    public class BillingModelItemType
    {

        public static BillingModelItemType EVENT_TYPE = new BillingModelItemType("EVENT_TYPE", "Type");
        public static BillingModelItemType EVENT_CATEGORY = new BillingModelItemType("EVENT_CATEGORY", "Category");
        public static BillingModelItemType CLIENT = new BillingModelItemType("CLIENT", "Client");
        public static BillingModelItemType CLIENT_GROUP = new BillingModelItemType("CLIENT_GROUP", "Client Group");

        public String label;
        public String code;

        public BillingModelItemType(String code, String label)
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

        public static BillingModelItemType GetByCode(String code)
        {
            if (code == null) return null;
            if (EVENT_TYPE.code.Equals(code)) return EVENT_TYPE;
            if (EVENT_CATEGORY.code.Equals(code)) return EVENT_CATEGORY;
            if (CLIENT.code.Equals(code)) return CLIENT;
            if (CLIENT_GROUP.code.Equals(code)) return CLIENT_GROUP;
            return null;
        }

        public static ObservableCollection<BillingModelItemType> GetTypes()
        {
            ObservableCollection<BillingModelItemType> conditions = new ObservableCollection<BillingModelItemType>();
            conditions.Add(EVENT_TYPE);
            conditions.Add(EVENT_CATEGORY);
            conditions.Add(CLIENT);
            conditions.Add(CLIENT_GROUP);
            return conditions;
        }

    }
}
