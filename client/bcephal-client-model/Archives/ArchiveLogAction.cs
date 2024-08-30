using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Bcephal.Models.Archives
{
    public class ArchiveLogAction
    {

        public static ArchiveLogAction CREATION = new ArchiveLogAction("CREATION", "Creation");
        public static ArchiveLogAction ACTIVATION = new ArchiveLogAction("ACTIVATION", "Activation");
        public static ArchiveLogAction DESACTIVATION = new ArchiveLogAction("DESACTIVATION", "Desactivation");
        public static ArchiveLogAction IMPORT = new ArchiveLogAction("IMPORT", "Import");
        public static ArchiveLogAction DELETION = new ArchiveLogAction("DELETION", "Deletionet");


        public String label;
        public String code;


        public ArchiveLogAction(String code, String label)
        {
            this.code = code;
            this.label = label;
        }

        public String GetLabel()
        {
            return label;
        }

        public bool IsCreation()
        {
            return this == CREATION;
        }

        public bool IsActivation()
        {
            return this == ACTIVATION;
        }

        public bool IsDesactivation()
        {
            return this == DESACTIVATION;
        }

        public bool IsImport()
        {
            return this == IMPORT;
        }

        public bool IsDeletion()
        {
            return this == DELETION;
        }

        public override String ToString()
        {
            return GetLabel();
        }

        public static ArchiveLogAction GetByLabel(string label)
        {
            if (label == null) return null;
            if (CREATION.label.Equals(label)) return CREATION;
            if (ACTIVATION.label.Equals(label)) return ACTIVATION;
            if (DESACTIVATION.label.Equals(label)) return DESACTIVATION;
            if (IMPORT.label.Equals(label)) return IMPORT;
            if (DELETION.label.Equals(label)) return DELETION;
            return null;
        }

        public static ArchiveLogAction GetByCode(string code)
        {
            if (code == null) return null;
            if (CREATION.code.Equals(code)) return CREATION;
            if (ACTIVATION.code.Equals(code)) return ACTIVATION;
            if (DESACTIVATION.code.Equals(code)) return DESACTIVATION;
            if (IMPORT.code.Equals(code)) return IMPORT;
            if (DELETION.code.Equals(code)) return DELETION;
            return null;
        }

    }
}
