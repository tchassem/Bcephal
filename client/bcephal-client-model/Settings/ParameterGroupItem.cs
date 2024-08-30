using Newtonsoft.Json;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Bcephal.Models.Settings
{
    public class ParameterGroupItem
    {
        public string Code { get; set; }

        public string Type { get; set; }

        public string ParentCode { get; set; }

        public ParameterGroupItem(String code, string type, String parentCode)
        {
            this.Code = code;
            this.Type = type;
            this.ParentCode = parentCode;
        }

        public override string ToString()
        {
            return this.Code;
        }

        public bool IsEntity =>  this.Type == ParameterTypes.ENTITY.code;

        public bool IsAttribute => this.Type == ParameterTypes.ATTRIBUTE.code;

        public bool IsAttributeValue => this.Type == ParameterTypes.ATTRIBUTE_VALUE.code;

        public bool IsMeasure => this.Type == ParameterTypes.MEASURE.code;

        public bool IsPeriod => this.Type == ParameterTypes.PERIOD.code;

        public bool IsModel => this.Type == ParameterTypes.MODEL.code;

        public bool IsGrid => this.Type == ParameterTypes.GRID.code;

        public bool IsIncrementalNumber => this.Type == ParameterTypes.INCREMENTAL_NUMBER.code;

        public bool IsBillTemplate => this.Type == ParameterTypes.BILL_TEMPLATE.code;

        public bool IsInteger => this.Type == ParameterTypes.INTEGER.code;

        public bool IsLong => this.Type == ParameterTypes.LONG.code;

        public bool IsDecimal => this.Type == ParameterTypes.DECIMAL.code;

        public bool IsString => this.Type == ParameterTypes.STRING.code;

        public bool IsDate => this.Type == ParameterTypes.DATE.code;

        public bool IsBoolean => this.Type == ParameterTypes.BOOLEAN.code;

    }
}
