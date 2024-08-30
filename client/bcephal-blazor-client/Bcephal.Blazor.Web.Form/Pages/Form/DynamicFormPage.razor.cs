using Microsoft.AspNetCore.Components;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Bcephal.Blazor.Web.Form.Pages.Form
{
  public partial  class DynamicFormPage
    {
        [Parameter]
        public long? Id { get; set; }

        [Parameter]
        public long? FormModelId { get; set; }

        private string Key => $"DynamicForm{FormModelId.Value}__{SubKey}";
        private string SubKey => Id.HasValue ? "Edit" : "new";

        public override  Task SetParametersAsync(ParameterView parameters)
        {
            parameters.TryGetValue("Id",out long? id);
            Id = id;
            return base.SetParametersAsync(parameters);
        }
    }
}
