using Bcephal.Models.Base;
using Bcephal.Models.Grids;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Bcephal.Models.Billing.Model
{
    public class BillingModelLabel : Persistent
    {

        public int Position { get; set; }

        public string Code { get; set; }

        public UniverseFilter Filter { get; set; }

        public ListChangeHandler<BillingModelLabelValue> ValueListChangeHandler { get; set; }

        public BillingModelLabel()
        {
            ValueListChangeHandler = new ListChangeHandler<BillingModelLabelValue>();
        }

        public void AddValue(BillingModelLabelValue value, bool sort = true)
        {
            value.Position = ValueListChangeHandler.Items.Count;
            ValueListChangeHandler.AddNew(value, sort);
        }

        public void UpdateValue(BillingModelLabelValue value, bool sort = true)
        {
            ValueListChangeHandler.AddUpdated(value, sort);
        }

        public void DeleteOrForgetValue(BillingModelLabelValue value)
        {
            if (value.Id.HasValue)
            {
                DeleteValue(value);
            }
            else
            {
                ForgetValue(value);
            }
        }

        public void DeleteValue(BillingModelLabelValue value)
        {
            ValueListChangeHandler.AddDeleted(value);
            foreach (BillingModelLabelValue child in ValueListChangeHandler.Items)
            {
                if (child.Position > value.Position)
                {
                    child.Position = child.Position - 1;
                    ValueListChangeHandler.AddUpdated(child, false);
                }
            }
        }

        public void ForgetValue(BillingModelLabelValue value)
        {
            ValueListChangeHandler.forget(value);
            foreach (BillingModelLabelValue child in ValueListChangeHandler.Items)
            {
                if (child.Position > value.Position)
                {
                    child.Position = child.Position - 1;
                    ValueListChangeHandler.AddUpdated(child, false);
                }
            }
        }
    }
}
