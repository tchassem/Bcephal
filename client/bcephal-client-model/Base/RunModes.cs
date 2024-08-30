using System;
using System.Collections.Generic;
using System.Collections.ObjectModel;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Bcephal.Models.Base
{
    public class RunModes
    {
        public static RunModes A = new RunModes("A", "Automatic");
        public static RunModes M = new RunModes("M", "Manual");

        public String label;
        public String code;

        public RunModes(String code, String label)
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

        public static RunModes GetByCode(String code)
        {
            if (code == null) return null;
            if (A.code.Equals(code)) return A;
            if (M.code.Equals(code)) return M;
            return null;
        }


        public static ObservableCollection<RunModes> GetModes()
        {
            ObservableCollection<RunModes> modes = new ObservableCollection<RunModes>();
            modes.Add(A);
            modes.Add(M);
            return modes;
        }
    }
}
