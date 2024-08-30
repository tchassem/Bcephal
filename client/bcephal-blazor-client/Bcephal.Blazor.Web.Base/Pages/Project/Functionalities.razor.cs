using Bcephal.Blazor.Web.Base.Services;
using Bcephal.Blazor.Web.Base.Shared.Component;
using Bcephal.Models.Functionalities;
using Microsoft.AspNetCore.Components;
using System.Linq;
using System.Threading.Tasks;

namespace Bcephal.Blazor.Web.Base.Pages.Project
{
    public partial class Functionalities : FormComponent<FunctionalityBlockGroup>
    {
        [Inject]
        FunctionalityService FunctionalityService { get; set; }

        [Inject]
        UserService UserService { get; set; }

        [Parameter]
        public string projectId { get; set; }

        [Parameter]
        public bool ShowTab { get; set; } = true;

        protected override FunctionalityBlockGroup NewItem { get => new FunctionalityBlockGroup(); }

        private string Username { get; set; }
        protected override string GroupName(FunctionalityBlockGroup item)
        {
            if (item != null)
            {
                return item.Name;
            }
            return null;
        }
        protected override void SetGroupName(FunctionalityBlockGroup item)
        {
            if (item != null)
            {
                if (string.IsNullOrWhiteSpace(item.Name))
                {
                    int index = 1;
                    item.Name = AppState["HomePage"] + index;
                    while (isContains(item))
                    {
                        index++;
                        item.Name = AppState["HomePage"] + index;
                    }
                }
            }
        }

        protected override void SetGroupName(FunctionalityBlockGroup item, string name)
        {
            item.Name = name;
        }
        private bool isContains(FunctionalityBlockGroup item)
        {
            if (Items != null)
            {
                var values = Items.Where(ite_ => !string.IsNullOrWhiteSpace(ite_.Name) && ite_.Name.Equals(item.Name) && ite_ != item);
                if (values.Any())
                {
                    return true;
                }
            }
            return false;
        }

        public async void DeleteFunctionalityBlockGroup(FunctionalityBlockGroup FBlockGroup)
        {
            string result = await FunctionalityService.DeleteFunctionalityBlockGroup(FBlockGroup);
            if (result.Equals("true"))
            {
                toastService.ShowSuccess(AppState["FunctionalityBlockGroup.SuccessFullyDeleted"]);
            }
            else
            {
                toastService.ShowError(result);
            }
        }

        protected override void DeleteItem(FunctionalityBlockGroup FBlockGroup)
        {
            if (FBlockGroup != null && FBlockGroup.Id.HasValue)
            {
                DeleteFunctionalityBlockGroup(FBlockGroup);
            }
        }

        protected override bool CanShowTab { get => ShowTab; set => ShowTab = value; }
        protected override async Task OnInitializedAsync()
        {
            Username = await UserService.GetUserName();
            await base.OnInitializedAsync();
        }
       
    }
}
