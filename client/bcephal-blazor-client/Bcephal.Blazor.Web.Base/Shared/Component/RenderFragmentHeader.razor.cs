using Microsoft.AspNetCore.Components;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Bcephal.Blazor.Web.Base.Shared.Component
{
  public partial  class RenderFragmentHeader
    {
        [Parameter]
        public string Title { get; set; }

        [Parameter]
        public RenderFragment ChildContent { get; set; }
    }
}
