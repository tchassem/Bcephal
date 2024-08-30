using Bcephal.Blazor.Web.Base.Services;
using Bcephal.Blazor.Web.Base.Shared.AbstractComponent;
using Bcephal.Blazor.Web.Planification.Services;
using Bcephal.Models.Base;
using Bcephal.Models.Grids.Filters;
using Bcephal.Models.Routines;
using DevExtreme.AspNet.Data;
using Microsoft.AspNetCore.Components;
using Microsoft.JSInterop;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Bcephal.Blazor.Web.Planification.Pages.Routine
{
    //[RouteAttribute("/browser-transformation-routine")]
    public class TransformationRoutineBrowser : AbstractGridComponent<TransformationRoutine, BrowserData>
    {
        [Inject]
        private TransformationRoutineService TRoutineService { get; set; }

        //[Inject]
        //private WebSocketAddress WebSocketAddress { get; set; }

        protected dynamic[] GridColumns => new[] {
                        new {CaptionName = AppState["Name"], ColumnWidth="15%", ColumnName = nameof(BrowserData.Name), ColumnType = typeof(string)},
                        new {CaptionName = AppState["Group"], ColumnWidth="10%", ColumnName = nameof(BrowserData.Group), ColumnType = typeof(BGroup)},
                        new {CaptionName = AppState["CreationDate"], ColumnWidth="auto", ColumnName = nameof(BrowserData.CreationDateTime), ColumnType = typeof(DateTime?)},
                        new {CaptionName = AppState["ModificationDate"], ColumnWidth="auto", ColumnName = nameof(BrowserData.ModificationDateTime), ColumnType = typeof(DateTime?)},
                        new {CaptionName = AppState["VisibleInShortcut"], ColumnWidth="20%", ColumnName = nameof(BrowserData.VisibleInShortcut), ColumnType = typeof(bool)},
                    };

        protected override int ItemsCount => GridColumns.Length;

        private object GetPropertyValue(BrowserData obj, string propName)
        {
            return obj.GetType().GetProperty(propName).GetValue(obj, null);
        }

        protected override async Task OnInitializedAsync()
        {
            await base.OnInitializedAsync();
            EditorRoute = Route.EDIT_TRANSFORMATION_ROUTINE;
            IsNavLink = true;
            NewButtonVisible = false;
            ClearFilterButtonVisible = false;
            EditButtonVisible = false;
            CanRefresh = true && !AppState.IsDashboard;
            DeleteButtonVisible = true;
            OnSelectionChangeHandler_ += ChechSelectionList;
            if (AppState.PrivilegeObserver != null && AppState.PrivilegeObserver.TransformationRoutineCreateAllowed && AppState.PrivilegeObserver.TransformationCreateAllowed)
            {
                await Task.Delay(TimeSpan.FromSeconds(1.2)).ContinueWith(t => AppState.CanCreate = true && !AppState.IsDashboard);
            }
            
        }

        //protected override void OnAfterRender(bool firstRender)
        //{
        //    base.OnAfterRender(firstRender);
        //    if (firstRender)
        //    {
        //        AppState.CanRun = true;
        //        AppState.RunHander += RunSelectedReco;
        //    }
        //}

        public override async ValueTask DisposeAsync()
        {
            if(AppState.CanRun)
            {
                AppState.CanRun = false;
                AppState.RunHander -= RunSelectedTRoutine;
                // AppState.Hander = null;
            }
            if (AppState.PrivilegeObserver != null && AppState.PrivilegeObserver.TransformationRoutineCreateAllowed && AppState.PrivilegeObserver.TransformationCreateAllowed)
            {
                AppState.CanCreate = false;
            }
            await base.DisposeAsync();
        }

        /// <summary>
        ///     Methode permettant de lancer l'exécution des Auto reco qui sont sélectionnés
        /// </summary>
        public async void RunSelectedTRoutine()
        {
            await JSRuntime.InvokeVoidAsync("console.log", "Try to run the selected routines !!");

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

        /// <summary>
        ///     Cette méthode sert à verifier qu'il y a des éléments sélectionnés dans la liste et que le bouton run n'est pas encore activé
        ///     et si c'est le cas, on affiche le bouton run à la toolbar ceci depend du fait que ce soit dans une modale ou pas
        ///     
        ///     Cette méthode est attachée au OnSelectionChangeHandler_ de AbstractGridComponent et est exécuté lorsque la liste change
        /// </summary>
        /// <param name="SelectedItems"></param>
        /// <param name="UnSelectedItems"></param>
        /// <returns></returns>
        private Task ChechSelectionList(List<long> SelectedItems, List<long> UnSelectedItems)
        {
            if (SelectedItems.Count > 0 && !AppState.CanRun)
            {
                AppState.CanRun = true;
                AppState.RunHander += RunSelectedTRoutine;
            }
            else if (SelectedItems.Count == 0 && AppState.CanRun)
            {
                AppState.CanRun = false;
                AppState.RunHander -= RunSelectedTRoutine;
            }
            
            return Task.CompletedTask;
        }

        protected override AbstractGridDataItem GetGridDataItem(int Position)
        {
            return new GridDataItem(GridColumns[Position], Position);
        }

        protected override object GetFieldValue(BrowserData item, int grilleColumnPosition)
        {
            return GetPropertyValue(item, GridColumns[grilleColumnPosition].ColumnName);
        }
        protected override string KeyFieldName()
        {
            return nameof(TransformationRoutine.Id);
        }

        protected override object KeyFieldValue(BrowserData item)
        {
            return item.Id;
        }

        protected override string NavLinkURI()
        {
            return Route.EDIT_TRANSFORMATION_ROUTINE;
        }

        protected override Task OnRowInserting(Dictionary<string, object> newValues)
        {
            return Task.CompletedTask;
        }

        protected override async Task OnRowRemoving(BrowserData dataItem)
        {
            await TRoutineService.Delete(new List<long>() { dataItem.Id.Value });
        }
        protected override async Task OnRowRemoving(List<long> ids)
        {
            if (ids != null && ids.Count > 0)
            {
                await TRoutineService.Delete(ids);
            }
        }

        protected override Task OnRowUpdating(BrowserData dataItem, Dictionary<string, object> newValues)
        {
            return Task.CompletedTask;
        }

        protected override async Task SearchRows(BrowserDataFilter filter, BrowserDataPage<BrowserData> page_, DataSourceLoadOptionsBase options)
        {
            BrowserDataPage<BrowserData> page = await TRoutineService.Search(filter);
            foreach (BrowserData row in page.Items)
            {
                page_.Items.Add(row);
            }

            page_.CurrentPage = page.CurrentPage;
            page_.PageCount = page.PageCount;
            page_.PageFirstItem = page.PageFirstItem;
            page_.PageLastItem = page.PageLastItem;
            page_.PageSize = page.PageSize;
            page_.TotalItemCount = page.TotalItemCount;
        }

        protected override string FormatDateCellValue(string format, Object obj)
        {
            return base.FormatDateCellValue("dd/MM/yyyy hh:mm:ss", obj);
        }

    }
}
