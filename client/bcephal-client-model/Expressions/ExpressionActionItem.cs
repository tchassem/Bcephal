using Bcephal.Models.Base;
using Newtonsoft.Json;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Bcephal.Models.Expressions
{
    public class ExpressionActionItem : Persistent
    {

		public string Type { get; set; }
		[JsonIgnore]
		public ExpressionActionType ActionType
		{
			get { return ExpressionActionType.GetByCode(this.Type); }
			set { this.Type = value != null ? value.code : null; }
		}

		public Condition Condition { get; set; }

		public string ValueType { get; set; }
		[JsonIgnore]
		public ExpressionActionValueType ActionValueType
		{
			get { return ExpressionActionValueType.GetByCode(this.ValueType); }
			set { this.ValueType = value != null ? value.code : null; }
		}

		public ExpressionValue Value { get; set; }

		public bool OnlyIfEmpty { get; set; }

		public int Position { get; set; }

		public override int CompareTo(object obj)
		{
			if (obj == null || !(obj is ExpressionActionItem)) return 1;
			if (this == obj) return 0;
			if (this.Id.HasValue && this.Id.Equals(((ExpressionActionItem)obj).Id)) return 0;
			return this.Position.CompareTo(((ExpressionActionItem)obj).Position);
		}

	}
}
