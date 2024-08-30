using Bcephal.Blazor.Web.Base.Services;
using Bcephal.Blazor.Web.Base.Shared;
using Bcephal.Blazor.Web.Reporting.Services;
using Bcephal.Models.Base;
using Bcephal.Models.Billing;
using Bcephal.Models.Billing.Invoices;
using Bcephal.Models.socket;
using Microsoft.AspNetCore.Components;
using Microsoft.AspNetCore.Components.Forms;
using Microsoft.JSInterop;
using System;
using System.Collections.Generic;
using System.Collections.ObjectModel;
using System.IO;
using System.Linq;
using System.Reflection;
using System.Threading.Tasks;

namespace Bcephal.Blazor.Web.Billing.Pages.Billing.Template
{
    public partial class BillingTemplate : Form<BillTemplate, BillingTemplateBrowserData>
    {
        public override string LeftTitle { get { return AppState["New.billing.temp"]; ; } }

        public override string LeftTitleIcon { get { return "bi-file-plus"; } }

        public bool IsSmallScreen { get; set; }

        public string LabelWidth { get; set; } = Constant.GROUP_INFOS_LABEL_WIDTH;

        public string TextWidth { get; set; } = Constant.GROUP_INFOS_TEXT_WIDTH;
        public override int LeftSize { get; set; } = 9;
        public override int RightSize { get; set; } = 3;

        [Inject] 
        BillingTemplateService BillingTemplateService {get;set;}

        [Inject] 
        IJSRuntime JSRuntime { get;set;}

        [Parameter]
        public long? BillingTemplateId { get; set; }

        protected override BillingTemplateService GetService()
        {
            return BillingTemplateService;
        }

        public bool Editable
        {
            get
            {
                var first = AppState.PrivilegeObserver.CanCreatedBillingModel;
                var second = AppState.PrivilegeObserver.CanEditBillingModel(EditorData.Item);
                return first || second;
            }
        }

        public override string GetBrowserUrl { get => Route.BILLING_TEMPLATE_LIST; set => base.GetBrowserUrl = value; }
        public override async ValueTask DisposeAsync()
        {
            await base.DisposeAsync();
            AppState.Update = false;
            AppState.CanLoad = false;
        }

        protected override EditorDataFilter getEditorDataFilter()
        {
            return new EditorDataFilter();
        }

        private List<string> FileNames { get; set; }


        private int CheckColSpanMd = 8;
        private int ColSpanMd = 4;

        private string SelectedItem { get; set; }

        protected override async Task OnInitializedAsync()
        {
            EditorDataFilter filter = new EditorDataFilter();
            if (BillingTemplateId.HasValue)
            {
                filter.Id = BillingTemplateId.Value;
            }
            else
            {
                filter.NewData = true;
            }

            FileNames = new();

            EditorData = await BillingTemplateService.GetEditorData(filter);
            await base.OnInitializedAsync();

        }

        protected override async Task OnAfterRenderAsync(bool firstRender)
        {
            await base.OnAfterRenderAsync(firstRender);
            AppState.HideLoadingStatus();

        }

        private void UpdateFileNames(List<string> files)
        {
            JSRuntime.InvokeVoidAsync("count element in files________: " + files.Count() + "\n");
            FileNames = files;
            JSRuntime.InvokeVoidAsync("count element in FileNames------------------: " + FileNames.Count());
        }

        private string Name
        {
            get { return EditorData.Item.Name; }
            set
            {
                EditorData.Item.Name = value;
                StateHasChanged_();
            }
        }

        private string MainFile
        {
            get { return EditorData.Item.MainFile; }
            set
            {
                EditorData.Item.MainFile = value;
                AppState.Update = true;
                StateHasChanged_();
            }
        }

        private string Code
        {
            get { return !string.IsNullOrEmpty(EditorData.Item.Code) ? EditorData.Item.Code : ""; }
            set
            {
                EditorData.Item.Code = value;
                StateHasChanged_();
            }
        }

        private string Repository
        {
            get { return EditorData.Item.Repository; }
            set
            {
                EditorData.Item.Repository = value;
                StateHasChanged_();
            }
        }

        private bool VisibleInShortcut
        {
            get { return EditorData.Item.VisibleInShortcut; }
            set
            {
                EditorData.Item.VisibleInShortcut = value;
                StateHasChanged_();
            }
        }

        private bool SystemTemplate
        {
            get { return EditorData.Item.SystemTemplate; }
            set
            {
                EditorData.Item.SystemTemplate = value;
                StateHasChanged_();
            }
        }

        private string Description
        {
            get { return !string.IsNullOrEmpty(EditorData.Item.Description) ? EditorData.Item.Description : ""; }
            set
            {
                EditorData.Item.Description = value;
                StateHasChanged_();
            }
        }

        public void StateHasChanged_()
        {
            StateHasChanged();
            AppState.Update = true;
        }

        public void EditorDataHandler(EditorData<BillTemplate> editor)
        {
            EditorData.Item = editor.Item;
            StateHasChanged_();
        }

        public async void selectfile()
        {
            await JSRuntime.InvokeVoidAsync("OpenFileUpload");
        }

        byte[] zipSize;
        public async Task UpdateFileNames(Dictionary<string, string> res)
        {
            Dictionary<string, byte[]> files = new();

            try
            {
                FileNames.Clear();
                foreach (var filePath in res.Keys)
                {
                    if(FileNames.IndexOf(filePath) < 0)
                    {
                        FileNames.Add(filePath);
                    }

                    int index = res.Keys.ToList().IndexOf(filePath);
                    string database64 = res.Values.ToList().ElementAt(index);

                    database64 = database64.Split(',')[1];

                    byte[] bytes = Convert.FromBase64String(database64);

                    files.Add(filePath, bytes);
                }
                // --- zipSize = await BillingTemplateService.ZipFiles_(files);
                zipSize = BillingTemplateService.ZipFiles(files);
                files.Clear();
                await Task.CompletedTask;
            }
            catch (Exception e)
            {
                Error.ProcessError(e);
                StateHasChanged();
            }
        }

        protected async override void save()
        {
            if(zipSize != null && zipSize.Length > 0)
            {
                DataTransfert res = await GetService().Upload(new DataTransfert() { Name="template.zip", Data=zipSize, Folder=Path.GetTempPath(), decision=Decision.NEW });
                if (string.IsNullOrEmpty(EditorData.Item.Repository))
                {
                    EditorData.Item.Repository = res.RemotePath;
                }
            }
            zipSize = null;
            base.save();
        }

    }
}
