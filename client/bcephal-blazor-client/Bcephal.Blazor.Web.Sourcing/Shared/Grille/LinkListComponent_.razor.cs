using Bcephal.Blazor.Web.Base.Services;
using Bcephal.Models.Base;
using Bcephal.Models.Links;
using DevExpress.Blazor;
using Microsoft.AspNetCore.Components;
using System;
using System.Collections.Generic;
using System.Linq;

namespace Bcephal.Blazor.Web.Sourcing.Shared.Grille
{
    public partial class LinkListComponent_ : ComponentBase
    {
        [Inject]
        protected AppState AppState { get; set; }

        [Parameter]
        public Models.Links.Link Link { get; set; }

        [Parameter]
        public bool Editable { get; set; } = true;

        [Parameter]
        public Action StateChange { get; set; }

        [Parameter]
        public Action AddRender { get; set; }
        [Parameter]
        public Action<Models.Links.Link> RemoveRender { get; set; }

        [Parameter]
        public EditorData<Models.Grids.Grille> EditorData { get; set; }

        [Parameter]
        public EventCallback<EditorData<Models.Grids.Grille>> EditorDataChanged { get; set; }

        [Parameter]
        public bool CanDisplay { get; set; }

        private Action<Models.Grids.GrilleColumn, Models.Links.LinkedAttribute> SelectedHandler { get; set; }
        private Models.Links.LinkedAttribute CurrentSelectionItem { get; set; }
        private DxPopup PopupRef { get; set; }
        private bool DxPopupVisible { get; set; } = false;

        public string SelectedValue { get; set; } = "1";
        private void ClickButton()
        {
            DxPopupVisible = true;
        }

        private void SetAction(Action<Models.Grids.GrilleColumn, Models.Links.LinkedAttribute> SelectedHandler, Models.Links.LinkedAttribute item)
        {
            this.SelectedHandler = SelectedHandler;
            CurrentSelectionItem = item;
        }

        private void AddLinkedAttribute(Models.Links.LinkedAttribute linkedAttribute)
        {
            Link.AddLinkedAttribute(linkedAttribute);
            if (string.IsNullOrWhiteSpace(Link.Name))
            {
                Link.Name = linkedAttribute.AttributeName;
            }
        }

        private void UpdateLinkedAttribute(Models.Links.LinkedAttribute linkedAttribute)
        {
            Link.UpdateLinkedAttribute(linkedAttribute);
        }

        private void DeleteOrForgetLinkedAttribute(Models.Links.LinkedAttribute linkedAttribute)
        {
            Link.DeleteOrForgetLinkedAttribute(linkedAttribute);
            StateHasChanged();
        }

        private void DeleteOrForgetLink()
        {
            EditorData.Item.DeleteOrForgetLink(Link);
            RemoveRender?.Invoke(Link);
            if (EditorDataChanged.HasDelegate)
            {
                EditorDataChanged.InvokeAsync(EditorData);
            }
        }

        private bool CanAddLinkAttribute(long id) => !Link.LinkedAttributeListChangeHandler.GetItems().Where(item => item.AttributeId == id).Any();
        private List<Models.Links.LinkedAttribute> LinkedAttributesKeys => Link.LinkedAttributeListChangeHandler.GetItems().Where(item => item.Iskey).ToList();
        private List<Models.Links.LinkedAttribute> LinkedAttributesValues => Link.LinkedAttributeListChangeHandler.GetItems().Where(item => !item.Iskey).ToList();

        private void Ok()
        {
            if (Link.IsPersistent)
            {
                EditorData.Item.UpdateLink(Link);
            }
            else
            {
                EditorData.Item.AddLink(Link);
                AddRender?.Invoke();
            }

            if (EditorDataChanged.HasDelegate)
            {
                EditorDataChanged.InvokeAsync(EditorData);
            }
            Cancel();
        }
        private void Cancel()
        {
            DxPopupVisible = false;
            StateChange?.Invoke();
        }

        

        private List<Models.Grids.GrilleColumn> GrilleColumns_;
        private IEnumerable<Models.Grids.GrilleColumn> GrilleColumns
        {
            get
            {
                if (EditorData == null || EditorData.Item == null)
                {
                    return new List<Models.Grids.GrilleColumn>();
                }
                if (GrilleColumns_ != null)
                {
                    return GrilleColumns_;
                }
                GrilleColumns_ = EditorData.Item.ColumnListChangeHandler.GetItems().Where(item => item.IsAttribute).ToList();
                return GrilleColumns_;
            }
        }

        private IEnumerable<Models.Grids.GrilleColumn> CurrentGrilleColumns_ { get; set; }
        private IEnumerable<Models.Grids.GrilleColumn> CurrentGrilleColumns
        {
            get => CurrentGrilleColumns_;
            set
            {
                CurrentGrilleColumns_ = value;
                if(CurrentGrilleColumns_ != null && CurrentGrilleColumns_.Count() > 0)
                {
                    var attrib = CurrentGrilleColumns_.FirstOrDefault();
                    SelectedHandler?.Invoke(attrib, CurrentSelectionItem);
                    StateHasChanged();
                }
            }

        }

        private void SelectedItemsChanged(IEnumerable<Models.Grids.GrilleColumn> grilleColumns)
        {
            CurrentGrilleColumns = grilleColumns;
        }
    }
}
