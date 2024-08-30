using Microsoft.AspNetCore.Components;
using System.Collections.Generic;
using Bcephal.Models.Reconciliation;
using System.Collections.ObjectModel;
using Bcephal.Models.Base;
using System.Linq;
using System;
using System.Threading.Tasks;
using Bcephal.Models.Filters;

namespace Bcephal.Blazor.Web.Reconciliation.Shared.Component.Reconciliation.WriteOff
{
    public partial class WriteOffFormItem : ComponentBase
    {
        [Parameter]
        public EditorData<ReconciliationModel> WriteOffEditorDataBinding { get; set; }

        [Parameter]
        public WriteOffField Item { get; set; }

        [Parameter]
        public EventCallback<WriteOffField> CallBackRemove { get; set; }

        [Parameter]
        public EventCallback<WriteOffField> CallBackAddorUpdate { get; set; }

        [Parameter]
        public int offset { get; set; }

        [Parameter]
        public bool IsMandatory { get; set; }

        [Parameter]
        public bool RemoveButton { get; set; }

        public bool RemoveButton_ { get; set; }

        [Parameter]
        public Action AddRenderNext { get; set; }

        IEnumerable<string> Values  = new List<string>() { "ATTRIBUTE", "PERIOD" };

        public string top = "25%";

        [Parameter]
        public ObservableCollection<HierarchicalData> Periods { get; set; }


        [Parameter]
        public ObservableCollection<HierarchicalData> Models { get; set; }

        [Parameter]
        public ObservableCollection<Models.Dimensions.Attribute> Attributes { get; set; }

        [Parameter]
        public ObservableCollection<WriteOffFieldValueType> WriteOffValueTypesPeriods { get; set; }

        [Parameter]
        public ObservableCollection<WriteOffFieldValueType> WriteOffValueTypesAttributes { get; set; }

        [Parameter]
        public bool Editable { get; set; }

        public WriteOffFieldValueType ValueType_ { get; set; }


        public WriteOffFieldValueType ValueType
        {
            get 
            {
                return  !String.IsNullOrEmpty(Item.DefaultValueType) ? Item.DefaultValueTypes : ValueType_;
            }
            set 
            {
                ValueType_ = value;
            }
        }

        public List<BrowserData> SelectedItemList { get; set; }

        public void ValueTypeCallBack(WriteOffFieldValueType val)
        {
            Item.DefaultValueTypes = val;
            Item.DefaultValueType = val.code;
            ValueType_ = val;
            if (val.Equals(WriteOffFieldValueType.LIST_OF_VALUES))
            {
                if (RendersWriteOffItem.Any())
                {
                    RendersWriteOffItem.Clear();
                }
                if (KeysWriteOff.Any())
                {
                    KeysWriteOff.Clear();
                }
                InvokeAsync(AddRenderNextWriteOffFieldValue);
            }
            CallBackAddorUpdate.InvokeAsync(Item);
        }

        public string Value_ { get; set; }

        public string Value 
        { 
            get
            {
                if (Item.DimensionType.IsAttribute())
                {
                    Value_ = "ATTRIBUTE";
                }
                else if(Item.DimensionType.IsPeriod())
                {
                    Value_ = "PERIOD";
                }
                return Value_;
            }
        
        } 
     
        public void FilterChanged(HierarchicalData Hieralchicaldata_)
        {
            if (!Item.Id.HasValue && !Item.DimensionId.HasValue)
            {
                AddRenderNext.Invoke();
            }
            Item.DimensionId = Hieralchicaldata_.Id;
            DimensionName_ = Hieralchicaldata_.Name;
            CallBackAddorUpdate.InvokeAsync(Item);        
        }

        public string DimensionName_ { get; set; }
        public string DimensionName
        {
            get
            {
                if ( Item !=null && Item.DimensionId.HasValue)
                {
                    if (Item.DimensionType.Equals(DimensionType.PERIOD))
                    {
                        if (Periods != null && Periods.Any())
                        {
                            if (Periods.Where(x => x.Id == Item.DimensionId).Any())
                            {
                                DimensionName_ = Periods.Where(x => x.Id == Item.DimensionId).First().Name;
                            }
                        }  
                    }else if (Item.DimensionType.Equals(DimensionType.ATTRIBUTE))
                    {
                        if (Attributes != null && Attributes.Any())
                        {
                            if (Attributes.Where(x => x.Id == Item.DimensionId).Any())
                            {
                                DimensionName_ = Attributes.Where(x => x.Id == Item.DimensionId).First().Name;
                            }
                        }
                    }
                }
                return DimensionName_; 
            }
        }


