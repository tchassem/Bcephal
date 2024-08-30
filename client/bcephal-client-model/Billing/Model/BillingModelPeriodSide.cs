using System;
using System.Collections.Generic;
using System.Collections.ObjectModel;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Bcephal.Models.Billing.Model
{
    public class BillingModelPeriodSide
    {

        public static BillingModelPeriodSide ALL = new BillingModelPeriodSide("ALL", "All");
        public static BillingModelPeriodSide CURRENT = new BillingModelPeriodSide("CURRENT", "Current");
        public static BillingModelPeriodSide PREVIOUS = new BillingModelPeriodSide("PREVIOUS", "Previous");
        public static BillingModelPeriodSide INTERVAL = new BillingModelPeriodSide("INTERVAL", "Interval");

        public String label;
        public String code;

        public BillingModelPeriodSide(String code, String label)
        {
            this.code = code;
            this.label = label;
        }

        public String getCode()
        {
            return code;
        }

        public bool IsAll()
        {
            return this == ALL;
        }

        public bool IsCurrent()
        {
            return this == CURRENT;
        }

        public bool IsPrevious()
        {
            return this == PREVIOUS;
        }

        public bool IsInterval()
        {
            return this == INTERVAL;
        }
        public override String ToString()
        {
            return label;
        }

        public static BillingModelPeriodSide GetByCode(String code)
        {
            if (code == null) return null;
            if (ALL.code.Equals(code)) return ALL;
            if (CURRENT.code.Equals(code)) return CURRENT;
            if (PREVIOUS.code.Equals(code)) return PREVIOUS;
            if (INTERVAL.code.Equals(code)) return INTERVAL;
            return null;
        }

        public static ObservableCollection<BillingModelPeriodSide> GetSides()
        {
            ObservableCollection<BillingModelPeriodSide> conditions = new ObservableCollection<BillingModelPeriodSide>();
            conditions.Add(ALL);
            conditions.Add(CURRENT);
            conditions.Add(PREVIOUS);
            conditions.Add(INTERVAL);
            return conditions;
        }

    }

    public static class BillingModelPeriodSideExtention
    {
        public static ObservableCollection<string> GetAll(this BillingModelPeriodSide billingModelPeriodSide, Func<string, string> Localize)
        {
            ObservableCollection<string> operators = new ObservableCollection<string>();
            operators.Add(Localize?.Invoke("all"));
            operators.Add(Localize?.Invoke("current"));
            operators.Add(Localize?.Invoke("previous"));
            operators.Add(Localize?.Invoke("interval"));
            return operators;
        }

        public static string GetText(this BillingModelPeriodSide billingModelPeriodSide, Func<string, string> Localize)
        {
            if (billingModelPeriodSide.IsAll())
            {
                return Localize?.Invoke("all");
            }
            if (billingModelPeriodSide.IsCurrent())
            {
                return Localize?.Invoke("current");
            }
            if (billingModelPeriodSide.IsPrevious())
            {
                return Localize?.Invoke("previous");
            }
            if (billingModelPeriodSide.IsInterval())
            {
                return Localize?.Invoke("interval");
            }
            return null;
        }

        public static BillingModelPeriodSide GetBillingModelPeriodSide(this BillingModelPeriodSide billingModelPeriodSide, string text, Func<string, string> Localize)
        {
            if (!string.IsNullOrWhiteSpace(text))
            {
                if (text.Equals(Localize?.Invoke("all")))
                {
                    return BillingModelPeriodSide.ALL;
                }
                if (text.Equals(Localize?.Invoke("current")))
                {
                    return BillingModelPeriodSide.CURRENT;
                }
                if (text.Equals(Localize?.Invoke("previous")))
                {
                    return BillingModelPeriodSide.PREVIOUS;
                }
                if (text.Equals(Localize?.Invoke("interval")))
                {
                    return BillingModelPeriodSide.INTERVAL;
                }
            }
            return BillingModelPeriodSide.ALL;
        }
    }

}
