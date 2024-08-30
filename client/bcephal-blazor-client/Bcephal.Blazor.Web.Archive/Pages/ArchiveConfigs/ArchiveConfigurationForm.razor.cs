using Bcephal.Blazor.Web.Archive.Services;
using Bcephal.Blazor.Web.Base.Services;
using Bcephal.Blazor.Web.Base.Shared;
using Bcephal.Blazor.Web.Base.Shared.Component;
using Bcephal.Blazor.Web.Sourcing.Services;
using Bcephal.Blazor.Web.Sourcing.Shared.Grille;
using Bcephal.Models.Archives;
using Bcephal.Models.Base;
using Bcephal.Models.Dimensions;
using Bcephal.Models.Filters;
using Bcephal.Models.Grids;
using Microsoft.AspNetCore.Components;
using Microsoft.JSInterop;
using System;
using System.Collections.Generic;
using System.Collections.ObjectModel;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Bcephal.Blazor.Web.Archive.Pages.ArchiveConfigs
{
    public partial class ArchiveConfigurationForm : Form<ArchiveConfig, ArchiveConfigBrowserData>
    {

        int ActiveTabIndex { get; set; } = 0;

        int ActiveTabIndexFilter { get; set; } = 0;

        protected ArchiveConfigEnrichmentItem ArchiveConfigEnrichmentItem { get; set; }

        [Inject]
        public ArchiveConfigurationService ArchiveConfigurationService { get; set; }

        [Inject]
        public GrilleService GrilleService { get; set; }

        private InputGridComponentForm_ InputGridComponentForm { get; set; }

        private ConfigurationGrid ConfigurationGrid { get; set; }

        public override string LeftTitle { get { return AppState["Archive.Configuration"]; } }

        public override bool usingUnitPane => false;

        [Inject]
        private IJSRuntime JSRuntime { get; set; }

        [CascadingParameter]
        public Error Errors { get; set; }

        [Inject]
        public IToastService ToastService { get; set; }

        [Inject]
        private WebSocketAddress WebSocketAddress { get; set; }

        protected override ArchiveConfigurationService GetService()
        {
            return ArchiveConfigurationService;
        }

        public bool Editable
        {
            get
            {
                var first = AppState.PrivilegeObserver.CanCreatedDataManagementArchiveConfig;
                var second = AppState.PrivilegeObserver.CanEditDataManagementArchiveConfig(EditorData.Item);
                return first || second;
            }
        }



        protected EditorData<Grille> BackupGrid
        {
            get
            {
                var Data = new EditorData<Grille>();

                Data.Item = EditorDataBinding.Item.BackupGrid;
                Data.Measures = EditorDataBinding.Measures;
                Data.Periods = EditorDataBinding.Periods;
                initFilters(Data.Item);
                return Data;
            }
            set
            {
                if (EditorDataBinding != null)
                {
                    EditorDataBinding.Item.BackupGrid = value.Item;
                    AppState.Update = true;
                }
            }

        }


        public override string GetBrowserUrl { get => Route.ARCHIVE_CONFIGURATION_BROWSER; set => base.GetBrowserUrl = value; }
        protected EditorData<Grille> ReplacementGrid
        {
            get
            {
                var Data = new EditorData<Grille>();

                Data.Item = EditorDataBinding.Item.ReplacementGrid;
                Data.Measures = EditorDataBinding.Measures;
                Data.Periods = EditorDataBinding.Periods;
                return Data;
            }
            set
            {
                if (EditorDataBinding != null)
                {
                    EditorDataBinding.Item.ReplacementGrid = value.Item;
                    AppState.Update = true;
                }
            }
        }

        protected override string DuplicateName()
        {
            return AppState["duplicate.archive.config.name", EditorData.Item.Name];
        }

        public override async ValueTask DisposeAsync()
        {
            if (AppState.CanExport)
            {
                AppState.CanExport = false;
                AppState.ExportDataHandler -= InputGridComponentForm.ExportData;
            }

            AppState.Update = false;
            AppState.CanExport = false;
            AppState.CanRun = false;
            AppState.RunHander -= CreateArchive;
            await base.DisposeAsync();
        }

        protected override async Task OnInitializedAsync()
        {
            await base.OnInitializedAsync();
        }       

        private bool checkItemsReplacement(string name, Bcephal.Models.Filters.DimensionType type)
        {
            if (EditorData == null || EditorData.Item == null)
            {
                return false;
            }
            ObservableCollection<GrilleColumn> Items = EditorData.Item.ReplacementGrid.ColumnListChangeHandler.GetItems();
            if (Items.Count == 0)
            {
                return true;
            }
            foreach (var item in Items)
            {
                if (item.Type.Equals(type))
                {
                    if (!string.IsNullOrEmpty(item.Name) && !string.IsNullOrEmpty(name))
                    {
                        if (item.Name.ToLower().Equals(name.ToLower()))
                        {
                            return false;
                        }
                    }
                }
            }
            return true;
        }

        private bool checkItemsBackup(string name, Bcephal.Models.Filters.DimensionType type)
        {
            if (EditorData == null || EditorData.Item == null)
            {
                return false;
            }
            ObservableCollection<GrilleColumn> Items = EditorData.Item.BackupGrid.ColumnListChangeHandler.GetItems();
            if (Items.Count == 0)
            {
                return true;
            }
            foreach (var item in Items)
            {
                if (item.Type.Equals(type))
                {
                    if (!string.IsNullOrEmpty(item.Name) && !string.IsNullOrEmpty(name))
                    {
                        if (item.Name.ToLower().Equals(name.ToLower()))
                        {
                            return false;
                        }
                    }
                }
            }
            return true;
        }

        private void StateHasChanged_()
        {
            StateHasChanged();
            AppState.Update = true;
        }

        private void addMeasureColumnReplacement(Models.Dimensions.Measure measure)
        {
            if (measure != null && checkItemsReplacement(measure.Name, Bcephal.Models.Filters.DimensionType.MEASURE))
            {
                GrilleColumn grilleColumn = new GrilleColumn();
                grilleColumn.Name = measure.Name;
                grilleColumn.DimensionName = measure.Name;
                grilleColumn.Type = DimensionType.MEASURE;
                grilleColumn.DimensionId = measure.Id.Value;
                EditorData.Item.ReplacementGrid.AddColumn(grilleColumn);
                if (ConfigurationGrid != null)
                {
                    ConfigurationGrid.selectedColumn(grilleColumn);
                }
                StateHasChanged_();
            }
        }

        private void addMeasureColumnBackup(Models.Dimensions.Measure measure)
        {
            if (measure != null && checkItemsBackup(measure.Name, Bcephal.Models.Filters.DimensionType.MEASURE))
            {
                GrilleColumn grilleColumn = new GrilleColumn();
                grilleColumn.Name = measure.Name;
                grilleColumn.DimensionName = measure.Name;
                grilleColumn.Type = DimensionType.MEASURE;
                grilleColumn.DimensionId = measure.Id.Value;
                EditorData.Item.BackupGrid.AddColumn(grilleColumn);
                if (ConfigurationGrid != null)
                {
                    ConfigurationGrid.selectedColumn(grilleColumn);
                }
                StateHasChanged_();
            }
        }

        private void addPeriodColumnReplacement(Models.Dimensions.Period period)
        {
            if (period != null && checkItemsReplacement(period.Name, Bcephal.Models.Filters.DimensionType.PERIOD))
            {
                Console.WriteLine(period);
                GrilleColumn grilleColumn = new GrilleColumn();
                grilleColumn.Name = period.Name;
                grilleColumn.DimensionName = period.Name;
                grilleColumn.Type = DimensionType.PERIOD;
                grilleColumn.DimensionId = period.Id.Value;
                EditorData.Item.ReplacementGrid.AddColumn(grilleColumn);
                if (ConfigurationGrid != null)
                {
                    ConfigurationGrid.selectedColumn(grilleColumn);
                }
                StateHasChanged_();
            }
        }

        private void addPeriodColumnBackup(Models.Dimensions.Period period)
        {
            if (period != null && checkItemsBackup(period.Name, Bcephal.Models.Filters.DimensionType.PERIOD))
            {
                GrilleColumn grilleColumn = new GrilleColumn();
                grilleColumn.Name = period.Name;
                grilleColumn.DimensionName = period.Name;
                grilleColumn.Type = DimensionType.PERIOD;
                grilleColumn.DimensionId = period.Id.Value;
                EditorData.Item.BackupGrid.AddColumn(grilleColumn);
                if (ConfigurationGrid != null)
                {
                    ConfigurationGrid.selectedColumn(grilleColumn);
                }
                StateHasChanged_();
            }
        }

        private void addAttributColumnReplacement(HierarchicalData attribute_)
        {
            Models.Dimensions.Attribute attribute = attribute_ as Models.Dimensions.Attribute;
            if (attribute != null && checkItemsReplacement(attribute.Name, Bcephal.Models.Filters.DimensionType.ATTRIBUTE))
            {
                Console.WriteLine(attribute);
                GrilleColumn grilleColumn = new GrilleColumn();
                grilleColumn.Name = attribute.Name;
                grilleColumn.DimensionName = attribute.Name;
                grilleColumn.Type = DimensionType.ATTRIBUTE;
                grilleColumn.DimensionId = attribute.Id.Value;
                EditorData.Item.ReplacementGrid.AddColumn(grilleColumn);
                if (ConfigurationGrid != null)
                {
                    ConfigurationGrid.selectedColumn(grilleColumn);
                }
                StateHasChanged_();
            }
        }

        private void addAttributColumnBackup(HierarchicalData attribute_)
        {
            Models.Dimensions.Attribute attribute = attribute_ as Models.Dimensions.Attribute;
            if (attribute != null && checkItemsBackup(attribute.Name, Bcephal.Models.Filters.DimensionType.ATTRIBUTE))
            {
                GrilleColumn grilleColumn = new GrilleColumn();
                grilleColumn.Name = attribute.Name;
                grilleColumn.DimensionName = attribute.Name;
                grilleColumn.Type = DimensionType.ATTRIBUTE;
                grilleColumn.DimensionId = attribute.Id.Value;
                EditorData.Item.BackupGrid.AddColumn(grilleColumn);
                if (ConfigurationGrid != null)
                {
                    ConfigurationGrid.selectedColumn(grilleColumn);
                }
                StateHasChanged_();
            }
        }

        protected override EditorDataFilter getEditorDataFilter()
        {
            return new EditorDataFilter();
        }

        protected override void OnAfterRender(bool firstRender)
        {
            base.OnAfterRender(firstRender);
            if (firstRender)
            {
                AppState.CanRun = true;
                AppState.RunHander += CreateArchive;

                if (Id.HasValue)
                {
                    ActiveTabIndex = 0;
                }
                else
                {
                    ActiveTabIndex = 1;
                }
            }

            if (ActiveTabIndex == 0 && InputGridComponentForm != null && !AppState.CanExport)
            {
                AppState.CanExport = true;
                AppState.ExportDataHandler += InputGridComponentForm.ExportData;
            }
            else if (ActiveTabIndex != 0 && AppState.CanExport)
            {
                AppState.CanExport = false;
                AppState.ExportDataHandler -= InputGridComponentForm.ExportData;
            }

            if(ActiveTabIndex == 0 || ActiveTabIndex == 3 || ActiveTabIndex == 4)
            {
                RefreshRightContent(null);
            }

            canDisplayError = true;
        }

        protected bool canDisplayError = true;

        public void CallBackCreateArchive(object sender, object message)
        {
            if (message != null && !string.IsNullOrWhiteSpace(message.ToString()))
            {
            }
        }

        public async void CreateArchive()
        {
            try
            {
                if(EditorData != null && EditorData.Item != null)
                {
                    AppState.ShowLoadingStatus();
                    SocketJS Socket = new SocketJS(WebSocketAddress, CallBackCreateArchive, JSRuntime, AppState, true);

                    bool valueClose = false;
                    bool valueError = false;
                    Socket.CloseHandler += () =>
                    {
                        if (!valueClose && !valueError)
                        {
                            AppState.HideLoadingStatus();
                            ToastService.ShowSuccess(AppState["ArchiveSuccess.create.message"], AppState["Loader"]);
                            valueClose = true;

                        }
                    };

                    Socket.ErrorHandler += (errorMessage) =>
                    {
                        if (!valueError)
                        {
                            AppState.HideLoadingStatus();
                            ToastService.ShowError((string)errorMessage, AppState["Error"]);
                            valueError = true;
                        }
                    };
                    Socket.SendHandler += () =>
                    {
                        AppState.HideLoadingStatus();

                        ArchiveConfig archiveConfig = new ArchiveConfig();
                        archiveConfig.Id = EditorData.Item.Id;
                        archiveConfig.ArchiveName = EditorData.Item.Name;
                        archiveConfig.Name = EditorData.Item.Name;
                        archiveConfig.Description = (EditorData.Item.Description != null) ? EditorData.Item.Description : null;
                        archiveConfig.CreationDate = EditorData.Item.CreationDate;
                        archiveConfig.ModificationDate = EditorData.Item.ModificationDate;
                        archiveConfig.BackupGrid = EditorData.Item.BackupGrid;
                        archiveConfig.ReplacementGrid = EditorData.Item.ReplacementGrid;
                        archiveConfig.EnrichmentItemListChangeHandler = EditorData.Item.EnrichmentItemListChangeHandler;

                        string data = ArchiveConfigurationService.Serialize(archiveConfig);
                        Socket.send(data);
                    };

                    await ArchiveConfigurationService.ConnectSocketJS(Socket, "/generation");

                    AppState.HideLoadingStatus();
                }
            }
            catch(Exception ex)
            {
                Errors.ProcessError(ex);
            }
        }

        protected override void AfterSave(EditorData<ArchiveConfig> EditorData)
        {
            base.AfterSave(EditorData);
            _ = ArchiveName;
        }

        protected EditorData<ArchiveConfig> ArchiveName
        {
            get
            {
                var Data = new EditorData<ArchiveConfig>();

                Data.Item = EditorDataBinding.Item;
                Data.Item.ArchiveName = EditorDataBinding.Item.Name;
                Data.Measures = EditorDataBinding.Measures;
                Data.Periods = EditorDataBinding.Periods;
                AppState.Update = true;
                return Data;
            }
            set
            {
                if (EditorDataBinding != null)
                {
                    EditorDataBinding.Item = value.Item;
                    EditorDataBinding.Item.ArchiveName = value.Item.Name;
                    AppState.Update = true;
                }
            }

        }
    }
}
