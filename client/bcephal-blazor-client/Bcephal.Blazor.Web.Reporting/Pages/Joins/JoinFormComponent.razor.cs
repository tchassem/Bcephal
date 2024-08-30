using Bcephal.Blazor.Web.Base.Services;
using Bcephal.Models.Base;
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
    public partial class JoinFormComponent : ComponentBase
    {
        [Inject] public AppState AppState { get; set; }
        [Parameter] public int ActiveTabIndex_ { get; set; } = 0;
        [Parameter] public EditorData<Join> EditorData { get; set; }
        [Parameter] public ObservableCollection<HierarchicalData> Entities { get; set; }
        [Parameter] public bool Editable { get; set; }
        [Parameter] public EventCallback<EditorData<Join>> EditorDataChanged { get; set; }

         public int ActiveTabIndex
        {
            get => ActiveTabIndex_;
            set
            {
                ActiveTabIndex_ = value;                
                ActiveTabIndex_Changed.InvokeAsync(ActiveTabIndex_);
            }
        }
        [Parameter] public EventCallback<int> ActiveTabIndex_Changed { get; set; }

    }
}
