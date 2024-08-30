using Bcephal.Blazor.Web.Base.Services;
using Bcephal.Blazor.Web.Base.Shared.Component;
using Microsoft.AspNetCore.Components;
using System;
using System.Threading.Tasks;

namespace Bcephal.Blazor.Web.Reconciliation.Shared.Component.Reconciliation
{
    public partial class RecoNewCustomToolBar
    {
        [Inject]
        AppState AppState { get; set; }

        [CascadingParameter]
        public Error Error { get; set; }

        public bool ShowConfirmedResetPopup { get; set; }
        bool IsConfirmation { get; set; } = true;

        [Parameter]
        public bool CanRun { get; set; } = false;
        [Parameter]
        public bool CanReset { get; set; } = false;
        [Parameter]
        public bool CanDelete { get; set; } = false;

        [Parameter]
        public bool CanFreeze { get; set; } = false;

        [Parameter]
        public bool CanUnFreeze { get; set; } = false;

        [Parameter]
        public bool CanNeutralization { get; set; } = false;

        [Parameter]
        public bool CanUnNeutralization { get; set; } = false;


        [Parameter]
        public bool DisplayToobar { get; set; } = false;


        [Parameter]
        public EventCallback<bool> CanRunChanged { get; set; }
        [Parameter]
        public EventCallback<bool> CanResetChanged { get; set; }
        [Parameter]
        public EventCallback<bool> CanDeleteChanged { get; set; }

        [Parameter]
        public EventCallback<bool> CanFreezeChanged { get; set; }
        [Parameter]
        public EventCallback<bool> CanUnFreezeChanged { get; set; }

        [Parameter]
        public EventCallback<bool> CanNeutralizationChanged { get; set; }
        [Parameter]
        public EventCallback<bool> CanUnNeutralizationChanged { get; set; }

        [Parameter]
        public Func<Task> RunFreezeHander { get; set; }
        [Parameter]
        public Func<Task> RunUnFreezeHander { get; set; }

        [Parameter]
        public Func<Task> NeutralizationHander { get; set; }
        [Parameter]
        public Func<Task> UnNeutralizationHander { get; set; }

        [Parameter]
        public Func<Task> RunHande { get; set; }
        [Parameter]
        public Func<Task> ResetHande { get; set; }
        [Parameter]
        public Func<Task> ClearGridHande { get; set; }

        [Parameter]
        public string LeftValue { get; set; } = "0";
        [Parameter]
        public EventCallback<string> LeftValueChanged { get; set; }
        [Parameter]
        public string RightValue { get; set; } = "0";
        [Parameter]
        public EventCallback<string> RightValueChanged { get; set; }
        [Parameter]
        public string BalanceValue { get; set; } = "0";
        [Parameter]
        public EventCallback<string> BalanceValueChanged { get; set; }

        [Parameter]
        public string CreditLabel { get; set; } = "left";
        [Parameter]
        public EventCallback<string> CreditLabelChanged { get; set; }
        [Parameter]
        public string DebitLabel { get; set; } = "right";
        [Parameter]
        public EventCallback<string> DebitLabelChanged { get; set; }
        [Parameter]
        public string BalanceLabel { get; set; } = "balance";
        [Parameter]
        public EventCallback<string> BalanceLabelChanged { get; set; }


        public void Refresh() => StateHasChanged();

       async void RunEvent()
        {
            try
            {
               await RunHande?.Invoke();
            }
            catch (Exception ex)
            {
                Error.ProcessError(ex);
            }
        }

      async void ClearGridEvent()
        {
            try
            {
              await  ClearGridHande?.Invoke();
            }
            catch (Exception ex)
            {
                Error.ProcessError(ex);
            }
        }

        public void ConfirmReset()
        {
            ShowConfirmedResetPopup = true;
        }


      async  void ResetEvent()
        {
            ShowConfirmedResetPopup = false;
            try
            {
              await  ResetHande?.Invoke();
            }
            catch (Exception ex)
            {
                Error.ProcessError(ex);
            }
        }

      async  void RunFreeze()
        {
            try
            {
             await   RunFreezeHander?.Invoke();
            }
            catch (Exception ex)
            {
                Error.ProcessError(ex);
            }
        }

       async void RunUnFreeze()
        {
            try
            {
              await  RunUnFreezeHander?.Invoke();
            }
            catch (Exception ex)
            {
                Error.ProcessError(ex);
            }
        }

     async   void Neutralization()
        {
            try
            {
              await  NeutralizationHander?.Invoke();
            }
            catch (Exception ex)
            {
                Error.ProcessError(ex);
            }
        }

       async void UnNeutralization()
        {
            try
            {
              await  UnNeutralizationHander?.Invoke();
            }
            catch (Exception ex)
            {
                Error.ProcessError(ex);
            }
        }
    }
}
