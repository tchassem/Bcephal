using Bcephal.Blazor.Web.Base.Shared.Component.Splitter;
using Bcephal.Models.Base;
using Bcephal.Models.Dashboards;
using Microsoft.AspNetCore.Components;
using Microsoft.JSInterop;
using System;
using System.Collections.Generic;
using System.Globalization;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Bcephal.Blazor.Web.Base.Shared.Component.SplitterPane
{
    public partial class SplitterPanelStore : SplitterPanel
    {

        [Parameter] public Dictionary<int, DimensionPanel> Items { get; set; }
        [Parameter] public Bcephal.Models.Dashboards.DashboardLayout Layout { get; set; }
        [Parameter] public bool IsManuallyResize { get; set; } = false;

        [Parameter]
        public int[] Positions { get; set; } 
        protected override void builVWith(Splitter.Splitter item, BoundingClientRect BoundingClientRect)
        {
            if (Items != null  && Items.Any()  && Items[Items.First().Key].Getwidth() > 0)
            {
                buildVWhith_(item, BoundingClientRect);
            }
            else 
            {
                base.builVWith(item, BoundingClientRect);
            }
        }
        protected override void builVHeight(Splitter.Splitter item, BoundingClientRect BoundingClientRect)
        {
            if (Items != null && Items.Any()  && Items[Items.First().Key].GetHeight() > 0)
            {
                builVHeight_(item, BoundingClientRect);
            }
            else
            {
                base.builVHeight(item, BoundingClientRect);
            }
        }



        protected virtual void buildVWhith_(Splitter.Splitter item, BoundingClientRect BoundingClientRect)
        {
            double? size = (BoundingClientRect.Width - item.SplitterSettings.size) / (LeftSize + RightSize);           
            //double? sizeF = (BoundingClientRect.Width - item.SplitterSettings.size);
            int offset = 0;
            //double? totalSize = 0;
            foreach(var ite in Items)
            {
                HDivWidthContent.Add(null);
            }
            while (offset < Items.Count)
            {
                if (Items[offset] != null)
                {

                    double.TryParse(Items[offset].Getwidth().ToString(), NumberStyles.Any, provider, out double value);
                    HDivWidthContent[offset] = value;
                    //totalSize += value;
                    //if ((offset + 1) < HDivWidthContent.Count)
                    //{
                    //    double.TryParse(Items[offset].Getwidth().ToString(), NumberStyles.Any, provider, out double value_);
                    //    HDivWidthContent[offset + 1] = value_;
                    //}
                }
                offset ++;
                ///offset += 2;
            }
            MinHDivWidthContent = size / 3;
            MaxHDivWidthContent = size + MinHDivWidthContent;
        }

        protected void builVHeight_(Splitter.Splitter item, BoundingClientRect BoundingClientRect)
        {
            foreach (var ite in Items)
            {
                VDivHeightContent.Add(null);
            }
            if (RightSizePx.HasValue)
            {
                double size = (BoundingClientRect.Height - item.SplitterSettings.size - RightSizePx.Value);
                int offset = 0;
                double.TryParse(Items[offset].GetHeight().ToString(), NumberStyles.Any, provider, out double value);
                VDivHeightContent[offset] = value;
                VDivHeightContent[offset + 1] = size - value;
                if (size > RightSizePx.Value)
                {
                    MinVDivHeightContent = RightSizePx.Value / 3;
                    MaxVDivHeightContent = RightSizePx.Value + MinVDivHeightContent;
                }
                else
                {
                    MinVDivHeightContent = size / 3;
                    MaxVDivHeightContent = size + MinVDivHeightContent;
                    if (MinVDivHeightContent < DefaultMinHeight)
                    {
                        MinVDivHeightContent = DefaultMinHeight;
                        double? size2 = (BoundingClientRect.Height - item.SplitterSettings.size - RightSizePx.Value) - DefaultMinHeight;
                        MaxVDivHeightContent = size2;
                    }
                }
            }
            else
            {
                double? size = (BoundingClientRect.Height - item.SplitterSettings.size) / (LeftSize + RightSize);
                double? sizeF = (BoundingClientRect.Height - item.SplitterSettings.size);
                if (ISHorizontalFirst)
                {
                    ISHorizontalBuilded = true;
                    ISHorizontalHeight = BoundingClientRect.Height;
                    ISHorizontalHeightSplitter = item.SplitterSettings.size;
                }

                int offset = 0;
                if (Positions != null && Positions.Length > 0 && Items != null && Items.Keys.Count >= 2)
                {
                    double.TryParse(Items[Positions.ElementAt(0)].GetHeight().ToString(), NumberStyles.Any, provider, out double value);
                    double.TryParse(Items[Positions.ElementAt(1)].GetHeight().ToString(), NumberStyles.Any, provider, out double value1);
                    VDivHeightContent[0] = value * LeftSize;
                    VDivHeightContent[1] = value1 * LeftSize;
                    if (Layout.IsVertical3x3() && Positions.Length > 2 && Items.Keys.Count > 2)
                    {
                        double.TryParse(Items[Positions.ElementAt(2)].GetHeight().ToString(), NumberStyles.Any, provider, out double value2);
                        VDivHeightContent[2] = value2 * LeftSize;
                    }
                   
                }
                else
                {
                    while (offset < Items.Count)
                    {
                        if (Items[offset] != null)
                        {
                            double.TryParse(Items[offset].GetHeight().ToString(), NumberStyles.Any, provider, out double value);
                            VDivHeightContent[offset] = value * LeftSize;
                        }
                        offset++;
                    }
                }
               
                MinVDivHeightContent = size / 3;
                MaxVDivHeightContent = size + MinVDivHeightContent;
                if (MinVDivHeightContent < DefaultMinHeight)
                {
                    MinVDivHeightContent = DefaultMinHeight;
                    double? size2 = (BoundingClientRect.Height - item.SplitterSettings.size) - DefaultMinHeight;
                    MaxVDivHeightContent = (int)(size2);
                }
            }
        }
        protected override string cmdGetStyleDivContent(int index)
        {
            string value = "99";

            if (IsManuallyResize )
            {
                value = SetValue(index);
            }
            else
            {
                if (BoundingClientRect != null)
                {
                    if (vertical && HDivWidthContent[index].HasValue)
                    {
                        value = ((HDivWidthContent[index].Value * 100) / BoundingClientRect.Width).ToString(format, provider);
                    }
                    else
                    if (!vertical && VDivHeightContent[index].HasValue)
                    {
                        value = VDivHeightContent[index].Value.ToString(format, provider) + "";
                    }
                }
                else
                {
                    if (vertical)
                    {
                        if (index == 0)
                        {
                            value = "80";
                        }
                        else
                        {
                            value = "20";
                        }
                    }
                    else
                    {
                        if (index == 0)
                        {
                            value = "50";
                        }
                        else
                        {
                            value = "50";
                        }
                    }
                }
            }
            string subStyle = "";
            if (index == 0)
            {
                subStyle += StyleLeft;
            }
            if (index == 1)
            {
                subStyle += StyleRight;
            }
            string style = subStyle;
            if (index < SplitterForm.Count() && SplitterForm[index] != null
                && !string.IsNullOrWhiteSpace(SplitterForm[index].SplitterSettings.Style_))
            {
                SplitterForm[index].SetStyle("");
            }
            if (string.IsNullOrWhiteSpace(style) || !style.Contains("width"))
            {
                style += "width:" + value + "%;";
            }
            else
            {
                if (index < SplitterForm.Count() && SplitterForm[index] != null)
                {
                    SplitterForm[index].SetStyle("display:none");
                }
            }
            if (!vertical)
            {
                if (!ISHorizontalFirst)
                {
                    style = subStyle + "height:" + value + "px;display:block;";
                }
                else
                {
                    if (VDivHeightContent[index].HasValue)
                    {
                        double effectiveContentSize = (ISHorizontalHeightSplitter * 100) / ISHorizontalHeight;
                        double VDivHeightContent__ = (VDivHeightContent[index].Value * 100) / ISHorizontalHeight;
                        value = (VDivHeightContent__ - effectiveContentSize).ToString(format, provider);
                        style = subStyle + "height:" + value + "%;display:block;";
                    }
                }
            }
            return style;

        }

        private string SetValue(int index)
        {
            string value = "99";

            if (vertical && HDivWidthContent[index].HasValue)
            {
                value = HDivWidthContent[index].Value.ToString(format, provider) + "";
            }
            else
                 if (!vertical && VDivHeightContent[index].HasValue)
            {
                value = VDivHeightContent[index].Value.ToString(format, provider) + "";
            }
            return value;
        }


        //protected void builVHeight_(Splitter.Splitter item, BoundingClientRect BoundingClientRect)
        //{
        //    foreach (var ite in Items)
        //    {
        //        VDivHeightContent.Add(null);
        //    }
        //    if (RightSizePx.HasValue)
        //    {
        //        double size = (BoundingClientRect.Height - item.SplitterSettings.size - RightSizePx.Value);
        //        int offset = 0;
        //        double.TryParse(Items[offset].GetHeight().ToString(), NumberStyles.Any, provider, out double value);
        //        VDivHeightContent[offset] = value;
        //        VDivHeightContent[offset + 1] = size - value;
        //        if (size > RightSizePx.Value)
        //        {
        //            MinVDivHeightContent = RightSizePx.Value / 3;
        //            MaxVDivHeightContent = RightSizePx.Value + MinVDivHeightContent;
        //        }
        //        else
        //        {
        //            MinVDivHeightContent = size / 3;
        //            MaxVDivHeightContent = size + MinVDivHeightContent;
        //            if (MinVDivHeightContent < DefaultMinHeight)
        //            {
        //                MinVDivHeightContent = DefaultMinHeight;
        //                double? size2 = (BoundingClientRect.Height - item.SplitterSettings.size - RightSizePx.Value) - DefaultMinHeight;
        //                MaxVDivHeightContent = size2;
        //            }
        //        }
        //    }
        //    else
        //    {
        //        double? size = (BoundingClientRect.Height - item.SplitterSettings.size) / (LeftSize + RightSize);
        //        double? sizeF = (BoundingClientRect.Height - item.SplitterSettings.size);
        //        if (ISHorizontalFirst)
        //        {
        //            ISHorizontalBuilded = true;
        //            ISHorizontalHeight = BoundingClientRect.Height;
        //            ISHorizontalHeightSplitter = item.SplitterSettings.size;
        //        }

        //        int offset = 0;
        //        double? totalSize = 0;
        //        while (offset < SplitterSettingsFormPanel.Count)
        //        {
        //            if (SplitterSettingsFormPanel[offset] != null)
        //            {
        //                double.TryParse(Items[offset].GetHeight().ToString(), NumberStyles.Any, provider, out double value);
        //                VDivHeightContent[offset] = value * LeftSize;
        //                totalSize += value; ;
        //                if ((offset + 1) < VDivHeightContent.Count)
        //                {
        //                    VDivHeightContent[offset + 1] = sizeF - totalSize;
        //                }
        //            }
        //            offset += 2;
        //        }
        //        MinVDivHeightContent = size / 3;
        //        MaxVDivHeightContent = size + MinVDivHeightContent;
        //        if (MinVDivHeightContent < DefaultMinHeight)
        //        {
        //            MinVDivHeightContent = DefaultMinHeight;
        //            double? size2 = (BoundingClientRect.Height - item.SplitterSettings.size) - DefaultMinHeight;
        //            MaxVDivHeightContent = (int)(size2);
        //        }
        //    }
        //}


    }
}

