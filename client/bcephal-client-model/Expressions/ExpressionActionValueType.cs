using System;
using System.Collections.Generic;
using System.Collections.ObjectModel;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Bcephal.Models.Expressions
{
    public class ExpressionActionValueType
    {

        public static ExpressionActionValueType PARAMETER = new ExpressionActionValueType("PARAMETER", "Parameter");
        public static ExpressionActionValueType COLUMN = new ExpressionActionValueType("COLUMN", "Column");
        public static ExpressionActionValueType LOOP = new ExpressionActionValueType("LOOP", "Loop");
        public static ExpressionActionValueType CALCULATE = new ExpressionActionValueType("CALCULATE", "Calculate");
        public static ExpressionActionValueType MAPSPOT = new ExpressionActionValueType("MAPSPOT", "Map spot");
        public static ExpressionActionValueType MAPGRID = new ExpressionActionValueType("MAPGRID", "Map grid");
        public static ExpressionActionValueType LEFT = new ExpressionActionValueType("LEFT", "Left");
        public static ExpressionActionValueType RIGHT = new ExpressionActionValueType("RIGHT", "Right");
        public static ExpressionActionValueType POSITION = new ExpressionActionValueType("POSITION", "Position");
        public static ExpressionActionValueType CONCATENATE = new ExpressionActionValueType("CONCATENATE", "Concatenate");

        public string label;
        public string code;
        private ExpressionActionValueType(String code, String label)
        {
            this.label = label;
            this.code = code;
        }

        public static ExpressionActionValueType GetByCode(String code)
        {
            if (code == null) return null;
            if (LOOP.code.Equals(code)) return LOOP;
            if (CALCULATE.code.Equals(code)) return CALCULATE;
            if (PARAMETER.code.Equals(code)) return PARAMETER;
            if (COLUMN.code.Equals(code)) return COLUMN;
            if (MAPSPOT.code.Equals(code)) return MAPSPOT;
            if (MAPGRID.code.Equals(code)) return MAPGRID;
            if (LEFT.code.Equals(code)) return LEFT;
            if (RIGHT.code.Equals(code)) return RIGHT;
            if (POSITION.code.Equals(code)) return POSITION;
            if (CONCATENATE.code.Equals(code)) return CONCATENATE;
            return null;
        }

        public static ObservableCollection<ExpressionActionValueType> GetAll()
        {
            ObservableCollection<ExpressionActionValueType> operators = new ObservableCollection<ExpressionActionValueType>();
            operators.Add(PARAMETER);
            operators.Add(COLUMN);
            operators.Add(LOOP);
            operators.Add(CALCULATE);
            operators.Add(MAPSPOT);
            operators.Add(MAPGRID);
            operators.Add(LEFT);
            operators.Add(RIGHT);
            operators.Add(POSITION);
            operators.Add(CONCATENATE);
            return operators;
        }

    }
}
