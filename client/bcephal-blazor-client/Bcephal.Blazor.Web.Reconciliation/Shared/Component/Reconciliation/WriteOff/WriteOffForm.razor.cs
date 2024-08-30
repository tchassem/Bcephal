
using Microsoft.AspNetCore.Components;
using Bcephal.Models.Reconciliation;
using System.Collections.ObjectModel;
using Bcephal.Models.Base;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;
using Bcephal.Models.Utils;
using Microsoft.JSInterop;
using Bcephal.Blazor.Web.Base.Services;

namespace Bcephal.Blazor.Web.Reconciliation.Shared.Component.Reconciliation.WriteOff
{
    public partial class WriteOffForm : ComponentBase
    {

        public string Wid80 { get; set; } = "width: 80%;";
        public string Value { get; set; }
 

        [Inject]
        private AppState AppState { get; set; }

        [Parameter]
        public ReconciliationModelEditorData WriteOffEditorData { get; set; }

        [Parameter]
        public bool Editable { get; set; }

        [Parameter]
        public EventCallback<ReconciliationModelEditorData> WriteOffEditorDataChanged { get; set; }

        [Parameter]
        public ObservableCollection<ReconciliationModelSide> ReconciliationModelSideTypes { get; set; }

        [Parameter]
        public ObservableCollection<WriteOffFieldValueType> WriteOffValueTypesPeriods { get; set; }

        [Parameter]
        public ObservableCollection<WriteOffFieldValueType> WriteOffValueTypesAttributes { get; set; }

        public bool IsCustom { get; set; }

        public bool AllowWriteOff { get; set; }

        [Inject]
        public IJSRuntime JSRuntime { get; set; }
        public void AllowWriteOffChanged(bool val)
        {
            AllowWriteOff = val;
            WriteOffEditorData.Item.AllowWriteOff = AllowWriteOff;
            WriteOffEditorDataChanged.InvokeAsync(WriteOffEditorData);

        }

        public ReconciliationModelSide WriteOffMeasureSide { get; set; } 
       public void WriteOffMeasureSideOnChange(ReconciliationModelSide val)
        {
            WriteOffMeasureSide = val;
            WriteOffEditorData.Item.WriteOffModel.WriteOffMeasureSide = WriteOffMeasureSide.code;
            if (WriteOffEditorData.Item.WriteOffModel.WriteOffMeasureId.HasValue)
            {
                WriteOffEditorData.Item.WriteOffModel.WriteOffMeasureId = null;
            }
            WriteOffEditorDataChanged.InvokeAsync(WriteOffEditorData);
        }

        public List<string> KeysWriteOff { get; set; } = new();
        public List<RenderFragment> RendersWriteOff { get; set; } = new();

        public Models.Dimensions.Measure WriteOffMeasure_ { get; set; }
        public Models.Dimensions.Measure WriteOffMeasure 
        {
            get
            {
                if(WriteOffEditorData.Item !=null && WriteOffEditorData.Item.WriteOffModel != null && WriteOffEditorData.Item.WriteOffModel.WriteOffMeasureId.HasValue)
                {
                    ObservableCollection<Models.Dimensions.Measure> ob = new ObservableCollection<Models.Dimensions.Measure>(WriteOffEditorData.Measures.Where(x => x.Id == WriteOffEditorData.Item.WriteOffModel.WriteOffMeasureId));
                    if(ob.Any())
                    {
                        WriteOffMeasure_ = ob.First();
                    }
                }
                return WriteOffMeasure_;
            }
            set 
            {
                WriteOffMeasure_ = value;
                WriteOffEditorData.Item.WriteOffModel.WriteOffMeasureId = WriteOffMeasure_.Id;
                WriteOffEditorDataChanged.InvokeAsync(WriteOffEditorData);
            }

        }
        public ObservableCollection<WriteOffField> ItemsWriteOff
        {
            get
            {
                if (WriteOffEditorData.Item.WriteOffModel != null)
                {
                    return WriteOffEditorData.Item.WriteOffModel.FieldListChangeHandler.GetItems();
                }
                else
                {
                    return new();
                }
            }

        }

        public async Task InitRenderItemsWriteOffField()
        {
            foreach (var item in ItemsWriteOff)
            {
                await AddRenderWriteOff(item, true);
            }
        }


