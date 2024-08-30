using Bcephal.Blazor.Web.Reporting.Pages.Reporting.Report.Grid;
using Bcephal.Blazor.Web.Reporting.Services;
using Bcephal.Models.Exceptions;
using Bcephal.Models.Grids;
using Bcephal.Models.Grids.Filters;
using Microsoft.AspNetCore.Components;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;

namespace Bcephal.Blazor.Web.Billing.Pages.Billing.Event
{
    public partial class BillingEventRepository : ReportingForm
    {
        [Inject]
        RepositoryService RepositoryService { get; set; }

        protected override RepositoryService GetService()
        {
            return RepositoryService;
        }

        protected override RenderFragment CustomHeaderRenderHandler() => Header; 

        public override string GetBrowserUrl { get => null; set => base.GetBrowserUrl = null; }

        protected override bool IsInputGrid()  {
            if(EditorData.Item == null)
            {
                throw new BcephalException(AppState["not.found.repository"]);
            }
            return base.IsInputGrid();
        }

        public override bool Editable
        {
            get
            {
                var first = AppState.PrivilegeObserver.CanCreatedBillingEventRepository;
                var second = EditorData != null && EditorData.Item != null && AppState.PrivilegeObserver.CanEditBillingEventRepository(EditorData.Item);
                return first || second;
            }
        }

        protected override void OnCustomAfterFirstRender(bool firstRender)
        {
            ActiveTabIndex = 0;
        }
        private IEnumerable<string> DataFilter { get; set; } = new List<string>() { };
        GrilleRowType GrilleRowType { get; set; }
        private string getGrilleRowType()
        {
            if (GrilleRowType.BILLED.Equals(GrilleRowType))
            {
                return AppState["billed"];
            }
            {
                return AppState["draft"];
            }
        }

        private void setGrilleRowType(string value)
        {
            if (!string.IsNullOrWhiteSpace(value))
            {
                if (AppState["billed"].Equals(value))
                {
                    GrilleRowType = GrilleRowType.BILLED;
                }
                else
                {
                    GrilleRowType = GrilleRowType.DRAFT;
                }
                
            }
        }

        protected override Task OnInitializedAsync()
        {
            Task task = base.OnInitializedAsync();
            return task.ContinueWith(t => InitDataFilter());
        }

        private void InitDataFilter()
        {
            if (!DataFilter.Contains(AppState["billed"]))
            {
                ((List<string>)DataFilter).Add(AppState["billed"]);
            }
            if (!DataFilter.Contains(AppState["draft"]))
            {
                ((List<string>)DataFilter).Add(AppState["draft"]);
            }
            GrilleRowType = GrilleRowType.DRAFT;
        }

        protected override void BuildFilter(BrowserDataFilter filter)
        {
            filter.RowType = GrilleRowType != null ? GrilleRowType.code : null;
        }
    }
}
