using Bcephal.Models.Base;
using Newtonsoft.Json;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Bcephal.Models.Reconciliation
{
    public class AutoRecoLogItem : Persistent
    {

		public string Username { get; set; }

		public int? RecoNumber { get; set; }

		public string Mode { get; set; }

		[JsonIgnore]
		public RunModes RunModes
		{
			get
			{
				return string.IsNullOrEmpty(Mode) ? RunModes.M : RunModes.GetByCode(Mode);
			}
			set
			{
				this.Mode = value != null ? value.getCode() : null;
			}
		}

		public long? LeftRowCount { get; set; }

		public long? RigthRowCount { get; set; }

		public decimal? LeftAmount { get; set; }

		public decimal? RigthAmount { get; set; }

		public decimal? BalanceAmount { get; set; }

		public decimal? WriteoffAmount { get; set; }

	}
}
