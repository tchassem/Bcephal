using Bcephal.Models.Base;
using Newtonsoft.Json;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Bcephal.Models.Loaders
{
    public class FileLoaderLogItem : Persistent
    {

		public string File { get; set; }

		public int LineCount { get; set; }

		public bool Empty { get; set; }

		public bool Loaded { get; set; }

		public bool Error { get; set; }

		public string Message { get; set; }

		[JsonIgnore]
		public RunStatus RunStatus
		{
			get
			{
				return Loaded && !Error ? RunStatus.ENDED : RunStatus.ERROR;
			}
		}

	}
}
