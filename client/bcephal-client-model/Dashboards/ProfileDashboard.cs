using Bcephal.Models.Base;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Bcephal.Models.Dashboards
{
    public class ProfileDashboard : Nameable
    {
		
		public long? DashboardId { get; set; }

		public int Position { get; set; }

		public bool DefaultDashboard { get; set; }

		public ProfileDashboard()
        {

        }

		public ProfileDashboard(Nameable dashboard)
		{
			this.DashboardId = dashboard.Id;
			this.Name = dashboard.Name;
		}

		public override int CompareTo(object obj)
		{
			if (obj == null || !(obj is ProfileDashboard)) return 1;
			if (this == obj) return 0;
			if (this.Id.HasValue && this.Id.Equals(((ProfileDashboard)obj).Id)) return 0;
			if (this.Position.Equals(((ProfileDashboard)obj).Position))
			{
				return this.Name.CompareTo(((ProfileDashboard)obj).Name);
			}
			return this.Position.CompareTo(((ProfileDashboard)obj).Position);
		}

		public ProfileDashboard Copy()
		{
			ProfileDashboard copy = new ProfileDashboard();
			copy.Id = this.Id;
			copy.Name = this.Name;
			copy.Position = this.Position;
			copy.DashboardId = this.DashboardId;
			copy.DefaultDashboard = this.DefaultDashboard;
			return copy;
		}
	}
}
