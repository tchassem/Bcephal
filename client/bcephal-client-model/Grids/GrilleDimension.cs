using Bcephal.Models.Base;
using Bcephal.Models.Filters;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Bcephal.Models.Grids
{
    public class GrilleDimension : Persistent
    {

		public DimensionType Type { get; set; }

		public long? DimensionId { get; set; }

		public string Name { get; set; }

		public int Position { get; set; }


		public override int CompareTo(object obj)
		{
			if (obj == null || !(obj is GrilleDimension)) return 1;
			if (this == obj) return 0;
			if (this.Id.HasValue && this.Id.Equals(((GrilleDimension)obj).Id)) return 0;
			if (this.Position.Equals(((GrilleDimension)obj).Position))
			{
				return this.Name.CompareTo(((GrilleDimension)obj).Name);
			}
			return this.Position.CompareTo(((GrilleDimension)obj).Position);
		}

		public override string ToString()
		{
			return this.Name;
		}

	}
}
