using DevExpress.Blazor;
using Microsoft.AspNetCore.Components;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Bcephal.Blazor.Web.Reconciliation.Shared.Component.Reconciliation
{
   public partial class RecoConfigurationRow
    {
        bool IsXSmallScreen;
        [Parameter]
        public RenderFragment Item1 { get; set; }
        [Parameter]
        public RenderFragment Item2 { get; set; }


        [Parameter]
        public string Item1Length { get; set; } = "auto";
        [Parameter]
        public string Item2Length { get; set; } = "auto";

        [Parameter]
        public string CssClass { get; set; } = "w-100 m-0 p-0";

        [Parameter]
        public string ItemSpacing { get; set; } = "0px";

        [Parameter]
        public DeviceSize DeviceSize_ { get; set; } = DeviceSize.XSmall | DeviceSize.Small | DeviceSize.Medium;


    }
}
