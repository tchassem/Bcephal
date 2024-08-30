using Newtonsoft.Json;
using Newtonsoft.Json.Converters;
using System;
using System.Collections.Generic;
using System.Collections.ObjectModel;
using System.Text;

namespace Bcephal.Models.Filters
{
    [JsonConverter(typeof(StringEnumConverter))]
    public enum PeriodOperator
    {
        SPECIFIC,
        TODAY,
        BEGIN_WEEK,
        END_WEEK,
        BEGIN_MONTH,
        END_MONTH,
        BEGIN_YEAR,
        END_YEAR,
        CALENDAR,
    }

    public static class PeriodOperatorExtensionMethods
    {

        public static bool IsAcceptParameter(this PeriodOperator periodOperator)
        {
            return periodOperator == PeriodOperator.SPECIFIC;
        }

        public static ObservableCollection<PeriodOperator> GetAll(this PeriodOperator periodOperator)
        {
            ObservableCollection<PeriodOperator> operators = new ObservableCollection<PeriodOperator>();
            operators.Add(PeriodOperator.TODAY);
            operators.Add(PeriodOperator.BEGIN_WEEK);
            operators.Add(PeriodOperator.END_WEEK);
            operators.Add(PeriodOperator.BEGIN_MONTH);
            operators.Add(PeriodOperator.END_MONTH);
            operators.Add(PeriodOperator.BEGIN_YEAR);
            operators.Add(PeriodOperator.END_YEAR);
            operators.Add(PeriodOperator.CALENDAR);
            operators.Add(PeriodOperator.SPECIFIC);
            return operators;
        }

        public static ObservableCollection<PeriodOperator> GetAllOperator()
        {
            ObservableCollection<PeriodOperator> operators = new ObservableCollection<PeriodOperator>();
            operators.Add(PeriodOperator.TODAY);
            operators.Add(PeriodOperator.BEGIN_WEEK);
            operators.Add(PeriodOperator.END_WEEK);
            operators.Add(PeriodOperator.BEGIN_MONTH);
            operators.Add(PeriodOperator.END_MONTH);
            operators.Add(PeriodOperator.BEGIN_YEAR);
            operators.Add(PeriodOperator.END_YEAR);
            operators.Add(PeriodOperator.CALENDAR);
            operators.Add(PeriodOperator.SPECIFIC);
            return operators;
        }

        public static bool IsSpecific(this PeriodOperator periodOperator)
        {
            return periodOperator == PeriodOperator.SPECIFIC;
        }

        public static bool IsCalendar(this PeriodOperator periodOperator)
        {
            return periodOperator == PeriodOperator.CALENDAR;
        }

        public static ObservableCollection<string> GetAll(this PeriodOperator periodOperator, Func<string, string> Localize)
        {
            ObservableCollection<string> operators = new ObservableCollection<string>();
            operators.Add(null);
            operators.Add(Localize?.Invoke("SPECIFIC"));
            operators.Add(Localize?.Invoke("TODAY"));
            operators.Add(Localize?.Invoke("BEGIN_WEEK"));
            operators.Add(Localize?.Invoke("END_WEEK"));
            operators.Add(Localize?.Invoke("BEGIN_MONTH"));
            operators.Add(Localize?.Invoke("END_MONTH"));
            operators.Add(Localize?.Invoke("BEGIN_YEAR"));
            operators.Add(Localize?.Invoke("END_YEAR"));
            operators.Add(Localize?.Invoke("CALENDAR"));            
            return operators;
        }

        public static string GetText(this PeriodOperator periodOperator, Func<string, string> Localize)
        {
            if (PeriodOperator.TODAY.Equals(periodOperator))
            {
                return Localize?.Invoke("TODAY");
            }
            if (PeriodOperator.BEGIN_WEEK.Equals(periodOperator))
            {
                return Localize?.Invoke("BEGIN_WEEK");
            }
            if (PeriodOperator.END_WEEK.Equals(periodOperator))
            {
                return Localize?.Invoke("END_WEEK");
            }
            if (PeriodOperator.BEGIN_MONTH.Equals(periodOperator))
            {
                return Localize?.Invoke("BEGIN_MONTH");
            }
            if (PeriodOperator.END_MONTH.Equals(periodOperator))
            {
                return Localize?.Invoke("END_MONTH");
            }
            if (PeriodOperator.BEGIN_YEAR.Equals(periodOperator))
            {
                return Localize?.Invoke("BEGIN_YEAR");
            }
            if (PeriodOperator.END_YEAR.Equals(periodOperator))
            {
                return Localize?.Invoke("END_YEAR");
            }
            if (PeriodOperator.CALENDAR.Equals(periodOperator))
            {
                return Localize?.Invoke("CALENDAR");
            }
            if (PeriodOperator.SPECIFIC.Equals(periodOperator))
            {
                return Localize?.Invoke("SPECIFIC");
            }
            return null;
        }

        public static PeriodOperator GetPeriodOperator(this PeriodOperator periodOperator, string text, Func<string, string> Localize)
        {
            if (!string.IsNullOrWhiteSpace(text))
            {
                if (text.Equals(Localize?.Invoke("TODAY")))
                {
                    return PeriodOperator.TODAY;
                }
                if (text.Equals(Localize?.Invoke("BEGIN_WEEK")))
                {
                    return PeriodOperator.BEGIN_WEEK;
                }
                if (text.Equals(Localize?.Invoke("END_WEEK")))
                {
                    return PeriodOperator.END_WEEK;
                }
                if (text.Equals(Localize?.Invoke("BEGIN_MONTH")))
                {
                    return PeriodOperator.BEGIN_MONTH;
                }
                if (text.Equals(Localize?.Invoke("END_MONTH")))
                {
                    return PeriodOperator.END_MONTH;
                }
                if (text.Equals(Localize?.Invoke("BEGIN_YEAR")))
                {
                    return PeriodOperator.BEGIN_YEAR;
                }
                if (text.Equals(Localize?.Invoke("END_YEAR")))
                {
                    return PeriodOperator.END_YEAR;
                }
                if (text.Equals(Localize?.Invoke("CALENDAR")))
                {
                    return PeriodOperator.CALENDAR;
                }
                if (text.Equals(Localize?.Invoke("SPECIFIC")))
                {
                    return PeriodOperator.SPECIFIC;
                }
            }
            return PeriodOperator.SPECIFIC;
        }
    }

}
