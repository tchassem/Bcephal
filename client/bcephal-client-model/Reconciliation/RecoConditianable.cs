using Bcephal.Models.Base;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Bcephal.Models.Reconciliation
{
    public abstract class RecoConditianable : SchedulableObject
    {
        public ListChangeHandler<ReconciliationCondition> ConditionListChangeHandler { get; set; }

        public RecoConditianable()
        {
            this.ConditionListChangeHandler = new ListChangeHandler<ReconciliationCondition>();
        }

        public void AddCondition(ReconciliationCondition condition, bool sort = true)
        {
            condition.Position = ConditionListChangeHandler.Items.Count;
            ConditionListChangeHandler.AddNew(condition, sort);
        }

        public void UpdateCondition(ReconciliationCondition condition, bool sort = true)
        {
            ConditionListChangeHandler.AddUpdated(condition, sort);
        }

        public void InsertColumn(int position, ReconciliationCondition condition)
        {
            condition.Position = position;
            foreach (ReconciliationCondition child in ConditionListChangeHandler.Items)
            {
                if (child.Position >= condition.Position)
                {
                    child.Position = child.Position + 1;
                    ConditionListChangeHandler.AddUpdated(child, false);
                }
            }
            ConditionListChangeHandler.AddNew(condition);
        }


        public void DeleteOrForgetCondition(ReconciliationCondition condition)
        {
            if (condition.IsPersistent)
            {
                DeleteCondition(condition);
            }
            else
            {
                ForgetCondition(condition);
            }
        }

        public void DeleteCondition(ReconciliationCondition condition)
        {
            ConditionListChangeHandler.AddDeleted(condition);
            foreach (ReconciliationCondition child in ConditionListChangeHandler.Items)
            {
                if (child.Position > condition.Position)
                {
                    child.Position = child.Position - 1;
                    ConditionListChangeHandler.AddUpdated(child, false);
                }
            }
        }

        public void ForgetCondition(ReconciliationCondition condition)
        {
            ConditionListChangeHandler.forget(condition);
            foreach (ReconciliationCondition child in ConditionListChangeHandler.Items)
            {
                if (child.Position > condition.Position)
                {
                    child.Position = child.Position - 1;
                    ConditionListChangeHandler.AddUpdated(child, false);
                }
            }
        }

    }
}
