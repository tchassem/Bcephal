using System;
using System.Collections.Generic;
using System.Collections.ObjectModel;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Bcephal.Models.Messages
{
    public class MessageType
    {
        public static MessageType EMAIL = new MessageType("MAIL", "MAIL");
        public static MessageType SMS = new MessageType("SMS", "SMS");

        public String label;
        public String code;

        public MessageType(String code, String label)
        {
            this.code = code;
            this.label = label;
        }

        public String GetLabel()
        {
            return label;
        }

        public bool IsEMAIL()
        {
            return this == EMAIL;
        }

        public bool IsSMS()
        {
            return this == SMS;
        }

        public override String ToString()
        {
            return GetLabel();
        }

        public static MessageType GetByLabel(string label)
        {
            if (label == null) return null;
            if (EMAIL.label.Equals(label)) return EMAIL;
            if (SMS.label.Equals(label)) return SMS;
            return null;
        }

        public static MessageType GetByCode(string code)
        {
            if (code == null) return null;
            if (EMAIL.code.Equals(code)) return EMAIL;
            if (SMS.code.Equals(code)) return SMS;
            return null;
        }

        public static ObservableCollection<MessageType> GetAll()
        {
            ObservableCollection<MessageType> operators = new ObservableCollection<MessageType>();
            operators.Add(MessageType.EMAIL);
            operators.Add(MessageType.SMS);
            return operators;
        }
    }
}
