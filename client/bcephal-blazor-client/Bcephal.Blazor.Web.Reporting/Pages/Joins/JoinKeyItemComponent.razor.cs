using Bcephal.Blazor.Web.Base.Services;
using Bcephal.Models.Base;
using Bcephal.Models.Grids;
using Bcephal.Models.Joins;
using Microsoft.AspNetCore.Components;
using System.Collections.Generic;
using System.Collections.ObjectModel;
using System.Linq;

namespace Bcephal.Blazor.Web.Reporting.Pages.Joins
{
    public partial class JoinKeyItemComponent : ComponentBase
    {
        [Inject]
        public AppState AppState { get; set; }

        [Parameter]
        public bool Editable { get; set; } = true;

        [Parameter]
        public bool IsAdded { get; set; } = false;

        [Parameter]
        public EditorData<Join> EditorData { get; set; }

        [Parameter]
        public JoinKey JoinKey { get; set; }

        [Parameter]
        public EventCallback<JoinKey> AddKeyCallback { get; set; }

        [Parameter]
        public EventCallback<JoinKey> UpdKeyCallback { get; set; }

        [Parameter]
        public EventCallback<JoinKey> DelKeyCallback { get; set; }

        public bool IsSmallScreen { get; set; }

        public ObservableCollection<SmallGrilleColumn> Columns1 { get; set; }

        public ObservableCollection<SmallGrilleColumn> Columns2 { get; set; }

        public ObservableCollection<JoinGrid> JoinGridData { get {

                if (EditorData == null || EditorData.Item == null 
                    || EditorData.Item.GridListChangeHandler == null || EditorData.Item.GridListChangeHandler.Items == null)
                {
                    return new ObservableCollection<JoinGrid>();
                }
             return   EditorData.Item.GridListChangeHandler.Items;
            } 
        }

        protected override void OnInitialized()
        {
            base.OnInitialized();
            if (JoinKey == null)
            {
                JoinKey = new();
            }
        }

        public JoinGrid JoinGrid1
        {
            get
            {
                if (JoinKey != null && JoinKey.GridId1 != null)
                {
                    var joinGrid = JoinGridData.Where((jg) => jg.GridId.HasValue && JoinKey.GridId1.HasValue && jg.GridId == JoinKey.GridId1).FirstOrDefault();
                    if (Columns1 == null)
                    {
                        SmartGrille? smartGrille = GetEditorData().Grids.Where((sm) => sm.Id.HasValue && joinGrid.GridId.HasValue && sm.Id == joinGrid.GridId).FirstOrDefault();
                        Columns1 = smartGrille != null ? smartGrille.Columns : null;
                    }
                    return joinGrid;
                }
                return null;
            }
            set
            {
                JoinKey.GridId1 = value.GridId;
                Columns1 = GetEditorData().Grids.Where((sm) => sm.Id.HasValue && value.GridId.HasValue && sm.Id == value.GridId).FirstOrDefault().Columns;
                UpdateKey(JoinKey);
            }
        }

        public SmallGrilleColumn Column1
        {
            get
            {
                if (JoinKey != null && JoinKey.ColumnId1 != null && Columns1 != null)
                {
                    var item =  Columns1.Where((c) => c.Id.HasValue && JoinKey.ColumnId1.HasValue && c.Id == JoinKey.ColumnId1 && c.Type.Equals(JoinKey.dimensionType)).FirstOrDefault();
                    return item;
                }
                return null;
            }
            set
            {
                JoinKey.ColumnId1 = value.Id;
                JoinKey.dimensionType = value.Type;
                JoinKey.ValueId1 = value.DimensionId;
                if (!IsAdded)
                {
                    AddKey();
                }
                UpdateKey(JoinKey);
            }
        }

        public JoinGrid JoinGrid2
        {
            get
            {
                if (JoinKey != null && JoinKey.GridId2 != null)
                {
                    var joinGrid = JoinGridData.Where((jg) => jg.GridId == JoinKey.GridId2).FirstOrDefault();
                    if (Columns2 == null)
                    {
                        SmartGrille? smartGrille = GetEditorData().Grids.Where((sm) => sm.Id.HasValue && joinGrid.GridId.HasValue && sm.Id == joinGrid.GridId).FirstOrDefault();
                        Columns2 = smartGrille != null ? smartGrille.Columns : null;
                    }
                    return joinGrid;
                }
                return null;
            }
            set
            {
                JoinKey.GridId2 = value.GridId;
                Columns2 = GetEditorData().Grids.Where((sm) => sm.Id.HasValue && value.GridId.HasValue && sm.Id == value.GridId).FirstOrDefault().Columns;
                UpdateKey(JoinKey);
            }
        }

        public SmallGrilleColumn Column2
        {
            get
            {
                if (JoinKey != null && JoinKey.ColumnId2 != null && Columns2 != null)
                {
                    return Columns2.Where((c) => c.Id.HasValue && JoinKey.ColumnId1.HasValue && c.Id == JoinKey.ColumnId2).FirstOrDefault();
                }
                return null;
            }
            set
            {
                JoinKey.ColumnId2 = value.Id;
                JoinKey.ValueId2 = value.DimensionId;
                UpdateKey(JoinKey);
            }
        }

        private bool IsFilled()
        {
            if (JoinKey.GridId1 != null && JoinKey.GridId1 != 0 && JoinKey.ColumnId1 != null && JoinKey.ColumnId1 != 0)
            {
                return true;
            }
            return false;
        }

        private async void AddKey()
        {
            if (IsFilled())
            {
                await AddKeyCallback.InvokeAsync(JoinKey);
                //resetFields();
                //StateHasChanged();
            }
        }

        private async void RemoveKey(JoinKey Item)
        {
            await DelKeyCallback.InvokeAsync(Item);
        }

        private async void UpdateKey(JoinKey Item)
        {
            if (Item.IsPersistent)
            {
                await UpdKeyCallback.InvokeAsync(Item);
            }
        }

        private void resetFields()
        {
            JoinKey = new();
            Columns1 = null;
            Columns2 = null;
        }

        private JoinEditorData GetEditorData()
        {
            JoinEditorData JoinEditorData = (JoinEditorData)EditorData;
            return JoinEditorData;
        }
    }
}
