using Bcephal.Blazor.Web.Base.Services;
using Blazored.TextEditor;
//using Blazored.TextEditor;
using Microsoft.AspNetCore.Components;
using Microsoft.JSInterop;
using System;
using System.Collections.Generic;
using System.Threading.Tasks;

namespace Bcephal.Blazor.Web.Dashboard.Shared.Dashboard
{
    public partial class DashboardHomePage_ : ComponentBase
    {
        [Inject]
        IJSRuntime JsRuntime { get; set; }
        [Parameter]
        public Models.Dashboards.DashboardItem Item { get; set; }

        [Parameter]
        public Action<Models.Dashboards.DashboardItem> ItemHandler { get; set; }

        [Parameter]
        public Func<Models.Dashboards.DashboardItem> GetItemHandler { get; set; }

        [Parameter]
        public bool CanShowHeader { get; set; } = true;

        [Parameter]
        public bool CanShowBorder { get; set; } = true;

        [Parameter]
        public bool IsReadOnlyData { get; set; } = false;

        [Parameter]
        public bool Editable { get; set; } = true;

        [Inject]
        AppState AppState { get; set; }

        [Parameter]
        public string ProjectId { get; set; }

       
        public string UserMessage_ { get; set; } 
        public string UserMessage
        {
            get
            {
                Item = GetItem();
                if (Item != null  )
                {
                    if (Item.Id.HasValue && !string.IsNullOrWhiteSpace(Item.Description))
                    {
                        if (IsReadOnlyData)
                        {
                            UserMessage_ = GetUserMessage(Item.Description);
                        }
                        else
                        {
                            UserMessage_ = Item.Description;
                        }
                    }
                    else if(!Item.Id.HasValue && !string.IsNullOrWhiteSpace(Item.Description)) 
                    {
                        if (IsReadOnlyData)
                        {
                            UserMessage_ = GetUserMessage(Item.Description);
                        }
                        else
                        {
                            UserMessage_ = Item.Description;
                        }
                    }
                   
                }
               
                return UserMessage_+ "&nbsp";
            }

            set
            {
                UserMessage_ = value;
                if(Item != null)
                {
                    Item.Description = UserMessage_;
                }
                
            }
        }


        private readonly List<string> Variables = new() 
        { 
            "$DATE$=Current Date", 
            "$DATETIME$=Current Date and time", 
            "$USER-LASTNAME$=User lastname", 
            "$USER-FIRSTNAME$=User firstname"
        };

        protected override void OnInitialized()
        {
            base.OnInitialized();
            Item = GetItem();

        }




        public Models.Dashboards.DashboardItem GetItem()
        {
            if (Item != null)
            {
                return Item;
            }
            else
            {
                Models.Dashboards.DashboardItem item = GetItemHandler?.Invoke();
                return item;
            }
        }


        public string GetUserMessage(string msg)
        {
            string str = msg;
            if (!string.IsNullOrWhiteSpace(msg))
            {
                if (str.Contains("$DATE$"))
                {
                    str = str.Replace("$DATE$", DateTime.Now.Date.ToShortDateString());
                }
                if (str.Contains("$DATETIME$"))
                {
                    str = str.Replace("$DATETIME$", DateTime.Now.ToString());
                }
                if (str.Contains("$USER-LASTNAME$"))
                {
                    if(AppState.CurrentUser != null && !string.IsNullOrWhiteSpace(AppState.CurrentUser.LastName))
                    {
                        str = str.Replace("$USER-LASTNAME$", AppState.CurrentUser.LastName);
                    }
                    else
                    {
                        str = str.Replace("$USER-LASTNAME$", "");
                    }

                }
                if (str.Contains("$USER-FIRSTNAME$"))
                {
                    if (AppState.CurrentUser != null && !string.IsNullOrWhiteSpace(AppState.CurrentUser.FirstName))
                    {
                        str = str.Replace("$USER-FIRSTNAME$", AppState.CurrentUser.FirstName);
                    }
                    else 
                    {
                        str = str.Replace("$USER-FIRSTNAME$", "");
                    }  
                }
            }
           
            return str;
        }
        private async void UpdateUserMessage(string msg)
        {
            if (msg.Contains("="))
            {
                string[] str = msg.Split('=');
                if (str.Length > 1)
                {
                    msg = str[0];
                }
            }
            UserMessage = UserMessage + "&nbsp;" + msg;
            
            if (Item != null)
            {
                Item.Description = UserMessage;
            }
            string msg_ = await JsRuntime.InvokeAsync<string>("contenteditor.getHtml") ;
            msg_ = msg_ + "&nbsp;" + msg;
            await JsRuntime.InvokeVoidAsync("contenteditor.setHtml", msg_);
        }


    }
}
