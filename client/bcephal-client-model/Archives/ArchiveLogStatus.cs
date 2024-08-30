using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Bcephal.Models.Archives
{
    public class ArchiveLogStatus
    {

        public static ArchiveLogStatus SUCCESS = new ArchiveLogStatus("SUCCESS", "Archived");
        public static ArchiveLogStatus WARNING = new ArchiveLogStatus("WARNING", "Warning");
        public static ArchiveLogStatus ERROR = new ArchiveLogStatus("ERROR", "Error");


        public String label;
        public String code;

        public ArchiveLogStatus(String code, String label)
        {
            this.code = code;
            this.label = label;
        }

        public String GetLabel()
        {
            return label;
        }

        public bool IsSuccess()
        {
            return this == SUCCESS;
        }

        public bool IsWarning()
        {
            return this == WARNING;
        }

        public bool IsError()
        {
            return this == ERROR;
        }

        public override String ToString()
        {
            return GetLabel();
        }

        public static ArchiveLogStatus GetByLabel(string label)
        {
            if (label == null) return null;
            if (SUCCESS.label.Equals(label)) return SUCCESS;
            if (WARNING.label.Equals(label)) return WARNING;
            if (ERROR.label.Equals(label)) return ERROR;
            return null;
        }

        public static ArchiveLogStatus GetByCode(string code)
        {
            if (code == null) return null;
            if (SUCCESS.code.Equals(code)) return SUCCESS;
            if (WARNING.code.Equals(code)) return WARNING;
            if (ERROR.code.Equals(code)) return ERROR;
            return null;
        }

    }
}
