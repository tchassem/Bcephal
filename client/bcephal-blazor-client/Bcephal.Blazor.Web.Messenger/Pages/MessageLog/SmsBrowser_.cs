using Bcephal.Blazor.Web.Base.Services;
using Bcephal.Blazor.Web.Base.Shared.AbstractComponent;
using Bcephal.Blazor.Web.Base.Shared.Component;
using Bcephal.Blazor.Web.Messenger.Services;
using Bcephal.Models.Base;
using Bcephal.Models.Grids.Filters;
using DevExtreme.AspNet.Data;
using Microsoft.AspNetCore.Components;
using Microsoft.JSInterop;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Bcephal.Blazor.Web.Messenger.Pages.MessageLog
{
    [RouteAttribute("/browser-sms")]
    public class SmsBrowser_ : AbstractNewGridComponent<Models.Messages.Message, Models.Messages.Message>
    {

        protected dynamic[] GridColumns => new[] {
                        new {CaptionName = AppState["Operateur"] ,ColumnWidth="100px",  ColumnName = nameof(Models.Messages.Message.Operator), ColumnType = typeof(string)},
                        //new {CaptionName = AppState["Adress"] ,ColumnWidth="100px",  ColumnName = nameof(Models.Messages.MessageLog.Address.Phone), ColumnType = typeof(string)},
                        new {CaptionName = AppState["Message"] ,ColumnWidth="100px", ColumnName = nameof(Models.Messages.Message.Message_), ColumnType = typeof(string)},
                        new {CaptionName = AppState["Statut"] ,ColumnWidth="100px", ColumnName = nameof(Models.Messages.Message.Status), ColumnType = typeof(string)},
                        new {CaptionName = AppState["type"] ,ColumnWidth="100px",  ColumnName = nameof(Models.Messages.Message.Type), ColumnType = typeof(string)},

                      };
        protected override int ItemsCount => GridColumns.Length;


        private object GetPropertyValue(Models.Messages.Message item, string propName)
        {
            return item.GetType().GetProperty(propName).GetValue(item, null);
        }

        protected override object GetFieldValue(Models.Messages.Message obj, int grilleColumnPosition)
        {
            return GetPropertyValue(obj, GridColumns[grilleColumnPosition].ColumnName);
        }

        [Inject]
        public MessageService MessageLogService { get; set; }
        public virtual MessageService GetService()
        {
            return MessageLogService;
        }

        [Inject]
        public IToastService ToastService { get; set; }

        [Inject]
        private WebSocketAddress WebSocketAddress { get; set; }

        protected override async Task OnInitializedAsync()
        {
            await base.OnInitializedAsync();           
        }

        protected override AbstractNewGridDataItem GetGridDataItem(int Position)
        {
            return new NewGridDataItem(GridColumns[Position], Position);
        }

        protected override string KeyFieldName()
        {
            return nameof(Models.Messages.Message.Id);
        }

        protected override object KeyFieldValue(Models.Messages.Message item)
        {
            return item.Id;
        }

        protected override string NavLinkURI()
        {
            return "";
        }

        protected override Task OnRowInserting(Models.Messages.Message newValues)
        {
            return Task.CompletedTask;
        }

        protected override async Task OnRowRemoving(Models.Messages.Message dataItem)
        {
            await MessageLogService.Delete(new List<long>() { dataItem.Id.Value });
        }

        protected override async Task OnRowRemoving(IReadOnlyList<object> ids)
        {
            if (ids != null && ids.Count > 0)
            {
                if (ids != null && ids.Count > 0)
                {
                    var idss = ids.Select(obj => ((Models.Messages.Message)obj).Id.Value).ToList();
                    await MessageLogService.Delete(idss);
                }
            }
        }

        protected override Task OnRowUpdating(Models.Messages.Message dataItem, Models.Messages.Message newValues)
        {
            return Task.CompletedTask;
        }

        protected override  Task<BrowserDataPage<Models.Messages.Message>> SearchRows(BrowserDataFilter filter)
        {
            return Task.FromResult(GetService().Mock());
        }
    }
}
