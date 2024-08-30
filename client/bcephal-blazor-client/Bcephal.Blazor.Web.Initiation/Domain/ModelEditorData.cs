using Bcephal.Models.Base;
using System;
using System.Collections.Generic;
using System.Collections.ObjectModel;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Bcephal.Blazor.Web.Initiation.Domain
{
    public class ModelEditorData : EditorData<Model>
    {

        public ListChangeHandler<Model> AllModels { get; set; }

        public void AddModel(Model model, bool sort = true)
        {
            model.Position = AllModels.Items.Count;
            AllModels.AddNew(model, sort);
        }

        public void UpdateModel(Model model, bool sort = true)
        {
            AllModels.AddUpdated(model, sort);
        }

        public void DeleteOrForgetModel(Model model)
        {
            if (model.IsPersistent)
            {
                DeleteModel(model);
            }
            else
            {
                ForgetModel(model);
            }
        }

        public void DeleteModel(Model model)
        {
            AllModels.AddDeleted(model);
            foreach (Model child in AllModels.Items)
            {
                if (child.Position > model.Position)
                {
                    child.Position = child.Position - 1;
                    AllModels.AddUpdated(child, false);
                }
            }
        }

        public void ForgetModel(Model model)
        {
            AllModels.forget(model);
            foreach (Model child in AllModels.Items)
            {
                if (child.Position > model.Position)
                {
                    child.Position = child.Position - 1;
                    AllModels.AddUpdated(child, false);
                }
            }
        }

    }
}
