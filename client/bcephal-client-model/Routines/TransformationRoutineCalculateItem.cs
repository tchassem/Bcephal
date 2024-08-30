using Bcephal.Models.Base;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Bcephal.Models.Routines
{
    public class TransformationRoutineCalculateItem : Persistent
    {

		public int Position { get; set; }

		public string OpeningBracket { get; set; }

		public string ClosingBracket { get; set; }

		public string Sign { get; set; }

		public TransformationRoutineField Field { get; set; }


		public override int CompareTo(object obj)
		{
			if (obj == null || !(obj is TransformationRoutineCalculateItem)) return 1;
			if (this == obj) return 0;
			if (this.Id.HasValue && this.Id.Equals(((TransformationRoutineCalculateItem)obj).Id)) return 0;
			return this.Position.CompareTo(((TransformationRoutineCalculateItem)obj).Position);

		}

	}
}
