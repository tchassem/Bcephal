using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Bcephal.Models.Loaders
{
    public class FileExtensions
    {

        public static FileExtensions ALL = new FileExtensions("ALL", "All (.*)", "");
        public static FileExtensions CSV = new FileExtensions("CSV", "CSV (.csv)", ".csv");
        public static FileExtensions TXT = new FileExtensions("TXT", "TEXT (.txt)", ".txt");
        public static FileExtensions EXCEL = new FileExtensions("EXCEL", "EXCEL (.xslx)", ".xslx");

        public String label;
        public String code;
        public String pattern;

        public FileExtensions(String code, String label, String pattern)
        {
            this.code = code;
            this.label = label;
            this.pattern = pattern;
        }

        public String getCode()
        {
            return code;
        }


        public override String ToString()
        {
            return label;
        }

        public static FileExtensions GetByCode(String code)
        {
            if (code == null) return null;
            if (ALL.code.Equals(code)) return ALL;
            if (CSV.code.Equals(code)) return CSV;
            if (TXT.code.Equals(code)) return TXT;
            if (EXCEL.code.Equals(code)) return EXCEL;
            return null;
        }

    }
}
