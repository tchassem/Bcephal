using Bcephal.Models.Forms;
using Microsoft.AspNetCore.Components;
using System;
using System.Collections.Generic;
using System.Collections.ObjectModel;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using Bcephal.Models.Utils;

namespace Bcephal.Blazor.Web.Form.Shared.Component
{
   public partial class FormModelFieldEdit
    {
        [Parameter]
        public int ColumnSize { get; set; } = 4;
        [Parameter]
        public AbstractFormModel Model { get; set; }
        bool IsSmallScreen { get; set; }
        [Parameter]
        public Action<long, FormDataValue> AddOrUpdateHandler { get; set; }
        [Parameter]
        public Func<long?, FormDataValue> ValueHandler { get; set; }


        private void AddOrUpdateFromData(long key, FormDataValue value)
        {
            AddOrUpdateHandler?.Invoke(key, value);
        }

        private FormDataValue GetData(long? key)
        {
            return ValueHandler?.Invoke(key.Value);
        }

        IEnumerable<FormModelField> Fields() {
            ObservableCollection<FormModelField> fields = Model.FieldListChangeHandler.GetItems();
            fields.BubbleSort();
            return fields.Where(ite => ite.VisibleInEditor);
            }
    }
}
