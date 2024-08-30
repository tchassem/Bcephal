using Bcephal.Blazor.Web.Base.Services;
using Bcephal.Blazor.Web.Base.Shared.AbstractComponent;
using Bcephal.Blazor.Web.Billing.Services;
using Bcephal.Models.Base;
using Bcephal.Models.Billing.Model;
using Bcephal.Models.Grids.Filters;
using DevExtreme.AspNet.Data;
using Microsoft.AspNetCore.Components;
using Microsoft.JSInterop;
using System;
using System.Collections.Generic;
using System.Threading.Tasks;

namespace Bcephal.Blazor.Web.Billing.Pages.Billing.Models
{
    //[RouteAttribute("/billing/model-browser")]
    public class ModelBrowser : AbstractGridComponent<BillingModel, BrowserData>
    {
        protected dynamic[] GridColumns => new[] {
            new {CaptionName = AppState["Name"], ColumnWidth="19%", ColumnName = nameof(BrowserData.Name), ColumnType = typeof(string)},
            new {CaptionName = AppState["Group"], ColumnWidth="15%", ColumnName = nameof(BrowserData.Group), ColumnType = typeof(BGroup)},
            new {CaptionName = AppState["CreationDate"], ColumnWidth="22%", ColumnName = nameof(BrowserData.CreationDateTime), ColumnType = typeof(DateTime?)},
            new {CaptionName = AppState["ModificationDate"], ColumnWidth="23%", ColumnName = nameof(BrowserData.ModificationDateTime), ColumnType = typeof(DateTime?)},
            new {CaptionName = AppState["VisibleInShortcut"], ColumnWidth="20%", ColumnName = nameof(BrowserData.VisibleInShortcut), ColumnType = typeof(bool)},
        };

        protected override int ItemsCount => GridColumns.Length;

        private object GetPropertyValue(BrowserData obj, string propName)
        {
            return obj.GetType().GetProperty(propName).GetValue(obj, null);
        }

        [Inject]
        public BillingModelService BillingModelService { get; set; }

        [Inject] public IToastService ToastService { get; set; }

        [Inject] private WebSocketAddress WebSocketAddress { get; set; }

        [Parameter]
        public EventCallback<bool> OkBtnEnableCallback { get; set; }

        private long SelectedRowId { get; set; }

        protected override async Task OnInitializedAsync()
        {
            await base.OnInitializedAsync();
            EditorRoute = Route.BILLING_MODEL_FORM;
            IsNavLink = true;
            NewButtonVisible = false;
            ClearFilterButtonVisible = false;
            EditButtonVisible = false;
            DeleteButtonVisible = true;
            AppState.CanRun = false;
            AppState.RunHander += RunBillingModel;
            OnSelectionChangeHandler_ += OnSelectModelItems;

            if (AppState.PrivilegeObserver != null && AppState.PrivilegeObserver.BillingModelCreateAllowed && AppState.PrivilegeObserver.BillingModelCreateAllowed)
            {
                await Task.Delay(TimeSpan.FromSeconds(1.2)).ContinueWith(t => AppState.CanCreate = true && !AppState.IsDashboard);
            }
        }

        public override async ValueTask DisposeAsync()
        {

            AppState.CreateHander = null;
            if (AppState.PrivilegeObserver != null && AppState.PrivilegeObserver.BillingModelCreateAllowed && AppState.PrivilegeObserver.BillingModelCreateAllowed)
            {
                AppState.CanCreate = false;
            }
            AppState.CanRun = false;
            AppState.RunHander -= RunBillingModel;
            OnSelectionChangeHandler_ -= OnSelectModelItems;
            await base.DisposeAsync();
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
            return nameof(BillingModel.Id);
            //return nameof(BrowserData.Id);
        }

        protected override object KeyFieldValue(BrowserData item)
        {
            return item.Id;
        }

        protected override string NavLinkURI()
        {
            return Route.BILLING_MODEL_FORM;
        }

        protected override Task OnRowInserting(Dictionary<string, object> newValues)
        {
            return Task.CompletedTask;
        }

        protected override async Task OnRowRemoving(BrowserData dataItem)
        {
            await BillingModelService.Delete(new List<long>() { dataItem.Id.Value });
        }

        protected override async Task OnRowRemoving(List<long> ids)
        {
            if (ids != null && ids.Count > 0)
            {
                await BillingModelService.Delete(ids);
            }
        }

        protected async override Task OnRowUpdating(BrowserData dataItem, Dictionary<string, object> newValues)
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

