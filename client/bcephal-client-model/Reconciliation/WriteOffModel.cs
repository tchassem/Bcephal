using Bcephal.Models.Base;
using Newtonsoft.Json;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Bcephal.Models.Reconciliation
{
    public class WriteOffModel : Persistent
    {


		public string WriteOffMeasureSide { get; set; }
		[JsonIgnore]
		public ReconciliationModelSide WriteOffMeasureSides
		{
			get
			{
				return string.IsNullOrEmpty(WriteOffMeasureSide) ? ReconciliationModelSide.LEFT : ReconciliationModelSide.GetByCode(WriteOffMeasureSide);
			}
			set
			{
				this.WriteOffMeasureSide = value != null ? value.getCode() : null;
			}
		}

		public long? WriteOffMeasureId { get; set; }
				
		public ListChangeHandler<WriteOffField> FieldListChangeHandler { get; set; }


		public WriteOffModel()
		{
			this.FieldListChangeHandler = new ListChangeHandler<WriteOffField>();
		}


        public void AddWriteOffField(WriteOffField writeOffField, bool sort = true)
        {
            writeOffField.Position = FieldListChangeHandler.Items.Count;
            FieldListChangeHandler.AddNew(writeOffField, sort);
        }

        public void UpdateWriteOffField(WriteOffField writeOffField, bool sort = true)
        {
            FieldListChangeHandler.AddUpdated(writeOffField, sort);
        }

        public void InsertWriteOffField(int position, WriteOffField writeOffField)
        {
            writeOffField.Position = position;
            foreach (WriteOffField child in FieldListChangeHandler.Items)
            {
                if (child.Position >= writeOffField.Position)
                {
                    child.Position = child.Position + 1;
                    FieldListChangeHandler.AddUpdated(child, false);
                }
            }
            FieldListChangeHandler.AddNew(writeOffField);
        }


        public void DeleteOrForgetWriteOffField(WriteOffField writeOffField)
        {
            if (writeOffField.IsPersistent)
            {
                DeleteWriteOffField(writeOffField);
            }
            else
            {
                ForgetWriteOffField(writeOffField);
            }
        }

        public void DeleteWriteOffField(WriteOffField writeOffField)
        {
            FieldListChangeHandler.AddDeleted(writeOffField);
            foreach (WriteOffField child in FieldListChangeHandler.Items)
            {
                if (child.Position > writeOffField.Position)
                {
                    child.Position = child.Position - 1;
                    FieldListChangeHandler.AddUpdated(child, false);
                }
            }
        }

        public void ForgetWriteOffField(WriteOffField writeOffField)
        {
            FieldListChangeHandler.forget(writeOffField);
            foreach (WriteOffField child in FieldListChangeHandler.Items)
            {
                if (child.Position > writeOffField.Position)
                {
                    child.Position = child.Position - 1;
                    FieldListChangeHandler.AddUpdated(child, false);
                }
            }
        }
    }
}
