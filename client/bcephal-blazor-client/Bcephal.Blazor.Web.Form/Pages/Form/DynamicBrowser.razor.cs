using Bcephal.Blazor.Web.Base.Services;
using Bcephal.Blazor.Web.Base.Shared;
using Bcephal.Blazor.Web.Base.Shared.AbstractComponent;
using Bcephal.Blazor.Web.Base.Shared.Component;
using Bcephal.Blazor.Web.Form.Services;
using Bcephal.Blazor.Web.Sourcing.Services;
using Bcephal.Models.Base;
using Bcephal.Models.Forms;
using Bcephal.Models.Grids;
using Bcephal.Models.Grids.Filters;
using DevExtreme.AspNet.Data;
using Microsoft.AspNetCore.Components;
using System;
using System.Collections.Generic;
using System.Threading.Tasks;

namespace Bcephal.Blazor.Web.Form.Pages.Form
{
    public partial  class DynamicBrowser  
    {
        [Inject]
        private FormModelService FormService { get; set; }

        [CascadingParameter]
        public Error Error { get; set; }
        [Parameter]
        public long? Id { get; set; }
        [Inject]
        public AppState AppState { get; set; }
        [Inject]
        public IToastService toastService { get; set; }
        public EditorData<FormModel> EditorData { get; set; }
        protected override async Task OnParametersSetAsync()
        {
            await base.OnParametersSetAsync();
            await init();
        }

        private async Task init()
        {
            try
            {
                await initComponent();
            }
            catch (Exception ex)
            {
                Error.ProcessError(ex);
                StateHasChanged();
            }
        }
        protected virtual async Task initComponent()
        {
            try
            {
                AppState.ShowLoadingStatus();
                if (EditorData == null)
                {
                    EditorDataFilter filter = new EditorDataFilter();
                    filter.NewData = true;
                    if (Id.HasValue)
                    {
                        filter.NewData = false;
                        filter.Id = Id;
                    }
                    EditorData = await FormService.GetEditorData(filter);                    
                    AppState.Update = false;
                }
                AppState.HideLoadingStatus();
                StateHasChanged();
            }
            catch (Exception ex)
            {
                Error.ProcessError(ex);
                StateHasChanged();
            }
        }
    }
}
