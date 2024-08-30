using Bcephal.Blazor.Web.Base.Services;
using DevExpress.Blazor;
using Microsoft.AspNetCore.Components;
using Microsoft.AspNetCore.Components.Web;
using System;
using System.Collections.ObjectModel;
using System.Linq;

namespace Bcephal.Blazor.Web.Base.Shared.Component
{
    public abstract partial class FormComponent<B> : ComponentBase
    {
        [Inject]
        protected IToastService toastService { get; set; }
        protected abstract RenderFragment GroupContentTab(B item,int index);
        [Parameter]
        public ObservableCollection<B> Items { get; set; } = new();

        [Parameter]
        public Action<ObservableCollection<B>,int> ItemsChanged { get; set; }

       
        protected virtual bool CanShowTab { get; set; } = true;

        protected virtual B NewItem { get => default; }

        protected abstract string GroupName(B item);
        protected abstract void SetGroupName(B item);
        protected abstract void SetGroupName(B item,string name);

        [CascadingParameter]
        public Error Error { get; set; }
        [Inject]
        public AppState AppState { get; set; }
        public int ActiveIndex { get; set; } = 0;
       
        protected bool RenameVisible { get; set; } = false;
        protected DxContextMenu ContextMenu { get; set; }

       
        protected int CurrentIndex = 0;

        DxTabs DxTabsRef;
        
        [Parameter]
        public  int ActiveTabIndex_
        {
            get => ActiveIndex;
            set
            {
                ActiveIndex = value;
                CurrentIndex = value;
                ItemsChanged?.Invoke(Items, CurrentIndex);
            }
        }        

        private void AddTab()
        {
            Items.Add(NewItem);
            ActiveTabIndex_ = Items.Count - 1;
            
        }

        private void init()
        {
            if (!Items.Any())
            {
                Items.Add(NewItem);
                ActiveTabIndex_ = Items.Count - 1;
            }
        }
       

        private void RemoveTagAction(B item)
        {
            try
            {
                DeleteItem(item);
                Items.Remove(item);
                toastService.ShowSuccess(AppState["TagHasBeenSuccessfullyRemoved"]);
                ActiveTabIndex_ = (Items.Count - 1) > 0 ? Items.Count - 1 : 0 ;
            }
            catch(Exception ex)
            {
                Error.ProcessError(ex);
            }
            finally
            {
                StateHasChanged();
            }
        }

        protected virtual void DeleteItem(B item)
        {

        }

        int ClickIndex { get; set; }
        protected void RenderContextMenu(MouseEventArgs  e,int index)
        {
            ClickIndex = index;
            ContextMenu.ShowAsync(e);
        }

       
        protected void ShowRenameComponent()
        {
            RenameVisible = true;
            StateHasChanged();
        }        
    }
}
