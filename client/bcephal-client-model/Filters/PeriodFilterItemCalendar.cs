using Bcephal.Models.Base;
using Bcephal.Models.Dimensions;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Bcephal.Models.Filters
{
    public class PeriodFilterItemCalendar : IPersistent
    {
        public long? Id { get; set; }

        public bool Category { get; set; }

        public long? CategoryId { get; set; }

        public string CategoryName { get; set; }

        public CalendarDay Day { get; set; }

        public override string ToString()
        {
            if (Category)
            {
                return this.CategoryName;
            }
            if (Day != null)
            {
                return this.Day.ToString();
            }
            return base.ToString();
        }

        public int CompareTo(object obj)
        {
            if (this == obj) return 0;
            return -1;
        }

    }
}
