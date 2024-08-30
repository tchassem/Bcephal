using Bcephal.Models.Base;
using Newtonsoft.Json;

namespace Bcephal.Models.Joins
{
    public class JoinGrid : Persistent
    {
		public long? GridId { get; set; }

		public string Name { get; set; }

		public int Position { get; set; }

		public override int CompareTo(object obj)
		{
			if (obj == null || !(obj is JoinGrid)) return 1;
			if (this == obj) return 0;
			if (this.Id.HasValue && this.Id.Equals(((JoinGrid)obj).Id)) return 0;
			if (this.Position.Equals(((JoinGrid)obj).Position))
			{
				return this.Name.CompareTo(((JoinGrid)obj).Name);
			}
			
			return this.Position.CompareTo(((JoinGrid)obj).Position);
		}
	}
}
