using System;
using System.Collections.ObjectModel;

namespace Bcephal.Models.Joins
{
    public class JoinPublicationMethod
    {
        public static JoinPublicationMethod NEW_GRID = new JoinPublicationMethod("NEW_GRID", "New grid");
        public static JoinPublicationMethod APPEND = new JoinPublicationMethod("APPEND", "Append");
        public static JoinPublicationMethod REPLACE = new JoinPublicationMethod("REPLACE", "Replace");

        public string label;
        public string code;

        public JoinPublicationMethod(string code, string label)
        {
            this.code = code;
            this.label = label;
        }

        public string GetLabel()
        {
            return label;
        }

        public bool IsNewGrid()
        {
            return this == NEW_GRID;
        }

        public bool IsAppend()
        {
            return this == APPEND;
        }

        public bool IsReplace()
        {
            return this == REPLACE;
        }

        public override string ToString()
        {
            return GetLabel();
        }

        public static JoinPublicationMethod GetByLabel(string label)
        {
            if (label == null) return null;
            if (NEW_GRID.label.Equals(label)) return NEW_GRID;
            if (APPEND.label.Equals(label)) return APPEND;
            if (REPLACE.label.Equals(label)) return REPLACE;
            return null;
        }

        public static JoinPublicationMethod GetByCode(string code)
        {
            if (code == null) return null;
            if (NEW_GRID.code.Equals(code)) return NEW_GRID;
            if (APPEND.code.Equals(code)) return APPEND;
            if (REPLACE.code.Equals(code)) return REPLACE;
            return null;
        }

        public static ObservableCollection<JoinPublicationMethod> GetAll()
        {
            ObservableCollection<JoinPublicationMethod> operators = new ObservableCollection<JoinPublicationMethod>();
            operators.Add(JoinPublicationMethod.NEW_GRID);
            operators.Add(JoinPublicationMethod.APPEND);
            operators.Add(JoinPublicationMethod.REPLACE);
            return operators;
        }
    }

    public static class JoinPublicationMethodExtention
    {
        public static ObservableCollection<string> GetAll(this JoinPublicationMethod joinPublicationMethod, Func<string, string> Localize)
        {
            ObservableCollection<string> operators = new ObservableCollection<string>();
            operators.Add(null);
            operators.Add(Localize?.Invoke("NEW_GRID"));
            operators.Add(Localize?.Invoke("APPEND"));
            operators.Add(Localize?.Invoke("REPLACE"));
            return operators;
        }

        public static string GetText(this JoinPublicationMethod joinPublicationMethod, Func<string, string> Localize)
        {
            if (joinPublicationMethod.IsNewGrid())
            {
                return Localize?.Invoke("NEW_GRID");
            }
            if (joinPublicationMethod.IsAppend())
            {
                return Localize?.Invoke("APPEND");
            }
            if (joinPublicationMethod.IsReplace())
            {
                return Localize?.Invoke("REPLACE");
            }
            return null;
        }

        public static JoinPublicationMethod GetClientStatus(this JoinPublicationMethod joinPublicationMethod, string text, Func<string, string> Localize)
        {
            if (!string.IsNullOrWhiteSpace(text))
            {
                if (text.Equals(Localize?.Invoke("NEW_GRID")))
                {
                    return JoinPublicationMethod.NEW_GRID;
                }
                if (text.Equals(Localize?.Invoke("APPEND")))
                {
                    return JoinPublicationMethod.APPEND;
                }
                if (text.Equals(Localize?.Invoke("REPLACE")))
                {
                    return JoinPublicationMethod.REPLACE;
                }
            }
            return null;
        }
    }
}
