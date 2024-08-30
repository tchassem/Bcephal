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
    public partial class InvoiceCompany : ComponentBase
    {

        [Inject]
        public AppState AppState { get; set; }

        [Parameter]
        public bool Editable { get; set; }

        [Parameter]
        public EditorData<Invoice> EditorData { get; set; }

        [Parameter]
        public EventCallback<EditorData<Invoice>> EditorDataChanged { get; set; }

        bool IsXSmallScreen;
        string labelSm = "mt-auto mb-auto ml-auto mr-1";
        string labelLg = "mt-auto mb-auto ml-auto mr-1";
        public string LengthItem1Lg = "0.3fr";
        public string LengthItem2Lg = "0.7fr ";

        private string BillingCompanyNumber
        {
            get { return EditorData.Item.BillingCompanyNumber; }
            set
            {
                EditorData.Item.BillingCompanyNumber = value;
                EditorDataChanged.InvokeAsync(EditorData);
            }
        }

        private string BillingCompanyName
        {
            get { return EditorData.Item.BillingCompanyName; }
            set
            {
                EditorData.Item.BillingCompanyName = value;
                EditorDataChanged.InvokeAsync(EditorData);
            }
        }

        private string BillingCompanyAdressStreet
        {
            get { return EditorData.Item.BillingCompanyAdressStreet; }
            set
            {
                EditorData.Item.BillingCompanyAdressStreet = value;
                EditorDataChanged.InvokeAsync(EditorData);
            }
        }

        private string BillingCompanyAdressPostalCode
        {
            get { return EditorData.Item.BillingCompanyAdressPostalCode; }
            set
            {
                EditorData.Item.BillingCompanyAdressPostalCode = value;
                EditorDataChanged.InvokeAsync(EditorData);
            }
        }

        private string BillingCompanyAdressCity
        {
            get { return EditorData.Item.BillingCompanyAdressCity; }
            set
            {
                EditorData.Item.BillingCompanyAdressCity = value;
                EditorDataChanged.InvokeAsync(EditorData);
            }
        }

        private string BillingCompanyAdressCountry
        {
            get { return EditorData.Item.BillingCompanyAdressCountry; }
            set
            {
                EditorData.Item.BillingCompanyAdressCountry = value;
                EditorDataChanged.InvokeAsync(EditorData);
            }
        }

        private string BillingCompanyEmail
        {
            get { return EditorData.Item.BillingCompanyEmail; }
            set
            {
                EditorData.Item.BillingCompanyEmail = value;
                EditorDataChanged.InvokeAsync(EditorData);
            }
        }

        private string BillingCompanyPhone
        {
            get { return EditorData.Item.BillingCompanyPhone; }
            set
            {
                EditorData.Item.BillingCompanyPhone = value;
                EditorDataChanged.InvokeAsync(EditorData);
            }
        }
    }
}
