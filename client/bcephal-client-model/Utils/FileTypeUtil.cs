using System;
using System.Collections.Generic;
using System.IO;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Bcephal.Models.Utils
{
    public class FileTypeUtil
    {

        public static bool Exists(string path)
        {
            return File.Exists(path);
        }

        public static bool IsExcel(string path)
        {
            if (File.Exists(path))
            {
                string ext = Path.GetExtension(path);
                return ".XLSX".Equals(ext, StringComparison.OrdinalIgnoreCase) || ".XLS".Equals(ext, StringComparison.OrdinalIgnoreCase);
            }
            return false;
        }

        public static bool IsCsv(string path)
        {
            if (File.Exists(path))
            {
                string ext = Path.GetExtension(path);
                return ".CSV".Equals(ext, StringComparison.OrdinalIgnoreCase);
            }
            return false;
        }

        public static bool IsTexte(string path)
        {
            if (File.Exists(path))
            {
                string ext = Path.GetExtension(path);
                return ".TXT".Equals(ext, StringComparison.OrdinalIgnoreCase);
            }
            return false;
        }


    }
}
