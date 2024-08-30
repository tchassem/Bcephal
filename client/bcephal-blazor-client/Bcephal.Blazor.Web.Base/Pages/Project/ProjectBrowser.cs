using Bcephal.Blazor.Web.Base.Services;
using Bcephal.Blazor.Web.Base.Shared.AbstractComponent;
using Bcephal.Models.Base;
using Bcephal.Models.Grids.Filters;
using Bcephal.Models.Projects;
using Microsoft.AspNetCore.Components;
using System;
using System.Collections.Generic;
using System.Threading.Tasks;
using System.Linq;

namespace Bcephal.Blazor.Web.Base.Pages.Project
{
    public class ProjectBrowser_ : AbstractNewGridComponent<Models.Projects.Project, ProjectBrowserData>
    {
        protected dynamic[] ProjectColumns => new[] {
                        new {CaptionName = AppState["project"] ,ColumnWidth="30%", ColumnName = nameof(ProjectBrowserData.Name), ColumnType = typeof(string)},
                        new {CaptionName = AppState["Projects.DefaultProject"],ColumnWidth="30%", ColumnName = nameof(ProjectBrowserData.DefaultProject), ColumnType = typeof(bool)},
                        new {CaptionName = AppState["CreationDate"] ,ColumnWidth="auto", ColumnName = nameof(ProjectBrowserData.CreationDateTime), ColumnType = typeof(DateTime?)},
                    };

        protected override int ItemsCount => ProjectColumns.Length;

        private object GetPropertyValue(ProjectBrowserData obj, string propName)
        {
            return obj.GetType().GetProperty(propName).GetValue(obj, null);
        }

        [Inject]
        public ProjectService ProjectService { get; set; }
        public virtual ProjectService GetService()
        {
            return ProjectService;
        }

        protected override async Task OnInitializedAsync()
        {
            await base.OnInitializedAsync();
            EditorRoute = Route.PROJECT;
            IsNavLink = true;
            NewButtonVisible = false;
            ClearFilterButtonVisible = false;
            SelectionMode = DevExpress.Blazor.GridSelectionMode.Single;
            CurrentEditMode = DevExpress.Blazor.GridEditMode.PopupEditForm;
            CanCreate = !AppState.IsDashboard;
            if (AppState.PrivilegeObserver != null && AppState.PrivilegeObserver.ProjectAllowed && AppState.PrivilegeObserver.ProjectCreateAllowed)
            {
                EditButtonVisible = true;
                DeleteButtonVisible = true;
                DeleteAllButtonVisible = false;
            }
        }


        protected override Task OnAfterRenderAsync(bool firstRender)
        {
            if (AppState.PrivilegeObserver != null && AppState.PrivilegeObserver.ProjectAllowed && AppState.PrivilegeObserver.ProjectCreateAllowed)
            {
                AppState.CanCreate = !AppState.IsDashboard;
                AppState.CanRefresh = !AppState.IsDashboard;
                CanCreate = !AppState.IsDashboard;
                DeleteAllButtonVisible = false;
            }
            return base.OnAfterRenderAsync(firstRender);
        }

        public override ValueTask DisposeAsync()
        {
            if (AppState.PrivilegeObserver != null && AppState.PrivilegeObserver.ProjectAllowed && AppState.PrivilegeObserver.ProjectCreateAllowed)
            {
                AppState.CanCreate = false;
                AppState.CanRefresh = false;
            }

            return base.DisposeAsync();
        }

        protected override string getCommandColumnWidth()
        {
            if (DeleteButtonVisible && EditButtonVisible)
            {
                return "9%";
            }
            else
            {
                return "4.5%";
            }
        }
        // Methode 

        protected override AbstractNewGridDataItem GetGridDataItem(int Position)
        {
            return new NewGridDataItem(ProjectColumns[Position], Position);
        }

        protected override object GetFieldValue(ProjectBrowserData item, int grilleColumnPosition)
        {
            return GetPropertyValue(item, ProjectColumns[grilleColumnPosition].ColumnName);
        }

        protected override string KeyFieldName()
        {
            return nameof(ProjectBrowserData.Id);
        }

        protected override object KeyFieldValue(ProjectBrowserData item)
        {
            return item.Id;
        }

        protected override string NavLinkURI()
        {
            return Route.INDEX;
        }

        protected override Task OnRowInserting(ProjectBrowserData newValues)
        {
            return Task.CompletedTask;
        }

        protected async override Task OnRowUpdating(ProjectBrowserData dataItem, ProjectBrowserData newValues)
        {
            try
            {
                string link = NavLinkURI();
                if (link.Trim().EndsWith("/"))
                {
                    link += dataItem.Id;
                }
                else
                {
                    link += "/" + dataItem.Id;
                }
                await AppState.NavigateTo(link);
            }
            catch (Exception ex)
            {
                Error.ProcessError(ex);
            }
        }


        protected override async  Task OnRowRemoving(ProjectBrowserData dataItem)
        {
            await ProjectService.DeleteProject(dataItem.Id.Value);
            await Task.Delay(TimeSpan.FromSeconds(3));
        }

        protected override async Task OnRowRemoving(IReadOnlyList<object> ids)
        {
            if (ids != null && ids.Count > 0)
            {
                var idss = ids.Select(obj => ((ProjectBrowserData)obj).Id.Value).ToList();
                await Task.Run(() => idss.ForEach(async it => {
                    await ProjectService.DeleteProject(it).ContinueWith(t => RefreshGrid_());
                    //await Task.Delay(TimeSpan.FromSeconds(3));
                }));
            }
            //return Task.CompletedTask;
        }

        protected override  Task<BrowserDataPage<ProjectBrowserData>> SearchRows(BrowserDataFilter filter)
        {
            return  ProjectService.Search(filter);
        }

        protected async override void Create()
        {
            if (AppState.PrivilegeObserver != null
                && AppState.PrivilegeObserver.ProjectAllowed
                && AppState.PrivilegeObserver.ProjectCreateAllowed)
            {
                await AppState.CreateProject();
            }
        }

        ProjectBrowserData GetItemsById(long id)
        {
            foreach (var item_ in page_.Items)
            {
                ProjectBrowserData item = (ProjectBrowserData)item_;
                if (item.Id == id)
                {
                    return item;
                }
            }
            return null;
        }

        protected async override void SetEdite(ProjectBrowserData data)
        {
            if (AppState.PrivilegeObserver != null && (AppState.PrivilegeObserver.ProjectEditAllowed || AppState.PrivilegeObserver.ProjectCreateAllowed))
            {
                await AppState.EditProject(((ProjectBrowserData)data).Id.Value);
            }
        }

        protected override string GetOpenTabLink(object id, int? position = null)
        {
            return NavLinkURI()+id;
        }

        protected async override void NavigateTo(object id_, int? position = null)
        {
            if (id_ != null)
            {
                long.TryParse(id_.ToString(), out long id);
                ProjectBrowserData data = GetItemsById(id);
                if (data != null )
                {
                  await  AppState.OpenProject(data.Name, data.Id, data.Code);
                }
            }
        }
        protected override string FormatDateCellValue(string format, Object obj)
        {
            return base.FormatDateCellValue("dd/MM/yyyy hh:mm:ss", obj);
        }
    }
}
