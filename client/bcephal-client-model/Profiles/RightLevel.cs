using System;
using System.Collections.Generic;
using System.Collections.ObjectModel;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Bcephal.Models.Profiles
{
    public class RightLevel
    {

        public static RightLevel NONE = new RightLevel("NONE", "None");
        public static RightLevel VIEW = new RightLevel("VIEW", "View");
        public static RightLevel EXPORT = new RightLevel("EXPORT", "Export");
        public static RightLevel RUN = new RightLevel("RUN", "Run");
        public static RightLevel LOAD = new RightLevel("LOAD", "Load");
        public static RightLevel CLEAR = new RightLevel("CLEAR", "Clear");
        public static RightLevel VALIDATE = new RightLevel("VALIDATE", "Validate");
        public static RightLevel RESET = new RightLevel("RESET", "Reset");
        public static RightLevel ACTION = new RightLevel("ACTION", "Action");
        public static RightLevel EDIT = new RightLevel("EDIT", "Edit");
        public static RightLevel CREATE = new RightLevel("CREATE", "Create");
        public static RightLevel ALL = new RightLevel("ALL", "All");


        public String label;
        public String code;

        public RightLevel(String code, String label)
        {
            this.code = code;
            this.label = label;
        }

        public String GetLabel()
        {
            return label;
        }

        public override String ToString()
        {
            return GetLabel();
        }

        public static RightLevel GetByCode(string code)
        {
            if (code == null) return null;
            if (NONE.code.Equals(code)) return NONE;
            if (VIEW.code.Equals(code)) return VIEW;
            if (EXPORT.code.Equals(code)) return EXPORT;
            if (RUN.code.Equals(code)) return RUN;
            if (LOAD.code.Equals(code)) return LOAD;
            if (CLEAR.code.Equals(code)) return CLEAR;
            if (VALIDATE.code.Equals(code)) return VALIDATE;
            if (RESET.code.Equals(code)) return RESET;
            if (ACTION.code.Equals(code)) return ACTION;
            if (EDIT.code.Equals(code)) return EDIT;
            if (CREATE.code.Equals(code)) return CREATE;
            if (ALL.code.Equals(code)) return ALL;
            return null;
        }

        public static ObservableCollection<RightLevel> GetAll()
        {
            ObservableCollection<RightLevel> operators = new ObservableCollection<RightLevel>();            
            operators.Add(NONE);
            operators.Add(VIEW);
            operators.Add(EXPORT);
            operators.Add(RUN);
            operators.Add(LOAD);
            operators.Add(CLEAR);
            operators.Add(VALIDATE);
            operators.Add(RESET);
            operators.Add(ACTION);
            operators.Add(EDIT);
            operators.Add(CREATE);
            operators.Add(ALL);
            return operators;
        }

    }
}
