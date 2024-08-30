using Bcephal.Blazor.Web.Base.Services;
using Bcephal.Blazor.Web.Base.Shared;
using Bcephal.Blazor.Web.Form.Services;
using Bcephal.Models.Base;
using Bcephal.Models.Forms;
using Microsoft.AspNetCore.Components;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;

namespace Bcephal.Blazor.Web.Form.Pages.Form
{
  public partial  class DynamicForm : Form<FormData, object[]>
    {
        public override bool usingUnitPane => false;

        [Parameter]
        public long? FormModelId { get; set; }
        [Parameter]
        public string ViewMode { get; set; }
        [Inject]
        private FormDataService FormService { get; set; }
        public override string LeftTitle { get { return AppState["dynamicInputGridEdit", LeftTitlePage_]; } }
        public override string LeftTitleIcon { get { return  "bi-grid"; } }
        protected override FormDataService GetService()
        {
            return FormService;
        }

        public string LeftTitlePage_
        {
            get
            {
                if (EditorDataBinding_ == null || EditorDataBinding_.Item == null || EditorDataBinding_.FormModel == null)
                {
                    return null;
                }

                return EditorDataBinding_.FormModel.Menu.Caption;
            }
        }

        public override string LeftTitlePage
        {
            get
            {
                if (EditorDataBinding_ == null || EditorDataBinding_.Item == null 
                    || EditorDataBinding_.Item.Datas == null || EditorDataBinding_.Item.Datas.Count == 0)
                {
                    return null;
                }
                string title = null;
                foreach(var item in EditorDataBinding_.Item.Datas)
                {
                    if(item.Value != null && !string.IsNullOrWhiteSpace(item.Value.StringValue))
                    {
                        title = item.Value.StringValue;
                        break;
                    }
                }
                return title;
            }
        }

        protected  FormDataEditorData EditorDataBinding_
        {
            get { return (FormDataEditorData)EditorDataBinding; }
            set
            {
               EditorDataBinding = value;
            }
        }

        protected override Task OnAfterRenderAsync(bool firstRender)
        {
            if (firstRender)
            {
                RefreshRightContent(null);
                
            }
            return base.OnAfterRenderAsync(firstRender);
        }

        protected override EditorDataFilter getEditorDataFilter()
        {
            EditorDataFilter filter =  new EditorDataFilter();
            filter.SecondId = FormModelId;
            return filter;
        }

        public FormData FormDatasBinding
        {
            get => EditorData.Item;
            set
            {
                EditorDataBinding_.Item = value;
                EditorDataBinding = EditorDataBinding_;
            }
        }

        private void AddOrUpdateFromData(long key, FormDataValue value)
        {
            FormDatasBinding.Datas.Remove(key);
            FormDatasBinding.Datas.Add(key, value);            
        }

        private FormDataValue GetData(long? key)
        {
            if (key.HasValue)
            {
                FormDatasBinding.Datas.TryGetValue(key, out FormDataValue val);
                return val;
            }
            return new();
        }
       

        private void AddOrUpdateSubGridFromData(long subGridkey, FormData data)
        {
            FormDatasBinding.SubGridDatas.TryGetValue(subGridkey, out ListChangeHandler<FormData> FormDataHandler);            
            if (data.IsPersistent)
            {
                FormDataHandler.AddUpdated(data);
            }
            else
            {
                FormDataHandler.forget(data);
                FormDataHandler.AddNew(data);
            }
            AppState.Update = true;
        }

        private void DeleteSubGridFromData(long subGridkey, FormData data)
        {
            FormDatasBinding.SubGridDatas.TryGetValue(subGridkey, out ListChangeHandler<FormData> FormDataHandler);
            if (data.IsPersistent)
            {
                FormDataHandler.AddDeleted(data, false);
            }
            else
            {
                FormDataHandler.forget(data);
            }
            
            if (data.Position > 0 && FormDataHandler.GetItems().Count() > 0)
            {
                int offset = data.Position - 1;
                while (offset < FormDataHandler.GetItems().Count())
                {
                    var itemData = FormDataHandler.GetItems().ElementAt(offset);
                    itemData.Position -= 1;
                    AddOrUpdateSubGridFromData(subGridkey, itemData);
                    offset++;
                }
            }
            AppState.Update = true;
        }

        private FormData GetSubGridData(long? subFormDataGridkey, long? FormDataId, int position)
        {
            ListChangeHandler<FormData> FormDataHandler = null;
            if (subFormDataGridkey.HasValue)
            {
                FormDatasBinding.SubGridDatas.TryGetValue(subFormDataGridkey, out FormDataHandler);
                if (FormDataHandler == null)
                {
                    FormDataHandler = new();
                    FormDatasBinding.SubGridDatas.Add(subFormDataGridkey, FormDataHandler);
                }
            }
            FormData dataValue = null;
            if (FormDataId.HasValue && FormDataHandler != null)
            {
                dataValue = FormDataHandler.GetItems().Where(ite => ite.Id.HasValue && ite.Id.Value.Equals(FormDataId)).FirstOrDefault();                
            }
            else
            if(FormDataHandler != null)
            {
                dataValue = FormDataHandler.GetItems().Where(ite => ite.Position.Equals(position)).FirstOrDefault();
            }
            if (dataValue == null)
            {
                dataValue = new();
            }
            return dataValue;
        }

        private IEnumerable<FormData>  GetSubGridDatas(long? subFormDataGridkey)
        {
            ListChangeHandler<FormData> FormDataHandler = null;
            if (subFormDataGridkey.HasValue)
            {
                FormDatasBinding.SubGridDatas.TryGetValue(subFormDataGridkey, out FormDataHandler);
                if (FormDataHandler == null)
                {
                    FormDataHandler = new();
                    FormDatasBinding.SubGridDatas.Add(subFormDataGridkey, FormDataHandler);
                }
            }
            return FormDataHandler.GetItems();
        }
    }

    public static class FormDataExtention
    {
        public static void SortAfterPosition(this IEnumerable<FormData> formDatas, int position)
        {
            if (formDatas != null && position > 0 && formDatas.Count() > 0)
            {
                int offset = position - 1;
                while (offset < formDatas.Count())
                {
                    formDatas.ElementAt(offset).Position -= 1;
                    offset++;
                }
            }
        }
    }
}
