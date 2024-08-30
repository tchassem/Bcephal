using Bcephal.Blazor.Web.Base.Shared.AbstractComponent;
using Bcephal.Blazor.Web.Messenger.Services;
using Bcephal.Models.Base;
using Bcephal.Models.Grids.Filters;
using Bcephal.Models.Messages;
using DevExtreme.AspNet.Data;
using Microsoft.AspNetCore.Components;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Bcephal.Blazor.Web.Messenger.Pages.MessageLog
{
    public partial class AlarmMessagerLogBrowser : AbstractNewGridComponent<Persistent, MessageLogBrowserData>
    {

        protected dynamic[] GridColumns => new[] {
                        new {CaptionName = AppState["Id"] ,ColumnWidth="52px",  ColumnName = nameof(MessageLogBrowserData.Id), ColumnType = typeof(long)},
                        new {CaptionName = AppState["CreationDate"],ColumnWidth="130px", ColumnName = nameof(MessageLogBrowserData.CreationDateTime), ColumnType = typeof(DateTime?)},
                        new {CaptionName = AppState["Mode"] ,ColumnWidth="52px",  ColumnName = nameof(MessageLogBrowserData.Mode), ColumnType = typeof(string)},
                        new {CaptionName = AppState["User.name"] ,ColumnWidth="",  ColumnName = nameof(MessageLogBrowserData.Username), ColumnType = typeof(string)},
                        new {CaptionName = AppState["Type"] ,ColumnWidth="50px",  ColumnName = nameof(MessageLogBrowserData.MessageType), ColumnType = typeof(string)},
                        new {CaptionName = AppState["Object"] ,ColumnWidth="",  ColumnName = nameof(MessageLogBrowserData.Subject), ColumnType = typeof(string)},
                        new {CaptionName = AppState["message"] ,ColumnWidth="",  ColumnName = nameof(MessageLogBrowserData.Content), ColumnType = typeof(string)},
                        new {CaptionName = AppState["Audience"] ,ColumnWidth="",  ColumnName = nameof(MessageLogBrowserData.Audience), ColumnType = typeof(string)},
                        new {CaptionName = AppState["max.send.attempts"] ,ColumnWidth="52px",  ColumnName = nameof(MessageLogBrowserData.MaxSendAttempts), ColumnType = typeof(long)},
                        new {CaptionName = AppState["send.attempts"] ,ColumnWidth="52px",  ColumnName = nameof(MessageLogBrowserData.SendAttempts), ColumnType = typeof(long)},
                        new {CaptionName = AppState["first.send.date"] ,ColumnWidth="130px",  ColumnName = nameof(MessageLogBrowserData.FirstSendDate), ColumnType = typeof(DateTime?)},
                        new {CaptionName = AppState["last.send.date"] ,ColumnWidth="130px", ColumnName = nameof(MessageLogBrowserData.ModificationDate), ColumnType = typeof(DateTime?)},
                        new {CaptionName = AppState["Status"] ,ColumnWidth="75px",  ColumnName = nameof(MessageLogBrowserData.MessageLogStatus), ColumnType = typeof(string)},
                        new {CaptionName = "Logs" ,ColumnWidth="",  ColumnName = nameof(MessageLogBrowserData.Log), ColumnType = typeof(string)},

                      };

        protected override int ItemsCount => GridColumns.Length;

        [Inject]
        public MessageLogService MessageLogService { get; set; }

        public bool ConfirmationCancelModal { get; set; } = false;
        bool IsConfirmation { get; set; } = true;
        protected List<MessageLogStatus> AllStatus { get; set; } = new();

        public AlarmMessageBrowserDataFilter AlarmMessageBrowserDataFilter { get; set; }

        public override Task SetParametersAsync(ParameterView parameters)
        {
            CustomHeaderRenderHandler = CustomHeaderRender;
            return base.SetParametersAsync(parameters);
        }

        protected override async Task OnInitializedAsync()
        {
            await base.OnInitializedAsync();
            IsNavLink = false;
            ClearFilterButtonVisible = false;
            DeleteButtonVisible = true;

            foreach (var t in MessageLogStatus.GetAll().ToList())
            {
                if (MessageLogStatus.NEW != t)
                {
                    AllStatus.Add(t);
                }
            }

            if (AlarmMessageBrowserDataFilter == null)
            {
                AlarmMessageBrowserDataFilter = new AlarmMessageBrowserDataFilter();
                AlarmMessageBrowserDataFilter.PageSize = filter.PageSize;
                AlarmMessageBrowserDataFilter.OrderAsc = filter.OrderAsc;
                AlarmMessageBrowserDataFilter.ShowAll = filter.ShowAll;
                AlarmMessageBrowserDataFilter.AllowRowCounting = filter.AllowRowCounting;
                AlarmMessageBrowserDataFilter.Statut = MessageLogStatus.PENDING;
            }
        }

        protected override async Task OnAfterRenderAsync(bool firstRender)
        {
                if (CanRefresh && !AppState.IsDashboard)
                {
                    AppState.CanRefresh = true && !AppState.IsDashboard;
                }
                if (CanCreate && !AppState.IsDashboard)
                {
                    AppState.CreateHander += Create;
                }

                AppState.CanReset = true && !AppState.IsDashboard;
                AppState.ResetHandler += ResetAlarm;

                AppState.CanCancel = true && !AppState.IsDashboard;
                AppState.CancelHander += CancelAlarm;

                AppState.CanSend = true && !AppState.IsDashboard;
                AppState.SendHander += SendAlarm;

                await base.OnAfterRenderAsync(firstRender);
        }

        public virtual MessageLogService GetService()
        {
            return MessageLogService;
        }

        private object GetPropertyValue(MessageLogBrowserData obj, string propName)
        {
            return obj.GetType().GetProperty(propName).GetValue(obj, null);
        }

        protected override object GetFieldValue(MessageLogBrowserData item, int grilleColumnPosition)
        {
            return GetPropertyValue(item, GridColumns[grilleColumnPosition].ColumnName);
        }

        protected override AbstractNewGridDataItem GetGridDataItem(int Position)
        {
            return new NewGridDataItem(GridColumns[Position], Position);
        }

        protected override string KeyFieldName()
        {
            return nameof(MessageLogBrowserData.Id);
        }

        protected override object KeyFieldValue(MessageLogBrowserData item)
        {
            return item.Id;
        }

        protected override string NavLinkURI()
        {
            return "";
        }

        protected override Task OnRowInserting(MessageLogBrowserData newValues)
        {
            throw new NotImplementedException();
        }

        protected override async Task OnRowRemoving(MessageLogBrowserData dataItem)
        {
            await GetService().Delete(new List<long>() { dataItem.Id.Value });
        }

        protected override async Task OnRowRemoving(IReadOnlyList<object> ids)
        {
            if (ids != null && ids.Count > 0)
            {
                var idss = ids.Select(obj => ((MessageLogBrowserData)obj).Id.Value).ToList();
                await GetService().Delete(idss);
            }
        }

        protected async override Task OnRowUpdating(MessageLogBrowserData dataItem, MessageLogBrowserData newValues)
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

        


    protected override  Task<BrowserDataPage<MessageLogBrowserData>> SearchRows(BrowserDataFilter filter)
        {
            AlarmMessageBrowserDataFilter.PageSize = filter.PageSize;
            AlarmMessageBrowserDataFilter.OrderAsc = filter.OrderAsc;
            AlarmMessageBrowserDataFilter.ShowAll = filter.ShowAll;
            AlarmMessageBrowserDataFilter.AllowRowCounting = filter.AllowRowCounting;
            AlarmMessageBrowserDataFilter.ColumnFilters = filter.ColumnFilters;
           return GetService().Search<MessageLogBrowserData>(AlarmMessageBrowserDataFilter);
        }


        protected override string FormatDateCellValue(string format, Object obj)
        {
            return base.FormatDateCellValue("dd/MM/yyyy hh:mm:ss", obj);
        }

        public override async ValueTask DisposeAsync()
        {
            AppState.CanReset = false;
            AppState.ResetHandler -= ResetAlarm; 
            
            AppState.CanCancel = false;
            AppState.CancelHander -= CancelAlarmConfirm; 
            
            AppState.CanSend = false;
            AppState.CanRefresh = false;
            AppState.SendHander -= SendAlarm;
            await base.DisposeAsync();
        }

        private async void ResetAlarm()
        {
            bool val = await MessageLogService.Reset(GetSelectionDataItemsIds());
            if (val)
            {
                await this.RefreshGrid_();
            }
            SelectedDataItemsChanged(new List<MessageLogBrowserData>());
        }
        
        private void CancelAlarmConfirm()
        {
            ConfirmationCancelModal = true;
            StateHasChanged();
        }
        
        private async void CancelAlarm()
        {
            bool val = await MessageLogService.Cancel(GetSelectionDataItemsIds());
            if (val)
            {
                await this.RefreshGrid_();
            }
            SelectedDataItemsChanged(new List<MessageLogBrowserData>());
            Close();
        }

        private void Close()
        {
            ConfirmationCancelModal = false;
        }
        private async void SendAlarm()
        {
            bool val = await MessageLogService.Send(GetSelectionDataItemsIds());
            if (val)
            {
                await this.RefreshGrid_();
            }
            SelectedDataItemsChanged(new List<MessageLogBrowserData>());
        }
    }
}
