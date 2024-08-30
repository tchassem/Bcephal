using Bcephal.Blazor.Web.Base.Services;
using Microsoft.AspNetCore.Components;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Bcephal.Blazor.Web.Base.Shared
{
   public partial class Browser : ComponentBase
    {
        [Inject] protected AppState AppState { get; set; }
        public virtual string Title { get { return ""; } }

        public virtual string TitleIcon { get { return "bi bi-list"; } }
    }
}
