using Bcephal.Blazor.Web.Base.Services;
using Bcephal.Blazor.Web.Base.Shared.Component;
using Bcephal.Models.Base;
using Bcephal.Models.Billing.Model;
using Bcephal.Models.Dimensions;
using Bcephal.Models.Filters;
using Bcephal.Models.Grids;
using Bcephal.Models.Grids.Filters;
using Bcephal.Models.Utils;
using Microsoft.AspNetCore.Components;
using System;
using System.Collections.Generic;
using System.Collections.ObjectModel;
using System.Linq;
using System.Reflection;
using System.Threading.Tasks;

namespace Bcephal.Blazor.Web.Base.Shared
{
    public abstract partial class Form<P, B> : ComponentBase, IAsyncDisposable where P : Persistent
    {
        #region abctract

        public abstract RenderFragment LeftContent { get; }
        public abstract RenderFragment RightContent { get; }
        protected abstract Service<P, B> GetService();
        protected abstract EditorDataFilter getEditorDataFilter();

        #endregion

        #region Render
        private RenderFormContent LeftForm { get; set; }
        private RenderFormContent RightForm { get; set; }
        private RenderFormContent MixFormSplitter { get; set; }
        protected RenderFragment CurrentLeftContent { get; set; }
        protected RenderFragment CurrentRightContent { get; set; }
        #endregion

        #region Parameters

        [CascadingParameter] public Error Error { get; set; }
        [Parameter] public long? Id { get; set; }
        [Parameter] public virtual string CssClass { get; set; } = "content-bc";
        [Parameter] public virtual bool CanUsingGroup { get; set; } = true;
        [Parameter] public virtual string GetBrowserUrl { get; set; }

        #endregion



        #region virtual properties

        protected virtual bool usingMixPane { get; set; } = true;
        public virtual bool usingUnitPane { get { return true; } }
        public virtual int LeftSize { get; set; } = 4;
        public virtual int RightSize { get; set; } = 1;
        public virtual string RightTitle { get { return AppState["Properties"]; } }
        public virtual string LeftTitle { get; }
        public virtual string RightTitleIcon { get { return "bi-gear"; } }
        public virtual string LeftTitleIcon { get; }        

        public virtual string LeftTitlePage
        {
            get
            {
                if (EditorDataBinding == null || EditorDataBinding.Item == null)
                {
                    return null;
                }
                if (EditorDataBinding.Item is Nameable)
                {
                    Nameable item = EditorDataBinding.Item as Nameable;
                    if (item != null)
                    {
                        return item.Name;
                    }
                }
                return null;
            }
        }

        #endregion

        #region inject
        
        [Inject] public AppState AppState { get; set; }
        [Inject] public IToastService ToastService { get; set; }

        #endregion

        #region other properties
        public EditorData<P> EditorData { get; set; }
        public ObservableCollection<HierarchicalData> Entities { get; set; }
        protected List<RenderFragment> Panes { get; private set; }

        #endregion

        #region Operations
        protected virtual EditorData<P> EditorDataBinding
        {
            get { return EditorData; }
            set
            {
                EditorData = value;
                AppState.Update = true;
            }
        }
        protected ObservableCollection<HierarchicalData> AddDimension(HierarchicalData dimension, Entity entity_) 
        {
            ObservableCollection<HierarchicalData> items = new ObservableCollection<HierarchicalData>();
            if (dimension is Models.Dimensions.Measure)
            {
                if(EditorData.Measures == null)
                {
                    EditorData.Measures = new();
                }
                EditorData.Measures.Add(dimension as Models.Dimensions.Measure);
                EditorData.Measures.ToList().ForEach(x => items.Add(x));
                return items;
            }
            else if (dimension is Models.Dimensions.Period)
            {
                if (EditorData.Periods == null)
                {
                    EditorData.Periods = new();
                }
                EditorData.Periods.Add(dimension as Models.Dimensions.Period);
                EditorData.Periods.ToList().ForEach(x => items.Add(x));
                return items;
            }
            else
            {
                foreach (Models.Dimensions.Model item in EditorData.Models)
                {
                    foreach (Models.Dimensions.Entity entity in item.Entities)
                    {
                        if (entity_ != null && entity.Id.Equals(entity_.Id))
                        {
                            Models.Dimensions.Attribute attribue = null;
                            if (!string.IsNullOrWhiteSpace(dimension.ParentId))
                            {
                                long.TryParse(dimension.ParentId, out long parentId);
                                attribue = FindAttribute(entity, parentId);
                            }
                            Models.Dimensions.Attribute dimension_ = dimension as Models.Dimensions.Attribute;
                            if (dimension_.Children == null)
                            {
                                dimension_.Children = new();
                            }

                             if(attribue != null)
                               {
                                   if(attribue.Children == null)
                                    {
                                        attribue.Children = new();
                                   }
                                   attribue.Children.Add(dimension as Models.Dimensions.Attribute);
                               }
                              else 
                               {
                                    if (entity.Attributes == null)
                                    {
                                        entity.Attributes = new();
                                    }
                                    entity.Attributes.Add(dimension as Models.Dimensions.Attribute);
                               }
                               return Entities;
                           }
                       }
                   }
                return new();
            }
        }


