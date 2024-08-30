using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Bcephal.Models.Archives
{
    public class ArchiveStatus
    {

        public static ArchiveStatus ARCHIVED = new ArchiveStatus("ARCHIVED", "Archived");
        public static ArchiveStatus DISABLED = new ArchiveStatus("DISABLED", "Disabled");
        public static ArchiveStatus ENABLED = new ArchiveStatus("ENABLED", "Enabled");
        public static ArchiveStatus IMPORTED = new ArchiveStatus("IMPORTED", "Imported");
        public static ArchiveStatus DELETED = new ArchiveStatus("DELETED", "Deleted");


        public String label;
        public String code;


        public ArchiveStatus(String code, String label)
        {
            this.code = code;
            this.label = label;
        }

        public String GetLabel()
        {
            return label;
        }

        public bool IsArchived()
        {
            return this == ARCHIVED;
        }

        public bool IsDisabled()
        {
            return this == DISABLED;
        }

        public bool IsEnabled()
        {
            return this == ENABLED;
        }

        public bool IsImported()
        {
            return this == IMPORTED;
        }

        public bool IsDeleted()
        {
            return this == DELETED;
        }

        public override String ToString()
        {
            return GetLabel();
        }

        public static ArchiveStatus GetByLabel(string label)
        {
            if (label == null) return null;
            if (ARCHIVED.label.Equals(label)) return ARCHIVED;
            if (DISABLED.label.Equals(label)) return DISABLED;
            if (ENABLED.label.Equals(label)) return ENABLED;
            if (IMPORTED.label.Equals(label)) return IMPORTED;
            if (DELETED.label.Equals(label)) return DELETED;
            return null;
        }

        public static ArchiveStatus GetByCode(string code)
        {
            if (code == null) return null;
            if (ARCHIVED.code.Equals(code)) return ARCHIVED;
            if (DISABLED.code.Equals(code)) return DISABLED;
            if (ENABLED.code.Equals(code)) return ENABLED;
            if (IMPORTED.code.Equals(code)) return IMPORTED;
            if (DELETED.code.Equals(code)) return DELETED;
            return null;
        }

    }
}
