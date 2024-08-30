using Bcephal.Blazor.Web.Base.Services;
using Bcephal.Models.Base;
using Bcephal.Models.Loaders;
using Bcephal.Models.Routines;
using DevExpress.Blazor;
using Microsoft.AspNetCore.Components;
using System.Collections.Generic;
using System.Collections.ObjectModel;
using System.Linq;
using System.Threading.Tasks;

namespace Bcephal.Blazor.Web.Sourcing.Pages.Sourcing.MultipleFileLoader
{
    public partial class FileLoaderRoutine : ComponentBase
    {
        [Inject]
        public AppState AppState { get; set; }


        public FileLoaderEditorData FileLoaderEditorData
        {
            get
            {
                return (FileLoaderEditorData)EditorData;
            }
            set
            {
                EditorData = value;
            }

        }

        [Parameter]
        public EditorData<FileLoader> EditorData { get; set; }

        [Parameter]
        public bool Editable { get; set; } = true;

        [Parameter]
        public EventCallback<EditorData<Bcephal.Models.Loaders.FileLoader>> EditorDataChanged { get; set; }






        public ObservableCollection<RoutineExecutorType> RoutineExecutorType_ { get; set; }


        public RoutineExecutor RoutineExecutor_ { get; set; }


        public List<string> RoutineExecType = new List<string>();


        protected override async Task OnInitializedAsync()
        {


            await base.OnInitializedAsync();
            RoutineExecType = RoutineExecutorType.GetAll().OrderBy(x => x.code).Select(r => { return r.code; }).ToList();
            
            


        }



        //private void AddItem(RoutineExecutor Item)
        //{
        //    if (RoutineExecutor_ == null)
        //    {
        //        RoutineExecutor_ = new RoutineExecutor();
        //    }

        //}


        public Nameable SelectedRoutine { get; set; }
        private void AddRoutine(Nameable val)
        {
            EditorData.Item.AddRoutine(new RoutineExecutor()
            {
                RoutineId = val.Id.Value,
                RoutineExecutorType = RoutineExecutorType.POST
                // RoutineExecutorType = RoutineExecutorType.GetAll().FirstOrDefault()

            }) ;
            EditorDataChanged.InvokeAsync(EditorData);
            SelectedRoutine = null;

        } 
        
       

      
        private void RoutineDeleteHandler(Bcephal.Models.Routines.RoutineExecutor Routine)
        {
            EditorData.Item.DeleteRoutine(Routine);
            EditorDataChanged.InvokeAsync(FileLoaderEditorData);
            AppState.Update = true;
        }
    }
}

