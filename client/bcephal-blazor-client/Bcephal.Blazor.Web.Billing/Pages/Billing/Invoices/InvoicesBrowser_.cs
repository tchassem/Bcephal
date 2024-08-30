using Bcephal.Models.Base;
using Bcephal.Models.Grids.Filters;
using Microsoft.AspNetCore.Components;
using System;
using System.Collections.Generic;
using System.Threading.Tasks;
using DevExtreme.AspNet.Data;
using Bcephal.Models.Billing.Invoices;
using Bcephal.Blazor.Web.Base.Shared.AbstractComponent;
using Bcephal.Blazor.Web.Base.Services;
using Bcephal.Blazor.Web.Billing.Services;
using System.Linq;
using Microsoft.JSInterop;
using Bcephal.Blazor.Web.Base.Shared.Component;
using Bcephal.Models.Billing;

namespace Bcephal.Blazor.Web.Billing.Pages.Billing.Invoices
{
    public partial class InvoicesBrowser_ : AbstractNewGridComponent<Invoice, InvoiceBrowserData>
    {
        protected dynamic[] GridColumns => new[] {
            new {CaptionName = AppState["Reference"], ColumnWidth="7%", ColumnName = nameof(InvoiceBrowserData.Reference), ColumnType = typeof(string),IsNavLink= false},
            new {CaptionName = AppState["InvoiceDate"], ColumnWidth="auto",  ColumnName = nameof(InvoiceBrowserData.InvoiceDate), ColumnType = typeof(DateTime),IsNavLink= false},
            new {CaptionName = AppState["ClientNumero"], ColumnWidth="7%", ColumnName = nameof(InvoiceBrowserData.ClientNumber), ColumnType = typeof(string),IsNavLink= false},
            new {CaptionName = AppState["ClientName"], ColumnWidth="7%", ColumnName = nameof(InvoiceBrowserData.ClientName), ColumnType = typeof(string),IsNavLink= false},
            new {CaptionName = AppState["AmountWithoutVat"], ColumnWidth="7%", ColumnName = nameof(InvoiceBrowserData.AmountWithoutVat), ColumnType = typeof(Decimal),IsNavLink= false},
            new {CaptionName = AppState["vat.amount"], ColumnWidth="7%", ColumnName = nameof(InvoiceBrowserData.VatAmount), ColumnType = typeof(Decimal),IsNavLink= false},
            new {CaptionName = AppState["ManuallyModified"], ColumnWidth="7%", ColumnName = nameof(InvoiceBrowserData.ManuallyModified), ColumnType = typeof(bool),IsNavLink= false},
            new {CaptionName = AppState["version"], ColumnWidth="7%", ColumnName = nameof(InvoiceBrowserData.Version), ColumnType = typeof(int),IsNavLink= true},
            new {CaptionName = AppState["Status"], ColumnWidth="7%", ColumnName = nameof(InvoiceBrowserData.Status), ColumnType = typeof(string),IsNavLink= false},
            new {CaptionName = AppState["run.number"], ColumnWidth="7%", ColumnName = nameof(InvoiceBrowserData.RunNumber), ColumnType = typeof(Decimal),IsNavLink= false},
            new {CaptionName = AppState["CreationDate"], ColumnWidth="7%", ColumnName = nameof(InvoiceBrowserData.CreationDate), ColumnType = typeof(DateTime),IsNavLink= false},
        };

        [Inject]
        public InvoiceService InvoiceService { get; set; }

        [Inject]
        public IToastService ToastService { get; set; }

        [Inject]
        private WebSocketAddress WebSocketAddress { get; set; }

        [CascadingParameter]
        public Error Errors { get; set; }

        bool IsConfirmation { get; set; } = true;

        bool ValidationConfirmation { get; set; } = false;

        bool ResetConfirmation { get; set; } = false;

        bool ResetValidationConfirmation { get; set; } = false;

        protected string InvoiceValidationMessage1 { get; set; } = "";

        protected string InvoiceValidationMessage2 { get; set; } = "";

        protected string InvoiceResetMessage1 { get; set; } = "";

        protected string InvoiceResetMessage2 { get; set; } = "";

        protected string InvoiceResetMessage3 { get; set; } = "";

        protected string InvoiceResetValidationMessage1 { get; set; } = "";

        protected string InvoiceResetValidationMessage2 { get; set; } = "";

        protected override int ItemsCount => GridColumns.Length;

        protected override void OnInitialized()
        {
            base.OnInitialized();
            EditorRoute = Route.BILLING_MODEL_INVOICE;
            IsNavLink = true;
            NewButtonVisible = false;
            ClearFilterButtonVisible = false;
            EditButtonVisible = false;
        }

        public override async ValueTask DisposeAsync()
        {
            AppState.Hander = null;

            AppState.CanCreateCreditNote = false;
            AppState.CreateCreditNoteHander -= CreateCreditNote;

            AppState.CanResetValidation = false;
            AppState.ResetValidationHandler -= ResetValidation;

            AppState.CanSend = false;
            AppState.SendHander -= SendMail;

            AppState.CanReset = false;
            AppState.ResetHandler -= Reset;

            AppState.CanValidateRunOutcome = false;
            AppState.ValidateRunOutcomeHander -= Validate;

            await base.DisposeAsync();
        }

