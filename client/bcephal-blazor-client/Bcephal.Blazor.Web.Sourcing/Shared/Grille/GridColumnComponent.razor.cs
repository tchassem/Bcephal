
using Bcephal.Blazor.Web.Base.Services;
using Bcephal.Models.Grids;
using Microsoft.AspNetCore.Components;
using System.Collections.Generic;
using System.Threading.Tasks;

namespace Bcephal.Blazor.Web.Sourcing.Shared.Grille
{
    public partial class GridColumnComponent : ComponentBase
    {

        [Inject] private AppState AppState { get; set; }
        [Parameter] public GrilleColumn GrilleColumn { get; set; }
        [Parameter] public EventCallback<GrilleColumn> GrilleColumnChanged { get; set; }
        [Parameter] public GrilleType grilleType { get; set; }
        [Parameter] public int ItemsCount { get; set; }

        public GrilleColumn GrilleColumnBinding
        {
            get { return GrilleColumn; }
            set
            {
               
                GrilleColumn = value;
                GrilleColumnChanged.InvokeAsync(value);
            }
        }


        public string DimensionName
        {
            get { return GrilleColumn.DimensionName; }
            set
            {

                GrilleColumn.DimensionName = value;
                GrilleColumnChanged.InvokeAsync(GrilleColumn);
            }
        }

        public string Name
        {
            get { return GrilleColumn.Name; }
            set
            {

                GrilleColumn.Name = value;
                GrilleColumnChanged.InvokeAsync(GrilleColumn);
            }
        }

        public string Backgrounds
        {
            get { return GrilleColumn.Backgrounds; }
            set
            {

                GrilleColumn.Backgrounds = value;
                GrilleColumnChanged.InvokeAsync(GrilleColumn);
            }
        }

        public string Foregrounds
        {
            get { return GrilleColumn.Foregrounds; }
            set
            {

                GrilleColumn.Foregrounds = value;
                GrilleColumnChanged.InvokeAsync(GrilleColumn);
            }
        }


        public bool Editable_
        {
            get { return GrilleColumn.Editable; }
            set
            {

                GrilleColumn.Editable = value;
                GrilleColumnChanged.InvokeAsync(GrilleColumn);
            }
        }

        private string ColumnWidth() { return ItemsCount == 0 ? "auto" : ItemsCount < 10 ? "auto" : "200px"; }
        public int? Width
        {
            get {
                string valString = ColumnWidth();
                if (!string.IsNullOrWhiteSpace(valString) && valString.Contains("px") && !GrilleColumn.Width.HasValue)
                {
                    int.TryParse(valString.Replace("px", ""), out int v);
                    GrilleColumn.Width = v;
                }
                return GrilleColumn.Width;
            }
            set
            {

                GrilleColumn.Width = value;
                GrilleColumnChanged.InvokeAsync(GrilleColumn);
            }
        }



        public bool Mandatory
        {
            get { return GrilleColumn.Mandatory; }
            set
            {

                GrilleColumn.Mandatory = value;
                GrilleColumnChanged.InvokeAsync(GrilleColumn);
            }
        }


        public bool Show
        {
            get { return GrilleColumn.Show; }
            set
            {

                GrilleColumn.Show = value;
                GrilleColumnChanged.InvokeAsync(GrilleColumn);
            }
        }


        public bool ShowValuesInDropList
        {
            get { return GrilleColumn.ShowValuesInDropList; }
            set
            {

                GrilleColumn.ShowValuesInDropList = value;
                GrilleColumnChanged.InvokeAsync(GrilleColumn);
            }
        }


        public int NbrOfDecimal
        {
            get { return GrilleColumn.Format.NbrOfDecimal; }
            set
            {

                GrilleColumn.Format.NbrOfDecimal = value;
                GrilleColumnChanged.InvokeAsync(GrilleColumn);
            }
        }


        public bool UsedSeparator
        {
            get { return GrilleColumn.Format.UsedSeparator; }
            set
            {

                GrilleColumn.Format.UsedSeparator = value;
                GrilleColumnChanged.InvokeAsync(GrilleColumn);
            }
        }


        public string DefaultFormat
        {
            get { return GrilleColumn.Format.DefaultFormat; }
            set
            {

                GrilleColumn.Format.DefaultFormat = value;
                GrilleColumnChanged.InvokeAsync(GrilleColumn);
            }
        }


        bool IsSmallScreen { get; set; }
        bool ShowFormatPopUp = false;
       

        protected override async Task OnAfterRenderAsync(bool firstRender)
        {
            await base.OnAfterRenderAsync(firstRender);
            AppState.Update = true;
         
        }

        [Parameter]
        public bool Editable { get; set; } = true;
        bool IsReport => GrilleType.REPORT.Equals(grilleType);
        bool IsInputGrid => GrilleType.INPUT.Equals(grilleType);

        public string ColumnFixedStyle
        {
            get
            {
                if (GrilleColumn != null && GrilleColumn.ColumnFixedStyle != null)
                {
                    return GrilleColumn.ColumnFixedStyle.GetText(text => AppState[text]);
                }
                return GrilleColumnFixedStyle.None.GetText(text => AppState[text]);
            }
            set
            {
                GrilleColumn.ColumnFixedStyle = GrilleColumnFixedStyle.None.GetGrilleColumnFixedStyle(value, text => AppState[text]);
                GrilleColumnChanged.InvokeAsync(GrilleColumn);
            }
        }

        List<string> Functions => new() { "Sum", "Average", "Count", "Max", "Min" };
        
    }
}
