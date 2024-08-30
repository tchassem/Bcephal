using Bcephal.Blazor.Web.Base.Services;
using Bcephal.Models.Base;
using Bcephal.Models.Base.Accounting;
using Bcephal.Models.Dimensions;
using Microsoft.AspNetCore.Components;
using System;
using System.Collections.Generic;
using System.Collections.ObjectModel;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Bcephal.Blazor.Web.Accounting.Shared
{
  partial class BookingModelPivotComponent
    {
        [Parameter]
        public ObservableCollection<HierarchicalData> EntityItems { get; set; }

        [Parameter]
        public BookingModel BookingModel { get; set; }

        [Parameter]
        public EventCallback<BookingModel> BookingModelChanged { get; set; }

        public string KeyName { get; set; } = "BookingModelPivotComponent";

        public bool IsSmallScreen { get; set; }

        [Inject]
        public AppState AppState { get; set; }

        [Parameter]
        public bool removeButton { get; set; }

        public void ModelPivotHandler(HierarchicalData hierarchical)
        {
            BookingModel.AddPivot(new BookingModelPivot() { Name = hierarchical.Name, DimensionId = hierarchical.Id, Id = hierarchical.Id });
        }

        private void ModelPivotDeleteHandler(BookingModelPivot bookingModelPivot)
        {
            BookingModel.DeleteOrForgetPivot(bookingModelPivot);
        }
    }
}