        protected override void OnAfterRender(bool firstRender)
        {
            base.OnAfterRender(firstRender);
            if (firstRender)
            {
                AppState.ResetValidationHandler += ResetValidation;
                AppState.CreateCreditNoteHander += CreateCreditNote;
                AppState.ValidateRunOutcomeHander += Validate;
                AppState.ResetHandler += Reset;
                AppState.ResetMessage = "reset.invoice";
                AppState.SendHander += SendMail;
                AppState.SendMessage = "SendMail";
            }
        }

        protected override void OnSelectedDataItemChanged(object selectedDataItem)
        {
            base.OnSelectedDataItemChanged(selectedDataItem);
            bool canDoAction = selectedDataItem != null;
            var unvalidateItems = SelectedDataItems.Where(item => !((InvoiceBrowserData)item).Status.Equals(InvoiceStatus.VALIDATED)).ToList();
            bool validated = canDoAction && unvalidateItems.Any();
            AppState.CanValidateRunOutcome = validated && !AppState.IsDashboard;
            AppState.CanReset = true && canDoAction && !AppState.IsDashboard;
            AppState.CanSend = true && canDoAction && !AppState.IsDashboard;
            AppState.CanResetValidation = true && canDoAction && !AppState.IsDashboard;
            AppState.CanCreateCreditNote = true && canDoAction && !AppState.IsDashboard;
        }

        private object GetPropertyValue(InvoiceBrowserData obj, string propName)
        {
            return obj.GetType().GetProperty(propName).GetValue(obj, null);
        }

        protected override object GetFieldValue(InvoiceBrowserData item, int grilleColumnPosition)
        {
            return GetPropertyValue(item, GridColumns[grilleColumnPosition].ColumnName);
        }

        protected override AbstractNewGridDataItem GetGridDataItem(int Position)
        {
            return new NewGridDataItem(GridColumns[Position], Position);
        }

        protected override string KeyFieldName()
        {
            return nameof(InvoiceBrowserData.Id);
        }

        protected override object KeyFieldValue(InvoiceBrowserData item)
        {
            return item.Id;
        }

        protected override string NavLinkURI()
        {
            return Route.BILLING_MODEL_INVOICE;
        }

        protected override Task OnRowInserting(InvoiceBrowserData newValues)
        {
            return Task.CompletedTask;
        }

        protected override async Task OnRowRemoving(IReadOnlyList<object> ids)
        {
            if (ids != null && ids.Count > 0)
            {
                var idss = ids.Select(obj => ((InvoiceBrowserData)obj).Id.Value).ToList();
                await InvoiceService.Delete(idss);
            }
        }

        protected override async Task OnRowRemoving(InvoiceBrowserData dataItem)
        {
            await InvoiceService.Delete(new List<long>() { dataItem.Id.Value });
        }

        protected override async Task OnRowUpdating(InvoiceBrowserData dataItem, InvoiceBrowserData editModel)
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

        protected override async Task<BrowserDataPage<InvoiceBrowserData>> SearchRows(BrowserDataFilter filter)
        {
            BrowserDataPage<InvoiceBrowserData> page = await InvoiceService.Search(filter);
            foreach (InvoiceBrowserData row in page.Items)
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
            return base.FormatDateCellValue("dd/MM/yyyy hh:mm:ss", obj);
        }        

        public List<long> GetSelectionDataItemsIds()
        {
            List<long> ids = new();
            if (SelectedDataItems != null)
            {
                SelectedDataItems.ToList().ForEach(item =>
                {
                    object obje = KeyFieldValue((InvoiceBrowserData)item);
                    if (obje != null)
                    {
                        long.TryParse(obje.ToString(), out long id);
                        ids.Add(id);
                    }
                });
            }
            return ids;
        }

        private void Reset()
        {
            if (GetSelectionDataItemsIds() != null && GetSelectionDataItemsIds().Count() != 0)
            {
                if (GetSelectionDataItemsIds().Count() == 1)
                {
                    InvoiceResetMessage1 = AppState["invoice.reset.message", GetSelectionDataItemsIds().Count().ToString()];
                    InvoiceResetMessage2 = AppState["invoice.deleted.warning"];
                    InvoiceResetMessage3 = AppState["invoice.operation.corruption"];
                }
                else
                {
                    InvoiceResetMessage1 = AppState["invoices.reset.message", GetSelectionDataItemsIds().Count().ToString()];
                    InvoiceResetMessage2 = AppState["invoices.deleted.warning"];
                    InvoiceResetMessage3 = AppState["invoices.operation.corruption"];
                }
                ResetConfirmation = true;
                StateHasChanged();
            }
        }

