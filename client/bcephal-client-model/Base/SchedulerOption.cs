using System;
using System.Collections.ObjectModel;

namespace Bcephal.Models.Base
{
    public class SchedulerOption
    {

        public static SchedulerOption ON_REQUEST = new SchedulerOption("ON_REQUEST", "On request");
        public static SchedulerOption SCHEDULER = new SchedulerOption("SCHEDULER", "Scheduler");

        public String label;
        public String code;

        public SchedulerOption(String code, String label)
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

        public static SchedulerOption GetByCode(String code)
        {
            if (code == null) return null;
            if (ON_REQUEST.code.Equals(code)) return ON_REQUEST;
            if (SCHEDULER.code.Equals(code)) return SCHEDULER;
            return null;
        }
    }

    public static class SchedulerOptionExtension
    {
       

        public static ObservableCollection<string> GetAll(this SchedulerOption schedulerOption, Func<string, string> Localize)
        {
            ObservableCollection<string> operators = new ObservableCollection<string>();
            // --- operators.Add(null);
            operators.Add(Localize?.Invoke("ON_REQUEST"));
            operators.Add(Localize?.Invoke("SCHEDULER"));
            return operators;
        }

        public static string GetText(this SchedulerOption schedulerOption, Func<string, string> Localize)
        {
            if (SchedulerOption.ON_REQUEST.code.Equals(schedulerOption.code))
            {
                return Localize?.Invoke("ON_REQUEST");
            }
            if (SchedulerOption.SCHEDULER.code.Equals(schedulerOption.code))
            {
                return Localize?.Invoke("SCHEDULER");
            }
            return null;
        }

        public static SchedulerOption GetSchedulerOption(this SchedulerOption filterOperator, string text, Func<string, string> Localize)
        {
            if (!string.IsNullOrWhiteSpace(text))
            {
                if (text.Equals(Localize?.Invoke("ON_REQUEST")))
                {
                    return SchedulerOption.ON_REQUEST;
                }
                if (text.Equals(Localize?.Invoke("SCHEDULER")))
                {
                    return SchedulerOption.SCHEDULER;
                }
            }
            return null;
        }
    }
}
