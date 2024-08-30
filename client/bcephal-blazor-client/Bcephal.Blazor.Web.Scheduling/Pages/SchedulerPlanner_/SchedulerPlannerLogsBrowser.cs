using Bcephal.Blazor.Web.Base.Services;
using Bcephal.Blazor.Web.Base.Shared.AbstractComponent;
using Bcephal.Blazor.Web.Scheduling.Services;
using Bcephal.Models.Base;
using Bcephal.Models.Grids;
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
    public partial class SchedulerPlannerLogsBrowser : AbstractNewGridComponent<SchedulerPlannerLog, SchedulerPlannerLogBrowserData>
    {
        protected dynamic[] GridColumns => new[] {
                        new {CaptionName = AppState["Name"] ,ColumnWidth="7%", ColumnName = nameof(SchedulerPlannerLogBrowserData.Name), ColumnType = typeof(string)},
                        new {CaptionName = "#"+AppState["Step"] ,ColumnWidth="auto", ColumnName = nameof(SchedulerPlannerLogBrowserData.Steps), ColumnType = typeof(int)},
                        new {CaptionName = AppState["User"] ,ColumnWidth="auto", ColumnName = nameof(SchedulerPlannerLogBrowserData.Username), ColumnType = typeof(string)},
                        new {CaptionName = AppState["Mode"] ,ColumnWidth="auto", ColumnName = nameof(SchedulerPlannerLogBrowserData.Mode), ColumnType = typeof(string)},
                        new {CaptionName = AppState["StartDate"] ,ColumnWidth="10%", ColumnName = nameof(SchedulerPlannerLogBrowserData.CreationDateTime), ColumnType = typeof(DateTime?)},
                        new {CaptionName = AppState["EndDate"] ,ColumnWidth="10%", ColumnName = nameof(SchedulerPlannerLogBrowserData.EndDateTime), ColumnType = typeof(DateTime?)},
                        new {CaptionName = AppState["Status"] ,ColumnWidth="auto", ColumnName = nameof(SchedulerPlannerLogBrowserData.Status), ColumnType = typeof(string)},
                        new {CaptionName = AppState["Message"] ,ColumnWidth="auto", ColumnName = nameof(SchedulerPlannerLogBrowserData.Message), ColumnType = typeof(string)},
                    };

        protected override int ItemsCount => GridColumns.Length;

        [Parameter] public long? SchedulerPlannerId_ { get; set; }

        [Parameter] public long? SchedulerPlannerLogId_ { get; set; }

        [Parameter] public EventCallback<long?> SchedulerPlannerLogId_Changed { get; set; }

        public long? SchedulerPlannerLogIdBinding_
        {
            get { return SchedulerPlannerLogId_; }
            set
            {
                SchedulerPlannerLogId_ = value;
                if (SchedulerPlannerLogId_Changed.HasDelegate)
                {
                    SchedulerPlannerLogId_Changed.InvokeAsync(SchedulerPlannerLogId_);
                }
            }
        }
        protected override void OnSelectedDataItemChanged(object selection)
        {
            if (selection != null)
            {
                base.OnSelectedDataItemChanged(selection);
                SchedulerPlannerLogIdBinding_ = ((SchedulerPlannerLogBrowserData)selection).Id;
            }
        }

        [Inject] 
        public SchedulerPlannerLogService SchedulePlannerLogService { get; set; }


        public virtual SchedulerPlannerLogService GetService()
        {
            return SchedulePlannerLogService;
        }

        protected override async Task OnInitializedAsync()
        {
            await base.OnInitializedAsync();
            IsNavLink = false;
            NewButtonVisible = false;
            ClearFilterButtonVisible = false;
            DeleteButtonVisible = true;
            EditButtonVisible = false;
            SelectionMode = GridSelectionMode.Single;
        }

        protected override SchedulerPlannerLogBrowserData NewItem()
        {
            return null;
        }

        private object GetPropertyValue(SchedulerPlannerLogBrowserData obj, string propName)
        {
            return obj.GetType().GetProperty(propName).GetValue(obj, null);
        }

        protected override object GetFieldValue(SchedulerPlannerLogBrowserData item, int grilleColumnPosition)
        {
            return GetPropertyValue(item, GridColumns[grilleColumnPosition].ColumnName);
        }

        protected override AbstractNewGridDataItem GetGridDataItem(int Position)
        {
            return new NewGridDataItem(GridColumns[Position], Position);
        }

        protected override string KeyFieldName()
        {
            return nameof(SchedulerPlannerLogBrowserData.Id);
        }

        protected override object KeyFieldValue(SchedulerPlannerLogBrowserData item)
        {
            SchedulerPlannerLogIdBinding_ = item.Id;
            return item.Id;
        }

        protected override string NavLinkURI()
        {
            return Route.SCHEDULED_PLANNER;
        }

        protected override Task OnRowInserting(SchedulerPlannerLogBrowserData newValues)
        {
            return Task.CompletedTask;
        }

        protected override async Task OnRowRemoving(SchedulerPlannerLogBrowserData dataItem)
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

        protected override async Task OnRowUpdating(SchedulerPlannerLogBrowserData dataItem, SchedulerPlannerLogBrowserData editModel)
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

        protected override Task<BrowserDataPage<SchedulerPlannerLogBrowserData>> SearchRows(BrowserDataFilter filter)
        {
            if (SchedulerPlannerId_.HasValue)
            {
                filter.GroupId = SchedulerPlannerId_;
                return GetService().Search(filter);
            }
            return GetService().Search(filter);
        }

        GrilleColumn GetItemsByPosition(int position)
        {
            foreach (var item in GridColumns)
            {
                if (item.Position == position)
                {
                    return item;
                }
            }
            return null;
        }

        protected override string FormatDateCellValue(string format, Object obj)
        {
            return base.FormatDateCellValue("dd/MM/yyyy hh:mm:ss", obj);

        }

        protected override void OnCustomHtmlElementDataCellDecoration(GridCustomizeElementEventArgs e)
        {
            if (GridElementType.DataCell.Equals(e.ElementType) && e.Column.Caption == GridColumns[GridColumns.Length - 2].CaptionName)
            {
                SchedulerPlannerLogBrowserData dataItem = (SchedulerPlannerLogBrowserData)e.Grid.GetDataItem(e.VisibleIndex);
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
