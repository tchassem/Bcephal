using Bcephal.Blazor.Web.Base.Services;
using Bcephal.Models.Base;
using Bcephal.Models.Dimensions;
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
    public partial class JoinSchedulerInfosComponent : ComponentBase
    {
        [Inject]
        public AppState AppState { get; set; }

        [Parameter]
        public EditorData<Join> EditorData { get; set; }

        [Parameter]
        public bool Editable { get; set; }

        [Parameter]
        public EventCallback<EditorData<Join>> EditorDataChanged { get; set; }

        [Parameter]
        public ObservableCollection<HierarchicalData> Entities { get; set; }

        string FreezeAttributeName => FreezeAttribute != null ? FreezeAttribute.Name : "";

        public bool IsSmallScreen { get; set; }

        HierarchicalData GetAttribute(long? id)
        {
            if (id.HasValue)
            {
                foreach (var item in Entities)
                {
                    foreach (var attib in ((Entity)item).Attributes)
                    {
                        if (attib.Id.HasValue && attib.Id.Value == id.Value)
                        {
                            return attib;
                        }
                    }
                }
            }
            return null;
        }

        public HierarchicalData FreezeAttribute
        {
            get
            {
                return GetAttribute(EditorData.Item.PublicationRunAttributeId);
            }
            set
            {
                if (value != null)
                {
                    EditorData.Item.PublicationRunAttributeId = value.Id;
                }
                else
                {
                    EditorData.Item.PublicationRunAttributeId = null;
                }
                EditorDataChanged.InvokeAsync(EditorData);
                AppState.Update = true;
            }
        }

        string JoinMethod
        {
            get
            {
                return EditorData.Item.JoinPublicationMethod.GetText(text => AppState[text]);
            }
            set
            {
                if (value != null)
                {
                    EditorData.Item.JoinPublicationMethod = EditorData.Item.JoinPublicationMethod.GetClientStatus(value, text => AppState[text]);
                    EditorData.Item.PublicationGridId = null;
                    EditorData.Item.PublicationGridName = "";
                    EditorDataChanged.InvokeAsync(EditorData);
                    AppState.Update = true;
                }
            }
        }

        SmartGrille JoinSmartGrille
        {
            get
            {
                return GetEditorData().Grids.Where((item) => EditorData.Item.PublicationGridId.HasValue && item.Id.HasValue && item.Id.Value == EditorData.Item.PublicationGridId.Value).FirstOrDefault();
            }
            set
            {
                EditorData.Item.PublicationGridId = value.Id.HasValue ? value.Id.Value : null;
                EditorDataChanged.InvokeAsync(EditorData);
                AppState.Update = true;
            }
        }

        string JoinGrilleName
        {
            get
            {
                return EditorData.Item.PublicationGridName;
            }
            set
            {
                EditorData.Item.PublicationGridName = value;
                EditorDataChanged.InvokeAsync(EditorData);
                AppState.Update = true;
            }
        }

        bool AddRunNbr
        {
            get
            {
                return EditorData.Item.AddPublicationRunNbr;
            }
            set
            {
                EditorData.Item.AddPublicationRunNbr = value;
                EditorDataChanged.InvokeAsync(EditorData);
                AppState.Update = true;
            }
        }

        bool JoinRefresh
        {
            get
            {
                return EditorData.Item.RefreshGridsBeforePublication;
            }
            set
            {
                EditorData.Item.RefreshGridsBeforePublication = value;
                EditorDataChanged.InvokeAsync(EditorData);
                AppState.Update = true;
            }
        }

        JoinPublicationMethod? publicationMethod => JoinPublicationMethod.NEW_GRID;
        private ObservableCollection<string> PublicationMethodCollection_ { get => publicationMethod.GetAll(text => AppState[text]); }

        public void AttributeChanged(HierarchicalData Attribute)
        {
            FreezeAttribute = Attribute;
            EditorDataChanged.InvokeAsync(EditorData);
            AppState.Update = true;
        }

        private JoinEditorData GetEditorData()
        {
            JoinEditorData joinEditorData = (JoinEditorData)EditorData;
            return joinEditorData;
        }
    }
}
