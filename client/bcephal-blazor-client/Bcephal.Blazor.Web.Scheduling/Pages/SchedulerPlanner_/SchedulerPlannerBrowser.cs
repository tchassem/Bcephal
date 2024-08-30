using Bcephal.Blazor.Web.Base.Services;
using Bcephal.Blazor.Web.Base.Shared.AbstractComponent;
using Bcephal.Blazor.Web.Scheduling.Services;
using Bcephal.Models.Base;
using Bcephal.Models.Grids.Filters;
using Bcephal.Models.Planners;
using Microsoft.AspNetCore.Components;
using Microsoft.JSInterop;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Bcephal.Blazor.Web.Scheduling.Pages.SchedulerPlanner_
{
    public class SchedulerPlannerBrowser : AbstractNewGridComponent<SchedulerPlanner, SchedulerPlannerBrowserData>
    {
        protected dynamic[] GridColumns => new[] {
                        new {CaptionName = AppState["Name"], ColumnWidth="15%", ColumnName = nameof(SchedulerPlannerBrowserData.Name), ColumnType = typeof(string)},
                        new {CaptionName = AppState["Group"], ColumnWidth="10%", ColumnName = nameof(SchedulerPlannerBrowserData.Group), ColumnType = typeof(BGroup)},
                        new {CaptionName = AppState["CreationDate"], ColumnWidth="auto", ColumnName = nameof(SchedulerPlannerBrowserData.CreationDateTime), ColumnType = typeof(DateTime?)},
                        new {CaptionName = AppState["ModificationDate"], ColumnWidth="auto", ColumnName = nameof(SchedulerPlannerBrowserData.ModificationDateTime), ColumnType = typeof(DateTime?)},
                        new {CaptionName = AppState["VisibleInShortcut"], ColumnWidth="20%", ColumnName = nameof(SchedulerPlannerBrowserData.VisibleInShortcut), ColumnType = typeof(bool)},
                    };

        [Inject]
        public SchedulerPlannerService schedulerPlannerService { get; set; }

        [Parameter]
        public bool IsOpenInModal { get; set; } = false;

        public virtual SchedulerPlannerService GetService()
        {
            return schedulerPlannerService;
        }

        protected override int ItemsCount => GridColumns.Length;

        private object GetPropertyValue(BrowserData obj, string propName)
        {
            return obj.GetType().GetProperty(propName).GetValue(obj, null);
        }

        protected override async Task OnInitializedAsync()
        {
            await base.OnInitializedAsync();
            EditorRoute = Route.SCHEDULED_PLANNER;
            IsNavLink = true;
            NewButtonVisible = false;
            ClearFilterButtonVisible = false;
            EditButtonVisible = false;
            CanRefresh = true && !AppState.IsDashboard;
            DeleteButtonVisible = true;
            OnSelectionChangeHandler_ += ChechSelectionList;
            //if (AppState.PrivilegeObserver != null && AppState.PrivilegeObserver.TransformationRoutineCreateAllowed && AppState.PrivilegeObserver.TransformationCreateAllowed)
            //{
            //    await Task.Delay(TimeSpan.FromSeconds(1.2)).ContinueWith(t => AppState.CanCreate = true && !AppState.IsDashboard);
            //}
            await Task.Delay(TimeSpan.FromSeconds(1.2)).ContinueWith(t => AppState.CanCreate = true && !AppState.IsDashboard);

        }
        public override async ValueTask DisposeAsync()
        {
            if (AppState.CanRun)
            {
                AppState.CanRun = false;
                AppState.RunHander -= RunSelectedSchedulerPlanner;
            }
            //if (AppState.PrivilegeObserver != null && AppState.PrivilegeObserver.TransformationRoutineCreateAllowed && AppState.PrivilegeObserver.TransformationCreateAllowed)
            //{
            //    AppState.CanCreate = false;
            //}
            AppState.CanCreate = false;
            await base.DisposeAsync();
        }

        public async void RunSelectedSchedulerPlanner()
        {
            await JSRuntime.InvokeVoidAsync("console.log", "Try to run the selected scheduler planner !!");

            try
            {
                //if (ModelId.HasValue)
                //{
                //    await JSRuntime.InvokeVoidAsync("console.log", "Try to manually run an automatic reconciliation");
                //    SocketJS Socket = new SocketJS(WebSocketAddress, null, JSRuntime, AppState, true);
                //    Socket.SendHandler += async () =>
                //    {
                //        Socket.FullyProgressbar.FullBase = false;
                //        AppState.CanLoad = false;
                //        foreach (var autoRecoId in SelectedItems)
                //        {
                //            string data = AutoRecoService.Serialize(new AutoReco() { Id = autoRecoId, RecoId = ModelId.Value });
                //            Socket.send(data);
                //            await AutoRecoService.ConnectSocketJS(Socket, "/run");
                //        }
                //    };
                //}

            }
            catch (Exception ex)
            {
                Error.ProcessError(ex);
            }
            finally
            {
                StateHasChanged();
            }
        }

        private Task ChechSelectionList(List<long> SelectedItems)
        {
            if (!IsOpenInModal)
            {
                if (SelectedItems.Count > 0 && !AppState.CanRun)
                {
                    AppState.CanRun = true;
                    AppState.RunHander += RunSelectedSchedulerPlanner;
                }
                else if (SelectedItems.Count == 0 && AppState.CanRun)
                {
                    AppState.CanRun = false;
                    AppState.RunHander -= RunSelectedSchedulerPlanner;
                }
            }
            return Task.CompletedTask;
        }

        protected override AbstractNewGridDataItem GetGridDataItem(int Position)
        {
            return new NewGridDataItem(GridColumns[Position], Position);
        }

        protected override object GetFieldValue(SchedulerPlannerBrowserData item, int grilleColumnPosition)
        {
            return GetPropertyValue(item, GridColumns[grilleColumnPosition].ColumnName);
        }

        protected override string KeyFieldName()
        {
            return nameof(SchedulerPlanner.Id);
        }

        protected override object KeyFieldValue(SchedulerPlannerBrowserData item)
        {
            return item.Id;
        }

        protected override string NavLinkURI()
        {
            return Route.SCHEDULED_PLANNER;
        }

        protected override Task OnRowInserting(SchedulerPlannerBrowserData newValues)
        {
            return Task.CompletedTask;
        }

        protected override async Task OnRowRemoving(SchedulerPlannerBrowserData dataItem)
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

        protected override async Task OnRowUpdating(SchedulerPlannerBrowserData dataItem, SchedulerPlannerBrowserData editModel)
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

        protected override Task<BrowserDataPage<SchedulerPlannerBrowserData>> SearchRows(BrowserDataFilter filter)
        {
            return GetService().Search<SchedulerPlannerBrowserData>(filter);
        }

        protected override string FormatDateCellValue(string format, Object obj)
        {
            return base.FormatDateCellValue("dd/MM/yyyy hh:mm:ss", obj);

        }
    }
}