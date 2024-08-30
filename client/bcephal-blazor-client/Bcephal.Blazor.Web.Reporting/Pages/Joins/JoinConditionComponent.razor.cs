using Bcephal.Blazor.Web.Base.Services;
using Bcephal.Blazor.Web.Base.Shared.Component;
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
    public partial class JoinConditionComponent
    {
        [Inject]
        public AppState AppState { get; set; }

        [Parameter]
        public EditorData<Join> EditorData { get; set; }

        [Parameter]
        public EventCallback<EditorData<Join>> EditorDataChanged { get; set; }

        [Parameter]
        public ObservableCollection<HierarchicalData> Entities { get; set; }

        [Parameter]
        public bool Editable { get; set; } = true;

        private List<Models.Dimensions.Attribute> ModelsAttributes => InitAttributes();

        private JoinEditorData JoinEditorData => GetEditorData();

        private CardComponent CardComponentRef { get; set; }

        private void AddCondition(JoinCondition Item)
        {
            EditorData.Item.AddCondition(Item);
            EditorDataChanged.InvokeAsync(EditorData);
            CardComponentRef.RefreshBody();

        }

        private void UpdateCondition(JoinCondition Item)
        {
            EditorData.Item.UpdateCondition(Item);
            EditorDataChanged.InvokeAsync(EditorData);
            CardComponentRef.RefreshBody();
        }

        private void RemoveCondition(JoinCondition Item)
        {
            EditorData.Item.DeleteOrForgetCondition(Item);
            EditorDataChanged.InvokeAsync(EditorData);
            CardComponentRef.RefreshBody();
        }

        private List<Models.Dimensions.Attribute> InitAttributes()
        {
            List<Models.Dimensions.Attribute> ModelsAttributes_ = new List<Models.Dimensions.Attribute>();
            foreach (var item in Entities)
            {
                if (item is Models.Dimensions.Entity)
                {
                    Models.Dimensions.Entity item_ = (Models.Dimensions.Entity)item;
                    ModelsAttributes_.AddRange(item_.Descendents);
                }
                else
                    if (item is Models.Dimensions.Attribute)
                {
                    Models.Dimensions.Attribute item_ = (Models.Dimensions.Attribute)item;
                    ModelsAttributes_.AddRange(item_.Descendents);
                }
            }
            return ModelsAttributes_;
        }

        private JoinEditorData GetEditorData()
        {
            return (JoinEditorData)EditorData;
        }


    }
}
