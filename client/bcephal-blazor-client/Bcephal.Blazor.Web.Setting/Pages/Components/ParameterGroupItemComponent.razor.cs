using Bcephal.Blazor.Web.Base.Services;
using Bcephal.Models.Base;
using Bcephal.Models.Settings;
using Microsoft.AspNetCore.Components;
using System.Collections.ObjectModel;
using System.Threading.Tasks;
using System.Linq;
using Bcephal.Blazor.Web.Base.Shared;

namespace Bcephal.Blazor.Web.Setting.Pages.Components
{
    public partial class ParameterGroupItemComponent : ComponentBase
    {
        [Inject]
        AppState AppState { get; set; }

        [Parameter]
        public EditorData<Parameter> EditorData_ { get; set; }

        [Parameter]
        public EventCallback<EditorData<Parameter>> EditorData_Changed { get; set; }

        public ParameterEditorData EditorData { get; set; }

        [Parameter]
        public ObservableCollection<HierarchicalData> Entities { get; set; }

        [Parameter] public Parameter Parameter { get; set; }

        [Parameter] public Parameter OldParameter { get; set; }

        [Parameter] public EventCallback<Parameter> ParameterChanged { get; set; }

        public bool CanReset => CanResetItem();

        public Nameable IncrementalNumber { get; set; }

        public Nameable Grid { get; set; }

        public Nameable BillTemplate { get; set; }

        public Nameable OldIncrementalNumber { get; set; }

        public Nameable OldGrid { get; set; }

        public Nameable OldBillTemplate { get; set; }
        protected bool ShouldRender_ { get; set; } = false;
        protected override bool ShouldRender() => RenderCondition() || ShouldRender_;

        private bool CanResetItem()
        {
            if (Parameter != null &&
                   (Parameter.LongValue.HasValue ||
                   !string.IsNullOrWhiteSpace(Parameter.StringValue) ||
                   Parameter.DateValue.HasValue ||
                   Parameter.DecimalValue.HasValue ||
                   Parameter.IntegerValue.HasValue ||
                   Parameter.BooleanValue == true))
            {
                return true;
            }
            else
            {
                return false;
            }
        }

        private void Reset()
        {
            ShouldRender_ = true;
            if (Parameter != null)
            {
                if (Parameter.LongValue.HasValue)
                {
                    Parameter.LongValue = null;
                    Parameter.Name = "";

                }
                if (Parameter.IntegerValue.HasValue)
                {
                    Parameter.IntegerValue = null;
                }
                if (Parameter.DecimalValue.HasValue)
                {
                    Parameter.DecimalValue = null;
                }
                if (!string.IsNullOrWhiteSpace(Parameter.StringValue))
                {
                    Parameter.StringValue = "";
                }
                ResetValues();
                EditorData.Item.Parameters.AddUpdated(Parameter);
            }
        }

        private void ResetValues()
        {
            if (Parameter.IsIncrementalNumber)
            {
                IncrementalNumber = null;
            }
            if (Parameter.IsGrid)
            {
                Grid = null;
            }
            if (Parameter.IsBillTemplate)
            {
                BillTemplate = null;
            }
        }

        protected override Task OnAfterRenderAsync(bool firstRender)
        {
            if (!firstRender)
            {
                OldParameter = Parameter;
                ShouldRender_ = false;
            }
            return base.OnAfterRenderAsync(firstRender);
        }

        protected  bool RenderCondition()
        {

            if (OldParameter.Name != Parameter.Name)
            {
                return true;
            }
            else
            {
                return false;
            }
           
        }
        protected override Task OnInitializedAsync()
        {
            EditorData = GetEditorData();
            InitializedDimensionTypeValue();
            OldParameter = Parameter;
            return base.OnInitializedAsync();
        }

