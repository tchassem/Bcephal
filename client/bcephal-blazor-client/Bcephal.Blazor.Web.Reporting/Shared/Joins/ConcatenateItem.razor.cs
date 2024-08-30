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
   public partial class ConcatenateItem
    {

        [Parameter]
        public JoinColumnType Type { get; set; }
         [Inject]
         public AppState AppState { get; set; }
        [Parameter] 
        public bool Editable { get; set; } = true;
        [Parameter]
        public JoinColumnConcatenateItem JoinColumnConcatenateItem { get; set; }

        [Parameter]
        public JoinColumn JoinColumn { get; set; }

        [Parameter]
        public EventCallback<JoinColumn> JoinColumnChanged { get; set; }

        [Parameter]
        public EditorData<Join> EditorData { get; set; }

        [Parameter]
        public EventCallback<EditorData<Join>> EditorDataChanged { get; set; }


        [Parameter]
        public bool IsNew { get; set; }
       
        protected override Task OnInitializedAsync()
        {
            if (IsNew)
            {
                JoinColumnConcatenateItem = new();
                JoinColumnConcatenateItem.Field = new();
            }
            return base.OnInitializedAsync();
        }

        private void DeleteItem(JoinColumnConcatenateItem ite)
        {
            JoinColumn.Properties.ConcatenateItemListChangeHandler.AddDeleted(ite);
            JoinColumnChanged.InvokeAsync(JoinColumn);
            EditorData.Item.UpdateColumn(JoinColumn);
            EditorDataChanged.InvokeAsync(EditorData);
        }
        public JoinGrid JoinGrid_
        {
            get
            {
                if (JoinColumnConcatenateItem != null && JoinColumnConcatenateItem.Field != null && JoinColumnConcatenateItem.Field.GridId.HasValue)
                {
                    return EditorData.Item.GridListChangeHandler.Items.Where(x => x.GridId == JoinColumnConcatenateItem.Field.GridId).FirstOrDefault();
                }
                return new();
            }
            set
            {
                if (value != null && value.GridId.HasValue)
                {
                    JoinColumnConcatenateItem.Field.GridId = value.GridId;
                    if (IsNew)
                    {
                        JoinColumn.Properties.AddConcatenateItem(JoinColumnConcatenateItem);
                    }
                    else
                    {
                        JoinColumn.Properties.UpdateConcatenateItem(JoinColumnConcatenateItem);
                    }
                    JoinColumnChanged.InvokeAsync(JoinColumn);
                    EditorData.Item.UpdateColumn(JoinColumn);
                    EditorDataChanged.InvokeAsync(EditorData);
                }
            }
        }

        public string ColumnType
        {
            get
            {
                return JoinColumnConcatenateItem.Field.Type;
            }
            set
            {
                JoinColumnConcatenateItem.Field.JoinColumnType = JoinColumnConcatenateItem.Field.JoinColumnType.GetJoinColumnType(value, text => AppState[text]);
                if (IsNew)
                {
                    JoinColumn.Properties.AddConcatenateItem(JoinColumnConcatenateItem);
                }
                else
                {
                    JoinColumn.Properties.UpdateConcatenateItem(JoinColumnConcatenateItem);
                }
                JoinColumnChanged.InvokeAsync(JoinColumn);
                EditorData.Item.UpdateColumn(JoinColumn);
                EditorDataChanged.InvokeAsync(EditorData);
            }
        }
        public SmallGrilleColumn SelectedColumn
        {
            get
            {
                if (JoinColumnConcatenateItem != null && JoinColumnConcatenateItem.Field != null && JoinColumnConcatenateItem.Field.ColumnId.HasValue && JoinGrid_.GridId.HasValue)
                {
                    ObservableCollection<SmallGrilleColumn> obs = GetEditorData().Grids.Where(x => x.Id == JoinGrid_.GridId).FirstOrDefault().Columns;
                    return obs.Where(x => x.Id == JoinColumnConcatenateItem.Field.ColumnId).FirstOrDefault();
                }
                return null;
            }
            set
            {

                if (value != null && value.Id.HasValue)
                {
                    JoinColumnConcatenateItem.Field.ColumnId = value.Id;
                    JoinColumnConcatenateItem.Field.DimensionId = value.DimensionId;
                    JoinColumnConcatenateItem.Field.DimensionName = value.DimensionName;
                    JoinColumnConcatenateItem.Field.DimensionType = value.Type;
                    if (IsNew)
                    {
                        JoinColumn.Properties.AddConcatenateItem(JoinColumnConcatenateItem);
                    }
                    else
                    {
                        JoinColumn.Properties.UpdateConcatenateItem(JoinColumnConcatenateItem);
                    }
                    JoinColumnChanged.InvokeAsync(JoinColumn);
                    EditorData.Item.UpdateColumn(JoinColumn);
                    EditorDataChanged.InvokeAsync(EditorData);
                }
            }
        }

        public JoinColumn SelectedJoinColumn
        {
            get
            {
                Func<JoinColumn, bool> condition = (item) =>
                {
                    bool firstCondition = false;
                    if (JoinColumnConcatenateItem != null && JoinColumnConcatenateItem.Field != null && JoinColumnConcatenateItem.Field.ColumnId.HasValue)
                    {
                        firstCondition = item.ColumnId == JoinColumnConcatenateItem.Field.ColumnId;
                    }
                    bool secondCondition = (JoinColumnConcatenateItem.Field.JoinColumnType.IsCopy() && item.IsPersistent == true && item.Id == JoinColumnConcatenateItem.Field.ColumnId);
                    return firstCondition || secondCondition;
                };
                return EditorData.Item.ColumnListChangeHandler.Items.Where(condition).FirstOrDefault();
            }
            set
            {
                if (value != null && value.ColumnId.HasValue)
                {
                    if (JoinColumnConcatenateItem.Field.JoinColumnType.IsCopy())
                    {
                        JoinColumnConcatenateItem.Field.ColumnId = value.Id;
                        JoinColumnConcatenateItem.Field.DimensionId = null;
                        JoinColumnConcatenateItem.Field.DimensionName = null;
                        JoinColumnConcatenateItem.Field.DimensionType = value.Type;
                    }
                    else
                    {
                        JoinColumnConcatenateItem.Field.ColumnId = value.ColumnId;
                        JoinColumnConcatenateItem.Field.DimensionId = value.DimensionId;
                        JoinColumnConcatenateItem.Field.DimensionName = value.DimensionName;
                        JoinColumnConcatenateItem.Field.DimensionType = value.Type;
                    }
                }
                else
                {
                    JoinColumnConcatenateItem.Field.ColumnId = value.Id;
                    JoinColumnConcatenateItem.Field.DimensionId = null;
                    JoinColumnConcatenateItem.Field.DimensionName = null;
                    JoinColumnConcatenateItem.Field.DimensionType = value.Type;
                }
                if (IsNew)
                {
                    JoinColumn.Properties.AddConcatenateItem(JoinColumnConcatenateItem);
                }
                else
                {
                    JoinColumn.Properties.UpdateConcatenateItem(JoinColumnConcatenateItem);
                }

                JoinColumnChanged.InvokeAsync(JoinColumn);
                EditorData.Item.UpdateColumn(JoinColumn);
                EditorDataChanged.InvokeAsync(EditorData);
            }
        }

        public string StringValue
        {
            get
            {
                if (JoinColumnConcatenateItem != null && JoinColumnConcatenateItem.Field != null )
                {
                    return JoinColumnConcatenateItem.Field.StringValue;
                }
                return "";
            }
            set
            {
                JoinColumnConcatenateItem.Field.StringValue = value;
                JoinColumnConcatenateItem.Field.DimensionType = JoinColumn.Type;
            }
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
                    obs = new ObservableCollection<SmallGrilleColumn>(smartGrille.Columns.Where(x => x.Type.Equals(DimensionType.ATTRIBUTE)));
                }
            }
            return obs;
        }

        private JoinEditorData GetEditorData()
        {
            JoinEditorData JoinEditorData = (JoinEditorData)EditorData;
            return JoinEditorData;
        }

    }
}
