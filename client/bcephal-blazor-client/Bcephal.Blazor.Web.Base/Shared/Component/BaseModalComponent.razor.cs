using Bcephal.Blazor.Web.Base.Services;
using DevExpress.Blazor;
using Microsoft.AspNetCore.Components;
using Microsoft.JSInterop;
using System;
using System.Threading.Tasks;

namespace Bcephal.Blazor.Web.Base.Shared.Component
{
    public partial class BaseModalComponent : ComponentBase, IDisposable
    {
        #region :: Injections, Parameters and attributes section ::
        [Inject]
        private AppState AppState { get; set; }
        [Parameter]
        public string Title { get; set; }
        [Parameter]
        public bool ShowModal { get; set; } = false;
        [Parameter]
        public bool CloseOnOutsideClick { get; set; } = false;
        [Parameter]
        public bool CloseOnEscape { get; set; } = false;
        [Parameter]
        public bool ApplyBackgroundShading { get; set; } = false;
        [Parameter]
        public string Message { get; set; }
        [Parameter]
        public string CssClass { get; set; } = "";
        [Parameter]
        public RenderFragment ChildContent { get; set; }
        [Parameter]
        public Action OkHandler { get; set; }
        [Parameter]
        public Action CancelHandler { get; set; }
        public bool CanClose { get; set; } = true;

        [Parameter]
        public bool CanDisplayClose { get; set; } = true;

        [Parameter]
        public bool IsConfirmation { get; set; } = false;

        [Parameter]
        public bool CanDisplayOK { get; set; } = true;

        [Parameter]
        public bool OkBtnEnabled { get; set; } = true;

        [Parameter]
        public EventCallback<bool> OkBtnEnabledChanged { get; set; }

        string okclass { get; set; }

        string closeclass { get; set; }

        [Parameter]
        public EventCallback<bool> ShowModalChanged { get; set; }

        DxPopup DxPopupRef;

        [Parameter]
        public string BodyClass{ get; set; }
        [Parameter]
        public bool ShowHeader { get; set; } = true;
        [Parameter]
        public bool ShowFooter { get; set; } = true;
        [Parameter]
        public string HeaderClass { get; set; } = "bc-header-1 bc-header-height";


        [Parameter]
        public string Width { get; set; } = "auto !important";

        [Parameter]
        public string Height { get; set; } = "auto !important";

        public string Width_ { 
            get {
                    if (string.IsNullOrWhiteSpace(Width))
                    {
                        return "auto !important";
                    }
                    return Width;
                }
            }

        
        public string Height_ {
            get
            {
                if (string.IsNullOrWhiteSpace(Height))
                {
                    return "auto !important";
                }
                return Height;
            }
        }

        #endregion


        #region :: Methods section

        public void Dispose()
        {
           // GC.SuppressFinalize(DxPopupRef);
            GC.SuppressFinalize(this);
        }

        public async Task BtnOkClicked()
        {
            Func<Task> callback = async () => {
                OkHandler?.Invoke(); 
                if (CanClose)
                {
                    ShowModal = false;
                    CanDisplayOK = true;
                    await ShowModalChanged.InvokeAsync(ShowModal);
                }
               
            };
           await callback.Invoke();
        }
        public void BtnCancelClicked()
        {
            CancelHandler?.Invoke();

            ShowModal = false;
            CanDisplayOK = true;
            ShowModalChanged.InvokeAsync(ShowModal);
        }
       
        protected override void OnInitialized()
        {
            base.OnInitialized();
            okclass = CanDisplayClose ? "mr-1 wid-70 bc-btn-pointer" : "wid-70 bc-btn-pointer";
            closeclass = CanDisplayOK ? "ml-1 wid-70 bc-btn-pointer" : "ml-1 mr-1 wid-70 bc-btn-pointer";
        }

        #endregion
    }
}
