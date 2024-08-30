using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Bcephal.Models.Loaders
{
    public class FileLoaderColumnDataBuilder
    {

		public int SheetIndex { get; set; }

		public string SheetName { get; set; }

		public string Separator { get; set; }

		public long? GridId { get; set; }

		public FileType FileType { get; set; }

		public bool HasHeader { get; set; }

		public int HeaderRowcount { get; set; }


	}
}
