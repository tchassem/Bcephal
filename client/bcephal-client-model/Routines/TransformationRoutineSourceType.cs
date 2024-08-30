using Bcephal.Models.Filters;
using System;
using System.Collections.Generic;
using System.Collections.ObjectModel;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Bcephal.Models.Routines
{
    public class TransformationRoutineSourceType
    {
        
        public static TransformationRoutineSourceType FREE = new TransformationRoutineSourceType("FREE", "Free");
        public static TransformationRoutineSourceType DIMENSION = new TransformationRoutineSourceType("DIMENSION", "Dimension");
        public static TransformationRoutineSourceType CALCULATE = new TransformationRoutineSourceType("CALCULATE", "Calculate");
        public static TransformationRoutineSourceType CONCATENATE = new TransformationRoutineSourceType("CONCATENATE", "Concatenate");
        public static TransformationRoutineSourceType MAPPING = new TransformationRoutineSourceType("MAPPING", "Mapping");
        public static TransformationRoutineSourceType POSITION = new TransformationRoutineSourceType("POSITION", "Position");
        public static TransformationRoutineSourceType REPLACE = new TransformationRoutineSourceType("REPLACE", "Replace");


        public string label;
        public string code;

        public TransformationRoutineSourceType(string code, string label)
        {
            this.code = code;
            this.label = label;
        }

        public string GetLabel()
        {
            return label;
        }

        public bool IsFree()
        {
            return this == FREE;
        }

        public bool IsDimension()
        {
            return this == DIMENSION;
        }

        public bool IsCalculate()
        {
            return this == CALCULATE;
        }

        public bool IsConcatenate()
        {
            return this == CONCATENATE;
        }

        public bool IsMapping()
        {
            return this == MAPPING;
        }

        public bool IsPosition()
        {
            return this == POSITION;
        }

        public bool IsReplace()
        {
            return this == REPLACE;
        }

        public override String ToString()
        {
            return GetLabel();
        }

        public static TransformationRoutineSourceType GetByCode(string code)
        {
            if (code == null) return null;
            if (FREE.code.Equals(code)) return FREE;
            if (DIMENSION.code.Equals(code)) return DIMENSION;
            if (CALCULATE.code.Equals(code)) return CALCULATE;
            if (CONCATENATE.code.Equals(code)) return CONCATENATE;
            if (MAPPING.code.Equals(code)) return MAPPING;
            if (POSITION.code.Equals(code)) return POSITION;
            if (REPLACE.code.Equals(code)) return REPLACE;
            return null;
        }

        public static ObservableCollection<TransformationRoutineSourceType> GetAll(DimensionType type)
        {
            ObservableCollection<TransformationRoutineSourceType> types = new ObservableCollection<TransformationRoutineSourceType>();
            types.Add(FREE);
            types.Add(DIMENSION);
            types.Add(MAPPING);
            if (type.IsAttribute())
            {
                types.Add(CONCATENATE);
                types.Add(POSITION);
                types.Add(REPLACE);
            }
            else if (type.IsMeasure())
            {
                types.Add(CALCULATE);
            }
            else if (type.IsPeriod())
            {

            }
            return types;
        }

    }
}
