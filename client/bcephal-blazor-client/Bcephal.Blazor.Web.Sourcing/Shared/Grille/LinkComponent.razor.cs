
using Bcephal.Models.Base;
using Microsoft.AspNetCore.Components;
using System.Collections.Generic;
using System.Collections.ObjectModel;
using System.Linq;

namespace Bcephal.Blazor.Web.Sourcing.Shared.Grille
{
    public partial class LinkComponent : ComponentBase
    {
        [Parameter]
        public EditorData<Models.Grids.Grille> EditorData { get; set; }

        [Parameter]
        public EventCallback<EditorData<Models.Grids.Grille>> EditorDataChanged { get; set; }

        [Parameter]
        public bool Editable { get; set; } = true;

        private bool CanDisplay(Models.Links.Link Item)
        {
            int pos = Keys.FindIndex(key => key == Item.Key);
            return !(renders.Count() - 1 == pos);
        }
        protected virtual EditorData<Models.Grids.Grille> EditorDataBinding
        {
            get { return EditorData; }
            set
            {
                EditorData = value;
                if (EditorDataChanged.HasDelegate)
                {
                    EditorDataChanged.InvokeAsync(EditorData);
                }
            }
        }

        private List<string> Keys { get; set; }  = new();
        private List<RenderFragment> renders { get; set; } = new();
        private void AddRenderLink(Models.Links.Link item)
        {
            Keys.Add(item.Key);
            renders.Add(ItemRender(item));
        }
        private void RemoveRenderLink(Models.Links.Link Item)
        {
            int pos = Keys.FindIndex(key => key == Item.Key);
            if (pos >= 0)
            {
                Keys.RemoveAt(pos);
                renders.RemoveAt(pos);
                StateHasChanged();
            }
        }


        protected override void OnAfterRender(bool firstRender)
        {
            if (firstRender)
            {
                InitRendersLink();
            }
             base.OnAfterRender(firstRender);
        }

        protected void InitRendersLink()
        {
            ObservableCollection<Models.Links.Link> items = EditorDataBinding.Item.LinkListChangeHandler.GetItems();
            foreach (var item in items)
            {
                AddRenderLink(item);
            }
            AddRenderLink(new Models.Links.Link());
            StateHasChanged();
        }

        protected void AddRenderLink()
        {
            AddRenderLink(new Models.Links.Link());
            StateHasChanged();
        }
    }
}
