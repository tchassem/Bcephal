using Bcephal.Models.Base;
using Newtonsoft.Json;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Bcephal.Models.Reconciliation
{
    public class AutoRecoLog : Persistent
    {

		public string recoName { get; set; }

		public long? recoAttributeId { get; set; }

		public string recoAttributeName { get; set; }

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

		public string Status { get; set; }

		[JsonIgnore]
		public RunStatus RunStatus
		{
			get
			{
				return string.IsNullOrEmpty(Status) ? RunStatus.ENDED : RunStatus.GetByCode(Status);
			}
			set
			{
				this.Status = value != null ? value.getCode() : null;
			}
		}

		public string Username { get; set; }

		public long? LeftRowCount { get; set; }

		public long? RigthRowCount { get; set; }

		public long? RReconciliatedLeftRowCount { get; set; }

		public long? ReconciliatedRigthRowCount { get; set; }

	}
}
