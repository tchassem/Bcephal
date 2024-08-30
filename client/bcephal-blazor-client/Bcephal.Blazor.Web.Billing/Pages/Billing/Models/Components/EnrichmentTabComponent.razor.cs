using Bcephal.Blazor.Web.Base.Services;
using Bcephal.Blazor.Web.Base.Shared;
using Bcephal.Blazor.Web.Reporting.Services;
using Bcephal.Models.Base;
using Bcephal.Models.Billing.Model;
using Bcephal.Models.Dimensions;
using Bcephal.Models.Filters;
using Bcephal.Models.Grids;
using Microsoft.AspNetCore.Components;
using System;
using System.Collections.Generic;
using System.Collections.ObjectModel;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Bcephal.Blazor.Web.Billing.Pages.Billing.Models.Components
{
    public partial class EnrichmentTabComponent : ComponentBase
    {
        [Inject]
        public AppState AppState { get; set; }

        [Inject]
        RepositoryService RepositoryService { get; set; }

        [Parameter]
        public EditorData<BillingModel> EditorData { get; set; }
         [Parameter]
        public bool Editable { get; set; }
        [Parameter]
        public EventCallback<EditorData<BillingModel>> EditorDataChanged { get; set; }

        [Parameter]
        public ObservableCollection<HierarchicalData> AttributeList { get; set; }

        EditorData<Grille> BillingRepositories { get; set; }
        public ObservableCollection<GrilleColumn> BillingColumn { get; set; }

        protected bool IsExpand = true;

        private PeriodFilterItem pfi = new();

        DimensionType dimensionType => DimensionType.MEASURE;
        public ObservableCollection<string> Dimensions { get; set; }
        public ObservableCollection<HierarchicalData> PeriodsItems { get; set; }
        List<Bcephal.Models.Dimensions.Attribute> ModelsAttributes { get; set; } = new List<Bcephal.Models.Dimensions.Attribute>();

        protected override async Task OnInitializedAsync()
        {
            await base.OnInitializedAsync();
            Dimensions = dimensionType.GetAll(text => AppState[text]).Where(d =>
                dimensionType.GetDimensionType(d, text => AppState[text]).Equals(DimensionType.ATTRIBUTE) ||
                dimensionType.GetDimensionType(d, text => AppState[text]).Equals(DimensionType.BILLING_EVENT) ||
                dimensionType.GetDimensionType(d, text => AppState[text]).Equals(DimensionType.MEASURE) ||
                dimensionType.GetDimensionType(d, text => AppState[text]).Equals(DimensionType.PERIOD) || d is null
            ).ToObservableCollection();
            BillingRepositories = await RepositoryService.GetEditorData(new EditorDataFilter() { });
            BillingColumn = BillingRepositories.Item.ColumnListChangeHandler.Items;
            PeriodsItems = EditorData.Periods.ToObservableCollection<HierarchicalData>();
        }

        private PeriodFilterItem GetPeriodFilterItem(BillingModelEnrichmentItem enrichmentItem)
        {
            if (enrichmentItem.DateValue == null)
            {
                return new PeriodFilterItem()
                {
                    Number = 0
                };
            }
            else
            {
                return new PeriodFilterItem()
                {
                    DimensionId = enrichmentItem.SourceId,
                    Value = enrichmentItem.DateValue.DateValue,
                    Sign = enrichmentItem.DateValue.DateSign,
                    Operator = enrichmentItem.DateValue.DateOperator,
                    Number = enrichmentItem.DateValue.DateNumber,
                    Granularity = enrichmentItem.DateValue.DateGranularity,
                    DimensionName = GetDimensionName(enrichmentItem.SourceId, DimensionType.PERIOD)
                };
            }
        }

        private string GetTreeView(BillingModelEnrichmentItem element)
        {
            if (element.SourceId == null)
            {
                return "";
            }
            else
            {
                if (element.SourceType.Equals(DimensionType.ATTRIBUTE))
                {
                    return getAttribute(AttributeList, element);
                }
                else if (element.SourceType.Equals(DimensionType.MEASURE))
                {
                    Measure mes = EditorData.Measures.Where((a) => a.Id.HasValue && element.SourceId.HasValue && a.Id == element.SourceId).FirstOrDefault();
                    return mes != null ? mes.Name : "";
                }
                return "";
            }
        }

        private string getAttribute(ObservableCollection<HierarchicalData> list, BillingModelEnrichmentItem element)
        {
            foreach (var item in list)
            {
                if (item is Entity)
                {
                    Entity attr = item as Entity;
                    if (attr.Descendents != null && attr.Descendents.Count > 0)
                    {
                        string res = getAttribute(attr.Descendents.ToObservableCollection<HierarchicalData>(), element);
                        if (!string.IsNullOrWhiteSpace(res))
                        {
                            return res;
                        }
                    }
                }
            }
            bool condAttr = element.SourceType.Equals(DimensionType.ATTRIBUTE);
            Func<HierarchicalData, bool> checkType = (HierarchicalData e) => e is Bcephal.Models.Dimensions.Attribute && condAttr && e.Id.HasValue && element.SourceId.HasValue && e.Id == element.SourceId;
            HierarchicalData data = list.ToList().Where(checkType).FirstOrDefault();
            if (data != null)
            {
                return data.Name;
            }
            return "";
        }

        private string GetDimensionName(long? dimensionId, DimensionType type)
        {
            if (!dimensionId.HasValue)
            {
                return null;
            }
            if (DimensionType.MEASURE.Equals(type))
            {
                IEnumerable<Measure> measure = EditorData.Measures.Where((item) => item.Id.Value == dimensionId.Value);
                if (measure.Count() > 0)
                {
                    return measure.FirstOrDefault().Name;
                }
            }
            else if (DimensionType.PERIOD.Equals(type))
            {
                IEnumerable<Period> period = EditorData.Periods.Where((item) => item.Id.Value == dimensionId.Value);
                if (period.Count() > 0)
                {
                    return period.FirstOrDefault().Name;
                }
            }
            else
            {
                IEnumerable<Bcephal.Models.Dimensions.Attribute> entity = ModelsAttributes.Where((item) => item.Id.Value == dimensionId.Value && item is Bcephal.Models.Dimensions.Attribute);
                if (entity.Count() > 0)
                {
                    return entity.FirstOrDefault().Name;
                }
            }
            return null;
        }

        #region Handlers

        // Ceci est le handler du combobox de selection de dimensionType
        protected void DimensionSelectionChanged(string dt)
        {
            // Je verifie d'abord que l'élément sélectionné n'existe pas encore dans la liste avant de l'insérer
            if (dt == null) return;
            EditorData.Item.AddEnrichmentItem(new BillingModelEnrichmentItem() { SourceType = dimensionType.GetDimensionType(dt, text => AppState[text]) });
            EditorDataChanged.InvokeAsync(EditorData);
            AppState.Update = true;
        }

        protected void RemoveEnrichmentItem(BillingModelEnrichmentItem element)
        {
            EditorData.Item.DeleteOrForgetEnrichmentItem(element);
            EditorDataChanged.InvokeAsync(EditorData);
            AppState.Update = true;
        }

        protected void PeriodChanged(PeriodFilterItem periode, BillingModelEnrichmentItem element)
        {
            element.SourceId = periode.DimensionId;
            element.DateValue = new PeriodValue()
            {
                DateGranularity = periode.Granularity,
                DateNumber = periode.Number.Value,
                DateValue = periode.Value,
                DateOperator = periode.Operator,
                DateSign = periode.Sign
            };
            EditorData.Item.UpdateEnrichmentItem(element);
            EditorDataChanged.InvokeAsync(EditorData);
            AppState.Update = true;
        }

        private void BillingComboboxAction(GrilleColumn evnt, BillingModelEnrichmentItem element)
        {
            element.SourceId = evnt.Id;
            EditorData.Item.UpdateEnrichmentItem(element);
            EditorDataChanged.InvokeAsync(EditorData);
            AppState.Update = true;
        }

        private void TreeViewCallback(HierarchicalData hierarchicalData, BillingModelEnrichmentItem element)
        {
            element.SourceId = hierarchicalData.Id;
            element.SourceType = hierarchicalData is Measure ? DimensionType.MEASURE : DimensionType.ATTRIBUTE;
            EditorData.Item.UpdateEnrichmentItem(element);
            EditorDataChanged.InvokeAsync(EditorData);
            AppState.Update = true;
        }

        private void TextCallback(string val, BillingModelEnrichmentItem element)
        {
            element.StringValue = val;
            EditorData.Item.UpdateEnrichmentItem(element);
            EditorDataChanged.InvokeAsync(EditorData);
            AppState.Update = true;
        }
       
        #endregion
    }
}
