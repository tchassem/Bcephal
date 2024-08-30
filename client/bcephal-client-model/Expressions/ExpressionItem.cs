using Bcephal.Models.Base;
using Newtonsoft.Json;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Bcephal.Models.Expressions
{
    public class ExpressionItem : Persistent
    {

		public ExpressionValue Value1 { get; set; }

		public ExpressionValue Value2 { get; set; }

		public string Operator { get; set; }
		[JsonIgnore]
		public ExpressionOperator ExpressionOperator
		{
			get { return ExpressionOperator.GetByCode(this.Operator); }
			set { this.Operator = value != null ? value.code : null; }
		}


		public ExpressionVerb? Verb { get; set; }

		public int Position { get; set; }


		public override int CompareTo(object obj)
		{
			if (obj == null || !(obj is ExpressionItem)) return 1;
			if (this == obj) return 0;
			if (this.Id.HasValue && this.Id.Equals(((ExpressionItem)obj).Id)) return 0;			
			return this.Position.CompareTo(((ExpressionItem)obj).Position);
		}


    }
}
