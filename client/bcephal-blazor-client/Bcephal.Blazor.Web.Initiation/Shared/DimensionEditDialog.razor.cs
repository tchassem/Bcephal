using Bcephal.Blazor.Web.Base.Services;
using Bcephal.Blazor.Web.Base.Shared.Component;
using Bcephal.Blazor.Web.Initiation.Domain;
using Bcephal.Models.Base;
using Microsoft.AspNetCore.Components;
using Microsoft.AspNetCore.Components.Forms;
using System.Collections.Generic;
using System.Linq;

namespace Bcephal.Blazor.Web.Initiation.Shared
{
    public partial class DimensionEditDialog : ComponentBase
    {

        [Inject]
        public AppState AppState { get; set; }

        [Parameter]
        public Nameable Item { get; set; }

        public Nameable EmbeddedItem { get; set; }

        [Parameter]
        public string ActionType { get; set; } = "";

        [Parameter]
        public List<Nameable> ItemsListNames { get; set; }

        [Parameter]
        public bool ShowDialog { get; set; } = false;

        [Parameter]
        public EventCallback<bool> CloseEventCallback { get; set; }

        [Parameter]
        public EventCallback<Nameable> SaveEventCallback { get; set; }

        [Parameter]
        public string Title { get; set; }

        private string attribVal = "";

        private string AttributeValue
        {
            get
            {
                return "";
            }
            set
            {
                if (Item == null)
                {
                    Item = new Attribute();
                }
                AttributeValue attrVal = new();
                attrVal.Name = value;
                attribVal = value;
                if (!string.IsNullOrEmpty(value))
                {
                    ((Attribute)Item).AddValue(attrVal);
                }
            }
        }

        private bool IsDeclare
        {
            get
            {
                if (Item != null && Item is Attribute)
                {
                    if (ActionType.Equals("Add"))
                    {
                        if (EmbeddedItem == null)
                        {
                            if (Item is Measure)
                            {
                                EmbeddedItem = new Measure();
                            }
                            else if (Item is PeriodName)
                            {
                                EmbeddedItem = new PeriodName();
                            }
                            else
                            {
                                EmbeddedItem = Item is Model ? new Entity() : new Attribute();
                            }
                        }
                        return ((Attribute)EmbeddedItem).Declared;
                    }
                    return ((Attribute)Item).Declared;
                }
                return false;
            }
            set
            {
                if (Item != null && Item is Attribute)
                {
                    if (ActionType.Equals("Add"))
                    {
                        if (EmbeddedItem == null)
                        {
                            EmbeddedItem = new Attribute();
                        }
                        ((Attribute)EmbeddedItem).Declared = value;
                    }
                    else
                    {
                        ((Attribute)Item).Declared = value;
                    }
                }
            }
        }

        private string PreviousName;
        private string ItemName
        {
            get
            {
                if (ActionType.Equals("Add"))
                {
                    if (EmbeddedItem == null)
                    {
                        if (Item is Measure)
                        {
                            EmbeddedItem = new Measure();
                        }
                        else if (Item is PeriodName)
                        {
                            EmbeddedItem = new PeriodName();
                        }
                        else
                        {
                            EmbeddedItem = Item is Model ? new Entity() : new Attribute();
                        }
                    }
                    return EmbeddedItem.Name != null ? EmbeddedItem.Name : string.Empty;
                }
                else
                {
                    if (Item != null)
                    {
                        return Item.Name;
                    }
                }
                return "";
            }
            set
            {
                if (ActionType.Equals("Add"))
                {
                    if (EmbeddedItem == null)
                    {
                        if(Item is Measure)
                        {
                            EmbeddedItem = new Measure();
                        } else if(Item is PeriodName)
                        {
                            EmbeddedItem = new PeriodName();
                        } else
                        {
                            EmbeddedItem = Item is Model ? new Entity() : new Attribute();
                        }
                    }
                    EmbeddedItem.Name = value;
                }
                else
                {
                    if (Item == null)
                    {
                        Item = Title.Equals(@AppState["MeasureData.New"]) ? new Measure() : (Title.Equals(@AppState["PeriodData.New"]) ? new PeriodName() : new Model());
                    }
                    PreviousName = Item.Name;
                    Item.Name = value;
                }
            }
        }

        BaseModalComponent ModalItem { get; set; }
        private EditContext editContext;
        private ValidationMessageStore messageStore;
        private ValidationMessageStore messageStore2;

        public void Close()
        {
            ResetFields();
            ModalItem.CanClose = true;
            Dispose();
            CloseEventCallback.InvokeAsync(false);
        }

