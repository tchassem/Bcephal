using Bcephal.Blazor.Web.Base.Services;
using Bcephal.Models.Base;
using Bcephal.Models.Dimensions;
using Bcephal.Models.Grids;
using Bcephal.Models.Reconciliation;
using Microsoft.AspNetCore.Components;
using System.Collections.Generic;
using System.Collections.ObjectModel;
using System.Linq;
using System.Threading.Tasks;

namespace Bcephal.Blazor.Web.Reconciliation.Shared.Component.Reconciliation
{
    public partial class RecoEnrichMainComponent : ComponentBase
    {

        [Inject]
        private AppState AppState { get; set; }

        [Parameter]
        public ReconciliationModelEditorData EnrichmentValueEditorData { get; set; }

        [Parameter]
        public EventCallback<ReconciliationModelEditorData> EnrichmentValueEditorDataChanged { get; set; }

        [Parameter]
        public bool Editable { get; set; }

        [Parameter]
        public EditorData<Bcephal.Models.Grids.Grille> EditorDataBindingLeft { get; set; }
        [Parameter]
        public EventCallback<EditorData<Bcephal.Models.Grids.Grille>> EditorDataBindingLeftChanged { get; set; }

        [Parameter]
        public EditorData<Bcephal.Models.Grids.Grille> EditorDataBindingRight { get; set; }
        [Parameter]
        public EventCallback<EditorData<Bcephal.Models.Grids.Grille>> EditorDataBindingRightChanged { get; set; }

        [Parameter]
        public ObservableCollection<HierarchicalData> FreezeTypes { get; set; }

        public bool AddRecoDate
        {
            get
            {
                return EnrichmentValueEditorData.Item.AddRecoDate;
            }
            set
            {
                EnrichmentValueEditorData.Item.AddRecoDate = value;
                EnrichmentValueEditorDataChanged.InvokeAsync(EnrichmentValueEditorData);
            }
        }

        List<string> Keys = new();
        List<RenderFragment> renders = new();

        public Models.Dimensions.Period RecoPeriod_ { get; set; }

        public void PeriodChanged(Bcephal.Models.Dimensions.Period Periode)
        {
            if(Periode != null)
            {
                RecoPeriod_ = Periode;
                EnrichmentValueEditorData.Item.RecoPeriodId = RecoPeriod_.Id;
            }
            else
            {
                
                EnrichmentValueEditorData.Item.RecoPeriodId = null;
            }
            EnrichmentValueEditorDataChanged.InvokeAsync(EnrichmentValueEditorData);
        }

        public bool AddUser
        {
            get
            {
                return EnrichmentValueEditorData.Item.AddUser;
            }
            set
            {
                EnrichmentValueEditorData.Item.AddUser = value;
                EnrichmentValueEditorDataChanged.InvokeAsync(EnrichmentValueEditorData);
            }
        }

        public bool AddAutomaticManual
        {
            get
            {
                return EnrichmentValueEditorData.Item.AddAutomaticManual;
            }
            set
            {
                EnrichmentValueEditorData.Item.AddAutomaticManual = value;
                EnrichmentValueEditorDataChanged.InvokeAsync(EnrichmentValueEditorData);
            }
        }

        public bool IsActive
        {
            get
            {
                return EnrichmentValueEditorData.Item.AddNote;
            }
            set
            {
                EnrichmentValueEditorData.Item.AddNote = value;
                EnrichmentValueEditorDataChanged.InvokeAsync(EnrichmentValueEditorData);
            }
        }
        public bool IsMandatory
        {
            get
            {
                return EnrichmentValueEditorData.Item.MandatoryNote;
            }
            set
            {
                EnrichmentValueEditorData.Item.MandatoryNote = value;
                EnrichmentValueEditorDataChanged.InvokeAsync(EnrichmentValueEditorData);
            }

        }

        string NoteAttributeName => NoteAttribute != null ? NoteAttribute.Name : "";


