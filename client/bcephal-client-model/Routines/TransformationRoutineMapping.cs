using Bcephal.Models.Base;
using System;
using System.Collections.Generic;
using System.Collections.ObjectModel;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Bcephal.Models.Routines
{
    public class TransformationRoutineMapping : Persistent
    {

		public long? GridId { get; set; }

		public long? ValueColumnId { get; set; }

		public ObservableCollection<long?> MappingColumnIds { get; set; }

		public TransformationRoutineMapping()
		{
			MappingColumnIds = new ObservableCollection<long?>();
		}

	}
}