        protected async void HandleValidSubmit()
        {
            if (!CanSent())
            {
                return;
            }

            if (!string.IsNullOrEmpty(ActionType) && Item is not Measure && Item is not PeriodName)
            {
                Item = Processing();
                if (Item == null) return;
            }
            else if (ActionType.Equals("Add"))
            {
                if (Item is Measure)
                {
                    ((Measure)Item).AddChild((Measure)EmbeddedItem);
                }
                else if (Item is PeriodName)
                {
                    ((PeriodName)Item).AddChild((PeriodName)EmbeddedItem);
                }
            }
            else if (!ActionType.Equals("Edit"))
            {
                if (Item is Measure)
                {
                    ((Measure)Item).ParentId = null;
                }
                if (Item is PeriodName)
                {
                    ((PeriodName)Item).ParentId = null;
                }
            }
            await SaveEventCallback.InvokeAsync(Item);
            Close();
            StateHasChanged();
        }

        private Model Processing()
        {
            if (!string.IsNullOrEmpty(((Nameable)Item).Name))
            {
                if (Item is Model)
                {
                    if (ActionType.Equals("Add"))
                    {
                        ((Model)Item).AddEntity((Entity)EmbeddedItem);
                    }
                    return (Model)Item;
                }
                else if (Item is Entity)
                {
                    if (ActionType.Equals("Add"))
                    {
                        ((Entity)Item).AddAttribute((Attribute)EmbeddedItem);
                    }
                    else if (ActionType.Equals("Edit"))
                    {
                        ((Entity)Item).Model.UpdateEntity((Entity)Item);
                    }
                    return ((Entity)Item).Model;
                }
                else if (Item is Attribute)
                {
                    if (ActionType.Equals("Add"))
                    {
                        ((Attribute)Item).AddChildren((Attribute)EmbeddedItem);
                    }
                    //else
                    //{
                        if (((Attribute)Item).Entity != null)
                        {
                            ((Attribute)Item).Entity.UpdateAttribute((Attribute)Item);
                        }
                        else
                        {
                            ((Attribute)Item).Parent.UpdateChildren((Attribute)Item);
                        }
                    //}
                    return getModelFromAttribute((Attribute)Item);
                }
            }
            return null;
        }

        private Model getModelFromAttribute(Attribute item)
        {
            if (item.Entity != null)
            {
                return item.Entity.Model;
            }
            else
            {
                return getModelFromAttribute(item.Parent);
            }
        }

        private bool CanSent()
        {
            messageStore?.Clear();
            messageStore2?.Clear();

            if (Item == null || string.IsNullOrWhiteSpace(Item.Name))
            {
                return false;
            }

            if (ActionType.Equals("AddVal"))
            {
                if (string.IsNullOrEmpty(attribVal))
                {
                    messageStore2?.Add(() => AttributeValue, AppState["Form.NoEmpty"]);
                    ModalItem.CanClose = false;
                    return false;
                }
                return true;
            }

            if (NameAlreadyExist())
            {
                messageStore?.Add(() => ItemName, AppState["Form.UName"]);
                ModalItem.CanClose = false;
                ItemName = PreviousName;
                return false;
            }
            else if (string.IsNullOrEmpty(ItemName))
            {
                messageStore?.Add(() => ItemName, AppState["Form.NoEmpty"]);
                ModalItem.CanClose = false;
                ItemName = PreviousName;
                if (ActionType.Equals("Add"))
                {
                    EmbeddedItem.Name = PreviousName;
                }
                else
                {
                    Item.Name = PreviousName;
                }
                return false;
            }
            return true;
        }

        protected override void OnInitialized()
        {
            editContext = new(ItemName);
            messageStore = new(editContext);
            messageStore2 = new(editContext);
        }

        protected override void OnAfterRender(bool firstRender)
        {
            editContext = new(ItemName);
            messageStore = new(editContext);
            messageStore2 = new(editContext);
        }

        private void ResetFields()
        {
            ActionType = "";
            attribVal = "";
            Item = null;
            EmbeddedItem = null;
        }

        public void Dispose()
        {
            messageStore?.Clear();
            messageStore2?.Clear();
            if (editContext is not null)
            {
            }
        }

        private bool NameAlreadyExist()
        {
            bool isPresent = false;

            List<Nameable> presents = new List<Nameable>();
            if (ActionType.ToLower().Equals("Add".ToLower()))
            {
                presents = ItemsListNames.Where(n => n.Name.ToLower().Equals(EmbeddedItem.Name.ToLower()) && n.GetType() == EmbeddedItem.GetType()).ToList();
            }
            else
            {
                presents = ItemsListNames.Where(n => n.Name.ToLower().Equals(Item.Name.ToLower()) && n.GetType() == Item.GetType()).ToList();
            }

            if ((!ActionType.ToLower().Equals("Edit".ToLower()) && presents.Count() > 0) || (ActionType.ToLower().Equals("Edit".ToLower()) && presents.Count() > 1))
            {
                isPresent = true;
            }
            return isPresent;
        }
    }
}
