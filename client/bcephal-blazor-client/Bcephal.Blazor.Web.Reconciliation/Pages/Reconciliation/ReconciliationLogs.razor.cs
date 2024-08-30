using Bcephal.Blazor.Web.Base.Services;
using Bcephal.Blazor.Web.Base.Shared.AbstractComponent;
using Bcephal.Blazor.Web.Reconciliation.Services;
using Bcephal.Models.Base;
using Bcephal.Models.Grids.Filters;
using Bcephal.Models.Reconciliation;
using DevExtreme.AspNet.Data;
using Microsoft.AspNetCore.Components;
using Microsoft.Extensions.Localization;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;

namespace Bcephal.Blazor.Web.Reconciliation.Pages.Reconciliation
{
    public partial class ReconciliationLogs : AbstractNewGridComponent<ReconciliationLog, ReconciliationLog>
    {
        [Parameter]
        public Action<RenderFragment> Filter { get; set; }

        [Parameter]
        public long? RecoId { get; set; }

        protected dynamic[] GridColumns => new[] {
                        new {CaptionName = AppState["CreationDate"] ,ColumnWidth="100px", ColumnName = nameof(ReconciliationLog.CreationDateTime), ColumnType = typeof(DateTime?)},
                        new {CaptionName = AppState["Type"],ColumnWidth="100px", ColumnName = nameof(ReconciliationLog.RecoType), ColumnType = typeof(string)},
                        new {CaptionName = AppState["User"] ,ColumnWidth="100px", ColumnName = nameof(ReconciliationLog.Username), ColumnType = typeof(string)},
                        new {CaptionName = AppState["Reconciliation.Action"] ,ColumnWidth="100px", ColumnName = nameof(ReconciliationLog.Action), ColumnType = typeof(ReconciliationActions)},
                        new {CaptionName = AppState["Reconciliation.RecoN"] ,ColumnWidth="100px", ColumnName = nameof(ReconciliationLog.ReconciliationNbr), ColumnType = typeof(decimal)},
                        new {CaptionName = AppState["Reconciliation.LeftAmount"] ,ColumnWidth="100px", ColumnName = nameof(ReconciliationLog.LeftAmount), ColumnType = typeof(decimal)},
                        new {CaptionName = AppState["Reconciliation.RightAmount"],ColumnWidth="100px", ColumnName = nameof(ReconciliationLog.RigthAmount), ColumnType = typeof(decimal)},
                        new {CaptionName = AppState["Reconciliation.BalanceAmount"] ,ColumnWidth="100px", ColumnName = nameof(ReconciliationLog.BalanceAmount), ColumnType = typeof(decimal)},
                        new {CaptionName = AppState["Reconciliation.DeltaAmount"] ,ColumnWidth="100px", ColumnName = nameof(ReconciliationLog.WriteoffAmount), ColumnType = typeof(decimal)},

                    };

        protected override int ItemsCount => GridColumns.Length;

        protected bool ShowPopup { get; set; }

        [Inject]
        public ReconciliationLogService ReconciliationLogService { get; set; }

        [Parameter]
        public bool Editable { get; set; }


        protected override void OnInitialized()
        {
            base.OnInitialized();
            IsNavLink = false;
            NewButtonVisible = false;
            DeleteButtonVisible = true;
            ClearFilterButtonVisible = false;
            EditButtonVisible = false;
        }

        protected override ReconciliationLog NewItem()

        {
            return null;
        }

        protected override string KeyFieldName()
        {
            return nameof(ReconciliationLog.Id);
        }

        protected override object KeyFieldValue(ReconciliationLog item)
        {
            return item.Id;
        }
        protected override string NavLinkURI()
        {
            return Route.RECONCILIATION_LOG;
        }

        protected override Task OnRowInserting(ReconciliationLog newValues)
        {
            return Task.CompletedTask;
        }


        protected override  Task<BrowserDataPage<ReconciliationLog>> SearchRows(BrowserDataFilter filter)
        {
            if (RecoId.HasValue)
            {
                filter.GroupId = RecoId;
                return ReconciliationLogService.SearchLogs(filter);
            }
            else
            {
                return Task.FromResult(new BrowserDataPage<ReconciliationLog>());
            }
        }

        protected override async Task OnRowRemoving(ReconciliationLog dataItem)
        {
            await ReconciliationLogService.Delete(new List<long>() { dataItem.Id.Value });
        }

        protected override async Task OnRowRemoving(IReadOnlyList<object> ids)
        {
            if (ids != null && ids.Count > 0)
            {
                var idss = ids.Select(obj => ((ReconciliationLog)obj).Id.Value).ToList();
                await ReconciliationLogService.Delete(idss);
            }
        }

        protected override Task OnRowUpdating(ReconciliationLog dataItem, ReconciliationLog newValues)
        {
            string link = NavLinkURI();
            return Task.CompletedTask;
        }

        protected override AbstractNewGridDataItem GetGridDataItem(int Position)
        {
            return new NewGridDataItem(GridColumns[Position], Position);
        }

        protected override object GetFieldValue(ReconciliationLog item, int grilleColumnPosition)
        {
            return GetPropertyValue(item, GridColumns[grilleColumnPosition].ColumnName);
        }


        private object GetPropertyValue(ReconciliationLog obj, string propName)
        {
            return obj.GetType().GetProperty(propName).GetValue(obj, null);
        }

        protected override async Task OnAfterRenderAsync(bool firstRender)
        {
            await base.OnAfterRenderAsync(firstRender);
            if (firstRender)
            {
                Filter?.Invoke(null);
            }
        }
    }
}
