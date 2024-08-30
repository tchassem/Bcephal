using System;
using System.Collections.Generic;
using System.Collections.ObjectModel;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Bcephal.Models.Loaders
{
    public class FileLoaderMethod
    {
        public static FileLoaderMethod NEW_GRID = new FileLoaderMethod("NEW_GRID", "New Grid");
        public static FileLoaderMethod DIRECT_TO_GRID = new FileLoaderMethod("DIRECT_TO_GRID", "Direct to Grid");
        public static FileLoaderMethod VIA_AUTOMATIC_SOURCING = new FileLoaderMethod("VIA_AUTOMATIC_SOURCING", "Via Automatic Sourcing");
        public static FileLoaderMethod VIA_INPUT_TABLE = new FileLoaderMethod("VIA_INPUT_TABLE", "Via Input Table");

        public String label;
        public String code;

        public FileLoaderMethod(String code, String label)
        {
            this.code = code;
            this.label = label;
        }

        public String getCode()
        {
            return code;
        }

        public bool IsNewGrid()
        {
            return this == NEW_GRID;
        }

        public bool IsDirectToGrid()
        {
            return this == DIRECT_TO_GRID;
        }


        public override String ToString()
        {
            return label;
        }

        public static FileLoaderMethod GetByCode(String code)
        {
            if (code == null) return null;
            if (NEW_GRID.code.Equals(code)) return NEW_GRID;
            if (DIRECT_TO_GRID.code.Equals(code)) return DIRECT_TO_GRID;
            if (VIA_AUTOMATIC_SOURCING.code.Equals(code)) return VIA_AUTOMATIC_SOURCING;
            if (VIA_INPUT_TABLE.code.Equals(code)) return VIA_INPUT_TABLE;
            return null;
        }

        public static ObservableCollection<FileLoaderMethod> GetMethods()
        {
            ObservableCollection<FileLoaderMethod> conditions = new ObservableCollection<FileLoaderMethod>();
            conditions.Add(DIRECT_TO_GRID);
            conditions.Add(NEW_GRID);
            //conditions.Add(VIA_AUTOMATIC_SOURCING);
            //conditions.Add(VIA_INPUT_TABLE);
            return conditions;
        }

    }

    public static class FileLoaderMethodExtention
    {
        public static ObservableCollection<string> GetAll(this FileLoaderMethod fileLoaderMethod, Func<string, string> Localize)
        {
            ObservableCollection<string> operators = new ObservableCollection<string>();
            //operators.Add(null);
            operators.Add(Localize?.Invoke("DIRECT_TO_GRID"));
            operators.Add(Localize?.Invoke("NEW_GRID"));
            //operators.Add(Localize?.Invoke("VIA_AUTOMATIC_SOURCING"));
            //operators.Add(Localize?.Invoke("VIA_INPUT_TABLE"));
            return operators;
        }

        public static string GetText(this FileLoaderMethod fileLoaderMethod, Func<string, string> Localize)
        {
            if (fileLoaderMethod.IsNewGrid())
            {
                return Localize?.Invoke("NEW_GRID");
            }
            if (fileLoaderMethod.IsDirectToGrid())
            {
                return Localize?.Invoke("DIRECT_TO_GRID");
            }
            return null;
        }

        public static FileLoaderMethod GetFileLoaderMethod(this FileLoaderMethod fileLoaderMethod, string text, Func<string, string> Localize)
        {
            if (!string.IsNullOrWhiteSpace(text))
            {
                if (text.Equals(Localize?.Invoke("NEW_GRID")))
                {
                    return FileLoaderMethod.NEW_GRID;
                }
                if (text.Equals(Localize?.Invoke("DIRECT_TO_GRID")))
                {
                    return FileLoaderMethod.DIRECT_TO_GRID;
                }
            }
            return null;
        }
    }
}
