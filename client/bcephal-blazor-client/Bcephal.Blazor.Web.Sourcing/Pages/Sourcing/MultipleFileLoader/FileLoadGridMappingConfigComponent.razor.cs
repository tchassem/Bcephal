using Bcephal.Blazor.Web.Base.Services;
using Bcephal.Blazor.Web.Base.Shared.Component;
using Bcephal.Models.Base;
using Bcephal.Models.Grids;
using Bcephal.Models.Loaders;
using DevExpress.Blazor;
using Microsoft.AspNetCore.Components;
using Microsoft.AspNetCore.Components.Forms;
using Microsoft.AspNetCore.Components.Rendering;
using System;
using System.Collections.Generic;
using System.Collections.ObjectModel;
using System.Threading.Tasks;

namespace Bcephal.Blazor.Web.Sourcing.Pages.Sourcing.MultipleFileLoader
{
    public partial class FileLoadGridMappingConfigComponent: ComponentBase 
    {
        [Inject] private AppState AppState { get; set; }
        private string Repository_ { get; set; }
        [Parameter] public bool Editable { get; set; } = true;
        [Parameter] public string Repository
        {
            get => Repository_;
            set
            {
                Repository_ = value;
                if (!string.IsNullOrWhiteSpace(value))
                {
                    CanRefreshMappingGrid = true;
                }
                else
                {
                    CanRefreshMappingGrid = false;
                }
            }
        }

        [Parameter] public List<IBrowserFile> loadedFiles { get; set; }
        [Parameter] public EditorData<FileLoader> EditorData { get; set; }
        [Parameter] public EventCallback<EditorData<FileLoader>> EditorDataChanged { get; set; }
        [Parameter] public ObservableCollection<HierarchicalData> Entities { get; set; }
        [Parameter] public ObservableCollection<GrilleColumn> Columns { get; set; } = new ObservableCollection<GrilleColumn>();
        [Parameter] public Action CustomChangedState { get; set; }
        private FileLoaderGrilleBrowser FileLoaderGrilleBrowserRef { get; set; }
        private FileLoaderNewGrilleBrowser FileLoaderNewGrilleBrowserRef { get; set; }
        private DxToolbarItem BtnRefresh { get; set; }
        private bool ClickedRefresh { get; set; } = false;

        public EditorData<FileLoader> EditorDataBinding
        {
            get => EditorData;
            set
            {
                EditorData = value;
                EditorDataChanged.InvokeAsync(EditorData);
            }
        }

        private async Task RefreshGrid()
        {
            AppState.ShowLoadingStatus();
            //await  FileLoaderGrilleBrowserRef.RefreshGrid(true);
            //await FileLoaderGrilleBrowserRef.RefreshWebSocketGrid(true);
            if (FileLoaderNewGrilleBrowserRef != null)
            {
                await FileLoaderNewGrilleBrowserRef.RefreshWebSocketGrid(Repository, () => { Repository = null; StateHasChanged(); }, true);
            }
            CustomChangedState?.Invoke();
            AppState.HideLoadingStatus();
        }

        private bool Visible { get; set; } = false;
        private bool CanRefreshMappingGrid { get; set; }

        protected override async void OnAfterRender(bool firstRender)
        {
            if (firstRender)
            {
                if (!string.IsNullOrWhiteSpace(Repository))
                {
                    ClickedRefresh = true;
                    await RefreshGrid();
                }
            }
            if (BtnRefresh != null && !ClickedRefresh && !EditorData.Item.IsPersistent)
            {
                ClickedRefresh = true;
                await RefreshGrid();
            }
            await base.OnAfterRenderAsync(firstRender);
        }
    }
}
