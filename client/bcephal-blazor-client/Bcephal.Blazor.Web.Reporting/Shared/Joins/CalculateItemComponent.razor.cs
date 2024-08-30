using Bcephal.Blazor.Web.Base.Services;
using Bcephal.Models.Base;
using Bcephal.Models.Filters;
using Bcephal.Models.Grids;
using Bcephal.Models.Joins;
using Microsoft.AspNetCore.Components;
using System;
using System.Collections.Generic;
using System.Collections.ObjectModel;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Bcephal.Blazor.Web.Reporting.Shared.Joins
{
    public partial class CalculateItemComponent : ComponentBase
    {
        [Parameter]
        public JoinColumnCalculateItem Item { get; set; }

        [Parameter]
        public JoinColumn JoinColumn { get; set; }

        [Parameter]
        public EventCallback<JoinColumn> JoinColumnChanged { get; set; }

        [Parameter]
        public Action<JoinColumnCalculateItem> AddItemCallback { get; set; }
        [Parameter]
        public Action<JoinColumnCalculateItem> UpdateItemCallback { get; set; }
        [Parameter]
        public Action<JoinColumnCalculateItem> DeleteItemCallback { get; set; }

        [Parameter]
        public EditorData<Join> EditorData { get; set; }

        [Parameter]
        public EventCallback<EditorData<Join>> EditorDataChanged { get; set; }

        [Inject]
        public AppState AppState { get; set; }

        [Parameter]
        public bool IsNew { get; set; }

        List<string> Signs = new() { "", "+", "-", "*", "/" };

       
        public JoinColumnCalculateItem Item_
        {
            get
            {
                return Item;
            }
            set
            {
                Item = value;
                //if (IsNew)
                //{
                //    JoinColumn.Properties.CalculateItemListChangeHandler.AddNew(Item);
                //    EditorData.Item.AddColumn(JoinColumn);
                //}
                //else
                //{
                //    JoinColumn.Properties.CalculateItemListChangeHandler.AddUpdated(Item);
                //    EditorData.Item.UpdateColumn(JoinColumn);
                //}
                //EditorDataChanged.InvokeAsync(EditorData);
            }
        }

        public string ColumnType
        {
            get
            {
                return Item.Field.Type;
            }
            set
            {
                Item.Field.JoinColumnType = Item.Field.JoinColumnType.GetJoinColumnType(value,text => AppState[text]);
                Item.Field.DecimalValue = null;
                Item.Field.GridId = null;
                if (IsNew)
                {
                    JoinColumn.Properties.AddCalculateItem(Item);
                }
                else
                {
                    JoinColumn.Properties.UpdateCalculateItem(Item);  
                }
                EditorData.Item.UpdateColumn(JoinColumn);
                EditorDataChanged.InvokeAsync(EditorData);
            }
        }

        public JoinGrid JoinGrid__ { get; set; } = new();
        public JoinGrid JoinGrid_
        {
            get
            {
                if (Item != null && Item.Field != null && Item.Field.GridId.HasValue)
                {
                    return EditorData.Item.GridListChangeHandler.Items.Where(x => x.GridId == Item.Field.GridId).FirstOrDefault();
                }
                return JoinGrid__;
            }
            set
            {
                JoinGrid__ = value;
                if(value != null && value.GridId.HasValue)
                {
                    Item.Field.GridId = value.GridId;
                    Item.Field.ColumnId = null;
                    SelectedColumn = null;
                    JoinColumn.Properties.UpdateCalculateItem(Item);
                    EditorData.Item.UpdateColumn(JoinColumn);
                    EditorDataChanged.InvokeAsync(EditorData);
                }
            }
        }

        protected override Task OnInitializedAsync()
        {
            if (IsNew)
            {
                Item = new();
                Item.Field = new();
            }
            return base.OnInitializedAsync();
        }

        private ObservableCollection<SmallGrilleColumn> GetColumns()
        {
            ObservableCollection<SmallGrilleColumn> obs = new();
            SmartGrille smartGrille = new();
            if (GetEditorData() != null && GetEditorData().Grids != null && JoinGrid_ != null)
            {
                smartGrille = GetEditorData().Grids.Where(x => x.Id == JoinGrid_.GridId).FirstOrDefault();
                if (smartGrille != null && smartGrille.Columns != null)
                {
                    obs = new ObservableCollection<SmallGrilleColumn>(smartGrille.Columns.Where(x=> x.Type.Equals(DimensionType.MEASURE)));
                }
            }
            return obs;
        }
      
        private JoinEditorData GetEditorData()
        {
            JoinEditorData JoinEditorData = (JoinEditorData)EditorData;
            return JoinEditorData;
        }
        public SmallGrilleColumn SelectedColumn
        {
            get
            {
                if (Item != null && Item.Field != null && Item.Field.ColumnId.HasValue && JoinGrid_.GridId.HasValue)
                {
                    ObservableCollection<SmallGrilleColumn> obs = GetEditorData().Grids.Where(x => x.Id == JoinGrid_.GridId).FirstOrDefault().Columns;
                    return obs.Where(x => x.Id == Item.Field.ColumnId).FirstOrDefault();
                }
                return null;
            }
            set { 

                if (value != null && value.Id.HasValue)
                {
                    Item.Field.ColumnId = value.Id;
                    Item.Field.DimensionId = value.DimensionId;
                    Item.Field.DimensionName = value.DimensionName;
                    Item.Field.DimensionType = value.Type;
                    JoinColumn.Properties.UpdateCalculateItem(Item);
                    EditorData.Item.UpdateColumn(JoinColumn);
                    EditorDataChanged.InvokeAsync(EditorData);
                }
            }
        }

        public JoinColumn SelectedJoinColumn
        {
            get
            {
                if (Item != null && Item.Field != null && Item.Field.ColumnId.HasValue)
                {
                    return EditorData.Item.ColumnListChangeHandler.Items.Where(x => x.ColumnId == Item.Field.ColumnId).FirstOrDefault();
                }
                return null;
            }
            set
            {
                if (value != null )
                {
                    Item.Field.ColumnId = value.ColumnId;
                    Item.Field.DimensionId = value.DimensionId;
                    Item.Field.DimensionName = value.DimensionName;
                    Item.Field.DimensionType = value.Type;
                    JoinColumn.Properties.UpdateCalculateItem(Item);
                    EditorData.Item.UpdateColumn(JoinColumn);
                    EditorDataChanged.InvokeAsync(EditorData);
                }
            }
        }

        private void DeleteItem(JoinColumnCalculateItem ite)
        {
            JoinColumn.Properties.DeleteOrForgetCalculateItem(ite);
            EditorData.Item.UpdateColumn(JoinColumn);
            EditorDataChanged.InvokeAsync(EditorData);
        }
    }
}
