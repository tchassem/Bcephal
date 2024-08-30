using Bcephal.Blazor.Web.Base.Shared.Component.Splitter;
using Microsoft.AspNetCore.Components;
using Microsoft.JSInterop;
using System;
using System.Collections.Generic;
using System.Globalization;
using System.Linq;
using System.Threading.Tasks;

namespace Bcephal.Blazor.Web.Base.Shared.Component.SplitterPane
{
    public partial class SplitterPanel : ComponentBase
    {
        [Inject]
        protected virtual IJSRuntime JSRuntime { get; set; }

        [Parameter]
        public List<RenderFragment> Panes { get; set; }
        [Parameter]
        public bool vertical { get; set; } = true;

        [Parameter]
        public int LeftSize { get; set; } = 4;

        [Parameter]
        public int? RightSize { get; set; } = 1;

        [Parameter]
        public int? RightSizePx { get; set; }

        [Parameter]
        public string StyleLeft { get; set; } = "";

        [Parameter]
        public EventCallback<string> StyleLeftChanged { get; set; }

        [Parameter]
        public string StyleRight { get; set; } = "";

        [Parameter]
        public EventCallback<string> StyleRightChanged { get; set; }

        [Parameter]
        public bool EnableRender { get; set; } = true;


        protected List<double?> HDivWidthContent { get; set; } = new List<double?>(0);

        protected double? MinHDivWidthContent = 100;
        protected double? MaxHDivWidthContent = 1024;

        protected virtual List<double?> VDivHeightContent { get; set; } = new List<double?>(0);

        protected double? MinVDivHeightContent = 100;
        protected double? MaxVDivHeightContent = 1024;

        public BoundingClientRect BoundingClientRect { get; set; }
        protected virtual List<Splitter.Splitter> SplitterForm { get; set; } = new List<Splitter.Splitter>(0);
        protected virtual List<SplitterSettings> SplitterSettingsFormPanel { get; set; } = new ();

        [Parameter] // "var(--bc-dx-tabs-content-panel-height)";
        public  string heightCall { get; set; } = "var(--bc-dx-tabs-content-panel-height)";


        [Parameter]
        public  bool IsMinheight { get; set; } = false;

        [Parameter]
        public ElementReference ElementReference { get; set; }

        protected  override async Task OnParametersSetAsync()
        {
            await base.OnParametersSetAsync();
            int offset = 0;
            if (SplitterForm.Count() == 0)
            {
                if (vertical)
                {
                    while (offset < Panes.Count)
                    {
                        SplitterForm.Add(null);
                        HDivWidthContent.Add(null);
                        VDivHeightContent.Add(null);
                        SplitterSettingsFormPanel.Add(new SplitterSettings() { index = offset });
                        offset++;
                    }
                }
                else
                {
                    while (offset < Panes.Count)
                    {
                        SplitterForm.Add(null);
                        HDivWidthContent.Add(null);
                        VDivHeightContent.Add(null);
                        SplitterSettingsFormPanel.Add(new SplitterSettings() { index = offset });
                        offset++;
                    }
                }
            }
        }
        protected virtual bool ISHorizontalFirst => !vertical && IsMinheight;
        protected virtual bool ISHorizontalBuilded { get; set; } = false;
        protected virtual double ISHorizontalHeight { get; set; } = 0;
        protected virtual double ISHorizontalHeightSplitter { get; set; } = 0;
        protected virtual CultureInfo provider { get; set; }  = new CultureInfo("en-us");
        protected virtual string format { get; set; } = "0.000000";

        protected virtual async Task BoundingClientRectAsync(Splitter.Splitter item)
        {
            if (item != null && BoundingClientRect == null)
            {
                BoundingClientRect = await SplitterCJsInterop.roundingClientRect(JSRuntime, item.SplitterSettings.ID, ElementReference);
                if (BoundingClientRect != null)
                {
                    if (!vertical)
                    {
                        builVHeight(item, BoundingClientRect);
                    }
                    else
                    {
                        builVWith(item, BoundingClientRect);
                    }
                }
            }
        }

        protected virtual void builVWith(Splitter.Splitter item, BoundingClientRect BoundingClientRect)
        {
            if (RightSizePx.HasValue)
            {
                double size = (BoundingClientRect.Width - item.SplitterSettings.size - RightSizePx.Value);
                int offset = 0;
                HDivWidthContent[offset] = size;
                HDivWidthContent[offset + 1] = RightSizePx.Value;
                if (size > RightSizePx.Value)
                {
                    MinHDivWidthContent = RightSizePx.Value / 3;
                    MaxHDivWidthContent = RightSizePx.Value + MinHDivWidthContent;
                }
                else
                {
                    MinHDivWidthContent = size / 3;
                    MaxHDivWidthContent = size + MinHDivWidthContent;
                }
            }
            else
            {
                double? size = (BoundingClientRect.Width - item.SplitterSettings.size) / (LeftSize + RightSize);
                int offset = 0;
                while (offset < SplitterForm.Count)
                {
                    HDivWidthContent[offset] = size * LeftSize;
                    if((offset+1) < HDivWidthContent.Count)
                    {
                        HDivWidthContent[offset + 1] = size * RightSize;
                    }
                    
                    offset += 2;
                }
                MinHDivWidthContent = size / 3;
                MaxHDivWidthContent = size + MinHDivWidthContent;
                
            }
        }

