using Bcephal.Blazor.Web.Base.Services;
using Bcephal.Models.Clients;
using DevExpress.Blazor;
using Microsoft.AspNetCore.Components;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Bcephal.Blazor.Web.Administration.Pages.Client
{
   public partial class CustomListBox : ComponentBase
    {
        [Parameter]
        public IEnumerable<ClientFunctionality> Values { get; set; }

        [Parameter]
        public EventCallback<IEnumerable<ClientFunctionality>> ValuesChanged { get; set; }

        [Parameter]
        public bool ShowCheckboxes { get; set; } = true;

        [Parameter]
        public ListBoxSelectionMode ListBoxSelectionMode { get; set; } = ListBoxSelectionMode.Multiple;
        
        [Parameter]
        public IEnumerable<ClientFunctionality> Functionalities { get; set; }

        [Inject]
        public AppState AppState { get; set; }

        [Parameter]
        public string CssClass { get; set; } = "w-auto mt-1 mr-1 flex-grow-1 chi-220 ";

        [Parameter]
        public bool Editable_{ get; set; } = true;

        public IEnumerable<ClientFunctionality> ValuesBinding { get => Values;
            set {
                Values = value;
                ValuesChanged.InvokeAsync(Values);
            }
        }
    }
}
