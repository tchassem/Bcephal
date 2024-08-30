using Bcephal.Models.Base;
using Bcephal.Models.Grids;
using System;
using System.Collections.Generic;
using System.Collections.ObjectModel;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Bcephal.Models.Forms
{
    public class FormModel : AbstractFormModel
    {

		public FormModelMenu Menu { get; set; }

        public int Position { get; set; }

        public ListChangeHandler<FormModel> SubGridListChangeHandler { get; set; }


		public FormModel()
		{
			this.SubGridListChangeHandler = new ListChangeHandler<FormModel>();
		}


        public void AddSubGridModel(FormModel model, bool sort = true)
        {
            model.Position = SubGridListChangeHandler.Items.Count;
            SubGridListChangeHandler.AddNew(model, sort);
        }

        public void UpdateSubGridModel(FormModel model, bool sort = true)
        {
            SubGridListChangeHandler.AddUpdated(model, sort);
        }

        public void InsertSubGridModel(int position, FormModel model)
        {
            model.Position = position;
            foreach (FormModel child in SubGridListChangeHandler.Items)
            {
                if (child.Position >= model.Position)
                {
                    child.Position = child.Position + 1;
                    SubGridListChangeHandler.AddUpdated(child, false);
                }
            }
            SubGridListChangeHandler.AddNew(model);
        }

        public void DeleteOrForgetSubGridModel(FormModel model)
        {
            if (model.IsPersistent)
            {
                DeleteSubGridModel(model);
            }
            else
            {
                ForgetSubGridModel(model);
            }
        }

        public void DeleteSubGridModel(FormModel model)
        {
            SubGridListChangeHandler.AddDeleted(model);
            foreach (FormModel child in SubGridListChangeHandler.Items)
            {
                if (child.Position > model.Position)
                {
                    child.Position = child.Position - 1;
                    SubGridListChangeHandler.AddUpdated(child, false);
                }
            }
        }

        public void ForgetSubGridModel(FormModel model)
        {
            SubGridListChangeHandler.forget(model);
            foreach (FormModel child in SubGridListChangeHandler.Items)
            {
                if (child.Position > model.Position)
                {
                    child.Position = child.Position - 1;
                    SubGridListChangeHandler.AddUpdated(child, false);
                }
            }
        }



        public ObservableCollection<GrilleColumn> GetAvailaibleColumns(Grille grid)
        {
            ObservableCollection<GrilleColumn> columns = new ObservableCollection<GrilleColumn>();
            if (grid != null)
            {
                foreach (GrilleColumn column in grid.ColumnListChangeHandler.Items)
                {
                    FormModelField field = GetField(column);
                    if(field == null)
                    {
                        field = GetSugGridField(column);
                        if (field == null)
                        {
                            columns.Add(column);
                        }
                    }                    
                }
            }
            return columns;
        }

        public FormModelField GetField(GrilleColumn column)
        {
            foreach (FormModelField field in this.FieldListChangeHandler.Items)
            {
                if(field.ColumnId.HasValue && field.ColumnId == column.Id.Value)
                {
                    return field;
                }
            }
            return null;
        }

        public FormModelField GetSugGridField(GrilleColumn column)
        {
            foreach (FormModel model in this.SubGridListChangeHandler.Items)
            {
                FormModelField field = model.GetField(column);
                if (field != null)
                {
                    return field;
                }
            }
            return null;
        }

        public FormModelField GetField(long? id)
        {
            foreach (FormModelField field in this.FieldListChangeHandler.Items)
            {
                if (field.Id.HasValue && field.Id.Value == id.Value)
                {
                    return field;
                }
            }
            return null;
        }


    }
}
