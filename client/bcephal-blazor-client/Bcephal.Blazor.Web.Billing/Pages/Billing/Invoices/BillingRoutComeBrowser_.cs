using Bcephal.Models.Base;
using Bcephal.Models.Grids.Filters;
using Microsoft.AspNetCore.Components;
using System;
using System.Collections.Generic;
using System.Threading.Tasks;
using DevExtreme.AspNet.Data;
using Bcephal.Models.Billing;
using Bcephal.Blazor.Web.Base.Shared.AbstractComponent;
using Bcephal.Blazor.Web.Base.Services;
using Bcephal.Blazor.Web.Billing.Services;
using System.Linq;
using Microsoft.JSInterop;

using Bcephal.Blazor.Web.Base.Shared.Component;

namespace Bcephal.Blazor.Web.Billing.Pages.Billing.Invoices
{
    public partial class BillingRoutComeBrowser_ : AbstractNewGridComponent<Persistent, BillingRunOutcome>
    {
        protected dynamic[] GridColumns => new[] {
            new {CaptionName = AppState["NumberAbbreviation"], ColumnWidth="100px", ColumnName = nameof(BillingRunOutcome.Name), ColumnType = typeof(string)},
            new {CaptionName = AppState["From"], ColumnWidth="100px", ColumnName = nameof(BillingRunOutcome.PeriodFrom), ColumnType = typeof(DateTime?)},
            new {CaptionName = AppState["To"], ColumnWidth="100px", ColumnName = nameof(BillingRunOutcome.PeriodTo), ColumnType = typeof(DateTime?)},
            new {CaptionName = AppState["Mode"], ColumnWidth="100px", ColumnName = nameof(BillingRunOutcome.Mode), ColumnType = typeof(string)},
            new {CaptionName = AppState["User"], ColumnWidth="100px", ColumnName = nameof(BillingRunOutcome.Username), ColumnType = typeof(string)},
            new {CaptionName = AppState["Invoice"], ColumnWidth="100px", ColumnName = nameof(BillingRunOutcome.InvoiceCount), ColumnType = typeof(long)},
            new {CaptionName = AppState["InvoiceAmount"], ColumnWidth="100px", ColumnName = nameof(BillingRunOutcome.InvoiceAmount), ColumnType = typeof(Decimal)},
            new {CaptionName = AppState["credit.note"], ColumnWidth="100px", ColumnName = nameof(BillingRunOutcome.CreditNoteCount), ColumnType = typeof(long)},
            new {CaptionName = AppState["CreditNoteAmount"], ColumnWidth="100px", ColumnName = nameof(BillingRunOutcome.CreditNoteAmount), ColumnType = typeof(Decimal)},
            new {CaptionName = AppState["Status"], ColumnWidth="100px", ColumnName = nameof(BillingRunOutcome.Status), ColumnType = typeof(string)},
            new {CaptionName = AppState["RunDate"], ColumnWidth="100px", ColumnName = nameof(BillingRunOutcome.CreationDateTime), ColumnType = typeof(DateTime?)}
        };

        [Inject]
        public BillingRunOutcomeService BillingRunOutcomeService { get; set; }

        [Inject]
        private IJSRuntime JSRuntime { get; set; }

        [Inject]
        public IToastService ToastService { get; set; }

        [Inject]
        private WebSocketAddress WebSocketAddress { get; set; }

        [CascadingParameter]
        public Error Errors { get; set; }

        bool IsConfirmation { get; set; } = true;

        bool ValidationConfirmation { get; set; } = false;

        protected string RunOutcomeValidationMessage { get; set; } = "";

        bool ResetConfirmation { get; set; } = false;

        protected string RunOutcomeResetMessage { get; set; } = "";

        private BillingRunOutcomeService GetService()
        {
            return BillingRunOutcomeService;
        }

        protected override int ItemsCount => GridColumns.Length;

        public override async ValueTask DisposeAsync()
        {

            AppState.Hander = null;

            AppState.CanValidateRunOutcome = false;
            AppState.ValidateRunOutcomeHander -= Validate;

            AppState.CanReset = false;
            AppState.ResetHandler -= Reset;

            AppState.CanSend = false;
            AppState.SendHander -= SendMail;

            await base.DisposeAsync();
        }

        protected override void OnAfterRender(bool firstRender)
        {
            base.OnAfterRender(firstRender);
            if (firstRender)
            {
                AppState.ValidateRunOutcomeHander += Validate;
                AppState.ResetHandler += Reset;
                AppState.ResetMessage = "Reset";
                AppState.SendHander += SendMail;
                AppState.SendMessage = "SendMail";
            }
        }

