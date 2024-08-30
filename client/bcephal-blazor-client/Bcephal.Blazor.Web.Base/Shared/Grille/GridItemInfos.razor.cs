
using Bcephal.Blazor.Web.Base.Services;
using Bcephal.Blazor.Web.Base.Shared.Component;
using Bcephal.Models.Base;
using Bcephal.Models.Grids.Filters;
using Microsoft.AspNetCore.Components;
using System;
using System.Collections.Generic;
using System.Collections.ObjectModel;
using System.Linq;
using System.Threading;
using System.Threading.Tasks;

namespace Bcephal.Blazor.Web.Base.Shared.Grille
{
    public partial class GridItemInfos<T> : ComponentBase where T : MainObject
    {
        
        public bool IsSmallScreen { get; set; }

        [Inject]  private AppState AppState { get; set; }
        [Inject]  private BGroupService BGroupService { get; set; }
        [Parameter] public bool Editable { get; set; } = true;
        [CascadingParameter] public Error Error { get; set; }
        [Parameter] public RenderFragment<ComponentBase> ChildContent { get; set; }
        [Parameter] public EditorData<T> EditorData { get; set; }
        [Parameter] public EventCallback<EditorData<T>> EditorDataChanged { get; set; }
        [Parameter] public Action<EditorData<T>> EditorDataHandler { get; set; }
        [Parameter] public ObservableCollection<BrowserData> BGroups { get; set; } = new ObservableCollection<BrowserData>();
        [Parameter] public EventCallback<ObservableCollection<BrowserData>> BGroupsChanged { get; set; }        
        [Parameter] public bool DisplayGroup { get; set; } = true;
        [Parameter] public bool DisplayVisibleInShortcut { get; set; } = true;
        
        public string  LabelWidth { get; set; } = Constant.GROUP_INFOS_LABEL_WIDTH;
        public string TextWidth { get; set; } = Constant.GROUP_INFOS_TEXT_WIDTH;

        public string Name
        {
            get {
                if (EditorData != null && EditorData.Item != null)
                { 
                    return EditorData.Item.Name;
                }
                return "";
            }
            set
            {
                if (EditorData != null && EditorData.Item != null)
                {
                    EditorData.Item.Name = value;
                    if (EditorDataChanged.HasDelegate) {
                        EditorDataChanged.InvokeAsync(EditorData);
                    }
                    else
                    {
                        EditorDataHandler?.Invoke(EditorData);
                    }
                }
            }
        }

        

        public BrowserData group
        {
            get {
                if (EditorData != null && EditorData.Item != null)
                {
                    BrowserData bgs = new BrowserData();                    
                    if (EditorData.Item.group != null)
                    {
                        bgs.Id = EditorData.Item.group.Id;
                        bgs.Name = EditorData.Item.group.Name;
                        bgs.CreationDate = EditorData.Item.group.CreationDate;
                    }
                    return bgs;
                }
                return null;
            }
            set
            {
                if (EditorData != null && EditorData.Item != null)
                {
                    if (EditorData.Item.group == null && value != null)
                    {
                        EditorData.Item.group = new BGroup();
                    }
                    if (value != null)
                    {
                        EditorData.Item.group.Id = value.Id;
                        EditorData.Item.group.Name = value.Name;
                        EditorData.Item.group.CreationDate = value.CreationDate;
                    }
                    else
                    {
                        EditorData.Item.group = null;
                    }
                    if (EditorDataChanged.HasDelegate)
                    {
                        EditorDataChanged.InvokeAsync(EditorData);
                    }
                    else
                    {
                        EditorDataHandler?.Invoke(EditorData);
                    }
                }
            }
        }

        public bool VisibleInShortcut
        {
            get {
                if (EditorData != null && EditorData.Item != null)
                {

                    return EditorData.Item.VisibleInShortcut;
                }
                return false;
            }
            set
            {
                if (EditorData != null && EditorData.Item != null)
                {
                    EditorData.Item.VisibleInShortcut = value;
                    if (EditorDataChanged.HasDelegate)
                    {
                        EditorDataChanged.InvokeAsync(EditorData);
                    }
                    else
                    {
                        EditorDataHandler?.Invoke(EditorData);
                    }
                }
            }
        }

       private async Task<BGroup> getNewBGroup()
        {
            try
            {
                EditorDataFilter Filter = new EditorDataFilter();
                Filter.NewData = true;
                EditorData<BGroup> data = await BGroupService.GetEditorData(Filter);
                return data.Item;
            }
            catch (Exception ex)
            {
                Error.ProcessError(ex);
            }
            return new BGroup();
        }

        private async Task addGroup(BGroup bGroup)
        {
            try
            {
                if (bGroup != null && !string.IsNullOrWhiteSpace(bGroup.Name))
                {
                    EnablePopup = false;
                    LoadingText = AppState["saving"];
                    BGroup bg = await BGroupService.Save(bGroup);
                    BrowserData bgs = new BrowserData();
                    bgs.Id = bg.Id;
                    bgs.Name = bg.Name;
                    bgs.CreationDate = bg.CreationDate;
                    BGroups.Append(bgs);
                    popup = false;
                    EnablePopup = true;
                    LoadingText = AppState["Save"];
                    newBgroup = null;
                    await BGroupsChanged.InvokeAsync(BGroups);
                }
            }
            catch (Exception ex)
            {
                EnablePopup = true;
                LoadingText = AppState["Save"];
                Error.ProcessError(ex);
            }
        }

        protected async Task<IEnumerable<BrowserData>> BGroups_(CancellationToken cancellation)
        {
            if (BGroups == null || BGroups.Count == 0)
            {
                try
                {
                    BrowserDataFilter Filter = new BrowserDataFilter();
                    BrowserDataPage<BrowserData> page = await BGroupService.Search(Filter);
                    BGroups = page.Items;
                    if (BGroups != null && BGroups.Count() == 0)
                    {
                        BGroup bg = await BGroupService.Save(new BGroup(BGroup.DEFAULT_GROUP_NAME));
                        BrowserData bgs = new BrowserData();
                        bgs.Id = bg.Id;
                        bgs.Name = bg.Name;
                        bgs.CreationDate = bg.CreationDate;
                        BGroups.Append(bgs);
                        bg = await BGroupService.Save(new BGroup(BGroup.DEFAULT_GRID_ALLOCATION_GROUP_NAME));
                        bgs.Id = bg.Id;
                        bgs.Name = bg.Name;
                        bgs.CreationDate = bg.CreationDate;
                        bgs = new BrowserData();
                        BGroups.Append(bgs);
                    }
                    return BGroups;
                }
                catch (Exception ex)
                {
                    Error.ProcessError(ex);
                }
            }
            return BGroups;
        }

        private bool popup { get; set; } = false;
        private bool EnablePopup { get; set; }
        private string LoadingText { get; set; }
        private BGroup newBgroup { get; set; }
        protected async void showPopup()
        {
            popup = true;
            EnablePopup = true;
            LoadingText = AppState["Save"];
            newBgroup = await getNewBGroup();
            StateHasChanged();
        }

        private string newBgroupName { 
            get { return newBgroup != null ? newBgroup.Name : ""; }
            set {
                if (newBgroup != null)
                {
                    newBgroup.Name = value;
                    if (string.IsNullOrWhiteSpace(value))
                    {
                        EnablePopup = false;
                    }
                    else
                    {
                        EnablePopup = true;
                    }
                }
            }
        }
    }    
}
