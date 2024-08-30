using Bcephal.Models.Base;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Bcephal.Models.Profiles
{
    public class ProfileProject : Nameable
	{

		public long? ProfileId { get; set; }

		public int Position { get; set; }


		public ProfileProject()
		{

		}

		public ProfileProject(Nameable profile)
		{
			this.ProfileId = profile.Id;
			this.Name = profile.Name;
		}


		public override int CompareTo(object obj)
		{
			if (obj == null || !(obj is ProfileProject)) return 1;
			if (this == obj) return 0;
			if (this.Id.HasValue && this.Id.Equals(((ProfileProject)obj).Id)) return 0;
			if (this.Position.Equals(((ProfileProject)obj).Position))
			{
				return this.Name.CompareTo(((ProfileProject)obj).Name);
			}
			return this.Position.CompareTo(((ProfileProject)obj).Position);
		}


	}
}
