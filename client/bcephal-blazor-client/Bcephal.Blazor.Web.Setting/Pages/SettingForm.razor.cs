using Bcephal.Blazor.Web.Base.Services;
using Bcephal.Blazor.Web.Base.Shared;
using Bcephal.Blazor.Web.Setting.Services;
using Bcephal.Models.Base;
using Bcephal.Models.Settings;
using Microsoft.AspNetCore.Components;
using System;
using System.Collections.Generic;
using System.Collections.ObjectModel;
using System.Globalization;
using System.Text.RegularExpressions;
using System.Threading;
using System.Threading.Tasks;

namespace Bcephal.Blazor.Web.Setting.Pages
{
    public partial class SettingForm : Form<Parameter, BrowserData>
    {
        [Inject]
        public ParameterService ParameterService { get; set; }
        public override string LeftTitle { get { return AppState["settings"]; ; } }
        public override string LeftTitleIcon { get { return "bi-gear"; } }
        public override bool usingUnitPane => false;
        public Parameter Parameter_ { get; set; }

        int ActiveTabIndex_ = 0;
        protected override EditorDataFilter getEditorDataFilter()
        {
            return new EditorDataFilter();
        }
        public override RenderFragment RightContent => null;
        protected override void AfterInit(EditorData<Parameter> EditorData)
        {
            base.AfterInit(EditorData);
            Parameter_ = EditorData.Item;
        }

        protected override Task BeforeSave(EditorData<Parameter> EditorData)
        {
            if (EditorData.Item.Equals(EditorData.Item))
            {
                Parameter_ = EditorData.Item;
            }
            return base.BeforeSave(EditorData);
        }


        protected override void AfterSave(EditorData<Parameter> EditorData)
        {
            EditorData.Item = Parameter_;
        }

        protected override Service<Parameter, BrowserData> GetService()
        {
            return ParameterService;
        }

        public async Task<EditorData<Parameter>> CreateAutomatically(ParameterGroup parameterGroup)
        {
            AppState.ShowLoadingStatus();
            EditorData = await ParameterService.BuildAutomatically(parameterGroup.Code);
            EditorDataBinding = EditorData;
            AppState.HideLoadingStatus();
            ToastService.ShowSuccess(AppState["SuccessfullyCreateAutomatically"]);
            initEntities_();
            return EditorData;
        }
     
        protected override async Task OnAfterRenderAsync(bool firstRender)
        {
            await base.OnAfterRenderAsync(firstRender);
            if (firstRender)
            {
                RefreshRightContent(null);
            }
            AppState.Update = true;
        }
        public void Dispose()
        {
            AppState.Update = false;

        }
        public void OnActiveTabChanged(int value)
        {
            ActiveTabIndex_ = value;
        }

        protected override async void save()
        {
            if (EditorData != null && EditorData.Item != null)
            {
                try
                {
                    AppState.ShowLoadingStatus();
                    AppState.Update = false;
                    await BeforeSave(EditorData);
                    EditorData.Item = await GetService().Save(EditorData.Item);
                    AfterSave(EditorData);
                    ToastService.ShowSuccess(AppState["save.SuccessfullyAdd", LeftTitlePage]);
                }
                catch (Exception ex)
                {
                    AppState.Update = true;
                    Error.ProcessError(ex);
                }
                finally
                {
                    AppState.HideLoadingStatus();
                    AppState.Update = true;
                    // StateHasChanged();
                }
            }
        }

    }
}
