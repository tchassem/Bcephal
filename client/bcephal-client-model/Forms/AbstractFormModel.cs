using Bcephal.Models.Base;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Bcephal.Models.Forms
{
    public class AbstractFormModel : MainObject
    {

		public long? GridId { get; set; }

		public string Description { get; set; }

		public bool Active { get; set; }

		public bool AllowEditorView { get; set; }
		public string EditorViewTitle { get; set; }

		public bool AllowBrowserView { get; set; }
		public string BrowserViewTitle { get; set; }

		public bool AllowValidation { get; set; }

		public ListChangeHandler<FormModelField> FieldListChangeHandler { get; set; }

		public AbstractFormModel()
		{
			this.FieldListChangeHandler = new ListChangeHandler<FormModelField>();
		}

        public void AddField(FormModelField field, bool sort = true)
        {
            field.Position = FieldListChangeHandler.Items.Count;
            FieldListChangeHandler.AddNew(field, sort);
        }

        public void UpdateField(FormModelField field, bool sort = true)
        {
            FieldListChangeHandler.AddUpdated(field, sort);
        }

        public void InsertField(int position, FormModelField field)
        {
            field.Position = position;
            foreach (FormModelField child in FieldListChangeHandler.Items)
            {
                if (child.Position >= field.Position)
                {
                    child.Position = child.Position + 1;
                    FieldListChangeHandler.AddUpdated(child, false);
                }
            }
            FieldListChangeHandler.AddNew(field);
        }

        public void DeleteOrForgetField(FormModelField field)
        {
            if (field.IsPersistent)
            {
                DeleteField(field);
            }
            else
            {
                ForgetField(field);
            }
        }

        public void DeleteField(FormModelField field)
        {
            FieldListChangeHandler.AddDeleted(field);
            foreach (FormModelField child in FieldListChangeHandler.Items)
            {
                if (child.Position > field.Position)
                {
                    child.Position = child.Position - 1;
                    FieldListChangeHandler.AddUpdated(child, false);
                }
            }
        }

        public void ForgetField(FormModelField field)
        {
            FieldListChangeHandler.forget(field);
            foreach (FormModelField child in FieldListChangeHandler.Items)
            {
                if (child.Position > field.Position)
                {
                    child.Position = child.Position - 1;
                    FieldListChangeHandler.AddUpdated(child, false);
                }
            }
        }


    }
}
