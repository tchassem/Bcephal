using Bcephal.Models.Base;
using Bcephal.Models.Filters;
using Newtonsoft.Json;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Bcephal.Models.Joins
{
   public class JoinCondition : Persistent 
    {
        public int? Position { get; set; }

        public string Verb { get; set; }

        public string OpeningBracket { get; set; }

        public string ClosingBracket { get; set; }

        public string Comparator { get; set; }

        public JoinConditionItem Item1 { get; set; }

        public JoinConditionItem Item2 { get; set; }


        public JoinCondition()
        {
            this.Comparator = AttributeOperator.NOT_NULL.ToString();
        }

        //public override int CompareTo(object obj)
        //{
        //    if (obj == null || !(obj is JoinCondition)) return 1;
        //    if (this == obj) return 0;
        //    if (this.Id.HasValue && this.Id.Equals(((JoinCondition)obj).Id)) return 0;
        //    return this.Item1.Id.CompareTo(((JoinCondition)obj).Item1.Id);
        //}
    }
}
