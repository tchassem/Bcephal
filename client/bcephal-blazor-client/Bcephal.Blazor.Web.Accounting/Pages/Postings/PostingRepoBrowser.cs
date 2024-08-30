using Bcephal.Blazor.Web.Accounting.Services;
using Bcephal.Blazor.Web.Base.Services;
using Bcephal.Blazor.Web.Base.Shared.AbstractComponent;
using Bcephal.Blazor.Web.Sourcing.Pages.Grille;
using Bcephal.Blazor.Web.Sourcing.Pages.Sourcing.Grille;
using Bcephal.Models.Accounting;
using Microsoft.AspNetCore.Components;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Bcephal.Blazor.Web.Accounting.Pages.Postings
{
    [RouteAttribute("accounting/posting-entry-repository/")]
    public class PostingRepoBrowserr : GrilleForm
    {
        [Inject]
        PostingRepositoryService PostingRepositoryService { get; set; }

        public override string LeftTitle { get { return AppState["ReportGrid"]; } }

        // --- public override string LeftTitleIcon { get { return "bi-file-plus"; } }

        protected override PostingRepositoryService GetService()
        {
            return PostingRepositoryService;
        }


        protected override async Task OnInitializedAsync()
        {
            await base.OnInitializedAsync();
        }
    }
}
