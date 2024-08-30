using Bcephal.Blazor.Web.Base.Services;
using Bcephal.Models.Base;
using Bcephal.Models.Grids;
using Bcephal.Models.Joins;
using Microsoft.AspNetCore.Components;
using System.Collections.Generic;
using System.Collections.ObjectModel;
using System.Linq;
using System.Threading.Tasks;

namespace Bcephal.Blazor.Web.Reporting.Pages.Joins
{
    public partial class JoinConditionBaseComponent
    {
        [Inject]
        public AppState AppState { get; set; }

        [Parameter]
        public JoinEditorData JoinEditorData { get; set; }

        [Parameter]
        public bool Editable { get; set; } = true;

        [Parameter]
        public EventCallback<JoinConditionItem> AddOrUpdateConditionItemCallback { get; set; }

        [Parameter]
        public JoinConditionItem Item { get; set; }

        [Parameter]
        public JoinCondition JoinCondition { get; set; }

        public ObservableCollection<SmallGrilleColumn> Columns { get; set; }

        public bool IsSmallScreen { get; set; }

        public IEnumerable<JoinGrid> JoinGrids { get; set; } = new List<JoinGrid> { };

        protected override async Task OnInitializedAsync()
        {
            
            if(Item == null)
            {
                Item = new();
            }
            await base.OnInitializedAsync();
        }

        public JoinGrid JoinGrid
        {
            get
            {
                if (Item != null && Item.GridId != null)
                {
                    var joinGrid = JoinEditorData.Item.GridListChangeHandler.Items.Where((jg) => jg.GridId == Item.GridId).FirstOrDefault();
                    if (Columns == null && joinGrid.GridId != null)
                    {
                        if (JoinEditorData.Grids.Where((sm) => sm.Id == joinGrid.GridId).Any())
                        {
                            Columns = JoinEditorData.Grids.Where((sm) => sm.Id == joinGrid.GridId).First().Columns;
                        }
                    }
                    return joinGrid;
                }
                return null;
            }
            set
            {
                Item.GridId = value.GridId;
                if (JoinEditorData.Grids.Where((sm) => sm.Id == value.GridId).Any())
                {
                    Columns = JoinEditorData.Grids.Where((sm) => sm.Id == value.GridId).First().Columns;
                    //if (JoinCondition.Item1 != null && JoinCondition.Item1.ColumnId.HasValue && JoinCondition.Item2.GridId.HasValue)
                    //{
                    //    Columns = new ObservableCollection<SmallGrilleColumn>(Columns.Where((x) => x.Type.Equals(JoinCondition.Item1.DimensionType)));
                    //}
                    
                }
                   
            }
        }


        public SmallGrilleColumn Column
        {
            get
            {
                if (Item != null && Item.ColumnId != null && Columns != null)
                {
                    SmallGrilleColumn column = Columns.Where((c) => c.Id == Item.ColumnId).FirstOrDefault();
                    return column;
                }
                return null;
            }
            set
            {
                Item.ColumnId = value.Id;
                Item.DimensionType = value.Type;
                Item.DimensionName = value.DimensionName;
                Item.DimensionId = value.DimensionId;
                AddOrUpdateCondition();
            }
        }

        private async void AddOrUpdateCondition()
        {
           await AddOrUpdateConditionItemCallback.InvokeAsync(Item);
        }
    }
}
