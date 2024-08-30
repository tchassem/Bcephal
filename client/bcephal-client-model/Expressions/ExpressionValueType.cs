using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Bcephal.Models.Expressions
{
    public enum ExpressionValueType
    {

		COLUMN,
		LOOP,
		NULL,
		PARAMETER,
		SPOT,
		NO
	}


    public static class ExpressionValueTypeExtensionMethods
    {

        public static bool IsColumn(this ExpressionValueType type)
        {
            return type == ExpressionValueType.COLUMN;
        }

        public static bool IsParameter(this ExpressionValueType type)
        {
            return type == ExpressionValueType.PARAMETER;
        }

        public static bool IsSpot(this ExpressionValueType type)
        {
            return type == ExpressionValueType.SPOT;
        }

        public static bool IsLoop(this ExpressionValueType type)
        {
            return type == ExpressionValueType.LOOP;
        }

        public static bool IsNull(this ExpressionValueType type)
        {
            return type == ExpressionValueType.NULL;
        }

        public static bool IsNo(this ExpressionValueType type)
        {
            return type == ExpressionValueType.NO;
        }

    }

}
