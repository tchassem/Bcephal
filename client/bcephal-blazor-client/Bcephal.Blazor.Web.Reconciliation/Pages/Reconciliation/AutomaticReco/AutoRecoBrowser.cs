using Bcephal.Blazor.Web.Base.Services;
using Bcephal.Blazor.Web.Base.Shared.AbstractComponent;
using Bcephal.Blazor.Web.Sourcing.Services;
using Bcephal.Models.Base;
using Bcephal.Models.Grids.Filters;
using Bcephal.Models.Reconciliation;
using DevExtreme.AspNet.Data;
using Microsoft.AspNetCore.Components;
using Microsoft.JSInterop;
using System;
using System.Linq;
using System.Collections.Generic;
using System.Threading.Tasks;

namespace Bcephal.Blazor.Web.Reconciliation.Pages.Reconciliation.AutomaticReco
{
    //[RouteAttribute("/reconciliation/auto-reco-browser")]
    public class AutoRecoBrowser : AbstractNewGridComponent<AutoReco, RecoBrowserData>
    {

        [Inject]
        private AutoRecoService AutoRecoService { get; set; }

        [Inject]
        public IToastService ToastService { get; set; }

        [Inject]
        private WebSocketAddress WebSocketAddress { get; set; }

        // Ce parametre sert à s'assurer si l'on a ouvert cette vue dans une modale
        // afin de ne pas gérer le bouton Run de la Toolbar
        [Parameter]
        public bool IsOpenInModal { get; set; } = false;
        [Parameter]
        public long? ModelId { get; set; }

        protected dynamic[] GridColumns => new[] {
                        new {CaptionName = AppState["Name"] ,ColumnWidth="15%", ColumnName = nameof(RecoBrowserData.Name), ColumnType = typeof(string)},
                        new {CaptionName = AppState["Group"] ,ColumnWidth="10%", ColumnName = nameof(RecoBrowserData.Group), ColumnType = typeof(BGroup)},
                        new {CaptionName = AppState["CurrentlyExecuting"] ,ColumnWidth="15%", ColumnName = nameof(RecoBrowserData.CurrentlyExecuting), ColumnType = typeof(bool)},
                        new {CaptionName = AppState["CreationDate"] ,ColumnWidth="auto", ColumnName = nameof(RecoBrowserData.CreationDateTime), ColumnType = typeof(DateTime?)},
                        new {CaptionName = AppState["ModificationDate"] ,ColumnWidth="auto", ColumnName = nameof(RecoBrowserData.ModificationDateTime), ColumnType = typeof(DateTime?)},
                        new {CaptionName = AppState["VisibleInShortcut"] ,ColumnWidth="20%", ColumnName = nameof(RecoBrowserData.VisibleInShortcut), ColumnType = typeof(bool)},
                    };

        protected override int ItemsCount => GridColumns.Length;

        private object GetPropertyValue(RecoBrowserData obj, string propName)
        {
            return obj.GetType().GetProperty(propName).GetValue(obj, null);
        }
        
        protected override async Task OnInitializedAsync()
        {
            await base.OnInitializedAsync();
            EditorRoute = Route.RECONCILIATION_AUTO_FORM;
            IsNavLink = true;
            NewButtonVisible = false;
            ClearFilterButtonVisible = false;
            EditButtonVisible = false;
            OnSelectionChangeHandler_ += ChechSelectionList;
            //if (AppState.PrivilegeObserver != null && AppState.PrivilegeObserver.ReconciliationAutoRecoCreateAllowed && AppState.PrivilegeObserver.ReconciliationCreateAllowed)
            //{
                DeleteButtonVisible = true;
                await Task.Delay(TimeSpan.FromSeconds(1.2)).ContinueWith(t => AppState.CanCreate = true && !AppState.IsDashboard);
                AppState.CanRefresh = true && !AppState.IsDashboard;
            //}
        }


        public override async ValueTask DisposeAsync()
        {
            if (!IsOpenInModal && AppState.CanRun)
            {
                AppState.CanRun = false;
                AppState.RunHander -= RunSelectedAutoReco;
                // AppState.Hander = null;
            }

            AppState.CreateHander = null;
            //if (AppState.PrivilegeObserver != null && AppState.PrivilegeObserver.ReconciliationAutoRecoCreateAllowed && AppState.PrivilegeObserver.ReconciliationCreateAllowed)
            //{
                AppState.CanCreate = false;
            //}
            await base.DisposeAsync();
        }

