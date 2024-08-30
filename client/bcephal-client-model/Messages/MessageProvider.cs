using System;
using System.Collections.Generic;
using System.Collections.ObjectModel;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Bcephal.Models.Messages
{
    public class MessageProvider
    {
        public static MessageProvider MANUAL = new MessageProvider("M", "M");
        public static MessageProvider AUTOMATIC = new MessageProvider("A", "A");

        public String label;
        public String code;

        public MessageProvider(String code, String label)
        {
            this.code = code;
            this.label = label;
        }

        public String GetLabel()
        {
            return label;
        }

        public bool IsMANUAL()
        {
            return this == MANUAL;
        }

        public bool IsAUTOMATIC()
        {
            return this == AUTOMATIC;
        }

        public override String ToString()
        {
            return GetLabel();
        }

        public static MessageProvider GetByLabel(string label)
        {
            if (label == null) return null;
            if (MANUAL.label.Equals(label)) return MANUAL;
            if (AUTOMATIC.label.Equals(label)) return AUTOMATIC;
            return null;
        }

        public static MessageProvider GetByCode(string code)
        {
            if (code == null) return null;
            if (MANUAL.code.Equals(code)) return MANUAL;
            if (AUTOMATIC.code.Equals(code)) return AUTOMATIC;
            return null;
        }

        public static ObservableCollection<MessageProvider> GetAll()
        {
            ObservableCollection<MessageProvider> operators = new ObservableCollection<MessageProvider>();
            operators.Add(MessageProvider.MANUAL);
            operators.Add(MessageProvider.AUTOMATIC);
            return operators;
        }
    }
}
