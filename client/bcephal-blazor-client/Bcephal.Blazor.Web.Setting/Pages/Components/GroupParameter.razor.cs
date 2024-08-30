using Bcephal.Blazor.Web.Base.Services;
using Bcephal.Models.Base;
using Bcephal.Models.Settings;
using Microsoft.AspNetCore.Components;
using System;
using System.Collections.ObjectModel;
using System.Threading.Tasks;
using System.Linq;
using System.Reflection;
using Bcephal.Blazor.Web.Setting.Services;
using System.Collections.Generic;
using Bcephal.Models.Utils;

namespace Bcephal.Blazor.Web.Setting.Pages.Components
{
    public partial class GroupParameter : ComponentBase
    {
        [Inject]
        AppState AppState { get; set; }
        [Parameter]
        public ParameterGroup Item { get; set; }

        public bool IsXsmallScreen { get; set; }
        [Parameter]
        public Func<ParameterGroup, Task<EditorData<Parameter>>> AutoCreateHandler { get; set; }

        [Parameter]
        public string Title { get; set; }
        [Parameter]
        public ObservableCollection<HierarchicalData> Entities { get; set; }
        [Parameter]
        public EditorData<Parameter> EditorData { get; set; }
        [Parameter]
        public EventCallback<EditorData<Parameter>> EditorDataChanged { get; set; }

        int ActiveTabIndex_ = 0;
        public async Task<EditorData<Parameter>> CreateAutomatically(ParameterGroup parameterGroup)
        {
            EditorData = await AutoCreateHandler?.Invoke(parameterGroup);
            await EditorDataChanged.InvokeAsync(EditorData);
            return EditorData;
        }

        public Parameter GetParameterByCode(ParameterGroupItem Item)
        {
            Parameter paramer = new();
            if (Item != null)
            {
                if (EditorData.Item != null)
                {
                    IEnumerable<Parameter> obs = EditorData.Item.Parameters.Items.Where(x => (x.Code == Item.Code && x.ParameterType == Item.Type));
                    if (obs != null && obs.Any())
                    {
                        paramer = obs.FirstOrDefault();
                    }
                    else
                    {
                        paramer.Code = Item.Code;
                        paramer.ParentCode = Item.ParentCode;
                        paramer.ParameterType = Item.Type;
                    }
                }
            }
            return paramer;
        }


    }
}

