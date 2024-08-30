using System;
using System.Collections.Generic;
using System.Collections.ObjectModel;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Bcephal.Models.Grids
{
    public class GrilleColumnFixedStyle
    {

        public static GrilleColumnFixedStyle None = new GrilleColumnFixedStyle("None", "None");
        public static GrilleColumnFixedStyle Left = new GrilleColumnFixedStyle("Left", "Left");
        public static GrilleColumnFixedStyle Right = new GrilleColumnFixedStyle("Right", "Right");


        public String label;
        public String code;


        public GrilleColumnFixedStyle(String code, String label)
        {
            this.code = code;
            this.label = label;
        }

        public String getCode()
        {
            return code;
        }

        public String GetLabel()
        {
            return label;
        }

        public override String ToString()
        {
            return GetLabel();
        }

        public static GrilleColumnFixedStyle GetByLabel(String label)
        {
            if (label == null) return None;
            if (None.label.Equals(label)) return None;
            if (Left.label.Equals(label)) return Left;
            if (Right.label.Equals(label)) return Right;
            return None;
        }

        public static GrilleColumnFixedStyle GetByCode(String code)
        {
            if (code == null) return None;
            if (None.code.Equals(code)) return None;
            if (Left.code.Equals(code)) return Left;
            if (Right.code.Equals(code)) return Right;
            return None;
        }

        public static ObservableCollection<GrilleColumnFixedStyle> GetTypes()
        {
            ObservableCollection<GrilleColumnFixedStyle> conditions = new ObservableCollection<GrilleColumnFixedStyle>();
            conditions.Add(None);
            conditions.Add(Left);
            conditions.Add(Right);
            return conditions;
        }
        public bool IsNone()
        {
            return this == None;
        }

        public bool IsLeft()
        {
            return this == Left;
        }

        public bool IsRight()
        {
            return this == Right;
        }
    }

    public static class GrilleColumnFixedStyleExtention
    {
        public static ObservableCollection<string> GetAll(this GrilleColumnFixedStyle grilleColumnFixedStyle, Func<string, string> Localize)
        {
            ObservableCollection<string> operators = new ObservableCollection<string>();
            operators.Add(Localize?.Invoke("None"));
            operators.Add(Localize?.Invoke("LEFT_"));
            operators.Add(Localize?.Invoke("RIGHT_"));
            return operators;
        }

        public static string GetText(this GrilleColumnFixedStyle grilleColumnFixedStyle, Func<string, string> Localize)
        {
            if (grilleColumnFixedStyle.IsNone())
            {
                return Localize?.Invoke("None");
            }
            if (grilleColumnFixedStyle.IsLeft())
            {
                  return Localize?.Invoke("LEFT_");
              
            }
            if (grilleColumnFixedStyle.IsRight())
            {

                 return Localize?.Invoke("RIGHT_");
            }
            return null;
        }

        public static GrilleColumnFixedStyle GetGrilleColumnFixedStyle(this GrilleColumnFixedStyle grilleColumnFixedStyle, string text, Func<string, string> Localize)
        {
            if (!string.IsNullOrWhiteSpace(text))
            {
                if (text.Equals(Localize?.Invoke("None")))
                {
                    return GrilleColumnFixedStyle.None;
                }
                if (text.Equals(Localize?.Invoke("LEFT_")))
                {
                    return GrilleColumnFixedStyle.Left;
                }
                if (text.Equals(Localize?.Invoke("RIGHT_")))
                {
                    return GrilleColumnFixedStyle.Right;
                }
            }
            return GrilleColumnFixedStyle.None;
        }
    }

}
