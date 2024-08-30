using Newtonsoft.Json;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Bcephal.Models.Grids.Filters
{
    public class GrilleExportData
    {

        public BrowserDataFilter Filter { get; set; }

        public string Type { get; set; }

        [JsonIgnore]
        public GrilleExportDataType DataType
        {
            get { return GrilleExportDataType.GetByCode(this.Type); }
            set { this.Type = value != null ? value.code : null; }
        }

    }
}
