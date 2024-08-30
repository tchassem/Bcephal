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
   public class PivotTableProperties : DashboardReportProperties
    {
        private string WebLayoutData_ { get; set; }
        public override string WebLayoutData
        {
            get
            {
                if (WebPivotTableLayout == null)
                {
                    return WebLayoutData_;
                }
                return JsonConvert.SerializeObject(WebPivotTableLayout);
            }
            set
            {
                if (!string.IsNullOrWhiteSpace(value))
                {
                    WebPivotTableLayout = JsonConvert.DeserializeObject<WebPivotTableLayout>(value);
                }
                WebLayoutData_ = value;
            }
        }
        [JsonIgnore] public WebPivotTableLayout WebPivotTableLayout { get; set; }
    }

    public class WebPivotTableLayout
    {
       
        public List<WebPivotTableLayoutField> Fields { get; set; }

        public WebPivotTableLayout()
        {
            this.Fields = new List<WebPivotTableLayoutField> ();
        }

        public void AddField(WebPivotTableLayoutField field)
        {
            this.Fields.Add(field);
        }
        public void deleteField(WebPivotTableLayoutField field)
        {
            this.Fields.Remove(field);
        }
    }

    public class WebPivotTableLayoutField : IComparable
    {
        public DimensionType Type;
        public DashboardReportFieldGroup? FieldGroup { get; set; }
        public DashboardReportPeriodGrouping? PeriodGrouping { get; set; }
        public string DimensionName { get; set; }
        public string Caption { get; set; }
        public int Position{ get; set; }
        public  int CompareTo(object obj)
        {
            if (obj == null || !(obj is WebPivotTableLayoutField)) return 1;
            if (!string.IsNullOrEmpty(this.DimensionName))
            {
                return this.DimensionName.CompareTo(((WebPivotTableLayoutField)obj).DimensionName);
            }
            return this.Position.CompareTo(((WebPivotTableLayoutField)obj).Position);
        }
    }
}