        public Models.Dimensions.Attribute FindAttribute(Models.Dimensions.Entity entity, long dimensionId)
        {
            ObservableCollection<Models.Dimensions.Attribute> attributes = entity.Descendents;
            Models.Dimensions.Attribute attribue = attributes.Where(x => x.Id == dimensionId).FirstOrDefault();
            if (attribue != null)
            {
                return attribue;
            }
            return null;
        }


        protected override async  Task OnParametersSetAsync()
        {
            await base.OnParametersSetAsync();
            if (Panes == null)
            {
                Panes = new() { _LeftContent_, _RightContent_ };
            }
            await init();
            CurrentLeftContent = _LeftContent_;
        }

        protected async virtual void NavigateToBrowser()
        {
            if (!string.IsNullOrWhiteSpace(GetBrowserUrl))
            {
                await AppState.NavigateTo(GetBrowserUrl);
            }
        }

        protected override Task OnAfterRenderAsync(bool firstRender)
        {
            if (firstRender)
            {
                AppState.Hander += save;
                AppState.UpdateDimensionHander += AddDimension;
                AppState.DuplicateNameHandler += DuplicateName;
                if (!string.IsNullOrWhiteSpace(GetBrowserUrl))
                {
                    AppState.NavigateToBrowserHander_ += NavigateToBrowser;
                }
            }
            return base.OnAfterRenderAsync(firstRender);
        }

        protected virtual string DuplicateName()
        {
            return null;
        }

        protected readonly string DISPLAY_NONE = "display:none;";
        protected readonly string WIDTH_100 = "width:100%;";
        protected string displayRight { private get; set; } = "";
        protected string displayLeft { private get; set; } = "";
        protected async void RefreshRightContent(RenderFragment RightContent_ = null)
        {
            if (Panes != null && RightForm != null && usingUnitPane)
            {
                if (RightContent_ != null)
                {
                    CurrentRightContent = RightContent_;
                }
                await RightForm.Refresh(CurrentRightContent);
            }
            else
            {
                CurrentRightContent = RightContent_;
                usingMixPane = RightContent_ != null;
                if (!usingMixPane)
                {
                    displayRight = DISPLAY_NONE;
                    displayLeft = WIDTH_100;
                }
                else
                {
                    displayRight = "";
                    displayLeft = "";
                }
                if(MixFormSplitter != null)
                {
                    await MixFormSplitter.StateHasChanged_();
                }
            }
        }



        private async Task init()
        {
            try
            {
                await initComponent();
            }
            catch (Exception ex)
            {
                Error.ProcessError(ex);
            }
        }
        protected virtual async Task initComponent()
        {
            try
            {
                AppState.ShowLoadingStatus();
                if (EditorData == null)
                {
                    EditorDataFilter filter = getEditorDataFilter();
                    filter.NewData = true;
                    if (Id.HasValue)
                    {
                        filter.NewData = false;
                        filter.Id = Id;
                    }
                    EditorDataBinding = await GetService().GetEditorData(filter);
                    initModelParams();
                    AfterInit(EditorDataBinding);
                    AppState.Update = false;
                    AppState.HideLoadingStatus();
                }
            }
            catch (Exception ex)
            {
                AppState.HideLoadingStatus();
                Error.ProcessError(ex);
            }
        }
        protected virtual void AfterInit(EditorData<P> EditorData)
        {

        }
        protected virtual void AfterSave(EditorData<P> EditorData)
        {

        }

        protected virtual Task BeforeSave(EditorData<P> EditorData)
        {
            return Task.CompletedTask;
        }

        protected virtual async void save()
        {
            if (EditorData != null && EditorData.Item != null)
            {
                try
                {
                    AppState.ShowLoadingStatus();
                    AppState.Update = false;
                    await BeforeSave(EditorData);
                    EditorData.Item = await GetService().Save(EditorData.Item);
                    AfterSave(EditorData);
                    ToastService.ShowSuccess(AppState["save.SuccessfullyAdd", LeftTitlePage]);
                }
                catch (Exception ex)
                {
                    AppState.Update = true;
                    Error.ProcessError(ex);
                }
                finally
                {
                    AppState.HideLoadingStatus();
                    StateHasChanged();
                }
            }
        }

