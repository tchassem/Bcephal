using Bcephal.Blazor.Web.Archive.Services;
using Bcephal.Blazor.Web.Base.Services;
using Bcephal.Blazor.Web.Base.Shared.AbstractComponent;
using Bcephal.Blazor.Web.Base.Shared.Component;
using Bcephal.Models.Archives;
using Bcephal.Models.Base;
using Bcephal.Models.Grids.Filters;
using DevExpress.Blazor;
using DevExtreme.AspNet.Data;
using Microsoft.AspNetCore.Components;
using Microsoft.JSInterop;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Bcephal.Blazor.Web.Archive.Pages.ArchiveBrowsers
{
    //[RouteAttribute("/archive-browser/")]
    public partial class ArchiveBrowser : AbstractNewGridComponent<Bcephal.Models.Archives.Archive, ArchiveBrowserData>
    {
        protected dynamic[] GridColumns => new[] {
                        new {CaptionName = AppState["Name"] ,ColumnWidth="100px",  ColumnName = nameof(ArchiveBrowserData.Name), ColumnType = typeof(string)},
                        new {CaptionName = AppState["Status"] ,ColumnWidth="100px",  ColumnName = nameof(ArchiveBrowserData.Group), ColumnType = typeof(string)},
                        new {CaptionName = AppState["CreationDate"] ,ColumnWidth="100px", ColumnName = nameof(ArchiveBrowserData.CreationDateTime), ColumnType = typeof(DateTime?)},
                    };
        protected override int ItemsCount => GridColumns.Length;


        private object GetPropertyValue(ArchiveBrowserData item, string propName)
        {
            return item.GetType().GetProperty(propName).GetValue(item, null);
        }

        protected override object GetFieldValue(ArchiveBrowserData obj, int grilleColumnPosition)
        {
            return GetPropertyValue(obj, GridColumns[grilleColumnPosition].ColumnName);
        }

        [Inject]
        public ArchiveServices ArchiveBrowserServices { get; set; }


        [Inject]
        public IToastService ToastService { get; set; }

        [Inject]
        private WebSocketAddress WebSocketAddress { get; set; }

        protected override async Task OnInitializedAsync()
        {
            await base.OnInitializedAsync();
            IsNavLink = false;
            NewButtonVisible = false;
            ClearFilterButtonVisible = false;
            DeleteButtonVisible = false;
            EditButtonVisible = false;
            AppState.CanCreate = false;
            AppState.CanRefresh = true;
        }

        protected override AbstractNewGridDataItem GetGridDataItem(int Position)
        {
            return new NewGridDataItem(GridColumns[Position], Position);
        }

        protected override string KeyFieldName()
        {
            return nameof(ArchiveBrowserData.Id);
        }

        protected override object KeyFieldValue(ArchiveBrowserData item)
        {
            return item.Id;
        }

        protected override string NavLinkURI()
        {
            return "";
        }

        protected override Task OnRowInserting(ArchiveBrowserData newValues)
        {
            return Task.CompletedTask;
        }

        protected override  Task OnRowRemoving(ArchiveBrowserData dataItem)
        {
            return ArchiveBrowserServices.Delete(new List<long>() { dataItem.Id.Value });
        }

        protected override async Task OnRowRemoving(IReadOnlyList<object> ids)
        {
            if (ids != null && ids.Count > 0)
            {
                var idss = ids.Select(obj => ((ArchiveBrowserData)obj).Id.Value).ToList();
                await ArchiveBrowserServices.Delete(idss);
            }
        }

        protected override Task OnRowUpdating(ArchiveBrowserData dataItem, ArchiveBrowserData newValues)
        {
            return Task.CompletedTask;
        }

        protected override Task<BrowserDataPage<ArchiveBrowserData>> SearchRows(BrowserDataFilter filter)
        {
            return ArchiveBrowserServices.Search(filter);
        }

        public async void CallBackRestoreArchive(object sender, object message)
        {
            await JSRuntime.InvokeVoidAsync("console.log", "call of restore archive callback : ");
            if (message != null && !string.IsNullOrWhiteSpace(message.ToString()))
            {
                //Console.WriteLine("Archive CallBack =======> " + message.ToString());
            }
        }

        private async void RestoreArchive()
        {
            try
            {
                if (ItemsCount != 0 )
                {
                    AppState.ShowLoadingStatus();
                    await JSRuntime.InvokeVoidAsync("console.log", "try to restore archive");
                    SocketJS Socket = new SocketJS(WebSocketAddress, CallBackRestoreArchive, JSRuntime, AppState, true);

                    bool valueClose = false;
                    bool valueError = false;
                    Socket.CloseHandler += () =>
                    {
                        if (!valueClose && !valueError)
                        {
                            AppState.HideLoadingStatus();
                            ToastService.ShowSuccess(AppState["ArchiveSuccess.restore.message"], AppState["Loader"]);
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
                        Socket.send(((ArchiveBrowserData)SelectedDataItem).Id);
                    };

                    await ArchiveBrowserServices.ConnectSocketJS(Socket, "/restoration");

                    AppState.HideLoadingStatus();
                }
            }
            catch (Exception ex)
            {
                Error.ProcessError(ex);
            }
        }

        protected override Task OnItemClick(ContextMenuItemClickEventArgs args)
        {
            string text = args.ItemInfo.Text;
            DeleteContext = text;
            //IsNewRow = false;      
            if (text.Equals(AppState["Restore"]))
            {
                RestoreArchive();
            }
            return base.OnItemClick(args);
        }

        protected override string FormatDateCellValue(string format, Object obj)
        {
            return base.FormatDateCellValue("dd/MM/yyyy hh:mm:ss", obj);
        }
    }
}
