using Bcephal.Models.Base;
using Bcephal.Models.Filters;
using Newtonsoft.Json;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Bcephal.Models.Dashboards
{
    public class DashboardReportField : Nameable
    {

        public DimensionType Type { get; set; }

        public long? DimensionId { get; set; }

        public string DimensionName { get; set; }

        public int Position { get; set; }



       

        [JsonIgnore] public string Caption
        {
            get { return Name + (!string.IsNullOrWhiteSpace(DimensionName) ? " (" + DimensionName + ")" : ""); }
        }

        public override int CompareTo(object obj)
        {
            if (obj == null || !(obj is DashboardReportField)) return 1;
            return this.Position.CompareTo(((DashboardReportField)obj).Position);
        }

        public override string ToString()
        {
            return Caption;
        }

    }
}
