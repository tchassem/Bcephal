using Bcephal.Models.Base;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Bcephal.Models.Joins
{
    public class JoinColumnConcatenateItem: Persistent
    {
		public long? PropertiesId { get; set; }

		public int Position { get; set; }

		public JoinColumnField Field { get; set; }

		public override int CompareTo(object obj)
		{
			if (obj == null || !(obj is JoinColumnConcatenateItem)) return 1;
			if (this == obj) return 0;
			if (this.Id.HasValue && this.Id.Equals(((JoinColumnConcatenateItem)obj).Id)) return 0;
			return this.Position.CompareTo(((JoinColumnConcatenateItem)obj).Position);
		}
	}
}
