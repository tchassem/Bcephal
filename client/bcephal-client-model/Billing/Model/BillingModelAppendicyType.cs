using System;
using System.Collections.Generic;
using System.Collections.ObjectModel;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Bcephal.Models.Billing.Model
{
    public class BillingModelAppendicyType
    {

        public static BillingModelAppendicyType GRID = new BillingModelAppendicyType("GRID", "Grid");
        public static BillingModelAppendicyType SUB_INVOICE = new BillingModelAppendicyType("SUB_INVOICE", "Sub Invoice");

        public String label;
        public String code;

        public BillingModelAppendicyType(String code, String label)
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

        public static BillingModelAppendicyType GetByCode(String code)
        {
            if (code == null) return null;
            if (GRID.code.Equals(code)) return GRID;
            if (SUB_INVOICE.code.Equals(code)) return SUB_INVOICE;
            return null;
        }

        public static ObservableCollection<BillingModelAppendicyType> GetAll()
        {
            ObservableCollection<BillingModelAppendicyType> conditions = new ObservableCollection<BillingModelAppendicyType>();
            conditions.Add(GRID);
            conditions.Add(SUB_INVOICE);
            return conditions;
        }

    }
}