        private void CancelReset()
        {
            ResetConfirmation = false;
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
                                ToastService.ShowSuccess(AppState["success.reset.invoice.message"], GetSelectionDataItemsIds().Count().ToString());
                            }
                            else 
                            {
                                ToastService.ShowSuccess(AppState["success.reset.invoices.message"], GetSelectionDataItemsIds().Count().ToString());
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

                        billingRequest.Type = BillingRequestType.INVOICE;
                        foreach (long id in GetSelectionDataItemsIds())
                        {
                            billingRequest.Ids.Add(id);
                        }
                        string data = InvoiceService.Serialize(billingRequest);
                        Socket.send(data);
                    };
                    await InvoiceService.ConnectSocketJS(Socket, "/reset");
                    await this.RefreshGrid_();
                    AppState.HideLoadingStatus();
                }
            }
            catch (Exception ex)
            {
                Errors.ProcessError(ex);
            }
        }

        private void ResetValidation()
        {
            if (GetSelectionDataItemsIds() != null && GetSelectionDataItemsIds().Count() != 0)
            {
                if (GetSelectionDataItemsIds().Count() == 1)
                {
                    InvoiceResetValidationMessage1 = AppState["invoice.reset.validation.message", GetSelectionDataItemsIds().Count().ToString()];
                    InvoiceResetValidationMessage2 = AppState["invoice.operation.corruption"];
                }
                else
                {
                    InvoiceResetValidationMessage1 = AppState["invoices.reset.validation.message", GetSelectionDataItemsIds().Count().ToString()];
                    InvoiceResetValidationMessage2 = AppState["invoices.operation.corruption"];
                }
                ResetValidationConfirmation = true;
                StateHasChanged();
            }
        }

        private void CancelResetValidation()
        {
            ResetValidationConfirmation = false;
        }

        public async void ResetValidation_()
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
                                ToastService.ShowSuccess(AppState["success.reset.validation.invoice.message"], GetSelectionDataItemsIds().Count().ToString());
                            }
                            else
                            {
                                ToastService.ShowSuccess(AppState["success.reset.validation.invoices.message"], GetSelectionDataItemsIds().Count().ToString());
                            }



                            ToastService.ShowSuccess(AppState["success.runinvoice.message"], AppState["Loader"]);
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
                        billingRequest.Type = BillingRequestType.INVOICE;
                        foreach (long id in GetSelectionDataItemsIds())
                        {
                            billingRequest.Ids.Add(id);
                        }
                        string data = InvoiceService.Serialize(billingRequest);
                        Socket.send(data);
                    };
                    await InvoiceService.ConnectSocketJS(Socket, "/reset-validation");
                    await this.RefreshGrid_();
                    AppState.HideLoadingStatus();
                }
            }
            catch (Exception ex)
            {
                Errors.ProcessError(ex);
            }
        }

        public async void CreateCreditNote()
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
                            ToastService.ShowSuccess(AppState["success.create.credit.note.message"], AppState["Loader"]);
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
                        billingRequest.Type = BillingRequestType.INVOICE;
                        foreach (long id in GetSelectionDataItemsIds())
                        {
                            billingRequest.Ids.Add(id);
                        }
                        string data = InvoiceService.Serialize(billingRequest);
                        Socket.send(data);
                    };
                    await InvoiceService.ConnectSocketJS(Socket, "/create-credit-note");
                    AppState.HideLoadingStatus();
                }
            }
            catch (Exception ex)
            {
                Errors.ProcessError(ex);
            }
        }

        private void Validate()
        {
            if (GetSelectionDataItemsIds() != null && GetSelectionDataItemsIds().Count() != 0)
            {
                if (GetSelectionDataItemsIds().Count() == 1)
                {
                    InvoiceValidationMessage1 = AppState["invoice.validation.message", GetSelectionDataItemsIds().Count().ToString()];
                    InvoiceValidationMessage2 = AppState["invoice.no.modified"];
                }
                else
                {
                    InvoiceValidationMessage1 = AppState["invoices.validation.message", GetSelectionDataItemsIds().Count().ToString()];
                    InvoiceValidationMessage2 = AppState["invoices.no.modified"];
                }
                ValidationConfirmation = true;
                StateHasChanged();
            }
        }

        private void CancelValidation()
        {
            ValidationConfirmation = false;
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
                                ToastService.ShowSuccess(AppState["success.validate.invoice.message"], GetSelectionDataItemsIds().Count().ToString());
                            }
                            else
                            {
                                ToastService.ShowSuccess(AppState["success.validate.invoices.message"], GetSelectionDataItemsIds().Count().ToString());
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

                        billingRequest.Type = BillingRequestType.INVOICE;
                        foreach (long id in GetSelectionDataItemsIds())
                        {
                            billingRequest.Ids.Add(id);
                        }
                        string data = InvoiceService.Serialize(billingRequest);
                        Socket.send(data);
                    };
                    await InvoiceService.ConnectSocketJS(Socket, "/validation");
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
                        billingRequest.Type = BillingRequestType.INVOICE;
                        foreach (long id in GetSelectionDataItemsIds())
                        {
                            billingRequest.Ids.Add(id);
                        }
                        string data = InvoiceService.Serialize(billingRequest);
                        Socket.send(data);
                    };
                    await InvoiceService.ConnectSocketJS(Socket, "/send-mail");
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