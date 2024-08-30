using Bcephal.Blazor.Web.Base.Services;
using Bcephal.Models.Base;
using Bcephal.Models.Filters;
using Bcephal.Models.Planners;
using Microsoft.AspNetCore.Components;
using System;
using System.Collections.Generic;
using System.Collections.ObjectModel;
using System.Linq;
using System.Numerics;
using System.Text;
using System.Threading.Tasks;

namespace Bcephal.Blazor.Web.Scheduling.Pages.SchedulerPlanner_
{
    public partial class SchedulerPlannerItemComponent
    {
        [Inject]
        public AppState AppState { get; set; }

        [Parameter]
        public EditorData<SchedulerPlanner> EditorData { get; set; }

        [Parameter]
        public SchedulerPlannerItem Item { get; set; }

        [Parameter]
        public bool Editable { get; set; } = true;

        [Parameter]
        public bool IsAdded { get; set; }

        [Parameter]
        public EventCallback<EditorData<SchedulerPlanner>> EditorDataChanged { get; set; }

        SchedulerPlannerEditorData PlannerEditorData => GetEditorData();

        SchedulerPlannerItemType itemType => SchedulerPlannerItemType.ACTION;
        private ObservableCollection<string> SchedPlrItemTypeCollection_ { get => itemType.GetAll(text => AppState[text]); }

        MeasureOperator itemOperator => MeasureOperator.EQUALS_;
        private ObservableCollection<string> ItemOperatorCollection_ { get => itemOperator.GetAllForJoin(text => AppState[text]); }

        SchedulerPlannerItemTemporisationUnit temporisationUnit => SchedulerPlannerItemTemporisationUnit.DAY;
        private ObservableCollection<string> SchedPlrItemTempUnitCollection_ { get => temporisationUnit.GetAll(text => AppState[text]); }

        protected override async Task OnInitializedAsync()
        {
            if (Item == null)
            {
                Item = new SchedulerPlannerItem();
            }
            await base.OnInitializedAsync();
        }

        public string Code
        {
            get
            {          
                return Item.Code;
            }
            set
            {
                Item.Code = value;
                AddOrUpdateItem();
            }
        }

        public string ItemType
        {
            get
            {
                return Item.ItemType.GetText(text => AppState[text]);
            }
            set
            {
                Item.ItemType = Item.ItemType.GetItemType(value, text => AppState[text]);
                if (string.IsNullOrEmpty(Item.Code))
                {
                    Item.Code = GetPlannerCode();
                }
                ResetFields();
                if (Item.ItemType.IsAction())
                {
                    Item.Action1 = new();
                    Item.Action2 = null;
                } else if (Item.ItemType.IsCheck())
                {
                    Item.Action1 = new();
                    Item.Action2 = new();
                } else
                {
                    Item.Action1 = null;
                    Item.Action2 = null;
                }
                AddOrUpdateItem();
            }
        }

        public bool Active
        {
            get
            {
                return Item.Active;
            }
            set
            {
                Item.Active = value;
                if (IsAdded)
                {
                    AddOrUpdateItem();
                }
            }
        }

        public SchedulerPlannerItemAction Action1
        {
            get
            {
                return Item.Action1;
            }
            set
            {
                Item.Action1 = value;
                AddOrUpdateItem();
            }
        }

        public SchedulerPlannerItemAction Action2
        {
            get
            {
                return Item.Action2;
            }
            set
            {
                Item.Action2 = value;
                AddOrUpdateItem();
            }
        }

        private Nameable BillingItem
        {
            get
            {
                return GetEditorData().Billing.Where((b) => Item.ObjectId.HasValue && b.Id.HasValue && b.Id.Value == Item.ObjectId.Value).FirstOrDefault();
            }
            set
            {
                Item.ObjectId = value.Id;
                Item.ObjectName = value.Name;
                AddOrUpdateItem();
            }
        }

