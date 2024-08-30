using System;
using System.Collections.Generic;
using System.Net.Http;
using System.Threading.Tasks;
using Bcephal.Blazor.Web.Base.Shared;
using Bcephal.Models.Exceptions;
using Bcephal.Models.Functionalities;
using Microsoft.JSInterop;
using Newtonsoft.Json;

namespace Bcephal.Blazor.Web.Base.Services
{
    public class FunctionalityService : Service<Functionality, object>
    {

        public static string ErrorMessage { get; set; } = "";

        public FunctionalityService(HttpClient RestClient, IJSRuntime JSRuntime) : base(RestClient, JSRuntime)
        {
            ResourcePath = "func";
        }

        public async Task<bool> SaveFunctionalitiesBlocks(List<FunctionalityBlock> itemfunctionalities, string idparam)
        {
            
            string uri = "functionalities/save-functionality-blocks?BC-CLIENT=" + idparam;
            string responseMessage = "";
            bool resul = false ;

            try
            {
                responseMessage = await ExecutePost(uri, itemfunctionalities);
                resul = Convert.ToBoolean(responseMessage);

              
            }
            catch (BcephalException e)
            {
                ErrorMessage = e.Message;

            }

            return resul;
           
            
        }

        public async Task<FunctionalityBlockGroup> SaveGroup(FunctionalityBlockGroup group, string idparam)
        {

            string uri = "functionalities/save-functionality-block-group?BC-CLIENT=" + idparam;
            string responseMessage = await ExecutePost(uri, group);
            FunctionalityBlockGroup FunctionalityBlockGroup  = null;

            bool ValidJson = Helpers.IsJsonValid(responseMessage);
            try
            {
                FunctionalityBlockGroup = JsonConvert.DeserializeObject<FunctionalityBlockGroup>(responseMessage);
            }
            catch(Exception e)
            {
                ErrorMessage = e.Message;
            }
            return FunctionalityBlockGroup;



        }

        public async Task<FunctionalityBlockGroup> GetGroupById(string idparam, string idGroup)
        {

            string uri = $"functionalities/get-functionality-block-group-by-id/{idGroup}?BC-CLIENT={idparam}"  ;
            string responseMessage = await ExecutePost(uri);
            FunctionalityBlockGroup FunctionalityBlockGroup = null;

            bool ValidJson = Helpers.IsJsonValid(responseMessage);
            try
            {
                FunctionalityBlockGroup = JsonConvert.DeserializeObject<FunctionalityBlockGroup>(responseMessage);
            }
            catch (Exception e)
            {
                ErrorMessage = e.Message;
            }
            return FunctionalityBlockGroup;



        }


        public async Task<string> DeleteFunctionalityBlock(FunctionalityBlock FBlock)
        {
            //await JSRuntime.InvokeVoidAsync("console.log", "Out FunctionalityBlock Id: ", FBlock.Id);
            string uri = $"functionalities/delete-functionality-block/{FBlock.Id}";

            string responseMessage="";

            try
            {
              responseMessage = await ExecuteGet(uri);
               
            }catch (BcephalException e)
            {
                ErrorMessage = e.Message;
                await JSRuntime.InvokeVoidAsync("console.log", "message d'exception : ", ErrorMessage);

            }
            return responseMessage;
        }

        public async Task<string> DeleteFunctionalityBlockGroup(FunctionalityBlockGroup FBlockGroup)
        {
            string uri = $"functionalities/delete-functionality-block-group/{FBlockGroup.Id}";

            string responseMessage = "";

            try
            {

                responseMessage = await ExecuteGet(uri);
            }
            catch (BcephalException e)
            {
                ErrorMessage = e.Message;
                await JSRuntime.InvokeVoidAsync("console.log", "message d'exception : ", ErrorMessage);

            }
            return responseMessage;
        }
        

        public async Task<object> SaveFunctionalityBlock(FunctionalityBlock Functionalityblock, string idparam)
        {
            //await JSRuntime.InvokeVoidAsync("console.log", "Begining to add a functionalityBlock to project");
            string uri = "functionalities/save-functionality-block?BC-CLIENT=" + idparam;
            object result;
            string responseMessage = await ExecutePost(uri, Functionalityblock);
            bool ValidJson = Helpers.IsJsonValid(responseMessage);
            if (ValidJson)
            {
                result = JsonConvert.DeserializeObject<FunctionalityBlock>(responseMessage);
            }
            else
            {
                result = responseMessage;
            }
          
            return result;
        }


    }
}