        protected override void OnSelectedDataItemChanged(object selectedDataItem)
        {
            base.OnSelectedDataItemChanged(selectedDataItem);
            bool canDoAction = selectedDataItem != null;
            var unvalidateItems = SelectedDataItems.Where(item => !((BillingRunOutcome)item).Status.Equals(InvoiceStatus.VALIDATED)).ToList();
            bool validated = canDoAction && unvalidateItems.Any();
            AppState.CanValidateRunOutcome = validated && !AppState.IsDashboard;
            AppState.CanReset = true && canDoAction && !AppState.IsDashboard;
            AppState.CanSend = true && canDoAction && !AppState.IsDashboard;
        }

        private object GetPropertyValue(BillingRunOutcome obj, string propName)
        {
            return obj.GetType().GetProperty(propName).GetValue(obj, null);
        }

        protected override object GetFieldValue(BillingRunOutcome item, int grilleColumnPosition)
        {
            return GetPropertyValue(item, GridColumns[grilleColumnPosition].ColumnName);
        }

        protected override AbstractNewGridDataItem GetGridDataItem(int Position)
        {
            return new NewGridDataItem(GridColumns[Position], Position);
        }

        protected override string KeyFieldName()
        {
            return nameof(BillingRunOutcome.Id);
        }

        protected override object KeyFieldValue(BillingRunOutcome item)
        {
            return item.Id;
        }

        protected override string NavLinkURI()
        {
            return Route.EDIT_GRID;
        }

        protected override Task OnRowInserting(BillingRunOutcome newValues)
        {
            return Task.CompletedTask;
        }

        protected override Task OnRowRemoving(BillingRunOutcome dataItem)
        {
            return GetService().Delete(new List<long>() { dataItem.Id.Value });
        }

        protected override async Task OnRowRemoving(IReadOnlyList<object> ids)
        {
            if (ids != null && ids.Count > 0)
            {
                var idss = ids.Select(obj => ((BillingRunOutcome)obj).Id.Value).ToList();
                await GetService().Delete(idss);
            }
        }

        protected override async Task OnRowUpdating(BillingRunOutcome dataItem, BillingRunOutcome editModel)
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

        protected override async Task<BrowserDataPage<BillingRunOutcome>> SearchRows(BrowserDataFilter filter)
        {
            BrowserDataPage<BillingRunOutcome> page = await BillingRunOutcomeService.Search(filter);
            foreach (BillingRunOutcome row in page.Items)
            {
                page_.Items.Add(row);
            }
            page_.CurrentPage = page.CurrentPage;
            page_.PageCount = page.PageCount;
            page_.PageFirstItem = page.PageFirstItem;
            page_.PageLastItem = page.PageLastItem;
            page_.PageSize = page.PageSize;
            page_.TotalItemCount = page.TotalItemCount;
            return page;
        }

        protected override string FormatDateCellValue(string format, Object obj)
        {
            return base.FormatDateCellValue("dd/MM/yyyy hh:MM:ss", obj);
        }

        public void CallBackValidateOutcome(object sender, object message)
        {
            if (message != null && !string.IsNullOrWhiteSpace(message.ToString()))
            {

            }
        }

        public List<long> GetSelectionDataItemsIds()
        {
            List<long> ids = new();
            if (SelectedDataItems != null)
            {
                SelectedDataItems.ToList().ForEach(item =>
                {
                    object obje = KeyFieldValue((BillingRunOutcome)item);
                    if (obje != null)
                    {
                        long.TryParse(obje.ToString(), out long id);
                        ids.Add(id);
                    }
                });
            }
 
            return ids;
        }   

        private void Validate()
        {
            if (GetSelectionDataItemsIds() != null && GetSelectionDataItemsIds().Count() != 0)
            {
                if (GetSelectionDataItemsIds().Count() == 1)
                {
                    RunOutcomeValidationMessage = AppState["billing.run.outcome.validation.message", GetSelectionDataItemsIds().Count().ToString()];
                }
                else
                {
                    RunOutcomeValidationMessage = AppState["billing.runs.outcome.validation.message", GetSelectionDataItemsIds().Count().ToString()];
                }
                ValidationConfirmation = true;
                StateHasChanged();
            }
        }
     
        private void CancelValidation()
        {
            ValidationConfirmation = false;
        }

        private void Reset()
        {
            if (GetSelectionDataItemsIds() != null && GetSelectionDataItemsIds().Count() != 0)
            {
                if (GetSelectionDataItemsIds().Count() == 1)
                {
                    RunOutcomeResetMessage = AppState["billing.run.outcome.reset.message", GetSelectionDataItemsIds().Count().ToString()];
                }
                else
                {
                    RunOutcomeResetMessage = AppState["billing.runs.outcome.reset.message", GetSelectionDataItemsIds().Count().ToString()];
                }
                ResetConfirmation = true;
                StateHasChanged();
            }
        }

