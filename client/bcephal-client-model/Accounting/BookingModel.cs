using Bcephal.Models.Billing.Model;
using Bcephal.Models.Grids;
using Newtonsoft.Json;
using System;
using System.Linq;

namespace Bcephal.Models.Base.Accounting
{
    public class BookingModel : SchedulableObject
    {
        public string schedulerOption { get; set; }
        [JsonIgnore]
        public SchedulerOption SchedulingOption
        {
            get { return !string.IsNullOrEmpty(schedulerOption) ? SchedulerOption.GetByCode(schedulerOption) : SchedulerOption.ON_REQUEST; }
            set { this.schedulerOption = value != null ? value.code : null; }
        }

        public string OldSchedulingOption { get; set; }
        public bool OldActive { get; set; }
        public string OldCronExpression { get; set; }

        public string periodSide { get; set; }
        [JsonIgnore]
        public BillingModelPeriodSide PeriodSide
        {
            get { return !string.IsNullOrEmpty(periodSide) ? BillingModelPeriodSide.GetByCode(periodSide) : BillingModelPeriodSide.ALL; }
            set { this.periodSide = value != null ? value.code : null; }
        }

        public string periodGranularity { get; set; }
        [JsonIgnore]
        public BillingModelPeriodGranularity PeriodGranularity
        {
            get { return !string.IsNullOrEmpty(periodGranularity) ? BillingModelPeriodGranularity.GetByCode(periodGranularity) : BillingModelPeriodGranularity.MONTH; }
            set { this.periodGranularity = value != null ? value.code : null; }
        }

        public bool includeZeroAmountEntries { get; set; }

        public bool selectPeriodAtRuntime { get; set; }


        public UniverseFilter filter { get; set; }

        public string FromDynamicPeriod { get; set; }
        public int? FromOperationNumber { get; set; }
        public string FromOperationGranularity { get; set; }
        public string FromOperation { get; set; }

        public string ToDynamicPeriod { get; set; }
        public int? ToOperationNumber { get; set; }
        public string ToOperationGranularity { get; set; }
        public string ToOperation { get; set; }
        public string periodFrom { get; set; }

        [JsonIgnore]
        public DateTime? FromDateTime
        {
            get
            {
                try
                {
                    return DateUtils.Parse(periodFrom);
                }
                catch (Exception)
                {
                    return null;
                }
            }
        }

        public string periodTo { get; set; }
        [JsonIgnore]
        public DateTime? ToDateTime
        {
            get
            {
                try
                {
                    return DateUtils.Parse(periodTo);
                }
                catch (Exception)
                {
                    return null;
                }
            }
        }


        public ListChangeHandler<BookingModelPivot> pivotListChangeHandler { get; set; }

        public decimal minDeltaAmount { get; set; }

        public decimal maxDeltaAmount { get; set; }


        public BookingModel()
        {
            this.Active = true;
            this.pivotListChangeHandler = new ListChangeHandler<BookingModelPivot>();
            this.minDeltaAmount = 0;
            this.maxDeltaAmount = 0;
        }


        public void AddPivot(BookingModelPivot pivot)
        {
            pivot.Position = pivotListChangeHandler.Items.Count;
            pivotListChangeHandler.AddNew(pivot, true);
        }

        public void DeletePivot(BookingModelPivot pivot)
        {
            pivotListChangeHandler.AddDeleted(pivot, true);
            foreach (BookingModelPivot child in pivotListChangeHandler.Items)
            {
                if (child.Position > pivot.Position) child.Position = child.Position - 1;
            }
        }

        public void UpdatePivot(BookingModelPivot pivot)
        {
            pivotListChangeHandler.AddUpdated(pivot);
        }

        public void ForgetPivot(BookingModelPivot pivot)
        {
            pivotListChangeHandler.forget(pivot, true);
            foreach (BookingModelPivot child in pivotListChangeHandler.Items)
            {
                if (child.Position > pivot.Position) child.Position = child.Position - 1;
            }
        }

        public void DeleteOrForgetPivot(BookingModelPivot pivot)
        {
            if (pivot.Id.HasValue)
            {
                DeletePivot(pivot);
            }
            else
            {
                ForgetPivot(pivot);
            }
        }

        public override string ToString()
        {
            return this.Name != null ? this.Name : base.ToString();
        }

        public override int CompareTo(object obj)
        {
            if (obj == null || !(obj is BookingModel)) return 1;
            return this.Name.CompareTo(((BookingModel)obj).Name);
        }
    }
}
