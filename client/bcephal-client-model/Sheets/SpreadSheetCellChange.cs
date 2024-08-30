using Bcephal.Models.Utils;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Bcephal.Models.Sheets
{
    public class SpreadSheetCellChange
    {

        #region Properties

        public SpreadSheetCell Cell { get; set; }

        public Range Range { get; set; }

        public bool Reset { get; set; }

        public bool CellMeasureChanged { get; set; }

        public bool MeasureFilterChanged { get; set; }

        public bool AttributeFilterChanged { get; set; }

        public bool PeriodFilterChanged { get; set; }

        public bool SpotFilterChanged { get; set; }

        #endregion


        #region Constructors

        public SpreadSheetCellChange()
        {
            
        }

        public SpreadSheetCellChange(Range range) : this()
        {
            this.Range = range;
        }

        public SpreadSheetCellChange(SpreadSheetCell cell) : this()
        {
            this.Cell = new SpreadSheetCell()
            {
                Row = cell.Row,
                Col = cell.Col,
                Name = cell.Name,
                SheetIndex = cell.SheetIndex,
                SheetName = cell.SheetName,
            };
        }


        #endregion


        #region Operations

        public SpreadSheetCell Apply(SpreadSheetCell reference)
        {
            if (this.Cell != null)
            {
                if (CellMeasureChanged && this.Cell.CellMeasure != null)
                {
                    if (reference.CellMeasure == null)
                    {
                        reference.CellMeasure = new Filters.MeasureFilterItem();
                    }
                    string formula = this.Cell.CellMeasure.Formula;
                    formula = RangeUtil.BuildRefFormula(this.Cell, reference, formula);
                    reference.CellMeasure.Synchronize(this.Cell.CellMeasure, formula);
                }
                if (MeasureFilterChanged && this.Cell.Filter != null)
                {
                    if (reference.Filter == null)
                    {
                        reference.Filter = new Grids.UniverseFilter();
                    }
                    reference.Filter.MeasureFilter.Synchronize(this.Cell, reference, this.Cell.Filter.MeasureFilter);
                }
                if (PeriodFilterChanged && this.Cell.Filter != null)
                {
                    if (reference.Filter == null)
                    {
                        reference.Filter = new Grids.UniverseFilter();
                    }
                    reference.Filter.PeriodFilter.Synchronize(this.Cell, reference, this.Cell.Filter.PeriodFilter);
                }
                if (AttributeFilterChanged && this.Cell.Filter != null)
                {
                    if (reference.Filter == null)
                    {
                        reference.Filter = new Grids.UniverseFilter();
                    }
                    reference.Filter.AttributeFilter.Synchronize(this.Cell, reference, this.Cell.Filter.AttributeFilter);
                }
                if (SpotFilterChanged && this.Cell.Filter != null)
                {
                    if (reference.Filter == null)
                    {
                        reference.Filter = new Grids.UniverseFilter();
                    }
                    reference.Filter.SpotFilter.Synchronize(this.Cell, reference, this.Cell.Filter.SpotFilter);
                }
            }
            return reference;
        }

        public bool Contains(SpreadSheetCell cell)
        {
            return this.Range != null ? this.Range.Contains(cell.Row, cell.Col, cell.SheetIndex): false;
        }

        #endregion


        #region Utils

        #endregion

    }

}
