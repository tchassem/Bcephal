using Bcephal.Models.Base;
using Bcephal.Models.Filters;
using Newtonsoft.Json;

namespace Bcephal.Models.Joins
{
    public class JoinKey : Persistent 
    {
		public int Position { get; set; }

		public DimensionType dimensionType { get; set; }

		public long? GridId1 { get; set; }

		public long? ColumnId1 { get; set; }

		public long? ValueId1 { get; set; }

		public long? GridId2 { get; set; }

		public long? ColumnId2 { get; set; }

		public long? ValueId2 { get; set; }

        public override int CompareTo(object obj)
        {
            if (obj == null || !(obj is JoinKey)) return 1;
            if (this == obj) return 0;
            if (this.Id.HasValue && this.Id.Equals(((JoinKey)obj).Id)) return 0;
            return this.Position.CompareTo(((JoinKey)obj).Position);
        }
    }
}