        public HierarchicalData NoteAttribute
        {
            get
            {
                return GetAttribute(EnrichmentValueEditorData.Item.NoteAttributeId);
            }
            set
            {
                if (value != null)
                {
                    EnrichmentValueEditorData.Item.NoteAttributeId = value.Id;
                }
                else
                {
                    EnrichmentValueEditorData.Item.NoteAttributeId = null;
                }
                EnrichmentValueEditorDataChanged.InvokeAsync(EnrichmentValueEditorData);
            }
        }
        HierarchicalData GetAttribute(long? id)
        {
            if (id.HasValue)
            {
                foreach (var item in FreezeTypes)
                {
                    foreach (var attib in ((Entity)item).Attributes)
                    {
                        if (attib.Id.HasValue && attib.Id.Value == id.Value)
                        {
                            return attib;
                        }
                    }
                }
            }
            return null;
        }



        private ObservableCollection<GrilleColumn> EnrichValuesTargetSideLeft { get; set; } = new ObservableCollection<GrilleColumn>();

        private ObservableCollection<GrilleColumn> EnrichValuesTargetSideRight { get; set; } = new ObservableCollection<GrilleColumn>();

        protected override async Task OnInitializedAsync()
        {
            await base.OnInitializedAsync();
            InitConfigLeftRightGrid();
            await InitRenderItems();
            if (Items.Any())
            {
                if (Items.Last().TargetColumnId.HasValue)
                {
                    await InvokeAsync(AddRenderNext);
                }
            }
            else
            {
                await InvokeAsync(AddRenderNext);
            }

        }

        public void InitConfigLeftRightGrid()
        {
            if (EditorDataBindingLeft != null && EditorDataBindingLeft.Item != null)
            {
                EnrichValuesTargetSideLeft = new ObservableCollection<GrilleColumn>(EditorDataBindingLeft.Item.GetPersistentColumns());
            }
            if (EditorDataBindingRight != null && EditorDataBindingRight.Item != null)
            {
                EnrichValuesTargetSideRight = new ObservableCollection<GrilleColumn>(EditorDataBindingRight.Item.GetPersistentColumns());
            }
        }

        public async Task InitRenderItems()
        {
            foreach (var item in Items)
            {
                await AddRenderReco(item, true);
            }
        }

        protected void AddRenderNext()
        {
            AddRenderReco(new Bcephal.Models.Reconciliation.ReconciliationModelEnrichment(), false);
        }

        protected Task AddRenderReco(Bcephal.Models.Reconciliation.ReconciliationModelEnrichment item, bool addbutton)
        {
            Keys.Add(item.Key);
            renders.Add(RenderWidgetReco(item, addbutton));
            StateHasChanged();
            return Task.CompletedTask;
        }

        protected void RemoveRenderReco(Bcephal.Models.Reconciliation.ReconciliationModelEnrichment Item)
        {
            int pos = Keys.FindIndex(key => key == Item.Key);
            if (pos >= 0)
            {
                Keys.RemoveAt(pos);
                renders.RemoveAt(pos);
            }
        }

        private void RemoveFilter(ReconciliationModelEnrichment item)
        {

            RemoveRenderReco(item);
            EnrichmentValueEditorData.Item.DeleteOrForgetEnrichment(item);
            EnrichmentValueEditorDataChanged.InvokeAsync(EnrichmentValueEditorData);

        }

        private bool IsContain(ReconciliationModelEnrichment item)
        {
            return EnrichmentValueEditorData.Item.EnrichmentListChangeHandler.GetItems().Where(x => x.TargetColumnId == item.TargetColumnId).Any();
        }

        private void AddFilter(ReconciliationModelEnrichment item)
        {
            if (!item.IsPersistent)
            {
                if (!IsContain(item))
                {
                    EnrichmentValueEditorData.Item.AddEnrichment(item);
                }
                
            }
            else
            {
                EnrichmentValueEditorData.Item.UpdateEnrichment(item);
            }
            EnrichmentValueEditorDataChanged.InvokeAsync(EnrichmentValueEditorData);
        }

        public ObservableCollection<ReconciliationModelEnrichment> Items
        {
            get
            {
                if (EnrichmentValueEditorData.Item.EnrichmentListChangeHandler != null)
                {
                    return EnrichmentValueEditorData.Item.EnrichmentListChangeHandler.GetItems();
                }
                else
                {
                    return new();
                }
            }
        }

        public void AttributeChanged(HierarchicalData Attribute)
        {
            NoteAttribute = Attribute;
            StateHasChanged();
        }

    }
}
