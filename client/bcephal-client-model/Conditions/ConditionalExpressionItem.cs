using Bcephal.Models.Base;
using Bcephal.Models.Filters;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Bcephal.Models.Conditions
{
    public class ConditionalExpressionItem : Persistent
    {
        public string FilterVerb { get; set; }

        public string OpenBrackets { get; set; }

        public string CloseBrackets { get; set; }

        public string Operator { get; set; }

        public decimal? Value { get; set; }

        public long? SpotId1 { get; set; }

        public long? SpotId2 { get; set; }

        public DimensionType Value2Type { get; set; }

        public int Position { get; set; }

        public override int CompareTo(object obj)
        {
            if (obj == null || !(obj is ConditionalExpressionItem)) return 1;
            if (this == obj) return 0;
            if (this == obj || !this.Id.HasValue) return 0;
            if (this.Id.HasValue && this.Id.Equals(((ConditionalExpressionItem)obj).Id)) return 0;
            return this.Position.CompareTo(((ConditionalExpressionItem)obj).Position);
        }

        public ConditionalExpressionItem Copy()
        {
            ConditionalExpressionItem copy = new ConditionalExpressionItem();
            copy.Operator = this.Operator;
            copy.Value = this.Value;
            copy.Id = this.Id;
            copy.SpotId1 = this.SpotId1;
            copy.SpotId2 = this.SpotId2;
            copy.Value2Type = this.Value2Type;
            copy.FilterVerb = this.FilterVerb;
            copy.OpenBrackets = this.OpenBrackets;
            copy.CloseBrackets = this.CloseBrackets;
            copy.Position = this.Position;
            return copy;
        }
    }
}
