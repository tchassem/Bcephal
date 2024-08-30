using Bcephal.Models.Base;
using Bcephal.Models.Grids;
using Newtonsoft.Json;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Bcephal.Models.Sheets
{
    public class SpreadSheet : MainObject
    {

        public SpreadSheetType Type { get; set; }

        public SpreadSheetSource SourceType { get; set; }

        public long? SourceId { get; set; }

        public bool UseLink { get; set; }

        public bool Active { get; set; }

        public bool Template { get; set; }

        public string FileName { get; set; }

        public UniverseFilter Filter { get; set; }

        [JsonIgnore]
        public ListChangeHandler<SpreadSheetCell> CellListChangeHandler { get; set; }


        [JsonIgnore]
        public bool IsReport
        {
            get { return this.Type == SpreadSheetType.REPORT; }
        }

        [JsonIgnore]
        public bool IsInput
        {
            get { return this.Type == SpreadSheetType.INPUT; }
        }


        public SpreadSheet()
        {
            this.CellListChangeHandler = new ListChangeHandler<SpreadSheetCell>();
        }



        public void AddCell(SpreadSheetCell cell, bool sort = true)
        {
            cell.IsNew = false;
            CellListChangeHandler.AddNew(cell, sort);
        }

        public void UpdateCell(SpreadSheetCell cell, bool sort = true)
        {
            cell.IsNew = false;
            CellListChangeHandler.AddUpdated(cell, sort);
        }

        public void InsertCell(int position, SpreadSheetCell cell)
        {
            CellListChangeHandler.AddNew(cell);
        }


        public void DeleteOrForgetCell(SpreadSheetCell cell)
        {
            if (cell.IsPersistent)
            {
                DeleteCell(cell);
            }
            else
            {
                ForgetCell(cell);
            }
        }

        public void DeleteCell(SpreadSheetCell cell)
        {
            CellListChangeHandler.AddDeleted(cell);            
        }

        public void ForgetCell(SpreadSheetCell cell)
        {
            CellListChangeHandler.forget(cell);            
        }



        public SpreadSheetCell GetCell(Cell refetrence)
        {
            return GetCellAt(refetrence.Row, refetrence.Column, refetrence.Sheet);
        }

        public SpreadSheetCell GetCellAt(int row, int column, int sheet)
        {
            foreach (SpreadSheetCell cell in CellListChangeHandler.Items)
            {
                if(cell.SheetIndex == sheet && cell.Col == column && cell.Row == row)
                {
                    return cell;
                }
            }
            return null;
        }


    }
}
