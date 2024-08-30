using Bcephal.Models.Base;
using Bcephal.Models.Filters;
using Bcephal.Models.Grids;
using System;
using System.Collections.ObjectModel;
using Microsoft.AspNetCore.Components;
using Bcephal.Blazor.Web.Base.Shared;
using Bcephal.Blazor.Web.Sourcing.Services;
using Bcephal.Blazor.Web.Sourcing.Shared.Grille;
using System.Threading.Tasks;
using Microsoft.JSInterop;
using Bcephal.Models.Utils;
using Bcephal.Blazor.Web.Base.Services;
using Bcephal.Models.Grids.Filters;

namespace Bcephal.Blazor.Web.Sourcing.Pages.Sourcing.Grille
{
    public partial class GrilleForm : Form<Bcephal.Models.Grids.Grille, object>
    {
        protected virtual RenderFragment CustomHeaderRenderHandler() => null;
        protected int ActiveTabIndex { get; set; } = 0;
        int ActiveTabIndexFilter { get; set; } = 0;

        int ActiveTabIndexGridFilter { get; set; } = 0;

        public virtual bool Editable
        {
            get 
            {
                var first = AppState.PrivilegeObserver.CanCreatedSourcingInputGrid;
                var second = AppState.PrivilegeObserver.CanEditSourcingInputGrid(EditorData.Item);
                return first || second;
            } 
        }
        [Inject] public GrilleService GrilleService { get; set; }
        private InputGridComponentForm_ InputGridComponentForm { get; set; }
        private ConfigurationGrid ConfigurationGrid { get; set; }
        public override string LeftTitle { get { return AppState["InputGrid"]; } }
        public override string LeftTitleIcon { get { return "bi-grid"; } }

        [Parameter] public bool CanRefreshGrid { get; set; } = true;

        protected override GrilleService GetService()
        {
            return GrilleService;
        }


        int ActiveTabIndexBing
        {
            get => ActiveTabIndex;
            set
            {
                ActiveTabIndex = value;
                if (value == 0)
                {
                    RefreshRightContent(RightContentSend);
                }
            }
        }
        protected override string DuplicateName()
        {
            return AppState["duplicate.grille.name", EditorData.Item.Name];
        }
        public override string GetBrowserUrl { get => Route.BROWSER_GRID; set => base.GetBrowserUrl = value; }

        private void StateHasChanged_()
        {
            StateHasChanged();
            AppState.Update = true;
        }

       
        private void addMeasureColumn(Models.Dimensions.Measure measure)
        {
            StateHasChanged_();
        }

        private void addPeriodColumn(Models.Dimensions.Period period)
        {
            StateHasChanged_();
        }

        private void addAttributColumn(HierarchicalData attribute_)
        {
            StateHasChanged_();
        }

        protected override EditorDataFilter getEditorDataFilter()
        {
            return new EditorDataFilter();
        }

        protected virtual void OnCustomAfterFirstRender(bool firstRender)
        {
            if (Id.HasValue)
            {
                ActiveTabIndex = 0;
            }
            else
            {
                ActiveTabIndex = 1;
            }
        }

        private void RefreshRightContent_() {
            RefreshRightContent(RightContentSend);
        }

        protected override async Task OnAfterRenderAsync(bool firstRender)
        {
            await  base.OnAfterRenderAsync(firstRender);
            if (firstRender)
            {
                OnCustomAfterFirstRender(firstRender);
                if (ActiveTabIndex == 0)
                {
                    RefreshRightContent(RightContentSend);
                }
                AppState.PublishedHander += Publish;
                AppState.ResetPublicationHandler += ResetPublication;
                AppState.RefreshPublicationHandler += RefreshPublication;
                AppState.ExportDataHandler += ExportData;
            }

            if (ActiveTabIndex == 0 && InputGridComponentForm != null && !AppState.CanExport)
            {
                AppState.CanExport = true;
            }
            else if (ActiveTabIndex != 0 && AppState.CanExport)
            {
                AppState.CanExport = false;
            }

            if (EditorData != null && EditorData.Item != null && EditorData.Item.Id.HasValue)
            {
                AppState.CanPublished = !EditorData.Item.Published;
                AppState.CanResetPublication = EditorData.Item.Published;
                AppState.CanRefreshPublication = EditorData.Item.Published;
            }
            canDisplayError = true;
        }

        protected bool canDisplayError = true;
        protected virtual bool IsInputGrid() => GrilleType.INPUT.Equals(EditorData.Item.Type);

        private async void Publish()
        {
            EditorData.Item.Published = true;
            try
            {
                EditorDataBinding = await GetService().Publish(EditorData.Item.Id.Value);
                if (EditorDataBinding != null)
                {
                    ToastService.ShowSuccess(AppState["publish.input.grid.success"]);
                }
                else
                {
                    ToastService.ShowError(AppState["publish.input.grid.error"]);
                }
            }
            catch(Exception ex)
            {
                Error.ProcessError(ex);
            }
            finally
            {
                StateHasChanged();
            }            
        }

        private async void ResetPublication()
        {
            EditorData.Item.Published = false;
            try
            {
                EditorDataBinding = await GetService().ResetPublication(EditorData.Item.Id.Value);
                if (EditorDataBinding != null)
                {
                    ToastService.ShowSuccess(AppState["reset.publication.input.grid.success"]);
                }
                else
                {
                    ToastService.ShowError(AppState["reset.publication.input.grid.error"]);
                }
            }
            catch (Exception ex)
            {
                Error.ProcessError(ex);
            }
            finally
            {
                StateHasChanged();
            }
        }
        private async void RefreshPublication()
        {
            try
            {
                bool response = await GetService().RefreshPublication(EditorData.Item.Id.Value);
                if (response)
                {
                    ToastService.ShowSuccess(AppState["refresh.publication.input.grid.success"]);
                }
                else
                {
                    ToastService.ShowError(AppState["refresh.publication.input.grid.error"]);
                }
            }
            catch (Exception ex)
            {
                Error.ProcessError(ex);
            }
            finally
            {
                StateHasChanged();
            }
        }
      private void ExportData(GrilleExportDataType type)
        {
            if (InputGridComponentForm != null)
            {
                InputGridComponentForm.ExportData(type);
            }
        }
        public override async ValueTask DisposeAsync()
        {
            if (AppState.CanExport)
            {
                AppState.CanExport = false;
                AppState.ExportDataHandler -= ExportData;
            }

            AppState.Update = false;
            AppState.CanPublished = false;
            AppState.PublishedHander -= Publish;
            AppState.CanResetPublication = false;
            AppState.CanRefreshPublication = false;
            AppState.ResetPublicationHandler -= ResetPublication;
            AppState.RefreshPublicationHandler -= RefreshPublication;

            await base.DisposeAsync();
        }

        protected virtual void BuildFilter(BrowserDataFilter filter)
        {
           
        }

    }
}
