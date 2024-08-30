using Bcephal.Models.Base;
using Newtonsoft.Json;
using System;

namespace Bcephal.Models.Joins
{
    public class JoinLogBrowserData : BrowserData
    {
		public long? JoinId { get; set; }

		public string PublicationGridName { get; set; }

		public string PublicationNumber { get; set; }

		public long? PublicationNbrAttributeId { get; set; }

		public string PublicationNbrAttributeName { get; set; }

		public string EndDate { get; set; }

		public string Status { get; set; }

		public string Mode { get; set; }

		public string User { get; set; }

		public long? RowCount { get; set; }

		public string Message { get; set; }

		[JsonIgnore]
		public RunStatus RunStatus
		{
			get { return !string.IsNullOrEmpty(Status) ? RunStatus.GetByCode(Status) : null; }
			set { this.Status = value != null ? value.code : null; }
		}

		[JsonIgnore]
		public RunModes RunMode
		{
			get { return !string.IsNullOrEmpty(Mode) ? RunModes.GetByCode(Mode) : null; }
			set { this.Mode = value != null ? value.code : null; }
		}

		[JsonIgnore]
		public DateTime? EndDateTime
		{
			get
			{
				try
				{
					return DateUtils.ParseDateTime(EndDate);
				}
				catch (Exception)
				{
					return null;
				}
			}
		}
	}
}