        private void InitEntities()
        {
            int offset = 0;
            List<HierarchicalData> items = new List<HierarchicalData>();
            while (EditorData != null && EditorData.Models != null && offset < EditorData.Models.Count)
            {
                Models.Dimensions.Model model = EditorData.Models[offset];
                int offset2 = 0;
                while (model != null && model.Entities != null && offset2 < model.Entities.Count)
                {
                    items.Add(model.Entities[offset2]);
                    offset2++;
                }
                offset++;
            }
            items.BubbleSort();
            Entities = new ObservableCollection<HierarchicalData>(items);
        }
        protected virtual void initEntities_()
        {
            InitEntities();
        }
        protected  void initModelParams()
        {
            if (EditorData == null || EditorData.Item == null)
            {
                return;
            }
            if (EditorData.Item is Models.Grids.Grille)
            {
                Models.Grids.Grille grille = EditorData.Item as Models.Grids.Grille;
                if (grille.ColumnListChangeHandler == null)
                {
                    grille.ColumnListChangeHandler = new ListChangeHandler<GrilleColumn>();
                }
                initFilters(grille);
            }
            if (EditorData.Item is Models.Reconciliation.AutoReco)
            {
                Models.Reconciliation.AutoReco autoReco_ = EditorData.Item as Models.Reconciliation.AutoReco;
                initFilters(autoReco_);
            }
            if (EditorData.Item is BillingModel)
            {
                BillingModel billingModel = EditorData.Item as BillingModel;
                initFilters(billingModel);
            }
            if (EditorData.Measures == null)
            {
                EditorData.Measures = new ObservableCollection<Models.Dimensions.Measure>();
            }
            if (EditorData.Periods == null)
            {
                EditorData.Periods = new ObservableCollection<Models.Dimensions.Period>();
            }
            if (EditorData.Models == null)
            {
                EditorData.Models = new ObservableCollection<Models.Dimensions.Model>();
            }
            InitEntities();
        }
        protected ObservableCollection<BrowserData> BGroups { get; set; } = new ObservableCollection<BrowserData>();

        protected virtual void initFilters(Models.Grids.Grille grille)
        {
            if (grille == null)
            {
                return;
            }
            if (grille.UserFilter == null)
            {
                grille.UserFilter = new UniverseFilter()
                {
                    AttributeFilter = new AttributeFilter(),
                    PeriodFilter = new PeriodFilter(),
                    MeasureFilter = new MeasureFilter()
                };
            }
            if (grille.AdminFilter == null)
            {
                grille.AdminFilter = new UniverseFilter()
                {
                    AttributeFilter = new AttributeFilter(),
                    PeriodFilter = new PeriodFilter(),
                    MeasureFilter = new MeasureFilter()
                };
            }
        }

        protected virtual void initFilters(Models.Reconciliation.AutoReco autoReco)
        {
            if (autoReco == null)
            {
                return;
            }
            if (autoReco.LeftFilter == null)
            {
                autoReco.LeftFilter = new UniverseFilter()
                {
                    AttributeFilter = new AttributeFilter(),
                    PeriodFilter = new PeriodFilter(),
                    MeasureFilter = new MeasureFilter()
                };
            }
            if (autoReco.RightFilter == null)
            {
                autoReco.RightFilter = new UniverseFilter()
                {
                    AttributeFilter = new AttributeFilter(),
                    PeriodFilter = new PeriodFilter(),
                    MeasureFilter = new MeasureFilter()
                };
            }
        }

        protected virtual void initFilters(BillingModel billingModel)
        {
            if (billingModel == null)
            {
                return;
            }
            if (billingModel.Filter == null)
            {
                billingModel.Filter = new UniverseFilter()
                {
                    AttributeFilter = new AttributeFilter(),
                    PeriodFilter = new PeriodFilter(),
                    MeasureFilter = new MeasureFilter()
                };
            }
            if (billingModel.Filter == null)
            {
                billingModel.Filter = new UniverseFilter()
                {
                    AttributeFilter = new AttributeFilter(),
                    PeriodFilter = new PeriodFilter(),
                    MeasureFilter = new MeasureFilter()
                };
            }
        }

        public virtual ValueTask DisposeAsync()
        {
            AppState.Hander -= save;
            AppState.UpdateDimensionHander -= AddDimension;
            AppState.DuplicateNameHandler -= DuplicateName;
            if (!string.IsNullOrWhiteSpace(GetBrowserUrl))
            {
                AppState.NavigateToBrowserHander_ -= NavigateToBrowser;
            }
            return ValueTask.CompletedTask;
        }

        #endregion

        #region Render  operation
        public static RenderFragment RenderContent(ComponentBase instance)
        {
            var fragmentField = GetPrivateField(instance.GetType(), "_renderFragment");
            var value = (RenderFragment)fragmentField.GetValue(instance);
            return value;
        }

        public static FieldInfo GetPrivateField(Type t, String name)
        {
            const BindingFlags bf = BindingFlags.Instance |
                                    BindingFlags.NonPublic |
                                    BindingFlags.DeclaredOnly;
            FieldInfo fi;
            while ((fi = t.GetField(name, bf)) == null && (t = t.BaseType) != null) ;
            return fi;
        }
        #endregion

    }
}
