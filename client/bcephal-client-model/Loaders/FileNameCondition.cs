using System;
using System.Collections.Generic;
using System.Collections.ObjectModel;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Bcephal.Models.Loaders
{
    public class FileNameCondition
    {

        public static FileNameCondition BEGINS_WITH = new FileNameCondition("BEGINS_WITH", "Begins with");
        public static FileNameCondition ENDS_WITH = new FileNameCondition("ENDS_WITH", "Ends with");
        public static FileNameCondition CONTAINS = new FileNameCondition("CONTAINS", "Contains");
        public static FileNameCondition DO_NOT_CONTAINS = new FileNameCondition("DO_NOT_CONTAINS", "Do not contains");

        public String label;
        public String code;

        public FileNameCondition(String code, String label)
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

        public static FileNameCondition GetByCode(String code)
        {
            if (code == null) return null;
            if (BEGINS_WITH.code.Equals(code)) return BEGINS_WITH;
            if (ENDS_WITH.code.Equals(code)) return ENDS_WITH;
            if (CONTAINS.code.Equals(code)) return CONTAINS;
            if (DO_NOT_CONTAINS.code.Equals(code)) return DO_NOT_CONTAINS;
            return null;
        }


        public static ObservableCollection<FileNameCondition> GetConditions()
        {
            ObservableCollection<FileNameCondition> conditions = new ObservableCollection<FileNameCondition>();
            conditions.Add(BEGINS_WITH);
            conditions.Add(ENDS_WITH);
            conditions.Add(CONTAINS);
            conditions.Add(DO_NOT_CONTAINS);
            return conditions;
        }

    }
}
