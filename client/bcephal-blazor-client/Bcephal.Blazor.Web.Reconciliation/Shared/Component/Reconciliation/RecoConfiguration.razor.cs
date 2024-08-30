using Bcephal.Blazor.Web.Base.Services;
using Bcephal.Models.Base;
using Bcephal.Models.Dimensions;
using Bcephal.Models.Reconciliation;
using Microsoft.AspNetCore.Components;
using System;
using System.Collections.Generic;
using System.Collections.ObjectModel;
using System.Threading.Tasks;

namespace Bcephal.Blazor.Web.Reconciliation.Shared.Component.Reconciliation
{
    public partial class RecoConfiguration : ComponentBase
    {

        [Inject] private AppState AppState { get; set; }
        [Parameter] public EditorData<ReconciliationModel> EditorData { get; set; }
        [Parameter] public EventCallback<EditorData<ReconciliationModel>> EditorDataChanged { get; set; }
        [Parameter] public EditorData<Bcephal.Models.Grids.Grille> EditorDataBindingLeft { get; set; }
        [Parameter] public EventCallback<EditorData<Bcephal.Models.Grids.Grille>> EditorDataBindingLeftChanged { get; set; }
        [Parameter] public EditorData<Bcephal.Models.Grids.Grille> EditorDataBindingRight { get; set; }
        [Parameter] public bool Editable { get; set; } = true;
        [Parameter] public EventCallback<EditorData<Bcephal.Models.Grids.Grille>> EditorDataBindingRightChanged { get; set; }
        public WriteOffModel Item_ { get; set; }
        [Parameter] public ObservableCollection<BrowserData> BGroups { get; set; } = new ObservableCollection<BrowserData>();
        [Parameter] public EventCallback<ObservableCollection<BrowserData>> BGroupsChanged { get; set; }
        [Parameter] public EventCallback<bool> IsSmallScreen_Changed { get; set; }
        [Parameter] public ObservableCollection<HierarchicalData> Entities { get; set; }

        [Parameter] public Action<RenderFragment> Filter { get; set; }

        private ObservableCollection<BrowserData> BGroups_
        {
            get => BGroups;
            set
            {
                BGroups = value;
                BGroupsChanged.InvokeAsync(BGroups);
            }
        }
        public EditorData<ReconciliationModel> EditorDataBinding
        {
            get { return EditorData; }
            set
            {
                EditorData = value;
                EditorDataChanged.InvokeAsync(EditorData);
            }
        }
        public ReconciliationModelEditorData GetEditorDataBinding
        {
            get { return GetEditorData; }
            set
            {
                EditorData = value;
                EditorDataChanged.InvokeAsync(EditorData);
            }
        }
        public ReconciliationModelEditorData GetEditorData
        {
           get { return (ReconciliationModelEditorData)EditorDataBinding; }
        }

        public ObservableCollection<WriteOffFieldValueType> WriteOffValueTypesPeriods { get; set; } 
            = new ObservableCollection<WriteOffFieldValueType>(new List<WriteOffFieldValueType>() { WriteOffFieldValueType.LEFT_SIDE, WriteOffFieldValueType.RIGHT_SIDE, WriteOffFieldValueType.FREE});

        public ObservableCollection<WriteOffFieldValueType> WriteOffValueTypesAttributes { get; set; } 
            = new ObservableCollection<WriteOffFieldValueType>(new List<WriteOffFieldValueType>() { WriteOffFieldValueType.LEFT_SIDE, WriteOffFieldValueType.RIGHT_SIDE, WriteOffFieldValueType.FREE, WriteOffFieldValueType.LIST_OF_VALUES });
        public ObservableCollection<ReconciliationModelSide> ReconciliationModelSideTypes { get; set; } 
            = new ObservableCollection<ReconciliationModelSide>(new List<ReconciliationModelSide>() { ReconciliationModelSide.LEFT, ReconciliationModelSide.RIGHT, ReconciliationModelSide.CUSTOM });


        

        protected override async Task OnAfterRenderAsync(bool firstRender)
        {
            await base.OnAfterRenderAsync(firstRender);
            if (firstRender)
            {
                Filter?.Invoke(RightContent);
            }
        }
    }
}
