using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Bcephal.Models.Expressions
{
    public class Condition
    {

        public Expression IfExpression { get; set; }

        public ExpressionAction ThenExpression { get; set; }

        public ExpressionAction ElseExpression { get; set; }

    }
}