        private void CancelReset()
        {
            ResetConfirmation = false;
        }

        public async void Validate_()
        {
            try
            {
                if (GetSelectionDataItemsIds().Count() != 0)
                {
                    AppState.ShowLoadingStatus();
                    SocketJS Socket = new SocketJS(WebSocketAddress, null, JSRuntime, AppState, true);
                    bool valueClose = false;
                    bool valueError = false;
                    Socket.CloseHandler += () =>
                    {
                        if (!valueClose && !valueError)
                        {
                            AppState.HideLoadingStatus();
                            if (GetSelectionDataItemsIds().Count() == 1)
                            {
                                ToastService.ShowSuccess(AppState["success.validate.billing.run.message"], GetSelectionDataItemsIds().Count().ToString());
                            }
                            else
                            {
                                ToastService.ShowSuccess(AppState["success.validate.billings.run.message"], GetSelectionDataItemsIds().Count().ToString());
                            }
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
                    Socket.SendHandler += () =>
                    {
                        AppState.HideLoadingStatus();
                        BillingRequest billingRequest = new BillingRequest();
                        billingRequest.Type = BillingRequestType.RUN_OUTCOME;
                        foreach (long id in GetSelectionDataItemsIds())
                        {
                            billingRequest.Ids.Add(id);
                        }
                        string data = BillingRunOutcomeService.Serialize(billingRequest);
                        Socket.send(data);
                    };
                    await BillingRunOutcomeService.ConnectSocketJS(Socket, "/validation");
                    await this.RefreshGrid_();
                    AppState.HideLoadingStatus();
                }
            }
            catch (Exception ex)
            {
                Errors.ProcessError(ex);
            }
        }

        public async void Reset_()
        {
            try
            {
                if (GetSelectionDataItemsIds().Count() != 0)
                {
                    AppState.ShowLoadingStatus();
                    SocketJS Socket = new SocketJS(WebSocketAddress, null, JSRuntime, AppState, true);
                    bool valueClose = false;
                    bool valueError = false;
                    Socket.CloseHandler += () =>
                    {
                        if (!valueClose && !valueError)
                        {
                            AppState.HideLoadingStatus();
                            if (GetSelectionDataItemsIds().Count() == 1)
                            {
                                ToastService.ShowSuccess(AppState["success.reset.billing.run.message"], GetSelectionDataItemsIds().Count().ToString());
                            }
                            else
                            {
                                ToastService.ShowSuccess(AppState["success.reset.billings.run.message"], GetSelectionDataItemsIds().Count().ToString());
                            }
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
                    Socket.SendHandler += () =>
                    {
                        AppState.HideLoadingStatus();
                        BillingRequest billingRequest = new BillingRequest();
                        billingRequest.Type = BillingRequestType.RUN_OUTCOME;
                        foreach (long id in GetSelectionDataItemsIds())
                        {
                            billingRequest.Ids.Add(id);
                        }
                        string data = BillingRunOutcomeService.Serialize(billingRequest);
                        Socket.send(data);
                    };
                    await BillingRunOutcomeService.ConnectSocketJS(Socket, "/reset");
                    await this.RefreshGrid_();
                    AppState.HideLoadingStatus();
                }
            }
            catch (Exception ex)
            {
                Errors.ProcessError(ex);
            }
        }

        public async void SendMail()
        {
            try
            {
                if (GetSelectionDataItemsIds().Count() != 0)
                {
                    AppState.ShowLoadingStatus();
                    SocketJS Socket = new SocketJS(WebSocketAddress, null, JSRuntime, AppState, true);
                    bool valueClose = false;
                    bool valueError = false;
                    Socket.CloseHandler += () =>
                    {
                        if (!valueClose && !valueError)
                        {
                            AppState.HideLoadingStatus();
                            ToastService.ShowSuccess(AppState["success.send.mail.message"]);
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
                    Socket.SendHandler += () =>
                    {
                        AppState.HideLoadingStatus();
                        BillingRequest billingRequest = new BillingRequest();
                        billingRequest.Type = BillingRequestType.RUN_OUTCOME;
                        foreach (long id in GetSelectionDataItemsIds())
                        {
                            billingRequest.Ids.Add(id);
                        }
                        string data = BillingRunOutcomeService.Serialize(billingRequest);
                        Socket.send(data);
                    };
                    await BillingRunOutcomeService.ConnectSocketJS(Socket, "/send-mail");
                    AppState.HideLoadingStatus();
                }
            }
            catch (Exception ex)
            {
                Errors.ProcessError(ex);
            }
        }
    }
}