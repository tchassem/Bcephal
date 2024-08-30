using Bcephal.Blazor.Web.Base.Services;
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
    public partial class InvoiceDetails : ComponentBase
    {

        [Inject]
        public AppState AppState { get; set; }

        [Parameter]
        public bool Editable { get; set; }

        [Parameter]
        public EditorData<Invoice> EditorData { get; set; }

        [Parameter]
        public EventCallback<EditorData<Invoice>> EditorDataChanged { get; set; }

        bool IsSmallScreen { get; set; }

        string labelSm = "mt-auto mb-auto ml-auto mr-1";
        string labelLg = "mt-auto mb-auto ml-auto mr-1";
        public string LengthItem1Lg = "0.3fr";
        public string LengthItem2Lg = "0.7fr ";

        string Reference
        {
            get
            {
                return EditorData.Item.Reference;
            }
            set
            {
                EditorData.Item.Reference = value;
                EditorDataChanged.InvokeAsync(EditorData);
            }
        }

        DateTime CreationDateTime
        {
            get
            {
                return EditorData.Item.CreationDateTime;
            }
            set
            {
                // EditorData.Item.CreationDateTime = value;
                EditorDataChanged.InvokeAsync(EditorData);
            }
        }

        DateTime? DueDate
        {
            get
            {
                return EditorData.Item.DueDate;
            }
            set
            {
                EditorData.Item.DueDate = value;
                EditorDataChanged.InvokeAsync(EditorData);
            }
        }

        string Currency
        {
            get
            {
                return EditorData.Item.AmountUnit;
            }
            set
            {
                EditorData.Item.AmountUnit = value;
                EditorDataChanged.InvokeAsync(EditorData);
            }
        }

        string Version
        {
            get
            {
                return EditorData.Item.Version.ToString();
            }
            set
            {
                EditorData.Item.Version = int.Parse(value);
                EditorDataChanged.InvokeAsync(EditorData);
            }
        }

        bool Modified
        {
            get
            {
                return EditorData.Item.ManuallyModified;
            }
            set
            {
                EditorData.Item.ManuallyModified = value;
                EditorDataChanged.InvokeAsync(EditorData);
            }
        }

        string Status
        {
            get
            {
                return EditorData.Item.Status;
            }
            set
            {
                EditorData.Item.Status = value;
                EditorDataChanged.InvokeAsync(EditorData);
            }
        }

        string Description
        {
            get
            {
                return EditorData.Item.Description;
            }
            set
            {
                EditorData.Item.Description = value;
                EditorDataChanged.InvokeAsync(EditorData);
            }
        }
    }
}
