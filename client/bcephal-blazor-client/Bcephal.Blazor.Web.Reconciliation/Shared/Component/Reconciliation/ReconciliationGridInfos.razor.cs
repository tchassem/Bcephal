using Bcephal.Blazor.Web.Base.Services;
using Bcephal.Models.Base;
using Microsoft.AspNetCore.Components;

namespace Bcephal.Blazor.Web.Reconciliation.Shared.Component.Reconciliation
{
    public partial class ReconciliationGridInfos<T> : ComponentBase where T : Models.Grids.Grille
    {

        [Inject]  private AppState AppState { get; set; }        
        [Parameter] public bool Editable { get; set; } = true;
        [Parameter] public EditorData<T> EditorData { get; set; }
        [Parameter] public EventCallback<EditorData<T>> EditorDataChanged { get; set; }
        public bool IsSmallScreen { get; set; }
        public string LabelWidth { get; set; } = Constant.GROUP_INFOS_LABEL_WIDTH;
        public string TextWidth { get; set; } = Constant.GROUP_INFOS_TEXT_WIDTH;
        public bool? Debit
        {
            get
            {
                if (EditorData != null && EditorData.Item != null)
                {

                    return EditorData.Item.Debit;
                }
                return false;
            }
            set
            {
                if (EditorData != null && EditorData.Item != null)
                {
                    EditorData.Item.Debit = value;
                    EditorDataChanged.InvokeAsync(EditorData);
                    AppState.Update = true;
                }
            }
        }

        public bool? Credit
        {
            get
            {
                if (EditorData != null && EditorData.Item != null)
                {

                    return EditorData.Item.Credit;
                }
                return false;
            }
            set
            {
                if (EditorData != null && EditorData.Item != null)
                {
                    EditorData.Item.Credit = value;
                    EditorDataChanged.InvokeAsync(EditorData);
                    AppState.Update = true;
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
                    AppState.Update = true;
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
                    AppState.Update = true;
                }
            }
        }

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
                    AppState.Update = true;
                }
            }
        }
    }
}
