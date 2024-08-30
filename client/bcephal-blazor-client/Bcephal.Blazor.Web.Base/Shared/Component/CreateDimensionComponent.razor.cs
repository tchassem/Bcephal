using Bcephal.Blazor.Web.Base.Services;
using Bcephal.Models.Base;
using Bcephal.Models.Dimensions;
using Bcephal.Models.Filters;
using Bcephal.Models.Loaders;
using DevExpress.Blazor;
using Microsoft.AspNetCore.Components;
using System;
using System.Collections.ObjectModel;
using System.Threading.Tasks;
using System.Linq;
using System.Collections.Generic;

namespace Bcephal.Blazor.Web.Base.Shared.Component
{
    public partial class CreateDimensionComponent<C> : ComponentBase, IDisposable where C : HierarchicalData
    {
        DxPopup DxPopupRef;

        [Inject]
        public AppState AppState { get; set; }

        [Inject]
        public ModelService ModelService { get; set; }

        [Inject]
        protected IToastService toastService { get; set; }

        [Parameter]
        public EditorData<C> EditorData { get; set; }

        [Parameter]
        public bool PopupVisible { get; set; }

        [Parameter]
        public ObservableCollection<C> Items { get; set; }

        [Parameter]
        public EventCallback<ObservableCollection<C>> ItemsChanged { get; set; }

        public ObservableCollection<C> Parents { get; set; } = new();

        public ObservableCollection<C> Entities { get; set; } = new();

        [Parameter]
        public ObservableCollection<Models.Dimensions.Measure> Measures { get; set; } 

        [Parameter]
        public ObservableCollection<Models.Dimensions.Period> Periods { get; set; }

      

        [Parameter]
        public Action<bool> CanRebuildTreeItemsFilter { get; set; }

        [Parameter]
        public ObservableCollection<Model> Models { get; set; } 

        [Parameter]
        public string HeaderTitle { get; set; }

        [Parameter]
        public EventCallback<bool> PopupVisibleChanged { get; set; }

        [Parameter]
        public bool ShowDimensionSelection { get; set; } = false;

        public DimensionType Dimensiontype_ { get; set; }
        public DimensionType Dimensiontype
        {
            get
            {
                return Dimensiontype_;
            }
            set
            {
              
                if (value.IsAttribute())
                {
                    if (Entities.Count > 0)
                    {
                        Entities.Clear();
                        SelectedEntity = null;
                    }
                    if (Parents.Count > 0)
                    {
                        Parents.Clear();
                        SelectedParent = null;
                    }
                }
                else
                {
                    if (Parents.Count > 0)
                    {
                        Parents.Clear();
                        SelectedParent = null;
                    }
                }
                Name = "";
                Dimensiontype_ = value;
                DimensionType_ = Dimensiontype_;
                GetItems();
            }
        }

        [Parameter]
        public DimensionType DimensionType_ { get; set; }

        public string Name_ { get; set; }
        public string Name
        {
            get
            {
                return Name_;
            }
            set
            {
                Name_ = value;
            }
        }

        public C SelectedEntity_ { get; set; }
        public C SelectedEntity
        {
            get
            {
                return SelectedEntity_;
            }
            set
            {
                SelectedEntity_ = value;
                if (SelectedEntity_.Id.HasValue)
                {
                    GetParentsByEntityId(SelectedEntity_.Id);
                    StateHasChanged();
                }
            }
        }

        public C SelectedParent { get; set; }

        public void Dispose()
        {
            if(DxPopupRef != null)
            {
                GC.SuppressFinalize(DxPopupRef);
                GC.SuppressFinalize(this);
            }
        }

        public void Close()
        {
            PopupVisible = false;
            PopupVisibleChanged.InvokeAsync(PopupVisible);
        }

        public void GetParentsByEntityId(long? entityId)
        {
            Models.Dimensions.Entity item = Entities.Where(x => x.Id == entityId).FirstOrDefault() as Models.Dimensions.Entity;
            if(item != null && item.Id.HasValue)
            {
                Entity entity = item as Models.Dimensions.Entity;
                if(Parents.Count > 0)
                {
                    Parents.Clear();
                }
                if(entity != null && entity.Attributes != null && entity.Attributes.Any())
                {
                    entity.Attributes.ToList().ForEach(
                      attrib =>
                      {
                          if (!string.IsNullOrWhiteSpace(attrib.ParentId) && attrib.ParentId.Contains("ENTITY"))
                          {
                              Parents.Add(attrib as C);
                              if (attrib.Children != null)
                              {
                                  attrib.Descendents.ForEach(x => Parents.Add(x as C));
                              }
                          }
                      }
                    );
                }
              
            }
        }

