using Bcephal.Models.Base;
using Bcephal.Models.Grids;
using DevExpress.Blazor;
using Microsoft.AspNetCore.Components;
using Microsoft.AspNetCore.Components.Web;
using Microsoft.JSInterop;
using System.Collections.Generic;
using System.Collections.ObjectModel;
using System.Linq;
using System.Threading.Tasks;
using Bcephal.Models.Utils;
using Bcephal.Blazor.Web.Base.Shared.Component;
using Bcephal.Blazor.Web.Base.Services;
using System;

namespace Bcephal.Blazor.Web.Sourcing.Shared.Grille
{
    public partial class ConfigurationGridContent : ComponentBase
    {
        [Inject] public AppState AppState { get; set; }
        [Parameter] public int GrilleColumnPosition { get; set; } = 0;
        [Parameter] public EventCallback<int> GrilleColumnPositionChanged { get; set; }
        [Parameter] public EditorData<Bcephal.Models.Grids.Grille> EditorData { get; set; }
        [Parameter] public EventCallback<EditorData<Bcephal.Models.Grids.Grille>> EditorDataChanged { get; set; }
        [Parameter] public bool Editable_ { get; set; } = true;
        public ObservableCollection<GrilleColumn> Items_ { get; set; }

        public ObservableCollection<GrilleColumn> Items
        {
            get
            {
                if (EditorData != null && EditorData.Item != null)
                {
                    Items_ = EditorData.Item.ColumnListChangeHandler.GetItems();
                    Items_.BubbleSort();
                }
                else {
                    Items_ = new();
                }
                return Items_;
            }
        }
        GrilleColumn GetCurrentColumn => EditorData.Item.ColumnListChangeHandler.Items.Where(key => key.Position == GrilleColumnPosition).FirstOrDefault();

        private GrilleColumn GrilleColumn_
        {
            get { return GetCurrentColumn; }
            set
            {
                AppState.Update = true;
                if (value != null && Items.Contains(value))
                {
                    EditorData.Item.UpdateColumn(value);
                }
                if (value != null)
                {
                    GrilleColumnPosition = value.Position;
                    GrilleColumnPositionChanged.InvokeAsync(GrilleColumnPosition);
                }
                EditorDataChanged.InvokeAsync(EditorData);
            }
        }
        public IEnumerable<GrilleColumn> GrilleColumns
        {
            get { return new List<GrilleColumn>() { GrilleColumn_ }; }
            set
            {
                if (value != null && value.Count() > 0)
                {
                    GrilleColumn_ = value.FirstOrDefault();
                }
            }
        }

        private DxContextMenu DxContextMenu_ { get; set; }

        public async void showContextMenu_(MouseEventArgs args)
        {
            await DxContextMenu_.ShowAsync(args);
        }

         Task OnItemClick(ContextMenuItemClickEventArgs arg)
        {
            string Text = arg.ItemInfo.Text;
            if (GrilleColumn_ != null)
            {
                int position = GrilleColumnPosition;
                if (Text == AppState["Move.up"])
                {
                    if (GrilleColumn_.Position > 0)
                    {
                        position = GrilleColumn_.Position - 1;
                        EditorData.Item.reverseColumn(GrilleColumn_.Position, GrilleColumn_.Position - 1, GrilleColumn_);
                    }
                }
                else
                if (Text == AppState["Move.down"])
                {
                    if (GrilleColumn_.Position + 1 < Items.Count)
                    {
                        position = GrilleColumn_.Position + 1;
                        EditorData.Item.reverseColumn(GrilleColumn_.Position, GrilleColumn_.Position + 1, GrilleColumn_);
                    }
                }
                else
                if (Text == AppState["Move.up.upper"])
                {
                    if (GrilleColumn_.Position - 5 >= 0)
                    {
                        position = GrilleColumn_.Position - 5;
                        EditorData.Item.reverseColumn(GrilleColumn_.Position, GrilleColumn_.Position - 5, GrilleColumn_);
                    }
                }
                else
                if (Text == AppState["Move.down.upper"])
                {
                    if (GrilleColumn_.Position + 5 < Items.Count)
                    {
                        position = GrilleColumn_.Position + 5;
                        EditorData.Item.reverseColumn(GrilleColumn_.Position, GrilleColumn_.Position + 5, GrilleColumn_);
                    }
                }
                else
                if (Text == AppState["Move.start"])
                {
                    position = 0;
                    EditorData.Item.reverseColumn(GrilleColumn_.Position, 0, GrilleColumn_);
                }
                else
                if (Text == AppState["Move.end"])
                {
                    if (Items.Count > 0)
                    {
                        position = Items.Count - 1;
                        EditorData.Item.reverseColumn(GrilleColumn_.Position, Items.Count - 1, GrilleColumn_);
                    }
                }
                else
                if (Text == AppState["remove"])
                {
                    EditorData.Item.DeleteOrForgetColumn(GrilleColumn_);
                    if (GrilleColumnPosition > 0)
                    {
                        position = GrilleColumnPosition - 1;
                    }
                }
                else
                if (Text == AppState["Rename"])
                {


                }
                GrilleColumnPositionChanged.InvokeAsync(position);
                return EditorDataChanged.InvokeAsync(EditorData);
            }
            return Task.CompletedTask;
        }
    }
}
