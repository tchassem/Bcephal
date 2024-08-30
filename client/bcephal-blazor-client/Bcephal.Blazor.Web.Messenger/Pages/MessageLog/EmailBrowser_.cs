using Bcephal.Blazor.Web.Base.Services;
using Bcephal.Blazor.Web.Base.Shared.AbstractComponent;
using Bcephal.Blazor.Web.Messenger.Services;
using Bcephal.Models.Base;
using Bcephal.Models.Clients;
using Bcephal.Models.Grids.Filters;
using DevExtreme.AspNet.Data;
using Microsoft.AspNetCore.Components;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Bcephal.Blazor.Web.Messenger.Pages.MessageLog
{
    [RouteAttribute("/browser-email")]
    public class EmailBrowser_ : AbstractNewGridComponent<Models.Messages.Message, Models.Messages.Message> 
    {

        protected dynamic[] GridColumns => new[] {
                        new {CaptionName = AppState["Operateur"] ,ColumnWidth="100px",  ColumnName = nameof(Models.Messages.Message.Operator), ColumnType = typeof(string)},
                        new {CaptionName = AppState["Title"] ,ColumnWidth="100px",  ColumnName = nameof(Models.Messages.Message.MessageTitle), ColumnType = typeof(string)},
                        new {CaptionName = AppState["Message"] ,ColumnWidth="100px", ColumnName = nameof(Models.Messages.Message.Message_), ColumnType = typeof(string)},
                        new {CaptionName = AppState["Statut"] ,ColumnWidth="100px", ColumnName = nameof(Models.Messages.Message.Status), ColumnType = typeof(string)},
                        new {CaptionName = AppState["type"] ,ColumnWidth="100px",  ColumnName = nameof(Models.Messages.Message.Type), ColumnType = typeof(string)},

                      };
        [Inject]
        public MessageService MessageService { get; set; }

        public virtual MessageService GetService()
        {
            return MessageService;
        }
       
        protected override int ItemsCount => GridColumns.Length;


        private object GetPropertyValue(Models.Messages.Message obj, string propName)
        {
            return obj.GetType().GetProperty(propName).GetValue(obj, null);
        }

        protected override AbstractNewGridDataItem GetGridDataItem(int Position)
        {
            return new NewGridDataItem(GridColumns[Position], Position);
        }
        protected override object GetFieldValue(Models.Messages.Message item, int grilleColumnPosition)
        {
            return GetPropertyValue(item, GridColumns[grilleColumnPosition].ColumnName);
        }
        protected override string KeyFieldName()
        {
            return nameof(Models.Messages.Message.Id);
        }


        protected override string NavLinkURI()
        {
            return "";
        }

        protected override Task OnRowInserting(Models.Messages.Message newValues)
        {
            return Task.CompletedTask;
        }

        protected override  Task OnRowRemoving(Models.Messages.Message dataItem)
        {
            return GetService().Delete(new List<long>() { dataItem.Id.Value });
        }
        protected override async Task OnRowRemoving(IReadOnlyList<object> ids)
        {
            if (ids != null && ids.Count > 0)
            {
                var idss = ids.Select(obj => ((Models.Messages.Message)obj).Id.Value).ToList();
                await GetService().Delete(idss);
            }
        }

        protected async override Task OnRowUpdating(Models.Messages.Message dataItem, Models.Messages.Message newValues)
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

        protected override Task<BrowserDataPage<Models.Messages.Message>> SearchRows(BrowserDataFilter filter)
        {
            return Task.FromResult(GetService().Mock());
        }
        protected override string FormatDateCellValue(string format, Object obj)
        {
            return base.FormatDateCellValue("dd/MM/yyyy hh:mm:ss", obj);
        }
        protected override object KeyFieldValue(Models.Messages.Message item)
        {
            return item.Id;
        }
    }
}
