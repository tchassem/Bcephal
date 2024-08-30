using DevExpress.Blazor;
using Microsoft.AspNetCore.Components;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Bcephal.Blazor.Web.Base.Shared.Component { 
   public partial class ComponentRow
    {
        [Parameter]
        public RenderFragment Item1 { get; set; }
        [Parameter]
        public RenderFragment Item2 { get; set; }

        [Parameter]
        public string Item1Width { get; set; } = "1fr";
        [Parameter]
        public string Item2Width { get; set; } = "1fr";

        [Parameter]
        public string Item1CssClass { get; set; } = "";
        [Parameter]
        public string Item2CssClass { get; set; } = "";


        [Parameter]
        public string CssClass { get; set; } = "w-100 m-0 p-0";

        [Parameter]
        public string ItemSpacing { get; set; } = "0px";

    }
}
