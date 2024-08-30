using Bcephal.Blazor.Web.Base.Services;
using Bcephal.Blazor.Web.Base.Shared.Component;
using Bcephal.Blazor.Web.Reporting.Shared.Joins;
using Bcephal.Models.Base;
using Bcephal.Models.Grids;
using Bcephal.Models.Joins;
using Microsoft.AspNetCore.Components;
using System;
using System.Collections.Generic;
using System.Collections.ObjectModel;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Bcephal.Blazor.Web.Reporting.Pages.Joins
{
    public partial class JoinGlobalComponent : ComponentBase
    {
        [Inject]
        public AppState AppState { get; set; }

        [Parameter]
        public bool Editable { get; set; }

        [Parameter]
        public EditorData<Join> EditorData { get; set; }

        [Parameter]
        public ObservableCollection<HierarchicalData> Entities { get; set; }

        private CardComponent CardComponentRef { get; set; }

        [Parameter]
        public EventCallback<EditorData<Join>> EditorDataChanged { get; set; }

        public int? JoinColumnPosition_ { get; set; } 
        public int? JoinColumnPosition 
        {
            get
            {
                return JoinColumnPosition_;
            }
            set
            {
                JoinColumnPosition_ = value;
                CardComponentRef.RefreshBody();
            }
        }

        //private void UpdateSelectedJoinColumn(int? JoinColumnPosition)
        //{
        //    JoinColumn_ = joinColumn;
        //    StateHasChanged();
        //}

        //private JoinColumn JoinColumn_
        //{
        //    get
        //    {
        //        return EditorData.Item.ColumnListChangeHandler.Items.Where(x => JoinColumn != null && x.Position == JoinColumn.Position).FirstOrDefault();
        //    }
        //    set
        //    {
        //        JoinColumn = value;
        //    }
        //}
    }
    }
