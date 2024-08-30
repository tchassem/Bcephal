using Bcephal.Blazor.Web.Accounting.Services;
using Bcephal.Blazor.Web.Base.Services;
using Bcephal.Blazor.Web.Base.Shared;
using Bcephal.Models.Base;
using Bcephal.Models.Base.Accounting;
using Bcephal.Models.Billing.Model;
using Bcephal.Models.Filters;
using Microsoft.AspNetCore.Components;
using Microsoft.JSInterop;
using System;
using System.Collections.Generic;
using System.Collections.ObjectModel;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Bcephal.Blazor.Web.Accounting.Pages.BookingModels
{
    public partial class BookingModelForm : Form<BookingModel, BrowserData>
    {
        [Inject]
        public BookingModelService BookingModelService { get; set; }

        public override string LeftTitle { get { return AppState["New.Booking.Model"]; } }

        public override string LeftTitleIcon { get { return "bi-plus"; } }

        public bool IsSmallScreen { get; set; }

        public int ActiveTabIndex { get; set; } = 0;

        public string LabelWidth { get; set; } = Constant.GROUP_INFOS_LABEL_WIDTH;

        public string TextWidth { get; set; } = Constant.GROUP_TEXT_WIDTH;

        public BillingModelPeriodSide PeriodSide
        {
            get
            {
                if (EditorData.Item != null)
                {
                    return EditorData.Item.PeriodSide;
                }
                return null;
            }
            set
            {
                if (EditorData.Item == null)
                {
                    EditorData.Item = new();
                }
                EditorData.Item.PeriodSide = value;
                AppState.Update = true;
            }
        }

        public BillingModelPeriodGranularity PeriodGranularity
        {
            get
            {
                if (EditorData.Item != null)
                {
                    return EditorData.Item.PeriodGranularity;
                }
                return null;
            }
            set
            {
                if (EditorData.Item == null)
                {
                    EditorData.Item = new();
                }
                EditorData.Item.PeriodGranularity = value;
            }
        }

        bool VperiodGranularity { get; set; } = true;
        bool VIntervalDisplay { get; set; } = false;

        private string InfosKey = "InfosKey";

        //public bool IsExpand { get; set; } = false;

        [Parameter]
        public PeriodFilter _PeriodFilters { get; set; }

        [Parameter]
        public string FilterName { get; set; } = "PeriodFilterComponent";

        [Parameter]
        public EventCallback<PeriodFilter> _PeriodFiltersChanged { get; set; }

        [Parameter]
        public bool ShouldRender_ { get; set; } = true;

        PeriodFilterItem from { get; set; } = new();

        PeriodFilterItem to { get; set; } = new();

        protected override Task initComponent()
        {
            EditorDataBinding = getEditorData();
            initModelParams();
            return Task.CompletedTask;
        }
        protected  override Task OnAfterRenderAsync(bool firstRender)
        {
            base.OnAfterRenderAsync(firstRender);
            if (firstRender)
            {
                
            }
            return base.OnAfterRenderAsync(firstRender);
        }


        public override string GetBrowserUrl { get => Route.BROWSER_BOOKING_MODEL; set => base.GetBrowserUrl = value; }
        protected override bool ShouldRender()
        {
            return ShouldRender_; // base.ShouldRender();
        }

        public override async Task SetParametersAsync(ParameterView parameters)
        {
            await base.SetParametersAsync(parameters);
            if (InfosKey.Equals("InfosKey"))
            {
                InfosKey = FilterName + InfosKey;
            }
        }

        private ObservableCollection<PeriodFilterItem> Items
        {
            get
            {
                if (_PeriodFilters != null)
                {
                    return _PeriodFilters.ItemListChangeHandler.GetItems();
                }
                else
                {
                    return new();
                }
            }
        }

        private void TargetFilter(PeriodFilterItem item)
        {
            //if (from.CompareTo(item) == 0)
            //{
            //    from = item;
            //    EditorData.Item.periodFrom = from.Sign;

            //    EditorData.Item.FromOperation = from.Operator.ToString();
            //    EditorData.Item.FromDynamicPeriod = from.Formula;
            //    EditorData.Item.FromOperationNumber = from.Number;
            //    EditorData.Item.FromOperationGranularity = from.Granularity.ToString();
            //}
            //else if (to.CompareTo(item) == 0)
            //{
            //    to = item;
            //}
            ShouldRender_ = true;
            if (!Items.Contains(item))
            {
                _PeriodFilters.AddItem(item);
            }
            else
            {
                _PeriodFilters.UpdateItem(item);
            }
            _PeriodFiltersChanged.InvokeAsync(_PeriodFilters);
        }

        //public void ShowOtherFieldFilters()
        //{
        //    ShouldRender_ = true;
        //    if (_PeriodFilters.ItemListChangeHandler.GetItems().Count > 0)
        //    {
        //        IsExpand = !IsExpand;
        //    }
        //    else
        //    {
        //        IsExpand = false;
        //    }
        //    InvokeAsync(StateHasChanged);
        //}

        private void changedPeriodSideOption(BillingModelPeriodSide PeriodSide)
        {

            if (PeriodSide == BillingModelPeriodSide.INTERVAL)
            {
                EditorData.Item.PeriodGranularity = null;    // BillingModelPeriodGranularity.WEEK;
                VperiodGranularity = false;
                VIntervalDisplay = true;
            }
            else
            {
                VperiodGranularity = true;
                VIntervalDisplay = false;
            }            
        }

        //RenderFormContent RenderFormContentRef { get; set; }
        public bool selectPeriodAtRuntime
        {
            get
            {
                if (EditorData.Item != null)
                {
                    return EditorData.Item.selectPeriodAtRuntime;
                }
                return false;
            }
            set
            {
                if (EditorData.Item == null)
                {
                    EditorData.Item = new();
                }
                EditorData.Item.selectPeriodAtRuntime = value;
            }
        }

        public bool includeZeroAmountEntries
        {
            get
            {
                if (EditorData.Item != null)
                {
                    return EditorData.Item.includeZeroAmountEntries;
                }
                return false;
            }
            set
            {
                if (EditorData.Item == null)
                {
                    EditorData.Item = new();
                }
                EditorData.Item.includeZeroAmountEntries = value;
                
            }
        }

        protected override BookingModelService GetService()
        {
            return BookingModelService;
        }


        protected override EditorDataFilter getEditorDataFilter()
        {
            return new EditorDataFilter();
        }

        string currentOption { get; set; } = "On request";
        bool VcronExpression { get; set; } = false;
        bool Vactive { get; set; } = false;

        IEnumerable<string> schedulerOption = new List<string>() {
            "On request",
            "Scheduler",
        };

        private void changedSchedulerOption(string valeur)
        {
            currentOption = valeur;

            if (currentOption == "On request")
            {
                VcronExpression = false;
                Vactive = false;
            }
            else
            {
                VcronExpression = true;
                Vactive = true;
             }
        }

        protected override void AfterInit(EditorData<BookingModel> EditorData)
        {
            if (EditorData.Item.filter == null)
            {
                EditorData.Item.filter = new Models.Grids.UniverseFilter();
            }
        }

        public ObservableCollection<HierarchicalData> PeriodsItems_
        {
            get
            {
                ObservableCollection<HierarchicalData> obs = new ObservableCollection<HierarchicalData>();
                EditorData.Periods.ToList().ForEach(x => obs.Add(x));
                return obs;
            }
            set
            {
            }

        }
        private string Name
        {
            get
            {
                if (EditorData != null)
                {
                    return EditorData.Item.Name;
                }
                return null;
            }
            set
            {
                if (EditorData != null)
                {
                    EditorData.Item.Name = value;
                }
                AppState.Update = true;
            }
        }
        public virtual ValueTask DisposeAsync()
        {
            AppState.Update = false;
            return ValueTask.CompletedTask;
        }

        protected EditorData<BookingModel> getEditorData()
        {
            var editor = new EditorData<BookingModel>();
            editor.Item = new BookingModel();
            editor.Item.Name = "BookingModel";
            editor.Models = new()
            {
                new()
                {
                    Entities = new()
                    {
                        new()
                        {
                            Name = "Default Entity",
                            Attributes = new()
                            {
                                new()
                                {
                                    Name = "Attribute 1",
                                    Id = 1,
                                    Children = new()
                                }
                    ,
                                new()
                                {
                                    Name = "Attribute 2",
                                    Id = 2,
                                    Children = new()
                                }
                    ,
                                new()
                                {
                                    Name = "Attribute 3",
                                    Id = 3,
                                    Children = new()
                                }
                    ,
                                new()
                                {
                                    Name = "Attribute 4",
                                    Id = 4,
                                    Children = new()
                                }
                    ,
                                new()
                                {
                                    Name = "Attribute 5",
                                    Id = 5,
                                    Children = new()
                                }
                    ,
                                new()
                                {
                                    Name = "Attribute 6",
                                    Id = 6,
                                    Children = new()
                                }
                    ,
                                new()
                                {
                                    Name = "Attribute 7",
                                    Id = 7,
                                    Children = new()
                                }
                    ,
                                new()
                                {
                                    Name = "Attribute 8",
                                    Id = 8,
                                    Children = new()
                                }
                            }
                        }
                    }
                }
            };
            return editor;
        }
    }
}
