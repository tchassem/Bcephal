using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Bcephal.Blazor.Web.Base.Shared.Component.Splitter
{
    public class SplitterSettings
    {
        public int index { get; set; }
        public string ID { get; private set; }
        public bool Vertical { get; set; } = false;
        public bool IsDiagonal { get; set; } = false;
        public double width { get; set; } = 100;
        public double height { get; set; } = 5;
        public double size { get; set; } = 5;
        public string BgColor { get; set; } = "";//"orange" "silver";

        public string Style_ { get; set; } = "";

        public SplitterSettings(string ScrollBarID = "Splitter")
        {
            if (string.IsNullOrEmpty(ScrollBarID))
            {
                ID = ScrollBarID + Guid.NewGuid().ToString("d");
            }
            else
            {
                ID = "Splitter" + Guid.NewGuid().ToString("d");
            }
        }

        internal string GetStyle()
        {
            StringBuilder Style = new StringBuilder();
            Style.Append(Style_);
            if (Vertical)
            {
                Style.Append("width:" + size + "px;height:auto;");
            }
            else
            {
                Style.Append("width:100%;height:" + size + "px;");
            }
            //else
            //{
            //    if (IsDiagonal)
            //    {
            //        Style.Append("width:" + width + "px;height:" + height + "px;");
            //    }
            //    else
            //    {
            //        Style.Append("width:5px;height:auto;");
            //    }
            //}

            if (!Vertical)
            {
                Style.Append("display:inline-block;");
            }
            Style.Append("background-color:" + BgColor + ";");
            if (IsDiagonal)
            {
                Style.Append("cursor:nwse-resize;");
            }
            else
            {
                if (!Vertical)
                {
                    Style.Append("cursor:s-resize;");
                }
                else
                {
                    Style.Append("cursor:e-resize;");
                }
            }
            return Style.ToString();
        }
    }
}