        public async void OnClickBtn()
        {
            DimensionApi data = new() ;
            if(ShowDimensionSelection == true)
            {
                DimensionType_ = Dimensiontype; 
            }
            if (string.IsNullOrWhiteSpace(Name))
            {
                toastService.ShowError(AppState["EmptyDimensionName"]);
                return;
            }
            if (DimensionType_.IsAttribute())
            {
                if(SelectedEntity != null )
                {
                    data = new() { 
                        Name = Name,
                        Entity = (SelectedParent == null || !SelectedParent.Id.HasValue) ? SelectedEntity.Id : null, 
                        Parent = (SelectedParent != null && SelectedParent.Id.HasValue) ? SelectedParent.Id : null 
                    };
                }
                else 
                {
                    toastService.ShowError(AppState["NullEntity"]);
                    return;
                }
               
            }
            if (DimensionType_.IsMeasure())
            {
                data = new() {
                    Name = Name, 
                    Parent = (SelectedParent != null && SelectedParent.Id.HasValue )? SelectedParent.Id : null
                };
            }

            if (DimensionType_.IsPeriod())
            {
                data = new() {
                    Name = Name,
                    Parent = (SelectedParent != null && SelectedParent.Id.HasValue) ? SelectedParent.Id : null 
                };
            }
            Dimension result = null;
            try
            {
                result = await ModelService.createDimension(DimensionType_, data);
                await UpdateDimension(result);
            }
            catch (Exception e)
            {
                toastService.ShowError(AppState["UnableToCreateDimension"]);
            }
            finally
            {
                Close();
            }
        }

        public ObservableCollection<DimensionType> DimensionTypes = new ObservableCollection<DimensionType>() {  DimensionType.ATTRIBUTE, DimensionType.MEASURE, DimensionType.PERIOD };

        public void GetItems()
        {
            if (Dimensiontype.IsAttribute())
            {
                Entities = GetEntities();
            }else if(Dimensiontype.IsPeriod())
            {
                if(Periods != null && Periods.Count > 0)
                {
                    foreach (Models.Dimensions.Period period in Periods)
                    {
                        Parents.Add(period as C);
                    }
                }
            }else if (Dimensiontype.IsMeasure())
            {
                if (Measures != null && Measures.Count > 0)
                {
                    foreach (Models.Dimensions.Measure measure in Measures)
                    {
                        Parents.Add(measure as C);
                    }
                }
            }
        }

        public ObservableCollection<C> GetEntities()
        {
            ObservableCollection<C> entities = new();

            if(Models != null && Models.Count > 0)
            {
                foreach (Model model in Models)
                {
                   foreach(Entity entity in model.Entities)
                    {
                        entities.Add(entity as C);
                    }
                }
            }
           
            return entities;
        }


