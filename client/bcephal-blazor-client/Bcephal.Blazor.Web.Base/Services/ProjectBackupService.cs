using Bcephal.Blazor.Web.Base.Shared;
using Bcephal.Models.Base;
using Bcephal.Models.Exceptions;
using Bcephal.Models.Projects;
using Microsoft.JSInterop;
using Newtonsoft.Json;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Net.Http;
using System.Text;
using System.Threading.Tasks;

namespace Bcephal.Blazor.Web.Base.Services
{
    public class ProjectBackupService : Service<SimpleArchive, SimpleArchive>
    {
        public ProjectBackupService(HttpClient RestClient, IJSRuntime JSRuntime) : base(RestClient, JSRuntime)
        {
            ResourcePath = "projects/backups";
            SocketResourcePath = "/ws/projects/backup-project";
        }

        /*public async Task<bool> Create(ProjectBackupData projectBackupData)
        {
            string Uri = ResourcePath +  projectBackupData;
            return bool.Parse(await ExecutePost(Uri));
        }*/

        public async Task<bool> DeleteProjectBackup(long? archiveId)
        {
            string uri = ResourcePath + "/delete/" + archiveId;
            bool isBackupDeleted = await DeleteString(uri);
            try
            {
                bool resul = Convert.ToBoolean(isBackupDeleted);
            }
            catch (BcephalException e)
            {
                //ErrorMessage = e.Message;
            }
            return isBackupDeleted;
        }

    }
}
