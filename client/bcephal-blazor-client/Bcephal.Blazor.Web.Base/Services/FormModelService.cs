using Bcephal.Models.Base;
using Bcephal.Models.Forms;
using Bcephal.Models.Grids;
using Microsoft.JSInterop;
using Newtonsoft.Json;
using System;
using System.Collections.Generic;
using System.Collections.ObjectModel;
using System.Net.Http;
using System.Threading.Tasks;

namespace Bcephal.Blazor.Web.Base.Services
{
    public class FormModelService : Service<FormModel, BrowserData>
    {

        public FormModelService(HttpClient RestClient, IJSRuntime JSRuntime) : base(RestClient, JSRuntime)
        {
            ResourcePath = "form/model";
        }

        public async Task<ObservableCollection<FormModelMenu>> Menus()
        {
            string response = await this.ExecuteGet(ResourcePath + "/menus");
            return JsonConvert.DeserializeObject<ObservableCollection<FormModelMenu>>(response, getJsonSerializerSettings());
        }

        protected override EditorData<FormModel> DeserialiazeEditorData(string response)
        {
            JsonSerializerSettings settings = new JsonSerializerSettings()
            {
                ReferenceLoopHandling = Newtonsoft.Json.ReferenceLoopHandling.Serialize,
                //DateFormatString = "dd/MM/yyyy",
                Converters = new List<JsonConverter>(new JsonConverter[] { new CustomDateTimeConverter() })
            };
            FormModelEditorData result = JsonConvert.DeserializeObject<FormModelEditorData>(response, settings);
            return result;
        }

        public async Task<bool> DeleteRows(List<long> id)
        {
            string response = await this.ExecutePost(ResourcePath + "/delete-rows", id);
            bool result = false;
            try
            {
                bool.TryParse(response, out result);
            }
            catch (Exception)
            {

            }
            return result;
        }
        public async Task<bool> SetEditable(long id, bool editable)
        {
            string response = await this.ExecutePost(ResourcePath + $"/set-editable/{id}/{editable}");
            bool result = false;
            try
            {
                bool.TryParse(response, out result);
            }
            catch (Exception)
            {

            }
            return result;
        }

        public async Task<GrilleEditedResult> EditCell(GrilleEditedElement grilleEditedElement)
        {
            string response = await this.ExecutePost(ResourcePath + "/edit-cell", grilleEditedElement);
            GrilleEditedResult page = JsonConvert.DeserializeObject<GrilleEditedResult>(response);
            return page;
        }

        public async Task<GridItem> EditCells(List<GrilleEditedElement> grilleEditedElements)
        {
            GrilleEditedResult page = null;
            GridItem gridItem = null;
            foreach (var el in grilleEditedElements)
            {
                if (gridItem != null)
                {
                    el.Id = gridItem.GetId();
                    page = await EditCell(el);
                    gridItem = new GridItem(page.Datas);
                }
                else
                {
                    page = await EditCell(el);
                    gridItem = new GridItem(page.Datas);
                }
            }
            return gridItem;
        }



        public async Task<ObservableCollection<GrilleColumn>> GetColumns(long id)
        {
            string Uri = ResourcePath + "/columns/" + id;
            string responseMessage = await ExecuteGet(Uri);
            ObservableCollection<GrilleColumn> resul = JsonConvert.DeserializeObject<ObservableCollection<GrilleColumn>>(responseMessage);
            return resul;
        }
    }
}
