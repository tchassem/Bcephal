using Bcephal.Models.Base;
using Bcephal.Models.Forms;
using Bcephal.Models.Grids;
using Microsoft.AspNetCore.Components;
using System;
using System.Collections.Generic;
using System.Collections.ObjectModel;
using Bcephal.Models.Utils;

namespace Bcephal.Blazor.Web.Form.Shared.Component
{
    public partial class DynamicFormComponent : ComponentBase
    {
        [Parameter]
        public FormModel FormModel { get; set; }
        
        [Parameter]
        public int ColumnSize { get; set; } = 4;
        int ActiveTabIndex { get; set; } = 0;
        [Parameter]
        public Action<long, FormDataValue> AddOrUpdateHandler { get; set; }
        [Parameter]
        public Func<long?, FormDataValue> ValueHandler { get; set; }

        [Parameter]
        public Action<long, FormData> AddOrUpdateFormDataHandler { get; set; }
        [Parameter]
        public Action<long, FormData> DeleteFormDataHandler { get; set; }
        [Parameter]
        public Func<long?, long?, int, FormData> GetFormDataHandler { get; set; }
        [Parameter]
        public FormDataEditorData EditorData { get; set; }

        [Parameter]
        public Func<long?, IEnumerable<FormData>> GetFormDatasHandler { get; set; }

        [Parameter]
        public string ViewMode { get; set; }

        private void AddOrUpdateFromData(long key, FormDataValue value)
        {
            AddOrUpdateHandler?.Invoke(key, value);
        }

        private FormDataValue GetData(long? key)
        {
            return ValueHandler?.Invoke(key.Value);
        }

        private void AddOrUpdateSubGridFromData(long subGridkey, FormData data)
        {
            AddOrUpdateFormDataHandler?.Invoke(subGridkey, data);
        }

        private void DeleteSubGridFromData(long subGridkey, FormData data)
        {
            DeleteFormDataHandler?.Invoke(subGridkey, data);
        }

        private FormData GetSubGridData(long? subFormDataGridkey, long? FormDataId, int position)
        {
            return GetFormDataHandler?.Invoke(subFormDataGridkey, FormDataId, position);
        }

        private IEnumerable<FormData> GetSubGridDatas(long? subFormDataGridkey)
        {
            return GetFormDatasHandler?.Invoke(subFormDataGridkey);
        }
    }
}
