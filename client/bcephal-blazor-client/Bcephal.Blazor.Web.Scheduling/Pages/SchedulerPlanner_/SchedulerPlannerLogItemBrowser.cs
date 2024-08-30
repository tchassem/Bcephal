using Bcephal.Blazor.Web.Base.Shared.AbstractComponent;
using Bcephal.Blazor.Web.Scheduling.Services;
using Bcephal.Models.Base;
using Bcephal.Models.Grids.Filters;
using Bcephal.Models.Planners;
using DevExpress.Blazor;
using Microsoft.AspNetCore.Components;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Bcephal.Blazor.Web.Scheduling.Pages.SchedulerPlanner_
{
    public class SchedulerPlannerLogItemBrowser : AbstractNewGridComponent<SchedulerPlannerLogItem, SchedulerPlannerLogItemBrowserData>
    {


        protected dynamic[] GridColumns => new[] {
                        new {CaptionName = AppState["Position"] ,ColumnWidth="35%",  ColumnName = nameof(SchedulerPlannerLogItemBrowserData.Position), ColumnType = typeof(int)},
                        new {CaptionName = AppState["Type"] ,ColumnWidth="5%", ColumnName = nameof(SchedulerPlannerLogItemBrowserData.Type), ColumnType = typeof(string)},
                        new {CaptionName = AppState["Name"] ,ColumnWidth="7%", ColumnName = nameof(SchedulerPlannerLogItemBrowserData.Name), ColumnType = typeof(string)},
                        new {CaptionName = AppState["StartDate"] ,ColumnWidth="7%", ColumnName = nameof(SchedulerPlannerLogItemBrowserData.CreationDateTime), ColumnType = typeof(DateTime?)},
                        new {CaptionName = AppState["EndDate"],ColumnWidth="auto", ColumnName = nameof(SchedulerPlannerLogItemBrowserData.EndDateTime), ColumnType = typeof(DateTime?)},
                        new {CaptionName = AppState["Status"],ColumnWidth="auto", ColumnName = nameof(SchedulerPlannerLogItemBrowserData.Status), ColumnType = typeof(string)},
                        new {CaptionName = AppState["Message"],ColumnWidth="auto", ColumnName = nameof(SchedulerPlannerLogItemBrowserData.Message), ColumnType = typeof(string)},
                    };

        protected override int ItemsCount => GridColumns.Length;

        private object GetPropertyValue(SchedulerPlannerLogItemBrowserData obj, string propName)
        {
            return obj.GetType().GetProperty(propName).GetValue(obj, null);
        }

        public long? SchedulerPlannerLogId_ { get; set; }

        [Parameter]
        public EventCallback<long?> SchedulerPlannerLogIdChanged { get; set; }

        [Parameter]
        public long? SchedulerPlannerLogId
        {
            get
            {
                return SchedulerPlannerLogId_;
            }
            set
            {
                SchedulerPlannerLogId_ = value;
                AppState.ShowLoadingStatus();
                InvokeAsync(RefreshGrid_);
                AppState.HideLoadingStatus();
            }
        }

        [Inject]
        public SchedulerPlannerLogItemService SchedulerPlannerLogItemService { get; set; }

        public virtual SchedulerPlannerLogItemService GetService()
        {
            return SchedulerPlannerLogItemService;
        }
        public void Dispose()
        {
            AppState.Hander = null;
        }

        protected override void OnInitialized()
        {
            base.OnInitialized();
            IsNavLink = false;
            NewButtonVisible = false;
            ClearFilterButtonVisible = false;
            EditButtonVisible = false;
            DeleteButtonVisible = false;
            SelectionMode = DevExpress.Blazor.GridSelectionMode.Single;

        }
        protected override SchedulerPlannerLogItemBrowserData NewItem()
        {
            return null;
        }

        protected override AbstractNewGridDataItem GetGridDataItem(int Position)
        {
            return new NewGridDataItem(GridColumns[Position], Position);
        }

        protected override object GetFieldValue(SchedulerPlannerLogItemBrowserData item, int grilleColumnPosition)
        {
            return GetPropertyValue(item, GridColumns[grilleColumnPosition].ColumnName);
        }

        protected override string KeyFieldName()
        {
            return nameof(SchedulerPlannerLogItemBrowserData.Id);
        }
        protected override object KeyFieldValue(SchedulerPlannerLogItemBrowserData item)
        {
            return item.Id;
        }
        protected override string NavLinkURI()
        {

            return null;
        }
        protected override Task OnRowInserting(SchedulerPlannerLogItemBrowserData newValues)
        {
            return Task.CompletedTask;
        }

        protected override async Task OnRowRemoving(SchedulerPlannerLogItemBrowserData dataItem)
        {
            await GetService().Delete(new List<long>() { dataItem.Id.Value });
        }
        protected override async Task OnRowRemoving(IReadOnlyList<object> ids)
        {
            if (ids != null && ids.Count > 0)
            {
                var idss = ids.Select(obj => ((SchedulerPlannerBrowserData)obj).Id.Value).ToList();
                await GetService().Delete(idss);
            }
        }

        protected async override Task OnRowUpdating(SchedulerPlannerLogItemBrowserData dataItem, SchedulerPlannerLogItemBrowserData newValues)
        {
            try
            {
                string link = NavLinkURI();
                if (link.Trim().EndsWith("/"))
                {
                    link += dataItem.Id;
                }
                else
                {
                    link += "/" + dataItem.Id;
                }
                await AppState.NavigateTo(link);
            }
            catch (Exception ex)
            {
                Error.ProcessError(ex);
            }
        }

        protected override string FormatDateCellValue(string format, Object obj)
        {
            return base.FormatDateCellValue("dd/MM/yyyy hh:mm:ss", obj);

        }

        protected override Task<BrowserDataPage<SchedulerPlannerLogItemBrowserData>> SearchRows(BrowserDataFilter filter)
        {
            if (SchedulerPlannerLogId.HasValue)
            {
                filter.GroupId = SchedulerPlannerLogId;
                return GetService().Search(filter);
            }
            return Task.FromResult(new BrowserDataPage<SchedulerPlannerLogItemBrowserData>());
        }


        protected override void OnCustomHtmlElementDataCellDecoration(GridCustomizeElementEventArgs e)
        {
            if (GridElementType.DataCell.Equals(e.ElementType) && e.Column.Caption == GridColumns[GridColumns.Length - 2].CaptionName)
            {
                SchedulerPlannerLogItemBrowserData dataItem = (SchedulerPlannerLogItemBrowserData)e.Grid.GetDataItem(e.VisibleIndex);
                if (dataItem != null)
                {

                    if (RunStatus.ENDED.Equals(dataItem.RunStatus))
                    {
                        e.Style += "background-color: #00945E; color:white;";
                    }
                    if (RunStatus.IN_PROGRESS.Equals(dataItem.RunStatus))
                    {
                        e.Style += "background-color: #3395ff; color:white;";
                    }
                    if (RunStatus.ERROR.Equals(dataItem.RunStatus))
                    {
                        e.Style += "background-color: #ff0000; color:white;";
                    }
                    if (RunStatus.STOPPED.Equals(dataItem.RunStatus))
                    {
                        e.Style += "background-color: ccb1b4; color:white;";
                    }
                }
            }
        }
    }
}
