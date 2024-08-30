using Bcephal.Blazor.Web.Base.Services;
using Bcephal.Blazor.Web.Base.Shared;
using Bcephal.Models.Filters;
using Microsoft.AspNetCore.Components;
using Microsoft.JSInterop;
using Newtonsoft.Json;
using System;
using System.Threading.Tasks;

namespace Bcephal.Blazor.Web.Dashboard.Shared.Dashboard
{
    public partial class DashboardItemPanelContent: ComponentBase, IAsyncDisposable
    {
       
        [Inject]
        IJSRuntime JsRuntime { get; set; }

        [Inject]
        private AppState AppState { get; set; }

        [Parameter]
        public NodeElement SelectItemNode { get; set; }

        [Parameter]
        public EventCallback<NodeElement> SelectItemNodeChanged { get; set; }

        [Parameter]
        public Action<Models.Dashboards.DashboardItem> ItemHandler { get; set; }

        [Parameter]
        public Action<Models.Dashboards.DashboardItem> UpdateItemHandler { get; set; }

        [Parameter]
        public Action<Models.Dashboards.DashboardItem> DeleteOrForgetItemHandler { get; set; }

        [Parameter]
        public Func<Models.Dashboards.DashboardItem> GetItemHandler { get; set; }

        [Parameter]
        public Models.Dashboards.DashboardItem Item { get; set; }

        [Parameter]
        public EventCallback<Models.Dashboards.DashboardItem> ItemChanged { get; set; }


        [Parameter]
        public Func<NodeElement> GetSelectedItem { get; set; }

        [Parameter]
        public bool CanEdit { get; set; } = false;

        public string DefautMessage { get; set; } = "<p><strong>Welcome $USER-FIRSTNAME$ $USER-LASTNAME$ </strong></p> <p> $DATE$ - $DATETIME$ </p>  ";

        [Parameter]
        public bool IsReadOnlyData { get; set; } = false;


        DashboardHomePage_ DashboardHomePage_ref;
        private RenderFormContent RenderFormContentRef { get; set; }
        private RenderFormContent RenderFormContentBaseModalRef { get; set; }

        bool Properties_ { get; set; }
        bool lastState { get; set; }
        bool Properties { get => Properties_; 
            set
            {
                Properties_ = value;
                if (value)
                {
                    lastState = value;
                    if (!lastState)
                    {
                        lastState = value;
                        if (RenderFormContentBaseModalRef != null)
                        {
                            RenderFormContentBaseModalRef.StateHasChanged_();
                        }
                    }
                }
                else
                {
                    if (lastState)
                    {
                        lastState = value;
                        if (RenderFormContentBaseModalRef != null)
                        {
                            RenderFormContentBaseModalRef.StateHasChanged_();
                        }
                    }
                }
            }
        }
        public bool ShowBox_ { get; set; }
        public bool ShowBox
        {
            get
            {
                return Item == null || Item.Visible;
            }
            set
            {
                if(Item != null)
                    Item.Visible = value;
            }
        }

        public bool ShowBorder
        {
            get
            {
                return Item == null || Item.ShowBorder;
            }
            set
            {
                if (Item != null)
                    Item.ShowBorder = value;
            }
        }

        public bool ShowTitleBar
        {
            get
            {
                return Item == null || Item.ShowTitleBar;
            } 
            set 
            {
                if (Item != null)
                    Item.ShowTitleBar = value;
            }
        }

        string Title_ { get; set; }

        public string Title
        {
            get
            {
                return Item != null && !Item.DashboardItemType.IsEmptyType()? Item.Name : "";
            }
            set
            {
                Title_ = value;
                if (Item != null)
                    Item.Name = Title_;
            }
        }

        string Background_ { get; set; }
        public string Background
        {
            get
            {
                return Item != null && !string.IsNullOrWhiteSpace(Item.Background) ? Item.Background :  "#fff";
            }
            set
            {
                if (!string.IsNullOrWhiteSpace(value))
                {
                    Background_ = value;
                    if (Item != null)
                    {
                        Item.Background = Background_;
                    }
                   
                }

            }
        }
        string Foreground_ { get; set; }

        public string Foreground
        {
            get
            {
                return Item != null && !string.IsNullOrWhiteSpace(Item.Foreground) ? Item.Foreground : "#000";
            }
            set
            {
                if (!string.IsNullOrWhiteSpace(value))
                {
                    Foreground_ = value;
                    if (Item != null)
                    {
                        Item.Foreground = Foreground_;
                    }
                    
                }
            }
        }


