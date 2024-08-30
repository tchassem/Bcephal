using System;
using System.Collections.Generic;
using System.Collections.ObjectModel;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Bcephal.Models.Billing.Model
{
    public class BillingModelPeriodGranularity
    {
        
        public static BillingModelPeriodGranularity WEEK = new BillingModelPeriodGranularity("WEEK", "Week");
        public static BillingModelPeriodGranularity MONTH = new BillingModelPeriodGranularity("MONTH", "Month");
        public static BillingModelPeriodGranularity QUARTER = new BillingModelPeriodGranularity("QUARTER", "Quarter");
        public static BillingModelPeriodGranularity YEAR = new BillingModelPeriodGranularity("YEAR", "Year");

        public String label;
        public String code;

        public BillingModelPeriodGranularity(String code, String label)
        {
            this.code = code;
            this.label = label;
        }

        public String getCode()
        {
            return code;
        }

        public bool IsWeek()
        {
            return this == WEEK;
        }

        public bool IsMonth()
        {
            return this == MONTH;
        }

        public bool IsQuarter()
        {
            return this == QUARTER;
        }

        public bool IsYear()
        {
            return this == YEAR;
        }

        public override String ToString()
        {
            return label;
        }

        public static BillingModelPeriodGranularity GetByCode(String code)
        {
            if (code == null) return null;
            if (WEEK.code.Equals(code)) return WEEK;
            if (MONTH.code.Equals(code)) return MONTH;
            if (QUARTER.code.Equals(code)) return QUARTER;
            if (YEAR.code.Equals(code)) return YEAR;
            return null;
        }

        public static ObservableCollection<BillingModelPeriodGranularity> GetGranularities()
        {
            ObservableCollection<BillingModelPeriodGranularity> conditions = new ObservableCollection<BillingModelPeriodGranularity>();
            conditions.Add(WEEK);
            conditions.Add(MONTH);
            conditions.Add(QUARTER);
            conditions.Add(YEAR);
            return conditions;
        }

    }

    public static class BillingModelPeriodGranularityExtention
    {
        public static ObservableCollection<string> GetAll(this BillingModelPeriodGranularity billingModelPeriodGranularity, Func<string, string> Localize)
        {
            ObservableCollection<string> operators = new ObservableCollection<string>();
            operators.Add(Localize?.Invoke("WEEK"));
            operators.Add(Localize?.Invoke("MONTH"));
            operators.Add(Localize?.Invoke("QUARTER"));
            operators.Add(Localize?.Invoke("YEAR"));
            return operators;
        }

        public static string GetText(this BillingModelPeriodGranularity billingModelPeriodGranularity, Func<string, string> Localize)
        {
            if (billingModelPeriodGranularity.IsWeek())
            {
                return Localize?.Invoke("WEEK");
            }
            if (billingModelPeriodGranularity.IsMonth())
            {
                return Localize?.Invoke("MONTH");
            }
            if (billingModelPeriodGranularity.IsQuarter())
            {
                return Localize?.Invoke("QUARTER");
            }
            if (billingModelPeriodGranularity.IsYear())
            {
                return Localize?.Invoke("YEAR");
            }
            return null;
        }

        public static BillingModelPeriodGranularity GetBillingModelPeriodGranularity(this BillingModelPeriodGranularity billingModelPeriodGranularity, string text, Func<string, string> Localize)
        {
            if (!string.IsNullOrWhiteSpace(text))
            {
                if (text.Equals(Localize?.Invoke("WEEK")))
                {
                    return BillingModelPeriodGranularity.WEEK;
                }
                if (text.Equals(Localize?.Invoke("MONTH")))
                {
                    return BillingModelPeriodGranularity.MONTH;
                }
                if (text.Equals(Localize?.Invoke("QUARTER")))
                {
                    return BillingModelPeriodGranularity.QUARTER;
                }
                if (text.Equals(Localize?.Invoke("YEAR")))
                {
                    return BillingModelPeriodGranularity.YEAR;
                }
            }
            return BillingModelPeriodGranularity.WEEK;
        }
    }
}
