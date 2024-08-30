using Newtonsoft.Json;
using Newtonsoft.Json.Converters;
using System;
using System.Collections.Generic;
using System.Collections.ObjectModel;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Bcephal.Models.Grids
{
    [JsonConverter(typeof(StringEnumConverter))]
    public enum PeriodGrouping
    {
        DAY_OF_WEEK, 
        DAY_OF_MONTH, 
        WEEK, MONTH, 
        QUARTER, 
        YEAR
    }
    public static class PeriodGroupingExtensionMethods
    {

        public static ObservableCollection<PeriodGrouping> GetAll(this PeriodGrouping periodGrouping)
        {
            ObservableCollection<PeriodGrouping> periodGroupings = new ObservableCollection<PeriodGrouping>
            {
                PeriodGrouping.DAY_OF_WEEK,
                PeriodGrouping.DAY_OF_MONTH,
                PeriodGrouping.WEEK,
                PeriodGrouping.MONTH,
                PeriodGrouping.QUARTER,
                PeriodGrouping.YEAR
            };
            return periodGroupings;
        }


        public static ObservableCollection<string> GetAll(this PeriodGrouping periodGrouping, Func<string, string> Localize)
        {
            ObservableCollection<string> periodGroupings = new ObservableCollection<string>
            {
                null,
                Localize?.Invoke("DAY_OF_WEEK"),
                Localize?.Invoke("DAY_OF_MONTH"),
                Localize?.Invoke("WEEK"),
                Localize?.Invoke("MONTH"),
                Localize?.Invoke("QUARTER"),
                Localize?.Invoke("YEAR")
            };
            return periodGroupings;
        }

        public static string GetText(this PeriodGrouping PeriodGrouping, Func<string, string> Localize)
        {
            if (PeriodGrouping.DAY_OF_WEEK.Equals(PeriodGrouping))
            {
                return Localize?.Invoke("DAY_OF_WEEK");
            }
            if (PeriodGrouping.DAY_OF_MONTH.Equals(PeriodGrouping))
            {
                return Localize?.Invoke("DAY_OF_MONTH");
            }
            if (PeriodGrouping.WEEK.Equals(PeriodGrouping))
            {
                return Localize?.Invoke("WEEK");
            }
            if (PeriodGrouping.MONTH.Equals(PeriodGrouping))
            {
                return Localize?.Invoke("MONTH");
            }
            if (PeriodGrouping.QUARTER.Equals(PeriodGrouping))
            {
                return Localize?.Invoke("QUARTER");
            }
            if (PeriodGrouping.YEAR.Equals(PeriodGrouping))
            {
                return Localize?.Invoke("YEAR");
            }
            return null;
        }

        public static PeriodGrouping GetJoinColumnCategory(this PeriodGrouping PeriodGrouping, string text, Func<string, string> Localize)
        {
            if (!string.IsNullOrWhiteSpace(text))
            {
                if (text.Equals(Localize?.Invoke("DAY_OF_WEEK")))
                {
                    return PeriodGrouping.DAY_OF_WEEK;
                }
                if (text.Equals(Localize?.Invoke("DAY_OF_MONTH")))
                {
                    return PeriodGrouping.DAY_OF_MONTH;
                }
                if (text.Equals(Localize?.Invoke("WEEK")))
                {
                    return PeriodGrouping.WEEK;
                }
                if (text.Equals(Localize?.Invoke("MONTH")))
                {
                    return PeriodGrouping.MONTH;
                }
                if (text.Equals(Localize?.Invoke("QUARTER")))
                {
                    return PeriodGrouping.QUARTER;
                }
                if (text.Equals(Localize?.Invoke("YEAR")))
                {
                    return PeriodGrouping.YEAR;
                }

            }
            return PeriodGrouping.DAY_OF_WEEK;
        }

    }
}
