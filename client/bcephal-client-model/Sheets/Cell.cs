using Bcephal.Models.Utils;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Bcephal.Models.Sheets
{
    public class Cell
    {

        public int Row { get; set; }
        public int Column { get; set; }

        public int Sheet { get; set; }

        private string name;
        public string Name 
        {
            get
            {
                if (string.IsNullOrWhiteSpace(name))
                {
                    name = RangeUtil.GetCellName(Row, Column);
                }
                return name;
            }
            set { name = value; }
        }

        public Cell() { }

        public Cell(int row, int column, int sheet, string name)
        {
            this.Row = row;
            this.Column = column;
            this.Sheet = sheet;
            this.Name = name;
        }

        public override string ToString()
        {
            return this.Name;
        }

    }
}
