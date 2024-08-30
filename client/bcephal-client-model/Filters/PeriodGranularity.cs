using Newtonsoft.Json;
using Newtonsoft.Json.Converters;
using System;
using System.Collections.Generic;
using System.Collections.ObjectModel;
using System.ComponentModel.DataAnnotations;
using System.Linq;
using System.Reflection;
using System.Text;

namespace Bcephal.Models.Filters
{
    [JsonConverter(typeof(StringEnumConverter))]
    public enum PeriodGranularity
    {
        DAY,
        WEEK,
        MONTH,
        YEAR,
    }

    public static class PeriodGranularityExtensionMethods
    {
        public static ObservableCollection<PeriodGranularity> GetAll(this PeriodGranularity granularity)
        {
            ObservableCollection<PeriodGranularity> operators = new ObservableCollection<PeriodGranularity>();
            operators.Add(PeriodGranularity.DAY);
            operators.Add(PeriodGranularity.WEEK);
            operators.Add(PeriodGranularity.MONTH);
            operators.Add(PeriodGranularity.YEAR);
            return operators;
        }

        public static ObservableCollection<PeriodGranularity?> GetAll(this PeriodGranularity? granularity)
        {
            ObservableCollection<PeriodGranularity?> operators = new ObservableCollection<PeriodGranularity?>();
            operators.Add(null);
            operators.Add(PeriodGranularity.DAY);
            operators.Add(PeriodGranularity.WEEK);
            operators.Add(PeriodGranularity.MONTH);
            operators.Add(PeriodGranularity.YEAR);
            return operators;
        }


        public static ObservableCollection<string> GetAll(this PeriodGranularity periodGranularity, Func<string, string> Localize)
        {
            ObservableCollection<string> operators = new ObservableCollection<string>();
            operators.Add(null);
            operators.Add(Localize?.Invoke("DAY"));
            operators.Add(Localize?.Invoke("WEEK"));
            operators.Add(Localize?.Invoke("MONTH"));
            operators.Add(Localize?.Invoke("YEAR"));
            return operators;
        }

        public static string GetText(this PeriodGranularity periodGranularity, Func<string, string> Localize)
        {
            if (PeriodGranularity.DAY.Equals(periodGranularity))
            {
                return Localize?.Invoke("DAY");
            }
            if (PeriodGranularity.WEEK.Equals(periodGranularity))
            {
                return Localize?.Invoke("WEEK");
            }
            if (PeriodGranularity.MONTH.Equals(periodGranularity))
            {
                return Localize?.Invoke("MONTH");
            }
            if (PeriodGranularity.YEAR.Equals(periodGranularity))
            {
                return Localize?.Invoke("YEAR");
            }
            return null;
        }

        public static PeriodGranularity GetPeriodGranularity(this PeriodGranularity periodGranularity, string text, Func<string, string> Localize)
        {
            if (!string.IsNullOrWhiteSpace(text))
            {
                if (text.Equals(Localize?.Invoke("DAY")))
                {
                    return PeriodGranularity.DAY;
                }
                if (text.Equals(Localize?.Invoke("WEEK")))
                {
                    return PeriodGranularity.WEEK;
                }
                if (text.Equals(Localize?.Invoke("MONTH")))
                {
                    return PeriodGranularity.MONTH;
                }
                if (text.Equals(Localize?.Invoke("YEAR")))
                {
                    return PeriodGranularity.YEAR;
                }
            }
            return PeriodGranularity.DAY;
        }
    }
}
