using Bcephal.Models.Base;
using Bcephal.Models.Filters;
using Bcephal.Models.Grids;
using Newtonsoft.Json;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Bcephal.Models.Loaders
{
    public class FileLoaderColumn : Persistent
    {

		public DimensionType Type { get; set; }

        public long? DimensionId { get; set; }

        public GrilleColumn GrilleColumn { get; set; }

		public string FileColumn { get; set; }

		public int Position { get; set; }

        public int? SheetPposition { get; set; }

        public string SheetName { get; set; }

        [JsonIgnore]
        public string DimensionName { get; set; }


        public override int CompareTo(object obj)
        {
            if (obj == null || !(obj is FileLoaderColumn)) return 1;
            if (obj == this) return 0;
            return this.Position.CompareTo(((FileLoaderColumn)obj).Position);
        }

    }
}
