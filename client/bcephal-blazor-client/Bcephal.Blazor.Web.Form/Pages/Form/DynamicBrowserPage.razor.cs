using Microsoft.AspNetCore.Components;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Bcephal.Blazor.Web.Form.Pages.Form
{
  public partial  class DynamicBrowserPage
    {
        [Parameter]
        public long? Id { get; set; }

        private string Key => $"DynamicBrowser{Id.Value}";
    }
}
