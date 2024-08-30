using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Bcephal.Models.Reconciliation
{
    public class RecoGridPosition
    {

        public static RecoGridPosition COLUMN = new RecoGridPosition("COLUMN", "Column");
        public static RecoGridPosition ROW = new RecoGridPosition("ROW", "Row");

        public String label;
        public String code;


        public RecoGridPosition(String code, String label)
        {
            this.code = code;
            this.label = label;
        }

        public String getLabel()
        {
            return label;
        }


        public override String ToString()
        {
            return getLabel();
        }

        public static RecoGridPosition getByCode(String code)
        {
            if (code == null) return null;
            if (COLUMN.code.Equals(code)) return COLUMN;
            if (ROW.code.Equals(code)) return ROW;
            return COLUMN;
        }

    }
}

