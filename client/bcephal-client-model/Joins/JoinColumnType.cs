using System;
using System.Collections.Generic;
using System.Collections.ObjectModel;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Bcephal.Models.Joins
{
    public class JoinColumnType
    {
        public static JoinColumnType FREE = new JoinColumnType("FREE", "Free");
        public static JoinColumnType COLUMN = new JoinColumnType("COLUMN", "Column");
        public static JoinColumnType COPY = new JoinColumnType("COPY", "Copy");
        public static JoinColumnType POSITION = new JoinColumnType("POSITION", "Position");
        public static JoinColumnType CONCATENATE = new JoinColumnType("CONCATENATE", "Concatenate");
        public static JoinColumnType CALCULATE = new JoinColumnType("CALCULATE", "Calculate");
        public static JoinColumnType SEQUENCE = new JoinColumnType("SEQUENCE", "Sequence");
        public static JoinColumnType CONDITION = new JoinColumnType("CONDITION", "Condition");
        public static JoinColumnType SPOT = new JoinColumnType("SPOT", "Spot");

        public String label;
        public String code;

        public JoinColumnType(String code, String label)
        {
            this.code = code;
            this.label = label;
        }

        public String GetLabel()
        {
            return label;
        }

        public bool IsFree()
        {
            return this == FREE;
        }

        public bool IsColumn()
        {
            return this == COLUMN;
        }

        public bool IsCopy()
        {
            return this == COPY;
        }

        public bool IsPosition()
        {
            return this == POSITION;
        }

        public bool IsConcatenate()
        {
            return this == CONCATENATE;
        }

        public bool IsCalculate()
        {
            return this == CALCULATE;
        }

        public bool IsCondition()
        {
            return this == CONDITION;
        }

        public bool IsSequence()
        {
            return this == SEQUENCE;
        }

        public bool IsSpot()
        {
            return this == SPOT;
        }

        public override String ToString()
        {
            return GetLabel();
        }

        public static JoinColumnType GetByLabel(string label)
        {
            if (label == null) return null;
            if (FREE.label.Equals(label)) return FREE;
            if (COLUMN.label.Equals(label)) return COLUMN;
            if (COPY.label.Equals(label)) return COPY;
            if (POSITION.label.Equals(label)) return POSITION;
            if (CONCATENATE.label.Equals(label)) return CONCATENATE;
            if (CALCULATE.label.Equals(label)) return CALCULATE;
            if (SEQUENCE.label.Equals(label)) return SEQUENCE;
            if (CONDITION.label.Equals(label)) return CONDITION;
            if (SPOT.label.Equals(label)) return SPOT;
            return null;
        }

        public static JoinColumnType GetByCode(string code)
        {
            if (code == null) return null;
            if (FREE.code.Equals(code)) return FREE;
            if (COLUMN.code.Equals(code)) return COLUMN;
            if (COPY.code.Equals(code)) return COPY;
            if (POSITION.code.Equals(code)) return POSITION;
            if (CONCATENATE.code.Equals(code)) return CONCATENATE;
            if (CALCULATE.code.Equals(code)) return CALCULATE;
            if (SEQUENCE.code.Equals(code)) return SEQUENCE;
            if (CONDITION.code.Equals(code)) return CONDITION;
            if (SPOT.code.Equals(code)) return SPOT;
            return null;
        }
    }

    public static class JoinColumnTypeExtention
    {
        public static ObservableCollection<string> GetAll(this JoinColumnType joinColumnType, Func<string, string> Localize)
        {
            ObservableCollection<string> operators = new ObservableCollection<string>();
            operators.Add(Localize?.Invoke("FREE"));
            operators.Add(Localize?.Invoke("COLUMN"));
            operators.Add(Localize?.Invoke("COPY"));
            operators.Add(Localize?.Invoke("POSITION"));
            operators.Add(Localize?.Invoke("CONCATENATE"));
            operators.Add(Localize?.Invoke("CALCULATE"));
            operators.Add(Localize?.Invoke("SEQUENCE"));
            operators.Add(Localize?.Invoke("CONDITION"));
            operators.Add(Localize?.Invoke("SPOT"));
            return operators;
        }

        public static ObservableCollection<string> GetAllTypeAttributes(this JoinColumnType joinColumnType, Func<string, string> Localize)
        {
            ObservableCollection<string> operators = new ObservableCollection<string>();
            operators.Add(Localize?.Invoke("FREE"));
            operators.Add(Localize?.Invoke("COLUMN"));
            operators.Add(Localize?.Invoke("COPY"));
            //operators.Add(Localize?.Invoke("POSITION"));
            operators.Add(Localize?.Invoke("CONCATENATE"));
            operators.Add(Localize?.Invoke("SEQUENCE"));
            return operators;
        }

        public static ObservableCollection<string> GetAllTypeMeasures(this JoinColumnType joinColumnType, Func<string, string> Localize)
        {
            ObservableCollection<string> operators = new ObservableCollection<string>();
            operators.Add(Localize?.Invoke("FREE"));
            operators.Add(Localize?.Invoke("COLUMN"));
            operators.Add(Localize?.Invoke("COPY"));
            operators.Add(Localize?.Invoke("CALCULATE"));
            operators.Add(Localize?.Invoke("SPOT"));
            return operators;
        }
        
        public static ObservableCollection<string> GetAllTypePeriods(this JoinColumnType joinColumnType, Func<string, string> Localize)
        {
            ObservableCollection<string> operators = new ObservableCollection<string>();
            operators.Add(Localize?.Invoke("FREE"));
            operators.Add(Localize?.Invoke("COLUMN"));
            operators.Add(Localize?.Invoke("COPY"));
            return operators;
        }

        public static string GetText(this JoinColumnType joinColumnType, Func<string, string> Localize)
        {
            if (joinColumnType.IsFree())
            {
                return Localize?.Invoke("FREE");
            }
            if (joinColumnType.IsColumn())
            {
                return Localize?.Invoke("COLUMN");
            }
            if (joinColumnType.IsCopy())
            {
                return Localize?.Invoke("COPY");
            }
            if (joinColumnType.IsPosition())
            {
                return Localize?.Invoke("POSITION");
            }
            if (joinColumnType.IsConcatenate())
            {
                return Localize?.Invoke("CONCATENATE");
            }
            if (joinColumnType.IsCalculate())
            {
                return Localize?.Invoke("CALCULATE");
            }
            if (joinColumnType.IsCondition())
            {
                return Localize?.Invoke("CONDITION");
            }
            if (joinColumnType.IsSequence())
            {
                return Localize?.Invoke("SEQUENCE");
            }
            if (joinColumnType.IsSpot())
            {
                return Localize?.Invoke("SPOT");
            }
            return null;
        }

        public static JoinColumnType GetJoinColumnType(this JoinColumnType joinColumnType, string text, Func<string, string> Localize)
        {
            if (!string.IsNullOrWhiteSpace(text))
            {
                if (text.Equals(Localize?.Invoke("FREE")))
                {
                    return JoinColumnType.FREE;
                }
                if (text.Equals(Localize?.Invoke("COLUMN")))
                {
                    return JoinColumnType.COLUMN;
                }
                if (text.Equals(Localize?.Invoke("COPY")))
                {
                    return JoinColumnType.COPY;
                }
                if (text.Equals(Localize?.Invoke("POSITION")))
                {
                    return JoinColumnType.POSITION;
                }
                if (text.Equals(Localize?.Invoke("CONCATENATE")))
                {
                    return JoinColumnType.CONCATENATE;
                }
                if (text.Equals(Localize?.Invoke("CALCULATE")))
                {
                    return JoinColumnType.CALCULATE;
                }
                if (text.Equals(Localize?.Invoke("CONDITION")))
                {
                    return JoinColumnType.CONDITION;
                }
                if (text.Equals(Localize?.Invoke("SEQUENCE")))
                {
                    return JoinColumnType.SEQUENCE;
                }
                if (text.Equals(Localize?.Invoke("SPOT")))
                {
                    return JoinColumnType.SPOT;
                }
            }
            return JoinColumnType.FREE;
        }
    }
}