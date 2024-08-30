using Bcephal.Models.Base;
using Bcephal.Models.Filters;
using Bcephal.Models.Grids;
using Newtonsoft.Json;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Bcephal.Models.Sheets
{
    public class SpreadSheetCell : Persistent
    {

        public SpreadSheetType? Type { get; set; }

        public string Name { get; set; }

        public int Col { get; set; }

        public int Row { get; set; }

        public string SheetName { get; set; }

        public int SheetIndex { get; set; }

        public MeasureFilterItem CellMeasure { get; set; }

        public UniverseFilter Filter { get; set; }

        public bool RefreshWhenEdit { get; set; }


        [JsonIgnore] public string FullName { get { return string.Format("{0}:{1}", this.SheetName, this.Name); } }

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

        [JsonIgnore]
        public bool IsNew { get; set; }


        public SpreadSheetCell()
        {
            this.RefreshWhenEdit = false;
        }

        public SpreadSheetCell(Cell cell) : this()
        {
            this.Name = cell.Name;
            this.Row = cell.Row;
            this.Col = cell.Column;
            this.SheetIndex = cell.Sheet;
        }


        public override int CompareTo(object obj)
        {
            if (obj == null || !(obj is SpreadSheetCell)) return 1;
            if (this == obj) return 0;
            if (this.Id.HasValue && this.Id.Equals(((SpreadSheetCell)obj).Id)) return 0;
            if (this.SheetIndex.Equals(((SpreadSheetCell)obj).SheetIndex))
            {
                return this.Name.CompareTo(((SpreadSheetCell)obj).Name);
            }
            return this.FullName.CompareTo(((SpreadSheetCell)obj).FullName);
        }

        //public override bool Equals(Object obj)
        //{
        //    return CompareTo(obj) == 0;
        //}

        public override string ToString()
        {
            return this.Name;
        }


    }
}
