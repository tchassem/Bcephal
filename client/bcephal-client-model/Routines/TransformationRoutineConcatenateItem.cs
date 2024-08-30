using Bcephal.Models.Base;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Bcephal.Models.Routines
{
    public class TransformationRoutineConcatenateItem : Persistent
    {

		public int Position { get; set; }

		public TransformationRoutineField Field { get; set; }


		public override int CompareTo(object obj)
		{
			if (obj == null || !(obj is TransformationRoutineConcatenateItem)) return 1;
			if (this == obj) return 0;
			if (this.Id.HasValue && this.Id.Equals(((TransformationRoutineConcatenateItem)obj).Id)) return 0;
			return this.Position.CompareTo(((TransformationRoutineConcatenateItem)obj).Position);

		}

	}
}
