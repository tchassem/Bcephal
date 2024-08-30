using Bcephal.Models.Base;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Bcephal.Models.Joins
{
    public  class JoinColumnCalculateItem : Persistent
    {
		public int Position;
		public string OpeningBracket { get; set; }
		public string ClosingBracket { get; set; }
		public string Sign { get; set; }
		public JoinColumnField Field { get; set; }

		public override int CompareTo(object obj)
		{
			if (obj == null || !(obj is JoinColumnCalculateItem)) return 1;
			if (this == obj) return 0;
			if (this.Id.HasValue && this.Id.Equals(((JoinColumnCalculateItem)obj).Id)) return 0;
			return this.Position.CompareTo(((JoinColumnCalculateItem)obj).Position);
		}
	}
}