        public async void CallBackCreateAutoReco(object sender, object message)
        {
            await JSRuntime.InvokeVoidAsync("console.log", "call of create AutoReco callback : ");
            if (message != null && !string.IsNullOrWhiteSpace(message.ToString()))
            {

                ////Console.WriteLine(message.ToString());

            }
        }

        /// <summary>
        ///     Methode permettant de lancer l'exécution des Auto reco qui sont sélectionnés
        /// </summary>
        public async void RunSelectedAutoReco()
        {
            await JSRuntime.InvokeVoidAsync("console.log", "Running selected automatic reconciliations !!");
            
            try
            {
                if (ModelId.HasValue)
                {
                    await JSRuntime.InvokeVoidAsync("console.log", "Manually running selected automatic reconciliations !!");
                    SocketJS Socket = new SocketJS(WebSocketAddress, CallBackCreateAutoReco, JSRuntime, AppState, true);
                    bool valueClose = false;
                    bool valueError = false;
                    Socket.CloseHandler += () =>
                    {
                        if (!valueClose && !valueError)
                        {
                            AppState.HideLoadingStatus();
                            ToastService.ShowSuccess(AppState["AutoRecoSuccess.create.message"], AppState["Loader"]);
                            valueClose = true;

                        }
                    };

                    Socket.ErrorHandler += (errorMessage) =>
                    {
                        if (!valueError)
                        {
                            AppState.HideLoadingStatus();
                            ToastService.ShowError((string)errorMessage, AppState["Error"]);
                            valueError = true;
                        }
                    };
                    Socket.SendHandler +=  () =>
                    {
                        Socket.FullyProgressbar.FullBase = false;
                        AppState.CanLoad = false;
                        foreach (var autoRecoId in GetSelectionDataItemsIds())
                        {
                            AppState.HideLoadingStatus();
                            string data = AutoRecoService.Serialize(new AutoReco() { Id = autoRecoId, RecoId = ModelId.Value });
                            Socket.send(data);
                            
                        }
                    };
                    await AutoRecoService.ConnectSocketJS(Socket, "/run");
                    AppState.HideLoadingStatus();
                }
                    
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
        private Task ChechSelectionList(List<long> SelectedItems)
        {
            if (!IsOpenInModal )
            {
                if (SelectedItems.Count > 0 && !AppState.CanRun)
                {
                    AppState.CanRun = true;
                    AppState.RunHander += RunSelectedAutoReco;
                }
                else if (SelectedItems.Count == 0 && AppState.CanRun)
                {
                    AppState.CanRun = false;
                    AppState.RunHander -= RunSelectedAutoReco;
                }
            }
            return Task.CompletedTask;
        }

        protected override AbstractNewGridDataItem GetGridDataItem(int Position)
        {
            return new NewGridDataItem(GridColumns[Position], Position);
        }

        protected override object GetFieldValue(RecoBrowserData item, int grilleColumnPosition)
        {
            return GetPropertyValue(item, GridColumns[grilleColumnPosition].ColumnName);
        }
        protected override string KeyFieldName()
        {
            return nameof(AutoReco.Id);
        }

        protected override object KeyFieldValue(RecoBrowserData item)
        {
            return item.Id;
        }

        protected override string NavLinkURI()
        {
            return Route.RECONCILIATION_AUTO_FORM;
        }

        protected override Task OnRowInserting(RecoBrowserData newValues)
        {
            return Task.CompletedTask;
        }

        protected override async Task OnRowRemoving(RecoBrowserData dataItem)
        {
            await AutoRecoService.Delete(new List<long>() { dataItem.Id.Value });
        }
        protected override async Task OnRowRemoving(IReadOnlyList<object> ids)
        {
            if (ids != null && ids.Count > 0)
            {
                var idss = ids.Select(obj => ((BrowserData)obj).Id.Value).ToList();
                await AutoRecoService.Delete(idss);
            }
        }

        protected override Task OnRowUpdating(RecoBrowserData dataItem, RecoBrowserData newValues)
        {
            return Task.CompletedTask;
        }

        protected override Task<BrowserDataPage<RecoBrowserData>> SearchRows(BrowserDataFilter filter)
        {
            if (ModelId.HasValue)
            {
                filter.GroupId = ModelId.Value;
            }
            return AutoRecoService.Search(filter);
        }

        protected override string FormatDateCellValue(string format, Object obj)
        {
            return base.FormatDateCellValue("dd/MM/yyyy hh:mm:ss", obj);

        }

    }
}
