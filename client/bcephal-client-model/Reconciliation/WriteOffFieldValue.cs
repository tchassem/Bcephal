using Bcephal.Models.Base;
using Newtonsoft.Json;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Bcephal.Models.Reconciliation
{
    public class WriteOffFieldValue : Persistent
    {

		public int Position { get; set; }

		public bool DefaultValue { get; set; }

		public string StringValue { get; set; }

		public decimal? DecimalValue { get; set; }
		
		public PeriodValue DateValue { get; set; }

		public WriteOffFieldValue()
		{
			
		}

		public override int CompareTo(object obj)
		{
			if (obj == null || !(obj is WriteOffFieldValue)) return 1;
			if (this == obj) return 0;
			if (this.Id.HasValue && this.Id.Equals(((WriteOffFieldValue)obj).Id)) return 0;
			if (this.Position.Equals(((WriteOffFieldValue)obj).Position) && !string.IsNullOrWhiteSpace(this.StringValue))
			{
				return this.StringValue.CompareTo(((WriteOffFieldValue)obj).StringValue);
			}
			return this.Position.CompareTo(((WriteOffFieldValue)obj).Position);
		}

		[JsonIgnore]
		public string Key
		{
			get
			{
				if (string.IsNullOrWhiteSpace(Key_))
				{
					Key_ = Guid.NewGuid().ToString("d");
				}
				return Key_;
			}
			set
			{
				Key_ = value;
			}
		}
		[JsonIgnore]
		private string Key_ { get; set; }

	}
}