        public void OnValueChanged(string val)
        {
            Value_ = val;
           
            if (val.Equals("ATTRIBUTE"))
            {
                Item.DimensionType = DimensionType.ATTRIBUTE;
            }
            else if(val.Equals("PERIOD"))
            {
                Item.DimensionType = DimensionType.PERIOD;
            }

        }

        protected override async Task OnInitializedAsync()
        {
            await base.OnInitializedAsync();
            if (Item != null)
            {
                if (!String.IsNullOrEmpty(Item.DefaultValueType))
                {
                    ValueType = Item.DefaultValueTypes;
                    if (ValueType.Equals(WriteOffFieldValueType.LIST_OF_VALUES))
                    {
                        await InvokeAsync(InitRenderItemsWriteOffFieldValue);
                        await InvokeAsync(AddRenderNextWriteOffFieldValue);
                    }

                }
            }
            SelectedItemList = new List<BrowserData>();
            if (Item.ValueListChangeHandler != null && Item.ValueListChangeHandler.GetItems().Any())
            {
                List<WriteOffFieldValue> items = Item.ValueListChangeHandler.GetItems().ToList();
                foreach (WriteOffFieldValue writeOffFieldValue in items)
                {
                    SelectedItemList.Add(new BrowserData() { Name = writeOffFieldValue.StringValue });
                }
            }

        }

      
        public List<string> KeysWriteOff { get; set; } = new();
        public List<RenderFragment> RendersWriteOffItem { get; set; } = new();

   
        public ObservableCollection<WriteOffFieldValue> ItemsWriteOff
        {
            get
            {
                if (WriteOffEditorDataBinding.Item.WriteOffModel != null)
                {
                    return Item.ValueListChangeHandler.GetItems();
                }
                else
                {
                    return new();
                }
            }

        }

        public async Task InitRenderItemsWriteOffFieldValue()
        {
            foreach (var item in ItemsWriteOff)
            {
                await AddRenderWriteOffFieldValue(item, true);
            }
        }


        protected void AddRenderNextWriteOffFieldValue()
        {
            AddRenderWriteOffFieldValue(new Bcephal.Models.Reconciliation.WriteOffFieldValue(), false);
        }
        private Task AddRenderWriteOffFieldValue(Bcephal.Models.Reconciliation.WriteOffFieldValue item, bool addbutton)
        {
            KeysWriteOff.Add(item.Key);
            RendersWriteOffItem.Add(RenderWriteOffFieldValue(item, addbutton));
            return Task.CompletedTask;
        }
        private void RemoveRenderWoffFieldValue(Bcephal.Models.Reconciliation.WriteOffFieldValue Item)
        {
            int pos = KeysWriteOff.FindIndex(key => key == Item.Key);
            if (pos >= 0)
            {
                KeysWriteOff.RemoveAt(pos);
                RendersWriteOffItem.RemoveAt(pos);
            }
        }

        public void AddFilterWriteOffFieldValue(Bcephal.Models.Reconciliation.WriteOffFieldValue item)
        {
            if (!item.IsPersistent)
            {
                Item.AddWriteOffFieldValue(item);
            }
            else
            {
                Item.UpdateWriteOffFieldValue(item);
            }
            CallBackAddorUpdate.InvokeAsync(Item);
        }

        public void RemoveFilterWriteOffFieldValue(Bcephal.Models.Reconciliation.WriteOffFieldValue item_)
        {
            if (item_ != null && !String.IsNullOrEmpty(item_.StringValue))
            {
                Item.DeleteOrForgetWriteOffFieldValue(item_);
                List<BrowserData> SelectedItemList_ = new List<BrowserData>();
                foreach (BrowserData browserData in SelectedItemList)
                {
                    if (!browserData.Name.Equals(item_.StringValue))
                    {
                        SelectedItemList_.Add(browserData);
                    }
                }
                if (SelectedItemList_.Any())
                {
                    SelectedItemList.Clear();
                    SelectedItemList = SelectedItemList_;
                }
                RemoveRenderWoffFieldValue(item_);
                CallBackAddorUpdate.InvokeAsync(Item);
            }          
        }
    }

}
