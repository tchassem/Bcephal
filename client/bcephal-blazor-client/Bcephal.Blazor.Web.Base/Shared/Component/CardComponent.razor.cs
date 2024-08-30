using Microsoft.AspNetCore.Components;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Bcephal.Blazor.Web.Base.Shared.Component
{
  public partial  class CardComponent
    {
        [Parameter] public RenderFragment Header { get; set; }
        [Parameter] public RenderFragment ChildContent { get; set; }
        [Parameter] public RenderFragment Footer { get; set; }
        [Parameter] public bool CanDisplayHeader { get; set; } = false;
        [Parameter] public bool CanDisplayFooter { get; set; } = false;
        [Parameter] public string HeaderLength { get; set; } = "auto";//"0.1fr"
        [Parameter] public string Length { get; set; } = "10fr";
        [Parameter] public string FooterLength { get; set; } = "auto";
        [Parameter] public string HeaderCssClass { get; set; } = "";
        [Parameter] public string FooterCssClass { get; set; } = "";
        [Parameter] public string CardCssClass { get; set; } = "";
        [Parameter] public bool ShouldRender_ { get; set; } = true;

        RenderFormContent RenderFormContentHeader { get; set; }
        RenderFormContent RenderFormContentBody { get; set; }
        RenderFormContent RenderFormContentFooter { get; set; }
        protected override bool ShouldRender()
        {
            return ShouldRender_;
        }

        protected override Task OnAfterRenderAsync(bool firstRender)
        {
            ShouldRender_ = false;
            return base.OnAfterRenderAsync(firstRender);
        }

        public void RefreshHeader()
        {
            if(RenderFormContentHeader != null)
            {
                RenderFormContentHeader.StateHasChanged_();
            }
        } 
        public void RefreshBody()
        {
            if(RenderFormContentBody != null)
            {
                RenderFormContentBody.StateHasChanged_();
            }
        } 
        public void RefreshFooter()
        {
            if(RenderFormContentFooter != null)
            {
                RenderFormContentFooter.StateHasChanged_();
            }
        }
    }
}
