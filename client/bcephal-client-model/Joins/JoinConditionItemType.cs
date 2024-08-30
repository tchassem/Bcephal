using Newtonsoft.Json;
using Newtonsoft.Json.Converters;
using System;
using System.Collections.Generic;
using System.Collections.ObjectModel;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Bcephal.Models.Joins
{
    [JsonConverter(typeof(StringEnumConverter))]
    public enum JoinConditionItemType
    {
        COLUMN,
        PARAMETER,
        SPOT
    }

    public static class JoinCoditionItemTypeExtensionMethods
    {
        public static ObservableCollection<JoinConditionItemType> GetAll(this JoinConditionItemType joinCoditionItemType)
        {
            ObservableCollection<JoinConditionItemType> JoinCoditionItemTypes = new ObservableCollection<JoinConditionItemType>
            {
                JoinConditionItemType.COLUMN,
                JoinConditionItemType.PARAMETER
            };
            return JoinCoditionItemTypes;
        }

        public static ObservableCollection<JoinConditionItemType> GetAllOperator()
        {
            ObservableCollection<JoinConditionItemType> operators = new ObservableCollection<JoinConditionItemType>();
            operators.Add(JoinConditionItemType.COLUMN);
            operators.Add(JoinConditionItemType.PARAMETER);
            operators.Add(JoinConditionItemType.SPOT);
            return operators;
        }

        public static bool IsColumn(this JoinConditionItemType periodOperator)
        {
            return periodOperator == JoinConditionItemType.COLUMN;
        }

        public static bool IsParameter(this JoinConditionItemType periodOperator)
        {
            return periodOperator == JoinConditionItemType.PARAMETER;
        }

        public static bool IsSpot(this JoinConditionItemType periodOperator)
        {
            return periodOperator == JoinConditionItemType.SPOT;
        }

        public static ObservableCollection<string> GetAll(this JoinConditionItemType? joinCoditionItemType, Func<string, string> Localize)
        {
            ObservableCollection<string> operators = new ObservableCollection<string>
            {
                null,
                Localize?.Invoke("COLUMN"),
                Localize?.Invoke("PARAMETER"),
                Localize?.Invoke("SPOT")
            };
            return operators;
        }

        public static string GetText(this JoinConditionItemType joinCoditionItemType, Func<string, string> Localize)
        {
            if (JoinConditionItemType.COLUMN.Equals(joinCoditionItemType))
            {
                return Localize?.Invoke("COLUMN");
            }
            if (JoinConditionItemType.PARAMETER.Equals(joinCoditionItemType))
            {
                return Localize?.Invoke("PARAMETER");
            }
            if (JoinConditionItemType.SPOT.Equals(joinCoditionItemType))
            {
                return Localize?.Invoke("SPOT");
            }
            return null;
        }

        public static JoinConditionItemType GetJoinCoditionItemType(this JoinConditionItemType joinCoditionItemType, string text, Func<string, string> Localize)
        {
            if (!string.IsNullOrWhiteSpace(text))
            {
                if (text.Equals(Localize?.Invoke("COLUMN")))
                {
                    return JoinConditionItemType.COLUMN;
                }
                if (text.Equals(Localize?.Invoke("PARAMETER")))
                {
                    return JoinConditionItemType.PARAMETER;
                }
                if (text.Equals(Localize?.Invoke("SPOT")))
                {
                    return JoinConditionItemType.SPOT;
                }
            }
            return JoinConditionItemType.PARAMETER;
        }             
    }
}
