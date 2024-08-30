using System;
using System.Collections.Generic;
using System.Collections.ObjectModel;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Bcephal.Models.Base
{
    public class MailSendingStatus
    {

        public static MailSendingStatus NO_YET_SENT = new MailSendingStatus("NO_YET_SENT", "No yet sent");
        public static MailSendingStatus SENT = new MailSendingStatus("SENT", "Sent");
        public static MailSendingStatus ERROR = new MailSendingStatus("ERROR", "Error");

        public String label;
        public String code;

        public MailSendingStatus(String code, String label)
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

        public static MailSendingStatus GetByCode(String code)
        {
            if (code == null) return null;
            if (NO_YET_SENT.code.Equals(code)) return NO_YET_SENT;
            if (SENT.code.Equals(code)) return SENT;
            if (ERROR.code.Equals(code)) return ERROR;
            return null;
        }

        public static ObservableCollection<MailSendingStatus> GetStatus()
        {
            ObservableCollection<MailSendingStatus> conditions = new ObservableCollection<MailSendingStatus>();
            conditions.Add(NO_YET_SENT);
            conditions.Add(SENT);
            conditions.Add(ERROR);
            return conditions;
        }

    }
}
