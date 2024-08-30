using Bcephal.Models.Base;
using Newtonsoft.Json;
using Newtonsoft.Json.Converters;
using System.Collections.Generic;

namespace Bcephal.Models.Dashboards
{
    public class ChartProperties : DashboardReportProperties
    {
        public string ChartType { get; set; }
        private string WebLayoutData_ { get; set; }
        public override string WebLayoutData
        {
            get 
            {
                if (WebChartData == null)
                {
                    return WebLayoutData_;
                }
                return JsonConvert.SerializeObject(WebChartData);
            }
            set
            {
                if (!string.IsNullOrWhiteSpace(value))
                {
                    WebChartData = JsonConvert.DeserializeObject<WebChartData>(value);
                }
                WebLayoutData_ = value;
            }
        }

        [JsonIgnore] public WebChartData WebChartData { get; set; }

        [JsonIgnore] public DashboardReportChartType DashboardReportChartType
        {
            get { return DashboardReportChartType.GetByCode(this.ChartType); }
            set { this.ChartType = value != null ? value.Code : null; }
        }

        public DashboardReportChartDispositionType ChartDispositionType { get; set; }
    }

    
    [JsonConverter(typeof(StringEnumConverter))]
    public enum DashboardReportChartDispositionType
    {
        COLUMNS_AS_SERIES,
        COLUMS_AS_ARGUMENTS,
    }

    public class WebChartData
    {
        public string Title { get; set; }
        public string Subtitle { get; set; }
        public bool ShowAxisTitles { get; set; }
        public string ValueAxisTitle { get; set; }
        public string ArgumentAxisTitle { get; set; }
        public bool Rotated { get; set; }
        public string LabelOverlap { get; set; }
        public bool ShowLegend { get; set; }
        public bool ShowLegendBorder { get; set; }
        public bool LegendAllowToggleSeries { get; set; }
        public string LegendOrientation { get; set; }
        public string LegendPosition { get; set; }
        public string LegendHorizontalAlignment { get; set; }
        public string LegendTitle { get; set; }
        public string LegendSubtitle { get; set; }
        public ChartSerie DefaultSerie { get; set; }

        public List<ChartSerie> ChartSerieList { get; set; }

        public WebChartData()
        {
            ShowAxisTitles = true;
            this.ChartSerieList = new List<ChartSerie>();
        }

        public void AddSerie(ChartSerie item, bool sort = true)
        {
            ChartSerieList.Add(item);
        }


        /// <summary>
        /// Met à jour une série du chart
        /// </summary>
        /// <param name="cell"></param>
        public void UpdateItem(ChartSerie item, bool sort = true)
        {
            //ChartSerieList.AddUpdated(item, sort);
        }

        /// <summary>
        /// Retire  une série du chart
        /// </summary>
        /// <param name="cell"></param>
        public void RemoveItem(ChartSerie item, bool sort = true)
        {
            ChartSerieList.Remove(item);
        }

        /// <summary>
        /// Oublier  une série du chart
        /// </summary>
        /// <param name="cell"></param>
        public void ForgetItem(ChartSerie item, bool sort = true)
        {
            ChartSerieList.Remove(item);
        }

        public void RemoveOrForgetItem(ChartSerie item, bool sort = true)
        {
            if (item.IsPersistent)
            {
                RemoveItem(item, sort);
            }
            else
            {
                ForgetItem(item, sort);
            }
        }
    }

}