        private string GetAttributeName()
        {
            Models.Dimensions.Attribute attr = FindAttributeById(Entities, Parameter.LongValue.Value);
            if (attr != null)
            {
                Parameter.Name = attr.Name;
            }
            return Parameter.Name;
        }
        private string GetEntityName()
        {
            Models.Dimensions.Entity entity = FindEntityById(Parameter.LongValue.Value);
            if (entity != null)
            {
                Parameter.Name = entity.Name;
            }
            return Parameter.Name;
        }
        private string GetMeasureName()
        {
            Models.Dimensions.Measure measure = FindMeasureById(Parameter.LongValue.Value);
            if (measure != null)
            {
                Parameter.Name = measure.Name;
            }
            return Parameter.Name;
        }
        private string GetModelName()
        {
            Models.Dimensions.Model model = FindModelById(Parameter.LongValue.Value);
            if (model != null)
            {
                Parameter.Name = model.Name;
            }
            return Parameter.Name;
        }
        private string GetPeriodName()
        {
            Models.Dimensions.Period period = FindPeriodById(Parameter.LongValue.Value);
            if (period != null)
            {
                Parameter.Name = period.Name;
            }
            return Parameter.Name;
        }

        private Nameable GetInCrementalNumber()
        {
            IncrementalNumber = FindIncrementalById(Parameter.LongValue.Value);
            return IncrementalNumber;
        }


        protected void InitializedDimensionTypeValue()
        {
            if (Parameter != null && Parameter.LongValue.HasValue)
            {
                if (Parameter.IsAttribute)
                {
                    GetAttributeName();
                }
                if (Parameter.IsModel)
                {
                    GetModelName();
                }
                if (Parameter.IsMeasure)
                {
                    GetMeasureName();
                }
                if (Parameter.IsPeriod)
                {
                    GetPeriodName();
                }
                if (Parameter.IsEntity)
                {
                    GetEntityName();
                }
                if (Parameter.IsIncrementalNumber)
                {
                    GetInCrementalNumber();
                }
            }
        }

        private async Task DimensionChanged(HierarchicalData value)
        {
            if (value != null)
            {
                if (value is Models.Dimensions.Attribute)
                {
                    Parameter.Name = value.Name;
                    Parameter.LongValue = value.Id.Value;
                    Parameter.ParameterTypes = ParameterTypes.ATTRIBUTE;
                   
                }
                else if (value is Models.Dimensions.Period)
                {
                    Parameter.Name = value.Name;
                    Parameter.LongValue = value.Id.Value;
                    Parameter.ParameterTypes = ParameterTypes.PERIOD;
                }
                else if (value is Models.Dimensions.Measure)
                {
                    Parameter.Name = value.Name;
                    Parameter.LongValue = value.Id.Value;
                    Parameter.ParameterTypes = ParameterTypes.MEASURE;
                }
                if (Parameter.Id.HasValue)
                {
                    EditorData_.Item.Parameters.AddUpdated(Parameter);
                }
                else
                {
                    EditorData_.Item.Parameters.AddNew(Parameter);
                }
                await EditorData_Changed.InvokeAsync(EditorData_);
            }
        }


        private async Task EntityChanged(HierarchicalData value)
        {
            if (value != null && value is Models.Dimensions.Entity)
            {
                Parameter.Name = value.Name;
                Parameter.LongValue = value.Id.Value;
                Parameter.ParameterTypes = ParameterTypes.ENTITY;
                if (Parameter.Id.HasValue)
                {
                    EditorData_.Item.Parameters.AddUpdated(Parameter);
                }
                else
                {
                    EditorData_.Item.Parameters.AddNew(Parameter);
                }
                await EditorData_Changed.InvokeAsync(EditorData);
            }
        }

        private async Task ModelChanged(Models.Dimensions.Model value)
        {
            if (value != null && value is Models.Dimensions.Model)
            {
                Parameter.Name = value.Name;
                Parameter.LongValue = value.Id.Value;
                Parameter.ParameterTypes = ParameterTypes.MODEL;
                if (Parameter.Id.HasValue)
                {
                    EditorData_.Item.Parameters.AddUpdated(Parameter);
                }
                else
                {
                    EditorData_.Item.Parameters.AddNew(Parameter);
                }
                await EditorData_Changed.InvokeAsync(EditorData);
            }
        }

        private async Task IncrementalNumberChanged(Nameable value)
        {
            if (value != null)
            {
                IncrementalNumber = value;
                Parameter.Name = value.Name;
                Parameter.LongValue = value.Id.Value;
                Parameter.ParameterTypes = ParameterTypes.INCREMENTAL_NUMBER;
                if (Parameter.Id.HasValue)
                {
                    EditorData_.Item.Parameters.AddUpdated(Parameter);
                }
                else
                {
                    EditorData_.Item.Parameters.AddNew(Parameter);
                }
                await EditorData_Changed.InvokeAsync(EditorData_);
            }
        }

