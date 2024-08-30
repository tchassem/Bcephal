using Bcephal.Models.Joins;
using Microsoft.AspNetCore.Components;
using Bcephal.Models.Base;
using Bcephal.Models.Grids;
using Bcephal.Blazor.Web.Base.Services;
using System.Collections.Generic;
using System.Threading.Tasks;
using Microsoft.AspNetCore.Components.Web;
using DevExpress.Blazor;
using System.Collections.ObjectModel;
using System.Linq;

namespace Bcephal.Blazor.Web.Reporting.Pages.Joins
{
    public partial class JoinGridComponent : ComponentBase
    {
        [Inject]
        public AppState AppState { get; set; }
        
        [Parameter]
        public EditorData<Join> EditorData { get; set; }
        bool ShowCheckboxes { get; set; } = true;
        public string ActionType { get; set; } = "";
        public string FuncItemTitle { get; set; } = "";
        [Parameter]
        public EventCallback<EditorData<Join>> EditorDataChanged { get; set; }
        public JoinGrid SelectedItem { get; set; }
        public DxContextMenu ContextMenu { get; set; }
        [Parameter]
        public bool Editable { get; set; }
        public bool IsSmallScreen { get; set; } = false;
        public SmartGrille Item_ { get; set; }

        public ObservableCollection<JoinGrid> Items { get {

                if(EditorData == null || EditorData.Item == null|| EditorData.Item.GridListChangeHandler == null)
                {
                    return new();
                }
                return EditorData.Item.GridListChangeHandler.Items;
            } 
        } 

        public SmartGrille Item { 
            get 
            {
                return Item_;
            }

            set
            {
                Item_ = value;
                if(value != null) {
                    JoinGrid JoinGrid = new() { GridId = value.Id, Name = value.Name};
                    var contain = EditorData.Item.GridListChangeHandler.Items.Where(ele => ele.GridId == value.Id && ele.Name.Equals(value.Name)).ToList();
                    if(contain.Count == 0)
                    {
                        EditorData.Item.AddGrid(JoinGrid);
                        EditorDataChanged.InvokeAsync(EditorData);
                        Item_ = null;
                    }
                }  
            }
        }


        private IEnumerable<JoinGrid> SelectedValues { get; set; } = new ObservableCollection<JoinGrid>();

       
        public async Task OnContextMenu(MouseEventArgs e, JoinGrid item)
        {
            SelectedItem = item;
            await ContextMenu.ShowAsync(e);
        }
        public async Task SelectItem(MouseEventArgs e, JoinGrid item)
        {
            SelectedItem = item;
            StateHasChanged();
            await Task.CompletedTask;
        }

        public async void showContextMenu(MouseEventArgs args)
        {
            await ContextMenu.ShowAsync(args);
        }
        async void OnItemClick(ContextMenuItemClickEventArgs arg)
        {
            string Text = arg.ItemInfo.Text;
            JoinGrid joinGrid = SelectedValues.ToList().FirstOrDefault();
            if (joinGrid != null)
            {
                if (Text == AppState["Move.up"])
                {
                    if (joinGrid.Position > 0)
                    {
                        EditorData.Item.reverseColumn(joinGrid.Position, joinGrid.Position - 1, joinGrid);
                    }
                }
                else
                if (Text == AppState["Move.down"])
                {
                    if (joinGrid.Position + 1 < EditorData.Item.GridListChangeHandler.Items.Count)
                    {
                        EditorData.Item.reverseColumn(joinGrid.Position, joinGrid.Position + 1, joinGrid);
                    }
                }
                else
                if (Text == AppState["Move.up.upper"])
                {
                    if (joinGrid.Position - 5 >= 0)
                    {
                        EditorData.Item.reverseColumn(joinGrid.Position, joinGrid.Position - 5, joinGrid);
                    }
                }
                else
                if (Text == AppState["Move.down.upper"])
                {
                    if (joinGrid.Position + 5 < EditorData.Item.GridListChangeHandler.Items.Count)
                    {
                        EditorData.Item.reverseColumn(joinGrid.Position, joinGrid.Position + 5, joinGrid);
                    }
                }
                else
                if (Text == AppState["Move.start"])
                {
                    EditorData.Item.reverseColumn(joinGrid.Position, 0, joinGrid);
                }
                else
                if (Text == AppState["Move.end"])
                {
                    if (EditorData.Item.GridListChangeHandler.Items.Count > 0)
                    {
                        EditorData.Item.reverseColumn(joinGrid.Position, EditorData.Item.GridListChangeHandler.Items.Count - 1, joinGrid);
                    }
                }
                else
                if (Text == AppState["Delete"])
                {
                    var gridIds = EditorData.Item.ColumnListChangeHandler.Items.Where(item => item.GridId == joinGrid.GridId).ToList();
                     if(gridIds.Count() == 0)
                    {
                        EditorData.Item.DeleteOrForgetGrid(joinGrid);
                    }
                    else
                    {
                        
                    }
                  
                    
                }
                await EditorDataChanged.InvokeAsync(EditorData);
            }
            return;
        }
        private JoinEditorData GetEditorData()
        {
            JoinEditorData JoinEditorData = (JoinEditorData)EditorData;
            return JoinEditorData;
        }
    }
}