        protected override Task OnParametersSetAsync()
        {
            if (ShowDimensionSelection == false)
            {
                if (DimensionType_.IsAttribute())
                {
                    Entities = Items;
                }
                else
                {
                    if (Parents.Count > 0)
                    {
                        Parents.Clear();
                    }
                    if (Items != null && Items.Any())
                    {
                        foreach (C item in Items)
                        {
                            Parents.Add(item);
                            if (DimensionType_.IsPeriod())
                            {
                                Models.Dimensions.Period item_ = item as Models.Dimensions.Period;
                                if (item_.Children != null)
                                {
                                    foreach(Models.Dimensions.Period period in item_.Children)
                                    {
                                        Parents.Add(period as C);
                                        if (period.Children != null )
                                        {
                                            List<Models.Dimensions.Period> childs = period.Descendents != null ? period.Descendents : period.Children.ToList();
                                            foreach (var ite in childs)
                                            {
                                                Parents.Add(ite as C);
                                            }
                                        }
                                    }
                                }
                            }
                            else if (DimensionType_.IsMeasure())
                            {
                                Models.Dimensions.Measure item_ = item as Models.Dimensions.Measure;
                                if (item_.Children != null)
                                {
                                    foreach (Models.Dimensions.Measure measure in item_.Children)
                                    {
                                        Parents.Add(measure as C);
                                        if(measure.Children != null )
                                        {
                                            List<Models.Dimensions.Measure> childs = measure.Descendents != null ? measure.Descendents : measure.Children.ToList();
                                            foreach (var ite in childs)
                                            {
                                                Parents.Add(ite as C);
                                            }
                                        }
                                    }
                                }
                            }else if (DimensionType_.IsAttribute())
                            {
                                Models.Dimensions.Attribute item_ = item as Models.Dimensions.Attribute;
                                if (item_.Children != null)
                                {
                                    foreach (Models.Dimensions.Attribute attribute in item_.Children)
                                    {
                                        Parents.Add(attribute as C);
                                        if (attribute.Children != null)
                                        {
                                            List<Models.Dimensions.Attribute> childs = attribute.Descendents != null ? attribute.Descendents : attribute.Children.ToList();
                                            foreach (var ite in childs)
                                            {
                                                Parents.Add(ite as C);
                                            }
                                        }
                                    }
                                }
                            }

                        }
                    }
                }
            }
            else
            {
                GetItems();
            }

            return base.OnParametersSetAsync();
        }


        private async Task UpdateDimension(Dimension dimension)
        {
            if (dimension != null)
            {
                if (ShowDimensionSelection == false && SelectedParent != null)
                {
                    dimension.ParentId = SelectedParent.Id.HasValue ? SelectedParent.Id.Value.ToString() : null;
                }
                ObservableCollection<HierarchicalData> items = AppState.UpdateDimension(dimension, SelectedEntity as Entity);
                if (items != null && SelectedParent != null && SelectedParent.Id.HasValue)
                {
                    C item = Items.Where(x => x.Id == SelectedParent.Id.Value).FirstOrDefault();

                    if (item == null)
                    {
                        item = items.Where(x => x.Id == SelectedParent.Id.Value).FirstOrDefault() as C;
                        if (item != null)
                        {
                            Items.Add(item);
                        }
                    }
                    int position = Items.IndexOf(item);

                    if (DimensionType_.IsPeriod())
                    {
                        if (item != null && item.Id.HasValue)
                        {
                            Models.Dimensions.Period period = item as Models.Dimensions.Period;
                            if (period.Children == null)
                            {
                                period.Children = new ObservableCollection<Models.Dimensions.Period>();
                            }
                            period.Children.Add(dimension as Models.Dimensions.Period);
                            if (position < Items.Count)
                            {
                                Items[position] = period as C;
                            }
                        }

                    }
                    if (DimensionType_.IsMeasure())
                    {
                        if (item != null && item.Id.HasValue)
                        {
                            Models.Dimensions.Measure measure = item as Models.Dimensions.Measure;
                            if (measure.Children == null)
                            {
                                measure.Children = new ObservableCollection<Models.Dimensions.Measure>();
                            }
                            measure.Children.Add(dimension as Models.Dimensions.Measure);
                            if (position < Items.Count)
                            {
                                Items[position] = measure as C;
                            }
                        }
                    }
                    if (DimensionType_.IsAttribute())
                    {
                        if (item != null && item.Id.HasValue)
                        {
                            Models.Dimensions.Attribute attribute = item as Models.Dimensions.Attribute;
                            if (attribute.Children == null)
                            {
                                attribute.Children = new ObservableCollection<Models.Dimensions.Attribute>();
                            }
                            attribute.Children.Add(dimension as Models.Dimensions.Attribute);
                            if (position < Items.Count)
                            {
                                Items[position] = attribute as C;
                            }
                        }
                    }

                }
                else if (items != null && SelectedParent == null)
                {
                    if (SelectedEntity == null)
                    {
                        Items.Add(dimension as C);
                    }

                }
                await ItemsChanged.InvokeAsync(Items);
                CanRebuildTreeItemsFilter?.Invoke(true);
                toastService.ShowSuccess(AppState["DimensionCreateSuccessfully"]);
            }
            await Task.CompletedTask;
        }

    }

   
}
