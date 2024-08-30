using System;
using System.Collections.Generic;
using System.Collections.ObjectModel;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Bcephal.Models.Planners
{
    public class SchedulerPlannerItemTemporisationUnit
    {

        public static SchedulerPlannerItemTemporisationUnit SECONDE = new SchedulerPlannerItemTemporisationUnit("SECONDE", "Seconde");
        public static SchedulerPlannerItemTemporisationUnit MINUTE = new SchedulerPlannerItemTemporisationUnit("MINUTE", "Minute");
        public static SchedulerPlannerItemTemporisationUnit HOUR = new SchedulerPlannerItemTemporisationUnit("HOUR", "Hour");
        public static SchedulerPlannerItemTemporisationUnit DAY = new SchedulerPlannerItemTemporisationUnit("DAY", "Day");

        public String label;
        public String code;

        public SchedulerPlannerItemTemporisationUnit(String code, String label)
        {
            this.code = code;
            this.label = label;
        }

        public String GetLabel()
        {
            return label;
        }

        public bool IsSeconde()
        {
            return this == SECONDE;
        }

        public bool IsMinute()
        {
            return this == MINUTE;
        }

        public bool IsHour()
        {
            return this == HOUR;
        }

        public bool IsDay()
        {
            return this == DAY;
        }


        public override String ToString()
        {
            return GetLabel();
        }

        public static SchedulerPlannerItemTemporisationUnit GetByLabel(string label)
        {
            if (label == null) return null;
            if (SECONDE.label.Equals(label)) return SECONDE;
            if (MINUTE.label.Equals(label)) return MINUTE;
            if (HOUR.label.Equals(label)) return HOUR;
            if (DAY.label.Equals(label)) return DAY;
            return null;
        }

        public static SchedulerPlannerItemTemporisationUnit GetByCode(string code)
        {
            if (code == null) return null;
            if (SECONDE.code.Equals(code)) return SECONDE;
            if (MINUTE.code.Equals(code)) return MINUTE;
            if (HOUR.code.Equals(code)) return HOUR;
            if (DAY.code.Equals(code)) return DAY;
            return null;
        }
    }

    public static class SchedulerPlannerItemTemporisationUnitExtensionMethods
    {

        public static ObservableCollection<string> GetAll(this SchedulerPlannerItemTemporisationUnit itemType, Func<string, string> Localize)
        {
            ObservableCollection<string> temporisationUnits = new ObservableCollection<string>();
            temporisationUnits.Add(null);
            temporisationUnits.Add(Localize?.Invoke("DAY"));
            temporisationUnits.Add(Localize?.Invoke("HOUR"));
            temporisationUnits.Add(Localize?.Invoke("MINUTE"));
            temporisationUnits.Add(Localize?.Invoke("SECONDE"));
            return temporisationUnits;
        }

        public static string GetText(this SchedulerPlannerItemTemporisationUnit itemType, Func<string, string> Localize)
        {
            if (SchedulerPlannerItemTemporisationUnit.DAY.Equals(itemType))
            {
                return Localize?.Invoke("DAY");
            }
            if (SchedulerPlannerItemTemporisationUnit.HOUR.Equals(itemType))
            {
                return Localize?.Invoke("HOUR");
            }
            if (SchedulerPlannerItemTemporisationUnit.MINUTE.Equals(itemType))
            {
                return Localize?.Invoke("MINUTE");
            }
            if (SchedulerPlannerItemTemporisationUnit.SECONDE.Equals(itemType))
            {
                return Localize?.Invoke("SECONDE");
            }
            return null;
        }

        public static SchedulerPlannerItemTemporisationUnit GetItemTemporisationUnit(this SchedulerPlannerItemTemporisationUnit tempUnit, string text, Func<string, string> Localize)
        {
            if (!string.IsNullOrWhiteSpace(text))
            {
                if (text.Equals(Localize?.Invoke("DAY")))
                {
                    return SchedulerPlannerItemTemporisationUnit.DAY;
                }
                if (text.Equals(Localize?.Invoke("HOUR")))
                {
                    return SchedulerPlannerItemTemporisationUnit.HOUR;
                }
                if (text.Equals(Localize?.Invoke("MINUTE")))
                {
                    return SchedulerPlannerItemTemporisationUnit.MINUTE;
                }
                if (text.Equals(Localize?.Invoke("SECONDE")))
                {
                    return SchedulerPlannerItemTemporisationUnit.SECONDE;
                }
            }
            return SchedulerPlannerItemTemporisationUnit.DAY;
        }

        public static SchedulerPlannerItemTemporisationUnit Parse(this SchedulerPlannerItemTemporisationUnit tempUnit, string text)
        {
            try
            {
                return string.IsNullOrWhiteSpace(text) ? null : ((SchedulerPlannerItemTemporisationUnit) Enum.Parse(typeof(SchedulerPlannerItemTemporisationUnit), text));
            }
            catch
            {
                return null;
            }
        }
    }
}
