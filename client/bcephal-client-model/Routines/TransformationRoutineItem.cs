using Bcephal.Models.Base;
using Bcephal.Models.Filters;
using Bcephal.Models.Grids;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Bcephal.Models.Routines
{
    public class TransformationRoutineItem : Nameable
    {

		public String description { get; set; }

		public int Position { get; set; }

		public DimensionType Type { get; set; }

		public long? TargetDimensionId { get; set; }

		public long? TargetGridId { get; set; }

		public bool ApplyOnlyIfEmpty { get; set; }

		public UniverseFilter Filter { get; set; }

		public TransformationRoutineField SourceField { get; set; }


		public ListChangeHandler<TransformationRoutineCalculateItem> CalculateItemListChangeHandler { get; set; }

		public ListChangeHandler<TransformationRoutineConcatenateItem> ConcatenateItemListChangeHandler { get; set; }


		public TransformationRoutineItem()
		{
			this.CalculateItemListChangeHandler = new ListChangeHandler<TransformationRoutineCalculateItem>();
			this.ConcatenateItemListChangeHandler = new ListChangeHandler<TransformationRoutineConcatenateItem>();
		}





		public override int CompareTo(object obj)
		{
			if (obj == null || !(obj is TransformationRoutineItem)) return 1;
			if (this == obj) return 0;
			if (this.Id.HasValue && this.Id.Equals(((TransformationRoutineItem)obj).Id)) return 0;
			if (!string.IsNullOrWhiteSpace(this.Name) &&  this.Position.Equals(((TransformationRoutineItem)obj).Position))
			{
				return this.Name.CompareTo(((TransformationRoutineItem)obj).Name);
			}
			return this.Position.CompareTo(((TransformationRoutineItem)obj).Position);
		}

	}
}
