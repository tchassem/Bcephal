using System;
using System.Collections.Generic;
using System.Collections.ObjectModel;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Bcephal.Models.Routines
{
    public class RoutineExecutorType
    {

        public static RoutineExecutorType PRE = new RoutineExecutorType("PRE", "Pre");
        public static RoutineExecutorType POST = new RoutineExecutorType("POST", "Post");

        public string label;
        public string code;


        public RoutineExecutorType(string code, string label)
        {
            this.code = code;
            this.label = label;
        }

        public String GetLabel()
        {
            return label;
        }
        public string getCode()
        {
            return code;
        }
        public override String ToString()
        {
            return GetLabel();
        }
        public bool IsPre()
        {
            return this == PRE;
        }

        public bool IsPost()
        {
            return this == POST;
        }

        public static RoutineExecutorType GetByCode(string code)
        {
            if (code == null) return POST;
            if (PRE.code.Equals(code)) return PRE;
            if (POST.code.Equals(code)) return POST;
            return null;
        }
        public static RoutineExecutorType GetByLabel(string label)
        {
            if (label == null) return null;
            if (PRE.label.Equals(label)) return PRE;
            if (POST.label.Equals(label)) return POST;
            return null;
        }

        public static ObservableCollection<RoutineExecutorType> GetAll()
        {
            ObservableCollection<RoutineExecutorType> conditions = new ObservableCollection<RoutineExecutorType>();
            conditions.Add(PRE);
            conditions.Add(POST);
            return conditions;
        }
    }
    public static class RoutineExecutorTypeExtention
    {

        public static ObservableCollection<string> GetAllRoutineExecutorTypes(this RoutineExecutorType routineExecutorType, Func<string, string> Localize)
        {
            ObservableCollection<string> operators = new ObservableCollection<string>
            {
                Localize?.Invoke("PRE"),
                Localize?.Invoke("POST")
            };
            return operators;
        }

        public static string GetText(this RoutineExecutorType routineExecutorType, Func<string, string> Localize)
        {
            if (routineExecutorType.IsPre())
            {
                return Localize?.Invoke("PRE");
            }
            if (routineExecutorType.IsPost())
            {
                return Localize?.Invoke("POST");
            }
            
            return null;
        }

        public static RoutineExecutorType GetRoutineExecutorType(this RoutineExecutorType routineExecutorType, string text, Func<string, string> Localize)
        {
            if (!string.IsNullOrWhiteSpace(text))
            {
                if (text.Equals(Localize?.Invoke("PRE")))
                {
                    return RoutineExecutorType.PRE;
                }
                if (text.Equals(Localize?.Invoke("POST")))
                {
                    return RoutineExecutorType.POST;
                }
               
            }
            return RoutineExecutorType.PRE;
        }
    }
}
