using Bcephal.Models.Base;
using Bcephal.Models.Filters;
using Newtonsoft.Json;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Bcephal.Models.Forms
{
    public class FormDataValue
    {
        public decimal? DecimalValue { set; get; }
        public string StringValue { set; get; }
        public string StringLinkedValue { set; get; }
        public string DateValue { set; get; }

        [JsonIgnore]
        public DateTime? DateTimeValue
        {
            get
            {
                try
                {
                    return DateUtils.Parse(DateValue);
                }
                catch (Exception) 
                { 
                    
                }
                return null;
            }
            set
            {
                DateValue = null;
                if (value.HasValue)
                {
                    try
                    {
                        DateValue = DateUtils.Format(value);
                    }
                    catch (Exception) { }
                }
            }
        }

        public FormDataValue() { }

        public FormDataValue(DimensionType type, Object value)
        {
            SetValue(type, value);
        }

        public void SetValue(DimensionType type, Object value)
        {
            if (value == null)
            {
                this.DateValue = null;
                this.DecimalValue = null;
                this.StringValue = null;
            }
            else
            {
                if (type.IsPeriod())
                {
                    if (value is DateTime)
                    {
                        this.DateTimeValue = (DateTime)value;
                    }
                    else if (value is DateTime?)
                    {
                        this.DateTimeValue = (DateTime?)value;
                    }
                }
                else if (type.IsMeasure())
                {
                    if(value is decimal)
                    {
                        this.DecimalValue = (decimal)value;
                    }
                    else
                    {
                        decimal val = 0;
                        if(decimal.TryParse(value.ToString(), out val))
                        {
                            this.DecimalValue = val;
                        }
                    }
                }
                else
                {
                    this.StringValue = value is string ? (string)value : value.ToString();
                }
            }
            
        }

        public object GetValue(DimensionType type)
        {
            if (type.IsPeriod())
            {
                return DateTimeValue;
            }
            else if (type.IsMeasure())
            {
                return DecimalValue;
            }
            return StringValue;
        }

    }
}