        private async Task GridChanged(Nameable value)
        {
            if (value != null)
            {
                Grid = value;
                Parameter.Name = value.Name;
                Parameter.LongValue = value.Id.Value;
                Parameter.ParameterTypes = ParameterTypes.GRID;
                if (Parameter.Id.HasValue)
                {
                    EditorData_.Item.Parameters.AddUpdated(Parameter);
                }
                else
                {
                    EditorData_.Item.Parameters.AddNew(Parameter);
                }
                await EditorData_Changed.InvokeAsync(EditorData);
            }
        }



        private async Task AttributeValueChanged(string value)
        {
            Parameter.StringValue = value;
            Parameter.ParameterTypes = ParameterTypes.ATTRIBUTE_VALUE;
            if (Parameter.Id.HasValue)
            {
                EditorData_.Item.Parameters.AddUpdated(Parameter);
            }
            else
            {
                EditorData_.Item.Parameters.AddNew(Parameter);
            }
            await EditorData_Changed.InvokeAsync(EditorData_);
        }

        private async Task BillTemplateChanged(Nameable value)
        {
            if (value != null)
            {
                BillTemplate = value;
                Parameter.Name = value.Name;
                Parameter.LongValue = value.Id.Value;
                Parameter.ParameterTypes = ParameterTypes.BILL_TEMPLATE;
                if (Parameter.Id.HasValue)
                {
                    EditorData_.Item.Parameters.AddUpdated(Parameter);
                }
                else
                {
                    EditorData_.Item.Parameters.AddNew(Parameter);
                }
                await EditorData_Changed.InvokeAsync(EditorData_);
            }
        }

        private string GetItemType()
        {
            return Parameter != null ? Parameter.ParameterType: "";
        }

        private Models.Dimensions.Attribute FindAttributeById(ObservableCollection<HierarchicalData> Obs, long Id)
        {
            Models.Dimensions.Attribute AttrFind = null;
            foreach (var item in Obs)
            {
                if (item is Models.Dimensions.Entity)
                {
                    Models.Dimensions.Entity entity = item as Models.Dimensions.Entity;
                    if (entity.Descendents != null && entity.Descendents.Any())
                    {
                        foreach (Models.Dimensions.Attribute attr  in entity.Descendents)
                        {
                            if(attr.Id == Id)
                            {
                                return attr;
                            }
                            else
                            {
                                 FindAttributeById(attr.Descendents.ToObservableCollection<HierarchicalData>(), Id);
                            }
                        }
                       
                    }
                }
                else if (item is Models.Dimensions.Attribute)
                {
                    Models.Dimensions.Attribute attr = item as Models.Dimensions.Attribute;
                    if(attr.Id == Id )
                    {
                        return attr;
                    }
                    else
                    {
                        if (attr.Descendents != null && attr.Descendents.Any())
                        {
                            foreach (Models.Dimensions.Attribute attrib in attr.Descendents)
                            {
                                if (attrib.Id == Id)
                                {
                                    return attrib;
                                }
                                else
                                {
                                    FindAttributeById(attrib.Descendents.ToObservableCollection<HierarchicalData>(), Id);
                                }
                            }
                           
                        }
                    }
                   
                }
            }
            return AttrFind;
        }
        
        private Models.Dimensions.Period FindPeriodById(long Id)
        {
            return EditorData.Periods.Where(x => x.Id == Id).FirstOrDefault();
        }
        private Models.Dimensions.Measure FindMeasureById(long Id)
        {
            return EditorData.Measures.Where(x => x.Id == Id).FirstOrDefault();
        }
        private Models.Dimensions.Model FindModelById(long Id)
        {
            return EditorData.Models.Where(x => x.Id == Id).FirstOrDefault();
        }
        private  Models.Dimensions.Entity FindEntityById(long Id)
        {
            Models.Dimensions.Entity entity = Entities.Where(x => x.Id == Id).FirstOrDefault() as Models.Dimensions.Entity;
            return entity;
        }

        private Nameable FindIncrementalById(long Id)
        {
            return EditorData.Sequences.Where(x => x.Id == Id).FirstOrDefault() as Nameable;
        }


        private ParameterEditorData GetEditorData()
        {
            ParameterEditorData parameterEditorData = (ParameterEditorData)EditorData_;
            return parameterEditorData;
        }

    }
}
