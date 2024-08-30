using Microsoft.AspNetCore.Components;
using System;

using Bcephal.Models.Functionalities;
using Microsoft.Extensions.Localization;
using Microsoft.JSInterop;
using Bcephal.Blazor.Web.Base.Services;

namespace Bcephal.Blazor.Web.Base.Shared.Functionalities
{
    public partial class FunctionalityBlockGroup : ComponentBase
    {
        [Inject]
        AppState AppState_ { get; set; }

        [Inject]
        IToastService toastService { get; set; }

        [Inject]
        FunctionalityService FunctionalityService { get; set; }

        [Inject]
        FunctionnalityWorkspaceService FunctionnalityWorkspaceService { get; set; }

        [Parameter]
        public string Title { get; set; }

        bool isXSmallScreen { get; set; }


        [Parameter]

        public Models.Functionalities.FunctionalityBlockGroup CurrentItem { get; set; }

        [Parameter]

        public EventCallback<Models.Functionalities.FunctionalityBlockGroup> CurrentItemChanged{ get; set; }


        [Inject]
        private IJSRuntime JSRuntime { get; set; }

       [Parameter]
       public EventCallback<Models.Functionalities.FunctionalityBlockGroup> DeleteFunctionalityBlockGroup { get; set; }
       

        public void Dispose()
        {

        }

        public async void DeleteFunctionalityBlock(FunctionalityBlock FBlock)
        {
            try
            {
                if(CurrentItem != null && AppState_.ProjectId.HasValue)
                {
                    CurrentItem.DeleteOrForgetBlock(FBlock);
                    await CurrentItemChanged.InvokeAsync(CurrentItem);
                   // CurrentItemChangedHandler.Invoke(CurrentItem);
                    CurrentItem = await FunctionalityService.SaveGroup(CurrentItem, AppState_.ProjectId.Value.ToString());
                    StateHasChanged();
                    toastService.ShowSuccess(AppState_["Functionality.SuccessfullyDeleted"]);
                }
               
            }
            catch(Exception e)
            {
                toastService.ShowError(e.Message);
            }
               

           
        }

        public async void UpdateFunctionalityBlock(FunctionalityBlock FBlock)
        {
           if(CurrentItem != null && AppState_.ProjectId.HasValue)
            {
                CurrentItem.UpdateBlock(FBlock);
                await FunctionalityService.SaveGroup(CurrentItem, AppState_.ProjectId.Value.ToString());
                await CurrentItemChanged.InvokeAsync(CurrentItem);
                //CurrentItemChangedHandler.Invoke(CurrentItem);
            }
         
        }
    }
}
