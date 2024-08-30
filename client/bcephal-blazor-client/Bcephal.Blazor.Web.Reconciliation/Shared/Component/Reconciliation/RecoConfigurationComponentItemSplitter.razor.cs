using Bcephal.Blazor.Web.Base.Shared;
using Microsoft.AspNetCore.Components;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Bcephal.Blazor.Web.Reconciliation.Shared.Component.Reconciliation
{
  public partial  class RecoConfigurationComponentItemSplitter
    {
        bool IsXSmallScreen = false;
        [Parameter]
        public List<RenderFragment> SimplePanel { get; set; }
        [Parameter]
        public List<RenderFragment> Panel { get; set; }
        string heightCall = "var(--bc-content-tabtwo-height)";

    }
}
