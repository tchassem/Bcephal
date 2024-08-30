
using Bcephal.Blazor.Web.Base.Services;
using Bcephal.Models.Grids;
using Microsoft.AspNetCore.Components;
using Microsoft.AspNetCore.Components.Web;
using System;
using System.Threading.Tasks;

namespace Bcephal.Blazor.Web.Sourcing.Shared.Grille
{
    public partial class GridColumnComponentValue : ComponentBase
    {
        [Inject]
        private AppState AppState { get; set; }

        [Parameter]
        public GrilleColumn GrilleColumn { get; set; }

        [Parameter]
        public EventCallback<GrilleColumn> GrilleColumnChanged { get; set; }

        [Parameter]
        public bool Editable { get; set; } = true;


        public string DefaultStringValue
        {
            get { return GrilleColumn.DefaultStringValue; }
            set {
                GrilleColumn.DefaultStringValue = value;
                GrilleColumnChanged.InvokeAsync(GrilleColumn);
            }
        }
        public decimal? DefaultDecimalValue
        {
            get { return GrilleColumn.DefaultDecimalValue; }
            set {

                GrilleColumn.DefaultDecimalValue = value;
                GrilleColumnChanged.InvokeAsync(GrilleColumn);
            }
        }
        public DateTime? DefaultDateValue
        {
            get { return GrilleColumn.DefaultDateValue; }
            set {

                GrilleColumn.DefaultDateValue = value;
                GrilleColumnChanged.InvokeAsync(GrilleColumn);
            }
        }

        public bool ApplyDefaultValueIfCellEmpty
        {
            get { return GrilleColumn.ApplyDefaultValueIfCellEmpty; }
            set
            {
                GrilleColumn.ApplyDefaultValueIfCellEmpty = value;
                GrilleColumnChanged.InvokeAsync(GrilleColumn);
            }
        }
        public bool ApplyDefaultValueToFutureLine
        {
            get { return GrilleColumn.ApplyDefaultValueToFutureLine; }
            set
            {
                GrilleColumn.ApplyDefaultValueToFutureLine = value;
                GrilleColumnChanged.InvokeAsync(GrilleColumn);
            }
        }

        private void Apply(MouseEventArgs args)
        {
            GrilleColumn.DefaultDateValue = DefaultDateValue;
            GrilleColumn.DefaultDecimalValue = DefaultDecimalValue;
            GrilleColumn.DefaultStringValue = DefaultStringValue;
            GrilleColumnChanged.InvokeAsync(GrilleColumn);
        }

        private void ResetValue(MouseEventArgs args)
        {
            GrilleColumn.DefaultDateValue = null;
            GrilleColumn.DefaultDecimalValue = null;
            GrilleColumn.DefaultStringValue = null;
            GrilleColumnChanged.InvokeAsync(GrilleColumn);
        }

        protected override Task OnInitializedAsync()
        {
            DefaultDateValue = GrilleColumn.DefaultDateValue;
            DefaultDecimalValue = GrilleColumn.DefaultDecimalValue;
            DefaultStringValue = GrilleColumn.DefaultStringValue;
            return base.OnInitializedAsync();
        }
    }
}
