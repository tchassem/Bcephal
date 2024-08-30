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
    public partial class InvoiceClient : ComponentBase
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

        private string ClientNumber
        {
            get { return EditorData.Item.ClientNumber; }
            set
            {
                EditorData.Item.ClientNumber = value;
                EditorDataChanged.InvokeAsync(EditorData);
            }
        }

        private string ClientName
        {
            get { return EditorData.Item.ClientName; }
            set
            {
                EditorData.Item.ClientName = value;
                EditorDataChanged.InvokeAsync(EditorData);
            }
        }

        private string ClientAdressStreet
        {
            get { return EditorData.Item.ClientAdressStreet; }
            set
            {
                EditorData.Item.ClientAdressStreet = value;
                EditorDataChanged.InvokeAsync(EditorData);
            }
        }

        private string ClientAdressPostalCode
        {
            get { return EditorData.Item.ClientAdressPostalCode; }
            set
            {
                EditorData.Item.ClientAdressPostalCode = value;
                EditorDataChanged.InvokeAsync(EditorData);
            }
        }

        private string ClientAdressCity
        {
            get { return EditorData.Item.ClientAdressCity; }
            set
            {
                EditorData.Item.ClientAdressCity = value;
                EditorDataChanged.InvokeAsync(EditorData);
            }
        }

        private string ClientAdressCountry
        {
            get { return EditorData.Item.ClientAdressCountry; }
            set
            {
                EditorData.Item.ClientAdressCountry = value;
                EditorDataChanged.InvokeAsync(EditorData);
            }
        }

        private string ClientEmail
        {
            get { return EditorData.Item.ClientEmail; }
            set
            {
                EditorData.Item.ClientEmail = value;
                EditorDataChanged.InvokeAsync(EditorData);
            }
        }

        private string ClientPhone
        {
            get { return EditorData.Item.ClientPhone; }
            set
            {
                EditorData.Item.ClientPhone = value;
                EditorDataChanged.InvokeAsync(EditorData);
            }
        }

        private string ClientLanguage
        {
            get { return EditorData.Item.ClientLanguage; }
            set
            {
                EditorData.Item.ClientLanguage = value;
                EditorDataChanged.InvokeAsync(EditorData);
            }
        }
    }
}
