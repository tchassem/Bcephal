using Bcephal.Models.Base;
using Bcephal.Models.Filters;
using Bcephal.Models.Grids;
using Newtonsoft.Json;
using System;
using System.Collections.Generic;
using System.Collections.ObjectModel;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Bcephal.Models.Billing.Model
{
    public class BillingModel : SchedulableObject
	{
		public long? InvoiceSequenceId { get; set; }

		public string Currency { get; set; }

		public UniverseFilter Filter { get; set; }

		public bool SelectPeriodAtRuntime { get; set; }

		public bool UseUnitCostToComputeAmount { get; set; }

		public bool IncludeUnitCost { get; set; }

		public bool UseVat { get; set; }

		public bool OrderItems { get; set; }

		public bool OrderItemsAsc { get; set; }

		public bool SeparateInvoicePerPeriod { get; set; }

		public bool IncludeZeroAmountEvents { get; set; }

		public bool BuildCommunicationMessage { get; set; }

		public bool DueDateCalculation { get; set; }

		public PeriodValue DueDateValue { get; set; }

		public string InvoiceDescription { get; set; }

		public bool InvoiceDateCalculation { get; set; }

		public PeriodValue InvoiceDateValue { get; set; }

		public PeriodValue FromDateValue { get; set; }

		public PeriodValue ToDateValue { get; set; }


		public string PeriodSide { get; set; }

		[JsonIgnore]
		public BillingModelPeriodSide BillingModelPeriodSide
		{
			get { return !string.IsNullOrEmpty(PeriodSide) ? BillingModelPeriodSide.GetByCode(PeriodSide) : null; }
			set { this.PeriodSide = value != null ? value.code : null; }
		}

		[JsonIgnore]
		public ObservableCollection<BillingModelPeriodSide> BillingModelPeriodSideItemsSource
		{
			get { return BillingModelPeriodSide.GetSides(); }
		}

		public bool AddAppendicies { get; set; }

		public string AppendicyType { get; set; }

		[JsonIgnore]
		public BillingModelAppendicyType BillingModelAppendicyType
		{
			get { return !string.IsNullOrEmpty(AppendicyType) ? BillingModelAppendicyType.GetByCode(AppendicyType) : null; }
			set { this.AppendicyType = value != null ? value.code : null; }
		}

		public string BillTemplateCode { get; set; }

		public string BillingCompanyCode { get; set; }

		public string PeriodGranularity { get; set; }

		[JsonIgnore]
		public BillingModelPeriodGranularity BillingModelPeriodGranularity
		{
			get { return !string.IsNullOrEmpty(PeriodGranularity) ? BillingModelPeriodGranularity.GetByCode(PeriodGranularity) : null; }
			set { this.PeriodGranularity = value != null ? value.code : null; }
		}

		[JsonIgnore]
		public ObservableCollection<BillingModelPeriodGranularity> BillingModelPeriodGranularityItemsSource
		{
			get { return BillingModelPeriodGranularity.GetGranularities(); }
		}

		public string InvoiceGranularityLevel { get; set; }

		public BillingModelInvoiceGranularityLevel BillingModelInvoiceGranularityLevel
		{
			get { return !string.IsNullOrEmpty(InvoiceGranularityLevel) ? BillingModelInvoiceGranularityLevel.GetByCode(InvoiceGranularityLevel) : null; }
			set { this.InvoiceGranularityLevel = value != null ? value.code : null; }
		}

		public ListChangeHandler<BillingModelItem> ItemListChangeHandler { get; set; }

		public ListChangeHandler<BillingModelPivot> PivotListChangeHandler { get; set; }

		public ListChangeHandler<BillingModelGroupingItem> GroupingItemListChangeHandler { get; set; }

		public ListChangeHandler<BillingModelParameter> ParameterListChangeHandler { get; set; }

		public ListChangeHandler<InvoiceLabel> InvoiceLabelListChangeHandler { get; set; }

		public ListChangeHandler<BillingModelDriverGroup> DriverGroupListChangeHandler { get; set; }

		public ListChangeHandler<BillingModelEnrichmentItem> EnrichmentItemListChangeHandler { get; set; }

		public ListChangeHandler<BillingDescription> BillingDescriptionsListChangeHandler { get; set; }

		public ListChangeHandler<BillingModelLabel> LabelListChangeHandler { get; set; }

		public BillingModel()
        {
            this.ItemListChangeHandler = new ListChangeHandler<BillingModelItem>();
            this.PivotListChangeHandler = new ListChangeHandler<BillingModelPivot>();
			this.GroupingItemListChangeHandler = new ListChangeHandler<BillingModelGroupingItem>();
			this.ParameterListChangeHandler = new ListChangeHandler<BillingModelParameter>();
			this.InvoiceLabelListChangeHandler = new ListChangeHandler<InvoiceLabel>();
			this.DriverGroupListChangeHandler = new ListChangeHandler<BillingModelDriverGroup>();
			this.EnrichmentItemListChangeHandler = new ListChangeHandler<BillingModelEnrichmentItem>();
			this.BillingDescriptionsListChangeHandler = new ListChangeHandler<BillingDescription>();
			this.LabelListChangeHandler = new ListChangeHandler<BillingModelLabel>();
			this.BillingModelPeriodSide = BillingModelPeriodSide.CURRENT;
			this.BillingModelPeriodGranularity = BillingModelPeriodGranularity.MONTH;
			this.BillingModelInvoiceGranularityLevel = BillingModelInvoiceGranularityLevel.CATEGORY;
			this.AddAppendicies = false;
		}


		#region Items

		public void AddItem(BillingModelItem item, bool sort = true)
		{
			item.Position = ItemListChangeHandler.Items.Count;
			ItemListChangeHandler.AddNew(item, sort);
		}

		public void UpdateItem(BillingModelItem item, bool sort = true)
		{
			ItemListChangeHandler.AddUpdated(item, sort);
		}

		public void DeleteOrForgetItem(BillingModelItem item)
		{
			if (item.IsPersistent)
			{
				DeleteItem(item);
			}
			else
			{
				ForgetItem(item);
			}
		}

		public void DeleteItem(BillingModelItem item)
		{
			ItemListChangeHandler.AddDeleted(item);
			foreach (BillingModelItem child in ItemListChangeHandler.Items)
			{
				if (child.Position > item.Position)
				{
					child.Position = child.Position - 1;
					ItemListChangeHandler.AddUpdated(child, false);
				}
			}
		}

		public void ForgetItem(BillingModelItem item)
		{
			ItemListChangeHandler.forget(item);
			foreach (BillingModelItem child in ItemListChangeHandler.Items)
			{
				if (child.Position > item.Position)
				{
					child.Position = child.Position - 1;
					ItemListChangeHandler.AddUpdated(child, false);
				}
			}
		}

		public void MoveUpItem(BillingModelItem item)
		{
			MoveItem(item, true);
		}

		public void MoveDownItem(BillingModelItem item)
		{
			MoveItem(item, false);
		}

		private void MoveItem(BillingModelItem item, bool up)
		{
			BillingModelItem child = ItemListChangeHandler.Items[item.Position + (up ? -1 : 1)];
			if (up && child.Position < item.Position)
			{
				item.Position = item.Position - 1;
				child.Position = child.Position + 1;
				ItemListChangeHandler.AddUpdated(item, false);
			}
			else if (!up && child.Position > item.Position)
			{
				item.Position = item.Position + 1;
				child.Position = child.Position - 1;
				ItemListChangeHandler.AddUpdated(item, false);
			}
			ItemListChangeHandler.AddUpdated(item, true);
		}

		#endregion


		#region Pivots

		public void AddPivot(BillingModelPivot item, bool sort = true)
		{
			item.Position = PivotListChangeHandler.Items.Count;
			PivotListChangeHandler.AddNew(item, sort);
		}

		public void UpdatePivot(BillingModelPivot item, bool sort = true)
		{
			PivotListChangeHandler.AddUpdated(item, sort);
		}

		public void DeleteOrForgetPivot(BillingModelPivot item)
		{
			if (item.IsPersistent)
			{
				DeletePivot(item);
			}
			else
			{
				ForgetPivot(item);
			}
		}

		public void DeletePivot(BillingModelPivot item)
		{
			PivotListChangeHandler.AddDeleted(item);
			foreach (BillingModelPivot child in PivotListChangeHandler.Items)
			{
				if (child.Position > item.Position)
				{
					child.Position = child.Position - 1;
					PivotListChangeHandler.AddUpdated(child, false);
				}
			}
		}

		public void ForgetPivot(BillingModelPivot item)
		{
			PivotListChangeHandler.forget(item);
			foreach (BillingModelPivot child in PivotListChangeHandler.Items)
			{
				if (child.Position > item.Position)
				{
					child.Position = child.Position - 1;
					PivotListChangeHandler.AddUpdated(child, false);
				}
			}
		}

		public void MoveUpPivot(BillingModelPivot item)
		{
			MovePivot(item, true);
		}

		public void MoveDownPivot(BillingModelPivot item)
		{
			MovePivot(item, false);
		}

		private void MovePivot(BillingModelPivot item, bool up)
		{
			BillingModelPivot child = PivotListChangeHandler.Items[item.Position + (up ? -1 : 1)];
			if (up && child.Position < item.Position)
			{
				item.Position = item.Position - 1;
				child.Position = child.Position + 1;
				PivotListChangeHandler.AddUpdated(item, false);
			}
			else if (!up && child.Position > item.Position)
			{
				item.Position = item.Position + 1;
				child.Position = child.Position - 1;
				PivotListChangeHandler.AddUpdated(item, false);
			}
			PivotListChangeHandler.AddUpdated(item, true);
		}

		#endregion


		#region GroupingItems

		public void AddGroupingItem(BillingModelGroupingItem item, bool sort = true)
		{
			item.Position = GroupingItemListChangeHandler.Items.Count;
			GroupingItemListChangeHandler.AddNew(item, sort);
		}

		public void UpdateGroupingItem(BillingModelGroupingItem item, bool sort = true)
		{
			GroupingItemListChangeHandler.AddUpdated(item, sort);
		}

		public void DeleteOrForgetGroupingItem(BillingModelGroupingItem item)
		{
			if (item.IsPersistent)
			{
				DeleteGroupingItem(item);
			}
			else
			{
				ForgetGroupingItem(item);
			}
		}

		public void DeleteGroupingItem(BillingModelGroupingItem item)
		{
			GroupingItemListChangeHandler.AddDeleted(item);
			foreach (BillingModelGroupingItem child in GroupingItemListChangeHandler.Items)
			{
				if (child.Position > item.Position)
				{
					child.Position = child.Position - 1;
					GroupingItemListChangeHandler.AddUpdated(child, false);
				}
			}
		}

		public void ForgetGroupingItem(BillingModelGroupingItem item)
		{
			GroupingItemListChangeHandler.forget(item);
			foreach (BillingModelGroupingItem child in GroupingItemListChangeHandler.Items)
			{
				if (child.Position > item.Position)
				{
					child.Position = child.Position - 1;
					GroupingItemListChangeHandler.AddUpdated(child, false);
				}
			}
		}

		public void MoveUpGroupingItem(BillingModelGroupingItem item)
		{
			MoveGroupingItem(item, true);
		}

		public void MoveDownGroupingItem(BillingModelGroupingItem item)
		{
			MoveGroupingItem(item, false);
		}

		private void MoveGroupingItem(BillingModelGroupingItem item, bool up)
		{
			BillingModelGroupingItem child = GroupingItemListChangeHandler.Items[item.Position + (up ? -1 : 1)];
			if (up && child.Position < item.Position)
			{
				item.Position = item.Position - 1;
				child.Position = child.Position + 1;
				GroupingItemListChangeHandler.AddUpdated(item, false);
			}
			else if (!up && child.Position > item.Position)
			{
				item.Position = item.Position + 1;
				child.Position = child.Position - 1;
				GroupingItemListChangeHandler.AddUpdated(item, false);
			}
			GroupingItemListChangeHandler.AddUpdated(item, true);
		}
		

		#endregion


		#region Parameters

		public void AddParameter(BillingModelParameter item, bool sort = true)
		{
			item.Position = ParameterListChangeHandler.Items.Count;
			ParameterListChangeHandler.AddNew(item, sort);
		}

		public void UpdateParameter(BillingModelParameter item, bool sort = true)
		{
			ParameterListChangeHandler.AddUpdated(item, sort);
		}

		public void DeleteOrForgetParameter(BillingModelParameter item)
		{
			if (item.IsPersistent)
			{
				DeleteParameter(item);
			}
			else
			{
				ForgetParameter(item);
			}
		}

		public void DeleteParameter(BillingModelParameter item)
		{
			ParameterListChangeHandler.AddDeleted(item);
			foreach (BillingModelParameter child in ParameterListChangeHandler.Items)
			{
				if (child.Position > item.Position)
				{
					child.Position = child.Position - 1;
					ParameterListChangeHandler.AddUpdated(child, false);
				}
			}
		}

		public void ForgetParameter(BillingModelParameter item)
		{
			ParameterListChangeHandler.forget(item);
			foreach (BillingModelParameter child in ParameterListChangeHandler.Items)
			{
				if (child.Position > item.Position)
				{
					child.Position = child.Position - 1;
					ParameterListChangeHandler.AddUpdated(child, false);
				}
			}
		}

		public void MoveUpParameter(BillingModelParameter item)
		{
			MoveParameter(item, true);
		}

		public void MoveDownParameter(BillingModelParameter item)
		{
			MoveParameter(item, false);
		}

		private void MoveParameter(BillingModelParameter item, bool up)
		{
			BillingModelParameter child = ParameterListChangeHandler.Items[item.Position + (up ? -1 : 1)];
			if (up && child.Position < item.Position)
			{
				item.Position = item.Position - 1;
				child.Position = child.Position + 1;
				ParameterListChangeHandler.AddUpdated(item, false);
			}
			else if (!up && child.Position > item.Position)
			{
				item.Position = item.Position + 1;
				child.Position = child.Position - 1;
				ParameterListChangeHandler.AddUpdated(item, false);
			}
			ParameterListChangeHandler.AddUpdated(item, true);
		}

		#endregion


		#region InvoiceLabel
		public void AddInvoiceLabel(InvoiceLabel item, bool sort = true)
		{
			item.Position = InvoiceLabelListChangeHandler.Items.Count;
			InvoiceLabelListChangeHandler.AddNew(item, sort);
		}

		public void UpdateInvoiceLabel(InvoiceLabel item, bool sort = true)
		{
			InvoiceLabelListChangeHandler.AddUpdated(item, sort);
		}

		public void DeleteOrForgetInvoiceLabel(InvoiceLabel item)
		{
			if (item.IsPersistent)
			{
				DeleteInvoiceLabel(item);
			}
			else
			{
				ForgetInvoiceLabel(item);
			}
		}

		public void DeleteInvoiceLabel(InvoiceLabel item)
		{
			InvoiceLabelListChangeHandler.AddDeleted(item);
			foreach (InvoiceLabel child in InvoiceLabelListChangeHandler.Items)
			{
				if (child.Position > item.Position)
				{
					child.Position = child.Position - 1;
					InvoiceLabelListChangeHandler.AddUpdated(child, false);
				}
			}
		}

		public void ForgetInvoiceLabel(InvoiceLabel item)
		{
			InvoiceLabelListChangeHandler.forget(item);
			foreach (InvoiceLabel child in InvoiceLabelListChangeHandler.Items)
			{
				if (child.Position > item.Position)
				{
					child.Position = child.Position - 1;
					InvoiceLabelListChangeHandler.AddUpdated(child, false);
				}
			}
		}
		#endregion


		#region DriverGroups

		public void AddDriverGroup(BillingModelDriverGroup item, bool sort = true)
		{
			item.Position = DriverGroupListChangeHandler.Items.Count;
			DriverGroupListChangeHandler.AddNew(item, sort);
		}

		public void UpdateDriverGroup(BillingModelDriverGroup item, bool sort = true)
		{
			DriverGroupListChangeHandler.AddUpdated(item, sort);
		}

		public void DeleteOrForgetDriverGroup(BillingModelDriverGroup item)
		{
			if (item.IsPersistent)
			{
				DeleteDriverGroup(item);
			}
			else
			{
				ForgetDriverGroup(item);
			}
		}

		public void DeleteDriverGroup(BillingModelDriverGroup item)
		{
			DriverGroupListChangeHandler.AddDeleted(item);
			foreach (BillingModelDriverGroup child in DriverGroupListChangeHandler.Items)
			{
				if (child.Position > item.Position)
				{
					child.Position = child.Position - 1;
					DriverGroupListChangeHandler.AddUpdated(child, false);
				}
			}
		}

		public void ForgetDriverGroup(BillingModelDriverGroup item)
		{
			DriverGroupListChangeHandler.forget(item);
			foreach (BillingModelDriverGroup child in DriverGroupListChangeHandler.Items)
			{
				if (child.Position > item.Position)
				{
					child.Position = child.Position - 1;
					DriverGroupListChangeHandler.AddUpdated(child, false);
				}
			}
		}

		public void MoveUpDriverGroup(BillingModelDriverGroup item)
		{
			MoveDriverGroup(item, true);
		}

		public void MoveDownDriverGroup(BillingModelDriverGroup item)
		{
			MoveDriverGroup(item, false);
		}

		private void MoveDriverGroup(BillingModelDriverGroup item, bool up)
		{
			BillingModelDriverGroup child = DriverGroupListChangeHandler.Items[item.Position + (up ? -1 : 1)];
			if (up && child.Position < item.Position)
			{
				item.Position = item.Position - 1;
				child.Position = child.Position + 1;
				DriverGroupListChangeHandler.AddUpdated(item, false);
			}
			else if (!up && child.Position > item.Position)
			{
				item.Position = item.Position + 1;
				child.Position = child.Position - 1;
				DriverGroupListChangeHandler.AddUpdated(item, false);
			}
			DriverGroupListChangeHandler.AddUpdated(item, true);
		}

		#endregion


		#region EnrichmentItems

		public void AddEnrichmentItem(BillingModelEnrichmentItem item, bool sort = true)
		{
			item.Position = EnrichmentItemListChangeHandler.Items.Count;
			EnrichmentItemListChangeHandler.AddNew(item, sort);
		}

		public void UpdateEnrichmentItem(BillingModelEnrichmentItem item, bool sort = true)
		{
			EnrichmentItemListChangeHandler.AddUpdated(item, sort);
		}

		public void DeleteOrForgetEnrichmentItem(BillingModelEnrichmentItem item)
		{
			if (item.IsPersistent)
			{
				DeleteEnrichmentItem(item);
			}
			else
			{
				ForgetEnrichmentItem(item);
			}
		}

		public void DeleteEnrichmentItem(BillingModelEnrichmentItem item)
		{
			EnrichmentItemListChangeHandler.AddDeleted(item);
			foreach (BillingModelEnrichmentItem child in EnrichmentItemListChangeHandler.Items)
			{
				if (child.Position > item.Position)
				{
					child.Position = child.Position - 1;
					EnrichmentItemListChangeHandler.AddUpdated(child, false);
				}
			}
		}

		public void ForgetEnrichmentItem(BillingModelEnrichmentItem item)
		{
			EnrichmentItemListChangeHandler.forget(item);
			foreach (BillingModelEnrichmentItem child in EnrichmentItemListChangeHandler.Items)
			{
				if (child.Position > item.Position)
				{
					child.Position = child.Position - 1;
					EnrichmentItemListChangeHandler.AddUpdated(child, false);
				}
			}
		}

		public void MoveUpEnrichmentItem(BillingModelEnrichmentItem item)
		{
			MoveEnrichmentItem(item, true);
		}

		public void MoveDownEnrichmentItem(BillingModelEnrichmentItem item)
		{
			MoveEnrichmentItem(item, false);
		}

		private void MoveEnrichmentItem(BillingModelEnrichmentItem item, bool up)
		{
			BillingModelEnrichmentItem child = EnrichmentItemListChangeHandler.Items[item.Position + (up ? -1 : 1)];
			if (up && child.Position < item.Position)
			{
				item.Position = item.Position - 1;
				child.Position = child.Position + 1;
				EnrichmentItemListChangeHandler.AddUpdated(item, false);
			}
			else if (!up && child.Position > item.Position)
			{
				item.Position = item.Position + 1;
				child.Position = child.Position - 1;
				EnrichmentItemListChangeHandler.AddUpdated(item, false);
			}
			EnrichmentItemListChangeHandler.AddUpdated(item, true);
		}

		#endregion


		#region BillingDescription
		public void AddBillingDescription(BillingDescription item, bool sort = true)
		{
			item.Position = BillingDescriptionsListChangeHandler.Items.Count;
			BillingDescriptionsListChangeHandler.AddNew(item, sort);
		}

		public void UpdateBillingDescription(BillingDescription item, bool sort = true)
		{
			BillingDescriptionsListChangeHandler.AddUpdated(item, sort);
		}

		public void DeleteOrForgetBillingDescription(BillingDescription item)
		{
			if (item.IsPersistent)
			{
				DeleteBillingDescription(item);
			}
			else
			{
				ForgetBillingDescription(item);
			}
		}

		public void DeleteBillingDescription(BillingDescription item)
		{
			BillingDescriptionsListChangeHandler.AddDeleted(item);
			foreach (BillingDescription child in BillingDescriptionsListChangeHandler.Items)
			{
				if (child.Position > item.Position)
				{
					child.Position = child.Position - 1;
					BillingDescriptionsListChangeHandler.AddUpdated(child, false);
				}
			}
		}

		public void ForgetBillingDescription(BillingDescription item)
		{
			BillingDescriptionsListChangeHandler.forget(item);
			foreach (BillingDescription child in BillingDescriptionsListChangeHandler.Items)
			{
				if (child.Position > item.Position)
				{
					child.Position = child.Position - 1;
					BillingDescriptionsListChangeHandler.AddUpdated(child, false);
				}
			}
		}

		public void MoveUpBillingDescription(BillingDescription item)
		{
			MoveBillingDescription(item, true);
		}

		public void MoveDownBillingDescription(BillingDescription item)
		{
			MoveBillingDescription(item, false);
		}

		private void MoveBillingDescription(BillingDescription item, bool up)
		{
			BillingDescription child = BillingDescriptionsListChangeHandler.Items[item.Position + (up ? -1 : 1)];
			if (up && child.Position < item.Position)
			{
				item.Position = item.Position - 1;
				child.Position = child.Position + 1;
				BillingDescriptionsListChangeHandler.AddUpdated(item, false);
			}
			else if (!up && child.Position > item.Position)
			{
				item.Position = item.Position + 1;
				child.Position = child.Position - 1;
				BillingDescriptionsListChangeHandler.AddUpdated(item, false);
			}
			BillingDescriptionsListChangeHandler.AddUpdated(item, true);
		}

		#endregion


		#region Label
		public void AddLabel(BillingModelLabel label, bool sort = true)
		{
			label.Position = LabelListChangeHandler.Items.Count;
			LabelListChangeHandler.AddNew(label, sort);
		}

		public void UpdateLabel(BillingModelLabel label, bool sort = true)
		{
			LabelListChangeHandler.AddUpdated(label, sort);
		}

		public void DeleteOrForgetLabel(BillingModelLabel label)
		{
			if (label.IsPersistent)
			{
				DeleteLabel(label);
			}
			else
			{
				ForgetLabel(label);
			}
		}

		public void DeleteLabel(BillingModelLabel label)
		{
			LabelListChangeHandler.AddDeleted(label);
			foreach (BillingModelLabel child in LabelListChangeHandler.Items)
			{
				if (child.Position > label.Position)
				{
					child.Position = child.Position - 1;
					LabelListChangeHandler.AddUpdated(child, false);
				}
			}
		}

		public void ForgetLabel(BillingModelLabel label)
		{
			LabelListChangeHandler.forget(label);
			foreach (BillingModelLabel child in LabelListChangeHandler.Items)
			{
				if (child.Position > label.Position)
				{
					child.Position = child.Position - 1;
					LabelListChangeHandler.AddUpdated(child, false);
				}
			}
		}

		#endregion

	}
}
