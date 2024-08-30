using Bcephal.Blazor.Web.Base.Services;
using Bcephal.Models.Base;
using Bcephal.Models.Joins;
using Bcephal.Models.Routines;
using Microsoft.AspNetCore.Components;
using System;
using System.Collections.Generic;
using System.Collections.ObjectModel;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Bcephal.Blazor.Web.Reporting.Pages.Joins
{
    public partial class JoinRoutineItem_ : ComponentBase
    {
        [Inject] public AppState AppState { get; set; }
        [Parameter] public EditorData<Join> EditorData { get; set; }
        [Parameter] public RoutineExecutor Item { get; set; }

        [Parameter] public Action<RoutineExecutor> DeletedItemCallback { get; set; }

        [Parameter] public Action<RoutineExecutor> AddItemCallback { get; set; }

        [Parameter] public Action<RoutineExecutor> UpdateItemCallback { get; set; }

        public RoutineExecutorType RoutineExecutorType_ => RoutineExecutorType.PRE;

        [Parameter] public bool IsNew { get; set; } = true;
        public ObservableCollection<RoutineExecutor> RoutineList { get; set; }
        private ObservableCollection<string> RoutineExectorTypes => RoutineExecutorType_.GetAllRoutineExecutorTypes(text => AppState[text]);


        protected override void OnInitialized()
        {
            base.OnInitialized();
            RoutineList = EditorData.Item.RoutineListChangeHandler.GetItems(); 
        }
        public string Type_
        {
            get
            {
                if (Item != null && !string.IsNullOrEmpty(Item.Type) && Item.RoutineExecutorType != null)
                {
                   return Item.RoutineExecutorType.GetText(text => AppState[text]);
               
                }
                return RoutineExecutorType.PRE.GetText(text => AppState[text]);
            }
            set
            {
                bool isPost = RoutineExecutorType.POST.IsPost();
                Item.RoutineExecutorType = RoutineExecutorType.PRE.GetRoutineExecutorType(value, text => AppState[text]) ;
                UpdateItemCallback?.Invoke(Item);
            }
        }

        public Nameable Name
        {
            get
            {
                if (Item != null && Item.RoutineId.HasValue)
                {
                    Nameable nameable = GetEditorData().Routines.Where(x => x.Id == Item.RoutineId.Value).FirstOrDefault();
                    return nameable;
                }
                return null;
            }
            set
            {
                Item.RoutineId = value.Id.Value;
                 if (IsNew)
                {
                    AddItemCallback?.Invoke(Item);
                }
                else
                {
                    UpdateItemCallback?.Invoke(Item);
                } 
            }
        }

        private void DeleteItem()
        {
            DeletedItemCallback?.Invoke(Item);
        }
       
        private JoinEditorData GetEditorData()
        {
            JoinEditorData JoinEditorData = (JoinEditorData)EditorData;
            return JoinEditorData;
        }
    }
}
