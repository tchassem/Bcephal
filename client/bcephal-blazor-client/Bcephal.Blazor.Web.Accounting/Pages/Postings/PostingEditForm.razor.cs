using Bcephal.Blazor.Web.Accounting.Services;
using Bcephal.Blazor.Web.Base.Services;
using Bcephal.Blazor.Web.Base.Shared;
// using Bcephal.Blazor.Web.Base.Shared.AbstractComponent;
using Bcephal.Models.Accounting;
using Bcephal.Models.Base;
using Bcephal.Models.Base.Accounting;
using Bcephal.Models.Grids.Filters;
using DevExtreme.AspNet.Data;
using Microsoft.AspNetCore.Components;
using Microsoft.AspNetCore.Components.Forms;
using Newtonsoft.Json;
using System;
using System.Collections.Generic;
using System.Collections.ObjectModel;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Bcephal.Blazor.Web.Accounting.Pages.Postings
{
    public partial class PostingEditForm : Form<Posting, PostingBrowserData>
    {

        [Inject]
        public PostingService PostingService { get; set; }

        private bool IsValidated = false;

        private string PId
        {
            get
            {
                if (this.EditorData.Item != null && this.EditorData.Item.Id != null)
                {
                    return this.EditorData.Item.Id.Value.ToString();
                }
                return "";
            }
            set
            {
                if (this.EditorData.Item == null)
                {
                    this.EditorData.Item = new Posting();
                }
                this.EditorData.Item.Id = long.Parse(value);
                AppState.Update = true;
            }
        }

        private PostingStatus PStatus
        {
            get
            {
                if (this.EditorData.Item != null)
                {
                    return this.EditorData.Item.Status;
                }
                return null;
            }
            set
            {
                if (this.EditorData.Item == null)
                {
                    this.EditorData.Item = new Posting();
                }
                this.EditorData.Item.Status = value;
                AppState.Update = true;
            }
        }

        private DateTime ValueDate
        {
            get
            {
                if (this.EditorData.Item != null)
                {
                    return this.EditorData.Item.ValueDate;
                }
                return DateTime.Now;
            }
            set
            {
                if (this.EditorData.Item == null)
                {
                    this.EditorData.Item = new Posting();
                }
                this.EditorData.Item.ValueDate = value;
                AppState.Update = true;
            }
        }

        private DateTime EntryDate
        {
            get
            {
                if (this.EditorData.Item != null)
                {
                    return this.EditorData.Item.EntryDate;
                }
                return DateTime.Now;
            }
            set
            {
                if (this.EditorData.Item == null)
                {
                    this.EditorData.Item = new Posting();
                }
                this.EditorData.Item.EntryDate = value;
                AppState.Update = true;
            }
        }

        private string Description
        {
            get
            {
                if (this.EditorData.Item != null)
                {
                    return this.EditorData.Item.comment;
                }
                return "";
            }
            set
            {
                if (this.EditorData.Item == null)
                {
                    this.EditorData.Item = new Posting();
                }
                this.EditorData.Item.comment = value;
                AppState.Update = true;
            }
        }

        IEnumerable<PostingEntry> PostingEntryDataSource = new List<PostingEntry>().AsEnumerable();

        Posting posting = new Posting();
        List<Account> Accounts = new List<Account>();

        private EditContext editContext;

        bool IsSmallScreen { get; set; }

        public string SumCreditAmount = "0";
        public string SumDebitAmount = "0";
        public string BalanceAmount = "0";
        public override string CssClass { get; set; } = "w-100 m-0 p-0";

        public string ItemSpacing { get; set; } = "1px";

        public string labelSm = "0.1fr";
        public string labelLg = "0.9fr";

        public override string LeftTitle => AppState["Posting.Edit"];

        RenderFragment SpanRender;

        public override Task SetParametersAsync(ParameterView parameters)
        {
            usingMixPane = false;
            displayRight = DISPLAY_NONE;
            displayLeft = WIDTH_100;
            return base.SetParametersAsync(parameters);
        }

        protected override PostingService GetService()
        {
            return PostingService;
        }

        protected override EditorDataFilter getEditorDataFilter()
        {
            return new EditorDataFilter();
        }

        protected override void OnInitialized()
        {
            AppState.CanCreate = true;
            AppState.CanValidate = true;
            AppState.ValidateHandler -= PostingValidation;
            AppState.CanReset = true;
            AppState.ResetHandler -= PostingUnValidation;
            base.OnInitialized();
        }

        public override ValueTask DisposeAsync()
        {
            AppState.CanCreate = false;
            AppState.Update = false;
            AppState.CanValidate = false;
            AppState.ValidateHandler += PostingValidation;
            AppState.CanReset = false;
            AppState.ResetHandler += PostingUnValidation;
            return base.DisposeAsync();
        }

        // -------------------------------------------------------------------------------------------------------------------------------------------------------------------
        PostingEditorData GetPostingEditorData => (PostingEditorData)EditorData;
        protected override void AfterInit(EditorData<Posting> postingEditorData)
        {
            IsValidated = EditorData.Item.Status == PostingStatus.DRAFT ? false : true;
            AppState.CanValidate = !IsValidated;
            AppState.CanReset = IsValidated;
            if (postingEditorData.Item != null || (GetPostingEditorData.accounts != null && GetPostingEditorData.accounts.Count > 0))
            {
                if (postingEditorData.Item.Id != null)
                {
                    this.SetDataSource(GetPostingEditorData.Item.entryListChangeHandler.Items);
                    this.AmountCompile(GetPostingEditorData.Item.entryListChangeHandler.Items);
                }
                this.Accounts = GetPostingEditorData.accounts.ToList();
            }
        }

        protected override void AfterSave(EditorData<Posting> EditorData)
        {
            this.AfterInit(this.EditorData);
            StateHasChanged();
        }

        private void SetDataSource(ObservableCollection<PostingEntry> postingEntries)
        {
            this.PostingEntryDataSource = postingEntries.ToList().AsEnumerable();
        }

        private void AmountCompile(ObservableCollection<PostingEntry> postingEntries)
        {
            SumCreditAmount = postingEntries.Where(p => p.Sign.Equals(PostingSign.CREDIT)).Sum((p) => p.Amount).ToString();
            SumDebitAmount = postingEntries.Where(p => p.Sign.Equals(PostingSign.DEBIT)).Sum((p) => p.Amount).ToString();
            BalanceAmount = (double.Parse(SumCreditAmount) - double.Parse(SumDebitAmount)).ToString();
        }

        private async void PostingValidation()
        {
            EditorData.Item = await PostingService.Validation(EditorData.Item.Id.Value);
            this.AfterInit(this.EditorData);
            StateHasChanged();
        }

        private async void PostingUnValidation()
        {
            bool result = await PostingService.ResetValidation(EditorData.Item.Id.Value);
            if(result)
            {
                this.EditorData.Item = await PostingService.getById(this.EditorData.Item.Id.Value);
            }            
            this.AfterInit(this.EditorData);
            StateHasChanged();
        }
    }
}