        protected void AddRenderNextWriteOffField()
        {
            AddRenderWriteOff(new Bcephal.Models.Reconciliation.WriteOffField(), false);
        }
        private Task AddRenderWriteOff(WriteOffField item, bool addbutton)
        {
            KeysWriteOff.Add(item.Key);
            RendersWriteOff.Add(RenderWidget(item, addbutton));
            return Task.CompletedTask;
        }
        private void RemoveRenderWriteOff(Bcephal.Models.Reconciliation.WriteOffField Item)
        {
            int pos = KeysWriteOff.FindIndex(key => key == Item.Key);
            if (pos >= 0)
            {
                KeysWriteOff.RemoveAt(pos);
                RendersWriteOff.RemoveAt(pos);
            }
        }

      private  bool IsContains(WriteOffField item)
        {
            return WriteOffEditorData.Item.WriteOffModel.FieldListChangeHandler.GetItems()
                .Where(element => element.DimensionId == item.DimensionId && element.DimensionType == item.DimensionType
                ).Any();
        }

        public void AddFilterWriteOffField(WriteOffField item)
        {
            if (item != null)
            {

                if (!item.IsPersistent)
                {
                    if (IsContains(item))
                    {
                        WriteOffEditorData.Item.WriteOffModel.DeleteOrForgetWriteOffField(item);
                    }
                    WriteOffEditorData.Item.WriteOffModel.AddWriteOffField(item);
                }
                else
                {
                    WriteOffEditorData.Item.WriteOffModel.UpdateWriteOffField(item);
                }
                WriteOffEditorDataChanged.InvokeAsync(WriteOffEditorData);
            }
        }

        public void RemoveFilterWriteOffField(WriteOffField item)
        {
            if (item != null)
            {
                RemoveRenderWriteOff(item);
                WriteOffEditorData.Item.WriteOffModel.DeleteOrForgetWriteOffField(item);
                WriteOffEditorDataChanged.InvokeAsync(WriteOffEditorData);
            }
        }

        public ObservableCollection<HierarchicalData> Periods { get; set; }

        public ObservableCollection<HierarchicalData> Models { get; set; }

        public ObservableCollection<Models.Dimensions.Attribute> Attributes { get; set; } = new ObservableCollection<Models.Dimensions.Attribute>();

        public void GetHierarchicalDatas()
        {
            ObservableCollection<Models.Dimensions.Period> periods_ = WriteOffEditorData.Periods;
            Periods = new ObservableCollection<HierarchicalData>();
            periods_.ToList().ForEach(x => Periods.Add((HierarchicalData)x));
            InitEntities();
        }

        private void InitEntities()
        {
            int offset = 0;
            List<HierarchicalData> items = new List<HierarchicalData>();
            ObservableCollection<Models.Dimensions.Model> models_ = WriteOffEditorData.Models;

            while (WriteOffEditorData != null && models_ != null && offset < models_.Count)
            {
                Models.Dimensions.Model model = models_[offset];
                int offset2 = 0;
                while (model != null && model.Entities != null && offset2 < model.Entities.Count)
                {
                    Models.Dimensions.Entity Entity_ = model.Entities[offset2];
                    items.Add(Entity_);
                    int offset3 = 0;
                    while (Entity_ !=null && Entity_.Attributes !=null && offset3 < Entity_.Attributes.Count)
                    {
                        Attributes.Add(Entity_.Attributes[offset3]);
                        offset3++;
                    }
                    offset2++;
                }
                offset++;
            }
            items.BubbleSort();
            Models = new ObservableCollection<HierarchicalData>(items);
        }

        protected override  async Task OnInitializedAsync()
        {
            await base.OnInitializedAsync();
            GetHierarchicalDatas();
            await InitRenderItemsWriteOffField();
            await InvokeAsync(AddRenderNextWriteOffField);
            if (WriteOffEditorData != null && WriteOffEditorData.Item != null && WriteOffEditorData.Item.WriteOffModel != null)
            {
                AllowWriteOff = WriteOffEditorData.Item.AllowWriteOff;
                if(WriteOffEditorData.Item.WriteOffModel.WriteOffMeasureSide != null) 
                {
                    WriteOffMeasureSide = ReconciliationModelSide.GetByCode(WriteOffEditorData.Item.WriteOffModel.WriteOffMeasureSide);
                }            
            }
        }

        //public async void CloseTreeView()
        //{

        //    await JSRuntime.InvokeVoidAsync("CloseTreeView");
        //}

    }
}
