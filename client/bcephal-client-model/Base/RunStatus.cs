using System;
using System.Collections.Generic;
using System.Collections.ObjectModel;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Bcephal.Models.Base
{
    public class RunStatus
    {

        public static RunStatus IN_PROGRESS = new RunStatus("IN_PROGRESS", "In Progress");
        public static RunStatus ENDED = new RunStatus("ENDED", "Ended");
        public static RunStatus ERROR = new RunStatus("ERROR", "Error");
        public static RunStatus STOPPED = new RunStatus("STOPPED", "Stopped");

        public String label;
        public String code;

        public RunStatus(String code, String label)
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

        public static RunStatus GetByCode(String code)
        {
            if (code == null) return null;
            if (IN_PROGRESS.code.Equals(code)) return IN_PROGRESS;
            if (ENDED.code.Equals(code)) return ENDED;
            if (ERROR.code.Equals(code)) return ERROR;
            if (STOPPED.code.Equals(code)) return STOPPED;
            return null;
        }


        public static ObservableCollection<RunStatus> GetModes()
        {
            ObservableCollection<RunStatus> modes = new ObservableCollection<RunStatus>();
            modes.Add(IN_PROGRESS);
            modes.Add(ENDED);
            modes.Add(ERROR);
            modes.Add(STOPPED);
            return modes;
        }

    }
}