        string BackgroundTitle_ { get; set; } 
        public string BackgroundTitle
        {
            get
            {
                return Item != null && !string.IsNullOrWhiteSpace(Item.BackgroundTitle) ? Item.BackgroundTitle : "#adb9ca";
            }
            set
            {
                if (!string.IsNullOrWhiteSpace(value))
                {
                    BackgroundTitle_ = value;
                    if(Item != null)
                    {
                        Item.BackgroundTitle = BackgroundTitle_;
                    }
                    
                }
            }
        }
        string ForegroundTitle_ { get; set; } 

        public string ForegroundTitle
        {
            get
            {
                return Item != null && !string.IsNullOrWhiteSpace(Item.ForegroundTitle) ? Item.ForegroundTitle : "#ffffff";
            }
            set
            {
                if (!string.IsNullOrWhiteSpace(value))
                {
                    ForegroundTitle_ = value;
                    if (Item != null)
                    {
                        Item.ForegroundTitle = ForegroundTitle_;
                    }
                   
                }
         
            }
        }

        [Parameter]
        public bool Editable { get; set; } = true;

        public string Width_ { get; set; } = "100%";

        public string Height_ { get; set; } = "100%";


        public async ValueTask DisposeAsync()
        {
            await JSRuntime.InvokeVoidAsync("drop_handler_Callback_delete", Id);
        }
        [Inject]
        private IJSRuntime JSRuntime { get; set; }
        DotNetObjectReference<DashboardItemPanelContent> dotNetReference;
        string Id = Guid.NewGuid().ToString("d");

        protected override async Task OnAfterRenderAsync(bool firstRender)
        {
            await base.OnAfterRenderAsync(firstRender);
            if (firstRender)
            {
                Item = GetItem();
                if (Item == null)
                {
                    Item = new Models.Dashboards.DashboardItem() { DashboardItemType = Models.Dashboards.DashboardItemType.EMPTY_TYPE , ItemName = "Empty type", Name = ""};
                    ItemHandler?.Invoke(Item);
                }
            }

        }

        protected async override Task OnInitializedAsync()
        {
            
            dotNetReference = DotNetObjectReference.Create(this);
            await JSRuntime.InvokeVoidAsync("drop_handler_Callback", Id, dotNetReference, "Drop");
            await base.OnInitializedAsync();
        }

        //  [JSInvokable("Drop")]
        //public void Drop(string Current_)
        //{
        //    if (Current_ != null)
        //    {
        //        SelectItemNode = JsonConvert.DeserializeObject<NodeElement>(Current_);
        //        Bcephal.Models.Dashboards.DashboardItemType DashboardItemType_ = Bcephal.Models.Dashboards.DashboardItemType.GetByCode(SelectItemNode.ItemType);
        //        string itemname = SelectItemNode.Name;
        //        string Description = DashboardItemType_.IsDashboardHomePage() ? DefautMessage : "";
        //        var item = GetItem();
        //        if (item != null)
        //        {
        //            item.ItemType = SelectItemNode.ItemType;
        //            item.DashboardItemType = DashboardItemType_;
        //            item.ItemName = itemname;
        //            item.Description = Description;
        //            item.Name = SelectItemNode.Name;
        //            item.ItemId = SelectItemNode.Id;
        //            item.BackgroundTitle = BackgroundTitle_;
        //            item.ForegroundTitle = ForegroundTitle_;
        //            item.Background = Background_;
        //            item.Foreground = Foreground_;
        //            item.Width = Defaultvalue;
        //            item.Height = Defaultvalue;
        //            ItemHandler?.Invoke(Item);
        //        }

        //         Item = GetItem();
        //    }
        //}


        [JSInvokable("Drop")]
        public void Drop(string Current_)
        {
            if (Current_ != null)
            {
                SelectItemNode = JsonConvert.DeserializeObject<NodeElement>(Current_);
                Bcephal.Models.Dashboards.DashboardItemType DashboardItemType_ = Bcephal.Models.Dashboards.DashboardItemType.GetByCode(SelectItemNode.ItemType);
                string itemname = SelectItemNode.Name;
                string Description = DashboardItemType_.IsDashboardHomePage() ? DefautMessage : "";
                Models.Dashboards.DashboardItem item_ = GetItem();
                if (item_ == null)
                {
                    item_ = new();
                    item_.ItemType = SelectItemNode.ItemType;
                    item_.DashboardItemType = DashboardItemType_;
                    item_.ItemName = itemname;
                    item_.Description = Description;
                    item_.Name = SelectItemNode.Name;
                    item_.ItemId = SelectItemNode.Id;
                    item_.BackgroundTitle = BackgroundTitle_;
                    item_.ForegroundTitle = ForegroundTitle_;
                    item_.Background = Background_;
                    item_.Foreground = Foreground_;
                    ItemHandler?.Invoke(item_);
                }
                else
                {
                    item_.ItemType = SelectItemNode.ItemType;
                    item_.DashboardItemType = DashboardItemType_;
                    item_.ItemName = itemname;
                    item_.Description = Description;
                    item_.Name = SelectItemNode.Name;
                    item_.ItemId = SelectItemNode.Id;
                    item_.BackgroundTitle = BackgroundTitle_;
                    item_.ForegroundTitle = ForegroundTitle_;
                    item_.Background = Background_;
                    item_.Foreground = Foreground_;
                    UpdateItemHandler?.Invoke(item_);
                }



                Item = GetItem();
            }
        }

