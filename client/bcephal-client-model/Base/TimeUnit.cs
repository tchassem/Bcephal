using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Bcephal.Models.Base
{
    public class TimeUnit
    {

        public static TimeUnit SECOND = new TimeUnit("SECOND", "Second(s)");
        public static TimeUnit MINUTE = new TimeUnit("MINUTE", "Minute(s)");
        public static TimeUnit HOUR = new TimeUnit("HOUR", "Hour(s)");


        public String Code { get; protected set; }
        public String Label { get; protected set; }

        public TimeUnit(String code, String label)
        {
            this.Label = label;
            this.Code = code;
        }

        public override string ToString()
        {
            return this.Label;
        }

        public bool IsSecond()
        {
            return this == TimeUnit.SECOND;
        }

        public bool IsMinute()
        {
            return this == TimeUnit.MINUTE;
        }

        public bool IsHour()
        {
            return this == TimeUnit.HOUR;
        }



        public static TimeUnit GetByCode(String code)
        {
            if (TimeUnit.SECOND.Code.Equals(code)) return TimeUnit.SECOND;
            if (TimeUnit.MINUTE.Code.Equals(code)) return TimeUnit.MINUTE;
            if (TimeUnit.HOUR.Code.Equals(code)) return TimeUnit.HOUR;
            return TimeUnit.MINUTE;
        }


    }
}
