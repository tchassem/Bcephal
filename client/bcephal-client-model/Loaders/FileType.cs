using Newtonsoft.Json;
using Newtonsoft.Json.Converters;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Bcephal.Models.Loaders
{
    [JsonConverter(typeof(StringEnumConverter))]
    public enum FileType
    {
        EXCEL,
        CSV,
    }
    public static class FileTypeExtensionMethods
    {

        public static bool IsMeasure(this FileType fileType)
        {
            return fileType == FileType.EXCEL;
        }

        public static bool IsPeriod(this FileType fileType)
        {
            return fileType == FileType.CSV;
        }

    }
}