        protected virtual void builVHeight(Splitter.Splitter item, BoundingClientRect BoundingClientRect)
        {
            if (RightSizePx.HasValue)
            {
                double size = (BoundingClientRect.Height - item.SplitterSettings.size - RightSizePx.Value);
                int offset = 0;
                VDivHeightContent[offset] = (int)size;
                VDivHeightContent[offset + 1] = RightSizePx.Value;
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
               // heightCall = (BoundingClientRect.Height - item.SplitterSettings.size).ToString(format, provider) + "px;";
                if (ISHorizontalFirst)
                {
                    ISHorizontalBuilded = true;
                    ISHorizontalHeight = BoundingClientRect.Height;
                    ISHorizontalHeightSplitter = item.SplitterSettings.size;
                }

                int offset = 0;
                while (offset < SplitterForm.Count)
                {
                    VDivHeightContent[offset] = size * LeftSize;
                    if((offset+1) < VDivHeightContent.Count)
                    {
                        VDivHeightContent[offset + 1] = size * RightSize;
                    }             
                    offset += 2;
                }
                MinVDivHeightContent = size / 3;
                MaxVDivHeightContent = size + MinVDivHeightContent;
                if (MinVDivHeightContent < DefaultMinHeight)
                {
                    MinVDivHeightContent = DefaultMinHeight;
                    double? size2 = (BoundingClientRect.Height - item.SplitterSettings.size)  - DefaultMinHeight;
                    MaxVDivHeightContent = (int)(size2);
                }
            }
        }

        protected virtual double MaxWidth => ((int)(BoundingClientRect != null ? BoundingClientRect.Width: MaxHDivWidthContent));
        protected virtual double MinWidth => MinHDivWidthContent.Value;

        //private int MaxHeight => ((int)(BoundingClientRect != null ? BoundingClientRect.Height: MaxVDivHeightContent));
        protected virtual double MaxHeight => MaxVDivHeightContent.Value;
        protected virtual double MinHeight => MinVDivHeightContent.Value;

        protected virtual int DefaultMinHeight { get; set; } = 150;


        protected override async Task OnAfterRenderAsync(bool firstRender)
        {
            await base.OnAfterRenderAsync(firstRender);
            if (firstRender && SplitterForm != null && SplitterForm.Count > 0)
            {
                await BoundingClientRectAsync(GetSplitter());
                StateHasChanged();
            }
        }


        protected virtual Splitter.Splitter GetSplitter()
        {
            if (SplitterForm != null && SplitterForm.Count > 0)
            {
                int offset = 0;
                while (offset < SplitterForm.Count)
                {
                    if(SplitterForm[offset] != null)
                    {
                        return SplitterForm[offset];
                    }
                    offset++;
                }
            }
            return null;
        }

        protected virtual void OnPositionChange(bool b, int index, int p)
        {
            if (vertical)
            {
                OnPositionChangeWidth(b, index, p);
            }
            else
            {
                OnPositionChangeHeight(b, index, p);
            }
        }
        protected virtual void OnPositionChangeHeight(bool b, int index, int p)
        {
            if (p > 0)
            {
                if (VDivHeightContent[index - 1] < MaxHeight)
                {
                    correctSizesHeight(p, index);
                }
            }
            else
            {
                if (VDivHeightContent[index - 1] > MinHeight)
                {
                    correctSizesHeight(p, index);
                }
            }
        }

        protected virtual void correctSizesHeight(int p, int index)
        {
            VDivHeightContent[index - 1] += p;
            VDivHeightContent[index] -= p;
            if (VDivHeightContent[index - 1] > MaxHeight) VDivHeightContent[index - 1] = MaxHeight;
            if (VDivHeightContent[index] > MaxHeight) VDivHeightContent[index] = MaxHeight;
            if (VDivHeightContent[index - 1] < MinHeight) VDivHeightContent[index - 1] = MinHeight;
            if (VDivHeightContent[index] < MinHeight) VDivHeightContent[index] = MinHeight;
            InvokeAsync(StateHasChanged);
        }

        protected virtual void OnPositionChangeWidth(bool b, int index, int p)
        {
            if (p > 0)
            {
                if (HDivWidthContent[index - 1] < MaxWidth)
                {
                    correctSizesWidth(p, index);
                }
            }
            else
            {
                if (HDivWidthContent[index - 1] > MinWidth)
                {
                    correctSizesWidth(p, index);
                }
            }
        }

        protected virtual void correctSizesWidth(int p, int index)
        {
            HDivWidthContent[index - 1] += p;
            HDivWidthContent[index] -= p;
            if (HDivWidthContent[index - 1] > MaxWidth) HDivWidthContent[index - 1] = MaxWidth;
            if (HDivWidthContent[index] > MaxWidth) HDivWidthContent[index] = MaxWidth;
            if (HDivWidthContent[index - 1] < MinWidth) HDivWidthContent[index - 1] = MinWidth;
            if (HDivWidthContent[index] < MinWidth) HDivWidthContent[index] = MinWidth;
            InvokeAsync(StateHasChanged);
        }

        
        protected virtual string cmdGetStyleDivContent(int index)
        {
            string value = "99";
            
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

        protected virtual string GetStyleSplitterBlockVartical()
        {
            string value = "height:100%;";
            return value;
        }

        protected virtual string GetStyleSplitterBlockHorizontal()
        {
            string value = "height: " + heightCall.Replace(";", "") + ";" ;
            if (ISHorizontalBuilded)
            {
                value = "height:100%;display:block;";
            }
            return value;
        }

        protected virtual string GetHeightCall(int size, int restSize, int count)
        {
            string value =  "calc(((" + heightCall.Replace(";", "") + " * " + size + ") / " + (size + restSize) + ") - (5px * " + count + "))";
            return value;
        }

    }
}
