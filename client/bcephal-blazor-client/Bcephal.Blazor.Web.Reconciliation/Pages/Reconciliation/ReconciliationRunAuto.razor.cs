using Bcephal.Blazor.Web.Base.Services;
using Bcephal.Blazor.Web.Base.Shared;
using Bcephal.Blazor.Web.Sourcing.Services;
using Bcephal.Blazor.Web.Sourcing.Shared.Grille;
using Bcephal.Models.Base;
using Bcephal.Models.Reconciliation;
using Microsoft.AspNetCore.Components;
using System.Threading.Tasks;

namespace Bcephal.Blazor.Web.Reconciliation.Pages.Reconciliation
{
     public partial class ReconciliationRunAuto : Form<AutoReco, RecoBrowserData> 
    {
        [Inject]
        public AutoRecoService ReconciliationRunAutoService { get; set; }
        
        bool Sequential_ { get; set; } = true;
        private bool Sequential
        {
            get { return Sequential_; }
            set
            {
                Sequential_ = value;
                Paralel_ = !Sequential_;
                AppState.CanRun = true;
            }
        }

        bool Paralel_ { get; set; } = false;
        private bool Paralel
        {
            get { return Paralel_; }
            set
            {
                Paralel_ = value;
                Sequential_ = !Paralel_;
                AppState.CanRun = true;
            }
        }

        [Inject]
        public GrilleService GrilleService { get; set; }

        private InputGridComponentForm_ InputGridComponentForm  { get; set; }

        public override async ValueTask DisposeAsync()
        {
            await base.DisposeAsync();
            AppState.Update = false;
            AppState.CanLoad = false;
        }

        public override string LeftTitle { get { return AppState["Run.Automatic.Reconciliations"]; } }

        public override string LeftTitleIcon { get { return "bi-box-arrow-right"; } }



        protected override AutoRecoService GetService()
        {
            return ReconciliationRunAutoService;
        }
        protected override EditorDataFilter getEditorDataFilter()
        {
            return new EditorDataFilter();
        }
    }
}
