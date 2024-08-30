using Bcephal.Models.Base;
using Newtonsoft.Json;
using System;
using System.Collections.Generic;
using System.Text;

namespace Bcephal.Models.Links
{
	[Serializable]
	public class LinkedAttribute : Persistent
	{
		public long? AttributeId { get; set; }
		public string AttributeName { get; set; }

		public bool Iskey { get; set; }

		public LinkType? LinkType { get; set; }

		public int Position { get; set; }


		[JsonIgnore]
		public bool IsOneToOne
		{
			get { return this.LinkType.HasValue && this.LinkType.Value.Equals(Models.Links.LinkType.ONE_TO_ONE); }
			set { this.LinkType = value ? Models.Links.LinkType.ONE_TO_ONE : Models.Links.LinkType.MANY_TO_ONE; }
		}


		public override int CompareTo(object obj)
		{
			if (obj == null || !(obj is LinkedAttribute)) return 1;
			if (this == obj) return 0;
			if (this.Id.HasValue && this.Id.Equals(((LinkedAttribute)obj).Id)) return 0;
			if (this.Position.Equals(((LinkedAttribute)obj).Position) && !string.IsNullOrEmpty(this.AttributeName))
			{
				return this.AttributeName.CompareTo(((LinkedAttribute)obj).AttributeName);
			}
			return this.Position.CompareTo(((LinkedAttribute)obj).Position);
		}
	}
}
