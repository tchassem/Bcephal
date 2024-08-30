using Bcephal.Models.Base;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.ComponentModel;

namespace Bcephal.Models.Grids
{
    public class GridItem : DataForEdit
    {

        #region Properties

        public static String LEFT_SIDE = "L";
        public static String RIGHT_SIDE = "R";

        public String Side { get; set; }
        public bool IsSelected { get; set; }
        public Object[] Datas { get; set; }

        //public bool IsValidated { get; set; }

        //public bool IsEdit { get; set; } = false;

        #endregion


        #region Constructor

        public GridItem(String side = null) { IsSelected = false; Side = side; }

        public GridItem(Object[] datas, String side = null) : this(side) { IsSelected = false; Datas = datas; }

        #endregion


        #region Operations

        public long? GetId()
        {
            if (Datas == null || this.Datas.Length <= 0) return null;
            object obj = this.Datas[this.Datas.Length - 1];
            if (obj == null) {
                if (this._IsInNewEditMode)
                {
                    return -1;
                }
                return null;
            }
            long oid;
            if (long.TryParse(obj.ToString(), out oid)) return oid;
            
            return null;
        }
        public long? Id
        {
            get { return GetId(); }
        }
        public bool IsLeftSide()
        {
            return !String.IsNullOrWhiteSpace(this.Side) && this.Side == LEFT_SIDE;
        }

        public bool IsRightSide()
        {
            return !String.IsNullOrWhiteSpace(this.Side) && this.Side == RIGHT_SIDE;
        }

        #endregion

    }
}
