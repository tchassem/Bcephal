
using Bcephal.Blazor.Web.Base.Services;
using Bcephal.Models.Base;
using Bcephal.Models.Grids;
using Microsoft.AspNetCore.Components;
using System.Threading.Tasks;

namespace Bcephal.Blazor.Web.Sourcing.Shared.Grille
{
    public partial class InputGridInfos : ComponentBase
    {
        bool IsSmallScreen { get; set; }
        public string LabelWidth { get; set; } = Constant.GROUP_INFOS_LABEL_WIDTH;
        public string TextWidth { get; set; } = Constant.GROUP_INFOS_TEXT_WIDTH;
        [Inject]
        public AppState AppState { get; set; }
        [Inject]
        public IToastService toastService { get; set; }

        [Parameter]
        public EditorData<Models.Grids.Grille> EditorData { get; set; }

        [Parameter]
        public bool Editable { get; set; } = true;

        [Parameter]
        public EventCallback<EditorData<Models.Grids.Grille>> EditorDataChanged { get; set; }

        private bool IsInputGrid => GrilleType.INPUT.Equals(EditorData.Item.Type);

        private bool IsReportGrid => GrilleType.REPORT.Equals(EditorData.Item.Type);

        private bool IsArchiveBackupGrid => GrilleType.ARCHIVE_BACKUP.Equals(EditorData.Item.Type);

        private bool IsArchiveReplacementGrid => GrilleType.ARCHIVE_REPLACEMENT.Equals(EditorData.Item.Type);


        public bool? UseLink
        {
            get
            {
                if (EditorData != null && EditorData.Item != null)
                {

                    return EditorData.Item.UseLink;
                }
                return false;
            }
            set
            {
                if (EditorData != null && EditorData.Item != null)
                {
                    EditorData.Item.UseLink = value.Value;
                    EditorDataChanged.InvokeAsync(EditorData);
                }
            }
        }

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

        protected override Task OnAfterRenderAsync(bool firstRender)
        {
            if (firstRender)
            {
                if (EditorData.Item.IsReport && !EditorData.Item.Id.HasValue)
                {
                    UseLink = true;
                }
            }
            return base.OnAfterRenderAsync(firstRender);
        }
    }
}
