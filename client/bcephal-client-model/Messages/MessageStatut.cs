using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Bcephal.Models.Messages
{
    public class MessageStatut
    {
        public static MessageStatut SENDED = new MessageStatut("SENDED", "Sended");
        public static MessageStatut FAILED = new MessageStatut("FAILED", "Failed");
        public static MessageStatut PENDING = new MessageStatut("PENDING", "Pending");



        public String label;
        public String code;


        public MessageStatut(String code, String label)
        {
            this.code = code;
            this.label = label;
        }

        public String GetLabel()
        {
            return label;
        }

        public bool IsSended()
        {
            return this == SENDED;
        }

        public bool IsFailed()
        {
            return this == FAILED;
        }

        public bool IsPending()
        {
            return this == PENDING;
        }


        public override String ToString()
        {
            return GetLabel();
        }

        public static MessageStatut GetByLabel(string label)
        {
            if (label == null) return null;
            if (SENDED.label.Equals(label)) return SENDED;
            if (FAILED.label.Equals(label)) return FAILED;
            if (PENDING.label.Equals(label)) return PENDING;
            return null;
        }

        public static MessageStatut GetByCode(string code)
        {
            if (code == null) return null;
            if (SENDED.label.Equals(code)) return SENDED;
            if (FAILED.label.Equals(code)) return FAILED;
            if (PENDING.label.Equals(code)) return PENDING;
            return null;
        }
    }
}
