using Bcephal.Models.Utils;
using Newtonsoft.Json;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Bcephal.Models.Sheets
{
    public class RangeItem
    {

        #region Properties

        public int Row1 { get; set; }
        public int Row2 { get; set; }
        public int Column1 { get; set; }
        public int Column2 { get; set; }

        public int Sheet { get; set; }

        public string FirstCellName { get; set; }
        public string LastCellName { get; set; }

        #endregion


        #region Constructors

        public RangeItem() { }

        public RangeItem(int row1, int row2, int column1, int column2, int sheet, string name1, string name2) : this()
        {
            this.Row1 = row1;
            this.Row2 = row2;
            this.Column1 = column1;
            this.Column2 = column2;
            this.Sheet = sheet;
            this.FirstCellName = name1;
            this.LastCellName = name2;
        }

        #endregion


        #region Methos

        /// <summary>
        /// Retoune le nombre de celulles présentes dans la plage.
        /// </summary>
        ///
        [JsonIgnore]
        public int CellCount
        {
            get
            {
                return (this.Row2 - this.Row1 + 1) * (this.Column2 - this.Column1 + 1);
            }
        }

        /// <summary>
        /// Retoune le nom de la plage.
        /// </summary>
        [JsonIgnore]
        public string Name
        {
            get
            {
                string cell1 = FirstCellName;
                string cell2 = LastCellName;
                if (cell1 == cell2) return cell1;
                return cell1 + ":" + cell2;
            }
        }

        public Cell GetFirstCell()
        {
            return new Cell(Row1, Column1, this.Sheet, FirstCellName);
        }

        public Cell GetLastCell()
        {
            return new Cell(Row2, Column2, this.Sheet, LastCellName);
        }


        /// <summary>
        /// La liste des cellules
        /// </summary>
        [JsonIgnore]
        public List<Cell> Cells
        {
            get
            {
                List<Cell> cells = new List<Cell>(0);
                for (int row = this.Row1; row <= this.Row2; row++)
                {
                    for (int col = this.Column1; col <= this.Column2; col++)
                    {
                        cells.Add(new Cell(row, col, this.Sheet, null));
                    }
                }
                return cells;
            }
        }

        /// <summary>
        /// 
        /// </summary>
        /// <param name="row"></param>
        /// <param name="col"></param>
        /// <returns></returns>
        public bool Contains(int row, int col, int sheet)
        {
            return sheet == this.Sheet 
                && row >= this.Row1 && row <= this.Row2
                && col >= this.Column1 && col <= this.Column2;
        }

        /// <summary>
        /// 
        /// </summary>
        /// <returns></returns>
        public override string ToString()
        {
            return Name;
        }

        public void PerformCellAction(Action<Cell> action)
        {
            bool firstCell = false;
            bool lastCell = false;
            for (int row = this.Row1; row <= this.Row2; row++)
            {
                for (int col = this.Column1; col <= this.Column2; col++)
                {
                    firstCell = row == this.Row1 && col == this.Column1;
                    lastCell = row == this.Row2 && col == this.Column2;
                    Cell cell = new Cell(row, col, this.Sheet, null);
                    action.Invoke(cell);
                }
            }
        }

        //public void PerformCellAction(CellAction action)
        //{
        //    bool firstCell = false;
        //    bool lastCell = false;
        //    for (int row = this.Row1; row <= this.Row2; row++)
        //    {
        //        for (int col = this.Column1; col <= this.Column2; col++)
        //        {
        //            firstCell = row == this.Row1 && col == this.Column1;
        //            lastCell = row == this.Row2 && col == this.Column2;
        //            Cell cell = new Cell(row, col);
        //            action.PerformAction(cell, firstCell, lastCell);
        //        }
        //    }
        //}

        #endregion

    }
}
