using Bcephal.Models.Base;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Bcephal.Models.Scripts
{
    public class ScriptRunner : MainObject
    {

        public string Description { get; set; }

        public bool Scheduled { get; set; }

        public string CronExpression { get; set; }

        public ListChangeHandler<ScriptRunnerItem> ItemListChangeHandler { get; set; }


        public ScriptRunner() : base()
        {
            ItemListChangeHandler = new ListChangeHandler<ScriptRunnerItem>();
        }

        public void AddItem(ScriptRunnerItem item, bool sort = true)
        {
            item.Position = ItemListChangeHandler.Items.Count;
            ItemListChangeHandler.AddNew(item, sort);
        }

        public void UpdateItem(ScriptRunnerItem item, bool sort = true)
        {
            ItemListChangeHandler.AddUpdated(item, sort);
        }

        public void InsertIem(int position, ScriptRunnerItem item)
        {
            item.Position = position;
            foreach (ScriptRunnerItem child in ItemListChangeHandler.Items)
            {
                if (child.Position >= item.Position)
                {
                    child.Position = child.Position + 1;
                    ItemListChangeHandler.AddUpdated(child, false);
                }
            }
            ItemListChangeHandler.AddNew(item);
        }


        public void DeleteOrForgetItem(ScriptRunnerItem item)
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

        public void DeleteItem(ScriptRunnerItem item)
        {
            ItemListChangeHandler.AddDeleted(item);
            foreach (ScriptRunnerItem child in ItemListChangeHandler.Items)
            {
                if (child.Position > item.Position)
                {
                    child.Position = child.Position - 1;
                    ItemListChangeHandler.AddUpdated(child, false);
                }
            }
        }

        public void ForgetItem(ScriptRunnerItem item)
        {
            ItemListChangeHandler.forget(item);
            foreach (ScriptRunnerItem child in ItemListChangeHandler.Items)
            {
                if (child.Position > item.Position)
                {
                    child.Position = child.Position - 1;
                    ItemListChangeHandler.AddUpdated(child, false);
                }
            }
        }

    }
}
