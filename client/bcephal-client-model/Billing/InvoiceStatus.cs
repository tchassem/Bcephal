using System;
using System.Collections.Generic;
using System.Collections.ObjectModel;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Bcephal.Models.Billing
{
    public class InvoiceStatus
    {

        public static InvoiceStatus DRAFT = new InvoiceStatus("DRAFT", "Draft");
        public static InvoiceStatus VALIDATED = new InvoiceStatus("VALIDATED", "Validated");

        public String label;
        public String code;

        public InvoiceStatus(String code, String label)
        {
            this.code = code;
            this.label = label;
        }

        public String getCode()
        {
            return code;
        }


        public override String ToString()
        {
            return label;
        }

        public static InvoiceStatus GetByCode(String code)
        {
            if (code == null) return null;
            if (DRAFT.code.Equals(code)) return DRAFT;
            if (VALIDATED.code.Equals(code)) return VALIDATED;
            return null;
        }

        public static ObservableCollection<InvoiceStatus> GetStatus()
        {
            ObservableCollection<InvoiceStatus> conditions = new ObservableCollection<InvoiceStatus>();
            conditions.Add(DRAFT);
            conditions.Add(VALIDATED);
            return conditions;
        }

    }
}
