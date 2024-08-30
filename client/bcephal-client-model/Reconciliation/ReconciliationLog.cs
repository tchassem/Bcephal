using Bcephal.Models.Base;
using Newtonsoft.Json;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Bcephal.Models.Reconciliation
{
    public class ReconciliationLog : Persistent
	{

		public string Username { get; set; }

		public string RecoType { get; set; }

		public string Action { get; set; }
		[JsonIgnore]
		public ReconciliationActions Actions
		{
			get
			{
				return string.IsNullOrEmpty(Action) ? ReconciliationActions.RECONCILIATION : ReconciliationActions.GetByCode(Action);
			}
			set
			{
				this.Action = value != null ? value.getCode() : null;
			}
		}

		public decimal? LeftAmount { get; set; }

		public decimal? RigthAmount { get; set; }

		public decimal? BalanceAmount { get; set; }

		public decimal? WriteoffAmount { get; set; }

		public string ReconciliationNbr { get; set; }

		public string CreationDate { get; set; }

		[JsonIgnore]
		public DateTime CreationDateTime
		{
			get
			{
				try
				{
					return DateUtils.ParseDateTime(CreationDate);
				}
				catch (Exception)
				{
					return DateTime.Now;
				}
			}
		}

	}
}
