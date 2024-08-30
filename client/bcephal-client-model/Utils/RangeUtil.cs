using Bcephal.Models.Sheets;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Bcephal.Models.Utils
{
    public class RangeUtil
    {

        public enum Type { ROW, COL }

        public static String WORKBOOK_END = "]";
        public static String SHEET_SEPARATOR = ";";
        public static String SHEET_DELIMITOR = "!";
        public static String RANGE_SEPARATOR = ":";
        public static String CELL_SEPARATOR = "$";
        public static String FORMUALA_SIGN = "=";
        public static String ERROR = "REFERENCE NOT FOUND";

        static String[] alphabet = {"A","B","C","D","E","F","G","H","I","J","K","L","M",
            "N","O","P","Q","R","S","T","U","V","W","X","Y","Z"};
        static String[] numeric = { "0", "1", "2", "3", "4", "5", "6", "7", "8", "9" };



        public static string BuildRefFormula(SpreadSheetCell cell, SpreadSheetCell targetCell, string refFormula)
        {
            string formula = refFormula;
            if (IsFromula(formula))
            {
                formula = formula.Replace(FORMUALA_SIGN, "");
                int first = formula.IndexOf(CELL_SEPARATOR);
                int last = formula.LastIndexOf(CELL_SEPARATOR);
                if(first == 0 && last > 0) //$A$1
                {
                    return FORMUALA_SIGN + formula;
                }

                int fr = GetRowIndex(formula) ;
                int fc = GetColumnIndex(formula);
                int r = fr;
                int c = fc;
                if (first == 0 && last == 0) //$A1
                {
                    r = targetCell.Row - cell.Row + fr + 1;
                }
                if (first > 0)//A$1
                {
                    c = targetCell.Col - cell.Col + fc;
                }
                if (first < 0)//A1
                {
                    r = targetCell.Row - cell.Row + fr + 1;
                    c = targetCell.Col - cell.Col + fc;
                }
                string col = GetColumnName(c);
                formula = FORMUALA_SIGN + col + r;
            }
            return formula;
        }





        public static string GetCellName(string formula)
        {
            string name = null;
            if (IsFromula(formula))
            {
                name = formula.Trim().Substring(1).Trim();
            }
            return name;
        }

        public static bool IsFromula(string formula)
        {
            return formula != null && formula.StartsWith(FORMUALA_SIGN);
        }


        public static String GetCellName(int row, int col)
        {
            return GetColumnName(col) + (row + 1);
        }

        public static String GetColumnName(int value)
        {
            return GetName(value);
        }


        public static int GetColumnIndex(string name)
        {
            string col = "";
            foreach (char c in name.Replace(CELL_SEPARATOR, "").ToUpper().ToCharArray())
            {
                if (!Char.IsDigit(c))
                {                   
                    col += c;
                }
            }
            char[] chars = col.ToCharArray();
            int length = chars.Length;
            if (length == 1)
            {
                return getPosition(chars[0]);
            }
            return getPosition(chars, length - 1);
        }

        public static int GetRowIndex(string name)
        {
            string col = "";
            string row = "";
            foreach (char c in name.Replace(CELL_SEPARATOR, "").ToUpper().ToCharArray())
            {
                if(Char.IsDigit(c))
                {
                    row += c;
                }
                else
                {
                    col += c;
                }
            }
            return int.Parse(row) - 1;
        }



        public static string GetName(int value)
        {
            String[] tab = {"A","B","C","D","E","F","G","H","I","J","K","L","M",
            "N","O","P","Q","R","S","T","U","V","W","X","Y","Z"};
            if (value <= 25)
            {
                return tab[value];
            }
            int r = value % 25;             
            String lastName = tab[r-1];
            int d = (int)(value - r) / 25;
            String name = GetName(d-1);

            return name + lastName;
        }

        protected static int getPosition(char[] chars, int index)
        {
            int length = chars.Length;
            if (index == 0)
            {
                return getPosition(chars[0]);
            }
            int precPos = getPosition(chars, index - 1);
            int pos = getPosition(chars[index]);
            return (26 * precPos) + pos;
        }


        protected static int getPosition(char c)
        {
            char[] tab = {'A','B','C','D','E','F','G','H','I','J','K','L','M',
                    'N','O','P','Q','R','S','T','U','V','W','X','Y','Z'};
            int length = 26;
            for (int i = 0; i < length; i++)
            {
                if (tab[i] == c)
                {
                    return i;
                }
            }
            return 0;
        }

    }
}

