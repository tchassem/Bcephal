using System;
using System.Collections.Generic;
using System.Collections.ObjectModel;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Bcephal.Models.Messages
{
    public class MessageLogStatus
    {
        public static MessageLogStatus SENDED = new MessageLogStatus("SENDED", "Sended");
        public static MessageLogStatus FAILED = new MessageLogStatus("FAIL", "Failed");
        public static MessageLogStatus PENDING = new MessageLogStatus("IN_PROCESS", "Pending");
        public static MessageLogStatus NEW = new MessageLogStatus("NEW", "New");



        public String label;
        public String code;


        public MessageLogStatus(String code, String label)
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

        public bool IsNew()
        {
            return this == NEW;
        }


        public override String ToString()
        {
            return GetLabel();
        }

        public static MessageLogStatus GetByLabel(string label)
        {
            if (label == null) return null;
            if (SENDED.label.Equals(label)) return SENDED;
            if (FAILED.label.Equals(label)) return FAILED;
            if (PENDING.label.Equals(label)) return PENDING;
            if (NEW.label.Equals(label)) return NEW;
            return null;
        }

        public static MessageLogStatus GetByCode(string code)
        {
            if (code == null) return null;
            if (SENDED.code.Equals(code)) return SENDED;
            if (FAILED.code.Equals(code)) return FAILED;
            if (PENDING.code.Equals(code)) return PENDING;
            if (NEW.code.Equals(code)) return NEW;
            return PENDING;
        }


        public static ObservableCollection<MessageLogStatus> GetAll()
        {
            ObservableCollection<MessageLogStatus> operators = new ObservableCollection<MessageLogStatus>();
            operators.Add(MessageLogStatus.SENDED);
            operators.Add(MessageLogStatus.FAILED);
            operators.Add(MessageLogStatus.NEW);
            operators.Add(MessageLogStatus.PENDING);
            return operators;
        }
    }
}
