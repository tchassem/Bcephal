using Bcephal.Models.Grids.Filters;
using Newtonsoft.Json;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Bcephal.Models.socket
{
  public  class RunData
    {
        public bool Start { get; set; }
        public bool End { get; set; }
        public object Data { get; set; }
    }

    public class DataTransfert
    {
        public byte[] Data { get; set; }
        public Decision? decision { get; set; }

        public string Name { get; set; }
        public string Folder { get; set; }
        public long? userId { get; set; }
        public string RemotePath { get; set; }
        public string Type { get; set; }


        [JsonIgnore]
        public DataType DataType
        {
            get { return DataType.GetByCode(this.Type); }
            set { this.Type = value != null ? value.code : null; }
        }

        [JsonIgnore]
        public bool IsExcel
        {
            get { return DataType == DataType.EXCEL; }
        }

        [JsonIgnore]
        public bool IsCsv
        {
            get { return DataType == DataType.CSV; }
        }

        [JsonIgnore]
        public bool IsJson
        {
            get { return DataType == DataType.JSON; }
        }

    }

    public enum Decision
    {
        END, STOP, CONTINUE, NEW, CLOSE
    }
}
