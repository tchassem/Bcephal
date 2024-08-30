using Microsoft.AspNetCore.Components;
using Microsoft.AspNetCore.Components.Rendering;
using Microsoft.AspNetCore.Components.Web;
using Microsoft.JSInterop;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;

namespace Bcephal.Blazor.Web.Base.Shared.Component.Splitter
{
    public partial class Splitter : ComponentBase, IDisposable
    {
        [Inject]
        private IJSRuntime jsRuntimeCurrent { get; set; }

        [Parameter]
        public SplitterSettings SplitterSettings { get; set; }

        [Parameter]
        public Action<bool, int, int> OnPositionChange { get; set; }

        [Parameter]
        public Action<int, int, int> OnDiagonalPositionChange { get; set; }

        [Parameter]
        public Action<int, int, int> OnDragStart { get; set; }

        [Parameter]
        public Action<int, int, int> OnDragEnd { get; set; }


        [Parameter]
        public RenderFragment ChildContent { get; set; }

        private InternalSplitter InternalSplitter { get; set; } = new InternalSplitter();

        private bool DragMode = false;

        [Parameter]
        public bool EnableRender { get; set; } = true;

        [Parameter]
        public bool VerticalAline { get; set; } = false;


        protected override void OnInitialized()
        {
            InternalSplitter.SplitterSettings = SplitterSettings;
            InternalSplitter.PropertyChanged = BSplitter_PropertyChanged;
            InternalSplitter.SplitterSettings.Vertical = VerticalAline;
            DragMode = false;
            base.OnInitialized();
        }


        protected override bool ShouldRender()
        {
            return EnableRender;
        }


        private void BSplitter_PropertyChanged()
        {
            EnableRender = true;
            StateHasChanged();
        }

        protected override void BuildRenderTree(RenderTreeBuilder builder)
        {
            if (EnableRender)
            {
                base.BuildRenderTree(builder);
                int k = 0;
                builder.OpenElement(k++, "div");
                builder.AddAttribute(k++, "id", InternalSplitter.SplitterSettings.ID);
                builder.AddAttribute(k++, "style", InternalSplitter.SplitterSettings.GetStyle());

                builder.AddAttribute(k++, "onpointerdown", EventCallback.Factory.Create<PointerEventArgs>(this, OnPointerDown));
                builder.AddAttribute(k++, "onpointermove", EventCallback.Factory.Create<PointerEventArgs>(this, OnPointerMove));
                builder.AddAttribute(k++, "onpointerup", EventCallback.Factory.Create<PointerEventArgs>(this, OnPointerUp));

                builder.AddEventPreventDefaultAttribute(k++, "onmousemove", true);

                builder.CloseElement();
                EnableRender = false;
            }
        }

        private void OnPointerDown(PointerEventArgs e)
        {
            SplitterCJsInterop.SetPointerCapture(jsRuntimeCurrent, InternalSplitter.SplitterSettings.ID, e.PointerId);
            DragMode = true;

            if (SplitterSettings.IsDiagonal)
            {
                InternalSplitter.PreviousPosition = (int)e.ClientX;
                InternalSplitter.PreviousPosition2 = (int)e.ClientY;
            }
            else
            {
                if (SplitterSettings.Vertical)
                {
                    InternalSplitter.PreviousPosition = (int)e.ClientX;
                    InternalSplitter.PreviousPosition2 = (int)e.ClientY;
                }
                else
                {
                    InternalSplitter.PreviousPosition = (int)e.ClientY;
                    InternalSplitter.PreviousPosition2 = (int)e.ClientX;
                }
            }
            OnDragStart?.Invoke(InternalSplitter.SplitterSettings.index, (int)e.ClientX, (int)e.ClientY);
        }


        private void OnPointerMove(PointerEventArgs e)
        {
            if (DragMode)
            {
                if (e.Buttons == 1)
                {
                    int NewPosition = 0;
                    int NewPosition2 = 0;
                    if (SplitterSettings.IsDiagonal)
                    {
                        NewPosition = (int)e.ClientX;
                        NewPosition2 = (int)e.ClientY;
                        if (InternalSplitter.PreviousPosition != NewPosition || InternalSplitter.PreviousPosition2 != NewPosition2)
                        {
                            OnDiagonalPositionChange?.Invoke(SplitterSettings.index, NewPosition - InternalSplitter.PreviousPosition, NewPosition2 - InternalSplitter.PreviousPosition2);
                            InternalSplitter.PreviousPosition = NewPosition;
                            InternalSplitter.PreviousPosition2 = NewPosition2;
                        }
                    }
                    else
                    {
                        if (SplitterSettings.Vertical)
                        {
                            NewPosition = (int)e.ClientX;
                            NewPosition2 = (int)e.ClientY;
                        }
                        else
                        {
                            NewPosition = (int)e.ClientY;
                            NewPosition2 = (int)e.ClientX;
                        }
                        if (Math.Abs(InternalSplitter.PreviousPosition2 - NewPosition2) < 100)
                        {
                            if (InternalSplitter.PreviousPosition != NewPosition)
                            {
                                OnPositionChange?.Invoke(SplitterSettings.Vertical, SplitterSettings.index, NewPosition - InternalSplitter.PreviousPosition);
                                InternalSplitter.PreviousPosition = NewPosition;
                            }
                        }
                        //else
                        //{
                        //    //BsJsInterop.StopDrag(bSplitter.bsbSettings.ID);
                        //}
                    }
                }
            }
        }



        private void OnPointerUp(PointerEventArgs e)
        {
            SplitterCJsInterop.releasePointerCapture(jsRuntimeCurrent, InternalSplitter.SplitterSettings.ID, e.PointerId);
            DragMode = false;
            OnDragEnd?.Invoke(InternalSplitter.SplitterSettings.index, (int)e.ClientX, (int)e.ClientY);
        }


        public void Dispose()
        {

        }

        public void SetColor(string c)
        {
            InternalSplitter.SplitterSettings.BgColor = c;
            EnableRender = true;
            StateHasChanged();
        }

        public void SetStyle(string style)
        {
            InternalSplitter.SplitterSettings.Style_ = style;
            EnableRender = true;
            StateHasChanged();
        }

        public void SetVertical(bool vertical)
        {
            InternalSplitter.SplitterSettings.Vertical = vertical;
            //EnableRender = true;
            //StateHasChanged();
        }
    }
}
