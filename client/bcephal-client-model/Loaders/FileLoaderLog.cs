using Bcephal.Models.Base;
using Newtonsoft.Json;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Bcephal.Models.Loaders
{
    public class FileLoaderLog : Persistent
    {

		public string LoaderName { get; set; }

		public string UploadMethod{ get; set; }

		[JsonIgnore]
		public FileLoaderMethod FileLoaderMethod
		{
			get
			{
				return string.IsNullOrEmpty(UploadMethod) ? FileLoaderMethod.DIRECT_TO_GRID : FileLoaderMethod.GetByCode(UploadMethod);
			}
			set
			{
				this.UploadMethod = value != null ? value.getCode() : null;
			}
		}

		public string Mode{ get; set; }

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

		public string Status{ get; set; }

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

		public int FileCount { get; set; }

		public int EmptyFileCount { get; set; }

		public int ErrorFileCount { get; set; }

		public int LoadedFileCount { get; set; }

		public bool Error { get; set; }

		public string Message { get; set; }

		public DateTime? StartDate{ get; set; }

		public DateTime? EndDate { get; set; }

	}
}
