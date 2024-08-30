using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Bcephal.Models.Settings
{
    public class ParameterTypes
    {

        public static ParameterTypes MODEL = new ParameterTypes("MODEL", "Model");
        public static ParameterTypes ENTITY = new ParameterTypes("ENTITY", "Entity");
        public static ParameterTypes ATTRIBUTE = new ParameterTypes("ATTRIBUTE", "Attribute");
        public static ParameterTypes ATTRIBUTE_VALUE = new ParameterTypes("ATTRIBUTE_VALUE", "Attribute Value");
        public static ParameterTypes MEASURE = new ParameterTypes("MEASURE", "Measure");
        public static ParameterTypes PERIOD = new ParameterTypes("PERIOD", "Period");
        public static ParameterTypes GRID = new ParameterTypes("GRID", "Grid");
        public static ParameterTypes JOIN = new ParameterTypes("JOIN", "Join");
        public static ParameterTypes INCREMENTAL_NUMBER = new ParameterTypes("INCREMENTAL_NUMBER", "Sequence");
        public static ParameterTypes BILL_TEMPLATE = new ParameterTypes("BILL_TEMPLATE", "Bill Template");        
        public static ParameterTypes INTEGER = new ParameterTypes("INTEGER", "Integer");
        public static ParameterTypes LONG = new ParameterTypes("LONG", "Long");
        public static ParameterTypes DATE = new ParameterTypes("DATE", "Date");
        public static ParameterTypes BOOLEAN = new ParameterTypes("BOOLEAN", "Boolean");
        public static ParameterTypes DECIMAL = new ParameterTypes("DECIMAL", "Decimal");
        public static ParameterTypes STRING = new ParameterTypes("STRING", "String");

        public String label;
        public String code;

        public ParameterTypes(String code, String label)
        {
            this.code = code;
            this.label = label;
        }

        public String getCode()
        {
            return code;
        }


        public bool IsAttribute { get { return this == ATTRIBUTE; } }

        public bool IsAttributeValue { get { return this == ATTRIBUTE_VALUE; } }

        public bool IsMeasure { get { return this == MEASURE; } }

        public bool IsPeriod { get { return this == PERIOD; } }

        public bool IsEntity { get { return this == ENTITY; } }

        public bool IsModel { get { return this == MODEL; } }

        public bool IsGrid { get { return this == GRID; } }

        public bool IsJoin { get { return this == JOIN; } }

        public bool IsIncrementalNumber { get { return this == INCREMENTAL_NUMBER; } }

        public bool IsBillTemplate { get { return this == BILL_TEMPLATE; } }

        public bool IsInteger { get { return this == INTEGER; } }
        public bool IsLong { get { return this == LONG; } }
        public bool IsDecimal { get { return this == DECIMAL; } }
        public bool IsString { get { return this == STRING; } }
        public bool IsDate { get { return this == DATE; } }
        public bool IsBoolean { get { return this == BOOLEAN; } }


        public override String ToString()
        {
            return label;
        }

        public static ParameterTypes GetByCode(String code)
        {
            if (code == null) return null;

            if (ATTRIBUTE.code.Equals(code)) return ATTRIBUTE;
            if (ATTRIBUTE_VALUE.code.Equals(code)) return ATTRIBUTE_VALUE;
            if (MEASURE.code.Equals(code)) return MEASURE;
            if (PERIOD.code.Equals(code)) return PERIOD;
            if (MODEL.code.Equals(code)) return MODEL;
            if (ENTITY.code.Equals(code)) return ENTITY;
            if (GRID.code.Equals(code)) return GRID;
            if (JOIN.code.Equals(code)) return JOIN;
            if (INCREMENTAL_NUMBER.code.Equals(code)) return INCREMENTAL_NUMBER;
            if (BILL_TEMPLATE.code.Equals(code)) return BILL_TEMPLATE;
            if (INTEGER.code.Equals(code)) return INTEGER;
            if (LONG.code.Equals(code)) return LONG;
            if (DATE.code.Equals(code)) return DATE;
            if (BOOLEAN.code.Equals(code)) return BOOLEAN;
            if (DECIMAL.code.Equals(code)) return DECIMAL;
            if (STRING.code.Equals(code)) return STRING;
            return null;
        }


    }
}