        private Nameable SpotItem
        {
            get
            {
                return GetEditorData().Spots.Where((b) => Item.ObjectId.HasValue && b.Id.HasValue && b.Id.Value == Item.ObjectId.Value).FirstOrDefault();
            }
            set
            {
                Item.ObjectId = value.Id;
                Item.ObjectName = value.Name;
                AddOrUpdateItem();
            }
        }

        private string Comparator
        {
            get
            {
                return Item.Comparator;
            }
            set
            {
                Item.Comparator = value;
                AddOrUpdateItem();
            }
        }

        private decimal? DecimalValue
        {
            get
            {
                return Item.DecimalValue;
            }
            set
            {
                Item.DecimalValue = value;
                AddOrUpdateItem();
            }
        }

        private Nameable JoinItem
        {
            get
            {
                return GetEditorData().Joins.Where((b) => Item.ObjectId.HasValue && b.Id.HasValue && b.Id.Value == Item.ObjectId.Value).FirstOrDefault();
            }
            set
            {
                Item.ObjectId = value.Id;
                Item.ObjectName = value.Name;
                AddOrUpdateItem();
            }
        }

        private Nameable RecoItem
        {
            get
            {
                return GetEditorData().Recos.Where((b) => Item.ObjectId.HasValue && b.Id.HasValue && b.Id.Value == Item.ObjectId.Value).FirstOrDefault();
            }
            set
            {
                Item.ObjectId = value.Id;
                Item.ObjectName = value.Name;
                AddOrUpdateItem();
            }
        }

        private Nameable RoutineItem
        {
            get
            {
                return GetEditorData().Routines.Where((b) => Item.ObjectId.HasValue && b.Id.HasValue && b.Id.Value == Item.ObjectId.Value).FirstOrDefault();
            }
            set
            {
                Item.ObjectId = value.Id;
                Item.ObjectName = value.Name;
                AddOrUpdateItem();
            }
        }

        private Nameable TreeItem
        {
            get
            {
                return GetEditorData().Trees.Where((b) => Item.ObjectId.HasValue && b.Id.HasValue && b.Id.Value == Item.ObjectId.Value).FirstOrDefault();
            }
            set
            {
                Item.ObjectId = value.Id;
                Item.ObjectName = value.Name;
                AddOrUpdateItem();
            }
        }

        private int TemporisationValue
        {
            get
            {
                return Item.TemporisationValue;
            }
            set
            {
                Item.TemporisationValue = value;
                AddOrUpdateItem();
            }
        }

        public string TemporisationUnit
        {
            get
            {
                return Item.ItemTemporisationUnit.GetText(text => AppState[text]);
            }
            set
            {
                Item.ItemTemporisationUnit = Item.ItemTemporisationUnit.GetItemTemporisationUnit(value, text => AppState[text]);
                AddOrUpdateItem();
            }
        }

        private void AddOrUpdateItem()
        {
            if (!IsAdded)
            {
                EditorData.Item.AddItem(Item);
            }
            else
            {
                EditorData.Item.UpdateItem(Item);
            }
            EditorDataChanged.InvokeAsync(EditorData);
            AppState.Update = true;
            StateHasChanged();
        }

        private async void RemoveItem(SchedulerPlannerItem plannerItem)
        {
            GetEditorData().Item.DeleteItem(plannerItem);
            await EditorDataChanged.InvokeAsync(EditorData);
            AppState.Update = true;
            StateHasChanged();
        }

        private SchedulerPlannerEditorData GetEditorData()
        {
            return (SchedulerPlannerEditorData) EditorData;
        }

        private string GetPlannerCode()
        {
            int Timestamp = (int)DateTime.UtcNow.Subtract(new DateTime(1970, 1, 1)).TotalSeconds;
            string res = Timestamp.ToString();
            return res.Substring(res.Length - 3);
        }

        private void ResetFields()
        {
            Item.Action1 = null;
            Item.Action2 = null;
            Item.ObjectId = null;
            Item.ObjectName = "";
            Item.Comparator = "";
            Item.DecimalValue = null;
            Item.TemporisationValue = 0;
        }
    }
}
