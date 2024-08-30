using Bcephal.Models.Base;
using Newtonsoft.Json;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Bcephal.Models.Dimensions
{
    public class CalendarDay : Persistent
    {

        public string DayType { get; set; }

        [JsonIgnore]
        public CalendarDayType CalendarDayType
        {
            get { return !string.IsNullOrEmpty(DayType) ? CalendarDayType.GetByCode(DayType) : CalendarDayType.FIXED_DATE; }
            set { this.DayType = value != null ? value.code : null; }
        }

        public int? Day { get; set; }

        [JsonIgnore]
        public CalendarDays CalendarDays
        {
            get { return CalendarDays.GetByCode(Day, CalendarDayType); }
            set { this.Day = value != null ? value.code : null; }
        }

        public int? Month { get; set; }

        [JsonIgnore]
        public CalendarMonths CalendarMonths
        {
            get { return CalendarMonths.GetByCode(Month); }
            set { this.Month = value != null ? value.code : null; }
        }

        public int? Year { get; set; }

        public int Position { get; set; }



        [JsonIgnore]
        public DateTime Date
        {
            get { return new DateTime(this.Year.Value, this.Month.Value + 1, this.Day.Value); }
            set
            {
                this.Day = value.Day;
                this.Month = value.Month - 1;
                this.Year = value.Year;
            }
        }


        public override int CompareTo(object obj)
        {
            if (obj == null || !(obj is CalendarDay)) return 1;
            int c = this.Position.CompareTo(((CalendarDay)obj).Position);
            if (c != 0) return c;
            if (this.DayType != null) return this.DayType.CompareTo(((CalendarDay)obj).DayType);
            return 1;
        }

        public override string ToString()
        {
            if (this.CalendarDayType.IsFixedDate())
            {
                return this.Date.ToString();
            }
            String value = "";
            String separator = "";
            if (this.Day.HasValue)
            {
                value = this.CalendarDays.label;
                separator = "-";
            }
            if (this.Month.HasValue)
            {
                value += separator + this.CalendarMonths.label;
                separator = "-";
            }
            if (this.Year.HasValue)
            {
                value += separator + this.Year;
                separator = "-";
            }
            return value;
        }

    }
}

