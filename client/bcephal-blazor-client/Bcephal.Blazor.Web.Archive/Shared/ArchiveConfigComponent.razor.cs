using Bcephal.Blazor.Web.Base.Services;
using Bcephal.Models.Archives;
using Bcephal.Models.Base;
using Bcephal.Models.Dimensions;
using Bcephal.Models.Filters;
using Microsoft.AspNetCore.Components;
using System;
using System.Collections.Generic;
using System.Collections.ObjectModel;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Bcephal.Blazor.Web.Archive.Shared
{
    public partial class ArchiveConfigComponent
    {
        public List<string> DimensionItems = new List<string>();

        [Parameter]
        public EditorData<ArchiveConfig> EditorData { get; set; }

        [Parameter]
        public ObservableCollection<HierarchicalData> Entities { get; set; }

        [Parameter]
        public EventCallback<EditorData<ArchiveConfig>> EditorDataChanged { get; set; }

        [Parameter]
        public ObservableCollection<HierarchicalData> EntityItems { get; set; }

        [Parameter]
        public EventCallback<ObservableCollection<HierarchicalData>> EntityItemsChanged { get; set; }

        [Parameter]
        public bool ExpandField { get; set; }

        [Parameter]
        public EventCallback<bool> ExpandFieldChanged { get; set; }

        [Parameter] public bool Editable { get; set; } = true;
        public string KeyName { get; set; } = "ArchiveConfigComponent";

        public string ComboBoxName { get; set; }


        public bool IsSmallScreen { get; set; }

        [Inject]
        public AppState AppState { get; set; }

        public EventCallback<ArchiveConfigEnrichmentItem> ArchiveConfigEnrichmentChanged { get; set; }

        public EventCallback<ArchiveConfigEnrichmentItem> ArchiveConfigEnrichmentDelete { get; set; }

        [Parameter]
        public bool removeButton { get; set; }

        public void ArchiveConfigEnrichmentHandler(string type)
        {
            foreach (var t in Enum.GetValues(typeof(DimensionType)).OfType<DimensionType>().ToList())
            {
                if (type.Equals(t.ToString()))
                {
                    EditorData.Item.AddEnrichmentItem(new ArchiveConfigEnrichmentItem()
                    {
                        Type = t 
                    });

                    EditorDataChanged.InvokeAsync(EditorData);
                }
            }
        }

        private void ArchiveConfigEnrichmentDeleteHandler(ArchiveConfigEnrichmentItem archiveConfigEnrichmentItem)
        {
            EditorData.Item.DeleteOrForgetEnrichmentItem(archiveConfigEnrichmentItem);
            EditorDataChanged.InvokeAsync(EditorData);
        }

        private void ArchiveConfigEnrichmentUpdateMeasureHandler(ArchiveConfigEnrichmentItem archiveConfigEnrichmentItem, MeasureFilterItem Item)
        {
            archiveConfigEnrichmentItem.DecimalValue = Item.Value;
            archiveConfigEnrichmentItem.SourceId = this.GetDimensionId(Item.DimensionName, Item.DimensionType);
            EditorData.Item.UpdateEnrichmentItem(archiveConfigEnrichmentItem);
            EditorDataChanged.InvokeAsync(EditorData);
        }

        private void ArchiveConfigEnrichmentUpdateAttributeHandler(ArchiveConfigEnrichmentItem archiveConfigEnrichmentItem, AttributeFilterItem Item)
        {
            archiveConfigEnrichmentItem.StringValue = Item.Value;
            archiveConfigEnrichmentItem.SourceId = this.GetDimensionId(Item.DimensionName, Item.DimensionType);
            EditorData.Item.UpdateEnrichmentItem(archiveConfigEnrichmentItem);
            EditorDataChanged.InvokeAsync(EditorData);
        }

        private void ArchiveConfigEnrichmentUpdatePeriodHandler(ArchiveConfigEnrichmentItem archiveConfigEnrichmentItem, PeriodFilterItem Item)
        {
            archiveConfigEnrichmentItem.PeriodValue = new PeriodValue()
            {
                DateGranularity = Item.Granularity,
                DateNumber = (int)Item.Number,
                DateOperator = Item.Operator,
                DateSign = Item.Sign,
                DateValue = Item.Value
            };
            archiveConfigEnrichmentItem.SourceId = this.GetDimensionId(Item.DimensionName, Item.DimensionType);
            EditorData.Item.UpdateEnrichmentItem(archiveConfigEnrichmentItem);
            EditorDataChanged.InvokeAsync(EditorData);
        }

        protected override Task OnInitializedAsync()
        {
            foreach (var t in Enum.GetValues(typeof(DimensionType)).OfType<DimensionType>().ToList())
            {
                if (t == DimensionType.ATTRIBUTE || t == DimensionType.MEASURE || t == DimensionType.PERIOD)
                {
                    DimensionItems.Add(t.ToString());
                }
            }

            InitAttributes();
            return base.OnInitializedAsync();
        }

        [Parameter]
        public ObservableCollection<Bcephal.Models.Dimensions.Measure> MeasureItems { get; set; }

        public ObservableCollection<HierarchicalData> MeasureItems_
        {
            get
            {
                ObservableCollection<HierarchicalData> obse = new ObservableCollection<HierarchicalData>();
                MeasureItems.ToList().ForEach(x => obse.Add((HierarchicalData)x));
                return obse;
            }
            set
            {
            }
        }

        [Parameter]
        public ObservableCollection<Bcephal.Models.Dimensions.Period> PeriodsItems { get; set; }

        public ObservableCollection<HierarchicalData> PeriodsItems_
        {
            get
            {
                ObservableCollection<HierarchicalData> obs = new ObservableCollection<HierarchicalData>();
                PeriodsItems.ToList().ForEach(x => obs.Add((HierarchicalData)x));
                return obs;
            }
            set
            {
            }
        }

        private PeriodFilterItem GetPeriodFilterItem(ArchiveConfigEnrichmentItem enrichmentItem)
        {
            if (enrichmentItem.PeriodValue == null)
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
                    Value = enrichmentItem.PeriodValue.DateValue,
                    Sign = enrichmentItem.PeriodValue.DateSign,
                    Operator = enrichmentItem.PeriodValue.DateOperator,
                    Number = enrichmentItem.PeriodValue.DateNumber,
                    Granularity = enrichmentItem.PeriodValue.DateGranularity,
                    DimensionName = GetDimensionName(enrichmentItem.SourceId, DimensionType.PERIOD)

                };
            }
        }

        private MeasureFilterItem GetMeasureFilterItem(ArchiveConfigEnrichmentItem enrichmentItem)
        {
            if (enrichmentItem.DecimalValue == null)
            {
                return new MeasureFilterItem() { Value = 0 };
            }
            else
            {
                return new MeasureFilterItem()
                {
                    Value = enrichmentItem.DecimalValue,
                    DimensionName = GetDimensionName(enrichmentItem.SourceId, DimensionType.MEASURE)
                };
            }
        }

        private AttributeFilterItem GetAttributeFilterItem(ArchiveConfigEnrichmentItem enrichmentItem)
        {
            if (enrichmentItem.StringValue == null)
            {
                return new AttributeFilterItem()
                {
                    Value = ""
                };
                ;
            }
            else
            {
                return new AttributeFilterItem()
                {
                    Value = enrichmentItem.StringValue,
                    DimensionName = GetDimensionName(enrichmentItem.SourceId, DimensionType.ATTRIBUTE),
                    DimensionId = enrichmentItem.SourceId
                };
            }
        }

        private string GetDimensionName(long? dimensionId, DimensionType type)
        {
            if (!dimensionId.HasValue)
            {
                return null;
            }
            if (DimensionType.MEASURE.Equals(type))
            {
                IEnumerable<Models.Dimensions.Measure> measure = EditorData.Measures.Where((item) => item.Id.Value == dimensionId.Value);
                if (measure.Count() > 0)
                {
                    return measure.FirstOrDefault().Name;
                }
            }
            else
                if (DimensionType.PERIOD.Equals(type))
            {
                IEnumerable<Models.Dimensions.Period> period = EditorData.Periods.Where((item) => item.Id.Value == dimensionId.Value);
                if (period.Count() > 0)
                {
                    return period.FirstOrDefault().Name;
                }
            }
            else
            {

                IEnumerable<Models.Dimensions.Attribute> entity = ModelsAttributes.Where((item) => item.Id.Value == dimensionId.Value && item is Models.Dimensions.Attribute);
                if (entity.Count() > 0)
                {
                    return entity.FirstOrDefault().Name;
                }
            }
            return null;
        }

        private long? GetDimensionId(string dimensionName, DimensionType type)
        {
            if(dimensionName == null)
            {
                return null;
            }
            if (DimensionType.MEASURE.Equals(type))
            {
                IEnumerable<Models.Dimensions.Measure> measure = EditorData.Measures.Where((item) => item.Name == dimensionName);
                if (measure.Count() > 0)
                {
                    return measure.FirstOrDefault().Id;
                }
            }
            else
                if (DimensionType.PERIOD.Equals(type))
            {
                IEnumerable<Models.Dimensions.Period> period = EditorData.Periods.Where((item) => item.Name == dimensionName);
                if (period.Count() > 0)
                {
                    return period.FirstOrDefault().Id;
                }
            }
            else
            {

                IEnumerable<Models.Dimensions.Attribute> entity = ModelsAttributes.Where((item) => item.Name == dimensionName && item is Models.Dimensions.Attribute);
                if (entity.Count() > 0)
                {
                    return entity.FirstOrDefault().Id;
                }
            }
            return null;
        }

        private void InitAttributes()
        {
            foreach (var item in Entities)
            {
                if (item is Models.Dimensions.Entity)
                {
                    Models.Dimensions.Entity item_ = (Models.Dimensions.Entity)item;
                    ModelsAttributes.AddRange(item_.Descendents);
                }
                else
                    if (item is Models.Dimensions.Attribute)
                {
                    Models.Dimensions.Attribute item_ = (Models.Dimensions.Attribute)item;
                    ModelsAttributes.AddRange(item_.Descendents);
                }

            }
        }

        List<Models.Dimensions.Attribute> ModelsAttributes { get; set; } = new List<Models.Dimensions.Attribute>();
    }
}