        protected override async Task SearchRows(BrowserDataFilter filter, BrowserDataPage<BrowserData> page_, DataSourceLoadOptionsBase options)
        {
            //BrowserDataPage<BillingModel> page = await BillingModelService.GetAllMockAsync();
            BrowserDataPage<BrowserData> page = await BillingModelService.Search(filter);
            //await JSRuntime.InvokeVoidAsync("console.log", "Show all BillingModels : ", page.Items);
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

        private Task OnSelectModelItems(List<long> SelectedItems, List<long> UnSelectedItems)
        {
            if (SelectedItems.Count == 1 && UnSelectedItems.Count == 0)
            {
                foreach (long selectedId in SelectedItems)
                {
                    SelectedRowId = selectedId;
                }
                AppState.CanRun = true;
                OkBtnEnableCallback.InvokeAsync(true);
            }
            else
            {
                SelectedRowId = 0;
                AppState.CanRun = false;
                OkBtnEnableCallback.InvokeAsync(false);
            }
            return Task.CompletedTask;
        }

        public async void RunBillingModel()
        {
            try
            {
                if(SelectedRowId > 0)
                {
                    AppState.ShowLoadingStatus();
                    if (AppState.Update)
                    {
                        AppState.Save();
                    }

                    await JSRuntime.InvokeVoidAsync("console.log", "Attempt to handling log of billing model!");
                    SocketJS Socket = new SocketJS(WebSocketAddress, null, JSRuntime, AppState, true);
                    bool valueClose = false;
                    bool valueError = false;
                    Socket.CloseHandler += () =>
                    {
                        if (!valueClose && !valueError)
                        {
                            AppState.HideLoadingStatus();
                            ToastService.ShowSuccess(AppState["billing.model.run.success"], AppState["Loader"]);
                            valueClose = true;
                        }
                    };
                    Socket.ErrorHandler += (errorMessage) =>
                    {
                        if (!valueError)
                        {
                            AppState.HideLoadingStatus();
                            ToastService.ShowError((string)errorMessage, AppState["billing.model.run.error"]);
                            valueError = true;
                        }
                    };
                    Socket.SendHandler += () =>
                    {
                        Socket.FullyProgressbar.FullBase = true;
                        AppState.CanLoad = false;
                        string data = BillingModelService.Serialize(SelectedRowId);
                        Socket.send(data);
                        AppState.HideLoadingStatus();
                    };

                    await BillingModelService.ConnectSocketJS(Socket, "");
                }
            }
            catch (Exception ex)
            {
                Error.ProcessError(ex);
            }
            finally
            {
                AppState.HideLoadingStatus();
                StateHasChanged();
            }
        }

        /*protected override async Task BuildFilter(BrowserDataFilter filter, DataSourceLoadOptionsBase options)
        {

            await JSRuntime.InvokeVoidAsync("console.log", "Call LoadCustomData");
            await JSRuntime.InvokeVoidAsync("console.log", "Show Filter Options : ", options);

            if (options.Filter?.Count > 0)
            {
                await JSRuntime.InvokeVoidAsync("console.log", "Show Filter content : ", (options.Filter[0]));
                //filter.ColumnFilters.Name = (options.Filter[0] as IList<dynamic>)[0] as string;
                //filter.ColumnFilters.Operation = (options.Filter[0] as IList<dynamic>)[1] as string;
                //filter.ColumnFilters.Value = (options.Filter[0] as IList<dynamic>)[2] as string;

                string name = (options.Filter[0] as IList<dynamic>)[0] as string;
                string operation = (options.Filter[0] as IList<dynamic>)[1] as string;
                string value = (options.Filter[0] as IList<dynamic>)[2] as string;

                filter.Criteria = $"{name} {operation} {value}";

                await JSRuntime.InvokeVoidAsync("console.log", "Show Filter Options cn : ", name);
                await JSRuntime.InvokeVoidAsync("console.log", "Show Filter Options va : ", value);
                await JSRuntime.InvokeVoidAsync("console.log", "Call filter 0 : ", filter);
                // à garder pour le multiple filter criterias
                //foreach (var item in options.Filter)
                //{
                //    // Lorsqu'un filtre de colonne est saisi, on lui crée un tab de dynamic qu'on ajoute a un tab parent
                //    // si un filtre est ajouté dans une autre colonne, on ajoute un autre tab de dynamic au même tab parent
                //    if (item is IList<dynamic>)
                //    {
                //        await JSRuntime.InvokeVoidAsync("console.log", "Je reussi à parcourir la liste d'éléments !!");
                //    }
                //}
            }
            else
            {
                filter.Criteria = null;
            }

            await JSRuntime.InvokeVoidAsync("console.log", "Print Filter -> ", filter);
            //filter.ColumnFilters

        }*/
    }

}
