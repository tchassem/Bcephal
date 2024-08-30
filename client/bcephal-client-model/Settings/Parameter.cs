using Bcephal.Models.Base;
using Newtonsoft.Json;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Bcephal.Models.Settings
{
    public class Parameter : Persistent
    {

		public string Code { get; set; }

		public string ParentCode { get; set; }

		public string ParameterType { get; set; }

		[JsonIgnore]
		public ParameterTypes ParameterTypes
		{
			get { return !string.IsNullOrEmpty(this.ParameterType) ? ParameterTypes.GetByCode(this.ParameterType) : null; }
			set { this.ParameterType = value != null ? value.code : null; }
		}

        [JsonIgnore]
        public string Name { get; set; }

        public int? IntegerValue { get; set; }

		public long? LongValue { get; set; }

		public decimal? DecimalValue { get; set; }

		public string StringValue { get; set; }

		public DateTime? DateValue { get; set; }

		public bool? BooleanValue { get; set; }


        [JsonIgnore]
        public string Label { get; set; }

        public ListChangeHandler<Parameter> Parameters { get; set; }


        [JsonIgnore]
        public bool IsEntity { get { return this.ParameterType != null && this.ParameterTypes.IsEntity; } }

        [JsonIgnore]
        public bool IsAttribute { get { return this.ParameterType != null && this.ParameterTypes.IsAttribute; } }

        [JsonIgnore]
        public bool IsAttributeValue { get { return this.ParameterType != null && this.ParameterTypes.IsAttributeValue; } }

        [JsonIgnore]
        public bool IsMeasure { get { return this.ParameterType != null && this.ParameterTypes.IsMeasure; } }

        [JsonIgnore]
        public bool IsPeriod { get { return this.ParameterType != null && this.ParameterTypes.IsPeriod; } }

        [JsonIgnore]
        public bool IsModel { get { return this.ParameterType != null && this.ParameterTypes.IsModel; } }

        [JsonIgnore]
        public bool IsGrid { get { return this.ParameterType != null && this.ParameterTypes.IsGrid; } }

        [JsonIgnore]
        public bool IsIncrementalNumber { get { return this.ParameterType != null && this.ParameterTypes.IsIncrementalNumber; } }

        [JsonIgnore]
        public bool IsBillTemplate { get { return this.ParameterType != null && this.ParameterTypes.IsBillTemplate; } }

        [JsonIgnore]
        public bool IsInteger { get { return this.ParameterType != null && this.ParameterTypes.IsInteger; } }

        [JsonIgnore]
        public bool IsLong { get { return this.ParameterType != null && this.ParameterTypes.IsLong; } }

        [JsonIgnore]
        public bool IsDecimal { get { return this.ParameterType != null && this.ParameterTypes.IsDecimal; } }

        [JsonIgnore]
        public bool IsString { get { return this.ParameterType != null && this.ParameterTypes.IsString; } }

        [JsonIgnore]
        public bool IsDate { get { return this.ParameterType != null && this.ParameterTypes.IsDate; } }

        [JsonIgnore]
        public bool IsBoolean { get { return this.ParameterType != null && this.ParameterTypes.IsBoolean; } }


        public Parameter()
        {
            Parameters = new ListChangeHandler<Parameter>();
        }

        public Parameter(String code) : this()
        {
            this.Code = code;
        }

        public Parameter(String code, ParameterTypes parameterTypes)
            : this(code)
        {
            this.ParameterTypes = parameterTypes;
        }

        public Parameter(String code, ParameterTypes parameterTypes, string label)
            : this(code, parameterTypes)
        {
            this.Label = label;
        }

        public Parameter(String code, ParameterTypes parameterTypes, string label, string parentCode)
            : this(code, parameterTypes, label)
        {
            this.ParentCode = parentCode;
        }

        public override int CompareTo(object obj)
        {
            if (obj == null || !(obj is Parameter)) return 1;
            if (this == obj) return 0;
            return this.Code.CompareTo(((Parameter)obj).Code);
        }

    }
}
