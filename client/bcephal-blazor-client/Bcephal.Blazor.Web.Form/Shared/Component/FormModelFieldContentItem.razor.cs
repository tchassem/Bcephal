using Bcephal.Models.Forms;
using Microsoft.AspNetCore.Components;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Bcephal.Blazor.Web.Form.Shared.Component
{
   public partial class FormModelFieldContentItem
    {
        [Parameter]
        public Action<long, FormDataValue> AddOrUpdateHandler { get; set; }
        [Parameter]
        public Func<long?, FormDataValue> ValueHandler { get; set; }
        [Parameter]
        public FormModelField Field { get; set; }
        [Parameter]
        public string filedName { get; set; }

        private void AddOrUpdateFromData(long key, FormDataValue value)
        {
            AddOrUpdateHandler?.Invoke(key, value);
        }

        private FormDataValue GetData(long? key)
        {
            return ValueHandler?.Invoke(key.Value);
        }

    }
}
