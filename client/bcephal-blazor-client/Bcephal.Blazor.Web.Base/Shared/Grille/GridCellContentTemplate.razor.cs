using Bcephal.Blazor.Web.Base.Shared.AbstractComponent;
using Bcephal.Models.Base;
using DevExpress.Blazor;
using Microsoft.AspNetCore.Components;
using Microsoft.AspNetCore.Components.Web;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Bcephal.Blazor.Web.Base.Shared.Grille
{
   public partial class GridCellContentTemplate<C> : IAsyncDisposable where C : DataForEdit
    {
        [Parameter] public Func<C> EditedValues { get; set; }
        [Parameter] public GridDataColumnCellDisplayTemplateContext ContextData { get; set; }
        [Parameter] public AbstractNewGridDataItem GridDataItem { get; set; }
        [Parameter] public string columnFormat { get; set; }
        [Parameter] public int position { get; set; }
        [Parameter] public bool IsNavLink { get; set; }
        [Parameter] public Action<C, int, object> EditGridCell { get; set; }
        [Parameter] public Func<KeyboardEventArgs, C, Task> EnterHandleValidSubmit { get; set; }
        [Parameter] public Func<C, AbstractNewGridDataItem, DateTime?> GetEditDateTimeFieldName { get; set; }
        [Parameter] public Func<C, AbstractNewGridDataItem, decimal?> GetEditDecimalFieldName { get; set; }
        [Parameter] public Func<C, AbstractNewGridDataItem, string> GetEditTextFieldName { get; set; }
        [Parameter] public Func<GridDataColumnCellDisplayTemplateContext, AbstractNewGridDataItem, RenderFragment<Action>> GetEditData { get; set; }
        [Parameter] public Func<string, object, string> FormatDateCellValue { get; set; }
        [Parameter] public Func<string, object, string> FormatDoubleCellValue { get; set; }
        [Parameter] public Func<C, int, object> GetFieldValue { get; set; }
        [Parameter] public Func<C, object> KeyFieldValue { get; set; }
        [Parameter] public Func<object, int?, string> GetOpenTabLink { get; set; }
        [Parameter] public Action<C, int, bool> CheckedChanged { get; set; }        
        [Parameter] public Action<object, int?> NavigateTo { get; set; }
        [Parameter] public Action<MouseEventArgs, C, string, string> OpenLing { get; set; }
        RenderFormContent RenderFormContentRef { get; set; }

        private bool IsInEditMode { get; set; } = false;
        void state(object sender, EventArgs pro)
        {
            IsInEditMode = ((C)sender).IsInEditMode; 
            InvokeAsync(RenderFormContentRef.StateHasChanged_);
        }

        protected override Task OnAfterRenderAsync(bool firstRender)
        {
            ((C)ContextData.DataItem).PropertyStateChanged -= state;
            ((C)ContextData.DataItem).PropertyStateChanged += state;
            return base.OnAfterRenderAsync(firstRender);
        }

        public ValueTask DisposeAsync()
        {
            if (ContextData != null && ContextData.DataItem != null)
            {
                ((C)ContextData.DataItem).PropertyStateChanged -= state;
            }
            return ValueTask.CompletedTask;
        }
    }
}
