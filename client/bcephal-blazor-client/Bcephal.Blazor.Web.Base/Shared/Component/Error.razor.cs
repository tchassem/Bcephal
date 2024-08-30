using Bcephal.Blazor.Web.Base.Services;
using Bcephal.Models.Exceptions;
using Microsoft.AspNetCore.Components;
using Microsoft.Extensions.Logging;
using Microsoft.JSInterop;
using System;

namespace Bcephal.Blazor.Web.Base.Shared.Component
{
    public partial class Error : ComponentBase
    {
        [Parameter] public RenderFragment ChildContent { get; set; }
        [Inject] public IJSRuntime JSRuntime { get; set; }
        [Inject] IToastService ToastService { get; set; }
        [Inject] public AppState AppState { get; set; }
        [Inject] private ILogger<Error> Logger { get; set; }

        public void ProcessError(Exception ex)
        {
            if(ex == null)
            {
                Logger.LogError("Error:ProcessError - Type: {}", ex);
                ToastService.ShowError("Error");
            }
            Logger.LogError("Error:ProcessError - Type: {Type} Message: {Message}",
                ex.GetType(), ex.Message);
            Logger.LogError("Error:ProcessError - Type: {}",ex);
            if (ex is BcephalException && ex.Message.Equals("!!==== ResetSession ====!!"))
            {
                AppState.ResetSession();
                return;
            }
            string message = ex.Message;
            if (ex is BcephalException)
            {

                BcephalException ex_ = (BcephalException)ex;
                if (ex_.status.HasValue)
                {
                    switch (ex_.status.Value)
                    {
                        case ((int)System.Net.HttpStatusCode.ServiceUnavailable):
                            message = AppState["HttpServiceUnavailable"];
                            break;
                        case ((int)System.Net.HttpStatusCode.RequestTimeout):
                            message = AppState["HttpServiceRequestTimeout"];
                            break;
                    }
                }
            }
            if ("duplicate.name".Equals(message))
            {
                string val = AppState.DuplicateName();
                if (!string.IsNullOrEmpty(val))
                {
                    message = val;
                }
                else
                {
                    message = AppState[message];
                }
            }
            else if ("unable.to.rename.project".Equals(message))
            {
                message = AppState[message];
            }
            else if ("unable.to.get.rows.dashboard.data".Equals(message))
            {
                message = AppState[message];
            }
            else if ("unable.to.save.entity".Equals(message))
            {
                message = AppState[message];
            }           
            else if ("unable.to.search.entity.by.filter".Equals(message))
            {
                message = AppState[message];
            }           
            else if ("unable.to.retreive.user.info".Equals(message))
            {
                message = AppState[message];
            }
            else if ("unable.to.retreive.user.clients".Equals(message))
            {
                message = AppState[message];
            }
            else if ("unable.to.retreive.user.privileges".Equals(message))
            {
                message = AppState[message];
            }
            else if ("unable.to.save.profile.projects".Equals(message))
            {
                message = AppState[message];
            }
            ToastService.ShowError(message);
            AppState.HideLoadingStatus();
        }
    }
}
