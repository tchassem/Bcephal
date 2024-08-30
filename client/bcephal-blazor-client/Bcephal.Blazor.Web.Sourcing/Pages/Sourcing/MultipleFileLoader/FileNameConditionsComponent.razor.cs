using Bcephal.Blazor.Web.Base.Services;
using Bcephal.Models.Base;
using Bcephal.Models.Loaders;
using Microsoft.AspNetCore.Components;
using System.Collections.Generic;

namespace Bcephal.Blazor.Web.Sourcing.Pages.Sourcing.MultipleFileLoader
{
    public partial class FileNameConditionsComponent : ComponentBase
    {
        [Parameter]
        public EventCallback<FileLoaderNameCondition> AddItem { get; set; }

        [Parameter]
        public EventCallback<FileLoaderNameCondition> UpdateItem { get; set; }

        [Parameter]
        public EventCallback<FileLoaderNameCondition> DeleteItem { get; set; }

        [Parameter]
        public EventCallback AddRender { get; set; }


        [Parameter]
        public FileLoaderNameCondition item { get; set; }

        [Parameter]
        public EditorData<FileLoader> EditorData { get; set; }

        bool IsSmallScreen;

        public string FilterBing { 
            get {
                return item.Filter;
            } 
            set {
                item.Filter = value;
                if (item.FileNameCondition != null)
                {
                    UpdateItem.InvokeAsync(item);
                }
             }
        }

        public FileNameCondition FileNameCondition
        {
            get
            {
                return item.FileNameCondition;
            }
            set
            {
                if (value != null)
                {
                    if (item.FileNameCondition == null)
                    {
                        item.FileNameCondition = value;
                        AddItem.InvokeAsync(item);
                        AddRender.InvokeAsync();
                    }
                    else
                    {
                        item.FileNameCondition = value;
                        UpdateItem.InvokeAsync(item);
                    }
                }
            }
        }

        [Inject]
        public AppState appState { get; set; }

        IEnumerable<FileNameCondition> filenameconditons = new List<FileNameCondition>(){
          FileNameCondition.BEGINS_WITH,
          FileNameCondition.CONTAINS,
          FileNameCondition.DO_NOT_CONTAINS,
          FileNameCondition.ENDS_WITH,
        };

        protected void deleteCodition()
        {
            if (DeleteItem.HasDelegate)
            {
                DeleteItem.InvokeAsync(item);
            }
        }
    }
}
