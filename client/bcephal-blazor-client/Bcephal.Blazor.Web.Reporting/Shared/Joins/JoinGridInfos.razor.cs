
using Bcephal.Blazor.Web.Base.Services;
using Bcephal.Models.Base;
using Bcephal.Models.Grids;
using Bcephal.Models.Joins;
using Microsoft.AspNetCore.Components;
using System.Threading.Tasks;

namespace Bcephal.Blazor.Web.Reporting.Shared.Joins
{
    public partial class JoinGridInfos : ComponentBase
    {
        bool IsSmallScreen { get; set; }
        public string LabelWidth { get; set; } = Constant.GROUP_INFOS_LABEL_WIDTH;
        public string TextWidth { get; set; } = Constant.GROUP_INFOS_TEXT_WIDTH;
        [Inject]
        public AppState AppState { get; set; }
        [Inject]
        public IToastService toastService { get; set; }

        [Parameter]
        public EditorData<Join> EditorData { get; set; }

        [Parameter]
        public bool Editable { get; set; } = true;

        [Parameter]
        public EventCallback<EditorData<Join>> EditorDataChanged { get; set; }



      
        public bool? Consolidated
        {
            get
            {
                if (EditorData != null && EditorData.Item != null)
                {

                    return EditorData.Item.Consolidated;
                }
                return false;
            }
            set
            {
                if (EditorData != null && EditorData.Item != null)
                {
                    EditorData.Item.Consolidated = value.Value;
                    EditorDataChanged.InvokeAsync(EditorData);
                }
            }
        }
        public bool? ShowAllRowsByDefault
        {
            get
            {
                if (EditorData != null && EditorData.Item != null)
                {

                    return EditorData.Item.ShowAllRowsByDefault;
                }
                return false;
            }
            set
            {
                if (EditorData != null && EditorData.Item != null)
                {
                    EditorData.Item.ShowAllRowsByDefault = value.Value;
                    EditorDataChanged.InvokeAsync(EditorData);
                }
            }
        }

        public bool? AllowAccounting
        {
            get
            {
                if (EditorData != null && EditorData.Item != null)
                {

                    return EditorData.Item.AllowLineCounting;
                }
                return false;
            }
            set
            {
                if (EditorData != null && EditorData.Item != null && value.HasValue)
                {
                    EditorData.Item.AllowLineCounting = value.Value;
                    EditorDataChanged.InvokeAsync(EditorData);
                }
            }
        }
    }
}