        //public async void drop(Microsoft.AspNetCore.Components.Web.DragEventArgs e)
        //{
        //    if(SelectItemNode != null)
        //    {
        //        Bcephal.Models.Dashboards.DashboardItemType DashboardItemType_ = Bcephal.Models.Dashboards.DashboardItemType.GetByCode(SelectItemNode.ItemType);
        //        string itemname = SelectItemNode.Name;
        //        string Description = DashboardItemType_.IsDashboardHomePage() ? DefautMessage : "";
        //        Item = GetItem();
        //        if(Item != null)
        //        {
        //            Item.ItemType = SelectItemNode.ItemType;
        //            Item.DashboardItemType = DashboardItemType_;
        //            Item.ItemName = itemname;
        //            Item.Description = Description;
        //            Item.Name = SelectItemNode.Name;
        //            Item.ItemId = SelectItemNode.Id;
        //            Item.BackgroundTitle = BackgroundTitle_;
        //            Item.ForegroundTitle = ForegroundTitle_;
        //            Item.Background = Background_;
        //            Item.Foreground = Foreground_;
        //            UpdateItemHandler?.Invoke(Item);
        //        }
        //        else
        //        {
        //            Models.Dashboards.DashboardItem Item_ = new()
        //            {
        //                ItemType = SelectItemNode.ItemType,
        //                DashboardItemType = DashboardItemType_,
        //                ItemName = itemname,
        //                Description = Description,
        //                Name = SelectItemNode.Name,
        //                ItemId = SelectItemNode.Id,
        //                BackgroundTitle = BackgroundTitle_,
        //                ForegroundTitle = ForegroundTitle_,
        //                Background = Background_,
        //                Foreground = Foreground_,
        //            };
        //            ItemHandler?.Invoke(Item_);
        //        }
              
               
        //        Item = GetItem();
        //        await Task.CompletedTask;
        //    }
            
        //}


        public async void Update()
        {
            if(Item != null)
            {
                if (Item.DashboardItemType.IsDashboardHomePage())
                {
                    var html = await JsRuntime.InvokeAsync<string>("contenteditor.getHtml");
                    if (!string.IsNullOrWhiteSpace(html))
                    {
                        Item.Description = html;
                    }

                }

                Item.Background = !string.IsNullOrWhiteSpace(Background_) ? Background_ : Item.Background;
                Item.Foreground = !string.IsNullOrWhiteSpace(Foreground_) ? Foreground_ : Item.Foreground;
                Item.BackgroundTitle = !string.IsNullOrWhiteSpace(BackgroundTitle_) ? BackgroundTitle_ : Item.BackgroundTitle;
                Item.ForegroundTitle = !string.IsNullOrWhiteSpace(ForegroundTitle_) ? ForegroundTitle_ : Item.ForegroundTitle;
                UpdateItemHandler?.Invoke(Item);
            }
           
            Properties = false;
        }
        
        public Models.Dashboards.DashboardItem GetItem()
        {
         
            Models.Dashboards.DashboardItem Item_ = GetItemHandler?.Invoke();
            return Item_;
        }

        public void RemoveItem()
        {
            Models.Dashboards.DashboardItem Item_ = GetItem();
            DeleteOrForgetItemHandler.Invoke(Item_);
            RenderFormContentRef.StateHasChanged_();
        }

        private void UpdateForegroundBox(ChangeEventArgs  e)
        {
            Foreground_ = e?.Value?.ToString();
        }
        private void UpdateBackgroundBox(ChangeEventArgs e)
        {
            Background_ = e?.Value?.ToString();
        }
        private void UpdateForegroundTitle(ChangeEventArgs e)
        {
            ForegroundTitle_ = e?.Value?.ToString();
        }
        private void UpdateBackgroundTitle(ChangeEventArgs e)
        {
            BackgroundTitle_ = e?.Value?.ToString();
        }

        private void UpdateProperties()
        {
            Properties = true;
        }


    }
}
