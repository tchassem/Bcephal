using Bcephal.Models.Base;
using Bcephal.Models.Filters;
using Newtonsoft.Json;
using Newtonsoft.Json.Converters;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Bcephal.Models.Dashboards
{
    public class ChartSerie: Persistent
    {
        public string Color { get; set; }
        public DashboardReportField ValueAxis { get; set; }
        public DashboardReportField ArgumentAxis { get; set; }
        public DashboardReportField SerieAxis { get; set; }
        public string Name { get; set; }
        public bool IsDefault { get; set; }
        public bool IsVisible { get; set; }
        public bool ShowLabel { get; set; } = false;
        public string Type { get; set; }
        public bool AddCustomValueAxis { get; set; } = false;
        public bool ShowCustomValueAxisTitle { get; set; } = false;
        public string CustomValueAxisTitle { get; set; }
        public string CustomValueAxisAlignment { get; set; }
        public ChartSerieFilter SerieFilter { get; set; }

        public ChartSerie()
        {
            SerieFilter = new ChartSerieFilter();
        }

        public override int CompareTo(object obj)
        {
            if (obj == null || !(obj is ChartSerie)) return 1;
            if (this.Name == null || ((ChartSerie)obj).Name == null ) return 1;
            if (this == obj) return 0;
            return this.Name.CompareTo(((ChartSerie)obj).Name);
        }
    }


    public class ChartSerieFilter
    {
        public IList<SerieFilterItem> ItemList  { get; set; }

        public ChartSerieFilter()
        {
            ItemList = new List<SerieFilterItem>();
        }

        public void AddItem(SerieFilterItem item)
        {
            if (!ItemList.Contains(item))
            {
                ItemList.Add(item);
            }
        }

        public void DeleteItem(SerieFilterItem item)
        {
            if (ItemList.Contains(item))
            {
                ItemList.Remove(item);
            }
        }

        public int ItemsCount { get => ItemList.Count; }
    }

    public class SerieFilterItem
    {
        public DashboardReportField Field { get; set; }
        public FilterVerb FilterVerb { get; set; }
        public string OpenBracket { get; set; }
        public string CloseBracket { get; set; }

        public string Operator { get; set; }
        public string Comparator { get; set; }
        public string Value { get; set; }
        public string Sign { get; set; }
        public int? Number { get; set; }
        public PeriodGranularity Granularity { get; set; }
        public SerieFilterItem()
        {
            this.FilterVerb = FilterVerb.AND;
        }

        public bool Validate(Dictionary<string, object> element)
        {
            try
            {
                if (this.Field != null && this.Operator != null)
                {
                    object objValue = element[this.Field.DimensionName];
                    if (this.Field.Type == DimensionType.MEASURE)
                    {
                        decimal? decimalFieldValue = GetDecimalValue(objValue);
                        decimal? decimalValue = GetDecimalValue(this.Value);
                        if (this.Operator == MeasureOperator.EQUALS)
                        {
                            return (decimalValue.HasValue && decimalFieldValue.HasValue && decimalValue.Value == decimalFieldValue.Value)
                                || (!decimalValue.HasValue && !decimalFieldValue.HasValue);
                        }
                        else if (this.Operator == MeasureOperator.NOT_EQUALS)
                        {
                            return (decimalValue.HasValue && decimalFieldValue.HasValue && decimalValue.Value != decimalFieldValue.Value)
                                || (!decimalValue.HasValue && !decimalFieldValue.HasValue);
                        }
                        else if (this.Operator == MeasureOperator.GRETTER_OR_EQUALS)
                        {
                            return (decimalValue.HasValue && decimalFieldValue.HasValue && decimalValue.Value <= decimalFieldValue.Value)
                                || (!decimalValue.HasValue && !decimalFieldValue.HasValue);
                        }
                        else if (this.Operator == MeasureOperator.LESS_OR_EQUALS)
                        {
                            return (decimalValue.HasValue && decimalFieldValue.HasValue && decimalValue.Value >= decimalFieldValue.Value)
                                || (!decimalValue.HasValue && !decimalFieldValue.HasValue);
                        }
                        else if (this.Operator == MeasureOperator.GRETTER_THAN)
                        {
                            return (decimalValue.HasValue && decimalFieldValue.HasValue && decimalValue.Value < decimalFieldValue.Value)
                                || (!decimalValue.HasValue && !decimalFieldValue.HasValue);
                        }
                        else if (this.Operator == MeasureOperator.LESS_THAN)
                        {
                            return (decimalValue.HasValue && decimalFieldValue.HasValue && decimalValue.Value > decimalFieldValue.Value)
                                || (!decimalValue.HasValue && !decimalFieldValue.HasValue);
                        }
                    }
                    else if (this.Field.Type == DimensionType.ATTRIBUTE)
                    {
                        string stringFieldValue = objValue != null ? objValue.ToString() : null;
                        if (stringFieldValue == null)
                        {

                        }
                        string stringValue = this.Value;
                        if (this.Operator == AttributeOperator.EQUALS.ToString())
                        {
                            return stringFieldValue == stringValue;
                        }
                        else if (this.Operator == AttributeOperator.NOT_EQUALS.ToString())
                        {
                            return !stringFieldValue.Equals(stringValue);
                        }
                        else if (this.Operator == AttributeOperator.CONTAINS.ToString())
                        {
                            return stringFieldValue.Contains(stringValue);
                        }
                        else if (this.Operator == AttributeOperator.NOT_CONTAINS.ToString())
                        {
                            return !stringFieldValue.Contains(stringValue);
                        }
                        else if (this.Operator == AttributeOperator.STARTS_WITH.ToString())
                        {
                            return stringFieldValue.StartsWith(stringValue);
                        }
                        else if (this.Operator == AttributeOperator.ENDS_WITH.ToString())
                        {
                            return stringFieldValue.EndsWith(stringValue);
                        }
                        else if (this.Operator == AttributeOperator.NULL.ToString())
                        {
                            return stringFieldValue == null || stringFieldValue.Equals(null);
                        }
                        else if (this.Operator == AttributeOperator.NOT_NULL.ToString())
                        {
                            return stringFieldValue != null && !stringFieldValue.Equals(null);
                        }
                    }
                    else if (this.Field.Type == DimensionType.PERIOD)
                    {
                        DateTime? dateFieldValue = GetPeriodValue(objValue);
                        DateTime? dateValue = GetPeriodValue(this.Value);
                        if (this.Operator == MeasureOperator.EQUALS)
                        {
                            return (dateFieldValue.HasValue && dateValue.HasValue && dateFieldValue.Value == dateValue.Value)
                                || (!dateFieldValue.HasValue && !dateValue.HasValue);
                        }
                        else if (this.Operator == MeasureOperator.NOT_EQUALS)
                        {
                            return (dateFieldValue.HasValue && dateValue.HasValue && dateFieldValue.Value != dateValue.Value)
                                || (dateFieldValue.HasValue && !dateValue.HasValue) || (!dateFieldValue.HasValue && dateValue.HasValue);
                        }
                        else if (this.Operator == MeasureOperator.GRETTER_OR_EQUALS)
                        {
                            return (dateFieldValue.HasValue && dateValue.HasValue && dateFieldValue.Value >= dateValue.Value);
                        }
                        else if (this.Operator == MeasureOperator.LESS_OR_EQUALS)
                        {
                            return (dateFieldValue.HasValue && dateValue.HasValue && dateFieldValue.Value <= dateValue.Value);
                        }
                        else if (this.Operator == MeasureOperator.GRETTER_THAN)
                        {
                            return (dateFieldValue.HasValue && dateValue.HasValue && dateFieldValue.Value > dateValue.Value);
                        }
                        else if (this.Operator == MeasureOperator.LESS_THAN)
                        {
                            return (dateFieldValue.HasValue && dateValue.HasValue && dateFieldValue.Value < dateValue.Value);
                        }
                    }
                }
            }
            catch (Exception ex)
            {
                Console.WriteLine(ex);
            }
            return true;
        }

        private decimal? GetDecimalValue(object value)
        {
            try
            {
                return Convert.ToDecimal(value);
            }
            catch (Exception)
            {

            }
            return null;
        }

        private DateTime? GetPeriodValue(object value)
        {
            try
            {
                return Convert.ToDateTime(value);
            }
            catch (Exception)
            {

            }
            return null;
        }

        public static IEnumerable<PeriodOperator> GetPeriodOperators()
        {
            return new List<PeriodOperator>(){
                PeriodOperator.TODAY,
                PeriodOperator.BEGIN_WEEK,
                PeriodOperator.END_WEEK,
                PeriodOperator.BEGIN_MONTH,
                PeriodOperator.END_MONTH,
                PeriodOperator.BEGIN_YEAR,
                PeriodOperator.END_YEAR,
                PeriodOperator.SPECIFIC
            };
        }
    }

}
