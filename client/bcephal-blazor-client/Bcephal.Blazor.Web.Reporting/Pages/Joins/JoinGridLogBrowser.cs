using Bcephal.Blazor.Web.Base.Services;
using Bcephal.Blazor.Web.Base.Shared.AbstractComponent;
using Bcephal.Blazor.Web.Reporting.Services;
using Bcephal.Models.Base;
using Bcephal.Models.Grids.Filters;
using Bcephal.Models.Joins;
using Microsoft.AspNetCore.Components;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;

namespace Bcephal.Blazor.Web.Reporting.Pages.Joins
{
    //[RouteAttribute("browser-join-grid-log/")]
    public class JoinGridLogBrowser : AbstractNewGridComponent<JoinLog, JoinLogBrowserData>
    {
        protected dynamic[] GridColumns => new[] {
            new {CaptionName = AppState["Name"], ColumnWidth="35%", ColumnName = nameof(JoinLogBrowserData.Name), ColumnType = typeof(long)},
            new {CaptionName = AppState["PublicationGridName"], ColumnWidth="15%", ColumnName = nameof(JoinLogBrowserData.PublicationGridName), ColumnType = typeof(string)},
            new {CaptionName = AppState["User"], ColumnWidth="auto", ColumnName = nameof(JoinLogBrowserData.User), ColumnType = typeof(string)},
            new {CaptionName = AppState["Mode"], ColumnWidth="auto", ColumnName = nameof(JoinLogBrowserData.RunMode), ColumnType = typeof(string)},
            new {CaptionName = AppState["Status"], ColumnWidth="auto", ColumnName = nameof(JoinLogBrowserData.RunStatus), ColumnType = typeof(string)},
            new {CaptionName = AppState["RowCount"], ColumnWidth="auto", ColumnName = nameof(JoinLogBrowserData.RowCount), ColumnType = typeof(long?)},
            new {CaptionName = AppState["PublicationNumber"], ColumnWidth="auto", ColumnName = nameof(JoinLogBrowserData.PublicationNumber), ColumnType = typeof(long)},
            new {CaptionName = AppState["PublicationNbrAttributeName"], ColumnWidth="auto", ColumnName = nameof(JoinLogBrowserData.PublicationNbrAttributeName), ColumnType = typeof(string)},
            new {CaptionName = AppState["CreationDate"], ColumnWidth="auto", ColumnName = nameof(JoinLogBrowserData.CreationDateTime), ColumnType = typeof(DateTime?)},
            new {CaptionName = AppState["EndDate"], ColumnWidth="auto", ColumnName = nameof(JoinLogBrowserData.EndDateTime), ColumnType = typeof(DateTime?)},
            new {CaptionName = AppState["Message"], ColumnWidth="auto", ColumnName = nameof(JoinLogBrowserData.Message), ColumnType = typeof(string)},
        };

        [Inject]
        public JoinLogService JoinLogService { get; set; }

        public virtual JoinLogService GetService()
        {
            return JoinLogService;
        }

        protected override int ItemsCount => GridColumns.Length;

        private object GetPropertyValue(JoinLogBrowserData obj, string propName)
        {
            return obj.GetType().GetProperty(propName).GetValue(obj, null);
        }

        protected override object GetFieldValue(JoinLogBrowserData item, int grilleColumnPosition)
        {
            return GetPropertyValue(item, GridColumns[grilleColumnPosition].ColumnName);
        }

        protected override AbstractNewGridDataItem GetGridDataItem(int Position)
        {
            return new NewGridDataItem(GridColumns[Position], Position);
        }

        protected override string KeyFieldName()
        {
            return nameof(JoinLogBrowserData.Id);
        }

        protected override object KeyFieldValue(JoinLogBrowserData item)
        {
            return item.Id;
        }

        protected override string NavLinkURI()
        {
            return "";
        }

        protected override Task OnRowInserting(JoinLogBrowserData newValues)
        {
            throw new NotImplementedException();
        }

        protected override async Task OnRowRemoving(JoinLogBrowserData dataItem)
        {
            await GetService().Delete(new List<long>() { dataItem.Id.Value });
        }

        protected override async Task OnRowRemoving(IReadOnlyList<object> ids)
        {
            if (ids != null && ids.Count > 0)
            {
                var idss = ids.Select(obj => ((JoinLogBrowserData)obj).Id.Value).ToList();
                await GetService().Delete(idss);
            }
        }

        protected override async Task OnRowUpdating(JoinLogBrowserData dataItem, JoinLogBrowserData editModel)
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

        protected override  Task<BrowserDataPage<JoinLogBrowserData>> SearchRows(BrowserDataFilter filter)
        {
            return GetService().Search<JoinLogBrowserData>(filter);
        }

        protected override string FormatDateCellValue(string format, Object obj)
        {
            return base.FormatDateCellValue("dd/MM/yyyy hh:mm:ss", obj);

        }
    }
}
