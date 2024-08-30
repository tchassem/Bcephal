using Bcephal.Blazor.Web.Base.Services;
using Bcephal.Blazor.Web.Base.Shared;
using Bcephal.Blazor.Web.Base.Shared.Component;
using Bcephal.Models.Base;
using Bcephal.Models.Grids;
using Bcephal.Models.Joins;
using DevExpress.Blazor;
using Microsoft.AspNetCore.Components;
using Microsoft.AspNetCore.Components.Web;
using System;
using System.Collections.Generic;
using System.Collections.ObjectModel;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Bcephal.Blazor.Web.Reporting.Shared.Joins
{
    public partial class ColumnReportComponent: ComponentBase
    {
        [Inject] public AppState AppState { get; set; }
        [Inject] public IToastService toastService { get; set; }
        [Parameter] public bool Editable { get; set; }
        [Parameter] public EditorData<Join> EditorData { get; set; }
        [Parameter] public EventCallback<EditorData<Join>> EditorDataChanged { get; set; }
        [Parameter] public EventCallback<int?> JoinColumnPositionChanged { get; set; }
        [Parameter] public int? JoinColumnPosition { get; set; }
        private DxContextMenu DxContextMenus { get; set; }

        private RenderFormContent SelectedColumnRef { get; set; }
        private RenderFormContent CustomListBoxRef { get; set; }



        private IEnumerable<JoinColumn> SelectedValues 
        {
            get 
            {
                return EditorData.Item.ColumnListChangeHandler.Items.Where(x => JoinColumnPosition.HasValue && x.Position == JoinColumnPosition.Value).ToList();
            }
            set
            {
                if(value != null)
                {
                    var el = value.FirstOrDefault();
                    if (el != null)
                    {
                        JoinColumnPosition = el.Position;
                        JoinColumnPositionChanged.InvokeAsync(JoinColumnPosition);
                    }
                }
            } 
        }     

        public void selectedColumn(JoinColumn joinColumn__)
        {
            SelectedValues = new List<JoinColumn>() { joinColumn__ };
        }        

        public JoinGrid SelectedGrid_ { get; set; }
        public JoinGrid SelectedGrid
        {
            get
            {
                return SelectedGrid_;
            }
            set
            {
                SelectedGrid_ = value;
                SelectedColumn_ = null;
                SelectedColumnRef.StateHasChanged_();
            }
        }

        private SmallGrilleColumn SelectedColumn_ { get; set; } 
        public SmallGrilleColumn SelectedColumn
        {
            get
            {
                return SelectedColumn_;
            }
            set
            {
                if(value != null)
                {
                    SelectedColumn_ = value;
                    JoinColumn joinColumn = new(value);
                    var contains = EditorData.Item.ColumnListChangeHandler.Items.Where(ite => ite.ColumnId.Equals(joinColumn.ColumnId)).ToList();
                    if (contains.Count() == 0)
                    {
                        EditorData.Item.AddColumn(joinColumn);
                        EditorDataChanged.InvokeAsync(EditorData);
                        SelectedValues = EditorData.Item.ColumnListChangeHandler.Items;
                        selectedColumn(joinColumn);
                        CustomListBoxRef.StateHasChanged_();
                    }
                    else
                    {
                        toastService.ShowError(AppState["duplicate.join.column", value.Name]);
                    }
                }
            }
        }

        private ObservableCollection<SmallGrilleColumn> GetColumns()
        {
                if (GetEditorData() != null && GetEditorData().Grids != null && SelectedGrid !=null)
                {
                    SmartGrille smartGrille = GetEditorData().Grids.Where(x => x.Id == SelectedGrid.GridId).FirstOrDefault();
                    if( smartGrille != null && smartGrille.Columns != null)
                    {
                        return smartGrille.Columns;
                    }
                }
            return new();
        }

        private JoinEditorData GetEditorData()
        {
            JoinEditorData JoinEditorData = (JoinEditorData)EditorData;
            return JoinEditorData;
        }


        protected override Task OnAfterRenderAsync(bool firstRender)
        {
            if(firstRender && EditorData != null &&  EditorData.Item.ColumnListChangeHandler.Items.Any())
            {
                selectedColumn(EditorData.Item.ColumnListChangeHandler.Items.FirstOrDefault());
            }
            return base.OnAfterRenderAsync(firstRender);
        }

        private void AddColumn()
        {
            JoinColumn joinColumn = new();
            joinColumn.Name = CreateColumnName();
            joinColumn.Category = JoinColumnCategory.CUSTOM;
            EditorData.Item.AddColumn(joinColumn);
            EditorDataChanged.InvokeAsync(EditorData);
            selectedColumn(joinColumn);
            CustomListBoxRef.StateHasChanged_();
        }

        async void OnItemClick(ContextMenuItemClickEventArgs arg)
        {
            string Text = arg.ItemInfo.Text;
            JoinColumn joinColumn = SelectedValues.FirstOrDefault();
            if (joinColumn != null)
            {
                if (Text == AppState["Move.up"])
                {
                    if (joinColumn.Position > 0)
                    {
                        EditorData.Item.reverseColumn(joinColumn.Position, joinColumn.Position - 1, joinColumn);
                        JoinColumnPosition = joinColumn.Position;
                    }
                }
                else
                if (Text == AppState["Move.down"])
                {
                    if (joinColumn.Position + 1 < EditorData.Item.ColumnListChangeHandler.Items.Count)
                    {
                       EditorData.Item.reverseColumn(joinColumn.Position, joinColumn.Position + 1, joinColumn);
                       JoinColumnPosition = joinColumn.Position;
                    }
                }
                else
                if (Text == AppState["Move.up.upper"])
                {
                    if (joinColumn.Position - 5 >= 0)
                    {
                        EditorData.Item.reverseColumn(joinColumn.Position, joinColumn.Position - 5, joinColumn);
                    }
                }
                else
                if (Text == AppState["Move.down.upper"])
                {
                    if (joinColumn.Position + 5 < EditorData.Item.ColumnListChangeHandler.Items.Count)
                    {
                        EditorData.Item.reverseColumn(joinColumn.Position, joinColumn.Position + 5, joinColumn);
                    }
                }
                else
                if (Text == AppState["Move.start"])
                {
                    EditorData.Item.reverseColumn(joinColumn.Position, 0, joinColumn);
                }
                else
                if (Text == AppState["Move.end"])
                {
                    if (EditorData.Item.ColumnListChangeHandler.Items.Count > 0)
                    {
                        EditorData.Item.reverseColumn(joinColumn.Position, EditorData.Item.ColumnListChangeHandler.Items.Count - 1, joinColumn);
                    }
                }
                else
                if (Text == AppState["Delete"])
                {
                    EditorData.Item.DeleteOrForgetColumn(joinColumn);
                    if (joinColumn.Position == 0 && EditorData.Item.ColumnListChangeHandler.Items.Count() > 0)
                    {
                        JoinColumnPosition = joinColumn.Position;
                    }
                    else
                    if (joinColumn.Position > EditorData.Item.ColumnListChangeHandler.Items.Count() && EditorData.Item.ColumnListChangeHandler.Items.Count() > 0)
                    {
                        JoinColumnPosition = EditorData.Item.ColumnListChangeHandler.Items.Count();
                    }
                    else
                    if (EditorData.Item.ColumnListChangeHandler.Items.Count() == 0)
                    {
                        JoinColumnPosition = null;
                    }
                    SelectedColumn = null;
                    SelectedGrid = null;
                }
                await EditorDataChanged.InvokeAsync(EditorData);
                await JoinColumnPositionChanged.InvokeAsync(JoinColumnPosition);
                await CustomListBoxRef.StateHasChanged_();
            }
            return;
        }
       
        public async void showContextMenu_(MouseEventArgs args)
        {
            if(DxContextMenus != null)
            {
                await DxContextMenus.ShowAsync(args);
            }
        }

        private string CreateColumnName()
        {
            int offset = 1;
            bool contains = true;
            string name = "";
            while (contains)
            {
                name = "Column" + offset.ToString();
                contains = EditorData.Item.ColumnListChangeHandler.Items.Where(ite => ite.Name.Equals(name)).Any();
                offset++;
            } 
            return name;
        }


    }
}
