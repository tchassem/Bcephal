using Bcephal.Blazor.Web.Base.Services;
using Bcephal.Blazor.Web.Base.Shared;
using Bcephal.Blazor.Web.Billing.Services;
using Bcephal.Models.Base;
using Bcephal.Models.Billing.Invoices;
using Microsoft.AspNetCore.Components;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Bcephal.Blazor.Web.Billing.Pages.Billing.Invoices
{
    public partial class InvoiceForm : Form<Invoice, InvoiceBrowserData>
    {
        [Inject] 
        public InvoiceService InvoiceService { get; set; }

        public override string LeftTitle { get { return AppState["Invoice"]; } }
        public override string LeftTitleIcon { get { return "bi-file-plus"; } }
        public string heigth { get; set; } = "var(--grid-bc-two)";
        public override bool usingUnitPane => true;
        public override string GetBrowserUrl { get => Route.LIST_INVOICE; set => base.GetBrowserUrl = value; }
        protected override string DuplicateName()
        {
            return AppState["duplicate.invoice.name", EditorData.Item.Name];
        }

        public bool Editable { get; set; } = true;
        public bool IsSmallScreen { get; set; }
        public string LabelWidth { get; set; } = Constant.GROUP_INFOS_LABEL_WIDTH;
        public string TextWidth { get; set; } = Constant.GROUP_INFOS_TEXT_WIDTH;
        protected override EditorDataFilter getEditorDataFilter()
        {
            return new EditorDataFilter();
        }

        protected override InvoiceService GetService()
        {
            return InvoiceService;
        }

        private bool ShouldRender_ { get; set; } = true;
        protected override bool ShouldRender()
        {
            return ShouldRender_;
        }

        protected override Task OnAfterRenderAsync(bool firstRender)
        {
            ShouldRender_ = EditorDataBinding == null;
            return base.OnAfterRenderAsync(firstRender);
        }

        
    }
}
