using Bcephal.Blazor.Web.Reporting.Pages.Joins;
using Bcephal.Blazor.Web.Reporting.Pages.Reporting.Report.Grid;
using Bcephal.Blazor.Web.Reporting.Services;
using Bcephal.Models.Exceptions;
using Microsoft.AspNetCore.Components;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Bcephal.Blazor.Web.Billing.Pages.Billing.Client
{
    public partial class ClientRepository : ReportingForm
    {
        [Inject]
        ClientRepositoryService ClientRepositoryService { get; set; }

        protected override ClientRepositoryService GetService()
        {
            return ClientRepositoryService;
        }


        public override string GetBrowserUrl { get => null; set => base.GetBrowserUrl = null; }

        protected override bool IsInputGrid()
        {
            if (EditorData.Item == null)
            {
                throw new BcephalException(AppState["not.found.repository"]);
            }
            return base.IsInputGrid();
        }

        protected override void OnCustomAfterFirstRender(bool firstRender)
        {
            ActiveTabIndex = 0;
        }

        public override bool Editable
        {
            get
            {
                var first = AppState.PrivilegeObserver.CanCreatedClientRepository;
                var second = EditorData != null && EditorData.Item != null &&  AppState.PrivilegeObserver.CanEditClientRepository(EditorData.Item);
                return first || second;
            }
        }
    }
}
