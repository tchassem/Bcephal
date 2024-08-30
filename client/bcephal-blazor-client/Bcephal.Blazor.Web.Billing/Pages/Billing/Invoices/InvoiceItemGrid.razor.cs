using Bcephal.Blazor.Web.Base.Services;
using Bcephal.Blazor.Web.Base.Shared.AbstractComponent;
using Bcephal.Models.Base;
using Bcephal.Models.Billing.Invoices;
using Bcephal.Models.Grids.Filters;
using DevExpress.Blazor;
using Microsoft.AspNetCore.Components;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Bcephal.Blazor.Web.Billing.Pages.Billing.Invoices
{
    public partial class InvoiceItemGrid : AbstractNewGridComponent<Invoice, InvoiceItem>
    {

        [Parameter]
        public bool Editable { get; set; }
        protected dynamic[] GridColumns => new[] {
            new {CaptionName = AppState["Description"], ColumnWidth="10%", ColumnName = nameof(InvoiceItem.Description), ColumnType = typeof(string),ColumnFormat=""},
            new {CaptionName = AppState["amount.without.vat"], ColumnWidth="20%", ColumnName = nameof(InvoiceItem.Amount), ColumnType = typeof(decimal),ColumnFormat="c"},
            new {CaptionName = AppState["vat.rate"], ColumnWidth="5%", ColumnName = nameof(InvoiceItem.VatRate), ColumnType = typeof(decimal),ColumnFormat="p2"},
            new {CaptionName = AppState["vat.amount"], ColumnWidth="5%", ColumnName = AppState["vat.amount"], ColumnType = typeof(decimal),ColumnFormat="{0:c}"},
            new {CaptionName = AppState["total"], ColumnWidth="5%", ColumnName = AppState["total"], ColumnType = typeof(decimal),ColumnFormat="{0:c}"}
        };

        //protected dynamic[] ItemsSummaryColumns => new[] {
        //    new {SummaryType=GridSummaryItemType.Sum, FieldName=nameof(InvoiceItem.Amount), ValueDisplayFormat="c", Name="SumAmount"},
        //    new {SummaryType=GridSummaryItemType.Sum, FieldName="VatAmount", ValueDisplayFormat="c", Name="SumVatAmount"},
        //    new {SummaryType=GridSummaryItemType.Sum, FieldName="Total", ValueDisplayFormat="c", Name="SumTotal"}
        //};

        protected override int ItemsCount => GridColumns.Length;

        //protected override int ItemsSummaryCount => ItemsSummaryColumns.Length;


        private object GetPropertyValue(InvoiceItem obj, string propName)
        {
            if (propName.Equals(AppState["vat.amount"]))
            {
                var amount = Convert.ToDecimal(obj.GetPropertyValue(nameof(InvoiceItem.Amount)));
                var vat = Convert.ToDecimal(obj.GetPropertyValue(nameof(InvoiceItem.VatRate)));
                return Convert.ToDecimal((amount * vat) / 100);
            }
            if (propName.Equals(AppState["total"]))
            {
                var amount = Convert.ToDecimal(obj.GetPropertyValue(nameof(InvoiceItem.Amount)));
                var vat = Convert.ToDecimal(obj.GetPropertyValue(nameof(InvoiceItem.VatRate)));
                var vat_amount = Convert.ToDecimal((amount * vat) / 100);
                return amount + vat_amount;
            }
            return obj.GetPropertyValue(propName);
        }
        protected override dynamic GetSummaryData(string[] columnName, InvoiceItem item)
        {
            
            var ob = item.GetPropertyValue(nameof(InvoiceItem.Amount));
            var Amount = ob == null ? 0 : Convert.ToDecimal(ob);
            var ob2 = item.GetPropertyValue(nameof(InvoiceItem.VatRate));
            var vat = ob2== null ? 0 : Convert.ToDecimal(ob2);
            var VatAmount = Convert.ToDecimal((Amount * vat) / 100);
            var total = Amount + VatAmount;
            return new { Amount = Amount, VatAmount = VatAmount, Total = total };
        }
        protected override AbstractNewGridDataItem GetGridDataItem(int Position)
        {
            return new CustomizeSummaryNewGridDataItem(GridColumns[Position], Position);
        }
        //protected override SummaryDataItem GetSummaryItem(int Position)
        //{
        //    return new SummaryDataItem(ItemsSummaryColumns[Position], Position);
        //}

        protected override object GetFieldValue(InvoiceItem item, int grilleColumnPosition)
        {
            return GetPropertyValue(item, GridColumns[grilleColumnPosition].ColumnName);
        }

        protected override string KeyFieldName()
        {
            return nameof(InvoiceItem.Id);
        }

        protected override Task OnRowInserting(InvoiceItem newValues)
        {
            return Task.CompletedTask;
        }

        protected override object KeyFieldValue(InvoiceItem item)
        {
            return item.Id;
        }

        protected override string NavLinkURI()
        {
            return null;
        }

        protected override  Task OnRowRemoving(InvoiceItem dataItem)
        {
            //await GetService().Delete(new List<long>() { dataItem.Id.Value });
            return Task.CompletedTask;
        }
        protected override  Task OnRowRemoving(IReadOnlyList<object> ids)
        {
            //if (ids != null && ids.Count > 0)
            //{
            //    var idss = ids.Select(obj => ((InvoiceItem)obj).Id.Value).ToList();
            //    await GetService().Delete(idss);
            //}
            return Task.CompletedTask;
        }

        protected async override Task OnRowUpdating(InvoiceItem dataItem, InvoiceItem newValues)
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

        protected override Task<BrowserDataPage<InvoiceItem>> SearchRows(BrowserDataFilter filter)
        {
            BrowserDataPage<InvoiceItem> page = new BrowserDataPage<InvoiceItem>();
            if (EditorData != null && EditorData.Item != null)
            {
                page.Items = EditorData.Item.ItemListChangeHandler.Items;
                page.TotalItemCount = page.Items.Count;
            }
            return Task.FromResult(page);
        }



        //protected override void Grid_CustomUnboundColumnData(GridUnboundColumnDataEventArgs e)
        //{
        //    if (e.FieldName == AppState["vat.amount"])
        //    {
        //        var amount = Convert.ToDecimal(e.GetRowValue(nameof(InvoiceItem.Amount)));
        //        var vat = Convert.ToDecimal(e.GetRowValue(nameof(InvoiceItem.VatRate)));
        //        e.Value = Convert.ToDecimal((amount * vat) / 100);
        //    }
        //    if (e.FieldName == AppState["total"])
        //    {
        //        var amount = Convert.ToDecimal(e.GetRowValue(nameof(InvoiceItem.Amount)));
        //        var vat_amount = Convert.ToDecimal(e.GetRowValue(AppState["vat.amount"]));
        //        e.Value = amount + vat_amount;
        //    }
        //}

        protected override void Grid_CustomizeSummaryDisplayText(GridCustomizeSummaryDisplayTextEventArgs e)
        {
            if (e.Item.Name == "SumAmount")
                e.DisplayText = string.Format("{0:c}", e.Value);
            if (e.Item.Name == "SumVatAmount")
                e.DisplayText = string.Format("{0:c}", e.Value);
            if (e.Item.Name == "SumTotal")
                e.DisplayText = string.Format("{0:c}", e.Value);
        }
    }

    public class CustomizeSummaryNewGridDataItem : NewGridDataItem
    {
        public CustomizeSummaryNewGridDataItem(object GridColumns, int position) : base(GridColumns, position)
        {

        }
        public override string ColumnFormat
        {
            get

            {
                if (Position == 2)
                {
                    return "p2";
                }
                if (Position == 0)
                {
                    return null;
                }
                else
                    return "c";
            }
        }
    }
}
