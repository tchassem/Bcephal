using Bcephal.Blazor.Web.Base.Services;
using Bcephal.Blazor.Web.Base.Shared;
using Bcephal.Blazor.Web.Dashboard.Services;
using Bcephal.Blazor.Web.Dashboard.Shared.Dashboard;
using Bcephal.Blazor.Web.Sourcing.Services;
using Bcephal.Models.Alarms;
using Bcephal.Models.Base;
using Bcephal.Models.Conditions;
using DevExpress.Blazor;
using Microsoft.AspNetCore.Components;
using Microsoft.JSInterop;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;

namespace Bcephal.Blazor.Web.Dashboard.Pages.Dashboard
{
    public partial class AlarmForm : Form<Bcephal.Models.Alarms.Alarm, BrowserData>
    {
        private bool IsSmallScreen { get; set; }

        [Inject]
        public SpotService SpotService { get; set; }

        [Inject]
        public AlarmService AlarmService { get; set; }

        public AlarmModelEditorData AlarmModelEditorData { get; set; }

        [Inject]
        public IJSRuntime JsRuntime { get; set; }

        [Parameter]
        public EventCallback<EditorData<Bcephal.Models.Alarms.Alarm>> EditorDataChanged { get; set; }

        int ActiveTabIndex { get; set; } = 0;

        private string EditorRoute { get; set; }

        private bool showConditionsModal = false;
        private bool showAttachmentsModal = false;
        private bool showRecipientsModal = false;
        private bool mustSaved = false;
        private List<AlarmAttachment> TypeList= new();

       
        public bool Editable
        {
            get
            {   if(EditorData != null)
                {
                    var first = AppState.PrivilegeObserver.CanCreatedDashboardingAlarm;
                    var second = AppState.PrivilegeObserver.CanEditDashboardingAlarm(EditorData.Item);
                    return first || second;
                }
                return false;
                
            }
        }
        public override string LeftTitle { get { return AppState["New.Alarm"]; } }

        public override string LeftTitleIcon { get { return "bi-file-plus"; } }

        public string IdWidget { get; private set; }

        private void ChangeTab(TabClickEventArgs e)
        {
            ActiveTabIndex = e.TabIndex;
            if (ActiveTabIndex == 0)
            {
                usingMixPane = true;
                displayRight = "";
                displayLeft = "";
            } 
            else
            {
                usingMixPane = false;
                displayRight = DISPLAY_NONE;
                displayLeft = WIDTH_100;
            }
        }

        protected override void AfterInit(EditorData<Models.Alarms.Alarm> EditorData)
        {
            base.AfterInit(EditorData);
            TypeList = EditorData.Item.attachmentListChangeHandler.Items.ToList();
            AlarmModelEditorData = GetEditorData();
            ActivationRunner();
        }

        private AlarmModelEditorData GetEditorData()
        {
            return (AlarmModelEditorData)EditorData;
        }

        protected override Task OnAfterRenderAsync(bool firstRender)
        {
            if (firstRender)
            {               
                RefreshRightContent(null);
            }
            return base.OnAfterRenderAsync(firstRender);
        }
        
        protected override void OnInitialized()
        {          
            base.OnInitialized();
        }

        protected override string DuplicateName()
        {
            return AppState["duplicate.alarm.name", EditorData.Item.Name];
        }

        private void ActivationRunner()
        {
            if (EditorData.Item.IsPersistent)
            {
                if (!AppState.CanRun)
                {
                    AppState.CanRun = true;
                    AppState.RunHander += sendAlertMessage;
                }
            }
        }

        public override string GetBrowserUrl { get => Route.BROWSER_REPORT_ALARM; set => base.GetBrowserUrl = value; }

        protected override void AfterSave(EditorData<Models.Alarms.Alarm> EditorData)
        {
            AfterInit(this.EditorData);
            ActivationRunner();
            mustSaved = true;
            StateHasChanged();
        }

        private async void sendAlertMessage()
        {
            await AlarmService.sendAlertMessage(EditorData.Item);
        }

        public override async ValueTask DisposeAsync()
        {
            if (AppState.CanRun)
            {
                AppState.CanRun = false;
                AppState.RunHander -= sendAlertMessage;
            }
            await base.DisposeAsync();
            AppState.Update = false;
        }

        protected override EditorDataFilter getEditorDataFilter()
        {
            return new EditorDataFilter();
        }

        protected override Service<Models.Alarms.Alarm, BrowserData> GetService()
        {
            return AlarmService;
        }

        private void UpdateEmailMessage(string item)
        {            
            if (EditorData.Item.SendEmail && IdWidget.Equals("EmailId"))
            {
                EditorDataBinding.Item.Email += " " + item;
            }
            if (EditorData.Item.SendSms && IdWidget.Equals("SmsId"))
            {
                EditorDataBinding.Item.Sms += " " + item;
            }
             if (EditorData.Item.SendChat && IdWidget.Equals("ChatId"))
            {
                EditorDataBinding.Item.Chat += " " + item;
            } 
            if (IdWidget.Equals("titleId"))
            {
                EditorDataBinding.Item.EmailTitle += " " + item;
            }
            StateHasChange_();
        }

        private void SettingWidgetIden( string iden)
        {
            IdWidget = iden;
        }
            
        private void OnSendMailChanged(bool value)
        {
            EditorData.Item.SendEmail = value;
            StateHasChange_();
        }

        private void OnEmailTitleChanged(string newValue)
        {
            EditorData.Item.EmailTitle = newValue;
            StateHasChange_();
        }

        private void OnEmailChanged(string newValue)
        {
            EditorData.Item.Email = newValue;
            StateHasChange_();
        }

        private void OnSendSmsChanged(bool value)
        {
            EditorData.Item.SendSms = value;
            StateHasChange_();
        }

        private void OnSmsChanged(string newValue)
        {
            EditorData.Item.Sms = newValue;
            StateHasChange_();
        }

        private void OnSendChatChanged(bool value)
        {
            EditorData.Item.SendChat = value;
            StateHasChange_();
        }

        private void OnChatChanged(string newValue)
        {
            EditorData.Item.Chat = newValue;
            StateHasChange_();
        }

        public void StateHasChange_()
        {
            AppState.Update = true;
            mustSaved = true;
        }

    }

}
